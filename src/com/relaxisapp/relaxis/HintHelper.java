package com.relaxisapp.relaxis;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

public class HintHelper {
	
	public static Toast createAndPositionHint(Context context, int stringId, View view) {
		Toast hint = Toast.makeText(context, stringId, Toast.LENGTH_SHORT);
		hint.setGravity(Gravity.TOP | Gravity.LEFT, view.getRight(), view.getTop() + 90);
		
		return hint;
	}
	
}
