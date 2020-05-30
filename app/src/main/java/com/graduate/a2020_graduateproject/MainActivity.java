package com.graduate.a2020_graduateproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import com.kakao.usermgmt.callback.MeV2ResponseCallback;
import com.kakao.usermgmt.callback.UnLinkResponseCallback;
import com.kakao.usermgmt.response.MeV2Response;
import com.kakao.usermgmt.response.model.Profile;
import com.kakao.usermgmt.response.model.UserAccount;
import com.kakao.util.OptionalBoolean;
import com.kakao.util.helper.log.Logger;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Toolbar toolbar;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    View nav_header_view;
    private ImageView profile_image;
    private TextView profile_name;
    private TextView profile_email;

    private static Long kakao_id;
    private static String kakao_thumnail;
    private static String kakao_email;
    private static String kakao_name;

    // ----------------------- MyTripRoomList

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



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("나의 여행");

        toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);


        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.openNavDrawer,
                R.string.closeNavDrawer
        );

        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        nav_header_view = navigationView.getHeaderView(0);
        profile_image = nav_header_view.findViewById(R.id.profile_image);
        profile_name = (TextView)nav_header_view.findViewById(R.id.name);
        profile_email = (TextView)nav_header_view.findViewById(R.id.email);

        //requestUserInfo();

        // --------------- MyTripRoomList

        Intent intent = getIntent();
        kakao_id = intent.getExtras().getLong("kakao_id");
        kakao_email = intent.getExtras().getString("kakao_email");
        kakao_name = intent.getExtras().getString("kakao_name");
        kakao_thumnail = intent.getExtras().getString("kakao_thumnail");

        // 프로필 정보 화면에 출력
        Glide.with(getApplicationContext()).load(kakao_thumnail).error(R.drawable.kakao_default_profile_image).into(profile_image);
        profile_name.setText(kakao_name);
        profile_email.setText(kakao_email);

        newRoom_btn = findViewById(R.id.newRoom_btn);
        invited_btn = findViewById(R.id.invited_btn);
        newRoomName_text = findViewById(R.id.newRoomName_text);
        invited_text = findViewById(R.id.invited_text);
        myRoomList = findViewById(R.id.room_list);
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, new ArrayList<String>());
        myRoomList.setAdapter(arrayAdapter);

        System.out.println("current_user_id:"+ kakao_id );

        userReference = FirebaseDatabase.getInstance().getReference("sharing_trips/user_list").child(kakao_id.toString()).child("/myRoomList/"); // 변경값을 확인할 child 이름
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

                Intent intent = new Intent(MainActivity.this, TripRoomActivity.class);
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

                invited_room_id = decryptRoomId(invited_room_id);


                invited_room_name = null;
                invited_room_master_id = null;

                DatabaseReference tripRoomRef = FirebaseDatabase.getInstance().getReference("sharing_trips")
                        .child("tripRoom_list");
                tripRoomRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {



                        for (DataSnapshot snapshot : dataSnapshot.getChildren()){

                            Log.e("snapshot KEY", snapshot.getKey());


                            /// byte -> String 으로 변환하면서 무언가 바뀌는 듯
                            // 복호화 한 문자열과 키값이 일치하지 않음
                            AES aes = new AES();
                            String key = aes.encrypt(snapshot.getKey());
                            key = aes.decrypt(key);

                            System.out.println("key = " + key);

                            if(invited_room_id.equals(key)){
                                Log.e("snapshot ROOM IS EXIST", invited_room_id);

                                invited_room_name = snapshot.child("name").getValue().toString();
                                invited_room_master_id = snapshot.child("master_id").getValue().toString();

                                updateInvited(kakao_id, snapshot.getKey(), invited_room_name, invited_room_master_id);
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

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.myCalendar) { // 내비게이션 테스트

            Intent intent = new Intent(MainActivity.this, MyCalendarActivity.class);
            intent.putExtra("kakao_id", kakao_id);
            intent.putExtra("kakao_email", kakao_email);
            intent.putExtra("kakao_name", kakao_name);
            intent.putExtra("kakao_thumnail", kakao_thumnail);
            startActivity(intent);
        }
//        else if (id == R.id.myRooms){
//            Intent intent = new Intent(MainActivity.this, MyTripRoomListActivity.class);
//            intent.putExtra("kakao_id", kakao_id);
//            intent.putExtra("kakao_email", kakao_email);
//            intent.putExtra("kakao_name", kakao_name);
//            intent.putExtra("kakao_thumnail", kakao_thumnail);
//            startActivity(intent);
//        }
        else if(id == R.id.settings) {

        }
        else if(id == R.id.logout) { // 카카오 로그아웃

           onClickLogout();

        }
        else if(id == R.id.withdraw){ // 앱 탈퇴

            onClickUnlink();


        }

        return true;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    protected void redirectLoginActivity(){
        final Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }


    /* 로그아웃 */
    private void onClickLogout() {
        UserManagement.getInstance().requestLogout(new LogoutResponseCallback() {
            @Override
            public void onCompleteLogout() {
                Log.e("KakaoLogout ::", "로그아웃 합니다..");
                final Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    /* 앱 연결 해제 */
    private void onClickUnlink() {
        final String appendMessage = getString(R.string.com_kakao_confirm_unlink);
        new AlertDialog.Builder(this)
                .setMessage(appendMessage)
                .setPositiveButton(getString(R.string.com_kakao_ok_button),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                UserManagement.getInstance().requestUnlink(new UnLinkResponseCallback() {
                                    @Override
                                    public void onFailure(ErrorResult errorResult) {
                                        Log.e("KakaoSession","앱연결해제 실패" );
                                        Logger.e(errorResult.toString());
                                    }

                                    @Override
                                    public void onSessionClosed(ErrorResult errorResult) {
                                        Log.e("KakaoSession","앱연결해제 세션이 이미 닫힌경우" );
                                        redirectLoginActivity();
                                    }

                                    @Override
                                    public void onNotSignedUp() {

                                        Log.e("KakaoSession","앱연결해제 미가입" );
                                        redirectLoginActivity();
                                    }

                                    @Override
                                    public void onSuccess(Long userId) {

                                        Log.e("KakaoSession","앱연결해제 성공" );
                                        deleteUser(kakao_id);
                                        redirectLoginActivity();
                                    }
                                });
                                dialog.dismiss();
                            }
                        })
                .setNegativeButton(getString(R.string.com_kakao_cancel_button),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();

    }



    public void requestUserInfo(){
        UserManagement.getInstance()
                .me(new MeV2ResponseCallback() {
                    @Override
                    public void onSessionClosed(ErrorResult errorResult) {
                        Log.e("KAKAO_API", "세션이 닫혀 있음: " + errorResult);
                    }

                    @Override
                    public void onFailure(ErrorResult errorResult) {
                        Log.e("KAKAO_API", "사용자 정보 요청 실패: " + errorResult);
                    }

                    @Override
                    public void onSuccess(MeV2Response result) {

                        kakao_id = result.getId();
                        Log.i("KAKAO_API", "사용자 아이디: " + result.getId());

                        UserAccount kakaoAccount = result.getKakaoAccount();
                        if (kakaoAccount != null) {

                            // 이메일
                            String e = kakaoAccount.getEmail();

                            if (e != null) {
                                Log.i("KAKAO_API", "email: " + e);

                            } else if (kakaoAccount.emailNeedsAgreement() == OptionalBoolean.TRUE) {
                                // 동의 요청 후 이메일 획득 가능
                                // 단, 선택 동의로 설정되어 있다면 서비스 이용 시나리오 상에서 반드시 필요한 경우에만 요청해야 합니다.

                            } else {
                                // 이메일 획득 불가
                            }

                            // 프로필
                            Profile profile = kakaoAccount.getProfile();

                            if (profile != null) {
                                Log.d("KAKAO_API", "nickname: " + profile.getNickname());
                                Log.d("KAKAO_API", "profile image: " + profile.getProfileImageUrl());
                                Log.d("KAKAO_API", "thumbnail image: " + profile.getThumbnailImageUrl());

                            } else if (kakaoAccount.profileNeedsAgreement() == OptionalBoolean.TRUE) {
                                // 동의 요청 후 프로필 정보 획득 가능

                            } else {
                                // 프로필 획득 불가
                            }

                            // 프로필 정보 화면에 출력
                            Glide.with(getApplicationContext()).load( profile.getThumbnailImageUrl()).error(R.drawable.kakao_default_profile_image).into(profile_image);
                            profile_name.setText(profile.getNickname());
                            profile_email.setText(e);

                            // 로그인 정보 저장
                            kakao_thumnail = profile.getThumbnailImageUrl();
                            if( kakao_thumnail == null){
                                kakao_thumnail = "no thumnail";
                            }
                            kakao_email = e;
                            kakao_name = profile.getNickname();
                        }
                    }
                });
    }

    public void deleteUser(Long kakao_id){

        DatabaseReference mPostReference = FirebaseDatabase.getInstance().getReference("sharing_trips");
        Map<String, Object> childUpdates = new HashMap<>();
        Map<String, Object> postValues = null;

        childUpdates.put("/user_list/" + kakao_id, postValues);
        mPostReference.updateChildren(childUpdates);
    }

    // ------------------ MyTripRoomList


    public String decryptRoomId(String encrypt_str){
        AES aes = new AES();
        String decrypt_str = aes.decrypt(encrypt_str);

        System.out.println("decrypt_str = " + decrypt_str);

        return decrypt_str;
    }

    public void addMyTripRoom(Long kakao_id, String name){

        // 현재시간
        long now = System.currentTimeMillis();
        Date mDate = new Date(now);
        SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String getTime = simpleDate.format(mDate);

        // 여행방 코드 정해야 함 (지금은 테스트 용)
        //String room_id = getTime + "master:"+ kakao_id; // 방 생성 시간 + 만든 사람 id 로 일단 테스트
        String room_id = getTime + kakao_id;

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
