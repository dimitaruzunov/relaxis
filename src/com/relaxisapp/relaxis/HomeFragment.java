package com.relaxisapp.relaxis;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class HomeFragment extends Fragment {

	public final static String SECTION_TITLE = "section title";
	
	static TextView heartRateTextView;
	static TextView instantSpeedTextView;
	static TextView rRIntervalTextView;
	static TextView instantHeartRateTextView;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_home, container, false);
		
		setupViews(view);
		
		return view;
	}
	
	private void setupViews(View view) {
		heartRateTextView = (TextView) view.findViewById(R.id.heartRateTextView);
		heartRateTextView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				handleHeartRateTextViewClick((TextView) view);
			}
		});
		
		instantSpeedTextView = (TextView) view.findViewById(R.id.instantSpeedTextView);
		
		rRIntervalTextView = (TextView) view.findViewById(R.id.rRIntervalTextView);
		rRIntervalTextView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				handleRRIntervalTextViewClick((TextView) view);
			}
		});
		
		instantHeartRateTextView = (TextView) view.findViewById(R.id.instantHeartRateTextView);
		instantHeartRateTextView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				handleInstantHeartRateTextViewClick((TextView) view);
			}
		});
	}
	
	void handleHeartRateTextViewClick(TextView textView) {
		HintHelper.createAndPositionHint(getActivity(), R.string.heartRate, textView).show();
	}
	
	void handleInstantHeartRateTextViewClick(TextView textView) {
		HintHelper.createAndPositionHint(getActivity(), R.string.instantHeartRate, textView).show();
	}
	
	void handleRRIntervalTextViewClick(TextView textView) {
		HintHelper.createAndPositionHint(getActivity(), R.string.rRInterval, textView).show();
	}
	
	final static Handler Newhandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case BtConnection.HEART_RATE:
				String HeartRatetext = msg.getData().getString("HeartRate");
				HomeFragment.heartRateTextView.setText(HeartRatetext);
				break;

			case BtConnection.INSTANT_SPEED:
				String InstantSpeedtext = msg.getData().getString("InstantSpeed");
				HomeFragment.instantSpeedTextView.setText(InstantSpeedtext);
				break;

			case BtConnection.RR_INTERVAL:
				String RRInterval = msg.getData().getString("RRInterval");
				HomeFragment.rRIntervalTextView.setText(RRInterval);
				break;

			case BtConnection.INSTANT_HR:
				String InstantHR = msg.getData().getString("InstantHR");
				HomeFragment.instantHeartRateTextView.setText(InstantHR);
				break;
			}
		}

	};
	
}
