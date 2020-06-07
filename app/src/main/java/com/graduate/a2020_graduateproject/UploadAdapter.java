package com.graduate.a2020_graduateproject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.graduate.a2020_graduateproject.bottomNavigation.MapInfoViewHolder;

import java.util.ArrayList;

public class UploadAdapter  extends RecyclerView.Adapter<UploadViewHolder> {

    private ArrayList<Upload> uploadItems = new ArrayList<>();


    @NonNull
    @Override
    public UploadViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(
                parent.getContext()
        ).inflate(R.layout.gallery_image_item, parent, false);
        return new UploadViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull UploadViewHolder holder, int position) {

        // 여기서 이미지 띄우면 되는데 imgage url이 "com.google.android~" 여서 안뜨는데 ㅜㅠ 뭐로 바꿔야해? ㅜㅠ
        // load()여기에 넣으면 돼
        Glide.with(holder.image).load(uploadItems.get(position).getImageUrl())
                .error(R.drawable.kakao_default_profile_image).into(holder.image);


    }

    @Override
    public int getItemCount() {
        return uploadItems.size();
    }

    public void clear(){
        uploadItems.clear();
    }

    public void add(Upload upload){
        uploadItems.add(upload);
    }
}
