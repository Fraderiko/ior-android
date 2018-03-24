package ru.iorcontrol.ior.ior.Model;

import java.util.List;

/**
 * Created by alexeykazinets on 30/11/2017.
 */

public class User {

    public String _id;
    public String type;
    public String name;
    public String mail;
    public String phone;
    public String password;
    public Boolean new_orders_notification;
    public Boolean new_status_notification;
    public Boolean new_orders_push_notification;
    public Boolean new_status_push_notification;
    public boolean new_chat_notification;
    public Boolean permission_to_edit_orders;
    public Boolean permission_to_cancel_orders;
    public List<String> favorites;

    public User(String _id, String type, String name, String mail, String phone, String password, Boolean new_orders_notification, Boolean new_status_notification, Boolean new_orders_push_notification, Boolean new_status_push_notification, Boolean permission_to_cancel_orders, Boolean permission_to_edit_orders, List<String> favorites, Boolean new_chat_notification) {
        this._id = _id;
        this.type = type;
        this.name = name;
        this.mail = mail;
        this.phone = phone;
        this.password = password;
        this.new_orders_notification = new_orders_notification;
        this.new_status_notification = new_status_notification;
        this.new_orders_push_notification = new_orders_push_notification;
        this.new_status_push_notification = new_status_push_notification;
        this.permission_to_cancel_orders = permission_to_cancel_orders;
        this.favorites = favorites;
        this.permission_to_edit_orders = permission_to_edit_orders;
        this.new_chat_notification = new_chat_notification;
    }

    public Boolean getPermission_to_edit_orders() {
        return permission_to_edit_orders;
    }

    public void setPermission_to_edit_orders(Boolean permission_to_edit_orders) {
        this.permission_to_edit_orders = permission_to_edit_orders;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getNew_orders_notification() {
        return new_orders_notification;
    }

    public void setNew_orders_notification(Boolean new_orders_notification) {
        this.new_orders_notification = new_orders_notification;
    }

    public Boolean getNew_status_notification() {
        return new_status_notification;
    }

    public void setNew_status_notification(Boolean new_status_notification) {
        this.new_status_notification = new_status_notification;
    }

    public Boolean getNew_orders_push_notification() {
        return new_orders_push_notification;
    }

    public void setNew_orders_push_notification(Boolean new_orders_push_notification) {
        this.new_orders_push_notification = new_orders_push_notification;
    }

    public Boolean getNew_status_push_notification() {
        return new_status_push_notification;
    }

    public void setNew_status_push_notification(Boolean new_status_push_notification) {
        this.new_status_push_notification = new_status_push_notification;
    }

    public Boolean getPermission_to_cancel_orders() {
        return permission_to_cancel_orders;
    }

    public void setPermission_to_cancel_orders(Boolean permission_to_cancel_orders) {
        this.permission_to_cancel_orders = permission_to_cancel_orders;
    }

    public List<String> getFavorites() {
        return favorites;
    }

    public void setFavorites(List<String> favorites) {
        this.favorites = favorites;
    }
}
