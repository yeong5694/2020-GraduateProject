package com.graduate.a2020_graduateproject;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class PlanAdapter extends RecyclerView.Adapter<PlanViewHolder>
        implements PlanItemTouchHelperCallback.ItemTouchHelperAdapter {

    public interface OnStartDragListener{
        void  onStartDrag(PlanViewHolder holder);
    }

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
        return new PlanViewHolder(v, parent.getContext(), selected_room_id);
    }


    @Override
    public void onBindViewHolder(@NonNull PlanViewHolder holder, int position) {

        PlanItem item = planItems.get(position);
        holder.day_text.setText("Day" + item.getDay());



    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public int getItemCount() {
        return planItems.size();
    }



    public void sort_and_update(){



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


    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(planItems, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(planItems, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);


      return true;
    }

    @Override
    public void onItemDismiss(int position) {

        //planItems.remove(position);

        Log.e("PlanAdapter","remove day:"+planItems.get(position).getDay());

        DatabaseReference removeRef = FirebaseDatabase.getInstance().getReference("sharing_trips/tripRoom_list")
                .child(selected_room_id);
        Query rmQuery = removeRef.child("schedule_list").orderByChild("day").equalTo(planItems.get(position).getDay());
        rmQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    snapshot.getRef().removeValue(); // 파이어베이스에서 삭제
                }
                // 나머지 업데이트
                sort_and_update();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        notifyItemRemoved(position);
    }



}
