package com.graduate.a2020_graduateproject.memo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.graduate.a2020_graduateproject.ChattingRoomActivity;
import com.graduate.a2020_graduateproject.R;
import com.graduate.a2020_graduateproject.TripRoomActivity;

import butterknife.BindView;

public class memoActivity extends AppCompatActivity {

    // 로그인 정보
    private Long kakao_id;
    private String kakao_email;
    private String kakao_thumnail;
    private String kakao_name;
    // 선택한 여행방 정보
    private String selected_room_name; // 여행방 이름
    private String selected_room_id; // 여행방 id

    private Toolbar toolbar;
    private FloatingActionButton edit_btn;

    static final String TAG = memoActivity.class.getSimpleName();

    private memoAdapter memoAdapter;
    private RecyclerView memoRecyclerView;
    private RecyclerView.LayoutManager memoLayoutManager;

    private DatabaseReference memoRef;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo);

        Intent intent = getIntent();
        kakao_id = intent.getExtras().getLong("kakao_id");
        kakao_email = intent.getExtras().getString("kakao_email");
        kakao_name = intent.getExtras().getString("kakao_name");
        kakao_thumnail = intent.getExtras().getString("kakao_thumnail");
        selected_room_id = intent.getExtras().getString("selected_room_id");
        selected_room_name = intent.getExtras().getString("selected_room_name");

        toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼
        setTitle(selected_room_name + " 메모");

        memoRecyclerView = findViewById(R.id.memoRecyclerView);
        memoAdapter = new memoAdapter();
        memoLayoutManager = new LinearLayoutManager(this);
        memoRecyclerView.setLayoutManager(memoLayoutManager);
        memoRecyclerView.setAdapter(memoAdapter);

        edit_btn = findViewById(R.id.edit_btn);
        edit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(memoActivity.this, memoAddActivity.class);
                intent.putExtra("kakao_id", kakao_id);
                intent.putExtra("kakao_email", kakao_email);
                intent.putExtra("kakao_name", kakao_name);
                intent.putExtra("kakao_thumnail", kakao_thumnail);
                intent.putExtra("selected_room_name", selected_room_name);
                intent.putExtra("selected_room_id", selected_room_id);
                startActivity(intent);
            }
        });

        memoRef = FirebaseDatabase.getInstance().getReference("sharing_trips/tripRoom_list")
                .child(selected_room_id).child("memo_list");
        memoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                memoAdapter.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    String key = snapshot.getKey();
                    System.out.println("memo key : " + key);

                    String id =  snapshot.child("id").getValue().toString();
                    String content = snapshot.child("content").getValue().toString();
                    String thumnail = snapshot.child("thumnail").getValue().toString();
                    String time = snapshot.child("time").getValue().toString();

                    memoItem newItem = new memoItem(id,  content, thumnail, time);

                    memoAdapter.add(newItem);

                }

                memoAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "memo list 가져오지 못함");

            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{ // toolbar의 back 키 눌렀을 때 동작
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
