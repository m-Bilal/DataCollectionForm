package com.bilal.datacollectionform.service;

import android.app.IntentService;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.bilal.datacollectionform.activity.UnsyncedListActivity;
import com.bilal.datacollectionform.helper.CallbackHelper;
import com.bilal.datacollectionform.helper.NotificationHelper;
import com.bilal.datacollectionform.model.FileModel;

import io.realm.RealmResults;

public class FileUploadService extends IntentService {

    private Context context;
    private FileModel fileModel;
    private int totalFiles;
    private int uploaded;
    private int failedToUpload;
    private static CallbackHelper.FileUploadServiceCallback callback;


    public FileUploadService() {
        super("FileUploadService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        context = getApplicationContext();
        uploaded = 0;
        failedToUpload = 0;
        RealmResults<FileModel> fileModels = FileModel.getAllUnsyncedModels(context);
        totalFiles = fileModels.size();

        final Notification notification = NotificationHelper.createFileUploadNotification(context);
        NotificationHelper.updateFileNotificationProgress(context, notification, uploaded, failedToUpload, totalFiles);

        for (FileModel i : fileModels) {
            fileModel = i;
            i.syncUploadToServer(context, i, new CallbackHelper.Callback() {
                @Override
                public void onSuccess() {
                    uploaded++;

                    FileModel.setSyncedWithServer(context, fileModel, true);
                    //callback.setSyncedWithServer(fileModel, true);
                    if (UnsyncedListActivity.isRunning) {
                        callback.updateFileUpload(uploaded, failedToUpload, totalFiles);
                    }
                    NotificationHelper.updateFileNotificationProgress(context, notification, uploaded, failedToUpload, totalFiles);
                    if (uploaded + failedToUpload == totalFiles) {
                        if (UnsyncedListActivity.isRunning) {
                            callback.completedFileUpload();
                        }
                        stopSelf();
                    }
                }

                @Override
                public void onFailure() {
                    failedToUpload++;
                    //callback.setSyncedWithServer(fileModel, false);
                    FileModel.setSyncedWithServer(context, fileModel, false);
                    if (UnsyncedListActivity.isRunning) {
                        callback.updateFileUpload(uploaded, failedToUpload, totalFiles);
                    }
                    NotificationHelper.updateFileNotificationProgress(context, notification, uploaded, failedToUpload, totalFiles);
                    if (uploaded + failedToUpload == totalFiles) {
                        if (UnsyncedListActivity.isRunning) {
                            callback.completedFileUpload();
                        }
                        stopSelf();
                    }
                }
            });
        }
        startForeground(NotificationHelper.FILE_UPLOAD_NOTIFICATION_ID, notification);
    }

    public static void setCallback(CallbackHelper.FileUploadServiceCallback callbacks) {
        callback = callbacks;
    }
}
