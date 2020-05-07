package com.graduate.a2020_graduateproject;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
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

import java.lang.reflect.Array;
import java.util.ArrayList;

public class PlanAdapter extends BaseAdapter {

    private ArrayList<PlanListViewItem> planListViewItems = new ArrayList<PlanListViewItem>();
    private String selected_room_id;

    public PlanAdapter(String selected_room_id){
        this.selected_room_id = selected_room_id;

    }
    @Override
    public int getCount() {
        return planListViewItems.size();
    }

    @Override
    public Object getItem(int i) {
        return planListViewItems.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        mViewHolder viewHolder;
        if(convertView == null){
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.plan_list,parent,false);
            viewHolder = new mViewHolder();

            viewHolder.day_text = convertView.findViewById(R.id.day_text);
            viewHolder.remove_view = convertView.findViewById(R.id.remove_view);
            viewHolder.drag_view = convertView.findViewById(R.id.drag_view);


            convertView.setTag(viewHolder);
        }else{
            viewHolder = (mViewHolder)convertView.getTag();
        }
        viewHolder.day_text.setText("Day"+planListViewItems.get(position).getName());
        viewHolder.remove_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference removeRef = FirebaseDatabase.getInstance().getReference("sharing_trips/tripRoom_list")
                        .child(selected_room_id);
                Query rmQuery = removeRef.child("schedule_list").orderByChild("day").equalTo(planListViewItems.get(position).getName());
                rmQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                            snapshot.getRef().removeValue();
                        }
                        // 나머지 업데이트
                        sort_and_update(planListViewItems.get(position).getName());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("itemClick", viewHolder.day_text.getText().toString());



                Intent intent = new Intent(parent.getContext(), MapActivity.class);
                intent.putExtra("selected_room_id", selected_room_id);
                intent.putExtra("day", viewHolder.day_text.getText().toString());

                parent.getContext().startActivity(intent);
            }
        });
//        convertView.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View view) {
//
//
//
//                return false;
//            }
//        });



        return convertView;

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

    public void add(PlanListViewItem item){

        planListViewItems.add(item);

    }

    public void clear(){
        planListViewItems.clear();
    }

    private class mViewHolder{
        TextView day_text;
        //ImageButton edit_index;
        ImageView remove_view;
        ImageView drag_view;



    }


}
