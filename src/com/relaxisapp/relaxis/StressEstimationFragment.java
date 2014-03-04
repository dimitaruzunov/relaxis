package com.relaxisapp.relaxis;

import java.util.Timer;
import java.util.TimerTask;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class StressEstimationFragment extends Fragment {

	public final static String SECTION_TITLE = "section title";
	
	private Timer graphUpdateTimer = new Timer();
	private TimeLeftUpdateTimerTask graphUpdateTimerTask = new TimeLeftUpdateTimerTask();
	
	private Handler timeLeftUpdateHandler = new Handler();
	
	private static int TIME_SECONDS = 60;
	
	static int timeLeft = TIME_SECONDS;

	static double stressLevel;
	
	static TextView stressLevelTextView;
	private TextView stressLevelDescTextView;
	private TextView timeLeftTextView;
	private Button startStressEstimationButton;
	private boolean isStopped = true;
	
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
		startStressEstimationButton = (Button) view.findViewById(R.id.startStressEstimationButton);
		startStressEstimationButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				handleStartStressEstimationButtonClick((Button) view);
			}
		});
		
		timeLeftTextView = (TextView) view.findViewById(R.id.stressTimeLeftTextView);
		timeLeftTextView.setText(String.valueOf(TIME_SECONDS));
		
		stressLevelDescTextView = (TextView) view.findViewById(R.id.stressLevelDescTextView);
		
		stressLevelTextView = (TextView) view.findViewById(R.id.stressLevelTextView);
	}
	
	private void handleStartStressEstimationButtonClick(Button button) {
		if (isStopped) {
			start(button);
		} else {
			stop(button);
		}
	}
	
	private void start(Button button) {
		if (HomeFragment.connectionState != 2) {
			MainActivity.viewPager.setCurrentItem(SectionsPagerAdapter.HOME_FRAGMENT);
			Toast.makeText(getActivity(), "Please connect to HxM", Toast.LENGTH_SHORT).show();
		} else {
			isStopped = false;
			changeButtonIconStop(button);
			showStressLevel();
			StressEstimationFragment.stressLevelTextView
			.setText("Current stress level: " + stressLevel);
		}
	}
	
	private void stop(Button button) {
		isStopped = true;
		changeButtonIconStart(button);
		hideStressLevel();
	}
	
	private void changeButtonIconStart(Button button) {
		button.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_action_play, 0, 0, 0);
		button.setText(R.string.start);
	}
	
	private void changeButtonIconStop(Button button) {
		button.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_action_stop, 0, 0, 0);
		button.setText(R.string.stop);
	}
	
	private void showStressLevel() {
		stressLevelDescTextView.setVisibility(1);
		stressLevelTextView.setVisibility(1);
	}
	
	private void hideStressLevel() {
		stressLevelDescTextView.setVisibility(4);
		stressLevelTextView.setVisibility(4);
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
