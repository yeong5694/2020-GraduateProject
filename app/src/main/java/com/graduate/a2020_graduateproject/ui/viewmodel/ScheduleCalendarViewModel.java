package com.graduate.a2020_graduateproject.ui.viewmodel;


import androidx.lifecycle.ViewModel;

import com.graduate.a2020_graduateproject.Schedule;
import com.graduate.a2020_graduateproject.data.TSLiveData;

import java.util.Calendar;

public class ScheduleCalendarViewModel extends ViewModel {
    public TSLiveData<Schedule> mSchedule = new TSLiveData<>();

    public void setSchedule(Schedule schedule) {
        this.mSchedule.setValue(schedule);
    }


}
