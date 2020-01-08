package co.siempo.phone.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;

import co.siempo.phone.R;
import co.siempo.phone.adapters.FavoritePositioningAdapter;
import co.siempo.phone.app.App;
import co.siempo.phone.app.CoreApplication;
import co.siempo.phone.customviews.ItemOffsetDecoration;
import co.siempo.phone.interfaces.OnFavoriteItemListChangedListener;
import co.siempo.phone.main.OnStartDragListener;
import co.siempo.phone.main.SimpleItemTouchHelperCallback;
import co.siempo.phone.models.MainListItem;
import co.siempo.phone.service.LoadFavoritePane;
import co.siempo.phone.util.AppUtils;
import co.siempo.phone.utils.PackageUtil;
import co.siempo.phone.utils.PrefSiempo;

public class FavoriteAppsPositionActivity extends CoreActivity implements OnFavoriteItemListChangedListener,
        OnStartDragListener {
    private ArrayList<MainListItem> items = new ArrayList<>();
    private FavoritePositioningAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ItemOffsetDecoration itemDecoration;
    private ItemTouchHelper mItemTouchHelper;
    private Parcelable mListState;
    private RecyclerView recyclerView;
    private Toolbar toolbar;
    private TextView txtSelectTools;
    private RelativeLayout relTop;
    private RelativeLayout relPane;

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            finish();
        }
    };
    private LinearLayout linMain;
    private ImageView imgBackground;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_apps_positioning);
        String filePath = PrefSiempo.getInstance(this).read(PrefSiempo
                .DEFAULT_BAG, "");
        linMain = findViewById(R.id.linMain);
        imgBackground = findViewById(R.id.imgBackground);

        try {
            if (!TextUtils.isEmpty(filePath)) {

                Glide.with(this)
                        .load(Uri.fromFile(new File(filePath))) // Uri of the
                        // picture
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(imgBackground);
                linMain.setBackgroundColor(ContextCompat.getColor(this, R.color
                        .trans_black_bg));

                linMain.setBackgroundColor(ContextCompat.getColor(this, R.color.trans_black_bg));

            } else {

                imgBackground.setImageBitmap(null);
                imgBackground.setBackground(null);
                linMain.setBackgroundColor(ContextCompat.getColor(this, R.color
                        .transparent));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        AppUtils.notificationBarManaged(this, null);
        AppUtils.statusBarManaged(this);
        AppUtils.statusbarColor0(this, 1);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.app_junkfood_flagging, menu);
        MenuItem menuItem = menu.findItem(R.id.item_save);
        menuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                finish();
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initView();
    }

    @Override
    protected void onPause() {
        super.onPause();
        new LoadFavoritePane(PrefSiempo.getInstance(this)).execute();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void initView() {

        toolbar = findViewById(R.id.toolbar);
        relTop = findViewById(R.id.relTop);
        relPane = findViewById(R.id.relPane);
        toolbar.setTitle(R.string.editing_frequently_apps);
        setSupportActionBar(toolbar);
        items = PackageUtil.getFavoriteList(this);

        recyclerView = findViewById(R.id.recyclerView);
        txtSelectTools = findViewById(R.id.txtSelectTools);
        recyclerView.setHasFixedSize(true);
        mLayoutManager = new GridLayoutManager(this, 4);
        recyclerView.setLayoutManager(mLayoutManager);
        if (itemDecoration != null) {
            recyclerView.removeItemDecoration(itemDecoration);
        }
        itemDecoration = new ItemOffsetDecoration(this, R.dimen.dp_10);
        recyclerView.addItemDecoration(itemDecoration);


        mAdapter = new FavoritePositioningAdapter(this, CoreApplication.getInstance().isHideIconBranding(), items, this, this);
        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(mAdapter, this);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(recyclerView);
        recyclerView.setAdapter(mAdapter);
        txtSelectTools.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FavoriteAppsPositionActivity.this, FavoritesSelectionActivity.class);
                startActivity(intent);
                FavoriteAppsPositionActivity.this
                        .overridePendingTransition(R.anim
                                        .fade_in,
                                R.anim.fade_out);
            }
        });


        relTop.setOnClickListener(onClickListener);

        toolbar.setOnClickListener(onClickListener);

        relPane.setOnClickListener(onClickListener);

    }


    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }

    @Override
    public void onFavoriteItemListChanged(ArrayList<MainListItem> customers) {
        LinkedList<App> listOfSortedCustomerId = new LinkedList<>();

        for (MainListItem customer : customers) {
            listOfSortedCustomerId.add(customer.app);
        }
        PrefSiempo.getInstance(this).writeAppList(PrefSiempo.FAVORITE_SORTED_MENU, listOfSortedCustomerId);
    }


}
