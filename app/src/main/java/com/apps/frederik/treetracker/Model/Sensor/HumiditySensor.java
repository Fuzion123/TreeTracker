package com.apps.frederik.treetracker.Model.Sensor;

import com.apps.frederik.treetracker.Model.Sensor.SensorData.HumidityData;
import com.apps.frederik.treetracker.Model.Sensor.SensorData.ISensorData;
import com.apps.frederik.treetracker.Model.Util.GpsCoordinate;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Frederik on 12/9/2017.
 */

public class HumiditySensor implements ISensor {

    private List<ISensorData> _sensorReadings;
    private String _name;
    private String _uuid;
    private GpsCoordinate _coordinate;

    public HumiditySensor(List<ISensorData> sensorReadings, String name, String uuid, GpsCoordinate coordinate) {
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
    public List<ISensorData> GetHistoricalData() {
        return _sensorReadings;
    }
}
