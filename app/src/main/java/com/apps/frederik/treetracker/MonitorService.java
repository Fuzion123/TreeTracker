package com.apps.frederik.treetracker;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import com.apps.frederik.treetracker.Model.DataAccessLayer.DatabaseRepository;
import com.apps.frederik.treetracker.Model.MonitoredObject.MonitoredObject;
import com.apps.frederik.treetracker.Model.PropertiesReading.PropertiesReading;

import java.util.List;

public class MonitorService extends Service {
    private IBinder _sensorBinder = new MonitorServiceBinder();
    private DatabaseRepository _dBRepository = null;

    @Override
    public IBinder onBind(Intent intent) {
        return _sensorBinder;
    }

    @Override
    public void onDestroy() {
        _dBRepository.RemoveListenerForMonitoredObjects();
        super.onDestroy();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }


    public class MonitorServiceBinder extends Binder implements OverviewActivityBinder, DetailActivityBinder, AddMonitoredObjectActivityBinder {
        @Override
        public MonitoredObject GetMonitoredObjectFor(String uuid) { return _dBRepository.GetMonitoredObjectFor(uuid); }

        @Override
        public List<MonitoredObject> GetAllMonitoredObjects(){ return _dBRepository.GetAllMonitoredObjects(); }

        @Override
        public List<PropertiesReading> GetHistorical() { return _dBRepository.GetHistorical(); }

        @Override
        public PropertiesReading GetCurrent() { return _dBRepository.GetCurrent(); }

        @Override
        public void AddMonitoredObject(MonitoredObject obj){ _dBRepository.addMonitoredObject(obj);}

        @Override
        public void SetupRepository(String userId) {
            if(_dBRepository != null) return;

            _dBRepository = new DatabaseRepository("users/" + userId, MonitorService.this);
            _dBRepository.SetupDbListenerForMonitoredObjects();
        }

        @Override
        public void SetupPropertiesReadingLister(String id) { _dBRepository.SetupDbListenersForPropertiesForMonitoredObject(id); }

        @Override
        public void RemovePropertiesReadingListener(String id) { _dBRepository.RemoveListenerForPropertiesForMonitoredObject(id); }
    }

    // below defines three different interfaces that is used by the three different Activities (Overview, Detailed and AddSensor)
    public interface OverviewActivityBinder{
        MonitoredObject GetMonitoredObjectFor(String uuid);
        List<MonitoredObject> GetAllMonitoredObjects();
        void SetupRepository(String userId);
    }

    public interface DetailActivityBinder{
        void SetupPropertiesReadingLister(String id);
        void RemovePropertiesReadingListener(String id);
        List<PropertiesReading> GetHistorical();
        PropertiesReading GetCurrent();
    }

    public interface AddMonitoredObjectActivityBinder{
        void AddMonitoredObject(MonitoredObject obj);
    }
}

