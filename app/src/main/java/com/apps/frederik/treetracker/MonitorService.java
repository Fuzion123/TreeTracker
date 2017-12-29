package com.apps.frederik.treetracker;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;


import com.apps.frederik.treetracker.Model.DataAccessLayer.DatabaseRepository;
import com.apps.frederik.treetracker.Model.InternalCommunication.SensorEventArgs;
import com.apps.frederik.treetracker.Model.MonitoredObject.MonitoredObject;
import com.apps.frederik.treetracker.Model.MonitoredObject.MonitoredObjectToFirebase;
import com.apps.frederik.treetracker.Model.MonitoredProperty.MonitoredProperty;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MonitorService extends Service {//implements IModelEventListener {
    private IBinder _sensorBinder = new MonitorServiceBinder();
    private DatabaseRepository _dBRepository;

    @Override
    public IBinder onBind(Intent intent) {
        return _sensorBinder;
    }

    @Override
    public void onCreate() {
        _dBRepository = new DatabaseRepository("/Users/Fuzion123", this);

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


    public class MonitorServiceBinder extends Binder {
        MonitoredObject GetMonitoredObjectFor(String uuid) { return _dBRepository.getMonitoredObjectFor(uuid); }
        List<MonitoredObject> GetAllMonitoredObjects(){ return _dBRepository.GetAllMonitoredObjects(); }
        MonitoredProperty GetMonitoredPropertyFor(String uuid, String identifer){ return _dBRepository.getMonitoredObjectsPropertyFor(uuid, identifer); }
        List<MonitoredProperty> GetAllMonitoredPropertiesFor(String uuid){ return _dBRepository.getAllMonitoredPropertiesFor(uuid); }
        boolean SensorPackageWithUuidExists(String uuid) {return _dBRepository.sensorPackageWithUuidExists(uuid);}
        void AddMonitoredObject(MonitoredObject obj){ _dBRepository.addMonitoredObject(obj);}
    }
}
