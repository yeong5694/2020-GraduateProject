package com.graduate.a2020_graduateproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kakao.network.ErrorResult;
import com.kakao.auth.*;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeV2ResponseCallback;
import com.kakao.usermgmt.response.MeV2Response;
import com.kakao.usermgmt.response.model.Profile;
import com.kakao.usermgmt.response.model.UserAccount;
import com.kakao.util.OptionalBoolean;
import com.kakao.util.exception.KakaoException;
import com.kakao.util.helper.log.Logger;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private SessionCallback callback;

    private static Long kakao_id;
    private static String kakao_thumnail;
    private static String kakao_email;
    private static String kakao_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //카카오 로그인 콜백받기
        callback = new SessionCallback();
        Session.getCurrentSession().addCallback(callback);
        Session.getCurrentSession().checkAndImplicitOpen();

        //키값 알아내기(알아냈으면 등록하고 지워도 상관없다)
        //getAppKeyHash();



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            return;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Session.getCurrentSession().removeCallback(callback);
    }



    protected void redirectMainActivity() {
        //final Intent intent = new Intent(this, SuccessLoginActivity.class);
        final Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("kakao_id", kakao_id);
        intent.putExtra("kakao_email", kakao_email);
        intent.putExtra("kakao_name", kakao_name);
        intent.putExtra("kakao_thumnail", kakao_thumnail);
        startActivity(intent);
        finish();
    }

    protected void redirectLoginActivity(){
        final Intent intent = new Intent(LoginActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }




    private class SessionCallback implements ISessionCallback {

        @Override
        public void onSessionOpened() {

            Log.e("KakaoLogin ::", "로그인 성공");
            requestUserInfo();
        }

        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            Log.e("KakaoLogin ::", "로그인 실패");
            if(exception != null) {
                Logger.e(exception);
            }
            redirectLoginActivity();
        }
    }

    public ISessionConfig getSessionConfig() {
        return new ISessionConfig() {
            @Override
            public AuthType[] getAuthTypes() {
                return new AuthType[] {AuthType.KAKAO_TALK};
            }

            @Override
            public boolean isUsingWebviewTimer() {
                return false;
            }

            @Override
            public boolean isSecureMode() {
                return false;
            }

            @Override
            public ApprovalType getApprovalType() {
                return ApprovalType.INDIVIDUAL;
            }

            @Override
            public boolean isSaveFormData() {
                return true;
            }
        };
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
                            String email = kakaoAccount.getEmail();

                            if (email != null) {
                                Log.i("KAKAO_API", "email: " + email);
                                kakao_email = email;

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


                            // 로그인 정보 저장
                            kakao_thumnail = profile.getThumbnailImageUrl();
                            if( kakao_thumnail == null){
                                kakao_thumnail = "no thumnail";
                            }
                            if( kakao_email == null){
                                kakao_email = "no email";
                            }

                            kakao_name = profile.getNickname();

                            //updateUser(result.getId(), profile.getNickname(), kakaoAccount.getEmail(), profile.getThumbnailImageUrl()); // 파이어베이스에 저장
                            updateUser(result.getId(), profile.getNickname(), kakaoAccount.getEmail(), profile.getProfileImageUrl()); // 파이어베이스에 저장

                            redirectMainActivity();

                        }
                    }
                });
    }




    public void updateUser(Long id, String name, String email, String thumbnail){

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("sharing_trips/user_list").child(id.toString());

        if(thumbnail == null)
        {
            thumbnail = "no thumnail";
        }
        if(email == null ){
            email = "no email";
        }
        if( name == null ){
            name = "no name";
        }
        Map<String, Object> userUpdate = new HashMap<>();
        userUpdate.put("id",id);
        userUpdate.put("name", name);
        userUpdate.put("email", email);
        userUpdate.put("thumbnail", thumbnail);

        userRef.updateChildren(userUpdate);
    }

    /* 해쉬키 구하는 함수 (구했으면 지워도됨)*/
    private void getAppKeyHash() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String something = new String(Base64.encode(md.digest(), 0));
                Log.e("Hash key", something);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            Log.e("name not found", e.toString());
        }
    }
}
