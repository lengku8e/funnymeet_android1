/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mining.app.zxing.view;

import java.util.Collection;
import java.util.HashSet;

import mtcent.funnymeet.R;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.View;

import com.google.zxing.ResultPoint;
import com.mining.app.zxing.camera.CameraManager;
import com.mtcent.funnymeet.util.StrUtil;

/**
 * This view is overlaid on top of the camera preview. It adds the viewfinder
 * rectangle and partial transparency outside it, as well as the laser scanner
 * animation and result points.
 * 
 */
public final class ViewfinderView extends View {

	private static final long ANIMATION_DELAY = 10L;
	private static final int OPAQUE = 0xFF;

	private int ScreenRate;

	private static final int CORNER_WIDTH = 5;

	private static final int MIDDLE_LINE_WIDTH = 6;

	private static final int MIDDLE_LINE_PADDING = 5;

	private static final int SPEEN_DISTANCE = StrUtil.dip2px(null, 3);

	private static float density;

	private static final int TEXT_SIZE = 16;

	private static final int TEXT_PADDING_TOP = 30;

	private Paint paint;

	private int slideTop;

	private static int scan_type;
	public final static int scanner = 1;
	public final static int scan_post = 2;
	public final static int scan_sight = 3;

	private Bitmap resultBitmap;
	private final int maskColor;
	private final int resultColor;

	private final int resultPointColor;
	private Collection<ResultPoint> possibleResultPoints;
	private Collection<ResultPoint> lastPossibleResultPoints;

	boolean isFirst;
	boolean beginChangeAnmi = false;

	public ViewfinderView(Context context, AttributeSet attrs) {
		super(context, attrs);

		density = context.getResources().getDisplayMetrics().density;
		ScreenRate = (int) (15 * density);

		paint = new Paint();
		Resources resources = getResources();
		maskColor = resources.getColor(R.color.viewfinder_mask);
		resultColor = resources.getColor(R.color.result_view);

		resultPointColor = resources.getColor(R.color.possible_result_points);
		possibleResultPoints = new HashSet<ResultPoint>(5);
	}

	public void setScannerType(int type) {
		
		scan_type = type;
		beginChangeAnmi = true;
		Rect frame = getRect();
		speedY= (currentFrame.height() - frame.height())/7;
		speedX= (currentFrame.width() - frame.width())/7;
		speedX = Math.abs(speedX);
		speedY = Math.abs(speedY);
		
	}

	RectF currentFrame = new RectF();
	float speedX =1.0f;
	float speedY =1.0f;
	public Rect changeRect(Rect frame) {

		int center_x = frame.centerX();
		int center_y = frame.centerY();
		int w = frame.width();
		int h = frame.height();
		float cw = currentFrame.width();
		float ch = currentFrame.height();

		
		if (cw > w) {
			cw -= speedX;
			if (cw < w) {
				cw = w;
			}
		}
		if (cw < w) {
			cw += speedX;
			if (cw > w) {
				cw = w;
			}
		}
		if (ch > h) {
			ch -= speedY;
			if (ch < h) {
				ch = h;
			}
		}
		if (ch < h) {
			ch += speedY;
			if (ch > h) {
				ch = h;
			}
		}

		currentFrame.top = center_y - ch / 2;
		currentFrame.bottom = center_y + ch / 2;
		currentFrame.left = center_x - cw / 2;
		currentFrame.right = center_x + cw / 2;

		int ww = (int) cw;
		int hh = (int) ch;

		if (ww == w && hh == h) {
			beginChangeAnmi = false;
			postInvalidateDelayed(ANIMATION_DELAY);
		}

		return new Rect(center_x - ww / 2, center_y - hh / 2,
				center_x + ww / 2, center_y + hh / 2);
	}

