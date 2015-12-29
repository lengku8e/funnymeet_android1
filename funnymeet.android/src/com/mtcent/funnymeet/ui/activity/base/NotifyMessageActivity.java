package com.mtcent.funnymeet.ui.activity.base;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class NotifyMessageActivity extends Activity {
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TextView t = new TextView(this);
		t.setText("This is the message!");
		setContentView(t);
	}
}
