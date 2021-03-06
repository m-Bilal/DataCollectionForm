package com.bilal.datacollectionform.fragment;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.bilal.datacollectionform.R;
import com.bilal.datacollectionform.activity.FormQuestionActivity;
import com.bilal.datacollectionform.helper.CallbackHelper;
import com.bilal.datacollectionform.helper.FileChooser;
import com.bilal.datacollectionform.helper.Helper;
import com.bilal.datacollectionform.model.FormAnswerModel;
import com.bilal.datacollectionform.model.FormQuestionModel;
import com.bilal.datacollectionform.model.QuestionAnswerModel;

import java.util.LinkedList;
import java.util.List;

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
    private Uri answerUri;
    private List<QuestionAnswerModel> questionAnswerModels;
    private TextView errorTextview;

    private boolean alreadyAnswered;
    private int position;
    private String answer;
    private boolean required;

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
        position = getArguments().getInt(FormQuestionActivity.BUNDLE_ARG_POSITION);

        Log.d(TAG, "onCreateView, position " + position);

        questionTextView = v.findViewById(R.id.textview_question);
        button = v.findViewById(R.id.button);
        selectedFileTextview = v.findViewById(R.id.textview_selected_file);
        errorTextview = v.findViewById(R.id.textview_error);
        errorTextview.setVisibility(View.GONE);

        formQuestionModel = FormQuestionModel.getModelForPrimaryKey(context, questionKey);
        questionTextView.setText(formQuestionModel.label);
        if (formQuestionModel.required == 1) {
            required = true;
        } else {
            required = false;
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImageSelector();
            }
        });

        fragmentCallback.setAnswerListInCurrentFragment();
        return v;
    }

    private void openImageSelector() {
        Intent intent = new Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        getActivity().startActivityForResult(Intent.createChooser(intent, "Select a picture"),
                FormQuestionActivity.INTENT_SELECT_IMAGE_REQUEST_CODE);
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

    public void setResultUri(Uri uri) {
        answerUri = uri;
        answer = uri.toString();
        selectedFileTextview.setText("Selected Image : " + Helper.getFileName(context, Uri.parse(answer)));
    }

    public void setAnswerList(LinkedList<QuestionAnswerModel> questionAnswerModels) {
        Log.d(TAG, "setAnswerList()");
        this.questionAnswerModels = questionAnswerModels;
        checkIfAlreadyAnswered();
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

    private void checkIfAlreadyAnswered() {
        try {
            questionAnswerModel = questionAnswerModels.get(position);
            alreadyAnswered = true;
            answer = questionAnswerModel.value;
            selectedFileTextview.setText("Selected Image : " + Helper.getFileName(context, Uri.parse(answer)));

        } catch (IndexOutOfBoundsException e){
            questionAnswerModel = new QuestionAnswerModel();
            alreadyAnswered = false;
            answer = "";
            selectedFileTextview.setText(answer);
        } catch (Exception e) {
            selectedFileTextview.setText("No Image Selected");
        }

        Log.d(TAG, "checkIfAlreadyAnswered() " + alreadyAnswered);
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}
