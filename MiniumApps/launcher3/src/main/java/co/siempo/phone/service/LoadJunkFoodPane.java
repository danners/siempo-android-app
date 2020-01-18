package co.siempo.phone.service;

import android.os.AsyncTask;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;

import co.siempo.phone.app.App;
import co.siempo.phone.app.CoreApplication;
import co.siempo.phone.event.NotifyJunkFoodView;
import co.siempo.phone.utils.PrefSiempo;
import co.siempo.phone.utils.Sorting;
import co.siempo.phone.utils.UIUtils;

/**
 * Created by rajeshjadi on 14/3/18.
 */

public class LoadJunkFoodPane extends AsyncTask<String, String, ArrayList<App>> {

    private PrefSiempo prefSiempo;

    public LoadJunkFoodPane(PrefSiempo prefSiempo) {
        this.prefSiempo = prefSiempo;
    }

    @Override
    protected ArrayList<App> doInBackground(String... strings) {
        LinkedList<App> junkFoodList = prefSiempo.readAppList(PrefSiempo.JUNKFOOD_APPS);
        ArrayList<App> items = new ArrayList<>();
        ArrayList<App> itemsToRemove = new ArrayList<>();
        for (App junkApp : junkFoodList) {
            if (UIUtils.isAppInstalledAndEnabled(junkApp.packageName)) {
                items.add(junkApp);
            } else {
                itemsToRemove.add(junkApp);
            }
        }

        junkFoodList.removeAll(itemsToRemove);
        prefSiempo.writeAppList(PrefSiempo.JUNKFOOD_APPS, junkFoodList);

        if (junkFoodList.size() > 0) {
            if (prefSiempo.read(PrefSiempo.IS_RANDOMIZE_JUNKFOOD, true)) {
                Collections.shuffle(items);
            } else {
                items = Sorting.sortJunkAppAssignment(items);
            }
        }
        return items;
    }

    @Override
    protected void onPostExecute(ArrayList<App> s) {
        super.onPostExecute(s);
        CoreApplication.getInstance().setJunkFoodList(s);
        EventBus.getDefault().postSticky(new NotifyJunkFoodView(true));
    }
}
