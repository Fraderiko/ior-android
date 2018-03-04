package ru.iorcontrol.ior.ior;

import java.util.Comparator;

import ru.iorcontrol.ior.ior.Model.Order;

/**
 * Created by alexeykazinets on 29/11/2017.
 */

class OrderComparator implements Comparator<Order> {
    @Override
    public int  compare(Order t1, Order t2) {
        if(t1.getUpdated() > t2.getUpdated())
            return -1;
        else if (t1.getUpdated() < t2.getUpdated())
            return +1;
        return 0;
    }
}