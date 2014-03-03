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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.CustomLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.LineGraphView;

public class BreathingFragment extends Fragment {

	// TODO find out why it is breaking at some point

	public final static String SECTION_TITLE = "section title";

	static Timer graphUpdateTimer = new Timer();
	static GraphUpdateTimerTask graphUpdateTimerTask;

	static Handler idealHRUpdateHandler = new Handler();

	final static int TIMER_TICKS_PER_SECOND = 10;
	final static int VIEWPORT_WIDTH = 24;
	final static int IDEAL_MID_HR = 73;
	final static int IDEAL_HR_DEVIATION = 7;

	final static int POINT_BARRIER = 20;

	static int idealMinHR = IDEAL_MID_HR - IDEAL_HR_DEVIATION;
	static int idealMaxHR = IDEAL_MID_HR + IDEAL_HR_DEVIATION;

	static int tMaxHR = idealMaxHR;
	static int tMinHR = idealMinHR;
	static int newMaxHR = tMaxHR;
	static int newMinHR = tMinHR;
	static double tAvgHR = (tMaxHR + tMinHR) / 2.0;
	static double tDeviation;
	static double tIdealHR;
	static int score = 0;
	static int consecutivePoints = 0;
	static int multiplier = 1;

	// level settings
	static final int EASY_TIME_SECONDS = 60;
	static final int INTERMEDIATE_TIME_SECONDS = 120;
	static final int HARD_TIME_SECONDS = 180;

	static int beatsCount = 0;
	static int timerCounter = 0;

	LinearLayout layout;

	static GraphView graphView;

	static TextView timeLeftTextView;

	static TextView scoreTextView;
	
	private boolean isStopped = true;
	private Button startBreathingButton;
	private TextView scoreDescTextView;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_breathing, container,
				false);

		setupViews(view);

		graphView.setScrollable(true);
		graphView.setScalable(true);
		graphView.setViewPort(0, VIEWPORT_WIDTH);
		graphView.setCustomLabelFormatter(new CustomLabelFormatter() {

			@Override
			public String formatLabel(double value, boolean isValueX) {
				if (isValueX) {
					if (value < 0) {
						return "";
					}
					return String.valueOf((int) Math.floor(value));
				} else {
					return String.valueOf((int) Math.floor(value));
				}
			}
		});
		graphView.getGraphViewStyle()
				.setNumHorizontalLabels(VIEWPORT_WIDTH / 2);
		graphView.getGraphViewStyle().setNumVerticalLabels(5);
		graphView.getGraphViewStyle().setVerticalLabelsWidth(80);
		BtConnection.idealBreathingCycle.resetData(new GraphViewData[] {});
		BtConnection.instantHRSeries.resetData(new GraphViewData[] {});
		BtConnection.dummySeries.resetData(new GraphViewData[] {});
		graphView.addSeries(BtConnection.idealBreathingCycle);
		graphView.addSeries(BtConnection.instantHRSeries);
		graphView.addSeries(BtConnection.dummySeries);
		// TODO show legend and customize it

		layout.addView(graphView);

		return view;
	}

	private void setupViews(View view) {
		layout = (LinearLayout) view.findViewById(R.id.breathingFragmentLinearLayout);
		
		timeLeftTextView = (TextView) view.findViewById(R.id.breathingTimeLeftTextView);
		timeLeftTextView.setText(String.valueOf(EASY_TIME_SECONDS));
		
		scoreDescTextView = (TextView) view.findViewById(R.id.scoreDescTextView);
		scoreTextView = (TextView) view.findViewById(R.id.scoreTextView);
		
		startBreathingButton = (Button) view.findViewById(R.id.startBreathingButton);
		startBreathingButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				handleStartBreathingButtonClick((Button) view);
			}
		});

		graphView = new LineGraphView(getActivity(), "GraphViewTest");
	}
	
	private void handleStartBreathingButtonClick(Button button) {
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
			showScore();
		}
	}
	
	private void stop(Button button) {
		isStopped = true;
		changeButtonIconStart(button);
		hideScore();
	}
	
	private void showScore() {
		scoreDescTextView.setVisibility(1);
		scoreTextView.setVisibility(1);
	}
	
	private void hideScore() {
		scoreDescTextView.setVisibility(4);
		scoreTextView.setVisibility(4);
	}
	
	private void changeButtonIconStart(Button button) {
		button.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_action_play, 0, 0, 0);
		button.setText(R.string.start);
	}
	
	private void changeButtonIconStop(Button button) {
		button.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_action_stop, 0, 0, 0);
		button.setText(R.string.stop);
	}

	static class GraphUpdateTimerTask extends TimerTask {

		@Override
		public void run() {
			if (Math.sin(timerCounter * Math.PI
					/ (6 * TIMER_TICKS_PER_SECOND)) == 1) {
				tMinHR = newMinHR;
			} else if (Math.sin(timerCounter * Math.PI
					/ (6 * TIMER_TICKS_PER_SECOND)) == -1) {
				tMaxHR = newMaxHR;
			}
			tIdealHR = tAvgHR
					+ Math.sin(timerCounter * Math.PI
							/ (6 * TIMER_TICKS_PER_SECOND)) * tDeviation;
			idealHRUpdateHandler.post(new Runnable() {

				@Override
				public void run() {
					updateIdealHRGraph(tIdealHR);
					updateTimeLeft();
				}
			});
			timerCounter++;
		}

	}

	private static void updateIdealHRGraph(final double currentIdealHR) {
		BtConnection.idealBreathingCycle.appendData(new GraphViewData(
				timerCounter * 1.0 / TIMER_TICKS_PER_SECOND, currentIdealHR),
				false, VIEWPORT_WIDTH * TIMER_TICKS_PER_SECOND);

		// keep the viewport 2 seconds forward and zoom it to the min/max ideal
		// HR
		BtConnection.dummySeries.appendData(new GraphViewData(
				(timerCounter * 1.0 / TIMER_TICKS_PER_SECOND) + 2,
				(timerCounter % 2 == 0) ? idealMinHR : idealMaxHR), true, 2);
	}

	private static void updateTimeLeft() {
		timeLeftTextView.setText(String.valueOf(EASY_TIME_SECONDS
				- timerCounter / TIMER_TICKS_PER_SECOND));
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		graphUpdateTimerTask.cancel();
		graphUpdateTimer.cancel();

		layout.removeView(graphView);
	}
	
}
