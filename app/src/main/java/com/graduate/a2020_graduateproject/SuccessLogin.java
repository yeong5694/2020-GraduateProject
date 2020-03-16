package com.graduate.a2020_graduateproject;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import com.kakao.usermgmt.callback.UnLinkResponseCallback;
import com.kakao.util.helper.log.Logger;

public class SuccessLogin extends AppCompatActivity {

    private Button kakao_logout_btn;

    private Button redirect_login_btn;
    private Button delete_session_btn;
    private Button redirect_mypage_btn;

  private Button mainButton;  // 메인화면으로 가는 버튼     // 예원

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success_login);

      // 예원
        // 메인화면으로 가는 버튼
        mainButton = findViewById(R.id.go_to_main);
        mainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SuccessLogin.this, MainActivity.class);
                startActivity(intent);
            }
        });
      
      
        // 카카오 로그아웃
        kakao_logout_btn = (Button)findViewById(R.id.kakao_logout_btn);
        kakao_logout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickLogout();
            }
        });

        // 이 앱 탈퇴
        delete_session_btn = (Button)findViewById(R.id.delete_session_btn);
        delete_session_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("KakaoSessionDelete ::", "앱 탈퇴하기 호출");
                onClickUnlink();
            }
        });
        // LoginActivity 로 돌아감
        redirect_login_btn = (Button)findViewById(R.id.redirect_login_btn);
        redirect_login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                redirectLoginActivity();
            }
        });
        // 로그인 정보
        redirect_mypage_btn = (Button)findViewById(R.id.redirect_mypage_btn);
        redirect_mypage_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                redirectKakaoTalkMainActivity();
            }
        });

        Toast.makeText(getApplicationContext(),"로그인 성공!",Toast.LENGTH_LONG).show();
    }

    protected void redirectLoginActivity(){
        final Intent intent = new Intent(SuccessLogin.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    protected void redirectKakaoTalkMainActivity(){
        final Intent intent = new Intent(SuccessLogin.this, KakaoTalkMainActivity.class);
        startActivity(intent);
        finish();

    }

    /* 로그아웃 */
    private void onClickLogout() {
        UserManagement.getInstance().requestLogout(new LogoutResponseCallback() {
            @Override
            public void onCompleteLogout() {
                Log.e("KakaoLogout ::", "로그아웃 합니다..");
                final Intent intent = new Intent(SuccessLogin.this, LoginActivity.class);
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
}

