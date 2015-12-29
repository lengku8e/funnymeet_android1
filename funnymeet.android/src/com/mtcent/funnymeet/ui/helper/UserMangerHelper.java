package com.mtcent.funnymeet.ui.helper;

import com.mtcent.funnymeet.SOApplication;
import com.mtcent.funnymeet.config.Constants;
import com.mtcent.funnymeet.ui.helper.RequestHelper.DownBack;
import com.mtcent.funnymeet.ui.helper.RequestHelper.Pdtask;

import org.json.JSONException;
import org.json.JSONObject;

public class UserMangerHelper {

	public UserMangerHelper() {
	}

	public static String key[] = new String[] { "nickname", "country",
			"province", "city", "address", "faceHash", "genderName",
			"privateSolgan", "accountName", "faceUrl", "realname", "company" };

	public static void requestUpDateInfo(JSONObject user, Object owner,
			DownBack back) {
		// 1.清除未完成的更新，
		// 2.添加最新的请求
		Pdtask task = new Pdtask(owner, back, Constants.SERVICE_HOST, null,
				RequestHelper.Type_PostParam, null, 0, true);
		task.addParam("method", "setUserInfoByGuid");// 页码
		task.addParam("user_guid", UserMangerHelper.getDefaultUserGuid());// 页码
		task.addParam("user_session_guid",
				UserMangerHelper.getDefaultUserLongsession());// 页码

		for (String string : key) {
			if (user.has(string)) {
				task.addParam(string, user.optString(string));// 页码
			}
		}
		UserMangerHelper.saveDefaultUser(user);
		UserMangerHelper.setDefaultUserChange(true);
		SOApplication.getDownLoadManager().remove(owner);
		SOApplication.getDownLoadManager().addTask(task);
	}

	public static void setDefaultUserChange(boolean b) {

		SOApplication.getDataManager().setValue(
				SOApplication.DefaultUserChange, String.valueOf(b));

		return;
	}

	public static void setDefaultUserChange(JSONObject user) {

		JSONObject locationUser = UserMangerHelper.getDefaultUser();
		boolean change = false;

		for (String string : key) {
			if (user.has(string)) {
				if (!user.optString(string).equals(
						locationUser.optString(string))) {
					change = true;
					break;
				}
			} else if (locationUser.has(string)) {
				change = true;
				break;
			}
		}

		UserMangerHelper.setDefaultUserChange(change);
		return;
	}

	public static boolean isDefaultUserChange() {
		String bStr = SOApplication.getDataManager().getValue(
				SOApplication.DefaultUserChange);

		return String.valueOf(true).equals(bStr);
	}

	public static void saveDefaultUser(JSONObject userJson) {
		if (userJson != null) {
			SOApplication.getDataManager().setValue(SOApplication.DefaultUser,
					userJson.toString());
			setDefaultUserChange(false);
		}
		return;
	}

	public static JSONObject getDefaultUser() {
		String str = SOApplication.getDataManager().getValue(
				SOApplication.DefaultUser);
		JSONObject json = new JSONObject();
		if (str != null) {
			try {
				json = new JSONObject(str);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return json;
	}

	public static String getDefaultUserNickName() {
		return getDefaultUser().optString("nickname", null);
	}

	public static String getDefaultUserPhone() {
		return getDefaultUser().optString("mobilePhone", null);
	}

	public static String getDefaultUserGuid() {
		return getDefaultUser().optString("guid", null);
	}

	public static String getDefaultUserQQ() {
		return getDefaultUser().optString("qq", null);
	}

	public static String getDefaultUserWeixin() {
		return getDefaultUser().optString("weixin", null);
	}

	public static String getDefaultUserFaceUrl() {
		return getDefaultUser().optString("faceUrl", null);
	}

	public static String getDefaultUserAccountName() {
		return getDefaultUser().optString("accountName", null);
	}

	public static String getDefaultUserEmail() {
		return getDefaultUser().optString("email", null);
	}

	public static void setDefaultUserFaceUrl(String url) {
		JSONObject user = getDefaultUser();
		try {
			user.put("faceUrl", url);
			saveDefaultUser(user);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return;
	}

	public static String getDefaultUserLongsession() {
		return getDefaultUser().optString("longSession", null);
	}

	public static boolean isDefaultUserLogin() {
		return getDefaultUserLongsession() != null;
	}

	public static void cleanDefaultUserLogin() {
		JSONObject json = getDefaultUser();
		try {
			json.put("longSession", null);
			saveDefaultUser(json);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return;
	}
}
