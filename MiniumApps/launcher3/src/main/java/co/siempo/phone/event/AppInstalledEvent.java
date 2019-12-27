package co.siempo.phone.event;


public class AppInstalledEvent {

    private boolean isAppInstalled;

    public AppInstalledEvent(boolean isAppInstalled) {
        this.isAppInstalled = isAppInstalled;
    }

    public boolean isAppInstalledSuccessfully() {
        return isAppInstalled;
    }

}
