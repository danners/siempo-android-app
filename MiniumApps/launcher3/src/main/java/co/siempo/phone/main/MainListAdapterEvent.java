package co.siempo.phone.main;

import java.util.List;

import co.siempo.phone.models.MainListItem;

/**
 * Created by Shahab on 2/17/2017.
 */

public class MainListAdapterEvent {

    private List<MainListItem> filteredData;

    public MainListAdapterEvent(List<MainListItem> filteredData) {
        this.filteredData = filteredData;
    }

    public List<MainListItem> getData() {
        return filteredData;
    }
}
