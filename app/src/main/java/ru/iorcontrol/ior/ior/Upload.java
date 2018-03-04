package ru.iorcontrol.ior.ior;

import java.io.Serializable;

/**
 * Created by alexeykazinets on 29/11/2017.
 */

public class Upload implements Serializable {

    String _id;
    String url;

    public Upload(String _id, String url) {
        this._id = _id;
        this.url = url;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
