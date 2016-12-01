package com.example.myselfapp.music;

import org.litepal.crud.DataSupport;

import java.io.Serializable;

/**
 * 播放列表
 */
public class Playlist extends DataSupport {

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private int id;
    private int _mid;
    private String title;
    private int duration;
    private String artist;
    private int album_id;

    public Playlist() {
        super();
    }

    public int get_mid() {
        return _mid;
    }

    public void set_mid(int _mid) {
        this._mid = _mid;
    }

    public int getAlbum_id() {
        return album_id;
    }

    public void setAlbum_id(int album_id) {
        this.album_id = album_id;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }


    public Playlist(int _mid, String title, int duration, String artist, int album_id) {
        super();
        this._mid = _mid;
        this.title = title;
        this.duration = duration;
        this.artist = artist;
        this.album_id = album_id;
    }

    @Override
    public boolean equals(Object o) {
        return o != null && ((Playlist) o).get_mid() == _mid && ((Playlist) o).getDuration() == duration;
    }

    @Override
    public String toString() {
        return "Playlist [id=" + getId() + ", title_tv=" + title + ", duration=" + duration + ", artist=" + artist + ", album_id=" + album_id + "]";
    }

}
