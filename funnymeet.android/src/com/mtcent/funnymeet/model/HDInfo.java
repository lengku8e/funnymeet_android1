package com.mtcent.funnymeet.model;

import java.util.ArrayList;

/**
 * Created by Administrator on 2015/8/15.
 */
public class HDInfo {
    ArrayList<String> previewList;
    ArrayList<String> miniHDList;
    String singlePreview;

    public HDInfo(ArrayList<String> miniHDList, ArrayList<String> previewList,
                  String singlePreview) {

        this.miniHDList = miniHDList;
        this.previewList = previewList;
        this.singlePreview = singlePreview;
    }

    public ArrayList<String> getPreviewList() {
        return previewList;
    }

    public String getSinglePreview() {
        return singlePreview;
    }

    public ArrayList<String> getMiniHDList() {
        return miniHDList;
    }

}
