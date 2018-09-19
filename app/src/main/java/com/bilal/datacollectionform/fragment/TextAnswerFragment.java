package com.bilal.datacollectionform.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.bilal.datacollectionform.R;
import com.bilal.datacollectionform.activity.FormQuestionActivity;
import com.bilal.datacollectionform.helper.CallbackHelper;
import com.bilal.datacollectionform.helper.Helper;
import com.bilal.datacollectionform.model.FormAnswerModel;
import com.bilal.datacollectionform.model.FormQuestionModel;
import com.bilal.datacollectionform.model.QuestionAnswerModel;

import java.util.LinkedList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class TextAnswerFragment extends Fragment {

    private final static String TAG = "TextAnswerFrag";

    private Context context;
    private TextView questionTextview;
    private EditText answerEdittext;
    private FormQuestionModel formQuestionModel;
    private QuestionAnswerModel questionAnswerModel;
    private List<QuestionAnswerModel> questionAnswerModels;
    private TextView errorTextview;

    private boolean alreadyAnswered;
    private int position;
    private boolean required;

    private CallbackHelper.FragmentAnswerCallback callback;
    private CallbackHelper.FragmentCallback fragmentCallback;

    public TextAnswerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_text_answer, container, false);

        context = getActivity();
        callback = (CallbackHelper.FragmentAnswerCallback) getActivity();
        fragmentCallback = (CallbackHelper.FragmentCallback) getActivity();
        fragmentCallback.setCurrentFragment(this);
        int questionKey = getArguments().getInt(FormQuestionActivity.BUNDLE_ARG_QUESTION_KEY);
        position = getArguments().getInt(FormQuestionActivity.BUNDLE_ARG_POSITION);

        Log.d(TAG, "onCreateView, position " + position);


        questionTextview = v.findViewById(R.id.textview_question);
        answerEdittext = v.findViewById(R.id.edittext_answer);
        errorTextview = v.findViewById(R.id.textview_error);
        errorTextview.setVisibility(View.GONE);

        formQuestionModel = FormQuestionModel.getModelForPrimaryKey(context, questionKey);
        questionTextview.setText(formQuestionModel.label);
        if (formQuestionModel.required == 1) {
            required = true;
        } else {
            required = false;
        }

        if (!formQuestionModel.type.equalsIgnoreCase(FormQuestionModel.TYPE_PARAGRAPH)) {
            answerEdittext.setMaxLines(1);
        }

        fragmentCallback.setAnswerListInCurrentFragment();
        return v;
    }

    public boolean emailValid() {
        return Helper.validateEmail(answerEdittext.getText().toString());
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
            answerEdittext.setText(questionAnswerModel.value);
            Log.d(TAG, "checkIfAlreadyAnswered, true");
        } catch (IndexOutOfBoundsException e) {
            questionAnswerModel = new QuestionAnswerModel();
            alreadyAnswered = false;
            Log.d(TAG, "checkIfAlreadyAnswered, false");
        }
        Log.d(TAG, "checkIfAlreadyAnswered() " + alreadyAnswered);

    }

    private void closeKeyboard(View view) {
        InputMethodManager inputManager = (InputMethodManager)
                context.getSystemService(Context.INPUT_METHOD_SERVICE);

        inputManager.hideSoftInputFromWindow(view.getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public boolean requirementsSatisfied() {
        String answer = answerEdittext.getText().toString();

        if (required) {
            if (answer.trim().equals("")) {
                errorTextview.setVisibility(View.VISIBLE);
                errorTextview.setText("Mandatory to answer this question, cannot be skipped");
                return false;
            } else {
                if (formQuestionModel.type.equals(FormQuestionModel.TYPE_EMAIL)) {
                    if (emailValid()) {
                        errorTextview.setVisibility(View.GONE);
                        return true;
                    } else {
                        errorTextview.setVisibility(View.VISIBLE);
                        errorTextview.setText("Please enter a valid email");
                        return false;
                    }
                } else {
                    errorTextview.setVisibility(View.GONE);
                    return true;
                }
            }
        } else {
            if (!answer.trim().equals("") && formQuestionModel.type.equals(FormQuestionModel.TYPE_EMAIL)) {
                if (emailValid()) {
                    errorTextview.setVisibility(View.GONE);
                    return true;
                } else {
                    errorTextview.setVisibility(View.VISIBLE);
                    errorTextview.setText("Please enter a valid email");
                    return false;
                }
            } else {
                return true;
            }
        }
    }

    public void saveAnswer() {
        closeKeyboard(answerEdittext);
        if (alreadyAnswered) {
            questionAnswerModel.value = answerEdittext.getText().toString();
            callback.updateAnswer(position, questionAnswerModel);
        } else {
            questionAnswerModel.label = formQuestionModel.label;
            questionAnswerModel.type = formQuestionModel.type;
            questionAnswerModel.value = answerEdittext.getText().toString();
            questionAnswerModel.formId = formQuestionModel.formId;
            callback.addAnswer(questionAnswerModel);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}
