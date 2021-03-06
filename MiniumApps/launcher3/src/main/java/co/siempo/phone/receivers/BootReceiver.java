package co.siempo.phone.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import co.siempo.phone.utils.PrefSiempo;

/**
 * Created by hardik on 18/1/18.
 */

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String TAG = "BootReceiver";
        Log.d(TAG, "Boot complete");
        try {
            PrefSiempo.getInstance(context).write(PrefSiempo.CALL_RUNNING, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
