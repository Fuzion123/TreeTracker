package com.apps.frederik.treetracker.Model.Provider;

import com.apps.frederik.treetracker.Model.Sensor.ISensor;

import java.util.List;

/**
 * Created by Frederik on 12/9/2017.
 */

public interface ISensorProvider {
    List<ISensor> GetAllSensors();
    ISensor GetSensor(String uuid);
}
