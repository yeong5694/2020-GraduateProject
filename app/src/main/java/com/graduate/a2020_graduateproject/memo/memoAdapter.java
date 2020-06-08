package com.graduate.a2020_graduateproject.memo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.graduate.a2020_graduateproject.R;
import com.graduate.a2020_graduateproject.bottomNavigation.MapInfoItem;
import com.graduate.a2020_graduateproject.bottomNavigation.MapInfoViewHolder;

import java.util.ArrayList;

public class memoAdapter extends RecyclerView.Adapter<memoViewHolder> {

    private ArrayList<memoItem> memoItems = new ArrayList<>();

    public void add(memoItem item) {
        memoItems.add(item);
    }

    public void clear(){
        memoItems.clear();
    }

    @NonNull
    @Override
    public memoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(
                parent.getContext()
        ).inflate(R.layout.item_memo, parent, false);
        return new memoViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull memoViewHolder holder, int position) {

        memoItem item = memoItems.get(position);

        holder.setId(item.getId());
        holder.setContent(item.getContent());
        holder.setThumnail(item.getThumnail());
        holder.setTime(item.getTime());

    }

    @Override
    public int getItemCount() {
        return memoItems.size();
    }
}





