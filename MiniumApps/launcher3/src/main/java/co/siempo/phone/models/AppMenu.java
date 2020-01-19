package co.siempo.phone.models;

import co.siempo.phone.app.App;

/**
 * Created by rajeshjadi on 7/2/18.
 */

public class AppMenu {
    private boolean isVisible;
    private boolean isBottomDoc;
    private String applicationName;
    private App app;

    public AppMenu(boolean isVisible, boolean isBottomDoc, String applicationName) {
        this.isVisible = isVisible;
        this.isBottomDoc = isBottomDoc;
        this.applicationName = applicationName;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }

    public String getApplicationName() {
        if (app != null) {
            return app.packageName;
        }

        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        app = null;
        this.applicationName = applicationName;
    }

    public void setApplication(App app) {
        this.app = app;
    }

    public boolean isBottomDoc() {
        return isBottomDoc;
    }

    public void setBottomDoc(boolean bottomDoc) {
        isBottomDoc = bottomDoc;
    }

    public App getApp() {
        return app;
    }
}
