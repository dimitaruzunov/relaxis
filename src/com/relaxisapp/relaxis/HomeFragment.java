package com.relaxisapp.relaxis;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class HomeFragment extends Fragment implements OnBtConnectionChangeListener {

	public final static String SECTION_TITLE = "section title";

	static TextView heartRateTextView;
	static TextView instantSpeedTextView;
	static TextView rRIntervalTextView;
	static TextView instantHeartRateTextView;
	static TextView testTextView;
	
	public static int connectionState = 0;
	Button connectButton;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_home, container, false);

		setupViews(view);
		
		return view;
	}

	private void setupViews(View view) {
		connectButton = (Button) view.findViewById(R.id.connectButton);
		connectButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				handleConnectButtonClick((Button) view);
			}
		});

		heartRateTextView = (TextView) view.findViewById(R.id.heartRateTextView);
		heartRateTextView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				handleHeartRateTextViewClick((TextView) view);
			}
		});

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
		
		instantSpeedTextView = (TextView) view.findViewById(R.id.instantSpeedTextView);
		instantSpeedTextView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				handleInstantSpeedTextViewClick((TextView) view);
			}
		});
	}
	
	void handleConnectButtonClick(Button button) {
		if (this.connectionState == 0) {
			((MainActivity) getActivity()).executeConnect(button);
		}
		else if (this.connectionState == 2) {
			((MainActivity) getActivity()).executeDisconnect(button);
		}
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
	
	void handleInstantSpeedTextViewClick(TextView textView) {
		HintHelper.createAndPositionHint(getActivity(), R.string.instantSpeed, textView).show();
	}
	
	@Override
	public void onBtConnectionChange(int connectionState, Button button) {
		this.connectionState = connectionState;
		
		switch (connectionState) {
		case 0:
			changeBtIconConnect(button);
			break;
		case 1:
			changeBtIconConnecting(button);
			break;
		case 2:
			changeBtIconConnected(button);
			break;
		}
	}
	
	void changeBtIconConnect(Button button) {
		button.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_action_bluetooth, 0, 0, 0);
		button.setText(R.string.action_bluetooth_connect);
	}

	void changeBtIconConnecting(Button button) {
		button.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_action_bluetooth_searching, 0, 0, 0);
		button.setText(R.string.action_bluetooth_connecting);
	}

	void changeBtIconConnected(Button button) {
		button.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_action_bluetooth_connected, 0, 0, 0);
		button.setText(R.string.action_bluetooth_disconnect);
	}

}
