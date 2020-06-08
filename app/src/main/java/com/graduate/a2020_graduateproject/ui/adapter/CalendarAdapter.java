package com.graduate.a2020_graduateproject.ui.adapter;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.graduate.a2020_graduateproject.R;
import com.graduate.a2020_graduateproject.Schedule;
import com.graduate.a2020_graduateproject.databinding.CalendarHeaderBinding;
import com.graduate.a2020_graduateproject.databinding.DayItemBinding;
import com.graduate.a2020_graduateproject.databinding.EmptyDayBinding;
import com.graduate.a2020_graduateproject.databinding.ScheduleDayItemBinding;
import com.graduate.a2020_graduateproject.ui.viewmodel.CalendarHeaderViewModel;
import com.graduate.a2020_graduateproject.ui.viewmodel.CalendarViewModel;
import com.graduate.a2020_graduateproject.ui.viewmodel.EmptyViewModel;
import com.google.gson.Gson;
import com.graduate.a2020_graduateproject.ui.viewmodel.ScheduleCalendarViewModel;

import java.util.Calendar;

import static com.graduate.a2020_graduateproject.R.*;


public class CalendarAdapter extends ListAdapter<Object, RecyclerView.ViewHolder> {
    private final int HEADER_TYPE = 0;
    private final int EMPTY_TYPE = 1;
    private final int DAY_TYPE = 2;
    //schedule이 있는 day_type
    private final int DAY_TYPE_S = 3;

    public CalendarAdapter() {
        super(new DiffUtil.ItemCallback<Object>() {
            @Override
            public boolean areItemsTheSame(@NonNull Object oldItem, @NonNull Object newItem) {
                return oldItem == newItem;
            }

            @Override
            public boolean areContentsTheSame(@NonNull Object oldItem, @NonNull Object newItem) {
                Gson gson = new Gson();
                return gson.toJson(oldItem).equals(gson.toJson(newItem));
            }
        });
    }


    @Override
    public int getItemViewType(int position) { //뷰타입 나누기
        Object item = getItem(position);
        if (item instanceof Long) {
            return HEADER_TYPE; // 날짜 타입
        } else if (item instanceof String) {
            return EMPTY_TYPE; // 비어있는 일자 타입
        } else if ( item instanceof Schedule){
            return DAY_TYPE_S; // 스케줄있는 일자 타입
        }
        else {
            return DAY_TYPE; // 일자 타입
        }
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == HEADER_TYPE) { // 날짜 타입
            CalendarHeaderBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), layout.item_calendar_header, parent, false);
            StaggeredGridLayoutManager.LayoutParams params = (StaggeredGridLayoutManager.LayoutParams) binding.getRoot().getLayoutParams();
            params.setFullSpan(true); //Span을 하나로 통합하기
            binding.getRoot().setLayoutParams(params);
            return new HeaderViewHolder(binding);
        } else if (viewType == EMPTY_TYPE) { //비어있는 일자 타입
            EmptyDayBinding binding =
                    DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), layout.item_day_empty, parent, false);
            return new EmptyViewHolder(binding);
        }else if (viewType == DAY_TYPE_S) { //스케줄 있는 일자 타입
            ScheduleDayItemBinding binding =
                    DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), layout.item_day_schedule, parent, false);
            // 스케줄있는 일자 타입
            return new ScheduleDayViewHolder(binding);
        }
        //일자 타입
        DayItemBinding binding =
                DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), layout.item_day, parent, false);// 일자 타입
        return new DayViewHolder(binding);
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        int viewType = getItemViewType(position);
        if (viewType == HEADER_TYPE) { //날짜 타입 꾸미기
            HeaderViewHolder holder = (HeaderViewHolder) viewHolder;
            Object item = getItem(position);
            CalendarHeaderViewModel model = new CalendarHeaderViewModel();
            if (item instanceof Long) {
                model.setHeaderDate((Long) item);
            }
            holder.setViewModel(model);
        } else if (viewType == EMPTY_TYPE) { //비어있는 날짜 타입 꾸미기
            EmptyViewHolder holder = (EmptyViewHolder) viewHolder;
            EmptyViewModel model = new EmptyViewModel();
            holder.setViewModel(model);
        } else if (viewType == DAY_TYPE) { // 일자 타입 꾸미기
            DayViewHolder holder = (DayViewHolder) viewHolder;
            Object item = getItem(position);
            CalendarViewModel model = new CalendarViewModel();
            if (item instanceof Calendar) {
                model.setCalendar((Calendar) item);
            }
            holder.setViewModel(model);
        } else if (viewType == DAY_TYPE_S) { // 스케줄 있는 일자 타입 꾸미기

            ScheduleDayViewHolder holder = (ScheduleDayViewHolder) viewHolder;
            Object item = getItem(position);
            ScheduleCalendarViewModel model = new ScheduleCalendarViewModel();
            if (item instanceof Schedule) {
                model.setSchedule((Schedule) item);
            }
            holder.setViewModel(model);
        }
    }


    private class HeaderViewHolder extends RecyclerView.ViewHolder { //날짜 타입 ViewHolder
        private CalendarHeaderBinding binding;

        private HeaderViewHolder(@NonNull CalendarHeaderBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        private void setViewModel(CalendarHeaderViewModel model) {
            binding.setModel(model);
            binding.executePendingBindings();
        }
    }


    private class EmptyViewHolder extends RecyclerView.ViewHolder { // 비어있는 요일 타입 ViewHolder
        private EmptyDayBinding binding;

        private EmptyViewHolder(@NonNull EmptyDayBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        private void setViewModel(EmptyViewModel model) {
            binding.setModel(model);
            binding.executePendingBindings();
        }

    }

    private class DayViewHolder extends RecyclerView.ViewHolder {// 요일 타입 ViewHolder
        private DayItemBinding binding;

        private DayViewHolder(@NonNull DayItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        private void setViewModel(CalendarViewModel model) {
            binding.setModel(model);
            binding.executePendingBindings();
        }
    }


    private class ScheduleDayViewHolder extends RecyclerView.ViewHolder {// 스케줄있는 요일 타입 ViewHolder
        private ScheduleDayItemBinding binding;

        private ScheduleDayViewHolder(@NonNull ScheduleDayItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        private void setViewModel(ScheduleCalendarViewModel model) {
            binding.setModel(model);
            binding.executePendingBindings();
        }
    }

}
