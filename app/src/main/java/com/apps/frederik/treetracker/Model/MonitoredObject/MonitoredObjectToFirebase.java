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

public class MonitoredObjectToFirebase {
    private Coordinate Coordinate;
    private String Description;
    private Metadata Metadata;
    private List<MonitoredProperty> MonitoredProperties = new ArrayList<>();
    private String UUID;

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
}
