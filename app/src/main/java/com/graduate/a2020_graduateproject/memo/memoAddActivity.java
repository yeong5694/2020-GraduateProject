package com.graduate.a2020_graduateproject.memo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.graduate.a2020_graduateproject.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class memoAddActivity extends AppCompatActivity {

    // 로그인 정보
    private Long kakao_id;
    private String kakao_email;
    private String kakao_thumnail;
    private String kakao_name;
    // 선택한 여행방 정보
    private String selected_room_name; // 여행방 이름
    private String selected_room_id; // 여행방 id

    private TextView content;
    private Button save_btn;

    private DatabaseReference memoRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo_add);

        Intent intent = getIntent();
        kakao_id = intent.getExtras().getLong("kakao_id");
        kakao_email = intent.getExtras().getString("kakao_email");
        kakao_name = intent.getExtras().getString("kakao_name");
        kakao_thumnail = intent.getExtras().getString("kakao_thumnail");
        selected_room_id = intent.getExtras().getString("selected_room_id");
        selected_room_name = intent.getExtras().getString("selected_room_name");

        content = findViewById(R.id.content);
        save_btn = findViewById(R.id.save_btn);

        memoRef = FirebaseDatabase.getInstance().getReference("sharing_trips/tripRoom_list").child(selected_room_id).child("memo_list");

        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if( content.getText().toString() != null){
                    save(content.getText().toString());
                    content.setText("");
                    Toast.makeText(getApplicationContext(), "저장되었습니다", Toast.LENGTH_LONG).show();
                }

            }
        });


    }

    public void save(String content){

        // 현재시간
        long now = System.currentTimeMillis();
        Date mDate = new Date(now);
        SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy년 MM월 dd일 hh시mm분");
        String currentTime = simpleDate.format(mDate);

        memoRef.push().setValue(new memoItem(kakao_name, content, kakao_thumnail, currentTime));



    }
}
