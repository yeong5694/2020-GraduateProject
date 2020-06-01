package com.graduate.a2020_graduateproject.bottomNavigation;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.graduate.a2020_graduateproject.BottomViewActivity;
import com.graduate.a2020_graduateproject.PlanAdapter;
import com.graduate.a2020_graduateproject.PlanItem;
import com.graduate.a2020_graduateproject.PlanItemTouchHelperCallback;
import com.graduate.a2020_graduateproject.R;

import java.util.ArrayList;

public class FragmentDay extends Fragment {

///////// gggg
    private Toolbar toolbar;

    private String day;
    private String selected_room_id;
    private TextView descriptionText;

    private FloatingActionButton edit_btn;
    private FloatingActionButton editFin_btn;

    private RecyclerView mapRecyclerView;
    private RecyclerView.LayoutManager mapLayoutManager;
    private MapInfoAdapter mapAdapter;
    private ItemTouchHelper mItemTouchHelper;

    private DatabaseReference mapDataReference;
    private String Mapkey;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //View view = inflater.inflate(R.layout.fragment_day, container, false);

        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_day, container, false);

        TextView day_textview = viewGroup.findViewById(R.id.day_textview);

        toolbar = viewGroup.findViewById(R.id.main_toolbar);
        toolbar.setTitle("일정 상세 조회");

        selected_room_id = ((BottomViewActivity) getActivity()).getSelected_room_id();
        day = ((BottomViewActivity) getActivity()).getDay();

        day_textview.setText("Day"+day);

        descriptionText = viewGroup.findViewById(R.id.descriptionText);
        descriptionText.setVisibility(View.INVISIBLE); //

        mapRecyclerView = viewGroup.findViewById(R.id.mapRecyclerView);
        mapAdapter = new MapInfoAdapter(selected_room_id);
        mapLayoutManager = new LinearLayoutManager(getContext());
        mapRecyclerView.setLayoutManager(mapLayoutManager);
        mapRecyclerView.setAdapter(mapAdapter);

        MapInfoItemTouchHelperCallback mCallback = new MapInfoItemTouchHelperCallback(mapAdapter);
        mCallback.setMintem(false); // 드래그 안되게

        mItemTouchHelper = new ItemTouchHelper((mCallback));
        mItemTouchHelper.attachToRecyclerView(mapRecyclerView);

        edit_btn = viewGroup.findViewById(R.id.edit_btn);
        editFin_btn = viewGroup.findViewById(R.id.editfin_btn);

        edit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edit_btn.setVisibility(View.GONE); // 안보이게
                editFin_btn.setVisibility(View.VISIBLE);

                mCallback.setMintem(true); // 드래그 활성화

            }
        });

        editFin_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edit_btn.setVisibility(View.VISIBLE);
                editFin_btn.setVisibility(View.GONE);

                mCallback.setMintem(false); // 드래그 락

                change();

            }
        });


        mapDataReference = FirebaseDatabase.getInstance().getReference("sharing_trips/tripRoom_list").child(selected_room_id)
                .child("schedule_list");
        mapDataReference.orderByChild("day").equalTo(day).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Mapkey = snapshot.getKey();
                    System.out.println("Mapkey : " + Mapkey);

                }

                DatabaseReference mapRef = mapDataReference.child(Mapkey).child("map_info");
                mapRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        mapAdapter.clear();

                        sort_list();

                        if(mapAdapter.getItemCount() == 0){
                            descriptionText.setVisibility(View.VISIBLE);
                            descriptionText.setText("일정이 없습니다.");
                        }
                        else{
                            descriptionText.setVisibility(View.INVISIBLE);
                        }

                        mapAdapter.notifyDataSetChanged();


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


        return viewGroup;
    }

    public void change(){
        Log.e("FragmentDay", "수정 완료 버튼 클릭, 순서변경");
        if(mapAdapter.getItemCount() != 0){
            mapAdapter.change(mapDataReference,Mapkey);
            Toast.makeText(getContext(), "변경되었습니다", Toast.LENGTH_LONG).show();
        }
        else{
            Toast.makeText(getContext(), "일정이 없습니다", Toast.LENGTH_LONG).show();
        }



    }

    public void sort_list(){

        mapDataReference.child(Mapkey).child("map_info").orderByChild("index").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                mapAdapter.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String mapInfoKey = snapshot.getKey();
                    System.out.println("mapInfokey : " + mapInfoKey);

                    String index =  snapshot.child("index").getValue().toString();
                    String latitude = snapshot.child("latitude").getValue().toString();
                    String longitude = snapshot.child("longitude").getValue().toString();
                    String name = snapshot.child("name").getValue().toString();

                    MapInfoItem newItem = new MapInfoItem(mapInfoKey, index, latitude, longitude, name);
                    //System.out.println(newItem);

                    mapAdapter.add(newItem);

                }


                mapAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

}
