package com.mtcent.funnymeet.biz.receiver;

import java.io.File;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;

import com.mtcent.funnymeet.config.Constants;

public class ReplaceBroadcastReceiver extends BroadcastReceiver {
	private static final String TAG = "ApkDelete";

	@Override
	public void onReceive(Context arg0, Intent arg1) {
		File downLoadApk = new File(Environment.getExternalStorageDirectory(),
				Constants.APP_NAME);
		if (downLoadApk.exists()) {
			downLoadApk.delete();
			Log.i(TAG, "downLoadApkFile was deleted!");
		}
	}
}
