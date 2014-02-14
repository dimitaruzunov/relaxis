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
import android.widget.Button;
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
	private TextView heartRateTextView;
	private TextView instantSpeedTextView;
	private TextView rRIntervalTextView;
	private TextView instantHeartRateTextView;
	private Button app1Button;
	private Button app2Button;
	private Button app3Button;
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

		tvTest.setText("App started.");
	}
	
	void setupUiEvents() {
		// Testing purposes
		tvTest = (TextView) findViewById(R.id.testTextView);
		tv = (TextView) findViewById(R.id.labelStatusMsg);
		
		heartRateTextView = (TextView) findViewById(R.id.heartRateTextView);
		instantSpeedTextView = (TextView) findViewById(R.id.instantSpeedTextView);
		rRIntervalTextView = (TextView) findViewById(R.id.rRIntervalTextView);
		instantHeartRateTextView = (TextView) findViewById(R.id.instantHeartRateTextView);
		
		app1Button = (Button) findViewById(R.id.app1Button);
		app1Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleApp1ButtonClick((Button) view);
            }
        });
		
		app2Button = (Button) findViewById(R.id.app2Button);
		app2Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleApp2ButtonClick((Button) view);
            }
        });
		
		app3Button = (Button) findViewById(R.id.app3Button);
		app3Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleApp3ButtonClick((Button) view);
            }
        });
	}
	
	void handleApp1ButtonClick(Button button) {
		Intent intent = new Intent(this, App1Activity.class);
        startActivity(intent);
	}
	
	void handleApp2ButtonClick(Button button) {
		// Intent intent = new Intent(this, App2Activity.class);
        // startActivity(intent);
	}
	
	void handleApp3ButtonClick(Button button) {
		// Intent intent = new Intent(this, App3Activity.class);
        // startActivity(intent);
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
		item.setIcon(R.drawable.ic_action_bluetooth);
		item.setTitle(R.string.action_bluetooth_connect);
		
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
			switch (msg.what) {
			case HEART_RATE:
				String HeartRatetext = msg.getData().getString("HeartRate");
				heartRateTextView.setText(HeartRatetext);
				break;

			case INSTANT_SPEED:
				String InstantSpeedtext = msg.getData().getString("InstantSpeed");
				instantSpeedTextView.setText(InstantSpeedtext);
				break;

			case RR_INTERVAL:
				String RRInterval = msg.getData().getString("RRInterval");
				rRIntervalTextView.setText(RRInterval);
				break;

			case INSTANT_HR:
				String InstantHR = msg.getData().getString("InstantHR");
				instantHeartRateTextView.setText(InstantHR);
				break;
			}
		}

	};

}
