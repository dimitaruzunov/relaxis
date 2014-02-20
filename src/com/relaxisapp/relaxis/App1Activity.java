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
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;

public class App1Activity extends Activity {

	private int count = 1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app1);
		
		BtConnection._bt.Close();
		
		BtConnection._bt = new BTClient(BtConnection.adapter, BtConnection.BhMacID);
		
		BtConnection.instantHRListener = new NewConnectedListener(Newhandler, Newhandler);
		BtConnection._bt.addConnectedEventListener(BtConnection.instantHRListener);
		
		BtConnection._bt.start();
		
		GraphView graphView = new LineGraphView(this, "GraphViewTest");
		graphView.setScrollable(true);
		graphView.setScalable(true);
		graphView.addSeries(BtConnection.instantHRSeries);
		graphView.addSeries(BtConnection.idealBreathingCycle);
		
		LinearLayout layout = (LinearLayout) findViewById(R.id.graph1);
		layout.addView(graphView);
		
		/*Timer timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			
			int idealHR;
			
			@Override
			public void run() {
				BtConnection.idealBreathingCycle.appendData(new GraphViewData(count, 80 + Math.sin(count) * 7), true, 10);
			}
			
		}, 1000, 1000000000);*/
	}
	
	final Handler Newhandler = new Handler() {
		
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case BtConnection.INSTANT_HR:
				String InstantHR = msg.getData().getString("InstantHR");
				BtConnection.instantHRSeries.appendData(new GraphViewData(count, Double.parseDouble(InstantHR)), true, 10);
				count++;
				break;
			}
		}

	};

}
