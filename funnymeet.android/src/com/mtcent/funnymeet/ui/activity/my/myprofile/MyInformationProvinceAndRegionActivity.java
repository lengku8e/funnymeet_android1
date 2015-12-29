package com.mtcent.funnymeet.ui.activity.my.myprofile;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.mtcent.funnymeet.ui.activity.base.BaseActivity;
import com.mtcent.funnymeet.ui.view.dialog.CustomDialog;
import com.mtcent.funnymeet.ui.helper.RequestHelper.Pdtask;
import com.mtcent.funnymeet.ui.helper.UserMangerHelper;
import com.mtcent.funnymeet.util.StrUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Set;
import java.util.TreeMap;

import mtcent.funnymeet.R;

public class MyInformationProvinceAndRegionActivity extends BaseActivity {

	TextView titleTextView;

	ListView provinceAndRegion;
	ListView regions;
	public static final int ID = MyInformationProvinceAndRegionActivity.class
			.hashCode();
	TreeMap<String, String> regionOfProvince;
	Set<String> tmp;
	String[] province;
	String tmpProvince;
	ListViewCityAdapter cityAdapter = new ListViewCityAdapter();
	ListViewProvinceAdapter provinceAdapter = new ListViewProvinceAdapter();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		super.onCreate(savedInstanceState);
		setContentView(R.layout.provinceandregionlist);
		init();
	}

	@SuppressWarnings("unchecked")
	protected void init() {
		View v = findViewById(R.id.left_backLayout);
		v.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {

				if (provinceAndRegion.getVisibility() == View.VISIBLE) {
					finish();
				} else if (regions.getVisibility() == View.VISIBLE) {

					regions.setVisibility(View.GONE);
					provinceAndRegion.setVisibility(View.VISIBLE);
				}

			}
		});

		provinceAndRegion = (ListView) findViewById(R.id.provinceAndRegion);

		titleTextView = (TextView) findViewById(R.id.titleTextView);
		titleTextView.setText("地区");

		regionOfProvince = StrUtil.getRegionOfProvince();
		province = StrUtil.getProvince;

		// Collections.sort(province);

		provinceAndRegion.setAdapter(provinceAdapter);
		provinceAdapter.setProvince(province);
		regions = (ListView) findViewById(R.id.regions);
		regions.setAdapter(cityAdapter);

		regions.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				commit((String) cityAdapter.getItem(position));
			}
		});

		provinceAndRegion.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				tmpProvince = province[position]; // 通过position获取所点击的对象

				provinceAndRegion.setVisibility(View.GONE);
				regions.setVisibility(View.VISIBLE);

				String[] cities = regionOfProvince.get(tmpProvince).split("/");
				cityAdapter.setCity(cities);
			}
		});

	}

	private void commit(String city) {
		JSONObject user = UserMangerHelper.getDefaultUser();
		try {

			user.put("city", city);
			user.put("province", tmpProvince);
			UserMangerHelper.requestUpDateInfo(user, this, this);
			showWait();

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void onFinish(Pdtask t) {
		// TODO Auto-generated method stub
		if (t.getParam("method").equals("setUserInfoByGuid")) {
			boolean succ = false;
			String msg = "设置个人信息失败";
			JSONObject user = null;
			if (t.json != null) {
				JSONObject results = t.json.optJSONObject("results");
				if (results != null) {
					user = results.optJSONObject("user");
					int su = results.optInt("success");
					if (su == 1) {
						succ = true;
					} else if (results.has("msg")) {
						msg = results.optString("msg");
					}
				}
			}

			if (succ && user != null && user.has("mobilePhone")) {
				UserMangerHelper.setDefaultUserChange(user);

			} else {
				StrUtil.showMsg(this, msg);
			}

		}
		hideWait();
		finish();
	}

	@Override
	public void onUpdate(Pdtask t) {
		// TODO Auto-generated method stub

	}

	CustomDialog waitDialog = null;

	public void showWait() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (waitDialog == null) {
					waitDialog = new CustomDialog(
							MyInformationProvinceAndRegionActivity.this);
					waitDialog.setContentView(R.layout.dialog_wait);
				}
				waitDialog.show();
			}
		});
	}

	public void hideWait() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				waitDialog.dismiss();
				finish();
			}
		});
	}

	class ListViewCityAdapter extends BaseAdapter {

		String[] cities = new String[0];

		public void setCity(final String[] citys) {
			runOnUiThread(new Runnable() {
				public void run() {
					cities = citys;
					ListViewCityAdapter.this.notifyDataSetChanged();
				}
			});

		}

		public int getCount() {
			return cities.length;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				LayoutInflater inflater = (LayoutInflater) MyInformationProvinceAndRegionActivity.this
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

				// 使用View的对象itemView与R.layout.item关联
				convertView = inflater.inflate(
						R.layout.provinceandregionlist_item, null);
			}

			// 通过findViewById()方法实例R.layout.item内各组件
			TextView name = (TextView) convertView.findViewById(R.id.name);
			name.setText(cities[position]); // 填入相应的值
			return convertView;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub

			return cities[position];
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

	}

	class ListViewProvinceAdapter extends BaseAdapter {

		String[] provinces = new String[0];

		public void setProvince(final String[] pro) {
			runOnUiThread(new Runnable() {
				public void run() {
					provinces = pro;
					ListViewProvinceAdapter.this.notifyDataSetChanged();
				}
			});

		}

		public int getCount() {
			return provinces.length;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				LayoutInflater inflater = (LayoutInflater) MyInformationProvinceAndRegionActivity.this
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

				// 使用View的对象itemView与R.layout.item关联
				convertView = inflater.inflate(
						R.layout.provinceandregionlist_item, null);
			}

			// 通过findViewById()方法实例R.layout.item内各组件
			TextView name = (TextView) convertView.findViewById(R.id.name);
			name.setText(provinces[position]); // 填入相应的值
			return convertView;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub

			return provinces[position];
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

	}

}
