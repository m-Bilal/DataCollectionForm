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
    private FormAnswerModel formAnswerModel;
    private MyRecyclerViewAdapter adapter;

    private boolean alreadyAnswered;
    private int position;
    private String answer;

    private CallbackHelper.FragmentAnswerCallback callback;

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
        int questionKey = getArguments().getInt(FormQuestionActivity.BUNDLE_ARG_QUESTION_KEY);
        int formAnswerKey = getArguments().getInt(FormQuestionActivity.BUNDLE_ARG_ANSWER_FORM_KEY);
        position = getArguments().getInt(FormQuestionActivity.BUNDLE_ARG_POSITION);

        recyclerView = v.findViewById(R.id.recyclerview);
        textView = v.findViewById(R.id.textview_question);

        adapter = new MyRecyclerViewAdapter();
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        formQuestionModel = FormQuestionModel.getModelForPrimaryKey(context, questionKey);
        formAnswerModel = FormAnswerModel.getModelForPrimaryKey(context, formAnswerKey);
        textView.setText(formQuestionModel.label);
        checkIfAlreadyAnswered();

        return v;
    }

    private void checkIfAlreadyAnswered() {
        try {
            questionAnswerModel = formAnswerModel.questionAnswerModelRealmList.get(position);
            questionAnswerModel = new QuestionAnswerModel(questionAnswerModel);
            answer = questionAnswerModel.value;
            alreadyAnswered = true;
        } catch (Exception e){
            questionAnswerModel = new QuestionAnswerModel();
            alreadyAnswered = false;
            answer = "";
        }
        finally {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onPause() {
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
        super.onPause();
    }

    public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.MyViewHolder> {

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
                        if (checkBox.isChecked() == true) {
                            checkBox.setChecked(false);
                            answer = "";
                        } else {
                            checkBox.setChecked(true);
                            int pos = getAdapterPosition();
                            answer = formQuestionModel.optionList.get(pos).value;
                        }
                        adapter.notifyDataSetChanged();
                    }
                });

                checkBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (checkBox.isChecked() == true) {
                            checkBox.setChecked(false);
                            answer = "";
                        } else {
                            checkBox.setChecked(true);
                            int pos = getAdapterPosition();
                            answer = formQuestionModel.optionList.get(pos).value;
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
            if (formQuestionModel.optionList.get(position).value.equalsIgnoreCase(answer)) {
                holder.checkBox.setChecked(true);
            }

        }

        @Override
        public int getItemCount() {
            return formQuestionModel.optionList.size();
        }
    }
}