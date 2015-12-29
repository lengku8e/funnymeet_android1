package com.mtcent.funnymeet.ui.activity.discovery;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.mtcent.funnymeet.ui.helper.RequestHelper;
import com.mtcent.funnymeet.ui.view.dialog.CustomDialog;
import com.mtcent.funnymeet.ui.view.fragment.ChildDiscussionFragment;
import com.viewpagerindicator.TabPageIndicator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import mtcent.funnymeet.R;

public class DiscussTopicListActivity extends FragmentActivity implements RequestHelper.DownBack {


    LayoutInflater inflater;
    PopupWindow popupMenu;
    Intent get_intent;

    JSONArray titleList;
    JSONObject jsonObject;
    ViewPager pager;
    TabPageIndicator indicator;
    FragmentPagerAdapter adapter;
    final static int CREATE_ONE_NEW_POST_REQUESTCODE = 1816;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_find_topiclist);
        init();
    }

    @Override
    public void onFinish(RequestHelper.Pdtask t) {

        JSONObject json = t.json;
        boolean succ = false;


        hideWait();

    }

    @Override
    public void onUpdate(RequestHelper.Pdtask t) {

    }


    private void init() {

        View v = findViewById(R.id.left_backLayout);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                finish();
            }
        });

        get_intent = DiscussTopicListActivity.this.getIntent();

        final TextView discuss_name = (TextView) this.findViewById(R.id.discuss_name);
        final TextView number_of_members = (TextView) this.findViewById(R.id.number_of_members);
        final TextView number_of_posts = (TextView) this.findViewById(R.id.number_of_posts);

        if (get_intent != null) {

            try {
                titleList = new JSONArray(get_intent.getStringExtra("jsonArray"));
                jsonObject = new JSONObject(get_intent.getStringExtra("jsonObject"));
                discuss_name.setText(jsonObject.optString("name"));
//                number_of_members.setText(jsonObject.optString(""));
//                number_of_posts.setText(jsonObject.optString(""));

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        if (titleList == null) {
            titleList = new JSONArray();
        }


        final LinearLayout create_new_post = (LinearLayout) this.findViewById(R.id.create_new_post);
        create_new_post.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                Intent intent = new Intent();
                intent.setClass(DiscussTopicListActivity.this, CreateOneNewPostActivity.class);
                intent.putExtra("titleList", titleList.toString());
                intent.putExtra("jsonObject", jsonObject.toString());
                startActivityForResult(intent, CREATE_ONE_NEW_POST_REQUESTCODE);
//                startActivity(intent);

            }
        });

        inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);

        final TextView child_filter = (TextView) this.findViewById(R.id.tab_title_child);
        child_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initChildFilter(view);
            }
        });

        final LinearLayout create_post_icon = (LinearLayout) this.findViewById(R.id.create_new_post);
        create_post_icon.setVisibility(View.VISIBLE);


        final TextView titleTextView = (TextView) this.findViewById(R.id.titleTextView);
        titleTextView.setText("讨论区");

        //ViewPager的adapter
        adapter = new TabPageIndicatorAdapter(getSupportFragmentManager());
        pager = (ViewPager) DiscussTopicListActivity.this.findViewById(R.id.pager);
        pager.setAdapter(adapter);


        //实例化TabPageIndicator然后设置ViewPager与之关联
        indicator = (TabPageIndicator) DiscussTopicListActivity.this.findViewById(R.id.indicator);
        indicator.setViewPager(pager);

        //如果我们要对ViewPager设置监听，用indicator设置就行了
        indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {
                String title = titleList.optJSONObject(arg0).optString("name");
                Toast.makeText(getApplicationContext(), title, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });


    }

    /**
     * ViewPager适配器
     *
     * @author len
     */
    class TabPageIndicatorAdapter extends FragmentPagerAdapter {
        public TabPageIndicatorAdapter(FragmentManager fm) {
            super(fm);
        }


        @Override
        public Fragment getItem(int position) {
            //新建一个Fragment来展示ViewPager item的内容，并传递参数
            Fragment fragment = new ChildDiscussionFragment();
            Bundle args = new Bundle();
            JSONObject j = titleList.optJSONObject(position);
            args.putString("board_guid", j.optString("guid"));
            fragment.setArguments(args);

            return fragment;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            String title = titleList.optJSONObject(position % titleList.length()).optString("name");
            return title;
        }

        @Override
        public int getCount() {
            return titleList.length();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (resultCode) {
            case CREATE_ONE_NEW_POST_REQUESTCODE:
                ChildDiscussionFragment.mPtrFrame.autoRefresh();
                break;
        }
    }

    private void initChildFilter(View trigger) {

        View popUpMenuFrame = inflater.inflate(R.layout.new_find_child_topic_layout, null);

        popupMenu = new PopupWindow(popUpMenuFrame, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        popupMenu.setFocusable(true);
        popupMenu.setBackgroundDrawable(new ColorDrawable(0xaaffffff));
        popupMenu.showAsDropDown(trigger);
        popupMenu.setOutsideTouchable(false);
        popupMenu.update();

    }

    CustomDialog waitDialog = null;

    public void showWait() {
        showWait("加载中...");
    }

    public void showWait(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (waitDialog == null) {
                    waitDialog = CustomDialog.createWaitDialog(DiscussTopicListActivity.this, msg,
                            false);
                }
                waitDialog.setCancelable(false);
                waitDialog.findViewById(R.id.outside).setOnClickListener(null);
                waitDialog.findViewById(R.id.progressBar1).setVisibility(
                        View.VISIBLE);

                ((TextView) waitDialog.findViewById(R.id.textView1))
                        .setText(msg);
                waitDialog.show();
            }
        });
    }

    public void showWaitToMsg(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (waitDialog == null) {
                    waitDialog = CustomDialog.createWaitDialog(DiscussTopicListActivity.this, msg,
                            false);
                }
                waitDialog.setCancelable(true);
                waitDialog.findViewById(R.id.outside).setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View arg0) {
                                waitDialog.dismiss();
                            }
                        });
                waitDialog.findViewById(R.id.progressBar1).setVisibility(
                        View.GONE);
                ((TextView) waitDialog.findViewById(R.id.textView1))
                        .setText(msg);
                waitDialog.show();
            }
        });
    }

    public void hideWait() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (waitDialog != null) {
                    waitDialog.dismiss();
                }
            }
        });
    }
}




