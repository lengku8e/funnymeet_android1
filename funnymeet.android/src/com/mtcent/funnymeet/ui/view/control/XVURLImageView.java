package com.mtcent.funnymeet.ui.view.control;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.mtcent.funnymeet.SOApplication;
import com.mtcent.funnymeet.ui.helper.RequestHelper;
import com.mtcent.funnymeet.ui.helper.RequestHelper.DownBack;
import com.mtcent.funnymeet.ui.helper.RequestHelper.Pdtask;
import com.mtcent.funnymeet.common.XVBitmapDrawable;
import com.mtcent.funnymeet.common.XVBitmapDrawable.CGETBITMAP;

public class XVURLImageView extends ImageView implements DownBack {

	Context mActivity;

	public XVURLImageView(Context context) {
		super(context);
		init(context);
	}

	public XVURLImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public XVURLImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	@SuppressLint({ "HandlerLeak", "NewApi" })
	void init(Context context) {
		mActivity = context;
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				setImageUrl();
			}
		};
		// android:layerType="software"
		// 使用硬解码在画图时会将图片引用，然后交给GPU绘制。但GPU绘制前由于图片太多，图片管理器将该图片释放，导致GPU绘制失败。
		if (android.os.Build.VERSION.SDK_INT > 10) {
			this.setLayerType(ImageView.LAYER_TYPE_SOFTWARE, null);
		}

	}

	String imageUrl;
	boolean getImageUrl = false;
	private Handler handler;

	void setImageUrl() {
		Drawable drawable = null;
		if (imageUrl != null) {
			drawable = new XVBitmapDrawable(mActivity.getResources(),
					new CGETBITMAP() {

						@Override
						public Bitmap getBitmap() {
							Bitmap bitmap = SOApplication.getDownLoadManager()
									.getBitmapFormMemoryCacheORadd(imageUrl);

							return bitmap;
						}
					});
		}
		setImageDrawable(drawable);
		invalidate();
	}

	public void setImageUrl(String url) {
		if (imageUrl == null || (imageUrl != null && !imageUrl.equals(url))) {
			getImageUrl = false;
			imageUrl = url;
			if (imageUrl != null && getImageUrl == false) {
				SOApplication.getDownLoadManager().addTask(
						new RequestHelper.Pdtask(mActivity, this, imageUrl,
								null, RequestHelper.Type_Image, null, 0,
								false));
				getImageUrl = true;
			}
			setImageUrl();
		}

	}

	@Override
	public void onFinish(Pdtask t) {
		handler.sendEmptyMessage(0);
	}

	@Override
	public void onUpdate(Pdtask t) {

	}

}
