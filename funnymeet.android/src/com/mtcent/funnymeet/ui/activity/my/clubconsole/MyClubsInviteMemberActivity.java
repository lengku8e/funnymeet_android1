package com.mtcent.funnymeet.ui.activity.my.clubconsole;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.SectionIndexer;
import android.widget.TextView;
import android.widget.Toast;

import com.mtcent.funnymeet.SOApplication;
import com.mtcent.funnymeet.config.Constants;
import com.mtcent.funnymeet.ui.activity.base.BaseActivity;
import com.mtcent.funnymeet.ui.helper.RequestHelper;
import com.mtcent.funnymeet.ui.view.dialog.CustomDialog;
import com.mtcent.funnymeet.ui.helper.RequestHelper.Pdtask;
import com.mtcent.funnymeet.ui.view.control.IndexableListView;
import com.mtcent.funnymeet.ui.view.control.LetterImageView;
import com.mtcent.funnymeet.util.StringMatcher;
import com.mtcent.funnymeet.ui.helper.UserMangerHelper;
import com.mtcent.funnymeet.ui.view.control.XVURLImageView;
import com.mtcent.funnymeet.util.NetUtil;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import mtcent.funnymeet.R;

@SuppressLint("HandlerLeak")
public class MyClubsInviteMemberActivity extends BaseActivity {


    IndexableListView indexableListView;
    ArrayList<ContactInfo> contactNameList;
    LayoutInflater inflater;
    TextView titleTextView;
    LinkedList<ContactInfo> selectedList;
    Handler handler;
    TextView finishButton;
    CustomDialog dialog;
    TextView my_clubs_invite_member_msg;
    XVURLImageView my_clubs_invite_member_clubicon;
    EditText my_clubs_invite_member_addtion;
    TextView my_clubs_invite_sms_cancel;
    TextView my_clubs_invite_sms_confirm;
    EditText my_clubs_invite_phones;
    Intent get_intent;
    String iconUrl;
    String clubName;
    String clubGuid;
    String clubId;

