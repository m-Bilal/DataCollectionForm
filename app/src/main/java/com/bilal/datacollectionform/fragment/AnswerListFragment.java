package com.bilal.datacollectionform.fragment;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bilal.datacollectionform.R;
import com.bilal.datacollectionform.activity.FormQuestionActivity;
import com.bilal.datacollectionform.helper.CallbackHelper;
import com.bilal.datacollectionform.model.FormAnswerModel;
import com.bilal.datacollectionform.model.QuestionAnswerModel;

import java.util.List;

import io.realm.RealmResults;

/**
 * A simple {@link Fragment} subclass.
 */
public class AnswerListFragment extends Fragment {

    private static final String TAG = "AnswerListFrag";

    private Context context;

    private List<QuestionAnswerModel> questionAnswerModels;
    private RecyclerView recyclerView;
    MyRecyclerViewAdapter adapter;
    private CallbackHelper.FragmentCallback fragmentCallback;

    public AnswerListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_answer_list, container, false);
        context = getActivity();
        fragmentCallback = (CallbackHelper.FragmentCallback) getActivity();
        fragmentCallback.setCurrentFragment(this);

        recyclerView = v.findViewById(R.id.recyclerview);
        int key = getArguments().getInt(FormQuestionActivity.BUNDLE_ARG_QUESTION_KEY);
        FormAnswerModel formAnswerModel = FormAnswerModel.getModelForPrimaryKey(context, key);
        questionAnswerModels = formAnswerModel.questionAnswerModelRealmList;
        //questionAnswerModels = QuestionAnswerModel.getAllModelsForFormId(context, formAnswerModel);
        formAnswerModel.saveJson(context);

        adapter = new MyRecyclerViewAdapter();
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        syncFormWithServer(formAnswerModel);

        return v;
    }

    private void syncFormWithServer(FormAnswerModel formAnswerModel) {
        final ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Loading");
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(true);

        FormAnswerModel.syncUploadToServer(context, formAnswerModel, new CallbackHelper.IntCallback() {
            @Override
            public void onSuccess(int response) {
                progressDialog.dismiss();
                if (response == 1) {
                    Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure() {
                progressDialog.dismiss();
            }
        });

    }

    public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.MyViewHolder> {

        public class MyViewHolder extends RecyclerView.ViewHolder {
            TextView questionTextview;
            TextView answerTextview;

            public MyViewHolder(View view) {
                super(view);
                questionTextview = view.findViewById(R.id.textview_question);
                answerTextview = view.findViewById(R.id.textview_answer);
            }
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_question_answer, parent, false);

            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, int position) {
            holder.questionTextview.setText(questionAnswerModels.get(position).label);
            holder.answerTextview.setText(questionAnswerModels.get(position).value);
        }

        @Override
        public int getItemCount() {
            return questionAnswerModels.size();
        }
    }
}
