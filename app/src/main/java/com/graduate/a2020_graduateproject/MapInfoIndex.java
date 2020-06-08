package com.graduate.a2020_graduateproject;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.Map;

public class MapInfoIndex implements Parcelable{

        double latitude; //위도

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getName() {
        return name;
    }

    public int getIndex() {
        return index;
    }

    double longitude; //경도
        String name; //장소이름
        int index;



        public MapInfoIndex(double latitude, double longitude, String name, int index) {
            this.latitude = latitude;
            this.longitude = longitude;
            this.name = name;
            this.index=index;
        }

        protected MapInfoIndex(Parcel in) {
            latitude = in.readDouble();
            longitude = in.readDouble();
            name = in.readString();
            index=in.readInt();
        }

        public static final Parcelable.Creator<MapInfoIndex> CREATOR = new Parcelable.Creator<MapInfoIndex>() {
            @Override
            public MapInfoIndex createFromParcel(Parcel in) {
                return new MapInfoIndex(in);
            }

            @Override
            public MapInfoIndex[] newArray(int size) {
                return new MapInfoIndex[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeDouble(latitude);
            dest.writeDouble(longitude);
            dest.writeString(name);
            dest.writeInt(index);
        }

        public Map<String, Object> toMap(){
            HashMap<String, Object> result=new HashMap<>();
            result.put("latitude", latitude);
            result.put("longitude", longitude);
            result.put("name", name);
            result.put("index", index);

            return result;
        }


    }
