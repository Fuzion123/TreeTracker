package com.apps.frederik.treetracker.Model.Provider;

import com.apps.frederik.treetracker.Model.InternalCommunication.ISensorEventListener;
import com.apps.frederik.treetracker.Model.InternalCommunication.ISensorReadingEventListener;
import com.apps.frederik.treetracker.Model.Sensor.ISensor;

import java.util.List;

/**
 * Created by Frederik on 12/9/2017.
 */

public abstract class BaseSensorProvider implements ISensorEventListener {
    abstract ISensor GetSensor(String uuid);
    abstract List<ISensor> GetAllSensors();
    abstract void setSensorEventListener(ISensorEventListener listener);
}
