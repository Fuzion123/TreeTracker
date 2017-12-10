package com.apps.frederik.treetracker.Model.InternalCommunication;

import com.apps.frederik.treetracker.Model.Sensor.ISensor;

/**
 * Created by Frederik on 12/10/2017.
 */

public class SensorEventArgs {
    public final ISensor Sensor;

    public SensorEventArgs(ISensor sensor) {
        Sensor = sensor;
    }
}
