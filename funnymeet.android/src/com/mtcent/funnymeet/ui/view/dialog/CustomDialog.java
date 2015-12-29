package com.mtcent.funnymeet.ui.view.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import mtcent.funnymeet.R;

public class CustomDialog extends Dialog {

	public CustomDialog(Context context) {
		super(context, R.style.CustomDialog);
		// CustomDialog.this.requestWindowFeature(Window.FEATURE_NO_TITLE);
	}

	public static CustomDialog createWaitDialog(Activity actv, String msg,
			Boolean cancenl) {
		final CustomDialog waitDialog = new CustomDialog(actv);
		waitDialog.setContentView(R.layout.dialog_wait);
		waitDialog.setCancelable(cancenl);
		waitDialog.setCanceledOnTouchOutside(cancenl);
		TextView tv = (TextView) waitDialog.findViewById(R.id.textView1);
		tv.setText(msg);
		if (cancenl) {
			waitDialog.findViewById(R.id.outside).setOnClickListener(
					new View.OnClickListener() {
						@Override
						public void onClick(View arg0) {
							waitDialog.dismiss();
						}
					});
		}
		return waitDialog;
	}

	public static CustomDialog createMsgDialog(Activity actv, String msg,
			final OnDismissListener onHide) {
		final CustomDialog waitDialog = new CustomDialog(actv);
		waitDialog.setContentView(R.layout.dialog_msg);
		TextView tv = (TextView) waitDialog.findViewById(R.id.textView1);
		tv.setText(msg);

		waitDialog.findViewById(R.id.cancelchange).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View arg0) {
						waitDialog.dismiss();
					}
				});
		waitDialog.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface arg0) {
				if (onHide != null) {
					onHide.onDismiss(arg0);
				}
			}
		});

		return waitDialog;
	}

	public interface OnConfirmListern {
		void onCancle();

		void onConfirm(String text);
	}
	public static CustomDialog createConfirmDialog(Activity actv, String msg,
			boolean hasInput, String cancelTitle, String confirmTitle,OnConfirmListern listern) {
		CustomDialog confirmDialog = createConfirmDialog(actv,msg,hasInput,listern);
		((TextView)confirmDialog.findViewById(R.id.cancel)).setText(cancelTitle);
		((TextView)confirmDialog.findViewById(R.id.confirm)).setText(confirmTitle);
		return confirmDialog;
	}
	public static CustomDialog createConfirmDialog(Activity actv, String msg,
			boolean hasInput, final OnConfirmListern listern) {
		final CustomDialog waitDialog = new CustomDialog(actv);
		waitDialog.setContentView(R.layout.dialog_input);
		TextView tv = (TextView) waitDialog.findViewById(R.id.msg);
		tv.setText(msg);
		if (!hasInput) {
			waitDialog.findViewById(R.id.inputEditText)
					.setVisibility(View.GONE);
		}
		waitDialog.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface arg0) {
				if (listern != null) {
					listern.onCancle();
				}
			}
		});
		waitDialog.findViewById(R.id.cancel).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View arg0) {
						waitDialog.dismiss();
					}
				});
		waitDialog.findViewById(R.id.confirm).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View arg0) {
						waitDialog.hide();
						if (listern != null) {
							EditText et = (EditText) waitDialog
									.findViewById(R.id.inputEditText);
							String text = et.getText().toString();
							et.setText(null);
							listern.onConfirm(text);
						}
					}
				});
		return waitDialog;
	}
	// public CustomDialog(Context context, View contentVie) {
	// super(context, R.style.CustomDialog);
	// CustomDialog.this.setContentView(contentVie);
	// }
	//
	// public CustomDialog(Context context, int layoutId) {
	// super(context, R.style.CustomDialog);
	// CustomDialog.this.setContentView(layoutId);
	// }

}