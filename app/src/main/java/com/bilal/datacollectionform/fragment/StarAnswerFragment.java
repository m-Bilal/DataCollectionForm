package com.bilal.datacollectionform.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
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
public class StarAnswerFragment extends Fragment {

    private final static String TAG = "StarAnswerFrag";

    private Context context;
    private RatingBar ratingBar;
    private TextView textView;
    private FormQuestionModel formQuestionModel;
    private QuestionAnswerModel questionAnswerModel;
    private List<QuestionAnswerModel> questionAnswerModels;
    private TextView errorTextview;

    private boolean alreadyAnswered;
    private int position;
    private String answer;
    private boolean required;

    private CallbackHelper.FragmentAnswerCallback callback;
    private CallbackHelper.FragmentCallback fragmentCallback;

    public StarAnswerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_star_answer, container, false);

        context = getActivity();
        callback = (CallbackHelper.FragmentAnswerCallback) getActivity();
        fragmentCallback = (CallbackHelper.FragmentCallback) getActivity();
        fragmentCallback.setCurrentFragment(this);
        int questionKey = getArguments().getInt(FormQuestionActivity.BUNDLE_ARG_QUESTION_KEY);
        position = getArguments().getInt(FormQuestionActivity.BUNDLE_ARG_POSITION);

        Log.d(TAG, "onCreateView, position " + position);


        ratingBar = v.findViewById(R.id.ratingbar);
        textView = v.findViewById(R.id.textview_question);
        errorTextview = v.findViewById(R.id.textview_error);
        errorTextview.setVisibility(View.GONE);

        formQuestionModel = FormQuestionModel.getModelForPrimaryKey(context, questionKey);
        textView.setText(formQuestionModel.label);
        if (formQuestionModel.required == 1) {
            required = true;
        } else {
            required = false;
        }

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                answer = "" + rating;
            }
        });

        fragmentCallback.setAnswerListInCurrentFragment();
        return v;
    }

    public boolean requirementsSatisfied() {
        if (required) {
            if (answer.trim().equals("0")) {
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
            answer = questionAnswerModel.value;
            ratingBar.setRating(Float.parseFloat(answer));
            alreadyAnswered = true;
        } catch (IndexOutOfBoundsException e) {
            questionAnswerModel = new QuestionAnswerModel();
            alreadyAnswered = false;
            answer = "0";
        } finally {
            Log.d(TAG, "checkIfAlreadyAnswered() " + alreadyAnswered);
            ratingBar.setRating(Float.parseFloat(answer));
        }
    }

    public void saveAnswer() {
        if (alreadyAnswered) {
            questionAnswerModel.value = answer;
            callback.updateAnswer(position, questionAnswerModel);
        } else {
            questionAnswerModel = new QuestionAnswerModel();
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
