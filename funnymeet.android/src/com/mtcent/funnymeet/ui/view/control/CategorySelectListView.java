package com.mtcent.funnymeet.ui.view.control;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.ArrayList;

import mtcent.funnymeet.R;

@SuppressWarnings("unchecked")
public class CategorySelectListView extends RelativeLayout {
	public interface CategorySelectAdapter {
		public void getCategory();

		public String getCategoryName(int index, JSONObject categoryJson);

		public void getCategoryChildList(int index, JSONObject categoryJson);

		public String getCategoryChildName(int index, JSONObject categoryJson,
				int indexChild, JSONObject childJson);

		public void onChildSelect(int index, JSONObject categoryJson,
				int indexChild, JSONObject childJson);
	}

	public CategorySelectListView(Context context) {
		super(context);
		oncreate(context);
	}

	public CategorySelectListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		oncreate(context);
	}

	public CategorySelectListView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		oncreate(context);
	}

	protected Activity mActivity;
	protected View mainView;
	protected LayoutInflater inflater;
	CategorySelectAdapter mAdapter;

	ListView allviewChildListView;
	CategoryAdapter categoryAdapter;
	GridView mainContentGridView;
	CategoryChildAdapter childAdapter;

	LinearLayout childListItemContent;
	ArrayList<JSONObject> categoryList = new ArrayList<JSONObject>();

	public ArrayList<JSONObject>[] categoryChildList = new ArrayList[0];

	int onSelectIndex = -1;
	int onSelectIndex2 = -1;

	public void setCategorySelectAdapter(CategorySelectAdapter adapter) {
		mAdapter = adapter;
		mAdapter.getCategory();
	}

	void oncreate(Context context) {
		mActivity = (Activity) context;
		inflater = LayoutInflater.from(mActivity);
		init();
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.MATCH_PARENT,
				RelativeLayout.LayoutParams.MATCH_PARENT);
		addView(mainView, params);
	}

	void init() {
		mainView = inflater
				.inflate(R.layout.somain_find_child_select_all, null);
		allviewChildListView = (ListView) mainView.findViewById(R.id.childList);
		categoryAdapter = new CategoryAdapter();
		allviewChildListView.setAdapter(categoryAdapter);
		allviewChildListView
				.setOnItemClickListener(new ListView.OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						onselectCategoryItem(arg2);
						categoryAdapter.notifyDataSetChanged();

					}
				});
		allviewChildListView.setVerticalScrollBarEnabled(false);
		childListItemContent = (LinearLayout) mainView
				.findViewById(R.id.childListItemContent);
		mainContentGridView = (GridView) mainView.findViewById(R.id.gridView1);

		childAdapter = new CategoryChildAdapter();
		mainContentGridView.setAdapter(childAdapter);
		mainContentGridView
				.setOnItemClickListener(new GridView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						onselectChildItem(arg2);
						childAdapter.notifyDataSetChanged();
					}
				});
	}

	class CategoryAdapter extends BaseAdapter {
		public ArrayList<JSONObject> dataList = new ArrayList<JSONObject>();

		class TagObject {
			TextView textView;
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			TagObject tag = null;
			if (arg1 == null) {
				tag = new TagObject();
				arg1 = inflater.inflate(R.layout.item_find_subject1, null);
				tag.textView = (TextView) arg1.findViewById(R.id.name);
				arg1.setTag(tag);
			} else {
				tag = (TagObject) arg1.getTag();
			}

			JSONObject json = (JSONObject) getItem(arg0);
			String name = null;
			if (json != null) {
				// name = json.optString("name");
				name = mAdapter.getCategoryName(arg0, json);
			}
			tag.textView.setText(name);
			if (onSelectIndex == arg0) {
				// if (arg0 == 0) {
				arg1.setBackgroundResource(R.drawable.hdtypelist_selected_firstone);
				// } else if (arg0 == getCount() - 1) {
				// arg1.setBackgroundResource(R.drawable.hdtypelist_selected_firstone);
				// } else {
				// arg1.setBackgroundResource(R.drawable.hdtypelist_selected_normal);
				// }
				// arg1.setBackgroundColor(0x00000000);
			} else {
				// arg1.setBackgroundResource(R.drawable.hdtypelist_unselected);
				arg1.setBackgroundColor(0x00000000);
			}
			return arg1;
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public Object getItem(int arg0) {
			if (arg0 < getCount()) {
				return dataList.get(arg0);
			}

			return null;
		}

		@Override
		public int getCount() {
			return dataList.size();
		}

	};

	class CategoryChildAdapter extends BaseAdapter {
		public ArrayList<JSONObject> dataListItemContent = new ArrayList<JSONObject>();

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {

			if (arg1 == null) {
				arg1 = inflater.inflate(R.layout.somain_find_child_select_item,
						null);

			}
			JSONObject json = (JSONObject) getItem(arg0);
			TextView findlistitem = (TextView) arg1
					.findViewById(R.id.findlistitem);
			String name = null;
			if (json != null) {
				// name = json.optString("name");
				name = mAdapter.getCategoryChildName(onSelectIndex,
						(JSONObject) categoryAdapter.getItem(onSelectIndex),
						arg0, json);
			}
			findlistitem.setText(name);
			// if (onSelectIndex2 == arg0) {
			// findlistitem.setBackgroundColor(0xff04e4e4);
			// } else {
			// findlistitem.setBackgroundColor(0xfff4f4f4);
			// }
			return arg1;
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public Object getItem(int arg0) {
			if (dataListItemContent.size() > arg0) {
				return dataListItemContent.get(arg0);
			}
			return null;
		}

		@Override
		public int getCount() {

			return dataListItemContent.size();
		}
	};

	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 0) {
				categoryAdapter.dataList = categoryList;
				onselectCategoryItem(0);
				categoryAdapter.notifyDataSetChanged();
			} else if (msg.what == 1) {
				ArrayList<JSONObject> list = null;
				if (categoryChildList.length > onSelectIndex) {
					list = categoryChildList[onSelectIndex];
				}
				if (list == null) {
					list = new ArrayList<JSONObject>();
				}
				onSelectIndex2 = -1;
				childAdapter.dataListItemContent = list;
				childAdapter.notifyDataSetChanged();
			}
		}
	};

	void onselectCategoryItem(int index) {
		onSelectIndex = index;

		ArrayList<JSONObject> list = null;
		if (categoryChildList.length > onSelectIndex) {
			list = categoryChildList[onSelectIndex];
		}
		if (list == null) {
			list = new ArrayList<JSONObject>();
			JSONObject json = (JSONObject) categoryAdapter
					.getItem(onSelectIndex);
			if (mAdapter != null && json != null) {
				mAdapter.getCategoryChildList(onSelectIndex, json);
			}
		}
		onSelectIndex2 = -1;
		childAdapter.dataListItemContent = list;
		childAdapter.notifyDataSetChanged();
	}

	void onselectChildItem(int index) {
		onSelectIndex2 = index;
		if (mAdapter != null) {
			mAdapter.onChildSelect(onSelectIndex,
					(JSONObject) categoryAdapter.getItem(onSelectIndex),
					onSelectIndex2,
					(JSONObject) childAdapter.getItem(onSelectIndex2));
		}
	}

	@SuppressWarnings("unchecked")
	public void updateCategory(ArrayList<JSONObject> list) {
		categoryList = list;
		if (categoryList == null) {
			categoryList = new ArrayList<JSONObject>();
		}
		categoryChildList = new ArrayList[categoryList.size()];
		handler.sendEmptyMessage(0);
	}

	public void updateCategoryChild(int categoryIndex, JSONObject categoryJson,
			ArrayList<JSONObject> childList) {
		if (categoryChildList.length > categoryIndex) {
			categoryChildList[categoryIndex] = childList;
		}
		handler.sendEmptyMessage(1);
	}
}