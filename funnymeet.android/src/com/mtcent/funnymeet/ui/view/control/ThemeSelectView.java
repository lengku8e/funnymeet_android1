package com.mtcent.funnymeet.ui.view.control;

import android.app.Activity;

import com.mtcent.funnymeet.SOApplication;
import com.mtcent.funnymeet.config.Constants;
import com.mtcent.funnymeet.ui.helper.RequestHelper;
import com.mtcent.funnymeet.ui.view.control.CategorySelectListView.CategorySelectAdapter;
import com.mtcent.funnymeet.ui.helper.RequestHelper.Pdtask;
import com.mtcent.funnymeet.util.StrUtil;

import org.json.JSONObject;

import java.util.ArrayList;

public abstract class ThemeSelectView extends FunnymeetBaseView implements CategorySelectAdapter {

	public ThemeSelectView(Activity activity) {
		super(activity);
		init();
	}

	CategorySelectListView allView;

	void init() {
		mainView = allView = new CategorySelectListView(mActivity);
		allView.setCategorySelectAdapter(this);
	}

	@Override
	public void getCategory() {
		RequestHelper.DownBack back = new RequestHelper.DownBack(){

			@Override
			public void onFinish(Pdtask t) {
				ArrayList<JSONObject> list = StrUtil.getJSONArrayList(t.json);
				allView.updateCategory(list);
			}

			@Override
			public void onUpdate(Pdtask t) {
				onFinish(t);
			}
			
		};
		Pdtask task = new Pdtask(this, back, Constants.SERVICE_HOST, null,
				RequestHelper.Type_DownJsonString, null, 0, true);
		task.addParam("method", "listProjectParentSubject");
		SOApplication.getDownLoadManager().startTask(task);

	}

	@Override
	public String getCategoryName(int index, JSONObject categoryJson) {
		if(categoryJson!=null){
			return categoryJson.optString("name");
		}
		return "getCategoryName";
	}

	@Override
	public void getCategoryChildList(final int index, final JSONObject categoryJson) {
		RequestHelper.DownBack back = new RequestHelper.DownBack(){

			@Override
			public void onFinish(Pdtask t) {
				ArrayList<JSONObject> list = StrUtil.getJSONArrayList(t.json);
				allView.updateCategoryChild(index, categoryJson,
						list);
			}
			@Override
			public void onUpdate(Pdtask t) {
				onFinish(t);
			}
		};
		Pdtask task = new Pdtask(this, back, Constants.SERVICE_HOST, null,
				RequestHelper.Type_DownJsonString, null, 0, true);
		task.addParam("method", "listProjectChildSubject");
		task.addParam("id", String.valueOf(categoryJson.optInt("id")));
		SOApplication.getDownLoadManager().startTask(task);
	}

	@Override
	public String getCategoryChildName(int index, JSONObject categoryJson,
			int indexChild, JSONObject childJson) {
		if(childJson!=null){
			return childJson.optString("name");
		}
		return "getCategoryChildName";
	}

	@Override
	public void onChildSelect(int index, JSONObject categoryJson,
			int indexChild, JSONObject childJson) {
		onSelect(categoryJson,childJson,allView.categoryChildList[index]);
	}
	public abstract void onSelect(JSONObject theme1, JSONObject theme2,ArrayList<JSONObject> dataListItemContent);
}
