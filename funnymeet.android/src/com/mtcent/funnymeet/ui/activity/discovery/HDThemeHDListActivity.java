package com.mtcent.funnymeet.ui.activity.discovery;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mtcent.funnymeet.SOApplication;
import com.mtcent.funnymeet.config.Constants;
import com.mtcent.funnymeet.ui.activity.discovery.HDDetailsActivity.HdDetails;
import com.mtcent.funnymeet.ui.view.control.HDListView;
import com.mtcent.funnymeet.ui.view.control.HDListView.HDListViewAct;
import com.mtcent.funnymeet.ui.helper.RequestHelper;
import com.mtcent.funnymeet.ui.view.control.DistanceSelectView;
import com.mtcent.funnymeet.ui.view.control.PriceSelectView;
import com.mtcent.funnymeet.ui.view.control.TimeSelectView;
import com.mtcent.funnymeet.ui.view.control.TypeSelectView;
import com.mtcent.funnymeet.ui.view.dialog.CustomDialog;
import com.mtcent.funnymeet.ui.helper.RequestHelper.DownBack;
import com.mtcent.funnymeet.ui.helper.RequestHelper.Pdtask;
import com.mtcent.funnymeet.util.StrUtil;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

import mtcent.funnymeet.R;

public class HDThemeHDListActivity extends Activity implements DownBack {
	HDInfo info = new HDInfo();;
	TextView titleName;
	ViewGroup huoDongChildListLayout;
	Activity mActivity;
	LayoutInflater inflater;
	RelativeLayout mainContentLayout;
	View typeTextView;
	FrameLayout selectLayout2;
	ImageView huoDongListEmpty_pic;
	TextView huoDongListEmpty_text;
	ImageView menu;

	//
	ArrayList<JSONObject> childList = new ArrayList<JSONObject>();
	JSONObject parentJson = null;
	JSONObject childJson = null;

	// 1、2级形式列表
	ArrayList<JSONObject> typeList = new ArrayList<JSONObject>();
	JSONObject jsonType1 = new JSONObject();
	JSONObject jsonType2 = new JSONObject();
	ArrayList<Integer> ticketsPrice;
	// 活动列表
	HDListView mListView;
	// filter_sort
	TypeSelectView typeView;
	TimeSelectView timeView;
	DistanceSelectView sortView;
	PriceSelectView priceView;
	ArrayList<JSONObject> timeViewDataList = new ArrayList<JSONObject>();
	ArrayList<JSONObject> filterSortViewDataList = new ArrayList<JSONObject>();
	ArrayList<JSONObject> priceViewDataList = new ArrayList<JSONObject>();
	JSONObject jsonTime = new JSONObject();
	JSONObject jsonFilterSort = new JSONObject();
	JSONObject jsonPrice = new JSONObject();

	public static class HDInfo implements Serializable {
		private static final long HDInfoUID = 1L;
		public static final String key = "HDListSelect";
		public String parentJson = null;
		public String childJson = null;
		public String childJsonList = null;

	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		doIntent(intent);
		// 拉条件列表
		requestFilterType();
		requestFilterPrice();
		requestFilterDate();
		requestFilterSort();
		// 拉活动列表
		requestData(1);
		resetView();
	}

