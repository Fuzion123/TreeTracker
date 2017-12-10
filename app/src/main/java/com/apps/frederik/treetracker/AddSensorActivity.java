package com.apps.frederik.treetracker;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.apps.frederik.treetracker.Model.Util.GpsCoordinate;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.samples.vision.barcodereader.BarcodeCaptureActivity;
import com.google.android.gms.vision.barcode.Barcode;

public class AddSensorActivity extends AppCompatActivity {

    private static final int RC_BARCODE_CAPTURE = 1234;
    private static final int RC_HANDLE_LOCATION_PERM = 4321;
    private static final String TAG = "AddSensorActivity";

    private Button buttonAddSensor;
    private EditText editTextSensorName;
    private TextView textViewInformationToUser, textViewUUIDKey, textViewUUIDValue, textViewLocationPermission;

    private ImageButton imageButton;

    // Acquire a reference to the system Location Manager
    LocationManager locationManager;
    String locationProvider = LocationManager.GPS_PROVIDER;
    Location lastKnownLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_sensor);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        buttonAddSensor = findViewById(R.id.buttonAddSensor);
        editTextSensorName = findViewById(R.id.editTextSensorName);
        imageButton = findViewById(R.id.imageButton);
        textViewInformationToUser = findViewById(R.id.textViewInformationToUser);
        textViewUUIDKey = findViewById(R.id.textViewUUIDKey);
        textViewUUIDValue = findViewById(R.id.textViewUUIDValue);
        textViewLocationPermission = findViewById(R.id.textViewLocationPermission);

         locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        //Setup state of buttons and text fields
        imageButton.setVisibility(View.VISIBLE);
        buttonAddSensor.setVisibility(View.INVISIBLE);
        editTextSensorName.setVisibility(View.INVISIBLE);
        textViewUUIDKey.setVisibility(View.INVISIBLE);
        textViewUUIDValue.setVisibility(View.INVISIBLE);
        textViewLocationPermission.setVisibility(View.INVISIBLE);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddSensorActivity.this, BarcodeCaptureActivity.class);
                startActivityForResult(intent, RC_BARCODE_CAPTURE);
            }
        });

        buttonAddSensor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Use helper class to add sensor to Firebase

                //If succesfully added to Firebase, return to OverviewActivity
                //TODO: Add a proper (null?) check
                if (invalidSensorName())
                {
                    Toast.makeText(AddSensorActivity.this, "Enter a name, please", Toast.LENGTH_LONG).show();
                    return;
                }

                if (checkLocationPermission())
                    FinalizeAddingSensor();
                else
                    requestLocationPermission();
            }
        });
    }

    private void FinalizeAddingSensor(){
        if (!checkLocationPermission())
            return;
        lastKnownLocation = locationManager.getLastKnownLocation(locationProvider);
        double currentLongitude = lastKnownLocation.getLongitude();
        double currentLatitude = lastKnownLocation.getLatitude();
        GpsCoordinate gpsCoordinate = new GpsCoordinate(currentLatitude, currentLongitude);
        //sensorManagement.mapSensor(gpsCoordinate);
        finish();
    }

    private boolean checkLocationPermission() {
        //Check if location permission is granted
        int rc = ActivityCompat.checkSelfPermission(AddSensorActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);
        // Register the listener with the Location Manager to receive location updates
        if (rc == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            return true;
        }
        else
            return false;
    }

    // Define a listener that responds to location updates
    private LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            // Called when a new location is found by the network location provider.
            // makeUseOfNewLocation(location);
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onProviderDisabled(String provider) {
        }
    };


    @Override
    protected void onStop() {
        locationManager.removeUpdates(locationListener);
        super.onStop();
    }

    private void requestLocationPermission() {
        Log.w(TAG, "Location permission is not granted. Requesting permission");

        final Activity thisActivity = AddSensorActivity.this;
        final String[] permissions = new String[]
                {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

        ActivityCompat.requestPermissions(thisActivity, permissions, RC_HANDLE_LOCATION_PERM);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == RC_HANDLE_LOCATION_PERM){
            FinalizeAddingSensor();
            if (checkLocationPermission())
                return;
            else {
                Log.e(TAG, "Permissions not granted." +
                        " Result code = " + (grantResults.length > 0 ? grantResults[0] : "(empty)"));
            }
        }
        else{
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private boolean invalidSensorName() {
        String Name = editTextSensorName.getText().toString();
        return Name.isEmpty() && Name.matches("");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_BARCODE_CAPTURE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
                    buttonAddSensor.setVisibility(View.VISIBLE);
                    editTextSensorName.setVisibility(View.VISIBLE);
                    imageButton.setVisibility(View.INVISIBLE);
                    textViewUUIDKey.setVisibility(View.VISIBLE);
                    textViewUUIDValue.setVisibility(View.VISIBLE);
                    textViewLocationPermission.setVisibility(View.VISIBLE);
                    textViewInformationToUser.setText(R.string.text_give_sensor_a_name);
                    textViewUUIDValue.setText(barcode.displayValue);
                    Log.d(TAG, "Barcode read: " + barcode.displayValue);
                } else {
                    Log.d(TAG, "No barcode captured, intent data is null");
                }
            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
