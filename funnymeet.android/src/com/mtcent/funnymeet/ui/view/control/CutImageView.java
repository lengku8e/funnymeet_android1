package com.mtcent.funnymeet.ui.view.control;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Toast;

import com.mtcent.funnymeet.util.StrUtil;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

public class CutImageView extends View {
	public interface CutImageListen {
		public void finsh(String path);

		public void cancle();
	}

	public CutImageListen listen = null;

	class MyView {
		Path path = null;
		RectF rect = new RectF();
		boolean focus = false;
		boolean bMove = false;
		Paint paint;
		float fx = 0;
		float fy = 0;

		public MyView() {
			paint = new Paint();
			paint.setColor(0xffffffff);
			paint.setAntiAlias(true);
			paint.setStyle(Paint.Style.FILL);
		}

		void reBulid() {
		}

		void Draw(Canvas canvas) {
		}

		boolean isOnMe(float x, float y) {
			return rect.contains(x, y);
		}

		boolean Touch(MotionEvent event) {
			boolean ret = false;
			/** 处理单点、多点触摸 **/
			switch (event.getAction() & MotionEvent.ACTION_MASK) {
			case MotionEvent.ACTION_DOWN:
				if (isOnMe(event.getX(), event.getY())) {
					fx = event.getX();
					fy = event.getY();
					focus = true;
					bMove = false;
					ret = true;
				}
				break;

			case MotionEvent.ACTION_MOVE:
				if (focus) {
					float nx = event.getX();
					float ny = event.getY();
					//if (Math.abs(fx - nx) > 5 || Math.abs(fy - ny) > 5) {
					if (!isOnMe(event.getX(), event.getY())) {
						bMove = true;
					}
					ret = true;
				}

				break;
			case MotionEvent.ACTION_UP:
				if (focus) {
					ret = true;
					if (bMove == false) {
						click();
					} else {
						bMove = false;
					}
					focus = false;
				}
				break;
			}
			return ret;
		};

		void click() {
		};
	}

	// 背景图片
	class BackView extends MyView {
		Bitmap bitmap = null;
		float bitmapW = 0;
		float bitmapH = 0;

		String saveFile() {
			String path = Environment.getExternalStorageState().equals(
					Environment.MEDIA_MOUNTED) ? Environment
					.getExternalStorageDirectory() + "/sohuodong" : null;
			if (path != null) {
				File file = new File(path);
				// 判断文件目录是否存在
				if (!file.exists()) {
					file.mkdir();
				}
				path += "/temp2.jpg";
			}

			if (bitmap != null) {
				Bitmap tmp = getCutBitmap();
				if (!SavePicInLocal(tmp, path)) {
					path = null;
				}
				tmp.recycle();
			} else {
				path = null;
			}
			return path;
		}

		public Bitmap getCutBitmap() {
			Bitmap cutBitmap = Bitmap.createBitmap((int) cutW, (int) cutH,
					Bitmap.Config.ARGB_8888);
			Canvas canvas = new Canvas(cutBitmap);
			Paint paint = new Paint();

			Rect dRect = new Rect(0, 0, (int) cutW, (int) cutH);

			Rect sRect = new Rect();
			sRect.left = (int) (cutView.rect.left - rect.left);
			sRect.top = (int) (cutView.rect.top - rect.top);
			sRect.right = (int) (sRect.left + cutView.rect.width());
			sRect.bottom = (int) (sRect.top + cutView.rect.height());

			float scal = bitmapW / rect.width();
			sRect.left *= scal;
			sRect.top *= scal;
			sRect.right *= scal;
			sRect.bottom *= scal;

			// 相当于清屏
			canvas.drawARGB(0, 0, 0, 0);
			// 再把原来的bitmap画到现在的bitmap！！！注意这个理解
			canvas.drawBitmap(bitmap, sRect, dRect, paint);
			return cutBitmap;
		}

