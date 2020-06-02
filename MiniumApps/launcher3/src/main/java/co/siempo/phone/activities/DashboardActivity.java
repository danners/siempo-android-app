package co.siempo.phone.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.File;
import java.util.Objects;
import java.util.concurrent.RejectedExecutionException;

import co.siempo.phone.R;
import co.siempo.phone.adapters.DashboardPagerAdapter;
import co.siempo.phone.event.HomePress;
import co.siempo.phone.event.NotifyBackgroundChange;
import co.siempo.phone.event.OnBackPressedEvent;
import co.siempo.phone.event.ThemeChangeEvent;
import co.siempo.phone.fragments.PaneFragment;
import co.siempo.phone.service.LoadFavoritePane;
import co.siempo.phone.service.LoadJunkFoodPane;
import co.siempo.phone.service.LoadToolPane;
import co.siempo.phone.service.ScreenFilterService;
import co.siempo.phone.service.SiempoNotificationListener_;
import co.siempo.phone.ui.SiempoViewPager;
import co.siempo.phone.util.AppUtils;
import co.siempo.phone.utils.PackageUtil;
import co.siempo.phone.utils.PermissionUtil;
import co.siempo.phone.utils.PrefSiempo;
import co.siempo.phone.utils.UIUtils;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class DashboardActivity extends CoreActivity {

    public static final String IS_FROM_HOME = "isFromHome";
    public static final String CLASS_NAME = DashboardActivity.class.getSimpleName();
    public static String isTextLenghGreater = "";
    public static boolean isJunkFoodOpen = false;
    public static int currentIndexDashboard = 1;
    public static int currentIndexPaneFragment = -1;
    public static long startTime = 0;
    PermissionUtil permissionUtil;
    NotificationManager notificationManager;
    int swipeCount;
    /**
     * The pager widget, which handles animation and allows swiping horizontally to access previous
     * and next wizard steps.
     */
    private SiempoViewPager mPager;
    /**
     * The pager adapter, which provides the pages to the view pager widget.
     */
    private DashboardPagerAdapter mPagerAdapter;
    private AlertDialog notificationDialog;
    private Dialog overlayDialog;
    private RelativeLayout linMain;
    private ImageView imgBackground;

    /**
     * @return True if {@link android.service.notification.NotificationListenerService} is enabled.
     */
    public static boolean isEnabled(Context mContext) {

        ComponentName cn = new ComponentName(mContext, SiempoNotificationListener_.class);
        String flat = Settings.Secure.getString(mContext.getContentResolver(), "enabled_notification_listeners");
        return flat != null && flat.contains(cn.flattenToString());

    }

    @Override
    protected void onResume() {
        super.onResume();


        AppUtils.notificationBarManaged(this, linMain);
        AppUtils.statusbarColor0(this, 1);
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        boolean read = PrefSiempo.getInstance(this).read(PrefSiempo.IS_DARK_THEME, false);
        setTheme(read ? R.style.SiempoAppThemeDark : R.style.SiempoAppTheme);

        Window w = getWindow(); // in Activity's onCreate() for instance



        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        linMain = findViewById(R.id.linMain);


        imgBackground = findViewById(R.id.imgBackground);
        swipeCount = PrefSiempo.getInstance(DashboardActivity.this).read(PrefSiempo.TOGGLE_LEFTMENU, 0);
        loadViews();
        Log.d("Test", "P1");
        if (startTime == 0) {
            startTime = System.currentTimeMillis();
        }
        permissionUtil = new PermissionUtil(this);
        overlayDialog = new Dialog(this, 0);
        showOverlayOfDefaultLauncher();
        View decor = w.getDecorView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !read) {
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        if (permissionUtil.hasGiven(PermissionUtil.WRITE_EXTERNAL_STORAGE_PERMISSION)) {
            changeLayoutBackground(-1);
        } else {
            PrefSiempo.getInstance(this).write(PrefSiempo
                    .DEFAULT_BAG, "");
            PrefSiempo.getInstance(this).write(PrefSiempo
                    .DEFAULT_BAG_ENABLE, false);
        }

        getColorList();

        if(PrefSiempo.getInstance(DashboardActivity.this).read(PrefSiempo.DEFAULT_SCREEN_OVERLAY, false)) {
            Intent command = new Intent(DashboardActivity.this, ScreenFilterService.class);
            command.putExtra(ScreenFilterService.BUNDLE_KEY_COMMAND, 0);
            startService(command);
        }
    }

    private void getColorList()
    {
        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = getTheme();
        theme.resolveAttribute(R.attr.junk_top, typedValue, true);
        AppUtils.backGroundColor = typedValue.resourceId;
        theme.resolveAttribute(R.attr.junk_top, typedValue, true);
        AppUtils.statusBarColorJunk = typedValue.data;
        theme.resolveAttribute(R.attr.status_bar_pane, typedValue, true);
        AppUtils.statusBarColorPane = typedValue.data;
    }


    public void changeLayoutBackground(int color)
    {
        Log.e("image","image "+ PrefSiempo.getInstance(this).read(PrefSiempo.DEFAULT_BAG, ""));
        if (color == -1) {
            try {
                String filePath = PrefSiempo.getInstance(this).read(PrefSiempo
                        .DEFAULT_BAG, "");
                boolean isEnable = PrefSiempo.getInstance(this).read(PrefSiempo
                        .DEFAULT_BAG_ENABLE, false);
                if (!TextUtils.isEmpty(filePath) && isEnable) {
                    Glide.with(this)
                            .load(Uri.fromFile(new File(filePath))) // Uri of the
                            // picture
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(imgBackground);
                } else {
                    imgBackground.setImageBitmap(null);
                    imgBackground.setBackground(null);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            if (!PrefSiempo.getInstance(this).read(PrefSiempo.DEFAULT_BAG_ENABLE, false)) {
                try {
                    String filePath = PrefSiempo.getInstance(this).read(PrefSiempo
                            .DEFAULT_BAG, "");
                    boolean isEnable = PrefSiempo.getInstance(this).read(PrefSiempo
                            .DEFAULT_BAG_ENABLE, false);
                    if (!TextUtils.isEmpty(filePath) && isEnable) {
                        Glide.with(this)
                                .load(Uri.fromFile(new File(filePath))) // Uri of the
                                // picture
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .into(imgBackground);
                    } else {
                        imgBackground.setImageBitmap(null);
                        imgBackground.setBackground(null);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        AppUtils.notificationBarManaged(this, linMain);
    }


    private void showOverlayOfDefaultLauncher() {
        if (!PackageUtil.isSiempoLauncher(this) && !overlayDialog.isShowing()) {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    showOverLay();
                }
            }, 1000);
        }

        //If already shown there is an overlay dialog and user sets siempo as
        // default launcher from settings or home button then this overlay
        // needs to be dismissed
        if (PackageUtil.isSiempoLauncher(this) && null != overlayDialog &&
                overlayDialog
                        .isShowing()) {
            overlayDialog.dismiss();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        boolean read = PrefSiempo.getInstance(this).read(PrefSiempo.IS_DARK_THEME, false);
        setTheme(read ? R.style.SiempoAppThemeDark : R.style.SiempoAppTheme);
        super.onNewIntent(intent);
        currentIndexDashboard = 1;
        currentIndexPaneFragment = 2;
        mPager.setCurrentItem(currentIndexDashboard, false);
        EventBus.getDefault().postSticky(new HomePress(2));
        loadPane();
        //In case of home press, when app is launched again we need to show
        // this overlay of default launcher if siempo is not set as default
        // launcher
        showOverlayOfDefaultLauncher();

    }

    public void loadViews() {

        mPager = findViewById(R.id.pager);
        mPager.setOnTouchListener(new View.OnTouchListener() {
            private float pointX;
            private float pointY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int tolerance = 50;
                switch(event.getAction()) {
                    case MotionEvent.ACTION_MOVE:
                        return false; //This is important, if you return TRUE the action of swipe will not take place.
                    case MotionEvent.ACTION_DOWN:
                        pointX = event.getX();
                        pointY = event.getY();
                        break;
                    case MotionEvent.ACTION_UP:
                        boolean sameX = pointX + tolerance > event.getX() && pointX - tolerance < event.getX();
                        boolean sameY = pointY + tolerance > event.getY() && pointY - tolerance < event.getY();
                        if (sameX && sameY) {
                            //The user "clicked" certain point in the screen or just returned to the same position an raised the finger
                        }
                }
                DashboardActivity.this.gestureDetector.onTouchEvent(event);
                return false;
            }

        });

        linMain = findViewById(R.id.linMain);

        mPagerAdapter = new DashboardPagerAdapter(getFragmentManager());
        loadPane();
        mPager.setAdapter(mPagerAdapter);
        mPager.setCurrentItem(currentIndexDashboard);
        mPager.setOffscreenPageLimit(2);
        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }

            @Override
            public void onPageSelected(int i) {
                if(i == 1)
                {
                    AppUtils.notificationBarManaged(DashboardActivity.this, null);
                    AppUtils.statusbarColor0(DashboardActivity.this, 1);
                }
                if (currentIndexDashboard == 1 && i == 0) {
                    if (swipeCount >= 0 && swipeCount < 3) {
                        swipeCount = PrefSiempo.getInstance(DashboardActivity.this).read(PrefSiempo.TOGGLE_LEFTMENU, 0);
                        swipeCount = swipeCount + 1;
                        PrefSiempo.getInstance(DashboardActivity.this).write(PrefSiempo.TOGGLE_LEFTMENU, swipeCount);
                    }


                }
                currentIndexDashboard = i;
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        if (PrefSiempo.getInstance(this).read(PrefSiempo
                .INSTALLED_APP_VERSION_CODE, 0) == 0 || (PrefSiempo.getInstance(this).read(PrefSiempo
                .INSTALLED_APP_VERSION_CODE, 0) < UIUtils
                .getCurrentVersionCode(this))) {
            PrefSiempo.getInstance(this).write(PrefSiempo
                    .INSTALLED_APP_VERSION_CODE, UIUtils.getCurrentVersionCode(this));
        }


    }

    private void loadPane() {
        try {
            new LoadFavoritePane(PrefSiempo.getInstance(DashboardActivity.this)).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            new LoadToolPane().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            new LoadJunkFoodPane(PrefSiempo.getInstance(this)).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } catch (RejectedExecutionException e) {
            e.printStackTrace();
        }

    }


    @Override
    public void onBackPressed() {
        if (mPager != null && mPager.getCurrentItem() == 0) {
            if (DashboardActivity.currentIndexPaneFragment == 2 || DashboardActivity.currentIndexPaneFragment == 1) {
                if (mPagerAdapter.getItem(0) instanceof PaneFragment) {
                    EventBus.getDefault().post(new OnBackPressedEvent(true));
                } else {
                    mPager.setCurrentItem(1);
                }
            } else {
                mPager.setCurrentItem(1);
            }
        } else {
            if (mPager != null && mPager.getCurrentItem() == 0) {
                mPager.setCurrentItem(0);
            }
        }
    }


    @Override
    protected void onStop() {
        if (notificationDialog != null && notificationDialog.isShowing()) {
            notificationDialog.dismiss();
        }
        super.onStop();
    }

    public void notificationAccessDialog() {
        notificationDialog = new AlertDialog.Builder(DashboardActivity.this)
                .setTitle(null)
                .setMessage(getString(R.string.msg_noti_service_dialog))
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        startActivityForResult(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"), 100);
                    }
                })
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100) {
            if (isEnabled(DashboardActivity.this)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
                            && !notificationManager.isNotificationPolicyAccessGranted()) {
                        Intent intent = new Intent(
                                android.provider.Settings
                                        .ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
                        startActivityForResult(intent, 103);
                    }
                }

            } else {
                notificationAccessDialog();
            }
        }
        if (requestCode == 102) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
                        && !notificationManager.isNotificationPolicyAccessGranted()) {
                    Intent intent = new Intent(
                            android.provider.Settings
                                    .ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
                    startActivityForResult(intent, 103);
                }
            }
        }

        if (requestCode == 103) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
                    && !notificationManager.isNotificationPolicyAccessGranted()) {
                Intent intent = new Intent(
                        android.provider.Settings
                                .ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
                startActivityForResult(intent, 103);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DashboardActivity.isTextLenghGreater = "";
        currentIndexDashboard = 1;
        currentIndexPaneFragment = 2;
    }

    /**
     * Method to show overlay for default launcher setting
     */
    private void showOverLay() {
        try {
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            overlayDialog = new Dialog(this, 0);
            Objects.requireNonNull(overlayDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            overlayDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            overlayDialog.setContentView(R.layout.layout_default_launcher);
            Window window = overlayDialog.getWindow();

            window.setGravity(Gravity.BOTTOM);
            WindowManager.LayoutParams params = window.getAttributes();
            window.setAttributes(params);
            overlayDialog.getWindow().setLayout(WindowManager.LayoutParams
                    .MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

            //overlayDialog.setCancelable(false);
            overlayDialog.setCanceledOnTouchOutside(false);
            if (null != mPager && mPager.getCurrentItem() == 1) {
                overlayDialog.show();
            }

            Button btnEnable = overlayDialog.findViewById(R.id.btnEnable);
            Button btnLater = overlayDialog.findViewById(R.id.btnLater);
            btnEnable.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    overlayDialog.dismiss();
                    AppUtils.notificationBarManaged(DashboardActivity.this, linMain);
                    try {
                        Intent intent = new Intent(Settings.ACTION_HOME_SETTINGS);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    } catch (Exception e) {
                        Intent intent = new Intent(Settings.ACTION_SETTINGS);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }

                }
            });

            btnLater.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    overlayDialog.dismiss();
                    AppUtils.notificationBarManaged(DashboardActivity.this, linMain);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onEvent(NotifyBackgroundChange notifyBackgroundChange) {
        if (notifyBackgroundChange != null && notifyBackgroundChange.isNotify()) {
            changeLayoutBackground(-1);
            EventBus.getDefault().removeStickyEvent(notifyBackgroundChange);
        }

    }

    @Subscribe(sticky = true, threadMode = ThreadMode.BACKGROUND)
    public void onEvent(ThemeChangeEvent themeChangeEvent) {
        if (themeChangeEvent != null && themeChangeEvent.isNotify()) {
            Intent startMain = getIntent();
            startMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startMain.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            EventBus.getDefault().removeStickyEvent(themeChangeEvent);
            finish();
            startActivity(startMain);

        }
    }


}
