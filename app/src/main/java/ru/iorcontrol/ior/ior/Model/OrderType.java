package ru.iorcontrol.ior.ior.Model;

import java.io.Serializable;

/**
 * Created by me on 24/11/2017.
 */

public class OrderType implements Serializable {

    public String name;

    public OrderType(String name) {
        this.name = name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
