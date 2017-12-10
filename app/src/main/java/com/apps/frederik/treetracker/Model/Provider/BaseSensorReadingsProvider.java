package com.apps.frederik.treetracker.Model.Provider;

import com.apps.frederik.treetracker.Model.InternalCommunication.ISensorReadingEventListener;
import com.apps.frederik.treetracker.Model.Sensor.SensorData.ISensorReading;

import java.util.List;

/**
 * Created by Frederik on 12/9/2017.
 */

public abstract class BaseSensorReadingsProvider implements ISensorReadingEventListener {
    abstract ISensorReading GetLastReading(String uuid);
    abstract List<ISensorReading> GetAllReadings(String uuid);
    abstract void setSensorEventListener(ISensorReadingEventListener listener);
}
