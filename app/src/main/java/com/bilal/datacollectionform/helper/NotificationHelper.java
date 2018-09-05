package com.bilal.datacollectionform.helper;

import android.content.Context;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

public class NotificationHelper {

    private final static String CHANNEL_ID = "1";
    private final static int FILE_UPLOAD_NOTIFICATION_ID = 1000;

    public static NotificationCompat.Builder createFileUploadNotification(Context context) {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, CHANNEL_ID);
        mBuilder.setContentTitle("File Upload")
                .setContentText("Uploading Files")
                //.setSmallIcon(R.drawable.ic_notification)
                .setPriority(NotificationCompat.PRIORITY_LOW);

        // Issue the initial notification with zero progress
        int PROGRESS_MAX = 100;
        int PROGRESS_CURRENT = 0;
        mBuilder.setProgress(PROGRESS_MAX, PROGRESS_CURRENT, false);
        notificationManager.notify(FILE_UPLOAD_NOTIFICATION_ID, mBuilder.build());
        return mBuilder;
    }

    public static void updateFileNotificationProgress(Context context, NotificationCompat.Builder builder,
                                                      int currentProgress, int maxProgress) {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        if (currentProgress != maxProgress) {
            builder.setContentText(currentProgress + " files out of " + maxProgress + " uploaded")
                    .setProgress(maxProgress, currentProgress, false);
            notificationManager.notify(FILE_UPLOAD_NOTIFICATION_ID, builder.build());
        } else {
            builder.setContentText("Download complete")
                    .setProgress(0,0,false);
            notificationManager.notify(FILE_UPLOAD_NOTIFICATION_ID, builder.build());
        }
    }
}
