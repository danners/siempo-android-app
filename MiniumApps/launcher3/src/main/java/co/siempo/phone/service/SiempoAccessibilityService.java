package co.siempo.phone.service;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import org.androidannotations.annotations.SystemService;

import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.accessibility.AccessibilityEvent;

import co.siempo.phone.SiempoNotificationBar.ViewService_;
import co.siempo.phone.util.PackageUtil;
import minium.co.core.app.CoreApplication;

import static android.view.accessibility.AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED;


public class SiempoAccessibilityService extends AccessibilityService {

    public String packageName = "";
    public static String activityName = "";
    private final String TAG = "Accessibility";

    AudioManager audioManager;
    private NotificationManager notificationManager;

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        if (event != null && event.getPackageName() != null && event.getClass() != null) {
            if (event.getEventType() == TYPE_WINDOW_STATE_CHANGED) {
                try {
                    ComponentName componentName = new ComponentName(event.getPackageName().toString(), event.getClassName().toString());
                    ActivityInfo activityInfo = getActivityInfo(componentName);
                    boolean isActivity = activityInfo != null;
                    if (isActivity) {
                        packageName = activityInfo.packageName;
                        activityName = componentName.flattenToShortString();
                    }
                    Log.d(TAG, "Package Name::" + packageName);
                    Log.d(TAG, "Activity Name::" + activityName);
                } catch (Exception e) {
                    CoreApplication.getInstance().logException(e);
                    e.printStackTrace();
                }


                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                    // check the condition for the Marshmallow device.
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (Settings.canDrawOverlays(this)) {
                            siempoNotificationBarStatus();
                        }
                    } else {
                        siempoNotificationBarStatus();
                    }
                }

            }
        }
    }

    @Override
    public void onInterrupt() {

    }


    private ActivityInfo getActivityInfo(ComponentName componentName) {
        try {
            return getPackageManager().getActivityInfo(componentName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            CoreApplication.getInstance().logException(e);
            return null;
        }
    }

    public synchronized void siempoNotificationBarStatus() {
        // below code will use for further development
        if ((PackageUtil.isSiempoLauncher(this) || packageName.equalsIgnoreCase(getPackageName()))) {
            ViewService_.intent(getApplication()).showMask().start();
        } else {
            ViewService_.intent(getApplication()).hideMask().start();
        }
    }
}