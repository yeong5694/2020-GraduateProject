package com.graduate.a2020_graduateproject;

import com.google.firebase.database.Exclude;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

public class TripRoom {

    public String id;
    public String name;
    public Long master_id;
    //public String simpleDate;


    public TripRoom(String id, Long master_id,String name){
        this.id = id;
        this.master_id = master_id;
        this.name = name;
        //this.simpleDate = simpleDate;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", id);
        result.put("master_id", master_id);
        result.put("name", name);
        //result.put("updated_time", simpleDate);
        return result;
    }

    public String getName(){
        return name;
    }
    public String getId(){
        return id;
    }
    public Long getMaster_id(){
        return master_id;
    }

}
