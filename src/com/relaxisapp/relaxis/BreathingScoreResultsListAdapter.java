package com.relaxisapp.relaxis;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class BreathingScoreResultsListAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<BreathingScore> breathingScores;
	
	public BreathingScoreResultsListAdapter(Context context, ArrayList<BreathingScore> breathingScores) {
		this.context = context;
		this.breathingScores = breathingScores;
	}
	
	@Override
	public int getCount() {
		return breathingScores.size();
	}

	@Override
	public Object getItem(int position) {
		return breathingScores.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.breathing_score_result_list_item, null);
        }
		
		TextView scoreResultTextView = (TextView) convertView.findViewById(R.id.scoreResultTextView);
		scoreResultTextView.setText(String.valueOf(breathingScores.get(position).getScore()));
		
		return convertView;
	}
	
}
