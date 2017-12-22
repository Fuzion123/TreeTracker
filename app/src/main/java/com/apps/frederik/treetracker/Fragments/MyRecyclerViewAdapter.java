package com.apps.frederik.treetracker.Fragments;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.apps.frederik.treetracker.Model.MonitoredObject.MonitoredObject;
import com.apps.frederik.treetracker.R;
import java.util.List;

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.MonitoredObjectViewHolder> {

    private List<MonitoredObject> mValues;
    private final ListFragment.OnListFragmentInteractionListener mListener;

    public MyRecyclerViewAdapter(List<MonitoredObject> items, ListFragment.OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public MonitoredObjectViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_sensor, parent, false);
        return new MonitoredObjectViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MonitoredObjectViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mDiscriptionView.setText(mValues.get(position).getDescription());
        //TODO This is not the latest reading, that is being shown in the view.
        String lastReading = mValues.get(position).getMonitoredProperties().get(0).getReadings().get(0).getData().toString();
        holder.mLastReadingView.setText(lastReading);
        holder.mId.setText(mValues.get(position).getUUID());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    public void AddData(MonitoredObject obj){
        mValues.add(obj);
    }

    public void AddAllData(List<MonitoredObject> objs){
        mValues = objs;
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class MonitoredObjectViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mDiscriptionView;
        public final TextView mLastReadingView;
        public final TextView mId;
        public MonitoredObject mItem;

        public MonitoredObjectViewHolder(View view) {
            super(view);
            mView = view;
            mDiscriptionView = view.findViewById(R.id.content);
            mLastReadingView = view.findViewById(R.id.lastReading);
            mId = view.findViewById(R.id.id);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mDiscriptionView.getText() + "'";
        }
    }
}
