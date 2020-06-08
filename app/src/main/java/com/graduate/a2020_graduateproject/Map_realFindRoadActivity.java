package com.graduate.a2020_graduateproject;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.graduate.a2020_graduateproject.memo.memoActivity;
import com.kakao.kakaolink.v2.KakaoLinkResponse;
import com.kakao.kakaolink.v2.KakaoLinkService;
import com.kakao.message.template.ButtonObject;
import com.kakao.message.template.ContentObject;
import com.kakao.message.template.FeedTemplate;
import com.kakao.message.template.LinkObject;
import com.kakao.network.ErrorResult;
import com.kakao.network.callback.ResponseCallback;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import com.kakao.util.helper.log.Logger;
import com.skt.Tmap.TMapPoint;

import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Map_realFindRoadActivity extends AppCompatActivity
        implements OnMapReadyCallback{

    private GoogleMap gMap;
    private TextView text_start;
    private TextView text_dest;


    // 선택한 여행방 정보
    private String selected_room_name; // 여행방 이름
    private String selected_room_id; // 여행방 id



    private Toolbar toolbar;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;


    private ImageView image_find;
    private TMapPoint tMapStart;
    private TMapPoint tMapDest;
    private ArrayList item;

    private Spinner spinner;

    private Spinner spinner_start;
    private Spinner spinner_dest;


    private ArrayList<MapInfoIndex> startInfoList;
    private ArrayList<MapInfoIndex> destInfoList;


    private ArrayList<LatLng> clickList;
    ArrayList<TMapPoint> tMapPoints;

    private DatabaseReference mapDataReference=null;
    private String Mapkey="";
    private String DayKey="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_realfindroad_layout);


        //// 지도 Fragment
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.findMap);
        mapFragment.getMapAsync(this);

        text_start=findViewById(R.id.text_start);
        text_dest=findViewById(R.id.text_dest);

        spinner_start=findViewById(R.id.spinner_start);
        spinner_dest=findViewById(R.id.spinner_dest);

        spinner=findViewById(R.id.spinner);

        startInfoList=new ArrayList<>();
        destInfoList=new ArrayList<>();

        clickList=new ArrayList<>();
        tMapPoints=new ArrayList<>();

        Intent intent = getIntent();
        selected_room_id=intent.getExtras().getString("selected_room_id");

        item=new ArrayList();
        item.add("선택");

        toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        setTitle("길찾기");

        mapDataReference = FirebaseDatabase.getInstance().getReference("sharing_trips/tripRoom_list").child(selected_room_id)
                .child("schedule_list");
        mapDataReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int count=0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Mapkey=snapshot.getKey();
                    count+=1;
                    System.out.println("snapshot get key : "+Mapkey);
                }
               for(int i=0;i<count;i++){
                   item.add("DAY "+(i+1));
               }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        final MapBasicAdapter adapter=new MapBasicAdapter(this, android.R.layout.simple_list_item_1, item);
        spinner.setAdapter(adapter);

        startInfoList.add(new MapInfoIndex(0,0,"출발지",0));
        destInfoList.add(new MapInfoIndex(0,0,"도착지", 0));


        final MapStartAdapter startEditAdapter=new MapStartAdapter(this,1, startInfoList);
        spinner_start.setAdapter(startEditAdapter);

        final MapStartAdapter destEditAdapter=new MapStartAdapter(this,1, destInfoList);
        spinner_dest.setAdapter(destEditAdapter);


        spinner_start.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (spinner_start.getSelectedItem().toString().equals("출발지")) {
                    Toast.makeText(getApplicationContext(), "출발지를 선택해주세요!!", Toast.LENGTH_SHORT).show();
                } else {
                    MapInfoIndex startMapInfo = startEditAdapter.getItem(position);
                    double slat = startMapInfo.latitude;
                    double slng = startMapInfo.longitude;

                    LatLng sLatlng = new LatLng(slat, slng);
                    MarkerOptions smarkerOption = new MarkerOptions();
                    smarkerOption.position(sLatlng);
                    smarkerOption.title(startMapInfo.getName());
                    Marker startMarker = gMap.addMarker(smarkerOption);
                    startMarker.showInfoWindow();

                    tMapStart = new TMapPoint(slat, slng);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        spinner_dest.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (spinner_dest.getSelectedItem().toString().equals("도착지")) {
                    Toast.makeText(getApplicationContext(), "도착지를 입력해주세요!!", Toast.LENGTH_SHORT).show();
                } else {
                    MapInfoIndex destMapInfo = startEditAdapter.getItem(position);
                    double dlat = destMapInfo.latitude;
                    double dlng = destMapInfo.longitude;

                    LatLng dLatlng = new LatLng(dlat, dlng);
                    MarkerOptions dmarkerOption = new MarkerOptions();
                    dmarkerOption.position(dLatlng);
                    dmarkerOption.title(destMapInfo.getName());

                    Marker destMarker = gMap.addMarker(dmarkerOption);
                    destMarker.showInfoWindow();

                    tMapDest = new TMapPoint(dlat, dlng);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        AutoCompleteTextView autoCompleteStartTextView=findViewById(R.id.text_start); //
        final MapPlaceAutoSuggestAdapter[] startAdapter = {new MapPlaceAutoSuggestAdapter(this, 1)};
        autoCompleteStartTextView.setAdapter(startAdapter[0]);

        AutoCompleteTextView autoCompleteDestTextView=findViewById(R.id.text_dest); //
        MapPlaceAutoSuggestAdapter destAdapter=new MapPlaceAutoSuggestAdapter(this,1);
        autoCompleteDestTextView.setAdapter(destAdapter);

        spinner_start.setVisibility(View.GONE);
        spinner_dest.setVisibility(View.GONE);

        autoCompleteStartTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MapAddressItem mapAddressItem= startAdapter[0].getItem(position);
                double apos=mapAddressItem.getLatitude();
                double bpos=mapAddressItem.getLongitude();

                MarkerOptions markerOptions=new MarkerOptions();
                markerOptions.position(new LatLng(apos, bpos));
                markerOptions.title(mapAddressItem.getName());
                Marker marker=gMap.addMarker(markerOptions);
                marker.showInfoWindow();

                tMapStart=new TMapPoint(apos, bpos);
                System.out.println("tMapStart : "+tMapStart.getLatitude() +" "+ tMapStart.getLongitude());
            }
        });

        autoCompleteDestTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MapAddressItem mapAddressItem=destAdapter.getItem(position);
                double apos=mapAddressItem.getLatitude();
                double bpos=mapAddressItem.getLongitude();

                MarkerOptions markerOptions=new MarkerOptions();
                markerOptions.position(new LatLng(apos, bpos));
                markerOptions.title(mapAddressItem.getName());
                Marker marker=gMap.addMarker(markerOptions);
                marker.showInfoWindow();

                tMapDest=new TMapPoint(apos, bpos);
                System.out.println("tMapDest : "+tMapDest.getLatitude() +" "+ tMapDest.getLongitude());

            }
        });


        image_find=findViewById(R.id.image_findroad);
        image_find.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                MapInfoIndex empty_start=(MapInfoIndex) spinner_start.getSelectedItem();
                MapInfoIndex empty_dest=(MapInfoIndex) spinner_dest.getSelectedItem();


                if(TextUtils.isEmpty(text_start.getText()) && spinner_start.getVisibility()==View.GONE){
                    Toast.makeText(getApplicationContext(), "출발지를 입력해주세요1", Toast.LENGTH_SHORT).show();
                }
                else if(text_start.getVisibility()==View.GONE&& empty_start.name.equals("출발지") ){
                    Toast.makeText(getApplicationContext(), "출발지를 입력해주세요2", Toast.LENGTH_SHORT).show();
                }

                else if(TextUtils.isEmpty(text_dest.getText()) && spinner_dest.getVisibility()==View.GONE){
                    Toast.makeText(getApplicationContext(), "도착지를 입력해주세요1", Toast.LENGTH_SHORT).show();
                }
                else if(text_dest.getVisibility()==View.GONE && empty_dest.name.equals("도착지") ){
                    Toast.makeText(getApplicationContext(), "도착지를 입력해주세요2", Toast.LENGTH_SHORT).show();
                }

                else {
                    gMap.clear();
                    LatLng startLng = new LatLng(tMapStart.getLatitude(), tMapStart.getLongitude());
                    MarkerOptions startmarkerOptions=new MarkerOptions();
                    startmarkerOptions.position(startLng);
                    gMap.addMarker(startmarkerOptions);

                    LatLng destLng = new LatLng(tMapDest.getLatitude(), tMapDest.getLongitude());
                    MarkerOptions destmarkerOptions=new MarkerOptions();
                    destmarkerOptions.position(destLng);
                    gMap.addMarker(destmarkerOptions);

                    gMap.moveCamera(CameraUpdateFactory.newLatLng(startLng));

                    String polyUrl = getUrl(tMapStart, tMapDest);

                    DownloadTask downloadTask = new DownloadTask();
                    downloadTask.execute(polyUrl);
                }
            }
        });

