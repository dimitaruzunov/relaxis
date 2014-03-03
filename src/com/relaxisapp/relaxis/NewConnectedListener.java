package com.relaxisapp.relaxis;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import zephyr.android.HxMBT.*;

public class NewConnectedListener extends ConnectListenerImpl {
	private Handler _OldHandler;
	private Handler _aNewHandler;
	private int GP_MSG_ID = 0x20;
	private int GP_HANDLER_ID = 0x20;
	private int HR_SPD_DIST_PACKET = 0x26;

	private HRSpeedDistPacketInfo HRSpeedDistPacket = new HRSpeedDistPacketInfo();

	public NewConnectedListener(Handler handler, Handler _NewHandler) {
		super(handler, null);
		_OldHandler = handler;
		_aNewHandler = _NewHandler;

		// TODO Auto-generated constructor stub

	}

	public void Connected(ConnectedEvent<BTClient> eventArgs) {
		System.out.println(String.format("Connected to BioHarness %s.",
				eventArgs.getSource().getDevice().getName()));

		// Creates a new ZephyrProtocol object and passes it the BTComms object
		ZephyrProtocol _protocol = new ZephyrProtocol(eventArgs.getSource()
				.getComms());
		// ZephyrProtocol _protocol = new
		// ZephyrProtocol(eventArgs.getSource().getComms(), );
		_protocol.addZephyrPacketEventListener(new ZephyrPacketListener() {
			public void ReceivedPacket(ZephyrPacketEvent eventArgs) {
				ZephyrPacketArgs msg = eventArgs.getPacket();
				byte CRCFailStatus;
				byte RcvdBytes;

				CRCFailStatus = msg.getCRCStatus();
				RcvdBytes = msg.getNumRvcdBytes();
				if (HR_SPD_DIST_PACKET == msg.getMsgID()) {

					byte[] DataArray = msg.getBytes();

					// for (int i = 0; i < DataArray.length; i++) {
					// System.out.println(i + " - " + DataArray[i]);
					// }

					int[] recentTimestamps = {
							CustomUtilities.TwoBytesToUnsignedInt(
									DataArray[11], DataArray[12]),
							CustomUtilities.TwoBytesToUnsignedInt(
									DataArray[13], DataArray[14]),
							CustomUtilities.TwoBytesToUnsignedInt(
									DataArray[15], DataArray[16]),
							CustomUtilities.TwoBytesToUnsignedInt(
									DataArray[17], DataArray[18]),
							CustomUtilities.TwoBytesToUnsignedInt(
									DataArray[19], DataArray[20]),
							CustomUtilities.TwoBytesToUnsignedInt(
									DataArray[21], DataArray[22]),
							CustomUtilities.TwoBytesToUnsignedInt(
									DataArray[23], DataArray[24]),
							CustomUtilities.TwoBytesToUnsignedInt(
									DataArray[25], DataArray[26]),
							CustomUtilities.TwoBytesToUnsignedInt(
									DataArray[27], DataArray[28]),
							CustomUtilities.TwoBytesToUnsignedInt(
									DataArray[29], DataArray[30]),
							CustomUtilities.TwoBytesToUnsignedInt(
									DataArray[31], DataArray[32]),
							CustomUtilities.TwoBytesToUnsignedInt(
									DataArray[33], DataArray[34]),
							CustomUtilities.TwoBytesToUnsignedInt(
									DataArray[35], DataArray[36]),
							CustomUtilities.TwoBytesToUnsignedInt(
									DataArray[37], DataArray[38]),
							CustomUtilities.TwoBytesToUnsignedInt(
									DataArray[39], DataArray[40]) };

					// fix the roll over after 65535 ??
					for (int i = 0; i < 14; i++) {
						if (recentTimestamps[i] < recentTimestamps[i + 1]) {
							for (int j = i; j >= 0; j--) {
								recentTimestamps[j] += 65535;
							}
						}
					}

					int[] rrIntervals = {
							recentTimestamps[0] - recentTimestamps[1],
							recentTimestamps[1] - recentTimestamps[2],
							recentTimestamps[2] - recentTimestamps[3],
							recentTimestamps[3] - recentTimestamps[4],
							recentTimestamps[4] - recentTimestamps[5],
							recentTimestamps[5] - recentTimestamps[6],
							recentTimestamps[6] - recentTimestamps[7],
							recentTimestamps[7] - recentTimestamps[8],
							recentTimestamps[8] - recentTimestamps[9],
							recentTimestamps[9] - recentTimestamps[10],
							recentTimestamps[10] - recentTimestamps[11],
							recentTimestamps[11] - recentTimestamps[12],
							recentTimestamps[12] - recentTimestamps[13],
							recentTimestamps[13] - recentTimestamps[14] };

					int[] rrDiffs = { rrIntervals[0] - rrIntervals[1],
							rrIntervals[1] - rrIntervals[2],
							rrIntervals[2] - rrIntervals[3],
							rrIntervals[3] - rrIntervals[4],
							rrIntervals[4] - rrIntervals[5],
							rrIntervals[5] - rrIntervals[6],
							rrIntervals[6] - rrIntervals[7],
							rrIntervals[7] - rrIntervals[8],
							rrIntervals[8] - rrIntervals[9],
							rrIntervals[9] - rrIntervals[10],
							rrIntervals[10] - rrIntervals[11],
							rrIntervals[11] - rrIntervals[12],
							rrIntervals[12] - rrIntervals[13] };

					// Standard Deviation
//					double mean = 0;
//					for (int i = 0; i < 6; i++) {
//						mean += rrDiffs[i];
//					}
//					mean /= 6.0d;
//
//					double SD = 0;
//					for (int i = 0; i < 6; i++) {
//						SD += ((rrDiffs[i] - mean) * (rrDiffs[i] - mean));
//					}
//					SD /= 6.0d;
//					SD = Math.sqrt(SD);
//
//					System.out.println("SD: " + SD);
//
//					// HRV = mean of nn50 and nn20
//					int nn50 = 0, nn20 = 0, nnCount = 12;
//					for (int i = 0; i < 12; i++) {
//						if (rrDiffs[i] > 20) {
//							nn20++;
//						}
//						if (rrDiffs[i] > 50) {
//							nn50++;
//						}
//					}
//					double hrv = (nn50 + nn20) * 1.0d / (2 * nnCount);
//					System.out.println("HRV: " + hrv);

					// pNN50
					BtConnection.nnCount++;
					if (rrDiffs[0] > 50) {
						BtConnection.recentNn50[BtConnection.nnCount % 60] = 1;
					}
					else {
						BtConnection.recentNn50[BtConnection.nnCount % 60] = 1;
					}
					double pNN50 = 0;
					for (int i = 0; i < BtConnection.recentNn50.length; i++) {
						pNN50 += BtConnection.recentNn50[i];
					}
					pNN50 = pNN50 * 1.0 / ((BtConnection.nnCount == 0) ? 1 : ((BtConnection.nnCount < 60) ? BtConnection.nnCount : 60));

					int instantHR = (rrIntervals[0] == 0) ? 0 : (60000 / rrIntervals[0]);

					System.out.println(CustomUtilities
							.ByteToUnsignedInt(DataArray[9])); // HR
					System.out.println(CustomUtilities
							.ByteToUnsignedInt(DataArray[10])); // Heart beat n

					// ***************Displaying the Heart
					// Rate********************************
					int HRate = HRSpeedDistPacket.GetHeartRate(DataArray);
					Message text1 = _aNewHandler
							.obtainMessage(Const.HEART_RATE);
					Bundle b1 = new Bundle();
					b1.putString("HeartRate", String.valueOf(HRate));
					text1.setData(b1);
					_aNewHandler.sendMessage(text1);
					System.out.println("Heart Rate is " + HRate);

					// ***************Displaying the Instant
					// Speed********************************
					double InstantSpeed = HRSpeedDistPacket
							.GetInstantSpeed(DataArray);

					text1 = _aNewHandler
							.obtainMessage(Const.INSTANT_SPEED);
					b1.putString("InstantSpeed", String.valueOf(InstantSpeed));
					text1.setData(b1);
					_aNewHandler.sendMessage(text1);
					System.out.println("Instant Speed is " + InstantSpeed);

					// *********** Add R-R interval to the message
					// ****************
					text1 = _aNewHandler
							.obtainMessage(Const.RR_INTERVAL);
					b1.putString("RRInterval", String.valueOf(rrIntervals[0]));
					text1.setData(b1);
					_aNewHandler.sendMessage(text1);
					System.out.println("R-R interval is " + rrIntervals[0]);

					// *********** Add Instant heart rate to the message
					// ****************
					text1 = _aNewHandler.obtainMessage(Const.INSTANT_HR);
					b1.putString("InstantHR", String.valueOf(instantHR));
					text1.setData(b1);
					_aNewHandler.sendMessage(text1);
					System.out.println("Instant HR is " + instantHR);

					// *********** Add pNN50 to the message ****************
					text1 = _aNewHandler.obtainMessage(Const.PNN50);
					b1.putString("pNN50", String.valueOf(pNN50));
					text1.setData(b1);
					_aNewHandler.sendMessage(text1);
					System.out.println("pNN50 is " + pNN50);
				}
			}
		});
	}

}