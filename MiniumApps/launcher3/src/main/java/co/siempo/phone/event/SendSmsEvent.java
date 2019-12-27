package co.siempo.phone.event;

/**
 * Created by rajeshjadi on 19/8/17.
 */

public class SendSmsEvent {
    private boolean isClearList = false;

    public SendSmsEvent(boolean isClearList) {
        this.isClearList = isClearList;
    }

    public SendSmsEvent() {

    }

    public boolean isClearList() {
        return isClearList;
    }

}
