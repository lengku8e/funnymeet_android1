package com.mtcent.funnymeet.ui.view.control;

import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.mtcent.funnymeet.SOApplication;
import com.mtcent.funnymeet.ui.helper.RequestHelper;
import com.mtcent.funnymeet.ui.helper.RequestHelper.DownBack;
import com.mtcent.funnymeet.ui.helper.RequestHelper.Pdtask;
import com.mtcent.funnymeet.util.StrUtil;

import java.util.Timer;
import java.util.TimerTask;

public class PreviewImageView extends View implements DownBack {
	public interface PreviewImageListen {
		public void onLightClick();

		public void onLongClick();
	}

	public PreviewImageListen listen = null;

	public void setListen(PreviewImageListen listen) {
		this.listen = listen;
	}

	/** 获取两点的距离 **/
	@SuppressLint("FloatMath")
	float getDistance(float x0, float y0, float x1, float y1) {
		float x = x0 - x1;
		float y = y0 - y1;
		return FloatMath.sqrt(x * x + y * y);
	}

	private Context context = null;

	@SuppressLint("InlinedApi")
	public PreviewImageView(Context context) {
		super(context);
		ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		this.setLayoutParams(params);
		init(context);
	}

	public PreviewImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	// public PreviewImageView(Context context, AttributeSet attrs, int
	// defStyle) {
	// super(context, attrs, defStyle);
	// initViewControl(context);
	// }

	private void init(Context context) {
		this.context = context;
	}

	// 如果返回最小能容下parent的值
	PointF getOutside(float parentW, float parentH, float childW, float childH) {
		float tmpW = 0;
		float tmpH = 0;

		float thisS = parentW / parentH;
		float cutS = childW / childH;
		if (thisS > cutS) {
			tmpW = parentW;
			tmpH = (int) (tmpW / cutS);

		} else {
			tmpH = parentH;
			tmpW = (int) (cutS * tmpH);
		}

		return new PointF(tmpW, tmpH);

	}

	// 如果parent能容下child，则直接返回child大小。　否则，按比例返回在parent最大的值。
	PointF getInside(float parentW, float parentH, float childW, float childH) {
		float tmpW = 0;
		float tmpH = 0;

		if (childW < parentW && childH < parentH) {
			tmpW = childW;
			tmpH = childH;
		} else {
			float thisS = parentW / parentH;
			float cutS = childW / childH;
			if (thisS > cutS) {
				tmpH = parentH;
				tmpW = (int) (cutS * tmpH);
			} else {
				tmpW = parentW;
				tmpH = (int) (tmpW / cutS);
			}
		}

		return new PointF(tmpW, tmpH);

	}

	int focusId = 0;

	boolean needBuild = true;
	float lastLength = 0;
	long countDoubleClickTime = 0;
	int countDoubleClickNum = 0;
	long dt = 300;
	float lastCx = 0;
	float lastCy = 0;
	boolean bMultPoint = false;
	boolean bRecove = false;
	float downX = 0;
	float downY = 0;
	RectF bitmapRect = new RectF();// 当前的位子：移动放大的结果
	RectF bitmapRectFixedSeat = new RectF();// 最合适的位子：如果图片小于View，则居中不缩放；如果图片大于View，则缩放至刚好。
	float minScale = 0.8f;
	float maxLeft;
	float maxTop;
	float minRight;
	float minBottom;
	Paint paint = new Paint();

	@Override
	public void onFinish(Pdtask t) {
		changeImage();
	}

	@Override
	public void onUpdate(Pdtask t) {

	}

	String imageUrl;
	boolean getImageUrl = false;

	void changeImage() {
		postInvalidate();
	}

	public void setImageUrl(String url) {
		if (imageUrl == null || (imageUrl != null && !imageUrl.equals(url))) {
			getImageUrl = false;
			imageUrl = url;
			if (imageUrl != null && getImageUrl == false) {
				SOApplication.getDownLoadManager().addTask(
						new RequestHelper.Pdtask(context, this, imageUrl,
								null, RequestHelper.Type_Image, null, 0,
								false));
				getImageUrl = true;
			}
		}
		needBuild = true;
		postInvalidate();
	}

