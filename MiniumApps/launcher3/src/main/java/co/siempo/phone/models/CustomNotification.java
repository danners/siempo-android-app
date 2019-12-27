package co.siempo.phone.models;

import java.util.ArrayList;
import java.util.Date;

import co.siempo.phone.db.TableNotificationSms;

public class CustomNotification {
    private Date date;
    private String packagename;
    private ArrayList<TableNotificationSms> notificationSms;

    public CustomNotification() {
    }

    public String getPackagename() {
        return packagename;
    }

    public void setPackagename(String packagename) {
        this.packagename = packagename;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public ArrayList<TableNotificationSms> getNotificationSms() {
        return notificationSms;
    }

    public void setNotificationSms(ArrayList<TableNotificationSms> notificationSms) {
        this.notificationSms = notificationSms;
    }
}
