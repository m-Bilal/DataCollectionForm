package com.bilal.datacollectionform.service;

import android.app.Notification;
import android.content.Context;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.bilal.datacollectionform.helper.CallbackHelper;
import com.bilal.datacollectionform.helper.NotificationHelper;
import com.bilal.datacollectionform.model.FileModel;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;

import java.util.List;

import static com.firebase.jobdispatcher.FirebaseJobDispatcher.SCHEDULE_RESULT_SUCCESS;

public class FileUploadFirebaseService extends JobService {

    private final static String TAG = "FileUploadService";

    private int filesUploaded;

    @Override
    public boolean onStartJob(JobParameters job) {
        Log.d(TAG, "onStartJob");
        List<FileModel> fileModelList = FileModel.getAllUnsyncedModels(getApplicationContext());
        filesUploaded = 0;
        final int totalFiles = fileModelList.size();
        final Notification notification = NotificationHelper.createFileUploadNotification(getApplicationContext());
        for (final FileModel model : fileModelList) {
            Log.d(TAG, "onStartJob, file model: " + model.uri);
            model.syncUploadToServer(getApplicationContext(), model, new CallbackHelper.Callback() {
                @Override
                public void onSuccess() {
                    NotificationHelper.updateFileNotificationProgress(getApplicationContext(),
                            ++filesUploaded, 0 ,totalFiles);
                    //model.setSyncedWithServer(getApplicationContext(), true);
                    Log.d(TAG, "onStartJob, onSuccess");
                }

                @Override
                public void onFailure() {
                    Log.d(TAG, "onStartJob, onFailure");
                }
            });
        }

        return false; // Answers the question: "Is there still work going on?"
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        Log.d(TAG, "onStopJob");
        return false; // Answers the question: "Should this job be retried?"
    }

    public static void startService(Context context) {
        Log.d(TAG, "startService, called");
        // Create a new dispatcher using the Google Play driver.
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(context));
        Job myJob = dispatcher.newJobBuilder()
                // the JobService that will be called
                .setService(FileUploadFirebaseService.class)
                // uniquely identifies the job
                .setTag("file-upload-service")
                // one-off job
                .setRecurring(false)
                // don't persist past a device reboot
                .setLifetime(Lifetime.UNTIL_NEXT_BOOT)
                // start between 0 and 2 seconds from now
                //.setTrigger(Trigger.executionWindow(0, 2))
                .setTrigger(Trigger.NOW)
                // don't overwrite an existing job with the same tag
                .setReplaceCurrent(false)
                // retry with exponential backoff
                .setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
                // constraints that need to be satisfied for the job to run
                .setConstraints(
                        // only run on an unmetered network
                        //Constraint.ON_UNMETERED_NETWORK // ,
                        // only run when the device is charging
                        //Constraint.DEVICE_CHARGING
                )
                //.setExtras(myExtrasBundle)
                .build();
        dispatcher.mustSchedule(myJob);
        if (dispatcher.schedule(myJob) == SCHEDULE_RESULT_SUCCESS) {
            Log.d(TAG, "startService, successfully scheduled");
        } else {
            Log.d(TAG, "startService, failed to schedule");
        }
    }
}