package com.mtcent.funnymeet.model;

/**
 * Created by Administrator on 2015/8/9.
 */
public class FakeInfo {
    int  i = 0;
    int iamge;
    String title;

    public FakeInfo(int image, String title) {
        this.iamge = image;
        this.title = title;
    }

    public int getIamge() {
        return iamge;
    }

    public String getTitle() {
        return title;
    }

}
