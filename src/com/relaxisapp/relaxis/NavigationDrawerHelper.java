package com.relaxisapp.relaxis;

import android.app.Activity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class NavigationDrawerHelper {

	DrawerLayout drawerLayout;
	ListView drawerListView;

    public void init(Activity activity, ListView.OnItemClickListener listener) {
        drawerLayout = (DrawerLayout) activity.findViewById(R.id.drawer_layout);
        drawerListView = (ListView) activity.findViewById(R.id.left_drawer);

        String[] navigationDrawerOptions = activity.getResources().getStringArray(R.array.navigation_drawer_options);
        
        ArrayAdapter<String> navigationDrawerAdapter = new ArrayAdapter<String>(activity,
        		R.layout.drawer_option_item, navigationDrawerOptions);

        drawerListView.setAdapter(navigationDrawerAdapter);
        drawerListView.setOnItemClickListener(listener);
        
        drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
    }

    public void handleSelect(int option) {
        drawerLayout.closeDrawer(drawerListView);
    }
	
}
