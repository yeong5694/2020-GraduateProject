package com.graduate.a2020_graduateproject;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

public class Map_realFindRoadActivity extends AppCompatActivity
        implements OnMapReadyCallback {

    private GoogleMap gMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_realfindroad_layout);

        //// 지도 Fragment
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.findMap);
        mapFragment.getMapAsync(this);


    }
    @Override
    public void onMapReady(GoogleMap googleMap) {

    }
}
