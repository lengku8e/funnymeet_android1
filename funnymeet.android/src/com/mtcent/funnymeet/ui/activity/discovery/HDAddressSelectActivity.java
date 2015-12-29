package com.mtcent.funnymeet.ui.activity.discovery;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.mtcent.funnymeet.SOApplication;
import com.mtcent.funnymeet.config.Constants;
import com.mtcent.funnymeet.ui.helper.RequestHelper;
import com.mtcent.funnymeet.ui.view.control.CategorySelectListView;
import com.mtcent.funnymeet.ui.view.control.CategorySelectListView.CategorySelectAdapter;
import com.mtcent.funnymeet.ui.helper.RequestHelper.Pdtask;
import com.mtcent.funnymeet.ui.view.control.ScrollHPageWithTableView;
import com.mtcent.funnymeet.ui.view.control.ScrollHPageWithTableView.ScrollHPageWithTableAdapter;
import com.mtcent.funnymeet.util.StrUtil;

import org.json.JSONObject;

import java.util.ArrayList;

import mtcent.funnymeet.R;

public class HDAddressSelectActivity extends Activity implements
		ScrollHPageWithTableAdapter, CategorySelectAdapter {

	TextView titleName;
	CategorySelectListView allView;
	View appointView;
	TextView selectAll;
	TextView selectAppoint;
	String title;
	protected Activity mActivity;
	protected LayoutInflater inflater;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.somain_base_tableview);
		mActivity = this;
		inflater = LayoutInflater.from(mActivity);
		init();

	}

	void init() {

		selectAll = (TextView) findViewById(R.id.selectAll);
		titleName = (TextView) findViewById(R.id.titleTextView);
		title = "地点";
		titleName.setText(title);
		View v = findViewById(R.id.left_backLayout);
		v.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});

		// allView = inflater.inflate(R.layout.somain_find_addr, null);

		allView = new CategorySelectListView(mActivity);
		allView.setCategorySelectAdapter(this);
		initAllview();

		ScrollHPageWithTableView scrollHPageWithTableView = (ScrollHPageWithTableView) findViewById(R.id.scrollHPageWithTable);
		scrollHPageWithTableView.setScrollHPageWithTableAdapter(this);

	}

	@Override
	public int getPageCount() {
		return 2;
	}

	@Override
	public String getTableString(int index) {
		String table = "";
		if (index == 0) {
			table = "所有分类";
		} else {
			table = "猜你喜欢";
		}
		return table;
	}

	@Override
	public View getPageView(int index) {
		View v = null;
		if (index == 0) {
			v = allView;
		} else if (index == 1) {
			v = appointView;
		}
		return v;
	}

	@Override
	public void onPageChange(int index) {
		
	}

	void initAllview() {
		
	}

	void resetView() {
		mActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {

				titleName.setText("地点");
			}
		});
	}

	ArrayList<JSONObject> allViewDataList;

	void testData1() {
		try {
			allViewDataList = new ArrayList<JSONObject>();
			allViewDataList.add(new JSONObject("{'name':'公园'}"));
			allViewDataList.add(new JSONObject("{'name':'商场'}"));
			allViewDataList.add(new JSONObject("{'name':'广场'}"));
			allViewDataList.add(new JSONObject("{'name':'体育场'}"));
			allViewDataList.add(new JSONObject("{'name':'展览馆'}"));
			allViewDataList.add(new JSONObject("{'name':'博物馆'}"));
			allViewDataList.add(new JSONObject("{'name':'健身房'}"));
			allViewDataList.add(new JSONObject("{'name':'温泉'}"));
			allViewDataList.add(new JSONObject("{'name':'度假村'}"));
			allViewDataList.add(new JSONObject("{'name':'超市'}"));
			allViewDataList.add(new JSONObject("{'name':'电影院'}"));
			allViewDataList.add(new JSONObject("{'name':'火车站'}"));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	ArrayList<JSONObject> allViewDataListItemContent;

	void testData2(int index) {
		try {
			allViewDataListItemContent = new ArrayList<JSONObject>();
			allViewDataListItemContent.add(new JSONObject("{'name':'" + index
					+ ".劳动公园'}"));
			allViewDataListItemContent.add(new JSONObject("{'name':'" + index
					+ ".劳动公园'}"));
			allViewDataListItemContent.add(new JSONObject("{'name':'" + index
					+ ".动物公园'}"));
			allViewDataListItemContent.add(new JSONObject("{'name':'" + index
					+ ".五一公园'}"));
			allViewDataListItemContent.add(new JSONObject("{'name':'" + index
					+ ".主题公园'}"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	void initAppointview() {

	}

	@Override
	public void getCategory() {
//		testData1();
//		allView.updateCategory(allViewDataList);
		RequestHelper.DownBack back = new RequestHelper.DownBack(){

			@Override
			public void onFinish(Pdtask t) {
				ArrayList<JSONObject> list = StrUtil.getJSONArrayList(t.json);
				allView.updateCategory(list);
			}

			@Override
			public void onUpdate(Pdtask t) {
				onFinish(t);
			}
			
		};
		Pdtask task = new Pdtask(this, back, Constants.SERVICE_HOST, null,
				RequestHelper.Type_DownJsonString, null, 0, true);
		task.addParam("method", "listLocationParentCategory");
		SOApplication.getDownLoadManager().startTask(task);

	}

	@Override
	public String getCategoryName(int index, JSONObject categoryJson) {
		if(categoryJson!=null){
			return categoryJson.optString("name");
		}
		return "getCategoryName";
	}

	@Override
	public void getCategoryChildList(final int index, final JSONObject categoryJson) {
//		testData2(index);
//		allView.updateCategoryChild(index, categoryJson,
//				allViewDataListItemContent);
//		
		RequestHelper.DownBack back = new RequestHelper.DownBack(){

			@Override
			public void onFinish(Pdtask t) {
				ArrayList<JSONObject> list = StrUtil.getJSONArrayList(t.json);
				allView.updateCategoryChild(index, categoryJson,
						list);
			}
			@Override
			public void onUpdate(Pdtask t) {
				onFinish(t);
			}
		};
		Pdtask task = new Pdtask(this, back, Constants.SERVICE_HOST, null,
				RequestHelper.Type_DownJsonString, null, 0, true);
		task.addParam("method", "listLocationChildCategory");
		task.addParam("id", String.valueOf(categoryJson.optInt("id")));
		SOApplication.getDownLoadManager().startTask(task);
	}

	@Override
	public String getCategoryChildName(int index, JSONObject categoryJson,
			int indexChild, JSONObject childJson) {
		if(childJson!=null){
			return childJson.optString("name");
		}
		return "getCategoryChildName";
	}

	@Override
	public void onChildSelect(int index, JSONObject categoryJson,
			int indexChild, JSONObject childJson) {
		Intent mIntent = new Intent(mActivity, HDAddressListActivity.class);
		Bundle mBundle = new Bundle();
		HDAddressListActivity.HDInfo info = new HDAddressListActivity.HDInfo();
		info.parentJson = (new JSONObject()).toString();
		mBundle.putSerializable(HDAddressListActivity.HDInfo.key, info);
		mIntent.putExtras(mBundle);
		mActivity.startActivity(mIntent);
	}
}
