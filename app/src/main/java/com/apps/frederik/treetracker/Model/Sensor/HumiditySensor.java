package com.apps.frederik.treetracker.Model.Sensor;

import com.apps.frederik.treetracker.Model.Sensor.SensorData.ISensorReading;
import com.apps.frederik.treetracker.Model.Util.GpsCoordinate;

import java.util.List;

/**
 * Created by Frederik on 12/9/2017.
 */

public class HumiditySensor implements ISensor {

    private List<ISensorReading> _sensorReadings;
    private String _name;
    private String _uuid;
    private GpsCoordinate _coordinate;

    public HumiditySensor(List<ISensorReading> sensorReadings, String name, String uuid, GpsCoordinate coordinate) {
        _sensorReadings = sensorReadings;
        _name = name;
        _uuid = uuid;
        _coordinate = coordinate;
    }

    @Override
    public String GetName() {
        return _name;
    }

    @Override
    public String GetUuid() {
        return _uuid;
    }

    @Override
    public GpsCoordinate GetCoordinate() {
        return _coordinate;
    }

    @Override
    public void SetGpsCoordinate(GpsCoordinate coordinate) {
        _coordinate = coordinate;
    }

    @Override
    public List<ISensorReading> GetHistoricalData() {
        return _sensorReadings;
    }
}
