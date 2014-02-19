package com.relaxisapp.relaxis;

import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.GraphView.GraphViewData;

import zephyr.android.HxMBT.BTClient;
import zephyr.android.HxMBT.ZephyrProtocol;
import android.bluetooth.BluetoothAdapter;

public class BtConnection {
	static BluetoothAdapter adapter = null;
	static BTClient _bt;
	static ZephyrProtocol _protocol;
	static NewConnectedListener _NConnListener;
	static NewConnectedListener instantHRListener;
	
	static final int HEART_RATE = 0x100;
	static final int INSTANT_SPEED = 0x101;
	static final int RR_INTERVAL = 0x102;
	static final int INSTANT_HR = 0x103;

	static int nn50 = 0;
	static int nnCount = 0;
	
	static GraphViewSeries instantHRSeries = new GraphViewSeries(new GraphViewData[] {});
}
