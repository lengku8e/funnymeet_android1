package com.mtcent.funnymeet.ui.view.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import mtcent.funnymeet.R;

public class ConfirmDialogFragment extends DialogFragment {
	public final static String CONFIRM_RESULT_YES = "2"; //2:accept 
	public final static String CONFIRM_RESULT_NO = "3";  //3: reject
	private TextView mMessage;

	public interface MessageInputListener {
		void onMessageInputComplete(String result);
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		// Get the layout inflater
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View view = inflater.inflate(R.layout.dialog_yesno, null);
		mMessage = (TextView) view.findViewById(R.id.message);
		mMessage.setText("是否接受俱乐部的邀请?");

		builder.setView(view)
				.setPositiveButton("接受邀请",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int id) {
								MessageInputListener listener = (MessageInputListener) getActivity();
								listener.onMessageInputComplete(ConfirmDialogFragment.CONFIRM_RESULT_YES);
							}
						})
				.setNegativeButton("残忍拒绝",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int id) {
								MessageInputListener listener = (MessageInputListener) getActivity();
								listener.onMessageInputComplete(ConfirmDialogFragment.CONFIRM_RESULT_NO);
							}
						});
		return builder.create();
	}

}
