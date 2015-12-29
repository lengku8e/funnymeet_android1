package com.mtcent.funnymeet.ui.activity.my.clubconsole;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.mtcent.funnymeet.SOApplication;
import com.mtcent.funnymeet.config.Constants;
import com.mtcent.funnymeet.model.FakeMemberInfo;
import com.mtcent.funnymeet.ui.activity.base.BaseActivity;
import com.mtcent.funnymeet.ui.helper.RequestHelper;
import com.mtcent.funnymeet.ui.view.dialog.CustomDialog;
import com.mtcent.funnymeet.ui.helper.RequestHelper.Pdtask;
import com.mtcent.funnymeet.ui.view.control.IndexableListView;
import com.mtcent.funnymeet.ui.view.control.LetterImageView;
import com.mtcent.funnymeet.util.StringMatcher;
import com.mtcent.funnymeet.util.StrUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

import mtcent.funnymeet.R;

public class MyClubAssignAssitantActivity extends BaseActivity {

	public static final String ADMIN_TYPE_ADMIN = "管理员";
	public static final String ADMIN_TYPE_ASSISTANT = "助理";

	IndexableListView indexableListView;
	ArrayList<FakeMemberInfo> utilList;
	LayoutInflater inflater;
	TextView titleTextView;
	LinkedList<JSONObject> selectedList = new LinkedList<JSONObject>();
	Handler handler;
	TextView finishButton;
	CustomDialog dialog;
	LinearLayout my_clubs_dialog_frame;
	EditText my_clubs_invite_member_addtion;
	TextView my_clubs_invite_sms_cancel;
	TextView my_clubs_invite_sms_confirm;
	Intent get_intent;
	String iconUrl;
	String clubName;
	TextView check_status;
	JSONObject jsonObject;
	AssignAssitantListViewAdapter adapter;
	JSONArray clubMemberInWaitingList;
	private LinearLayout emptyPlacehold;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_clubs_invite_member_contact_list);
		init();
		requestData();
	}

	void showSmsDialog() {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				check_status.setText("你正在对" + selectedList.size() + "名名会员指定助理");
				dialog.show();

			}
		});
	}

	void hideSmsDialog() {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				dialog.hide();
			}
		});
	}

	@Override
	public void onFinish(Pdtask t) {
		boolean succ = false;
		String msg = "";
		if (t.getParam("method").equals("listValidClubMember")) {
			if (t.json != null) {
				if (t.json.optString("status").equals("ok")
						&& t.json.optJSONArray("results") != null) {
					clubMemberInWaitingList = t.json.optJSONArray("results");
					succ = true;
				}
			}

		} else if (t.getParam("method").equals("setUsersRoleIdForClub")) {
			// setUsersRoleIdForClub
			// approveClubMemberInView
			msg = getString(R.string.message_set_role_ok).toString();
		}
		if (succ) {
			setViewContent();
			if (msg != null && msg.length() > 0) {
				StrUtil.showMsg(MyClubAssignAssitantActivity.this, msg);
			}
		}
		hideWait();
		super.onFinish(t);
	}

	protected void setViewContent() {

		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				adapter = new AssignAssitantListViewAdapter();
				adapter.setContactNameList(clubMemberInWaitingList);
				adapter.setInflater(inflater);
				indexableListView.setAdapter(adapter);
			}
		});

	}

	void commit(String roleId) {

		Pdtask task = new Pdtask(this, this, Constants.SERVICE_HOST, null,
				RequestHelper.Type_PostParam, null, 0, true);

		task.addParam("method", "setUsersRoleIdForClub");

		StringBuffer sb = new StringBuffer();
		for (Iterator<JSONObject> iter = selectedList.iterator(); iter
				.hasNext();) {

			JSONObject jo = iter.next();
			String user_guid = jo.optString("userGuid");
			if (sb.length() > 0) {
				sb.append(",");
			}
			sb.append(user_guid);

		}
		task.addParam("user_guids", sb.toString());
		// task.addParam("user_guid", jsonObject.optString("userGuid"));
		task.addParam("club_guid", jsonObject.optString("guid"));
		task.addParam("role_id", roleId);
		SOApplication.getDownLoadManager().startTask(task);
		showWait();
	}

	void requestData() {
		Pdtask task = new Pdtask(this, this, Constants.SERVICE_HOST, null,
				RequestHelper.Type_PostParam, null, 0, true);

		task.addParam("method", "listValidClubMember");
		task.addParam("club_guid", jsonObject.optString("guid"));
		SOApplication.getDownLoadManager().startTask(task);
		showWait();
	}

	protected void init() {
		// 隐藏填写手机号的项目
		LinearLayout inputphoneLayout = (LinearLayout) findViewById(R.id.my_club_check_member_inputphone);
		inputphoneLayout.setVisibility(View.GONE);
		TextView tvSeparator = (TextView)findViewById(R.id.my_club_check_member_separator);
		tvSeparator.setVisibility(View.GONE);
		
		indexableListView = (IndexableListView) findViewById(R.id.phone_contact_list);

		emptyPlacehold = (LinearLayout) findViewById(R.id.empty);
		indexableListView.setEmptyView(emptyPlacehold);

		get_intent = this.getIntent();

		try {
			jsonObject = new JSONObject(get_intent.getStringExtra("jsonObject"));
		} catch (JSONException e) {
		}

		if (jsonObject != null) {

		}

		handler = new Handler();
		View v = findViewById(R.id.left_backLayout);
		v.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});

		finishButton = (TextView) findViewById(R.id.finishbutton);
		finishButton.setVisibility(View.VISIBLE);
		finishButton.setBackgroundResource(R.drawable.green_btn_disable);

		finishButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (selectedList.size() > 0) {
					// showSmsDialog();

					if (dialog == null) {
						dialog = new CustomDialog(
								MyClubAssignAssitantActivity.this);
						dialog.setContentView(R.layout.my_club_admin_type_dialog);
						dialog.setCancelable(true);
						dialogfunction();
					}
					dialog.show();
				}
			}
		});

		titleTextView = (TextView) findViewById(R.id.titleTextView);
		titleTextView.setText("指定助理");

		inflater = (LayoutInflater) MyClubAssignAssitantActivity.this
				.getSystemService(LAYOUT_INFLATER_SERVICE);

		indexableListView.setFastScrollEnabled(true);

		indexableListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				TextView phone_contact_be_isselected = (TextView) arg1
						.findViewById(R.id.phone_contact_be_isselected);
				Tag get_tag = (Tag) arg1.getTag();
				if (get_tag.isselected == 0) {

					phone_contact_be_isselected
							.setBackgroundResource(R.drawable.selecter_selected_icon);
					JSONObject ci = (JSONObject) indexableListView
							.getItemAtPosition(arg2);

					selectedList.add(ci);
					get_tag.isselected++;
					changeFinishButtonStatus();
				} else if (get_tag.isselected == 1) {
					JSONObject ci = (JSONObject) indexableListView
							.getItemAtPosition(arg2);
					selectedList.remove(ci);
					phone_contact_be_isselected
							.setBackgroundResource(R.drawable.selecter_unselected_icon);
					get_tag.isselected--;
					changeFinishButtonStatus();
				}

			}
		});
	}

	private void dialogfunction() {

		LinearLayout adminTypeAdmin = (LinearLayout) dialog
				.findViewById(R.id.admin_type_admin);
		LinearLayout adminTypeAssistant = (LinearLayout) dialog
				.findViewById(R.id.admin_type_assistant);
		adminTypeAdmin.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				commit(getString(R.string.member_role_id_admin).toString());
				// feeSelected.setVisibility(View.VISIBLE);
				// freeSelected.setVisibility(View.GONE);
				// club_hd_fee_type.setText(FEE_TYPE_FEE);
				dialog.dismiss();
				finish();
			}
		});

		adminTypeAssistant.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				commit(getString(R.string.member_role_id_assistant).toString());
				// freeSelected.setVisibility(View.VISIBLE);
				// feeSelected.setVisibility(View.GONE);
				// club_hd_fee_type.setText(FEE_TYPE_FREE);
				dialog.dismiss();
				finish();
			}
		});

	}

	void showAdminTypeDialog() {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				dialog.show();
			}
		});
	}

	void hideAdminTypeDialog() {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				dialog.hide();
			}
		});
	}

	private void changeFinishButtonStatus() {

		handler.post(new Runnable() {

			@Override
			public void run() {
				if (selectedList.size() == 0) {
					finishButton
							.setBackgroundResource(R.drawable.green_btn_disable);
					finishButton.setText("完成");
				} else {
					finishButton
							.setBackgroundResource(R.drawable.green_btn_style);
					finishButton.setText("完成" + "(" + selectedList.size() + ")");
				}

			}
		});
	}
}