		// 保存拍摄的照片到手机的sd卡
		private boolean SavePicInLocal(Bitmap bitmap, String path) {
			FileOutputStream fos = null;
			BufferedOutputStream bos = null;
			ByteArrayOutputStream baos = null; // 字节数组输出流
			try {
				baos = new ByteArrayOutputStream();
				bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
				byte[] byteArray = baos.toByteArray();// 字节数组输出流转换成字节数组

				File file = new File(path);
				// 将字节数组写入到刚创建的图片文件中
				fos = new FileOutputStream(file);
				bos = new BufferedOutputStream(fos);
				bos.write(byteArray);
				baos.flush();
				bos.flush();
				fos.flush();
				baos.close();
				bos.close();
				fos.close();
				return true;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return false;
		}

		public void recycle() {
			if ((bitmap != null) && (bitmap.isRecycled() == false)) {
				bitmap.recycle();
				bitmap = null;
			}
		}

		public void reBulid() {
			recycle();
			Options options = new Options();
			File file = new File(picPath);
			long size = 0;
			if (file != null) {
				size = file.length();
			}
			paint.setColor(0xff000000);
			options.inJustDecodeBounds = false;// 设置为真正的解码图片
			bitmap = BitmapFactory.decodeFile(picPath, options);// 解码后可以options.outWidth和options.outHeight来获取图片的尺寸
			if (bitmap != null) {
				bitmapW = bitmap.getWidth();
				bitmapH = bitmap.getHeight();
				if (bitmapW > 0 && bitmapH > 0) {
					PointF pf = null;
					if (bFull) {
						pf = getOutside(cutView.rect.width(),
								cutView.rect.height(), bitmapW, bitmapH);
					} else {
						pf = getInside(thisW, thisH, bitmapW, bitmapH);
					}
					float rw = pf.x;
					float rh = pf.y;
					rect.left = (thisW - rw) / 2;
					rect.right = rect.left + rw;
					rect.top = (thisH - rh) / 2;
					rect.bottom = rect.top + rh;
					return;
				}
			}
//			rect.left = 0;
//			rect.right = thisW;
//			rect.top = 0;
//			rect.bottom = thisH;
			rect = new RectF(cutView.rect);
		}

		void Draw(Canvas canvas) {
			if (bitmap == null) {
				reBulid();
			}
			if (bitmap != null) {
				canvas.save();
				canvas.drawBitmap(bitmap, null, rect, paint);
				canvas.restore();
			} else {
				canvas.drawRect(rect, paint);
			}
		}

		float downX = 0;
		float downY = 0;
		float lastLength = 0;
		long countDoubleClickTime = 0;
		int countDoubleClickNum = 0;
		long dt = 300;
		boolean bFull = true;

		float lastCx = 0;
		float lastCy = 0;

		boolean Touch(MotionEvent event) {

			boolean ret = false;
			/** 处理单点、多点触摸 **/
			switch (event.getAction() & MotionEvent.ACTION_MASK) {
			case MotionEvent.ACTION_DOWN:
				if (isOnMe(event.getX(), event.getY())) {
					focus = true;
					downX = event.getX();
					downY = event.getY();
					if (System.currentTimeMillis() - countDoubleClickTime > dt) {
						countDoubleClickTime = System.currentTimeMillis();
						countDoubleClickNum = 1;
					} else {
						countDoubleClickNum++;
					}

					ret = true;
				}
				break;

			case MotionEvent.ACTION_MOVE:
				if (focus) {
					int count = event.getPointerCount();
					float w = rect.width();
					float h = rect.height();
					if (count == 1) {
						float x = event.getX();
						float y = event.getY();

						RectF tmp = new RectF();
						tmp.left = rect.left + (x - downX);
						tmp.top = rect.top + (y - downY);
						tmp.right = tmp.left + w;
						tmp.bottom = tmp.top + h;
						if (bFull == false
								|| (tmp.left <= cutView.rect.left && tmp.right >= cutView.rect.right)) {
							rect.left = tmp.left;
							rect.right = tmp.right;
						}
						if (bFull == false
								|| (tmp.top <= cutView.rect.top && tmp.bottom >= cutView.rect.bottom)) {
							rect.top = tmp.top;
							rect.bottom = tmp.bottom;
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
						float scale = nowLenght / lastLength;// 求的缩放的比例

						if (scale != 1) {
							w *= scale;
							h *= scale;
							if (bFull == false
									|| (cutView.rect.width() <= w && cutView.rect
											.height() <= h)) {
								float cx = (x1 + x0) / 2;
								float cy = (y1 + y0) / 2;
								float dx_cx = cx - rect.left - (cx - lastCx);
								float dy_cy = cy - rect.top - (cy - lastCy);
								dx_cx *= scale;
								dy_cy *= scale;
								if (bFull == false) {
									rect.left = cx - dx_cx;
									rect.top = cy - dy_cy;
									rect.right = rect.left + w;
									rect.bottom = rect.top + h;
								} else {
									if (cx - dx_cx >= cutView.rect.left) {
										rect.left = cutView.rect.left;
										rect.right = rect.left + w;
									} else {
										if (cx - dx_cx + w <= cutView.rect.right) {
											rect.right = cutView.rect.right;
											rect.left = rect.right - w;
										} else {
											rect.left = cx - dx_cx;
											rect.right = rect.left + w;
										}
									}

									if (cy - dy_cy >= cutView.rect.top) {
										rect.top = cutView.rect.top;
										rect.bottom = rect.top + h;
									} else {
										if (cy - dy_cy + h <= cutView.rect.bottom) {
											rect.bottom = cutView.rect.bottom;
											rect.top = rect.bottom - h;
										} else {
											rect.top = cy - dy_cy;
											rect.bottom = rect.top + h;
										}
									}
								}
							}

						}
						lastLength = nowLenght;
						lastCx = (x1 + x0) / 2;
						lastCy = (y1 + y0) / 2;
					}
					ret = true;
				}
				break;

			case MotionEvent.ACTION_UP:
				if (focus) {
					focus = false;
					if (countDoubleClickNum == 2
							&& System.currentTimeMillis()
									- countDoubleClickTime < dt) {
						countDoubleClickTime = 0;
						countDoubleClickNum = 0;
						onDoubleClick();
					}
					ret = true;
				}
				break;

			// 多点松开,
			case MotionEvent.ACTION_POINTER_UP:
				if (focus) {
					int count = event.getPointerCount();
					count -= 1;
					if (count == 1) {
						int actIndex = event.getActionIndex();
						if (actIndex == 1) {
							downX = event.getX(0);
							downY = event.getY(0);
						} else {
							downX = event.getX(1);
							downY = event.getY(1);
						}
					} else if (count == 2) {
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

					}
					ret = true;
				}
				break;
			// 多点触摸
			case MotionEvent.ACTION_POINTER_DOWN:
				if (focus) {
					int count = event.getPointerCount();
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
					}

					countDoubleClickTime = 0;
					ret = true;
				}
				break;

			}
			return ret;
		};

		void onDoubleClick() {
			if (bitmap != null) {
				float bitmapW = bitmap.getWidth();
				float bitmapH = bitmap.getHeight();

				// 如果是原图大小，则缩小至屏幕大小
				if (rect.width() == bitmapW && rect.height() == bitmapH) {
					PointF pf = null;
					if (bFull) {
						pf = getOutside(cutView.rect.width(),
								cutView.rect.height(), bitmapW, bitmapH);
					} else {
						pf = getInside(thisW, thisH, bitmapW, bitmapH);
					}

					float rw = pf.x;
					float rh = pf.y;
					rect.left = (thisW - rw) / 2;
					rect.right = rect.left + rw;
					rect.top = (thisH - rh) / 2;
					rect.bottom = rect.top + rh;

					// 否者放大至原图
				} else {
					if (bFull == false
							|| (bitmapW >= cutView.rect.width() && bitmapH >= cutView.rect
									.height())) {
						rect.left = (thisW - bitmapW) / 2;
						rect.right = rect.left + bitmapW;
						rect.top = (thisH - bitmapH) / 2;
						rect.bottom = rect.top + bitmapH;
					} else {
						if (bitmapW / bitmapH > cutView.rect.width()
								/ cutView.rect.height()) {
							rect.top = cutView.rect.top;
							rect.bottom = cutView.rect.bottom;
							float w = cutView.rect.height() * bitmapW / bitmapH;
							rect.left = (cutView.rect.left + cutView.rect.right)
									/ 2 - w / 2;
							rect.right = (cutView.rect.left + cutView.rect.right)
									/ 2 + w / 2;
						} else {
							rect.left = cutView.rect.left;
							rect.right = cutView.rect.right;
							float h = cutView.rect.width() * bitmapH / bitmapW;
							rect.top = (cutView.rect.top + cutView.rect.bottom)
									/ 2 - h / 2;
							rect.bottom = (cutView.rect.top + cutView.rect.bottom)
									/ 2 + h / 2;
						}

					}
				}

			}
		}
	}

