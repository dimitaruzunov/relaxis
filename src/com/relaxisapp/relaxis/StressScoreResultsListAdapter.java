package com.relaxisapp.relaxis;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class StressScoreResultsListAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<StressScore> stressScores;
	
	public StressScoreResultsListAdapter(Context context, ArrayList<StressScore> stressScores) {
		this.context = context;
		this.stressScores = stressScores;
	}
	
	@Override
	public int getCount() {
		return stressScores.size();
	}

	@Override
	public Object getItem(int position) {
		return stressScores.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.score_result_list_item, null);
        }
		
		TextView scoreResultTextView = (TextView) convertView.findViewById(R.id.scoreResultTextView);
		scoreResultTextView.setText(String.valueOf(stressScores.get(position).getScore()));
		
		return convertView;
	}
	
}
