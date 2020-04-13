package com.graduate.a2020_graduateproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;

public class TripRoomActivity extends AppCompatActivity  implements NavigationView.OnNavigationItemSelectedListener {

    private Toolbar toolbar;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_room);
        setTitle("○○ 여행");

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
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.home) {
            // 뒤로 돌아가도록(홈화면 MainActivity로) 고쳐야 함
            Intent intent = new Intent(TripRoomActivity.this, MainActivity.class);
            startActivity(intent);
        }
        else if(id == R.id.trip_mates) {

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
