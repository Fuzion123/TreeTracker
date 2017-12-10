package com.apps.frederik.treetracker.Model.Provider;

import com.apps.frederik.treetracker.Model.Sensor.HumiditySensor;
import com.apps.frederik.treetracker.Model.Sensor.ISensor;
import com.apps.frederik.treetracker.Model.Sensor.SensorData.HumidityData;
import com.apps.frederik.treetracker.Model.Sensor.SensorData.ISensorData;
import com.apps.frederik.treetracker.Model.Util.AarhusLatitudeLongitudeConstrains;
import com.apps.frederik.treetracker.Model.Util.GpsCoordinate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import static java.util.UUID.randomUUID;

/**
 * Created by Frederik on 12/9/2017.
 */

public class FakeHumiditySensorProvider implements ISensorProvider {
    private Random _random = new Random();
    private List<ISensor> sensors = new ArrayList<>();

    public FakeHumiditySensorProvider(int numberOfFakeSensors){
        for (int i = 0; i < numberOfFakeSensors; i++) {
            sensors.add(SensorCreator(i));
        }
    }

    private ISensor SensorCreator(int sensorNr){
        List<ISensorData> sensors = new ArrayList<>();
        String name = "HumiditySensor ".concat(String.valueOf(sensorNr));
        String uuid = randomUUID().toString();
        GpsCoordinate coordinate = GpsFakeGenerator();

        return new HumiditySensor(sensors, name, uuid, coordinate);
    }

    @Override
    public List<ISensor> GetAllSensors() {
        return sensors;
    }

    @Override
    public ISensor GetSensor(String uuid) {
        for (ISensor sensor: sensors) {
            if(sensor.GetUuid().equals(uuid)){
                return sensor;
            }
        }
        return null;
    }

    private GpsCoordinate GpsFakeGenerator(){
        double latRangeMin = AarhusLatitudeLongitudeConstrains.LatitudeLowerBound;
        double latRangeMax= AarhusLatitudeLongitudeConstrains.LatitudeUpperBound;
        double longRangeMin = AarhusLatitudeLongitudeConstrains.LongitudeLeftBound;
        double longRangeMax = AarhusLatitudeLongitudeConstrains.LongitudeRightBound;

        // random generated latitudes and longitudes from defined boundaries
        // inspired by: https://stackoverflow.com/questions/3680637/generate-a-random-double-in-a-range
        double latitude = latRangeMin + (latRangeMax - latRangeMin) * _random.nextDouble();
        double longitude = longRangeMin + (longRangeMax - longRangeMin) * _random.nextDouble();

        return new GpsCoordinate(latitude, longitude);
    }
}
