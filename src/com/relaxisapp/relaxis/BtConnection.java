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
	
	static final int HEART_RATE = 0x100;
	static final int INSTANT_SPEED = 0x101;
	static final int RR_INTERVAL = 0x102;
	static final int INSTANT_HR = 0x103;

	static int nn50 = 0;
	static int nnCount = 0;
	
	static GraphViewSeries InstantHRSeries = new GraphViewSeries(new GraphViewData[] {
		      new GraphViewData(1, 2.0d)
		      , new GraphViewData(2, 1.5d)
		      , new GraphViewData(3, 2.5d)
		      , new GraphViewData(4, 1.0d)
		});
}
