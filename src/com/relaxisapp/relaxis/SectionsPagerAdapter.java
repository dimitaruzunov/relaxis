package com.relaxisapp.relaxis;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.SparseArray;

public class SectionsPagerAdapter extends FragmentStatePagerAdapter {
	
	public final static int LOGIN_FRAGMENT = 0;
	public final static int HOME_FRAGMENT = 1;
	public final static int BREATHING_FRAGMENT = 2;
	public final static int STRESS_FRAGMENT = 3;
	
	String[] sectionTitles;
	SparseArray<Fragment> pageReferenceMap;
	
	public SectionsPagerAdapter(FragmentManager fm, Context context) {
		super(fm);
		
		Resources resources = context.getResources();
        sectionTitles = resources.getStringArray(R.array.section_titles);
        
        pageReferenceMap = new SparseArray<Fragment>();
	}

	@Override
	public Fragment getItem(int position) {
		Fragment fragment = null;
		
		Bundle arguments = new Bundle();
		
		switch (position) {
		case LOGIN_FRAGMENT:
			arguments.putString(LoginFragment.SECTION_TITLE, sectionTitles[position]);
			fragment = new LoginFragment();
			fragment.setArguments(arguments);
			pageReferenceMap.put(position, fragment);
			break;
		case HOME_FRAGMENT:
			arguments.putString(HomeFragment.SECTION_TITLE, sectionTitles[position]);
			fragment = new HomeFragment();
			fragment.setArguments(arguments);
			pageReferenceMap.put(position, fragment);
			break;
		case BREATHING_FRAGMENT:
			arguments.putString(BreathingFragment.SECTION_TITLE, sectionTitles[position]);
			fragment = new BreathingFragment();
			fragment.setArguments(arguments);
			pageReferenceMap.put(position, fragment);
			break;
		case STRESS_FRAGMENT:
			arguments.putString(StressEstimationFragment.SECTION_TITLE, sectionTitles[position]);
			fragment = new StressEstimationFragment();
			fragment.setArguments(arguments);
			pageReferenceMap.put(position, fragment);
			break;
		}
		
		return fragment;
	}

	@Override
	public int getCount() {
		return sectionTitles.length;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return sectionTitles[position];
	}
	
	public Fragment getFragment(int key) {
	    return pageReferenceMap.get(key);
	}
	
	public void setFragment(int position, ViewPager viewPager) {
		switch (position) {
		case NavigationDrawerListAdapter.HOME_OPTION_ITEM:
			viewPager.setCurrentItem(HOME_FRAGMENT);
			break;
		case NavigationDrawerListAdapter.BREATHING_OPTION_ITEM:
			viewPager.setCurrentItem(BREATHING_FRAGMENT);
			break;
		case NavigationDrawerListAdapter.STRESS_OPTION_ITEM:
			viewPager.setCurrentItem(STRESS_FRAGMENT);
			break;
		case NavigationDrawerListAdapter.LOGIN_OPTION_ITEM:
			viewPager.setCurrentItem(LOGIN_FRAGMENT);
			break;
		}
	}

}
