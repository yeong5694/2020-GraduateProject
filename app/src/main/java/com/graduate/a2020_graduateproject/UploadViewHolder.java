package com.graduate.a2020_graduateproject;

import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class UploadViewHolder extends RecyclerView.ViewHolder {

    public ImageView image;


    public UploadViewHolder(@NonNull View itemView) {
        super(itemView);

        image = itemView.findViewById(R.id.image);


    }

}
