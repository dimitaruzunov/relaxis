package com.relaxisapp.relaxis;

import java.util.Timer;
import java.util.TimerTask;

import zephyr.android.HxMBT.BTClient;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.widget.TextView;

public class StressEstimationActivity extends Activity {

	private Timer graphUpdateTimer = new Timer();
	private TimeLeftUpdateTimerTask graphUpdateTimerTask = new TimeLeftUpdateTimerTask();
	
	private Handler timeLeftUpdateHandler = new Handler();
	
	private int TIME_SECONDS = 60;
	
	private int timeLeft = TIME_SECONDS;

	private TextView stressLevelTextView;
	private TextView timeLeftTextView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_stress_estimation);
		
		setupViews();
		
		BtConnection._bt.Close();

		BtConnection._bt = new BTClient(BtConnection.adapter,
				BtConnection.BhMacID);

		BtConnection.stressLevelListener = new NewConnectedListener(Newhandler,
				Newhandler);
		BtConnection._bt
				.addConnectedEventListener(BtConnection.instantHRListener);

		BtConnection._bt.start();

		// TODO check if the timer is cleared when the back button is pressed
		// and then the activity is started again
		graphUpdateTimer.scheduleAtFixedRate(graphUpdateTimerTask, 0, 1000);
		
	}

	private void setupViews() {
		stressLevelTextView = (TextView) findViewById(R.id.stressLevel);
		timeLeftTextView = (TextView) findViewById(R.id.timeLeft);
	}
	
	private class TimeLeftUpdateTimerTask extends TimerTask {

		@Override
		public void run() {
			
			timeLeftUpdateHandler.post(new Runnable() {

				@Override
				public void run() {
					updateTimeLeft();
				}
			});
		}

	}

	private void updateTimeLeft() {
		timeLeftTextView.setText(String.valueOf(timeLeft));
		timeLeft--;
	}
	
//	private void saveData(double stressLevel) throws IOException {
//
//		FileOutputStream fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);
//		try {
//			fos.write(String.valueOf(stressLevel).getBytes());
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		fos.close();
//	}
	
//	private double getData() {
//		double stressLevel;
//		
//		FileOutputStream fos = openFileOutput(FILENAME, Context.MODE);
//		try {
//			fos.write(String.valueOf(stressLevel).getBytes());
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		fos.close();
//		
//		return stressLevel; 
//	}
	
	final Handler Newhandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			if (timeLeft <= 0) {
				switch (msg.what) {
				case BtConnection.PNN50:
					String pNN50 = msg.getData().getString("pNN50");
					
					stressLevelTextView.setText("Current stress level: " + pNN50);

					break;
				}
			}
		}

	};

}
