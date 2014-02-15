package com.relaxisapp.relaxis;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class BTBondReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle b = intent.getExtras();
		BluetoothDevice device = BtConnection.adapter.getRemoteDevice(b.get(
				"android.bluetooth.device.extra.DEVICE").toString());
		Log.d("Bond state", "BOND_STATED = " + device.getBondState());
	}
}
