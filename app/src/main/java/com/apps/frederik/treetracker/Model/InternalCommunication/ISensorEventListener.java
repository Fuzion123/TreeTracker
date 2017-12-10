package com.apps.frederik.treetracker.Model.InternalCommunication;

/**
 * Created by Frederik on 12/10/2017.
 */

public interface ISensorEventListener {
    void onNewSensorAddedEvent(Object sender, SensorEventArgs sensor);
}
