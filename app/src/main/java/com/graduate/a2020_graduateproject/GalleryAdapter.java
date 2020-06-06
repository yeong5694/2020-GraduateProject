package com.graduate.a2020_graduateproject;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.ArrayList;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder> {

    private Context context;
    //private ArrayList<Bitmap> imageList = new ArrayList<Bitmap>();
    //private ArrayList<Upload> imageList = new ArrayList<Upload>();

    private ArrayList<Upload> imageList;
    //private AdapterView.OnItemClickListener listener; // 2020-06-04 09:47
    private OnItemClickListener listener;

    //public GalleryAdapter(Context context, ArrayList<Bitmap> imageList) {
    public GalleryAdapter(Context context, ArrayList<Upload> imageList) {   // 2020-06-04 09:47
        this.context = context;
        this.imageList = imageList;
    }

    // onCreateViewHolder() - viewType 형태의 아이템 뷰를 위한 뷰홀더 객체 생성하여 리턴
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.gallery_image_item, parent,false);
        return new ViewHolder(view);
    }

    // onBindViewHolder() - position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Upload uploadCurrent = imageList.get(position);  //2020-06-06 12:54

        Glide.with(context)
                .load(uploadCurrent.getImageUrl())
                .into(holder.imageView);  // 2020-06-04 09:48
        //holder.imageView.setImageURI(imageUri);


        //holder.imageView.setImageBitmap(imageList.get(position));


        /*
        if(uploadCurrent != null) {
            Glide.with(context)
                    .load(uploadCurrent.getImageUrl())
                    .into(holder.imageView);
        }
        else {
            Toast.makeText(context, "Upload object not exist", Toast.LENGTH_SHORT).show();
        }
        */ //2020-06-06 12:53
    }

    // getItemCount() - 전체 데이터 갯수 리턴
    @Override
    public int getItemCount() {
        return imageList.size();
    }

    // ViewHolder는 화면에 표시될 아이템 뷰를 저장하는 객체
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener {   // 어댑터를 통해 만들어진 각 아이템 뷰를 ViewHolder 객체에 저장하고
        public ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.image);

            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(this);

            /*
            // 아이템 클릭 이벤트 처리
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });*/
        }

        @Override
        public void onClick(View v) {
            if(listener != null) {
                int position = getAdapterPosition();
                if(position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(position);
                }
            }
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.setHeaderTitle("Select Action");
            MenuItem doWhatEver = menu.add(Menu.NONE, 1, 1, "Do whatever");
            MenuItem delete = menu.add(Menu.NONE, 2, 2, "Delete");

            doWhatEver.setOnMenuItemClickListener(this);
            delete.setOnMenuItemClickListener(this);
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            if(listener != null) {
                int position = getAdapterPosition();
                if(position != RecyclerView.NO_POSITION) {
                    switch (item.getItemId()) {
                        case 1:
                            listener.onWhatEverClick(position);
                            return true;
                        case 2:
                            listener.onDeleteClick(position);
                            return true;
                    }
                }
            }
            return false;
        }     // 2020-06-04 09:49
    }


    public interface OnItemClickListener {
        void onItemClick(int position);
        void onWhatEverClick(int position);
        void onDeleteClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }     // 2020-06-04 09:49
}