	@Override
	public void onDraw(Canvas canvas) {

		int width = getWidth();
		int height = getHeight();
		// Rect frame = CameraManager.get().getFramingRect();
		Rect frame = getRect();
		CameraManager.get().setFramingRect(frame);
		CameraManager.get().setFramingRectInPreview(width, height);

		if (beginChangeAnmi) {
			frame = changeRect(frame);
		} else {
			currentFrame = new RectF(frame);
		}

		// if (frame == null || frame.top < titleHeight) {
		// return;
		// }

		paint.setColor(resultBitmap != null ? resultColor : maskColor);

		canvas.drawRect(0, 0, width, frame.top, paint);
		canvas.drawRect(0, frame.top, frame.left, frame.bottom + 1, paint);
		canvas.drawRect(frame.right + 1, frame.top, width, frame.bottom + 1,
				paint);
		canvas.drawRect(0, frame.bottom + 1, width, height, paint);

		// if (resultBitmap != null) {
		// // Draw the opaque result bitmap over the scanning rectangle
		// paint.setAlpha(OPAQUE);
		// canvas.drawBitmap(resultBitmap, frame.left, frame.top, paint);
		// } else
		{

			paint.setColor(Color.GREEN);
			canvas.drawRect(frame.left, frame.top, frame.left + ScreenRate,
					frame.top + CORNER_WIDTH, paint);
			canvas.drawRect(frame.left, frame.top, frame.left + CORNER_WIDTH,
					frame.top + ScreenRate, paint);
			canvas.drawRect(frame.right - ScreenRate, frame.top, frame.right,
					frame.top + CORNER_WIDTH, paint);
			canvas.drawRect(frame.right - CORNER_WIDTH, frame.top, frame.right,
					frame.top + ScreenRate, paint);
			canvas.drawRect(frame.left, frame.bottom - CORNER_WIDTH, frame.left
					+ ScreenRate, frame.bottom, paint);
			canvas.drawRect(frame.left, frame.bottom - ScreenRate, frame.left
					+ CORNER_WIDTH, frame.bottom, paint);
			canvas.drawRect(frame.right - ScreenRate, frame.bottom
					- CORNER_WIDTH, frame.right, frame.bottom, paint);
			canvas.drawRect(frame.right - CORNER_WIDTH, frame.bottom
					- ScreenRate, frame.right, frame.bottom, paint);

			if (!beginChangeAnmi) {

				slideTop += SPEEN_DISTANCE;
				if (slideTop >= frame.bottom) {
					slideTop = frame.top;
				}

				if (slideTop < frame.top) {
					slideTop = frame.top;
				}

				Rect lineRect = new Rect();
				lineRect.left = frame.left;
				lineRect.right = frame.right;
				lineRect.top = slideTop;
				lineRect.bottom = slideTop + lineHeight;
				canvas.drawBitmap(
						((BitmapDrawable) (getResources()
								.getDrawable(R.drawable.qrcode_scan_line)))
								.getBitmap(), null, lineRect, paint);

				paint.setColor(Color.WHITE);
				paint.setTextSize(TEXT_SIZE * density);
				paint.setAlpha(0x40);
				paint.setTypeface(Typeface.create("System", Typeface.BOLD));
				String text = getResources().getString(R.string.scan_text);
				float textWidth = paint.measureText(text);

				canvas.drawText(
						text,
						(width - textWidth) / 2,
						(float) (frame.bottom + hintWordHeight + getWordHeight()),
						paint);

				Collection<ResultPoint> currentPossible = possibleResultPoints;
				Collection<ResultPoint> currentLast = lastPossibleResultPoints;
				if (currentPossible.isEmpty()) {
					lastPossibleResultPoints = null;
				} else {
					// possibleResultPoints = new HashSet<ResultPoint>(5);
					// lastPossibleResultPoints = currentPossible;
					// paint.setAlpha(OPAQUE);
					// paint.setColor(resultPointColor);
					// for (ResultPoint point : currentPossible) {
					// canvas.drawCircle(frame.left + point.getX(), frame.top
					// + point.getY(), 6.0f, paint);
					// }
				}
				if (currentLast != null) {
					// paint.setAlpha(OPAQUE / 2);
					// paint.setColor(resultPointColor);
					// for (ResultPoint point : currentLast) {
					// canvas.drawCircle(frame.left + point.getX(), frame.top
					// + point.getY(), 3.0f, paint);
					// }
				}
				postInvalidateDelayed(ANIMATION_DELAY, frame.left, frame.top,
						frame.right, frame.bottom);
			} else {
				postInvalidateDelayed(ANIMATION_DELAY);
				//postInvalidateDelayed(ANIMATION_DELAY, 0, 0, width, height);
			}

		}
	}

	int titleHeight = StrUtil.dip2px(null, 48);
	int menuHeight = StrUtil.dip2px(null, 50);
	int hintWordHeight = StrUtil.dip2px(null, 20);
	int lineHeight = StrUtil.dip2px(null, 18);

	public Rect getRect() {

		float w_rate = 95f / 466f;
		float h_rate = 1.0f;
		if (scan_type == scan_post) {
			w_rate = 60f / 466f;
			h_rate = 1.3f;
		} else if (scan_type == scan_sight) {
			w_rate = 60f / 466f;
			h_rate = 1.3f;
		}else if (scan_type == scanner){
			w_rate = 95f / 466f;
			h_rate = 1.0f;
		}
		int width = getWidth();
		int height = getHeight();
		Rect frame = new Rect();
		frame.left = (int) (width * w_rate);
		frame.right = width - frame.left;
		int w = frame.right - frame.left;
		int h = (int) (h_rate * w);
		
		int dash = hintWordHeight;
		int wordHeight = getWordHeight();
		frame.top = (height - titleHeight - menuHeight - (h + dash + wordHeight))
				/ 2 + titleHeight;
		frame.bottom = frame.top + h;

		return frame;
	}

	public int getWordHeight() {
		int dash = StrUtil.dip2px(null, 20);
		return dash;
	}

	public void drawViewfinder() {
		resultBitmap = null;
		invalidate();
	}

	/**
	 * Draw a bitmap with the result points highlighted instead of the live
	 * scanning display.
	 * 
	 * @param barcode
	 *            An image of the decoded barcode.
	 */
	public void drawResultBitmap(Bitmap barcode) {
		resultBitmap = barcode;
		invalidate();
	}

	public void addPossibleResultPoint(ResultPoint point) {
		possibleResultPoints.add(point);
	}

}
