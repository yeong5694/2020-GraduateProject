package com.graduate.a2020_graduateproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

//extends RecyclerView.Adapter<MapViewHolder>
public class MapPlaceAutoSuggestAdapter extends ArrayAdapter<MapAddressItem> implements Filterable {


    TMapAdressParser TMapAdressParser =new TMapAdressParser();

    public MapPlaceAutoSuggestAdapter(@NonNull Context context, int resource) {
        super(context, resource);
    }

    /*public MapPlaceAutoSuggestAdapter(List<MapAddressItem> mapList){
       this.mapList=mapList;
       mapListFull=new ArrayList<>(mapList);

    }*/


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView==null){
            convertView= LayoutInflater.from(getContext()).inflate(
                    R.layout.map_autocomplete_row, parent, false
            );
        }

        TextView auto_name=convertView.findViewById(R.id.auto_name);
        TextView auto_subName=convertView.findViewById(R.id.auto_subName);

        MapAddressItem mapAddressItem=getItem(position);

        if(mapAddressItem!=null){
            auto_name.setText(mapAddressItem.getName());
            auto_subName.setText(mapAddressItem.getSub_name());
        }
        return convertView;
    }

    @Override
    public Filter getFilter(){
        return mapFilter;
    }

    Filter mapFilter=new Filter(){
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
               List<MapAddressItem> filterResults=new ArrayList<>();
              //  FilterResults filterResults=new FilterResults();

           //     if(constraint==null||constraint.length()==0){
             //       filterResults.addAll(mapListFull);
               // }
               // else{
                 if(constraint!=null){
                    filterResults= TMapAdressParser.autoComplete(constraint.toString());


                    for(MapAddressItem item:filterResults){
                        System.out.println("Filter name : "+item.getName());
                        System.out.println("SubName : "+item.getSub_name());
                        }

                    }


                FilterResults results=new FilterResults();
                results.values=filterResults;
                results.count=filterResults.size();

                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

                clear();
                addAll((List)results.values);
                notifyDataSetChanged();
            }

        @Override
        public CharSequence convertResultToString(Object resultValue) {
            return ((MapAddressItem)resultValue).getName();
        }


    };




    /*@NonNull
    @Override
    public MapViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull MapViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return mapList.size();
    }*/
}
