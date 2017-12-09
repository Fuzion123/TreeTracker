package com.apps.frederik.treetracker.Model.Sensor.SensorData;

import com.apps.frederik.treetracker.Model.Util.TimeStamp;

/**
 * Created by Frederik on 12/9/2017.
 */

public interface ISensorData<T>{
    T GetData();
    TimeStamp GetTimeStamp();
}
