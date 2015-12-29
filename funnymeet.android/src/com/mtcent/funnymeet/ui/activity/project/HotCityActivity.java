package com.mtcent.funnymeet.ui.activity.project;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mtcent.funnymeet.SOApplication;
import com.mtcent.funnymeet.config.Constants;
import com.mtcent.funnymeet.util.BaiduMap.LocationCallBack;
import com.mtcent.funnymeet.ui.helper.RequestHelper;
import com.mtcent.funnymeet.ui.helper.RequestHelper.DownBack;
import com.mtcent.funnymeet.ui.helper.RequestHelper.Pdtask;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.CollationKey;
import java.text.Collator;
import java.text.RuleBasedCollator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import mtcent.funnymeet.R;

public class HotCityActivity extends Activity implements DownBack {
	final static String QUANPIN = "pinyinquan";
	final static String JIANPIN = "pinyinjian";
	ExpandableListView listView;
	TextView cityTag;
	AlphaAnimation cityTagAnimation;
	LinearLayout cityPinYinTagListView;
	EditText searchCityEditText;
	MyElistAdapter adapter;
	MyElistAdapter adapterSearch;
	private LayoutInflater inflater = null;
	ImageView cityTitleBack;
	JSONObject[] listHotCity = new JSONObject[0];
	JSONObject[] gpsCity = new JSONObject[0];
	JSONObject[] listAllCity = new JSONObject[0];
	JSONObject[] searchCity = new JSONObject[0];
	ArrayList<String> cityPYTagList = new ArrayList<String>();

	String hotCityUrl = Constants.SERVICE_HOST + "?method=listHotCity";
	String allCityUrl = Constants.SERVICE_HOST + "?method=listCity";

