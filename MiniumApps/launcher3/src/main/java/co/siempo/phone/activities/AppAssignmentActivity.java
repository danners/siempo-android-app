package co.siempo.phone.activities;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import co.siempo.phone.R;
import co.siempo.phone.adapters.viewholder.AppAssignmentAdapter;
import co.siempo.phone.app.App;
import co.siempo.phone.app.Constants;
import co.siempo.phone.app.CoreApplication;
import co.siempo.phone.event.AppInstalledEvent;
import co.siempo.phone.event.NotifySearchRefresh;
import co.siempo.phone.models.CategoryAppList;
import co.siempo.phone.models.MainListItem;
import co.siempo.phone.utils.NetworkUtil;
import co.siempo.phone.utils.PrefSiempo;
import co.siempo.phone.utils.Sorting;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class AppAssignmentActivity extends CoreActivity {

    public List<App> appList = new ArrayList<>();
    MainListItem mainListItem;
    MenuItem item_tools;
    //8 Photos
    List<Integer> idList = Arrays.asList(2, 4, 6, 9, 10, 12, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44);
    LinkedList<App> set = new LinkedList<>();
    List<App> appListAll = new ArrayList<>();
    ArrayList<App> mimeList = new ArrayList<>();
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private TextView txtErrorMessage;
    private AppAssignmentAdapter appAssignmentAdapter;
    private TextView showallAppBtn;
    private ImageView imgClear;
    private EditText edtSearch;
    private String class_name;
    private Context context;
    List<String> googleApps= Arrays.asList("com.google.android.gm","com.google.android.googlequicksearchbox","com.android.chrome","com.google.android.apps.photos","com.google.android.apps.googleassistant","com.google.android.calendar","com.google.android.apps.docs.editors.docs","com.google.android.apps.docs.editors.sheets","com.google.android.apps.docs.editors.slides","com.google.android.apps.docs","com.google.android.apps.tachyon","com.google.earth","com.google.android.apps.fitness","com.google.android.apps.chromecast.app","com.google.android.keep","com.google.android.apps.maps","com.google.android.apps.nbu.paisa.user","com.google.android.music","com.google.android.apps.podcasts");

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.app_assign, menu);
        item_tools = menu.findItem(R.id.item_tools);
        if (item_tools != null && mainListItem != null) {
            item_tools.setIcon(mainListItem.getDrawable());
            item_tools.setTitle(mainListItem.getTitle());
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Subscribe
    public void appInstalledEvent(AppInstalledEvent event) {
        if (event.isAppInstalledSuccessfully()) {
            filterList();
            initView();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_assignement);
        mainListItem = (MainListItem) getIntent().getSerializableExtra(Constants.INTENT_MAINLISTITEM);
        class_name = getIntent().getStringExtra("class_name");
        if (mainListItem != null) {
            set = PrefSiempo.getInstance(this).readAppList(PrefSiempo.JUNKFOOD_APPS);
        } else {
            finish();
        }
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Added to refresh if app is marked as non-junk by navigating to Flag
        // Junk Apps directly from this screen
        filterList();
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().postSticky(new NotifySearchRefresh(true));
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mainListItem = (MainListItem) savedInstanceState.getSerializable("MainListItem");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("MainListItem", mainListItem);

    }

    private void filterList() {
        appList = new ArrayList<>();

        if (mainListItem != null) {
            if(NetworkUtil.isOnline(context)){
                showListByCategory();
            }
            else{
                showListByMimeType();
            }
        } else {
            finish();
        }
    }

    public void showListByCategory(){
        boolean isCategoryAvailable=false;
        List<CategoryAppList> categoryAppList=CoreApplication.getInstance().categoryAppList;


        if(categoryAppList!=null && categoryAppList.size()>0) {
            Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
            mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            List<App> installedPackageList = CoreApplication.getInstance().getApplications();


            for (App app : installedPackageList) {
                if (app.packageName != null && !app.packageName.equalsIgnoreCase(getPackageName())) {

                    for (String googleAppPackages: googleApps) {
                        if ((mainListItem != null && app.packageName.equalsIgnoreCase(googleAppPackages.toLowerCase()))){
                            isCategoryAvailable = true;
                            appList.add(app);
                        }
                    }
                }
            }



            for (App app : installedPackageList) {

                if (app.packageName != null && !app.packageName.equalsIgnoreCase(getPackageName())) {

                    String appName="";
                    try{
                        appName =  app.displayName;
                    }catch (Exception e){

                    }
                    for (CategoryAppList category : categoryAppList) {

                        if(mainListItem!=null && mainListItem.getCategory()!=null && mainListItem.getCategory().equalsIgnoreCase("Travel & Local")  && app.packageName.equalsIgnoreCase("me.lyft.android")){
                            isCategoryAvailable = true;
                            appList.add(app);
                        }

                        if ((mainListItem != null && app.packageName.equalsIgnoreCase(category.getPackageName()) &&  mainListItem.getCategory().equalsIgnoreCase(category.getCategoryName())) || ( appName.contains(mainListItem.getTitle()) || mainListItem.getTitle().contains(appName) ) ) {
                            isCategoryAvailable = true;
                            appList.add(app);
                        }
                    }
                }
            }



            appList=removeDuplicates(appList);
            if(isCategoryAvailable){
                bindList(appList);
            }
            else{
                showListByMimeType();
            }
        }
        else{
            showListByMimeType();
        }
    }

    // Function to remove duplicates from an ArrayList
    public static <T> List<T> removeDuplicates(List<T> list)
    {

        // Create a new ArrayList
        List<T> newList = new ArrayList<T>();

        // Traverse through the first list
        for (T element : list) {

            // If this element is not present in newList
            // then add it
            if (!newList.contains(element)) {

                newList.add(element);
            }
        }

        // return the new list
        return newList;
    }

    private void AddMatchingApps(List<App> list, List<ResolveInfo> matchingApps) {
        for (ResolveInfo info: matchingApps) {
            for (App app: CoreApplication.getInstance().getApplications()) {
                if (info.activityInfo.packageName.equals(app.packageName)) {
                    list.add(app);
                }
            }
        }
    }

    public void showListByMimeType(){
        List<App> installedPackageList = new ArrayList<>();
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> installedLaunchers = getPackageManager().queryIntentActivities(mainIntent, 0);
        AddMatchingApps(installedPackageList, installedLaunchers);

        if (idList.contains(mainListItem.getId())) {
            mimeList = getMimeList();
            for (App app : installedPackageList) {
                if (!app.packageName.equalsIgnoreCase(getPackageName())) {
                    appList.add(app);
                }
            }
            if (showallAppBtn != null) {
                showallAppBtn.setVisibility(View.GONE);
            }
        } else {
            appList = CoreApplication.getInstance().getApplicationByCategory(mainListItem.getId());
        }

        appListAll = new ArrayList<>();
        for (App app : installedPackageList) {
            if (!app.packageName.equalsIgnoreCase(getPackageName())) {
                appListAll.add(app);
            }
        }
        appListAll = Sorting.sortAppAssignment(appListAll);
        appListAll=removeDuplicates(appListAll);
        appList=removeDuplicates(appList);

        if (showallAppBtn.getVisibility()!=View.VISIBLE) {
            bindList(appListAll);
        } else {
            bindList(appList);
        }
    }

    private ArrayList<App> getMimeList() {
        ArrayList<App> mimeListLocal = new ArrayList<>();
        for (int i = 1; i <= 20; i++) {
            mimeListLocal.addAll(CoreApplication.getInstance().getApplicationByCategory(i));
        }
        return mimeListLocal;
    }

    private void initView() {
        context = AppAssignmentActivity.this;
        toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_blue_24dp);
        if (mainListItem != null) {
            toolbar.setTitle(getString(R.string.assign_an_app) + " " + mainListItem.getTitle());
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        setSupportActionBar(toolbar);
        recyclerView = findViewById(R.id.recyclerView);
        txtErrorMessage = findViewById(R.id.txtErrorMessage);

        showallAppBtn = findViewById(R.id.txtViewAllapps);
        showallAppBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showallAppBtn.setVisibility(View.GONE);
                bindList(appListAll);
            }
        });

        //Added for searchbar
        imgClear = findViewById(R.id.imgClear);
        edtSearch = findViewById(R.id.edtSearch);
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (appAssignmentAdapter != null) {
                    appAssignmentAdapter.getFilter().filter(s.toString());
                }
                if (s.toString().length() > 0) {
                    imgClear.setVisibility(View.VISIBLE);
                } else {
                    imgClear.setVisibility(View.INVISIBLE);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        imgClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edtSearch.setText("");
            }
        });

        getAllInstallApps();
    }

    public void getAllInstallApps(){
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<App> installedPackageList = CoreApplication.getInstance().getApplications();
        appListAll = new ArrayList<>();
        for (App app : installedPackageList) {
            if (!app.packageName.equalsIgnoreCase(getPackageName())) {
                appListAll.add(app);
            }
        }
        appListAll = Sorting.sortAppAssignment(appListAll);

    }

    private void bindList(List<App> appList) {
        if (appList != null && appList.size() > 0) {
            recyclerView.setVisibility(View.VISIBLE);
            txtErrorMessage.setVisibility(View.INVISIBLE);
            appList = Sorting.sortAppAssignment(appList);
            LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.addItemDecoration(
                    new DividerItemDecoration(this, mLayoutManager.getOrientation()));
            if (mainListItem != null) {
                appAssignmentAdapter = new AppAssignmentAdapter(this, mainListItem.getId(),
                        appList, class_name);
                recyclerView.setAdapter(appAssignmentAdapter);
                appAssignmentAdapter.getFilter().filter(edtSearch.getText().toString().trim());
            }
        } else {
            recyclerView.setVisibility(View.INVISIBLE);
            txtErrorMessage.setVisibility(View.VISIBLE);
            if (mainListItem != null) {
                txtErrorMessage.setText("No " + mainListItem.getTitle() + " apps are installed.");
            }
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    public void hideOrShowMessage(boolean isShow) {
        if (isShow) {
            recyclerView.setVisibility(View.VISIBLE);
            txtErrorMessage.setVisibility(View.INVISIBLE);
        } else {
            recyclerView.setVisibility(View.INVISIBLE);
            txtErrorMessage.setVisibility(View.VISIBLE);
            txtErrorMessage.setText(R.string.no_mattched_text);
        }
    }
}
