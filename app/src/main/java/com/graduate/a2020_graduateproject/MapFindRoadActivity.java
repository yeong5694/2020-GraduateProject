package com.graduate.a2020_graduateproject;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

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
    private Geocoder geocoder;
    private static final int LOCATION_REQUEST=500;
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

//        findRoad_Btn=findViewById(R.id.map_findroad);
     /*   findRoad_Btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String url=getRequestUrl()
            }
        });*/
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap=googleMap;
   /*     gMap.getUiSettings().setZoomControlsEnabled(true);
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



        gMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                if(listPoints.size()==2){
                    listPoints.clear();
                    gMap.clear();
                }

                listPoints.add(latLng);

                MarkerOptions markerOptions=new MarkerOptions();
                markerOptions.position(latLng);

                if(listPoints.size()==1){
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                }
                else{
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

                }
                gMap.addMarker(markerOptions);

                if(listPoints.size()==2){
                    String url=getRequestUrl(listPoints.get(0), listPoints.get(1));
                    DownloadTask downloadTask=new DownloadTask();
                    downloadTask.execute();
                }
            }
        });




    }

    private String getRequestUrl(LatLng origin, LatLng dest){

        String str_origin="origin="+origin.latitude+","+origin.longitude;
        String str_dest="destination="+dest.latitude+","+dest.longitude;
        String sensor="sensor=false";
        String mode="mode=transit";
        String param=str_origin+"&"+str_dest+"&"+sensor+"&"+mode;
        String output="json";
        System.out.println("param : "+param);
//        String url="http://maps.googleapis.com/maps/api/directions/"+output+"?"+param+"&key="+getString(R.string.google_api_key);
        String url="https://maps.googleapis.com/maps/api/directions/"+output+"?"+param+"&key="+"AIzaSyANE7MTnzzpaQ08SsN9quflkstM-cC1tIw";
        System.out.println("url : "+url);
        return url;
    }

////requestDirections
    private String downloadURL(String downurl) throws IOException {
        String data="";
        InputStream inputStream =null;
        HttpURLConnection urlConnection=null;

        try {
            URL url = new URL(downurl);

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();

            //데이터 받아오기
            inputStream = urlConnection.getInputStream();
            InputStreamReader inputStreamReader=new InputStreamReader(inputStream);

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            StringBuffer stringBuffer = new StringBuffer();

            String line = "";

            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line);
            }

            data=stringBuffer.toString();
            bufferedReader.close();
            inputStreamReader.close();

        } catch (Exception e) {
            //    e.printStackTrace();
        }finally {
            if(inputStream!=null) {
                inputStream.close();

            }
            urlConnection.disconnect();
        }
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
    ////TaskRequestDirections
    public class DownloadTask extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... strings) {
            String data="";
            try {
                data=downloadURL(strings[0]);
            } catch (IOException e) {
                //e.printStackTrace();
            }
            return data;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            ParserTask parserTask=new ParserTask();
            parserTask.execute();
            //parse json
        }
    }

    /** A class to parse the Google Directions in JSON format */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>> >{

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try{
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
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;

            // Traversing through all the routes
            for(int i=0;i<result.size();i++){
                points = new ArrayList<LatLng>();
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

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(8);
                lineOptions.color(Color.RED);
            }

            // Drawing polyline in the Google Map for the i-th route
            if(lineOptions != null) {
                if(polyline != null){
                    polyline.remove();
                }
                polyline = gMap.addPolyline(lineOptions);

            }else
                Toast.makeText(getApplicationContext(),"No route is found", Toast.LENGTH_LONG).show();
        }
    }



}

