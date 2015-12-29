package com.mtcent.funnymeet.ui.activity.discovery;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.TextView;

import mtcent.funnymeet.R;

public class HtmlActivity extends Activity {

	WebView htmlWeb;
	String title;
	String html;

	void doIntent(Intent intent) {
		title = intent.getStringExtra("title");
		html = intent.getStringExtra("html");
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_html);
		doIntent(getIntent());
		init();

	}

	void init() {
		TextView tv = (TextView) findViewById(R.id.titleTextView);
		tv.setText(title);
		View v = findViewById(R.id.left_backLayout);
		v.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		htmlWeb = (WebView) findViewById(R.id.htmlWeb);
		htmlWeb.loadData(html, "text/html", "utf-8");
	}

}
