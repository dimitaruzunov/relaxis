package com.relaxisapp.relaxis;

import java.util.Timer;
import java.util.TimerTask;

import zephyr.android.HxMBT.BTClient;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.LinearLayout;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.LineGraphView;

public class App1Activity extends Activity {

	static private Timer timer = new Timer();

	private int VIEWPORT_WIDTH = 24;
	private int IDEAL_MID_HR = 80;
	private int IDEAL_HR_DEVIATION = 15;

	private int idealMinHR = IDEAL_MID_HR - IDEAL_HR_DEVIATION;
	private int idealMaxHR = IDEAL_MID_HR + IDEAL_HR_DEVIATION;

	private int count = 1;
	private int timerCounter = 0;
	private double idealHR;
	private Handler idealHRUpdateHandler = new Handler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app1);

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

		LinearLayout layout = (LinearLayout) findViewById(R.id.graph1);
		layout.addView(graphView);

		//TODO check if the timer is cleared when the back button is pressed and then the activity is started again
		timer.scheduleAtFixedRate(new IdealHRUpdateTimerTask(), 0, 100);

	}

	private class IdealHRUpdateTimerTask extends TimerTask {

		@Override
		public void run() {
			idealHR = IDEAL_MID_HR + Math.sin(timerCounter * Math.PI / 60)
					* IDEAL_HR_DEVIATION;
			idealHRUpdateHandler.post(new Runnable() {

				@Override
				public void run() {
					updateIdealHRGraph(idealHR);
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
						: idealMaxHR), true, VIEWPORT_WIDTH * 10);
	}

	final Handler Newhandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case BtConnection.INSTANT_HR:
				String InstantHR = msg.getData().getString("InstantHR");
				BtConnection.instantHRSeries.appendData(new GraphViewData(
						count, Double.parseDouble(InstantHR)), false,
						VIEWPORT_WIDTH);
				count++;
				break;
			}
		}

	};

}
