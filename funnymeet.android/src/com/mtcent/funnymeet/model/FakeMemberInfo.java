package com.mtcent.funnymeet.model;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import java.io.Serializable;

/**
 * Created by Administrator on 2015/8/15.
 */
public class FakeMemberInfo implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    String firstLetter;
    String memberName;
    String imageUrl;
    String memberType;
    String isTakePartIn;

    private String[] pinyin;

    private HanyuPinyinOutputFormat format = null;

    public FakeMemberInfo(String nickName, String imageUrl, String memberType,
                          String isTakePartIn) {

        this.isTakePartIn = isTakePartIn;
        this.memberType = memberType;
        this.memberName = nickName;
        this.imageUrl = imageUrl;
        pinyin = null;
        format = new HanyuPinyinOutputFormat();
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);

        try

        {
            pinyin = PinyinHelper.toHanyuPinyinStringArray(
                    this.memberName.charAt(0), format);
        }

        catch (BadHanyuPinyinOutputFormatCombination e)

        {
            e.printStackTrace();
        }

        // 如果c不是汉字，toHanyuPinyinStringArray会返回null

        if (pinyin == null) {
            this.firstLetter = String.valueOf(nickName.charAt(0)).toUpperCase();
        } else {
            this.firstLetter = String.valueOf(pinyin[0].charAt(0))
                    .toUpperCase();
        }

    }

    public String getFirstLetter() {
        return firstLetter;
    }

    public String getMemberName() {
        return memberName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getMemberType() {
        return memberType;
    }

    public String getIsTakePartIn() {
        return isTakePartIn;
    }
}
