package com.graduate.a2020_graduateproject;

public class PlanItem {

    private String day;
    private String key;


    public PlanItem(String day, String key){
        this.day = day;
        this.key = key;
    }
    public void setDay(String day){
        this.day = day;
    }
    public String getDay() {
        return day;
    }
    public String getKey(){ return key;}
}
