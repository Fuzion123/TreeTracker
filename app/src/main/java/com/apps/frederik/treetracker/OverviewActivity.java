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
import android.support.v4.app.Fragment;
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
import android.widget.Toast;

import com.apps.frederik.treetracker.Fragments.IActivityToFragmentCommunication;
import com.apps.frederik.treetracker.Fragments.ListFragment;
import com.apps.frederik.treetracker.Fragments.MapFragment;
import com.apps.frederik.treetracker.Fragments.MonitoredObjectFragment;
import com.apps.frederik.treetracker.Model.MonitoredObject.MonitoredObject;
import com.apps.frederik.treetracker.SensorService.SensorServiceBinder;

import java.util.List;

public class OverviewActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, ListFragment.OnListFragmentInteractionListener {
    private SensorServiceBinder _binder;
    private boolean _isBoundToService;
    private MonitoredObjectFragment _currentFragement;
    private final String FRAGMENT_LIST_TAG = "com.apps.frederik.treetracker.listFragment";
    private final String FRAGMENT_MAP_TAG = "com.apps.frederik.treetracker.mapFragment";


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

            _currentFragement = new ListFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, _currentFragement, FRAGMENT_LIST_TAG)
                    .addToBackStack(FRAGMENT_LIST_TAG) // previous state will be added to the backstack allowing you to go back with the back button. cite from: https://stackoverflow.com/questions/14354885/android-fragments-backstack
                    .commit();
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

            List<MonitoredObject> objects = _binder.GetAllMonitoredObjects();
            _currentFragement.SetMonitoredObjects(objects);
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

    // inspired from: https://guides.codepath.com/android/fragment-navigation-drawer
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Class fragmentClass;

        // TODO all other menu items displayed are nmot yet implemented, resulting in no reponse
        if (id == R.id.nav_listFragment) {
            fragmentClass = ListFragment.class;
            InstantiateFragment(FRAGMENT_LIST_TAG, fragmentClass);
        } else if (id == R.id.nav_mapFragment) {
            fragmentClass = MapFragment.class;
            InstantiateFragment(FRAGMENT_MAP_TAG, fragmentClass);
        } else{
            fragmentClass = ListFragment.class; // default behavior!
            InstantiateFragment(FRAGMENT_LIST_TAG, fragmentClass);
        }

        item.setChecked(true);
        setTitle(item.getTitle());
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }


    private void InstantiateFragment(String fragmentTag, Class fragmentClass){
        // used to hide the current fragment
        String currentFragmentActive = (fragmentTag.equals(FRAGMENT_LIST_TAG) ? FRAGMENT_MAP_TAG : FRAGMENT_LIST_TAG);

        // inspired from: https://stackoverflow.com/questions/22713128/how-can-i-switch-between-two-fragments-without-recreating-the-fragments-each-ti
        if(getSupportFragmentManager().findFragmentByTag(fragmentTag) != null) {
            getSupportFragmentManager().beginTransaction()
                    .hide(getSupportFragmentManager().findFragmentByTag(currentFragmentActive)) // should hide automatically when showing another fragment, but map view does not hide for some reason.
                    .show(getSupportFragmentManager().findFragmentByTag(fragmentTag))
                    .commit();
        } else {
            MonitoredObjectFragment fragment;
            try{
                fragment = (MonitoredObjectFragment) fragmentClass.newInstance();
                fragment.SetMonitoredObjects(_binder.GetAllMonitoredObjects());
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, fragment, fragmentTag)
                        .addToBackStack(fragmentTag)
                        .commit();
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onListFragmentInteraction(MonitoredObject item) {
        Toast.makeText(this, "Monitored metadata: " + item.getMeta().getType(), Toast.LENGTH_SHORT).show();
    }
}
