package ru.iorcontrol.ior.ior.Model;

import java.util.List;

/**
 * Created by alexeykazinets on 18/02/2018.
 */

public class CheckStatusRequest {
    public List<String> groups;
    public String user;

    public CheckStatusRequest(List<String> groups, String user) {
        this.groups = groups;
        this.user = user;
    }
}
