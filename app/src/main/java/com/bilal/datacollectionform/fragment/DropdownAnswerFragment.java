package com.bilal.datacollectionform.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.bilal.datacollectionform.R;
import com.bilal.datacollectionform.activity.FormQuestionActivity;
import com.bilal.datacollectionform.helper.CallbackHelper;
import com.bilal.datacollectionform.model.FormAnswerModel;
import com.bilal.datacollectionform.model.FormQuestionModel;
import com.bilal.datacollectionform.model.QuestionAnswerModel;

import java.util.LinkedList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class DropdownAnswerFragment extends Fragment {

    private final static String TAG = "DateAnswerFrag";

    private Context context;
    private TextView questionTextView;
    private Spinner spinner;
    private FormQuestionModel formQuestionModel;
    private QuestionAnswerModel questionAnswerModel;
    private String[] optionsArray;
    private List<QuestionAnswerModel> questionAnswerModels;
    private TextView errorTextview;

    private boolean alreadyAnswered;
    private int position;
    private String answer;
    private boolean required;

    private CallbackHelper.FragmentAnswerCallback callback;
    private CallbackHelper.FragmentCallback fragmentCallback;


    public DropdownAnswerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_dropdown_answer, container, false);

        context = getActivity();
        callback = (CallbackHelper.FragmentAnswerCallback) getActivity();
        fragmentCallback = (CallbackHelper.FragmentCallback) getActivity();
        fragmentCallback.setCurrentFragment(this);
        int questionKey = getArguments().getInt(FormQuestionActivity.BUNDLE_ARG_QUESTION_KEY);
        position = getArguments().getInt(FormQuestionActivity.BUNDLE_ARG_POSITION);

        Log.d(TAG, "onCreateView, position " + position);

        questionTextView = v.findViewById(R.id.textview_question);
        spinner = v.findViewById(R.id.spinner);
        errorTextview = v.findViewById(R.id.textview_error);
        errorTextview.setVisibility(View.GONE);

        formQuestionModel = FormQuestionModel.getModelForPrimaryKey(context, questionKey);
        questionTextView.setText(formQuestionModel.label);
        if (formQuestionModel.required == 1) {
            required = true;
        } else {
            required = false;
        }
        createOptionArrayForSpinner();


        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(context,
                android.R.layout.simple_spinner_item, optionsArray);
        spinner.setAdapter(arrayAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                answer = optionsArray[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        fragmentCallback.setAnswerListInCurrentFragment();

        return v;
    }

    private void createOptionArrayForSpinner() {
        optionsArray = new String[formQuestionModel.optionList.size()];
        for(int i = 0; i < formQuestionModel.optionList.size(); i++) {
            optionsArray[i] = formQuestionModel.optionList.get(i).value;
        }
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

        } catch (IndexOutOfBoundsException e){
            questionAnswerModel = new QuestionAnswerModel();
            alreadyAnswered = false;
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
