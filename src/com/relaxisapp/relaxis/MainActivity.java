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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
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
        boolean handled;

        int id = item.getItemId();
        switch (id) {
            case R.id.action_bluetooth:
            	onClickMenuBluetooth(item);
                handled = true;
                break;
            default:
                handled = super.onOptionsItemSelected(item);
        }
        return handled;
    }
	
	public void  onClickMenuBluetooth(MenuItem item) {
        // TODO Code for bluetooth connection goes here
		// TODO Zdravko, delete the following code, it's just for testing purposes
		Toast toast = Toast.makeText(this, "Woohoo!", Toast.LENGTH_LONG);
		toast.show();
    }

	public void clickMe(View view) {

		final TextView tvTest = (TextView) findViewById(R.id.tv_heartRate);

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

		Button btnConnect = (Button) findViewById(R.id.ButtonConnect);
		if (btnConnect != null) {
			btnConnect.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {

					// Getting the Bluetooth adapter
					BluetoothAdapter adapter = BluetoothAdapter
							.getDefaultAdapter();
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

					// Starting the device discovery
					tvTest.append("\nStarting discovery...");
					adapter.startDiscovery();
					tvTest.append("\nDone with discovery...");

					// Listing paired devices
					tvTest.append("\nDevices Pared:");
					Set<BluetoothDevice> devices = adapter.getBondedDevices();
					for (BluetoothDevice device : devices) {
						tvTest.append("\nFound device: " + device);
					}

					// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
					// TEST CODE HERE

					String BhMacID = "00:07:80:5B:02:95";
					adapter = BluetoothAdapter.getDefaultAdapter();

					Set<BluetoothDevice> pairedDevices = adapter
							.getBondedDevices();
					if (pairedDevices.size() > 0) {
						for (BluetoothDevice device : pairedDevices) {
							if (device.getName().startsWith("HXM")) {
								BluetoothDevice btDevice = device;
								BhMacID = btDevice.getAddress();
								break;
							}
						}
					}

					// BhMacID = btDevice.getAddress();

					BluetoothDevice Device = adapter.getRemoteDevice(BhMacID);
					String DeviceName = Device.getName();
					_bt = new BTClient(adapter, BhMacID);
					_NConnListener = new NewConnectedListener(Newhandler,
							Newhandler);
					_bt.addConnectedEventListener(_NConnListener);

					TextView tv1 = (EditText) findViewById(R.id.labelHeartRate);
					tv1.setText("000");

					tv1 = (EditText) findViewById(R.id.labelInstantSpeed);
					tv1.setText("0.0");
					// tv1 = (EditText)findViewById(R.id.labelSkinTemp);
					// tv1.setText("0.0");

					// tv1 = (EditText)findViewById(R.id.labelPosture);
					// tv1.setText("000");
					// tv1 = (EditText)findViewById(R.id.labelPeakAcc);
					// tv1.setText("0.0");
					if (_bt.IsConnected()) {
						_bt.start();
						TextView tv = (TextView) findViewById(R.id.labelStatusMsg);
						String ErrorText = "Connected to HxM " + DeviceName;
						tv.setText(ErrorText);

						// Reset all the values to 0s

					} else {
						TextView tv = (TextView) findViewById(R.id.labelStatusMsg);
						String ErrorText = "Unable to Connect !";
						tv.setText(ErrorText);
					}
				}
			});
		}
		/* Obtaining the handle to act on the DISCONNECT button */
		Button btnDisconnect = (Button) findViewById(R.id.ButtonDisconnect);
		if (btnDisconnect != null) {
			btnDisconnect.setOnClickListener(new OnClickListener() {
				@Override
				/* Functionality to act if the button DISCONNECT is touched */
				public void onClick(View v) {
					// TODO Auto-generated method stub
					/* Reset the global variables */
					TextView tv = (TextView) findViewById(R.id.labelStatusMsg);
					String ErrorText = "Disconnected from HxM!";
					tv.setText(ErrorText);

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
			});
		}

		// END TEST CODE

		tvTest.setText("clicked");
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
				String InstantSpeedtext = msg.getData().getString(
						"InstantSpeed");
				tv = (EditText) findViewById(R.id.labelInstantSpeed);
				if (tv != null)
					tv.setText(InstantSpeedtext);

				break;

			}
		}

	};

}
