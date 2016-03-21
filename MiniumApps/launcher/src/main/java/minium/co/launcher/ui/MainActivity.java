package minium.co.launcher.ui;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Fullscreen;
import org.androidannotations.annotations.Trace;

import minium.co.core.config.Config;
import minium.co.core.ui.CoreActivity;
import minium.co.launcher.R;
import minium.co.launcher.battery.BatteryChangeReceiver_;

@Fullscreen
@EActivity(R.layout.activity_main)
public class MainActivity extends CoreActivity {

    private final String TRACE_TAG = Config.TRACE_TAG + "MainActivity";

    @Trace(tag = TRACE_TAG)
    @AfterViews
    void afterViews() {
        loadTopView();
        loadMainView();
        loadBottomView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        this.registerReceiver(this.mBatteryInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }

    @Override
    protected void onStop() {
        super.onStop();
        this.unregisterReceiver(this.mBatteryInfoReceiver);
    }

    @Trace(tag = TRACE_TAG)
    void loadTopView() {
        loadFragment(TopFragment_.builder().build(), R.id.topView);
    }

    @Trace(tag = TRACE_TAG)
    void loadBottomView() {
        loadFragment(BottomFragment_.builder().build(), R.id.bottomView);
    }

    @Trace(tag = TRACE_TAG)
    void loadMainView() {
        loadFragment(MainFragment_.builder().build());
    }

    private BroadcastReceiver mBatteryInfoReceiver = new BatteryChangeReceiver_();
}
