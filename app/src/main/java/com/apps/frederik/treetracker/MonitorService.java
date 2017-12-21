package com.apps.frederik.treetracker;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;


import com.apps.frederik.treetracker.Model.DataAccessLayer.FakeRepository;
import com.apps.frederik.treetracker.Model.MonitoredObject.MonitoredObject;


import java.util.ArrayList;
import java.util.List;

public class MonitorService extends Service {//implements IModelEventListener {
    private List<MonitoredObject> _objects = new ArrayList<>();
    private IBinder _sensorBinder = new MonitorServiceBinder();

    @Override
    public IBinder onBind(Intent intent) {
        return _sensorBinder;
    }

    @Override
    public void onCreate() {
        _objects = new FakeRepository(this).GenerateFakeModel();
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    /*
    @Override
    public void onModelChangedEvent(Object sender, ModelEventArgs args) {
        Intent broadcast;

        if(args.Sensor != null){
            IMonitoredProperty sensor = args.Sensor;
            broadcast = new Intent(Globals.LOCAL_BROADCAST_NEW_SENSOR_ADDED);
        }

        else{
            IReading reading = args.Reading;
            broadcast = new Intent(Globals.LOCAL_BROADCAST_NEW_READING);
        }

        LocalBroadcastManager.getInstance(this).sendBroadcast(broadcast);
    }
    */


    public class MonitorServiceBinder extends Binder {
        MonitoredObject GetMonitoredObjectFor(String uuid)
        {
            return GetMoniToredObjectFor(uuid);
        }

        List<MonitoredObject> GetAllMonitoredObjects(){
            return _objects;
        }
    }

    private MonitoredObject GetMoniToredObjectFor(String uuid){
        int cnt = _objects.size();
        for (int i = 0; i < cnt; i++){
            if(_objects.get(i).getUUID().equals(uuid)){
                return _objects.get(i);
            }
        }
        return null;
    }
}
