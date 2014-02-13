package com.relaxisapp.relaxis;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;
import zephyr.android.HxMBT.BTClient;
import zephyr.android.HxMBT.ZephyrProtocol;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	BluetoothAdapter adapter = null;
	BTClient _bt;
	ZephyrProtocol _protocol;
	NewConnectedListener _NConnListener;
	
	private final int HEART_RATE = 0x100;
	private final int INSTANT_SPEED = 0x101;
	private final int RR_INTERVAL = 0x102;
	private final int INSTANT_HR = 0x103;

	private TextView tvTest;
	private TextView tv;
	private boolean connected = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		setupUiEvents();
		
		// TODO Try to put the following code out of the onCreate method
		
		/*
		 * Sending a message to android that we are going to initiate a pairing
		 * request
		 */
		IntentFilter filter = new IntentFilter(
				"android.bluetooth.device.action.PAIRING_REQUEST");
		/*
		 * Registering a new BTBroadcast receiver from the Main Activity context
		 * with pairing request event
		 */
		this.getApplicationContext().registerReceiver(
				new BTBroadcastReceiver(), filter);
		// Registering the BTBondReceiver in the application that the
		// status of the receiver has changed to Paired
		IntentFilter filter2 = new IntentFilter(
				"android.bluetooth.device.action.BOND_STATE_CHANGED");
		this.getApplicationContext().registerReceiver(new BTBondReceiver(),
				filter2);

		tvTest.setText("App started.");
	}
	
	void setupUiEvents() {
		tvTest = (TextView) findViewById(R.id.tv_heartRate);
		tv = (TextView) findViewById(R.id.labelStatusMsg);
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
            	}
            	else {
            		onClickMenuBluetoothDisconnect(item);
            	}
                break;
            default:
                handled = super.onOptionsItemSelected(item);
        }
        return handled;
    }
	
	void onClickMenuBluetoothConnect(MenuItem item) {
		item.setIcon(R.drawable.ic_action_bluetooth_searching);
		item.setTitle(R.string.action_bluetooth_connecting);
		
		// Getting the Bluetooth adapter
		BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
		tvTest.append("\nAdapter: " + adapter);

		// Check for Bluetooth support in the first place
		// Emulator doesn't support Bluetooth and will return null
		if (adapter == null) {
			tvTest.append("\nBluetooth NOT supported. Aborting.");
			return;
		}

		// TODO ask user for explicit permission
		// Enable bluetooth
		if (!adapter.isEnabled()) {
			adapter.enable();
		}

		// TODO try to write this better
		while (!adapter.isEnabled()) { //wait until the bluetooth is on
		}

		Set<BluetoothDevice> pairedDevices = adapter.getBondedDevices();
		if (pairedDevices.size() > 0) {
			for (BluetoothDevice device : pairedDevices) {
				if (device.getName().startsWith("HXM")) {
					BluetoothDevice btDevice = device;
					String BhMacID = btDevice.getAddress();

					BluetoothDevice Device = adapter.getRemoteDevice(BhMacID);
					String DeviceName = Device.getName();
					_bt = new BTClient(adapter, BhMacID);
					_NConnListener = new NewConnectedListener(Newhandler, Newhandler);
					_bt.addConnectedEventListener(_NConnListener);

					if (_bt.IsConnected()) {
						connected = true;
						item.setIcon(R.drawable.ic_action_bluetooth_connected);
						item.setTitle(R.string.action_bluetooth_disconnect);
						
						_bt.start();
						tv.setText("Connected to HxM " + DeviceName);

						// TODO Reset all the values to 0s
					} else {
						item.setIcon(R.drawable.ic_action_bluetooth);
						item.setTitle(R.string.action_bluetooth_connect);
						
						tv.setText("Unable to Connect!");
					}

					break;
				}
			}
		}

		Toast toast = Toast.makeText(this, "Woohoo!", Toast.LENGTH_LONG);
		toast.show();
	}
	
	void onClickMenuBluetoothDisconnect(MenuItem item) {
		connected = false;
		tv.setText("Disconnected from HxM!");
		
		/*
		 * This disconnects listener from acting on received
		 * messages
		 */
		_bt.removeConnectedEventListener(_NConnListener);
		/*
		 * Close the communication with the device & throw an
		 * exception if failure
		 */
		_bt.Close();
	}

	private class BTBondReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			Bundle b = intent.getExtras();
			BluetoothDevice device = adapter.getRemoteDevice(b.get(
					"android.bluetooth.device.extra.DEVICE").toString());
			Log.d("Bond state", "BOND_STATED = " + device.getBondState());
		}
	}

	private class BTBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d("BTIntent", intent.getAction());
			Bundle b = intent.getExtras();
			Log.d("BTIntent", b.get("android.bluetooth.device.extra.DEVICE")
					.toString());
			Log.d("BTIntent",
					b.get("android.bluetooth.device.extra.PAIRING_VARIANT")
							.toString());
			try {
				BluetoothDevice device = adapter.getRemoteDevice(b.get(
						"android.bluetooth.device.extra.DEVICE").toString());
				Method m = BluetoothDevice.class.getMethod("convertPinToBytes",
						new Class[] { String.class });
				byte[] pin = (byte[]) m.invoke(device, "1234");
				m = device.getClass().getMethod("setPin",
						new Class[] { pin.getClass() });
				Object result = m.invoke(device, pin);
				Log.d("BTTest", result.toString());
			} catch (SecurityException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (NoSuchMethodException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	final Handler Newhandler = new Handler() {
		
		@Override
		public void handleMessage(Message msg) {
			TextView tv;
			
			switch (msg.what) {
			case HEART_RATE:
				String HeartRatetext = msg.getData().getString("HeartRate");
				tv = (EditText) findViewById(R.id.labelHeartRate);
				System.out.println("Heart Rate Info is " + HeartRatetext);
				if (tv != null)
					tv.setText(HeartRatetext);
				break;

			case INSTANT_SPEED:
				String InstantSpeedtext = msg.getData().getString("InstantSpeed");
				tv = (EditText) findViewById(R.id.labelInstantSpeed);
				if (tv != null)
					tv.setText(InstantSpeedtext);
				break;

			case RR_INTERVAL:
				String RRInterval = msg.getData().getString("RRInterval");
				tv = (EditText) findViewById(R.id.labelRRInterval);
				if (tv != null)
					tv.setText(RRInterval);
				break;

			case INSTANT_HR:
				String InstantHR = msg.getData().getString("InstantHR");
				tv = (EditText) findViewById(R.id.labelInstantHR);
				if (tv != null)
					tv.setText(InstantHR);
				break;
			}
		}

	};

}
