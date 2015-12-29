package com.mtcent.funnymeet.ui.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mtcent.funnymeet.SOApplication;
import com.mtcent.funnymeet.config.Constants;
import com.mtcent.funnymeet.biz.service.CheckClubUpdateService;
import com.mtcent.funnymeet.biz.service.UpdateVersionService;
import com.mtcent.funnymeet.ui.activity.base.BaseActivity;
import com.mtcent.funnymeet.ui.view.control.FunnymeetAddressBookView;
import com.mtcent.funnymeet.ui.activity.club.FindNewClubActivity;
import com.mtcent.funnymeet.ui.view.control.FunnymeetFindView;
import com.mtcent.funnymeet.ui.view.control.FunnymeetHomeView;
import com.mtcent.funnymeet.ui.view.control.FunnymeetMyView;
import com.mtcent.funnymeet.config.CurrentVersion;
import com.mtcent.funnymeet.ui.activity.user.DefaultLoginActivity;
import com.mtcent.funnymeet.ui.activity.user.StartupActivity;
import com.mtcent.funnymeet.ui.view.control.FunnymeetBaseView;
import com.mtcent.funnymeet.ui.helper.RequestHelper;
import com.mtcent.funnymeet.ui.helper.RequestHelper.Pdtask;
import com.mtcent.funnymeet.ui.view.control.ScrollHPageWithTableExExView;
import com.mtcent.funnymeet.ui.view.control.ScrollHPageWithTableExExView.ScrollHPageWithTableAdapterExEx;
import com.mtcent.funnymeet.util.StrUtil;
import com.readystatesoftware.viewbadger.BadgeView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import mtcent.funnymeet.R;

