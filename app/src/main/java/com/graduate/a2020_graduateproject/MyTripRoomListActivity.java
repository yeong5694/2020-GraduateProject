package com.graduate.a2020_graduateproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyTripRoomListActivity extends AppCompatActivity {

    private Long kakao_id;
    private String kakao_email;
    private String kakao_thumnail;
    private String kakao_name;


    private Button newRoom_btn; // 방 만들기 (내가 방장인경우)
    private Button invited_btn; // 초대받은 방 입력
    private EditText newRoomName_text;
    private EditText invited_text;
    private ListView myRoomList;

    private ArrayAdapter<String> arrayAdapter;
    private List<String> arrayData = new ArrayList<String>(); // 내 여행방 리스트 방 이름 저장
    private List<String> arrayIndex = new ArrayList<String>(); // 내 여행방 리스트 방 인덱스 저장

    private DatabaseReference userReference;
    private DatabaseReference tripRoomReference;


    private String invited_room_name = null;
    private String invited_room_id = null;
    private String invited_room_master_id = null;


    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_trip_room_list);


        newRoom_btn = findViewById(R.id.newRoom_btn);
        invited_btn = findViewById(R.id.invited_btn);
        newRoomName_text = findViewById(R.id.newRoomName_text);
        invited_text = findViewById(R.id.invited_text);
        myRoomList = findViewById(R.id.room_list);
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, new ArrayList<String>());
        myRoomList.setAdapter(arrayAdapter);



        Intent intent = getIntent();
        kakao_id = intent.getExtras().getLong("kakao_id");
        kakao_email = intent.getExtras().getString("kakao_email");
        kakao_name = intent.getExtras().getString("kakao_name");
        kakao_thumnail = intent.getExtras().getString("kakao_thumnail");

        toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        setTitle("내 여행방 목록");



        userReference = FirebaseDatabase.getInstance().getReference("sharing_trips/user_list").child(kakao_id.toString()).child("/myRoomList/"); // 변경값을 확인할 child 이름
        //mReference = databaseReference.child("/tripRoom_list/");
        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                arrayAdapter.clear();
                arrayData.clear();
                arrayIndex.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    // snapshot 내에 있는 데이터만큼 반복합니다.
                    String key = snapshot.getKey();
                    Log.e("MyTripRoomListActivity KEY", "key: "+key);


                    String room_name = snapshot.child("name").getValue().toString();
                    Log.e("MyTripRoomListActivity", "roon_name: "+ room_name);


                    arrayData.add(room_name);
                    arrayIndex.add(key);
                    arrayAdapter.add(room_name);

                }


                arrayAdapter.notifyDataSetChanged();
                myRoomList.setSelection(arrayAdapter.getCount()-1);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("getFirebaseDatabase","loadPost:onCancelled", databaseError.toException());
            }
        });



        myRoomList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String selected_room_name = arrayData.get(i);
                String selected_room_id = arrayIndex.get(i);

                //Toast.makeText(getApplicationContext(), selected_room_name, Toast.LENGTH_LONG).show();
                Log.e("MyTripRoomListActivity SELECTED ROOM ID", selected_room_id);

                Intent intent = new Intent(MyTripRoomListActivity.this, TripRoomActivity.class);
                intent.putExtra("kakao_id", kakao_id);
                intent.putExtra("kakao_email", kakao_email);
                intent.putExtra("kakao_name", kakao_name);
                intent.putExtra("kakao_thumnail", kakao_thumnail);
                intent.putExtra("selected_room_name", selected_room_name);
                intent.putExtra("selected_room_id", selected_room_id);

                startActivity(intent);

            }
        });







        newRoom_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String name = newRoomName_text.getText().toString();

                if(name.equals("")){
                    Toast.makeText(getApplicationContext(), "새로 만들 방이름 미입력", Toast.LENGTH_LONG).show();
                    return;
                }

                addMyTripRoom(kakao_id, name);

                Toast.makeText(getApplicationContext(), "새로운 방 생성 완료", Toast.LENGTH_LONG).show();
                newRoomName_text.setText("");

            }
        });

        invited_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                invited_room_id = invited_text.getText().toString();
                Log.e("InvitedRoomId", invited_room_id);


                if(invited_room_id.equals("")){
                    Toast.makeText(getApplicationContext(), "여행코드 미입력", Toast.LENGTH_LONG).show();
                    return;
                }


                invited_room_name = null;
                invited_room_master_id = null;

                DatabaseReference tripRoomRef = FirebaseDatabase.getInstance().getReference("sharing_trips")
                        .child("tripRoom_list");
                tripRoomRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {



                        for (DataSnapshot snapshot : dataSnapshot.getChildren()){

                            Log.e("snapshot KEY", snapshot.getKey());

                            if(invited_room_id.equals(snapshot.getKey())){
                                Log.e("snapshot ROOM IS EXIST", invited_room_id);

                                invited_room_name = snapshot.child("name").getValue().toString();
                                invited_room_master_id = snapshot.child("master_id").getValue().toString();

                                updateInvited(kakao_id, invited_room_id, invited_room_name, invited_room_master_id);
                                Toast.makeText(getApplicationContext(), "갱신되었습니다", Toast.LENGTH_LONG).show();

                                Log.e("snapshot ROOM INFO ", invited_room_name);


                                return;
                            }

                        }

                        Log.e("snapshot NO ROOM IN LIST ", invited_room_id);
                        Toast.makeText(getApplicationContext(), "존재하지 않는 방입니다", Toast.LENGTH_LONG).show();


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });



                invited_text.setText("");



            }

        });
    }



    public void addMyTripRoom(Long kakao_id, String name){

        // 현재시간
        long now = System.currentTimeMillis();
        Date mDate = new Date(now);
        SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String getTime = simpleDate.format(mDate);

        // 여행방 코드 정해야 함 (지금은 테스트 용)
        String room_id = getTime + "master:"+ kakao_id; // 방 생성 시간 + 만든 사람 id 로 일단 테스트

        // 전체 방 목록에 추가
        DatabaseReference tripRoomRef = FirebaseDatabase.getInstance().getReference("sharing_trips/tripRoom_list")
                .child(room_id);

        Map<String, Object> tripRoomUpdate = new HashMap<>();

        tripRoomUpdate.put("name", name);
        tripRoomUpdate.put("master_id", kakao_id);
        tripRoomUpdate.put("updated_time", getTime);

        tripRoomRef.updateChildren(tripRoomUpdate);

        // 내 방 목록에 추가
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("sharing_trips/user_list").child(kakao_id.toString())
                .child("/myRoomList").child(room_id);

        Map<String, Object> myRoomUpdate = new HashMap<>();

        myRoomUpdate.put("name", name);
        //myRoomUpdate.put("master_id", kakao_id);
        myRoomUpdate.put("authority", "master");

        userRef.updateChildren(myRoomUpdate);

    }




    public void updateInvited(Long kakao_id, String invited_room_id, String invited_room_name, String invited_room_master_id){

        // 내 방 목록에 추가
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("sharing_trips/user_list").child(kakao_id.toString())
                .child("/myRoomList").child(invited_room_id);

        Map<String, Object> myRoomUpdate = new HashMap<>();

        myRoomUpdate.put("name", invited_room_name);
        //myRoomUpdate.put("master_id", invited_room_master_id);
        myRoomUpdate.put("authority", "invited_user");

        userRef.updateChildren(myRoomUpdate);

        // 초대 받은 방에 구성원으로 등록
        DatabaseReference tripRoomRef = FirebaseDatabase.getInstance().getReference("sharing_trips/tripRoom_list")
                .child(invited_room_id).child("invited_user_list").child(kakao_id.toString());

        Map<String, Object> tripRoomUpdate = new HashMap<>();

        tripRoomUpdate.put("/id", kakao_id);


        tripRoomRef.updateChildren(tripRoomUpdate);
    }




}
