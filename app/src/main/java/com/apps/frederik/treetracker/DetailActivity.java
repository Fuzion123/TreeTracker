package com.apps.frederik.treetracker;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.apps.frederik.treetracker.MonitorService.MonitorServiceBinder;
import com.apps.frederik.treetracker.Model.MonitoredObject.MonitoredObject;

import java.util.List;

public class DetailActivity extends AppCompatActivity {
    private MonitorServiceBinder _binder;
    private boolean _isBoundToService;
    private MonitoredObject _object;
    private String UUID = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        setTitle(Globals.APP_NAME);

        UUID = getIntent().getExtras().getString(Globals.UUID_DETAILED_MONITORED_OBJECT);

        Intent service = new Intent(this, MonitorService.class);
        bindService(service,_connection, Context.BIND_AUTO_CREATE);
    }

    private void Initialize(){
        _object = _binder.GetMonitoredObjectFor(UUID);
        Toast.makeText(this, "UUID: " + UUID, Toast.LENGTH_LONG).show();
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
    protected void onDestroy() {
        if(_isBoundToService){
            unbindService(_connection);
        }
        super.onDestroy();
    }
}
