package co.siempo.phone.models;

/**
 * Created by hardik on 23/11/17.
 * Model Class for the app packages
 * whose notifications have been blocked
 */
public class AppListInfo {
    /**
     * It will hold information about the installed application information
     */
    public String packageName;
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
    public boolean isWorkApp = false;

    public String applicationName;

    public AppListInfo(String packageName, String applicationName, boolean isShowHeader, boolean isShowTitle, boolean isFlagApp, boolean isWorkApp) {
        this.packageName = packageName;
        this.applicationName = applicationName;
        this.isShowHeader = isShowHeader;
        this.isShowTitle = isShowTitle;
        this.isFlagApp = isFlagApp;
        this.isWorkApp = isWorkApp;
    }

    public AppListInfo() {
    }

    @Override
    public String toString() {
        return "packageName : " + packageName + " applicationName " + applicationName + " isShowHeader" + isShowHeader + " "
                + "isShowTitle " + isShowTitle + " isFlagApp " + isFlagApp;
    }
}
