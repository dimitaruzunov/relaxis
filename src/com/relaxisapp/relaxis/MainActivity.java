package com.relaxisapp.relaxis;

import java.util.ArrayList;
import java.util.Set;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import zephyr.android.HxMBT.BTClient;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView.GraphViewData;

public class MainActivity extends FragmentActivity implements ListView.OnItemClickListener {

	private NavigationDrawerListAdapter navigationDrawerListAdapter;
	private ListView drawerListView;
	private SectionsPagerAdapter sectionsPagerAdapter;
	static ViewPager viewPager;
	private OnBtConnectionChangeListener btConnectionChangeListener;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		// Navigation drawer setup
		String[] navigationMenuTitles = getResources().getStringArray(
				R.array.navigation_drawer_options);
		TypedArray navigationMenuIcons = getResources().obtainTypedArray(
				R.array.navigation_drawer_icons);

		ArrayList<NavigationDrawerItem> navigationDrawerItems = new ArrayList<NavigationDrawerItem>();

		for (int i = 0, len = navigationMenuTitles.length; i < len; i++) {
			navigationDrawerItems.add(new NavigationDrawerItem(
					navigationMenuTitles[i], navigationMenuIcons.getResourceId(
							i, -1)));
		}

		// Recycle the typed array
		navigationMenuIcons.recycle();

		navigationDrawerListAdapter = new NavigationDrawerListAdapter(
				getApplicationContext(), navigationDrawerItems);

		drawerListView = (ListView) findViewById(R.id.left_drawer);
		drawerListView.setAdapter(navigationDrawerListAdapter);

		navigationDrawerListAdapter.setup(this, this);
		navigationDrawerListAdapter
				.setSelection(NavigationDrawerListAdapter.HOME_OPTION_ITEM);

