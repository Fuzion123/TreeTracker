package com.apps.frederik.treetracker.Model.InternalCommunication;

/**
 * Created by Frederik on 12/10/2017.
 */

public interface ISensorReadingEventListener {
    void onNewReadingEvent(Object Sender, ReadingEventArgs reading);
}
