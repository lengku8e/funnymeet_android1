package com.mtcent.funnymeet.ui.activity.club;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.mtcent.funnymeet.ui.view.control.AutoGridView;
import com.mtcent.funnymeet.ui.view.control.CEditTextView;
import com.mtcent.funnymeet.ui.view.control.CEditTextView.onRectChangeListener;
import com.mtcent.funnymeet.model.FakeInfo;
import com.mtcent.funnymeet.ui.view.layout.InputChangeLinearLayout;
import com.mtcent.funnymeet.ui.helper.UserMangerHelper;
import com.mtcent.funnymeet.ui.view.control.XVURLImageView;

import java.util.ArrayList;

import mtcent.funnymeet.R;

public class HostChatActivity extends Activity {

	TextView type_select_btn;
	TextView chat_sent_btn;
	CEditTextView chat_content;
	LayoutInflater inflater;
	View chatContentView;
	LinearLayout chatContent;
	LinearLayout hoster_chat_MainLayout;
	ScrollView chatScrollView;
	Handler handler;
	Intent intent;
	String fromHeadImageUrl;
	LinearLayout fromChatContent;
	AutoGridView autoGridView;
	XVURLImageView fromHeadImage;
	TextView chatFromMessageContent;

	ArrayList<FakeInfo> info;

