package com.bilal.datacollectionform.fragment;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
public class ImageAnswerFragment extends Fragment {

    private final static String TAG = "ImageAnswerFrag";

    private Context context;
    private TextView questionTextView;
    private TextView selectedFileTextview;
    private Button button;
    private FormQuestionModel formQuestionModel;
    private QuestionAnswerModel questionAnswerModel;
    private FormAnswerModel formAnswerModel;
    private Uri answerUri;

    private boolean alreadyAnswered;
    private int position;
    private String answer;

    private CallbackHelper.FragmentAnswerCallback callback;
    private CallbackHelper.FragmentCallback fragmentCallback;


    public ImageAnswerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_image_answer, container, false);

        context = getActivity();
        callback = (CallbackHelper.FragmentAnswerCallback) getActivity();
        fragmentCallback = (CallbackHelper.FragmentCallback) getActivity();
        fragmentCallback.setCurrentFragment(this);
        int questionKey = getArguments().getInt(FormQuestionActivity.BUNDLE_ARG_QUESTION_KEY);
        int formAnswerKey = getArguments().getInt(FormQuestionActivity.BUNDLE_ARG_ANSWER_FORM_KEY);
        position = getArguments().getInt(FormQuestionActivity.BUNDLE_ARG_POSITION);

        questionTextView = v.findViewById(R.id.textview_question);
        button = v.findViewById(R.id.button);
        selectedFileTextview = v.findViewById(R.id.textview_selected_file);

        formQuestionModel = FormQuestionModel.getModelForPrimaryKey(context, questionKey);
        formAnswerModel = FormAnswerModel.getModelForPrimaryKey(context, formAnswerKey);
        questionTextView.setText(formQuestionModel.label);
        checkIfAlreadyAnswered();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImageSelector();
            }
        });

        return v;
    }

    private void openImageSelector() {
        Intent intent = new Intent()
                .setType("image/*")
                .setAction(Intent.ACTION_GET_CONTENT);

        getActivity().startActivityForResult(Intent.createChooser(intent, "Select a picture"),
                FormQuestionActivity.INTENT_SELECT_IMAGE_REQUEST_CODE);
    }

    public void setResultUri(Uri uri) {
        answerUri = uri;
        answer = uri.toString();
        selectedFileTextview.setText("Selected Image : " + answer);
    }

    public void saveAnswer() {
        if (alreadyAnswered) {
            questionAnswerModel.value = answer;
            callback.updateAnswer(questionAnswerModel);
        } else {
            questionAnswerModel.label = formQuestionModel.label;
            questionAnswerModel.type = formQuestionModel.type;
            questionAnswerModel.value = answer;
            questionAnswerModel.formId = formQuestionModel.formId;
            callback.addAnswer(questionAnswerModel);
        }
    }

    private void checkIfAlreadyAnswered() {
        try {
            questionAnswerModel = formAnswerModel.questionAnswerModelRealmList.get(position);
            questionAnswerModel = new QuestionAnswerModel(questionAnswerModel);
            alreadyAnswered = true;
            answer = questionAnswerModel.value;
            selectedFileTextview.setText("Selected Image : " + answer);

        } catch (Exception e){
            questionAnswerModel = new QuestionAnswerModel();
            alreadyAnswered = false;
            answer = "";
            selectedFileTextview.setText("Selected Image : " + answer);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}
