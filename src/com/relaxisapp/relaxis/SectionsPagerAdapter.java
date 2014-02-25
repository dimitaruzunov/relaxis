package com.relaxisapp.relaxis;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class SectionsPagerAdapter extends FragmentPagerAdapter {
	
	String[] sectionTitles;
	
	public SectionsPagerAdapter(FragmentManager fm, Context context) {
		super(fm);
		
		Resources resources = context.getResources();
        sectionTitles = resources.getStringArray(R.array.section_titles);
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
	
}
