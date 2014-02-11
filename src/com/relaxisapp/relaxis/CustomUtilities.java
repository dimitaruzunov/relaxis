package com.relaxisapp.relaxis;

public class CustomUtilities {

	public static int ByteToUnsignedInt(byte b) {
	    return b & 0xFF;
	}
	
	
	/**
	 * @param byte1 is the least significant byte
	 * @param byte2
	 * @return Unsigned integer based on the two bytes.
	 */
	public static int TwoBytesToUnsignedInt(byte byte1, byte byte2) {
//		int result = 0;
//		for (int i = 0; i < bytes.length; i++) {
//			result = (result << 8) + (bytes[i] & 0xFF);
//		}
		return ((byte2 & 0xFF) << 8) + (byte1& 0xFF);
	}
}
