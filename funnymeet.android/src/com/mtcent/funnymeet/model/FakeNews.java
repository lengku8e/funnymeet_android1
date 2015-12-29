package com.mtcent.funnymeet.model;

/**
 * Created by Administrator on 2015/8/15.
 */
public class FakeNews {
    String title;
    String source;
    String timeInfo;

    public FakeNews(String title, String source, String timeInfo) {

        this.title = title;
        this.source = source;
        this.timeInfo = timeInfo;
    }

    public String getTitle() {
        return title;
    }

    public String getSource() {
        return source;
    }

    public String getTimeInfo() {
        return timeInfo;
    }
}
