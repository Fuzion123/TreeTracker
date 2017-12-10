package com.apps.frederik.treetracker.Model.Provider;

import com.apps.frederik.treetracker.Model.InternalCommunication.ISensorReadingEventListener;
import com.apps.frederik.treetracker.Model.Sensor.SensorData.ISensorReading;

import java.util.List;

/**
 * Created by Frederik on 12/9/2017.
 */

public interface ISensorReadingProvider {
    ISensorReading GetLastReading(String uuid);
    List<ISensorReading> GetAllReadings(String uuid);
    void setSensorEventListener(ISensorReadingEventListener listener);
}
