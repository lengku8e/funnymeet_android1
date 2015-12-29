package com.mtcent.funnymeet.model;

import com.mtcent.funnymeet.model.FakeMemberInfo;

import java.util.Comparator;

/**
 * Created by Administrator on 2015/8/15.
 */
public class MyComparator implements Comparator<FakeMemberInfo> {
    @Override
    public int compare(FakeMemberInfo lhs, FakeMemberInfo rhs) {

        return lhs.getFirstLetter().compareTo(rhs.getFirstLetter());
    }
}
