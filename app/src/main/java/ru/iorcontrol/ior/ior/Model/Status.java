package ru.iorcontrol.ior.ior.Model;

import java.io.Serializable;
import java.util.List;

import ru.iorcontrol.ior.ior.Model.Field;

/**
 * Created by me on 24/11/2017.
 */

public class Status implements Serializable {

    public String name;
    public String _id;
    public List<Field> fields;
    public String state;
    public Boolean isFinal;
    public List<String> groups_permission_to_edit;
    public List<String> users_permission_to_edit;

    public Status(String name, String _id, List<Field> fields, String state, Boolean isFinal) {
        this.name = name;
        this._id = _id;
        this.fields = fields;
        this.state = state;
        this.isFinal = isFinal;
    }

    public String getName() {
        return name;
    }

    public String get_id() {
        return _id;
    }

    public List<Field> getFields() {
        return fields;
    }

    public String getState() {
        return state;
    }

    public Boolean getFinal() {
        return isFinal;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public void setFields(List<Field> fields) {
        this.fields = fields;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setFinal(Boolean aFinal) {
        isFinal = aFinal;
    }
}
