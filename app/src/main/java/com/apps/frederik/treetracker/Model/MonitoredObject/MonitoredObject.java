package com.apps.frederik.treetracker.Model.MonitoredObject;


import java.util.List;

import com.apps.frederik.treetracker.Model.Util.Coordinate;
import com.apps.frederik.treetracker.Model.PropertiesReading.PropertiesReading;
/**
 * Created by Frederik on 12/20/2017.
 */

public class MonitoredObject {
    private PropertiesReading Current;
    private List<PropertiesReading> Historical;
    private Coordinate Coordinate;
    private String UniqueDescription;

    public Coordinate getCoordinate() {
        return Coordinate;
    }

    public void setCoordinate(Coordinate coordinate) {
        this.Coordinate = coordinate;
    }

    public String getUniqueDescription() {
        return UniqueDescription;
    }

    public void setUniqueDescription(String uniqueDescription) {
        this.UniqueDescription = uniqueDescription;
    }

    public PropertiesReading getCurrent() {
        return Current;
    }

    public void setCurrent(PropertiesReading current) {
        Current = current;
    }

    public List<PropertiesReading> getHistorical() {
        return Historical;
    }

    public void setHistorical(List<PropertiesReading> historical) {
        Historical = historical;
    }
}
