package com.graduate.a2020_graduateproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class MapBasicAdapter extends ArrayAdapter<String> {

    private List<String> itemList;

    public MapBasicAdapter(@NonNull Context context, int resource, @NonNull List<String> objects) {
        super(context, resource, objects);
        this.itemList=objects;

    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.map_basicspinner, parent, false
            );

            TextView address = convertView.findViewById(R.id.itemView);

           String item = itemList.get(position);

            if (item != null) {
                address.setText(item);
            }
        }
        return convertView;
    }

    @NonNull
    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.map_basicspinner, parent, false
            );

            TextView item = convertView.findViewById(R.id.itemView);
                item.setText(itemList.get(position));
        }
        return convertView;
    }

}
