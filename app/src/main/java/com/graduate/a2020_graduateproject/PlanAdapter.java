package com.graduate.a2020_graduateproject;

import android.content.Context;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class PlanAdapter extends BaseAdapter {

    private ArrayList<PlanListViewItem> planListViewItems = new ArrayList<PlanListViewItem>();

    public PlanAdapter(){


    }
    @Override
    public int getCount() {
        return planListViewItems.size();
    }

    @Override
    public Object getItem(int i) {
        return planListViewItems.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        final int pos = i;
        final Context context = viewGroup.getContext();

        // "listview_item" Layout을 inflate하여 convertView 참조 획득.
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.plan_list, viewGroup, false);
        }

        TextView  day_text = view.findViewById(R.id.day_text);

        ImageButton edit_index = view.findViewById(R.id.edit_index);
        edit_index.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("button", day_text.toString());
            }
        });

        PlanListViewItem item = planListViewItems.get(i);

        day_text.setText("Day"+item.getName());
        return view;
    }

    public void add(PlanListViewItem item){

        planListViewItems.add(item);

    }

    public void clear(){
        planListViewItems.clear();
    }
}
