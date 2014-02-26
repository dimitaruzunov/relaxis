package com.relaxisapp.relaxis;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

public class SectionsPagerAdapter extends FragmentPagerAdapter {
	
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
			arguments.putString(HomeFragment.SECTION_TITLE, sectionTitles[position]);
			fragment = new HomeFragment();
			fragment.setArguments(arguments);
			pageReferenceMap.put(position, fragment);
			break;
		case 1:
			arguments.putString(BreathingFragment.SECTION_TITLE, sectionTitles[position]);
			fragment = new BreathingFragment();
			fragment.setArguments(arguments);
			pageReferenceMap.put(position, fragment);
			break;
		case 2:
			arguments.putString(StressEstimationFragment.SECTION_TITLE, sectionTitles[position]);
			fragment = new BreathingFragment();
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
	
}
