package com.mtcent.funnymeet.model;

/**
 * Created by Administrator on 2015/8/15.
 */
public class ShareInfo {
    public String logoUrl;
    public String shareTime;
    public String shareOwner;
    public String shareType;
    public String shareBrief;

    public ShareInfo(String logoUrl, String shareTime, String shareOwner,
                     String shareType, String shareBrief) {
        this.logoUrl = logoUrl;
        this.shareBrief = shareBrief;
        this.shareOwner = shareOwner;
        this.shareTime = shareTime;
        this.shareType = shareType;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public String getShareTime() {
        return shareTime;
    }

    public String getShareOwner() {
        return shareOwner;
    }

    public String getShareType() {
        return shareType;
    }

    public String getShareBrief() {
        return shareBrief;
    }
}
