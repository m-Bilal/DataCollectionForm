package com.bilal.datacollectionform.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bilal.datacollectionform.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class TimeAnswerFragment extends Fragment {


    public TimeAnswerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_time_answer, container, false);
    }

}
