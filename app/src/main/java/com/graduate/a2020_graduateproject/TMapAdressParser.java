package com.graduate.a2020_graduateproject;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TMapAdressParser {
    public ArrayList<String> autoComplete(String input){

        ArrayList<String> arrayList=new ArrayList();
        HttpURLConnection httpURLConnection=null;
        InputStream inputStream = null;
        String data="";

        StringBuilder jsonResult=new StringBuilder();

      //  String tUrl=getUrl(input);
        //DownloadTask downloadTask=new DownloadTask();
        //downloadTask.execute(tUrl);

/*  google map
        StringBuilder stringBuilder=new StringBuilder("https://maps.googleapis.com/maps/api/place/autocomplete/json?");
        stringBuilder.append("input="+input);
        stringBuilder.append("&key=AIzaSyANE7MTnzzpaQ08SsN9quflkstM-cC1tIw");
*/
////// TMap
        StringBuilder stringBuilder=new StringBuilder("https://apis.openapi.sk.com/tmap/pois?version=1");
        stringBuilder.append("&searchKeyword="+input);
        stringBuilder.append("&appKey=l7xx12628330ec6a4ad4ba9b01e1a8e0ea5a");

        ///https://apis.openapi.sk.com/tmap/pois?version=1&searchKeyword=%EA%B5%AC%EC%9D%98%EC%97%AD&appKey=l7xx12628330ec6a4ad4ba9b01e1a8e0ea5a

  /*      try {
            System.out.println("Full url : "+stringBuilder.toString());

            URL url=new URL(stringBuilder.toString());
            httpURLConnection=(HttpURLConnection)url.openConnection();
            InputStreamReader inputStreamReader=new InputStreamReader(httpURLConnection.getInputStream());

            int read;

            char[] buffer=new char[1024];
            while((read=inputStreamReader.read(buffer))!=-1){
                jsonResult.append(buffer, 0,read);
            }

        } catch (MalformedURLException e) {
            System.out.println("MalformedURLException!!!");
        }catch (IOException e){
            System.out.println("IOException!!!");
        }finally {
            if(httpURLConnection!=null){
                httpURLConnection.disconnect();
            }
        }
*/
        try{
            URL downloadUrl = new URL(stringBuilder.toString());

            System.out.println("download Url : "+stringBuilder.toString());

            httpURLConnection = (HttpURLConnection) downloadUrl.openConnection();

            httpURLConnection.connect();

            //데이터 읽어옴
            inputStream = httpURLConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));

            String line = "";
            while( ( line = br.readLine())  != null){
                jsonResult.append(line);
            }

            br.close();
            inputStream.close();

        }catch(Exception e){

        }finally{
            httpURLConnection.disconnect();
        }

//        System.out.println("downloadURL data : "+jsonResult.toString());



        try{
            JSONObject jsonObject=new JSONObject(jsonResult.toString());
            System.out.println("jsonObject : "+jsonObject.toString());

            JSONObject pois=jsonObject.getJSONObject("searchPoiInfo").getJSONObject("pois");
            JSONArray poi=pois.getJSONArray("poi");

            System.out.println("poi.length() : "+poi.length());
            for(int i=0;i<poi.length();i++){

             //   arrayList.add(poi.getJSONObject(i).getString("name"));
                String name=poi.getJSONObject(i).getString("name");
                HashMap<String, String> hashMap=new HashMap<>();
                hashMap.put("name", name);
                System.out.println("TMapPlace api name : "+name);

                arrayList.add(name);
            }

        }catch(JSONException e){
            System.out.println("JsonException!!!");
        }

        return arrayList;
    }

/*
    private String getUrl(String input){

        String inputUrl="&searchKeyword="+input;
        String keyURL="&appKey=l7xx12628330ec6a4ad4ba9b01e1a8e0ea5a";
        String fullUrl="https://apis.openapi.sk.com/tmap/app/pois?version=1"+inputUrl+keyURL;

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

            AddressParserTask parserTask = new AddressParserTask();
            parserTask.execute(result);
        }
    }


    private class AddressParserTask extends AsyncTask<String, Integer, List<HashMap<String,String>>>{

        /// 백그라운드에서 json파일 파싱
        @Override
        protected List<HashMap<String, String>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<HashMap<String, String>> pois = null;

            try{
                System.out.println("ParserTask jsonData[0] : "+jsonData[0]);

                jObject = new JSONObject(jsonData[0]);

                TMapAdressParser parser = new TMapAdressParser();

                pois = parser.autoComplete(jObject);
                System.out.println("ParserTask : "+pois);


            }catch(Exception e){
                e.printStackTrace();
            }
            return pois;
        }

        @Override
        protected void onPostExecute(List<HashMap<String, String>> result) {
            ArrayList<String> address=new ArrayList<>();

            HashMap<String, String> path;

            System.out.println("result.size : "+result.size());

            for(int i=0;i<result.size();i++){
                path=new HashMap<>();

                path = result.get(i);
                String name=path.get("name");

                address.add(name);
            }

        }
    }*/
}
