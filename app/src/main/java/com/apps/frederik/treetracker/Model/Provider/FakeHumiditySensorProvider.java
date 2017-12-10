package com.apps.frederik.treetracker.Model.Provider;

import com.apps.frederik.treetracker.Model.DataAccessLayer.FakeDatabaseRepository;
import com.apps.frederik.treetracker.Model.InternalCommunication.ISensorEventListener;
import com.apps.frederik.treetracker.Model.InternalCommunication.SensorEventArgs;
import com.apps.frederik.treetracker.Model.Sensor.ISensor;

import java.util.List;

/**
 * Created by Frederik on 12/9/2017.
 */

public class FakeHumiditySensorProvider extends BaseSensorProvider {
    private ISensorEventListener _listener;

    @Override
    public List<ISensor> GetAllSensors() {
        return FakeDatabaseRepository.MappedSensors;
    }

    @Override
    public void setSensorEventListener(ISensorEventListener listener) {
        _listener = listener;
    }

    @Override
    public ISensor GetSensor(String uuid) {
        for (ISensor sensor: FakeDatabaseRepository.MappedSensors) {
            if(sensor.GetUuid().equals(uuid)){
                return sensor;
            }
        }
        return null;
    }

    @Override
    public void onNewSensorAddedEvent(Object sender, SensorEventArgs args) throws Exception {
        // fires event that new sensor was added!
        if(_listener != null){
            _listener.onNewSensorAddedEvent(this, new SensorEventArgs(args.Sensor));
        }
    }
}