	// http://14.17.77.117/api/api.htm?method=listCity
	public static class CITY {
		int id = 0;
		String name = null;
		int sortNumber = 0;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hotcity);
		init();
		requestData();
		resetView();
	}

	@Override
	protected void onResume() {
		try {
			gpsCity = new JSONObject[1];
			String str = SOApplication.getDataManager().getValue(
					SOApplication.MyCityTag);
			gpsCity[0] = new JSONObject(str);
			resetView();
		} catch (Exception e) {
			e.printStackTrace();
		}

		super.onResume();
	}

	void resetView() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (adapter.equals(listView.getTag())) {
					adapter.child[0] = gpsCity.clone();
					adapter.child[1] = listHotCity.clone();
					adapter.child[2] = listAllCity.clone();
					adapter.notifyDataSetChanged();
					int groupCount = adapter.getGroupCount();

					for (int i = 0; i < groupCount; i++) {
						listView.expandGroup(i);
					}
					cityPinYinTagListView.setVisibility(View.VISIBLE);
				} else if (adapterSearch.equals(listView.getTag())) {
					adapterSearch.child[0] = searchCity.clone();
					int childCount = adapterSearch.getChildrenCount(0);
					if (childCount > 0) {
						adapterSearch.group[0] = "搜索到" + childCount + "个结果";
					} else {
						adapterSearch.group[0] = "没有搜索";
					}
					adapterSearch.notifyDataSetChanged();
					listView.expandGroup(0);
					cityTag.setVisibility(View.INVISIBLE);

					cityPinYinTagListView.setVisibility(View.GONE);
				}
			}
		});
	}

	void requestData() {

		Pdtask task = new Pdtask(this, this, hotCityUrl, null,
				RequestHelper.Type_DownJsonString, "", 0, true);
		SOApplication.getDownLoadManager().startTask(task);

		if (!getAllCity()) {
			task = new Pdtask(this, this, allCityUrl, null,
					RequestHelper.Type_DownJsonString, "", 0, false);
			SOApplication.getDownLoadManager().startTask(task);
		}

		SOApplication.getBaiduMap().getbdlocation(new LocationCallBack() {
			@Override
			public void onfinish(double latitude, double longitude,
					double accuracy, String city) {
				if (city != null) {
					try {
						gpsCity = new JSONObject[1];
						gpsCity[0] = new JSONObject();
						gpsCity[0].put("name", city);
						SOApplication.getDataManager().setValue(
								SOApplication.MyCityTag, gpsCity[0].toString());
						resetView();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});

	}

	void saveAllCity() {
		try {
			JSONArray allCity = new JSONArray();
			for (JSONObject json : listAllCity) {
				allCity.put(json);
			}
			SOApplication.getDataManager().setValue("allcity",
					allCity.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	boolean getAllCity() {
		try {
			String str = SOApplication.getDataManager().getValue("allcity");
			JSONArray allCity = new JSONArray(str);
			int count = allCity.length();
			JSONObject[] listAllCitytemp = new JSONObject[count];
			for (int index = 0; index < count; index++) {
				JSONObject jj = allCity.getJSONObject(index);
				listAllCitytemp[index] = jj;
			}
			listAllCity = listAllCitytemp;
			buildCityPinYinTag();
			return listAllCity.length > 0;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	String convertDuoYin(String str) {
		String s = str.replace("重庆", "崇庆");
		s = s.replace("长", "偿");
		s = s.replace("六安", "路安");
		s = s.replace("沈阳", "审阳");

		return s;
	}

	public class CityComparator implements Comparator<JSONObject> {

		private RuleBasedCollator collator;

		public CityComparator() {
			collator = (RuleBasedCollator) Collator
					.getInstance(java.util.Locale.CHINA);
		}

		@Override
		public int compare(JSONObject obj1, JSONObject obj2) {
			String str1 = obj1.optString("name");
			String str2 = obj2.optString("name");
			if (str1 != null && str2 != null) {
				str1 = convertDuoYin(str1);
				str2 = convertDuoYin(str2);

				CollationKey c1 = collator.getCollationKey(str1);
				CollationKey c2 = collator.getCollationKey(str2);
				return c1.compareTo(c2);
			} else if (str1 == null && str2 != null) {
				return -1;
			} else if (str1 != null && str2 == null) {
				return 1;
			} else {
				return 0;
			}
		}
	}

	@Override
	public void onFinish(Pdtask t) {
		try {
			JSONObject j = t.json;
			if ("OK".equalsIgnoreCase(j.optString("status"))) {
				if (j.has("results")) {
					JSONArray jArray = j.getJSONArray("results");
					int count = jArray.length();
					if (count > 0) {
						HanyuPinyinOutputFormat hanYuPinOutputFormat = new HanyuPinyinOutputFormat();
						hanYuPinOutputFormat
								.setCaseType(HanyuPinyinCaseType.UPPERCASE);
						hanYuPinOutputFormat
								.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
						hanYuPinOutputFormat
								.setVCharType(HanyuPinyinVCharType.WITH_U_UNICODE);

						ArrayList<JSONObject> list = new ArrayList<JSONObject>();
						for (int index = 0; index < count; index++) {
							JSONObject jj = jArray.getJSONObject(index);
							String name = jj.optString("name");
							if (name != null && name.length() > 0) {
								// 目标文字，输出个数，间隔字符串
								name = convertDuoYin(name);
								int leng = name.length();
								String quanPin = "";
								String jianPin = "";
								for (int i = 0; i < leng; i++) {
									String pinyin = PinyinHelper
											.toHanyuPinyinString(
													name.substring(i, i + 1),
													hanYuPinOutputFormat, "");
									quanPin += pinyin;
									jianPin += pinyin.substring(0, 1);
								}
								jj.putOpt(QUANPIN, quanPin.toUpperCase());
								jj.putOpt(JIANPIN, jianPin.toUpperCase());
								list.add(jj);
							}
						}
						Collections.sort(list, new CityComparator());
						if (hotCityUrl.equals(t.url)) {
							listHotCity = (JSONObject[]) list
									.toArray(new JSONObject[list.size()]);
						} else if (allCityUrl.equals(t.url)) {
							listAllCity = (JSONObject[]) list
									.toArray(new JSONObject[list.size()]);
							saveAllCity();
							// 重新建索引
							buildCityPinYinTag();
						}
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		resetView();
	}

	void buildCityPinYinTag() {

		ArrayList<String> list = new ArrayList<String>();
		for (JSONObject jsonObject : listAllCity) {
			String c = jsonObject.optString(JIANPIN);
			c = (c != null && c.length() > 0) ? c.substring(0, 1) : null;
			if (c != null) {
				if (!list.contains(c)) {
					list.add(c);
				}
			}
		}

		Collections.sort(list, String.CASE_INSENSITIVE_ORDER);
		list.add(0, "热");
		cityPYTagList = list;
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				cityPinYinTagListView.removeAllViews();
				for (String str : cityPYTagList) {
					TextView textV = new TextView(HotCityActivity.this);
					LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
							LayoutParams.MATCH_PARENT, 0, 1);
					textV.setText(str);
					textV.setGravity(Gravity.CENTER);
					textV.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
					textV.setTag(str);
					cityPinYinTagListView.addView(textV, param);

					// cityPinYinTagListView.findViewWithTag(tag)
				}
			}
		});

	}

	@Override
	public void onUpdate(Pdtask t) {
		// TODO Auto-generated method stub

	}

	void init() {
		inflater = LayoutInflater.from(HotCityActivity.this);
		cityTag = (TextView) findViewById(R.id.cityTag);
		cityTag.setVisibility(View.INVISIBLE);

		cityTagAnimation = new AlphaAnimation(1f, 1f);
		cityTagAnimation.setDuration(300);
		cityTagAnimation.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				cityTag.setVisibility(View.INVISIBLE);
			}
		});

		cityPinYinTagListView = (LinearLayout) findViewById(R.id.cityPinYinTagList);
		cityPinYinTagListView
				.setOnTouchListener(new LinearLayout.OnTouchListener() {

					@Override
					public boolean onTouch(View arg0, MotionEvent arg1) {

						if (arg1.getAction() == MotionEvent.ACTION_DOWN
								|| arg1.getAction() == MotionEvent.ACTION_MOVE) {
							float dy = arg1.getY();
							int count = cityPYTagList.size();
							int vh = cityPinYinTagListView.getHeight();
							int index = (int) (dy * count / vh);
							if (count > 0) {
								int childcount = cityPinYinTagListView
										.getChildCount();
								if (index < 0) {
									index = 0;
								}
								if (index >= childcount) {
									index = childcount - 1;
								}
								TextView ttt = (TextView) cityPinYinTagListView
										.getChildAt(index);
								scrllto((String) (ttt.getTag()));
								if (cityTag.getVisibility() != View.VISIBLE) {
									cityTag.setVisibility(View.VISIBLE);
									cityTag.clearAnimation();
									cityTag.startAnimation(cityTagAnimation);
								}
							}
						}
						return true;
					}
				});

		adapterSearch = new MyElistAdapter(HotCityActivity.this);
		adapterSearch.group = new String[] { "没有搜索结果" };
		adapterSearch.child = new JSONObject[][] { {} };

		adapter = new MyElistAdapter(HotCityActivity.this);
		adapter.group = new String[] { "GPS定位城市", "热门城市", "全部城市" };
		adapter.child = new JSONObject[][] { {}, {}, {} };
		listView = (ExpandableListView) findViewById(R.id.cityList);
		listView.setGroupIndicator(null);
		listView.setAdapter(adapter);
		listView.setTag(adapter);

		listView.setOnGroupClickListener(new OnGroupClickListener() {
			@Override
			public boolean onGroupClick(ExpandableListView parent, View v,
					int groupPosition, long id) {
				return true; // 默认为false，设为true时，点击事件不会展开Group
			}

		});
		listView.setOnChildClickListener(new OnChildClickListener() {

			@Override
			public boolean onChildClick(ExpandableListView arg0, View arg1,
					int arg2, int arg3, long arg4) {

				if (arg1 != null && arg1.getTag() != null
						&& arg1.getTag() instanceof MyElistAdapter.tagChild) {
					MyElistAdapter.tagChild tag = (MyElistAdapter.tagChild) arg1
							.getTag();
					if (tag.city != null) {
						SOApplication.getDataManager().setValue(
								SOApplication.HotCityTag, tag.city.toString());
					}
					finish();
				}
				return false;
			}
		});
		// listView.setChildDivider(null);
		// listView.setDivider(null);
		// 遍历所有group,将所有项设置成默认展开

		cityTag = (TextView) findViewById(R.id.cityTag);
		cityTag.setBackgroundResource(R.drawable.bg_list_sel_alpha);
		cityTag.setTextSize(40f);
		cityTag.setTextColor(0xffffffff);

		cityTag.setVisibility(View.INVISIBLE);

		listView.setOnScrollListener(new ExpandableListView.OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView arg0, int arg1) {
				if (adapter.equals(listView.getTag())) {
					if (arg1 == SCROLL_STATE_IDLE) {
						cityTag.setVisibility(View.INVISIBLE);
					} else {
						cityTag.setVisibility(View.VISIBLE);
					}
				}
			}

			@Override
			public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {
				onScrollChange(arg1);
			}
		});
		searchCityEditText = (EditText) findViewById(R.id.searchCityEditText);
		searchCityEditText.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable arg0) {
				if (searchCityEditText.getText().length() > 0) {
					doSearch();
					if (!adapterSearch.equals(listView.getTag())) {
						listView.setAdapter(adapterSearch);
						listView.setTag(adapterSearch);
						adapterSearch.notifyDataSetChanged();
					}
					resetView();
				} else {
					if (!adapter.equals(listView.getTag())) {
						listView.setAdapter(adapter);
						listView.setTag(adapter);
						adapter.notifyDataSetChanged();
					}
					resetView();
				}
			}
		});
		searchCityEditText
				.setOnFocusChangeListener(new EditText.OnFocusChangeListener() {

					@Override
					public void onFocusChange(View arg0, boolean hasFocus) {

					}
				});

		searchCityEditText.setOnKeyListener(new EditText.OnKeyListener() {

			@Override
			public boolean onKey(View arg0, int arg1, KeyEvent arg2) {
				if (arg2.getAction() == KeyEvent.ACTION_DOWN
						&& arg1 == KeyEvent.KEYCODE_BACK
						&& searchCityEditText.getText().length() > 0) {
					searchCityEditText.setText("");
					return true;
				}
				return false;
			}
		});
		cityTitleBack = (ImageView) findViewById(R.id.left_back);
		cityTitleBack.setClickable(true);
		cityTitleBack.setOnClickListener(new ImageView.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				HotCityActivity.this.finish();
			}
		});

	}

	void scrllto(String s) {
		if (s != null && adapter.equals(listView.getTag())) {
			if (s.equals("热")) {
				listView.setSelection(0);
				onScrollChange(0);
			} else {

				int count = adapter.getChildrenCount(2);
				for (int i = 0; i < count; i++) {
					JSONObject json = (JSONObject) adapter.getChild(2, i);
					String jianPin = json.optString(JIANPIN);
					if (jianPin != null && jianPin.startsWith(s)) {
						int index = 1 + adapter.getChildrenCount(0) + 1
								+ adapter.getChildrenCount(1) + 1 + i;
						listView.setSelection(index);
						onScrollChange(index);
						break;
					}
				}

			}
		}
	}

	void onScrollChange(int curIndex) {
		if (adapter.equals(listView.getTag())) {
			String text = "热门";
			int countGps = adapter.getChildrenCount(0);
			int countHot = adapter.getChildrenCount(1);
			int seekAllCity = 1 + countGps + 1 + countHot + 1;
			if (curIndex < seekAllCity) {
				text = "热门";
			} else {
				int countAll = adapter.getChildrenCount(2);
				JSONObject json = (JSONObject) adapter.getChild(2, curIndex
						- seekAllCity);
				if (json != null) {
					text = json.optString(JIANPIN);
					if (text != null) {
						text = text.substring(0, 1);
					}
				}
			}

			if (!cityTag.getText().equals(text)) {
				cityTag.setText(text);
			}

			int colorSelect = Color.parseColor("#66696969");
			int colorUnSelect = Color.parseColor("#00000000");
			int childcount = cityPinYinTagListView.getChildCount();
			for (int i = 0; i < childcount; i++) {
				TextView ttt = (TextView) cityPinYinTagListView.getChildAt(i);
				String s = (String) (ttt.getTag());
				if (text.startsWith(s)) {
					ttt.setBackgroundColor(colorSelect);
				} else {
					ttt.setBackgroundColor(colorUnSelect);
				}
			}

		}
	}

	public class MyElistAdapter extends BaseExpandableListAdapter {

		// 分组数据
		public class tagChild {
			TextView text;
			JSONObject city;
		}

		public class tagParent {
			TextView text;
		}

		String searchStr = "";
		private String[] group = { "GPS定位城市", "热门城市", "全部城市" };

		private JSONObject[][] child = { {},

		{}, {} };

		private Context mContext;

		public MyElistAdapter(Context mContext) {

			super();

			this.mContext = mContext;

		}

		@Override
		public int getGroupCount() {

			return group.length;

		}

		@Override
		public int getChildrenCount(int groupPosition) {

			return child[groupPosition].length;

		}

		@Override
		public Object getGroup(int groupPosition) {

			return group[groupPosition];

		}

		@Override
		public Object getChild(int groupPosition, int childPosition) {

			return child[groupPosition][childPosition];

		}

		@Override
		public long getGroupId(int groupPosition) {

			return groupPosition;

		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {

			return childPosition;

		}

		@Override
		public boolean hasStableIds() {

			return true;

		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,

		View convertView, ViewGroup parent) {
			tagParent tag = null;
			if (convertView == null
					|| !(convertView.getTag() instanceof tagChild)) {
				tag = new tagParent();
				convertView = inflater.inflate(R.layout.hotcity_list_table,
						null);
				tag.text = (TextView) convertView.findViewById(R.id.name);
				convertView.setTag(tag);
			}
			tag = (tagParent) convertView.getTag();
			tag.text.setText((String) getGroup(groupPosition));

			return convertView;

		}

		@Override
		public View getChildView(int groupPosition, int childPosition,

		boolean isLastChild, View convertView, ViewGroup parent) {

			// 实例化布局文件
			tagChild tag = null;
			if (convertView == null
					|| !(convertView.getTag() instanceof tagParent)) {
				tag = new tagChild();
				convertView = inflater
						.inflate(R.layout.hotcity_list_item, null);
				tag.text = (TextView) convertView.findViewById(R.id.name);
				convertView.setTag(tag);

			}
			tag = (tagChild) convertView.getTag();

			JSONObject city = (JSONObject) getChild(groupPosition,
					childPosition);
			tag.city = city;
			if (city != null) {
				String name = city.optString("name");
				tag.text.setText(name);
			}
			return convertView;

		}

		@Override
		public boolean isChildSelectable(int arg0, int arg1) {
			// TODO Auto-generated method stub
			return true;
		}

	}

	void doSearch() {
		String s = searchCityEditText.getText().toString().toUpperCase();
		if (s != null && s.length() > 0) {
			JSONObject[] olist = adapter.child[2];
			ArrayList<JSONObject> list = new ArrayList<JSONObject>();
			if (adapterSearch.searchStr.length() > 0
					&& s.startsWith(adapterSearch.searchStr)) {
				olist = adapterSearch.child[0];
			}
			adapterSearch.searchStr = s;
			for (JSONObject json : olist) {
				if (isContain(json.optString(JIANPIN), s)) {
					list.add(json);
				} else if (isContain(json.optString(QUANPIN), s)) {
					list.add(json);
				} else if (isContain(json.optString("name"), s)) {
					list.add(json);
				}
			}

			searchCity = (JSONObject[]) list
					.toArray(new JSONObject[list.size()]);
		} else {
			searchCity = new JSONObject[0];
		}
	}

	boolean isContain(String str, String s) {
		return str != null && str.contains(s);
	}

}
