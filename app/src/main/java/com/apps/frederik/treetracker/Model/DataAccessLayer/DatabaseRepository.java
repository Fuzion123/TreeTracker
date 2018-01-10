package com.apps.frederik.treetracker.Model.DataAccessLayer;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.apps.frederik.treetracker.Globals;
import com.apps.frederik.treetracker.Model.MonitoredObject.MonitoredObject;
import com.apps.frederik.treetracker.Model.PropertiesReading.PropertiesReading;
import com.apps.frederik.treetracker.Model.Util.Coordinate;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
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
            MonitoredObject obj = new MonitoredObject();

            // sets the description of the
            obj.setUniqueDescription(description);

            // deserialize the Coordinate of the MonitoredObject
            obj.setCoordinate(dataSnapshot.child("coordinate").getValue(Coordinate.class));

            // deserialize the monitored properties
            // DataSnapshot snapshot = dataSnapshot.child("properties").child("current");
            // obj.setCurrent(PropertiesReadingFromDataSnapshot(snapshot));

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

            if(key.equals("historical")){
                _detailedMonitoredObject.setHistorical(new ArrayList<PropertiesReading>());

                Iterator<DataSnapshot> it = dataSnapshot.getChildren().iterator();

                // sets historical readings
                PropertiesReading current = null; // used to set the current by using the last value of the historical
                while(it.hasNext()){
                    DataSnapshot snapshot = it.next();
                    current = PropertiesReadingFromDataSnapshot(snapshot);
                    _detailedMonitoredObject.getHistorical().add(current);
                }
                // sets current reading.
                _detailedMonitoredObject.setCurrent(current);

                // notifies listeners that a new reading has been captured
                Intent broadcast = new Intent(Globals.LOCAL_BROADCAST_NEW_READING);
                LocalBroadcastManager.getInstance(_context).sendBroadcast(broadcast);
            }
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            String key = dataSnapshot.getKey();

            if(key.equals("historical")){
                Iterator<DataSnapshot> it = dataSnapshot.getChildren().iterator();
                PropertiesReading current = null; // used to set current value after the while loop.
                while(it.hasNext()){
                    DataSnapshot snapshot = it.next();
                    current = PropertiesReadingFromDataSnapshot(snapshot);
            }

                _detailedMonitoredObject.setCurrent(current); // sets the current
                _detailedMonitoredObject.getHistorical().add(current); // adds to historical

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
        PropertiesReading propsReading = new PropertiesReading();

        // sets the timestamp
        String time = convertTime(dataSnapshot.getKey());
        propsReading.setTimeStamp(time);

        // adds all properties (humidity and battery)
        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
            propsReading.getProperties().put(snapshot.getKey(), snapshot.getValue().toString());
        }

        return propsReading;
    }

    private String convertTime(String time){
        String regx = "\\.[0-9]*";
        time = time.replaceAll(regx, "");
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'");
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));

        Date d = null;
        try{
            d = formatter.parse(time);
        }catch (ParseException e){
            throw new RuntimeException("time could not be parsed be SimpleFormatter");
        }

        String readableTime = d.toString();
        return readableTime;
    }
}

