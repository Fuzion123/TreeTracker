package com.apps.frederik.treetracker.Model.Provider;

import com.apps.frederik.treetracker.Model.InternalCommunication.ISensorEventListener;
import com.apps.frederik.treetracker.Model.InternalCommunication.ISensorReadingEventListener;
import com.apps.frederik.treetracker.Model.Sensor.ISensor;

import java.util.List;

/**
 * Created by Frederik on 12/9/2017.
 */

public interface ISensorProvider {
    ISensor GetSensor(String uuid);
    List<ISensor> GetAllSensors();
    void setSensorEventListener(ISensorEventListener listener);
}
