package co.siempo.phone.models;

public class AlarmData {
    private int hours;
    private int minute;
    private String index;

    public AlarmData(int hours, int minute, String index) {
        this.hours = hours;
        this.minute = minute;
        this.index = index;
    }

    public int getHours() {
        return hours;
    }



    public int getMinute() {
        return minute;
    }



    public String getIndex() {
        return index;
    }


}