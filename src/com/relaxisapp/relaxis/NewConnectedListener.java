package com.relaxisapp.relaxis;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import zephyr.android.HxMBT.*;

public class NewConnectedListener extends ConnectListenerImpl
{
	private Handler _OldHandler;
	private Handler _aNewHandler; 
	private int GP_MSG_ID = 0x20;
	private int GP_HANDLER_ID = 0x20;
	private int HR_SPD_DIST_PACKET =0x26;
	
	private HRSpeedDistPacketInfo HRSpeedDistPacket = new HRSpeedDistPacketInfo();
	public NewConnectedListener(Handler handler, Handler _NewHandler) {
		super(handler, null);
		_OldHandler= handler;
		_aNewHandler = _NewHandler;

		// TODO Auto-generated constructor stub

	}
	public void Connected(ConnectedEvent<BTClient> eventArgs) {
		System.out.println(String.format("Connected to BioHarness %s.", eventArgs.getSource().getDevice().getName()));

	
				
		
		//Creates a new ZephyrProtocol object and passes it the BTComms object
		ZephyrProtocol _protocol = new ZephyrProtocol(eventArgs.getSource().getComms());
		//ZephyrProtocol _protocol = new ZephyrProtocol(eventArgs.getSource().getComms(), );
		_protocol.addZephyrPacketEventListener(new ZephyrPacketListener() {
			public void ReceivedPacket(ZephyrPacketEvent eventArgs) {
				ZephyrPacketArgs msg = eventArgs.getPacket();
				byte CRCFailStatus;
				byte RcvdBytes;
				
								
				CRCFailStatus = msg.getCRCStatus();
				RcvdBytes = msg.getNumRvcdBytes() ;
				if (HR_SPD_DIST_PACKET==msg.getMsgID())
				{
					
					byte [] DataArray = msg.getBytes();
					
					int mostRecentTs = CustomUtilities.TwoBytesToUnsignedInt(DataArray[11], DataArray[12]);
					int secondRecentTs = CustomUtilities.TwoBytesToUnsignedInt(DataArray[13], DataArray[14]);
					
					//TODO fix the roll over after 65535
					int rrInterval = mostRecentTs - secondRecentTs;
					
					int instantHR = 60000 / rrInterval;
					
					System.out.println(CustomUtilities.ByteToUnsignedInt(DataArray[9])); //HR
					System.out.println(CustomUtilities.ByteToUnsignedInt(DataArray[10])); //Heart beat n
					System.out.println("*"+mostRecentTs);//most recent TS
					System.out.println("*"+secondRecentTs);
					
					System.out.println("!"+(rrInterval));
					
					//***************Displaying the Heart Rate********************************
					int HRate =  HRSpeedDistPacket.GetHeartRate(DataArray);
					Message text1 = _aNewHandler.obtainMessage(BtConnection.HEART_RATE);
					Bundle b1 = new Bundle();
					b1.putString("HeartRate", String.valueOf(HRate));
					text1.setData(b1);
					_aNewHandler.sendMessage(text1);
					System.out.println("Heart Rate is "+ HRate);

					//***************Displaying the Instant Speed********************************
					double InstantSpeed = HRSpeedDistPacket.GetInstantSpeed(DataArray);
					
					text1 = _aNewHandler.obtainMessage(BtConnection.INSTANT_SPEED);
					b1.putString("InstantSpeed", String.valueOf(InstantSpeed));
					text1.setData(b1);
					_aNewHandler.sendMessage(text1);
					System.out.println("Instant Speed is "+ InstantSpeed);
					
					//*********** Add R-R interval to the message ****************
					text1 = _aNewHandler.obtainMessage(BtConnection.RR_INTERVAL);
					b1.putString("RRInterval", String.valueOf(rrInterval));
					text1.setData(b1);
					_aNewHandler.sendMessage(text1);
					System.out.println("R-R interval is "+ rrInterval);
					
					//*********** Add Instant heart rate to the message ****************
					text1 = _aNewHandler.obtainMessage(BtConnection.INSTANT_HR);
					b1.putString("InstantHR", String.valueOf(instantHR));
					text1.setData(b1);
					_aNewHandler.sendMessage(text1);
					System.out.println("Instant HR is "+ instantHR);
				}
			}
		});
	}
	
}