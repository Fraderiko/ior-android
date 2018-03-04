package ru.iorcontrol.ior.ior.Model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by alexeykazinets on 17/02/2018.
 */

public class Egroup implements Serializable {
    public String _id;
    public String name;
    public List<String> users;
}
