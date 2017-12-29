package com.apps.frederik.treetracker.Model.DataAccessLayer;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.apps.frederik.treetracker.Globals;
import com.apps.frederik.treetracker.Model.MonitoredObject.MonitoredObject;
import com.apps.frederik.treetracker.Model.MonitoredProperty.MonitoredProperty;
import com.apps.frederik.treetracker.Model.Reading.Reading;
import com.apps.frederik.treetracker.MonitorService;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by frede on 22/12/2017.
 */

public class DatabaseRepository {
    private DatabaseReference _dBRef;
    private List<MonitoredObject> _objects = new ArrayList<>();
    private List<MonitoredObject> _unmappedObjects = new ArrayList<>();
    private Context _context;

    public DatabaseRepository(String relativePath, Context context){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        _dBRef = database.getReference(relativePath);
        _context = context;

        _dBRef.child("MonitoredObjects").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                MonitoredObject obj = dataSnapshot.getValue(MonitoredObject.class);
                _objects.add(obj);
                _objects.get(_objects.size()-1).SetupDatabaseListeners(_dBRef.child("MonitoredObjects").child(dataSnapshot.getKey()), _context);

                Intent broadcast = new Intent(Globals.LOCAL_BROADCAST_NEW_MONITORED_OBJECT_ADDED);
                broadcast.putExtra(Globals.UUID, obj.getUUID());
                LocalBroadcastManager.getInstance(_context).sendBroadcast(broadcast);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {}

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d("DB", "onChildRemoved, key: " + dataSnapshot.getKey());
                MonitoredObject obj = dataSnapshot.getValue(MonitoredObject.class);

                // obj was actually removed
                if(_objects.remove(obj)){
                    Intent broadcast = new Intent(Globals.LOCAL_BROADCAST_MONITORED_OBJECT_REWMOVED);
                    broadcast.putExtra(Globals.UUID, obj.getUUID());
                    LocalBroadcastManager.getInstance(_context).sendBroadcast(broadcast);
                }
            }
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });


        _dBRef.child("AvailableSensorPackages").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                MonitoredObject obj = dataSnapshot.getValue(MonitoredObject.class);
                _unmappedObjects.add(obj);
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {}
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    public List<MonitoredObject> GetAllMonitoredObjects(){
        return _objects;
    }

    public List<MonitoredObject> GetAllUnmappedObjects(){
        return _unmappedObjects;
    }

    public MonitoredObject getMonitoredObjectFor(String uuid){
        int cnt = _objects.size();
        for (int i = 0; i < cnt; i++){
            if(_objects.get(i).getUUID().equals(uuid)){
                return _objects.get(i);
            }
        }
        return null;
    }

    public MonitoredProperty getMonitoredObjectsPropertyFor(String uuid , String identifier){
        MonitoredObject obj = getMonitoredObjectFor(uuid);

        if(obj == null) return null;

        int cnt = obj.getMonitoredProperties().size();

        for(int i = 0; i < cnt ; i++){
            if(obj.getMonitoredProperties().get(i).getIdentifier().equals(identifier)){
                return obj.getMonitoredProperties().get(i);
            }
        }
        return null;
    }

    public List<MonitoredProperty> getAllMonitoredPropertiesFor(String uuid){
        MonitoredObject obj = getMonitoredObjectFor(uuid);
        if(obj == null) return null;

        return obj.getMonitoredProperties();
    }

    public boolean sensorPackageWithUuidExists(String uuid){
        int cnt = _unmappedObjects.size();
        for(int i = 0; i<cnt; i++){
            if(_unmappedObjects.get(i).getUUID().equals(uuid)){
                return true;
            }
        }
        return false;
    }

    public void addMonitoredObject(MonitoredObject obj){
        if(!sensorPackageWithUuidExists(obj.getUUID())){
            throw new RuntimeException("this method not be called if sensorPackageWithUuidExists returned false!!");
        }

        int cnt = _unmappedObjects.size();
        List<MonitoredProperty> props = new ArrayList<>();
        for(int i = 0; i<cnt; i++){
            if(_unmappedObjects.get(i).getUUID().equals(obj.getUUID())){
                props = _unmappedObjects.get(i).getMonitoredProperties();
                obj.setMonitorGedProperties(props);
            }
        }

        int newMonitoredObjectsDbArray = _objects.size()-1+1; // fun fact, I use size directly, because entry number 0 in array evens out that we need to Add an entry.
        String path = "MonitoredObjects/" + String.valueOf(newMonitoredObjectsDbArray);
        _dBRef.child(path).setValue(obj);
    }
}
