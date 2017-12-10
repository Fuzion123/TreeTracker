package com.apps.frederik.treetracker.Model;

import android.util.Log;

import com.apps.frederik.treetracker.Model.DataAccessLayer.FakeDatabaseRepository;
import com.apps.frederik.treetracker.Model.InternalCommunication.IModelEventListener;
import com.apps.frederik.treetracker.Model.InternalCommunication.ModelEventArgs;
import com.apps.frederik.treetracker.Model.InternalCommunication.ReadingEventArgs;
import com.apps.frederik.treetracker.Model.InternalCommunication.SensorEventArgs;
import com.apps.frederik.treetracker.Model.Provider.FakeHumidityDataProvider;
import com.apps.frederik.treetracker.Model.Provider.FakeHumiditySensorProvider;
import com.apps.frederik.treetracker.Model.Provider.BaseSensorProvider;
import com.apps.frederik.treetracker.Model.Provider.BaseSensorReadingsProvider;
import com.apps.frederik.treetracker.Model.Sensor.ISensor;
import com.apps.frederik.treetracker.Model.Sensor.SensorData.ISensorReading;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Frederik on 12/10/2017.
 */

public class FakeSensorModel implements ISensorModel {
    private FakeHumiditySensorProvider _sensorProvider;
    private FakeHumidityDataProvider _readingProvider;
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
    public void SetModelEventListener(IModelEventListener listener) throws Exception {
        _listener = listener;

        // sensorProvider setup
        _sensorProvider = new FakeHumiditySensorProvider();
        _sensorProvider.setSensorEventListener(this);
        FakeDatabaseRepository.AddSensorListener(_sensorProvider);


        // sensorReadingsProvider setup
        _readingProvider = new FakeHumidityDataProvider();
        _readingProvider.setSensorEventListener(this);
        FakeDatabaseRepository.AddReadingListener(_readingProvider);

        // generates all the fake data
        FakeDatabaseRepository.InstantiateFakeRepository();
    }

    @Override
    public void onNewSensorAddedEvent(Object sender, SensorEventArgs args) {
        Log.d("FakeModel", "Sensor Added");
        _listener.onModelChangedEvent(this, new ModelEventArgs(args.Sensor));
    }

    @Override
    public void onNewReadingEvent(Object Sender, ReadingEventArgs args) {
        Log.d("FakeModel", "New Reading");
        _listener.onModelChangedEvent(this, new ModelEventArgs(args.reading));
    }
}
