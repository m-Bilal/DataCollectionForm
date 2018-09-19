package com.bilal.datacollectionform.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bilal.datacollectionform.R;
import com.bilal.datacollectionform.helper.CallbackHelper;
import com.bilal.datacollectionform.model.FileModel;
import com.bilal.datacollectionform.model.FormAnswerModel;
import com.bilal.datacollectionform.model.FormModel;
import com.bilal.datacollectionform.service.FileUploadService;
import com.bilal.datacollectionform.service.FormUploadService;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class UnsyncedListActivity extends AppCompatActivity implements CallbackHelper.FileUploadServiceCallback,
        CallbackHelper.FormUploadServiceCallback {

    private final static String TAG = "UnsyncedListActivity";

    public static boolean isRunning;

    private Context context;
    private Toolbar toolbar;
    private TextView unsyncedFormTextview;
    private TextView unsyncedFileTextview;
    private RecyclerView recyclerView;
    private List<FileModel> unsyncedFileList;
    private List<FormAnswerModel> unsyncedFormList;

    private MyRecyclerViewAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private ProgressDialog fileUploadDialog;
    private ProgressDialog formUploadDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unsynced_list);

        isRunning = true;
        context = this;
        unsyncedFileTextview = findViewById(R.id.textview_unsynced_files);
        unsyncedFormTextview = findViewById(R.id.textview_unsynced_forms);
        recyclerView = findViewById(R.id.recyclerview);

        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        unsyncedFileTextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (unsyncedFileList.size() > 0) {
                    startFileUpload();
                }
            }
        });

        unsyncedFormTextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (unsyncedFormList.size() > 0) {
                    startFormUpload();
                }
            }
        });

        updateViews();
    }

    private void updateViews() {
        unsyncedFileList = FileModel.getAllUnsyncedModels(context);
        unsyncedFormList = FormAnswerModel.getAllUnsyncedModels(context);
        mAdapter = new MyRecyclerViewAdapter();
        recyclerView.setAdapter(mAdapter);
        if (unsyncedFormList.size() > 0) {
            unsyncedFormTextview.setText("Tap to sync " + unsyncedFormList.size() + " forms");
        } else {
            unsyncedFormTextview.setText("No unsynced forms");
        }
        if (unsyncedFileList.size() > 0) {
            unsyncedFileTextview.setText("Tap to sync " + unsyncedFileList.size() + " files");
        } else {
            unsyncedFileTextview.setText("No unsynced files");
        }
    }

    void startFileUpload() {
        FileUploadService.setCallback(this);
        fileUploadDialog = new ProgressDialog(context);
        fileUploadDialog.setTitle("Uploading Files");
        fileUploadDialog.setIndeterminate(false);
        fileUploadDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        fileUploadDialog.setMax(unsyncedFileList.size());
        fileUploadDialog.setCanceledOnTouchOutside(false);
        fileUploadDialog.setButton(DialogInterface.BUTTON_NEUTRAL, "Move to background", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                fileUploadDialog.dismiss();
            }
        });
        fileUploadDialog.show();
        Intent intent = new Intent(this, FileUploadService.class);
        startService(intent);

    }

    void startFormUpload() {
        FormUploadService.setCallback(this);
        formUploadDialog = new ProgressDialog(context);
        formUploadDialog.setTitle("Uploading Forms");
        formUploadDialog.setIndeterminate(false);
        formUploadDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        formUploadDialog.setMax(unsyncedFormList.size());
        formUploadDialog.setCanceledOnTouchOutside(false);
        formUploadDialog.setButton(DialogInterface.BUTTON_NEUTRAL, "Move to background", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                formUploadDialog.dismiss();
            }
        });
        formUploadDialog.show();
        Intent intent = new Intent(this, FormUploadService.class);
        startService(intent);

    }

    @Override
    public void updateFileUpload(int synced, int failed, int total) {
        String message = synced + " out of " + total + " files uploaded, " + failed + " failed.";
        fileUploadDialog.setMessage(message);
        fileUploadDialog.setMax(total);
        fileUploadDialog.setProgress(synced + failed);
    }

    @Override
    public void updateFormUpload(int synced, int failed, int total) {
        String message = synced + " out of " + total + " forms uploaded, " + failed + " failed.";
        formUploadDialog.setMessage(message);
        formUploadDialog.setMax(total);
        formUploadDialog.setProgress(synced + failed);
    }

    @Override
    public void completedFileUpload() {
        fileUploadDialog.dismiss();
        updateViews();
    }

    @Override
    public void completedFormUpload() {
        formUploadDialog.dismiss();
        updateViews();
    }

    @Override
    protected void onPause() {
        isRunning = false;
        super.onPause();
    }

    @Override
    public void setSyncedWithServer(FormAnswerModel model, boolean synced) {
        //model.setSyncedWithServer(context, synced);
    }

    @Override
    public void setSyncedWithServer(FileModel model, boolean synced) {
        //model.setSyncedWithServer(context, synced);
    }

    private class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder> {

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            View v = layoutInflater.inflate(R.layout.list_item_unsynced_form, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.nameTextview.setText(FormModel.getName(context, unsyncedFormList.get(position).formId));
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
            Date date = new Date();
            date.setTime(unsyncedFormList.get(position).captureDate);
            String dateStr = simpleDateFormat.format(date);
            holder.dateTextview.setText(dateStr);
            if (unsyncedFormList.get(position).latitude == -1) {
                holder.latitudeTextview.setText("Not available");
            } else {
                holder.latitudeTextview.setText("" + unsyncedFormList.get(position).latitude);
            }
            if (unsyncedFormList.get(position).longitude == -1) {
                holder.longitudeTextview.setText("Not available");
            } else {
                holder.longitudeTextview.setText("" + unsyncedFormList.get(position).longitude);
            }

        }

        @Override
        public int getItemCount() {
            return unsyncedFormList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            public TextView nameTextview;
            public TextView dateTextview;
            public TextView latitudeTextview;
            public TextView longitudeTextview;
            public CardView cardView;

            public ViewHolder(View v) {
                super(v);
                nameTextview = v.findViewById(R.id.textview_name);
                dateTextview = v.findViewById(R.id.textview_date);
                latitudeTextview = v.findViewById(R.id.textview_latitude);
                longitudeTextview = v.findViewById(R.id.textview_longitude);
                cardView = v.findViewById(R.id.cardview);
            }
        }
    }
}
