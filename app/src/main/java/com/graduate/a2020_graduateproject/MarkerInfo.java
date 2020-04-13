package com.graduate.a2020_graduateproject;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.Map;

// Intent할 때 ArrayList<MarkerInfo> 전달하기 위해 Parcelable 상속
public class MarkerInfo implements Parcelable {
    double latitude; //위도
    double longitude; //경도
    String name; //장소이름



    public MarkerInfo(double latitude, double longitude, String name) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.name = name;
    }

    protected MarkerInfo(Parcel in) {
        latitude = in.readDouble();
        longitude = in.readDouble();
        name = in.readString();
    }

    public static final Creator<MarkerInfo> CREATOR = new Creator<MarkerInfo>() {
        @Override
        public MarkerInfo createFromParcel(Parcel in) {
            return new MarkerInfo(in);
        }

        @Override
        public MarkerInfo[] newArray(int size) {
            return new MarkerInfo[size];
        }
    };
/*
    public double getXpos() {
        return xpos;
    }

    public double getYpos() {
        return ypos;
    }

    public String getName() {
        return name;
    }

    public void setXpos(double xpos) {
        this.xpos = xpos;
    }

    public void setYpos(double ypos) {
        this.ypos = ypos;
    }

    public void setName(String name) {
        this.name = name;
    }
*/
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeString(name);
    }

    public Map<String, Object> toMap(){
        HashMap<String, Object> result=new HashMap<>();
        result.put("latitude", latitude);
        result.put("longitude", longitude);
        result.put("name", name);

        return result;
    }

}
