package ru.iorcontrol.ior.ior.Model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by me on 24/11/2017.
 */

public class Field implements Serializable {

    public String name;
    public String type;
    public String value;
    public Boolean required;
    public Boolean recepientvisible;
    public String _id;
    public List<String> media;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Boolean getRequired() {
        return required;
    }

    public void setRequired(Boolean required) {
        this.required = required;
    }

    public Boolean getRecepientvisible() {
        return recepientvisible;
    }

    public void setRecepientvisible(Boolean recepientvisible) {
        this.recepientvisible = recepientvisible;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public List<String> getMedia() {
        return media;
    }

    public void setMedia(List<String> media) {
        this.media = media;
    }
}
