package com.graduate.a2020_graduateproject;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.kakao.auth.authorization.accesstoken.AccessToken;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.LoginButton;
import com.kakao.auth.*;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.callback.MeV2ResponseCallback;
import com.kakao.usermgmt.callback.UnLinkResponseCallback;
import com.kakao.usermgmt.response.MeV2Response;
import com.kakao.usermgmt.response.model.Profile;
import com.kakao.usermgmt.response.model.UserAccount;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.OptionalBoolean;
import com.kakao.util.exception.KakaoException;
import com.kakao.util.helper.log.Logger;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    private SessionCallback callback;


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



    protected void redirectSignupActivity() {
        final Intent intent = new Intent(this, SuccessLogin.class);
        startActivity(intent);
        finish();
    }

    protected void redirectLoginActivity(){
        final Intent intent = new Intent(LoginActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }



    /* 회원가입 */
    private class SessionCallback implements ISessionCallback {

        // 로그인 성공 시 진입
        @Override
        public void onSessionOpened() {
            Log.e("KakaoLogin ::", "로그인 성공 --> 사용자 정보 요청");
            requestMe();

            //redirectSignupActivity();
        }
        // 로그인 실패
        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            Log.e("KakaoLogin ::", "로그인 실패");
            if(exception != null) {
                Logger.e(exception);
            }
            redirectLoginActivity();
        }


    }

    /* 사용자 정보 수집*/
    public void requestMe(){
        List<String> keys = new ArrayList<>();
        keys.add("properties.nickname");
        keys.add("properties.profile_image");
        keys.add("kakao_account.email");

        // 사용자정보 요청 결과에 대한 Callback
        UserManagement.getInstance().me(keys, new MeV2ResponseCallback() {

            // 세션 오픈 실패. 세션이 삭제된 경우
            @Override
            public void onSessionClosed(ErrorResult errorResult) {
                Log.e("KakaoSessionCallback :: ", "onSessionClosed : " + errorResult.getErrorMessage());
            }



            @Override
            public void onFailure(ErrorResult errorResult){
                Log.e("SessionCallback :: ", "onFailure : " + errorResult.getErrorMessage());
            }

            /* */
            @Override
            public void onSuccess(MeV2Response response) {
                Log.e("SessionCallback :: ", "onSuccess");
                Log.e("kakaoLogin ::  ", "카카오 로그인 정보 가져오기");
                Log.e("KakaoLogin","user id : " + response.getId());
                /*사용자 아이디(ID)의 경우 앱 연결 과정에서 발급하는 앱별 사용자의 고유 아이디입니다. 해당 아이디를 통해 사용자를 앱에서 식별 가능하며, 앱 연결 해제를 하더라도 같은 값으로 계속 유지됩니다.*/

                UserAccount kakaoAccount = response.getKakaoAccount();
                if (kakaoAccount != null) {
                    String email = kakaoAccount.getEmail();
                    if (email != null) {
                        Log.e("KakaoLogin","email : " + email);
                    } else if (kakaoAccount.emailNeedsAgreement() == OptionalBoolean.TRUE) {
                        // 동의 요청 후 이메일 획득 가능
                        // 단, 선택 동의로 설정되어 있다면 서비스 이용 시나리오 상에서 반드시 필요한 경우에만 요청해야 합니다.
                        ///// 이메일 따로 획득해야한다.
                    } else {
                        // 이메일 획득 불가
                    }

                    Profile profile = kakaoAccount.getProfile();
                    if (profile != null) {
                        Log.e("KakaoLogin","nickname : " + profile.getNickname());
                        Log.e("KakaoLogin","profile image : " + profile.getProfileImageUrl());
                        Log.e("KakaoLogin","thumbnail image : " + profile.getThumbnailImageUrl());
                    } else if (kakaoAccount.profileNeedsAgreement() == OptionalBoolean.TRUE) {
                        // 동의 요청 후 프로필 정보 획득 가능

                    } else {
                        // 프로필 획득 불가
                    }
                }

                redirectSignupActivity(); // SuccessLogin 으로 이동
            }





        });
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
