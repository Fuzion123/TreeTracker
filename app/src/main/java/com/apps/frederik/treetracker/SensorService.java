package com.apps.frederik.treetracker;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.apps.frederik.treetracker.Model.FakeSensorModel;
import com.apps.frederik.treetracker.Model.ISensorModel;
import com.apps.frederik.treetracker.Model.InternalCommunication.IModelEventListener;
import com.apps.frederik.treetracker.Model.InternalCommunication.ModelEventArgs;
import com.apps.frederik.treetracker.Model.Provider.FakeHumiditySensorProvider;
import com.apps.frederik.treetracker.Model.Provider.ISensorReadingProvider;
import com.apps.frederik.treetracker.Model.Provider.ISensorProvider;
import com.apps.frederik.treetracker.Model.Sensor.ISensor;
import com.apps.frederik.treetracker.Model.Sensor.SensorData.ISensorReading;

import java.text.ParseException;
import java.util.List;

public class SensorService extends Service implements IModelEventListener {
    private List<ISensor> _sensors;
    private IBinder _sensorBinder = new SensorServiceBinder();
    private ISensorModel _model;

    @Override
    public IBinder onBind(Intent intent) {
        return _sensorBinder;
    }

    @Override
    public void onCreate() {
        _model = new FakeSensorModel();
        _model.SetModelEventListener(this);

        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onModelChangedEvent(Object sender, ModelEventArgs args) {
        Intent broadcast;

        if(args.Sensor != null){
            ISensor sensor = args.Sensor;
            Log.d("ModelChanged Test", sensor.GetName());
            broadcast = new Intent(Globals.LOCAL_BROADCAST_NEW_SENSOR_ADDED);
        }

        else{
            ISensorReading reading = args.Reading;
            Log.d("ModelChanged Test", reading.GetData().toString());
            broadcast = new Intent(Globals.LOCAL_BROADCAST_NEW_READING);
        }

        LocalBroadcastManager.getInstance(this).sendBroadcast(broadcast);
    }


    public class SensorServiceBinder extends Binder {

        ISensor GetSensorFor(String uuid){
            return _model.GetSensorFor(uuid);
        }

        List<ISensor> GetAllSensors(){
            return _model.GetAllSensors();
        }

        ISensorReading GetLastReadingFor(String uuid){
            return _model.GetLastReadingFor(uuid);
        }

        List<ISensorReading> GetAllReadingsFor(String uuid){
            return _model.GetAllReadingsFor(uuid);
        }
    }

}
