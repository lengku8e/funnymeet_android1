package com.mtcent.funnymeet.ui.view.control;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import mtcent.funnymeet.R;

public abstract class TypeSelectView extends FunnymeetBaseView {
	ListView TYPR1;
	GridView TYPR2;
	BaseAdapter TYPR1Adapter;
	BaseAdapter TYPR2Adapter;
	ArrayList<JSONObject> typeList = new ArrayList<JSONObject>();
	ArrayList<JSONObject> typeList2 = new ArrayList<JSONObject>();
	int typeListSelect = 0;

	public TypeSelectView(Activity activity, ArrayList<JSONObject> list) {
		super(activity);
		mainView = inflater.inflate(
				R.layout.somain_find_child_select_huodong_type, null);
		typeList = list;
		init();
		onSelectType1(getDefaultIndex());
		resetView();
	}

	int getDefaultIndex() {
		int index = 0;
		for (int i = 0; i < typeList.size(); i++) {
			JSONObject json = (JSONObject) typeList.get(i);
			if (json.optString("id").equals("-1")) {
				index = i;
				break;
			}
		}
		return index;
	}

	public void setDataList(final ArrayList<JSONObject> list) {
		mActivity.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				typeList = list;
				if(typeList==null){
					typeList = new ArrayList<JSONObject>();
				}
				onSelectType1(getDefaultIndex());
				resetView();
			}
		});
	}

	void init() {
		TYPR1 = (ListView) mainView.findViewById(R.id.TYPR1);
		TYPR2 = (GridView) mainView.findViewById(R.id.TYPR2);
		TYPR1Adapter = new BaseAdapter() {
			class TagObject {
				TextView textView;
				String id;
			}

			@Override
			public View getView(int arg0, View arg1, ViewGroup arg2) {
				TagObject tag = null;
				if (arg1 == null) {
					tag = new TagObject();
					arg1 = inflater.inflate(R.layout.item_find_select, null);
					tag.textView = (TextView) arg1.findViewById(R.id.name);
					arg1.setTag(tag);
				} else {
					tag = (TagObject) arg1.getTag();
				}

				JSONObject json = (JSONObject) getItem(arg0);
				if (json != null) {
					tag.textView.setText(json.optString("name"));
				}
				if (typeListSelect == arg0) {
					if (arg0 == 0) {
						arg1.setBackgroundResource(R.drawable.hdtypelist_selected_firstone);
					} else if (arg0 == getCount() - 1) {
						arg1.setBackgroundResource(R.drawable.hdtypelist_selected_lastone);
					} else {
						arg1.setBackgroundResource(R.drawable.hdtypelist_selected_normal);
					}

				} else {
					arg1.setBackgroundResource(R.drawable.hdtypelist_unselected);
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
					return typeList.get(arg0);
				}
				return 0;
			}

			@Override
			public int getCount() {
				return typeList.size();
			}
		};
		TYPR2Adapter = new BaseAdapter() {

			@Override
			public View getView(int arg0, View arg1, ViewGroup arg2) {

				if (arg1 == null) {
					arg1 = inflater.inflate(
							R.layout.somain_find_child_select_kindlist_item,
							null);

				}
				JSONObject json = (JSONObject) getItem(arg0);
				if (json != null) {
					TextView findlistitem = (TextView) arg1
							.findViewById(R.id.findlistitem);
					((TextView) findlistitem).setText(json.optString("name"));
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
					return typeList2.get(arg0);
				}
				return null;
			}

			@Override
			public int getCount() {
				return typeList2.size();
			}
		};
		TYPR1.setAdapter(TYPR1Adapter);
		TYPR1.setOnItemClickListener(new ListView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				onSelectType1((JSONObject) TYPR1Adapter.getItem(arg2));
				onSelectType1(arg2);
				TYPR1Adapter.notifyDataSetChanged();
				TYPR2Adapter.notifyDataSetChanged();
			}
		});
		TYPR2.setAdapter(TYPR2Adapter);
		TYPR2.setOnItemClickListener(new GridView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				onSelectType2((JSONObject) TYPR1Adapter.getItem(arg2));
			}
		});
	}

	public abstract void onSelectType1(JSONObject sJson);

	public abstract void onSelectType2(JSONObject sJson);

	void onSelectType1(int index) {
		try {
			typeListSelect = index;
			typeList2 = new ArrayList<JSONObject>();
			if (typeListSelect < typeList.size()) {
				JSONObject json2 = typeList.get(typeListSelect);
				JSONArray arr = json2.optJSONArray("childs");
				if (arr != null) {
					int count = arr.length();
					for (int i = 0; i < count; i++) {
						typeList2.add(arr.getJSONObject(i));
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onShow() {
		resetView();
	}

	@Override
	public void onHide() {
		super.onHide();
	}

	void resetView() {
		mActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				TYPR1Adapter.notifyDataSetChanged();
				TYPR2Adapter.notifyDataSetChanged();
			}
		});
	}
}
