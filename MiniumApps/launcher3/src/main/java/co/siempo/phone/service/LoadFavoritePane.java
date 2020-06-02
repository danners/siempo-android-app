package co.siempo.phone.service;

import android.os.AsyncTask;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import co.siempo.phone.app.CoreApplication;
import co.siempo.phone.event.NotifyFavoriteView;
import co.siempo.phone.models.MainListItem;
import co.siempo.phone.utils.PackageUtil;
import co.siempo.phone.utils.PrefSiempo;

/**
 * Created by rajeshjadi on 14/3/18.
 */

public class LoadFavoritePane extends AsyncTask<String, String, ArrayList<MainListItem>> {

    PrefSiempo prefSiempo;

    public LoadFavoritePane(PrefSiempo prefSimepo) {
        this.prefSiempo = prefSimepo;
    }

    @Override
    protected ArrayList<MainListItem> doInBackground(String... strings) {
        ArrayList<MainListItem> items = PackageUtil.getFavoriteList();
        return items;
    }

    @Override
    protected void onPostExecute(ArrayList<MainListItem> s) {
        super.onPostExecute(s);
        CoreApplication.getInstance().setFavoriteItemsList(s);
        EventBus.getDefault().postSticky(new NotifyFavoriteView(true));
    }
}
