package com.apps.frederik.treetracker.Model.InternalCommunication;

import com.apps.frederik.treetracker.Model.Sensor.SensorData.ISensorReading;

/**
 * Created by Frederik on 12/10/2017.
 */

public class ReadingEventArgs {
    public final ISensorReading reading;

    public ReadingEventArgs(ISensorReading reading) {
        this.reading = reading;
    }
}
