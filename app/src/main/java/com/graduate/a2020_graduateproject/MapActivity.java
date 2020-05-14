package com.graduate.a2020_graduateproject;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import noman.googleplaces.NRPlaces;
import noman.googleplaces.Place;
import noman.googleplaces.PlaceType;
import noman.googleplaces.PlacesException;
import noman.googleplaces.PlacesListener;

public class MapActivity extends AppCompatActivity
        implements OnMapReadyCallback, PlacesListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap gMap;
    private Geocoder geocoder;
    private Button place_Btn;
    private EditText place_Text;
    private Button go_Btn; //직선거리 보여주는 화면으로 이동 //나중에 바꿀예정
    private Button find_Btn;
    private ArrayList<LatLng> clickList;
    private ArrayList<LatLng> markerList;

    //내가 사용할 데이터베이스 인스턴스 불러오기
    private FirebaseDatabase database= null;
    private DatabaseReference databaseReference=null;
    private Map<String, Object> info;
    private Map<String, Object> clickinfo;

    private AddressResultReceiver resultReceiver;

    private String addressOutput="";

    //private MarkerAdapter markerAdapter;

//    markerAdapter=new MarkerAdapter();

    private MqttClient mqttClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_layout);

        try {

            System.out.println("onCreate-mqtt Connect");
            connectMqtt();
        } catch (Exception e) {
            e.printStackTrace();
        }


//        mqttClient=new MqttClient("tcp://18.204.210.252:1883", MqttClient.generateClientId(),null);
  //     mqttClient.connect();

        place_Text=(EditText)findViewById(R.id.place_Text); //장소 입력하는 공간
        place_Btn=(Button)findViewById(R.id.place_Btn);//장소 찾기 버튼

        go_Btn=findViewById(R.id.go_button);
        find_Btn=findViewById(R.id.find_button);

//        markerList=new ArrayList<MarkerInfo>();
        markerList=new ArrayList<>(); //장소찾기 버튼을 통한 저장
        clickList=new ArrayList<>(); //클릭한 장소 저장

        database= FirebaseDatabase.getInstance();
       // databaseReference=database.getReference("SharingTrips");

        ////지도 띄우기
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.planningMap);
        mapFragment.getMapAsync(this);

         go_Btn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intent =new Intent(getApplicationContext(), MapDistanceActivity.class);
                System.out.println("MapActivity에서 MapDistanceActivity로 ");
             //   intent.putParcelableArrayListExtra("markerList", markerList);
                intent.putExtra("markerList", markerList);
                intent.putExtra("clickList", clickList);
                try {
                    mqttClient.disconnect();
                    mqttClient.close();
                    System.out.println("disconnect !! ");

                } catch (MqttException e) {
                    e.printStackTrace();
                }
                startActivity(intent);
            }
        });


         find_Btn.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
               ///////////  Intent findIntent=new Intent(getApplicationContext(), MapFindRoadActivity.class);
                 Intent findIntent=new Intent(getApplicationContext(), TMapActivity.class);

                 try {
                     mqttClient.disconnect();
                     mqttClient.close();

                     System.out.println("disconnect !! ");
                 } catch (MqttException e) {
                     e.printStackTrace();
                 }

                 System.out.println("MapActivity에서 MapFindRoadActivity로 ");
                 startActivity(findIntent);
             }
         });

        place_Btn.setOnClickListener(new Button.OnClickListener(){ //장소 찾기 버튼 누르면

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
                MarkerOptions markerOptions=new MarkerOptions();
                 markerOptions.title(place);
                markerOptions.snippet(address);
                markerOptions.position(latLng);
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)); //마커 색깔 파란색으로
                //   HUE_MAGENTA, HUE_VioLet, HUE_ORANGE, HUE_RED, HUE_BLUE, HUE_GREEN, HUE_AZURE, HUE_ROSE, HUE_CYAN, HUE_YELLOW

                Marker marker=gMap.addMarker(markerOptions);
//                markerList.add(new MarkerInfo(latLng.latitude, latLng.longitude, place));

                //firebase에 추가하기
                info=new MarkerInfo(latLng.latitude, latLng.longitude, place).toMap();

