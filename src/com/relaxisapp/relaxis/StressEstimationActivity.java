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

public class StressEstimationActivity extends Activity {

	private int count = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app1);
	}

	final Handler Newhandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			if (BtConnection.nnCount > 200) {
				switch (msg.what) {
				case BtConnection.INSTANT_HR:
					String InstantHR = msg.getData().getString("InstantHR");
					BtConnection.instantHRSeries.appendData(new GraphViewData(
							count, Double.parseDouble(InstantHR)), true, 10);

					break;
				}
			}
		}

	};

}
