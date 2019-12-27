package co.siempo.phone.event;

/**
 * Created by rajeshjadi on 14/3/18.
 */

public class HomePress {

    private int currentIndexPaneFragment;

    public HomePress(int currentIndexPaneFragment) {
        this.currentIndexPaneFragment = currentIndexPaneFragment;
    }

    public int getCurrentIndexPaneFragment() {
        return currentIndexPaneFragment;
    }
}