	// Bitmap bmp = null;

	// 最合适的位子：如果图片小于View，则居中不缩放；如果图片大于View，则缩放至刚好。
	void resetRect(Bitmap bmp) {
		if (bmp != null) {
			float w = bmp.getWidth();
			float h = bmp.getHeight();
			float vw = getWidth();
			float vh = getHeight();

			if (w > 0 && h > 0 && vw > 0 && vh > 0) {
				float scale = 1.0f;
				if (w > vw || h > vh) {
					float sw = vw / w;
					float sh = vh / h;
					scale = (sw < sh) ? sw : sh;
				} else if (w > vw) {
					scale = vw / w;
				} else if (h > vh) {
					scale = vh / h;
				} else {
					scale = 1.0f;
				}
				float bw = w * scale;
				float bh = h * scale;
				bitmapRectFixedSeat.left = (vw - bw) / 2;
				bitmapRectFixedSeat.right = bitmapRectFixedSeat.left + bw;
				bitmapRectFixedSeat.top = (vh - bh) / 2;
				bitmapRectFixedSeat.bottom = bitmapRectFixedSeat.top + bh;
				maxLeft = bitmapRectFixedSeat.centerX()
						- (bitmapRectFixedSeat.centerX() - bitmapRectFixedSeat.left)
						* minScale;
				maxTop = bitmapRectFixedSeat.centerY()
						- (bitmapRectFixedSeat.centerY() - bitmapRectFixedSeat.top)
						* minScale;
				minRight = bitmapRectFixedSeat.centerX()
						+ (bitmapRectFixedSeat.right - bitmapRectFixedSeat
								.centerX()) * minScale;
				minBottom = bitmapRectFixedSeat.centerY()
						+ (bitmapRectFixedSeat.bottom - bitmapRectFixedSeat
								.centerY()) * minScale;
				bitmapRect.set(bitmapRectFixedSeat);
			}
		}
	}

	Bitmap getBitmap() {
		Bitmap bitmap = null;
		bitmap = SOApplication.getDownLoadManager()
				.getBitmapFormMemoryCacheORadd(imageUrl);
		if (bitmap != null && needBuild) {
			paint.setAntiAlias(true);
			resetRect(bitmap);
			needBuild = false;
		}

		return bitmap;
	}

	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {

		Bitmap bitmap = getBitmap();
		if (bitmap != null) {
			onRecoveBitmap();

			canvas.save();
			canvas.setDrawFilter(new PaintFlagsDrawFilter(0,
					Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));

			canvas.drawBitmap(bitmap, null, bitmapRect, paint);
			canvas.restore();
		}
		super.onDraw(canvas);
	}

	// class LLC<llClass> {
	// List<llClass> list = new ArrayList<llClass>();
	// public void addItem(llClass l) {
	// list.add(l);
	// }
	// public llClass getItem(int index) {
	// return list.get(index);
	// }
	// }

