package com.apps.frederik.treetracker.Model.Util;

import java.util.Random;

/**
 * Created by Frederik on 12/10/2017.
 */

public final class GpsFakeGenerator {
    private static final Random _random = new Random();

    public static GpsCoordinate GenerateCoordinates(){
        double latRangeMin = AarhusLatitudeLongitudeConstrains.LatitudeLowerBound;
        double latRangeMax= AarhusLatitudeLongitudeConstrains.LatitudeUpperBound;
        double longRangeMin = AarhusLatitudeLongitudeConstrains.LongitudeLeftBound;
        double longRangeMax = AarhusLatitudeLongitudeConstrains.LongitudeRightBound;

        // random generated latitudes and longitudes from defined boundaries
        // inspired by: https://stackoverflow.com/questions/3680637/generate-a-random-double-in-a-range
        double latitude = latRangeMin + (latRangeMax - latRangeMin) * _random.nextDouble();
        double longitude = longRangeMin + (longRangeMax - longRangeMin) * _random.nextDouble();

        return new GpsCoordinate(latitude, longitude);
    }
}
