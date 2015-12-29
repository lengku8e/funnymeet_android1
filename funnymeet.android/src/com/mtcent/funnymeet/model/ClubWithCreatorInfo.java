package com.mtcent.funnymeet.model;

import android.annotation.SuppressLint;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import java.io.Serializable;

@SuppressLint("DefaultLocale")
public class ClubWithCreatorInfo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1490454166293013304L;
	private String createUserName;
	private String clubname;
	private String status;
	private String logourl;
	private String clubGuid;
	private String firstLetter;

	private String[] pinyin;

	private HanyuPinyinOutputFormat format = null;

	public ClubWithCreatorInfo(String createUserName, String clubGuid,
			String clubname, String logourl, String status) {
		super();
		this.createUserName = createUserName;
		this.clubGuid = clubGuid;
		this.clubname = clubname;
		this.logourl = logourl;
		this.status = status;

		pinyin = null;
		format = new HanyuPinyinOutputFormat();
		format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);

		try {
			pinyin = PinyinHelper.toHanyuPinyinStringArray(
					this.clubname.charAt(0), format);
		} catch (BadHanyuPinyinOutputFormatCombination e) {
			e.printStackTrace();
		}

		// 如果c不是汉字，toHanyuPinyinStringArray会返回null

		if (pinyin == null) {
			this.firstLetter = String.valueOf(clubname.charAt(0)).toUpperCase();
		} else {
			this.firstLetter = String.valueOf(pinyin[0].charAt(0))
					.toUpperCase();
		}

	}

	public String getClubGuid() {
		return clubGuid;
	}

	public void setClubGuid(String clubGuid) {
		this.clubGuid = clubGuid;
	}



	public String getCreateUserName() {
		return createUserName;
	}

	public void setCreateUserName(String createUserName) {
		this.createUserName = createUserName;
	}

	public String getClubname() {
		return clubname;
	}

	public void setClubname(String clubname) {
		this.clubname = clubname;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setFirstLetter(String firstLetter) {
		this.firstLetter = firstLetter;
	}

	public String getFirstLetter() {
		return firstLetter;
	}

	public String getLogourl() {
		return logourl;
	}

	public void setLogourl(String logourl) {
		this.logourl = logourl;
	}

}
