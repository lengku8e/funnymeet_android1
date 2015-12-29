package com.mtcent.funnymeet.ui.view.control;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.mtcent.funnymeet.SOApplication;
import com.mtcent.funnymeet.config.Constants;
import com.mtcent.funnymeet.ui.activity.club.FindNewClub2Activity;
import com.mtcent.funnymeet.ui.activity.my.clubconsole.MyClubManagementActivity;
import com.mtcent.funnymeet.ui.helper.RequestHelper;
import com.mtcent.funnymeet.ui.helper.RequestHelper.Pdtask;
import com.mtcent.funnymeet.ui.helper.UserMangerHelper;
import com.mtcent.funnymeet.util.StrUtil;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Comparator;
import java.util.Timer;
import java.util.TimerTask;

import mtcent.funnymeet.R;

@SuppressLint({ "ClickableViewAccessibility", "InflateParams", "DefaultLocale" })
public class FunnymeetAddressBookView extends FunnymeetBaseView {

	ListView addressBookListView;
	View headView;
	View footView;
	View listFrame;
	LinearLayout addressBookRightList;
	TextView addressbook_index;
	MyAdapter adapter;
	LinearLayout newClubs;

	AlphaAnimation letterAnimation;
	JSONArray focusedClubList;
	
	Timer timer = new Timer();

	public FunnymeetAddressBookView(Activity activity) {
		super(activity);
		init();
		requestFavoriteClubList();
		mainView = listFrame;
		timer.schedule(task, 1000, 1000);
	}

	TimerTask task = new TimerTask() {  
		  
        @Override  
        public void run() {  
        	if (SOApplication.isClubUpdated()) {
                onShow(); 
        	}
        }  
    };  
	public void requestFavoriteClubList() {

		Pdtask task = new Pdtask(this, this, Constants.SERVICE_HOST, null,
				RequestHelper.Type_PostParam, null, 0, true);
		task.addParam("method", "listUserClub");
		task.addParam("user_guid", UserMangerHelper.getDefaultUserGuid());
		task.addParam("user_session_guid",
				UserMangerHelper.getDefaultUserLongsession());

		SOApplication.getDownLoadManager().startTask(task);
	}

	public void handleMessageToHandled(String messageGuid) {
		Pdtask task = new Pdtask(this, this, Constants.SERVICE_HOST, null,
				RequestHelper.Type_PostParam, null, 0, true);
		task.addParam("method", "handleMessageToHandled");
		task.addParam("message_guid", messageGuid);

		SOApplication.getDownLoadManager().startTask(task);
	}

	@Override
	public void onFinish(Pdtask t) {
		boolean succ = false;
		if (t.getParam("method").equals("listUserClub")) {
			if (t.json != null) {
				focusedClubList = t.json.optJSONArray("results");
				succ = true;
			}
		}

		if (succ) {
			mActivity.runOnUiThread(new Runnable() {

				@Override
				public void run() {
					adapter.notifyDataSetChanged();
					addressBookListView.refreshDrawableState();
					
					TextView totalFriends = (TextView) footView
							.findViewById(R.id.totalFriends);
					totalFriends.setText(focusedClubList.length() + "个俱乐部");
				}
			});
		}
		super.onFinish(t);
	}

	public void onShow() {
		if (SOApplication.isClubUpdated()) {
			//StrUtil.showMsg(mActivity, "俱乐部信息有最新更新,正在为您刷新俱乐部列表。");
			requestFavoriteClubList();
			SOApplication.setClubUpdated(false);
			JSONArray messages = SOApplication.getUnhandledMessages();
			if (messages == null) {
				return;
			}
			for (int i = 0; i < messages.length(); i++) {
				JSONObject message = null;
				try {
					message = messages.getJSONObject(i);
				} catch (JSONException e) {
					e.printStackTrace();
					continue;
				}
				String messageGuid = message.optString("messageGuid");
				handleMessageToHandled(messageGuid);
			}
		} else {
			adapter.notifyDataSetChanged();
		}
	}
	
