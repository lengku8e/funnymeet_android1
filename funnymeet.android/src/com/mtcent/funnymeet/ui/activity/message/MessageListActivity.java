package com.mtcent.funnymeet.ui.activity.message;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.mtcent.funnymeet.SOApplication;
import com.mtcent.funnymeet.config.Constants;
import com.mtcent.funnymeet.ui.activity.base.BaseActivity;
import com.mtcent.funnymeet.ui.helper.RequestHelper;
import com.mtcent.funnymeet.ui.view.fragment.ConfirmDialogFragment;
import com.mtcent.funnymeet.ui.view.fragment.ConfirmDialogFragment.MessageInputListener;
import com.mtcent.funnymeet.ui.helper.RequestHelper.Pdtask;
import com.mtcent.funnymeet.ui.helper.UserMangerHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import mtcent.funnymeet.R;

public class MessageListActivity extends BaseActivity implements
		MessageInputListener {

	public static final int ID = MessageListActivity.class.hashCode();
	
	private TextView titleTextView;
	private LinearLayout mMyInviteMessage;
	private JSONArray mInvitingClubs;
	private PullToRefreshListView mPullToRefreshListView;

	// JSONArray mMessageListSource;

	private BaseAdapter adapter;
	private LayoutInflater inflater;

	private ListView realListView;

	private TextView mMyMessageTitle;
	private TextView mMyMessageBrief;
	private JSONObject mOnClickedClub;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.message_list);
		init();
		requestData();
	}

	protected void init() {

		mInvitingClubs = new JSONArray();

		// mMyMessageTitle = (TextView) findViewById(R.id.my_message_title);
		// mMyMessageBrief = (TextView) findViewById(R.id.my_message_brief);

		View v = findViewById(R.id.left_backLayout);
		v.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});

		mPullToRefreshListView = (PullToRefreshListView) findViewById(R.id.mPullToRefreshListView);

		mPullToRefreshListView
				.setOnRefreshListener(new OnRefreshListener<ListView>() {
					@Override
					public void onRefresh(
							PullToRefreshBase<ListView> refreshView) {
						// String label = DateUtils.formatDateTime(
						// getApplicationContext(),
						// System.currentTimeMillis(),
						// DateUtils.FORMAT_SHOW_TIME
						// | DateUtils.FORMAT_SHOW_DATE
						// | DateUtils.FORMAT_ABBREV_ALL);
						//
						// refreshView.getLoadingLayoutProxy()
						// .setLastUpdatedLabel(label);
						//
						// requestData();
					}
				});

		inflater = (LayoutInflater) MessageListActivity.this
				.getSystemService(LAYOUT_INFLATER_SERVICE);

		realListView = mPullToRefreshListView.getRefreshableView();
		// realListView.addHeaderView(headerView);

		realListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				mOnClickedClub = (JSONObject) adapter.getItem((int) arg3);
//				AlertDialog.Builder builder = new AlertDialog.Builder(
//						MessageListActivity.this).setTitle("要加入俱乐部吗?")
//						.setMessage("接受俱乐部的邀请吗?");
//				builder.setPositiveButton("确定",
//						new DialogInterface.OnClickListener() {
//							public void onClick(DialogInterface dialog,
//									int which) {
//
//							}
//						});
//				builder.create().show();
				ConfirmDialogFragment dialog = new ConfirmDialogFragment();
		        dialog.show(getFragmentManager(), "confirminvitaion");  
				// intent.putExtra("jsonobject", fi.toString());
				// intent.putExtra("groupName", fi.optString("clubName"));
				// intent.setClass(MessageListActivity.this,
				// MyClubManagementActivity.class);
				// startActivityForResult(intent,
				// MyClubListActivity.TO_MYCLUBMANAGERMENTACTIVITY);
			}
		});
		adapter = new BaseAdapter() {

			class Tag {

				ImageView my_clubs_clubicon;
				TextView my_clubs_clubname;
			}

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				// TODO Auto-generated method stub

				Tag tag = null;
				if (convertView == null) {

					tag = new Tag();
					convertView = inflater.inflate(
							R.layout.my_clubs_child_layout, null);
					tag.my_clubs_clubicon = (ImageView) convertView
							.findViewById(R.id.my_clubs_clubicon);
					tag.my_clubs_clubname = (TextView) convertView
							.findViewById(R.id.my_clubs_clubname);
					convertView.setTag(tag);
				} else {
					tag = (Tag) convertView.getTag();
				}

				JSONObject fi = (JSONObject) getItem(position);
				// tag.my_clubs_clubicon.setImageUrl(fi.optString());
				tag.my_clubs_clubname.setText("俱乐部【" + fi.optString("name")
						+ "】邀请您加入。");

				return convertView;
			}

			@Override
			public long getItemId(int position) {
				// TODO Auto-generated method stub
				return position;
			}

			@Override
			public Object getItem(int position) {
				// TODO Auto-generated method stub
				return mInvitingClubs.optJSONObject(position);
			}

			@Override
			public int getCount() {
				// TODO Auto-generated method stub
				return mInvitingClubs.length();
			}
		};

		realListView.setAdapter(adapter);

		titleTextView = (TextView) findViewById(R.id.titleTextView);
		titleTextView.setText("查看收到的消息");

	}

	@Override
	public void onFinish(Pdtask t) {
		// TODO Auto-generated method stub

		boolean succ = false;
		if (t.getParam("method").equals("listInvitingClubs")) {
			if (t.json != null && "ok".equals(t.json.optString("status"))) {
				if (t.json.optJSONArray("results") != null) {
					mInvitingClubs = t.json.optJSONArray("results");
					succ = true;
				}
			}
		} else if ("responseClubInvitation".equals(t.getParam("method"))) {
			if (t.json != null && "ok".equals(t.json.optString("status"))) {
				requestData();
				//succ = true;
			}
		}

		if (succ) {
			setViewContent();
		}
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
		task.addParam("method", "listInvitingClubs");
		task.addParam("mobile_phone", UserMangerHelper.getDefaultUserPhone());

		SOApplication.getDownLoadManager().startTask(task);
		showWait();

	}

	@Override
	public void onMessageInputComplete(String result) {
		String message ;
		if (ConfirmDialogFragment.CONFIRM_RESULT_NO.equals(result)) {
			message = "拒绝俱乐部邀请成功，消息不再提示。";
		} else {
			message = "恭喜你成为【" + mOnClickedClub.optString("name") + "】俱乐部的成员。";
		}
		Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
		Pdtask task = new Pdtask(this, this, Constants.SERVICE_HOST, null,
				RequestHelper.Type_PostParam, null, 0, true);
		task.addParam("method", "responseClubInvitation");
		task.addParam("user_guid", UserMangerHelper.getDefaultUserGuid());
		task.addParam("club_guid", mOnClickedClub.optString("guid"));
		task.addParam("response_result", result);

		SOApplication.getDownLoadManager().startTask(task);
		showWait();
	}

}
