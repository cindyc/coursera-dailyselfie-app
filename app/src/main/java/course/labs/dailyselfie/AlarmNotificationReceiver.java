package course.labs.dailyselfie;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


public class AlarmNotificationReceiver extends BroadcastReceiver {

    private Intent mSelfieIntent;
    private PendingIntent mContentIntent;
    private NotificationManager mNotificationManager;

    private static final int MY_NOTIFICATION_ID = 1;
    private static final String TAG = "AlarmNotificationReceiver";
    private static final CharSequence CONTENT_TITLE = "Daily Selfie";
    private static final CharSequence CONTENT_TEXT = "Time for another selfie";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "Received alarm notification");
        mSelfieIntent = new Intent(context, MainActivity.class);
        mContentIntent = PendingIntent.getActivity(context, 0, mSelfieIntent,
                                                   PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder notificationBuilder = new Notification.Builder(context)
                .setContentText(CONTENT_TEXT)
                .setContentTitle(CONTENT_TITLE)
                .setSmallIcon(R.drawable.alarm)
                .setContentIntent(mContentIntent)
                .setAutoCancel(true);

        mNotificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(1, notificationBuilder.build());

        Log.i(TAG, "Sent alarm notification");
    }
}
