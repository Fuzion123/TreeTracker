package com.apps.frederik.treetracker.Model.Util;

/**
 * Created by Frederik on 12/9/2017.
 */

public class Coordinate {

    private Double Latitude;
    private Double Longitude;

    public Coordinate(){

    }

    public Coordinate(double latitude, double longitude){
        this.Latitude = latitude;
        this.Longitude = longitude;
    }

    public Double getLatitude() {
        return Latitude;
    }

    public void setLatitude(Double latitude) {
        this.Latitude = latitude;
    }

    public Double getLongitude() {
        return Longitude;
    }

    public void setLongitude(Double longitude) {
        this.Longitude = longitude;
    }
}

