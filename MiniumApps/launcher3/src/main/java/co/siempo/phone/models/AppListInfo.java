package co.siempo.phone.models;

import co.siempo.phone.app.App;

/**
 * Created by hardik on 23/11/17.
 * Model Class for the app packages
 * whose notifications have been blocked
 */
public class AppListInfo {

    public App app;

    /**
     * to Check if notification is allowed or not
     * -True: app notifications are allowed
     * -False: app notifications are blocked
     */
    public boolean ischecked;

    public String errorMessage = "";

    public String headerName = "";

    public boolean isShowHeader = false;
    public boolean isShowTitle = false;
    public boolean isFlagApp = false;

    public AppListInfo(String packageName, String applicationName, boolean isShowHeader, boolean isShowTitle, boolean isFlagApp, boolean isWorkApp) {
        this.app = new App();
        this.app.packageName = packageName;
        this.app.isWorkApp = isWorkApp;
        this.app.displayName = applicationName;

        this.isShowHeader = isShowHeader;
        this.isShowTitle = isShowTitle;
        this.isFlagApp = isFlagApp;
    }

    public AppListInfo() {
    }

    @Override
    public String toString() {
        return "packageName : " + app.packageName + " applicationName " + app.displayName + " isShowHeader" + isShowHeader + " "
                + "isShowTitle " + isShowTitle + " isFlagApp " + isFlagApp;
    }
}
