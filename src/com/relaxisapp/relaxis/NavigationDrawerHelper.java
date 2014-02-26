package com.relaxisapp.relaxis;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.content.Intent;

public class NavigationDrawerHelper {

	DrawerLayout drawerLayout;
	ListView drawerListView;

    public void init(Activity activity, ListView.OnItemClickListener listener) {
        drawerLayout = (DrawerLayout) activity.findViewById(R.id.drawer_layout);
        drawerListView = (ListView) activity.findViewById(R.id.left_drawer);

        String[] navigationDrawerOptions = activity.getResources().getStringArray(R.array.navigation_drawer_options);
        
        ArrayAdapter<String> navigationDrawerAdapter = new ArrayAdapter<String>(activity, R.layout.drawer_option_item, navigationDrawerOptions);

        drawerListView.setAdapter(navigationDrawerAdapter);
        drawerListView.setOnItemClickListener(listener);
        
        drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
    }

    public void handleSelect(Context context, View view, int option) {
    	switch(option) {
    	case 0:
    		handleBreathingAppOptionClick(context, view);
    		break;
    	case 1:
    		handleStressAppOptionClick(context, view);
    		break;
    	}
    	
        drawerListView.setItemChecked(option, true);
        drawerLayout.closeDrawer(drawerListView);
    }
    
    void handleBreathingAppOptionClick(Context context, View view) {
    	Intent intent = new Intent(context, BreathingFragment.class);
		context.startActivity(intent);
    }
    
    void handleStressAppOptionClick(Context context, View view) {
    	Intent intent = new Intent(context, StressEstimationActivity.class);
		context.startActivity(intent);
    }
	
}
