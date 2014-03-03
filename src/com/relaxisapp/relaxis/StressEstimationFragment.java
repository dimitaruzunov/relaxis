package com.relaxisapp.relaxis;

import java.util.Timer;
import java.util.TimerTask;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class StressEstimationFragment extends Fragment {

	public final static String SECTION_TITLE = "section title";
	
	private Timer graphUpdateTimer = new Timer();
	private TimeLeftUpdateTimerTask graphUpdateTimerTask = new TimeLeftUpdateTimerTask();
	
	private Handler timeLeftUpdateHandler = new Handler();
	
	private static int TIME_SECONDS = 60;
	
	static int timeLeft = TIME_SECONDS;

	static TextView stressLevelTextView;
	private TextView timeLeftTextView;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_stress_estimation, container, false);
		
		setupViews(view);
		
		// TODO check if the timer is cleared when the back button is pressed
		// and then the activity is started again
		// graphUpdateTimer.scheduleAtFixedRate(graphUpdateTimerTask, 0, 1000);
		
		return view;
	}

	private void setupViews(View view) {
		stressLevelTextView = (TextView) view.findViewById(R.id.stressLevel);
		timeLeftTextView = (TextView) view.findViewById(R.id.timeLeft);
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
		timeLeftTextView.append(": " + String.valueOf(timeLeft));
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

}
