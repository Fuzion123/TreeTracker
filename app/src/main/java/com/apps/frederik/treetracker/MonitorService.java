package com.apps.frederik.treetracker;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;


import com.apps.frederik.treetracker.Model.MonitoredObject.MonitoredObject;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.util.ArrayList;
import java.util.List;

public class MonitorService extends Service {//implements IModelEventListener {
    private List<MonitoredObject> _objects = new ArrayList<>();
    private IBinder _sensorBinder = new MonitorServiceBinder();
    private FirebaseDatabase dB = FirebaseDatabase.getInstance();
    private DatabaseReference _dBRef;


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
                Log.d("DB", "onChildAdded, key: " + dataSnapshot.getKey());
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