	boolean needShowTypeSelectGridView = false;
	boolean hasShowSoftInput = false;
	InputChangeLinearLayout mainLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.hoster_list_chat_activity);
		init();
	}

	protected void init() {

		intent = HostChatActivity.this.getIntent();
		fromHeadImageUrl = intent.getStringExtra("imageUrl");
		View v = findViewById(R.id.left_backLayout);
		v.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		mainLayout = (InputChangeLinearLayout) findViewById(R.id.chatMainLayout);
		mainLayout.setOnKeyboardChangeListener(new InputChangeLinearLayout.KeyboardChangeListener() {

			@Override
			public void onKeyboardChange(boolean isShow, int w, int h,
					int oldw, int oldh) {
				hasShowSoftInput = isShow;
				if (hasShowSoftInput
						&& autoGridView.getVisibility() != View.GONE) {
					handler.post(new Runnable() {
						@Override
						public void run() {
							autoGridView.setVisibility(View.GONE);
						}
					});
				} else {
					if (needShowTypeSelectGridView
							&& autoGridView.getVisibility() != View.VISIBLE) {
						handler.post(new Runnable() {
							@Override
							public void run() {
								autoGridView.setVisibility(View.VISIBLE);
							}
						});
					}
				}
				needShowTypeSelectGridView = false;
			}
		});
		info = new ArrayList<FakeInfo>();
		info.add(new FakeInfo(R.drawable.app_panel_pic_icon, "图片"));
		info.add(new FakeInfo(R.drawable.app_panel_video_icon, "视频"));
		info.add(new FakeInfo(R.drawable.app_panel_location_icon, "位置"));
		info.add(new FakeInfo(R.drawable.app_panel_friendcard_icon, "名片"));
		info.add(new FakeInfo(R.drawable.app_panel_fav_icon, "我的收藏"));
		info.add(new FakeInfo(R.drawable.app_panel_voiceinput_icon, "语音输入"));
		info.add(new FakeInfo(R.drawable.com_dianping_v1, "大众点评"));
		info.add(new FakeInfo(R.drawable.app_panel_add_icon, ""));

		handler = new Handler();
		inflater = (LayoutInflater) HostChatActivity.this
				.getSystemService(LAYOUT_INFLATER_SERVICE);

		chatScrollView = (ScrollView) findViewById(R.id.chatScrollView);
		hoster_chat_MainLayout = (LinearLayout) findViewById(R.id.hoster_chat_MainLayout);
		fromHeadImage = (XVURLImageView) findViewById(R.id.chatFromHeadImage);
		fromHeadImage.setImageUrl(fromHeadImageUrl);
		fromHeadImage.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		chatFromMessageContent = (TextView) findViewById(R.id.chatFromMessageContent);
		chatFromMessageContent.setText("你好,我是"
				+ intent.getStringExtra("nickname") + ",我们开始聊天吧");
		type_select_btn = (TextView) findViewById(R.id.type_select_btn);
		chat_sent_btn = (TextView) findViewById(R.id.chat_sent_btn);
		chat_content = (CEditTextView) findViewById(R.id.chat_content);
		autoGridView = (AutoGridView) findViewById(R.id.toolsPanel);
		chatScrollView.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (hasShowSoftInput) {

					InputMethodManager inputmanger = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					inputmanger.hideSoftInputFromWindow(
							chat_content.getWindowToken(), 0);

				}
				if (autoGridView.getVisibility() != View.GONE) {
					autoGridView.setVisibility(View.GONE);
				}
				return false;
			}
		});
		// chatScrollView.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// // TODO Auto-generated method stub
		// View view = getWindow().peekDecorView();
		// if (view != null) {
		// InputMethodManager inputmanger = (InputMethodManager)
		// getSystemService(Context.INPUT_METHOD_SERVICE);
		// inputmanger.hideSoftInputFromWindow(view.getWindowToken(),
		// 0);
		// }
		// }
		// });

		type_select_btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (hasShowSoftInput) {
					needShowTypeSelectGridView = true;
					getWindow().setSoftInputMode(
							WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
					InputMethodManager inputmanger = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					inputmanger.hideSoftInputFromWindow(
							chat_content.getWindowToken(),
							InputMethodManager.HIDE_NOT_ALWAYS);
				} else {
					autoGridView.setVisibility(View.VISIBLE);
				}
			}
		});

		autoGridView.setAdapter(new BaseAdapter() {

			class Tag {
				ImageView image;
				TextView title;
			}

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				// TODO Auto-generated method stub

				Tag tag = null;
				if (convertView == null) {

					LayoutInflater inflater = (LayoutInflater) HostChatActivity.this
							.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
					convertView = inflater.inflate(
							R.layout.hoster_chat_toolsbar_item, null);
					tag = new Tag();

					tag.image = (ImageView) convertView
							.findViewById(R.id.toolsImage);
					tag.title = (TextView) convertView
							.findViewById(R.id.toolsTitle);
					convertView.setTag(tag);
				} else {
					tag = (Tag) convertView.getTag();
				}

				FakeInfo info = (FakeInfo) getItem(position);
				tag.image.setImageResource(info.getIamge());
				tag.title.setText(info.getTitle());
				return convertView;
			}

			@Override
			public long getItemId(int position) {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public Object getItem(int position) {
				// TODO Auto-generated method stub
				return info.get(position);
			}

			@Override
			public int getCount() {
				// TODO Auto-generated method stub
				return info.size();
			}
		});

		chat_content.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				if (s.length() != 0) {
					type_select_btn.setVisibility(View.GONE);
					chat_sent_btn.setVisibility(View.VISIBLE);
				} else {
					chat_sent_btn.setVisibility(View.GONE);
					type_select_btn.setVisibility(View.VISIBLE);
				}
			}
		});

		chat_content.setRectChangeListener(new onRectChangeListener() {

			@Override
			public void onChage() {
				// TODO Auto-generated method stub
				handler.post(new Runnable() {
					@Override
					public void run() {
						chatScrollView.fullScroll(ScrollView.FOCUS_DOWN);
					}
				});
			}
		});

		// chat_content.setOnFocusChangeListener(new OnFocusChangeListener() {
		//
		// @Override
		// public void onFocusChange(View v, boolean hasFocus) {
		// handler.post(new Runnable() {
		// @Override
		// public void run() {
		// chatScrollView.fullScroll(ScrollView.FOCUS_DOWN);
		// }
		// });
		// }
		// });

		chat_sent_btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String content = chat_content.getText().toString();
				chatContentView = inflater.inflate(
						R.layout.hoster_chat_tolayout, null);
				chatContent = (LinearLayout) chatContentView
						.findViewById(R.id.chat_to_content);
				TextView text = (TextView) chatContent
						.findViewById(R.id.chat_to_content_text);
				XVURLImageView headImage = (XVURLImageView) chatContent
						.findViewById(R.id.chatHeadImage);

				headImage.setImageUrl(UserMangerHelper.getDefaultUserFaceUrl());
				text.setText(content);
				hoster_chat_MainLayout.addView(chatContentView);
				chat_content.setText("");
				handler.post(new Runnable() {
					@Override
					public void run() {
						chatScrollView.fullScroll(ScrollView.FOCUS_DOWN);
					}
				});

			}
		});

	}
}

