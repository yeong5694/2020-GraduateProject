package com.graduate.a2020_graduateproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class GalleryImageViewerActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_image_viewer);

        toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼
        setTitle("");

        imageView = findViewById(R.id.imageView);

        Intent intent = getIntent();

        Uri imageUri = Uri.parse(intent.getExtras().getString("clicked image"));

        Glide.with(this)
                .load(imageUri)
                .into(imageView);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{ // toolbar의 back 키 눌렀을 때 동작
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

}
