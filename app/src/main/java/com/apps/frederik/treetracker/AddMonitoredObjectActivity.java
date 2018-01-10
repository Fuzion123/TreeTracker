package com.apps.frederik.treetracker;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.samples.vision.barcodereader.BarcodeCaptureActivity;
import com.google.android.gms.vision.barcode.Barcode;

import org.json.JSONException;
import org.json.JSONObject;

public class AddMonitoredObjectActivity extends AppCompatActivity {

    private final String UUID_VALUE_KEY = "UUID_VALUE";
    private final String ENTER_NAME_HINT = "Enter a name, please";
    private static final int RC_BARCODE_CAPTURE = 1234;
    private static final int RC_HANDLE_LOCATION_PERM = 4321;
    private final String ADD_SENSOR_FAIL_HINT = "Adding the sensor package failed!";
    private final String MESSAGE_RESPONSE_SUCCESSFULL = "Successfull";
    private final String NON_ERROR_HTTP_RESPONSE_EXCEPTION_MESSAGE = "Response from HTTP was NOT an error!";
    private final String MISSING_BARCODE = "No barcode captured, intent data is null";
    private final String REQUEST_BODY_OWNER = "owner";
    private final String REQUEST_BODY_ASSIGNTO = "assignTo";
    private final String REQUEST_BODY_LOCATION = "location";
    private final String REQUEST_BODY_HARDWARE_SERIAL = "hardware_serial";
    private final String LOCATION_LATITUDE = "latitude";
    private final String LOCATION_LONGITUDE = "longitude";

    // fields to assign a new MonitoredObject
    private String _owner;
    private String _assignTo;
    private double _latitude;
    private double _longitude;
    private String _hardware_serial;

    private RequestQueue volleyQueue;

    private boolean _isBoundToService;

    private Button buttonAddSensor;
    private EditText editTextSensorName;
    private TextView textViewInformationToUser, textViewUUIDKey, textViewUUIDValue, textViewLocationPermission;
    private ImageView imageViewTree;
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
        _owner = getIntent().getExtras().getString(Globals.USERID);
         volleyQueue = Volley.newRequestQueue(this);

