package com.mtcent.funnymeet;

import org.json.JSONArray;

import android.app.Application;
import android.content.Context;

import com.mtcent.funnymeet.ui.helper.RequestHelper;
import com.mtcent.funnymeet.util.BaiduMap;
import com.mtcent.funnymeet.ui.helper.DataHelper;
import com.mtcent.funnymeet.util.StrUtil;

public class SOApplication extends Application {
	
	private static boolean hasUpdata = false;
	private static Context context;
	private static SOApplication app;
	private static DataHelper mDataHelper;
	private static RequestHelper mRequestHelper;
	private static BaiduMap mBaiduMap;
	private static boolean isClubUpdated = false;
	private static JSONArray unhandledMessages;


	// final static public String SERVICE_HOST =
	// "http://192.168.0.215:8080/api/api.htm";
	// final static public String SERVICE_HOST =
	// "http://203.195.163.188:8080/api/api.htm";
	// final static public String SERVICE_HOST =
	// "http://192.168.0.127:8080/api/api.htm";

	// 本地+全局 变量数据标签
	final static public String HotCityTag = "hotcity";// 选中的城市
	final static public String MyCityTag = "mycity";// GPS定位的城市
	final static public String DefaultUser = "defaultuser";// 当前默认的登录用户
	final static public String DefaultUserChange = "defaultuserchange";// 当前默认的登录用户的变更
	public static final String CONTEXT_EXTRA_PARAM_REFRESH_CLUB = "CONTEXT_EXTRA_PARAM_REFRESH_CLUB";

	public void onCreate() {
		super.onCreate();
		SOApplication.context = getApplicationContext();
		app = this;
		mDataHelper = new DataHelper(context);
		mRequestHelper = new RequestHelper();
		mBaiduMap = new BaiduMap();
	}

	public static DataHelper getDataManager() {
		return mDataHelper;
	}

	public static RequestHelper getDownLoadManager() {
		return mRequestHelper;
	}

	public static BaiduMap getBaiduMap() {
		return mBaiduMap;
	}

	@Override
	public void onTerminate() {
		if (mRequestHelper != null) {
			mRequestHelper.cancle();
			mRequestHelper = null;
			mDataHelper.colosDataManager();
			mDataHelper = null;
		}
		// if (BaiduMapXView.mBMapManager != null) {
		// BaiduMapXView.mBMapManager.destroy();
		// BaiduMapXView.mBMapManager = null;
		// }
		StrUtil.colosTencent();

		super.onTerminate();
	}

	public static Context getAppContext() {
		return SOApplication.context;
	}

	public static void setHasUpdate(boolean hasUpdate) {
		SOApplication.hasUpdata = hasUpdate;
	}
	
	public static boolean getHasUpdate() {
		return SOApplication.hasUpdata;
	}

	public static boolean isClubUpdated() {
		return isClubUpdated;
	}

	public static void setClubUpdated(boolean isClubUpdated) {
		SOApplication.isClubUpdated = isClubUpdated;
	}

	public static JSONArray getUnhandledMessages() {
		return unhandledMessages;
	}

	public static void setUnhandledMessages(JSONArray unhandledMessages) {
		SOApplication.unhandledMessages = unhandledMessages;
	}
}
