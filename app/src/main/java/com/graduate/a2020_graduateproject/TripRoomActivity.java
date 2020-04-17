package com.graduate.a2020_graduateproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.kakao.kakaolink.v2.KakaoLinkResponse;
import com.kakao.kakaolink.v2.KakaoLinkService;
import com.kakao.message.template.ButtonObject;
import com.kakao.message.template.ContentObject;
import com.kakao.message.template.FeedTemplate;
import com.kakao.message.template.LinkObject;
import com.kakao.network.ErrorResult;
import com.kakao.network.callback.ResponseCallback;
import com.kakao.util.helper.log.Logger;

import java.util.HashMap;
import java.util.Map;

public class TripRoomActivity extends AppCompatActivity  implements NavigationView.OnNavigationItemSelectedListener {

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
        Toast.makeText(getApplicationContext(), selected_room_id, Toast.LENGTH_LONG).show();

        setTitle(selected_room_name);
        nav_header_view = navigationView.getHeaderView(0);
        profile_image = nav_header_view.findViewById(R.id.profile_image);
        profile_name = (TextView)nav_header_view.findViewById(R.id.name);
        profile_email = (TextView)nav_header_view.findViewById(R.id.email);

        profile_email.setText(kakao_email);
        profile_name.setText(kakao_name);
        Glide.with(getApplicationContext()).load(kakao_thumnail).error(R.drawable.kakao_default_profile_image).into(profile_image);



    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.home) {
            // 뒤로 돌아가도록(홈화면 MainActivity로) 고쳐야 함
            Intent intent = new Intent(TripRoomActivity.this, MainActivity.class);
            startActivity(intent);
        }
        else if(id == R.id.trip_mates){

            Intent intent = new Intent(TripRoomActivity.this, TripRoomActivity.class);
            intent.putExtra("kakao_id", kakao_id);
            intent.putExtra("kakao_email", kakao_email);
            intent.putExtra("kakao_name", kakao_name);
            intent.putExtra("kakao_thumnail", kakao_thumnail);
            intent.putExtra("selected_room_name", selected_room_name);
            intent.putExtra("selected_room_id", selected_room_id);
            startActivity(intent);

        }
        else if(id == R.id.invite_trip_mates) {
            // 기본적으로 구현해야 할 것
            Map<String, String> serverCallbackArgs = new HashMap<String, String>();
            serverCallbackArgs.put("user_id", "${current_user_id}");
            serverCallbackArgs.put("product_id", "${shared_product_id}");
            // 템플릿 생성
            FeedTemplate params = FeedTemplate
                    .newBuilder(ContentObject.newBuilder("Sharing Trips",
                            "http://mud-kage.kakao.co.kr/dn/NTmhS/btqfEUdFAUf/FjKzkZsnoeE4o19klTOVI1/openlink_640x640s.jpg",
                            LinkObject.newBuilder().setWebUrl(KAKAO_BASE_LINK) //
                                    .setMobileWebUrl(KAKAO_BASE_LINK).build()) //
                            .setDescrption("여행방 id : "+ selected_room_id)
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
        else if(id == R.id.chat) {

        }
        else if(id == R.id.calendar) {

        }
        else if(id == R.id.plan) {

        }
        else if(id == R.id.map) {
            Intent intent = new Intent(TripRoomActivity.this, MapActivity.class);
            startActivity(intent);
        }
        else if(id == R.id.memo) {

        }
        else if(id == R.id.gallery) {
            Intent intent = new Intent(TripRoomActivity.this, ShareGalleryActivity.class);
            startActivity(intent);
        }
        else if(id == R.id.settings) {

        }
        else if(id == R.id.logout) {

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
}
