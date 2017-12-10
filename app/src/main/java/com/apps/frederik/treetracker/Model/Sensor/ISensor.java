package com.apps.frederik.treetracker.Model.Sensor;

import com.apps.frederik.treetracker.Model.Sensor.SensorData.ISensorReading;
import com.apps.frederik.treetracker.Model.Util.GpsCoordinate;

import java.util.List;

/**
 * Created by Frederik on 12/9/2017.
 */

public interface ISensor {
    String GetName();
    String GetUuid();
    GpsCoordinate GetCoordinate();
    List<ISensorReading> GetHistoricalData();
}