public class MainActivity extends BaseActivity implements
        ScrollHPageWithTableAdapterExEx {
    public static final int Exit = 999;
    public static final int NeedLogin = 1000;
    public static final int SCANNIN_GREQUEST_CODE = 1001;
    public static final int FROM_MENU_TO_MYCLUBLIST = 1726;
    public static final String INTENT_ACTION_USER_LOGIN = "INTENT_ACTION_USER_LOGIN";
    public static final String APPID_SOHUODONG = "sohuodong";

    /**
     * 用户登录，切换后接收消息，更新MainActivity的数据显示
     */
    private BroadcastReceiver bcrUserLogin;

    private Activity mActivity;
    private ScrollHPageWithTableExExView scrollHPageWithTable;

    private FunnymeetFindView findView;
    private FunnymeetHomeView homeView;
    private FunnymeetAddressBookView clubView;
    private FunnymeetMyView myView;

    private String downPath = "http://" + Constants.SERVER_HOST_IP
            + "/file/";
    private ProgressDialog pBar;
    private Handler handler = new Handler();

    private LinearLayout newClub;

    @SuppressLint("HandlerLeak")
    private final class HandlerExtension extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            isExit = false;
        }
    }

    class ItemData {
        int iconId = 0;
        String name = "";

        public ItemData(int id, String namestr) {
            iconId = id;
            name = namestr;
        }
    }

    private String tables[] = {"活动", "俱乐部", "发现", "我的"};
    private int iconsNormal[] = {R.drawable.huodong_normal,
            R.drawable.club_normal, R.drawable.explore_normal,
            R.drawable.info_normal};
    private int iconsSelect[] = {R.drawable.huodong_selected,
            R.drawable.club_selected, R.drawable.explore_selected,
            R.drawable.info_selected};

    private int colorNormal[] = {0xff8b8b8b, 0xff8b8b8b, 0xff8b8b8b,
            0xff8b8b8b};
    private int colorSelect[] = {0xff45c01a, 0xff45c01a, 0xff45c01a,
            0xff45c01a};

    private FunnymeetBaseView pageView[] = {null, null, null, null};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = this;
        setContentView(R.layout.activity_main);

        showSOMainSplash();

        init();
        IntentFilter filter = new IntentFilter();
        filter.addAction(INTENT_ACTION_USER_LOGIN);
        this.registerReceiver(bcrUserLogin, filter);

        // 启动版本检查Service
        // Intent versionIntent = new Intent(this, UpdateVersionService.class);
        // this.startService(versionIntent);
        /*UpdateVersionService.setServiceAlarm(this, true);
        CheckClubUpdateService.setServiceAlarm(this, true);
        checkUpdate();*/
    }

    void showSOMainSplash() {
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, StartupActivity.class);
        startActivityForResult(intent, StartupActivity.ID);
    }

    private void checkUpdate() {
        String pkName = mActivity.getPackageName();
        int versionNum = 1;
        try {
            versionNum = mActivity.getPackageManager()
                    .getPackageInfo(pkName, 0).versionCode;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }

        Pdtask task = new Pdtask(this, this, Constants.SERVICE_HOST, null,
                RequestHelper.Type_PostParam, null, 0, true);

        task.addParam("method", "checkVersionOfAndroidApp");
        task.addParam("local_version_number", String.valueOf(versionNum));
        task.addParam("appid", APPID_SOHUODONG);

        SOApplication.getDownLoadManager().startTask(task);
    }

   /* public void onFinish(Pdtask t) {
        if (t.getParam("method").equals("checkVersionOfAndroidApp")) {
            boolean succ = false;
            String msg = "没有新版本";
            JSONObject version = null;
            int statue = 0;
            int versionNum = 0;
            String versionName = "";

            if (t.json != null) {
                JSONObject results = t.json.optJSONObject("results");
                if (results != null) {
                    version = results.optJSONObject("appVersion");
                    if (version == null) {
                        // 最新版本
                        msg = results.optString("message");
                    } else {
                        versionNum = version.optInt("versionNum");
                        versionName = version.optString("versionName");
                        msg = results.optString("message");
                        int su = results.optInt("success");
                        statue = results.optInt("statue", 0);
                        if (su == 1) {
                            succ = true;
                        } else if (results.has("message")) {
                            msg = results.optString("message");
                        }
                    }
                }
            }

            if (succ && version != null && statue == 1
                    && version.optString("fileUrl") != null) {
                int currentCode = 0;
                try {
                    currentCode = CurrentVersion.getVerCode(this);
                } catch (NameNotFoundException e) {
                    //
                }
                if (versionNum >= currentCode) {
                    SOApplication.setHasUpdate(true);
                    showUpdateDialog(versionName);
                } else {
                    StrUtil.showMsg(this, msg);
                }
            }
        }
        hideWait();
    }*/

    private void init() {

        scrollHPageWithTable = (ScrollHPageWithTableExExView) findViewById(R.id.scrollHPageWithTable);

        inflater = LayoutInflater.from(MainActivity.this);
        findViewById(R.id.left_backLayout).setClickable(false);
        TextView tv = (TextView) findViewById(R.id.titleTextView);
        tv.setText("趣聚");
        ImageView imageView = (ImageView) findViewById(R.id.left_back);
        imageView.setVisibility(View.GONE);
        findViewById(R.id.left_comment_frame).setVisibility(View.GONE);
        //新的俱乐部
        newClub = (LinearLayout) findViewById(R.id.add_club);
        newClub.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(mActivity, FindNewClubActivity.class);
                mActivity.startActivity(intent);
            }

        });

        homeView = new FunnymeetHomeView(this);
        pageView[0] = homeView;
        clubView = new FunnymeetAddressBookView(this);
        pageView[1] = clubView;
        findView = new FunnymeetFindView(this);
        pageView[2] = findView;
        myView = new FunnymeetMyView(this);
        pageView[3] = myView;

        scrollHPageWithTable.setScrollHPageWithTableAdapterExEx(this);

        // 设置更新标志
        View targetNormal = getNormalIconView(3);
        BadgeView badge = new BadgeView(this, targetNormal);
        badge.setTextSize(5);
        // badge.setBadgePosition(BadgeView.);
        // badge.setText("");
        // badge.show();

        bcrUserLogin = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                homeView.requestListUserProject();
                clubView.requestFavoriteClubList();
                myView.requestData();
            }
        };
    }

    // private CharSequence getTodayText() {
    // Date today = new Date();
    // DateFormat df = new SimpleDateFormat("yyyy-MM-dd EE", Locale.CHINA);
    // return df.format(today);
    // }

    public void onShow() {
        for (int i = 0; i < pageView.length; i++) {
            pageView[i].onShow();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Exit) {
            finish();
        } else if (resultCode == NeedLogin) {
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, DefaultLoginActivity.class);
            startActivityForResult(intent, DefaultLoginActivity.ID);
        } else if ((requestCode == DefaultLoginActivity.ID || requestCode == StartupActivity.ID)
                && resultCode == RESULT_CANCELED) {
            finish();
        } else if (requestCode == SCANNIN_GREQUEST_CODE) {
            if (data != null) {
                Bundle bundle = data.getExtras();
                if (findView != null && bundle != null) {
                    findView.onScannin(bundle.getString("result"),
                            (Bitmap) data.getParcelableExtra("bitmap"));
                }
            }

        } else if (requestCode == FunnymeetHomeView.TO_FORCED_CLUBHD_DETAILACTIVITY) {
            homeView.requestListUserProject();
        } else if (requestCode == FROM_MENU_TO_MYCLUBLIST) {
            clubView.requestFavoriteClubList();
            homeView.requestListUserProject();
        }
        onShow();
    }

    @Override
    protected void onDestroy() {
        for (FunnymeetBaseView baseView : pageView) {
            if (baseView != null) {
                baseView.onDestroy();
            }
        }
        this.unregisterReceiver(bcrUserLogin);

        super.onDestroy();
    }

    void initMain() {
        scrollHPageWithTable = (ScrollHPageWithTableExExView) findViewById(R.id.scrollHPageWithTable);
        scrollHPageWithTable.setScrollHPageWithTableAdapterExEx(this);
    }

    protected void onStart() {
        super.onStart();
    }

    protected void onResume() {
        super.onResume();
        onShow();
    }

    protected void onStop() {
        super.onStop();
    }

    @Override
    public int getPageCount() {
        return tables.length;
    }

    @Override
    public FunnymeetBaseView getClassBaseView(int index) {
        return pageView[index];
    }

    @Override
    public View getPageView(int index) {
        return pageView[index].getMainView();
    }

    @Override
    public String getTableTitle(int index) {
        return tables[index];
    }

    @Override
    public int getTableIconSelect(int index) {
        return iconsSelect[index];
    }

    @Override
    public int getTableIconNormal(int index) {
        return iconsNormal[index];
    }

    @Override
    public int getTableColorSelect(int index) {
        return colorSelect[index];
    }

    @Override
    public int getTableColorNormal(int index) {
        return colorNormal[index];
    }

    @Override
    public void onPageChange(int index) {
        // if (index == 0) {
        // StatService.onEvent(this, "clickOnProjectList", "onPageChange", 1);
        // } else if (index == 1) {
        // StatService.onEvent(this, "clickOnMyInvest", "onPageChange", 1);
        // } else if (index == 2) {
        // StatService.onEvent(this, "clickOnMyFinance", "onPageChange", 1);
        //
        // } else if (index == 3) {
        // StatService.onEvent(this, "clickOnMyAccount", "onPageChange", 1);
        // }
        // View[] normals = scrollHPageWithTable.getNormalIconView();
        // for (int i = 0; i < getPageCount(); i++) {
        // if (i == index) {
        // normals[index].setVisibility(View.GONE);
        // } else {
        // normals[index].setVisibility(View.VISIBLE);
        // }
        // }

        onShow();
    }

    @Override
    public View getNormalIconView(int index) {

        return scrollHPageWithTable.getNormalIconView()[index];
    }

    private void showUpdateDialog(final String versionName) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                StringBuffer sb = new StringBuffer();
                sb.append("发现新版本：" + versionName);
                sb.append("\n");
                sb.append("是否更新？");
                Dialog dialog = new AlertDialog.Builder(MainActivity.this)
                        .setTitle("软件更新")
                        .setMessage(sb.toString())
                        .setPositiveButton("更新",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        showProgressBar();// 更新当前版本
                                    }

                                })
                        .setNegativeButton("暂不更新",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                    }
                                }).create();
                dialog.show();
            }
        });
    }

    private void showProgressBar() {
        pBar = new ProgressDialog(MainActivity.this);
        pBar.setTitle("正在下载");
        pBar.setMessage("请稍后...");
        pBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        downAppFile(downPath + Constants.APP_NAME);
    }

    private void downAppFile(final String url) {
        pBar.show();
        new Thread() {
            public void run() {
                HttpClient client = new DefaultHttpClient();
                HttpGet get = new HttpGet(url);
                HttpResponse response;
                try {
                    response = client.execute(get);
                    HttpEntity entity = response.getEntity();
                    long length = entity.getContentLength();
                    Log.isLoggable("DownTag", (int) length);
                    InputStream is = entity.getContent();
                    FileOutputStream fileOutputStream = null;
                    if (is == null) {
                        throw new RuntimeException("isStream is null");
                    }
                    File file = new File(
                            Environment.getExternalStorageDirectory(),
                            Constants.APP_NAME);
                    fileOutputStream = new FileOutputStream(file);
                    byte[] buf = new byte[1024];
                    int ch = -1;
                    do {
                        ch = is.read(buf);
                        if (ch <= 0)
                            break;
                        fileOutputStream.write(buf, 0, ch);
                    } while (true);
                    is.close();
                    fileOutputStream.close();
                    haveDownLoad();
                } catch (ClientProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }.start();
    }

    private void haveDownLoad() {
        handler.post(new Runnable() {
            public void run() {
                pBar.cancel();
                // 弹出警告框 提示是否安装新的版本
                Dialog installDialog = new AlertDialog.Builder(
                        MainActivity.this)
                        .setTitle("下载完成")
                        .setMessage("是否安装新的应用")
                        .setPositiveButton("确定",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        installNewApk();
                                        finish();
                                    }

                                })
                        .setNegativeButton("取消",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        finish();
                                    }
                                }).create();
                installDialog.show();
            }
        });
    }

    private void installNewApk() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.fromFile(new File(Environment
                        .getExternalStorageDirectory(), Constants.APP_NAME)),
                "application/vnd.android.package-archive");
        startActivity(intent);
    }

    private boolean isExit = false;
    Handler mHandler = new HandlerExtension();

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    public void exit() {
        if (!isExit) {
            isExit = true;
            Toast.makeText(getApplicationContext(), "再按一次退出程序",
                    Toast.LENGTH_SHORT).show();
            mHandler.sendEmptyMessageDelayed(0, 2000);
        } else {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            startActivity(intent);
            System.exit(0);
        }
    }
}