/*
        button_find.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(button_find.getText().equals("길찾기")) {
                    if (clickList.size() < 1) {
                        Toast.makeText(getApplicationContext(), "마커를 클릭해주세요", Toast.LENGTH_LONG).show();
                    }
                    else if (clickList.size() == 1) {
                        Toast.makeText(getApplicationContext(), "도착할 곳을 클릭해주세요", Toast.LENGTH_LONG).show();
                    }
                    else {
                        ArrayList<LatLng> dijkstraList = dijkstra(clickList);


                        for (int i = 0; i < dijkstraList.size(); i++) {
                            tMapPoints.add(new TMapPoint(dijkstraList.get(i).latitude, dijkstraList.get(i).longitude));
                            System.out.println("tMapPoints " + i + "번째 값 : " + tMapPoints);
                        }

                        for (int i = 0; i < dijkstraList.size() - 1; i++) {
                            String shortpolyUrl = getUrl(tMapPoints.get(i), tMapPoints.get(i + 1));
                            System.out.println("dijkstraList.get(i), dijkstraList.get(i+1) : " + shortpolyUrl);
                            DownloadTask downloadTask = new DownloadTask();
                            downloadTask.execute(shortpolyUrl);

                        }
                        button_find.setText("지우기");
                    }
                 }
                else{
                    gMap.clear();
                    clickList.clear();
                    tMapPoints.clear();
                    button_find.setText("길찾기");
                }

            }
        });

*/
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(spinner.getItemAtPosition(position).equals("선택")){

                    autoCompleteDestTextView.setVisibility(View.VISIBLE);
                    autoCompleteStartTextView.setVisibility(View.VISIBLE);

                    spinner_start.setVisibility(View.GONE);
                    spinner_dest.setVisibility(View.GONE);

                    image_find.setVisibility(View.VISIBLE);

                }
               else{

                    autoCompleteStartTextView.setText("");
                   autoCompleteDestTextView.setText("");

                    autoCompleteDestTextView.setVisibility(View.GONE);
                    autoCompleteStartTextView.setVisibility(View.GONE);

                    spinner_start.setVisibility(View.VISIBLE);
                    spinner_dest.setVisibility(View.VISIBLE);

                    String findDay=spinner.getItemAtPosition(position).toString();
                    System.out.println("findDay : "+findDay);

                    String day=findDay.split(" ")[1].toString();
                    System.out.println("findDay : "+findDay+" day : "+day);

                    mapDataReference = FirebaseDatabase.getInstance().getReference("sharing_trips/tripRoom_list").child(selected_room_id)
                            .child("schedule_list");
                    mapDataReference.orderByChild("day").equalTo(day).addValueEventListener(new ValueEventListener() {

                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            startInfoList.clear();
                            destInfoList.clear();

                            startInfoList.add(new MapInfoIndex(0,0,"출발지",0));
                            destInfoList.add(new MapInfoIndex(0,0,"도착지", 0));

                            int cnt=0;
                            System.out.println("dataSnapshot.getCHildCount() : "+dataSnapshot.getChildrenCount());

                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                cnt+=1;
                               DayKey = snapshot.getKey();
                               System.out.println("snapshot daykey : " + DayKey);
                            }

                            System.out.println("cnt : "+cnt+"DayKey : "+DayKey);
                            DataSnapshot daySnapshot=dataSnapshot.child(DayKey).child("map_info");

                              if (daySnapshot != null) {
                                  System.out.println("firebase에서 day 받아옴 ");

                                  for (DataSnapshot snapshot : daySnapshot.getChildren()) {
                                      System.out.println("snapshot 받아오기");

                                      double dayLat = Double.parseDouble(snapshot.child("latitude").getValue().toString());
                                      double dayLng = Double.parseDouble(snapshot.child("longitude").getValue().toString());
                                      String dayName = snapshot.child("name").getValue().toString();
                                      int dayIndex = Integer.parseInt(snapshot.child("index").getValue().toString());

                                      System.out.println("daySnapShot : " + dayLat + dayLng + dayName + dayIndex);
                                      MapInfoIndex mapInfo=new MapInfoIndex(dayLat, dayLng, dayName, dayIndex);
                                      startInfoList.add(mapInfo);
                                      destInfoList.add(mapInfo);

                                  }
                                  startEditAdapter.notifyDataSetChanged();
                                  destEditAdapter.notifyDataSetChanged();
                              }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }




    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap=googleMap;

        gMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

                if(spinner.getSelectedItem().equals("마커")) {

                            clickList.add(latLng);
                            MarkerOptions markerOptions = new MarkerOptions();
                            markerOptions.position(latLng);
                            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                            Marker marker = gMap.addMarker(markerOptions);
                            marker.showInfoWindow();
                        }
                  }
            });

    }

    private String getUrl(TMapPoint origin, TMapPoint dest){

        //출발지
        String startX="&startX="+origin.getLongitude();
        String startY="&startY="+origin.getLatitude();

        //도착지
        String endX="&endX="+dest.getLongitude();
        String endY="&endY="+dest.getLatitude();
////////TMap의 경우 Google Map과 달리 lat, long 반대임

        String startName="&startName=출발지";
        String endName="&endName=도착지";

        String appKey="&appKey=l7xx12628330ec6a4ad4ba9b01e1a8e0ea5a";

        String fullUrl="https://apis.openapi.sk.com/tmap/routes/pedestrian?version=1&"+appKey+startX+startY+endX+endY+startName+endName;

        System.out.println("Fullurl : "+fullUrl);

        return fullUrl;
    }


    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream inputStream = null;
        HttpURLConnection httpURLConnection = null;

        try{
            URL downloadUrl = new URL(strUrl);

            httpURLConnection = (HttpURLConnection) downloadUrl.openConnection();

            httpURLConnection.connect();

            //데이터 읽어옴
            inputStream = httpURLConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));

            StringBuffer sb  = new StringBuffer();

            String line = "";
            while( ( line = br.readLine())  != null){
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        }catch(Exception e){

        }finally{
            inputStream.close();
            httpURLConnection.disconnect();
        }

        System.out.println("downloadURL data : "+data);
        return data;
    }



    private class DownloadTask extends AsyncTask<String, Void, String> {

        //백그라운드에서 데이터 다운로드 수행
        @Override
        protected String doInBackground(String... url) {

            String data = "";

            try{
                System.out.println("DownloadTask url[0] : "+url[0]);
                data = downloadUrl(url[0]);

            }catch(Exception e){

            }
            return data;
        }

        //dolnBackground() 수행 후 UI에서 실행
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            parserTask.execute(result);
        }
    }


    private class ParserTask extends AsyncTask<String, Integer, List<HashMap<String,String>>>{

        /// 백그라운드에서 json파일 파싱
        @Override
        protected List<HashMap<String, String>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<HashMap<String, String>> routes = null;

            try{
                System.out.println("ParserTask jsonData[0] : "+jsonData[0]);

                jObject = new JSONObject(jsonData[0]);

                TMapDirectionsJSONParser parser = new TMapDirectionsJSONParser();

                routes = parser.parse(jObject);
                System.out.println("ParserTask : "+routes);


            }catch(Exception e){
                e.printStackTrace();
            }
            return routes;
        }

        //UI에서 실행-> polyline 찍기
        @Override
        protected void onPostExecute(List<HashMap<String, String>> result) {
            ArrayList<LatLng> points=new ArrayList<>();
            PolylineOptions polylineOptionsT = new PolylineOptions();
            HashMap<String, String> path;

            System.out.println("result.size : "+result.size());

            for(int i=0;i<result.size();i++){
                path=new HashMap<>();

                path = result.get(i);
                double lat = Double.parseDouble(path.get("lat"));
                double lng = Double.parseDouble(path.get("lng"));

                LatLng position = new LatLng(lng, lat);

                MarkerOptions marker=new MarkerOptions();
                marker.position(position);
                //  gMap.addMarker(marker);

                points.add(position);

            }

            polylineOptionsT.addAll(points);
            polylineOptionsT.width(12);
            polylineOptionsT.color(Color.MAGENTA);

//            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)); //마커 색깔 파란색으로
            //   HUE_MAGENTA, HUE_VioLet, HUE_ORANGE, HUE_RED, HUE_BLUE, HUE_GREEN, HUE_AZURE, HUE_ROSE, HUE_CYAN, HUE_YELLOW


            if(polylineOptionsT != null) {
                System.out.println("poly null아님");
                gMap.addPolyline(polylineOptionsT);
                System.out.println(polylineOptionsT);

            }else {
                Toast.makeText(getApplicationContext(), "zero route", Toast.LENGTH_LONG).show();
                System.out.println("들은게 없엄....");
            }
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

}
