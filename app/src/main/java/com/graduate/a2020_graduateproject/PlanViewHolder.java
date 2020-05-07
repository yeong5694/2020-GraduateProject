package com.graduate.a2020_graduateproject;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


public class PlanViewHolder extends RecyclerView.ViewHolder {

    public TextView  day_text;
    public ImageView remove_view;
    public ImageView drag_view;

    public PlanViewHolder(@NonNull View itemView) {
        super(itemView);
        day_text = itemView.findViewById(R.id.day_text);
        remove_view = itemView.findViewById(R.id.remove_view);
        drag_view = itemView.findViewById(R.id.drag_view);
    }
}
