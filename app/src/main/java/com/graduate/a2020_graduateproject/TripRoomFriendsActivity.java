package com.graduate.a2020_graduateproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TripRoomFriendsActivity extends AppCompatActivity {

    // 로그인 정보
    private Long kakao_id;
    private String kakao_email;
    private String kakao_thumnail;
    private String kakao_name;
    // 선택한 여행방 정보
    private String selected_room_name; // 여행방 이름
    private String selected_room_id; // 여행방 id

    private UserAdapter userAdapter;
    private DatabaseReference userRef;

    private UserAdapter masterAdapter;
    private DatabaseReference masterRef;

    private User findUser = null;

    private String master_id;

    @BindView(R.id.friend_listView)
    ListView userListView;

    @BindView(R.id.master_listView)
    ListView masterListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_room_friends);

        ButterKnife.bind(this);

        Intent intent = getIntent();
        kakao_id = intent.getExtras().getLong("kakao_id");
        kakao_email = intent.getExtras().getString("kakao_email");
        kakao_name = intent.getExtras().getString("kakao_name");
        kakao_thumnail = intent.getExtras().getString("kakao_thumnail");
        selected_room_id = intent.getExtras().getString("selected_room_id");
        selected_room_name = intent.getExtras().getString("selected_room_name");
        //Toast.makeText(getApplicationContext(), selected_room_id, Toast.LENGTH_LONG).show();


        masterAdapter = new UserAdapter(selected_room_id);
        masterAdapter.setMaster(false);
        masterListView = findViewById(R.id.master_listView);
        masterListView.setAdapter(masterAdapter);
        masterRef = FirebaseDatabase.getInstance().getReference("sharing_trips/tripRoom_list")
                .child(selected_room_id).child("master_id");
        masterRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                masterAdapter.clear();

                master_id = dataSnapshot.getValue().toString();
                Log.e("TripRoomFriendsActivity", "master_id: "+ master_id);

                // 권한 검사
                if(master_id.equals(kakao_id.toString())){
                    Log.e("TripRoomFriendsActivity", "kakao_id: "+ kakao_id);
                    //masterAdapter.setMaster(true);
                    userAdapter.setMaster(true);
                }
                else{
                    masterAdapter.setMaster(false);
                    userAdapter.setMaster(false);
                }

                getUser(master_id, 2);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        userAdapter = new UserAdapter(selected_room_id);
        // master 인지 체크
        userAdapter.setMaster(false);

        userListView = findViewById(R.id.friend_listView);
        userListView.setAdapter(userAdapter);

        userRef = FirebaseDatabase.getInstance().getReference("sharing_trips/tripRoom_list")
                .child(selected_room_id).child("invited_user_list");
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                userAdapter.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    // snapshot 내에 있는 데이터만큼 반복합니다.
                    String key = snapshot.getKey();
                    Log.e("TripRoomFriendsActivity", "key: "+key); // 채팅 id


                    String send_id =  snapshot.child("id").getValue().toString();

                    getUser(send_id, 1); // id 가 send_id 인 user 찾음




                }


                userAdapter.notifyDataSetChanged();
                userListView.setSelection(userAdapter.getCount()-1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




    }

    public void getUser(String find_id, int i){

        findUser = null;
        Log.e("TripRoomFriendsActivity", "find_id: "+find_id); // 채팅 id

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("sharing_trips/user_list");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    // snapshot 내에 있는 데이터만큼 반복합니다.
                    String key = snapshot.getKey();
                    Log.e("TripRoomFriendsActivity", "get user key: "+key); // 채팅 id


                    if(find_id.equals(key)){

                        Log.e("TripRoomFriendsActivity", "find id equals key true: " + key); // 채팅 id

                        String id =  snapshot.child("id").getValue().toString();
                        String name = snapshot.child("name").getValue().toString();
                        String email = snapshot.child("email").getValue().toString();
                        String thumbnail = snapshot.child("thumbnail").getValue().toString();

                        findUser = new User(id,name,email,thumbnail);

                        if(findUser != null && i == 1){ // 초대된 사람
                            userAdapter.add(findUser);
                            userAdapter.notifyDataSetChanged();
                        }
                        else if(findUser != null && i == 2){ // 방장
                            masterAdapter.add(findUser);
                            masterAdapter.notifyDataSetChanged();
                        }
                        else{
                            Log.e("TripRoomFriendsActivity","findUser is null");
                        }

                        break;
                    }


                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
