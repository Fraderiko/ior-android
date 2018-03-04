package ru.iorcontrol.ior.ior.Model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by alexeykazinets on 25/01/2018.
 */

public class ChatMessage implements Serializable {

    @SerializedName("date")
    private double date;

    @SerializedName("type")
    private String type;

    @SerializedName("value")
    private String value;

    @SerializedName("order")
    private String order;

    @SerializedName("username")
    private String username;

    public ChatMessage(double date, String type, String value, String order, String username) {
        this.date = date;
        this.type = type;
        this.value = value;
        this.order = order;
        this.username = username;
    }

    public void setDate(double date){
        this.date = date;
    }

    public double getDate(){
        return date;
    }

    public void setType(String type){
        this.type = type;
    }

    public String getType(){
        return type;
    }

    public void setValue(String value){
        this.value = value;
    }

    public String getValue(){
        return value;
    }

    public void setOrder(String order){
        this.order = order;
    }

    public String getOrder(){
        return order;
    }

    public void setUsername(String username){
        this.username = username;
    }

    public String getUsername(){
        return username;
    }

    @Override
    public String toString(){
        return
                "Response{" +
                        "date = '" + date + '\'' +
                        ",type = '" + type + '\'' +
                        ",value = '" + value + '\'' +
                        ",order = '" + order + '\'' +
                        ",username = '" + username + '\'' +
                        "}";
    }
}
