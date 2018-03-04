package ru.iorcontrol.ior.ior;

/**
 * Created by alexeykazinets on 28/11/2017.
 */

public class Settings {

    private static Settings mInstance = null;

    private String mString;
    private String APIhost = "http://188.225.47.101";
//    private String APIhost = "http://192.168.1.8:3000";
    private String type;
    private String _id;
    private String order;
    private String push;

    private Settings(){
        mString = "Hello";
    }

    public static Settings getInstance(){
        if(mInstance == null)
        {
            mInstance = new Settings();
        }
        return mInstance;
    }

    public String getAPIHost(){
        return this.APIhost;
    }
    public String getType() { return this.type; }
    public String getID() { return this._id; }
    public String getPUSH() { return this.push; }
    public String getOrderName() { return this.order; }
    public void setID(String _id) { this._id = _id; }
    public void setType(String type) {this.type = type; }
    public void setOrderName(String name) { this.order = name; }
    public void setPush(String push) { this.push = push; }



}
