package com.graduate.a2020_graduateproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class PlaningActivity extends AppCompatActivity
implements  PlanAdapter.OnStartDragListener{

    // 로그인 정보
    private Long kakao_id;
    private String kakao_email;
    private String kakao_thumnail;
    private String kakao_name;
    // 선택한 여행방 정보
    private String selected_room_name; // 여행방 이름
    private String selected_room_id; // 여행방 id

    private static String TAG = "PlaningActivity";

    private Toolbar toolbar;

    private TextView schedule_txt;
    private TextView schedule_end_txt;
    private ImageView planEdit;
    private ImageView planEditFin;
    private ImageView add;
    private TextView description;

    private DatabaseReference scheduleRef;

    private RecyclerView planRecyclerView;
    private RecyclerView.Adapter planRecycleAdapter;
    private RecyclerView.LayoutManager planLayoutManager;

    private PlanAdapter planAdapter;
    private ItemTouchHelper mItemTouchHelper;
    private ArrayList<String> arrayIndex = new ArrayList<String>();

    Calendar fromCal = Calendar.getInstance();
    Calendar toCal = Calendar.getInstance();

    private DatabaseReference fromRef;
    private DatabaseReference toRef;


    DatePickerDialog.OnDateSetListener fromDatePicker = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            fromCal.set(Calendar.YEAR, year);
            fromCal.set(Calendar.MONTH, month);
            fromCal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            String myFormat = "yyyy년 MM월 dd일";    // 출력형식   2018/11/28
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.KOREA);

            DatabaseReference fromRef = FirebaseDatabase.getInstance().getReference("sharing_trips/tripRoom_list").child(selected_room_id)
                    .child("from");
            fromRef.setValue(sdf.format(fromCal.getTime()));

            //schedule_txt.setText(sdf.format(fromCal.getTime()));
        }

    };

    DatePickerDialog.OnDateSetListener toDatePicker = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            toCal.set(Calendar.YEAR, year);
            toCal.set(Calendar.MONTH, month);
            toCal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            String myFormat = "yyyy년 MM월 dd일";    // 출력형식   2018/11/28
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.KOREA);

            DatabaseReference toRef = FirebaseDatabase.getInstance().getReference("sharing_trips/tripRoom_list").child(selected_room_id)
                    .child("to");
            toRef.setValue(sdf.format(toCal.getTime()));

            //schedule_end_txt.setText(sdf.format(toCal.getTime()));
        }

    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_planing);

        Intent intent = getIntent();
        kakao_id = intent.getExtras().getLong("kakao_id");
        kakao_email = intent.getExtras().getString("kakao_email");
        kakao_name = intent.getExtras().getString("kakao_name");
        kakao_thumnail = intent.getExtras().getString("kakao_thumnail");
        selected_room_id = intent.getExtras().getString("selected_room_id");
        selected_room_name = intent.getExtras().getString("selected_room_name");
        Log.e("Intent info", selected_room_id);
        Log.e("Intent info", kakao_thumnail);

        toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        setTitle(selected_room_name + " 여행 일정");


        schedule_txt = findViewById(R.id.schedule_txt);
        schedule_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(PlaningActivity.this, fromDatePicker, fromCal.get(Calendar.YEAR),
                        fromCal.get(Calendar.MONTH), fromCal.get(Calendar.DAY_OF_MONTH)).show();

            }
        });
        schedule_end_txt = findViewById(R.id.schedule_end_txt);
        schedule_end_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(PlaningActivity.this, toDatePicker, toCal.get(Calendar.YEAR),
                        toCal.get(Calendar.MONTH), toCal.get(Calendar.DAY_OF_MONTH)).show();
            }

        });

        fromRef = FirebaseDatabase.getInstance().getReference("sharing_trips/tripRoom_list").child(selected_room_id).child("from");
        fromRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //dataSnapshot.getKey();
                if(dataSnapshot.getValue() != null){
                    Log.e("from",dataSnapshot.getValue().toString());
                    schedule_txt.setText(dataSnapshot.getValue().toString());
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        toRef = FirebaseDatabase.getInstance().getReference("sharing_trips/tripRoom_list").child(selected_room_id).child("to");
        toRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() != null){
                    Log.e("to",dataSnapshot.getValue().toString());
                    schedule_end_txt.setText(dataSnapshot.getValue().toString());
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        planRecyclerView = findViewById(R.id.planRecyclerView);
        //planAdapter = new PlanAdapter(selected_room_id);

        planAdapter = new PlanAdapter(selected_room_id,this,this);


        planLayoutManager = new LinearLayoutManager(this);
        planRecyclerView.setLayoutManager(planLayoutManager);
        planRecyclerView.setAdapter(planAdapter);

//        ItemTouchHelper.Callback callback =  new PlanItemTouchHelperCallback(planAdapter);
//        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
//        touchHelper.attachToRecyclerView(planRecyclerView);

        PlanItemTouchHelperCallback mCallback = new PlanItemTouchHelperCallback(planAdapter);
        mCallback.setMintem(false); // 드래그 안되게
        mCallback.setMintem2(true); // 스와핑 되게


        mItemTouchHelper = new ItemTouchHelper((mCallback));
        mItemTouchHelper.attachToRecyclerView(planRecyclerView);





        scheduleRef = FirebaseDatabase.getInstance().getReference("sharing_trips/tripRoom_list").child(selected_room_id)
                .child("schedule_list");
        scheduleRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                sort_list();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




        add = findViewById(R.id.planAdd);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("sharing_trips/tripRoom_list").child(selected_room_id)
                        .child("schedule_list");

                int d = planAdapter.getItemCount() +1;

                ref.push().setValue(new Schedule(Integer.toString(d)));
            }
        });
        description = findViewById(R.id.descriptionText);
        description.setText("밀어서 삭제");


        planEdit = findViewById(R.id.planEdit);
        planEditFin = findViewById(R.id.planEditFin);
        planEditFin.setVisibility(View.INVISIBLE);

        planEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                planEdit.setVisibility(View.INVISIBLE);
                planEditFin.setVisibility(View.VISIBLE);
                description.setText("드래그해서 순서변경");
                add.setVisibility(View.INVISIBLE);

                mCallback.setMintem(true); // 드래그 되게
                mCallback.setMintem2(false); // 스와핑 안되게
            }
        });
        planEditFin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(TAG,"plan edit fin");
                mCallback.setMintem(false); // 드래그 안되게
                mCallback.setMintem2(true); // 스와핑 되게
                planEdit.setVisibility(View.VISIBLE);
                planEditFin.setVisibility(View.INVISIBLE);
                add.setVisibility(View.VISIBLE);
                description.setText("밀어서 삭제");

                planAdapter.change();

                Toast.makeText(getApplicationContext(), "변경되었습니다", Toast.LENGTH_LONG).show();
            }
        });


    }



    public void sort_list(){
      scheduleRef.orderByChild("day").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Log.e(TAG,"day 기준으로 내림차순 정렬 -- ");

                planAdapter.clear();
                //arrayIndex.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    String key = snapshot.getKey();

                    String day =  snapshot.child("day").getValue().toString();


                    PlanItem planItem = new PlanItem(day,key);


                    //arrayIndex.add(key);
                    planAdapter.add(planItem);
                }


                planAdapter.notifyDataSetChanged();
                //planRecyclerView.setSelection(planAdapter.getCount()-1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    @Override
    public void onStartDrag(PlanViewHolder holder) {
        mItemTouchHelper.startDrag(holder);
    }
}
