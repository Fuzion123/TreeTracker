package com.apps.frederik.treetracker.Fragments;


import android.content.res.Configuration;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.apps.frederik.treetracker.R;
import com.google.gson.Gson;
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
import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;
import java.util.Iterator;
import java.util.List;

// using Graph View from: https://github.com/appsthatmatter/GraphView
// All usage is inspired by guides from: http://www.android-graphview.org/
public class GraphFragment extends Fragment {
    private List<DataPoint> data = new ArrayList<>();
    private GraphView _graph;
    private PointsGraphSeries<DataPoint> _seriesPoint = new PointsGraphSeries<DataPoint>();
    private LineGraphSeries<DataPoint> _seriesLine = new LineGraphSeries<DataPoint>();
    private View _view;
    private final String ON_SAVED_INSTANCE_PROPERTY = "com.apps.frederik.treetracker.Fragments.on.saved.instance";



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //setRetainInstance(true); // saves complex datatypes, like MonitoredProperty _property on orientation swift.

        if(savedInstanceState != null){
            String propertyAsJson = savedInstanceState.getString(ON_SAVED_INSTANCE_PROPERTY);

            Gson gson = new Gson();

            //_property = gson.fromJson(propertyAsJson, MonitoredProperty.class);
        }

        _view = inflater.inflate(R.layout.fragment_graph, container, false);
        _graph = _view.findViewById(R.id.graph);

        SetupGraphLayout();
        PopulateGraph();

        return _view;
    }

    /*
    public void UpdateGraph(MonitoredProperty prop) {
        _property = prop;

        if(_graph == null) return;

        PopulateGraph();
    }
    */

    private void PopulateGraph(){
        if(data.size() == 0) return;

        /*for (Reading r : _property.getReadings()){
            data.add(new DataPoint(TimeStampHelper.get_dataTime(r.getTimeStamp()), r.getData()));
        }*/

        DataPoint[] dataArray = data.toArray(new DataPoint[data.size()]);
        _seriesLine.resetData(dataArray);
        _seriesPoint.resetData(dataArray);

        double minX;
        int maxNumberDatapointsPortrait = 7;
        int maxNumberDatapointsLandscape = 12;
        int numberToShow = 0;

        if(data.size() > maxNumberDatapointsPortrait){
            numberToShow = maxNumberDatapointsPortrait;
            if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                if(data.size() > maxNumberDatapointsLandscape){
                    numberToShow = maxNumberDatapointsLandscape;
                }
            }
        }
        else{
            numberToShow = data.size();
        }

        minX = data.get(data.size()-numberToShow).getX();

        _graph.getViewport()
                .setMinX(minX);


        double maxX = _seriesLine.getHighestValueX();
        _graph.getViewport()
                .setMaxX(maxX);

        // clears all series and adds newly added
        _graph.removeAllSeries();
        _graph.addSeries(_seriesLine);
        _graph.addSeries(_seriesPoint);
    }

    private void SetupGraphLayout(){
        _graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(getActivity(), new SimpleDateFormat("dd/MM")));
        _graph.getViewport().setScalable(true);
        _graph.setTitle("Historical data");
        _graph.setTitleTextSize(60f);
        _graph.getGridLabelRenderer().setHorizontalAxisTitle("Time");
        //_graph.getGridLabelRenderer().setVerticalAxisTitle(_property.getIdentifier()); // for example humidity
        _graph.getGridLabelRenderer().setPadding(70); // add padding for the graph to be fully shown for the max datapoint to be seen on the graph also
        _graph.getGridLabelRenderer().setHorizontalLabelsAngle(15); // makes horizontal date labels angled ( due to long label names)

        _seriesPoint.setOnDataPointTapListener(new OnDataPointTapListener() {
            @Override
            public void onTap(Series series, DataPointInterface dataPoint) {
                /*
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

                SimpleDateFormat formatter = new SimpleDateFormat("EEE, dd. MMM yyyy  hh:mm");
                String readableDate = formatter.format(dataPoint.getX());

                Toast.makeText(getActivity(), msg + "\n" + readableDate, Toast.LENGTH_LONG).show();
                */
            }
        });
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        /*
        Gson gson = new GsonBuilder().create();
        String propAsJson = gson.toJson(_property);

        outState.putString(ON_SAVED_INSTANCE_PROPERTY, propAsJson);
        */
    }
}