	void onRecoveBitmap() {
		if (bRecove && bitmapRect.width() > 0 && bitmapRect.height() > 0) {
			float scale = bitmapRectFixedSeat.width() / bitmapRect.width();
			if (scale > 1.1f) {
				scale = scale - 0.1f;
			} else if (scale < 0.9f) {
				scale = scale + 0.1f;
			}
			if (scale >= 0.9 && scale <= 1.1) {
				scale = 1f;
			}

			float w = bitmapRectFixedSeat.width() / scale;
			float h = bitmapRectFixedSeat.height() / scale;

			float dw = w - bitmapRect.width();
			float dh = h - bitmapRect.height();

			bitmapRect.left -= dw / 2;
			bitmapRect.right = bitmapRect.left + w;
			bitmapRect.top -= dh / 2;
			bitmapRect.bottom = bitmapRect.top + h;
			if ((bitmapRect.left > bitmapRectFixedSeat.left && dw < 0)//
					|| (bitmapRect.left < bitmapRectFixedSeat.left && dw > 0)) {
				bitmapRect.left = bitmapRectFixedSeat.left;
				bitmapRect.right = bitmapRect.left + w;
			}

			if ((bitmapRect.right < bitmapRectFixedSeat.right && dw < 0)//
					|| (bitmapRect.right > bitmapRectFixedSeat.right && dw > 0)) {
				bitmapRect.right = bitmapRectFixedSeat.right;
				bitmapRect.left = bitmapRect.right - w;
			}

			if ((bitmapRect.top > bitmapRectFixedSeat.top && dh < 0)//
					|| (bitmapRect.top < bitmapRectFixedSeat.top && dh > 0)) {
				bitmapRect.top = bitmapRectFixedSeat.top;
				bitmapRect.bottom = bitmapRect.top + h;
			}

			if ((bitmapRect.bottom < bitmapRectFixedSeat.bottom && dh < 0)//
					|| (bitmapRect.bottom > bitmapRectFixedSeat.bottom && dh > 0)) {
				bitmapRect.bottom = bitmapRectFixedSeat.bottom;
				bitmapRect.top = bitmapRect.bottom - h;
			}

			if (scale == 1) {
				bRecove = false;
			} else {
				bRecove = true;
			}
			invalidate();
		} else {
			bRecove = false;
		}
	}

	void onDoubleClick() {
		Bitmap bitmap = getBitmap();
		if (bitmap != null) {
			// if (bitmapRect.equals(bitmapRectFixedSeat)) {

			if (bitmapRect.left == bitmapRectFixedSeat.left
					&& bitmapRect.right == bitmapRectFixedSeat.right
					&& bitmapRect.top == bitmapRectFixedSeat.top
					&& bitmapRect.bottom == bitmapRectFixedSeat.bottom) {
				float w = bitmap.getWidth();
				float h = bitmap.getHeight();
				float cx = bitmapRectFixedSeat.centerX();
				float cy = bitmapRectFixedSeat.centerY();
				bitmapRect.set(cx - w / 2, cy - h / 2, cx + w / 2, cy + h / 2);
			} else {
				bitmapRect.set(bitmapRectFixedSeat);
			}
		}
	}

	boolean bLightClick = false;
	Timer lightClickTimer = null;
	float lightClickX = 0;
	float lightClickY = 0;
	long lightClickTime = 0;
	Timer longClickTimer = null;

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int penEvent = event.getAction() & MotionEvent.ACTION_MASK;
		// 轻点事件
		if (penEvent == MotionEvent.ACTION_DOWN) {
			if (bLightClick == true) {
				if (lightClickTimer != null) {
					lightClickTimer.cancel();
					lightClickTimer = null;
				}
				bLightClick = false;
			}
			bLightClick = true;
			lightClickX = event.getX();
			lightClickY = event.getY();
			lightClickTime = System.currentTimeMillis();
			if (longClickTimer != null) {
				longClickTimer.cancel();
				longClickTimer = null;
			}
			longClickTimer = new Timer();
			longClickTimer.schedule(new TimerTask() {
				@Override
				public void run() {
					if (listen != null) {
						listen.onLongClick();
					}
					longClickTimer = null;
				}
			}, 500);

		} else if (penEvent == MotionEvent.ACTION_UP && bLightClick
				&& countDoubleClickNum < 2) {
			if (System.currentTimeMillis() - lightClickTime < 300) {
				lightClickTimer = new Timer();
				lightClickTimer.schedule(new TimerTask() {
					@Override
					public void run() {
						if (listen != null) {
							listen.onLightClick();
						}
						lightClickTimer = null;
					}
				}, 350);
			}
		} else if (bLightClick == true
				&& getDistance(lightClickX, lightClickY, event.getX(),
						event.getY()) > StrUtil.dip2px(context, 2)) {
			bLightClick = false;
			if (lightClickTimer != null) {
				lightClickTimer.cancel();
				lightClickTimer = null;
			}
			if (longClickTimer != null) {
				longClickTimer.cancel();
				longClickTimer = null;
			}
		} else if (penEvent == MotionEvent.ACTION_UP) {
			if (longClickTimer != null) {
				longClickTimer.cancel();
				longClickTimer = null;
			}
		}

