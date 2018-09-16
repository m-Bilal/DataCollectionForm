package com.bilal.datacollectionform.service;

import android.app.IntentService;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.bilal.datacollectionform.activity.UnsyncedListActivity;
import com.bilal.datacollectionform.helper.CallbackHelper;
import com.bilal.datacollectionform.helper.NotificationHelper;
import com.bilal.datacollectionform.model.FormAnswerModel;

import java.util.ArrayList;

public class FormUploadService extends IntentService {

    private Context context;
    private int totalForms;
    private int uploaded;
    private int failedToUpload;
    private static CallbackHelper.FormUploadServiceCallback callback;

    public FormUploadService() {
        super("FormUploadService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        context = getApplicationContext();
        uploaded = 0;
        failedToUpload = 0;
        ArrayList<FormAnswerModel> formModels = FormAnswerModel.getAllUnsyncedModels(context);
        totalForms = formModels.size();

        Notification notification = NotificationHelper.createFormUploadNotification(context);
        NotificationHelper.updateFormNotificationProgress(context, uploaded, failedToUpload, totalForms);

        syncWithServer(0, formModels);

        /*
        pos = 0;
        boolean sync = true;
        while (sync) {
            final FormAnswerModel formAnswerModel = formModels.get(pos);
            FormAnswerModel.syncUploadToServer(context, formAnswerModel, new CallbackHelper.IntCallback() {
                @Override
                public void onSuccess(int response) {
                    uploaded++;
                    pos++;
                    FormAnswerModel.setSyncedWithServer(context, formAnswerModel, true);
                    if (UnsyncedListActivity.isRunning) {
                        callback.updateFormUpload(uploaded, failedToUpload, totalForms);
                    }
                    NotificationHelper.updateFormNotificationProgress(context, uploaded, failedToUpload, totalForms);
                    if (uploaded + failedToUpload == totalForms) {
                        if (UnsyncedListActivity.isRunning) {
                            callback.completedFormUpload();
                        }
                        stopSelf();
                    }
                }

                @Override
                public void onFailure() {
                    failedToUpload++;
                    pos++
                }
            });
        }

        for (FormAnswerModel i : formModels) {
            formAnswerModel = i;
            i.syncUploadToServer(context, i, new CallbackHelper.IntCallback() {
                @Override
                public void onSuccess(int response) {
                    uploaded++;
                    FormAnswerModel.setSyncedWithServer(context, formAnswerModel, true);
                    //callback.setSyncedWithServer(formAnswerModel, true);
                    if (UnsyncedListActivity.isRunning) {
                        callback.updateFormUpload(uploaded, failedToUpload, totalForms);
                    }
                    NotificationHelper.updateFormNotificationProgress(context, uploaded, failedToUpload, totalForms);
                    if (uploaded + failedToUpload == totalForms) {
                        if (UnsyncedListActivity.isRunning) {
                            callback.completedFormUpload();
                        }
                        stopSelf();
                    }
                }

                @Override
                public void onFailure() {
                    failedToUpload++;
                    //callback.setSyncedWithServer(formAnswerModel, true);
                    FormAnswerModel.setSyncedWithServer(context, formAnswerModel, true);
                    if (UnsyncedListActivity.isRunning) {
                        callback.updateFormUpload(uploaded, failedToUpload, totalForms);
                    }
                    NotificationHelper.updateFormNotificationProgress(context, uploaded, failedToUpload, totalForms);
                    if (uploaded + failedToUpload == totalForms) {
                        if (UnsyncedListActivity.isRunning) {
                            callback.completedFormUpload();
                        }
                        stopSelf();
                    }
                }
            });
        }
        */
        startForeground(NotificationHelper.FILE_UPLOAD_NOTIFICATION_ID, notification);
    }

    private void syncWithServer(final int position, final ArrayList<FormAnswerModel> models) {
        if (position < models.size()) {
            FormAnswerModel.syncUploadToServer(context, models.get(position), new CallbackHelper.IntCallback() {
                @Override
                public void onSuccess(int response) {
                    uploaded++;
                    FormAnswerModel.setSyncedWithServer(context, models.get(position), true);
                    if (UnsyncedListActivity.isRunning) {
                        callback.updateFormUpload(uploaded, failedToUpload, totalForms);
                    }
                    NotificationHelper.updateFormNotificationProgress(context, uploaded, failedToUpload, totalForms);
                    if (uploaded + failedToUpload == totalForms) {
                        if (UnsyncedListActivity.isRunning) {
                            callback.completedFormUpload();
                        }
                        stopSelf();
                    } else {
                        syncWithServer(position + 1, models);
                    }
                }

                @Override
                public void onFailure() {
                    failedToUpload++;
                    FormAnswerModel.setSyncedWithServer(context, models.get(position), true);
                    if (UnsyncedListActivity.isRunning) {
                        callback.updateFormUpload(uploaded, failedToUpload, totalForms);
                    }
                    NotificationHelper.updateFormNotificationProgress(context, uploaded, failedToUpload, totalForms);
                    if (uploaded + failedToUpload == totalForms) {
                        if (UnsyncedListActivity.isRunning) {
                            callback.completedFormUpload();
                        }
                        stopSelf();
                    } else {
                        syncWithServer(position + 1, models);
                    }
                }
            });
        }
    }

    public static void setCallback(CallbackHelper.FormUploadServiceCallback callbacks) {
        callback = callbacks;
    }
}
