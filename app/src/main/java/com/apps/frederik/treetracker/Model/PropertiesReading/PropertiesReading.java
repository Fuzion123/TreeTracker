package com.apps.frederik.treetracker.Model.PropertiesReading;


import java.util.HashMap;

/**
 * Created by Frederik on 12/20/2017.
 */

public class PropertiesReading {
    private HashMap<String, String> Properties = new HashMap<>();
    private String TimeStamp;

    public String getTimeStamp() {
        return TimeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.TimeStamp = timeStamp;
    }

    /*
    public boolean equals(Object r){
        if(r instanceof PropertiesReading){
            PropertiesReading reading = (PropertiesReading) r;
            if(reading.getTimeStamp().equals(this.getTimeStamp()) && reading.Data.equals(this.Data)) return true;
        }
        return false;
    }
    */

    public HashMap<String, String> getProperties() {
        return Properties;
    }

    public void setProperties(HashMap<String, String> properties) {
        Properties = properties;
    }
}
