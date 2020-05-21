package com.graduate.a2020_graduateproject;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public class PlanItemTouchHelperCallback extends ItemTouchHelper.Callback {


   // private final ItemTouchHelperAdapter mAdapter;

    private final onItemMoveListener mItemMoveListener;
    public PlanItemTouchHelperCallback(onItemMoveListener listener){
        mItemMoveListener = listener;
    }


//    public PlanItemTouchHelperCallback(ItemTouchHelperAdapter adapter){
//        this.mAdapter = adapter;
//    }

    public interface  onItemMoveListener {
        boolean onItemMove(int fromPosition, int toPosition);
        void onItemDismiss(int position);
    }

    boolean mintem = true;
    boolean mintem2 = true;


    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {

        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;

        return makeMovementFlags(dragFlags, swipeFlags);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {

        //mAdapter.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        mItemMoveListener.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;

    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        //mAdapter.onItemDismiss(viewHolder.getAdapterPosition());
        mItemMoveListener.onItemDismiss(viewHolder.getAdapterPosition());

    }

    @Override
    public boolean isLongPressDragEnabled() {

        //return true;
        return mintem;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {

        //return true;
        return mintem2;
    }

    public void setMintem(boolean mintem){
        this.mintem = mintem;
    }

    public void setMintem2(boolean mintem2){
        this.mintem2 = mintem2;
    }


//    public interface ItemTouchHelperAdapter {
//
//        boolean onItemMove(int fromPosition, int toPosition);
//
//        void onItemDismiss(int position);
//    }


}