    private String dwz = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_clubs_invite_member_contact_list);
        init();
        new Thread(networkTask).start();
    }

    void showSmsDialog() {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                dialog.show();
            }
        });
    }

    void hideSmsDialog() {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                dialog.hide();
            }
        });
    }

    void inviteClubMemberByMobile(String phoneNumber) {
        Pdtask task = new Pdtask(this, this, Constants.SERVICE_HOST, null,
                RequestHelper.Type_PostParam, null, 0, true);
        task.addParam("method", "inviteByMobile");
        task.addParam("user_guid", UserMangerHelper.getDefaultUserGuid());
        task.addParam("mobile_phone", phoneNumber);
        task.addParam("club_guid", clubGuid);

        SOApplication.getDownLoadManager().startTask(task);
        showWait();
    }

    @Override
    public void onFinish(Pdtask t) {
        // Toast.makeText(MyClubsInviteMemberActivity.this, "邀请开始",
        // Toast.LENGTH_SHORT).show();
        // hideWait();
        // super.onFinish(t);
        MyClubsInviteMemberActivity.this.finish();
        boolean succ = false;
        if (t.getParam("method").equals("inviteByMobile")) {

            if (t.json != null) {

                if (t.json.optJSONArray("results") != null
                        && t.json.optString("status").equals("ok")) {
                    succ = true;
                }
            }
        }

        if (succ) {
            Toast.makeText(MyClubsInviteMemberActivity.this, "邀请成功",
                    Toast.LENGTH_SHORT).show();
            hideWait();
        }
        super.onFinish(t);
    }

    private void sendSMS(String phone_number, String sms_content) {

        SmsManager smsManager = SmsManager.getDefault();
        if (sms_content.length() > 70) {
            List<String> contents = smsManager.divideMessage(sms_content);
            for (String sms : contents) {
                smsManager.sendTextMessage(phone_number, null, sms, null, null);
            }
        } else {
            smsManager.sendTextMessage(phone_number, null, sms_content, null,
                    null);
        }
        Toast.makeText(MyClubsInviteMemberActivity.this, "邀请函已发送",
                Toast.LENGTH_SHORT).show();
    }

    protected void init() {

        get_intent = this.getIntent();
        iconUrl = get_intent.getStringExtra("iconUrl");
        clubName = get_intent
                .getStringExtra(InviteMemberSelectTypeActivity.EXTRA_PARAM_CLUBNAME);
        clubGuid = get_intent
                .getStringExtra(InviteMemberSelectTypeActivity.EXTRA_PARAM_CLUBGUID);
        clubId = get_intent
                .getStringExtra(InviteMemberSelectTypeActivity.EXTRA_PARAM_CLUBID);
        dialog = new CustomDialog(this);

        dialog.setContentView(R.layout.my_clubs_invite_sms_dialog);

        contactNameList = new ArrayList<ContactInfo>();
        selectedList = new LinkedList<ContactInfo>();

        my_clubs_invite_member_clubicon = (XVURLImageView) dialog
                .findViewById(R.id.my_clubs_invite_member_clubicon);
        my_clubs_invite_member_addtion = (EditText) dialog
                .findViewById(R.id.my_clubs_invite_member_addtion);
        my_clubs_invite_member_msg = (TextView) dialog
                .findViewById(R.id.my_clubs_invite_member_msg);
        my_clubs_invite_sms_cancel = (TextView) dialog
                .findViewById(R.id.my_clubs_invite_sms_cancel);
        my_clubs_invite_sms_confirm = (TextView) dialog
                .findViewById(R.id.my_clubs_invite_sms_confirm);

        my_clubs_invite_phones = (EditText) findViewById(R.id.my_clubs_invite_phones);
        my_clubs_invite_phones.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // do nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                changeFinishButtonStatus();
            }

            @Override
            public void afterTextChanged(Editable s) {
                //
            }

        });
        dialog.setCancelable(true);

        if (dwz == null) {
            dwz = Constants.SERVER_INVITE_LINK + clubId;
        }
        my_clubs_invite_member_msg.setText("【趣聚】您的朋友"
                + UserMangerHelper.getDefaultUserNickName() + "，邀请您加入“"
                + clubName + "”。请点击" + dwz);

        my_clubs_invite_member_clubicon.setImageUrl(get_intent
                .getStringExtra("iconUrl"));

        my_clubs_invite_sms_cancel
                .setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        hideSmsDialog();
                    }
                });

        my_clubs_invite_sms_confirm
                .setOnClickListener(new View.OnClickListener() {
                    private void handleInputPhones() {
                        if (getInputPhoneNumbers() <= 0) {
                            return;
                        }
                        String phones = my_clubs_invite_phones.getText()
                                .toString();
                        String phoneNumber = null;
                        String[] phoneNumbers = new String[] {};
                        if (phones.indexOf("+") < 0) {
                            phoneNumbers = new String[] { phones };
                        } else {
                            phoneNumbers = phones.split("\\+");
                        }
                        for (int i = 0; i < phoneNumbers.length; i++) {
                            phoneNumber = phoneNumbers[i];
                            String sms_content = my_clubs_invite_member_msg
                                    .getText().toString()
                                    + my_clubs_invite_member_addtion.getText()
                                    .toString();
                            sendSMS(phoneNumber, sms_content);
                            inviteClubMemberByMobile(phoneNumber);
                        }
                    }

                    @Override
                    public void onClick(View v) {
                        handleInputPhones();
                        if (selectedList.size() > 0) {
                            hideSmsDialog();
                            for (Iterator<ContactInfo> iter = selectedList
                                    .iterator(); iter.hasNext();) {
                                ContactInfo ci = iter.next();
                                String phone_number = ci.getContactMobile();
                                String sms_content = my_clubs_invite_member_msg
                                        .getText().toString()
                                        + my_clubs_invite_member_addtion
                                        .getText().toString();
                                sendSMS(phone_number, sms_content);
                                inviteClubMemberByMobile(phone_number);
                                // MyClubsInviteMemberActivity.this.finish();
                            }
                        }

                    }
                });

        handler = new Handler();
        View v = findViewById(R.id.left_backLayout);
        v.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                finish();
            }
        });

        finishButton = (TextView) findViewById(R.id.finishbutton);
        finishButton.setVisibility(View.VISIBLE);
        finishButton.setBackgroundResource(R.drawable.green_btn_disable);

        finishButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                int numbers = getInputAndSelectedPhoneNumbers();
                if (numbers > 0) {
                    showSmsDialog();
                }
            }
        });

        titleTextView = (TextView) findViewById(R.id.titleTextView);
        titleTextView.setText("选择");

        // 取本机通讯录
        showWait();
        readPhoneList();
        hideWait();
    }

    private void readPhoneList() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ContentResolver resolver = MyClubsInviteMemberActivity.this
                        .getContentResolver();
                // 获取手机联系人
                Cursor phoneCursor = resolver.query(Phone.CONTENT_URI, null,
                        null, null, null); // 传入正确的uri
                if (phoneCursor != null) {
                    while (phoneCursor.moveToNext()) {
                        int nameIndex = phoneCursor
                                .getColumnIndex(Phone.DISPLAY_NAME); // 获取联系人name
                        String name = phoneCursor.getString(nameIndex);
                        String phoneNumber = phoneCursor.getString(phoneCursor
                                .getColumnIndex(Phone.NUMBER)); // 获取联系人number
                        if (TextUtils.isEmpty(phoneNumber)) {
                            continue;
                        }
                        contactNameList.add(new ContactInfo(name, null,
                                phoneNumber));
                    }
                }

                Uri uri = Uri.parse("content://icc/adn");
                phoneCursor = resolver.query(uri, null, null, null, null);
                if (phoneCursor != null) {
                    while (phoneCursor.moveToNext()) {
                        String name = phoneCursor.getString(phoneCursor
                                .getColumnIndex("name"));
                        String phoneNumber = phoneCursor.getString(phoneCursor
                                .getColumnIndex("number"));
                        if (TextUtils.isEmpty(phoneNumber)) {
                            continue;
                        }
                        contactNameList.add(new ContactInfo(name, null,
                                phoneNumber));
                    }
                }

                inflater = (LayoutInflater) MyClubsInviteMemberActivity.this
                        .getSystemService(LAYOUT_INFLATER_SERVICE);

                // Collections.sort(contactNameList, new
                // MyComparatorForContact());
                Collections.sort(contactNameList, new MyComparatorForContact());

                @SuppressLint("InflateParams")
                class MyIndexableListViewAdapter extends BaseAdapter implements
                        SectionIndexer {

                    private String mSections = "#ABCDEFGHIJKLMNOPQRSTUVWXYZ";

                    @Override
                    public int getCount() {
                        return contactNameList.size();
                    }

                    @Override
                    public Object getItem(int position) {
                        if (position < 0 || position >= getCount()) {
                            return null;
                        } else {
                            return contactNameList.get(position);
                        }

                    }

                    @Override
                    public long getItemId(int position) {
                        return position;
                    }

                    @Override
                    public View getView(int position, View convertView,
                                        ViewGroup parent) {
                        Tag tag = null;
                        if (convertView == null) {
                            tag = new Tag();
                            convertView = inflater
                                    .inflate(
                                            R.layout.my_clubs_invite_member_contact_list_item,
                                            null);
                            tag.letterImageView = (LetterImageView) convertView
                                    .findViewById(R.id.letterImageView);
                            tag.phone_contact_be_isselected = (TextView) convertView
                                    .findViewById(R.id.phone_contact_be_isselected);
                            tag.phoneContactName = (TextView) convertView
                                    .findViewById(R.id.phoneContactName);
                            tag.isselected = 0;
                            convertView.setTag(tag);

                        } else {
                            tag = (Tag) convertView.getTag();
                        }

                        ContactInfo ci = (ContactInfo) getItem(position);
                        tag.letterImageView.setLetter(ci.getFirstLetter()
                                .charAt(0));
                        tag.phone_contact_be_isselected
                                .setBackgroundResource(R.drawable.selecter_unselected_icon);
                        tag.phoneContactName.setText(ci.getContactName());
                        return convertView;
                    }

                    @Override
                    public int getPositionForSection(int section) {
                        // If there is no item for current section, previous
                        // section
                        // will be
                        // selected
                        for (int i = section; i >= 0; i--) {
                            for (int j = 0; j < getCount(); j++) {
                                if (i == 0) {
                                    // For numeric section
                                    for (int k = 0; k <= 9; k++) {
                                        ContactInfo ci = (ContactInfo) getItem(j);
                                        if (StringMatcher.match(String
                                                        .valueOf(ci.getFirstLetter()),
                                                String.valueOf(k)))
                                            return j;
                                    }
                                } else {
                                    ContactInfo ci = (ContactInfo) getItem(j);
                                    if (StringMatcher
                                            .match(String.valueOf(ci
                                                    .getFirstLetter()), String
                                                    .valueOf(mSections
                                                            .charAt(i))))
                                        return j;
                                }
                            }
                        }
                        return 0;
                    }

                    @Override
                    public int getSectionForPosition(int position) {
                        return position;
                    }

                    @Override
                    public Object[] getSections() {
                        String[] sections = new String[mSections.length()];
                        for (int i = 0; i < mSections.length(); i++)
                            sections[i] = String.valueOf(mSections.charAt(i));
                        return sections;
                    }

                }
                final MyIndexableListViewAdapter adapter = new MyIndexableListViewAdapter();
                indexableListView = (IndexableListView) findViewById(R.id.phone_contact_list);
                indexableListView.setAdapter(adapter);
                indexableListView.setFastScrollEnabled(true);

                indexableListView
                        .setOnItemClickListener(new OnItemClickListener() {

                            @Override
                            public void onItemClick(AdapterView<?> arg0,
                                                    View arg1, int arg2, long arg3) {
                                TextView phone_contact_be_isselected = (TextView) arg1
                                        .findViewById(R.id.phone_contact_be_isselected);
                                Tag get_tag = (Tag) arg1.getTag();
                                if (get_tag.isselected == 0) {

                                    phone_contact_be_isselected
                                            .setBackgroundResource(R.drawable.selecter_selected_icon);
                                    ContactInfo ci = (ContactInfo) adapter
                                            .getItem(arg2);
                                    selectedList.add(ci);
                                    get_tag.isselected++;
                                    changeFinishButtonStatus();
                                } else if (get_tag.isselected == 1) {
                                    ContactInfo ci = (ContactInfo) adapter
                                            .getItem(arg2);
                                    selectedList.remove(ci);
                                    phone_contact_be_isselected
                                            .setBackgroundResource(R.drawable.selecter_unselected_icon);
                                    get_tag.isselected--;
                                    changeFinishButtonStatus();
                                }

                            }
                        });

            }
        });
    }

    Runnable networkTask = new Runnable() {

        @Override
        public void run() {
            setDwz();
            Message msg = new Message();
            Bundle data = new Bundle();
            data.putString("dwz", dwz);
            msg.setData(data);
            dwzHandler.sendMessage(msg);
        }
    };

    Handler dwzHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            String val = data.getString("dwz");
            my_clubs_invite_member_msg.setText("【趣聚】您的朋友"
                    + UserMangerHelper.getDefaultUserNickName() + "，邀请您加入“"
                    + clubName + "”。请点击" + val);
            // UI界面的更新等相关操作
        }
    };

    private void setDwz() {
        String lurl = Constants.SERVER_INVITE_LINK + clubId + "&test=0";
        String encodedUrl = null;
        try {
            encodedUrl = URLEncoder.encode(lurl, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            encodedUrl = lurl;
        }

        // String uri = "http://6du.in/?is_api=1&lurl=" + lurl;
        String uri = "http://dwz.cn/create.php";
        String jsonStr = NetUtil.sendPost(uri, "url=" + encodedUrl);
        JSONObject jsonDwz = null;
        try {
            jsonDwz = new JSONObject(jsonStr);
            dwz = jsonDwz.optString("tinyurl");
        } catch (JSONException e1) {
            dwz = lurl;
            e1.printStackTrace();
        }

        // try {
        // //dwz = getUrl(uri);
        // dwz = sendPost(uri, "url=" + lurl);
        // } catch (Exception e) {
        // e.printStackTrace();
        // dwz = null;
        // }
    }

    public byte[] getUrlBytes(String urlSpec) throws IOException {
        URL url = new URL(urlSpec);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = conn.getInputStream();
            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return null;
            }
            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            while ((bytesRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, bytesRead);
            }
            out.close();
            return out.toByteArray();
        } finally {
            conn.disconnect();
        }
    }

    public String getUrl(String urlSpec) throws IOException {
        return new String(getUrlBytes(urlSpec));
    }

    private void changeFinishButtonStatus() {

        handler.post(new Runnable() {

            @Override
            public void run() {
                int inputNumber = getInputPhoneNumbers();

                int selectedNumber = selectedList.size();
                int number = inputNumber + selectedNumber;
                if (number == 0) {
                    finishButton
                            .setBackgroundResource(R.drawable.green_btn_disable);
                    finishButton.setText("完成");
                } else {
                    finishButton
                            .setBackgroundResource(R.drawable.green_btn_style);
                    finishButton.setText("完成" + "(" + number + ")");
                }

            }

        });
    }

    private int getInputAndSelectedPhoneNumbers() {
        int inputNumber = getInputPhoneNumbers();

        int selectedNumber = selectedList.size();
        return inputNumber + selectedNumber;
    }

    private int getInputPhoneNumbers() {
        String input = my_clubs_invite_phones.getText().toString().trim();

        if (input == null || input.length() == 0) {
            return 0;
        } else if (input.indexOf("+") < 0) {
            return 1;
        } else {
            String[] phones = input.split("\\+");
            return phones.length;
        }
    }
}

