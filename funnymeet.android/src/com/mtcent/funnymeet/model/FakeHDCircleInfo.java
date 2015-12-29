package com.mtcent.funnymeet.model;

/**
 * Created by Administrator on 2015/8/15.
 */
public class FakeHDCircleInfo {
    public ShareInfo shareInfo;
    public HDInfo hdInfo;

    public FakeHDCircleInfo(ShareInfo shareInfo, HDInfo hdInfo) {
        this.shareInfo = shareInfo;
        this.hdInfo = hdInfo;
    }

    public ShareInfo getShareInfo() {
        return shareInfo;
    }

    public HDInfo getHdInfo() {
        return hdInfo;
    }

}
