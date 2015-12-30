package com.mtcent.funnymeet.ui.activity.my.clubconsole;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.mtcent.funnymeet.SOApplication;
import com.mtcent.funnymeet.config.Constants;
import com.mtcent.funnymeet.model.ClubActivityList;
import com.mtcent.funnymeet.ui.activity.base.BaseActivity;
import com.mtcent.funnymeet.ui.helper.RequestHelper;
import com.mtcent.funnymeet.ui.helper.RequestHelper.Pdtask;
import com.mtcent.funnymeet.ui.helper.UserMangerHelper;
import com.mtcent.funnymeet.ui.view.control.XVURLImageView;
import com.mtcent.funnymeet.util.StrUtil;
import com.qiniu.auth.JSONObjectRet;
import com.qiniu.io.IO;
import com.qiniu.io.PutExtra;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;

import mtcent.funnymeet.R;

public class MyCreateClubNameActivity extends BaseActivity {

    public static final int MYCREATECLUBNAMEACTIVITY_REQUESTCODE = 123;

    TextView titleTextView;
    EditText my_clubs_create_name;
    TextView my_clubs_create_name_leftword;
    int leftwords = 10;
    Intent intent;
    TextView my_clubs_create_club_type;
    TextView next_step;
    ImageView my_clubs_create_fakeicon;
    XVURLImageView my_clubs_create_realicon;
    String imageFilePath = "";
    String imageFileHash = null;

    String iconUrl;
    String clubName;
    String club_type_id;
    String club_type_name;