        buttonAddSensor = findViewById(R.id.buttonAddSensor);
        editTextSensorName = findViewById(R.id.editTextSensorName);
        imageButton = findViewById(R.id.imageButton);
        textViewInformationToUser = findViewById(R.id.textViewInformationToUser);
        textViewUUIDKey = findViewById(R.id.textViewUUIDKey);
        textViewUUIDValue = findViewById(R.id.textViewUUIDValue);
        textViewLocationPermission = findViewById(R.id.textViewLocationPermission);
        imageViewTree = findViewById(R.id.imageViewTree);

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        //Setup UI based on saved instance state in a rather messy way
        if (savedInstanceState != null) {
            if (savedInstanceState.getString(UUID_VALUE_KEY).length() == 0) {
                imageButton.setVisibility(View.VISIBLE);
                buttonAddSensor.setVisibility(View.INVISIBLE);
                editTextSensorName.setVisibility(View.INVISIBLE);
                textViewUUIDKey.setVisibility(View.INVISIBLE);
                textViewUUIDValue.setVisibility(View.INVISIBLE);
                textViewLocationPermission.setVisibility(View.INVISIBLE);
                imageViewTree.setVisibility(View.INVISIBLE);
            }
            else{
                imageButton.setVisibility(View.INVISIBLE);
                textViewInformationToUser.setText(R.string.text_give_sensor_a_name);
                textViewUUIDValue.setText(savedInstanceState.getString(UUID_VALUE_KEY));
            }
        }
        else{
            imageButton.setVisibility(View.VISIBLE);
            buttonAddSensor.setVisibility(View.INVISIBLE);
            editTextSensorName.setVisibility(View.INVISIBLE);
            textViewUUIDKey.setVisibility(View.INVISIBLE);
            textViewUUIDValue.setVisibility(View.INVISIBLE);
            textViewLocationPermission.setVisibility(View.INVISIBLE);
            imageViewTree.setVisibility(View.INVISIBLE);
        }

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddMonitoredObjectActivity.this, BarcodeCaptureActivity.class);
                startActivityForResult(intent, RC_BARCODE_CAPTURE);
            }
        });

        buttonAddSensor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Use helper class to add sensor to Firebase
                //If succesfully added to Firebase, return to OverviewActivity
                if (invalidSensorName())
                {
                    Toast.makeText(AddMonitoredObjectActivity.this, ENTER_NAME_HINT, Toast.LENGTH_LONG).show();
                    return;
                }

                if (checkLocationPermission())
                    try {
                        FinalizeAddingSensor();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                else
                    requestLocationPermission();
            }
        });

        Intent service = new Intent(this, MonitorService.class);
        bindService(service,_connection, Context.BIND_AUTO_CREATE);
    }

    private ServiceConnection _connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder binder) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            _isBoundToService = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            _isBoundToService = false;
        }
    };

    @Override
    protected void onDestroy() {
        if(_isBoundToService){
            unbindService(_connection);
        }
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        locationManager.removeUpdates(locationListener);
        super.onPause();
    }

    //Save UUID value if already captured from barcode activity
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(UUID_VALUE_KEY, textViewUUIDValue.getText().toString());
        super.onSaveInstanceState(outState);
    }

    //Fetch last known location when adding sensor and make request using volley.
    //Location and sensor name will be added in the body of the volley request.
    private void FinalizeAddingSensor() throws JSONException {
        // return if permissions is not granted!
        if (!checkLocationPermission())
            return;

        lastKnownLocation = locationManager.getLastKnownLocation(locationProvider);
        _latitude = lastKnownLocation.getLatitude();
        _longitude = lastKnownLocation.getLongitude();
        _assignTo = editTextSensorName.getText().toString();

        volleyQueue.add(createRegisterRequest());
    }

    //We are making a JSON Object request, but receive a JSON string from the database.
    //Since the method expects a JSON Object response, a work-around has been made:
    //We know that what we receive is a string, which results in an error response, therefore
    //an ErrorListener is constructed, that checks if the response is successful.
    //Similar approach for the AssignRequest, which is called on a successful response.
    private JsonObjectRequest createRegisterRequest() throws JSONException {
        String url = Globals.TTN_CLOUD_FUNCTIONS_URL + "register";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, getJsonBodyForRegister(),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        throw new RuntimeException(NON_ERROR_HTTP_RESPONSE_EXCEPTION_MESSAGE);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try {
                    if (error.getMessage().contains(MESSAGE_RESPONSE_SUCCESSFULL)) {
                        volleyQueue.add(createAssignRequest());
                    } else {
                        Toast.makeText(AddMonitoredObjectActivity.this, ADD_SENSOR_FAIL_HINT, Toast.LENGTH_LONG).show();
                        finish();
                        return;
                    }
                }catch (Exception e){
                    Toast.makeText(AddMonitoredObjectActivity.this, ADD_SENSOR_FAIL_HINT, Toast.LENGTH_LONG).show();
                    finish();
                    return;
                }
            }
        });
        return request;
    }

    private JsonObjectRequest createAssignRequest() throws JSONException {
        String url = Globals.TTN_CLOUD_FUNCTIONS_URL + "assign";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, getJsonBodyForAssign(),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        throw new RuntimeException(NON_ERROR_HTTP_RESPONSE_EXCEPTION_MESSAGE);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try {
                    if (error.getMessage().contains(MESSAGE_RESPONSE_SUCCESSFULL)) {
                        Toast.makeText(AddMonitoredObjectActivity.this, "TreeTracker: " + _assignTo + " was succesfully added!", Toast.LENGTH_LONG).show();
                        finish();
                        return;
                    } else {
                        Toast.makeText(AddMonitoredObjectActivity.this, ADD_SENSOR_FAIL_HINT, Toast.LENGTH_LONG).show();
                        finish();
                        return;
                    }
                }catch (Exception e){
                    Toast.makeText(AddMonitoredObjectActivity.this, ADD_SENSOR_FAIL_HINT, Toast.LENGTH_LONG).show();
                    finish();
                    return;
                }
            }
        });
        return request;
    }

    //Construct JSON Object body for assigning a new sensor to firebase.
    private JSONObject getJsonBodyForAssign() throws JSONException {
        JSONObject requestBody = new JSONObject();
        JSONObject location = new JSONObject();

        requestBody.put(REQUEST_BODY_OWNER, _owner);
        requestBody.put(REQUEST_BODY_ASSIGNTO, _assignTo);

        location.put(LOCATION_LATITUDE, _latitude);
        location.put(LOCATION_LONGITUDE, _longitude);

        requestBody.put(REQUEST_BODY_LOCATION, location);
        requestBody.put(REQUEST_BODY_HARDWARE_SERIAL, _hardware_serial);

        return requestBody;
    }

    //Construct JSON Object body for registering a new sensor to firebase.
    private JSONObject getJsonBodyForRegister() throws JSONException {
        JSONObject requestBody = new JSONObject();

        requestBody.put(REQUEST_BODY_OWNER, _owner);
        requestBody.put(REQUEST_BODY_HARDWARE_SERIAL, _hardware_serial);

        return requestBody;
    }

    //If user has granted permissions, fetch location.
    private boolean checkLocationPermission() {
        //Check if location permission is granted
        int rc = ActivityCompat.checkSelfPermission(AddMonitoredObjectActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);
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

    //Request user permission to use location data
    private void requestLocationPermission() {
        final Activity thisActivity = AddMonitoredObjectActivity.this;
        final String[] permissions = new String[]
                {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

        ActivityCompat.requestPermissions(thisActivity, permissions, RC_HANDLE_LOCATION_PERM);
    }

    //Handle location request callback
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == RC_HANDLE_LOCATION_PERM){
            try {
                FinalizeAddingSensor();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (checkLocationPermission())
                return;
            else {
                Log.e("ADDNewObject", "Permissions not granted." +
                        " Result code = " + (grantResults.length > 0 ? grantResults[0] : "(empty)"));
            }
        }
        else{
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    //Validate sensor name
    private boolean invalidSensorName() {
        String Name = editTextSensorName.getText().toString();
        return Name.isEmpty() && Name.matches("");
    }

    //Update UI when returning from Barcode Capture based on activity result.
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
                    imageViewTree.setVisibility(View.VISIBLE);
                    textViewInformationToUser.setText(R.string.text_give_sensor_a_name);
                    textViewUUIDValue.setText(barcode.displayValue);
                    _hardware_serial = barcode.displayValue;
                } else {
                    Toast.makeText(AddMonitoredObjectActivity.this, MISSING_BARCODE, Toast.LENGTH_LONG).show();
                }
            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
