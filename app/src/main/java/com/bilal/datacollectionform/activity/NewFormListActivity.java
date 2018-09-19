package com.bilal.datacollectionform.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bilal.datacollectionform.R;
import com.bilal.datacollectionform.helper.CallbackHelper;
import com.bilal.datacollectionform.model.FormModel;
import com.bilal.datacollectionform.model.FormQuestionModel;
import com.bilal.datacollectionform.model.UserModel;

import java.util.ArrayList;
import java.util.List;

public class NewFormListActivity extends AppCompatActivity {


    private Context context;

    private Toolbar toolbar;

    private RecyclerView mRecyclerView;
    private MyRecyclerViewAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private ProgressDialog progressDialog;

    private List<FormModel> formModelList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_form_list);

        context = this;

        progressDialog = new ProgressDialog(context);
        progressDialog.setCanceledOnTouchOutside(false);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        formModelList = new ArrayList<>();
        mRecyclerView = findViewById(R.id.recyclerview);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new MyRecyclerViewAdapter();
        mRecyclerView.setAdapter(mAdapter);

        syncAllForms();
    }

    private void syncAllForms() {
        progressDialog.setMessage("Loading");
        progressDialog.show();
        FormModel.syncAllForms(context, UserModel.getUserFromRealm(context), new CallbackHelper.Callback() {
            @Override
            public void onSuccess() {
                progressDialog.dismiss();
                formModelList = FormModel.getAllUnopenedFromRealm(context);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure() {
                progressDialog.dismiss();
                if (FormModel.getAllFromRealm(context).size() == 0) {
                    Toast.makeText(context, "Error, no internet", Toast.LENGTH_SHORT).show();
                } else {
                    formModelList = FormModel.getAllFromRealm(context);
                    mAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void startFormQuestionActivity(int formId) {
        Intent intent = new Intent(context, FormQuestionActivity.class);
        intent.putExtra(FormQuestionActivity.INTENT_ARG_FORM_ID, formId);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_item_notification) {
            Intent intent = new Intent(context, UnsyncedListActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void syncForm(final FormModel formModel1) {
        progressDialog.setMessage("Loading");
        progressDialog.show();
        final FormModel formModel = new FormModel(formModel1);
        FormQuestionModel.syncForm(context, formModel, new CallbackHelper.Callback() {
            @Override
            public void onSuccess() {
                progressDialog.dismiss();
                formModel1.setOpened(context);
                startFormQuestionActivity(formModel.formId);
            }

            @Override
            public void onFailure() {
                progressDialog.dismiss();
                if (FormModel.getFromForId(context, formModel.formId) != null) {
                    startFormQuestionActivity(formModel.formId);
                } else {
                    Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder> {

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            View v = layoutInflater.inflate(R.layout.list_item_form, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.textView.setText(formModelList.get(position).formName);

        }

        @Override
        public int getItemCount() {
            return formModelList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            public TextView textView;
            public CardView cardView;

            public ViewHolder(View v) {
                super(v);
                textView = v.findViewById(R.id.textview);
                cardView = v.findViewById(R.id.cardview);

                cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        syncForm(formModelList.get(getAdapterPosition()));
                    }
                });
                //linearLayout = v.findViewById(R.id.linear_layout);
            }
        }
    }
}
