package com.apps.frederik.treetracker;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.apps.frederik.treetracker.Fragments.ListFragment;
import com.apps.frederik.treetracker.Fragments.MapFragment;
import com.apps.frederik.treetracker.Fragments.MonitoredObjectFragment;
import com.apps.frederik.treetracker.Model.MonitoredObject.MonitoredObject;

import java.util.HashMap;
import java.util.Map;

public class OverviewActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, ListFragment.OnListFragmentInteractionListener {
    private final String FRAGMENT_LIST_TAG = "com.apps.frederik.treetracker.listFragment";
    private final String FRAGMENT_MAP_TAG = "com.apps.frederik.treetracker.mapFragment";
    private final String LAST_FRAGMENT_ACTIVE = "com.apps.frederik.treetracker.last.fragment.active";
    private MonitorService.OverviewActivityBinder _binder;
    private boolean _isBoundToService;
    private MonitoredObjectFragment _currentFragement;
    private String _currentFragmentTag = FRAGMENT_LIST_TAG; // default behavior
    private ProgressBar _loadingAnimation;
    private String _userId;
    private TextView _txtViewUserId;
    private String _userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // retrieving the user id and email from login activity
        _userId = getIntent().getExtras().getString(Globals.USERID);
        _userEmail = getIntent().getExtras().getString(Globals.EMAIL);

        // setting up the loading bar color to the theme (colorPrimary)
        // inspired from: https://stackoverflow.com/questions/2020882/how-to-change-progress-bars-progress-color-in-android
        _loadingAnimation = findViewById(R.id.loadingAnimation);
        Drawable progressDrawable = _loadingAnimation.getIndeterminateDrawable().mutate();
        progressDrawable.setColorFilter(getResources().getColor(R.color.colorPrimary), android.graphics.PorterDuff.Mode.SRC_IN);
        _loadingAnimation.setIndeterminateDrawable(progressDrawable);

        // setting up the add new monitor FAB button
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OverviewActivity.this, AddMonitoredObjectActivity.class);
                intent.putExtra(Globals.USERID, _userId);
                startActivity(intent);
            }
        });

        // setting up the navigation bar
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Using the navigationView, the header is not instantly added, which mean
        View headerLayout = navigationView.inflateHeaderView(R.layout.nav_header_overview);
        _txtViewUserId = headerLayout.findViewById(R.id.textViewUserId);
        _txtViewUserId.setText(_userEmail);

        // on orientation the state of which
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
        LocalBroadcastManager.getInstance(this).registerReceiver(onMonitoredObjectAddedReceiver, new IntentFilter(Globals.LOCAL_BROADCAST_NEW_MONITORED_OBJECT_ADDED));
        LocalBroadcastManager.getInstance(this).registerReceiver(onMonitoredObjectRemoved, new IntentFilter(Globals.LOCAL_BROADCAST_MONITORED_OBJECT_REWMOVED));

        if(_isBoundToService){
            RehreshFragment();
        }

        super.onResume();
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(onMonitoredObjectAddedReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(onMonitoredObjectRemoved);
        super.onPause();
    }


    private BroadcastReceiver onMonitoredObjectAddedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(!_isBoundToService) throw new RuntimeException("Overview Activity was not bound to service, in a time where is should!");

            String uuid = intent.getExtras().getString(Globals.UNIQUE_DESCRIPTION);
            _currentFragement.AddMonitoredObject(_binder.GetMonitoredObjectFor(uuid));
            _loadingAnimation.setVisibility(View.GONE);
        }
    };

    private BroadcastReceiver onMonitoredObjectRemoved = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(!_isBoundToService) throw new RuntimeException("Overview Activity was not bound to service, in a time where is should!");

            String uuid = intent.getExtras().getString(Globals.UNIQUE_DESCRIPTION);
            _currentFragement.RemoveMonitoredObjectFor(uuid);
        }
    };

    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection _connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder binder) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            _binder = (MonitorService.OverviewActivityBinder) binder;
            _isBoundToService = true;
            _binder.SetupRepository(_userId);

            final Handler mHandler = new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(Message message) {

                }
            };

            new java.util.Timer().schedule(
                    new java.util.TimerTask() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if(_binder.GetAllMonitoredObjects().size() == 0){
                                        Toast.makeText(OverviewActivity.this, "It looks like you have no trackers yet :-(", Toast.LENGTH_LONG).show();
                                        _loadingAnimation.setVisibility(View.GONE);
                                    }
                                }
                            });
                        }
                    },5000
            );

            RehreshFragment();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            _isBoundToService = false;
        }
    };

    private void RehreshFragment(){
        _currentFragement.RefreshAllMonitoredObject(_binder.GetAllMonitoredObjects());

        int visibility = View.VISIBLE;
        if(_currentFragement != null){
            if(_currentFragement.GetMonitoredObject().size() > 0){
                visibility= View.GONE;
            }
        }
        _loadingAnimation.setVisibility(visibility);

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
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
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
            _currentFragement.RefreshAllMonitoredObject(_binder.GetAllMonitoredObjects());
        }
    }

    @Override
    public void onListFragmentInteraction(MonitoredObject item) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(Globals.UNIQUE_DESCRIPTION, item.getUniqueDescription());
        startActivity(intent);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(LAST_FRAGMENT_ACTIVE, _currentFragmentTag);
    }
}
