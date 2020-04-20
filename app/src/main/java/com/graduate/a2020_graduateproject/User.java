package com.graduate.a2020_graduateproject;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class User {

    public Long id;
    public String name;
    public String email;
    public String thumbnail;
    public String tripRoom_list;

    //private FirebaseDatabase mPostReference;

    public User (Long id, String name, String email, String thumbnail){

        this.id = id;
        this.name = name;
        this.email = email;
        this.thumbnail = thumbnail;

    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", id);
        result.put("name", name);
        result.put("email", email);
        result.put("thumbnail", thumbnail);
        return result;
    }


}
