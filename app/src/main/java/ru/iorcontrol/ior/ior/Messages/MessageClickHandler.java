package ru.iorcontrol.ior.ior.Messages;

/**
 * Created by alexeykazinets on 03/12/2017.
 */

public interface MessageClickHandler {

    void messageClicked(String url, MessagesImageSection.MEDIATYPE type);
}