class ContactInfo implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -1490454166293013304L;
    private String contactName;
    private String contactEmail;
    private String contactMobile;
    private String firstLetter;

    private String[] pinyin;

    private HanyuPinyinOutputFormat format = null;

    public ContactInfo(String contactName, String contactEmail,
                       String contactMobile) {
        super();
        this.contactName = contactName;
        this.contactEmail = contactEmail;
        this.contactMobile = contactMobile;

        pinyin = null;
        format = new HanyuPinyinOutputFormat();
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);

        if (this.contactName == null || this.contactName.length() ==0) {
            this.firstLetter = "";
            return;
        } else  {
            try {
                pinyin = PinyinHelper.toHanyuPinyinStringArray(
                        this.contactName.charAt(0), format);
            } catch (BadHanyuPinyinOutputFormatCombination e) {
                e.printStackTrace();
            }

            // 如果c不是汉字，toHanyuPinyinStringArray会返回null

            if (pinyin == null) {
                this.firstLetter = String.valueOf(contactName.charAt(0))
                        .toUpperCase();
            } else {
                this.firstLetter = String.valueOf(pinyin[0].charAt(0))
                        .toUpperCase();
            }
        }
    }

    public String getContactName() {
        return contactName;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public String getContactMobile() {
        return contactMobile;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public void setContactMobile(String contactMobile) {
        this.contactMobile = contactMobile;
    }

    public String getFirstLetter() {
        return firstLetter;
    }

}

class MyComparatorForContact implements Comparator<ContactInfo> {

    @Override
    public int compare(ContactInfo lhs, ContactInfo rhs) {
        return lhs.getFirstLetter().compareTo(rhs.getFirstLetter());
    }

}

class Tag {

    public TextView phoneContactName;
    public TextView phone_contact_be_isselected;
    public LetterImageView letterImageView;
    public int isselected;

}