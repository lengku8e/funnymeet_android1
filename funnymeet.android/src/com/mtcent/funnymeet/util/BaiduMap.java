package com.mtcent.funnymeet.util;


import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.mtcent.funnymeet.SOApplication;

//百度地图
public class BaiduMap {
	// 百度地图key 测试
	//final static public String BaiduMapKey = "djqSi0BUP0LEMaAlOECoV9tk";
	final static public String BaiduMapKey = "a83Ukj0FlwjTgdZlV7T5DCKd";
	

	public abstract static class LocationCallBack {
		public abstract void onfinish(double latitude, double longitude,
				double accuracy, String city);
	}
//	LocationClient mLocClient = new LocationClient(
//			SOApplication.getAppContext());
	
	
	// public static com.baidu.mapapi.BMapManager mBMapManager = null;
	// static boolean authorization = false;
	// static void createBaiduMapManager() {
	// // 注意：请在试用setContentView前初始化BMapManager对象，否则会报错
	// if (mBMapManager == null || authorization == false) {
	// mBMapManager = new BMapManager(FCApplication.getAppContext());
	// MKGeneralListener l = new MKGeneralListener() {
	// @Override
	// public void onGetNetworkState(int iError) {
	// }
	//
	// // public class com.baidu.mapapi.map.MKEvent {
	// public static final int MKEVENT_MAP_MOVE_FINISH = 14;
	// public static final int MKEVENT_BUS_DETAIL = 15;
	// public static final int MKEVENT_SUGGESTION = 16;
	// public static final int MKEVENT_POIRGCSHAREURL = 17;
	// public static final int MKEVENT_POIDETAILSHAREURL = 18;
	// public static final int ERROR_NETWORK_CONNECT = 2;
	// public static final int ERROR_NETWORK_DATA = 3;
	// public static final int ERROR_ROUTE_ADDR = 4;
	// public static final int ERROR_RESULT_NOT_FOUND = 100;
	// public static final int ERROR_PERMISSION_DENIED = 300;
	//
	// @Override
	// public void onGetPermissionState(int iError) {
	// // 非零值表示key验证未通过
	// if (iError != 0) {
	// mBMapManager.destroy();
	// mBMapManager = null;
	// authorization = false;
	// // 授权Key错误：
	// // Toast.makeText(jsRun.activity,
	// // "请在 DemoApplication.java文件输入正确的授权Key,并检查您的网络连接是否正常！error: "+iError,
	// // Toast.LENGTH_LONG).show();
	// } else {
	// // Toast.makeText(jsRun.activity, "key认证成功",
	// // Toast.LENGTH_LONG).show();
	// authorization = true;
	// }
	// }
	// };
	// mBMapManager.initViewControl(strKey, l);
	// }
	// }

	public void getbdlocation(LocationCallBack callBack) {

//		// 定位初始化
//		LocationClient mLocClient = new LocationClient(
//				SOApplication.getAppContext());
//		mLocClient.setAK(BaiduMapKey);
		MyLocationListenner listener = new MyLocationListenner();
//		listener.mLocClient = mLocClient;
		listener.callBack = callBack;
//		mLocClient.registerLocationListener(listener);
//		LocationClientOption option = new LocationClientOption();
//		option.setOpenGps(true); // 打开gps
//		option.setCoorType("bd09ll"); // 设置坐标类型
//		option.setScanSpan(5 * 1000); // 请求定位一次
//		mLocClient.setLocOption(option);
//		mLocClient.start();
//		mLocClient.requestLocation();

		
		final LocationClient mLocationClient = new LocationClient(SOApplication.getAppContext());

		mLocationClient.setAK(BaiduMapKey);
		mLocationClient.registerLocationListener(listener);
		listener.mLocClient = mLocationClient;
		
        LocationClientOption option = new LocationClientOption();  
        option.setOpenGps(true);  
        option.setAddrType("all");//返回的定位结果包含地址信息  
        option.setCoorType("bd09ll");//返回的定位结果是百度经纬度,默认值gcj02  
        option.setScanSpan(5000);//设置发起定位请求的间隔时间为5000ms  
        option.disableCache(true);//禁止启用缓存定位  
        option.setPoiNumber(5);    //最多返回POI个数     
        option.setPoiDistance(1000); //poi查询距离          
        option.setPoiExtraInfo(true); //是否需要POI的电话和地址等详细信息          
        mLocationClient.setLocOption(option);

        mLocationClient.start();
        mLocationClient.requestLocation();

//		new SafeThread(){
//
//			@Override
//			public boolean runUntilFalse() {
//				mLocationClient.start();
//				if(mLocationClient.isStarted()){
//					mLocationClient.requestLocation();
//				}
//				return false;
//			}
//			
//		} .start();
	}

	class MyLocationListenner implements  BDLocationListener {
		LocationClient mLocClient = null;
		LocationCallBack callBack;;

		@Override
		public void onReceiveLocation(BDLocation location) {
			double latitude = -1;// 纬度
			double longitude = -1;// 经度
			double accuracy = -1;// 有限范围
			String city = null;
			if (location != null) {
				try {
					int ttt = BDLocation.TypeNone;
					int type = location.getLocType();
					latitude = location.getLatitude();
					longitude = location.getLongitude();
					accuracy = location.getRadius();
					city = location.getCity();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			if (mLocClient != null) {
				mLocClient.stop();
				mLocClient = null;
			}
			callBack.onfinish(latitude, longitude, accuracy, city);
		}

		@Override
		public void onReceivePoi(BDLocation arg0) {

		}
	};
}
