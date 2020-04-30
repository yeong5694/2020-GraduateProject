package com.graduate.a2020_graduateproject;

import android.content.Intent;
import android.graphics.Color;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.skt.Tmap.TMapTapi;

import java.util.ArrayList;

public class MapDistanceActivity  extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap gMap;
    private Geocoder geocoder;

    private PolylineOptions polylineOptions;
    private PolylineOptions polylineOptionsClick;
    private PolylineOptions polylineOptionsDistance;

    private ArrayList<LatLng> markerList; //위도, 경도 정보 넣어두는 ArrayList
    private ArrayList<LatLng> clickList; // 클릭한 장소 넣어두는 ArrayList

    private Button button_short;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_distance_layout);

        Intent intent=getIntent();
        markerList=intent.getParcelableArrayListExtra("markerList");
        clickList=intent.getParcelableArrayListExtra("clickList");

        intent.getStringExtra("");
        ////지도
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_findroad);
        mapFragment.getMapAsync(this);




        button_short=findViewById(R.id.button_short);
        button_short.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               ArrayList<LatLng> dijkstraList=dijkstra(clickList);
                System.out.println("onClick dijstraList.length : "+dijkstraList.size());
               for(int i=0;i<dijkstraList.size();i++){
                   System.out.println("clickList : "+ clickList.get(i));
                   System.out.println("dijstraList : "+dijkstraList.get(i));

                   MarkerOptions markerOptions = new MarkerOptions();
                   markerOptions.position(dijkstraList.get(i));
                   markerOptions.title(i+"");
                   markerOptions.snippet(dijkstraList.get(i).latitude+", "+dijkstraList.get(i).longitude);

                   gMap.addMarker(markerOptions);

               }

                polylineOptionsDistance=new PolylineOptions();
                polylineOptionsDistance.color(Color.MAGENTA);
                polylineOptionsDistance.width(8);

                for(int i=0;i<dijkstraList.size();i++){
                      polylineOptionsDistance.add(dijkstraList.get(i));
                }
//                polylineOptionsDistance.addAll(dijkstraList);

                gMap.addPolyline( polylineOptionsDistance);

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

/*    public static double deg2rad(double lat){ //degree->radius
        return (lat*Math.PI/180.0);
    }
    public static double rad2deg(double radius){ //radius->degree
        return (radius*180/Math.PI);
    }

 */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        ////일단 좌표 찍어서 직선거리 보여줌 -> DB에 들어있는 정보 이용해서 직선거리 구하기

        gMap=googleMap;
        geocoder=new Geocoder(this);

        LatLng Hansung = new LatLng(37.582465, 127.009393); //Hansung University 위도, 경도

        gMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                gMap.clear();
            }
        });


        ////찾기 버튼을 통해 찾은 장소들
        polylineOptions=new PolylineOptions();
        polylineOptions.color(Color.RED);
        polylineOptions.width(8);

        polylineOptions.addAll(markerList);

        gMap.addPolyline(polylineOptions);


        ////클릭한 장소들
        polylineOptionsClick=new PolylineOptions();
        polylineOptionsClick.color(Color.GREEN);
        polylineOptionsClick.width(8);

        polylineOptionsClick.addAll(clickList);

        gMap.addPolyline(polylineOptionsClick);


        //지도 위치를 Hansung으로 맞춤
        gMap.moveCamera(CameraUpdateFactory.newLatLng(Hansung));
        gMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        //1로 지정하면 세계지도 -> 숫자가 클수록 상세하게 나타남
    }
}
