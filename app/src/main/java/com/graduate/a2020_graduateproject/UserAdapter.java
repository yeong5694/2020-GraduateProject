package com.graduate.a2020_graduateproject;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UserAdapter  extends BaseAdapter {

    private ArrayList<User> userItems = new ArrayList<>();

    private boolean master = false;

    private String selected_room_id;

    private String master_id;

    public UserAdapter(String selected_room_id){
        this.selected_room_id = selected_room_id;
    }

    public void setMaster(boolean master){
        this.master = master;
    }


    public void add(User item){
        userItems.add(item);
    }

    public void clear(){
        userItems.clear();
    }


    @Override
    public int getCount() {
        return userItems.size();
    }

    @Override
    public Object getItem(int i) {
        return userItems.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView == null){
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_list,parent,false);
            viewHolder = new ViewHolder();
            viewHolder.idText = convertView.findViewById(R.id.idText);
            viewHolder.profile_image = convertView.findViewById(R.id.profile_image);
            viewHolder.remove_btn = convertView.findViewById(R.id.remove_btn);
            viewHolder.change_btn = convertView.findViewById(R.id.change_btn);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder)convertView.getTag();
        }
        viewHolder.idText.setText(userItems.get(position).getName());
        Glide.with(viewHolder.profile_image).load(userItems.get(position).getThumbnail())
                .error(R.drawable.kakao_default_profile_image).into(viewHolder.profile_image);
        viewHolder.remove_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("UserAdapter","click remove_btn");

                // 초대 받은 방 구성원에서 삭제
                DatabaseReference tripRoomRef2 = FirebaseDatabase.getInstance().getReference("sharing_trips/tripRoom_list")
                        .child(selected_room_id).child("invited_user_list").child(userItems.get(position).getId());
                tripRoomRef2.removeValue();


                // 선택된 구성원 방 목록에서 삭제
                DatabaseReference userRef2 = FirebaseDatabase.getInstance().getReference("sharing_trips/user_list")
                        .child(userItems.get(position).getId())
                        .child("/myRoomList").child(selected_room_id);
                userRef2.removeValue();

            }
        });
        viewHolder.change_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("UserAdapter", "click change_btn");



                // master 변경
                DatabaseReference masterRef = FirebaseDatabase.getInstance().getReference("sharing_trips/tripRoom_list")
                        .child(selected_room_id).child("master_id");
                masterRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        master_id = dataSnapshot.getValue().toString();
                        Log.e("UserAdapter", "master_id : "+ master_id);

                        // master_id 를 선택된 구성원 id 로 바꾸기
                        dataSnapshot.getRef().setValue(userItems.get(position).getId());

                        // 선택된 구성원 authority = master 로 변경
                        DatabaseReference selectedRef = FirebaseDatabase.getInstance().getReference("sharing_trips/user_list")
                                .child(userItems.get(position).getId())
                                .child("/myRoomList").child(selected_room_id).child("authority");
                        selectedRef.setValue("master");

                        // master authority = invited_user 로 변경
                        DatabaseReference masterRef2 = FirebaseDatabase.getInstance().getReference("sharing_trips/user_list")
                                .child(master_id)
                                .child("/myRoomList").child(selected_room_id).child("authority");
                        masterRef2.setValue("invited_user");

                        // 선택된 구성원 초대 목록에서 삭제
                        DatabaseReference invitedRef = FirebaseDatabase.getInstance().getReference("sharing_trips/tripRoom_list")
                                .child(selected_room_id).child("invited_user_list").child(userItems.get(position).getId());
                        invitedRef.removeValue(); // 선택된 구성원 삭제하고

                        // master 초대 목록에 추가
                        DatabaseReference tripRoomRef = FirebaseDatabase.getInstance().getReference("sharing_trips/tripRoom_list")
                                .child(selected_room_id).child("invited_user_list").child(master_id);
                        Map<String, Object> tripRoomUpdate = new HashMap<>();
                        tripRoomUpdate.put("/id", master_id);
                        tripRoomRef.updateChildren(tripRoomUpdate); // master를 구성원으로 추가
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });











            }
        });

        if(master == true){
            viewHolder.remove_btn.setVisibility(convertView.VISIBLE);
            viewHolder.change_btn.setVisibility(convertView.VISIBLE);
        }
        else{
            viewHolder.remove_btn.setVisibility(convertView.INVISIBLE);
            viewHolder.change_btn.setVisibility(convertView.INVISIBLE);
        }

        return convertView;
    }

    private class ViewHolder{
        TextView idText;
        ImageView profile_image;
        Button remove_btn;
        Button change_btn;

    }
}
