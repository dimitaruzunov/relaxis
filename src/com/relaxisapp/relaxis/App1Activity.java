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

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.LineGraphView;

public class App1Activity extends Activity {

	// TODO find out why it is breaking at some point

	static private Timer graphUpdateTimer = new Timer();

	private int VIEWPORT_WIDTH = 24;
	private int IDEAL_MID_HR = 73;
	private int IDEAL_HR_DEVIATION = 7;

	private int idealMinHR = IDEAL_MID_HR - IDEAL_HR_DEVIATION;
	private int idealMaxHR = IDEAL_MID_HR + IDEAL_HR_DEVIATION;

	private int tMaxHR = idealMaxHR;
	private int tMinHR = idealMinHR;
	private double tAvgHR;
	private double tDeviation;
	private double tIdealHR;
	private double antiScoreSum = 0;
	private double score;

	private int EASY_TIME_SECONDS = 60;
	private int INTERMEDIATE_TIME_SECONDS = 120;
	private int HARD_TIME_SECONDS = 180;
	
	private int beatsCount = 0;
	private int timerCounter = 0;
	
	private Handler idealHRUpdateHandler = new Handler();


	private TextView timeLeftTextView;
	private TextView scoreTextView;
	private TextView lastAntiScoreTextView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app1);

		setupViews();
		
		BtConnection._bt.Close();

		BtConnection._bt = new BTClient(BtConnection.adapter,
				BtConnection.BhMacID);

		BtConnection.instantHRListener = new NewConnectedListener(Newhandler,
				Newhandler);
		BtConnection._bt
				.addConnectedEventListener(BtConnection.instantHRListener);

		BtConnection._bt.start();

		GraphView graphView = new LineGraphView(this, "GraphViewTest");
		graphView.setScrollable(true);
		graphView.setScalable(true);
		graphView.setViewPort(0, VIEWPORT_WIDTH);
		graphView.addSeries(BtConnection.idealBreathingCycle);
		graphView.addSeries(BtConnection.instantHRSeries);
		graphView.addSeries(BtConnection.dummySeries);
		// TODO show legend and customize it

		LinearLayout layout = (LinearLayout) findViewById(R.id.graph1);
		layout.addView(graphView);

		// TODO check if the timer is cleared when the back button is pressed
		// and then the activity is started again
		graphUpdateTimer.cancel();
		graphUpdateTimer = new Timer();
		graphUpdateTimer.scheduleAtFixedRate(new GraphUpdateTimerTask(),
				1000, 100);

	}
	
	private void setupViews() {
		timeLeftTextView = (TextView) findViewById(R.id.timeLeft);
		scoreTextView = (TextView) findViewById(R.id.score);
		lastAntiScoreTextView = (TextView) findViewById(R.id.lastAntiScore);
	}

	private class GraphUpdateTimerTask extends TimerTask {

		@Override
		public void run() {
			tIdealHR = tAvgHR + Math.sin(timerCounter * Math.PI / 60)
					* tDeviation;
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
				timerCounter / 10.0, currentIdealHR), false,
				VIEWPORT_WIDTH * 10);

		// keep the viewport 2 seconds forward and zoom it to the min/max ideal
		// HR
		BtConnection.dummySeries.appendData(new GraphViewData(
				(timerCounter / 10.0) + 2, (timerCounter % 2 == 0) ? idealMinHR
						: idealMaxHR), true, 2);
	}

	private void updateTimeLeft() {
		timeLeftTextView.setText(String.valueOf(EASY_TIME_SECONDS - timerCounter / 10));
	}

	final Handler Newhandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case BtConnection.INSTANT_HR:
				String instantHRString = msg.getData().getString("InstantHR");
				int instantHR = Integer.parseInt(instantHRString);
				BtConnection.instantHRSeries.appendData(new GraphViewData(
						beatsCount, instantHR), false, VIEWPORT_WIDTH);
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
				lastAntiScoreTextView.setText(String.valueOf(Math.abs(instantHR - tIdealHR)));

				break;
			}
		}

	};

}
