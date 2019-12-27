package co.siempo.phone.event;

/**
 * Created by rajeshjadi on 14/3/18.
 */

public class NotifyFavoriteView {
    boolean isNotify;

    public NotifyFavoriteView(boolean isNotify) {
        this.isNotify = isNotify;
    }

    public boolean isNotify() {
        return isNotify;
    }
}
