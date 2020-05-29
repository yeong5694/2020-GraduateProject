package com.graduate.a2020_graduateproject.bottomNavigation;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public class MapInfoItemTouchHelperCallback extends ItemTouchHelper.Callback {

    private final onItemMoveListener mItemMoveListener;
    boolean mintem = true;

    public MapInfoItemTouchHelperCallback(onItemMoveListener listener){
        mItemMoveListener = listener;
    }


    public interface  onItemMoveListener {
        boolean onItemMove(int fromPosition, int toPosition);
        void onItemDismiss(int position);
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;

        return makeMovementFlags(dragFlags, 0);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        mItemMoveListener.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

    }

    @Override
    public boolean isLongPressDragEnabled() {

        //return true;
        return mintem;
    }

    public void setMintem(boolean mintem){
        this.mintem = mintem;
    }
}
