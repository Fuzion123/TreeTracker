package com.apps.frederik.treetracker;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;


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
    private List<MonitoredObject> _objects = new ArrayList<>();
    private IBinder _sensorBinder = new MonitorServiceBinder();
    private FirebaseDatabase dB = FirebaseDatabase.getInstance();
    private DatabaseReference _dBRef;
    private List<MonitoredObject> _unMappedObjects = new ArrayList<>();

    @Override
    public IBinder onBind(Intent intent) {
        return _sensorBinder;
    }

    @Override
    public void onCreate() {

        _dBRef = dB.getReference("/Users/Fuzion123");

        _dBRef.child("MonitoredObjects").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                MonitoredObject obj = dataSnapshot.getValue(MonitoredObject.class);
                obj.SetupDatabaseListeners(_dBRef.child("MonitoredObjects").child(dataSnapshot.getKey()), MonitorService.this);
                _objects.add(obj);

                Intent broadcast = new Intent(Globals.LOCAL_BROADCAST_NEW_MONITORED_OBJECT_ADDED);
                broadcast.putExtra(Globals.UUID, obj.getUUID());
                LocalBroadcastManager.getInstance(MonitorService.this).sendBroadcast(broadcast);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Log.d("DB", "onChildChanged, key: " + dataSnapshot.getKey());
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d("DB", "onChildRemoved, key: " + dataSnapshot.getKey());
                MonitoredObject obj = dataSnapshot.getValue(MonitoredObject.class);

                // obj was actually removed
                if(_objects.remove(obj)){
                    Intent broadcast = new Intent(Globals.LOCAL_BROADCAST_MONITORED_OBJECT_REWMOVED);
                    broadcast.putExtra(Globals.UUID, obj.getUUID());
                    LocalBroadcastManager.getInstance(MonitorService.this).sendBroadcast(broadcast);
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                Log.d("DB", "onChildMoved, key: " + dataSnapshot.getKey());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("DB", "onCancelled, error msg: " + databaseError.getMessage());
            }
        });


        _dBRef.child("AvailableSensorPackages").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                MonitoredObject obj = dataSnapshot.getValue(MonitoredObject.class);
                _unMappedObjects.add(obj);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

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
        MonitoredObject GetMonitoredObjectFor(String uuid)
        {
            return getMoniToredObjectFor(uuid);
        }
        List<MonitoredObject> GetAllMonitoredObjects(){
            return _objects;
        }
        MonitoredProperty GetMonitoredPropertyFor(String uuid, String identifer){ return getMonitoredObjectsPropertyFor(uuid, identifer); }
        List<MonitoredProperty> GetAllMonitoredPropertiesFor(String uuid){ return getAllMonitoredPropertiesFor(uuid); }
        boolean SensorPackageWithUuidExists(String uuid) {return sensorPackageWithUuidExists(uuid);}
        void AddMonitoredObject(MonitoredObject obj){ addMonitoredObject(obj);}
    }

    private MonitoredObject getMoniToredObjectFor(String uuid){
        int cnt = _objects.size();
        for (int i = 0; i < cnt; i++){
            if(_objects.get(i).getUUID().equals(uuid)){
                return _objects.get(i);
            }
        }
        return null;
    }

    private MonitoredProperty getMonitoredObjectsPropertyFor(String uuid ,String identifier){
        MonitoredObject obj = getMoniToredObjectFor(uuid);

        if(obj == null) return null;

        int cnt = obj.getMonitoredProperties().size();

        for(int i = 0; i < cnt ; i++){
            if(obj.getMonitoredProperties().get(i).getIdentifier().equals(identifier)){
                return obj.getMonitoredProperties().get(i);
            }
        }
        return null;
    }

    private List<MonitoredProperty> getAllMonitoredPropertiesFor(String uuid){
        MonitoredObject obj = getMoniToredObjectFor(uuid);
        if(obj == null) return null;

        return obj.getMonitoredProperties();
    }

    private boolean sensorPackageWithUuidExists(String uuid){
        int cnt = _unMappedObjects.size();

        for(int i = 0; i<cnt; i++){
            if(_unMappedObjects.get(i).getUUID().equals(uuid)){
                return true;
            }
        }
        return false;
    }

    private void addMonitoredObject(MonitoredObject obj){
        if(!sensorPackageWithUuidExists(obj.getUUID())){
            throw new RuntimeException("this method not be called if sensorPackageWithUuidExists returned false!!");
        }



        //_dBRef.child("AvailableSensorPackages/"+ dbArrayNum).removeValue(); // TODO incomment this should delete available sensor packages

        int cnt = _unMappedObjects.size();
        List<MonitoredProperty> props = new ArrayList<>();
        for(int i = 0; i<cnt; i++){
            if(_unMappedObjects.get(i).getUUID().equals(obj.getUUID())){
                props = _unMappedObjects.get(i).getMonitoredProperties();
                obj.setMonitorGedProperties(props);
            }
        }


        int newMonitoredObjectsDbArray = _objects.size()-1+1; // fun fact, I use size directly, because entry number 0 in array evens out that we need to Add an entry.

        String path = "MonitoredObjects/" + String.valueOf(newMonitoredObjectsDbArray);

        _dBRef.child(path).setValue(obj);
    }
}
