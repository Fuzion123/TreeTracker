package com.apps.frederik.treetracker.Model.MonitoredProperty;


import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.apps.frederik.treetracker.Globals;
import com.apps.frederik.treetracker.Model.Reading.Reading;
import com.apps.frederik.treetracker.MonitorService;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;

/**
 * Created by Frederik on 12/20/2017.
 */

public class MonitoredProperty {
    private String Identifier;
    private List<Reading> Readings = new ArrayList<>();

    private transient DatabaseReference _dBRef;
    private transient Context _context;

    public String getIdentifier() {
        return Identifier;
    }

    public void setIdentifier(String identifier) {
        this.Identifier = identifier;
    }

    public List<Reading> getReadings() {
        return Readings;
    }

    public void setReadings(List<Reading> readings) {
        this.Readings = readings;
    }

    public boolean equals(Object obj) {
        if (obj instanceof MonitoredProperty) {
            MonitoredProperty prop = (MonitoredProperty) obj;

            if(!prop.getIdentifier().equals(this.Identifier)) return false;
            if(prop.getReadings().size() != this.Readings.size()) return false;

            int cnt = this.Readings.size();
            for (int i = 0; i < cnt; i++){
                if(!Readings.get(i).equals(prop.getReadings().get(i))) return false;
            }
            return true;
        }
        return false;
    }

    public void SetupDatabaseListeners(DatabaseReference ref, Context con){
        _dBRef = ref;
        _context = con;

        _dBRef.child("Readings").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Reading newReading = dataSnapshot.getValue(Reading.class);

                Iterator<Reading> it = Readings.iterator();
                while (it.hasNext()){
                    Reading r = it.next();

                    if(r.equals(newReading)){
                        return; // already added from initial pull
                    }
                }

                Readings.add(newReading); // new reading added
                Intent broadcast = new Intent(Globals.LOCAL_BROADCAST_NEW_READING);
                broadcast.putExtra(Globals.IDENTIFER, MonitoredProperty.this.getIdentifier());
                LocalBroadcastManager.getInstance(_context).sendBroadcast(broadcast);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {}

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Reading newReading = dataSnapshot.getValue(Reading.class);
                if(Readings.remove(newReading)){
                    Intent broadcast = new Intent(Globals.LOCAL_BROADCAST_READING_REMOVED);
                    broadcast.putExtra(Globals.IDENTIFER, MonitoredProperty.this.getIdentifier());
                    LocalBroadcastManager.getInstance(_context).sendBroadcast(broadcast);
                }
            }
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }
}
