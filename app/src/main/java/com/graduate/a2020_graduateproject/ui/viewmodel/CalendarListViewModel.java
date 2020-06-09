package com.graduate.a2020_graduateproject.ui.viewmodel;

import android.util.Log;

import androidx.annotation.NonNull;
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

    private DatabaseReference tripIdReference ;

//    private DatabaseReference tripFromReference  =FirebaseDatabase.getInstance().getReference("sharing_trips/tripRoom_list").child(room_id).child("/from/");
//    private DatabaseReference tripToReference = FirebaseDatabase.getInstance().getReference("sharing_trips/tripRoom_list").child(room_id).child("/to/");

    //일정 날짜 변수
    int trip_year_from;
    int trip_month_from;
    int trip_day_from;
    int trip_year_to;
    int trip_month_to;
    int trip_day_to;

    String trip_from;
    String trip_to;

    public CalendarListViewModel(){
        tripIdReference  =FirebaseDatabase.getInstance().getReference("sharing_trips/tripRoom_list").child(room_id);
        tripIdReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.e("cc:","."+room_id+".");
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if(snapshot.getKey().equals("from")){
                        trip_from = snapshot.getValue().toString();
                        Log.e("cc:","from."+trip_from+".");
                    }
                    if(snapshot.getKey().equals("to")){
                        trip_to = snapshot.getValue().toString();
                        Log.e("cc:","to."+trip_to+".");
                    }

                }

                trip_year_from=Integer.parseInt(trip_from.substring(0,4));
                trip_month_from=Integer.parseInt(trip_from.substring(6,8));
                trip_day_from=Integer.parseInt(trip_from.substring(10,12));
                trip_year_to=Integer.parseInt(trip_to.substring(0,4));
                trip_month_to=Integer.parseInt(trip_to.substring(6,8));
                trip_day_to=Integer.parseInt(trip_to.substring(10,12));


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { //실패
                trip_year_from = 0;
                trip_month_from=0;
                trip_day_from=0;
                trip_year_to=0;
                trip_month_to=0;
                trip_day_to=0;
            }
        });
    }

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
        Log.e("search:",room_id);
        setTitle(cal.getTimeInMillis());


        ArrayList<Object> calendarList = new ArrayList<>();
        //Log.e("cal", String.valueOf(cal.get(Calendar.YEAR)));
        //Log.e("cal", String.valueOf(cal.get(Calendar.MONTH)));
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
