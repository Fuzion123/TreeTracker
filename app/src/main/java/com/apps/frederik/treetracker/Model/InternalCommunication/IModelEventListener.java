package com.apps.frederik.treetracker.Model.InternalCommunication;

/**
 * Created by Frederik on 12/10/2017.
 */

public interface IModelEventListener {
    void onModelChangedEvent(Object sender, ModelEventArgs args);
}
