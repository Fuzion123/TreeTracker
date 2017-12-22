package com.apps.frederik.treetracker.Model.InternalCommunication;


import com.apps.frederik.treetracker.Model.MonitoredObject.MonitoredObject;

/**
 * Created by Frederik on 12/10/2017.
 */


public class ModelEventArgs {
    public final MonitoredObject _obj;

    public ModelEventArgs(MonitoredObject obj){
        _obj = obj;
    }
}
