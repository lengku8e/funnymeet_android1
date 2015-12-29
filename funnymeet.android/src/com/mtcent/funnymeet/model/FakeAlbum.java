package com.mtcent.funnymeet.model;

/**
 * Created by Administrator on 2015/8/15.
 */
public class FakeAlbum {
    String albumName;
    String numsOfAlbum;
    int imageResource;

    public FakeAlbum(String albumName, String numsOfAlbum, int imageResource) {
        this.albumName = albumName;
        this.numsOfAlbum = numsOfAlbum;
        this.imageResource = imageResource;
    }

    public String getAlbumName() {
        return albumName;
    }

    public String getNumsOfAlbum() {
        return numsOfAlbum;
    }

    public int getImageResource() {
        return imageResource;
    }
}
