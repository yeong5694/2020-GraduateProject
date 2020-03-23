package com.graduate.a2020_graduateproject;

import android.graphics.Color;
import android.location.Geocoder;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

public class MapDistanceActivity  extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap gMap;
    private Geocoder geocoder;
    private PolylineOptions polylineOptions;
    private ArrayList<LatLng> latList; //위도, 경도 정보 넣어두는 ArrayList

    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_distance_layout);

        ////지도
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_distance);
        mapFragment.getMapAsync(this);

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        ////일단 좌표 찍어서 직선거리 보여줌 -> DB에 들어있는 정보 이용해서 직선거리 구하기

        gMap=googleMap;
        geocoder=new Geocoder(this);
        latList=new ArrayList<LatLng>();
        //한성대학교 위치 찍기
        LatLng Hansung = new LatLng(37.582465, 127.009393); //Hansung University 위도, 경도
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(Hansung);
        markerOptions.title("Hansung University");
        markerOptions.snippet("Hansung University");
        gMap.addMarker(markerOptions);
        latList.add(Hansung);

        LatLng Hansung2 = new LatLng(37.592716, 127.016372); //Hansung University 위도, 경도
        MarkerOptions markerOptions2 = new MarkerOptions();
        markerOptions2.position(Hansung2);
        markerOptions2.title("Hansung University2");
        markerOptions2.snippet("Hansung University2");
        gMap.addMarker(markerOptions2);
        latList.add(Hansung2);

        polylineOptions=new PolylineOptions();
        polylineOptions.color(Color.RED);
        polylineOptions.width(5);
        polylineOptions.addAll(latList);
        gMap.addPolyline(polylineOptions);

        //지도 위치를 Hansung으로 맞춤
        gMap.moveCamera(CameraUpdateFactory.newLatLng(Hansung));
        gMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        //1로 지정하면 세계지도 -> 숫자가 클수록 상세하게 나타남




    }
}
