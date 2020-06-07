package com.graduate.a2020_graduateproject.memo;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.graduate.a2020_graduateproject.R;

public class memoViewHolder extends RecyclerView.ViewHolder {

    TextView id;
    TextView content;
    ImageView thumnail;
    TextView time;
    View divider2;

    public memoViewHolder(@NonNull View itemView) {
        super(itemView);

        id = itemView.findViewById(R.id.idText);
        content = itemView.findViewById(R.id.content);
        thumnail = itemView.findViewById(R.id.profile_image);
        time = itemView.findViewById(R.id.time);
        divider2 = itemView.findViewById(R.id.divider2);
    }

    public void setId(String id) {this.id.setText(id);}
    public void setContent(String content){this.content.setText(content);}
    public  void setThumnail(String thumnail){
        Glide.with(this.thumnail).load(thumnail).error(R.drawable.kakao_default_profile_image).into(this.thumnail);

    }
    public void setTime(String time){
        this.time.setText(time);
    }
}
