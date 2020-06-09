package com.graduate.a2020_graduateproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.graduate.a2020_graduateproject.databinding.CalendarListBinding;
import com.graduate.a2020_graduateproject.ui.adapter.CalendarAdapter;
import com.graduate.a2020_graduateproject.ui.viewmodel.CalendarListViewModel;

import java.util.ArrayList;

public class MyCalendarTripActivity extends AppCompatActivity {
    private CalendarListBinding binding;
    private CalendarAdapter calendarAdapter;
    private String selected_room_id="";
    //public static String room_id;
    private DatabaseReference tripIdReference ;

    //일정 날짜 변수
    int trip_year_from;
    int trip_month_from;
    int trip_day_from;
    int trip_year_to;
    int trip_month_to;
    int trip_day_to;

    String trip_from="";
    String trip_to="";

    public static int changed_from_day;
    public static int changed_to_day;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        Intent intent = getIntent();
        //kakao_id = intent.getExtras().getLong("kakao_id");
        selected_room_id = intent.getExtras().getString("selected_room_id");
        //selected_room_name = intent.getExtras().getString("selected_room_name");

        CalendarListViewModel.room_id = selected_room_id;

        //- firebase
        tripIdReference  = FirebaseDatabase.getInstance().getReference("sharing_trips/tripRoom_list").child(selected_room_id);
        tripIdReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.e("cc:","."+selected_room_id+".");
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if(snapshot.getKey().equals("from")){
                        trip_from = snapshot.getValue().toString();
                        Log.e("cc:","from."+trip_from+".");
                    }
                    if(snapshot.getKey().equals("to")){
                        trip_to = snapshot.getValue().toString();
                        Log.e("cc:","to."+trip_to+".");
                    }

                }

                trip_year_from=Integer.parseInt(trip_from.substring(0,4));
                trip_month_from=Integer.parseInt(trip_from.substring(6,8));
                trip_day_from=Integer.parseInt(trip_from.substring(10,12));
                Log.e("day",Integer.toString(trip_day_from));
                trip_year_to=Integer.parseInt(trip_to.substring(0,4));
                trip_month_to=Integer.parseInt(trip_to.substring(6,8));
                trip_day_to=Integer.parseInt(trip_to.substring(10,12));
                Log.e("day",Integer.toString(trip_day_to));

                changed_from_day = trip_day_from;
                changed_to_day = trip_day_to;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { //실패
                Log.e("day","실패");
                trip_year_from = 0;
                trip_month_from=0;
                trip_day_from=0;
                trip_year_to=0;
                trip_month_to=0;
                trip_day_to=0;
            }
        });
        //


        binding = DataBindingUtil.setContentView(this, R.layout.activity_calendar);
        binding.setVariable(BR.model, new ViewModelProvider(this).get(CalendarListViewModel.class));
        binding.setLifecycleOwner(this);

        binding.getModel().initCalendarList();

        StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(7, StaggeredGridLayoutManager.VERTICAL);
        calendarAdapter = new CalendarAdapter();
        binding.pagerCalendar.setLayoutManager(manager);
        binding.pagerCalendar.setAdapter(calendarAdapter);
        observe();



    }

    private void observe() {
        binding.getModel().mCalendarList.observe(this, new Observer<ArrayList<Object>>() {
            @Override
            public void onChanged(ArrayList<Object> objects) {
                calendarAdapter.submitList(objects);
                if (binding.getModel().mCenterPosition > 0) {
                    binding.pagerCalendar.scrollToPosition(binding.getModel().mCenterPosition);
                }
            }
        });
    }
}