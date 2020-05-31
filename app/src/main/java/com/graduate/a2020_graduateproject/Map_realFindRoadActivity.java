package com.graduate.a2020_graduateproject;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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

public class Map_realFindRoadActivity extends AppCompatActivity
        implements OnMapReadyCallback {

    private GoogleMap gMap;
    private TextView text_start;
    private TextView text_dest;


    private ImageView image_find;
    private TMapPoint tMapStart;
    private TMapPoint tMapDest;
    private ArrayList item;
    private Drawable firstXImage;

    private Spinner spinner;
    private ArrayAdapter<String> adapter;

    private DatabaseReference mapDataReference=null;
    private String Mapkey="";

    private String selected_room_id;
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
        spinner=findViewById(R.id.spinner);

        Intent intent = getIntent();
        selected_room_id=intent.getExtras().getString("selected_room_id");
      //  day=intent.getExtras().getString("day");
        item=new ArrayList();
        item.add("신규");
        item.add("마커");

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


//                DataSnapshot mapInfoSnapshot=dataSnapshot.child(Mapkey).child("map_info");

  /*              if (mapInfoSnapshot != null) {
                    System.out.println("firebase에서 받아옴 ");
                    for (DataSnapshot snapshot : mapInfoSnapshot.getChildren()) {

                        double fireLat = Double.parseDouble(snapshot.child("latitude").getValue().toString());
                        double fireLng = Double.parseDouble(snapshot.child("longitude").getValue().toString());
                        String fireName = snapshot.child("name").getValue().toString();

                        System.out.println("firebase에서 불러온 Nmae : "+fireName);

                        MarkerOptions markerOptions = new MarkerOptions();
                        LatLng fireLatlng = new LatLng(fireLat, fireLng);
                        markerOptions.position(fireLatlng);
                        markerOptions.title(fireName);

                        Marker fireMarker = gMap.addMarker(markerOptions);
                        markerList.add(fireMarker);
                    }
                }
*/
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        adapter=new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, item);
        spinner.setAdapter(adapter);




        AutoCompleteTextView autoCompleteStartTextView=findViewById(R.id.text_start); //
        MapPlaceAutoSuggestAdapter startAdapter=new MapPlaceAutoSuggestAdapter(this,1);
        autoCompleteStartTextView.setAdapter(startAdapter);

        AutoCompleteTextView autoCompleteDestTextView=findViewById(R.id.text_dest); //
        MapPlaceAutoSuggestAdapter destAdapter=new MapPlaceAutoSuggestAdapter(this,1);
        autoCompleteDestTextView.setAdapter(destAdapter);


        autoCompleteStartTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MapAddressItem mapAddressItem=startAdapter.getItem(position);
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
                if(TextUtils.isEmpty(text_start.getText())){
                    Toast.makeText(getApplicationContext(), "출발지를 입력해주세요", Toast.LENGTH_SHORT).show();
                }
                else if(TextUtils.isEmpty(text_dest.getText())){
                    Toast.makeText(getApplicationContext(), "도착지를 입력해주세요", Toast.LENGTH_SHORT).show();
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

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap=googleMap;
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
            polylineOptionsT.width(15);
            polylineOptionsT.color(Color.YELLOW);

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

}
