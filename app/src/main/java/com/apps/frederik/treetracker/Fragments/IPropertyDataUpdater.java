package com.apps.frederik.treetracker.Fragments;

import com.apps.frederik.treetracker.Model.MonitoredProperty.MonitoredProperty;
import com.apps.frederik.treetracker.Model.Reading.Reading;

/**
 * Created by frede on 22/12/2017.
 */

public interface IPropertyDataUpdater {
    void SetMonitoredProperty(MonitoredProperty prop);
    void AddReading(Reading read);
}
