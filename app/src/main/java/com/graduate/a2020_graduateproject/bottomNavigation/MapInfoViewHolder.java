package com.graduate.a2020_graduateproject.bottomNavigation;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.graduate.a2020_graduateproject.R;

public class MapInfoViewHolder extends RecyclerView.ViewHolder {

    private TextView place_text;
    private View divider;

    public MapInfoViewHolder(@NonNull View itemView) {
        super(itemView);

        place_text = itemView.findViewById(R.id.place_text);
        divider = itemView.findViewById(R.id.divider);
    }

    public void setPlace_text(String text){
        this.place_text.setText(text);
    }


}
