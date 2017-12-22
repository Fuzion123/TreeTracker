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
import android.widget.Toast;

import com.apps.frederik.treetracker.Fragments.GraphFragment;
import com.apps.frederik.treetracker.Model.MonitoredProperty.MonitoredProperty;
import com.apps.frederik.treetracker.MonitorService.MonitorServiceBinder;
import com.apps.frederik.treetracker.Model.MonitoredObject.MonitoredObject;

public class DetailActivity extends AppCompatActivity {
    private MonitorServiceBinder _binder;
    private boolean _isBoundToService;
    private MonitoredObject _object;
    private String UUID = null;
    private GraphFragment graphFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        setTitle(Globals.APP_NAME);

        UUID = getIntent().getExtras().getString(Globals.UUID);

        Intent service = new Intent(this, MonitorService.class);
        bindService(service,_connection, Context.BIND_AUTO_CREATE);
    }

    private void Initialize(){
        _object = _binder.GetMonitoredObjectFor(UUID);

        if(_object.getMonitoredProperties().size() == 0)
        {
            Toast.makeText(this, "No readings is yet available!", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        graphFragment = new GraphFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container_detail, graphFragment).commit();
        graphFragment.SetMonitoredProperty(_object.getMonitoredProperties().get(0));
    }

    private ServiceConnection _connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder binder) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            _binder = (MonitorService.MonitorServiceBinder) binder;
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
            if(_isBoundToService){
                String identifier = intent.getExtras().getString(Globals.IDENTIFER);
                MonitoredProperty prop = _binder.GetMonitoredPropertyFor(DetailActivity.this.UUID, identifier);
                int lastVal = prop.getReadings().size();
                graphFragment.AddReading(prop.getReadings().get(lastVal-1));
            }
            else{
                throw new RuntimeException("Overview Activity was not bound to service, in a time where is should!");
            }
        }
    };

    private BroadcastReceiver onReaddingRemoved = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("OverviewActivity", "New Reading Added");
        }
    };

    @Override
    protected void onDestroy() {
        if(_isBoundToService){
            unbindService(_connection);
        }
        super.onDestroy();
    }
}
