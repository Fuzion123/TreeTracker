package com.apps.frederik.treetracker.Model.InternalCommunication;

import com.apps.frederik.treetracker.Model.Sensor.ISensor;
import com.apps.frederik.treetracker.Model.Sensor.SensorData.ISensorReading;

/**
 * Created by Frederik on 12/10/2017.
 */

public class ModelEventArgs {
    public final ISensor Sensor;
    public final ISensorReading Reading;

    public ModelEventArgs(ISensor sensor, ISensorReading reading){
        this.Sensor = sensor;
        this.Reading = reading;
    }

    public ModelEventArgs(ISensor sensor){
        this.Sensor = sensor;
        this.Reading = null;
    }

    public ModelEventArgs(ISensorReading reading){
        this.Sensor = null;
        this.Reading = reading;
    }
}
