package com.apps.frederik.treetracker.Model;

import com.apps.frederik.treetracker.Model.InternalCommunication.IModelEventListener;
import com.apps.frederik.treetracker.Model.InternalCommunication.ModelEventArgs;
import com.apps.frederik.treetracker.Model.InternalCommunication.ReadingEventArgs;
import com.apps.frederik.treetracker.Model.InternalCommunication.SensorEventArgs;
import com.apps.frederik.treetracker.Model.Provider.FakeHumidityDataProvider;
import com.apps.frederik.treetracker.Model.Provider.FakeHumiditySensorProvider;
import com.apps.frederik.treetracker.Model.Provider.ISensorProvider;
import com.apps.frederik.treetracker.Model.Provider.ISensorReadingProvider;
import com.apps.frederik.treetracker.Model.Sensor.ISensor;
import com.apps.frederik.treetracker.Model.Sensor.SensorData.ISensorReading;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Frederik on 12/10/2017.
 */

public class FakeSensorModel implements ISensorModel {
    final int numberOfFakeSensors = 3;
    final int numberOfFakeReadingsPerSensor = 1;
    private ISensorProvider _sensorProvider;
    List<ISensor> _sensors = new ArrayList<>();
    IModelEventListener _listener;

    @Override
    public ISensor GetSensorFor(String uuid) {
        for (ISensor senser: _sensors) {
            if(senser.GetUuid().equals(uuid)){
                return senser;
            }
        }
        return null;
    }

    @Override
    public List<ISensor> GetAllSensors() {
        return _sensors;
    }

    @Override
    public ISensorReading GetLastReadingFor(String uuid) {
        for (ISensor sensor: _sensors) {
            if(sensor.GetUuid().equals(uuid)){
                int cnt = sensor.GetHistoricalData().size();
                if(cnt > 0){
                    return sensor.GetHistoricalData().get(cnt-1); // returning the last element in the list, which equals the last reading.
                }
                else { return null; }
            }
        }
        return null;
    }

    @Override
    public List<ISensorReading> GetAllReadingsFor(String uuid) {
        for (ISensor sensor: _sensors) {
            if(sensor.GetUuid().equals(uuid)){
                if(sensor.GetHistoricalData().size() > 0){
                    return sensor.GetHistoricalData();
                }
                else { return null; }
            }
        }
        return null;
    }

    @Override
    public void SetModelEventListener(IModelEventListener listener) {
        _listener = listener;

        _sensorProvider = new FakeHumiditySensorProvider(numberOfFakeSensors, this);

        for (int i = 0; i < numberOfFakeSensors; i++) {
            _sensors.add(_sensorProvider.GetAllSensors().get(i));
            ISensorReadingProvider readingsProvider = null;
            try {
                readingsProvider = new FakeHumidityDataProvider(numberOfFakeReadingsPerSensor, this);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            for (int j = 0; j < numberOfFakeReadingsPerSensor; j++) {
                _sensors.get(i).GetHistoricalData().add(readingsProvider.GetAllReadings("fakeUuid").get(j));
            }
        }
    }

    @Override
    public void onNewSensorAddedEvent(Object sender, SensorEventArgs args) {
        _listener.onModelChangedEvent(this, new ModelEventArgs(args.Sensor));
    }

    @Override
    public void onNewReadingEvent(Object Sender, ReadingEventArgs args) {
        _listener.onModelChangedEvent(this, new ModelEventArgs(args.reading));
    }
}
