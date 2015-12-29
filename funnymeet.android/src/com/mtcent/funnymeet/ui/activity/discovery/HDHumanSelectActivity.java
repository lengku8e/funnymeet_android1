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

public class HDHumanSelectActivity extends Activity implements
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
		title = "人物";
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

				titleName.setText("人物");
			}
		});
	}

	ArrayList<JSONObject> allViewDataList;

	void testData1() {
		try {
			allViewDataList = new ArrayList<JSONObject>();
			allViewDataList.add(new JSONObject("{'name':'演员'}"));
			allViewDataList.add(new JSONObject("{'name':'运动员'}"));
			allViewDataList.add(new JSONObject("{'name':'政要'}"));
			allViewDataList.add(new JSONObject("{'name':'模特'}"));
			allViewDataList.add(new JSONObject("{'name':'歌手'}"));
			allViewDataList.add(new JSONObject("{'name':'架构师'}"));
			allViewDataList.add(new JSONObject("{'name':'产品经理'}"));
			allViewDataList.add(new JSONObject("{'name':'商业领袖'}"));
			allViewDataList.add(new JSONObject("{'name':'开源组织'}"));
			allViewDataList.add(new JSONObject("{'name':'基金会'}"));
			allViewDataList.add(new JSONObject("{'name':'劳动模范'}"));
			allViewDataList.add(new JSONObject("{'name':'主持人 '}"));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	ArrayList<JSONObject> allViewDataListItemContent;

	void testData2(int index) {
		try {
			allViewDataListItemContent = new ArrayList<JSONObject>();
			allViewDataListItemContent.add(new JSONObject("{'name':'" + index
					+ ".汤唯'}"));
			allViewDataListItemContent.add(new JSONObject("{'name':'" + index
					+ ".刘德华'}"));
			allViewDataListItemContent.add(new JSONObject("{'name':'" + index
					+ ".马化腾'}"));
			allViewDataListItemContent.add(new JSONObject("{'name':'" + index
					+ ".奥巴马'}"));
			allViewDataListItemContent.add(new JSONObject("{'name':'" + index
					+ ".梅西'}"));
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
		task.addParam("method", "listPersonParentCategory");
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
	public void getCategoryChildList(final int index,final JSONObject categoryJson) {
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
		task.addParam("method", "listPersonChildCategory");
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
		Intent mIntent = new Intent(mActivity, HDHumanListActivity.class);
		Bundle mBundle = new Bundle();
		HDHumanListActivity.HDInfo info = new HDHumanListActivity.HDInfo();
		info.parentJson = (new JSONObject()).toString();
		mBundle.putSerializable(HDHumanListActivity.HDInfo.key, info);
		mIntent.putExtras(mBundle);
		mActivity.startActivity(mIntent);
	}
}
