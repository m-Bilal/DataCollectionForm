package com.bilal.datacollectionform.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.bilal.datacollectionform.R;
import com.bilal.datacollectionform.fragment.AnswerListFragment;
import com.bilal.datacollectionform.fragment.CheckboxAnswerFragment;
import com.bilal.datacollectionform.fragment.DateAnswerFragment;
import com.bilal.datacollectionform.fragment.DropdownAnswerFragment;
import com.bilal.datacollectionform.fragment.FileAnswerFragment;
import com.bilal.datacollectionform.fragment.ImageAnswerFragment;
import com.bilal.datacollectionform.fragment.RadioButtonAnswerFragment;
import com.bilal.datacollectionform.fragment.StarAnswerFragment;
import com.bilal.datacollectionform.fragment.TextAnswerFragment;
import com.bilal.datacollectionform.fragment.TimeAnswerFragment;
import com.bilal.datacollectionform.helper.CallbackHelper;
import com.bilal.datacollectionform.model.FormModel;
import com.bilal.datacollectionform.model.FormQuestionModel;
import com.bilal.datacollectionform.model.QuestionAnswerModel;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

public class FormQuestionActivity extends AppCompatActivity implements CallbackHelper.FragmentAnswerCallback,
        CallbackHelper.FragmentCallback{

    private final static String TAG = "FormQuestionActivity";

    public final static String INTENT_ARG_FORM_ID = "form_id";
    public final static String BUNDLE_ARG_QUESTION_KEY = "primary_key";
    public final static String BUNDLE_ARG_ANSWER_FORM_KEY = "answer_form_key";
    public final static String BUNDLE_ARG_POSITION = "pos";
    public final static String BUNDLE_ARG_TIME = "date";
    public final static String BUNDLE_ARG_LATITUDE = "latitude";
    public final static String BUNDLE_ARG_LONGITUDE = "longitude";
    public final static int INTENT_SELECT_FILE_REQUEST_CODE = 1001;
    public final static int INTENT_SELECT_IMAGE_REQUEST_CODE = 1002;
    public final static int REQUEST_CHECK_SETTINGS = 1003;

    private boolean deleteModel;

    private Context context;
    private TextView toolbarTitleTextview;
    private FormModel formModel;
    private List<Fragment> fragmentList;
    private LinkedList<QuestionAnswerModel> answerList;
    private Toolbar toolbar;
    private FrameLayout frameLayout;
    private TextView previousTextview;
    private TextView nextTextview;
    private TextView newEntryTextview;
    private Fragment currentFragment;
    private LocationCallback locationCallback;
    private LocationRequest mLocationRequest;

    private FusedLocationProviderClient fusedLocationProviderClient;

    private double longitude;
    private double latitude;

    private int pos;
    private long time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_question);

        context = this;
        deleteModel = false;

        frameLayout = findViewById(R.id.frame_layout);
        toolbar = findViewById(R.id.toolbar);
        previousTextview = findViewById(R.id.textview_previous);
        nextTextview = findViewById(R.id.textview_next);
        newEntryTextview = findViewById(R.id.textview_new_entry);
        toolbarTitleTextview = findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        int formId = getIntent().getIntExtra(INTENT_ARG_FORM_ID, 0);
        latitude = -1;
        longitude = -1;
        time = Calendar.getInstance().getTimeInMillis();
        formModel = FormModel.getFromForId(context, formId);
        answerList = new LinkedList<>();

        toolbarTitleTextview.setText(formModel.formName);

        previousTextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attachPreviousFragment();
            }
        });

        nextTextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswerRequirementsAndProceed();
            }
        });

        newEntryTextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startFormListActivity();
            }
        });

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Location tracking
        try {
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {

                    if (location != null) {
                        Log.d(TAG, "onCreateView, fusedLocationProciderClient, latitude" + location.getLatitude());
                        Log.d(TAG, "onCreateView, fusedLocationProciderClient, longitude" + location.getLongitude());
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                    } else {
                        Log.d(TAG, "onCreateView, fusedLocationProciderClient, location null");
                    }
                }
            });

            mLocationRequest = new LocationRequest();
            mLocationRequest.setInterval(5 * 1000); // Increase to consume less battery
            mLocationRequest.setFastestInterval(2 * 1000); // Increase to consume less battery
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(mLocationRequest);
            SettingsClient client = LocationServices.getSettingsClient(this);
            Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

            task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
                @Override
                public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                    // All location settings are satisfied. The client can initialize
                    // location requests here.
                    // ...
                }
            });

            task.addOnFailureListener(this, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    if (e instanceof ResolvableApiException) {
                        // Location settings are not satisfied, but this can be fixed
                        // by showing the user a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            ResolvableApiException resolvable = (ResolvableApiException) e;
                            resolvable.startResolutionForResult(FormQuestionActivity.this,
                                    REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException sendEx) {
                            // Ignore the error.
                        }
                    }
                }
            });

            locationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    if (locationResult == null) {
                        Log.d(TAG, "onCreateView, locationCallback, null");
                        return;
                    }
                    for (Location location : locationResult.getLocations()) {
                        Log.d(TAG, "onCreateView, locationCallback, latitude" + location.getLatitude());
                        Log.d(TAG, "onCreateView, locationCallback, longitude" + location.getLongitude());
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                    }
                }
            };

        } catch (SecurityException e) {
            Log.e(TAG, "fusedLocationProciderClient, Security Exception " + e.toString());
            e.printStackTrace();
        }
        createQuestionFragments();
        attachFirstFragment();
    }

    private void startFormListActivity() {
        Intent intent = new Intent(context, ExistingFormListActivity.class);
        startActivity(intent);
        finish();
    }

    private void createQuestionFragments() {
        fragmentList = new LinkedList<>();
        pos = 0;
        int j = 0;
        for (FormQuestionModel i : formModel.formQuestionModelRealmList) {
            Log.d(TAG, "createQuestionFragment, type : " + i.type);
            if (i.type != null) {
                if (i.type.equalsIgnoreCase(FormQuestionModel.TYPE_EMAIL)
                        || i.type.equalsIgnoreCase(FormQuestionModel.TYPE_PARAGRAPH)
                        || i.type.equalsIgnoreCase(FormQuestionModel.TYPE_TEXT)) {
                    Fragment fragment = new TextAnswerFragment();
                    Bundle bundle = new Bundle();
                    bundle.putInt(BUNDLE_ARG_QUESTION_KEY, i.primaryKey);
                    bundle.putInt(BUNDLE_ARG_POSITION, j++);
                    fragment.setArguments(bundle);
                    fragmentList.add(fragment);
                } else if (i.type.equalsIgnoreCase(FormQuestionModel.TYPE_DROPDOWN)) {
                    Fragment fragment = new DropdownAnswerFragment();
                    Bundle bundle = new Bundle();
                    bundle.putInt(BUNDLE_ARG_QUESTION_KEY, i.primaryKey);
                    bundle.putInt(BUNDLE_ARG_POSITION, j++);
                    fragment.setArguments(bundle);
                    fragmentList.add(fragment);
                } else if (i.type.equalsIgnoreCase(FormQuestionModel.TYPE_DATE)) {
                    Fragment fragment = new DateAnswerFragment();
                    Bundle bundle = new Bundle();
                    bundle.putInt(BUNDLE_ARG_QUESTION_KEY, i.primaryKey);
                    bundle.putInt(BUNDLE_ARG_POSITION, j++);
                    fragment.setArguments(bundle);
                    fragmentList.add(fragment);
                } else if (i.type.equalsIgnoreCase(FormQuestionModel.TYPE_STARS)) {
                    Fragment fragment = new StarAnswerFragment();
                    Bundle bundle = new Bundle();
                    bundle.putInt(BUNDLE_ARG_QUESTION_KEY, i.primaryKey);
                    bundle.putInt(BUNDLE_ARG_POSITION, j++);
                    fragment.setArguments(bundle);
                    fragmentList.add(fragment);
                } else if (i.type.equalsIgnoreCase(FormQuestionModel.TYPE_FILE_UPLOAD)) {
                    Fragment fragment = new FileAnswerFragment();
                    Bundle bundle = new Bundle();
                    bundle.putInt(BUNDLE_ARG_QUESTION_KEY, i.primaryKey);
                    bundle.putInt(BUNDLE_ARG_POSITION, j++);
                    fragment.setArguments(bundle);
                    fragmentList.add(fragment);
                } else if (i.type.equalsIgnoreCase(FormQuestionModel.TYPE_CHECKBOX)) {
                    Fragment fragment = new CheckboxAnswerFragment();
                    Bundle bundle = new Bundle();
                    bundle.putInt(BUNDLE_ARG_QUESTION_KEY, i.primaryKey);
                    bundle.putInt(BUNDLE_ARG_POSITION, j++);
                    fragment.setArguments(bundle);
                    fragmentList.add(fragment);
                } else if (i.type.equalsIgnoreCase(FormQuestionModel.TYPE_RADIO_BUTTON)) {
                    Fragment fragment = new RadioButtonAnswerFragment();
                    Bundle bundle = new Bundle();
                    bundle.putInt(BUNDLE_ARG_QUESTION_KEY, i.primaryKey);
                    bundle.putInt(BUNDLE_ARG_POSITION, j++);
                    fragment.setArguments(bundle);
                    fragmentList.add(fragment);
                } else if (i.type.equalsIgnoreCase(FormQuestionModel.TYPE_TIME)) {
                    Fragment fragment = new TimeAnswerFragment();
                    Bundle bundle = new Bundle();
                    bundle.putInt(BUNDLE_ARG_QUESTION_KEY, i.primaryKey);
                    bundle.putInt(BUNDLE_ARG_POSITION, j++);
                    fragment.setArguments(bundle);
                    fragmentList.add(fragment);
                } else if (i.type.equalsIgnoreCase(FormQuestionModel.TYPE_IMAGE)) {
                    Fragment fragment = new ImageAnswerFragment();
                    Bundle bundle = new Bundle();
                    bundle.putInt(BUNDLE_ARG_QUESTION_KEY, i.primaryKey);
                    bundle.putInt(BUNDLE_ARG_POSITION, j++);
                    fragment.setArguments(bundle);
                    fragmentList.add(fragment);
                } else if (i.type.equalsIgnoreCase(FormQuestionModel.TYPE_HIDDEN)) {
                    // Hidden
                } else if (i.type.equalsIgnoreCase(FormQuestionModel.TYPE_SUBMIT)) {
                    // submit button
                }
            }
        }
    }

    private void askFragmentToSaveAnswer() {
        if (currentFragment instanceof CheckboxAnswerFragment) {
            ((CheckboxAnswerFragment) currentFragment).saveAnswer();
        } else if (currentFragment instanceof DateAnswerFragment) {
            ((DateAnswerFragment) currentFragment).saveAnswer();
        } else if (currentFragment instanceof DropdownAnswerFragment) {
            ((DropdownAnswerFragment) currentFragment).saveAnswer();
        } else if (currentFragment instanceof FileAnswerFragment) {
            ((FileAnswerFragment) currentFragment).saveAnswer();
        } else if (currentFragment instanceof ImageAnswerFragment) {
            ((ImageAnswerFragment) currentFragment).saveAnswer();
        } else if (currentFragment instanceof RadioButtonAnswerFragment) {
            ((RadioButtonAnswerFragment) currentFragment).saveAnswer();
        } else if (currentFragment instanceof StarAnswerFragment) {
            ((StarAnswerFragment) currentFragment).saveAnswer();
        } else if (currentFragment instanceof TextAnswerFragment) {
            ((TextAnswerFragment) currentFragment).saveAnswer();
        } else if (currentFragment instanceof TimeAnswerFragment) {
            ((TimeAnswerFragment) currentFragment).saveAnswer();
        }
    }

    @Override
    public void setAnswerListInCurrentFragment() {
        if (currentFragment instanceof AnswerListFragment) {
            ((AnswerListFragment) currentFragment).setAnswerList(answerList);
        } else if (currentFragment instanceof CheckboxAnswerFragment) {
            ((CheckboxAnswerFragment) currentFragment).setAnswerList(answerList);
        } else if (currentFragment instanceof DateAnswerFragment) {
            ((DateAnswerFragment) currentFragment).setAnswerList(answerList);
        } else if (currentFragment instanceof DropdownAnswerFragment) {
            ((DropdownAnswerFragment) currentFragment).setAnswerList(answerList);
        } else if (currentFragment instanceof FileAnswerFragment) {
            ((FileAnswerFragment) currentFragment).setAnswerList(answerList);
        } else if (currentFragment instanceof ImageAnswerFragment) {
            ((ImageAnswerFragment) currentFragment).setAnswerList(answerList);
        } else if (currentFragment instanceof RadioButtonAnswerFragment) {
            ((RadioButtonAnswerFragment) currentFragment).setAnswerList(answerList);
        } else if (currentFragment instanceof StarAnswerFragment) {
            ((StarAnswerFragment) currentFragment).setAnswerList(answerList);
        } else if (currentFragment instanceof TextAnswerFragment) {
            ((TextAnswerFragment) currentFragment).setAnswerList(answerList);
        } else if (currentFragment instanceof TimeAnswerFragment) {
            ((TimeAnswerFragment) currentFragment).setAnswerList(answerList);
        }
    }

    private void attachFirstFragment() {
        frameLayout.removeAllViews();
        getSupportFragmentManager().beginTransaction().add(R.id.frame_layout, fragmentList.get(pos)).commit();
    }

    private void checkAnswerRequirementsAndProceed() {
        boolean conditionSatisfied = false;
        if (currentFragment instanceof CheckboxAnswerFragment) {
            conditionSatisfied = ((CheckboxAnswerFragment) currentFragment).requirementsSatisfied();
        } else if (currentFragment instanceof DateAnswerFragment) {
            conditionSatisfied = ((DateAnswerFragment) currentFragment).requirementsSatisfied();
        } else if (currentFragment instanceof DropdownAnswerFragment) {
            conditionSatisfied = ((DropdownAnswerFragment) currentFragment).requirementsSatisfied();
        } else if (currentFragment instanceof FileAnswerFragment) {
            conditionSatisfied = ((FileAnswerFragment) currentFragment).requirementsSatisfied();
        } else if (currentFragment instanceof ImageAnswerFragment) {
            conditionSatisfied = ((ImageAnswerFragment) currentFragment).requirementsSatisfied();
        } else if (currentFragment instanceof RadioButtonAnswerFragment) {
            conditionSatisfied = ((RadioButtonAnswerFragment) currentFragment).requirementsSatisfied();
        } else if (currentFragment instanceof StarAnswerFragment) {
            conditionSatisfied = ((StarAnswerFragment) currentFragment).requirementsSatisfied();
        } else if (currentFragment instanceof TextAnswerFragment) {
            conditionSatisfied = ((TextAnswerFragment) currentFragment).requirementsSatisfied();
        } else if (currentFragment instanceof TimeAnswerFragment) {
            conditionSatisfied = ((TimeAnswerFragment) currentFragment).requirementsSatisfied();
        }
        if (conditionSatisfied) {
            attachNextFragment();
        }
    }

    private void attachNextFragment() {
        askFragmentToSaveAnswer();
        if (pos < fragmentList.size()) {
            pos++;
        }
        if (pos < fragmentList.size()) {
            frameLayout.removeAllViews();
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, fragmentList.get(pos)).commit();
            if (pos == fragmentList.size() - 1) {
                nextTextview.setText("Upload and save");
            } else {
                nextTextview.setText("Next");
            }
        } else if (pos == fragmentList.size()) {
            frameLayout.removeAllViews();
            nextTextview.setVisibility(View.GONE);
            previousTextview.setVisibility(View.GONE);
            newEntryTextview.setVisibility(View.VISIBLE);
            Fragment fragment = new AnswerListFragment();
            Bundle bundle = new Bundle();
            bundle.putInt(BUNDLE_ARG_ANSWER_FORM_KEY, formModel.formId);
            bundle.putLong(BUNDLE_ARG_TIME, time);
            bundle.putDouble(BUNDLE_ARG_LONGITUDE, longitude);
            bundle.putDouble(BUNDLE_ARG_LATITUDE, latitude);
            fragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, fragment).commit();
        }
    }

    private void attachPreviousFragment() {
        askFragmentToSaveAnswer();
        if (pos > 0) {
            pos--;
            frameLayout.removeAllViews();
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, fragmentList.get(pos)).commit();
            if (pos == fragmentList.size() - 1) {
                nextTextview.setText("Upload and save");
            } else {
                nextTextview.setText("Next");
            }
        }
    }

    private void showWarningAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Going back will delete the current entry. Are you sure you want to delete this entry?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        deleteModelAndGoBack();
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

    @Override
    public void addAnswer(QuestionAnswerModel questionAnswerModel) {
        answerList.add(questionAnswerModel);
        //formAnswerModel.addAnswer(context, questionAnswerModel);
    }

    @Override
    public void updateAnswer(int position, QuestionAnswerModel questionAnswerModel) {
        answerList.set(position, questionAnswerModel);
        //QuestionAnswerModel.updateInRealm(context, questionAnswerModel);
    }

    @Override
    public void setCurrentFragment(Fragment fragment) {
        currentFragment = fragment;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == INTENT_SELECT_FILE_REQUEST_CODE && resultCode == RESULT_OK) {
            Uri selectedFile = data.getData(); //The uri with the location of the file
            ((FileAnswerFragment) currentFragment).setResultUri(selectedFile);
        }
        else if(requestCode == INTENT_SELECT_IMAGE_REQUEST_CODE && resultCode == RESULT_OK) {
            Uri selectedFile = data.getData(); //The uri with the location of the file
            ((ImageAnswerFragment) currentFragment).setResultUri(selectedFile);
        }
    }

    private void deleteModelAndGoBack() {
        //FormAnswerModel.deleteModel(context, formAnswerModel);
        super.onBackPressed();
    }

    @Override
    public void onBackPressed() {
        if (!(currentFragment instanceof AnswerListFragment)) {
            showWarningAlertDialog();
        } else {
            super.onBackPressed();
        }
    }

    private void startLocationUpdates() {
        try {
            fusedLocationProviderClient.requestLocationUpdates(mLocationRequest,
                    locationCallback,
                    null /* Looper */);
        } catch (SecurityException e) {

        }
    }

    private void stopLocationUpdates() {
        try {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        } catch (SecurityException e) {

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        startLocationUpdates();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }
}
