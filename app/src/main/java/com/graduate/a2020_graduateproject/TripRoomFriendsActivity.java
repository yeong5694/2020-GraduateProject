package com.graduate.a2020_graduateproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class TripRoomFriendsActivity extends AppCompatActivity {

    // 로그인 정보
    private Long kakao_id;
    private String kakao_email;
    private String kakao_thumnail;
    private String kakao_name;
    // 선택한 여행방 정보
    private String selected_room_name; // 여행방 이름
    private String selected_room_id; // 여행방 id

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_room_friends);

        Intent intent = getIntent();
        kakao_id = intent.getExtras().getLong("kakao_id");
        kakao_email = intent.getExtras().getString("kakao_email");
        kakao_name = intent.getExtras().getString("kakao_name");
        kakao_thumnail = intent.getExtras().getString("kakao_thumnail");
        selected_room_id = intent.getExtras().getString("selected_room_id");
        selected_room_name = intent.getExtras().getString("selected_room_name");
        Toast.makeText(getApplicationContext(), selected_room_id, Toast.LENGTH_LONG).show();


    }
}
