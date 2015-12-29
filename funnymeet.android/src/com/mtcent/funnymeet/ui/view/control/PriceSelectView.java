package com.mtcent.funnymeet.ui.view.control;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.ArrayList;

import mtcent.funnymeet.R;

public abstract class PriceSelectView extends FunnymeetBaseView {
	TextView praicinterval;
	TextView praiccustom;
	ListView praicintervalList;
	BaseAdapter adapter;
	RelativeLayout praiccustomLinearLayout;
	ArrayList<JSONObject> dataList = new ArrayList<JSONObject>();

	public PriceSelectView(Activity activity) {
		super(activity);
		mainView = inflater.inflate(
				R.layout.somain_find_child_select_huodong_price, null);
		init();
		resetView();
	}

	public void setDataList(final ArrayList<JSONObject> list) {
		mActivity.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				dataList = list;
				adapter.notifyDataSetChanged();
			}
		});
	}

	void init() {
		praicinterval = (TextView) mainView.findViewById(R.id.praicinterval);
		praiccustom = (TextView) mainView.findViewById(R.id.praiccustom);
		praicintervalList = (ListView) mainView
				.findViewById(R.id.praicintervalList);
		praiccustomLinearLayout = (RelativeLayout) mainView
				.findViewById(R.id.praiccustomLinearLayout);
		Button priceButton = (Button) mainView.findViewById(R.id.priceButton);
		adapter = new BaseAdapter() {

			@Override
			public View getView(int arg0, View arg1, ViewGroup arg2) {
				arg1 = inflater.inflate(R.layout.item_find_select, null);
				JSONObject json = (JSONObject) adapter.getItem(arg0);
				if (json != null) {
					((TextView) arg1.findViewById(R.id.name)).setText(json
							.optString("title"));
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
		praicintervalList.setAdapter(adapter);
		praicintervalList
				.setOnItemClickListener(new ListView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						onSelect((JSONObject) adapter.getItem(arg2));
					}
				});

		praicinterval
				.setBackgroundResource(R.drawable.hdtypelist_selected_firstone);
		praiccustom.setBackgroundResource(R.drawable.hd_price_unselected);

		praicinterval.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				praicintervalList.setVisibility(View.VISIBLE);
				praiccustomLinearLayout.setVisibility(View.GONE);
				praicinterval
						.setBackgroundResource(R.drawable.hdtypelist_selected_firstone);
				praiccustom
						.setBackgroundResource(R.drawable.hd_price_unselected);
			}
		});

		praiccustom.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				praicintervalList.setVisibility(View.GONE);
				praiccustomLinearLayout.setVisibility(View.VISIBLE);
				praiccustom
						.setBackgroundResource(R.drawable.hdtypelist_selected_firstone);
				praicinterval
						.setBackgroundResource(R.drawable.hd_price_range_unselected);

			}
		});

		final EditText lowPrice = (EditText) mainView
				.findViewById(R.id.lowPrice);
		final EditText hightPrice = (EditText) mainView
				.findViewById(R.id.hightPrice);

		priceButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				JSONObject json = new JSONObject();
				try {
					json.putOpt("id", "-1");
					json.putOpt("start", lowPrice.getText().toString());
					json.putOpt("end", hightPrice.getText().toString());
				} catch (Exception e) {
					e.printStackTrace();
				}
				onSelect(json);
			}
		});
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
