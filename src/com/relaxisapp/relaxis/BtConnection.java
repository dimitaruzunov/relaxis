package com.relaxisapp.relaxis;

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
}
