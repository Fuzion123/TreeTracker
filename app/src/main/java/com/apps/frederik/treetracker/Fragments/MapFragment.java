package com.apps.frederik.treetracker.Fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.apps.frederik.treetracker.DetailActivity;
import com.apps.frederik.treetracker.Globals;
import com.apps.frederik.treetracker.Model.MonitoredObject.MonitoredObject;
import com.apps.frederik.treetracker.Model.Util.Coordinate;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;

import com.apps.frederik.treetracker.R;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
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
    private CameraPosition _camPosition;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);

        _mapView = (MapView) rootView.findViewById(R.id.mapView);

        _mapView.onCreate(savedInstanceState);
        _mapView.onResume();

        setRetainInstance(true); // saves complex objects on orientation rotation


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

                _googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {
                        int cnt = _markers.size();
                        for (int i = 0; i< cnt; i++)
                        {
                            if(_markers.get(i).equals(marker)){
                                String uuid = _objects.get(i).getUUID();
                                Intent detail = new Intent(getContext(), DetailActivity.class);
                                detail.putExtra(Globals.UUID, uuid);
                                startActivity(detail);
                            }
                        }
                    }
                });
            }
        });

        return rootView;
    }

    @Override
    public void AddMonitoredObject(MonitoredObject obj) {
        _objects.add(obj);

        AddMarker(obj);
        SetMapZoom();
    }

    @Override
    public void SetAllData(List<MonitoredObject> objs) {
        _objects = objs;
        for (MonitoredObject obj:_objects) {
            AddMarker(obj);
        }
        SetMapZoom();
    }

    private void AddMarker(MonitoredObject obj){
        if(_googleMap != null){
            Coordinate c = obj.getCoordinate();
            LatLng latLng = new LatLng(c.getLatitude(), c.getLongitude());
            Marker m = _googleMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .anchor(0.5f,0.5f)
                    .title(obj.getDescription() + "\ngo to details")
                    .draggable(false)
                    .icon(BitmapDescriptorFactory.fromBitmap(findMarkerImage(obj.getMetadata().getType()))));

            _markers.add(m);


        }
    }



    private Bitmap findMarkerImage(String type){
        // to make image smaller
        int height = 100;
        int width = 80;
        BitmapDrawable bitmapdraw;

        // choose different marker images depending on the type.
        switch (type){
            case "tree":{
                bitmapdraw = (BitmapDrawable)getResources().getDrawable(R.drawable.tree);
                break;
            }
            default:{
                bitmapdraw = (BitmapDrawable)getResources().getDrawable(R.drawable.marker_default);
            }
        }
        Bitmap bitmap = bitmapdraw.getBitmap();
        return Bitmap.createScaledBitmap(bitmap,width, height, false);
    }

    // inspired by: https://stackoverflow.com/questions/14828217/android-map-v2-zoom-to-show-all-the-markers
    private void SetMapZoom(){
        if(_googleMap == null) return;

        if(_camPosition != null){
            _googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(_camPosition));
            return;
        }

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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(_googleMap == null) return;
        _camPosition = _googleMap.getCameraPosition();
    }
}
