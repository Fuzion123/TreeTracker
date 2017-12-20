package com.apps.frederik.treetracker.Model.DataAccessLayer;

import android.util.Log;

import com.apps.frederik.treetracker.Model.Util.Coordinate;
import com.apps.frederik.treetracker.Model.Util.GpsFakeGenerator;
import com.apps.frederik.treetracker.Model.Util.TimeStamp;
import com.apps.frederik.treetracker.Model.Util.TimestampFakeGenerator;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static java.util.UUID.randomUUID;

/**
 * Created by Frederik on 12/10/2017.
 */

final public class FakeDatabaseRepository {

    /*
    private static boolean isInstantiated = false;
    private static Random _random = new Random();
    private static int _numberOfSensors = 2;
    private static int _numberOfReadingsPerSensor = 5;
    private static List<ISensorEventListener> _sensorListeners = new ArrayList<>();
    private static List<ISensorReadingEventListener> _readingListeners = new ArrayList<>();
    public static List<IMonitoredProperty> UnmappedSensors = new ArrayList<>();
    public static List<IMonitoredProperty> MappedSensors = new ArrayList<>();
    public static List<String> Uuids = new ArrayList<>();

    public static void InstantiateFakeRepository() throws Exception {
        // generates fake mapped sensors
        boolean shouldBeMapped = false;

        for (int i = 0; i < _numberOfSensors; i++) {

            String sensorName = "Humidity ".concat(String.valueOf(i));
            String uuid = randomUUID().toString();
            Uuids.add(uuid);

            IMonitoredProperty newSensor = new Humidity(new ArrayList<IReading>(), sensorName, uuid, null);

            // adds fake historical data
            for (int j = 0; j < _numberOfReadingsPerSensor; j++) {
                IReading newReading = GenerateFakeData();
                newSensor.GetHistoricalReadings().add(newReading);

                for (ISensorReadingEventListener listener: _readingListeners) {
                    listener.onNewReadingEvent(new Object(), new ReadingEventArgs(newReading));
                }
            }

            if(shouldBeMapped){
                Coordinate coordinate = GpsFakeGenerator.GenerateCoordinates();
                newSensor.SetGpsCoordinate(coordinate);
                MappedSensors.add(newSensor);

                for (ISensorEventListener listener: _sensorListeners) {
                    listener.onNewSensorAddedEvent(new Object(), new SensorEventArgs(newSensor));
                }
            }
            else{
                UnmappedSensors.add(newSensor);
            }
            shouldBeMapped = !shouldBeMapped;
        }

        isInstantiated = true;
        Log.d("Fake DatabaseRepository", "Sensors and historical data was generated!");
    }

    public static boolean IsInstantiated(){
        return isInstantiated;
    }

    public static void AddSensorListener(ISensorEventListener listener){
        _sensorListeners.add(listener);
    }

    public static void AddReadingListener(ISensorReadingEventListener listener){
        _readingListeners.add(listener);
    }

    public static void RemoveSensorListener(){
        throw new UnsupportedOperationException();
    }

    public static void RemoveReadingListener(){
        throw new UnsupportedOperationException();
    }

    // fake reading generator
    private static HumidityReading GenerateFakeData() throws ParseException {
        int readingValue = _random.nextInt(100);
        TimeStamp timeStamp = TimestampFakeGenerator.GenerateTimeStamp();

        return new HumidityReading(readingValue, timeStamp);
    }
    */
}
