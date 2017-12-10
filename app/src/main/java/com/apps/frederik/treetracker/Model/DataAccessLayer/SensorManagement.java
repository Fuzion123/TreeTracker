package com.apps.frederik.treetracker.Model.DataAccessLayer;

import com.apps.frederik.treetracker.Model.Sensor.ISensor;
import com.apps.frederik.treetracker.Model.Util.GpsCoordinate;

/**
 * Created by Frederik on 12/10/2017.
 */

public class SensorManagement {

    public enum MapSensorResult{
        SENSOR_SUCCESSFULLY_MAPPED,
        WAS_ALREADY_MAPPED,
        NO_SENSOR_WITH_THAT_UUID_ON_DATABASE
    }

    public MapSensorResult MapExistingSensor(String uuid, GpsCoordinate coordinate){

        for (int i = 0; i < FakeDatabaseRepository.MappedSensors.size(); i++) {
            if(FakeDatabaseRepository.MappedSensors.get(i).GetUuid().equals(uuid)) {
                return MapSensorResult.WAS_ALREADY_MAPPED;
            }
        }

        for (int i = 0; i < FakeDatabaseRepository.UnmappedSensors.size(); i++) {
            if(FakeDatabaseRepository.UnmappedSensors.get(i).GetUuid().equals(uuid)) {
                ISensor s = FakeDatabaseRepository.UnmappedSensors.get(i);
                FakeDatabaseRepository.UnmappedSensors.remove(s);

                s.SetGpsCoordinate(coordinate);
                FakeDatabaseRepository.MappedSensors.add(s);
                return MapSensorResult.SENSOR_SUCCESSFULLY_MAPPED;
            }
        }
        return MapSensorResult.NO_SENSOR_WITH_THAT_UUID_ON_DATABASE;
    }
}
