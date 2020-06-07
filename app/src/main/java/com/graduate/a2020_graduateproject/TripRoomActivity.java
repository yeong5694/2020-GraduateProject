package com.graduate.a2020_graduateproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.graduate.a2020_graduateproject.memo.memoActivity;
import com.kakao.kakaolink.v2.KakaoLinkResponse;
import com.kakao.kakaolink.v2.KakaoLinkService;
import com.kakao.message.template.ButtonObject;
import com.kakao.message.template.ContentObject;
import com.kakao.message.template.FeedTemplate;
import com.kakao.message.template.LinkObject;
import com.kakao.network.ErrorResult;
import com.kakao.network.callback.ResponseCallback;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import com.kakao.util.helper.log.Logger;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class TripRoomActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Toolbar toolbar;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    // 로그인 정보
    private Long kakao_id;
    private String kakao_email;
    private String kakao_thumnail;
    private String kakao_name;
    // 선택한 여행방 정보
    private String selected_room_name; // 여행방 이름
    private String selected_room_id; // 여행방 id

    View nav_header_view;
    private ImageView profile_image;
    private TextView profile_name;
    private TextView profile_email;

    private String KAKAO_BASE_LINK = "https://developers.kakao.com"; // 나중에 playStore 로 연결

    private String authority = null;

    //////////////////////

    private static String TAG = "PlaningActivity";

    private TextView schedule_txt;
    private TextView schedule_end_txt;
    private ImageView planEdit;
    private ImageView planEditFin;
    private ImageView add;
    private TextView description;

    private DatabaseReference scheduleRef;

    private RecyclerView planRecyclerView;
    private RecyclerView.Adapter planRecycleAdapter;
    private RecyclerView.LayoutManager planLayoutManager;

    private PlanAdapter planAdapter;
    private ItemTouchHelper mItemTouchHelper;
    private ArrayList<String> arrayIndex = new ArrayList<String>();

    Calendar fromCal = Calendar.getInstance();
    Calendar toCal = Calendar.getInstance();

    private DatabaseReference fromRef;
    private DatabaseReference toRef;

    DatePickerDialog.OnDateSetListener fromDatePicker = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            fromCal.set(Calendar.YEAR, year);
            fromCal.set(Calendar.MONTH, month);
            fromCal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            String myFormat = "yyyy년 MM월 dd일";    // 출력형식   2018/11/28
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.KOREA);

            DatabaseReference fromRef = FirebaseDatabase.getInstance().getReference("sharing_trips/tripRoom_list").child(selected_room_id)
                    .child("from");
            fromRef.setValue(sdf.format(fromCal.getTime()));

            //schedule_txt.setText(sdf.format(fromCal.getTime()));
        }

    };

    DatePickerDialog.OnDateSetListener toDatePicker = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            toCal.set(Calendar.YEAR, year);
            toCal.set(Calendar.MONTH, month);
            toCal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            String myFormat = "yyyy년 MM월 dd일";    // 출력형식   2018/11/28
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.KOREA);

            DatabaseReference toRef = FirebaseDatabase.getInstance().getReference("sharing_trips/tripRoom_list").child(selected_room_id)
                    .child("to");
            toRef.setValue(sdf.format(toCal.getTime()));

            //schedule_end_txt.setText(sdf.format(toCal.getTime()));
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_room);
        //setTitle("○○ 여행");

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

        Intent intent = getIntent();
        kakao_id = intent.getExtras().getLong("kakao_id");
        kakao_email = intent.getExtras().getString("kakao_email");
        kakao_name = intent.getExtras().getString("kakao_name");
        kakao_thumnail = intent.getExtras().getString("kakao_thumnail");
        selected_room_id = intent.getExtras().getString("selected_room_id");
        selected_room_name = intent.getExtras().getString("selected_room_name");

        //Toast.makeText(getApplicationContext(), selected_room_id, Toast.LENGTH_LONG).show();

        setTitle(selected_room_name);
        nav_header_view = navigationView.getHeaderView(0);
        profile_image = nav_header_view.findViewById(R.id.profile_image);
        profile_name = (TextView)nav_header_view.findViewById(R.id.name);
        profile_email = (TextView)nav_header_view.findViewById(R.id.email);

        profile_email.setText(kakao_email);
        profile_name.setText(kakao_name);
        Glide.with(getApplicationContext()).load(kakao_thumnail).error(R.drawable.kakao_default_profile_image).into(profile_image);


        schedule_txt = findViewById(R.id.schedule_txt);
        schedule_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(TripRoomActivity.this, fromDatePicker, fromCal.get(Calendar.YEAR),
                        fromCal.get(Calendar.MONTH), fromCal.get(Calendar.DAY_OF_MONTH)).show();

            }
        });
        schedule_end_txt = findViewById(R.id.schedule_end_txt);
        schedule_end_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(TripRoomActivity.this, toDatePicker, toCal.get(Calendar.YEAR),
                        toCal.get(Calendar.MONTH), toCal.get(Calendar.DAY_OF_MONTH)).show();
            }

        });

        fromRef = FirebaseDatabase.getInstance().getReference("sharing_trips/tripRoom_list").child(selected_room_id).child("from");
        fromRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //dataSnapshot.getKey();
                if(dataSnapshot.getValue() != null){
                    Log.e("from",dataSnapshot.getValue().toString());
                    schedule_txt.setText(dataSnapshot.getValue().toString());
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        toRef = FirebaseDatabase.getInstance().getReference("sharing_trips/tripRoom_list").child(selected_room_id).child("to");
        toRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() != null){
                    Log.e("to",dataSnapshot.getValue().toString());
                    schedule_end_txt.setText(dataSnapshot.getValue().toString());
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        planRecyclerView = findViewById(R.id.planRecyclerView);
        //planAdapter = new PlanAdapter(selected_room_id);

        planAdapter = new PlanAdapter(selected_room_id, kakao_id, kakao_email, kakao_thumnail, kakao_name );


        planLayoutManager = new LinearLayoutManager(this);
        planRecyclerView.setLayoutManager(planLayoutManager);
        planRecyclerView.setAdapter(planAdapter);

//        ItemTouchHelper.Callback callback =  new PlanItemTouchHelperCallback(planAdapter);
//        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
//        touchHelper.attachToRecyclerView(planRecyclerView);

        PlanItemTouchHelperCallback mCallback = new PlanItemTouchHelperCallback(planAdapter);
        mCallback.setMintem(false); // 드래그 안되게
        mCallback.setMintem2(true); // 스와핑 되게


        mItemTouchHelper = new ItemTouchHelper((mCallback));
        mItemTouchHelper.attachToRecyclerView(planRecyclerView);





        scheduleRef = FirebaseDatabase.getInstance().getReference("sharing_trips/tripRoom_list").child(selected_room_id)
                .child("schedule_list");
        scheduleRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                sort_list();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




        add = findViewById(R.id.planAdd);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("sharing_trips/tripRoom_list").child(selected_room_id)
                        .child("schedule_list");

                int d = planAdapter.getItemCount() +1;

                ref.push().setValue(new Schedule(Integer.toString(d)));
            }
        });
        description = findViewById(R.id.descriptionText);
        description.setText("밀어서 삭제하세요");


        planEdit = findViewById(R.id.planEdit);
        planEditFin = findViewById(R.id.planEditFin);
        planEditFin.setVisibility(View.INVISIBLE);

        planEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                planEdit.setVisibility(View.INVISIBLE);
                planEditFin.setVisibility(View.VISIBLE);
                description.setText("드래그해서 순서변경");
                add.setVisibility(View.INVISIBLE);

                mCallback.setMintem(true); // 드래그 되게
                mCallback.setMintem2(false); // 스와핑 안되게
            }
        });
        planEditFin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(TAG,"plan edit fin");
                mCallback.setMintem(false); // 드래그 안되게
                mCallback.setMintem2(true); // 스와핑 되게
                planEdit.setVisibility(View.VISIBLE);
                planEditFin.setVisibility(View.INVISIBLE);
                add.setVisibility(View.VISIBLE);
                description.setText("밀어서 삭제하세요");

                planAdapter.change();

                Toast.makeText(getApplicationContext(), "변경되었습니다", Toast.LENGTH_LONG).show();
            }
        });








    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.home) {
            // 뒤로 돌아가도록(홈화면 MainActivity로) 고쳐야 함
            Intent intent = new Intent(TripRoomActivity.this, MainActivity.class);

            intent.putExtra("kakao_id", kakao_id);
            intent.putExtra("kakao_email", kakao_email);
            intent.putExtra("kakao_name", kakao_name);
            intent.putExtra("kakao_thumnail", kakao_thumnail);

            startActivity(intent);

        }
        else if(id == R.id.trip_mates){

            Intent intent = new Intent(TripRoomActivity.this, TripRoomFriendsActivity.class);
            intent.putExtra("kakao_id", kakao_id);
            intent.putExtra("kakao_email", kakao_email);
            intent.putExtra("kakao_name", kakao_name);
            intent.putExtra("kakao_thumnail", kakao_thumnail);
            intent.putExtra("selected_room_name", selected_room_name);
            intent.putExtra("selected_room_id", selected_room_id);
            startActivity(intent);

        }
        else if(id == R.id.invite_trip_mates) {

            String room_id = encrypt_room_id(selected_room_id);

            createTemplate(room_id);

        }
        else if(id == R.id.chat) {
            Intent intent = new Intent(TripRoomActivity.this, ChattingRoomActivity.class);
            intent.putExtra("kakao_id", kakao_id);
            intent.putExtra("kakao_email", kakao_email);
            intent.putExtra("kakao_name", kakao_name);
            intent.putExtra("kakao_thumnail", kakao_thumnail);
            intent.putExtra("selected_room_name", selected_room_name);
            intent.putExtra("selected_room_id", selected_room_id);
            startActivity(intent);


        }
        else if(id == R.id.calendar) {

            Intent intent = new Intent(TripRoomActivity.this, MyCalendarActivity.class);
            startActivity(intent);

        }

        else if(id == R.id.planningMap) {
            Intent intent = new Intent(TripRoomActivity.this, Map_realFindRoadActivity.class);
            intent.putExtra("selected_room_name", selected_room_name);
            intent.putExtra("selected_room_id", selected_room_id);
            startActivity(intent);
        }
        else if(id == R.id.memo) {

            Intent intent = new Intent(TripRoomActivity.this, memoActivity.class);
            intent.putExtra("kakao_id", kakao_id);
            intent.putExtra("kakao_email", kakao_email);
            intent.putExtra("kakao_name", kakao_name);
            intent.putExtra("kakao_thumnail", kakao_thumnail);
            intent.putExtra("selected_room_name", selected_room_name);
            intent.putExtra("selected_room_id", selected_room_id);
            startActivity(intent);

        }
        else if(id == R.id.gallery) {
            Intent intent = new Intent(TripRoomActivity.this, ShareGalleryActivity.class);
            intent.putExtra("selected_room_name", selected_room_name);
            intent.putExtra("selected_room_id", selected_room_id);
            startActivity(intent);
        }
        else if(id == R.id.exit){
            do_exit();
            finish();

        }
        else if(id == R.id.settings) {

        }
        else if(id == R.id.logout) {

            onClickLogout();

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

    public String encrypt_room_id(String selected_room_id){

        // selected_room_id 암호화

        AES aes = new AES();


        String encrypt_str = aes.encrypt(selected_room_id);

        System.out.println("encrypt_str = " + encrypt_str);



        return encrypt_str;

    }
    public void createTemplate(String room_id){
        // 기본적으로 구현해야 할 것
        Map<String, String> serverCallbackArgs = new HashMap<String, String>();
        serverCallbackArgs.put("user_id", "${current_user_id}");
        serverCallbackArgs.put("product_id", "${shared_product_id}");
        // 템플릿 생성
        FeedTemplate params = FeedTemplate
                .newBuilder(ContentObject.newBuilder("여행초대코드",
                        "",
                        LinkObject.newBuilder().setWebUrl(KAKAO_BASE_LINK) //
                                .setMobileWebUrl(KAKAO_BASE_LINK).build()) //
                        .setDescrption(room_id)
                        .build())
                .addButton(new ButtonObject("앱에서 보기", LinkObject.newBuilder()
                        .setWebUrl(KAKAO_BASE_LINK) //
                        .setMobileWebUrl(KAKAO_BASE_LINK) //
                        .setAndroidExecutionParams("key1=value1")
                        .build()))
                .build();

        KakaoLinkService.getInstance().sendDefault(this, params, serverCallbackArgs, new ResponseCallback<KakaoLinkResponse>() {
            @Override
            public void onFailure(ErrorResult errorResult) {
                Logger.e(errorResult.toString());
            }

            @Override
            public void onSuccess(KakaoLinkResponse result) {
                // 템플릿 밸리데이션과 쿼터 체크가 성공적으로 끝남. 톡에서 정상적으로 보내졌는지 보장은 할 수 없다.
                // 전송 성공 유무는 서버콜백 기능을 이용하여야 한다.
            }
        });
    }
    public void exit_room(){ // 여행 구성원이 방 나가는 경우


        // 초대 받은 방 구성원에서 삭제
        DatabaseReference tripRoomRef2 = FirebaseDatabase.getInstance().getReference("sharing_trips/tripRoom_list")
                .child(selected_room_id).child("invited_user_list").child(kakao_id.toString());
        tripRoomRef2.removeValue();


        // 내 방 목록에서 삭제
        DatabaseReference userRef2 = FirebaseDatabase.getInstance().getReference("sharing_trips/user_list").child(kakao_id.toString())
                .child("/myRoomList").child(selected_room_id);
        userRef2.removeValue();


    }

    public void remove_room(){ // 방장이 방 나가는 경우

        // 내 방 목록에서 삭제
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("sharing_trips/user_list").child(kakao_id.toString())
                .child("/myRoomList").child(selected_room_id);
        userRef.removeValue();

        // 초대된 사람들 방 목록에서 삭제 (확인해봐야함)
        ArrayList<String> id_list = new ArrayList<>();

        DatabaseReference otherRef = FirebaseDatabase.getInstance().getReference("sharing_trips/tripRoom_list")
                .child(selected_room_id).child("invited_user_list");
        otherRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot snapshot : dataSnapshot.getChildren()){

                    id_list.add(snapshot.getKey());
                }

                remove_room_in_id_list(id_list); // 각자 방목록에서 삭제
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        // 전체 여행방리스트에서 영구 삭제
        DatabaseReference tripRoomRef = FirebaseDatabase.getInstance().getReference("sharing_trips/tripRoom_list")
                .child(selected_room_id);
        tripRoomRef.removeValue();

        // 전체 채팅리스트에서 영구 삭제
        DatabaseReference chatRef =  chatRef = FirebaseDatabase.getInstance().getReference("sharing_trips/chat_list").child(selected_room_id);
        chatRef.removeValue();
    }

    public void do_exit(){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("sharing_trips")
                .child("user_list").child(kakao_id.toString()).child("myRoomList");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {



                for (DataSnapshot snapshot : dataSnapshot.getChildren()){

                    Log.e("snapshot KEY", snapshot.getKey());

                    if(selected_room_id.equals(snapshot.getKey())){
                        Log.e("selectedRoomId", snapshot.getKey());

                        authority = snapshot.child("authority").getValue().toString();
                        Log.e("selectedRoomId-Master", authority);
                        if(authority.equals("master")){
                            remove_room();
                        }
                        else if(authority.equals("invited_user")){
                            exit_room();
                        }
                        else{

                        }
                        return;
                    }

                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void remove_room_in_id_list(ArrayList<String> id_list){

        for(String id : id_list){

            DatabaseReference rmRef =  FirebaseDatabase.getInstance().getReference("sharing_trips/user_list").child(id)
                    .child("/myRoomList").child(selected_room_id);
            rmRef.removeValue();
        }
    }


    public void sort_list(){
        scheduleRef.orderByChild("day").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Log.e(TAG,"day 기준으로 내림차순 정렬 -- ");

                planAdapter.clear();
                //arrayIndex.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    String key = snapshot.getKey();

                    String day =  snapshot.child("day").getValue().toString();


                    PlanItem planItem = new PlanItem(day,key);


                    //arrayIndex.add(key);
                    planAdapter.add(planItem);
                }


                planAdapter.notifyDataSetChanged();
                //planRecyclerView.setSelection(planAdapter.getCount()-1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    /* 로그아웃 */
    private void onClickLogout() {
        UserManagement.getInstance().requestLogout(new LogoutResponseCallback() {
            @Override
            public void onCompleteLogout() {
                Log.e("KakaoLogout ::", "로그아웃 합니다..");
                final Intent intent = new Intent(TripRoomActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }



}

