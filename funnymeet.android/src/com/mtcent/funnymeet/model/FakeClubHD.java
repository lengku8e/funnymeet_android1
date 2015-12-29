package com.mtcent.funnymeet.model;

/**
 * Created by Administrator on 2015/8/15.
 */
public class FakeClubHD {
    String date;
    String previewUrl;
    String title;
    String city;
    String building;
    String location;

    public FakeClubHD(String date, String previewUrl, String title,
                      String city, String building, String location) {
        super();
        this.date = date;
        this.previewUrl = previewUrl;
        this.title = title;
        this.city = city;
        this.building = building;
        this.location = location;
    }

    public String getDate() {
        return date;
    }

    public String getPreviewUrl() {
        return previewUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getCity() {
        return city;
    }

    public String getBuilding() {
        return building;
    }

    public String getLocation() {
        return location;
    }
}
