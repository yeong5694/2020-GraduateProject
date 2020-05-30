package com.graduate.a2020_graduateproject;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;


public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ImageViewHolder> {

    private Context context;
    //private ArrayList<Bitmap> imageList = new ArrayList<Bitmap>();
    private ArrayList<Upload> imageList = new ArrayList<Upload>();

    public GalleryAdapter(Context context, ArrayList<Upload> imageList) {
        this.context = context;
        this.imageList = imageList;
    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.gallery_image_item,parent,false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ImageViewHolder holder, int position) {
        Upload uploadCurrent = imageList.get(position);
        Glide.with(context)
                .load(uploadCurrent.getImageUrl())
                .into(holder.imageView);

        /*
        Glide.with(context)
                .load(uploadCurrent.getImageUri())
                .fit()
                .centerCrop()
                .into(holder.imageView);
        */
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    // ViewHolder는 화면에 표시될 아이템 뷰를 저장하는 객체
    public class ImageViewHolder extends RecyclerView.ViewHolder {   // 어댑터를 통해 만들어진 각 아이템 뷰를 ViewHolder 객체에 저장하고
        public ImageView imageView;

        public ImageViewHolder(View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView);

            /*
            // 아이템 클릭 이벤트 처리
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });*/
        }
    }
}
/*
public class GalleryAdapter extends BaseAdapter {

    private Context context;
    //private LayoutInflater layoutInflater;

    //private ArrayList<Bitmap> imageList = new ArrayList<Bitmap>();
    private ArrayList<Upload> imageList = new ArrayList<Upload>();

    //public GalleryAdapter(Context context, ArrayList<Bitmap> imageList) {
    public GalleryAdapter(Context context, ArrayList<Upload> imageList) {
        this.context = context;
        this.imageList = imageList;
        //this.layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return imageList.size();
    }

    @Override
    public Object getItem(int position) {
        return imageList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if(convertView == null){
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.gallery_image_item,parent,false);
            viewHolder = new ViewHolder();
            viewHolder.imageView = convertView.findViewById(R.id.image);

            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.imageView.setImageBitmap(imageList.get(position));

        return convertView;
    }


    // ViewHolder는 화면에 표시될 아이템 뷰를 저장하는 객체
    private class ViewHolder{   // 어댑터를 통해 만들어진 각 아이템 뷰를 ViewHolder 객체에 저장하고
        ImageView imageView;
    }

/*
    // ViewHolder는 화면에 표시될 아이템 뷰를 저장하는 객체
    private class ViewHolder extends RecyclerView.ViewHolder {   // 어댑터를 통해 만들어진 각 아이템 뷰를 ViewHolder 객체에 저장하고
        ImageView imageView;

        ViewHolder(View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView);

            // 아이템 클릭 이벤트 처리
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
    }* /
}*/
