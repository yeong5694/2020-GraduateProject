package com.graduate.a2020_graduateproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.kakao.kakaotalk.callback.TalkResponseCallback;
import com.kakao.kakaotalk.response.KakaoTalkProfile;
import com.kakao.kakaotalk.v2.KakaoTalkService;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.util.helper.log.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KakaoTalkMainActivity extends AppCompatActivity {

    private ImageView profile_image;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage);

        profile_image = findViewById(R.id.profile_image);

        //requestProfile();

        /*
        Glide.with(this).load("http://th-p.talk.kakao.co.kr/th/talkp/wlBoIs5StI/j5WeBXAuhnhSVz5RqbUHVK/fozflp_110x110_c.jpg")
                .error(R.drawable.error_img).into(profile_image);

         */

    }






    protected void redirectLoginActivity(){
        final Intent intent = new Intent(KakaoTalkMainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
