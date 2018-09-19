package com.bilal.datacollectionform.fragment;


import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

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
public class DateAnswerFragment extends Fragment {

    private final static String TAG = "DateAnswerFrag";
    private final static String DATE_FORMAT = "dd/MM/yyyy";

    private Context context;
    private TextView questionTextView;
    private FormQuestionModel formQuestionModel;
    private QuestionAnswerModel questionAnswerModel;
    private SimpleDateFormat simpleDateFormat;
    private Date date;
    private DatePicker datePicker;
    private List<QuestionAnswerModel> questionAnswerModels;
    private TextView errorTextview;

    private boolean alreadyAnswered;
    private int position;
    private String answer;
    private boolean required;

    private CallbackHelper.FragmentAnswerCallback callback;
    private CallbackHelper.FragmentCallback fragmentCallback;


    public DateAnswerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_date_answer, container, false);

        context = getActivity();
        callback = (CallbackHelper.FragmentAnswerCallback) getActivity();
        fragmentCallback = (CallbackHelper.FragmentCallback) getActivity();
        fragmentCallback.setCurrentFragment(this);
        int questionKey = getArguments().getInt(FormQuestionActivity.BUNDLE_ARG_QUESTION_KEY);
        position = getArguments().getInt(FormQuestionActivity.BUNDLE_ARG_POSITION);

        Log.d(TAG, "onCreateView, position " + position);

        questionTextView = v.findViewById(R.id.textview_question);
        datePicker = v.findViewById(R.id.datepicker);
        errorTextview = v.findViewById(R.id.textview_error);
        errorTextview.setVisibility(View.GONE);

        formQuestionModel = FormQuestionModel.getModelForPrimaryKey(context, questionKey);
        if (formQuestionModel.required == 1) {
            required = true;
        } else {
            required = false;
        }
        questionTextView.setText(formQuestionModel.label);

        fragmentCallback.setAnswerListInCurrentFragment();
        return v;
    }

    private void initDatePicker(final Date date1) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date1);
        datePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {
                    @Override
                    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        Calendar myCalendar = Calendar.getInstance();
                        myCalendar.set(Calendar.YEAR, year);
                        myCalendar.set(Calendar.MONTH, monthOfYear);
                        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        date = myCalendar.getTime();
                        selectDate();
                    }
                });
    }

    public boolean requirementsSatisfied() {
        if (required) {
            if (answer.trim().equals("")) {
                errorTextview.setVisibility(View.VISIBLE);
                errorTextview.setText("Mandatory to answer this question, cannot be skipped");
                return false;
            } else {
                errorTextview.setVisibility(View.GONE);
                return true;
            }
        } else {
            return true;
        }
    }

    private void selectDate() {
        simpleDateFormat = new SimpleDateFormat(DATE_FORMAT);
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
            simpleDateFormat = new SimpleDateFormat(DATE_FORMAT);
            Date date = simpleDateFormat.parse(answer);
            initDatePicker(date);

        } catch (IndexOutOfBoundsException e){
            questionAnswerModel = new QuestionAnswerModel();
            alreadyAnswered = false;
            answer = "";
            initDatePicker(new Date());
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
