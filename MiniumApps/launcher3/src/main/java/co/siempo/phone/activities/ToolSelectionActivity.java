package co.siempo.phone.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;

import co.siempo.phone.R;
import co.siempo.phone.adapters.ToolsListAdapter;
import co.siempo.phone.app.CoreApplication;
import co.siempo.phone.event.NotifyBottomView;
import co.siempo.phone.event.NotifySearchRefresh;
import co.siempo.phone.event.NotifyToolView;
import co.siempo.phone.main.MainListItemLoader;
import co.siempo.phone.models.AppMenu;
import co.siempo.phone.models.MainListItem;
import co.siempo.phone.service.LoadToolPane;
import co.siempo.phone.utils.PrefSiempo;
import co.siempo.phone.utils.Sorting;
import de.greenrobot.event.EventBus;

public class ToolSelectionActivity extends CoreActivity {

    public static final int TOOL_SELECTION = 100;
    private HashMap<Integer, AppMenu> map;
    private Toolbar toolbar;
    private ArrayList<MainListItem> items = new ArrayList<>();
    private LinearLayoutManager mLayoutManager;
    private RecyclerView recyclerView;
    private ToolsListAdapter mAdapter;
    private ArrayList<MainListItem> adapterList;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.app_assignment_list, menu);
        MenuItem menuItem = menu.findItem(R.id.item_save);
        menuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (mAdapter != null) {
                    for (MainListItem mainListItem : adapterList) {
                        map.get(mainListItem.getId()).setVisible(mainListItem
                                .isVisable());
                    }

                    PrefSiempo.getInstance(ToolSelectionActivity.this).write(PrefSiempo.TOOLS_SETTING, new Gson().toJson(map));
                    EventBus.getDefault().postSticky(new NotifyBottomView(true));
                    EventBus.getDefault().postSticky(new NotifyToolView(true));
                    finish();
                }
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tool_selection);
        map = CoreApplication.getInstance().getToolsSettings();

        initView();
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAdapter != null) {
            PrefSiempo.getInstance(ToolSelectionActivity.this).write(PrefSiempo.TOOLS_SETTING, new Gson().toJson(mAdapter.getMap()));
            EventBus.getDefault().postSticky(new NotifyBottomView(true));
            EventBus.getDefault().postSticky(new NotifyToolView(true));
        }
        new LoadToolPane().execute();
        EventBus.getDefault().postSticky(new NotifySearchRefresh(true));
    }

    private void initView() {
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.select_tools);
        setSupportActionBar(toolbar);
        recyclerView = findViewById(R.id.recyclerView);
        filterListData();
        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new ToolsListAdapter(this, adapterList, map);
        recyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    private void filterListData() {
        //Copy List
        items = new ArrayList<>();
        new MainListItemLoader().loadItemsDefaultApp(items);
        for (int i = 0; i < items.size(); i++) {
            items.get(i).setVisable(map.get(items.get(i).getId()).isVisible());
        }

        //original list which will be edited
        adapterList = new ArrayList<>();
        new MainListItemLoader().loadItemsDefaultApp(adapterList);
        int size = adapterList.size();
        for (int i = 0; i < size; i++) {
            adapterList.get(i).setVisable(map.get(adapterList.get(i).getId()).isVisible());
        }
        adapterList = Sorting.sortToolAppAssignment(this, adapterList);
    }

    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TOOL_SELECTION) {
            if (resultCode == RESULT_OK) {
                mAdapter.refreshEvents(adapterList);
            } else if (resultCode == RESULT_CANCELED) {
                mAdapter.changeClickable(true);
                mAdapter.notifyDataSetChanged();
            }
        }
    }


    @Override
    public void onBackPressed() {
        map = mAdapter.getMap();
        for (MainListItem mainListItem : items) {
            map.get(mainListItem.getId()).setVisible(mainListItem
                    .isVisable());
        }
        PrefSiempo.getInstance(ToolSelectionActivity.this).write(PrefSiempo.TOOLS_SETTING, new Gson().toJson(map));
        super.onBackPressed();


    }
}
