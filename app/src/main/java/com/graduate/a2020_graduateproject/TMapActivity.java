package com.graduate.a2020_graduateproject;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
    TMapPoint tMapPointStart = new TMapPoint(37.570841, 126.985302); // SKT타워(출발지)
    TMapPoint tMapPointEnd = new TMapPoint(37.551135, 126.988205); // N서울타워(목적지)


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
                    System.out.println("tMapPoints 값 : "+tMapPoints);
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

    private void test(){
        System.out.println("test");
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


    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>> >{

        /// 백그라운드에서 json파일 파싱
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

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
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {

            ArrayList<LatLng> points;
            PolylineOptions polylineOptions = null;

            for(int i=0;i<result.size();i++){
                points = new ArrayList<>();
                polylineOptions = new PolylineOptions();

                List<HashMap<String, String>> path = result.get(i);

                for(int j=0;j<path.size();j++){

                    HashMap<String,String> point = path.get(j);
                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));

                    LatLng position = new LatLng(lng, lat);
                    MarkerOptions marker=new MarkerOptions();
                    marker.position(position);

                    points.add(position);

            //        gMap.addMarker(marker);
                }

                polylineOptions.addAll(points);

                polylineOptions.width(15);
                polylineOptions.color(Color.YELLOW);
                gMap.addPolyline(polylineOptions);
             //   System.out.println("draw gMap.addPolyline");

            }
        }
    }

}
