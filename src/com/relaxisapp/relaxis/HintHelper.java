package com.relaxisapp.relaxis;

import android.app.Activity;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

public class HintHelper {
	
	public static Toast createAndPositionHint(Activity activity, int stringId, View view) {
		Toast hint = Toast.makeText(activity, stringId, Toast.LENGTH_SHORT);
		hint.setGravity(Gravity.TOP | Gravity.LEFT, view.getRight() + 16, view.getBottom() + 10);
		
		return hint;
	}
	
}
