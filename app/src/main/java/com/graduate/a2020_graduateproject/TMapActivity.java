package com.graduate.a2020_graduateproject;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.webkit.HttpAuthHandler;
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
import com.google.android.gms.maps.model.PolylineOptions;
import com.skt.Tmap.TMapPoint;
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

public class TMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap gMap;
    ArrayList<LatLng> listPoints;
    ArrayList<TMapPoint> tMapPoints;

    private Button findRoad_Btn;
    private Button findRoad_Btn_short;
 //   TMapPoint tMapPointStart = new TMapPoint(37.570841, 126.985302); // SKT타워(출발지)
 //   TMapPoint tMapPointEnd = new TMapPoint(37.551135, 126.988205); // N서울타워(목적지)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tmap_layout);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_findroad);
        mapFragment.getMapAsync(this);

        listPoints=new ArrayList<>();
        tMapPoints=new ArrayList<>();

        findRoad_Btn=findViewById(R.id.findroad_button);
        findRoad_Btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

//                String polyUrl=getUrl(tMapPointStart,tMapPointEnd, mode);
                tMapPoints.clear();

                for(int i=0;i<listPoints.size();i++){
                    tMapPoints.add(new TMapPoint(listPoints.get(i).latitude, listPoints.get(i).longitude));
                    System.out.println("tMapPoints "+i+"번째 값 : "+tMapPoints);
                }

                for(int i=0;i<tMapPoints.size()-1;i++){
                    String polyUrl=getUrl(tMapPoints.get(i), tMapPoints.get(i+1));

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
                tMapPoints.clear();

                ArrayList<LatLng> dijkstraList=dijkstra(listPoints);

                for(int i=0;i<dijkstraList.size();i++){
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(dijkstraList.get(i));
                    markerOptions.title(i+"");
                    markerOptions.snippet(dijkstraList.get(i).latitude+", "+dijkstraList.get(i).longitude);

                    gMap.addMarker(markerOptions);
                }

                for(int i=0;i<dijkstraList.size();i++){
                    tMapPoints.add(new TMapPoint(dijkstraList.get(i).latitude, dijkstraList.get(i).longitude));
                    System.out.println("tMapPoints "+i+"번째 값 : "+tMapPoints);
                }

                for(int i=0;i<dijkstraList.size()-1;i++){
                    String shortpolyUrl=getUrl(tMapPoints.get(i), tMapPoints.get(i+1));
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
                tMapPoints.clear();
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

        //String fullUrl="https://apis.openapi.sk.com/tmap/routes/pedestrian?version=1&appKey=l7xx12628330ec6a4ad4ba9b01e1a8e0ea5a&startX=126.977022&startY=37.569758&endX=126.997589&endY=37.5705947&startName=출발지&endName=도착지";

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


    private class ParserTask extends AsyncTask<String, Integer, List<HashMap<String,String>> >{

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
