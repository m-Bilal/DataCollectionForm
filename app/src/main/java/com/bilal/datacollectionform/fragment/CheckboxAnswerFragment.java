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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bilal.datacollectionform.R;
import com.bilal.datacollectionform.activity.FormQuestionActivity;
import com.bilal.datacollectionform.helper.CallbackHelper;
import com.bilal.datacollectionform.model.FormAnswerModel;
import com.bilal.datacollectionform.model.FormQuestionModel;
import com.bilal.datacollectionform.model.QuestionAnswerModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class CheckboxAnswerFragment extends Fragment {

    private final static String TAG = "CheckboxAnswerFrag";

    private Context context;
    private RecyclerView recyclerView;
    private TextView textView;
    private FormQuestionModel formQuestionModel;
    private QuestionAnswerModel questionAnswerModel;
    //private FormAnswerModel formAnswerModel;
    private MyRecyclerViewAdapter adapter;
    private List<String> answerList;
    private List<QuestionAnswerModel> questionAnswerModels;

    private boolean alreadyAnswered;
    private int position;

    private CallbackHelper.FragmentAnswerCallback callback;
    private CallbackHelper.FragmentCallback fragmentCallback;

    public CheckboxAnswerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_checkbox_answer, container, false);

        context = getActivity();
        callback = (CallbackHelper.FragmentAnswerCallback) getActivity();
        fragmentCallback = (CallbackHelper.FragmentCallback) getActivity();
        fragmentCallback.setCurrentFragment(this);
        int questionKey = getArguments().getInt(FormQuestionActivity.BUNDLE_ARG_QUESTION_KEY);
        int formAnswerKey = getArguments().getInt(FormQuestionActivity.BUNDLE_ARG_ANSWER_FORM_KEY);
        position = getArguments().getInt(FormQuestionActivity.BUNDLE_ARG_POSITION);

        Log.d(TAG, "onCreateView, position " + position);

        recyclerView = v.findViewById(R.id.recyclerview);
        textView = v.findViewById(R.id.textview_question);

        answerList = new ArrayList<>();
        adapter = new MyRecyclerViewAdapter();
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        formQuestionModel = FormQuestionModel.getModelForPrimaryKey(context, questionKey);
        //formAnswerModel = FormAnswerModel.getModelForPrimaryKey(context, formAnswerKey);
        textView.setText(formQuestionModel.label);

        fragmentCallback.setAnswerListInCurrentFragment();
        return v;
    }

    public void setAnswerList(LinkedList<QuestionAnswerModel> questionAnswerModels) {
        Log.d(TAG, "setAnswerList()");
        this.questionAnswerModels = questionAnswerModels;
        checkIfAlreadyAnswered();
    }

    private void checkIfAlreadyAnswered() {
        try {
            questionAnswerModel = questionAnswerModels.get(position);
            parseAnswer();
            alreadyAnswered = true;
        } catch (Exception e){
            questionAnswerModel = new QuestionAnswerModel();
            alreadyAnswered = false;
            answerList = new ArrayList<>();
        }
        finally {
            Log.d(TAG, "checkIfAlreadyAnswered " + alreadyAnswered);
            adapter.notifyDataSetChanged();
        }
    }

    private void parseAnswer() {
        answerList = new ArrayList<>();
        String arr[] = questionAnswerModel.value.split(",");
        Log.d(TAG, "parseAnswer: value: " + questionAnswerModel.value);
        for (String i : arr) {
            answerList.add(i);
        }
        //answerList = Arrays.asList(arr);
        for (String i : answerList) {
            Log.d(TAG, "parseAnswer: " + i);
        }
    }

    private String getAnswersAsString() {
        StringBuilder result = new StringBuilder();
        for (String i : answerList) {
            result.append(i);
            result.append(",");
        }
        Log.d(TAG, "getAnsAsString : " +result.toString());
        return result.toString();
    }

    public void saveAnswer() {
        if (alreadyAnswered) {
            questionAnswerModel.value = getAnswersAsString();
            callback.updateAnswer(position, questionAnswerModel);
        } else {
            questionAnswerModel.label = formQuestionModel.label;
            questionAnswerModel.type = formQuestionModel.type;
            questionAnswerModel.value = getAnswersAsString();
            questionAnswerModel.formId = formQuestionModel.formId;
            callback.addAnswer(questionAnswerModel);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.MyViewHolder> {

        public class MyViewHolder extends RecyclerView.ViewHolder {
            TextView textView;
            CheckBox checkBox;
            LinearLayout linearLayout;


            public MyViewHolder(View view) {
                super(view);
                textView = view.findViewById(R.id.textview);
                checkBox = view.findViewById(R.id.checkbox);
                linearLayout = view.findViewById(R.id.linear_layout);

                linearLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (checkBox.isChecked()) {
                            checkBox.setChecked(false);
                            answerList.remove(textView.getText().toString());
                        } else {
                            checkBox.setChecked(true);
                            answerList.add(textView.getText().toString());
                        }
                        adapter.notifyDataSetChanged();
                    }
                });

                checkBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (checkBox.isChecked()) {
                            answerList.add(textView.getText().toString());
                        } else {
                            answerList.remove(textView.getText().toString());
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_checkbox, parent, false);

            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, int position) {
            holder.textView.setText(formQuestionModel.optionList.get(position).value);
            if (answerList.contains(holder.textView.getText().toString())) {
                holder.checkBox.setChecked(true);
            } else {
                holder.checkBox.setChecked(false);
            }

        }

        @Override
        public int getItemCount() {
            return formQuestionModel.optionList.size();
        }
    }
}