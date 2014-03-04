package com.relaxisapp.relaxis;

import java.util.ArrayList;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class NavigationDrawerListAdapter extends BaseAdapter {

	public final static int HOME_OPTION_ITEM = 0;
	public final static int BREATHING_OPTION_ITEM = 1;
	public final static int STRESS_OPTION_ITEM = 2;
	public final static int LOGIN_OPTION_ITEM = 3;
	
	private Context context;
    private ArrayList<NavigationDrawerItem> navigationDrawerItems;
	private DrawerLayout drawerLayout;
	private ListView drawerListView;
	private ActionBarDrawerToggle drawerToggle;
	
	public NavigationDrawerListAdapter(Context context, ArrayList<NavigationDrawerItem> navigationDrawerItems) {
        this.context = context;
        this.navigationDrawerItems = navigationDrawerItems;
    }
	
	@Override
	public int getCount() {
		return navigationDrawerItems.size();
	}

	@Override
	public Object getItem(int position) {
		return navigationDrawerItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.drawer_option_item, null);
        }
		
        ImageView icon = (ImageView) convertView.findViewById(R.id.icon);
        TextView title = (TextView) convertView.findViewById(R.id.title);
          
        icon.setImageResource(navigationDrawerItems.get(position).getIcon());
        title.setText(navigationDrawerItems.get(position).getTitle());
         
        return convertView;
	}

    public void setup(Activity activity, ListView.OnItemClickListener listener) {
        drawerLayout = (DrawerLayout) activity.findViewById(R.id.drawer_layout);
        drawerListView = (ListView) activity.findViewById(R.id.left_drawer);
        
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
    	switch (option) {
		case SectionsPagerAdapter.HOME_FRAGMENT:
			setSelection(HOME_OPTION_ITEM);
			break;
		case SectionsPagerAdapter.BREATHING_FRAGMENT:
			setSelection(BREATHING_OPTION_ITEM);
			break;
		case SectionsPagerAdapter.STRESS_FRAGMENT:
			setSelection(STRESS_OPTION_ITEM);
			break;
		case SectionsPagerAdapter.LOGIN_FRAGMENT:
			setSelection(LOGIN_OPTION_ITEM);
			break;
		default:
			// Drawer option doesn't exist
			break;
		}
    }
    
    public void closeDrawer() {
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
