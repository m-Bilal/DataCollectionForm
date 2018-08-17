package com.bilal.datacollectionform.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.bilal.datacollectionform.R;
import com.bilal.datacollectionform.activity.FormQuestionActivity;
import com.bilal.datacollectionform.helper.CallbackHelper;
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

    private CallbackHelper.FragmentAnswerCallback callback;

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
        questionTextview = v.findViewById(R.id.textview_question);
        answerEdittext = v.findViewById(R.id.edittext_answer);
        int key = getArguments().getInt(FormQuestionActivity.BUNDLE_ARG_KEY);
        formQuestionModel = FormQuestionModel.getModelForPrimaryKey(context, key);
        questionTextview.setText(formQuestionModel.label);
        return v;
    }

    @Override
    public void onPause() {
        questionAnswerModel = new QuestionAnswerModel();
        questionAnswerModel.label = formQuestionModel.label;
        questionAnswerModel.type = formQuestionModel.type;
        questionAnswerModel.value = answerEdittext.getText().toString();
        questionAnswerModel.formId = formQuestionModel.formId;
        callback.addAnswer(questionAnswerModel);
        super.onPause();
    }
}
