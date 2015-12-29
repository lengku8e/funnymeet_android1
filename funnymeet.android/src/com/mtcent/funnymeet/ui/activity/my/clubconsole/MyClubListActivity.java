package com.mtcent.funnymeet.ui.activity.my.clubconsole;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.mtcent.funnymeet.SOApplication;
import com.mtcent.funnymeet.config.Constants;
import com.mtcent.funnymeet.ui.activity.base.BaseActivity;
import com.mtcent.funnymeet.ui.helper.RequestHelper;
import com.mtcent.funnymeet.ui.helper.RequestHelper.Pdtask;
import com.mtcent.funnymeet.ui.helper.UserMangerHelper;
import com.mtcent.funnymeet.ui.view.control.XVURLImageView;

import org.json.JSONArray;
import org.json.JSONObject;

import mtcent.funnymeet.R;

@SuppressLint("InflateParams")
public class MyClubListActivity extends BaseActivity {

	public static final int ID = MyClubListActivity.class.hashCode();

	PullToRefreshListView mPullToRefreshListView;

	JSONArray myClubListSource;

	BaseAdapter adapter;
	LayoutInflater inflater;

	ListView realListView;
	TextView titleTextView;

	public static final int TO_MYCLUBMANAGERMENTACTIVITY = 1609;
	public static final int TO_CREATENEWCLUB = 1717;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_myclubs);
		init();
		requestData();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == TO_MYCLUBMANAGERMENTACTIVITY) {
			if (resultCode == MyClubManagementActivity.CLOSE_CLUB_REFRESH_CLUBLIST) {
				requestData();
			}
		} else if (requestCode == TO_CREATENEWCLUB) {

			requestData();
		}
	}

	protected void init() {

		myClubListSource = new JSONArray();

		View v = findViewById(R.id.left_backLayout);
		v.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});

//		LinearLayout club_search_button = (LinearLayout) findViewById(R.id.club_search_button);
//		club_search_button.setVisibility(View.VISIBLE);
//		club_search_button.setOnClickListener(new View.OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				Intent intent = new Intent();
//				intent.setClass(MyClubListActivity.this, SearchActivity.class);
//				startActivity(intent);
//			}
//		});

		mPullToRefreshListView = (PullToRefreshListView) findViewById(R.id.mPullToRefreshListView);

		mPullToRefreshListView
				.setOnRefreshListener(new OnRefreshListener<ListView>() {
					@Override
					public void onRefresh(
							PullToRefreshBase<ListView> refreshView) {
						String label = DateUtils.formatDateTime(
								getApplicationContext(),
								System.currentTimeMillis(),
								DateUtils.FORMAT_SHOW_TIME
										| DateUtils.FORMAT_SHOW_DATE
										| DateUtils.FORMAT_ABBREV_ALL);

						refreshView.getLoadingLayoutProxy()
								.setLastUpdatedLabel(label);

						requestData();
//						new GetDataTask().execute();  
					}
				});

		titleTextView = (TextView) findViewById(R.id.titleTextView);
		titleTextView.setText("俱乐部管理");

		v = findViewById(R.id.newClubs);
		v.setVisibility(View.VISIBLE);
		v.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(MyClubListActivity.this, MyCreateClubTypeActivity.class);

				startActivityForResult(intent, TO_CREATENEWCLUB);
				// finish();
			}
		});

		inflater = (LayoutInflater) MyClubListActivity.this
				.getSystemService(LAYOUT_INFLATER_SERVICE);

		// View headerView = inflater.inflate(R.layout.my_clubs_search, null);

		realListView = mPullToRefreshListView.getRefreshableView();
		// realListView.addHeaderView(headerView);

		realListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Intent intent = new Intent();
				JSONObject fi = (JSONObject) adapter.getItem((int) arg3);
				intent.putExtra("jsonobject", fi.toString());
				intent.putExtra("groupName", fi.optString("clubName"));
				intent.setClass(MyClubListActivity.this, MyClubManagementActivity.class);
				startActivityForResult(intent,
						MyClubListActivity.TO_MYCLUBMANAGERMENTACTIVITY);
			}
		});

		adapter = new BaseAdapter() {

			class Tag {

				XVURLImageView my_clubs_clubicon;
				TextView my_clubs_clubname;
			}

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				Tag tag = null;
				if (convertView == null) {

					tag = new Tag();
					convertView = inflater.inflate(
							R.layout.my_clubs_child_layout, null);
					tag.my_clubs_clubicon = (XVURLImageView) convertView
							.findViewById(R.id.my_clubs_clubicon);
					tag.my_clubs_clubname = (TextView) convertView
							.findViewById(R.id.my_clubs_clubname);
					convertView.setTag(tag);
				} else {
					tag = (Tag) convertView.getTag();
				}

				JSONObject fi = (JSONObject) getItem(position);
				// tag.my_clubs_clubicon.setImageUrl(fi.optString());
				tag.my_clubs_clubname.setText(fi.optString("clubName"));
				String logoUrl = fi.optString("logoUrl");
				if (logoUrl != null & logoUrl.length() > 10) {
					tag.my_clubs_clubicon.setImageUrl(logoUrl);
				} else {
					//
				}

				return convertView;
			}

			@Override
			public long getItemId(int position) {
				return position;
			}

			@Override
			public Object getItem(int position) {
				return myClubListSource.optJSONObject(position);
			}

			@Override
			public int getCount() {
				return myClubListSource.length();
			}
		};

		realListView.setAdapter(adapter);

	}

	@Override
	public void onFinish(Pdtask t) {
		boolean succ = false;
		if (t.getParam("method").equals("listUserAdminClub")) {

			JSONObject js = t.json;

			if (t.json != null) {

				if (t.json.optJSONArray("results") != null) {
					myClubListSource = t.json.optJSONArray("results");
//					setViewContent();
					succ = true;
				}
			}
		}
		//if (succ) {
			setViewContent();

		//}
		hideWait();
		super.onFinish(t);

	}

	protected void setViewContent() {

		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				adapter.notifyDataSetChanged();
				mPullToRefreshListView.onRefreshComplete();
			}
		});

	}

	void requestData() {

		Pdtask task = new Pdtask(this, this, Constants.SERVICE_HOST, null,
				RequestHelper.Type_PostParam, null, 0, true);
		task.addParam("method", "listUserAdminClub");// 页码
		task.addParam("user_guid", UserMangerHelper.getDefaultUserGuid());
		task.addParam("user_session_guid",
				UserMangerHelper.getDefaultUserLongsession());

		SOApplication.getDownLoadManager().startTask(task);
		showWait();
	}
	
//	private class GetDataTask extends AsyncTask<Void, Void, String> {
//
//		//后台处理部分
//		@Override
//		protected String doInBackground(Void... params) {
//			// Simulates a background job.
//			try {
//				Thread.sleep(1000);
//			} catch (InterruptedException e) {
//			}
//			String str="Added after refresh...I add";
//			return str;
//		}
//
//		//这里是对刷新的响应，可以利用addFirst（）和addLast()函数将新加的内容加到LISTView中
//		//根据AsyncTask的原理，onPostExecute里的result的值就是doInBackground()的返回值
//		@Override
//		protected void onPostExecute(String result) {
//			//在头部增加新添内容
//			mListItems.addFirst(result);
//			
//			//通知程序数据集已经改变，如果不做通知，那么将不会刷新mListItems的集合
//			mAdapter.notifyDataSetChanged();
//			// Call onRefreshComplete when the list has been refreshed.
//			mPullRefreshListView.onRefreshComplete();
//
//			super.onPostExecute(result);
//		}
//	}
}
