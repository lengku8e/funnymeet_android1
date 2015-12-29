package com.mtcent.funnymeet.ui.view.control;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.mtcent.funnymeet.SOApplication;
import com.mtcent.funnymeet.ui.helper.RequestHelper;

import mtcent.funnymeet.R;

/**
 * 自定义View，实现圆角，圆形等效果
 * 
 * @author Kezhdia
 * 
 */
public class CustomImageView extends View implements RequestHelper.DownBack {

	/**
	 * TYPE_CIRCLE / TYPE_ROUND
	 */
	private int type;
	private static final int TYPE_CIRCLE = 0;
	private static final int TYPE_ROUND = 1;
	private String imageUrl;
	boolean getImageUrl = false;
	private Activity mActivity;
	private Handler handler;
	private Bitmap mSrc;

	// private Bitmap mSrcFromUrl;

	public CustomImageView(Context context) {
		super(context);
		init(context);
	}

	@SuppressLint("HandlerLeak")
	void init(Context context) {
		mActivity = (Activity) context;
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				setImageUrl();
			}
		};

	}

	void setImageUrl() {
		// mSrcFromUrl = SOApplication.getDownLoadManager()
		// .getBitmapFormMemoryCacheORadd(imageUrl);
		postInvalidate();
	}

	/**
	 * 图片
	 */

	public void setmSrc(Bitmap mSrc) {
		this.mSrc = mSrc;
	}

	public Bitmap getImage() {
		Bitmap bitmap = SOApplication.getDownLoadManager()
				.getBitmapFormMemoryCacheORadd(imageUrl);

		if (bitmap == null) {
			bitmap = mSrc;
		}

		return bitmap;
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

	/**
	 * 圆角的大小
	 */
	private int mRadius;

	/**
	 * 控件的宽度
	 */
	private int mWidth;
	/**
	 * 控件的高度
	 */
	private int mHeight;

	public CustomImageView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);

	}

	// public CustomImageView(Context context) {
	// this(context, null);
	// }

	/**
	 * 初始化一些自定义的参数
	 * 
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public CustomImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		init(context);

		TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
				R.styleable.CustomImageView, defStyle, 0);
		int n = a.getIndexCount();
		for (int i = 0; i < n; i++) {
			int attr = a.getIndex(i);
			switch (attr) {
			case R.styleable.CustomImageView_src:

				mSrc = BitmapFactory.decodeResource(getResources(),
						a.getResourceId(attr, 0));

				break;
			case R.styleable.CustomImageView_type:
				type = a.getInt(attr, 0);// 默认为Circle
				break;
			case R.styleable.CustomImageView_borderRadius:
				type = a.getDimensionPixelSize(attr, (int) TypedValue
						.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10f,
								getResources().getDisplayMetrics()));// 默认为10DP
				break;
			}
		}
		a.recycle();
	}

	/**
	 * 计算控件的高度和宽度
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		/**
		 * 设置宽度
		 */
		int specMode = MeasureSpec.getMode(widthMeasureSpec);
		int specSize = MeasureSpec.getSize(widthMeasureSpec);

		// if (specMode == MeasureSpec.EXACTLY)// match_parent , accurate
		{
			mWidth = specSize;
		}

		/***
		 * 设置高度
		 */

		specMode = MeasureSpec.getMode(heightMeasureSpec);
		specSize = MeasureSpec.getSize(heightMeasureSpec);
		// if (specMode == MeasureSpec.EXACTLY)// match_parent , accurate
		{
			mHeight = specSize;
		}
		setMeasuredDimension(mWidth, mHeight);

	}

	/**
	 * 绘制
	 */
	@Override
	protected void onDraw(Canvas canvas) {

		switch (type) {
		// 如果是TYPE_CIRCLE绘制圆形
		case TYPE_CIRCLE:

			int min = Math.min(mWidth, mHeight);
			// mSrc = Bitmap.createScaledBitmap(mSrc, min, min, false);
			// /canvas.drawBitmap(createCircleImage(mSrc, min), 0, 0, null);
			/**
			 * 长度如果不一致，按小的值进行压缩
			 */

			float r = min / 2;
			RectF rect = new RectF(mWidth / 2 - r, mHeight / 2 - r, mWidth / 2
					+ r, mHeight / 2 + r);

			canvas.drawBitmap(createCircleImage(getImage(), min), null, rect,
					new Paint());

			// canvas.save();
			// Paint paint = new Paint();
			// paint.setAntiAlias(true);
			// // paint.setFlags(Paint.ANTI_ALIAS_FLAG);
			// canvas.drawCircle(mWidth / 2, mHeight / 2, min / 2, paint);
			// // Path path = new Path();
			// // path.addCircle(mWidth / 2, mHeight / 2, min / 2,
			// Direction.CW);
			// // canvas.clipPath(path);
			// paint.setXfermode(new
			// PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
			// canvas.drawBitmap(bitmap, 0, 0, paint);
			//
			// canvas.restore();

			break;
		case TYPE_ROUND:

			canvas.drawBitmap(createRoundConerImage(getImage()), 0, 0, null);
			break;

		}
		// postInvalidate();
	}

	/**
	 * 根据原图和变长绘制圆形图片
	 * 
	 * @param source
	 * @param min
	 * @return
	 */
	private Bitmap createCircleImage(Bitmap source, int min) {
		final Paint paint = new Paint();
		paint.setAntiAlias(true);
		Bitmap target = Bitmap.createBitmap(min, min, Config.ARGB_8888);
		/**
		 * 产生一个同样大小的画布
		 */
		Canvas canvas = new Canvas(target);
		/**
		 * 首先绘制圆形
		 */
		canvas.drawCircle(min / 2, min / 2, min / 2, paint);
		/**
		 * 使用SRC_IN，参考上面的说明
		 */
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
		/**
		 * 绘制图片
		 */
		RectF rect = new RectF(0, 0, min, min);

		canvas.drawBitmap(source, null, rect, paint);
		return target;
	}

	/**
	 * 根据原图添加圆角
	 * 
	 * @param source
	 * @return
	 */
	private Bitmap createRoundConerImage(Bitmap source) {
		final Paint paint = new Paint();
		paint.setAntiAlias(true);
		Bitmap target = Bitmap.createBitmap(mWidth, mHeight, Config.ARGB_8888);
		Canvas canvas = new Canvas(target);
		Rect rect = new Rect(0, 0, source.getWidth(), source.getHeight());
		RectF rect0 = new RectF(0, 0, getWidth(), getHeight());
		canvas.drawRoundRect(rect0, 8f, 8f, paint);
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
		// canvas.drawBitmap(source, 0, 0, paint);
		// .drawRoundRect(rect, rx, ry, paint)
		canvas.drawBitmap(source, rect, rect0, paint);

		return target;
	}

	@Override
	public void onFinish(RequestHelper.Pdtask t) {
		// TODO Auto-generated method stub
		handler.sendEmptyMessage(0);
	}

	@Override
	public void onUpdate(RequestHelper.Pdtask t) {
		// TODO Auto-generated method stub

	}
}
