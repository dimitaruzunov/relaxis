package com.relaxisapp.relaxis;

import java.util.Timer;
import java.util.TimerTask;

import zephyr.android.HxMBT.BTClient;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jjoe64.graphview.CustomLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.LineGraphView;

public class BreathingActivity extends Activity {

	// TODO find out why it is breaking at some point

	private Timer graphUpdateTimer = new Timer();
	private GraphUpdateTimerTask graphUpdateTimerTask = new GraphUpdateTimerTask();

	private Handler idealHRUpdateHandler = new Handler();

	private int TIMER_TICKS_PER_SECOND = 10;
	private int VIEWPORT_WIDTH = 24;
	private int IDEAL_MID_HR = 73;
	private int IDEAL_HR_DEVIATION = 7;

	private int idealMinHR = IDEAL_MID_HR - IDEAL_HR_DEVIATION;
	private int idealMaxHR = IDEAL_MID_HR + IDEAL_HR_DEVIATION;

	private int tMaxHR = idealMaxHR;
	private int tMinHR = idealMinHR;
	private double tAvgHR = (tMaxHR + tMinHR) / 2.0;
	private double tDeviation;
	private double tIdealHR;
	private double antiScoreSum = 0;
	private double score;

	// level settings
	private int EASY_TIME_SECONDS = 60;
	private int INTERMEDIATE_TIME_SECONDS = 120;
	private int HARD_TIME_SECONDS = 180;

	private int beatsCount = 0;
	private int timerCounter = 0;

	private LinearLayout layout;
	
	private GraphView graphView;

	private TextView timeLeftTextView;
	private TextView scoreTextView;
	private TextView lastAntiScoreTextView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_breathing);

		setupViews();

		BtConnection._bt.Close();

		BtConnection._bt = new BTClient(BtConnection.adapter,
				BtConnection.BhMacID);

		BtConnection.instantHRListener = new NewConnectedListener(Newhandler,
				Newhandler);
		BtConnection._bt
				.addConnectedEventListener(BtConnection.instantHRListener);

		BtConnection._bt.start();
		
		
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
					return String.valueOf((int)Math.floor(value));
				}
				else {
					return String.valueOf((int)Math.floor(value));
				}
			}
		});
		graphView.getGraphViewStyle().setNumHorizontalLabels(VIEWPORT_WIDTH / 2);
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

		// TODO check if the timer is cleared when the back button is pressed
		// and then the activity is started again
		graphUpdateTimer.scheduleAtFixedRate(graphUpdateTimerTask, 1000,
				1000 / TIMER_TICKS_PER_SECOND);

	}

	private void setupViews() {
		layout = (LinearLayout) findViewById(R.id.graph1);
		
		graphView = new LineGraphView(this, "GraphViewTest");
		
		timeLeftTextView = (TextView) findViewById(R.id.timeLeft);
		scoreTextView = (TextView) findViewById(R.id.score);
		lastAntiScoreTextView = (TextView) findViewById(R.id.lastAntiScore);
	}

	private class GraphUpdateTimerTask extends TimerTask {

		@Override
		public void run() {
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

	private void updateIdealHRGraph(final double currentIdealHR) {
		BtConnection.idealBreathingCycle.appendData(new GraphViewData(
				timerCounter * 1.0 / TIMER_TICKS_PER_SECOND, currentIdealHR),
				false, VIEWPORT_WIDTH * TIMER_TICKS_PER_SECOND);

		// keep the viewport 2 seconds forward and zoom it to the min/max ideal
		// HR
		BtConnection.dummySeries.appendData(new GraphViewData(
				(timerCounter * 1.0 / TIMER_TICKS_PER_SECOND) + 2,
				(timerCounter % 2 == 0) ? idealMinHR : idealMaxHR), true, 2);
	}

	private void updateTimeLeft() {
		timeLeftTextView.setText(String.valueOf(EASY_TIME_SECONDS
				- timerCounter / TIMER_TICKS_PER_SECOND));
	}

	final Handler Newhandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case BtConnection.INSTANT_HR:
				String instantHRString = msg.getData().getString("InstantHR");
				int instantHR = Integer.parseInt(instantHRString);
				BtConnection.instantHRSeries.appendData(new GraphViewData(
						beatsCount, instantHR), false, VIEWPORT_WIDTH+1);
				beatsCount++;

				if (instantHR > tMaxHR) {
					tMaxHR = instantHR;
				}
				if (instantHR < tMinHR) {
					tMinHR = instantHR;
				}
				tAvgHR = (tMaxHR + tMinHR) / 2.0;
				tDeviation = tMaxHR - tAvgHR;

				antiScoreSum += Math.abs(instantHR - tIdealHR);
				score = beatsCount * 100.0 / antiScoreSum;

				scoreTextView.setText(String.valueOf(score));
				lastAntiScoreTextView.setText(String.valueOf(Math.abs(instantHR
						- tIdealHR)));

				break;
			}
		}

	};

	@Override
    public void onDestroy() {
        super.onDestroy();
        
        graphUpdateTimerTask.cancel();
		graphUpdateTimer.cancel();
		
		layout.removeView(graphView);
    }
	
}
