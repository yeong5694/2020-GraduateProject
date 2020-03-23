package com.graduate.a2020_graduateproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //지도화면으로 넘어가는 버튼 하나 만들어놓음
       Button map_button=(Button)findViewById(R.id.map_button);
        map_button.setOnClickListener(new View.OnClickListener(){

        @Override
            public void onClick(View v) {
                Intent myintent =new Intent(MainActivity.this, MapActivity.class);
                startActivity(myintent);
            }
        });

    }

}