	/** 获取两点的距离 **/
	float getDistance(float x0, float y0, float x1, float y1) {
		float x = x0 - x1;
		float y = y0 - y1;
		return FloatMath.sqrt(x * x + y * y);
	}

	// 剪切框
	class CutView extends MyView {

		@Override
		boolean Touch(MotionEvent event) {
			return false;
		}

		void Draw(Canvas canvas) {
			initPath();
			paint.setColor(0xaa000000);
			paint.setStyle(Paint.Style.FILL);
			paint.setStrokeWidth(1);
			canvas.drawPath(path, paint);

			paint.setColor(0xffffffff);
			paint.setStyle(Paint.Style.STROKE);
			paint.setStrokeWidth(3);
			canvas.drawRect(rect, paint);
		}

		void initPath() {
			if (path == null) {
				path = new Path();
				path.addRect(0, 0, thisW, thisH, Path.Direction.CW);
				path.addRect(rect.left, rect.top, rect.right, rect.bottom,
						Path.Direction.CCW);

			}
		}

	}

	// 菜单背景
	class MenuBackView extends MyView {
		void Draw(Canvas canvas) {
			 //paint.setColor(0x5500000);
			 //canvas.drawRect(rect, paint);
		}

		boolean Touch(MotionEvent event) {
			return false;
		};
	}

