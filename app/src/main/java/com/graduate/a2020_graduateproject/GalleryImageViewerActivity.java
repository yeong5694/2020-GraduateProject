package com.graduate.a2020_graduateproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class GalleryImageViewerActivity extends AppCompatActivity {

    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_image_viewer);

        imageView = findViewById(R.id.imageView);

        // Get intent data
        Intent intent = getIntent();

        Uri imageUri = Uri.parse(intent.getExtras().getString("clicked image"));
        System.out.println(intent.getExtras().getString("clicked image"));

        // Get Selected Image Id
        /*
        int position = intent.getExtras().getInt("id");
        imageView.setImageResource(position);*/

//        Uri image_uri = Uri.parse(intent.getExtras().getString("uri"));



        Glide.with(this)
                .load(imageUri)
                .into(imageView);
/*
        Glide.with(holder.image)
                .load(uploadItems.get(position).getImageUrl())
                .error(R.drawable.kakao_default_profile_image)
                .into(holder.image);*/
    }
}
