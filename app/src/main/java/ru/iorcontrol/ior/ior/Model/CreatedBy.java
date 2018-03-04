package ru.iorcontrol.ior.ior.Model;

import java.io.Serializable;

/**
 * Created by me on 24/11/2017.
 */

public class CreatedBy implements Serializable {

    public String name;
    public String _id;

    public CreatedBy(String name, String _id) {
        this.name = name;
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public String get_id() {
        return _id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void set_id(String _id) {
        this._id = _id;
    }
}
