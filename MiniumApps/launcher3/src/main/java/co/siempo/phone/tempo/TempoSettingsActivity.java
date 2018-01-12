package co.siempo.phone.tempo;


import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OptionsMenu;

import co.siempo.phone.R;
import co.siempo.phone.app.Launcher3App;
import de.greenrobot.event.Subscribe;
import minium.co.core.app.CoreApplication;
import minium.co.core.event.AppInstalledEvent;
import minium.co.core.ui.CoreActivity;

@EActivity(R.layout.activity_tempo_settings)
public class TempoSettingsActivity extends CoreActivity {
    private String TAG = "TempoActivity";

    @Subscribe
    public void appInstalledEvent(AppInstalledEvent event) {
        if (event.isRunning()) {
            ((Launcher3App) CoreApplication.getInstance()).setAllDefaultMenusApplication();
        }
    }


    @AfterViews
    void afterViews() {
        loadFragment(TempoSettingsFragment_.builder().build(), R.id.tempoView, "main");

    }


    @Override
    protected void onResume() {
        super.onResume();

    }




}