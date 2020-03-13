package com.graduate.a2020_graduateproject;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

import noman.googleplaces.NRPlaces;
import noman.googleplaces.Place;
import noman.googleplaces.PlaceType;
import noman.googleplaces.PlacesException;
import noman.googleplaces.PlacesListener;

public class MapActivity extends AppCompatActivity
        implements OnMapReadyCallback, PlacesListener {

    private GoogleMap gMap;
    private Geocoder geocoder;
    private Button place_Btn;
    private EditText place_Text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_layout);

        place_Text=(EditText)findViewById(R.id.place_Text); //장소 입력하는 공간
        place_Btn=(Button)findViewById(R.id.place_Btn);//장소 찾기 버튼

      ////지도
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {

        gMap = googleMap;
        geocoder=new Geocoder(this);

        //한성대학교 위치 찍기
        LatLng Hansung = new LatLng(37.582465, 127.009393); //Hansung University 위도, 경도
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(Hansung);
        markerOptions.title("Hansung University");
        markerOptions.snippet("Hansung University");
        gMap.addMarker(markerOptions);

        //지도 위치를 Hansung으로 맞춤
        gMap.moveCamera(CameraUpdateFactory.newLatLng(Hansung));
        gMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        //1로 지정하면 세계지도 -> 숫자가 클수록 상세하게 나타남

        //지도 클릭했을 때 마커 찍기
        gMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                MarkerOptions markerOptions2=new MarkerOptions();
                markerOptions2.title("좌표");
                Double xpos=latLng.latitude; //위도
                Double ypos=latLng.longitude; //경도
                markerOptions2.snippet(xpos.toString()+", "+ypos.toString());
                markerOptions2.position(new LatLng(xpos, ypos));
                googleMap.addMarker(markerOptions2);
            }
        });

        place_Btn.setOnClickListener(new Button.OnClickListener(){

            @Override
            public void onClick(View v) {

                String place=place_Text.getText().toString();
                List<Address> addressList=null;
                try {
                    addressList=geocoder.getFromLocationName(
                            place, 10 //장소, 최대 검색 결과 개수
                    );
                } catch (IOException e) {
                    e.printStackTrace();
                }
                /*Address[addressLines=[0:"대한민국 서울특별시 한성대입구역"],feature=한성대입구역,admin=서울특별시,
                 sub-admin=null,locality=null,thoroughfare=null,postalCode=null,countryCode=KR,countryName=대한민국,
                 hasLatitude=true,latitude=37.588374,hasLongitude=true,longitude=127.005907,phone=null,url=null,extras=null]*/
                //10, 12
                System.out.println(addressList.get(0).toString());
                String []Places=addressList.get(0).toString().split(",");
                String address = Places[0].substring(Places[0].indexOf("\"") + 1,Places[0].length() - 2);

                Double xpos=Double.parseDouble(Places[10].substring(Places[10].indexOf("=")+1));
                Double ypos=Double.parseDouble(Places[12].substring(Places[12].indexOf("=")+1));

                LatLng latLng=new LatLng(xpos, ypos);
                MarkerOptions markerOptions3=new MarkerOptions();
                markerOptions3.title("result");
                markerOptions3.snippet(address);
                markerOptions3.position(latLng);
                gMap.addMarker(markerOptions3);
                gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                showPlaces(xpos, ypos);
            }
        });

    }

    public void showPlaces(double xpos, double ypos){
        //gMap.clear();
        Log.d("a","showPlaces 호출됨");
        new NRPlaces.Builder()
                .listener(MapActivity.this)
                .key("AIzaSyDiQGIg5FkdX06OfqIb9d-9R1SAsCdmGeg")
                .latlng(xpos, ypos)
                .radius(500)
                .type(PlaceType.RESTAURANT)
                .build()
                .execute();


    }

    //커스텀 마커 추가->입력한 위치 주변 장소 띄우기
    //선택하면 색깔 변하도록->데이터베이스에 저장.....할 예정
    public void onPlacesSuccess(final List<Place> places){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for(Place place : places){
                    LatLng latLng=new LatLng(place.getLatitude(), place.getLongitude());

                    MarkerOptions markerPOptions=new MarkerOptions();
                    markerPOptions.position(latLng);
                    markerPOptions.title(place.getName());
                    Marker item=gMap.addMarker(markerPOptions);

                }
            }
        });
    }

    @Override
    public void onPlacesFinished() {

    }

    @Override
    public void onPlacesFailure(PlacesException e) {

    }

    @Override
    public void onPlacesStart() {

    }

}
