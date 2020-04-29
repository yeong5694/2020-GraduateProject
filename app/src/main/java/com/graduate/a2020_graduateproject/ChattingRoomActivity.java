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
import java.util.Date;

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

    // TOPIC
    static final String TAG = ChattingRoomActivity.class.getSimpleName();
    static String TOPIC;


    private ChatAdapter chatAdapter;
    private MqttClient mqttClient;

    private Toolbar toolbar;

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
        String currentTime;
        // 현재시간
        long now = System.currentTimeMillis();
        Date mDate = new Date(now);
        SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy년 MM월 dd일 hh시mm분");
        currentTime = simpleDate.format(mDate);
        if(content.equals("")){ }
        else{
            JSONObject json = new JSONObject();
            try{
                json.put("id",id);
                json.put("content",content);
                //
                json.put("thumnail", kakao_thumnail);
                json.put("time", currentTime);
                //
                mqttClient.publish(TOPIC,new MqttMessage(json.toString().getBytes()));
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
        //getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);

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



        TOPIC = selected_room_id + "/chatting"; // topic 설정

        chatAdapter = new ChatAdapter();
        chatListView = findViewById(R.id.chat_list);
        chatListView.setAdapter(chatAdapter);




        try{connectMqtt();}catch(Exception e){
            Log.d(TAG,"MqttConnect Error");
        }



    }

    private void connectMqtt() throws Exception{ // 192.168.43.149 // 192.168.219.103
        mqttClient = new MqttClient("tcp://192.168.43.149:1883", MqttClient.generateClientId(), null);
        mqttClient.connect();
        mqttClient.subscribe(TOPIC);
        mqttClient.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                Log.d(TAG,"Mqtt ReConnect");
                try{connectMqtt();}catch(Exception e){Log.d(TAG,"MqttReConnect Error");}
            }
            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                Log.e("chatting","messageArrived");
                JSONObject json = new JSONObject(new String(message.getPayload(), "UTF-8"));

                chatAdapter.add(new ChatItem(json.getString("id"), json.getString("content"), json.getString("thumnail"), json.getString("time")));
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        chatAdapter.notifyDataSetChanged();
                    }
                });
            }
            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
    }

    private void hideKeyboard()
    {
        imm.hideSoftInputFromWindow(chatEditText.getWindowToken(), 0);

    }




}
