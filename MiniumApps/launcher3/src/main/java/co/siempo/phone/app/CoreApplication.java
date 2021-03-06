package co.siempo.phone.app;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.LauncherActivityInfo;
import android.content.pm.LauncherApps;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.AlarmClock;
import android.provider.CalendarContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;

import androidx.multidex.MultiDexApplication;

import com.androidnetworking.AndroidNetworking;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeModule;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import co.siempo.phone.R;
import co.siempo.phone.event.AppInstalledEvent;
import co.siempo.phone.log.Tracer;
import co.siempo.phone.models.AppMenu;
import co.siempo.phone.models.CategoryAppList;
import co.siempo.phone.models.MainListItem;
import co.siempo.phone.service.CategoriesApp;
import co.siempo.phone.service.LoadFavoritePane;
import co.siempo.phone.utils.PackageUtil;
import co.siempo.phone.utils.PrefSiempo;
import co.siempo.phone.utils.UIUtils;

import static co.siempo.phone.main.MainListItemLoader.TOOLS_ADDITIONAL_MESSAGE;
import static co.siempo.phone.main.MainListItemLoader.TOOLS_ASSISTANT;
import static co.siempo.phone.main.MainListItemLoader.TOOLS_AUTHENTICATION;
import static co.siempo.phone.main.MainListItemLoader.TOOLS_BANKING;
import static co.siempo.phone.main.MainListItemLoader.TOOLS_BOOKS;
import static co.siempo.phone.main.MainListItemLoader.TOOLS_BROWSER;
import static co.siempo.phone.main.MainListItemLoader.TOOLS_CALCULATOR;
import static co.siempo.phone.main.MainListItemLoader.TOOLS_CALENDAR;
import static co.siempo.phone.main.MainListItemLoader.TOOLS_CALL;
import static co.siempo.phone.main.MainListItemLoader.TOOLS_CAMERA;
import static co.siempo.phone.main.MainListItemLoader.TOOLS_CLOCK;
import static co.siempo.phone.main.MainListItemLoader.TOOLS_CLOUD;
import static co.siempo.phone.main.MainListItemLoader.TOOLS_COURCE;
import static co.siempo.phone.main.MainListItemLoader.TOOLS_DOC;
import static co.siempo.phone.main.MainListItemLoader.TOOLS_EMAIL;
import static co.siempo.phone.main.MainListItemLoader.TOOLS_FILES;
import static co.siempo.phone.main.MainListItemLoader.TOOLS_FITNESS;
import static co.siempo.phone.main.MainListItemLoader.TOOLS_FLASH;
import static co.siempo.phone.main.MainListItemLoader.TOOLS_FOOD;
import static co.siempo.phone.main.MainListItemLoader.TOOLS_HEALTH;
import static co.siempo.phone.main.MainListItemLoader.TOOLS_JOURNAL;
import static co.siempo.phone.main.MainListItemLoader.TOOLS_LANGUAGES;
import static co.siempo.phone.main.MainListItemLoader.TOOLS_LEARNING;
import static co.siempo.phone.main.MainListItemLoader.TOOLS_MAP;
import static co.siempo.phone.main.MainListItemLoader.TOOLS_MEDITATION;
import static co.siempo.phone.main.MainListItemLoader.TOOLS_MESSAGE;
import static co.siempo.phone.main.MainListItemLoader.TOOLS_MICROPHONE;
import static co.siempo.phone.main.MainListItemLoader.TOOLS_MUSIC;
import static co.siempo.phone.main.MainListItemLoader.TOOLS_NEWS;
import static co.siempo.phone.main.MainListItemLoader.TOOLS_NOTES;
import static co.siempo.phone.main.MainListItemLoader.TOOLS_PAYMENT;
import static co.siempo.phone.main.MainListItemLoader.TOOLS_PHOTOS;
import static co.siempo.phone.main.MainListItemLoader.TOOLS_PODCAST;
import static co.siempo.phone.main.MainListItemLoader.TOOLS_RECORDER;
import static co.siempo.phone.main.MainListItemLoader.TOOLS_SEARCH;
import static co.siempo.phone.main.MainListItemLoader.TOOLS_SETTINGS;
import static co.siempo.phone.main.MainListItemLoader.TOOLS_SLEEP;
import static co.siempo.phone.main.MainListItemLoader.TOOLS_TODO;
import static co.siempo.phone.main.MainListItemLoader.TOOLS_TRANSLATE;
import static co.siempo.phone.main.MainListItemLoader.TOOLS_TRANSPORT;
import static co.siempo.phone.main.MainListItemLoader.TOOLS_VIDEO;
import static co.siempo.phone.main.MainListItemLoader.TOOLS_VOICE;
import static co.siempo.phone.main.MainListItemLoader.TOOLS_WEATHER;
import static co.siempo.phone.main.MainListItemLoader.TOOLS_WELLNESS;


public abstract class CoreApplication extends MultiDexApplication {

    private static CoreApplication sInstance;
    UserManager userManager;
    LauncherApps launcherApps;



    public String getApplicationName(String packageName) {
        for (App app : apps) {
            if (app.packageName.equals(packageName)) {
                return app.displayName;
            }

        }
        return "not found";
    }

    public String getApplicationName(App app) {
        for (App loadedApp: apps) {
            if (loadedApp.packageName.equals(app.packageName) && loadedApp.isWorkApp == app.isWorkApp) {
                return loadedApp.displayName;
            }
        }
        return "not found";
    }

    private List<App> apps = new LinkedList<>();

