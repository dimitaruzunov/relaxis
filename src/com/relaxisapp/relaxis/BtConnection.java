package com.relaxisapp.relaxis;

import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphViewSeries.GraphViewSeriesStyle;

import zephyr.android.HxMBT.BTClient;
import zephyr.android.HxMBT.ZephyrProtocol;
import android.bluetooth.BluetoothAdapter;
import android.graphics.Color;

public class BtConnection {
	static BluetoothAdapter adapter = null;
	static BTClient _bt;
	static ZephyrProtocol _protocol;
	static NewConnectedListener _NConnListener;
	static NewConnectedListener instantHRListener;
	static NewConnectedListener stressLevelListener;
	static String BhMacID;
	static String deviceName;

	static int [] recentInstantHR;
	
	static int [] recentNn50;
	static int nnCount;

	static double SDSum = 0;
	static double SDCount = 0;

	static GraphViewSeries instantHRSeries = new GraphViewSeries(
			"Instant HR Curve", new GraphViewSeriesStyle(
					Color.rgb(20, 20, 255), 3), new GraphViewData[] {});
	static GraphViewSeries idealBreathingCycle = new GraphViewSeries(
			"Ideal Breathing Curve", new GraphViewSeriesStyle(
					Color.rgb(255, 20, 20), 3), new GraphViewData[] {});
	
	// HACK: used to move the viewport the way we want
	static GraphViewSeries dummySeries = new GraphViewSeries(
			"Ideal Breathing Curve", new GraphViewSeriesStyle(
					R.color.lightgrey, 0), new GraphViewData[] {});
}
