package com.apps.frederik.treetracker;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.samples.vision.barcodereader.BarcodeCaptureActivity;
import com.google.android.gms.vision.barcode.Barcode;
import android.widget.Toast;

import com.apps.frederik.treetracker.ListFragment.SensorListFragment;
import com.apps.frederik.treetracker.ListFragment.dummy.DummyContent;
import com.apps.frederik.treetracker.Model.Provider.FakeHumidityDataProvider;
import com.apps.frederik.treetracker.Model.Provider.FakeHumiditySensorProvider;
import com.apps.frederik.treetracker.Model.Sensor.HumiditySensor;
import com.apps.frederik.treetracker.Model.Sensor.ISensor;
import com.apps.frederik.treetracker.Model.Sensor.SensorData.ISensorData;

import java.text.ParseException;
import java.util.List;

public class OverviewActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, SensorListFragment.OnListFragmentInteractionListener {

    private static final int RC_BARCODE_CAPTURE = 1234;
    private static final String TAG = "OverviewActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OverviewActivity.this, BarcodeCaptureActivity.class);
                startActivityForResult(intent, RC_BARCODE_CAPTURE);
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // inflating fragment
        // inspired by: https://developer.android.com/training/basics/fragments/fragment-ui.html
        if(findViewById(R.id.fragment_container) != null){
            if(savedInstanceState != null){
                return;
            }

            SensorListFragment listFragment = new SensorListFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, listFragment).commit();
        }


        try {
            int sensorCount = 10;
            FakeHumiditySensorProvider provider = new FakeHumiditySensorProvider(sensorCount);
            List<ISensor> sensors = provider.GetAllSensors();

            for (ISensor sensor : sensors) {

                Log.d("Test", "Sensor Name: "+ sensor.GetName());
                Log.d("Test", "Sensor UUID: " + sensor.GetUuid());
                Log.d("Test", "Sensor GPS Coordinates: (Lat: " + sensor.GetCoordinate().GetLatitude() + ", Long: " + sensor.GetCoordinate().GetLongitude()+")");

                FakeHumidityDataProvider dataProvider = new FakeHumidityDataProvider(10);
                List<ISensorData> data = dataProvider.GetAllReadings("hehe");

                for (ISensorData d : data) {
                    sensor.GetHistoricalData().add(d);
                    Log.d("Test", "SensorReading Data: " + d.GetData());
                    Log.d("Test", "SensorReading Timetstamp: " + d.GetTimeStamp().get_year());
                }
                Log.d("Test", "Sensor Readings: " + sensor.GetHistoricalData().size());
             }
            }catch (ParseException e) {
                e.printStackTrace();
            }



    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.overview, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_BARCODE_CAPTURE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
                    Toast.makeText(this, "Now Tracking: "+barcode.displayValue, Toast.LENGTH_LONG).show();
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

    @Override
    public void onListFragmentInteraction(DummyContent.DummyItem item) {
        Toast.makeText(this, "Item Details: " + item.details, Toast.LENGTH_SHORT).show();
    }
}
