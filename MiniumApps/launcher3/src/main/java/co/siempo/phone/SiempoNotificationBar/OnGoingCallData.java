package co.siempo.phone.SiempoNotificationBar;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * Created by hardik on 26/10/17.
 */
public class OnGoingCallData {

    private int id;
    private String _contact_title;
    private String _message;
    private java.util.Date _date;
    private Integer _contact_id;
    private Integer _sms_id;
    private Long _snooze_time;
    private Boolean _is_read;
    private Integer notification_type;
    private Boolean _isCallRunning;


    public OnGoingCallData() {
    }

    public OnGoingCallData(int id) {
        this.id = id;
    }

    public OnGoingCallData(int id, String _contact_title, String _message, java.util.Date _date, Integer _contact_id, Integer _sms_id, Long _snooze_time, Boolean _is_read,
                                Integer notification_type, Boolean _isCallRunning) {
        this.id = id;
        this._contact_title = _contact_title;
        this._message = _message;
        this._date = _date;
        this._contact_id = _contact_id;
        this._sms_id = _sms_id;
        this._snooze_time = _snooze_time;
        this._is_read = _is_read;
        this.notification_type = notification_type;
        this._isCallRunning = _isCallRunning;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String get_contact_title() {
        return _contact_title;
    }

    public void set_contact_title(String _contact_title) {
        this._contact_title = _contact_title;
    }

    public String get_message() {
        return _message;
    }

    public void set_message(String _message) {
        this._message = _message;
    }

    public java.util.Date get_date() {
        return _date;
    }

    public void set_date(java.util.Date _date) {
        this._date = _date;
    }

    public Integer get_contact_id() {
        return _contact_id;
    }

    public void set_contact_id(Integer _contact_id) {
        this._contact_id = _contact_id;
    }

    public Integer get_sms_id() {
        return _sms_id;
    }

    public void set_sms_id(Integer _sms_id) {
        this._sms_id = _sms_id;
    }

    public Long get_snooze_time() {
        return _snooze_time;
    }

    public void set_snooze_time(Long _snooze_time) {
        this._snooze_time = _snooze_time;
    }

    public Boolean get_is_read() {
        return _is_read;
    }

    public void set_is_read(Boolean _is_read) {
        this._is_read = _is_read;
    }



    public Integer getNotification_type() {
        return this.notification_type;
    }

    public void setNotification_type(Integer notification_type) {
        this.notification_type = notification_type;
    }

    public Boolean get_isCallRunning() {
        return this._isCallRunning;
    }

    public void set_isCallRunning(Boolean _isCallRunning) {
        this._isCallRunning = _isCallRunning;
    }

}