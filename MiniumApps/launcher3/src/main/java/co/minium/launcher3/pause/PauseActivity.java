package co.minium.launcher3.pause;

import android.view.KeyEvent;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Fullscreen;
import org.androidannotations.annotations.KeyDown;

import co.minium.launcher3.R;
import minium.co.core.ui.CoreActivity;

@Fullscreen
@EActivity(R.layout.activity_pause)
public class PauseActivity extends CoreActivity {

    private PauseFragment pauseFragment;

    @AfterViews
    void afterViews() {
        pauseFragment = PauseFragment_.builder().build();
        loadFragment(pauseFragment, R.id.mainView, "main");
    }

    @KeyDown(KeyEvent.KEYCODE_VOLUME_UP)
    void volumeUpPressed() {
        pauseFragment.volumeUpPresses();
    }
}
