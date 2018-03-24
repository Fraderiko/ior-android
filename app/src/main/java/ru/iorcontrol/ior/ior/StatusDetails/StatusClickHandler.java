package ru.iorcontrol.ior.ior.StatusDetails;

import ru.iorcontrol.ior.ior.Model.Status;

/**
 * Created by alexeykazinets on 27/11/2017.
 */

public interface StatusClickHandler {

    void statusClicked(Status status, int position);
}
