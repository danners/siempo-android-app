package co.siempo.phone.models;

/**
 * Created by itc on 20/02/17.
 */

public class Notification {
    private Long id;
    private String _text;
    private String _time;
    private NotificationContactModel notificationContactModel;
    private int notificationType;
    private String number;

    private byte[] user_icon;
    private String packageName;
    private String strTitle;

    public Notification() {
    }

    public Notification(NotificationContactModel notificationContactModel, Long id, String number, String _text, String _time, int notificationType, String packageName) {
        this.notificationContactModel = notificationContactModel;
        this.id = id;
        this._text = _text;
        this._time = _time;
        this.notificationType = notificationType;
        this.number = number;
        this.packageName = packageName;
    }

    public Notification(int notificationType, String packageName, String dateTime, String strTitle, String message) {
        this.notificationType = notificationType;
        this.packageName = packageName;
        this._time = dateTime;
        this.strTitle = strTitle;
        this._text = message;
    }

    public String get_text() {
        return _text;
    }

    public void set_text(String _text) {
        this._text = _text;
    }

    public String get_time() {
        return _time;
    }

    public void set_time(String _time) {
        this._time = _time;
    }

    public NotificationContactModel getNotificationContactModel() {
        return notificationContactModel;
    }

    public int getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(int notificationType) {
        this.notificationType = notificationType;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public byte[] getUser_icon() {
        return user_icon;
    }

    public void setUser_icon(byte[] user_icon) {
        this.user_icon = user_icon;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getStrTitle() {
        return strTitle;
    }

    public void setStrTitle(String strTitle) {
        this.strTitle = strTitle;
    }

}
