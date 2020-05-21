package com.graduate.a2020_graduateproject;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

public class MarkerAdapter extends BaseAdapter {
    private ArrayList<MarkerInfo> markerList=new ArrayList<>();

    public void add(MarkerInfo marker){
        markerList.add(marker);
    }

    @Override
    public int getCount() {
        return markerList.size();
    }

    @Override
    public Object getItem(int position) {
        return markerList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }
}
