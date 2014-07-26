package com.example.pablo.app2;

/**
 * Created by pablo on 25/07/14.
 */

public class Model{

    private int icon;
    private String user;
    private String hour;
    private String message;

    public Model(int icon, String title, String hour, String message) {
        super();
        this.icon = icon;
        this.user = title;
        this.hour = hour;
        this.message = message;

    }

    public int getIcon() {
        return icon;
    }
    public String getUser() {
        return user;
    }
    public String getHour() {
        return hour;
    }
    public String getMessage(){
        return message;
    }

//gettters & setters...
}
