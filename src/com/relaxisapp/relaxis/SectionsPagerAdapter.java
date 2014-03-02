package com.relaxisapp.relaxis;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.SparseArray;
import android.view.ViewGroup;

public class SectionsPagerAdapter extends FragmentPagerAdapter {
	
	public final static int BREATHING_FRAGMENT = 0;
	public final static int HOME_FRAGMENT = 1;
	public final static int STRESS_FRAGMENT = 2;
	public final static int LOGIN_FRAGMENT = 3;
	
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
		case 0:
			arguments.putString(BreathingFragment.SECTION_TITLE, sectionTitles[position]);
			fragment = new BreathingFragment();
			fragment.setArguments(arguments);
			pageReferenceMap.put(position, fragment);
			break;
		case 1:
			arguments.putString(HomeFragment.SECTION_TITLE, sectionTitles[position]);
			fragment = new HomeFragment();
			fragment.setArguments(arguments);
			pageReferenceMap.put(position, fragment);
			break;
		case 2:
			arguments.putString(StressEstimationFragment.SECTION_TITLE, sectionTitles[position]);
			fragment = new StressEstimationFragment();
			fragment.setArguments(arguments);
			pageReferenceMap.put(position, fragment);
			break;
		case 3:
			arguments.putString(StressEstimationFragment.SECTION_TITLE, sectionTitles[position]);
			fragment = new LoginFragment();
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

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		super.destroyItem(container, position, object);
		
		pageReferenceMap.remove(position);
	}
	
	public Fragment getFragment(int key) {
	    return pageReferenceMap.get(key);
	}
	
	public void setFragment(int position, ViewPager viewPager) {
		switch (position) {
		case 0:
			viewPager.setCurrentItem(HOME_FRAGMENT);
			break;
		case 1:
			viewPager.setCurrentItem(BREATHING_FRAGMENT);
			break;
		case 2:
			viewPager.setCurrentItem(STRESS_FRAGMENT);
			break;
		case 3:
			viewPager.setCurrentItem(LOGIN_FRAGMENT);
			break;
		}
	}

}
