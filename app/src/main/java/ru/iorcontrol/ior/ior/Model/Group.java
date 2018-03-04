package ru.iorcontrol.ior.ior.Model;

import java.util.List;

/**
 * Created by alexeykazinets on 30/11/2017.
 */

public class Group {

    public String _id;
    public String name;
    public List<User> canworkwith;
    public List<OrderTemplate> orders;
    public List<Egroup> canworkwithgroups;

    public Group(String _id, String name, List<User> canworkwith, List<OrderTemplate> orders) {
        this._id = _id;
        this.name = name;
        this.canworkwith = canworkwith;
        this.orders = orders;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<User> getCanworkwith() {
        return canworkwith;
    }

    public void setCanworkwith(List<User> canworkwith) {
        this.canworkwith = canworkwith;
    }

    public List<OrderTemplate> getOrders() {
        return orders;
    }

    public void setOrders(List<OrderTemplate> orders) {
        this.orders = orders;
    }
}