	void init() {

		focusedClubList = new JSONArray();
		adapter = new MyAdapter();

		listFrame = inflater.inflate(R.layout.addressbook_list, null);

		headView = inflater.inflate(R.layout.addressbook_head, null);
		newClubs = (LinearLayout) headView.findViewById(R.id.newClubs);
		newClubs.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(mActivity, FindNewClub2Activity.class);
				mActivity.startActivity(intent);
			}
		});
		footView = inflater.inflate(R.layout.addressbook_foot, null);
		addressBookRightList = (LinearLayout) listFrame
				.findViewById(R.id.addressbook_rightlist);
		addressBookListView = (ListView) listFrame
				.findViewById(R.id.addressbook);
		addressbook_index = (TextView) listFrame
				.findViewById(R.id.addressbook_index);

		letterAnimation = new AlphaAnimation(1f, 1f);
		letterAnimation.setDuration(300);
		letterAnimation.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				addressbook_index.setVisibility(View.INVISIBLE);
			}
		});

		inflater.inflate(R.layout.addressbook_rightlist_item,
				addressBookRightList);
		TextView text = (TextView) addressBookRightList
				.getChildAt(addressBookRightList.getChildCount() - 1);
		text.setText("↑");

		inflater.inflate(R.layout.addressbook_rightlist_item,
				addressBookRightList);
		text = (TextView) addressBookRightList.getChildAt(addressBookRightList
				.getChildCount() - 1);
		text.setText("☆");

		char letterA = 'A';
		for (int i = 0; i < 26; i++) {

			inflater.inflate(R.layout.addressbook_rightlist_item,
					addressBookRightList);
			text = (TextView) addressBookRightList
					.getChildAt(addressBookRightList.getChildCount() - 1);
			char letter = (char) (letterA + i);
			final String textTmp = String.valueOf(letter);
			text.setText(textTmp);

		}

		inflater.inflate(R.layout.addressbook_rightlist_item,
				addressBookRightList);
		text = (TextView) addressBookRightList.getChildAt(addressBookRightList
				.getChildCount() - 1);
		text.setText("#");

		addressBookRightList
				.setOnTouchListener(new LinearLayout.OnTouchListener() {

					@Override
					public boolean onTouch(View v, MotionEvent event) {
						if (event.getAction() == MotionEvent.ACTION_DOWN
								|| event.getAction() == MotionEvent.ACTION_MOVE) {
							float dy = event.getY();
							int count = 29;
							int vh = addressBookRightList.getHeight();
							int index = (int) (dy * count / vh);
							if (count > 0) {
								int childcount = addressBookRightList
										.getChildCount();
								if (index < 0) {
									index = 0;
								}
								if (index >= childcount) {
									index = childcount - 1;
								}

								TextView t = (TextView) addressBookRightList
										.getChildAt(index);

								if (index != 0 && index != 1
										&& index != childcount - 1) {
									addressbook_index.setText(t.getText()
											.toString());

									scrllto(t.getText().toString());

									if (addressbook_index.getVisibility() != View.VISIBLE) {
										addressbook_index
												.setVisibility(View.VISIBLE);
										addressbook_index.clearAnimation();
										addressbook_index
												.startAnimation(letterAnimation);
									}
								}
							}
						}
						return true;
					}

				});

		TextView totalFriends = (TextView) footView
				.findViewById(R.id.totalFriends);
		totalFriends.setText(focusedClubList.length() + "个俱乐部");
		addressBookListView.addHeaderView(headView);
		addressBookListView.addFooterView(footView);

		addressBookListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Intent intent = new Intent();

				JSONObject f = (JSONObject) adapter.getItem((int) arg3);
				
				if (f != null) {
					intent.putExtra(MyClubManagementActivity.EXTRA_PARAM_JSONOBJECT, f.toString());
					//
					intent.putExtra(MyClubManagementActivity.EXTRA_PARAM_CLUB_TYPE, MyClubManagementActivity.CLUB_MANAGER_TYPE_USER);
					intent.setClass(mActivity, MyClubManagementActivity.class);
					mActivity.startActivity(intent);
				}

			}

		});
		addressBookListView.setAdapter(adapter);

	}

	void scrllto(String s) {
		int index = 0;
		if (s != null) {
			if (s.equals("↑")) {
				addressBookListView.setSelection(0);
			} else {

				int count = adapter.getCount();
				for (int i = 0; i < count; i++) {

					if (s.equals(StrUtil
							.getFirstLetterFromPinyin(focusedClubList
									.optJSONObject(i).optString("clubName")))) {

						index = i;

						break;
					}

				}
				addressBookListView.setSelection(index);
			}
		}

	}

	class FakeAddressInfo {

		String firstLetter;
		String nickName;
		String imageUrl;

		private String[] pinyin;

		private HanyuPinyinOutputFormat format = null;

		public FakeAddressInfo(String nickName, String imageUrl) {

			this.nickName = nickName;
			this.imageUrl = imageUrl;
			pinyin = null;

			format = new HanyuPinyinOutputFormat();
			format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);

			try

			{
				pinyin = PinyinHelper.toHanyuPinyinStringArray(
						this.nickName.charAt(0), format);
			}

			catch (BadHanyuPinyinOutputFormatCombination e)

			{
				e.printStackTrace();
			}

			// 如果c不是汉字，toHanyuPinyinStringArray会返回null

			if (pinyin == null) {
				this.firstLetter = String.valueOf(nickName.charAt(0))
						.toUpperCase();
			} else {
				this.firstLetter = String.valueOf(pinyin[0].charAt(0))
						.toUpperCase();
			}

		}

		public String getFirstLetter() {
			return firstLetter;
		}

		public String getNickName() {
			return nickName;
		}

		public String getImageUrl() {
			return imageUrl;
		}

	}

	class MyComparator implements Comparator<FakeAddressInfo> {

		@Override
		public int compare(FakeAddressInfo lhs, FakeAddressInfo rhs) {
			return lhs.getFirstLetter().compareTo(rhs.getFirstLetter());
		}

	}

	class MyAdapter extends BaseAdapter {
		class Tag {

			TextView userNickName;
			XVURLImageView userHeadImage;
			TextView addressBookDivider;
			TextView firstLetter;
			TextView addressBookItemDivider;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Tag tag = null;
			if (convertView == null) {

				tag = new Tag();
				convertView = inflater.inflate(R.layout.addressbook_list_item,
						null);

				tag.userHeadImage = (XVURLImageView) convertView
						.findViewById(R.id.userHeadImage);
				tag.firstLetter = (TextView) convertView
						.findViewById(R.id.firstLetter);
				tag.addressBookDivider = (TextView) convertView
						.findViewById(R.id.addressBookDivider);
				tag.userNickName = (TextView) convertView
						.findViewById(R.id.userNickName);
				tag.addressBookItemDivider = (TextView) convertView
						.findViewById(R.id.addressBookItemDivider);
				convertView.setTag(tag);
			} else {
				tag = (Tag) convertView.getTag();
			}

			JSONObject current_fi = (JSONObject) getItem(position);
			JSONObject pre_fi = (JSONObject) getItem(position - 1);
			JSONObject pos_fi = (JSONObject) getItem(position + 1);

			tag.userNickName.setText(current_fi.optString("clubName"));
			String logoUrl = current_fi.optString("logoUrl");
			if (logoUrl != null & logoUrl.length() > 10) {
				tag.userHeadImage.setImageUrl(logoUrl);
			} else {
				//
			}

			if (pos_fi == null) {
				tag.addressBookItemDivider.setVisibility(View.VISIBLE);
			} else {
				if (StrUtil.getFirstLetterFromPinyin(
						current_fi.optString("clubName")).equals(
						pos_fi.optString("clubName"))) {
					tag.addressBookItemDivider.setVisibility(View.VISIBLE);
				} else {
					tag.addressBookItemDivider.setVisibility(View.GONE);
				}
			}

			if (pre_fi == null) {
				tag.addressBookDivider.setVisibility(View.VISIBLE);
				tag.firstLetter.setVisibility(View.VISIBLE);
				tag.firstLetter.setText(StrUtil
						.getFirstLetterFromPinyin(current_fi
								.optString("clubName")));
			} else {

				if (StrUtil.getFirstLetterFromPinyin(
						pre_fi.optString("clubName")).equals(
						StrUtil.getFirstLetterFromPinyin(current_fi
								.optString("clubName")))) {
					tag.addressBookDivider.setVisibility(View.GONE);
					tag.firstLetter.setVisibility(View.GONE);
					tag.firstLetter.setText(StrUtil
							.getFirstLetterFromPinyin(current_fi
									.optString("clubName")));

				} else {
					tag.addressBookDivider.setVisibility(View.VISIBLE);
					tag.firstLetter.setVisibility(View.VISIBLE);
					tag.firstLetter.setText(StrUtil
							.getFirstLetterFromPinyin(current_fi
									.optString("clubName")));
				}

			}

			return convertView;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public Object getItem(int position) {
			if (position < 0 || position >= focusedClubList.length()) {
				return null;
			}
			return focusedClubList.optJSONObject(position);
		}

		@Override
		public int getCount() {
			return focusedClubList.length();
		}
	}
}
