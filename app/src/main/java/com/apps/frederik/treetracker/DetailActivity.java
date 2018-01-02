package com.apps.frederik.treetracker;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.apps.frederik.treetracker.Fragments.GraphFragment;
import com.apps.frederik.treetracker.Model.PropertiesReading.PropertiesReading;
import com.apps.frederik.treetracker.Model.Util.TimeStampHelper;
import com.apps.frederik.treetracker.MonitorService.MonitorServiceBinder;
import com.apps.frederik.treetracker.Model.MonitoredObject.MonitoredObject;
import com.jjoe64.graphview.series.DataPoint;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DetailActivity extends AppCompatActivity {
    private MonitorService.DetailActivityBinder _binder;
    private boolean _isBoundToService;
    private String UniqueDiscription;
    private String DetailType;
    private GraphFragment graphFragment;
    private final String GRAPH_FRAGMENT_TAG = "com.apps.frederik.treetracker.graph.fragment.tag";
    private TextView txtViewCurrent;
    private TextView txtViewCurrentDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        setTitle(Globals.APP_NAME);
        txtViewCurrent = findViewById(R.id.textViewCurrent);
        txtViewCurrentDate = findViewById(R.id.textViewCurrentDate);

        UniqueDiscription = getIntent().getExtras().getString(Globals.UNIQUE_DESCRIPTION);
        DetailType = getIntent().getExtras().getString(Globals.DETAIL_TYPE);

        Intent service = new Intent(this, MonitorService.class);
        bindService(service,_connection, Context.BIND_AUTO_CREATE);
    }

    private void Initialize(){
        _binder.SetupPropertiesReadingLister(UniqueDiscription);

        graphFragment = (GraphFragment) getSupportFragmentManager().findFragmentByTag(GRAPH_FRAGMENT_TAG);

        if(graphFragment != null) return;

        graphFragment = new GraphFragment();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container_detail, graphFragment, GRAPH_FRAGMENT_TAG)
                .commit();
    }

    private ServiceConnection _connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder binder) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            _binder = (MonitorService.DetailActivityBinder) binder;
            _isBoundToService = true;
            Initialize();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            _isBoundToService = false;
        }
    };

    @Override
    protected void onResume() {
        Log.d("OverviewActivity", "onResume");
        LocalBroadcastManager.getInstance(this).registerReceiver(onNewReadingAdded, new IntentFilter(Globals.LOCAL_BROADCAST_NEW_READING));
        LocalBroadcastManager.getInstance(this).registerReceiver(onReaddingRemoved, new IntentFilter(Globals.LOCAL_BROADCAST_READING_REMOVED));

        super.onResume();
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(onNewReadingAdded);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(onReaddingRemoved);
        super.onPause();
    }




    private BroadcastReceiver onNewReadingAdded = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            UpdateGraphFragment(intent);
        }
    };

    private BroadcastReceiver onReaddingRemoved = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
        UpdateGraphFragment(intent);
        }
    };

    @Override
    protected void onDestroy() {
        if(_isBoundToService){
            unbindService(_connection);
        }

        _binder.RemovePropertiesReadingListener(UniqueDiscription);
        super.onDestroy();
    }

    private void UpdateGraphFragment(Intent intent){
        if(_isBoundToService){

            // updating the historical values
            List<PropertiesReading> reads = new ArrayList<>(_binder.GetHistorical());
            List<DataPoint> data = new ArrayList<>();

            for (PropertiesReading r : reads)
            {
                Date time = TimeStampHelper.get_dataTime(r.getTimeStamp());
                double value = Double.valueOf(r.getProperties().get("humidity"));
                data.add(new DataPoint(time, value));
            }
            graphFragment.AddData(data);

            // update the current value
            PropertiesReading current = _binder.GetCurrent();
            SimpleDateFormat formatter = new SimpleDateFormat("EEE, dd. MMM yyyy  hh:mm:ss aa");
            String readableDate = formatter.format(TimeStampHelper.get_dataTime(current.getTimeStamp()));
            String value = current.getProperties().get("humidity") + "%"; // TODO hardcoded!!

            txtViewCurrent.setText(getString(R.string.current)+ " " + DetailType + ": " + value);
            txtViewCurrentDate.setText(getString(R.string.date) + ": " + readableDate);
        }
        else{
            throw new RuntimeException("Overview Activity was not bound to service, in a time where is should!");
        }
    }
}
