package com.graduate.a2020_graduateproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;

public class SuccessLogin extends AppCompatActivity {

    private Button kakao_logout_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success_login);

        kakao_logout_btn = (Button)findViewById(R.id.kakao_logout_btn);
        kakao_logout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickLogout();
            }
        });
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
}
