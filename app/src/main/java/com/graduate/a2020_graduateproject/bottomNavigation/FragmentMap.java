package com.graduate.a2020_graduateproject.bottomNavigation;

import android.content.Intent;
import android.graphics.Color;
import android.location.Geocoder;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
import com.graduate.a2020_graduateproject.MapAddressItem;
import com.graduate.a2020_graduateproject.MapConstants;
import com.graduate.a2020_graduateproject.MapFetchAddressIntentService;
import com.graduate.a2020_graduateproject.MapInfoIndex;
import com.graduate.a2020_graduateproject.MapPlaceAutoSuggestAdapter;
import com.graduate.a2020_graduateproject.R;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

public class FragmentMap extends Fragment implements OnMapReadyCallback  {

    private GoogleMap gMap;

    private DatabaseReference mapDataReference=null;

    private AddressResultReceiver resultReceiver;
    private String addressOutput="";

    private Polyline polyline;
    private ArrayList<Marker> markerList;
    private Boolean flag=FALSE;
    //    private ArrayList<Boolean> isClickList;
    private MqttClient mqttClient;

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
    private ImageView image_delete;


    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //View view = inflater.inflate(R.layout.fragment_map, container, false);

        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.map_planning_layout, container, false);

        /////AutoComplete
        text_auto = viewGroup.findViewById(R.id.text_start);

        markerList=new ArrayList<>();


        AutoCompleteTextView autoCompleteTextView = viewGroup.findViewById(R.id.text_start); //
        MapPlaceAutoSuggestAdapter madapter=new MapPlaceAutoSuggestAdapter(getContext(),1);
        autoCompleteTextView.setAdapter(madapter);

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

        mapDataReference = FirebaseDatabase.getInstance().getReference("sharing_trips/tripRoom_list").child(selected_room_id)
                .child("schedule_list");
            mapDataReference.orderByChild("day").equalTo(day).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Mapkey=snapshot.getKey();
                    System.out.println("snapshot get key : "+Mapkey);

                }
                markerList.clear();

                DataSnapshot mapInfoSnapshot=dataSnapshot.child(Mapkey).child("map_info");

                if (mapInfoSnapshot != null) {
                    System.out.println("firebase에서 받아옴 --------------------------------------");
                    for (DataSnapshot snapshot : mapInfoSnapshot.getChildren()) {

                        double fireLat = Double.parseDouble(snapshot.child("latitude").getValue().toString());
                        double fireLng = Double.parseDouble(snapshot.child("longitude").getValue().toString());
                        String fireName = snapshot.child("name").getValue().toString();

                        //System.out.println("firebase에서 불러온 Nmae : "+fireName);

                        MarkerOptions markerOptions = new MarkerOptions();
                        LatLng fireLatlng = new LatLng(fireLat, fireLng);
                        markerOptions.position(fireLatlng);
                        markerOptions.title(fireName);

                        Marker fireMarker = gMap.addMarker(markerOptions);
                        markerList.add(fireMarker);

                        System.out.println(fireMarker);

                    }
                    System.out.println("-------------------------------------------------------");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        button_update = viewGroup.findViewById(R.id.button_update);
        button_polly = viewGroup.findViewById(R.id.button_polly);
        image_delete=viewGroup.findViewById(R.id.image_delete);

        image_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                text_auto.setText("");
            }
        });


        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MapAddressItem mapAddressItem=madapter.getItem(position);
                double apos=mapAddressItem.getLatitude();
                double bpos=mapAddressItem.getLongitude();

                //   Toast.makeText(getApplicationContext(), "Latlng : "+apos+" "+bpos, Toast.LENGTH_SHORT).show();

                LatLng latLng=new LatLng(apos, bpos);

                MarkerOptions markerOptions=new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title(mapAddressItem.getName());
                Marker adaptMarker=gMap.addMarker(markerOptions);
                adaptMarker.showInfoWindow();

                gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));

                JSONObject json=new JSONObject();
                try {
                    json.put("lat", Double.toString(apos));
                    json.put("lng", Double.toString(bpos));
                    json.put("name", mapAddressItem.getName());
                    json.put("isClick", "FALSE");
                    json.put("isAllDelete", "FALSE");


                    mqttClient.publish(TOPIC, new MqttMessage(json.toString().getBytes()));



                } catch (Exception e) {
                    System.out.println("안보내짐....");
                }



            }
        });


        button_update.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {

               mapDataReference.child(Mapkey).child("map_info").removeValue(); // 파이어베이스에서 map_info 삭제

                DatabaseReference clickRef=mapDataReference.child(Mapkey).child("map_info");



                System.out.println("-- 수정 버튼 클릭 ---------------------------------------------------");


                ArrayList<Marker> routeList=new ArrayList<>();
                if(markerList.size()<2){
                    routeList=markerList;
                }
                else {
                    routeList=dijkstra(markerList);
                }

                for(int i=0;i<markerList.size();i++){
                    System.out.println("i  : "+i);

                    MapInfo=new MapInfoIndex(routeList.get(i).getPosition().latitude,
                            routeList.get(i).getPosition().longitude,
                            routeList.get(i).getTitle(), i+1).toMap();
                    System.out.println(MapInfo);

                    //System.out.println("--- MapInfo Firebase로 ----"+routeList.get(i).getTitle());
                    clickRef.push().setValue(MapInfo);
                }

                System.out.println("-----------------------------------------------------");

                Toast.makeText(getContext(), "수정되었습니다!", Toast.LENGTH_LONG).show();
            }
        });

        button_polly.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ArrayList<LatLng> dijkstraList = new ArrayList<>();
                ArrayList<Marker> dijkstraArrList=dijkstra(markerList);

                System.out.println("onClick dijstraList.length : " + dijkstraArrList.size());

                PolylineOptions polylineOptionsDistance;

                polylineOptionsDistance = new PolylineOptions();
                polylineOptionsDistance.color(Color.MAGENTA);
                polylineOptionsDistance.width(8);

                if(!flag){
                    for (int i = 0; i < dijkstraArrList.size(); i++) {
                        polylineOptionsDistance.add(dijkstraArrList.get(i).getPosition());
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


                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        System.out.println("click한 Latlng : "+ latLng.latitude+" "+latLng.longitude);
                        System.out.println("click한 주소 : "+addressOutput);
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


                            System.out.println("mqtt 보내기 전 name : "+addressOutput);
                            mqttClient.publish(TOPIC, new MqttMessage(json.toString().getBytes()));

                            //planningList.add(markerOptions.getPosition());
                            //markerList.add(marker);



                        } catch (Exception e) {
                            System.out.println("안보내짐....");
                        }

                    }
                }, 500);



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
                    json.put("name", "");
                    json.put("isClick", "TRUE");
                    json.put("isAllDelete", "TRUE");

                    mqttClient.publish(TOPIC, new MqttMessage(json.toString().getBytes()));

                } catch (Exception e) {
                    System.out.println("안보내짐....");
                }

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

    public ArrayList<Marker> dijkstra(ArrayList<Marker> list){
        double a[][]=new double[list.size()][list.size()]; //가중치 저장할 배열

        ArrayList<Marker> LatDistance=new ArrayList<>();

        for(int i=0;i<list.size();i++){ //가중치(거리) 계산해서 저장
            for(int j=0;j<list.size();j++){
                if(i==j){
                    a[i][j]=0;
                }
                else{
                    a[i][j]=calculate(list.get(i).getPosition(), list.get(j).getPosition());
                    System.out.println( list.get(i).getPosition().latitude+" "+list.get(i).getPosition().longitude);
                    System.out.println(i+" + "+j+" calculate 값 : "+a[i][j]);
                }
            }
        }

        for(int v=0;v<a.length;v++){
            for(int u=0;u<a.length;u++){
                System.out.print(a[v][u]+" ");
            }
            System.out.println("");
        }

        int start=0;
        double[] distance=a[0].clone();

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

            System.out.println("minindex = "+minIndex+" list.get(minIndex) = "+list.get(minIndex).getTitle());

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
        double calDistance=0.0;
        double radius=6371; //지구 반지름
    /*    double toRadian=Math.PI/180.0;

        double deltaLat=Math.toRadians(Math.abs(origin.latitude-destination.latitude));
        double deltaLog=Math.toRadians(Math.abs(origin.longitude-destination.longitude));

        double sinDeltaLat=Math.sin(deltaLat/2);
        double sinDeltaLog=Math.sin(deltaLog/2);

        double root=Math.sqrt(sinDeltaLat*sinDeltaLat+ Math.cos(Math.toRadians(origin.latitude))*Math.cos(Math.toRadians(destination.latitude))*sinDeltaLog*sinDeltaLog);

        calDistance=2*radius*Math.asin(root);

        System.out.println("calDistance : "+calDistance);
*/
        double dLat = Math.toRadians(destination.latitude - origin.latitude);
        double dLon = Math.toRadians(destination.longitude - origin.longitude);

        double lat1 = Math.toRadians(origin.latitude);
        double lat2 = Math.toRadians(destination.latitude);

        double a = Math.pow(Math.sin(dLat / 2),2) + Math.pow(Math.sin(dLon / 2),2) * Math.cos(lat1) * Math.cos(lat2);
        calDistance = 2 * Math.asin(Math.sqrt(a));
        return radius * calDistance;


    }

    private void connectMqtt() throws  Exception{

        mqttClient = ((BottomViewActivity) getActivity()).getMqttClient();

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

                            System.out.println("mqtt isClick : "+isClick);
                            System.out.println("mqtt isAllDelete : "+isAllDelete);

                            LatLng resLat=new LatLng(lat, lng);
                            if(isAllDelete){
                                gMap.clear();
                                markerList.clear();
                            }
                            else {
                                if (!isClick) { //false-> 마커 찍기
                                    markerOptions.position(resLat);
                                    markerOptions.title(name);
                                    Marker resMarker=gMap.addMarker(markerOptions);

                                    resMarker.showInfoWindow();

                                    markerList.add(resMarker);
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
