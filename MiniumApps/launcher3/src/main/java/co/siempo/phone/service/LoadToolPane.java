package co.siempo.phone.service;

import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import co.siempo.phone.app.CoreApplication;
import co.siempo.phone.event.NotifyBottomView;
import co.siempo.phone.event.NotifyToolView;
import co.siempo.phone.main.MainListItemLoader;
import co.siempo.phone.models.AppMenu;
import co.siempo.phone.models.MainListItem;
import co.siempo.phone.utils.PackageUtil;
import org.greenrobot.eventbus.EventBus;

/**
 * Created by rajeshjadi on 14/3/18.
 */

public class LoadToolPane extends AsyncTask<String, String, ArrayList<MainListItem>> {

    ArrayList<MainListItem> bottomDockList;

    public LoadToolPane() {
        bottomDockList = new ArrayList<>();
    }

    @Override
    protected ArrayList<MainListItem> doInBackground(String... strings) {
        ArrayList<MainListItem> items = new ArrayList<>();
        ArrayList<MainListItem> items1 = new ArrayList<>();

        try {
            new MainListItemLoader().loadItemsDefaultApp(items);
            items = PackageUtil.getToolsMenuData(items);
            Set<Integer> list = new HashSet<>();

            if (null != CoreApplication.getInstance() && null != CoreApplication
                    .getInstance().getToolsSettings()) {
                for (Map.Entry<Integer, AppMenu> entry : CoreApplication.getInstance().getToolsSettings().entrySet()) {
                    if (entry.getValue().isBottomDoc()) {
                        list.add(entry.getKey());
                    }
                }
            }

            for (MainListItem mainListItem : items) {
                if (list.contains(mainListItem.getId())) {
                    bottomDockList.add(mainListItem);
                } else {
                        items1.add(mainListItem);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        return items1;
    }

    @Override
    protected void onPostExecute(ArrayList<MainListItem> s) {
        super.onPostExecute(s);


        try {
            CoreApplication.getInstance().setToolItemsList(s);
            CoreApplication.getInstance().setToolBottomItemsList(bottomDockList);
            EventBus.getDefault().postSticky(new NotifyBottomView(true));
            EventBus.getDefault().postSticky(new NotifyToolView(true));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
