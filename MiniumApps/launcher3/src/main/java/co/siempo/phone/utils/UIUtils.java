package co.siempo.phone.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.IBinder;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import android.util.TypedValue;
import android.view.Display;
import android.view.ViewGroup;
import android.view.ViewGroupOverlay;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;

import co.siempo.phone.BuildConfig;
import co.siempo.phone.R;
import co.siempo.phone.app.CoreApplication;
import co.siempo.phone.app.Launcher3App;


public class UIUtils {
    public static final String PACKAGE_NAME = "co.siempo.phone";
    private static AlertDialog alertDialog;

    public static int dpToPx(Context context, int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dp, context.getResources().getDisplayMetrics());
    }


    public static void toast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }

    public static void toast(Context context, @StringRes int msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }

    public static void toastShort(Context context, @StringRes int msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }


    public static void alert(Context context, String msg) {
        alert(context, null, msg);
    }

    public static void alert(Context context, String title, String msg) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(msg)
                .setPositiveButton(android.R.string.ok, null);
        alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }


    public static void notification(Context context, String title, String msg, @StringRes int resOk, @StringRes int resCancel, @DrawableRes int resIcon, DialogInterface.OnClickListener listener) {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(msg)
                .setIcon(resIcon)
                .setPositiveButton(resOk, listener)
                .setNegativeButton(resCancel, listener)
                .show();
    }

    public static void showKeyboard(EditText editText) {
        try {
            if (editText != null) {
                InputMethodManager imm = (InputMethodManager) editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Method return true if my application is default launcher.
     *
     * @param activity
     * @return
     */
    public static boolean isMyLauncherDefault(Activity activity) {
        return getLauncherPackageName(activity).equals(activity.getPackageName());
    }

    /**
     * This method returns default launcher package name.
     *
     * @param activity
     * @return
     */
    public static String getLauncherPackageName(Activity activity) {
        PackageManager localPackageManager = activity.getPackageManager();
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.HOME");
        return localPackageManager.resolveActivity(intent,
                PackageManager.MATCH_DEFAULT_ONLY).activityInfo.packageName;
    }

    /**
     * Hides the soft keyboard
     */
    public static void hideSoftKeyboard(Context mContext, IBinder windowToken) {
        InputMethodManager inputMethodManager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            inputMethodManager.hideSoftInputFromWindow(windowToken, 0);
        }
    }

    /**
     * This method returns the device model name.
     *
     * @param context refrence of calling view.
     * @return return the Manufacture Name,Model Name,Android Version and Display Size in pixel.
     */
    public static String getDeviceInfo(Context context) {
        return "\n\n\nMy Device Information is as follows:"
                + "\nMANUFACTURER : " + Build.MANUFACTURER
                + "\nMODEL : " + Build.MODEL
                + "\nOS VERSION : " + Build.VERSION.RELEASE
                + "\nDISPLAY : " + getScreenDisplaySize(context);
    }

    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    /**
     * This method returns the screen resolution.
     *
     * @param context takem the current view context.
     * @return Display size in pixel
     */
    private static String getScreenDisplaySize(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = null;
        if (wm != null) {
            display = wm.getDefaultDisplay();
        }
        Point size = new Point();
        display.getSize(size);
        int height = size.y;
        int width = size.x;
        return "" + width + "*" + height;
    }


    public static boolean isAppInstalledAndEnabled(Context context, String packageName) {
        PackageManager packageManager = context.getPackageManager();
        ApplicationInfo applicationInfo = null;
        try {
            applicationInfo = packageManager.getApplicationInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            CoreApplication.getInstance().logException(e);
            //  e.printStackTrace();
        }
        return applicationInfo != null && applicationInfo.enabled;
// Installed
    }

    public static boolean isAppInstalledAndEnabled(String packageName) {
        PackageManager packageManager = Launcher3App.getInstance().getApplicationContext().getPackageManager();
        ApplicationInfo applicationInfo = null;
        try {
            applicationInfo = packageManager.getApplicationInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            CoreApplication.getInstance().logException(e);
        }
        return applicationInfo != null && applicationInfo.enabled;
// Installed
    }

    public static boolean isInstalled(Context context, String packageName) {
        PackageManager packageManager = context.getPackageManager();
        ApplicationInfo applicationInfo = null;
        try {
            applicationInfo = packageManager.getApplicationInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            CoreApplication.getInstance().logException(e);
            e.printStackTrace();
        }
        return applicationInfo != null;
// Installed
    }

    public static boolean isAppEnabled(Context context, String packageName) {
        PackageManager packageManager = context.getPackageManager();
        ApplicationInfo applicationInfo = null;
        try {
            applicationInfo = packageManager.getApplicationInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            CoreApplication.getInstance().logException(e);
            e.printStackTrace();
        }
        return applicationInfo.enabled;
// Installed
    }


    public static byte[] convertBitmapToByte(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    public static Bitmap convertBytetoBitmap(byte[] byteArray) {
        try {
            Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            if (bitmap != null && bitmap.isMutable()) {
                bitmap.setWidth(bitmap.getWidth() / 2);
                bitmap.setHeight(bitmap.getHeight() / 2);
                return bitmap;
            } else {
                return bitmap;
            }
        } catch (Exception e) {
            return null;
        }
    }

    public static int getCurrentVersionCode(Context context) {
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return pInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            CoreApplication.getInstance().logException(e);
            e.printStackTrace();
        }
        return BuildConfig.VERSION_CODE;
    }

    /**
     * This method is used for dim the background when user open the pop up dialog.
     *
     * @param parent
     * @param dimAmount
     */
    public static void applyDim(@NonNull ViewGroup parent, float dimAmount) {
        //Drawable dim = new ColorDrawable(Color.BLACK);
        Drawable dim = new ColorDrawable(parent.getContext().getResources()
                .getColor(R.color.black));
        dim.setBounds(0, 0, parent.getWidth(), parent.getHeight());
        dim.setAlpha((int) (255 * dimAmount));

        ViewGroupOverlay overlay = parent.getOverlay();
        overlay.add(dim);
    }

    /**
     * This method remove the dim background.
     *
     * @param parent
     */
    public static void clearDim(@NonNull ViewGroup parent) {
        ViewGroupOverlay overlay = parent.getOverlay();
        overlay.clear();
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static boolean hasUsageStatsPermission(Context context) {
        AppOpsManager appOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        int mode = 0;
        if (appOps != null) {
            mode = appOps.checkOpNoThrow("android:get_usage_stats",
                    android.os.Process.myUid(), context.getPackageName());
        }
        return mode == AppOpsManager.MODE_ALLOWED;
    }



}
