package com.graduate.a2020_graduateproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.util.List;

public class MapStartAdapter extends ArrayAdapter<MapInfoIndex>  {

    private List< MapInfoIndex> mapObjects;

    public MapStartAdapter(@NonNull Context context, int resource, @NonNull List<MapInfoIndex> objects) {
        super(context, resource, objects);
        this.mapObjects=objects;

    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.map_startadapter, parent, false
            );

            TextView address = convertView.findViewById(R.id.address);

            MapInfoIndex mapAddressItem = getItem(position);

            if (mapAddressItem != null) {
                address.setText(mapAddressItem.getName());
            }
        }
        return convertView;
    }

    @NonNull
    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.map_startadapter, parent, false
            );

            TextView address = convertView.findViewById(R.id.address);

            MapInfoIndex mapAddressItem = getItem(position);

            if (mapAddressItem != null) {
                address.setText(mapAddressItem.getName());
            }
        }
        return convertView;
    }

    @Override
    public  MapInfoIndex getItem(int position) {
        return  mapObjects.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
