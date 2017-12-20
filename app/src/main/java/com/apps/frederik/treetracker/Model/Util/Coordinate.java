package com.apps.frederik.treetracker.Model.Util;

/**
 * Created by Frederik on 12/9/2017.
 */

public class Coordinate {
    private final double _latitude;
    private final double _longitude;

    public Coordinate(double latitude, double longitude) {
        _latitude = latitude;
        _longitude = longitude;
    }

    public double GetLatitude(){
        return _latitude;
    }

    public double GetLongitude(){
        return _longitude;
    }
}

