package com.apps.frederik.treetracker.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.apps.frederik.treetracker.Model.MonitoredObject.MonitoredObject;
import com.apps.frederik.treetracker.R;
import java.util.ArrayList;
import java.util.List;

public class ListFragment extends MonitoredObjectFragment {
    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    private RecyclerView _recyclerView;
    private List<MonitoredObject> _objects = new ArrayList<>();

    public ListFragment() {
    }

    @SuppressWarnings("unused")
    public static ListFragment newInstance(int columnCount) {
        ListFragment fragment = new ListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sensor_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            _recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                _recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                _recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            _recyclerView.setAdapter(new MyRecyclerViewAdapter(_objects, mListener));
            _recyclerView.getAdapter().notifyDataSetChanged();
        }
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void AddMonitoredObject(MonitoredObject obj) {
        if(!_objects.contains(obj)){
            _objects.add(obj);
        }

        if(_recyclerView == null) return;

        ((MyRecyclerViewAdapter)_recyclerView.getAdapter()).SetMonitoredObjects(_objects);
        _recyclerView.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void RemoveMonitoredObjectFor(String uuid){
        int cnt = _objects.size();
        for(int i = 0; i<cnt;i++){
            if(_objects.get(i).getUniqueDescription().equals(uuid)){
                _objects.remove(i);
                ((MyRecyclerViewAdapter)_recyclerView.getAdapter()).SetMonitoredObjects(_objects);
                _recyclerView.getAdapter().notifyDataSetChanged();
                return;
            }
        }
    }

    @Override
    public void RefreshAllMonitoredObject(List<MonitoredObject> objs) {
        _objects = new ArrayList<>(objs);

        if(_recyclerView == null) return;

        ((MyRecyclerViewAdapter)_recyclerView.getAdapter()).SetMonitoredObjects(_objects);
        _recyclerView.getAdapter().notifyDataSetChanged();
    }

    @Override
    public List<MonitoredObject> GetMonitoredObject(){
        return _objects;
    }


    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(MonitoredObject item);
    }
}
