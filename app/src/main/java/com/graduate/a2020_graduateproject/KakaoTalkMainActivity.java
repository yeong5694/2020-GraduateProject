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

        requestProfile();

        /*
        Glide.with(this).load("http://th-p.talk.kakao.co.kr/th/talkp/wlBoIs5StI/j5WeBXAuhnhSVz5RqbUHVK/fozflp_110x110_c.jpg")
                .error(R.drawable.error_img).into(profile_image);

         */

    }


    private abstract class KakaoTalkResponseCallback<T> extends TalkResponseCallback<T> {
        @Override
        public void onNotKakaoTalkUser() {
            Log.e("KakaoTalkResponseCallback ::", "카카오톡 사용자가 아니다");
        }

        @Override
        public void onFailure(ErrorResult errorResult) {
            Log.e("KakaoTalkResponseCallback ::", "이외 다른 이유");
            Logger.e("failure : " + errorResult);
        }

        @Override
        public void onSessionClosed(ErrorResult errorResult) {
            Log.e("KakaoTalkResponseCallback ::", "세션이 닫혀 실패한경우");
            redirectLoginActivity();
        }
    }

    public void requestProfile() {
        KakaoTalkService.getInstance().requestProfile(new KakaoTalkResponseCallback<KakaoTalkProfile>() {
            @Override
            public void onSuccess(KakaoTalkProfile talkProfile) { // 실시간 톡 프로필 정보
                Log.e("KakaoTalkResponseCallback ::", "프로필 가져오기 시작");

                final String nickName = talkProfile.getNickName(); // 카톡 별명
                final String profileImageURL = talkProfile.getProfileImageUrl(); // 카톡 프로필 이미지
                final String thumbnailURL = talkProfile.getThumbnailUrl(); // 카톡 썸네일 이미지
                final String countryISO = talkProfile.getCountryISO(); // 카톡 국가

                Log.e("KakaoTalkProfile ::", "nickname = " + nickName);
                Log.e("KakaoTalkProfile ::", "profileImageURL = " + profileImageURL);
                Log.e("KakaoTalkProfile ::", "thumbnailURL = " + thumbnailURL);
                Log.e("KakaoTalkProfile ::", "countryISO = " + countryISO);

                Log.e("KakaoTalkResponseCallback ::", "프로필 가져오기 종료");

                Glide.with(getApplicationContext()).load(thumbnailURL).error(R.drawable.error_img).into(profile_image);

            }
        });
    }

    protected void redirectLoginActivity(){
        final Intent intent = new Intent(KakaoTalkMainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
