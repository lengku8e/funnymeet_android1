package com.mtcent.funnymeet.ui.view.fragment;

import android.app.Activity;
import android.app.ListFragment;
import android.content.Intent;
import android.content.res.XmlResourceParser;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.mtcent.funnymeet.model.XmlArea;
import com.mtcent.funnymeet.model.area.Area;
import com.mtcent.funnymeet.model.area.City;
import com.mtcent.funnymeet.model.area.District;
import com.mtcent.funnymeet.model.area.Province;
import com.mtcent.funnymeet.ui.activity.my.clubconsole.MyClubNewProjectActivity;

import java.util.ArrayList;
import java.util.List;

import mtcent.funnymeet.R;

public class AreaListFragment extends ListFragment {

	public static final int CURR_CONTENT_PROVINCE = 0;
	public static final int CURR_CONTENT_CITY = 1;
	public static final int CURR_CONTENT_DISTRICT = 2;
	public static final String ADDRESS_DELIMITER = " ";
	private int mCurrContent = CURR_CONTENT_PROVINCE;

	private Province mCurrProvince;
	private City mCurrCity;
	private District mCurrDistrict;

	/**
	 * 中国全省列表
	 */
	private ArrayList<Province> mProvinces;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		XmlResourceParser xmlParser = null;
		try {
			xmlParser = this.getResources().getXml(R.xml.area);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Area area = new XmlArea(xmlParser);
		mProvinces = (ArrayList<Province>) area.getProvinces();
		ArrayAdapter<Province> adapter = new ArrayAdapter<Province>(
				getActivity(), R.layout.fragment_area_item, mProvinces);
		setListAdapter(adapter);
	}

	/**
	 * 取得当前显示内容
	 * 
	 * @return
	 */
	public int getCurrContent() {
		return this.mCurrContent;
	}

	/**
	 * 回退显示内容，例如从地区回退到城市，从城市回退到省，当前显示为省时不能再回退
	 */
	public void rollbackContent() {
		if (this.mCurrContent == CURR_CONTENT_PROVINCE) {
			return;
		}
		switch (this.mCurrContent) {
		case CURR_CONTENT_CITY:
			ArrayAdapter<Province> adapter = new ArrayAdapter<Province>(
					getActivity(), R.layout.fragment_area_item, mProvinces);
			setListAdapter(adapter);
			break;
		case CURR_CONTENT_DISTRICT:
			List<City> cities = mCurrProvince.getCities();
			ArrayAdapter<City> cityAdapter = new ArrayAdapter<City>(
					getActivity(), R.layout.fragment_area_item, cities);
			setListAdapter(cityAdapter);
			break;
		}
		this.mCurrContent--;
	}

	public void onListItemClick(ListView lv, View v, int position, long id) {

		switch (this.mCurrContent) {
		case CURR_CONTENT_PROVINCE:
			Province p = (Province) (getListAdapter()).getItem(position);
			this.mCurrProvince = p;
			this.mCurrContent = CURR_CONTENT_CITY;
			List<City> cities = p.getCities();
			ArrayAdapter<City> cityAdapter = new ArrayAdapter<City>(
					getActivity(), R.layout.fragment_area_item, cities);
			setListAdapter(cityAdapter);
			break;
		case CURR_CONTENT_CITY:
			City c = (City) (getListAdapter()).getItem(position);
			this.mCurrCity = c;
			this.mCurrContent = CURR_CONTENT_DISTRICT;
			List<District> districts = c.getDistricts();
			ArrayAdapter<District> districtAdapter = new ArrayAdapter<District>(
					getActivity(), R.layout.fragment_area_item, districts);
			setListAdapter(districtAdapter);
			break;
		case CURR_CONTENT_DISTRICT:
			District d = (District) (getListAdapter()).getItem(position);
			this.mCurrDistrict = d;
			Intent i = new Intent();
			i.putExtra(MyClubNewProjectActivity.EXTRA_PARAM_ADDRESS,
					this.mCurrProvince.getName() + ADDRESS_DELIMITER
							+ this.mCurrCity.getName() + ADDRESS_DELIMITER
							+ this.mCurrDistrict.getName());
			getActivity().setResult(Activity.RESULT_FIRST_USER, i);
			getActivity().finish();
			break;
		}

	}

}
