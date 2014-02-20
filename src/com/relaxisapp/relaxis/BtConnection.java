package com.relaxisapp.relaxis;

import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.GraphView.GraphViewData;

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
	static String BhMacID;
	
	static final int HEART_RATE = 0x100;
	static final int INSTANT_SPEED = 0x101;
	static final int RR_INTERVAL = 0x102;
	static final int INSTANT_HR = 0x103;

	static int nn50 = 0;
	static int nnCount = 0;
	
	static double SDSum = 0;
	static double SDCount = 0;
	
	static GraphViewSeries instantHRSeries = new GraphViewSeries(new GraphViewData[] {});
	static GraphViewSeries idealBreathingCycle = new GraphViewSeries(new GraphViewData[] {});
}
