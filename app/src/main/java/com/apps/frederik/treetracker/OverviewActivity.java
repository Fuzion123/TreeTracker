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

import com.apps.frederik.treetracker.Fragments.ListFragment;
import com.apps.frederik.treetracker.Fragments.MapFragment;
import com.apps.frederik.treetracker.Fragments.MonitoredObjectFragment;
import com.apps.frederik.treetracker.Model.MonitoredObject.MonitoredObject;
import com.apps.frederik.treetracker.MonitorService.MonitorServiceBinder;

import java.util.List;

import static com.apps.frederik.treetracker.Globals.UUID_DETAILED_MONITORED_OBJECT;

public class OverviewActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, ListFragment.OnListFragmentInteractionListener {
    private MonitorServiceBinder _binder;
    private boolean _isBoundToService;
    private MonitoredObjectFragment _currentFragement;
    private final String FRAGMENT_LIST_TAG = "com.apps.frederik.treetracker.listFragment";
    private final String FRAGMENT_MAP_TAG = "com.apps.frederik.treetracker.mapFragment";
    private final String LAST_FRAGMENT_ACTIVE = "com.apps.frederik.treetracker.last.fragment.active";
    private String _currentFragmentTag = FRAGMENT_LIST_TAG; // default behavior

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


        if(savedInstanceState != null) {
            _currentFragmentTag = savedInstanceState.getString(LAST_FRAGMENT_ACTIVE);

            if (_currentFragmentTag.equals(FRAGMENT_LIST_TAG)) {
                navigationView.setCheckedItem(R.id.nav_listFragment);
            } else {
                navigationView.setCheckedItem(R.id.nav_mapFragment);
            }
        }
        else {
            navigationView.setCheckedItem(R.id.nav_listFragment);
        }

        InstantiateFragmentByTag(_currentFragmentTag);

        Intent intentService = new Intent(this, MonitorService.class);
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
            _binder = (MonitorServiceBinder) binder;
            _isBoundToService = true;

            if(_currentFragement == null) return;

            List<MonitoredObject> objects = _binder.GetAllMonitoredObjects();
            _currentFragement.SetData(objects);
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

        if (id == R.id.nav_listFragment) {
            _currentFragmentTag = FRAGMENT_LIST_TAG;
            InstantiateFragmentByTag(_currentFragmentTag);
        } else if (id == R.id.nav_mapFragment) {
            _currentFragmentTag = FRAGMENT_MAP_TAG;
            InstantiateFragmentByTag(_currentFragmentTag);
        } else{
            _currentFragmentTag = FRAGMENT_LIST_TAG;
            InstantiateFragmentByTag(_currentFragmentTag);
        }

        item.setChecked(true);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }


    private void InstantiateFragmentByTag(String fragmentTag){
        if(fragmentTag.equals(FRAGMENT_LIST_TAG)){
            _currentFragement = new ListFragment();
        }
        else{
            _currentFragement = new MapFragment();
        }

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, _currentFragement)
                .commit();

        if(_isBoundToService) {
            _currentFragement.SetData(_binder.GetAllMonitoredObjects());
        }
    }

    @Override
    public void onListFragmentInteraction(MonitoredObject item) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(UUID_DETAILED_MONITORED_OBJECT, item.getUUID());
        startActivity(intent);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(LAST_FRAGMENT_ACTIVE, _currentFragmentTag);
    }
}
