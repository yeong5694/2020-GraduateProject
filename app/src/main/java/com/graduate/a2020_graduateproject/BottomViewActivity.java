package com.graduate.a2020_graduateproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.WindowManager;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.graduate.a2020_graduateproject.bottomNavigation.FragmentChat;
import com.graduate.a2020_graduateproject.bottomNavigation.FragmentDay;
import com.graduate.a2020_graduateproject.bottomNavigation.FragmentMap;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;


public class BottomViewActivity extends AppCompatActivity {

    private FragmentManager fragmentManager = getSupportFragmentManager();
    private FragmentDay fragmentDay = new FragmentDay();
    private FragmentMap fragmentMap = new FragmentMap();
    private FragmentChat fragmentChat = new FragmentChat();

    // 로그인 정보
    private Long kakao_id;
    private String kakao_email;
    private String kakao_thumnail;
    private String kakao_name;
    private String selected_room_id;
    private String day;

    private MqttClient mqttClient;
    private static String TOPIC;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_view);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE|WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);


        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frameLayout, fragmentDay).commitAllowingStateLoss();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavi);
        bottomNavigationView.setOnNavigationItemSelectedListener(new ItemSelectedListener());

        Intent intent = getIntent();
        kakao_id = intent.getExtras().getLong("kakao_id");
        kakao_email = intent.getExtras().getString("kakao_email");
        kakao_name = intent.getExtras().getString("kakao_name");
        kakao_thumnail = intent.getExtras().getString("kakao_thumnail");
        selected_room_id=intent.getExtras().getString("selected_room_id");
        day=intent.getExtras().getString("day");
        System.out.println("selected_room_id : "+selected_room_id+ " day : "+day);



        try {
            mqttClient=new MqttClient("tcp://3.224.178.67:1883", MqttClient.generateClientId(), null);
            System.out.println("ConnectMqtt() 시작");  //탄력적 ip 3.224.178.67
        } catch (MqttException e) {
            e.printStackTrace();
        }


        try {
            System.out.println("ConnectMqtt() 연결 준비" +MqttClient.generateClientId());
            mqttClient.connect();
            System.out.println("ConnectMqtt() 연결" +MqttClient.generateClientId());
        } catch (MqttException e) {
            e.printStackTrace();
        }



        TOPIC="Map/"+selected_room_id+"/"+day;

        try {
            mqttClient.subscribe(TOPIC);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }



    class ItemSelectedListener implements BottomNavigationView.OnNavigationItemSelectedListener{
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();

            switch(menuItem.getItemId())
            {
                case R.id.dayItem:
                    transaction.replace(R.id.frameLayout, fragmentDay).commitAllowingStateLoss();

                    break;
                case R.id.mapItem:
                    transaction.replace(R.id.frameLayout, fragmentMap).commitAllowingStateLoss();
                    break;
                case R.id.chatItem:
                    transaction.replace(R.id.frameLayout, fragmentChat).commitAllowingStateLoss();
                    break;
                default :
                    return true;
            }
            return true;
        }
    }

    public  String getSelected_room_id(){
        return selected_room_id;
    }
    public String getKakao_email(){ return kakao_email; }
    public String getKakao_thumnail(){ return kakao_thumnail;}
    public String getKakao_name(){ return kakao_name;}
    public Long getKakao_id(){ return kakao_id;
    }

    public String getDay(){
        return day;
    }

    public MqttClient getMqttClient(){
        return mqttClient;
    }
}
