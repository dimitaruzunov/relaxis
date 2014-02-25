package com.relaxisapp.relaxis;

import java.util.Set;

import zephyr.android.HxMBT.BTClient;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.ListView;

public class MainActivity extends FragmentActivity implements ListView.OnItemClickListener {

	public static final int REQUEST_ENABLE_BT = 1000;

	private boolean connected = false;
	
	NavigationDrawerHelper navigationDrawerHelper;
	SectionsPagerAdapter sectionsPagerAdapter;
	ViewPager viewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		navigationDrawerHelper = new NavigationDrawerHelper();
        navigationDrawerHelper.init(this, this);
        
        sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), this);
        
        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(sectionsPagerAdapter);

		// TODO Try to put the following code out of the onCreate method

		/*
		 * Sending a message to android that we are going to initiate a pairing
		 * request
		 */
		IntentFilter filter = new IntentFilter("android.bluetooth.device.action.PAIRING_REQUEST");
		/*
		 * Registering a new BTBroadcast receiver from the Main Activity context
		 * with pairing request event
		 */
		this.getApplicationContext().registerReceiver(new BTBroadcastReceiver(), filter);
		// Registering the BTBondReceiver in the application that the
		// status of the receiver has changed to Paired
		IntentFilter filter2 = new IntentFilter("android.bluetooth.device.action.BOND_STATE_CHANGED");
		this.getApplicationContext().registerReceiver(new BTBondReceiver(), filter2);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		boolean handled = true;

		int id = item.getItemId();
		switch (id) {
		case R.id.action_bluetooth:
			if (!connected) {
				onClickMenuBluetoothConnect(item);
			} else {
				onClickMenuBluetoothDisconnect(item);
			}
			break;
		default:
			handled = super.onOptionsItemSelected(item);
		}
		return handled;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent resultIntent) {
		switch (requestCode) {
		case REQUEST_ENABLE_BT:
			handleBluetoothConnectResult(resultCode, resultIntent);
			break;
		}
	}

	private void handleBluetoothConnectResult(int resultCode, Intent resultIntent) {
		if (resultCode == RESULT_OK) {
            Toast.makeText(this, "Bluetooth is now enabled", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "User cancelled the bluetooth connect intent", Toast.LENGTH_LONG).show();
        }
	}

	void onClickMenuBluetoothConnect(MenuItem item) {
		item.setIcon(R.drawable.ic_action_bluetooth_searching);
		item.setTitle(R.string.action_bluetooth_connecting);

		// Getting the Bluetooth adapter
		BtConnection.adapter = BluetoothAdapter.getDefaultAdapter();

		// Check for Bluetooth support
		if (BtConnection.adapter == null) {
			Toast.makeText(this, "Bluetooth is not supported.", Toast.LENGTH_LONG).show();
			return;
		}

		// Enable bluetooth
		if (!BtConnection.adapter.isEnabled()) {
			Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
		}

		// TODO try to write this better
		while (!BtConnection.adapter.isEnabled()) { // wait until the bluetooth
													// is on
		}

		Set<BluetoothDevice> pairedDevices = BtConnection.adapter.getBondedDevices();
		if (pairedDevices.size() > 0) {
			for (BluetoothDevice device : pairedDevices) {
				if (device.getName().startsWith("HXM")) {
					BluetoothDevice btDevice = device;
					BtConnection.BhMacID = btDevice.getAddress();

					BluetoothDevice Device = BtConnection.adapter.getRemoteDevice(BtConnection.BhMacID);
					String DeviceName = Device.getName();
					BtConnection._bt = new BTClient(BtConnection.adapter, BtConnection.BhMacID);
					BtConnection._NConnListener = new NewConnectedListener(Newhandler, Newhandler);
					BtConnection._bt.addConnectedEventListener(BtConnection._NConnListener);

					if (BtConnection._bt.IsConnected()) {
						connected = true;
						item.setIcon(R.drawable.ic_action_bluetooth_connected);
						item.setTitle(R.string.action_bluetooth_disconnect);

						BtConnection._bt.start();
						
						Toast.makeText(this, "Connected to HxM " + DeviceName, Toast.LENGTH_LONG).show();

						// TODO Reset all the values to 0s
					} else {
						item.setIcon(R.drawable.ic_action_bluetooth);
						item.setTitle(R.string.action_bluetooth_connect);
						
						Toast.makeText(this, "Unable to Connect!", Toast.LENGTH_LONG).show();
					}

					break;
				}
			}
		}
	}

	void onClickMenuBluetoothDisconnect(MenuItem item) {
		connected = false;
		item.setIcon(R.drawable.ic_action_bluetooth);
		item.setTitle(R.string.action_bluetooth_connect);

		Toast.makeText(this, "Disconnected from HxM!", Toast.LENGTH_LONG).show();

		/*
		 * This disconnects listener from acting on received messages
		 */
		BtConnection._bt.removeConnectedEventListener(BtConnection._NConnListener);
		/*
		 * Close the communication with the device & throw an exception if
		 * failure
		 */
		BtConnection._bt.Close();
	}

	final Handler Newhandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case BtConnection.HEART_RATE:
				String HeartRatetext = msg.getData().getString("HeartRate");
				HomeFragment.heartRateTextView.setText(HeartRatetext);
				break;

			case BtConnection.INSTANT_SPEED:
				String InstantSpeedtext = msg.getData().getString("InstantSpeed");
				HomeFragment.instantSpeedTextView.setText(InstantSpeedtext);
				break;

			case BtConnection.RR_INTERVAL:
				String RRInterval = msg.getData().getString("RRInterval");
				HomeFragment.rRIntervalTextView.setText(RRInterval);
				break;

			case BtConnection.INSTANT_HR:
				String InstantHR = msg.getData().getString("InstantHR");
				HomeFragment.instantHeartRateTextView.setText(InstantHR);
				break;
			}
		}

	};

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int option, long id) {
		navigationDrawerHelper.handleSelect(this, view, option);
	}

}