		//
		if (bRecove) {
			getParent().requestDisallowInterceptTouchEvent(true);
			invalidate();
			return true;
		}
		boolean ret = false;
		int count = event.getPointerCount();

		/** 处理单点、多点触摸 **/

		switch (penEvent) {
		case MotionEvent.ACTION_DOWN: {
			downX = event.getX();
			downY = event.getY();
			if (System.currentTimeMillis() - countDoubleClickTime > dt) {
				countDoubleClickTime = System.currentTimeMillis();
				countDoubleClickNum = 1;
			} else {
				countDoubleClickNum++;
			}
			bMultPoint = false;
			ret = true;
		}
			break;

		case MotionEvent.ACTION_MOVE: {
			float w = bitmapRect.width();
			float h = bitmapRect.height();
			if (count == 1) {
				float x = event.getX();
				float y = event.getY();

				float dx = (x - downX);
				// float leftMin = 0;
				float leftMin = bitmapRectFixedSeat.left;
				if (bitmapRect.left + dx > leftMin) {
					if (bitmapRect.left < leftMin && dx > 0) {
						dx = leftMin - bitmapRect.left;
					} else {
						dx = 0;
					}
				}
				// float leftMax = bitmapRect.width();
				float leftMax = bitmapRectFixedSeat.right;
				if (bitmapRect.right + dx < leftMax) {
					if (bitmapRect.right > leftMax && dx < 0) {
						dx = leftMax - bitmapRect.right;
					} else {
						dx = 0;
					}
				}
				float dy = (y - downY);
				if (bitmapRect.top + dy > bitmapRectFixedSeat.top) {
					if (bitmapRect.top < bitmapRectFixedSeat.top && dy > 0) {
						dy = bitmapRectFixedSeat.top - bitmapRect.top;
					} else {
						dy = 0;
					}
				}
				if (bitmapRect.bottom + dy < bitmapRectFixedSeat.bottom) {
					if (bitmapRect.bottom > bitmapRectFixedSeat.bottom
							&& dy < 0) {
						dy = bitmapRectFixedSeat.bottom - bitmapRect.bottom;
					} else {
						dy = 0;
					}
				}

				bitmapRect.offset(dx, dy);
				if (dx == 0 && downX != x && !bMultPoint) {
					ret = false;
				} else {
					ret = true;
				}
				downX = x;
				downY = y;
			} else if (count == 2) {
				float x0 = event.getX(0);
				float y0 = event.getY(0);
				float x1 = event.getX(1);
				float y1 = event.getY(1);

				float nowLenght = getDistance(x0, y0, x1, y1);// 获取两点的距离
				if (lastLength == 0) {
					lastLength = nowLenght;
				}
				// 求的缩放的比例

				float scale = nowLenght / lastLength;
				float scaleMinW = bitmapRectFixedSeat.width() * minScale / w;
				float scaleMinH = bitmapRectFixedSeat.height() * minScale / h;
				if (scale < scaleMinW || scale < scaleMinH) {
					if (w >= scaleMinW) {
						scale = scaleMinW;
					} else if (h * scale > scaleMinH) {
						scale = scaleMinH;
					} else {
						scale = Math.max(scaleMinW, scaleMinH);
					}
				}
				w *= scale;
				h *= scale;

				// cx - bitmapRect_left = (lastCx - bitmapRect.left)*scale;
				float nowCx = (x1 + x0) / 2;
				float nowCy = (y1 + y0) / 2;
				float dx_cx = lastCx - bitmapRect.left;
				float dy_cy = lastCy - bitmapRect.top;
				dx_cx *= scale;
				dy_cy *= scale;
				bitmapRect.left = nowCx - dx_cx;
				bitmapRect.top = nowCy - dy_cy;
				bitmapRect.right = bitmapRect.left + w;
				bitmapRect.bottom = bitmapRect.top + h;

				if (bitmapRect.left > maxLeft) {
					bitmapRect.left = maxLeft;
					bitmapRect.right = bitmapRect.left + w;
				}
				if (bitmapRect.top > maxTop) {
					bitmapRect.top = maxTop;
					bitmapRect.bottom = bitmapRect.top + h;
				}
				if (bitmapRect.right < minRight) {
					bitmapRect.right = minRight;
					bitmapRect.left = bitmapRect.right - w;
				}
				if (bitmapRect.bottom < minBottom) {
					bitmapRect.bottom = minBottom;
					bitmapRect.top = bitmapRect.bottom - h;
				}

				lastCx = nowCx;
				lastCy = nowCy;
				bMultPoint = true;
				lastLength = nowLenght;
				ret = true;
			}
		}
			break;

		case MotionEvent.ACTION_UP: {
			if (countDoubleClickNum == 2
					&& System.currentTimeMillis() - countDoubleClickTime < dt) {
				countDoubleClickTime = 0;
				countDoubleClickNum = 0;
				onDoubleClick();
			}
			if (bitmapRect.left > bitmapRectFixedSeat.left || //
					bitmapRect.right < bitmapRectFixedSeat.right || //
					bitmapRect.top > bitmapRectFixedSeat.top || //
					bitmapRect.bottom < bitmapRectFixedSeat.bottom //
			) {
				bRecove = true;
			} else {
				bRecove = false;
			}
			bMultPoint = false;
			// ret = true;
		}
			break;

		// 多点松开,
		case MotionEvent.ACTION_POINTER_UP: {

			if (count == 2) {
				int actIndex = event.getActionIndex();
				if (actIndex == 1) {
					downX = event.getX(0);
					downY = event.getY(0);
				} else {
					downX = event.getX(1);
					downY = event.getY(1);
				}
			} else if (count == 3) {
				int actIndex = event.getActionIndex();
				float x0 = event.getX(0);
				float y0 = event.getY(0);
				float x1 = event.getX(1);
				float y1 = event.getY(1);
				float x2 = event.getX(2);
				float y2 = event.getY(2);
				if (actIndex == 0) {
					lastLength = getDistance(x2, y2, x1, y1);// 获取两点的距离
					lastCx = (x1 + x2) / 2;
					lastCy = (y1 + y2) / 2;
				} else if (actIndex == 1) {
					lastLength = getDistance(x0, y0, x2, y2);// 获取两点的距离
					lastCx = (x2 + x0) / 2;
					lastCy = (y2 + y0) / 2;
				} else {
					lastLength = getDistance(x0, y0, x1, y1);// 获取两点的距离
					lastCx = (x1 + x0) / 2;
					lastCy = (y1 + y0) / 2;
				}

				// ret = true;
			}
		}
			break;
		// 多点触摸
		case MotionEvent.ACTION_POINTER_DOWN: {
			if (count == 1) {
				downX = event.getX(0);
				downY = event.getY(0);
			} else if (count == 2) {
				float x0 = event.getX(0);
				float y0 = event.getY(0);
				float x1 = event.getX(1);
				float y1 = event.getY(1);

				lastLength = getDistance(x0, y0, x1, y1);// 获取两点的距离
				lastCx = (x1 + x0) / 2;
				lastCy = (y1 + y0) / 2;
				bMultPoint = true;
			}
			bMultPoint = true;
			countDoubleClickTime = 0;
			ret = true;
		}
			break;
		}

		if (ret || bMultPoint) {
			getParent().requestDisallowInterceptTouchEvent(true);
		} else {
			getParent().requestDisallowInterceptTouchEvent(false);
		}
		invalidate();
		return ret;
	}

}
