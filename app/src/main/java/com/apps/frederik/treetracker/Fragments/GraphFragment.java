package com.apps.frederik.treetracker.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.apps.frederik.treetracker.Model.MonitoredProperty.MonitoredProperty;
import com.apps.frederik.treetracker.Model.Reading.Reading;
import com.apps.frederik.treetracker.Model.Util.TimeStampHelper;
import com.apps.frederik.treetracker.R;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.PointsGraphSeries;
import com.jjoe64.graphview.series.Series;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

// using Graph View from: https://github.com/appsthatmatter/GraphView
// All usage is inspired by guides from: http://www.android-graphview.org/
public class GraphFragment extends Fragment implements IPropertyDataUpdater {
    private MonitoredProperty _property;
    private GraphView _graph;
    private PointsGraphSeries<DataPoint> seriesPoint = new PointsGraphSeries<DataPoint>();
    private LineGraphSeries<DataPoint> seriesLine = new LineGraphSeries<DataPoint>();
    private Date minTimeXAxis;
    private Date maxTimeXAxis;
    private View _view;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        _view = inflater.inflate(R.layout.fragment_graph, container, false);
        _graph = _view.findViewById(R.id.graph);

        setRetainInstance(true); // saves complex datatypes, like MonitoredProperty _property on orientation swift.

        SetupGraphLayout();
        PopulateGraph();

        return _view;
    }

    @Override
    public void SetMonitoredProperty(MonitoredProperty prop) {
        _property = prop;

        if(_graph == null) return;

        PopulateGraph();
    }

    @Override
    public void AddReading(Reading read) {
        _property.getReadings().add(read);
        PopulateGraph();
    }

    private void PopulateGraph(){
        final List<DataPoint> data = new ArrayList<>();

        int cnt = _property.getReadings().size();

        if(cnt == 0){

            return;
        }

        for (Reading r : _property.getReadings()){
            data.add(new DataPoint(TimeStampHelper.get_dataTime(r.getTimeStamp()), r.getData()));
        }

        DataPoint[] dataArray = data.toArray(new DataPoint[data.size()]);
        seriesPoint = new PointsGraphSeries<DataPoint>(dataArray);

        seriesPoint.setOnDataPointTapListener(new OnDataPointTapListener() {
            @Override
            public void onTap(Series series, DataPointInterface dataPoint) {
                String msg;
                switch (_property.getIdentifier()){
                    case "Humidity" : {
                        msg = "Humidity is " + dataPoint.getY() + "%";
                        break;
                    }
                    case "Temperature" : {
                        msg = "Temperature is " + dataPoint.getY() + "\"\\u00b0\"" + "C";
                    }
                    default:{
                        msg = "Data value: " + dataPoint.getY();
                    }
                }
                Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
            }
        });

        seriesLine = new LineGraphSeries<DataPoint>(dataArray);

        _graph.removeAllSeries();
        _graph.addSeries(seriesPoint);
        _graph.addSeries(seriesLine);
    }


    private void SetupGraphLayout(){
        _graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(getActivity(), new SimpleDateFormat("dd.MM.yy - hh:mm a")));

        _graph.getGridLabelRenderer().setHumanRounding(false);
        _graph.getViewport().setXAxisBoundsManual(true);

        int cnt = _property.getReadings().size();
        _graph.getGridLabelRenderer().setNumHorizontalLabels(2); // sets only first x axis label and last

        // first in readings is time-wise the first reading, and last element is the latest reading.
        minTimeXAxis = TimeStampHelper.get_dataTime(_property.getReadings().get(0).getTimeStamp());
        maxTimeXAxis = TimeStampHelper.get_dataTime(_property.getReadings().get(cnt-1).getTimeStamp());
        _graph.getViewport().setMinX(minTimeXAxis.getTime());
        _graph.getViewport().setMaxX(maxTimeXAxis.getTime());
    }


}
