package co.siempo.phone.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Color;
import android.os.Build;

import co.siempo.phone.R;

/**
 * Created by hardik on 6/11/17.
 */

public class NotificationUtils extends ContextWrapper {

    public static final String ANDROID_CHANNEL_ID = "co.siempo.phone.ANDROID";
    private static final String ANDROID_CHANNEL_NAME = "ANDROID CHANNEL";
    private NotificationManager mManager;

    public NotificationUtils(Context base) {
        super(base);
        createChannels();
    }

    public void createChannels() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            // create android channel
            NotificationChannel androidChannel = new NotificationChannel(ANDROID_CHANNEL_ID,
                    ANDROID_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            // Sets whether notifications posted to this channel should display notification lights
            androidChannel.enableLights(true);
            // Sets whether notification posted to this channel should vibrate.
            androidChannel.enableVibration(true);
            // Sets the notification light color for notifications posted to this channel
            androidChannel.setLightColor(Color.GREEN);
            // Sets whether notifications posted to this channel appear on the lockscreen or not
            androidChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

            getManager().createNotificationChannel(androidChannel);

        }
    }

    public NotificationManager getManager() {
        if (mManager == null) {
            mManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return mManager;
    }

}
