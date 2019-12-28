package co.siempo.phone.app;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;

import co.siempo.phone.utils.PackageUtil;

/**
 * Created by rajeshjadi on 23/2/18.
 */

public class BitmapWorkerTask extends AsyncTask<Object, Void, Void> {
    // Decode image in background.
    private ApplicationInfo appInfo;
    private PackageManager packageManager;

    public BitmapWorkerTask(PackageManager packageManager, String packageName) {
        this.packageManager = packageManager;
        try {
            this.appInfo = packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected Void doInBackground(Object... params) {
        if (appInfo == null) {
            return null;
        }
        Drawable drawable = appInfo.loadIcon(packageManager);
        Bitmap bitmap = PackageUtil.drawableToBitmap(drawable);
        CoreApplication.getInstance().addBitmapToMemoryCache(appInfo.packageName, bitmap);
        return null;
    }
}
