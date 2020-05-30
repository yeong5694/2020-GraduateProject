package com.graduate.a2020_graduateproject.ui.viewmodel;


import androidx.lifecycle.ViewModel;

import com.graduate.a2020_graduateproject.data.TSLiveData;

public class CalendarHeaderViewModel extends ViewModel {
    public TSLiveData<Long> mHeaderDate = new TSLiveData<>();

    public void setHeaderDate(long headerDate) {
        this.mHeaderDate.setValue(headerDate);
    }
}
