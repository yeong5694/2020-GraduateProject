package com.graduate.a2020_graduateproject;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

import noman.googleplaces.PlacesListener;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

public class MapPlaningActivity  extends AppCompatActivity
        implements OnMapReadyCallback {

    private GoogleMap gMap;

    private DatabaseReference mapDataReference=null;

    private AddressResultReceiver resultReceiver;
    private String addressOutput="";

    private Polyline polyline;
    private ArrayList<LatLng> planningList;
    private ArrayList<Marker> markerList;
    private Boolean flag=FALSE;
//    private ArrayList<Boolean> isClickList;
    private MqttClient mqttClient;


    private Button button_update;
    private Button button_polly;

    static String TOPIC="";
    private String selected_room_id, day;

    private Map<String, Object> MapInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_planning_layout);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.planningMap);
        mapFragment.getMapAsync(this);

        Intent intent = getIntent();
        selected_room_id=intent.getExtras().getString("selected_room_id");
        day=intent.getExtras().getString("day");
        System.out.println("selected_room_id : "+selected_room_id+ " day : "+day);

        TOPIC="Map/"+selected_room_id+"/"+day;

        mapDataReference = FirebaseDatabase.getInstance().getReference("sharing_trips/map_list").child(selected_room_id).child(day);

        mapDataReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                System.out.println("OnDataChange");
                System.out.println(dataSnapshot);

                if (dataSnapshot.getChildren() != null) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {


                        // snapshot 내에 있는 데이터만큼 반복합니다.

                        String key = snapshot.getKey();
                        System.out.println("snapshot key : "+key);
                        double fireLat = Double.parseDouble(snapshot.child("latitude").getValue().toString());
                        double fireLng = Double.parseDouble(snapshot.child("longitude").getValue().toString());
                        String fireName = snapshot.child("name").toString();


                        MarkerOptions markerOptions = new MarkerOptions();
                        LatLng fireLatlng = new LatLng(fireLat, fireLng);
                        markerOptions.position(fireLatlng);
                        markerOptions.title(fireName);
                        System.out.println("firebase에서 받아옴 ");

                        Marker fireMarker = gMap.addMarker(markerOptions);
                        planningList.add(fireLatlng);
                        markerList.add(fireMarker);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                    System.out.println("실패");
            }

            });



        try {
            System.out.println("onCreate-mqtt Connect");
            connectMqtt();
        } catch (Exception e) {
            e.printStackTrace();
        }

        ///mqtt로 전달하는 마커 저장
        planningList=new ArrayList<>();
        markerList=new ArrayList<>();

        button_update=findViewById(R.id.button_update);
        button_polly=findViewById(R.id.button_polly);

        button_update.setOnClickListener(new Button.OnClickListener(){

            @Override
            public void onClick(View v) {
                ///mqtt->firebase 저장




            }
        });

        button_polly.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<LatLng> dijkstraList = dijkstra(planningList);
                System.out.println("onClick dijstraList.length : " + dijkstraList.size());

               if(!flag){
                    PolylineOptions polylineOptionsDistance;

                    polylineOptionsDistance = new PolylineOptions();
                    polylineOptionsDistance.color(Color.MAGENTA);
                    polylineOptionsDistance.width(8);

                    for (int i = 0; i < dijkstraList.size(); i++) {
                        polylineOptionsDistance.add(dijkstraList.get(i));
                    }
//                polylineOptionsDistance.addAll(dijkstraList);

                    polyline = gMap.addPolyline(polylineOptionsDistance);
                    flag=TRUE;

                }
               else{
                   System.out.println("polyline remove 전 : "+polyline);
                       polyline.remove();
                   System.out.println("polyline remove 후 : "+polyline);
                   flag=FALSE;


               }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap=googleMap;

        //지도 클릭했을 때 마커 찍기
        gMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

                startIntentService(latLng);

                MarkerOptions markerOptions=new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title(addressOutput);
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

//                isMarkerClick=FALSE;
                JSONObject json=new JSONObject();
                try {
                    json.put("lat", Double.toString(latLng.latitude));
                    json.put("lng", Double.toString(latLng.longitude));
                    json.put("name", addressOutput);
                    json.put("isClick", "FALSE");

                    mqttClient.publish(TOPIC, new MqttMessage(json.toString().getBytes()));


                } catch (Exception e) {
                    System.out.println("안보내짐....");
                }

                Marker clickMarker=gMap.addMarker(markerOptions);

                planningList.add(latLng);
                markerList.add(clickMarker);

                MapInfo=new MarkerInfo(latLng.latitude, latLng.longitude, addressOutput).toMap();
                mapDataReference.push().setValue(MapInfo);


                System.out.println("markerList add size : "+markerList.size());
                //   clickinfo=new MarkerInfo(latLng.latitude, latLng.longitude, place).toMap();
                // databaseReference.child("MapInfo").child("click").push().setValue(info);

            }
        });

        gMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
             //   clickList.clear();
                gMap.clear();
                planningList.clear();
                markerList.clear();
//                isClickList.clear();
                polyline.remove();

            }
        });

        gMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

