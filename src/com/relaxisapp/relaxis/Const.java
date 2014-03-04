package com.relaxisapp.relaxis;

public class Const {
	
	static final int REQUEST_ENABLE_BT = 1000;
	
	// sensor communication code constants
	static final int HEART_RATE = 0x100;
	static final int INSTANT_SPEED = 0x101;
	static final int RR_INTERVAL = 0x102;
	static final int INSTANT_HR = 0x103;
	static final int PNN50 = 0x104;

	// settings constants
	static final int SAVED_HR_COUNT = 24;
	static final int SAVED_NN50_COUNT = 180;
	static final int TIMER_TICKS_PER_SECOND = 10;
	
	// breathing graph settings
	static final int IDEAL_MID_HR = 73;
	static final int IDEAL_HR_DEVIATION = 10;
	
	static final int VIEWPORT_WIDTH = 24;
	
	static final int POINT_BARRIER = 15;

	static final int TIME_BREATHING_SECONDS = 60;
	static final int TIME_STRESS_SECONDS = 20;//180;

}
