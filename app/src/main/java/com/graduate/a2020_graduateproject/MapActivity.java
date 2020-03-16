package com.graduate.a2020_graduateproject;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
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

    private GoogleMap gMap; //구글 지도
    private Geocoder geocoder; //
    private Button place_Btn; //장소 찾는 버튼
    private EditText place_Text; //입력한 장소
    private TextView marker_place;
    private View bubbleView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_layout);

        place_Text=(EditText)findViewById(R.id.place_Text); //입력한 장소
        place_Btn=(Button)findViewById(R.id.place_Btn); //찾기 버튼

        //장소 이름
        marker_place=(TextView)findViewById(R.id.marker_place);
        //말풍선 xml 가져오기
        bubbleView= LayoutInflater.from(this).inflate(R.layout.bubblelayout, null);



      ////지도
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

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
                /* System.out.println(addressList.get(0).toString()); 했을 때 나오는 정보
                Address[addressLines=[0:"대한민국 서울특별시 한성대입구역"],feature=한성대입구역,admin=서울특별시,
                 sub-admin=null,locality=null,thoroughfare=null,postalCode=null,countryCode=KR,countryName=대한민국,
                 hasLatitude=true,latitude=37.588374,hasLongitude=true,longitude=127.005907,phone=null,url=null,extras=null]*/
                //10, 12
                System.out.println(addressList.get(0).toString());
                String []Places=addressList.get(0).toString().split(",");
                String address = Places[0].substring(Places[0].indexOf("\"") + 1,Places[0].length() - 2);

                //위도, 경도 구하기
                Double xpos=Double.parseDouble(Places[10].substring(Places[10].indexOf("=")+1));
                Double ypos=Double.parseDouble(Places[12].substring(Places[12].indexOf("=")+1));

                LatLng latLng=new LatLng(xpos, ypos);
                MarkerOptions markerOptions3=new MarkerOptions();
                markerOptions3.title("result");
                markerOptions3.snippet(address);
                markerOptions3.position(latLng);
                Marker marker=gMap.addMarker(markerOptions3);
                marker.showInfoWindow();
                gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
              //  showPlaces(xpos, ypos);

                Log.d("a","showPlaces 호출됨");
                new NRPlaces.Builder()
                        .listener(MapActivity.this)
                        .key("AIzaSyANE7MTnzzpaQ08SsN9quflkstM-cC1tIw")
                        //place api키 새로 만들어서 제한사항을 web으로 바꿈, 안드로이드앱으로 해서 계속 허가 거부당함
                        .latlng(xpos, ypos) //입력한 장소 위치에서
                        .radius(500) //500미터 이내
                        .type(PlaceType.AMUSEMENT_PARK)
                        .build()
                        .execute();
            }
        });
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {

        gMap = googleMap;
        geocoder=new Geocoder(this);

        //한성대학교 위치 찍기
        LatLng Hansung = new LatLng(37.582465, 127.009393); //Hansung University 위도, 경도 저장
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(Hansung);
        markerOptions.title("Hansung University"); //title
        markerOptions.snippet("Hansung University"); //눌렀을 때 나오는 부가설명
       // markerOptionsshowInfo();

        gMap.addMarker(markerOptions);//지도에 마커 추가
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
        //         googleMap.addMarker(markerOptions2);
            }
        });

    }

    //커스텀 마커 추가->입력한 위치 주변 장소 띄우기 ///장소 이름 기본으로 뜨도록
    //선택하면 색깔 변하도록->데이터베이스에 저장.....할 예정
    @Override
    public void onPlacesSuccess(final List<Place> places){
        //placeListener 상속받는 함수 //찾는거 성공시 호출
        System.out.println("onPlacesSuccess 호출");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for(noman.googleplaces.Place  place : places){
                    LatLng latLng=new LatLng(place.getLatitude(), place.getLongitude());

                    MarkerOptions markerPOptions=new MarkerOptions();
                    markerPOptions.position(latLng);
                    markerPOptions.title(place.getName());
//                    markerPOptions.title("찾았다!!!");

                    Marker marker=gMap.addMarker(markerPOptions);
                //    marker.showInfoWindow();
                }
            }
        });
    }

    @Override
    public void onPlacesFinished() {
        //placeListener 상속받는 함수
    }

    @Override
    public void onPlacesFailure(PlacesException e) {
        //placeListener 상속받는 함수
    }

    @Override
    public void onPlacesStart() {
        //placeListener 상속받는 함수
    }

    public Marker addMarker( LatLng lat, String name, boolean isSelected){
        marker_place.setText(name);
        if(isSelected){
            marker_place.setTextColor(Color.RED);
        }
        else{
            marker_place.setTextColor(Color.BLACK);
        }

        MarkerOptions markerOptions=new MarkerOptions();
        markerOptions.title(name);
        markerOptions.position(lat);
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(this, bubbleView)));

        return gMap.addMarker(markerOptions);
    }

    //View를 Bitmap으로 변환
    private Bitmap createDrawableFromView(MapActivity mapActivity, View bubbleView) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        mapActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        bubbleView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        bubbleView.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        bubbleView.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        bubbleView.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(bubbleView.getMeasuredWidth(), bubbleView.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        bubbleView.draw(canvas);

        return bitmap;

    }

}
