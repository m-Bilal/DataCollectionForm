package com.bilal.datacollectionform.fragment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bilal.datacollectionform.R;
import com.bilal.datacollectionform.activity.FormListActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    private Context context;

    private CardView existingProjectCartview;


    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        context = getActivity();

        existingProjectCartview = v.findViewById(R.id.cardview_existing_project);
        existingProjectCartview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startFormListActivity();
            }
        });
        return v;
    }

    private void startFormListActivity() {
        Intent intent = new Intent(context, FormListActivity.class);
        startActivity(intent);
    }
}
