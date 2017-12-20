package com.apps.frederik.treetracker.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.apps.frederik.treetracker.Model.MonitoredObject.MonitoredObject;
import com.apps.frederik.treetracker.Model.Util.Coordinate;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;

import com.apps.frederik.treetracker.R;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

// some of this fragment is copied from: https://stackoverflow.com/questions/19353255/how-to-put-google-maps-v2-on-a-fragment-using-viewpager

public class MapFragment extends MonitoredObjectFragment {
    private MapView _mapView;
    private GoogleMap _googleMap = null;
    private List<MonitoredObject> _objects = new ArrayList<>();
    private List<Marker> _markers = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);

        _mapView = (MapView) rootView.findViewById(R.id.mapView);

        _mapView.onCreate(savedInstanceState);

        _mapView.onResume();

        try{
            MapsInitializer.initialize(getActivity().getApplicationContext());
        }catch(Exception e){
            e.printStackTrace();
        }

        _mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                _googleMap = googleMap;
                for(MonitoredObject obj : _objects){
                    AddMarker(obj);
                }
                SetMapZoom();
            }
        });

        return rootView;
    }

    public void SetMonitoredObjects(List<MonitoredObject> objects) {
        _objects = objects;
        for (MonitoredObject obj:_objects) {
            AddMarker(obj);
        }
        SetMapZoom();
    }

    private void AddMarker(MonitoredObject obj){
        if(_googleMap != null){
            Coordinate c = obj.getCoordinate();
            LatLng latLng = new LatLng(c.getLatitude(), c.getLongitude());
            _markers.add(_googleMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title(obj.getDescription())));

        }
    }
    // inspired by: https://stackoverflow.com/questions/14828217/android-map-v2-zoom-to-show-all-the-markers
    private void SetMapZoom(){
        if(_googleMap!= null){
            int cnt =_markers.size();
            if(cnt == 0){
                return; // no zoom
            }

            // camera object that specifies where to "look" on map
            CameraUpdate cameraUpdate;

            // only 1 marker on map have no area, so cant create bounds : sets a fixed zoom to that specific marker pos.
            if(cnt == 1){
                cameraUpdate = CameraUpdateFactory.newLatLngZoom(_markers.get(0).getPosition(), 12F);
            }
            // multiple markers can create an area of where to zoomn
            else{
                LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();

                for (Marker m: _markers) {
                    boundsBuilder.include(m.getPosition());
                }
                LatLngBounds bounds = boundsBuilder.build();

                int padding = 200; // additional padding  to not be zoomed all the way into the boundary area
                cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding);
            }
            _googleMap.moveCamera(cameraUpdate);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        _mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        _mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        _mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        _mapView.onLowMemory();
    }
}
