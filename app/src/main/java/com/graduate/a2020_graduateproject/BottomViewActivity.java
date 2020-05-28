package com.graduate.a2020_graduateproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.graduate.a2020_graduateproject.bottomNavigation.FragmentDay;
import com.graduate.a2020_graduateproject.bottomNavigation.FragmentMap;

public class BottomViewActivity extends AppCompatActivity {

    private FragmentManager fragmentManager = getSupportFragmentManager();
    private FragmentDay fragmentDay = new FragmentDay();
    private FragmentMap fragmentMap = new FragmentMap();

    private String selected_room_id;
    private String day;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_view);

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frameLayout, fragmentDay).commitAllowingStateLoss();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavi);
        bottomNavigationView.setOnNavigationItemSelectedListener(new ItemSelectedListener());

        Intent intent = getIntent();
        selected_room_id=intent.getExtras().getString("selected_room_id");
        day=intent.getExtras().getString("day");
        System.out.println("selected_room_id : "+selected_room_id+ " day : "+day);
    }



    class ItemSelectedListener implements BottomNavigationView.OnNavigationItemSelectedListener{
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();

            switch(menuItem.getItemId())
            {
                case R.id.dayItem:
                    transaction.replace(R.id.frameLayout, fragmentDay).commitAllowingStateLoss();

                    break;
                case R.id.mapItem:
                    transaction.replace(R.id.frameLayout, fragmentMap).commitAllowingStateLoss();
                    break;
                default :
                    return true;
            }
            return true;
        }
    }

    public  String getSelected_room_id(){
        return selected_room_id;
    }

    public String getDay(){
        return day;
    }
}
