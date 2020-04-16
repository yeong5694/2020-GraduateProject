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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_findroad_layout);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_findroad);
        mapFragment.getMapAsync(this);

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

    private String getUrl(LatLng origin, LatLng dest, String mode){

        //출발지
        String originUrl="origin="+origin.latitude+","+origin.longitude;
        //도착지
        String destUrl="destination="+dest.latitude+","+dest.longitude;
        //센서
        String sensor="sensor=false";
        //모드
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

            // Creating an http connection to communicate with url
            httpURLConnection = (HttpURLConnection) downloadUrl.openConnection();

            // Connecting to url
            httpURLConnection.connect();

            // Reading data from url
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
    /** A class to download data from Google Directions URL */
    private class DownloadTask extends AsyncTask<String, Void, String> {

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try{
                // Fetching the data from web service
                System.out.println("DownloadTask url[0] : "+url[0]);
                data = downloadUrl(url[0]);

            }catch(Exception e){

            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            parserTask.execute(result);
        }
    }


    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>> >{

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try{
                System.out.println("ParserTask jsonData[0] : "+jsonData[0]);

                jObject = new JSONObject(jsonData[0]);

                MapDirectionsJSONParser parser = new MapDirectionsJSONParser();

                // Starts parsing data
                routes = parser.parse(jObject);

            }catch(Exception e){
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points;
            PolylineOptions lineOptions = null;

            // Traversing through all the routes
            for(int i=0;i<result.size();i++){
                points = new ArrayList<>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for(int j=0;j<path.size();j++){
                    HashMap<String,String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                lineOptions.addAll(points);
                lineOptions.width(12);
                lineOptions.color(Color.YELLOW);
            }

            if(lineOptions != null) {
                polyline = gMap.addPolyline(lineOptions);
            }else
                Toast.makeText(getApplicationContext(),"zero route", Toast.LENGTH_LONG).show();
        }
    }

}
