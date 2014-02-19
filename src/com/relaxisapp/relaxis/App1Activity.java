package com.relaxisapp.relaxis;

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
		
		BtConnection.instantHRListener = new NewConnectedListener(Newhandler, Newhandler);
		BtConnection._bt.addConnectedEventListener(BtConnection.instantHRListener);
		
		GraphView graphView = new LineGraphView(this, "GraphViewTest");
		graphView.addSeries(BtConnection.instantHRSeries);
		
		LinearLayout layout = (LinearLayout) findViewById(R.id.graph1);
		layout.addView(graphView);
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
