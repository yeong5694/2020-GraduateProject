package com.graduate.a2020_graduateproject.bottomNavigation;

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
import com.graduate.a2020_graduateproject.PlanItem;
import com.graduate.a2020_graduateproject.PlanViewHolder;
import com.graduate.a2020_graduateproject.R;

import java.util.ArrayList;
import java.util.Collections;

public class MapInfoAdapter extends RecyclerView.Adapter<MapInfoViewHolder>
        implements MapInfoItemTouchHelperCallback.onItemMoveListener {

    private String selected_room_id;
    private String day;
    private String Mapkey;
    private ArrayList<MapInfoItem> mapInfoItems = new ArrayList<>();

    public MapInfoAdapter(String selected_room_id, String day){
        this.selected_room_id = selected_room_id;
        this.day = day;
    }

    @NonNull
    @Override
    public MapInfoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(
                parent.getContext()
        ).inflate(R.layout.map_info_list, parent, false);
        return new MapInfoViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MapInfoViewHolder holder, int position) {

        MapInfoItem item = mapInfoItems.get(position);
        holder.setPlace_text(item.getName()); // 나중에 name 집어넣기


    }

    @Override
    public int getItemCount() {
        return mapInfoItems.size();
    }

    public void add(MapInfoItem newItem){
        mapInfoItems.add(newItem);
    }

    public void clear(){
        mapInfoItems.clear();
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(mapInfoItems, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(mapInfoItems, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);


        return true;
    }

    @Override
    public void onItemDismiss(int position) {

        DatabaseReference rmRef = FirebaseDatabase.getInstance().getReference("sharing_trips/tripRoom_list").child(selected_room_id)
                .child("schedule_list");
        rmRef.orderByChild("day").equalTo(day).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Mapkey = snapshot.getKey();
                    System.out.println("mapinfoadatper day key : " + Mapkey);

                }

                Query rmquery = rmRef.child(Mapkey).child("map_info").orderByChild("index").equalTo(mapInfoItems.get(position).getIndex());
                rmquery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            String rmkey= snapshot.getKey();
                            System.out.println("rmkey : " + rmkey);

                            snapshot.getRef().removeValue(); // firebase에서 삭제

                        }

                        sort_and_update(rmRef, Mapkey);






                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        System.out.println("마커정보를 가져오지 못함");
                    }
                });

            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("파이어베이스에서 해당 day 키값 못찾음");
            }
        });



        return;
    }

    public void sort_and_update(DatabaseReference ref, String key){

        Query idxquery = ref.child(key).child("map_info").orderByChild("index");
        idxquery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int i=1;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String mapInfoKey = snapshot.getKey();
                    System.out.println("sort_and_update map info key : " + mapInfoKey);

                    snapshot.child("index").getRef().setValue(Integer.toString(i));
                    i++;


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    public void change(DatabaseReference ref, String day){

        ArrayList<MapInfoItem> tempItems = new ArrayList<MapInfoItem>();

        int idx = 1;
        for(MapInfoItem item : mapInfoItems ){

            Log.e("item key ",item.getKey());
            Log.e("item index ", item.getIndex());
            item.setIndex(Integer.toString(idx));
            tempItems.add(item);
            Log.e("temp item key ",item.getKey());
            Log.e("temp item day ", item.getIndex());
            idx++;

        }

       ref.child(day).child("map_info").orderByChild("index").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            String key = snapshot.getKey();
                            System.out.println("marker : " + key);


                            for(MapInfoItem item : tempItems) {
                                if (item.getKey().equals(snapshot.getKey())) {
                                    snapshot.child("index").getRef().setValue(item.getIndex());

                                    break;
                                }
                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


    }
}
