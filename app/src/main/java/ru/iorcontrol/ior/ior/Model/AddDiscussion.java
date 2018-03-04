package ru.iorcontrol.ior.ior.Model;

import ru.iorcontrol.ior.ior.Model.Discussion;

/**
 * Created by alexeykazinets on 01/12/2017.
 */

public class AddDiscussion {

    public String _id;
    public Discussion discussion;

    public AddDiscussion(String _id, Discussion discussion) {
        this._id = _id;
        this.discussion = discussion;
    }
}
