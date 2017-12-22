package com.apps.frederik.treetracker.Model.Reading;


/**
 * Created by Frederik on 12/20/2017.
 */

public class Reading {
    private Double Data;
    private String TimeStamp;

    public Double getData() {
        return Data;
    }

    public void setData(Double data) {
        this.Data = data;
    }

    public String getTimeStamp() {
        return TimeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.TimeStamp = timeStamp;
    }

    public boolean equals(Object r){
        if(r instanceof Reading){
            Reading reading = (Reading) r;
            if(reading.getTimeStamp().equals(this.getTimeStamp()) && reading.Data.equals(this.Data)) return true;
        }
        return false;
    }
}
