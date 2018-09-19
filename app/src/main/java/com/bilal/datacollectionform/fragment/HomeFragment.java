package com.bilal.datacollectionform.fragment;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bilal.datacollectionform.R;
import com.bilal.datacollectionform.activity.ExistingFormListActivity;
import com.bilal.datacollectionform.activity.LoginActivity;
import com.bilal.datacollectionform.activity.NewFormListActivity;
import com.bilal.datacollectionform.helper.Helper;
import com.bilal.datacollectionform.model.FileModel;
import com.bilal.datacollectionform.model.FormAnswerModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    private Context context;

    private CardView existingProjectCardview;
    private CardView newProjectCardview;
    private TextView logoutTextview;


    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        context = getActivity();

        existingProjectCardview = v.findViewById(R.id.cardview_existing_project);
        newProjectCardview = v.findViewById(R.id.cardview_new_project);
        logoutTextview = v.findViewById(R.id.textview_logout);

        existingProjectCardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startExistingFormListActivity();
            }
        });

        newProjectCardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNewFormListActivity();
            }
        });

        logoutTextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initiateSafeLogout();
            }
        });
        return v;
    }

    private void startExistingFormListActivity() {
        Intent intent = new Intent(context, ExistingFormListActivity.class);
        startActivity(intent);
    }

    private void startNewFormListActivity() {
        Intent intent = new Intent(context, NewFormListActivity.class);
        startActivity(intent);
    }

    private void initiateSafeLogout() {
        if (FormAnswerModel.getAllUnsyncedModels(context).size() > 0 ||
                FileModel.getAllUnsyncedModels(context).size() > 0) {
            createWarningDialog();
        } else {
            logoutAndStartLoginActivity();
        }
    }

    private void logoutAndStartLoginActivity() {
        Helper.deleteAllAndResetRealm(context);
        Intent intent = new Intent(context, LoginActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

    private void createWarningDialog() {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Warning");
        builder.setMessage("You have unsynced items that will be deleted if you logout. Continue?")
                .setPositiveButton("Logout", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        logoutAndStartLoginActivity();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        // Create the AlertDialog object and return it
        builder.create();
        builder.show();
    }
}