	// 菜单左按钮 形状："<-"
	class MenuLeftView extends MyView {

		void initPath() {
			if (path == null) {
				path = new Path();
				float h = rect.height();
				float w = h * 5 / 4;
				float x0 = (w) / 2 + rect.left;
				float y0 = (h) / 2 + rect.top;

				float pw = h / 2;
				float ph = pw * 3 / 4;
				float ph1 = ph * 2 / 7;

				path.moveTo(x0 - pw / 2, y0);
				path.lineTo(x0, y0 - ph / 2);
				path.lineTo(x0, y0 - ph / 2 + ph1);
				path.lineTo(x0 + pw / 2, y0 - ph / 2 + ph1);
				path.lineTo(x0 + pw / 2, y0 + ph / 2 - ph1);
				path.lineTo(x0, y0 + ph / 2 - ph1);
				path.lineTo(x0, y0 + ph / 2);
				path.close();
			}
		}

		void Draw(Canvas canvas) {
			initPath();
			if (focus) {
				paint.setColor(0xff333333);
				canvas.drawRect(rect, paint);
			}
			paint.setColor(0xffffffff);
			canvas.drawPath(path, paint);
		}

		void click() {
			if (listen != null) {
				listen.cancle();
			}
			return;
		};
	}

	// 菜单右按钮 形状："√"
	class MenuRightView extends MyView {
		boolean saveFile = false;

		void initPath() {
			if (path == null) {
				path = new Path();
				float h = rect.height();
				float w = h * 5 / 4;
				float pw = h / 2;
				float ph = pw * 3 / 4;

				float l1 = pw / 39 * FloatMath.sqrt(14 * 14 * 2);
				float l2 = pw / 39 * FloatMath.sqrt(25 * 25 * 2);
				float lw = pw / 39 * FloatMath.sqrt(6 * 6 * 2);
				float scale = 1.2f;
				l1 *= scale;
				l2 *= scale;
				lw *= scale;
				float cos45 = FloatMath.cos(45);

				float x0 = rect.left + (w - pw) / 2 + l1 * cos45;
				float y0 = rect.top + (h - ph) / 2 + ph;
				path.moveTo(x0, y0);
				float x = x0 - l1 * cos45;
				float y = y0 - l1 * cos45;
				path.lineTo(x, y);
				x += lw * cos45;
				y -= lw * cos45;
				path.lineTo(x, y);
				x += (l1 - lw) * cos45;
				y += (l1 - lw) * cos45;
				path.lineTo(x, y);
				x += (l2 - lw) * cos45;
				y -= (l2 - lw) * cos45;
				path.lineTo(x, y);
				x += (lw) * cos45;
				y += (lw) * cos45;
				path.lineTo(x, y);
				path.close();
			}
		}