//                databaseReference.child("MapInfo").child("find").push().setValue(info);

                markerList.add(latLng);

                marker.showInfoWindow(); //장소 정보 보여주기


                gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));

                //찾은 장소 주변 관광지 보여주기
                Log.d("a","showPlaces 호출됨");
                new NRPlaces.Builder()
                        .listener(MapActivity.this)
                        .key("AIzaSyANE7MTnzzpaQ08SsN9quflkstM-cC1tIw")
                        //place api키 새로 만들어서 제한사항을 web으로 바꿈, 안드로이드앱으로 해서 계속 허가 거부당함
                        .latlng(xpos, ypos) //입력한 장소 위치에서
                        .radius(500) //500미터 이내
                        .type(PlaceType.RESTAURANT)
                        .build()
                        .execute();
            }
        });
    }

    static String TOPIC="googlemap2";

    private void connectMqtt() throws  Exception{

        System.out.println("ConnectMqtt() 시작");  /// 192.168.0.5   18.204.210.252 tcp://192.168.56.1:1883 //탄력적 ip 3.224.178.67
        mqttClient=new MqttClient("tcp://3.224.178.67:1883", MqttClient.generateClientId(), null);
        System.out.println("ConnectMqtt() 연결 준비" +MqttClient.generateClientId());

        mqttClient.connect();

        System.out.println("ConnectMqtt() 연결" +MqttClient.generateClientId());

        mqttClient.subscribe(TOPIC);

        mqttClient.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                System.out.println("mqtt reconnect");
                try{
                    connectMqtt();
                }catch (Exception e){
                    System.out.println("mqtt connect error");
                }
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
              System.out.println("messageArrived");
                JSONObject json=new JSONObject(new String(message.getPayload(), "UTF-8"));

             //   markerAdapter.add(new MarkerInfo(Double.parseDouble(json.getString("lat")), Double.parseDouble(json.getString("lng")),json.getString("name")));
               // System.out.println("markerAdapter messageArrived : "+markerAdapter);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        MarkerOptions markerOptions=new MarkerOptions();
                        try {
                            double lat,lng; String name;
                            lat=Double.parseDouble(json.getString("lat"));
                            lng=Double.parseDouble(json.getString("lng"));
                            name=json.getString("name");
                            markerOptions.position(new LatLng(lat, lng));
                            markerOptions.title(name);
//                            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

                            gMap.addMarker(markerOptions);

                            System.out.println("Mqtt Subscribe 받아서 마커 찍음");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }



                 //       markerAdapter.notifyDataSetChanged();
                    }
                });
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {

        gMap = googleMap;
        geocoder=new Geocoder(this);
        // 내부적인 좌표값에 대한 정보 얻기 위한 객체

        gMap.clear();
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

                startIntentService(latLng);

                MarkerOptions markerOptions2=new MarkerOptions();
                markerOptions2.position(latLng);
                markerOptions2.title(addressOutput);
                markerOptions2.snippet(latLng.latitude+", "+latLng.longitude);

                String lat=Double.toString(latLng.latitude);
                String lng=Double.toString(latLng.longitude);
                String name=addressOutput;

                JSONObject json=new JSONObject();
                try {
                    json.put("lat", lat);
                    json.put("lng", lng);
                    json.put("name", name);

                    mqttClient.publish(TOPIC, new MqttMessage(json.toString().getBytes()));
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("안보내짐....");
                }

                googleMap.addMarker(markerOptions2);

                clickList.add(latLng);

             //   clickinfo=new MarkerInfo(latLng.latitude, latLng.longitude, place).toMap();
               // databaseReference.child("MapInfo").child("click").push().setValue(info);

            }
        });

        gMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                clickList.clear();
                gMap.clear();

            }
        });
    }


    //ResultReceiver->주소조회 결과를 처리하기 위한 인스턴스
    public void startIntentService(LatLng latLng){ //역지오코딩 실행
        resultReceiver=new AddressResultReceiver(new android.os.Handler());
        Intent intent=new Intent(this, MapFetchAddressIntentService.class);
        intent.putExtra(MapConstants.RECEIVER, resultReceiver);
        intent.putExtra(MapConstants.LOCATION_DATA_EXTRA,latLng);
        startService(intent);
    }

    //커스텀 마커 추가->입력한 위치 주변 장소 띄우기
    //선택하면 색깔 변하도록->데이터베이스에 저장.....할 예정
    public void onPlacesSuccess(final List<Place> places){
        //placeListener 상속받는 함수 //찾는거 성공시 호출
        System.out.println("onPlacesSuccess 호출");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                    for(noman.googleplaces.Place  place : places){
                        MarkerInfo markinfo=new MarkerInfo(place.getLatitude(),place.getLongitude(), place.getName());
//                  markList.add(new MarkerInfo(place.getLatitude(),place.getLongitude(), place.getName()));
                        //                  addMarker(markinfo, false);

                        LatLng latLng=new LatLng(place.getLatitude(), place.getLongitude());
                        String name=place.getName();
                        MarkerOptions markerPOptions=new MarkerOptions();
                        markerPOptions.position(latLng);
                        markerPOptions.title(place.getName());
//                    markerPOptions.title("찾았다!!!");
                        gMap.addMarker(markerPOptions);

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

    @Override
    public void onConnected(@Nullable Bundle bundle) {


    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    //FetchAddressIntentService의 응답을 처리하기 위해 ResultReceiver를 확장하는 AddressResultReceiver정의
    class AddressResultReceiver extends ResultReceiver{

        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            addressOutput=resultData.getString(MapConstants.RESULT_DATA_KEY);
            System.out.println("MapActivity RESULT_DATA_KEY:: "+MapConstants.RESULT_DATA_KEY);


        }
    }
}