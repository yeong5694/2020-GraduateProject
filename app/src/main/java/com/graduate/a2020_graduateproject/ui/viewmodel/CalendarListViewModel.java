package com.graduate.a2020_graduateproject.ui.viewmodel;

import android.util.Log;

import androidx.lifecycle.ViewModel;

import com.graduate.a2020_graduateproject.Schedule;
import com.graduate.a2020_graduateproject.TripRoomActivity;
import com.graduate.a2020_graduateproject.data.TSLiveData;
import com.graduate.a2020_graduateproject.utils.DateFormat;
import com.graduate.a2020_graduateproject.utils.Keys;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kakao.network.ErrorResult;


public class CalendarListViewModel extends ViewModel {
    private long mCurrentTime;

    public TSLiveData<String> mTitle = new TSLiveData<>();
    public TSLiveData<ArrayList<Object>> mCalendarList = new TSLiveData<>(new ArrayList<>());
    public static String room_id="";

    public int mCenterPosition;

    private DatabaseReference tripFromReference;
    private DatabaseReference tripToReference;

//    public CalendarListViewModel(){
//        this.selected_room_id = selec1tedRoomId;
//    }
    public void setTitle(int position) {
        try {
            Object item = mCalendarList.getValue().get(position);
            if (item instanceof Long) {
                setTitle((Long) item);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setTitle(long time) {
        mCurrentTime = time;
        mTitle.setValue(DateFormat.getDate(time, DateFormat.CALENDAR_HEADER_FORMAT));
    }


    public void initCalendarList() {
        GregorianCalendar cal = new GregorianCalendar();
        setCalendarList(cal);
    }

    public void setCalendarList(GregorianCalendar cal) {

        //selected_room_id = TripRoomActivity.room_id;
        Log.e("search",room_id);
        setTitle(cal.getTimeInMillis());

        ArrayList<Object> calendarList = new ArrayList<>();
        for (int i = -300; i < 300; i++) {
            try {
                GregorianCalendar calendar = new GregorianCalendar(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + i, 1, 0, 0, 0);
                if (i == 0) {
                    mCenterPosition = calendarList.size();
                }
                calendarList.add(calendar.getTimeInMillis());

                int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1; //해당 월에 시작하는 요일 -1 을 하면 빈칸을 구할 수 있겠죠 ?
                int max = calendar.getActualMaximum(Calendar.DAY_OF_MONTH); // 해당 월에 마지막 요일

                for (int j = 0; j < dayOfWeek; j++) {
                    calendarList.add(Keys.EMPTY);
                }
                for (int j = 1; j <= max; j++) {
                   // if (true)   //일정이 있는 날짜면
                   //     ;//구분이 가능한 타입이되 그레고리캘린더
                    if(5<=j&&j<=7){ //5~7일 여행이면
                        calendarList.add(new Schedule(Integer.toString(j))); //일정이 있는 일자 타입
                    }
                    else {
                        calendarList.add(new GregorianCalendar(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), j)); //일자타입
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        mCalendarList.setValue(calendarList);
    }


}
