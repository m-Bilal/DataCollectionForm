package com.bilal.datacollectionform.fragment;


import android.app.ProgressDialog;
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
import android.widget.TextView;
import android.widget.Toast;

import com.bilal.datacollectionform.R;
import com.bilal.datacollectionform.activity.FormQuestionActivity;
import com.bilal.datacollectionform.helper.CallbackHelper;
import com.bilal.datacollectionform.model.FormAnswerModel;
import com.bilal.datacollectionform.model.FormModel;
import com.bilal.datacollectionform.model.PrimaryKeyModel;
import com.bilal.datacollectionform.model.QuestionAnswerModel;
import com.bilal.datacollectionform.model.UserModel;

import java.util.LinkedList;
import java.util.List;

import io.realm.RealmList;

/**
 * A simple {@link Fragment} subclass.
 */
public class AnswerListFragment extends Fragment {

    private static final String TAG = "AnswerListFrag";

    private Context context;

    private List<QuestionAnswerModel> questionAnswerModels;
    private RealmList<QuestionAnswerModel> questionAnswerModelRealmList;
    private FormModel formModel;
    private FormAnswerModel formAnswerModel;
    private RecyclerView recyclerView;
    MyRecyclerViewAdapter adapter;
    private CallbackHelper.FragmentCallback fragmentCallback;

    private double longitude;
    private double latitude;
    private long time;

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
        questionAnswerModelRealmList =  new RealmList<>();

        recyclerView = v.findViewById(R.id.recyclerview);
        int formId = getArguments().getInt(FormQuestionActivity.BUNDLE_ARG_ANSWER_FORM_KEY);
        time = getArguments().getLong(FormQuestionActivity.BUNDLE_ARG_TIME);
        longitude = getArguments().getDouble(FormQuestionActivity.BUNDLE_ARG_LONGITUDE);
        latitude = getArguments().getDouble(FormQuestionActivity.BUNDLE_ARG_LATITUDE);
        formModel = FormModel.getFromForId(context, formId);
        //FormAnswerModel formAnswerModel = FormAnswerModel.getModelForPrimaryKey(context, key);
        questionAnswerModels = new LinkedList<>();
        //questionAnswerModels = formAnswerModel.questionAnswerModelRealmList;
        //questionAnswerModels = QuestionAnswerModel.getAllModelsForFormId(context, formAnswerModel);
        //formAnswerModel.saveJson(context);

        adapter = new MyRecyclerViewAdapter();
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        fragmentCallback.setAnswerListInCurrentFragment();
        //saveFormToRealm(formAnswerModel);

        return v;
    }

    private void createFormAnswerModel() {
        formAnswerModel = FormAnswerModel.createModel(context, UserModel.getUserFromRealm(context), time,
                latitude, longitude, formModel, questionAnswerModelRealmList);
        formAnswerModel.saveJson(context);

    }

    private void allotPrimaryKeysToQuestionAnswerModels() {
        for(QuestionAnswerModel model : questionAnswerModels) {
            model.primaryKey = PrimaryKeyModel.getQuestionAnswerPrimaryKey(context);
            questionAnswerModelRealmList.add(model);
        }
    }

    public void setAnswerList(LinkedList<QuestionAnswerModel> questionAnswerModels) {
        Log.d(TAG, "setAnswerList()");
        this.questionAnswerModels = questionAnswerModels;
        allotPrimaryKeysToQuestionAnswerModels();
        createFormAnswerModel();
        adapter.notifyDataSetChanged();
    }

    private void saveFormToRealm(FormAnswerModel formAnswerModel) {
        formAnswerModel.saveJson(context);
        FormAnswerModel.setSyncedWithServer(context, formAnswerModel,false);
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
