package com.graduate.a2020_graduateproject;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class UploadViewHolder extends RecyclerView.ViewHolder {

    public ImageView image;
    public String imageUrl;


    public UploadViewHolder(@NonNull View itemView, Context parent) {
        super(itemView);

        image = itemView.findViewById(R.id.image);

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ////////////////// imageClickActivity 이동
                Intent intent = new Intent(parent, GalleryImageViewerActivity.class);
                intent.putExtra("uri", imageUrl);

                parent.startActivity(intent);
            }
        });


    }

    public void setImageUrl(String imageUrl){
        this.imageUrl = imageUrl;
    }

    public String getImageUrl(){
        return  imageUrl;
    }

}
