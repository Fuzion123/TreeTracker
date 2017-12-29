package com.apps.frederik.treetracker.Model.MonitoredObject;


import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.apps.frederik.treetracker.Globals;
import com.apps.frederik.treetracker.Model.Metadata.Metadata;
import com.apps.frederik.treetracker.Model.MonitoredProperty.MonitoredProperty;
import com.apps.frederik.treetracker.Model.Reading.Reading;
import com.apps.frederik.treetracker.Model.Util.Coordinate;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Frederik on 12/20/2017.
 */

public class MonitoredObject {
    private Coordinate Coordinate;
    private String Description;
    private Metadata Metadata;
    private List<MonitoredProperty> MonitoredProperties = new ArrayList<>();
    private String UUID;
    @Exclude
    private DatabaseReference _dBRef;
    @Exclude
    private Context _context;

    public Coordinate getCoordinate() {
        return Coordinate;
    }

    public void setCoordinate(Coordinate coordinate) {
        this.Coordinate = coordinate;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        this.Description = description;
    }

    public Metadata getMetadata() {
        return Metadata;
    }

    public void setMetadata(Metadata meta) {
        this.Metadata = meta;
    }

    public List<MonitoredProperty> getMonitoredProperties() {
        return MonitoredProperties;
    }

    public void setMonitorGedProperties(List<MonitoredProperty> monitoredProperties) {
        this.MonitoredProperties = monitoredProperties;
    }

    public String getUUID() {
        return UUID;
    }

    public void setUUID(String uUID) {
        this.UUID = uUID;
    }

    @Exclude
    public void SetupDatabaseListeners(DatabaseReference ref, Context con){
        _dBRef = ref;
        _context = con;

        _dBRef.child("MonitoredProperties").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                MonitoredProperty newProp = dataSnapshot.getValue(MonitoredProperty.class);

                Iterator<MonitoredProperty> it = MonitoredProperties.iterator();

                while(it.hasNext()){
                    MonitoredProperty m = it.next();

                    if(m.equals(newProp)){
                        m.SetupDatabaseListeners(_dBRef.child("MonitoredProperties").child(dataSnapshot.getKey()), _context); // TODO this is a dirty hack to initialize listeners for already added properties
                        return; // already added from initial pull
                    }
                }

                MonitoredProperties.add(newProp); // adds a new MonitoredProperty
                MonitoredProperties.get(MonitoredProperties.size()-1).SetupDatabaseListeners(_dBRef.child("MonitoredProperties").child(dataSnapshot.getKey()),_context);

                Intent broadcast = new Intent(Globals.LOCAL_BROADCAST_NEW_MON_PROPERTY);
                LocalBroadcastManager.getInstance(_context).sendBroadcast(broadcast);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                MonitoredProperty newProp = dataSnapshot.getValue(MonitoredProperty.class);
                if(MonitoredProperties.remove(newProp)){
                    Intent broadcast = new Intent(Globals.LOCAL_BROADCAST_MON_PROPERTY_REMOVED);
                    LocalBroadcastManager.getInstance(_context).sendBroadcast(broadcast);
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
