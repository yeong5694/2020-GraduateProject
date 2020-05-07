package com.graduate.a2020_graduateproject;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PlanAdapter extends RecyclerView.Adapter<PlanViewHolder> {

    private ArrayList<PlanItem> planItems = new ArrayList<PlanItem>();
    private String selected_room_id;

    public PlanAdapter(String selected_room_id){
        this.selected_room_id = selected_room_id;

    }


    @NonNull
    @Override
    public PlanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(
                parent.getContext()
        ).inflate(R.layout.plan_list, parent, false);
        return new PlanViewHolder(v);
    }


    @Override
    public void onBindViewHolder(@NonNull PlanViewHolder holder, int position) {

        PlanItem item = planItems.get(position);
        holder.day_text.setText("Day" + item.getDay()  + item.getKey());

    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public int getItemCount() {
        return planItems.size();
    }



    public void sort_and_update(String rm_day){



        DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference("sharing_trips/tripRoom_list").child(selected_room_id)
                .child("schedule_list");
        orderRef.orderByChild("day").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                int i=1;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {


                    Log.e("key",snapshot.getKey() );

                    //String beforeDay = snapshot.child("day").getValue().toString();
                    snapshot.child("day").getRef().setValue(Integer.toString(i));


                    i++;

                    //Log.e("before",beforeDay+" --> "+snapshot.child("day").getValue().toString() );


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    public void add(PlanItem item){

        planItems.add(item);
        //notifyDataSetChanged();

    }

    public void clear(){
        planItems.clear();

    }





}
