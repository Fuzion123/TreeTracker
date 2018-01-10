package com.apps.frederik.treetracker.Fragments;

import android.support.v4.app.Fragment;
import com.apps.frederik.treetracker.Model.MonitoredObject.MonitoredObject;
import java.util.List;

/**
 * Created by frede on 20/12/2017.
 */

// This base class is used solely for the overview Activity to treat both the ListFragment and the MapFragment equally, and have a CurrentFragment variable that fits them both.
public class MonitoredObjectFragment extends Fragment implements IMonitoredObjectDataUpdater {
    @Override
    public void AddMonitoredObject(MonitoredObject obj) {

    }

    @Override
    public void RemoveMonitoredObjectFor(String uuid) {

    }

    @Override
    public void RefreshAllMonitoredObject(List<MonitoredObject> objs) {

    }

    @Override
    public List<MonitoredObject> GetMonitoredObject() {
        return null;
    }
}
