package com.relaxisapp.relaxis;

import java.util.Timer;
import java.util.TimerTask;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

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

	static int idealMinHR = Const.IDEAL_MID_HR - Const.IDEAL_HR_DEVIATION;
	static int idealMaxHR = Const.IDEAL_MID_HR + Const.IDEAL_HR_DEVIATION;

	static int avgMaxHR = idealMaxHR;
	static int avgMinHR = idealMinHR;
	static int newMaxHR = avgMaxHR;
	static int newMinHR = avgMinHR;
	static double tAvgHR = (avgMaxHR + avgMinHR) / 2.0;
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

	static LinearLayout layout;

	static GraphView graphView;

	static TextView timeLeftTextView;

	static TextView scoreTextView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_breathing, container,
				false);

		setupViews(view);

		graphView.setScrollable(true);
		graphView.setScalable(true);
		graphView.setViewPort(0, Const.VIEWPORT_WIDTH);
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
				.setNumHorizontalLabels(Const.VIEWPORT_WIDTH / 2);
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
		layout = (LinearLayout) view.findViewById(R.id.graph1);
		timeLeftTextView = (TextView) view.findViewById(R.id.timeLeft);
		scoreTextView = (TextView) view.findViewById(R.id.score);

		graphView = new LineGraphView(getActivity(), "GraphViewTest");
	}

	static class GraphUpdateTimerTask extends TimerTask {

		@Override
		public void run() {
			idealHRUpdateHandler.post(new Runnable() {

				@Override
				public void run() {
					updateIdealHR();
					updateIdealHRGraph(tIdealHR);
					updateTimeLeft();
				}
			});
			timerCounter++;
		}

	}

	private static void updateIdealHR() {
		if (Math.sin(timerCounter * Math.PI / (6 * Const.TIMER_TICKS_PER_SECOND)) == 1) {
			avgMinHR = newMinHR;
		} else if (Math.sin(timerCounter * Math.PI
				/ (6 * Const.TIMER_TICKS_PER_SECOND)) == -1) {
			avgMaxHR = newMaxHR;
		}
		tAvgHR = (avgMaxHR + avgMinHR) / 2.0;
		tDeviation = avgMaxHR - tAvgHR;
		tIdealHR = tAvgHR
				+ Math.sin(timerCounter * Math.PI
						/ (6 * Const.TIMER_TICKS_PER_SECOND)) * tDeviation;
	}

	private static void updateIdealHRGraph(final double currentIdealHR) {
		BtConnection.idealBreathingCycle.appendData(new GraphViewData(
				timerCounter * 1.0 / Const.TIMER_TICKS_PER_SECOND, currentIdealHR),
				false, Const.VIEWPORT_WIDTH * Const.TIMER_TICKS_PER_SECOND);

		// keep the viewport 2 seconds forward and zoom it to the min/max ideal
		// HR
		BtConnection.dummySeries.appendData(new GraphViewData(
				(timerCounter * 1.0 / Const.TIMER_TICKS_PER_SECOND) + 2,
				(timerCounter % 2 == 0) ? idealMinHR : idealMaxHR), true, 2);
	}

	private static void updateTimeLeft() {
		timeLeftTextView.setText(String.valueOf(EASY_TIME_SECONDS
				- timerCounter / Const.TIMER_TICKS_PER_SECOND));
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		graphUpdateTimerTask.cancel();
		graphUpdateTimer.cancel();

		layout.removeView(graphView);
	}

}
