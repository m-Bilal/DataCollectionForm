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
    private FormAnswerModel formAnswerModel;

    private boolean alreadyAnswered;
    private int position;

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
        int formAnswerKey = getArguments().getInt(FormQuestionActivity.BUNDLE_ARG_ANSWER_FORM_KEY);
        position = getArguments().getInt(FormQuestionActivity.BUNDLE_ARG_POSITION);

        questionTextview = v.findViewById(R.id.textview_question);
        answerEdittext = v.findViewById(R.id.edittext_answer);
        formQuestionModel = FormQuestionModel.getModelForPrimaryKey(context, questionKey);
        formAnswerModel = FormAnswerModel.getModelForPrimaryKey(context, formAnswerKey);
        questionTextview.setText(formQuestionModel.label);

        if (!formQuestionModel.type.equalsIgnoreCase(FormQuestionModel.TYPE_PARAGRAPH)) {
            answerEdittext.setMaxLines(1);
        }

        checkIfAlreadyAnswered();
        return v;
    }

    public boolean emailValid() {
        return Helper.validateEmail(answerEdittext.getText().toString());
    }

    private void checkIfAlreadyAnswered() {
        try {
            questionAnswerModel = formAnswerModel.questionAnswerModelRealmList.get(position);
            questionAnswerModel = new QuestionAnswerModel(questionAnswerModel);
            alreadyAnswered = true;
            answerEdittext.setText(questionAnswerModel.value);
            Log.d(TAG, "checkIfAlreadyAnswered, true");
        } catch (Exception e){
            questionAnswerModel = new QuestionAnswerModel();
            alreadyAnswered = false;
            Log.d(TAG, "checkIfAlreadyAnswered, false");
        }
    }

    private void closeKeyboard(View view) {
        InputMethodManager inputManager = (InputMethodManager)
                context.getSystemService(Context.INPUT_METHOD_SERVICE);

        inputManager.hideSoftInputFromWindow(view.getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public void saveAnswer() {
        closeKeyboard(answerEdittext);
        if (alreadyAnswered) {
            questionAnswerModel.value = answerEdittext.getText().toString();
            callback.updateAnswer(questionAnswerModel);
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