    public List<App> getApplications() {
        return apps;
    }

    private Map<String, Drawable> mMemoryCache = new HashMap<>();

    private Map<String, Drawable> mWorkAppsBitmaps = new HashMap<>();


    public void addOrRemoveApplicationInfo(boolean addingOrDelete, String
            packageName) {


        try {

            ApplicationInfo appInfo = getPackageManager().getApplicationInfo(packageName, PackageManager.GET_META_DATA);

            App app = new App();
            app.packageName = appInfo.packageName;
            app.displayName = "" + getPackageManager().getApplicationLabel(appInfo);

            if (addingOrDelete) {
                    apps.add(app);
                    EventBus.getDefault().post(new AppInstalledEvent(true));


            } else {
                App toRemove = null;
                for (App appCandidate: apps) {
                    if (appCandidate.packageName == appInfo.packageName) {
                        toRemove = appCandidate;
                        break;
                    }
                }
                if (toRemove != null) {
                    apps.remove(toRemove);
                    EventBus.getDefault().post(new AppInstalledEvent(true));
                }
            }

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }


    private ArrayList<App> junkFoodList = new ArrayList<>();
    private ArrayList<MainListItem> toolItemsList = new ArrayList<>();
    private ArrayList<MainListItem> toolBottomItemsList = new ArrayList<>();
    private ArrayList<MainListItem> favoriteItemsList = new ArrayList<>();
    private boolean isHideIconBranding = true;
    private boolean isRandomize = true;
    public List<CategoryAppList> categoryAppList = new ArrayList<>();
    private List<String> runningDownloadigFileList = new ArrayList<>();


    /**
     *
     * Retrieving the third party app usage time when siempo set as launcher.
     *
     * @return
     */
    public ArrayMap<String, Long> getThirdpartyAppLogasLauncher() {
        String storedHashMapString = PrefSiempo.getInstance(this).read(PrefSiempo.THIRD_PARTY_APP_LOG_AS_LAUNCHER, "");
        java.lang.reflect.Type type = new TypeToken<ArrayMap<String, Long>>() {
        }.getType();
        return new Gson().fromJson(storedHashMapString, type);
    }

    /**
     * Retrieving the third party app usage time when siempo not set as launcher.
     *
     * @return
     */
    public ArrayMap<String, Long> getThirdpartyAppLogasnotLauncher() {
        String storedHashMapString = PrefSiempo.getInstance(this).read(PrefSiempo.THIRD_PARTY_APP_LOG_NOT_AS_LAUNCHER, "");
        java.lang.reflect.Type type = new TypeToken<ArrayMap<String, Long>>() {
        }.getType();
        return new Gson().fromJson(storedHashMapString, type);
    }

    public static synchronized CoreApplication getInstance() {
        return sInstance;
    }

    public List<String> getRunningDownloadigFileList() {
        return runningDownloadigFileList;
    }

    public boolean isHideIconBranding() {
        return isHideIconBranding;
    }

    public void setHideIconBranding(boolean hideIconBranding) {
        isHideIconBranding = hideIconBranding;
    }

    public boolean isRandomize() {
        return isRandomize;
    }

    public void setRandomize(boolean randomize) {
        this.isRandomize = randomize;
    }

    public ArrayList<App> getJunkFoodList() {
        return junkFoodList;
    }

    public void setJunkFoodList(ArrayList<App> junkFoodList) {
        this.junkFoodList = junkFoodList;
    }

    public ArrayList<MainListItem> getToolItemsList() {
        return toolItemsList;
    }

    public void setToolItemsList(ArrayList<MainListItem> toolItemsList) {
        this.toolItemsList = toolItemsList;
    }

    public ArrayList<MainListItem> getFavoriteItemsList() {
        return favoriteItemsList;
    }

    public void setFavoriteItemsList(ArrayList<MainListItem> favoriteItemsList) {
        this.favoriteItemsList = favoriteItemsList;
    }

    public ArrayList<MainListItem> getToolBottomItemsList() {
        return toolBottomItemsList;
    }

    public void setToolBottomItemsList(ArrayList<MainListItem> toolBottomItemsList) {
        this.toolBottomItemsList = toolBottomItemsList;
    }

    public List<CategoryAppList> getCategoryAppList() {
        return new ArrayList<>(categoryAppList);
    }



    @Override
    public void onCreate() {
        super.onCreate();
        userManager = (UserManager) getSystemService(Context.USER_SERVICE);
        launcherApps = (LauncherApps) getSystemService(Context.LAUNCHER_APPS_SERVICE);
        sInstance = this;
        init();
        getAllApplicationPackageName();
    }

    /**
     * This method is used for fetch all installed application package list.
     */
    public void getAllApplicationPackageName() {
        apps.clear();
        new LoadApplications().execute();
    }

    protected void init() {
        // set initial configurations here
        configTracer();
        configIconify();
        configureNetworking();
        if (PrefSiempo.getInstance(this).read(PrefSiempo.INSTALLED_APP_VERSION_CODE, 0) == 0) {
            PrefSiempo.getInstance(this).write(PrefSiempo.INSTALLED_APP_VERSION_CODE,
                    UIUtils.getCurrentVersionCode(this));
        }
        if (getToolsSettings() == null || getToolsSettings().isEmpty()) {
            configureToolsPane();
        } else {
            if (getToolsSettings().size() <= 16) {
                PrefSiempo.getInstance(this).write(PrefSiempo.SORTED_MENU, "");
                HashMap<Integer, AppMenu> oldMap;
                String storedHashMapString = PrefSiempo.getInstance(this).read(PrefSiempo.TOOLS_SETTING, "");
                java.lang.reflect.Type type = new TypeToken<HashMap<Integer, AppMenu>>() {
                }.getType();
                oldMap = new Gson().fromJson(storedHashMapString, type);
                PrefSiempo.getInstance(this).write(PrefSiempo.TOOLS_SETTING, "");
                configureToolsPane();
                HashMap<Integer, AppMenu> newMap = getToolsSettings();
                for (Map.Entry<Integer, AppMenu> newMenuEntry : newMap.entrySet()) {
                    if (oldMap.containsKey(newMenuEntry.getKey())) {
                        newMenuEntry.getValue().setApplicationName(oldMap.get(newMenuEntry.getKey()).getApplicationName());
                    }
                }
                String hashMapToolSettings = new Gson().toJson(newMap);
                PrefSiempo.getInstance(this).write(PrefSiempo.TOOLS_SETTING, hashMapToolSettings);
            }


        }
        setHideIconBranding(PrefSiempo.getInstance(sInstance).read(PrefSiempo.IS_ICON_BRANDING, true));
        setRandomize(PrefSiempo.getInstance(sInstance).read(PrefSiempo.IS_RANDOMIZE_JUNKFOOD, true));


        /*
         * Fetch Category App List
         */
        Intent intent = new Intent(this, CategoriesApp.class);
        startService(intent);

    }

    /**
     * first time called when user launch the application to set the default value for the
     * application show/hide bind tools to specific package name.
     */
    private void configureToolsPane() {
        try {
            if (PrefSiempo.getInstance(this).read(PrefSiempo.TOOLS_SETTING, "").equalsIgnoreCase("")) {
                HashMap<Integer, AppMenu> map = new HashMap<>();
                //by default on install, the "Recorder", "Payment", and "Browser" tools are hidden
                // (they may be revealed via the tool-selection screen (see tool-selection below)).
                map.put(TOOLS_MAP, new AppMenu(true, false, CoreApplication.getInstance
                        ().getApplicationByCategory(1).size() == 1 ? CoreApplication.getInstance().getApplicationByCategory(1).get(0).packageName : ""));
                map.put(TOOLS_TRANSPORT, new AppMenu(true, false, CoreApplication.getInstance
                        ().getApplicationByCategory(2).size() == 1 ? CoreApplication.getInstance().getApplicationByCategory(2).get(0).packageName : ""));
                map.put(TOOLS_CALENDAR, new AppMenu(true, false, CoreApplication.getInstance
                        ().getApplicationByCategory(3).size() == 1 ? CoreApplication.getInstance().getApplicationByCategory(3).get(0).packageName : ""));
                map.put(TOOLS_WEATHER, new AppMenu(true, false, CoreApplication.getInstance
                        ().getApplicationByCategory(4).size() == 1 ?
                        CoreApplication.getInstance().getApplicationByCategory(4).get(0).packageName : ""));
                map.put(TOOLS_NOTES, new AppMenu(true, false, CoreApplication.getInstance
                        ().getApplicationByCategory(5).size() == 1 ?
                        getString(R.string.notes) : ""));
                map.put(TOOLS_RECORDER, new AppMenu(false, false, CoreApplication
                        .getInstance().getApplicationByCategory(6).size() == 1 ? CoreApplication.getInstance().getApplicationByCategory(6).get(0).packageName : ""));
                map.put(TOOLS_CAMERA, new AppMenu(true, false, CoreApplication.getInstance
                        ().getApplicationByCategory(7).size() == 1 ?
                        CoreApplication.getInstance().getApplicationByCategory(7).get(0).packageName : ""));
                map.put(TOOLS_PHOTOS, new AppMenu(true, false, CoreApplication.getInstance
                        ().getApplicationByCategory(8).size() == 1 ? CoreApplication.getInstance().getApplicationByCategory(8).get(0).packageName : ""));
                map.put(TOOLS_PAYMENT, new AppMenu(false, false, CoreApplication
                        .getInstance().getApplicationByCategory(9).size() == 1 ? CoreApplication.getInstance().getApplicationByCategory(9).get(0).packageName : ""));
                map.put(TOOLS_WELLNESS, new AppMenu(true, false, CoreApplication
                        .getInstance().getApplicationByCategory(10).size() == 1 ? CoreApplication.getInstance().getApplicationByCategory(10).get(0).packageName : ""));

                map.put(TOOLS_TODO, new AppMenu(false, false, CoreApplication
                        .getInstance().getApplicationByCategory(12).size() == 1 ?
                        CoreApplication.getInstance().getApplicationByCategory
                                (12).get(0).packageName : ""));
                map.put(TOOLS_BROWSER, new AppMenu(false, false, CoreApplication
                        .getInstance().getApplicationByCategory(11).size() == 1
                        ? CoreApplication.getInstance().getApplicationByCategory
                        (11).get(0).packageName : ""));
                map.put(TOOLS_MUSIC, new AppMenu(false, false, CoreApplication
                        .getInstance().getApplicationByCategory(17).size() == 1 ?
                        CoreApplication.getInstance().getApplicationByCategory
                                (17).get(0).packageName : ""));
                map.put(TOOLS_PODCAST, new AppMenu(false, false, CoreApplication
                        .getInstance().getApplicationByCategory(18).size() == 1 ?
                        CoreApplication.getInstance().getApplicationByCategory
                                (18).get(0).packageName : ""));

                map.put(TOOLS_FOOD, new AppMenu(false, false, CoreApplication
                        .getInstance().getApplicationByCategory(19).size() == 1 ?
                        CoreApplication.getInstance().getApplicationByCategory
                                (19).get(0).packageName : ""));

                map.put(TOOLS_FITNESS, new AppMenu(false, false, CoreApplication
                        .getInstance().getApplicationByCategory(20).size() == 1 ?
                        CoreApplication.getInstance().getApplicationByCategory
                                (20).get(0).packageName : ""));

                map.put(TOOLS_CALL, new AppMenu(true, true, CoreApplication.getInstance
                        ().getApplicationByCategory(13).size() == 1 ? CoreApplication.getInstance().getApplicationByCategory(13).get(0).packageName : ""));
                map.put(TOOLS_CLOCK, new AppMenu(true, true, CoreApplication.getInstance
                        ().getApplicationByCategory(14).size() == 1 ? CoreApplication.getInstance().getApplicationByCategory(14).get(0).packageName : ""));
                map.put(TOOLS_MESSAGE, new AppMenu(true, true, CoreApplication.getInstance
                        ().getApplicationByCategory(15).size() == 1 ? CoreApplication.getInstance().getApplicationByCategory(15).get(0).packageName : ""));
                map.put(TOOLS_EMAIL, new AppMenu(true, true, CoreApplication.getInstance
                        ().getApplicationByCategory(16).size() == 1 ? CoreApplication.getInstance().getApplicationByCategory(16).get(0).packageName : ""));

                map.put(TOOLS_CLOUD, new AppMenu(false, false, CoreApplication.getInstance
                        ().getApplicationByCategory(21).size() == 1 ? CoreApplication.getInstance().getApplicationByCategory(21).get(0).packageName : ""));


                map.put(TOOLS_BOOKS, new AppMenu(false, false, CoreApplication.getInstance
                        ().getApplicationByCategory(22).size() == 1 ? CoreApplication.getInstance().getApplicationByCategory(22).get(0).packageName : ""));

                map.put(TOOLS_AUTHENTICATION, new AppMenu(false, false, CoreApplication.getInstance
                        ().getApplicationByCategory(23).size() == 1 ? CoreApplication.getInstance().getApplicationByCategory(23).get(0).packageName : ""));


                map.put(TOOLS_ASSISTANT, new AppMenu(false, false, CoreApplication.getInstance
                        ().getApplicationByCategory(24).size() == 1 ? CoreApplication.getInstance().getApplicationByCategory(24).get(0).packageName : ""));


                map.put(TOOLS_ADDITIONAL_MESSAGE, new AppMenu(false, false, CoreApplication.getInstance
                        ().getApplicationByCategory(25).size() == 1 ? CoreApplication.getInstance().getApplicationByCategory(25).get(0).packageName : ""));

                map.put(TOOLS_BANKING, new AppMenu(false, false, CoreApplication.getInstance
                        ().getApplicationByCategory(26).size() == 1 ? CoreApplication.getInstance().getApplicationByCategory(26).get(0).packageName : ""));


                map.put(TOOLS_COURCE, new AppMenu(false, false, CoreApplication.getInstance
                        ().getApplicationByCategory(27).size() == 1 ? CoreApplication.getInstance().getApplicationByCategory(27).get(0).packageName : ""));



                map.put(TOOLS_DOC, new AppMenu(false, false, CoreApplication.getInstance
                        ().getApplicationByCategory(28).size() == 1 ? CoreApplication.getInstance().getApplicationByCategory(28).get(0).packageName : ""));




                map.put(TOOLS_FILES, new AppMenu(false, false, CoreApplication.getInstance
                        ().getApplicationByCategory(29).size() == 1 ? CoreApplication.getInstance().getApplicationByCategory(29).get(0).packageName : ""));


                map.put(TOOLS_FLASH, new AppMenu(false, false, CoreApplication.getInstance
                        ().getApplicationByCategory(30).size() == 1 ? CoreApplication.getInstance().getApplicationByCategory(30).get(0).packageName : ""));



                map.put(TOOLS_HEALTH, new AppMenu(false, false, CoreApplication.getInstance
                        ().getApplicationByCategory(31).size() == 1 ? CoreApplication.getInstance().getApplicationByCategory(31).get(0).packageName : ""));



                map.put(TOOLS_JOURNAL, new AppMenu(false, false, CoreApplication.getInstance
                        ().getApplicationByCategory(32).size() == 1 ? CoreApplication.getInstance().getApplicationByCategory(32).get(0).packageName : ""));



                map.put(TOOLS_LANGUAGES, new AppMenu(false, false, CoreApplication.getInstance
                        ().getApplicationByCategory(33).size() == 1 ? CoreApplication.getInstance().getApplicationByCategory(33).get(0).packageName : ""));




                map.put(TOOLS_LEARNING, new AppMenu(false, false, CoreApplication.getInstance
                        ().getApplicationByCategory(34).size() == 1 ? CoreApplication.getInstance().getApplicationByCategory(34).get(0).packageName : ""));



                map.put(TOOLS_MEDITATION, new AppMenu(false, false, CoreApplication.getInstance
                        ().getApplicationByCategory(35).size() == 1 ? CoreApplication.getInstance().getApplicationByCategory(35).get(0).packageName : ""));



                map.put(TOOLS_MICROPHONE, new AppMenu(false, false, CoreApplication.getInstance
                        ().getApplicationByCategory(36).size() == 1 ? CoreApplication.getInstance().getApplicationByCategory(36).get(0).packageName : ""));





                map.put(TOOLS_NEWS, new AppMenu(false, false, CoreApplication.getInstance
                        ().getApplicationByCategory(37).size() == 1 ? CoreApplication.getInstance().getApplicationByCategory(37).get(0).packageName : ""));



                map.put(TOOLS_SEARCH, new AppMenu(false, false, CoreApplication.getInstance
                        ().getApplicationByCategory(38).size() == 1 ? CoreApplication.getInstance().getApplicationByCategory(38).get(0).packageName : ""));




                map.put(TOOLS_SETTINGS, new AppMenu(false, false, CoreApplication.getInstance
                        ().getApplicationByCategory(39).size() == 1 ? CoreApplication.getInstance().getApplicationByCategory(39).get(0).packageName : ""));




                map.put(TOOLS_VOICE, new AppMenu(false, false, CoreApplication.getInstance
                        ().getApplicationByCategory(40).size() == 1 ? CoreApplication.getInstance().getApplicationByCategory(40).get(0).packageName : ""));


                map.put(TOOLS_SLEEP, new AppMenu(false, false, CoreApplication.getInstance
                        ().getApplicationByCategory(41).size() == 1 ? CoreApplication.getInstance().getApplicationByCategory(41).get(0).packageName : ""));


                map.put(TOOLS_CALCULATOR, new AppMenu(false, false, CoreApplication.getInstance
                        ().getApplicationByCategory(42).size() == 1 ? CoreApplication.getInstance().getApplicationByCategory(42).get(0).packageName : ""));


                map.put(TOOLS_TRANSLATE, new AppMenu(false, false, CoreApplication.getInstance
                        ().getApplicationByCategory(43).size() == 1 ? CoreApplication.getInstance().getApplicationByCategory(43).get(0).packageName : ""));

                map.put(TOOLS_VIDEO, new AppMenu(false, false, CoreApplication.getInstance
                        ().getApplicationByCategory(44).size() == 1 ? CoreApplication.getInstance().getApplicationByCategory(44).get(0).packageName : ""));












                String hashMapToolSettings = new Gson().toJson(map);
                PrefSiempo.getInstance(this).write(PrefSiempo.TOOLS_SETTING, hashMapToolSettings);


            /*
              SSA-1321: Adding the mentioned apps in junk food by default
             */
                LinkedList<App> junkfoodList = new LinkedList<>();

                Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
                mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
                List<ResolveInfo> pkgAppsList = getPackageManager().queryIntentActivities(mainIntent, 0);


                for (ResolveInfo resolveInfo : pkgAppsList) {

                    String packageName = !TextUtils.isEmpty(resolveInfo.activityInfo
                            .packageName) ? resolveInfo.activityInfo
                            .packageName : "";
                    if (packageName.contains("com.facebook.katana") || packageName.contains("com.facebook.lite") || packageName
                            .contains("com.king")) {
                        if (UIUtils.isAppInstalledAndEnabled(getApplicationContext(), packageName)) {
                            App junk = new App();
                            junk.packageName = packageName;
                            junkfoodList.add(junk);
                        }
                    } else {

                        switch (packageName) {
                            case Constants.SNAP_PACKAGE:
                            case Constants.INSTAGRAM_PACKAGE:
                            case Constants.LINKEDIN_PACKAGE:
                            case Constants.CLASH_ROYAL_PACKAGE:
                            case Constants.HINGE_PACKAGE:
                            case Constants.NETFLIX_PACKAGE:
                            case Constants.REDDIT_PACKAGE:
                            case Constants.TINDER_PACKAGE:
                            case Constants.GRINDR_PACKAGE:
                            case Constants.YOUTUBE_PACKAGE:
                            case Constants.COFFEE_MEETS_PACKAGE:
                            case Constants.TWITTER_PACKAGE:
                            case Constants.BUMBLE_PACKAGE:
                                if (UIUtils.isAppInstalledAndEnabled(getApplicationContext(), packageName)) {
                                    App junk = new App();
                                    junk.packageName = packageName;
                                    junkfoodList.add(junk);
                                }
                                break;
                        }

                    }
                }


                PrefSiempo.getInstance(this).writeAppList(PrefSiempo.JUNKFOOD_APPS, junkfoodList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * HashMap to store tools settings data.
     *
     * @return
     */
    public HashMap<Integer, AppMenu> getToolsSettings() {
        String storedHashMapString = PrefSiempo.getInstance(this).read(PrefSiempo.TOOLS_SETTING, "");
        java.lang.reflect.Type type = new TypeToken<HashMap<Integer, AppMenu>>() {
        }.getType();
        return new Gson().fromJson(storedHashMapString, type);
    }

    private void configureNetworking() {
        AndroidNetworking.initialize(getApplicationContext());
    }



    private void configTracer() {
        Tracer.init();
    }


    public void logException(Throwable e) {
        try {
            Log.getStackTraceString(e);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void configIconify() {
        Iconify.with(new FontAwesomeModule());
    }



    public void updateBlockedList() {
        try {
            Set<String> blockedApps = PrefSiempo.getInstance(this).read(PrefSiempo
                    .BLOCKED_APPLIST, new HashSet<String>());


            List<String> packagesList = new ArrayList<>();
            for (App app : this.apps) {
                packagesList.add(app.packageName);
            }
            if (blockedApps != null && blockedApps.size() == 0) {
                blockedApps.addAll(packagesList);
                PrefSiempo.getInstance(this).write(PrefSiempo
                        .BLOCKED_APPLIST, blockedApps);
            }

        } catch (Exception e) {
            Tracer.d("Exception e ::" + e.toString());
        }
    }

    /**
     * Return the application name by providing it's package name.
     *
     * @param packageName
     * @return application name
     */
    public String getApplicationNameFromPackageName(String packageName) {
        return getApplicationName(packageName);
    }

    /**
     * Return the application icon by providing it's package name.
     *
     * @param packagename
     * @return application name
     */
    public Drawable getApplicationIconFromPackageName(String packagename) {
        Drawable icon = null;
        try {
            icon = getPackageManager().getApplicationIcon(packagename);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return icon;
    }

    /**
     * Get List of installed application related to specific category.
     *
     * @param id
     * @return
     */
    public ArrayList<App> getApplicationByCategory(int id) {
        ArrayList<App> list = new ArrayList<>();
        List<ResolveInfo> matchingApps;

        switch (id) {
            case TOOLS_MAP:// Map
                Double myLatitude = 44.433106;
                Double myLongitude = 26.103687;
                String labelLocation = "Jorgesys @ Bucharest";
                String urlAddress = "http://maps.google.com/maps?q=" + myLatitude + "," + myLongitude + "(" + labelLocation + ")&iwloc=A&hl=es";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlAddress));

                matchingApps = getPackageManager().queryIntentActivities(intent, 0);
                AddMatchingApps(list, matchingApps);
                break;
            case TOOLS_TRANSPORT:// Transport
                break;
            case TOOLS_CALENDAR://Calender
                Uri.Builder builder =
                        CalendarContract.CONTENT_URI.buildUpon();
                builder.appendPath("time");
                Intent calenderIntent =
                        new Intent(Intent.ACTION_VIEW, builder.build());
                matchingApps = getPackageManager().queryIntentActivities(calenderIntent, 0);
                AddMatchingApps(list, matchingApps);
                break;


            case TOOLS_WEATHER://Weather
                break;
            case TOOLS_NOTES://Notes
                String filepath = "mnt/sdcard/doc.txt";
                File file = new File(filepath);
                if (!file.exists()) {
                    try {
                        file.createNewFile();
                    } catch (IOException e) {
                        CoreApplication.getInstance().logException(e);
                        e.printStackTrace();
                    }
                }

                Intent intentNotes = new Intent(Intent.ACTION_EDIT);
                intentNotes.setDataAndType(Uri.fromFile(file), "text/plain");

                matchingApps = getPackageManager().queryIntentActivities(intentNotes, 0);
                AddMatchingApps(list, matchingApps);

                try {

                    if (UIUtils.isAppInstalledAndEnabled(this, "com.google.android.keep")) {
                        Intent keepIntent = new Intent();
                        keepIntent.setPackage("com.google.android.keep");
                        List<ResolveInfo> resolveInfo = getPackageManager().queryIntentActivities(keepIntent, 0);
                        AddMatchingApps(list, resolveInfo);
                    }

                    if (UIUtils.isAppInstalledAndEnabled(this, "com.evernote")) {
                        Intent evernote = new Intent();
                        evernote.setPackage("com.evernote");
                        List<ResolveInfo> resolveInfoEverNote = getPackageManager()
                                .queryIntentActivities(evernote, 0);
                        AddMatchingApps(list, resolveInfoEverNote);
                    }

                    if (UIUtils.isAppInstalledAndEnabled(this, "com.microsoft.office.onenote")) {
                        Intent oneNote = new Intent();
                        oneNote.setPackage("com.microsoft.office.onenote");
                        List<ResolveInfo> resolveInfoOneNote = getPackageManager()
                                .queryIntentActivities(oneNote, 0);
                        AddMatchingApps(list, resolveInfoOneNote);

                    }
                    if (UIUtils.isAppInstalledAndEnabled(this, "com.automattic.simplenote")) {
                        Intent simpleNote = new Intent();
                        simpleNote.setPackage("com.automattic.simplenote");
                        List<ResolveInfo> resolveInfoSimpleNote =
                                getPackageManager()
                                        .queryIntentActivities(simpleNote, 0);
                        AddMatchingApps(list, resolveInfoSimpleNote);

                    }
                    if (UIUtils.isAppInstalledAndEnabled(this, "com.socialnmobile.dictapps.notepad.color.note")) {
                        Intent colorNote = new Intent();
                        colorNote.setPackage("com.socialnmobile.dictapps.notepad.color.note");
                        List<ResolveInfo> resolveInfoSimpleNote =
                                getPackageManager()
                                        .queryIntentActivities(colorNote, 0);
                        AddMatchingApps(list, resolveInfoSimpleNote);

                    }

                    if (UIUtils.isAppInstalledAndEnabled(this, "com.task.notes")) {
                        Intent colorNote = new Intent();
                        colorNote.setPackage("com.task.notes");
                        List<ResolveInfo> resolveInfoSimpleNote =
                                getPackageManager()
                                        .queryIntentActivities(colorNote, 0);
                        AddMatchingApps(list, resolveInfoSimpleNote);
                    }

                    if (UIUtils.isAppInstalledAndEnabled(this, "com.edi.masaki.mymemoapp")) {
                        Intent colorNote = new Intent();
                        colorNote.setPackage("com.edi.masaki.mymemoapp");
                        List<ResolveInfo> resolveInfoSimpleNote =
                                getPackageManager()
                                        .queryIntentActivities(colorNote, 0);
                        AddMatchingApps(list, resolveInfoSimpleNote);

                    }

                    if (UIUtils.isAppInstalledAndEnabled(this, "com.dencreak.esmemo")) {
                        Intent colorNote = new Intent();
                        colorNote.setPackage("com.dencreak.esmemo");
                        List<ResolveInfo> resolveInfoSimpleNote =
                                getPackageManager()
                                        .queryIntentActivities(colorNote, 0);
                        AddMatchingApps(list, resolveInfoSimpleNote);

                    }
                    if (UIUtils.isAppInstalledAndEnabled(this, "com.samsung.android.snote")) {
                        Intent colorNote = new Intent();
                        colorNote.setPackage("com.samsung.android.snote");
                        List<ResolveInfo> resolveInfoSimpleNote =
                                getPackageManager()
                                        .queryIntentActivities(colorNote, 0);
                        AddMatchingApps(list, resolveInfoSimpleNote);

                    }
                    if (UIUtils.isAppInstalledAndEnabled(this, "com.samsung.android.app.notes")) {
                        Intent colorNote = new Intent();
                        colorNote.setPackage("com.samsung.android.app.notes");
                        List<ResolveInfo> resolveInfoSimpleNote =
                                getPackageManager()
                                        .queryIntentActivities(colorNote, 0);
                        AddMatchingApps(list, resolveInfoSimpleNote);

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


                break;
            case TOOLS_RECORDER://Recorder

            case TOOLS_LANGUAGES:

            case TOOLS_JOURNAL:

            case TOOLS_HEALTH:

            case TOOLS_FLASH:

            case TOOLS_FILES:

            case TOOLS_DOC:

            case TOOLS_COURCE:

            case TOOLS_BANKING:

            case TOOLS_ADDITIONAL_MESSAGE:

            case TOOLS_ASSISTANT:

            case TOOLS_AUTHENTICATION:

            case TOOLS_BOOKS:

            case TOOLS_CLOUD:

            case TOOLS_LEARNING:

            case TOOLS_MEDITATION:

            case TOOLS_MICROPHONE:

            case TOOLS_NEWS:

            case TOOLS_VIDEO:

            case TOOLS_TRANSLATE:

            case TOOLS_CALCULATOR:

            case TOOLS_SLEEP:

            case TOOLS_VOICE:

            case TOOLS_SETTINGS:

            case TOOLS_SEARCH:
                break;
            case TOOLS_CAMERA://Camera
                Intent intentCamera = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                matchingApps = getPackageManager().queryIntentActivities(intentCamera, 0);
                AddMatchingApps(list, matchingApps);
                break;
            case TOOLS_PHOTOS://Photos
                Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                pickIntent.setType("image/* video/*");
                matchingApps = getPackageManager().queryIntentActivities(pickIntent, 0);
                AddMatchingApps(list, matchingApps);

                break;
            case TOOLS_PAYMENT://Payment
                break;
            case TOOLS_WELLNESS://Wellness
                break;
            case TOOLS_BROWSER://Browser
                Intent intentBrowser = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/"));
                matchingApps = getPackageManager().queryIntentActivities(intentBrowser, 0);
                AddMatchingApps(list, matchingApps);
                break;

            case TOOLS_CALL://Call
                Uri number = Uri.parse("tel:");
                Intent dial = new Intent(Intent.ACTION_DIAL, number);
                matchingApps = getPackageManager().queryIntentActivities(dial, 0);
                AddMatchingApps(list, matchingApps);
                break;
            case TOOLS_CLOCK://Clock
                Intent intentClock = new Intent(AlarmClock.ACTION_SET_ALARM);
                matchingApps = getPackageManager().queryIntentActivities(intentClock, 0);
                AddMatchingApps(list, matchingApps);
                break;
            case TOOLS_MESSAGE://message
                Intent message = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + ""));
                message.putExtra("sms_body", "Test text...");
                matchingApps = getPackageManager().queryIntentActivities(message, 0);
                AddMatchingApps(list, matchingApps);

                try {
                    if (UIUtils.isAppInstalledAndEnabled(this, Constants
                            .WHATSAPP_PACKAGE)) {
                        Intent keepIntent = new Intent();
                        keepIntent.setPackage(Constants
                                .WHATSAPP_PACKAGE);
                        List<ResolveInfo> resolveInfo = getPackageManager().queryIntentActivities(keepIntent, 0);
                        AddMatchingApps(list, resolveInfo);

                    }

                    if (UIUtils.isAppInstalledAndEnabled(this, Constants
                            .LINE_PACKAGE)) {
                        Intent evernote = new Intent();
                        evernote.setPackage(Constants
                                .LINE_PACKAGE);
                        List<ResolveInfo> resolveInfoEverNote = getPackageManager()
                                .queryIntentActivities(evernote, 0);
                        AddMatchingApps(list, resolveInfoEverNote);

                    }

                    if (UIUtils.isAppInstalledAndEnabled(this, Constants
                            .VIBER_PACKAGE)) {
                        Intent evernote = new Intent();
                        evernote.setPackage(Constants
                                .VIBER_PACKAGE);
                        List<ResolveInfo> resolveInfoEverNote = getPackageManager()
                                .queryIntentActivities(evernote, 0);
                        AddMatchingApps(list, resolveInfoEverNote);

                    }

                    if (UIUtils.isAppInstalledAndEnabled(this, Constants
                            .SKYPE_PACKAGE)) {
                        Intent evernote = new Intent();
                        evernote.setPackage(Constants
                                .SKYPE_PACKAGE);
                        List<ResolveInfo> resolveInfoEverNote = getPackageManager()
                                .queryIntentActivities(evernote, 0);
                        AddMatchingApps(list, resolveInfoEverNote);

                    }
                    if (UIUtils.isAppInstalledAndEnabled(this, Constants
                            .WECHAT_PACKAGE)) {
                        Intent evernote = new Intent();
                        evernote.setPackage(Constants
                                .WECHAT_PACKAGE);
                        List<ResolveInfo> resolveInfoEverNote = getPackageManager()
                                .queryIntentActivities(evernote, 0);
                        AddMatchingApps(list, resolveInfoEverNote);

                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case TOOLS_EMAIL://email
                Intent intentEmail = new Intent(Intent.ACTION_VIEW);
                Uri data = Uri.parse("mailto:recipient@example.com?subject=" + "" + "&body=" + "");
                intentEmail.setData(data);
                matchingApps = getPackageManager().queryIntentActivities(intentEmail, 0);
                AddMatchingApps(list, matchingApps);

                break;

            case TOOLS_MUSIC:// Music
                Intent intentMusic = new Intent(MediaStore
                        .INTENT_ACTION_MUSIC_PLAYER);
                matchingApps = getPackageManager().queryIntentActivities(intentMusic, 0);
                AddMatchingApps(list, matchingApps);

                break;

            case TOOLS_PODCAST://PODCAST
                break;

            case TOOLS_FOOD://Food
                break;

            case TOOLS_FITNESS://Fitness
                break;

            default:
                break;
        }
        return list;
    }

    private void AddMatchingApps(ArrayList<App> list, List<ResolveInfo> matchingApps) {
        for (ResolveInfo info: matchingApps) {
            for (App app: getApplications()) {
                if (info.activityInfo.packageName.equals(app.packageName)) {
                    list.add(app);
                }
            }

        }
    }


    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, new BitmapDrawable(getResources(), bitmap));
        }
    }

    public Drawable getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }

    public Drawable getBitMapByApp(App app) {
        if (app.isWorkApp) {
            return mWorkAppsBitmaps.get(app.packageName);
        }
        return mMemoryCache.get(app.packageName);
    }

    private class LoadApplications extends AsyncTask<Object, Object, Object> {

        @Override
        protected Void doInBackground(Object... params) {


            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                LoadApplicationThroughLauncherApps();
            }
            else {
                LoadApplicationThroughQueryIntent();
            }
            new LoadFavoritePane(PrefSiempo.getInstance(CoreApplication.this)).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            return null;
        }

        private void LoadApplicationThroughQueryIntent() {
            Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
            mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            PackageManager packageManager = getPackageManager();
            List<ResolveInfo> pkgAppsList = packageManager.queryIntentActivities(mainIntent, 0);
            for (ResolveInfo appInfo : pkgAppsList) {
                try {
                    String packageName = appInfo.activityInfo.packageName;
                    if (!packageName.equalsIgnoreCase(getPackageName())) {

                        App app = new App();
                        app.packageName = packageName;
                        String applicationName = appInfo.loadLabel(packageManager).toString();
                        if (applicationName == null) {
                            ApplicationInfo applicationInfo = null;
                            try {
                                applicationInfo = packageManager.getApplicationInfo(packageName, 0);
                            } catch (final PackageManager.NameNotFoundException e) {
                                e.printStackTrace();
                            }
                            String applicationNameTemp = (String) (applicationInfo != null ? packageManager.getApplicationLabel(applicationInfo) : "");
                            app.displayName = applicationNameTemp;
                        } else {
                            app.displayName = applicationName;
                        }
                        Drawable drawable = appInfo.loadIcon(packageManager);
                        Bitmap bitmap = PackageUtil.drawableToBitmap(drawable);
                        CoreApplication.getInstance().addBitmapToMemoryCache(packageName, bitmap);
                        apps.add(app);

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        private void LoadApplicationThroughLauncherApps() {
            UserManager manager = (UserManager) getSystemService(Context.USER_SERVICE);
            LauncherApps launcher = (LauncherApps) getSystemService(Context.LAUNCHER_APPS_SERVICE);

            for (UserHandle profile : manager.getUserProfiles()) {
                boolean isCurrentUser = android.os.Process.myUserHandle().equals(profile);

                for (LauncherActivityInfo activityInfo : launcher.getActivityList(null, profile)) {
                    ApplicationInfo appInfo = activityInfo.getApplicationInfo();
                    String packageName = appInfo.packageName;


                    String applicationName = appInfo.loadLabel(getPackageManager()).toString();
                    App app = new App();
                    app.packageName = packageName;
                    app.displayName = applicationName;

                    Drawable icon = activityInfo.getBadgedIcon(0);
                    if (!isCurrentUser) {
                        // we should only get associated profiles, which i think they should only be work profiles
                        // https://developer.android.com/reference/android/os/UserManager.html#getUserProfiles()
                        app.isWorkApp = true;
                        app.displayName += " Work";
                        mWorkAppsBitmaps.put(appInfo.packageName, icon);

                    }
                    else {
                        mMemoryCache.put(appInfo.packageName, icon);
                    }


                    apps.add(app);

                }

            }
        }


        @Override
        protected void onPostExecute(Object object) {
            super.onPostExecute(object);
            updateBlockedList();
            EventBus.getDefault().post(new AppInstalledEvent(true));
        }
    }
}
