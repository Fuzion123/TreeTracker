package com.apps.frederik.treetracker.Model.Provider;

import com.apps.frederik.treetracker.Model.InternalCommunication.ISensorReadingEventListener;
import com.apps.frederik.treetracker.Model.InternalCommunication.ReadingEventArgs;
import com.apps.frederik.treetracker.Model.Sensor.SensorData.HumidityReading;
import com.apps.frederik.treetracker.Model.Sensor.SensorData.ISensorReading;
import com.apps.frederik.treetracker.Model.Util.TimeStamp;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * Created by Frederik on 12/9/2017.
 */

public class FakeHumidityDataProvider implements ISensorReadingProvider {
    Random _random = new Random();
    List<ISensorReading> _readings = new ArrayList<>();
    ISensorReadingEventListener _listener;

    public FakeHumidityDataProvider(int readingCount, ISensorReadingEventListener listener) throws ParseException {
        _listener = listener;

        for (int i = 0; i < readingCount; i++) {
            ISensorReading reading = GenerateFakeData();
            _readings.add(reading);

            if(_listener != null){
                _listener.onNewReadingEvent(this, new ReadingEventArgs(reading));
            }
        }
    }

    private HumidityReading GenerateFakeData() throws ParseException {
        int readingValue = _random.nextInt(100);
        TimeStamp timeStamp = GenerateFakeTimeStamp();

        return new HumidityReading(readingValue, timeStamp);
    }

    private TimeStamp GenerateFakeTimeStamp() throws ParseException {
        //Date dateLowerRange = new Date();
        //Date dateUpperRange = new Date();

        String dateInString = "01/01/2017/13:45:30";

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy/hh:mm:ss");
        Date parsedDate = formatter.parse(dateInString);

        return new TimeStamp(parsedDate);
    }

    public List<ISensorReading> GetAllReadings(String uuid) {
        return _readings;
    }

    @Override
    public void setSensorEventListener(ISensorReadingEventListener listener) {
        _listener = listener;
    }

    @Override
    public ISensorReading GetLastReading(String uuid) {
        if(_readings.size() == 0) return null;
        return _readings.get(_readings.size()-1);
    }


}
