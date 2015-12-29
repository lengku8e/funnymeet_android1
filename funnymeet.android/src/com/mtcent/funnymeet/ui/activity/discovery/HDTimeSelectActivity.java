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

public class HDTimeSelectActivity extends Activity implements
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
		title = "时间";
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

				titleName.setText("时间");
			}
		});
	}

	ArrayList<JSONObject> allViewDataList;

	void testData1() {
		try {
			allViewDataList = new ArrayList<JSONObject>();
			allViewDataList.add(new JSONObject("{'name':'最近'}"));
			allViewDataList.add(new JSONObject("{'name':'周末'}"));
			allViewDataList.add(new JSONObject("{'name':'节假日'}"));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	ArrayList<JSONObject> allViewDataListItemContent;

	void testData2(int index) {
		try {
			allViewDataListItemContent = new ArrayList<JSONObject>();
			allViewDataListItemContent.add(new JSONObject("{'name':'" + index
					+ ".周一到周五'}"));
			allViewDataListItemContent.add(new JSONObject("{'name':'" + index
					+ ".周末'}"));
			allViewDataListItemContent.add(new JSONObject("{'name':'" + index
					+ "春节'}"));
			allViewDataListItemContent.add(new JSONObject("{'name':'" + index
					+ ".国庆节'}"));
			allViewDataListItemContent.add(new JSONObject("{'name':'" + index
					+ ".劳动节'}"));
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
		task.addParam("method", "listProjectDateParentCategory");
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
		task.addParam("method", "listProjectDateChildCategory");
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
		Intent mIntent = new Intent(mActivity, HDTimeListActivity.class);
		Bundle mBundle = new Bundle();
		HDTimeListActivity.HDInfo info = new HDTimeListActivity.HDInfo();
		info.parentJson = (new JSONObject()).toString();
		mBundle.putSerializable(HDTimeListActivity.HDInfo.key, info);
		mIntent.putExtras(mBundle);
		mActivity.startActivity(mIntent);
	}
}
