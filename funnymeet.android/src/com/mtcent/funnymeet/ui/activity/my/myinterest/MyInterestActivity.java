package com.mtcent.funnymeet.ui.activity.my.myinterest;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.TextView;

import com.mtcent.funnymeet.SOApplication;
import com.mtcent.funnymeet.config.Constants;
import com.mtcent.funnymeet.ui.activity.base.BaseActivity;
import com.mtcent.funnymeet.ui.helper.RequestHelper;
import com.mtcent.funnymeet.ui.helper.RequestHelper.Pdtask;
import com.mtcent.funnymeet.ui.helper.UserMangerHelper;
import com.mtcent.funnymeet.util.StrUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import mtcent.funnymeet.R;

public class MyInterestActivity extends BaseActivity {

	public static final int ID = MyInterestActivity.class.hashCode();

	TextView titleTextView;
	ExpandableListView listview;
	JSONObject[] categoriesForList_temp;
	JSONObject[][] listItemForList_temp;
	JSONObject[] theUsersAnswer_temp;

	// JSONObject[] categories = new JSONObject[0];
	JSONObject user = new JSONObject();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_myinterest_special);// 默认登录界面
		requestData();
		init();

	}

	protected void init() {

		View v = findViewById(R.id.left_backLayout);
		v.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		titleTextView = (TextView) findViewById(R.id.titleTextView);
		titleTextView.setText("我的兴趣");

		listview = (ExpandableListView) findViewById(R.id.expandlist);

		listview.setOnGroupClickListener(new OnGroupClickListener() {
			@Override
			public boolean onGroupClick(ExpandableListView parent, View v,
					int groupPosition, long id) {
				return true; // 默认为false，设为true时，点击事件不会展开Group
			}

		});

		listview.setAdapter(adapter);
		listview.setOnChildClickListener(new OnChildClickListener() {

			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				JSONObject question = adapter.listItemForList[groupPosition][childPosition];

				Intent intent = new Intent();
				String gpAndcp = groupPosition + "-" + childPosition;
				intent.putExtra("questions", question.toString());
				intent.putExtra("gpAndcp", gpAndcp);
				JSONObject tmp = findSelectedQuestionsAnswer(question);
				if (tmp == null) {
					intent.putExtra("answers", "null");
				} else {
					intent.putExtra("answers", tmp.toString());
				}

				intent.setClass(MyInterestActivity.this,
						MyInterestModifyActivity.class);
				startActivityForResult(intent, MyInterestModifyActivity.ID);
				return false;
			}
		});
		listview.setGroupIndicator(null);
		resetView();

	}

	public JSONObject findSelectedQuestionsAnswer(JSONObject question) {

		for (int i = 0; i < adapter.theUsersAnswerList.size(); i++) {

			try {
				if (question.getString("guid").equals(
						adapter.theUsersAnswerList.get(i).getString(
								"questionGuid"))
						&& question.getString("categoryGuid").equals(
								adapter.theUsersAnswerList.get(i).getString(
										"categoryGuid"))) {
					return adapter.theUsersAnswerList.get(i);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;

	}

	MyBaseExpandableListAdapter adapter = new MyBaseExpandableListAdapter();

	class MyBaseExpandableListAdapter extends BaseExpandableListAdapter {
		public JSONObject[] categoriesForList;
		public JSONObject[][] listItemForList;
		public ArrayList<JSONObject> theUsersAnswerList = new ArrayList<JSONObject>();

		public class tagChild {
			TextView text;
			TextView myinterest_answer;
			JSONObject city;
		}

		public class tagParent {
			TextView text;
		}

		@Override
		// 获取指定组位置、指定子列表项处的子列表项数据
		public Object getChild(int groupPosition, int childPosition) {
			// TODO Auto-generated method stub
			if (listItemForList == null) {
				return null;
			}
			return listItemForList[groupPosition][childPosition];
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			// TODO Auto-generated method stub
			return childPosition;
		}

		@Override
		// 该方法决定每个子选项的外观
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			tagChild tag = null;
			if (convertView == null
					|| !(convertView.getTag() instanceof tagChild)) {
				tag = new tagChild();
				convertView = inflater.inflate(R.layout.my_myinterest_child,
						null);
				tag.text = (TextView) convertView.findViewById(R.id.name);
				tag.myinterest_answer = (TextView) convertView
						.findViewById(R.id.myinterest_answer);

				convertView.setTag(tag);
			}
			tag = (tagChild) convertView.getTag();
			JSONObject itemTitle = (JSONObject) getChild(groupPosition,
					childPosition);
			try {
				tag.text.setText(itemTitle.getString("question"));
				if (findSelectedQuestionsAnswer(itemTitle) != null) {
					tag.myinterest_answer.setText(findSelectedQuestionsAnswer(
							itemTitle).optString("answer"));
				} else {
					tag.myinterest_answer.setText("");
				}

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return convertView;
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			// TODO Auto-generated method stub
			return listItemForList[groupPosition].length;
		}

		@Override
		public Object getGroup(int groupPosition) {
			// TODO Auto-generated method stub
			if (categoriesForList == null) {
				return null;
			}
			return categoriesForList[groupPosition];
		}

		@Override
		public int getGroupCount() {
			// TODO Auto-generated method stub
			if (categoriesForList == null) {
				return 0;
			}
			return categoriesForList.length;
		}

		@Override
		public long getGroupId(int groupPosition) {
			// TODO Auto-generated method stub
			return groupPosition;
		}

		@Override
		// 该方法决定每个组选项的外观
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			tagParent tag = null;
			if (convertView == null
					|| !(convertView.getTag() instanceof tagParent)) {

				tag = new tagParent();
				convertView = inflater.inflate(R.layout.my_myinterest_group,
						null);
				tag.text = (TextView) convertView.findViewById(R.id.name);
				convertView.setTag(tag);
			}

			tag = (tagParent) convertView.getTag();
			JSONObject groupItem = (JSONObject) getGroup(groupPosition);
			try {
				tag.text.setText(groupItem.getString("title"));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return convertView;

		}

		@Override
		public boolean hasStableIds() {
			// TODO Auto-generated method stub
			return true;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			// TODO Auto-generated method stub
			return true;
		}

	};

	void requestData() {

		Pdtask task = new Pdtask(this, this, Constants.SERVICE_HOST, null,
				RequestHelper.Type_PostParam, null, 0, true);
		task.addParam("method", "listInterest");// 页码
		task.addParam("user_guid", UserMangerHelper.getDefaultUserGuid());// 页码
		task.addParam("long_session",
				UserMangerHelper.getDefaultUserLongsession());// 页码

		SOApplication.getDownLoadManager().startTask(task);
		showWait();

	}

	@Override
	public void onFinish(Pdtask t) {

		if (t.getParam("method").equals("listInterest")) {
			boolean succ = false;
			String msg = "获取兴趣列表失败";
			JSONArray categories = null;
			JSONArray listItem = null;
			JSONArray answers = null;
			ArrayList<JSONObject> temp = new ArrayList<JSONObject>();

			if (t.json != null) {
				JSONObject results = t.json.optJSONObject("results");
				if (results != null && results.optJSONArray("category")!=null) {
					categories = results.optJSONArray("category");
					categoriesForList_temp = new JSONObject[categories.length()];

					for (int i = 0; i < categories.length(); i++) {

						try {
							categoriesForList_temp[i] = categories
									.getJSONObject(i);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}
					listItem = results.optJSONArray("question");
					answers = results.optJSONArray("answer");
					theUsersAnswer_temp = new JSONObject[answers.length()];
					for (int i = 0; i < answers.length(); i++) {
						try {
							theUsersAnswer_temp[i] = answers.getJSONObject(i);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

					listItemForList_temp = new JSONObject[categories.length()][];

					for (int i = 0; i < categories.length(); i++) {
						for (int j = 0; j < listItem.length(); j++) {
							try {
								if (listItem
										.getJSONObject(j)
										.getString("categoryID")
										.equals(categories.getJSONObject(i)
												.getString("id"))) {

									temp.add(listItem.getJSONObject(j));

								}

							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						JSONObject[] temp2 = new JSONObject[temp.size()];
						for (int k = 0; k < temp2.length; k++) {
							temp2[k] = temp.get(k);
						}
						listItemForList_temp[i] = temp2;
						temp.clear();
					}

					int su = results.optInt("success");
					if (su == 1) {
						succ = true;
					} else if (results.has("msg")) {
						msg = results.optString("msg");
					}
				}
				resetView();
			} else {
				StrUtil.showMsg(this, msg);
			}

		}
		hideWait();
	}

	void resetView() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (listItemForList_temp != null
						|| categoriesForList_temp != null
						|| theUsersAnswer_temp != null) {
					adapter.listItemForList = listItemForList_temp;
					adapter.categoriesForList = categoriesForList_temp;
					for (JSONObject j : theUsersAnswer_temp) {
						adapter.theUsersAnswerList.add(j);
					}

					listItemForList_temp = null;
					categoriesForList_temp = null;
					theUsersAnswer_temp = null;
					adapter.notifyDataSetChanged();
					for (int i = 0; i < adapter.getGroupCount(); i++) {

						listview.expandGroup(i);

					}
				}
			}

		});

	}

	@Override
	public void onUpdate(Pdtask t) {
		// TODO Auto-generated method stub

	}

	// CustomDialog waitDialog = null;
	//
	// public void showWait() {
	// runOnUiThread(new Runnable() {
	// @Override
	// public void run() {
	// if (waitDialog == null) {
	// waitDialog = new CustomDialog(MyInterestActivity.this);
	// waitDialog.setContentView(R.layout.dialog_wait);
	// }
	// waitDialog.show();
	// }
	// });
	// }
	//
	// public void hideWait() {
	// runOnUiThread(new Runnable() {
	// @Override
	// public void run() {
	// waitDialog.dismiss();
	// // finish();
	// }
	// });
	// }
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (resultCode == RESULT_OK) {
			String[] position = data.getExtras().getString("gpAndcp")
					.split("-");
			String newAnswer = data.getExtras().getString("newAnswer");
			int x = Integer.valueOf(position[0]);
			int y = Integer.valueOf(position[1]);
			JSONObject tmpForUpdate = adapter.listItemForList[x][y];
			JSONObject answerExist = findSelectedQuestionsAnswer(tmpForUpdate);
			if (answerExist != null) {
				try {
					answerExist.put("answer", newAnswer);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				JSONObject newAnswerObject = new JSONObject();

				try {
					newAnswerObject.put("questionGuid",
							tmpForUpdate.getString("guid"));
					newAnswerObject.put("answer", newAnswer);
					newAnswerObject.put("categoryGuid",
							tmpForUpdate.getString("categoryGuid"));
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				adapter.theUsersAnswerList.add(newAnswerObject);
			}
			adapter.notifyDataSetChanged();
		}
	}
}
