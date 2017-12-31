package com.apps.frederik.treetracker.Model.DataAccessLayer;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.apps.frederik.treetracker.Globals;
import com.apps.frederik.treetracker.Model.MonitoredObject.MonitoredObject;
import com.apps.frederik.treetracker.Model.PropertiesReading.PropertiesReading;
import com.apps.frederik.treetracker.Model.Util.Coordinate;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by frede on 22/12/2017.
 */

public class DatabaseRepository {
    private DatabaseReference _dBRef;
    private List<MonitoredObject> _objects = new ArrayList<>();
    private MonitoredObject _detailedMonitoredObject = new MonitoredObject();
    private Context _context;

    public DatabaseRepository(String relativePath, Context context) {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        _dBRef = database.getReference(relativePath);
        _context = context;

        SetupDbListenerForMonitoredObjects();
        //SetupDbListenersForPropertiesForMonitoredObject("tree_0000");
    }

    public List<MonitoredObject> GetAllMonitoredObjects(){
        return _objects;
    }

    public MonitoredObject GetMonitoredObjectFor(String discription){
        int cnt = _objects.size();
        for (int i = 0; i < cnt; i++){
            if(_objects.get(i).getUniqueDescription().equals(discription)){
                return _objects.get(i);
            }
        }
        return null;
    }

    public PropertiesReading GetCurrent(){
        PropertiesReading current = _detailedMonitoredObject.getCurrent();
        if(current == null) return null;
        return current;
    }

    public List<PropertiesReading> GetHistorical(){
        List<PropertiesReading> hist = _detailedMonitoredObject.getHistorical();
        if(hist == null) return null;
        return hist;
    }

    public void addMonitoredObject(MonitoredObject obj){
        throw new UnsupportedOperationException();
    }

    // firebase real-time database listeners
    public void SetupDbListenerForMonitoredObjects(){
        _dBRef.child("monitored_objects").addChildEventListener(MonitoredObjectChildListener);
    }

    public void RemoveListenerForMonitoredObjects(){
        _dBRef.child("monitored_objects").removeEventListener(MonitoredObjectChildListener);
    }

    public void SetupDbListenersForPropertiesForMonitoredObject(String id){
        _dBRef.child("monitored_objects").child(id).child("properties").addChildEventListener(PropertiesChildListener);
    }

    public void RemoveListenerForPropertiesForMonitoredObject(String id){
        _dBRef.child("monitored_objects").child(id).child("properties").removeEventListener(PropertiesChildListener);
    }

    private ChildEventListener MonitoredObjectChildListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            String description = dataSnapshot.getKey();

            // if != null, it means it already is received.
            //if(GetMonitoredObjectFor(description) != null) return;


            MonitoredObject obj = new MonitoredObject();

            // sets the description of the
            obj.setUniqueDescription(description);

            // deserialize the Coordinate of the MonitoredObject
            obj.setCoordinate(dataSnapshot.child("coordinate").getValue(Coordinate.class));

            // deserialize the monitored properties
            DataSnapshot snapshot = dataSnapshot.child("properties").child("current");
            obj.setCurrent(PropertiesReadingFromDataSnapshot(snapshot));

            // finally adds the monitored object.
            _objects.add(obj);

            Intent broadcast = new Intent(Globals.LOCAL_BROADCAST_NEW_MONITORED_OBJECT_ADDED);
            broadcast.putExtra(Globals.UNIQUE_DESCRIPTION, obj.getUniqueDescription());
            LocalBroadcastManager.getInstance(_context).sendBroadcast(broadcast);
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
    };

    private ChildEventListener PropertiesChildListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            String key = dataSnapshot.getKey();
            String id = dataSnapshot.getRef().getParent().getParent().getKey();

            _detailedMonitoredObject.setUniqueDescription(id);

            if(key.equals("current")){
                PropertiesReading current = PropertiesReadingFromDataSnapshot(dataSnapshot);
                _detailedMonitoredObject.setCurrent(current);
            }
            // else it is for "historical"
            else{
                Intent broadcast = new Intent(Globals.LOCAL_BROADCAST_NEW_READING);
                _detailedMonitoredObject.setHistorical(new ArrayList<PropertiesReading>());
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    _detailedMonitoredObject.getHistorical().add(PropertiesReadingFromDataSnapshot(snapshot));

                    // notifies listeners that a new reading has been captured
                    LocalBroadcastManager.getInstance(_context).sendBroadcast(broadcast);
                }
            }
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            String key = dataSnapshot.getKey();

            if(key.equals("current")){
                PropertiesReading read = PropertiesReadingFromDataSnapshot(dataSnapshot);
                _detailedMonitoredObject.setCurrent(read);
                _detailedMonitoredObject.getHistorical().add(read);

                // notifies listeners that a new reading has been captured
                Intent broadcast = new Intent(Globals.LOCAL_BROADCAST_NEW_READING);
                LocalBroadcastManager.getInstance(_context).sendBroadcast(broadcast);
            }
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
    };

    private PropertiesReading PropertiesReadingFromDataSnapshot(DataSnapshot dataSnapshot){
        PropertiesReading propsRead = new PropertiesReading();
        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
            if (snapshot.getKey().equals("timestamp")) {
                // uses the phones current timezone to adjust the time. (+1 hour for dk time)
                TimeZone tz = TimeZone.getTimeZone("UTC");
                Calendar c = Calendar.getInstance(tz);
                c.setTime(new Date(snapshot.getValue().toString()));
                propsRead.setTimeStamp(c.getTime().toString());
            }
            else{
                propsRead.getProperties().put(snapshot.getKey(), snapshot.getValue().toString());
            }
        }
        return propsRead;
    }
}