		void Draw(Canvas canvas) {
			initPath();
			if (focus) {
				paint.setColor(0xff333333);
				canvas.drawRect(rect, paint);
			}
			paint.setColor(0xffffffff);
			canvas.drawPath(path, paint);
		}

		void click() {
			if (saveFile == false) {
				saveFile = true;
				String path = ((BackView) backView).saveFile();
				if (listen != null) {
					listen.finsh(path);
				} else {
					saveFile = false;
				}
			}
			return;
		};
	}

	ArrayList<MyView> mychildViewList = new ArrayList<MyView>();
	private Context context = null;

	public CutImageView(Context context) {
		super(context);
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		this.setLayoutParams(params);
		init(context);
	}

	public CutImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public CutImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	private void init(Context context) {
		this.context = context;
		mychildViewList.add(backView);
		mychildViewList.add(cutView);
		mychildViewList.add(menuBackView);
		mychildViewList.add(menuLeftView);
		mychildViewList.add(menuRightView);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		// TODO Auto-generated method stub
		super.onSizeChanged(w, h, oldw, oldh);
		needBuild = true;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		resetIf();
		for (MyView myView : mychildViewList) {
			myView.Draw(canvas);
		}
		super.onDraw(canvas);
	}

	public void setImage(String path, int w, int h) {
		if (path == null) {
			path = "";
		}

		picPath = new String(path);
		cutW = w;
		cutH = h;
		needBuild = true;
		postInvalidate();
	}

	public void recycle() {
		if (backView != null) {
			((BackView) backView).recycle();
		}
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

	void resetIf() {
		if (needBuild) {
			needBuild = false;
			thisW = this.getWidth();
			thisH = this.getHeight();
			if (cutW > 0 && cutH > 0) {
				PointF pf = getInside(thisW, thisH, cutW, cutH);
				float tmpW = pf.x;
				float tmpH = pf.y;

				cutView.path = null;
				cutView.rect.left = (thisW - tmpW) / 2;
				cutView.rect.top = (thisH - tmpH) / 2;
				cutView.rect.right = cutView.rect.left + tmpW;
				cutView.rect.bottom = cutView.rect.top + tmpH;

			}

			backView.reBulid();

			int menuH = StrUtil.dip2px(context, 48);
			menuBackView.path = null;
			menuBackView.rect.left = 0;
			menuBackView.rect.right = thisW;
			menuBackView.rect.top = 0;//thisH - menuH;
			menuBackView.rect.bottom = menuH;//thisH;

			menuLeftView.path = null;
			menuLeftView.rect.left = 0;
			menuLeftView.rect.right = (int) (menuH * 1.4);
			menuLeftView.rect.top =menuBackView.rect.top;// thisH - menuH;
			menuLeftView.rect.bottom = menuBackView.rect.bottom;//thisH;

			menuRightView.path = null;
			menuRightView.rect.left = thisW - (int) (menuH * 1.4);
			menuRightView.rect.right = thisW;
			menuRightView.rect.top = menuBackView.rect.top;//thisH - menuH;
			menuRightView.rect.bottom = menuBackView.rect.bottom;//thisH;

			// 初始图片

		}

	}

	int focusId = 0;

	boolean needBuild = true;
	MyView backView = new BackView();
	MyView cutView = new CutView();
	MyView menuBackView = new MenuBackView();
	MyView menuLeftView = new MenuLeftView();
	MyView menuRightView = new MenuRightView();
	float thisW = 0;
	float thisH = 0;

	String picPath = null;
	float cutW = 0;
	float cutH = 0;
	float cutScale = 1;

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int count = mychildViewList.size();
		for (int i = count - 1; i >= 0; i--) {
			MyView view = mychildViewList.get(i);
			if (view.Touch(event)) {
				postInvalidate();
				break;
			}
		}
		return true;
	}

	Toast toast = null;

	// 弹出消息
	void showMsg(final String msg) {
		if (toast == null) {
			toast = Toast.makeText(context, "", Toast.LENGTH_LONG);
		}
		toast.setText(msg);
		toast.show();
	}
}
