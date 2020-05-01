package com.graduate.a2020_graduateproject;

import android.graphics.Color;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.skt.Tmap.TMapTapi;

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

public class MapFindRoadActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap gMap;
    private Polyline polyline;
    ArrayList<LatLng> listPoints;

    private Button findRoad_Btn;
    private Button findRoad_Btn_short;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_findroad_layout);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_findroad);
        mapFragment.getMapAsync(this);


        ///tmap app 지도 사용하지 않고, 연동만
        TMapTapi tmaptapi = new TMapTapi(this);
        tmaptapi.setSKTMapAuthentication ("l7xx12628330ec6a4ad4ba9b01e1a8e0ea5a");



        listPoints=new ArrayList<>();

        findRoad_Btn=findViewById(R.id.findroad_button);
        findRoad_Btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String mode="transit"; //driving

                for(int i=0;i<listPoints.size()-1;i++){
                    String polyUrl=getUrl(listPoints.get(i), listPoints.get(i+1), mode);
                    System.out.println("listPoints.get(i), listPoints.get(i+1) : "+polyUrl);
                    DownloadTask downloadTask=new DownloadTask();
                    downloadTask.execute(polyUrl);
                }
            }
        });

        findRoad_Btn_short=findViewById(R.id.findroad_button_short);
        findRoad_Btn_short.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                gMap.clear();

                ArrayList<LatLng> dijkstraList=dijkstra(listPoints);

                for(int i=0;i<dijkstraList.size();i++){
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(dijkstraList.get(i));
                    markerOptions.title(i+"");
                    markerOptions.snippet(dijkstraList.get(i).latitude+", "+dijkstraList.get(i).longitude);

                    gMap.addMarker(markerOptions);
                }

                String mode="transit"; //driving

                for(int i=0;i<dijkstraList.size()-1;i++){
                    String shortpolyUrl=getUrl(dijkstraList.get(i), dijkstraList.get(i+1), mode);
                    System.out.println("dijkstraList.get(i), dijkstraList.get(i+1) : "+shortpolyUrl);
                    DownloadTask downloadTask=new DownloadTask();
                    downloadTask.execute(shortpolyUrl);
                }



            }
        });

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap=googleMap;
   /*  권한 -> Map Activity로 옮기기
      gMap.getUiSettings().setZoomControlsEnabled(true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Check Permissions Now
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_REQUEST);
            return;
        }
        gMap.setMyLocationEnabled(true);
*/

        LatLng Hansung = new LatLng(37.582465, 127.009393); //Hansung University 위도, 경도
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(Hansung);
        markerOptions.title("Hansung University");
        markerOptions.snippet("Hansung University");
        gMap.addMarker(markerOptions);

        gMap.moveCamera(CameraUpdateFactory.newLatLng(Hansung));
        gMap.animateCamera(CameraUpdateFactory.zoomTo(15));

        gMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

                listPoints.add(latLng);

                MarkerOptions markerOptions=new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

                gMap.addMarker(markerOptions);

            }
        });

        gMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                listPoints.clear();
                gMap.clear();
            }
        });
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


    public String getUrl(LatLng origin, LatLng dest, String mode){

        //출발지
        String originUrl="origin="+origin.latitude+","+origin.longitude;
        //도착지
        String destUrl="destination="+dest.latitude+","+dest.longitude;
        //센서
        String sensor="sensor=false";
        //모드 -> 한국은 transit만 지원
        String modeURL="mode="+mode;

        String urlParameter=originUrl+"&"+destUrl+"&"+sensor+"&"+modeURL;

       //json파일
        String output="json";
        System.out.println("param : "+urlParameter);

        String fullUrl="https://maps.googleapis.com/maps/api/directions/"+output+"?"+urlParameter+"&key="+"AIzaSyANE7MTnzzpaQ08SsN9quflkstM-cC1tIw";
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

/*    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode) {
            case LOCATION_REQUEST:
                if(grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    gMap.setMyLocationEnabled(true);
                }
                break;
        }
    }
*/

    ///구글 api로부터 데이터를 다운로드
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


    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>> >{

        /// 백그라운드에서 json파일 파싱
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try{
                System.out.println("ParserTask jsonData[0] : "+jsonData[0]);

                jObject = new JSONObject(jsonData[0]);

                MapDirectionsJSONParser parser = new MapDirectionsJSONParser();

                routes = parser.parse(jObject);

            }catch(Exception e){
                e.printStackTrace();
            }
            return routes;
        }

        //UI에서 실행-> polyline 찍기
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points;
            PolylineOptions polylineOptions = null;

            for(int i=0;i<result.size();i++){
                points = new ArrayList<>();
                polylineOptions = new PolylineOptions();

                List<HashMap<String, String>> path = result.get(i);

                for(int j=0;j<path.size();j++){
                    HashMap<String,String> point = path.get(j);

                    if(point.containsKey("distance")||point.containsKey(("duration"))){
                        String distance=point.get("distance");
                        String duration=point.get("duration");
                        System.out.println("distance, duration point.get : "+ distance +" " +duration);
                    }
                    else{
                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);
                    points.add(position);
                }
                }
                polylineOptions.addAll(points);
                polylineOptions.width(12);
                polylineOptions.color(Color.YELLOW);
            }

            if(polylineOptions != null) {
                polyline = gMap.addPolyline(polylineOptions);
            }else
                Toast.makeText(getApplicationContext(),"zero route", Toast.LENGTH_LONG).show();
        }
    }

}
