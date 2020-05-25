package com.graduate.a2020_graduateproject;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MapViewHolder  extends RecyclerView.ViewHolder{

    public TextView auto_name;
    public TextView auto_subName;

    //public View mapdivider;

    public MapViewHolder(@NonNull View itemView, Context parent) {
        super(itemView);

        auto_name=itemView.findViewById(R.id.auto_name);
        auto_subName=itemView.findViewById(R.id.auto_subName);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("onClick latlng !!!");
            }
        });


    }
}
