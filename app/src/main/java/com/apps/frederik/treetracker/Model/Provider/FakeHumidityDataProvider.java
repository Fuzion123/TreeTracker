package com.apps.frederik.treetracker.Model.Provider;

import com.apps.frederik.treetracker.Model.Sensor.SensorData.HumidityData;
import com.apps.frederik.treetracker.Model.Sensor.SensorData.ISensorData;
import com.apps.frederik.treetracker.Model.Util.TimeStamp;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Frederik on 12/9/2017.
 */

public class FakeHumidityDataProvider implements ISensorDataProvider {
    Random _random = new Random();
    List<ISensorData> _readings = new ArrayList<>();

    public FakeHumidityDataProvider(int readingCount) throws ParseException {
        for (int i = 0; i < readingCount; i++) {
            _readings.add(GenerateFakeData());
        }
    }

    private HumidityData GenerateFakeData() throws ParseException {
        int readingValue = _random.nextInt(100);
        TimeStamp timeStamp = GenerateFakeTimeStamp();

        return new HumidityData(readingValue, timeStamp);
    }

    private TimeStamp GenerateFakeTimeStamp() throws ParseException {
        //Date dateLowerRange = new Date();
        //Date dateUpperRange = new Date();



        String dateInString = "01/01/2017/13:45:30";

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy/hh:mm:ss");
        Date parsedDate = formatter.parse(dateInString);

        return new TimeStamp(parsedDate);
    }

    public List<ISensorData> GetAllReadings(String uuid) {
        return _readings;
    }

    @Override
    public ISensorData GetLastReading(String uuid) {
        if(_readings.size() == 0) return null;
        return _readings.get(_readings.size()-1);
    }


}
