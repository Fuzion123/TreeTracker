package com.apps.frederik.treetracker.Fragments;


import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.apps.frederik.treetracker.R;
import com.google.gson.Gson;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.PointsGraphSeries;
import com.jjoe64.graphview.series.Series;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

// using Graph View from: https://github.com/appsthatmatter/GraphView
// All usage is inspired by guides from: http://www.android-graphview.org/
public class GraphFragment extends Fragment {
    private Map<String, List<DataPoint>> _data = new HashMap<>();
    private GraphView _graph;
    private List<PointsGraphSeries<DataPoint>> _seriesPoints = new ArrayList<>();
    private List<LineGraphSeries<DataPoint>> _seriesLines = new ArrayList<>();

    private LineGraphSeries _lineHumidity = new LineGraphSeries();
    private PointsGraphSeries _pointsHumidity = new PointsGraphSeries();
    private LineGraphSeries _lineBattery = new LineGraphSeries();
    private PointsGraphSeries _pointsBattery = new PointsGraphSeries();

    private View _view;
    private final String ON_SAVED_INSTANCE_PROPERTY = "com.apps.frederik.treetracker.Fragments.on.saved.instance";
    private int numberOfDataPoints = 0;
    private int maxNumberDatapointsPortrait = 7;
    private int maxNumberDatapointsLandscape = 12;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        _view = inflater.inflate(R.layout.fragment_graph, container, false);
        _graph = _view.findViewById(R.id.graph);

        SetupGraphLayout();
        PopulateGraph();

        return _view;
    }


    public void AddData(Map<String, List<DataPoint>> data) {
        _data = new HashMap<>(data);

        if(_graph == null) return;

        PopulateGraph();
    }


    private void PopulateGraph(){
        int cnt = _data.size();
        if(cnt == 0) return;

        Iterator<Map.Entry<String, List<DataPoint>>> it = _data.entrySet().iterator();
        while (it.hasNext()){
            Map.Entry<String, List<DataPoint>> entry = it.next();
            DataPoint[] dataArray = entry.getValue().toArray(new DataPoint[entry.getValue().size()]);

            if(entry.getKey().equals("humidity")){
                _lineHumidity.resetData(dataArray);
                _pointsHumidity.resetData(dataArray);
            }
            else{
                _lineBattery.resetData(dataArray);
                _pointsBattery.resetData(dataArray);
            }
        }

        List<DataPoint> temp = _data.entrySet().iterator().next().getValue(); // get random List<Datapoints> to get size (they should all have equal size. .
        numberOfDataPoints = temp.size();

        // return if there is no datapoints!
        if(numberOfDataPoints == 0) return;

        float pointSize = _pointsBattery.getSize()/4;

        _pointsBattery.setSize(pointSize);
        _pointsHumidity.setSize(pointSize);

        // again, just using lineHumidity as example of size.
        double minX = _lineHumidity.getLowestValueX();
        _graph.getViewport()
                .setMinX(minX);

        double maxX = _lineHumidity.getHighestValueX();
        _graph.getViewport()
                .setMaxX(maxX);

        // clears all series and adds newly added
        _graph.removeAllSeries();

        //_graph.addSeries(_lineHumidity);
        _graph.addSeries(_pointsHumidity);
        //_graph.addSeries(_lineBattery);
        _graph.addSeries(_pointsBattery);

        // legend config
        _pointsHumidity.setTitle("Humidity");
        _pointsHumidity.setColor(Color.BLUE);
        _pointsBattery.setTitle("Battery");
        _pointsBattery.setColor(Color.RED);


        _graph.getLegendRenderer().setVisible(true);
        _graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);


        _pointsHumidity.setOnDataPointTapListener(new OnDataPointTapListener() {
            @Override
            public void onTap(Series series, DataPointInterface dataPoint) {
                String msg = "Humidity is " + dataPoint.getY() + "%";
                Toast.makeText(getActivity(), msg + "\n" + getReadableTime(dataPoint.getX()), Toast.LENGTH_LONG).show();
            }
        });

        _pointsBattery.setOnDataPointTapListener(new OnDataPointTapListener() {
            @Override
            public void onTap(Series series, DataPointInterface dataPoint) {
                String msg = "Battery level is " + dataPoint.getY() + "%";
                Toast.makeText(getActivity(), msg + "\n" + getReadableTime(dataPoint.getX()), Toast.LENGTH_LONG).show();
            }
        });

    }

    private String getReadableTime(double time){
        SimpleDateFormat formatter = new SimpleDateFormat("EEE, dd. MMM yyyy  hh:mm:ss aa");
        return formatter.format(time);
    }

    private int findNumberOfDatapointsToShow(){
        int numberToShow = 0;
        if(numberOfDataPoints > maxNumberDatapointsPortrait){
            numberToShow = maxNumberDatapointsPortrait;
            if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                if(numberOfDataPoints > maxNumberDatapointsLandscape){
                    numberToShow = maxNumberDatapointsLandscape;
                }
            }
        }
        else{
            numberToShow = numberOfDataPoints;
        }
        return numberToShow;
    }

    private void SetupGraphLayout(){
        // TODO so much hardcoded shit here.
        _graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(getActivity(), new SimpleDateFormat("dd/MM-yy")));
        _graph.getViewport().setScalable(true);
        _graph.setTitle("Historical data");
        _graph.setTitleTextSize(60f);
        _graph.getGridLabelRenderer().setHorizontalAxisTitle("Time");
        _graph.getGridLabelRenderer().setPadding(70); // add padding for the graph to be fully shown for the max datapoint to be seen on the graph also
        _graph.getGridLabelRenderer().setHorizontalLabelsAngle(15); // makes horizontal date labels angled ( due to long label names)
        _graph.getViewport().setYAxisBoundsManual(true);
        _graph.getViewport().setMinY(0);
        _graph.getViewport().setMaxY(100);
    }
}