		// Sections pager setup
		sectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager(), this);

		viewPager = (ViewPager) findViewById(R.id.pager);
		viewPager.setAdapter(sectionsPagerAdapter);
		viewPager.setCurrentItem(SectionsPagerAdapter.HOME_FRAGMENT);
		viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				navigationDrawerListAdapter.handleSelect(position);
			}

			@Override
			public void onPageScrolled(int position, float positionOffset,
					int positionOffsetPixels) {
			}

			@Override
			public void onPageScrollStateChanged(int state) {
			}
		});

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
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

		navigationDrawerListAdapter.syncState();
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		navigationDrawerListAdapter.handleOnPrepareOptionsMenu(menu);

		return super.onPrepareOptionsMenu(menu);
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

		navigationDrawerListAdapter.handleOnOptionsItemSelected(item);

		return handled;
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		navigationDrawerListAdapter.syncState();
		super.onConfigurationChanged(newConfig);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent resultIntent) {
		super.onActivityResult(requestCode, resultCode, resultIntent);
		switch (requestCode) {
		case Const.REQUEST_ENABLE_BT:
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

	void executeConnect(Button button) {
		btConnectionChangeListener = (OnBtConnectionChangeListener) sectionsPagerAdapter.getFragment(SectionsPagerAdapter.HOME_FRAGMENT);
		btConnectionChangeListener.onBtConnectionChange(1, button);

		new BluetoothConnectTask().execute(button);
	}

	private class AsyncTaskResults {
		public int result;
		public Button item;
	}

	private class BluetoothConnectTask extends
			AsyncTask<Button, Void, AsyncTaskResults> {

		private final int CODE_CANCELLED = 3;
		private final int CODE_NO_BT = 2;
		private final int CODE_FAILURE = 1;
		private final int CODE_SUCCESS = 0;

		@Override
		protected AsyncTaskResults doInBackground(Button... buttons) {
			// Setting the results to be returned
			AsyncTaskResults results = new AsyncTaskResults();
			results.item = buttons[0];

			// do the work unless user cancel
			while (!isCancelled()) {
				// Setting up an event listener listening for cancel intent
				// results.item.setOnClickListener(new View.OnClickListener() {
				// @Override
				// public void onClick(View view) {
				// cancel(true);
				// }
				// });

				// Getting the Bluetooth adapter
				BtConnection.adapter = BluetoothAdapter.getDefaultAdapter();

				// Check for Bluetooth support
				if (BtConnection.adapter == null) {
					results.result = CODE_NO_BT;
					return results;
				}

				// Enable bluetooth if not enabled
				if (!BtConnection.adapter.isEnabled()) {
					Intent enableBtIntent = new Intent(
							BluetoothAdapter.ACTION_REQUEST_ENABLE);
					startActivityForResult(enableBtIntent,
							Const.REQUEST_ENABLE_BT);
				}

				// TODO try to write this better
				while (!BtConnection.adapter.isEnabled()) {
					// wait until the bluetooth is on
				}

				Set<BluetoothDevice> pairedDevices = BtConnection.adapter
						.getBondedDevices();
				if (pairedDevices.size() > 0) {
					for (BluetoothDevice device : pairedDevices) {
						if (device.getName().startsWith("HXM")) {
							BluetoothDevice btDevice = device;
							BtConnection.BhMacID = btDevice.getAddress();

							BluetoothDevice Device = BtConnection.adapter
									.getRemoteDevice(BtConnection.BhMacID);
							BtConnection.deviceName = Device.getName();

							BtConnection._bt = new BTClient(
									BtConnection.adapter, BtConnection.BhMacID);

							BtConnection._NConnListener = new NewConnectedListener(
									SensorDataHandler, SensorDataHandler);
							BtConnection._bt
									.addConnectedEventListener(BtConnection._NConnListener);

							if (BtConnection._bt.IsConnected()) {
								BtConnection._bt.start();

								// TODO ? Reset all the values to 0s

								results.result = CODE_SUCCESS;
								return results;
							} else {
								results.result = CODE_FAILURE;
								return results;
							}
						}
					}
				}
			}

			results.result = CODE_CANCELLED;
			return results;
		}

		@Override
		protected void onPostExecute(AsyncTaskResults results) {
			switch (results.result) {
			case CODE_NO_BT:
				btConnectionChangeListener
						.onBtConnectionChange(0, results.item);
				Toast.makeText(MainActivity.this, "Bluetooth is not supported",
						Toast.LENGTH_LONG).show();
				break;
			case CODE_FAILURE:
				btConnectionChangeListener
						.onBtConnectionChange(0, results.item);
				Toast.makeText(MainActivity.this, "Unable to connect",
						Toast.LENGTH_LONG).show();
				break;
			case CODE_SUCCESS:
				btConnectionChangeListener
						.onBtConnectionChange(2, results.item);
				Toast.makeText(MainActivity.this,
						"Connected to HxM " + BtConnection.deviceName,
						Toast.LENGTH_LONG).show();

				// reset the stress score
				BtConnection.recentNn50 = new int[Const.SAVED_NN50_COUNT];
				for (int i = 0; i < BtConnection.recentNn50.length; i++) {
					BtConnection.recentNn50[i] = 0;

				}

				// reset the instant HR arr
				BtConnection.recentInstantHR = new int[Const.SAVED_HR_COUNT];
				for (int i = 0; i < BtConnection.recentInstantHR.length; i++) {
					BtConnection.recentInstantHR[i] = 0;

				}

				// TODO check if the timer is cleared when the back button is
				// pressed
				// and then the activity is started again
				BreathingFragment.graphUpdateTimerTask = new BreathingFragment.GraphUpdateTimerTask();
				BreathingFragment.graphUpdateTimer.scheduleAtFixedRate(
						BreathingFragment.graphUpdateTimerTask, 1000,
						1000 / Const.TIMER_TICKS_PER_SECOND);

				break;
			}
		}

		@Override
		protected void onCancelled(AsyncTaskResults results) {
			btConnectionChangeListener.onBtConnectionChange(0, results.item);
			Toast.makeText(MainActivity.this, "Connecting cancelled",
					Toast.LENGTH_LONG).show();
		}

	}

	void executeDisconnect(Button button) {
		btConnectionChangeListener.onBtConnectionChange(0, button);

		Toast.makeText(this, "Disconnected from HxM", Toast.LENGTH_LONG).show();

		// This disconnects listener from acting on received messages
		BtConnection._bt
				.removeConnectedEventListener(BtConnection._NConnListener);
		// Close the communication with the device & throw an exception if
		// failure
		BtConnection._bt.Close();

		BreathingFragment.graphUpdateTimerTask.cancel();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int option,
			long id) {
		sectionsPagerAdapter.setFragment(option, viewPager);
		navigationDrawerListAdapter.closeDrawer();
	}

	final static Handler SensorDataHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case Const.HEART_RATE:
				String HeartRatetext = msg.getData().getString("HeartRate");
				HomeFragment.heartRateTextView.setText(HeartRatetext);
				break;

			case Const.INSTANT_SPEED:
				String InstantSpeedtext = msg.getData().getString(
						"InstantSpeed");
				HomeFragment.instantSpeedTextView.setText(InstantSpeedtext);
				break;

			case Const.RR_INTERVAL:
				String RRInterval = msg.getData().getString("RRInterval");
				HomeFragment.rRIntervalTextView.setText(RRInterval);
				break;

			case Const.INSTANT_HR:
				String instantHRString = msg.getData().getString("InstantHR");
				int instantHR = Integer.parseInt(instantHRString);

				// update HomeFragment
				HomeFragment.instantHeartRateTextView.setText(instantHRString);

				// update BreathingFragment
				BtConnection.instantHRSeries.appendData(new GraphViewData(
						BreathingFragment.beatsCount, instantHR), false,
						Const.VIEWPORT_WIDTH + 1);
				BreathingFragment.beatsCount++;

				BtConnection.recentInstantHR[BreathingFragment.beatsCount
						% Const.SAVED_HR_COUNT] = instantHR;

				int tAvgMaxHR = 0,
				tAvgMinHR = 0,
				tAvgMaxCount = 0,
				tAvgMinCount = 0;
				int iHR,
				iHRPlus1,
				iHRPlus2,
				iHRMinus1,
				iHRMinus2;

				for (int i = 0; i < BtConnection.recentInstantHR.length; i++) {
					iHR = BtConnection.recentInstantHR[i];
					iHRPlus1 = BtConnection.recentInstantHR[(i + 1)
							% Const.SAVED_HR_COUNT];
					iHRPlus2 = BtConnection.recentInstantHR[(i + 2)
							% Const.SAVED_HR_COUNT];
					iHRMinus1 = BtConnection.recentInstantHR[(Const.SAVED_HR_COUNT
							+ i - 1)
							% Const.SAVED_HR_COUNT];
					iHRMinus2 = BtConnection.recentInstantHR[(Const.SAVED_HR_COUNT
							+ i - 2)
							% Const.SAVED_HR_COUNT];
					if (i < ((BreathingFragment.beatsCount - 1)
							% Const.SAVED_HR_COUNT + Const.SAVED_HR_COUNT - 2)
							% Const.SAVED_HR_COUNT
							|| i > ((BreathingFragment.beatsCount - 1)
									% Const.SAVED_HR_COUNT + 2)
									% Const.SAVED_HR_COUNT) {
						if (iHRPlus1 > 0 && iHRMinus1 > 0 && iHRPlus2 > 0
								&& iHRMinus2 > 0 && iHR >= iHRPlus1
								&& iHR >= iHRPlus2 && iHR >= iHRMinus1
								&& iHR >= iHRMinus2) {
							tAvgMaxHR += iHR;
							tAvgMaxCount++;
						}
						if (iHRPlus1 > 0 && iHRMinus1 > 0 && iHRPlus2 > 0
								&& iHRMinus2 > 0 && iHR <= iHRPlus1
								&& iHR <= iHRPlus2 && iHR <= iHRMinus1
								&& iHR <= iHRMinus2) {
							tAvgMinHR += iHR;
							tAvgMinCount++;
						}
					}
				}
				if (tAvgMaxCount > 0) { // => tAvgMaxHR > 0
					tAvgMaxHR /= tAvgMaxCount;
				}
				if (tAvgMinCount > 0) { // => tAvgMinHR > 0
					tAvgMinHR /= tAvgMinCount;
				}

				if (tAvgMaxCount > 0 && tAvgMinCount > 0) {
					if ((tAvgMaxHR - tAvgMinHR) / 2 > Const.IDEAL_HR_DEVIATION) {
						BreathingFragment.newMaxHR = tAvgMaxHR;
						BreathingFragment.newMinHR = tAvgMinHR;
					} else {
						if (tAvgMaxHR > BreathingFragment.newMaxHR) {
							BreathingFragment.newMaxHR = tAvgMaxHR;
						}
						if (tAvgMinHR < BreathingFragment.newMinHR) {
							BreathingFragment.newMinHR = tAvgMinHR;
						}
					}
				}

				if (BreathingFragment.updateScore) {
					if (Math.abs(BreathingFragment.tIdealHR - instantHR) <= Const.POINT_BARRIER) {
						BreathingFragment.consecutivePoints++;
						if (BreathingFragment.consecutivePoints >= 5 * BreathingFragment.multiplier) {
							BreathingFragment.multiplier++;
						}
						BreathingFragment.score += BreathingFragment.multiplier;
					} else {
						BreathingFragment.consecutivePoints = 0;
						BreathingFragment.multiplier = 1;
					}

					BreathingFragment.scoreTextView.setText(String
							.valueOf(BreathingFragment.score));
				}

				break;
			case Const.PNN50:
				if (StressEstimationFragment.timeLeft > 0) {
					String pNN50 = msg.getData().getString("pNN50");

					if (pNN50 != null) {
						StressEstimationFragment.stressLevel = Double.parseDouble(pNN50);
					}
				}
				break;
			}
		}

	};
	
	public void getUser(View view) {
		new HttpRequestTask().execute();
	}

	private class HttpRequestTask extends AsyncTask<Void, Void, User> {
		@Override
		protected User doInBackground(Void... params) {
			try {
				final String url = "http://relaxisapp.com.91-215-216-74.hera.icnhost.net/api/users/1";
				RestTemplate restTemplate = new RestTemplate();
				restTemplate.getMessageConverters().add(
						new MappingJackson2HttpMessageConverter());
				User user = restTemplate.getForObject(url, User.class);
				return user;
			} catch (Exception e) {
				Log.e("MainActivity", e.getMessage(), e);
			}

			return null;
		}

		@Override
		protected void onPostExecute(User user) {
			System.out.println("spring " + user.getUserId() + " "
					+ user.getFbUserId());
		}

	}
}
