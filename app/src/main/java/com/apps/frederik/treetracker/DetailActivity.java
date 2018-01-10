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
import android.widget.TextView;

import com.apps.frederik.treetracker.Fragments.GraphFragment;
import com.apps.frederik.treetracker.Model.PropertiesReading.PropertiesReading;
import com.apps.frederik.treetracker.Model.Util.TimeStampHelper;
import com.jjoe64.graphview.series.DataPoint;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DetailActivity extends AppCompatActivity {
    private final String NOT_BOUND_TO_SERVICE_EXCEPTION_MESSAGE = "Detail Activity was not bound to service, in a time where is should!";
    private final String DATE_FORMAT_PATTERN = "EEE, dd. MMM yyyy  hh:mm:ss aa";
    private MonitorService.DetailActivityBinder _binder;
    private boolean _isBoundToService;
    private String UniqueDescription;
    private GraphFragment graphFragment;
    private TextView txtViewCurrent;
    private TextView txtViewCurrentDate;
    private String humidityString = "humidity"; // used to not misspell
    private String batteryString = "battery"; // used to not misspell

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        setTitle(Globals.APP_NAME);
        txtViewCurrent = findViewById(R.id.textViewCurrent);
        txtViewCurrentDate = findViewById(R.id.textViewCurrentDate);

        UniqueDescription = getIntent().getExtras().getString(Globals.UNIQUE_DESCRIPTION);

        // starts bound service
        Intent service = new Intent(this, MonitorService.class);
        bindService(service,_connection, Context.BIND_AUTO_CREATE);
    }

    private void InitializeFragment(){
        // sets up the listener for properties for the specified UniqueDiscription.
        _binder.SetupPropertiesReadingLister(UniqueDescription);


        graphFragment = (GraphFragment) getSupportFragmentManager().findFragmentByTag(GRAPH_FRAGMENT_TAG);

        if(graphFragment != null) return;

        // instantiates the GraphFragment if != null
        graphFragment = new GraphFragment();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container_detail, graphFragment, GRAPH_FRAGMENT_TAG)
                .commit();
    }

    private ServiceConnection _connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder binder) {
            _binder = (MonitorService.DetailActivityBinder) binder;
            _isBoundToService = true;
            InitializeFragment();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            _isBoundToService = false;
        }
    };

    @Override
    protected void onResume() {
        LocalBroadcastManager.getInstance(this).registerReceiver(onReadingAdded, new IntentFilter(Globals.LOCAL_BROADCAST_NEW_READING));
        LocalBroadcastManager.getInstance(this).registerReceiver(onReadingRemoved, new IntentFilter(Globals.LOCAL_BROADCAST_READING_REMOVED));

        super.onResume();
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(onReadingAdded);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(onReadingRemoved);
        super.onPause();
    }

    private BroadcastReceiver onReadingAdded = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            UpdateGraphFragment(intent);
        }
    };

    private BroadcastReceiver onReadingRemoved = new BroadcastReceiver() {
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

        _binder.RemovePropertiesReadingListener(UniqueDescription);
        super.onDestroy();
    }

    private void UpdateGraphFragment(Intent intent){
        if(!_isBoundToService) {
            throw new RuntimeException(NOT_BOUND_TO_SERVICE_EXCEPTION_MESSAGE);
        }

        // updates the historical readings in the graph
        UpdateHistoricalReadingUI();

        // updates textView that displays the last readings.s
        UpdateCurrentReadingUI();
    }

    private void UpdateHistoricalReadingUI(){
        // updating the historical values
        List<PropertiesReading> reads = new ArrayList<>(_binder.GetHistorical());
        Map<String, List<DataPoint>> data = new HashMap<>();
        data.put(humidityString, new ArrayList<DataPoint>()); // adds entry "humidity"
        data.put(batteryString, new ArrayList<DataPoint>()); // addes entry "battery"

        // adds datapoints in the value field in the hashmap for every historical reading
        // a datapoint is a (X,Y) coordinate set of time and a value (humidity or battery level)
        for (PropertiesReading r : reads)
        {
            Date time = TimeStampHelper.get_dataTime(r.getTimeStamp());
            double humidity = Double.valueOf(r.getProperties().get(humidityString));
            double battery = Double.valueOf(r.getProperties().get(batteryString));

            data.get(humidityString).add(new DataPoint(time, humidity));
            data.get(batteryString).add(new DataPoint(time, battery));
        }

        graphFragment.AddData(data);
    }

    private void UpdateCurrentReadingUI(){
        // update the current value
        PropertiesReading current = _binder.GetCurrent();
        SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT_PATTERN);
        String readableDate = formatter.format(TimeStampHelper.get_dataTime(current.getTimeStamp()));
        String valueHumidity = current.getProperties().get("humidity") + "%";
        String valueBattery = current.getProperties().get(batteryString) + "%";

        txtViewCurrent.setText(getString(R.string.current)+ " " + humidityString + ": " + valueHumidity + ", " + batteryString + ": " + valueBattery);
        txtViewCurrentDate.setText(getString(R.string.date) + ": " + readableDate);
    }
}
