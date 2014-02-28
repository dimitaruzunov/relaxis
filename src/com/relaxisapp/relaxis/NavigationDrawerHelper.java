package com.relaxisapp.relaxis;

import android.app.ActionBar;
import android.app.Activity;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class NavigationDrawerHelper {

	DrawerLayout drawerLayout;
	ListView drawerListView;
	
	private ActionBarDrawerToggle drawerToggle;

    public void init(Activity activity, ListView.OnItemClickListener listener) {
        drawerLayout = (DrawerLayout) activity.findViewById(R.id.drawer_layout);
        drawerListView = (ListView) activity.findViewById(R.id.left_drawer);

        String[] navigationDrawerOptions = activity.getResources().getStringArray(R.array.navigation_drawer_options);
        
        ArrayAdapter<String> navigationDrawerAdapter = new ArrayAdapter<String>(activity,
        		R.layout.drawer_option_item, navigationDrawerOptions);

        drawerListView.setAdapter(navigationDrawerAdapter);
        drawerListView.setOnItemClickListener(listener);
        
        drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        
        setupActionBar(activity);
    }
    
    private void setupActionBar(Activity activity) {
    	final Activity activityConst = activity;
    	ActionBar actionBar = activity.getActionBar();
    	
    	actionBar.setDisplayHomeAsUpEnabled(true);
    	
    	drawerToggle = new ActionBarDrawerToggle(activity, drawerLayout, R.drawable.ic_drawer,
    			R.string.open_drawer_message, R.string.close_drawer_message) {
    		@Override
    		public void onDrawerOpened(View drawerView) {
    			activityConst.invalidateOptionsMenu();
    			super.onDrawerOpened(drawerView);
    		}
    		
    		@Override
    		public void onDrawerClosed(View drawerView) {
    			activityConst.invalidateOptionsMenu();
    			super.onDrawerClosed(drawerView);
    		}
    	};
    }

    public void handleSelect(int option) {
        drawerLayout.closeDrawer(drawerListView);
    }
    
    public void handleOnPrepareOptionsMenu(Menu menu) {
    	boolean itemVisible = !drawerLayout.isDrawerOpen(drawerListView);
    	
    	for (int index = 0; index < menu.size(); index++) {
    		MenuItem item = menu.getItem(index);
    		item.setEnabled(itemVisible);
    	}
    }
    
    public void handleOnOptionsItemSelected(MenuItem item) {
    	drawerToggle.onOptionsItemSelected(item);
    }
    
    public void syncState() {
    	drawerToggle.syncState();
    }
    
    public void setSelection(int option) {
    	drawerListView.setItemChecked(option, true);
    }
	
}
