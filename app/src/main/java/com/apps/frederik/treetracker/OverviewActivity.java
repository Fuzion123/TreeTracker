package com.apps.frederik.treetracker;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
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
import android.widget.Button;
import android.widget.Toast;
import com.apps.frederik.treetracker.ListFragment.SensorListFragment;
import com.apps.frederik.treetracker.ListFragment.dummy.DummyContent;
import com.apps.frederik.treetracker.MapFragment.MapFragment;
import com.apps.frederik.treetracker.Model.DataAccessLayer.FakeDatabaseRepository;
import com.apps.frederik.treetracker.Model.DataAccessLayer.SensorManagement;
import com.apps.frederik.treetracker.Model.Util.GpsCoordinate;
import com.apps.frederik.treetracker.Model.Util.GpsFakeGenerator;
import com.apps.frederik.treetracker.SensorService.SensorServiceBinder;
import java.text.ParseException;
import java.util.List;

public class OverviewActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, SensorListFragment.OnListFragmentInteractionListener {
    private SensorServiceBinder _binder;
    private boolean _isBoundToService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);




        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OverviewActivity.this, AddSensorActivity.class);
                startActivity(intent);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // inflating fragment
        // inspired by: https://developer.android.com/training/basics/fragments/fragment-ui.html
        if(findViewById(R.id.fragment_container) != null){
            if(savedInstanceState != null){
                return;
            }

            //SensorListFragment listFragment = new SensorListFragment();
            //getSupportFragmentManager().beginTransaction()
              //      .add(R.id.fragment_container, listFragment).commit();

            // TODO coment in the listView if desired!
            MapFragment mapFragment = new MapFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, mapFragment).commit();


        }

        Intent intentService = new Intent(this, SensorService.class);
        bindService(intentService, _connection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        if(_isBoundToService){
            unbindService(_connection);
        }
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        Log.d("OverviewActivity", "onResume");
        LocalBroadcastManager.getInstance(this).registerReceiver(onSensorAddedReceiver, new IntentFilter(Globals.LOCAL_BROADCAST_NEW_SENSOR_ADDED));
        LocalBroadcastManager.getInstance(this).registerReceiver(onReadingAddedReceiver, new IntentFilter(Globals.LOCAL_BROADCAST_NEW_READING));

        super.onResume();
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(onSensorAddedReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(onReadingAddedReceiver);
        super.onPause();
    }


    private BroadcastReceiver onSensorAddedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("OverviewActivity", "New Sensor Added");
        }
    };

    private BroadcastReceiver onReadingAddedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("OverviewActivity", "New Reading Added");
        }
    };

    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection _connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder binder) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            _binder = (SensorServiceBinder) binder;
            _isBoundToService = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            _isBoundToService = false;
        }
    };

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
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

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }



    @Override
    public void onListFragmentInteraction(DummyContent.DummyItem item) {
        Toast.makeText(this, "Item Details: " + item.details, Toast.LENGTH_SHORT).show();
    }
}
