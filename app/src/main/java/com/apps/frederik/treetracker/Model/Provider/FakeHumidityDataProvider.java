package com.apps.frederik.treetracker.Model.Provider;

import com.apps.frederik.treetracker.Model.DataAccessLayer.FakeDatabaseRepository;
import com.apps.frederik.treetracker.Model.InternalCommunication.ISensorReadingEventListener;
import com.apps.frederik.treetracker.Model.InternalCommunication.ReadingEventArgs;
import com.apps.frederik.treetracker.Model.Sensor.ISensor;
import com.apps.frederik.treetracker.Model.Sensor.SensorData.ISensorReading;

import java.util.List;

/**
 * Created by Frederik on 12/9/2017.
 */

public class FakeHumidityDataProvider extends BaseSensorReadingsProvider {
    ISensorReadingEventListener _listener;

    public List<ISensorReading> GetAllReadings(String uuid) {
        for (ISensor sensor: FakeDatabaseRepository.MappedSensors) {
            if(sensor.GetUuid().equals(uuid)){
                return sensor.GetHistoricalData();
            }
            else{
                return null;
            }
        }
        return null;
    }

    @Override
    public void setSensorEventListener(ISensorReadingEventListener listener) {
        _listener = listener;
    }

    @Override
    public ISensorReading GetLastReading(String uuid) {
        for (int i = 0; i < FakeDatabaseRepository.MappedSensors.size(); i++) {
            ISensor sensor = FakeDatabaseRepository.MappedSensors.get(i);

            if(sensor.GetUuid().equals(uuid)){
                int cnt = sensor.GetHistoricalData().size();

                if(cnt == 0) return null;
                return sensor.GetHistoricalData().get(cnt-1); // returns the last element of the list which equals to the last reading of the senso
            }
        }
        return null;
    }

    @Override
    public void onNewReadingEvent(Object Sender, ReadingEventArgs args) {
        // fires event that new reading was added!
        if(_listener != null){
            _listener.onNewReadingEvent(this, new ReadingEventArgs(args.reading));
        }
    }
}
