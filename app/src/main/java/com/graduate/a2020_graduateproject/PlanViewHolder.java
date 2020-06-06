package com.graduate.a2020_graduateproject;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


public class PlanViewHolder extends RecyclerView.ViewHolder {

    public TextView  day_text;
    public ImageView drag_view;

    public PlanViewHolder(@NonNull View itemView, Context parent, String selected_room_id) {
        super(itemView);
        day_text = itemView.findViewById(R.id.day_text);

        drag_view = itemView.findViewById(R.id.drag_view);


        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ////////////////// BottomNavigationVIew로 이동
                Intent intent = new Intent(parent, BottomViewActivity.class);
                intent.putExtra("selected_room_id", selected_room_id);

                String day = day_text.getText().toString();
                String[] days = day.split("Day");
                day = days[1];

                intent.putExtra("day", day);

                parent.startActivity(intent);
            }
        });




    }
}
