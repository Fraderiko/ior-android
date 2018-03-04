package ru.iorcontrol.ior.ior.Model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by me on 24/11/2017.
 */

public class Discussion implements Serializable {

    public long date;
    public String message;
    public List<String> image_media;
    public List<String> video_media;
    public String author;

    public Discussion(long date, String message, List<String> image_media, List<String> video_media, String author) {
        this.date = date;
        this.message = message;
        this.image_media = image_media;
        this.video_media = video_media;
        this.author = author;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setImage_media(List<String> image_media) {
        this.image_media = image_media;
    }

    public void setVideo_media(List<String> video_media) {
        this.video_media = video_media;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public long getDate() {
        return date;
    }

    public String getMessage() {
        return message;
    }

    public List<String> getImage_media() {
        return image_media;
    }

    public List<String> getVideo_media() {
        return video_media;
    }

    public String getAuthor() {
        return author;
    }
}
