package com.mtcent.funnymeet.ui.activity.my.clubconsole;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mtcent.funnymeet.ui.view.control.CutImageView;
import com.mtcent.funnymeet.ui.view.control.CutImageView.CutImageListen;
import com.mtcent.funnymeet.util.StrUtil;

import java.io.File;

public class SelectImageActivity extends Activity {
	public final static int REQUEST_CODE = 123;
	Context mContext = null;
	CutImageView cutImageView;
	RelativeLayout mainLayout;

	final int REQUEST_CODE_photo = 121;
	final int REQUEST_CODE_file = 122;
	String imageFilePath2;
	int w;
	int h;
	private String photo_path;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		int menuH = StrUtil.dip2px(mContext, 48);

		// 最外层layout
		mainLayout = new RelativeLayout(mContext);
		mainLayout.setLayoutParams(new ViewGroup.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		setContentView(mainLayout);
		mainLayout.setBackgroundColor(Color.WHITE);
		// 图片剪切VIEW
		cutImageView = new CutImageView(mContext);
		cutImageView.listen = new CutImageListen() {

			@Override
			public void finsh(String path) {
				if (path != null) {
					Intent intent = new Intent();
					intent.putExtra("path", new String(path));
					SelectImageActivity.this.setResult(Activity.RESULT_OK,
							intent);
					finish();
				} else {
					cancle();
				}
			}

			@Override
			public void cancle() {
				SelectImageActivity.this.setResult(Activity.RESULT_CANCELED,
						null);
				finish();
			}
		};
		mainLayout.addView(cutImageView);
		Intent intent = getIntent();
		if (intent != null) {
			imageFilePath2 = intent.getStringExtra("path");
			w = intent.getIntExtra("w", 0);
			h = intent.getIntExtra("h", 0);
			cutImage(imageFilePath2);
		}

		// 拍照获取图片
		TextView getFormPhone = new TextView(mContext);
		getFormPhone.setText("拍照");
		getFormPhone.setTextColor(0xffffffff);
		getFormPhone.setTextSize(18);
		getFormPhone.setGravity(Gravity.CENTER);
		getFormPhone.setBackgroundDrawable(newSelector(mContext, 0x00000000,
				0xff333333, 0xff666666, 0x00000000));
		getFormPhone.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				getFaceFromPhoto();
			}
		});
		RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(
				StrUtil.dip2px(mContext, 100), menuH);
		param.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
		param.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
		// param.bottomMargin = StrUtil.dip2px(mContext, 5);
		// param.leftMargin = StrUtil.dip2px(mContext, 5);
		mainLayout.addView(getFormPhone, param);

		// 选择文件获取图片
		TextView getFormFile = new TextView(mContext);
		getFormFile.setText("选择文件");
		getFormFile.setTextColor(0xffffffff);
		getFormFile.setTextSize(18);
		getFormFile.setGravity(Gravity.CENTER);
		getFormFile.setBackgroundDrawable(newSelector(mContext, 0x00000000,
				0xff333333, 0xff666666, 0x00000000));
		getFormFile.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				getFaceFromFile();
			}
		});
		param = new RelativeLayout.LayoutParams(StrUtil.dip2px(mContext, 100),
				menuH);
		param.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
		param.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
		// param.bottomMargin = StrUtil.dip2px(mContext, 5);
		// param.rightMargin = StrUtil.dip2px(mContext, 5);
		mainLayout.addView(getFormFile, param);

	}

	/** 设置Selector。 */
	public static StateListDrawable newSelector(Context context,
			int colorNormal, int colorPressed, int colorFocused, int colorUnable) {
		StateListDrawable bg = new StateListDrawable();
		Drawable normal = new ColorDrawable(colorNormal);
		Drawable pressed = new ColorDrawable(colorPressed);
		Drawable focused = new ColorDrawable(colorFocused);
		Drawable unable = new ColorDrawable(colorUnable);
		bg.addState(new int[] { android.R.attr.state_pressed,
				android.R.attr.state_enabled }, pressed);
		bg.addState(new int[] { android.R.attr.state_enabled,
				android.R.attr.state_focused }, focused);
		bg.addState(new int[] { android.R.attr.state_enabled }, normal);
		bg.addState(new int[] { android.R.attr.state_focused }, focused);
		bg.addState(new int[] { android.R.attr.state_window_focused }, unable);
		bg.addState(new int[] {}, normal);
		return bg;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == 556 && resultCode == RESULT_FIRST_USER) {
			setResult(RESULT_FIRST_USER);
			finish();
		} else if (resultCode == Activity.RESULT_OK) {
			if (requestCode == REQUEST_CODE_photo) {
				cutImage(imageFilePath2);
			} else if (requestCode == REQUEST_CODE_file && null != data) {
				// Uri selectedImage = data.getData();
				// String[] filePathColumn = { MediaStore.Images.Media.DATA };
				// Cursor cursor = getContentResolver().query(selectedImage,
				// filePathColumn, null, null, null);
				// cursor.moveToFirst();
				// int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
				// String path = cursor.getString(columnIndex);
				// cursor.close();
				// cutImage(path);

				String[] proj = { MediaStore.Images.Media.DATA };
				// 获取选中图片的路径
				Cursor cursor = getContentResolver().query(data.getData(),
						proj, null, null, null);

				if (cursor.moveToFirst()) {

					int column_index = cursor
							.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
					photo_path = cursor.getString(column_index);
					if (photo_path == null) {
						photo_path = StrUtil.getPath(getApplicationContext(),
								data.getData());
					}

				}

				cursor.close();
				cutImage(photo_path);
			}
		}

	}

	void cutImage(String imagePath) {
		imageFilePath2 = imagePath;
		cutImageView.setImage(imageFilePath2, w, h);

	}

	void getFaceFromPhoto() {
		imageFilePath2 = Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED) ? Environment
				.getExternalStorageDirectory() + "/sohuodong" : null;
		if (imageFilePath2 != null) {
			File file = new File(imageFilePath2);
			// 判断文件目录是否存在
			if (!file.exists()) {
				file.mkdir();
			}
			imageFilePath2 += "/temp1.jpg";
		}
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);//
		// 相机捕捉图片的意图
		intent.putExtra(MediaStore.EXTRA_OUTPUT,
				Uri.fromFile(new File(imageFilePath2)));//
		// 指定系统相机拍照保存在imageFileUri所指的位置
		startActivityForResult(intent, REQUEST_CODE_photo);// 启动系统相机,等待返回
	}

	void getFaceFromFile() {
		Intent intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		startActivityForResult(intent, REQUEST_CODE_file);
	}

	@Override
	public void finish() {
		super.finish();
	}

	@Override
	protected void onDestroy() {
		if (cutImageView != null) {
			cutImageView.recycle();
		}
		super.onDestroy();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		String path = intent.getStringExtra("path");
	}

}
