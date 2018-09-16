package com.bilal.datacollectionform.fragment;


import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;

import com.bilal.datacollectionform.R;
import com.bilal.datacollectionform.activity.FormQuestionActivity;
import com.bilal.datacollectionform.helper.CallbackHelper;
import com.bilal.datacollectionform.model.FormAnswerModel;
import com.bilal.datacollectionform.model.FormQuestionModel;
import com.bilal.datacollectionform.model.QuestionAnswerModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class TimeAnswerFragment extends Fragment {

    private final static String TAG = "DateAnswerFrag";
    private final static String TIME_FORMAT = "HH:mm";

    private Context context;
    private TextView questionTextView;
    private TimePicker timePicker;
    private FormQuestionModel formQuestionModel;
    private QuestionAnswerModel questionAnswerModel;
    private SimpleDateFormat simpleDateFormat;
    private Date date;
    private List<QuestionAnswerModel> questionAnswerModels;

    private boolean alreadyAnswered;
    private int position;
    private String answer;

    private CallbackHelper.FragmentAnswerCallback callback;
    private CallbackHelper.FragmentCallback fragmentCallback;


    public TimeAnswerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_time_answer, container, false);

        context = getActivity();
        callback = (CallbackHelper.FragmentAnswerCallback) getActivity();
        fragmentCallback = (CallbackHelper.FragmentCallback) getActivity();
        fragmentCallback.setCurrentFragment(this);
        int questionKey = getArguments().getInt(FormQuestionActivity.BUNDLE_ARG_QUESTION_KEY);
        position = getArguments().getInt(FormQuestionActivity.BUNDLE_ARG_POSITION);

        Log.d(TAG, "onCreateView, position " + position);

        questionTextView = v.findViewById(R.id.textview_question);
        timePicker = v.findViewById(R.id.timepicker);

        formQuestionModel = FormQuestionModel.getModelForPrimaryKey(context, questionKey);
        questionTextView.setText(formQuestionModel.label);

        fragmentCallback.setAnswerListInCurrentFragment();
        return v;
    }

    private void initTimePicker(Date date1) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date1);
        if (Build.VERSION.SDK_INT >= 23 ) {
            timePicker.setHour(calendar.get(Calendar.HOUR_OF_DAY));
            timePicker.setMinute(calendar.get(Calendar.MINUTE));
        } else {
            timePicker.setCurrentHour(calendar.get(Calendar.HOUR_OF_DAY));
            timePicker.setCurrentMinute(calendar.get(Calendar.MINUTE));
        }

        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);
                date = calendar.getTime();
                selectTime();
            }
        });
    }

    private void selectTime() {
        simpleDateFormat = new SimpleDateFormat(TIME_FORMAT);
        answer = simpleDateFormat.format(date);
    }

    public void setAnswerList(LinkedList<QuestionAnswerModel> questionAnswerModels) {
        Log.d(TAG, "setAnswerList()");
        this.questionAnswerModels = questionAnswerModels;
        checkIfAlreadyAnswered();
    }

    private void checkIfAlreadyAnswered() {
        try {
            questionAnswerModel = questionAnswerModels.get(position);
            alreadyAnswered = true;
            answer = questionAnswerModel.value;
            simpleDateFormat = new SimpleDateFormat(TIME_FORMAT);
            Date date1 = simpleDateFormat.parse(answer);
            initTimePicker(date1);

        } catch (IndexOutOfBoundsException e){
            questionAnswerModel = new QuestionAnswerModel();
            alreadyAnswered = false;
            initTimePicker(new Date());
            simpleDateFormat = new SimpleDateFormat(TIME_FORMAT);
            answer = simpleDateFormat.format(new Date());
        } catch (Exception e) {
            answer = "";
        }

        Log.d(TAG, "checkIfAlreadyAnswered() " + alreadyAnswered);

    }

    public void saveAnswer() {
        if (alreadyAnswered) {
            questionAnswerModel.value = answer;
            callback.updateAnswer(position, questionAnswerModel);
        } else {
            questionAnswerModel.label = formQuestionModel.label;
            questionAnswerModel.type = formQuestionModel.type;
            questionAnswerModel.value = answer;
            questionAnswerModel.formId = formQuestionModel.formId;
            callback.addAnswer(questionAnswerModel);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

}
