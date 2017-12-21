package com.apps.frederik.treetracker.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.apps.frederik.treetracker.Model.MonitoredProperty.MonitoredProperty;
import com.apps.frederik.treetracker.Model.Reading.Reading;
import com.apps.frederik.treetracker.R;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import java.util.ArrayList;
import java.util.List;

public class GraphFragment extends Fragment implements IActivityToFragmentCommunication {
    private MonitoredProperty _property;
    private GraphView graph;
    LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_graph, container, false);

        graph = view.findViewById(R.id.graph);
        graph.addSeries(series);
        return view;
    }

    @Override
    public void SetData(Object object) {
        _property = (MonitoredProperty) object;
        PopulateGraph();
    }

    private void PopulateGraph(){
        List<DataPoint> data = new ArrayList<>();

        for (Reading r : _property.getReadings()){
            data.add(new DataPoint(r.getTimeStamp(), r.getData()));
        }

        DataPoint[] dataArray = data.toArray(new DataPoint[data.size()]);
        series = new LineGraphSeries<DataPoint>(dataArray);

        if(graph != null){
            graph.addSeries(series);
        }
    }
}
