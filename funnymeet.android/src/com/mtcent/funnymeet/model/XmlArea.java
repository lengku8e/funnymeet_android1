package com.mtcent.funnymeet.model;

import com.mtcent.funnymeet.model.area.Area;
import com.mtcent.funnymeet.model.area.City;
import com.mtcent.funnymeet.model.area.District;
import com.mtcent.funnymeet.model.area.Province;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class XmlArea implements Area {

	public static final String TAG_NAME_ROOT = "root";
	
	public static final String TAG_NAME_PROVINCE = "province";

	public static final String TAG_NAME_CITY = "city";

	public static final String TAG_NAME_DISTRICT = "district";

	public static final String TAG_ATTRIBUTE_NAME = "name";

	private List<Province> provinces = new ArrayList<Province>();

	private XmlPullParser parser;

	public XmlArea(XmlPullParser parser) {
		this.parser = parser;
		try {
			createAreaFromParser();
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void createAreaFromParser() throws XmlPullParserException,
			IOException {

		int eventType = parser.getEventType();
		String currentTag = null;
		String currentProvinceName = null;
		String currentCityName = null;
		String currentDistrictName = null;
		Province currProvince = null;
		City currCity = null;
		District currDistrict = null;
		while (eventType != XmlPullParser.END_DOCUMENT) {
			if (eventType == XmlPullParser.START_TAG) {
				currentTag = parser.getName();
				if (TAG_NAME_PROVINCE.equals(currentTag)) {
					//
					currentProvinceName = parser.getAttributeValue(null,
							TAG_ATTRIBUTE_NAME);
					currProvince = new Province(currentProvinceName);
				} else if (TAG_NAME_CITY.equals(currentTag)) {
					//
					currentCityName = parser.getAttributeValue(null,
							TAG_ATTRIBUTE_NAME);
					currCity = new City(currentProvinceName, currentCityName);
				} else if (TAG_NAME_DISTRICT.equals(currentTag)) {
					//
					currentDistrictName = parser.getAttributeValue(null,
							TAG_ATTRIBUTE_NAME);
					currDistrict = new District(currentProvinceName,
							currentCityName, currentDistrictName);
				} else if (TAG_NAME_ROOT.equals(currentTag)) {
					//
					//
				}
			} else if (eventType == XmlPullParser.END_TAG) {
				currentTag = parser.getName();
				//
				if (TAG_NAME_PROVINCE.equals(currentTag)) {
					//
					provinces.add(currProvince);
				} else if (TAG_NAME_CITY.equals(currentTag)) {
					//
					currProvince.addCity(currCity);
				} else if (TAG_NAME_DISTRICT.equals(currentTag)) {
					//
					currCity.addDistrict(currDistrict);
				}
			}
			eventType = parser.next();
		}
	}

	@Override
	public List<Province> getProvinces() {
		return this.provinces;
	}

	@Override
	public List<City> getCities(String provinceName) {
		if (provinceName == null) {
			return null;
		}
		for (int i = 0; i < provinces.size(); i++) {
			Province p = provinces.get(i);
			if (provinceName.equals(p.getName())) {
				return p.getCities();
			}
		}
		return null;
	}

	@Override
	public List<District> getDistricts(String provinceName, String cityName) {
		if (provinceName == null || cityName == null) {
			return null;
		}
		for (int i = 0; i < provinces.size(); i++) {
			Province p = provinces.get(i);
			if (provinceName.equals(p.getName())) {
				List<City> cities = p.getCities();
				for (int j = 0; j < cities.size(); j++) {
					City c = cities.get(j);
					if (cityName.equals(c.getName())) {
						return c.getDistricts();
					}
				}
			}
		}
		return null;
	}
}
