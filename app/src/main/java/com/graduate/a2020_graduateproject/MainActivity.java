package com.graduate.a2020_graduateproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.DialogInterface;
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

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Toolbar toolbar;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    View nav_header_view;
    private ImageView profile_image;
    private TextView kakao_name;
    private TextView kakao_email;

    private Long kakao_id;
    private String kakao_thumnail;
    private String email;
    private String name;

    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("sharing_trips");
    private DatabaseReference mReference;


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
        kakao_name = (TextView)nav_header_view.findViewById(R.id.name);
        kakao_email = (TextView)nav_header_view.findViewById(R.id.email);

        requestUserInfo();


    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();



        if(id == R.id.trips) {
//            Intent intent = new Intent(MainActivity.this, TripRoomActivity.class);
//            startActivity(intent);
        }
        else if(id == R.id.friends) {

        }
        else if (id == R.id.myRooms){
            Intent intent = new Intent(MainActivity.this, MyTripRoomListActivity.class);
            intent.putExtra("kakao_id", kakao_id);
            intent.putExtra("kakao_email", email);
            intent.putExtra("kakao_name", name);
            intent.putExtra("kakao_thumnail", kakao_thumnail);
            startActivity(intent);
        }
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
                            kakao_name.setText(profile.getNickname());
                            kakao_email.setText(e);
                            // 로그인 정보 저장
                            kakao_thumnail = profile.getThumbnailImageUrl();
                            email = e;
                            name = profile.getNickname();
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


}
