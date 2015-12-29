package com.mtcent.funnymeet.ui.activity.discovery.search;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mtcent.funnymeet.util.SearchUtilStack;

import mtcent.funnymeet.R;

@SuppressLint("CutPasteId")
public class SearchActivity extends Activity {

	/**
	 * 搜索类新，用于区分是俱乐部搜索还是会员搜索
	 */
	public static final String EXTRA_KEY_SEARCH_TYPE = "EXTRA_KEY_SEARCH_TYPE";

	/**
	 * 当前俱乐部信息，用于会员搜索时的目标俱乐部信息
	 * 值为俱乐部的JSON字符串
	 */
	public static final String EXTRA_KEY_CURRENT_CLUB = "EXTRA_KEY_CURRENT_CLUB";

	public static final String EXTRA_KEY_CURRENT_CLUB_GUID = "EXTRA_KEY_CURRENT_CLUB_GUID";
	
	//搜索会员
	public static final int SEARCH_TYPE_MEMBER = 1;
	
	//搜索俱乐部
	public static final int SEARCH_TYPE_CLUB = 2;

	private int mSearchType = SEARCH_TYPE_CLUB;
	
	TextView titleTextView;
	LinearLayout title_squeeze;
	LinearLayout title_search_bar;
	LinearLayout title_search_voice;
	EditText smart_search;
	LinearLayout search_item;
	LinearLayout just_background;
	TextView search_content;

	String keyword = "";
	
	/**
	 * 当前俱乐部信息，用于会员搜索时的目标俱乐部信息
	 * 值为俱乐部的JSON字符串
	 */
	private String mCurrentClub;
	private String mCurrentClubGuid;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.searchbar);
		if (getIntent().hasExtra(EXTRA_KEY_SEARCH_TYPE)) {
			this.mSearchType = getIntent().getIntExtra(EXTRA_KEY_SEARCH_TYPE, SEARCH_TYPE_CLUB);
			if (this.mSearchType == SEARCH_TYPE_MEMBER) {
				//取得传入的当前俱乐部信息 
				this.mCurrentClub = getIntent().getStringExtra(EXTRA_KEY_CURRENT_CLUB);
				this.mCurrentClubGuid = getIntent().getStringExtra(EXTRA_KEY_CURRENT_CLUB_GUID);
			}
		} else {
			this.mSearchType = SEARCH_TYPE_CLUB;
		}
		SearchUtilStack.searchUtilStack.add(this);
		init();
		if (this.mSearchType == SEARCH_TYPE_MEMBER) {
			this.smart_search.setHint("输入会员手机号或者用户号码");
		}
	}

	// 2.14sohuodong_android_ui_1.4.1.8_20141021
	protected void init() {
		View v = findViewById(R.id.left_backLayout);
		v.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});

		search_content = (TextView) findViewById(R.id.search_content);
		search_item = (LinearLayout) findViewById(R.id.search_item);
		just_background = (LinearLayout) findViewById(R.id.just_background);
		titleTextView = (TextView) findViewById(R.id.titleTextView);
		titleTextView.setVisibility(View.GONE);
		title_squeeze = (LinearLayout) findViewById(R.id.title_squeeze);
		title_squeeze.setVisibility(View.GONE);

		title_search_bar = (LinearLayout) findViewById(R.id.title_search_bar);
		title_search_voice = (LinearLayout) findViewById(R.id.title_search_voice);

		title_search_bar.setVisibility(View.VISIBLE);
		title_search_voice.setVisibility(View.VISIBLE);

		smart_search = (EditText) findViewById(R.id.smart_search);

		search_item.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				View view = getWindow().peekDecorView();
				if (view != null) {
					InputMethodManager inputmanger = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					inputmanger.hideSoftInputFromWindow(view.getWindowToken(),
							0);
				}

				keyword = smart_search.getEditableText().toString();
				if (keyword != null && !keyword.equals("")) {

					Intent intent = new Intent();
					intent.putExtra("keyword", keyword);
					intent.putExtra(SearchActivity.EXTRA_KEY_SEARCH_TYPE, SearchActivity.this.mSearchType);
					intent.putExtra(SearchActivity.EXTRA_KEY_CURRENT_CLUB, SearchActivity.this.mCurrentClub);
					intent.putExtra(SearchActivity.EXTRA_KEY_CURRENT_CLUB_GUID, SearchActivity.this.mCurrentClubGuid);
					intent.setClass(SearchActivity.this,
							SearchResultListActivity.class);
					startActivity(intent);
				}

			}
		});

		TextView tv = (TextView) findViewById(R.id.titleTextView);
		tv.setText("综合搜索");

		smart_search.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					just_background.setVisibility(View.GONE);
				} else {
					just_background.setVisibility(View.VISIBLE);
				}
			}
		});

		smart_search.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				if (s.length() > 0) {
					search_item.setVisibility(View.VISIBLE);
					search_content.setText(smart_search.getText().toString());
				} else {
					search_item.setVisibility(View.GONE);
				}

				if (s.length() == 0) {
					search_content.setText("");
				}
			}
		});

	}
}