    JSONObject resultJson;
//    String resultStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_clubs_create_name);
        init();
    }

    protected void init() {

        ClubActivityList.myActivityList
                .add(MyCreateClubNameActivity.this);

        View v = findViewById(R.id.left_backLayout);
        v.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                finish();
            }
        });

        next_step = (TextView) findViewById(R.id.next_step);
        next_step.setVisibility(View.VISIBLE);
        next_step.setText("提交");
        next_step.setTextColor(0xffffffff);

        my_clubs_create_fakeicon = (ImageView) findViewById(R.id.my_clubs_create_fakeicon);

        my_clubs_create_fakeicon.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MyCreateClubNameActivity.this,
                        SelectImageActivity.class);
                intent.putExtra("path", imageFilePath);
                intent.putExtra("w", 400);
                intent.putExtra("h", 400);
                startActivityForResult(intent, MYCREATECLUBNAMEACTIVITY_REQUESTCODE);
            }
        });

        my_clubs_create_realicon = (XVURLImageView) findViewById(R.id.my_clubs_create_realicon);

        next_step.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                clubName = my_clubs_create_name.getText().toString();

                if (clubName.length() >= 2 && clubName.length() <= 10) {
                    commit();
                }
                iconUrl = imageFilePath;

            }
        });

        intent = this.getIntent();
        JSONObject club_type_json = null;
        try {
            club_type_json = new JSONObject(
                    intent.getStringExtra("club_type_json"));
            club_type_id = club_type_json.optString("id");
            club_type_name = club_type_json.optString("name");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        my_clubs_create_club_type = (TextView) findViewById(R.id.my_clubs_create_club_type);
        my_clubs_create_club_type.setText(club_type_name);

        my_clubs_create_name_leftword = (TextView) findViewById(R.id.my_clubs_create_name_left);
        my_clubs_create_name = (EditText) findViewById(R.id.my_clubs_create_name);
        my_clubs_create_name.addTextChangedListener(new TextWatcher() {

            private CharSequence temp;
            private int selectionStart;
            private int selectionEnd;

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                temp = s;
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                int number = leftwords - s.length();
                my_clubs_create_name_leftword.setText("" + number);
                selectionStart = my_clubs_create_name.getSelectionStart();
                selectionEnd = my_clubs_create_name.getSelectionEnd();
                if (temp.length() > leftwords) {
                    s.delete(selectionStart - 1, selectionEnd);
                    int tempSelection = selectionEnd;
                    my_clubs_create_name.setText(s);
                    my_clubs_create_name.setSelection(tempSelection);// 设置光标在最后
                }
            }
        });

        titleTextView = (TextView) findViewById(R.id.titleTextView);
        titleTextView.setText("填写俱乐部信息");

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 556 && resultCode == RESULT_FIRST_USER) {
            setResult(RESULT_FIRST_USER);
            finish();
        } else if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 123 && null != data) {
                imageFilePath = data.getStringExtra("path");
                imageFileHash = null;
                Options options = new Options();
                Bitmap bitmap = BitmapFactory
                        .decodeFile(imageFilePath, options);// 解码图片
                my_clubs_create_realicon.setImageUrl(null);
                my_clubs_create_realicon.setImageBitmap(bitmap);
                my_clubs_create_realicon.setVisibility(View.VISIBLE);
                my_clubs_create_fakeicon.setVisibility(View.GONE);
                requestToken();
            }
        }

    }

    private void requestToken() {
        if (imageFilePath != null && new File(imageFilePath).isFile()
                && imageFileHash == null) {

            Pdtask task = new Pdtask(this, this, Constants.SERVICE_HOST,
                    null, RequestHelper.Type_PostParam, null, 0, true);
            task.addParam("method", "getFileCloudToken");
            SOApplication.getDownLoadManager().startTask(task);
        }
        showWait();
    }


    private void createClubWithADiscussion(String club_guid) {


        Pdtask task = new Pdtask(this, this, Constants.SERVICE_HOST, null,
                RequestHelper.Type_PostParam, null, 0, true);
        task.addParam("method", "createDiscussionZone");// 页码
        task.addParam("user_guid", UserMangerHelper.getDefaultUserGuid());// 页码
        task.addParam("name", clubName);// 页码
        task.addParam("club_guid", club_guid);
        SOApplication.getDownLoadManager().startTask(task);

    }

    @Override
    public void onFinish(Pdtask t) {
        super.onFinish(t);
        boolean succ = false;
        if (t.getParam("method").equals("insertClub")) {

            if (t.json != null) {

                if (t.json.optJSONObject("results") != null
                        && t.json.optString("status") != null
                        && t.json.optString("status").equals("ok")) {
                    JSONObject result = t.json.optJSONObject("results");
                    if (result.optInt("id", -1) >= 0) {
                        succ = true;
                    } else {
                        StrUtil.showMsg(this, result.optString("message"));
                    }
                    succ = true;
                }

            }

        } else if (t.getParam("method").equals("getFileCloudToken")) {
            String token = null;
            if (t.json != null) {
                JSONObject results = t.json.optJSONObject("results");
                if (results != null && results.optInt("success", 0) == 1) {
                    token = results.optString("token", null);
                }
            }
            uploadClubLogo(token);
        } else if (t.getParam("method").equals("createDiscussionZone")) {
            String token = null;
            if (t.json != null) {
                StrUtil.showMsg(mActivity, "成功创建俱乐部并开通讨论区");
            }

        }

        if (succ) {
            resultJson = t.json.optJSONObject("results");
            //createClubWithADiscussion(resultJson.optString("guid"));
            setViewContent();

        }
        hideWait();
    }

    public void uploadClubLogo(final String token) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (token == null) {
                    //updateClubSetting();
                } else {
                    String uptoken = token;
                    String key = IO.UNDEFINED_KEY;
                    PutExtra extra = new PutExtra();
                    extra.params = new HashMap<String, String>();
                    extra.params.put("x:a", "");
                    Uri uri = Uri.fromFile(new File(imageFilePath));
                    IO.putFile(MyCreateClubNameActivity.this, uptoken, key, uri,
                            extra, new JSONObjectRet() {
                                @Override
                                public void onProcess(long current, long total) {

                                }

                                @Override
                                public void onSuccess(JSONObject resp) {
                                    String hash = resp.optString("hash", "");
                                    imageFileHash = hash;
                                    //updateClubSetting();
                                }

                                @Override
                                public void onFailure(Exception ex) {
                                    imageFileHash = null;
                                    //updateClubSetting();
                                }
                            });
                }

            }
        });

    }

    private void setViewContent() {

        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                intent.putExtra("jsonobject", resultJson.toString());

                intent.setClass(MyCreateClubNameActivity.this,
                        MyClubManagementActivity.class);
                startActivity(intent);
            }
        });
    }

    private void commit() {

        Pdtask task = new Pdtask(this, this, Constants.SERVICE_HOST, null,
                RequestHelper.Type_PostParam, null, 0, true);
        task.addParam("method", "insertClub");// 页码
        task.addParam("user_guid", UserMangerHelper.getDefaultUserGuid());// 页码
        task.addParam("user_session_guid",
                UserMangerHelper.getDefaultUserLongsession());
        task.addParam("name", clubName);// 页码
        task.addParam("type_id", club_type_id);
        task.addParam("type_name", club_type_name);
        task.addParam("logo_url",iconUrl);
        task.addParam("profile",clubName);
        task.addParam("logohash", imageFileHash);
        SOApplication.getDownLoadManager().startTask(task);
    }

}
