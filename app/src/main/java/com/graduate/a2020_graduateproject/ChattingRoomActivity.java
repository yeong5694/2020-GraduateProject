package com.graduate.a2020_graduateproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChattingRoomActivity extends AppCompatActivity {

    // 로그인 정보
    private Long kakao_id;
    private String kakao_email;
    private String kakao_thumnail;
    private String kakao_name;
    // 선택한 여행방 정보
    private String selected_room_name; // 여행방 이름
    private String selected_room_id; // 여행방 id

    static final String TAG = ChattingRoomActivity.class.getSimpleName();


    private ChatAdapter chatAdapter;
    private MqttClient mqttClient;

    private Toolbar toolbar;
    private DatabaseReference chatRef;


    InputMethodManager imm;



    @BindView(R.id.chat_list)
    ListView chatListView;

    @BindView(R.id.chatEditText)
    EditText chatEditText;

    @OnClick(R.id.chatSendButton)
    public void sendChat(){

        hideKeyboard();

        String id = kakao_name;
        String content = chatEditText.getText().toString();
        String thumnail = kakao_thumnail;
        String currentTime;

        // 현재시간
        long now = System.currentTimeMillis();
        Date mDate = new Date(now);
        SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy년 MM월 dd일 hh시mm분");
        currentTime = simpleDate.format(mDate);

        if(content.equals("")){ }
        else{

            try{

                DatabaseReference sendRef = FirebaseDatabase.getInstance().getReference("sharing_trips/chat_list").child(selected_room_id);
                sendRef.push().setValue(new ChatItem(id, content, thumnail, currentTime));

            }catch (Exception e){}

            chatEditText.setText("");
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatting_room);

        ButterKnife.bind(this);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE|WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);


        Intent intent = getIntent();
        kakao_id = intent.getExtras().getLong("kakao_id");
        kakao_email = intent.getExtras().getString("kakao_email");
        kakao_name = intent.getExtras().getString("kakao_name");
        kakao_thumnail = intent.getExtras().getString("kakao_thumnail");
        selected_room_id = intent.getExtras().getString("selected_room_id");
        selected_room_name = intent.getExtras().getString("selected_room_name");
        Log.e("Intent info",selected_room_id);
        Log.e("Intent info",kakao_thumnail);

        toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        setTitle(selected_room_name+ " 채팅방");

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);


        chatAdapter = new ChatAdapter();
        chatListView = findViewById(R.id.chat_list);
        chatListView.setAdapter(chatAdapter);

        chatRef = FirebaseDatabase.getInstance().getReference("sharing_trips/chat_list").child(selected_room_id);
        //mReference = databaseReference.child("/tripRoom_list/");
        chatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                chatAdapter.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    // snapshot 내에 있는 데이터만큼 반복합니다.
                    String key = snapshot.getKey();
                    Log.e(TAG, "key: "+key); // 채팅 id


                    String send_id =  snapshot.child("id").getValue().toString();
                    String content = snapshot.child("content").getValue().toString();
                    String thumnail = snapshot.child("thumnail").getValue().toString();
                    String time = snapshot.child("time").getValue().toString();

                    ChatItem chatItem = new ChatItem(send_id, content, thumnail, time);


                    chatAdapter.add(chatItem);

                }


                chatAdapter.notifyDataSetChanged();
                chatListView.setSelection(chatAdapter.getCount()-1);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG,"loadPost:onCancelled", databaseError.toException());
            }
        });







    }




    private void hideKeyboard()
    {
        imm.hideSoftInputFromWindow(chatEditText.getWindowToken(), 0);

    }




}