	void doIntent(Intent intent) {
		if (intent != null) {
			Bundle mBundle = intent.getExtras();
			Serializable serializable = null;
			if (mBundle != null) {
				serializable = mBundle.getSerializable(HDInfo.key);
			}
			if (serializable != null) {
				info = (HDInfo) serializable;
				try {
					if (info.parentJson != null) {
						parentJson = new JSONObject(info.parentJson);
					}
					if (info.childJson != null) {
						childJson = new JSONObject(info.childJson);
					}
					if (info.childJsonList != null) {
						childList = StrUtil.getJSONArrayList(new JSONObject(
								info.childJsonList));
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.somain_find_child_select_huodong);
		doIntent(getIntent());
		init();
		// 拉过滤条件
		requestFilterType();
		requestFilterPrice();
		requestFilterDate();
		requestFilterSort();
		// 拉活动列表
		requestData(1);
		resetView();
	}

	void init() {
		mActivity = this;
		inflater = LayoutInflater.from(mActivity);
		titleName = (TextView) findViewById(R.id.titleTextView);
		titleName.setText(parentJson.optString("name") + "/");

		huoDongChildListLayout = (ViewGroup) findViewById(R.id.menuLayout);
		huoDongChildListLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				hideCheckChildList();
			}
		});

		View v = findViewById(R.id.left_backLayout);
		v.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});

		menu = (ImageView) findViewById(R.id.menu);
		menu.setVisibility(View.VISIBLE);
		menu.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				onCheckType();
			}
		});

		mListView = (HDListView) findViewById(R.id.HuoDongList);

		View huoDongListEmpty = (View) findViewById(R.id.huoDongListEmpty);
		mListView.setEmptyView(huoDongListEmpty);
		huoDongListEmpty_pic = (ImageView) huoDongListEmpty
				.findViewById(R.id.huoDongListEmpty_pic);
		huoDongListEmpty_text = (TextView) huoDongListEmpty
				.findViewById(R.id.huoDongListEmpty_text);

		mainContentLayout = (RelativeLayout) findViewById(R.id.mainContent);

		typeTextView = (View) findViewById(R.id.typeSelect);
		typeTextView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				showTypeSelect();
			}
		});

		selectLayout2 = (FrameLayout) findViewById(R.id.selectLayout2);
		selectLayout2.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				selectLayout2.setVisibility(View.GONE);
			}
		});
		View priceSelect = (View) findViewById(R.id.priceSelect);
		priceSelect.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				showPriceSelect();
			}
		});
		View timeSelect = (View) findViewById(R.id.timeSelect);
		timeSelect.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				showTimeSelect();
			}
		});
		View disSelect = (View) findViewById(R.id.disSelect);
		disSelect.setVisibility(View.GONE);
		// disSelect.setOnClickListener(new OnClickListener() {
		// @Override
		// public void onClick(View arg0) {
		// showDistanceSelect();
		// }
		// });
		mListView.setAct(new HDListViewAct() {
			@Override
			public void loadMore(int index) {
				requestData(index);
			}

			@Override
			public void fresh() {
				requestData(1);
			}

			@Override
			public void onselectHD(int index, ArrayList<String> list) {
				if (index < list.size()) {
					HdDetails info = new HdDetails();
					info.index = index;
					info.list = list;
					Intent mIntent = new Intent(mActivity,
							HDDetailsActivity.class);
					Bundle mBundle = new Bundle();
					mBundle.putSerializable(HdDetails.key, info);
					mIntent.putExtras(mBundle);
					mActivity.startActivity(mIntent);
				}
			}
		});
	}

	void resetView() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (childJson != null) {
					String typeName = childJson.optString("name");
					titleName.setText(parentJson.optString("name") + "/"
							+ typeName);
				}
			}
		});
	}

	void onCheckType() {
		showCheckChildList();
	}

	void requestFilterType() {
		Pdtask task = new Pdtask(this, this, Constants.SERVICE_HOST, null,
				RequestHelper.Type_DownJsonString, null, 0, true);
		task.addParam("method", "listProjectTypeByParentSubject")
				// 方法名
				.addParam(
						"id",
						(parentJson == null || parentJson.optString("id") == null) ? "-1"
								: parentJson.optString("id"));// 一级主题id，-1代表全部
		SOApplication.getDownLoadManager().startTask(task);
	}

	void requestFilterPrice() {
		Pdtask task = new Pdtask(this, this, Constants.SERVICE_HOST, null,
				RequestHelper.Type_DownJsonString, null, 0, true);
		task.addParam("method", "listProjectFilterPrice");
		SOApplication.getDownLoadManager().startTask(task);
	}

	void requestFilterDate() {
		Pdtask task = new Pdtask(this, this, Constants.SERVICE_HOST, null,
				RequestHelper.Type_DownJsonString, null, 0, true);
		task.addParam("method", "listProjectFilterDate");
		SOApplication.getDownLoadManager().startTask(task);
	}

	void requestFilterSort() {
		Pdtask task = new Pdtask(this, this, Constants.SERVICE_HOST, null,
				RequestHelper.Type_DownJsonString, null, 0, true);
		task.addParam("method", "listProjectFilterSort");
		SOApplication.getDownLoadManager().startTask(task);
	}

	boolean isEmpty(String str) {
		return str == null || str.length() == 0;
	}

	int pageSize = 10;

	void requestData(int index) {
		Pdtask task = new Pdtask(this, this, Constants.SERVICE_HOST, null,
				RequestHelper.Type_PostParam, null, 0, true);

		// http://
		// 14.17.77.117/api/api.htm?method=listProject&parent_subject_id=**&child_subject_id=**&parent_type_id=*&child_type_id=*&state=*&page_size=**&page=*
		// parent_subject_id: 一级主题id，-1代表全部
		// child_subject_id：二级主题id，-1代表全部
		// parent_type_id：一级形式 -1代表全部
		// child_type_id：二级形式 -1代表全部
		// price：价格区间id -1代表全部
		// start_price：价格下限，-1指无下限
		// end_price：价格上限 -1指无上限
		// filter_sort_id:分类
		// start_date、end_date：日期区间 -1、-1代表无任何限制
		// state:-1全部2报名中 3进行中4已结束
		// page_size：分页大小
		// page：页码
		// JSONObject parentJson = null;
		// JSONObject childJson = null;
		// JSONObject childTJson = null;
		// JSONObject jsonTime = new JSONObject();
		// JSONObject jsonFilterSort = new JSONObject();
		// JSONObject jsonPrice = new JSONObject();

		String parent_subject_id = isEmpty(parentJson.optString("id")) ? "-1"
				: parentJson.optString("id");
		String child_subject_id = isEmpty(childJson.optString("id")) ? "-1"
				: childJson.optString("id");
		String parent_type_id = isEmpty(jsonType1.optString("id")) ? "-1"
				: jsonType1.optString("id");
		String child_type_id = isEmpty(jsonType2.optString("id")) ? "-1"
				: jsonType2.optString("id");

		String price = isEmpty(jsonPrice.optString("id")) ? "-1" : jsonPrice
				.optString("id");
		String start_price = isEmpty(jsonPrice.optString("start")) ? "-1"
				: jsonPrice.optString("start");
		String end_price = isEmpty(jsonPrice.optString("end")) ? "-1"
				: jsonPrice.optString("end");
		String filter_sort_id = isEmpty(jsonFilterSort.optString("id")) ? "-1"
				: jsonFilterSort.optString("id");
		String start_date = isEmpty(jsonTime.optString("start")) ? "-1"
				: jsonTime.optString("start");
		String end_date = isEmpty(jsonTime.optString("end")) ? "-1" : jsonTime
				.optString("end");
		String date = isEmpty(jsonTime.optString("id")) ? "-1" : jsonTime
				.optString("id");

		// 方法名
		task.addParam("method", "listProject")
		// 一级主题id，-1代表全部
				.addParam("parent_subject_id", parent_subject_id)
				// 二级主题id，-1代表全部
				.addParam("child_subject_id", child_subject_id)
				// 一级形式 -1代表全部
				.addParam("parent_type_id", parent_type_id)
				// 二级形式 -1代表全部
				.addParam("child_type_id", child_type_id)

				.addParam("filter_price", price)// 价格区间id，
				.addParam("start_price", start_price)//
				.addParam("end_price", end_price)//
				.addParam("filter_sort", filter_sort_id)//
				// .addParam("start_date", start_date)//
				// .addParam("end_date", end_date)//
				.addParam("filter_date", date)//
				// -1全部2报名中 3进行中4已结束
				.addParam("state", "-1")
				// 分页大小
				.addParam("page_size", pageSize + "")
				// 页码
				.addParam("page", String.valueOf(index));
		SOApplication.getDownLoadManager().startTask(task);

		showWait();
	}

	CustomDialog waitDialog = null;

	void showWait() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (waitDialog == null) {
					waitDialog = new CustomDialog(mActivity);
					waitDialog.setContentView(R.layout.dialog_wait);
				}
				huoDongListEmpty_pic.setVisibility(View.GONE);
				huoDongListEmpty_text.setText("正在拉取数据...");
				waitDialog.show();
			}
		});
	}

	void hideWait(final boolean err) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {

				if (err) {
					huoDongListEmpty_pic.setVisibility(View.GONE);
					huoDongListEmpty_text.setText("网络错误，请稍后重试");
				} else {
					huoDongListEmpty_pic.setVisibility(View.VISIBLE);
					huoDongListEmpty_text.setText("都被看光了");
				}
				waitDialog.dismiss();
			}
		});
	}

	@Override
	public void onFinish(Pdtask t) {

		ArrayList<JSONObject> list = StrUtil.getJSONArrayList(t.json);
		if (list == null) {
			list = new ArrayList<JSONObject>();
		}
		if ("listProject".equals(t.getParam("method"))) {
			if (t.getParam("child_subject_id")
					.equals(childJson.optString("id"))//
			// && t.getParam("parent_type_id").equals("-1")//
			// && t.getParam("child_type_id").equals("-1")
			) {
				int pageIndex = Integer.valueOf(t.getParam("page")).intValue();
				if (pageIndex == 1) {
					mListView.updata(list);
				} else {
					mListView.addMore(pageIndex, list);
				}
				if (list.size() == pageSize) {
					mListView.setLoadMoreAble(true);
				} else {
					mListView.setLoadMoreAble(false);
				}
			}
			boolean hasErr = false;
			if (t.json == null) {
				hasErr = true;
			} else {
				hasErr = false;
			}
			hideWait(hasErr);
		} else if ("listProjectTypeByParentSubject"
				.equals(t.getParam("method"))) {
			if (t.getParam("id").equals(parentJson.optString("id"))) {
				typeList = list;
				if (typeView != null) {
					typeView.setDataList(typeList);
				}
			}

		} else if ("listProjectFilterSort".equals(t.getParam("method"))) {

			filterSortViewDataList = list;
			jsonFilterSort = new JSONObject();
			if (sortView != null) {
				sortView.setDataList(filterSortViewDataList);
			}
		} else if ("listProjectFilterDate".equals(t.getParam("method"))) {
			timeViewDataList = list;
			jsonTime = new JSONObject();
			if (timeView != null) {
				timeView.setDataList(timeViewDataList);
			}
		} else if ("listProjectFilterPrice".equals(t.getParam("method"))) {

			priceViewDataList = list;
			jsonPrice = new JSONObject();
			if (priceView != null) {
				priceView.setDataList(priceViewDataList);
			}
		}

		resetView();

	}

	@Override
	public void onUpdate(Pdtask t) {
		onFinish(t);
	}

	// CustomDialog CheckChildDialog = null;
	ListView huoDongChildList;

	void showCheckChildList() {
		if (huoDongChildList == null) {

			huoDongChildList = (ListView) findViewById(R.id.menuList);

			BaseAdapter ad = new BaseAdapter() {

				@Override
				public View getView(int arg0, View arg1, ViewGroup arg2) {
					if (arg1 == null) {
						arg1 = inflater.inflate(
								R.layout.item_find_child_select_all, null);
					}
					TextView t = (TextView) arg1.findViewById(R.id.name);
					t.setTextColor(Color.WHITE);
					JSONObject json = (JSONObject) getItem(arg0);
					if (json != null) {
						String name = json.optString("name");
						t.setText(name);
					} else {
						t.setText(null);
					}
					return arg1;
				}

				@Override
				public long getItemId(int arg0) {
					return arg0;
				}

				@Override
				public Object getItem(int arg0) {
					if (childList.size() > arg0) {
						return childList.get(arg0);
					}
					return null;
				}

				@Override
				public int getCount() {
					return childList.size();
				}
			};
			huoDongChildList.setAdapter(ad);
			huoDongChildList
					.setOnItemClickListener(new ListView.OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> arg0, View arg1,
								int arg2, long arg3) {
							onSelectChild(arg2);
							hideCheckChildList();
						}
					});

			ad.notifyDataSetChanged();
		}
		huoDongChildListLayout.setVisibility(View.VISIBLE);
	}

	void onSelectChild(int arg2) {
		if (childList != null && arg2 < childList.size()) {
			childJson = childList.get(arg2);
			requestData(1);
			selectLayout2.setVisibility(View.GONE);
		}
		resetView();
	}

	void hideCheckChildList() {
		huoDongChildListLayout.setVisibility(View.GONE);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (huoDongChildListLayout.getVisibility() == View.VISIBLE) {
				hideCheckChildList();
				return true;
			} else if (selectLayout2.getVisibility() == View.VISIBLE) {
				selectLayout2.setVisibility(View.GONE);
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	void showTypeSelect() {
		if (typeView == null) {
			typeView = new TypeSelectView(mActivity, typeList) {

				@Override
				public void onSelectType1(JSONObject sJson) {
					jsonType1 = sJson;
					jsonType2 = new JSONObject();
					HDThemeHDListActivity.this.requestData(1);
				}

				@Override
				public void onSelectType2(JSONObject sJson) {
					jsonType2 = sJson;
					requestData(1);
					hideTypeSelect();
				}
			};
		}
		if (typeView != null) {
			if (selectLayout2.indexOfChild(typeView.getMainView()) < 0) {
				selectLayout2.removeAllViews();
				selectLayout2.addView(typeView.getMainView());
				selectLayout2.setVisibility(View.VISIBLE);
			} else {
				selectLayout2.removeAllViews();
				selectLayout2.setVisibility(View.GONE);
			}
		}
	}

	void hideTypeSelect() {
		selectLayout2.setVisibility(View.GONE);
	}

	void showTimeSelect() {
		if (timeView == null) {
			timeView = new TimeSelectView(mActivity) {
				@Override
				public void onSelect(JSONObject sJson) {
					// 选中价格，回调此函数。
					if (sJson != null) {
						jsonTime = sJson;
						requestData(1);
					}
					hideTimeSelect();
				}
			};
		}

		if (timeView != null) {
			if (selectLayout2.indexOfChild(timeView.getMainView()) < 0) {
				timeView.setDataList(timeViewDataList);
				selectLayout2.removeAllViews();
				selectLayout2.addView(timeView.getMainView());
				selectLayout2.setVisibility(View.VISIBLE);
			} else {
				selectLayout2.removeAllViews();
				selectLayout2.setVisibility(View.GONE);
			}
		}
	}

	void hideTimeSelect() {
		selectLayout2.setVisibility(View.GONE);
	}

	void showDistanceSelect() {
		if (sortView == null) {
			sortView = new DistanceSelectView(mActivity) {
				@Override
				public void onSelect(JSONObject sJson) {
					// 选中价格，回调此函数。
					if (sJson != null) {
						jsonFilterSort = sJson;
						requestData(1);
					}
					hideDistanceSelect();
				}
			};
		}

		if (sortView != null) {
			if (selectLayout2.indexOfChild(sortView.getMainView()) < 0) {
				sortView.setDataList(filterSortViewDataList);
				selectLayout2.removeAllViews();
				selectLayout2.addView(sortView.getMainView());
				selectLayout2.setVisibility(View.VISIBLE);
			} else {
				selectLayout2.removeAllViews();
				selectLayout2.setVisibility(View.GONE);
			}
		}

	}

	void hideDistanceSelect() {
		selectLayout2.setVisibility(View.GONE);
	}

	void showPriceSelect() {
		if (priceView == null) {
			priceView = new PriceSelectView(mActivity) {
				@Override
				public void onSelect(JSONObject sJson) {
					// 选中价格，回调此函数。
					if (sJson != null) {
						jsonPrice = sJson;
						requestData(1);
					}
					hidePriceSelect();
				}
			};
		}

		if (priceView != null) {
			if (selectLayout2.indexOfChild(priceView.getMainView()) < 0) {
				priceView.setDataList(priceViewDataList);
				selectLayout2.removeAllViews();
				selectLayout2.addView(priceView.getMainView());
				selectLayout2.setVisibility(View.VISIBLE);
			} else {
				selectLayout2.removeAllViews();
				selectLayout2.setVisibility(View.GONE);
			}
		}
	}

	void hidePriceSelect() {
		selectLayout2.setVisibility(View.GONE);
	}

	// void onselectHD(int index) {
	// JSONObject json = (JSONObject) adapter.getItem(index);
	// if (json != null && json.optString("id") != null) {
	// HdDetails info = new HdDetails();
	// info.id = json.optString("id");
	//
	// Intent mIntent = new Intent(mActivity, HDDetailsActivity.class);
	// Bundle mBundle = new Bundle();
	// mBundle.putSerializable(HdDetails.key, info);
	// mIntent.putExtras(mBundle);
	// mActivity.startActivity(mIntent);
	//
	// }
	//
	// }

}
