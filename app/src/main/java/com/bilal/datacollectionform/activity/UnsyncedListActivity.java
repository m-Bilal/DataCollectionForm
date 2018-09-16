package com.bilal.datacollectionform.activity;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.bilal.datacollectionform.R;
import com.bilal.datacollectionform.helper.CallbackHelper;
import com.bilal.datacollectionform.model.FileModel;
import com.bilal.datacollectionform.model.FormAnswerModel;
import com.bilal.datacollectionform.model.FormModel;
import com.bilal.datacollectionform.service.FileUploadService;
import com.bilal.datacollectionform.service.FormUploadService;

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
        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        unsyncedFileTextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startFileUpload();
            }
        });

        unsyncedFormTextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startFormUpload();
            }
        });

        updateViews();
    }

    private void updateViews() {
        unsyncedFileList = FileModel.getAllUnsyncedModels(context);
        unsyncedFormList = FormAnswerModel.getAllUnsyncedModels(context);
        if (unsyncedFormList.size() > 0) {
            unsyncedFormTextview.setText("Tap to sync " + unsyncedFormList.size() + " forms");
        } else {
            unsyncedFormTextview.setText("No unsynced forms");
        }
        if (unsyncedFileList.size() > 0) {
            unsyncedFileTextview.setText("Tap to sync " + unsyncedFileList.size() + " files");
        } else {
            unsyncedFileTextview.setText("No unsynced forms");
        }
    }

    void startFileUpload() {
        fileUploadDialog = new ProgressDialog(context);
        fileUploadDialog.setTitle("Uploading Files");
        fileUploadDialog.setIndeterminate(false);
        fileUploadDialog.setCanceledOnTouchOutside(false);
        fileUploadDialog.setButton(DialogInterface.BUTTON_NEUTRAL, "Move to background", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                fileUploadDialog.dismiss();
            }
        });
        fileUploadDialog.show();
        Intent intent = new Intent(this, FormUploadService.class);
        startService(intent);
        FileUploadService.setCallback(this);
    }

    void startFormUpload() {
        formUploadDialog = new ProgressDialog(context);
        formUploadDialog.setTitle("Uploading Files");
        formUploadDialog.setIndeterminate(false);
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
        FormUploadService.setCallback(this);
    }

    @Override
    public void updateFileUpload(int synced, int failed, int total) {
        String message = synced + " out of " + total + " files uploaded, " + failed + " failed.";
        fileUploadDialog.setMessage(message);
        float percent = ((float) synced + (float) failed) / (float) total * 100f;
        fileUploadDialog.setProgress((int) percent);
    }

    @Override
    public void updateFormUpload(int synced, int failed, int total) {
        String message = synced + " out of " + total + " forms uploaded, " + failed + " failed.";
        formUploadDialog.setMessage(message);
        float percent = ((float) synced + (float) failed) / (float) total * 100f;
        formUploadDialog.setProgress((int) percent);
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
}
