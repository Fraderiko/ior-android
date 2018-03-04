package ru.iorcontrol.ior.ior.Model;

import java.util.List;

import ru.iorcontrol.ior.ior.StatusTemplate;

/**
 * Created by alexeykazinets on 30/11/2017.
 */

public class OrderTemplate {

    public String _id;
    public String name;
    public List<StatusTemplate> statuses;

    public OrderTemplate(String _id, String name, List<StatusTemplate> statuses) {
        this._id = _id;
        this.name = name;
        this.statuses = statuses;
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

    public List<StatusTemplate> getStatuses() {
        return statuses;
    }

    public void setStatuses(List<StatusTemplate> statuses) {
        this.statuses = statuses;
    }
}

