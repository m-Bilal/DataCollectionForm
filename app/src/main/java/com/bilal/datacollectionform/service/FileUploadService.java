package com.bilal.datacollectionform.service;

import android.support.v4.app.NotificationCompat;

import com.bilal.datacollectionform.helper.CallbackHelper;
import com.bilal.datacollectionform.helper.NotificationHelper;
import com.bilal.datacollectionform.model.FileModel;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

import java.util.List;

public class FileUploadService extends JobService {

    private final static String TAG = "FileUploadService";

    private int filesUploaded;

    @Override
    public boolean onStartJob(JobParameters job) {
        List<FileModel> fileModelList = FileModel.getAllUnscyncedModels(getApplicationContext());
        filesUploaded = 0;
        final int totalFiles = fileModelList.size();
        final NotificationCompat.Builder builder = NotificationHelper.createFileUploadNotification(getApplicationContext());
        for (FileModel model : fileModelList) {
            model.syncUploadToServer(getApplicationContext(), model, new CallbackHelper.Callback() {
                @Override
                public void onSuccess() {
                    NotificationHelper.updateFileNotificationProgress(getApplicationContext(), builder,
                            ++filesUploaded, totalFiles);
                }

                @Override
                public void onFailure() {

                }
            });
        }

        return false; // Answers the question: "Is there still work going on?"
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        return false; // Answers the question: "Should this job be retried?"
    }
}