package co.siempo.phone.utils;

import android.Manifest;
import android.app.AppOpsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;

import co.siempo.phone.log.Tracer;
import co.siempo.phone.service.SiempoNotificationListener_;

/**
 * Created by Shahab on 1/12/2017.
 */

public class PermissionUtil {

    public static final int NOTIFICATION_ACCESS = 0;
    public static final int USAGE_STATISTICS = 1;
    public static final int DRAWING_OVER_OTHER_APPS = 2;
    public static final int APP_PERMISSION = 3;
    public static final int CONTACT_PERMISSION = 4;
    public static final int CAMERA_PERMISSION = 7;
    public static final int WRITE_EXTERNAL_STORAGE_PERMISSION = 8;
    public static final int ACCOUNT_PERMISSION = 10;
    public static final int SYSTEM_WINDOW_ALERT = 11;

    private Context context;

    public PermissionUtil(Context context) {
        this.context = context;
    }

    public boolean hasGiven(int permission) {
        switch (permission) {
            case NOTIFICATION_ACCESS:
                return isEnabled();
            case USAGE_STATISTICS:
                return checkUserStatPermission();
            case DRAWING_OVER_OTHER_APPS:
                return canDrawOverlays();
            case APP_PERMISSION:
                return hasAppPermissions();
            case CONTACT_PERMISSION:
                return hasAppPermissions(Manifest.permission.READ_CONTACTS)
                        && hasAppPermissions(Manifest.permission.WRITE_CONTACTS);
            case CAMERA_PERMISSION:
                return hasAppPermissions(Manifest.permission.CAMERA);
            case ACCOUNT_PERMISSION:
                return hasAppPermissions(Manifest.permission.GET_ACCOUNTS);
            case WRITE_EXTERNAL_STORAGE_PERMISSION:
                return hasAppPermissions(Manifest.permission
                        .WRITE_EXTERNAL_STORAGE) && hasAppPermissions
                        (Manifest.permission.READ_EXTERNAL_STORAGE);
            case SYSTEM_WINDOW_ALERT:
                return hasAppPermissions(Manifest.permission.SYSTEM_ALERT_WINDOW);
        }
        return false;
    }

    private boolean hasAppPermissions() {
        int appPermissions = PackageManager.PERMISSION_GRANTED;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            appPermissions = context.checkSelfPermission(Manifest.permission.READ_CONTACTS) +
                    context.checkSelfPermission(Manifest.permission.WRITE_CONTACTS) +
                    context.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
        }

        return appPermissions == PackageManager.PERMISSION_GRANTED;
    }


    private boolean hasAppPermissions(String strPermission) {
        int appPermissions = PackageManager.PERMISSION_GRANTED;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            appPermissions = context.checkSelfPermission(strPermission);
        }

        return appPermissions == PackageManager.PERMISSION_GRANTED;
    }

    private boolean canDrawOverlays() {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M || Settings.canDrawOverlays(context);
    }


    /**
     * @return True if {@link SiempoNotificationListener_} is enabled.
     */
    private boolean isEnabled() {
        String pkgName = context.getPackageName();
        final String flat = Settings.Secure.getString(context.getContentResolver(),
                "enabled_notification_listeners");
        if (!TextUtils.isEmpty(flat)) {
            final String[] names = flat.split(":");
            for (String name : names) {
                final ComponentName cn = ComponentName.unflattenFromString(name);
                if (cn != null) {
                    if (TextUtils.equals(pkgName, cn.getPackageName())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean checkUserStatPermission() {
        try {
            PackageManager packageManager = context.getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), 0);
            AppOpsManager appOpsManager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            int mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, applicationInfo.uid, applicationInfo.packageName);
            Tracer.i("Usage stat permission: " + mode);
            return (mode == AppOpsManager.MODE_ALLOWED);

        } catch (PackageManager.NameNotFoundException e) {
            Tracer.i("Usage stat permission: " + e.getMessage());
            return false;
        }
    }

}
