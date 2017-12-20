package com.apps.frederik.treetracker.Model.MonitoredProperty;


import java.util.List;

import com.apps.frederik.treetracker.Model.Reading.Reading;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Frederik on 12/20/2017.
 */

public class MonitoredProperty {
    @SerializedName("Identifier")
    @Expose
    private String identifier;
    @SerializedName("Readings")
    @Expose
    private List<Reading> readings = null;

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public List<Reading> getReadings() {
        return readings;
    }

    public void setReadings(List<Reading> readings) {
        this.readings = readings;
    }
}
