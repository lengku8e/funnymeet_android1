package com.mtcent.funnymeet.ui.activity.my.clubconsole;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.mtcent.funnymeet.SOApplication;
import com.mtcent.funnymeet.config.Constants;
import com.mtcent.funnymeet.ui.activity.base.BaseActivity;
import com.mtcent.funnymeet.ui.helper.RequestHelper;
import com.mtcent.funnymeet.ui.view.dialog.CustomDialog;
import com.mtcent.funnymeet.ui.helper.RequestHelper.Pdtask;
import com.mtcent.funnymeet.ui.helper.UserMangerHelper;
import com.mtcent.funnymeet.ui.view.control.XVURLImageView;
import com.mtcent.funnymeet.util.StrUtil;
import com.qiniu.auth.JSONObjectRet;
import com.qiniu.io.IO;
import com.qiniu.io.PutExtra;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;

import mtcent.funnymeet.R;

public class MyClubDetailActivity extends BaseActivity implements
        CloseClubConformDialogFragment.MessageInputListener {

    public static final int MYCLUBDETAILACTIVITY_REQUESTCODE = 1234;

    TextView club_detail_clubname;
    TextView club_detail_clubtype;
    TextView titleTextView;
    Intent get_intent;
    LinearLayout my_clubs_clubinfo_simple_clubqrcode;
    LinearLayout my_clubs_dialog_frame;
    LinearLayout to_club_type_select;
    LinearLayout to_change_club_name;// 修改俱乐部名称
    String clubName;
    JSONObject clubInfoJson;
    CustomDialog clubType;
    LinearLayout type_container;
    LayoutInflater inflater;
    JSONArray clubTypeList;
    JSONObject clubSettingInfo;
    ToggleButton mTogBtn;
    TextView club_setting_member_size;
    TextView exit_club;

    ImageView my_clubs_modify_fakeicon;
    XVURLImageView my_clubs_modify_realicon;
    String imageFilePath = "";
    String imageFileHash = null;
    boolean faceHasChange = false;

    public static final int EXIT_CLUB_CODE = 1430;
    public static final int CLOSE_CLUB_CODE = 1431;

    String club_name, type_id, is_searchable, profile, logohash, logo_url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_clubs_clubinfo_simple);
        init();
        getClubSetting();
        requestData();

    }

    void showClubTypeDialog() {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                clubType.show();
            }
        });
    }

    void hideClubTypeDialog() {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                clubType.hide();
            }
        });
    }

    protected void init() {

        exit_club = (TextView) findViewById(R.id.exit_club);

        exit_club.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                closeClub();
            }
        });

        club_setting_member_size = (TextView) findViewById(R.id.club_setting_member_size);

        mTogBtn = (ToggleButton) findViewById(R.id.mTogBtn);

        mTogBtn.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton arg0, boolean arg1) {

                String is_searchable_modify = null;
                if (arg1) {
                    is_searchable_modify = "1";

                } else {
                    is_searchable_modify = "0";
                }
                if (is_searchable_modify != null) {
                    updateClubSetting(club_name, type_id, is_searchable_modify,
                            profile, logohash);
                }

            }
        });

        club_detail_clubtype = (TextView) findViewById(R.id.club_detail_clubtype);

        View v = findViewById(R.id.left_backLayout);
        v.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                finish();
            }
        });

        to_change_club_name = (LinearLayout) findViewById(R.id.to_change_club_name);
        to_change_club_name.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                Intent intent = new Intent();
                intent.putExtra("clubJson", clubSettingInfo.toString());
                intent.setClass(MyClubDetailActivity.this,
                        MyClubDetailChangeClubNameActivity.class);
                startActivityForResult(intent,
                        MyClubDetailChangeClubNameActivity.ID);
            }
        });

        clubType = new CustomDialog(this);
        clubType.setContentView(R.layout.my_clubs_detail_clubtype_dialog);
        clubType.setCancelable(true);

        my_clubs_dialog_frame = (LinearLayout) clubType
                .findViewById(R.id.my_clubs_dialog_frame);

        my_clubs_dialog_frame.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                hideClubTypeDialog();
            }
        });

        inflater = (LayoutInflater) MyClubDetailActivity.this
                .getSystemService(LAYOUT_INFLATER_SERVICE);

        type_container = (LinearLayout) clubType
                .findViewById(R.id.type_container);

        get_intent = this.getIntent();

        try {
            clubInfoJson = new JSONObject(
                    get_intent.getStringExtra("clubInfoJson"));
        } catch (JSONException e) {

            e.printStackTrace();
        }

        my_clubs_clubinfo_simple_clubqrcode = (LinearLayout) findViewById(R.id.my_clubs_clubinfo_simple_clubqrcode);

        my_clubs_clubinfo_simple_clubqrcode
                .setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent();
                        intent.putExtra("clubJson", clubSettingInfo.toString());
                        intent.setClass(MyClubDetailActivity.this,
                                MyClubDetailInfoQrCodeActivity.class);
                        startActivity(intent);
                    }
                });

        my_clubs_modify_fakeicon = (ImageView) findViewById(R.id.my_clubs_modify_fakeicon);

        my_clubs_modify_fakeicon.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent intent = new Intent();
                intent.setClass(MyClubDetailActivity.this,
                        SelectImageActivity.class);
                intent.putExtra("path", imageFilePath);
                intent.putExtra("w", 400);
                intent.putExtra("h", 400);
                startActivityForResult(intent, MYCLUBDETAILACTIVITY_REQUESTCODE);
            }
        });

        my_clubs_modify_realicon = (XVURLImageView) findViewById(R.id.my_clubs_modify_realicon);
        my_clubs_modify_realicon.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent intent = new Intent();
                intent.setClass(MyClubDetailActivity.this,
                        SelectImageActivity.class);
                intent.putExtra("path", imageFilePath);
                intent.putExtra("w", 400);
                intent.putExtra("h", 400);
                startActivityForResult(intent, MYCLUBDETAILACTIVITY_REQUESTCODE);
            }
        });

        titleTextView = (TextView) findViewById(R.id.titleTextView);
        titleTextView.setText("俱乐部设置");

        club_detail_clubname = (TextView) findViewById(R.id.club_detail_clubname);

        to_club_type_select = (LinearLayout) findViewById(R.id.to_club_type_select);

        to_club_type_select.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                showClubTypeDialog();
            }
        });
    }

    @Override
    public void onFinish(Pdtask t) {

        super.onFinish(t);

        boolean succ = false;
        if (t.getParam("method").equals("listClubType")) {

            if (t.json != null) {
                if (t.json.optJSONArray("results") != null
                        && t.json.optString("status").equals("ok")) {

                    clubTypeList = t.json.optJSONArray("results");
                    succ = true;
                }
            }
        } else if (t.getParam("method").equals("findClubByGuid")) {

            JSONObject result = t.json.optJSONObject("results");
            if (result != null) {
                clubSettingInfo = result;
                setViewContent();
            }
        } else if (t.getParam("method").equals("updateClubSetting")) {

            clubSettingInfo = t.json.optJSONObject("results");
            StrUtil.showMsg(MyClubDetailActivity.this,
                    t.json.optString("status"));
            setViewContent();

        } else if (t.getParam("method").equals("quitClub")) {
            if (t.json != null) {
                StrUtil.showMsg(MyClubDetailActivity.this, t.json
                        .optJSONObject("results").optString("message"));
                if (t.json.optJSONObject("results").optString("message")
                        .equals("退出成功")) {
                    setResult(EXIT_CLUB_CODE);
                    finish();
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
        } else if (t.getParam("method").equals("closeClub")) {
            if (t.json != null) {
                StrUtil.showMsg(MyClubDetailActivity.this, t.json
                        .optJSONObject("results").optString("message"));
                if (t.json.optJSONObject("results").optString("success")
                        .equals("1")) {
                    setResult(CLOSE_CLUB_CODE);
                    SOApplication.setClubUpdated(true);
                    finish();
                }
            }
        }

        hideWait();
        if (succ) {
            reFreshDialog();
        }
    }

    public void uploadClubLogo(final String token) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (token == null) {
                    updateClubSetting();
                } else {
                    String uptoken = token;
                    String key = IO.UNDEFINED_KEY;
                    PutExtra extra = new PutExtra();
                    extra.params = new HashMap<String, String>();
                    extra.params.put("x:a", "");
                    Uri uri = Uri.fromFile(new File(imageFilePath));
                    IO.putFile(MyClubDetailActivity.this, uptoken, key, uri,
                            extra, new JSONObjectRet() {
                                @Override
                                public void onProcess(long current, long total) {

                                }

                                @Override
                                public void onSuccess(JSONObject resp) {
                                    String hash = resp.optString("hash", "");
                                    imageFileHash = hash;
                                    updateClubSetting();
                                }

                                @Override
                                public void onFailure(Exception ex) {
                                    imageFileHash = null;
                                    updateClubSetting();
                                }
                            });
                }

            }
        });

    }

    void setViewContent() {

        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                club_name = clubSettingInfo.optString("name");
                type_id = clubSettingInfo.optString("typeId");
                is_searchable = clubSettingInfo.optString("isSearchable");
                profile = clubSettingInfo.optString("profile");
                logo_url = clubSettingInfo.optString("logoUrl");
                logohash = clubSettingInfo.optString("logohash");
                if (logohash != null && logo_url != null
                        && logo_url.length() > 1) {
                    my_clubs_modify_realicon.setImageUrl(logo_url);
                    my_clubs_modify_realicon.setVisibility(View.VISIBLE);
                    my_clubs_modify_fakeicon.setVisibility(View.GONE);
                } else {
                    my_clubs_modify_realicon.setVisibility(View.GONE);
                    my_clubs_modify_fakeicon.setVisibility(View.VISIBLE);
                }

                club_detail_clubname.setText(club_name);
                club_setting_member_size.setText(clubSettingInfo
                        .optString("memberCount") + "人");
                String clubType = null;
                if (clubSettingInfo.optString("typeId").equals("1")) {
                    clubType = "公众俱乐部";
                } else if (clubSettingInfo.optString("typeId").equals("2")) {
                    clubType = "私人俱乐部";
                }
                club_detail_clubtype.setText(clubType);

                if (is_searchable.equals("0")) {
                    mTogBtn.setChecked(false);
                } else {
                    mTogBtn.setChecked(true);
                }
            }
        });
    }

    void reFreshDialog() {

        runOnUiThread(new Runnable() {

            @SuppressLint("InflateParams")
            @Override
            public void run() {

                type_container = (LinearLayout) clubType
                        .findViewById(R.id.type_container);

                for (int i = 0; i < clubTypeList.length(); i++) {
                    View v = inflater
                            .inflate(
                                    R.layout.my_clubs_detail_clubtype_dialog_item,
                                    null);

                    final JSONObject jo = clubTypeList.optJSONObject(i);

                    TextView club_type_item = (TextView) v
                            .findViewById(R.id.club_type_item);

                    club_type_item.setText(jo.optString("name"));

                    v.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View arg0) {

                            String type = jo.optString("id");
                            updateClubSetting(club_name, type, is_searchable,
                                    profile, logohash);
                            hideClubTypeDialog();
                        }
                    });

                    type_container.addView(v);
                }
            }
        });

    }

    private void exitAndClose() {

        Pdtask task = new Pdtask(this, this, Constants.SERVICE_HOST, null,
                RequestHelper.Type_PostParam, null, 0, true);
        task.addParam("method", "quitClub");
        task.addParam("user_guid", UserMangerHelper.getDefaultUserGuid());
        task.addParam("user_session_guid",
                UserMangerHelper.getDefaultUserLongsession());
        if (!clubInfoJson.optString("clubGuid").equals("")) {
            task.addParam("club_guid", clubInfoJson.optString("clubGuid"));
        } else if (!clubInfoJson.optString("guid").equals("")) {
            task.addParam("club_guid", clubInfoJson.optString("guid"));
        }
        SOApplication.getDownLoadManager().startTask(task);
        showWait();
    }

    private void closeClub() {
        CloseClubConformDialogFragment dialog = new CloseClubConformDialogFragment();
        dialog.show(getFragmentManager(), "confirminvitaion");

        // Pdtask task = new Pdtask(this, this, SOApplication.SERVICE_HOST, null,
        // RequestHelper.Type_PostParam, null, 0, true);
        // task.addParam("method", "closeClub");
        // task.addParam("user_guid", UserMangerHelper.getDefaultUserGuid());
        // task.addParam("user_session_guid",
        // UserMangerHelper.getDefaultUserLongsession());
        // if (!clubInfoJson.optString("clubGuid").equals("")) {
        // task.addParam("club_guid", clubInfoJson.optString("clubGuid"));
        // } else if (!clubInfoJson.optString("guid").equals("")) {
        // task.addParam("club_guid", clubInfoJson.optString("guid"));
        // }
        // SOApplication.getDownLoadManager().startTask(task);
        // showWait();
    }

    void requestData() {

        Pdtask task = new Pdtask(this, this, Constants.SERVICE_HOST, null,
                RequestHelper.Type_PostParam, null, 0, true);
        task.addParam("method", "listClubType");// 页码
        SOApplication.getDownLoadManager().startTask(task);
        showWait();
    }

    void getClubSetting() {

        Pdtask task = new Pdtask(this, this, Constants.SERVICE_HOST, null,
                RequestHelper.Type_PostParam, null, 0, true);
        task.addParam("method", "findClubByGuid");// 页码
        task.addParam("user_guid", UserMangerHelper.getDefaultUserGuid());
        task.addParam("user_session_guid",
                UserMangerHelper.getDefaultUserLongsession());
        if (!clubInfoJson.optString("clubGuid").equals("")) {
            task.addParam("club_guid", clubInfoJson.optString("clubGuid"));
        } else if (!clubInfoJson.optString("guid").equals("")) {
            task.addParam("club_guid", clubInfoJson.optString("guid"));
        }

        SOApplication.getDownLoadManager().startTask(task);
        showWait();

    }

    void updateClubSetting(String name, String type_id, String is_searchable,
                           String profile, String logo_url) {

        Pdtask task = new Pdtask(this, this, Constants.SERVICE_HOST, null,
                RequestHelper.Type_PostParam, null, 0, true);
        task.addParam("method", "updateClubSetting");// 页码
        task.addParam("user_guid", UserMangerHelper.getDefaultUserGuid());
        task.addParam("user_session_guid",
                UserMangerHelper.getDefaultUserLongsession());
        if (!clubInfoJson.optString("clubGuid").equals("")) {
            task.addParam("club_guid", clubInfoJson.optString("clubGuid"));
        } else if (!clubInfoJson.optString("guid").equals("")) {
            task.addParam("club_guid", clubInfoJson.optString("guid"));
        }
        task.addParam("name", name);
        task.addParam("type_id", type_id);
        task.addParam("is_searchable", is_searchable);
        task.addParam("profile", profile);
        task.addParam("logo_url", logo_url);

        SOApplication.getDownLoadManager().startTask(task);
        showWait();
    }

    private void updateClubSetting() {

        Pdtask task = new Pdtask(this, this, Constants.SERVICE_HOST, null,
                RequestHelper.Type_PostParam, null, 0, true);
        task.addParam("method", "updateClubSetting");// 页码
        task.addParam("user_guid", UserMangerHelper.getDefaultUserGuid());
        task.addParam("user_session_guid",
                UserMangerHelper.getDefaultUserLongsession());
        if (!clubInfoJson.optString("clubGuid").equals("")) {
            task.addParam("club_guid", clubInfoJson.optString("clubGuid"));
        } else if (!clubInfoJson.optString("guid").equals("")) {
            task.addParam("club_guid", clubInfoJson.optString("guid"));
        }
        task.addParam("name", club_name);
        task.addParam("type_id", type_id);
        task.addParam("is_searchable", is_searchable);
        task.addParam("profile", profile);
        task.addParam("logohash", imageFileHash);
        // task.addParam("logo_url", logo_url);

        SOApplication.getDownLoadManager().startTask(task);
        showWait();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MyClubDetailChangeClubNameActivity.ID) {
            getClubSetting();

        } else if (requestCode == 556 && resultCode == RESULT_FIRST_USER) {
            setResult(RESULT_FIRST_USER);
            finish();
        } else if (resultCode == Activity.RESULT_OK) {
            if (requestCode == MYCLUBDETAILACTIVITY_REQUESTCODE && null != data) {
                imageFilePath = data.getStringExtra("path");
                imageFileHash = null;
                Options options = new Options();
                Bitmap bitmap = BitmapFactory
                        .decodeFile(imageFilePath, options);// 解码图片
                faceHasChange = true;
                my_clubs_modify_realicon.setImageUrl(null);
                my_clubs_modify_realicon.setImageBitmap(bitmap);
                my_clubs_modify_realicon.setVisibility(View.VISIBLE);
                my_clubs_modify_fakeicon.setVisibility(View.GONE);
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
        } else {
            updateClubSetting();
        }
        showWait();
    }

    @Override
    public void onMessageInputComplete(String result) {
        String clubGuid = null;
        if (!clubInfoJson.optString("clubGuid").equals("")) {
            clubGuid = clubInfoJson.optString("clubGuid");
        } else if (!clubInfoJson.optString("guid").equals("")) {
            clubGuid = clubInfoJson.optString("guid");
        }
        if (CloseClubConformDialogFragment.CONFIRM_RESULT_YES.equals(result)) {
            Pdtask task = new Pdtask(this, this, Constants.SERVICE_HOST, null,
                    RequestHelper.Type_PostParam, null, 0, true);
            task.addParam("method", "closeClub");
            task.addParam("user_guid", UserMangerHelper.getDefaultUserGuid());
            task.addParam("club_guid", clubGuid);

            SOApplication.getDownLoadManager().startTask(task);
            showWait();
        }
    }
}

@SuppressLint("InflateParams")
class CloseClubConformDialogFragment extends DialogFragment {
    public final static String CONFIRM_RESULT_YES = "y"; // 解散
    public final static String CONFIRM_RESULT_NO = "n"; // 不解散
    private TextView mMessage;

    public interface MessageInputListener {
        void onMessageInputComplete(String result);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_yesno, null);
        mMessage = (TextView) view.findViewById(R.id.message);
        mMessage.setText("是否解散俱乐部?");

        builder.setView(view)
                .setPositiveButton("确认解散",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                MessageInputListener listener = (MessageInputListener) getActivity();
                                listener.onMessageInputComplete(CloseClubConformDialogFragment.CONFIRM_RESULT_YES);
                            }
                        })
                .setNegativeButton("不解散",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                MessageInputListener listener = (MessageInputListener) getActivity();
                                listener.onMessageInputComplete(CloseClubConformDialogFragment.CONFIRM_RESULT_NO);
                            }
                        });
        return builder.create();
    }

}
