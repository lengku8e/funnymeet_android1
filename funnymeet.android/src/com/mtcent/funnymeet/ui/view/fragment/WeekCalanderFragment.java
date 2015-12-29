package com.mtcent.funnymeet.ui.view.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import mtcent.funnymeet.R;

public class WeekCalanderFragment extends Fragment {

	public View onCreateView(LayoutInflater inflater, ViewGroup parent,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.week_calander_fragment, parent, false);
		return v;
	}
}
