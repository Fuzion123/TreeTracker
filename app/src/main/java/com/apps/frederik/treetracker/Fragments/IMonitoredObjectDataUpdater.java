package com.apps.frederik.treetracker.Fragments;

import com.apps.frederik.treetracker.Model.MonitoredObject.MonitoredObject;

import java.util.List;

/**
 * Created by frede on 20/12/2017.
 */

public interface IMonitoredObjectDataUpdater {
    void AddMonitoredObject(MonitoredObject obj);
    void RemoveMonitoredObjectFor(String uuid);
    void RefreshAllMonitoredObject(List<MonitoredObject> objs);
}
