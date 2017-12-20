package com.apps.frederik.treetracker.Model.MonitoredObject;


import java.util.List;

import com.apps.frederik.treetracker.Model.Metadata.Metadata;
import com.apps.frederik.treetracker.Model.MonitoredProperty.MonitoredProperty;
import com.apps.frederik.treetracker.Model.Util.Coordinate;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Frederik on 12/20/2017.
 */

public class MonitoredObject {

    @SerializedName("Coordinate")
    @Expose
    private Coordinate coordinate;
    @SerializedName("Description")
    @Expose
    private String description;
    @SerializedName("Metadata")
    @Expose
    private Metadata meta;
    @SerializedName("MonitoredProperties")
    @Expose
    private List<MonitoredProperty> monitoredProperties = null;
    @SerializedName("UUID")
    @Expose
    private String uUID;

    public Coordinate getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(Coordinate coordinate) {
        this.coordinate = coordinate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Metadata getMeta() {
        return meta;
    }

    public void setMeta(Metadata meta) {
        this.meta = meta;
    }

    public List<MonitoredProperty> getMonitoredProperties() {
        return monitoredProperties;
    }

    public void setMonitoredProperties(List<MonitoredProperty> monitoredProperties) {
        this.monitoredProperties = monitoredProperties;
    }

    public String getUUID() {
        return uUID;
    }

    public void setUUID(String uUID) {
        this.uUID = uUID;
    }
}
