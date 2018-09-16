package com.bilal.datacollectionform.helper;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.bilal.datacollectionform.R;

public class NotificationHelper {

    public final static int FILE_UPLOAD_NOTIFICATION_ID = 1000;
    public final static int FORM_UPLOAD_NOTIFICATION_ID = 1001;
    private final static String CHANNEL_ID = "1";

    public static Notification createFileUploadNotification(Context context) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String name = "Upload";
            String description = "Upload notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, CHANNEL_ID);
        mBuilder.setContentTitle("File Upload")
                .setContentText("Uploading Files")
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        // Issue the initial notification with zero progress
        int PROGRESS_MAX = 100;
        int PROGRESS_CURRENT = 0;
        mBuilder.setProgress(PROGRESS_MAX, PROGRESS_CURRENT, false);
        Notification notification = mBuilder.build();
        notificationManager.notify(FILE_UPLOAD_NOTIFICATION_ID, mBuilder.build());
        return notification;
    }

    public static void updateFileNotificationProgress(Context context, int currentProgress,
                                                      int failed, int maxProgress) {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, CHANNEL_ID);
        mBuilder.setContentTitle("File Upload")
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentText(currentProgress + " files out of " + maxProgress + " uploaded, "
                        + failed + " failed.");
        if ((currentProgress + failed) != maxProgress) {
            mBuilder.setProgress(maxProgress, currentProgress, false);
        } else {
            mBuilder.setProgress(0, 0, false);
        }
        notificationManager.notify(FILE_UPLOAD_NOTIFICATION_ID, mBuilder.build());
    }

    public static Notification createFormUploadNotification(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String name = "Upload";
            String description = "Upload notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, CHANNEL_ID);
        mBuilder.setContentTitle("Form Upload")
                .setContentText("Uploading Forms")
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        // Issue the initial notification with zero progress
        int PROGRESS_MAX = 100;
        int PROGRESS_CURRENT = 0;
        mBuilder.setProgress(PROGRESS_MAX, PROGRESS_CURRENT, false);
        Notification notification = mBuilder.build();
        notificationManager.notify(FORM_UPLOAD_NOTIFICATION_ID, notification);
        return notification;
    }

    public static void updateFormNotificationProgress(Context context, int currentProgress,
                                                      int failed, int maxProgress) {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, CHANNEL_ID);
        mBuilder.setContentTitle("Form Upload")
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentText(currentProgress + " forms out of " + maxProgress + " uploaded, "
                        + failed + " failed.");
        if ((currentProgress + failed) != maxProgress) {
            mBuilder.setProgress(maxProgress, currentProgress, false);
        } else {
            mBuilder.setProgress(0, 0, false);
        }
        notificationManager.notify(FORM_UPLOAD_NOTIFICATION_ID, mBuilder.build());
    }
}
