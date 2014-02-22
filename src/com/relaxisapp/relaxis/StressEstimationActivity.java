package com.relaxisapp.relaxis;

import java.io.FileOutputStream;
import java.io.IOException;

import zephyr.android.HxMBT.BTClient;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

public class StressEstimationActivity extends Activity {
	
	//TODO make .config file to setup such file paths
	private String FILENAME = "relaxisDataSave";
	private final TextView stressLevelTextView = (TextView) findViewById(R.id.stressLevelTextView);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_stress_estimation);
		
		BtConnection._bt.Close();

		BtConnection._bt = new BTClient(BtConnection.adapter,
				BtConnection.BhMacID);

		BtConnection.stressLevelListener = new NewConnectedListener(Newhandler,
				Newhandler);
		BtConnection._bt
				.addConnectedEventListener(BtConnection.instantHRListener);

		BtConnection._bt.start();
		
		
	}

	private void saveData(double stressLevel) throws IOException {

		FileOutputStream fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);
		try {
			fos.write(String.valueOf(stressLevel).getBytes());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		fos.close();
	}
	
//	private double getData() {
//		double stressLevel;
//		
//		FileOutputStream fos = openFileOutput(FILENAME, Context.MODE);
//		try {
//			fos.write(String.valueOf(stressLevel).getBytes());
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		fos.close();
//		
//		return stressLevel; 
//	}
	
	final Handler Newhandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			if (BtConnection.nnCount > 200) {
				switch (msg.what) {
				case BtConnection.PNN50:
					String pNN50 = msg.getData().getString("pNN50");
					
					stressLevelTextView.append("Stress level: " + pNN50 + "\n");
					
					try {
						saveData(Double.parseDouble(pNN50));
					} catch (NumberFormatException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					break;
				}
			}
		}

	};

}
