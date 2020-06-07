package com.graduate.a2020_graduateproject.bottomNavigation;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.graduate.a2020_graduateproject.BottomViewActivity;
import com.graduate.a2020_graduateproject.ChatAdapter;
import com.graduate.a2020_graduateproject.ChatItem;
import com.graduate.a2020_graduateproject.ChattingRoomActivity;
import com.graduate.a2020_graduateproject.R;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;

public class FragmentChat  extends Fragment {

    private ChatAdapter chatAdapter;
    private ListView chatListView;
    private EditText chatEditText;
    private Button chatSendBtn;

    private String selected_room_id;
    // 로그인 정보
    private Long kakao_id;
    private String kakao_email;
    private String kakao_thumnail;
    private String kakao_name;

    private Toolbar toolbar;

    InputMethodManager imm;

    private DatabaseReference chatRef;
    static final String TAG = FragmentChat.class.getSimpleName();

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.activity_chatting_room, container, false);


        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE|WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);


        chatListView = viewGroup.findViewById(R.id.chat_list);
        chatEditText = viewGroup.findViewById(R.id.chatEditText);
        chatSendBtn = viewGroup.findViewById(R.id.chatSendButton);
        chatSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendChat();
            }
        });

        selected_room_id = ((BottomViewActivity) getActivity()).getSelected_room_id();
        kakao_id = ((BottomViewActivity) getActivity()).getKakao_id();
        kakao_email= ((BottomViewActivity) getActivity()).getKakao_email();
        kakao_thumnail=((BottomViewActivity) getActivity()).getKakao_thumnail();
        kakao_name = ((BottomViewActivity) getActivity()).getKakao_name();

        toolbar = viewGroup.findViewById(R.id.main_toolbar);
        toolbar.setTitle("채팅");

        imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        chatAdapter = new ChatAdapter();
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
                Log.w(TAG,"채팅 가져오지 못함", databaseError.toException());
            }
        });


        return viewGroup;
    }

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

    private void hideKeyboard()
    {
        imm.hideSoftInputFromWindow(chatEditText.getWindowToken(), 0);

    }
}
