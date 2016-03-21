package minium.co.launcher.ui;


import android.content.Context;
import android.support.v4.app.Fragment;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.widget.TextView;

import com.joanzapata.iconify.Icon;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.Trace;
import org.androidannotations.annotations.ViewById;

import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;
import minium.co.core.log.Tracer;
import minium.co.core.ui.CoreFragment;
import minium.co.core.util.UIUtils;
import minium.co.launcher.R;
import minium.co.launcher.battery.BatteryChangeEvent;

/**
 * A simple {@link Fragment} subclass.
 */
@EFragment(R.layout.fragment_top)
public class TopFragment extends CoreFragment {

    @ViewById
    TextView iTxt1;

    @ViewById
    TextView iTxt2;

    @ViewById
    TextView iTxt3;

    FontAwesomeIcons [] batteryIcons = {
            FontAwesomeIcons.fa_battery_0,
            FontAwesomeIcons.fa_battery_1,
            FontAwesomeIcons.fa_battery_2,
            FontAwesomeIcons.fa_battery_3,
            FontAwesomeIcons.fa_battery_4
    };

    TelephonyManager telephonyManager;


    public TopFragment() {
        // Required empty public constructor
    }

    @AfterViews
    void afterViews() {
        // Default text
        updateBatteryText(50);
    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            telephonyManager = (TelephonyManager) getContext().getSystemService(Context.TELEPHONY_SERVICE);
            telephonyManager.listen(new SignalStrengthListener(), PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
        } catch (Exception e) {
            Tracer.e(e, e.getMessage());
        }
    }

    @Trace(tag = TRACE_TAG)
    @Subscribe(threadMode = ThreadMode.MainThread)
    public void onBatteryLevelChange(BatteryChangeEvent event) {
        updateBatteryText(event.getLevel());
    }

    private void updateBatteryText(int level) {
        iTxt3.setText(getString(R.string.format_battery, level));
        iTxt3.setCompoundDrawablesWithIntrinsicBounds(null, null, new IconDrawable(getContext(), getBatteryIcon(level)).colorRes(R.color.white).sizeDp(16), null);
    }

    private Icon getBatteryIcon(int level) {
        if (level < 15) return batteryIcons [0];
        else if (level <= 25) return batteryIcons [1];
        else if (level <= 65) return batteryIcons [2];
        else if (level <= 80) return batteryIcons [3];
        else if (level > 80) return batteryIcons [4];
        return batteryIcons [2];
    }

    private class SignalStrengthListener extends PhoneStateListener {
        @Override
        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            super.onSignalStrengthsChanged(signalStrength);
            int strengthVal = signalStrength.getGsmSignalStrength();
            strengthVal = (2 * strengthVal) - 113;
            updateSignalText(strengthVal);
        }
    }

    private void updateSignalText(int strength) {
        Tracer.i("Signal strength: " + strength + " Operator: " + telephonyManager.getNetworkOperatorName());
        iTxt1.setText(getString(R.string.format_signal, telephonyManager.getNetworkOperatorName(), strength));
    }
}