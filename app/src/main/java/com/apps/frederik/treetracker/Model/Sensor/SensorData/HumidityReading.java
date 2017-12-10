package com.apps.frederik.treetracker.Model.Sensor.SensorData;

import com.apps.frederik.treetracker.Model.Util.TimeStamp;

/**
 * Created by Frederik on 12/9/2017.
 */

public class HumidityReading implements ISensorReading {
    private final int _reading;
    private final TimeStamp _timeStamp;

    public HumidityReading(int reading, TimeStamp timeStamp) {
        _reading = reading;
        _timeStamp = timeStamp;
    }

    @Override
    public Integer GetData() {
        return _reading;
    }

    @Override
    public TimeStamp GetTimeStamp() {
        return _timeStamp;
    }
}
