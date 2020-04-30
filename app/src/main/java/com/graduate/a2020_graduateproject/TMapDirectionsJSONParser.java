package com.graduate.a2020_graduateproject;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class TMapDirectionsJSONParser {
    //// 위도, 경도 받아와서 polyline 찍기

    public List<List<HashMap<String,String>>> parse(JSONObject jObject){

        List<List<HashMap<String, String>>> features = new ArrayList<>() ;


        ////routes->legs->steps
        try {
            JSONArray jFeatures = jObject.getJSONArray("features"); ///route
            JSONObject jType;
            JSONArray corArray;
            JSONArray list;

            for(int i=0;i<jFeatures.length();i++){
                jType = (JSONObject) (jFeatures.get(i));
                List path = new ArrayList<HashMap<String, String>>();

                String type=((JSONObject)jType.get("geometry")).get("type").toString();

                if(type.equals("LineString")){

                    corArray=(JSONArray)((JSONObject)jType.get("geometry")).get("coordinates");
                    for(int j=0;j<corArray.length();j++){
                 //      String a=corArray.get(j).toString();
                   //    System.out.println(a);
                       list= (JSONArray)corArray.get(j);
//                       System.out.println("list length : "+list.length());
//                       System.out.println(list);

                       HashMap<String, String> hashMap=new HashMap();
                       hashMap.put("lat", list.get(0).toString());
                       hashMap.put("lng", list.get(1).toString());

//                       System.out.println("list [] : "+list.get(0).toString()+" "+list.get(1).toString());

                        path.add(hashMap);

                    }

//                    System.out.println("corArray.length() : "+corArray.length());
//                    System.out.println("coordinate : "+corArray);


                }

                else{
                }
                features.add(path);

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }catch (Exception e){
        }
        System.out.println("JSONPARSER features : "+features);
        return features;
    }
}