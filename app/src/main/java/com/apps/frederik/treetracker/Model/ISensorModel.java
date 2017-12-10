package com.apps.frederik.treetracker.Model;

import com.apps.frederik.treetracker.Model.InternalCommunication.IModelEventListener;
import com.apps.frederik.treetracker.Model.InternalCommunication.ISensorEventListener;
import com.apps.frederik.treetracker.Model.InternalCommunication.ISensorReadingEventListener;
import com.apps.frederik.treetracker.Model.Sensor.ISensor;
import com.apps.frederik.treetracker.Model.Sensor.SensorData.ISensorReading;

import java.util.List;

/**
 * Created by Frederik on 12/10/2017.
 */

public interface ISensorModel extends ISensorEventListener, ISensorReadingEventListener {
    ISensor GetSensorFor(String uuid);
    List<ISensor> GetAllSensors();
    ISensorReading GetLastReadingFor(String uuid);
    List<ISensorReading> GetAllReadingsFor(String uuid);
    void SetModelEventListener(IModelEventListener listener);
}
