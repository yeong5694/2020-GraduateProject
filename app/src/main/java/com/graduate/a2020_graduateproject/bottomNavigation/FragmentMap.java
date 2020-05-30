package com.graduate.a2020_graduateproject.bottomNavigation;

import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.graduate.a2020_graduateproject.BottomViewActivity;
import com.graduate.a2020_graduateproject.MapConstants;
import com.graduate.a2020_graduateproject.MapFetchAddressIntentService;
import com.graduate.a2020_graduateproject.MapPlaceAutoSuggestAdapter;
import com.graduate.a2020_graduateproject.MapPlaningActivity;
import com.graduate.a2020_graduateproject.MarkerInfo;
import com.graduate.a2020_graduateproject.R;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

public class FragmentMap extends Fragment implements OnMapReadyCallback  {

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

    private  ArrayList<LatLng> currentPlanningList;
    private ArrayList<Marker> currentMarkerList;

    private Button button_update;
    private Button button_polly;
    private Button button_save;

    static String TOPIC="";
    private String selected_room_id, day;

    private Map<String, Object> MapInfo;
    private String Mapkey="";
    private TextView text_auto;

    private Geocoder geocoder;

    private ImageView image_find;


    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //View view = inflater.inflate(R.layout.fragment_map, container, false);

        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.map_planning_layout, container, false);

        /////AutoComplete
        text_auto = viewGroup.findViewById(R.id.text_auto);

        AutoCompleteTextView autoCompleteTextView = viewGroup.findViewById(R.id.text_auto); //
        MapPlaceAutoSuggestAdapter madapter=new MapPlaceAutoSuggestAdapter(getContext(),1);
        autoCompleteTextView.setAdapter(madapter);
        //////

        image_find = viewGroup.findViewById(R.id.image_find);
        image_find.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String place=text_auto.getText().toString();
                List<Address> addressList=null;


                try {
                    addressList=geocoder.getFromLocationName(
                            place, 10 //장소, 최대 검색 결과 개수
                    );
                } catch (IOException e) {
                    e.printStackTrace();
                }

                System.out.println(addressList.get(0).toString());
                String []Places=addressList.get(0).toString().split(",");
                String address = Places[0].substring(Places[0].indexOf("\"") + 1,Places[0].length() - 2);

                //위도, 경도 구하기
                Double xpos=Double.parseDouble(Places[10].substring(Places[10].indexOf("=")+1));
                Double ypos=Double.parseDouble(Places[12].substring(Places[12].indexOf("=")+1));

                LatLng findlatLng=new LatLng(xpos, ypos);
                MarkerOptions markerOptions=new MarkerOptions();
                markerOptions.title(place);
                markerOptions.position(findlatLng);

                Marker marker=gMap.addMarker(markerOptions);
                marker.showInfoWindow();

                gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(findlatLng, 15));

                JSONObject json=new JSONObject();
                try {
                    json.put("lat", Double.toString(findlatLng.latitude));
                    json.put("lng", Double.toString(findlatLng.longitude));
                    json.put("name", place);
                    json.put("isClick", "FALSE");
                    json.put("isAllDelete", "FALSE");


                    mqttClient.publish(TOPIC, new MqttMessage(json.toString().getBytes()));

                    //planningList.add(markerOptions.getPosition());
                    //markerList.add(marker);



                } catch (Exception e) {
                    System.out.println("안보내짐....");
                }

                text_auto.setText("");




            }
        });


        //// 지도 Fragment
        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.planningMap);
        mapFragment.getMapAsync(this);

        selected_room_id = ((BottomViewActivity) getActivity()).getSelected_room_id();
        day = ((BottomViewActivity) getActivity()).getDay();
        System.out.println("Fragment Map :: selected_room_id : "+selected_room_id+ " day : "+day);

        TOPIC="Map/"+selected_room_id+"/"+day;

        try {
            System.out.println("onCreate-mqtt Connect");
            connectMqtt();
        } catch (Exception e) {
            e.printStackTrace();
        }

        /// 파이어베이스에서 가져온 마커 정보들
        currentPlanningList = new ArrayList<>();
        currentMarkerList =new ArrayList<>();

        mapDataReference = FirebaseDatabase.getInstance().getReference("sharing_trips/tripRoom_list").child(selected_room_id)
                .child("schedule_list");
        mapDataReference.orderByChild("day").equalTo(day).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Mapkey = snapshot.getKey();
                    System.out.println("Mapkey : "+ Mapkey);

                }

                DatabaseReference mapRef = mapDataReference.child(Mapkey).child("map_info");
                mapRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        currentPlanningList.clear();
                        currentMarkerList.clear();
                        gMap.clear();

                        System.out.println("------------ current_map_info ----------------------------");

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                            String key = snapshot.getKey();
                            System.out.println("mapRef : key : "+ key);

                            double fireLat = Double.parseDouble(snapshot.child("latitude").getValue().toString());
                            double fireLng = Double.parseDouble(snapshot.child("longitude").getValue().toString());
                            String fireName = snapshot.child("name").toString();


                            MarkerOptions markerOptions = new MarkerOptions();
                            LatLng fireLatlng = new LatLng(fireLat, fireLng);
                            markerOptions.position(fireLatlng);
                            markerOptions.title(fireName);

                            Marker fireMarker = gMap.addMarker(markerOptions);
                            currentPlanningList.add(fireLatlng);
                            currentMarkerList.add(fireMarker);

                        }

                        System.out.println("------------ current_map_info ----------------------------");

                        // mqtt 에서 사용할 마커 정보들
                        planningList = currentPlanningList; // 파이어베이스 정보 복사
                        markerList = currentMarkerList;

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        System.out.println("마커정보를 가져오지 못함");
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                    System.out.println("파이어베이스에서 해당 day 키값 못찾음");
            }
        });






        button_update = viewGroup.findViewById(R.id.button_update);
        button_polly = viewGroup.findViewById(R.id.button_polly);
        //     button_save=findViewById(R.id.button_save);


        button_update.setOnClickListener(new Button.OnClickListener(){

            @Override
            public void onClick(View v) {

                mapDataReference.child(Mapkey).child("map_info").removeValue(); // 파이어베이스에서 map_info 삭제

                gMap.clear();

                DatabaseReference clickRef=mapDataReference.child(Mapkey).child("map_info");

                for(int i=0;i<markerList.size();i++){

                    System.out.println("i  : "+i);
                    System.out.println(" click key : "+Mapkey);
                    MapInfo=new MarkerInfo(markerList.get(i).getPosition().latitude,
                            markerList.get(i).getPosition().longitude,
                            markerList.get(i).getTitle()).toMap();

                    clickRef.push().setValue(MapInfo);

                }

                // 마커정보 비우기
                planningList.clear();
                markerList.clear();

                Toast.makeText(getContext(), "수정되었습니다!", Toast.LENGTH_LONG).show();
            }
        });
        button_polly.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ArrayList<LatLng> dijkstraList = new ArrayList<>();
                ArrayList<LatLng> dijkstraList=dijkstra(planningList);
                /*
                for(int i=0;i<markerList.size();i++){
                    dijkstraList.add(markerList.get(i).getPosition());
                }*/

                System.out.println("onClick dijstraList.length : " + dijkstraList.size());

                PolylineOptions polylineOptionsDistance;

                polylineOptionsDistance = new PolylineOptions();
                polylineOptionsDistance.color(Color.MAGENTA);
                polylineOptionsDistance.width(8);

                if(!flag){
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


        return viewGroup;

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        gMap=googleMap;
        geocoder=new Geocoder(getContext());


        //지도 클릭했을 때 마커 찍기
        gMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

                startIntentService(latLng);

                MarkerOptions markerOptions=new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title(addressOutput);
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

                Marker marker=gMap.addMarker(markerOptions);
                marker.showInfoWindow();

                JSONObject json=new JSONObject();
                try {
                    json.put("lat", Double.toString(latLng.latitude));
                    json.put("lng", Double.toString(latLng.longitude));
                    json.put("name", addressOutput);
                    json.put("isClick", "FALSE");
                    json.put("isAllDelete", "FALSE");


                    mqttClient.publish(TOPIC, new MqttMessage(json.toString().getBytes()));

                    //planningList.add(markerOptions.getPosition());
                    //markerList.add(marker);



                } catch (Exception e) {
                    System.out.println("안보내짐....");
                }


//                System.out.println("planningList add size : "+planningList.size());

                //              System.out.println("markerList add size : "+markerList.size());
            }
        });

        gMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                gMap.clear();

                JSONObject json=new JSONObject();
                try {
                    json.put("lat", Double.toString(0.0));
                    json.put("lng", Double.toString(0.0));
                    json.put("name", addressOutput);
                    json.put("isClick", "TRUE");
                    json.put("isAllDelete", "TRUE");

                    mqttClient.publish(TOPIC, new MqttMessage(json.toString().getBytes()));

                } catch (Exception e) {
                    System.out.println("안보내짐....");
                }

                planningList.clear();
                markerList.clear();

                if(polyline!=null){
                    polyline.remove();
                }

            }
        });

        gMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                marker.remove();

                JSONObject json=new JSONObject();
                try {
                    json.put("lat", Double.toString(marker.getPosition().latitude));
                    json.put("lng", Double.toString(marker.getPosition().longitude));
                    json.put("name", addressOutput);
                    json.put("isClick", "TRUE");
                    json.put("isAllDelete", "FALSE");

                    mqttClient.publish(TOPIC, new MqttMessage(json.toString().getBytes()));

                } catch (Exception e) {
                    System.out.println("안보내짐....");
                }

                planningList.remove(marker.getPosition());
                markerList.remove(marker);


                return true;
            }
        });
    }


    //ResultReceiver->주소조회 결과를 처리하기 위한 인스턴스
    public void startIntentService(LatLng latLng){ //역지오코딩 실행
        resultReceiver=new AddressResultReceiver(new android.os.Handler());
        Intent intent=new Intent(getActivity(), MapFetchAddressIntentService.class);
        intent.putExtra(MapConstants.RECEIVER, resultReceiver);
        intent.putExtra(MapConstants.LOCATION_DATA_EXTRA,latLng);
        getActivity().startService(intent);
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

        // System.out.println("calDistance : "+calDistance);

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

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        MarkerOptions markerOptions=new MarkerOptions();
                        try {
                            double lat,lng;
                            String name;
                            boolean isClick, isAllDelete;

                            lat=Double.parseDouble(json.getString("lat"));
                            lng=Double.parseDouble(json.getString("lng"));
                            name=json.getString("name");
                            System.out.println("mqtt name : "+name);
                            isClick=Boolean.parseBoolean(json.getString("isClick"));
                            isAllDelete=Boolean.parseBoolean(json.getString("isAllDelete"));

                            LatLng resLat=new LatLng(lat, lng);
                            if(isAllDelete){
                                gMap.clear();
                                planningList.clear();
                                markerList.clear();
                            }
                            else {
                                if (!isClick) { //false-> 마커 찍기
                                    markerOptions.position(resLat);
                                    markerOptions.title(name);
                                    Marker resMarker=gMap.addMarker(markerOptions);

                                    resMarker.showInfoWindow();

                                    planningList.add(resLat);
                                    markerList.add(resMarker);
                                    System.out.println("add - planningList size size : " + planningList.size());
                                    System.out.println("add - MarkerList size click : " + markerList.size());

                                    Toast.makeText(getContext(), "마커가 추가되었습니다!", Toast.LENGTH_LONG).show();

                                }

                                else {

                                    System.out.println("markerList size reMarker : "+markerList.size());
                                    for(int i=0;i<markerList.size();i++){
                                        if(resLat.equals(markerList.get(i).getPosition())) {

                                            System.out.println("index : "+i);

                                            Marker removeMarker=markerList.get(i);
                                            System.out.println("resMarker Latlng : "+resLat);
                                            System.out.println("removeMarker Latlng : "+removeMarker.getPosition());

                                            removeMarker.remove();

                                            markerList.remove(removeMarker);

                                            planningList.remove(removeMarker.getPosition());

                                            System.out.println("sub-resMarker Latlng : "+resLat);
                                            System.out.println("sub-removeMarker Latlng : "+removeMarker.getPosition());

                                            Toast.makeText(getContext(), "마커가 삭제되었습니다!", Toast.LENGTH_LONG).show();

                                        }
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
