package ru.iorcontrol.ior.ior;

/**
 * Created by alexeykazinets on 28/11/2017.
 */

public interface GalleryClickListener {

    void fieldClicked(int fieldPosition, int elementPosition, HorizontalGalleryAdapter.TYPE type, String url);
}
