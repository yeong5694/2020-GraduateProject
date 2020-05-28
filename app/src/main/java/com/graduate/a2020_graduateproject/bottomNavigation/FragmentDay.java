package com.graduate.a2020_graduateproject.bottomNavigation;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.graduate.a2020_graduateproject.BottomActivityView;
import com.graduate.a2020_graduateproject.MainActivity;
import com.graduate.a2020_graduateproject.R;

public class FragmentDay extends Fragment {




    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //View view = inflater.inflate(R.layout.fragment_day, container, false);

        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_day, container, false);

        TextView day_textview = viewGroup.findViewById(R.id.day_textview);

        day_textview.setText(((BottomActivityView) getActivity()).getDay());


        return viewGroup;
    }
}
