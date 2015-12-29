package com.mtcent.funnymeet.ui.view.control;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.ArrayList;

import mtcent.funnymeet.R;

public abstract class DistanceSelectView extends FunnymeetBaseView {
	ListView disSelectList;
	BaseAdapter adapter;
	RelativeLayout praiccustomLinearLayout;
	ArrayList<JSONObject> dataList = new ArrayList<JSONObject>();

	public DistanceSelectView(Activity activity) {
		super(activity);
		mainView = inflater.inflate(
				R.layout.somain_find_child_select_huodong_distance, null);
		init();
		testData();
		resetView();
	}
	public void setDataList(final ArrayList<JSONObject> list)
	{
		mActivity.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				dataList = list;
				adapter.notifyDataSetChanged();
			}
		});
	}
	void init() {

		disSelectList = (ListView) mainView.findViewById(R.id.disSelectList);

		adapter = new BaseAdapter() {

			@Override
			public View getView(int arg0, View arg1, ViewGroup arg2) {
				arg1 = inflater.inflate(R.layout.item_find_select, null);
				JSONObject json = (JSONObject) adapter.getItem(arg0);
				if (json != null) {
					((TextView)arg1.findViewById(R.id.name)).setText(json.optString("title"));
				}
				return arg1;
			}

			@Override
			public long getItemId(int arg0) {
				return arg0;
			}

			@Override
			public Object getItem(int arg0) {
				if (getCount() > arg0) {
					return dataList.get(arg0);
				}
				return null;
			}

			@Override
			public int getCount() {
				return dataList.size();
			}
		};
		disSelectList.setAdapter(adapter);
		disSelectList
				.setOnItemClickListener(new ListView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						onSelect((JSONObject) adapter.getItem(arg2));
					}
				});
	}

	void testData() {
		try {
			JSONObject json = null;
			json = new JSONObject();
			json.putOpt("id", "1");
			json.putOpt("name", "一公里内");
			dataList.add(json);

			json = new JSONObject();
			json.putOpt("id", "1");
			json.putOpt("name", "五公里内");
			dataList.add(json);

			json = new JSONObject();
			json.putOpt("id", "1");
			json.putOpt("name", "十公里内");
			dataList.add(json);

			json = new JSONObject();
			json.putOpt("id", "1");
			json.putOpt("name", "市区");
			dataList.add(json);

			json = new JSONObject();
			json.putOpt("id", "1");
			json.putOpt("name", "郊区");
			dataList.add(json);

			adapter.notifyDataSetChanged();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public abstract void onSelect(JSONObject sJson);

	@Override
	public void onShow() {
		super.onShow();
	}

	@Override
	public void onHide() {
		super.onHide();
	}

	void resetView() {
		mActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {

			}
		});
	}


}
