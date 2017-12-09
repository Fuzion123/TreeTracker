package com.apps.frederik.treetracker.Model.Provider;

import com.apps.frederik.treetracker.Model.Sensor.SensorData.ISensorData;

import java.util.List;

/**
 * Created by Frederik on 12/9/2017.
 */

public interface ISensorDataProvider {
    List<ISensorData> GetAllReadings(String uuid);
    ISensorData GetLastReading(String uuid);
}