@SuppressLint("InflateParams")
class AssignAssitantListViewAdapter extends BaseAdapter implements
		SectionIndexer {

	private String mSections = "#ABCDEFGHIJKLMNOPQRSTUVWXYZ";

	JSONArray contactNameList;
	LayoutInflater inflater;

	public void setContactNameList(JSONArray contactNameList) {
		this.contactNameList = contactNameList;
	}

	public void setInflater(LayoutInflater inflater) {
		this.inflater = inflater;
	}

	@Override
	public int getCount() {
		return contactNameList.length();
	}

	@Override
	public Object getItem(int position) {
		if (position < 0 || position >= getCount()) {
			return null;
		} else {
			return contactNameList.optJSONObject(position);
		}

	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Tag tag = null;
		if (convertView == null) {
			tag = new Tag();
			convertView = inflater.inflate(
					R.layout.my_clubs_invite_member_contact_list_item, null);
			tag.letterImageView = (LetterImageView) convertView
					.findViewById(R.id.letterImageView);
			tag.phone_contact_be_isselected = (TextView) convertView
					.findViewById(R.id.phone_contact_be_isselected);
			tag.phoneContactName = (TextView) convertView
					.findViewById(R.id.phoneContactName);
			tag.isselected = 0;
			convertView.setTag(tag);

		} else {
			tag = (Tag) convertView.getTag();
		}

		JSONObject jo = (JSONObject) getItem(position);
		ContactInfo ci = new ContactInfo(jo.optString("nickname"), "", "");
		tag.letterImageView.setLetter(ci.getFirstLetter().charAt(0));
		tag.phone_contact_be_isselected
				.setBackgroundResource(R.drawable.selecter_unselected_icon);
		tag.phoneContactName.setText(ci.getContactName());
		return convertView;
	}

	@Override
	public int getPositionForSection(int section) {
		// If there is no item for current section, previous section
		// will be
		// selected
		for (int i = section; i >= 0; i--) {
			for (int j = 0; j < getCount(); j++) {
				if (i == 0) {
					// For numeric section
					for (int k = 0; k <= 9; k++) {
						JSONObject jo = (JSONObject) getItem(j);
						ContactInfo ci = new ContactInfo(
								jo.optString("nickname"), "", "");
						if (StringMatcher.match(
								String.valueOf(ci.getFirstLetter()),
								String.valueOf(k)))
							return j;
					}
				} else {
					JSONObject jo = (JSONObject) getItem(j);
					ContactInfo ci = new ContactInfo(
							jo.optString("nickname"), "", "");
					if (StringMatcher.match(
							String.valueOf(ci.getFirstLetter()),
							String.valueOf(mSections.charAt(i))))
						return j;
				}
			}
		}
		return 0;
	}

	@Override
	public int getSectionForPosition(int position) {
		return position;
	}

	@Override
	public Object[] getSections() {
		String[] sections = new String[mSections.length()];
		for (int i = 0; i < mSections.length(); i++)
			sections[i] = String.valueOf(mSections.charAt(i));
		return sections;
	}

}