//                isMarkerClick=TRUE;
                JSONObject json=new JSONObject();
                try {
                    json.put("lat", Double.toString(marker.getPosition().latitude));
                    json.put("lng", Double.toString(marker.getPosition().longitude));
                    json.put("name", addressOutput);
                    json.put("isClick", "TRUE");

                    mqttClient.publish(TOPIC, new MqttMessage(json.toString().getBytes()));

                } catch (Exception e) {
                    System.out.println("안보내짐....");
                }

                planningList.remove(marker.getPosition());
                markerList.remove(marker);

                marker.remove();

                mapDataReference.child(day).removeValue();

                System.out.println("markerList remove size : "+markerList.size());
                //isClickList.remove(marker.getPosition());
//                isMarkerClick=FALSE;

                return false;
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

    //FetchAddressIntentService의 응답을 처리하기 위해 ResultReceiver를 확장하는 AddressResultReceiver정의
    class AddressResultReceiver extends ResultReceiver {

        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            addressOutput=resultData.getString(MapConstants.RESULT_DATA_KEY);
            System.out.println("MapActivity RESULT_DATA_KEY:: "+MapConstants.RESULT_DATA_KEY);


        }
    }


        public ArrayList<LatLng> dijkstra(ArrayList<LatLng> list){
            double a[][]=new double[list.size()][list.size()]; //가중치 저장할 배열
            ArrayList<LatLng> LatDistance=new ArrayList<>();
            for(int i=0;i<list.size();i++){ //가중치(거리) 계산해서 저장
                for(int j=0;j<list.size();j++){
                    if(i==j){
                        a[i][j]=0;
                    }
                    else{
                        a[i][j]=calculate(list.get(i), list.get(j));
                        System.out.println( list.get(i).latitude+" "+list.get(i).longitude);
                        System.out.println(i+" + "+j+" calculate 값 : "+a[i][j]);
                    }
                }
            }

            int start=0;
            double[] distance=a[start].clone();
            boolean[] visited=new boolean[a.length]; //방문한 곳 기록

            System.out.println("a.length : "+a.length);

            for(int i=0;i<a.length;i++){
                int minIndex=-1;
                double min=10000000;

                for(int j=0;j<distance.length;j++){
                    if(!visited[j] && min>distance[j]){
                        minIndex=j;
                        min=distance[j];
                    }
                }

                visited[minIndex]=true;
                LatDistance.add(list.get(minIndex));

                System.out.println("minindex = "+minIndex+" list.get(minIndex) = "+list.get(minIndex));

                for(int k=0;k<distance.length;k++){
                    if(!visited[k] && distance[k]>distance[minIndex]+a[minIndex][k]){
                        distance[k]=distance[minIndex]+a[minIndex][k];
                    }
                }
            }
            return LatDistance;

        }



        public double calculate(LatLng origin, LatLng destination){
            //하버사인 공식 이용해서 위도, 경도로 거리 구하기 -> 일반 직선거리 구하는 것이랑 다름
            double calDistance;
            double radius=6371; //지구 반지름
            double toRadian=Math.PI/180.0;

            double deltaLat=Math.abs(origin.latitude-destination.latitude)*toRadian;
            double deltaLog=Math.abs(origin.longitude-destination.longitude)*toRadian;

            double sinDeltaLat=Math.sin(deltaLat/2);
            double sinDeltaLog=Math.sin(deltaLog/2);

            double root=Math.sqrt(Math.pow(sinDeltaLat,2)+ Math.cos(origin.latitude*toRadian)*Math.cos(destination.latitude*toRadian)*Math.pow(sinDeltaLog,2));

            calDistance=2*radius*Math.asin(root);

            System.out.println("calDistance : "+calDistance);

            return calDistance;
        }


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

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        MarkerOptions markerOptions=new MarkerOptions();
                        try {
                            double lat,lng;
                            String name;
                            boolean isClick;

                            lat=Double.parseDouble(json.getString("lat"));
                            lng=Double.parseDouble(json.getString("lng"));
                            name=json.getString("name");
                            isClick=Boolean.parseBoolean(json.getString("isClick"));

                            markerOptions.position(new LatLng(lat, lng));
                            markerOptions.title(name);
                       //     markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

                            Marker resMarker=gMap.addMarker(markerOptions);

                            if(!isClick){ //false-> 마커 찍기
                                planningList.add(markerOptions.getPosition());
                                markerList.add(resMarker);

                              //  MapInfo=new MarkerInfo(lat, lng, name).toMap();
                                //mapDataReference.push().setValue(MapInfo);
                                System.out.println("Mqtt Subscribe 받아서 마커 찍음");
                            }
                            else{
                                int index=-1;
                                for(int i=0;i<markerList.size();i++){
                                    if(markerList.get(i).getPosition().equals(resMarker.getPosition())) {
                                        index = i;
                                        System.out.println("index : "+index);

                                        markerList.get(index).remove();
                                        resMarker.remove();
                                        planningList.remove(markerOptions.getPosition());

                                        System.out.println("마커 지워짐!!!");
                                    }
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
    }

}