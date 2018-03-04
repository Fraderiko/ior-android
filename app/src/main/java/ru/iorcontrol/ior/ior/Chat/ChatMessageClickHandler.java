package ru.iorcontrol.ior.ior.Chat;

/**
 * Created by alexeykazinets on 26/01/2018.
 */

public interface ChatMessageClickHandler {
    void videoClicked(String url);
    void imageClicked(String url);
}
