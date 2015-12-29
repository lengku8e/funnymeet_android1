package com.mtcent.funnymeet.ui.activity.discovery;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.mining.app.zxing.camera.CameraManager;
import com.mining.app.zxing.decoding.CaptureActivityHandler;
import com.mining.app.zxing.decoding.InactivityTimer;
import com.mining.app.zxing.image.RGBLuminanceSource;
import com.mining.app.zxing.view.ViewfinderView;
import com.mtcent.funnymeet.util.StrUtil;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Hashtable;
import java.util.Vector;

import mtcent.funnymeet.R;

public class ScanQRCodeActivity extends Activity implements Callback,
		View.OnClickListener {

	private CaptureActivityHandler handler;
	private ViewfinderView viewfinderView;
	private TextView qrcode_album;
	private boolean hasSurface;
	private Vector<BarcodeFormat> decodeFormats;
	private String characterSet;
	private InactivityTimer inactivityTimer;
	private MediaPlayer mediaPlayer;
	private boolean playBeep;
	private static final float BEEP_VOLUME = 0.10f;
	private boolean vibrate;

	private static final int REQUEST_CODE = 100;
	private static final int PARSE_BARCODE_SUC = 300;
	private static final int PARSE_BARCODE_FAIL = 303;
	private ProgressDialog mProgress;
	private String photo_path;
	private Bitmap scanBitmap;

	private LinearLayout scan_scanner;
	private LinearLayout scan_post;
	private LinearLayout scan_sight;
	private TextView scan_scanner_icon;
	private TextView scan_post_icon;
	private TextView scan_sight_icon;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_capture);
		// ViewUtil.addTopView(getApplicationContext(), this,
		// R.string.scan_card);
		CameraManager.init(getApplication());
		viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);

		View mButtonBack = findViewById(R.id.left_backLayout);
		mButtonBack.setOnClickListener(this);
		hasSurface = false;
		inactivityTimer = new InactivityTimer(this);

		TextView tv = (TextView) findViewById(R.id.titleTextView);
		tv.setText("二维码/条码");

		scan_scanner = (LinearLayout) findViewById(R.id.scan_scaner);
		scan_post = (LinearLayout) findViewById(R.id.scan_post);
		scan_sight = (LinearLayout) findViewById(R.id.scan_sight);

		scan_post_icon = (TextView) findViewById(R.id.scan_post_icon);
		scan_sight_icon = (TextView) findViewById(R.id.scan_sight_icon);
		scan_scanner_icon = (TextView) findViewById(R.id.scanner_icon);

		scan_scanner_icon.setBackgroundResource(R.drawable.scan_qr_hl);
		scan_post_icon.setBackgroundResource(R.drawable.scan_book);
		scan_sight_icon.setBackgroundResource(R.drawable.scan_street);

		scan_scanner.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				scan_scanner_icon.setBackgroundResource(R.drawable.scan_qr_hl);
				scan_post_icon.setBackgroundResource(R.drawable.scan_book);
				scan_sight_icon.setBackgroundResource(R.drawable.scan_street);
				viewfinderView.setScannerType(1);
			}
		});

		scan_post.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				scan_scanner_icon.setBackgroundResource(R.drawable.scan_qr);
				scan_post_icon.setBackgroundResource(R.drawable.scan_book_hl);
				scan_sight_icon.setBackgroundResource(R.drawable.scan_street);
				viewfinderView.setScannerType(2);
			}
		});

		scan_sight.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				scan_scanner_icon.setBackgroundResource(R.drawable.scan_qr);
				scan_post_icon.setBackgroundResource(R.drawable.scan_book);
				scan_sight_icon
						.setBackgroundResource(R.drawable.scan_street_hl);
				viewfinderView.setScannerType(3);
			}
		});

		qrcode_album = (TextView) findViewById(R.id.qrcode_album);
		qrcode_album.setVisibility(View.VISIBLE);
		qrcode_album.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				photo();
			}
		});

	}

	private void photo() {

		Intent innerIntent = new Intent(); // "android.intent.action.GET_CONTENT"
		if (Build.VERSION.SDK_INT < 19) {
			innerIntent.setAction(Intent.ACTION_GET_CONTENT);
		} else {
			innerIntent.setAction(Intent.ACTION_OPEN_DOCUMENT);
		}
		// innerIntent.setAction(Intent.ACTION_GET_CONTENT);

		innerIntent.setType("image/*");

		Intent wrapperIntent = Intent.createChooser(innerIntent, "选择二维码图片");

		ScanQRCodeActivity.this.startActivityForResult(wrapperIntent,
				REQUEST_CODE);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.left_backLayout:
			this.finish();
			break;

		}
	}

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			mProgress.dismiss();
			switch (msg.what) {
			case PARSE_BARCODE_SUC:
				onResultHandler((String) msg.obj, scanBitmap);
				break;
			case PARSE_BARCODE_FAIL:
				Toast.makeText(ScanQRCodeActivity.this, (String) msg.obj,
						Toast.LENGTH_LONG).show();
				break;

			}
		}

	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == RESULT_OK) {

			switch (requestCode) {

			case REQUEST_CODE:

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
						Log.i("123path  Utils", photo_path);
					}
					Log.i("123path", photo_path);

				}

				cursor.close();

				new Thread(new Runnable() {

					@Override
					public void run() {

						Result result = scanningImage(photo_path);
						// String result = decode(photo_path);
						if (result == null) {
							Log.i("123", "   -----------");
							Looper.prepare();
							Toast.makeText(getApplicationContext(), "图片格式有误", 0)
									.show();
							Looper.loop();
						} else {
							Log.i("123result", result.toString());
							// Log.i("123result", result.getText());
							// 数据返回
							String recode = recode(result.toString());
							Intent data = new Intent();
							data.putExtra("result", recode);
							setResult(300, data);
							finish();
						}
					}
				}).start();
				break;

			}

		}

	}

	private String recode(String str) {
		String formart = "";

		try {
			boolean ISO = Charset.forName("ISO-8859-1").newEncoder()
					.canEncode(str);
			if (ISO) {
				formart = new String(str.getBytes("ISO-8859-1"), "GB2312");
				Log.i("1234      ISO8859-1", formart);
			} else {
				formart = str;
				Log.i("1234      stringExtra", str);
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return formart;
	}

	public Result scanningImage(String path) {
		if (TextUtils.isEmpty(path)) {
			return null;
		}
		Hashtable<DecodeHintType, String> hints = new Hashtable<DecodeHintType, String>();
		hints.put(DecodeHintType.CHARACTER_SET, "UTF8");

		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		scanBitmap = BitmapFactory.decodeFile(path, options);
		options.inJustDecodeBounds = false;
		int sampleSize = (int) (options.outHeight / (float) 200);
		if (sampleSize <= 0)
			sampleSize = 1;
		options.inSampleSize = sampleSize;
		scanBitmap = BitmapFactory.decodeFile(path, options);
		RGBLuminanceSource source = new RGBLuminanceSource(scanBitmap);
		BinaryBitmap bitmap1 = new BinaryBitmap(new HybridBinarizer(source));
		QRCodeReader reader = new QRCodeReader();
		try {
			return reader.decode(bitmap1, hints);

		} catch (NotFoundException e) {
			e.printStackTrace();
		} catch (ChecksumException e) {
			e.printStackTrace();
		} catch (FormatException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected void onResume() {
		super.onResume();
		SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
		SurfaceHolder surfaceHolder = surfaceView.getHolder();
		if (hasSurface) {
			initCamera(surfaceHolder);
		} else {
			surfaceHolder.addCallback(this);
			surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}
		decodeFormats = null;
		characterSet = null;

		playBeep = true;
		AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
		if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
			playBeep = false;
		}
		initBeepSound();
		vibrate = true;

	}

	@Override
	protected void onPause() {
		super.onPause();
		if (handler != null) {
			handler.quitSynchronously();
			handler = null;
		}
		CameraManager.get().closeDriver();
	}

	@Override
	protected void onDestroy() {
		inactivityTimer.shutdown();
		super.onDestroy();
	}

	public void handleDecode(Result result, Bitmap barcode) {
		// inactivityTimer.onActivity();
		inactivityTimer.shutdown();

		playBeepSoundAndVibrate();
		String resultString = result.getText();
		onResultHandler(resultString, barcode);
	}

	private void onResultHandler(String resultString, Bitmap bitmap) {
		if (TextUtils.isEmpty(resultString)) {
			Toast.makeText(ScanQRCodeActivity.this, "Scan failed!",
					Toast.LENGTH_SHORT).show();
			return;
		}
		Intent resultIntent = new Intent();
		Bundle bundle = new Bundle();
		bundle.putString("result", resultString);
		// bundle.putParcelable("bitmap", bitmap);
		resultIntent.putExtras(bundle);
		this.setResult(RESULT_OK, resultIntent);
		ScanQRCodeActivity.this.finish();
	}

	private void initCamera(SurfaceHolder surfaceHolder) {
		try {
			CameraManager.get().openDriver(surfaceHolder);
		} catch (IOException ioe) {
			return;
		} catch (RuntimeException e) {
			return;
		}
		if (handler == null) {
			handler = new CaptureActivityHandler(this, decodeFormats,
					characterSet);
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (!hasSurface) {
			hasSurface = true;
			initCamera(holder);
		}

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		hasSurface = false;

	}

	public ViewfinderView getViewfinderView() {
		return viewfinderView;
	}

	public Handler getHandler() {
		return handler;
	}

	public void drawViewfinder() {
		viewfinderView.drawViewfinder();

	}

	private void initBeepSound() {
		if (playBeep && mediaPlayer == null) {
			// The volume on STREAM_SYSTEM is not adjustable, and users found it
			// too loud,
			// so we now play on the music stream.
			setVolumeControlStream(AudioManager.STREAM_MUSIC);
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mediaPlayer.setOnCompletionListener(beepListener);

			AssetFileDescriptor file = getResources().openRawResourceFd(
					R.raw.beep);
			try {
				mediaPlayer.setDataSource(file.getFileDescriptor(),
						file.getStartOffset(), file.getLength());
				file.close();
				mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
				mediaPlayer.prepare();
			} catch (IOException e) {
				mediaPlayer = null;
			}
		}
	}

	private static final long VIBRATE_DURATION = 200L;

	private void playBeepSoundAndVibrate() {
		if (playBeep && mediaPlayer != null) {
			mediaPlayer.stop();

			try {
				mediaPlayer.prepare();
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			mediaPlayer.seekTo(0);
			mediaPlayer.start();
		}
		if (vibrate) {
			Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
			vibrator.vibrate(VIBRATE_DURATION);
		}
	}

	/**
	 * When the beep has finished playing, rewind to queue up another one.
	 */
	private final OnCompletionListener beepListener = new OnCompletionListener() {
		public void onCompletion(MediaPlayer mediaPlayer) {
			mediaPlayer.seekTo(0);
		}
	};

}