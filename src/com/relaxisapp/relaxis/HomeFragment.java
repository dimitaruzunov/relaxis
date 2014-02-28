package com.relaxisapp.relaxis;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class HomeFragment extends Fragment {

	public final static String SECTION_TITLE = "section title";
	
	static TextView heartRateTextView;
	static TextView instantSpeedTextView;
	static TextView rRIntervalTextView;
	static TextView instantHeartRateTextView;
	static TextView testTextView;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_home, container, false);
		
		setupViews(view);
		
		return view;
	}
	
	private void setupViews(View view) {
		testTextView = (TextView) view.findViewById(R.id.testTextView);
		
		heartRateTextView = (TextView) view.findViewById(R.id.heartRateTextView);
		heartRateTextView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				handleHeartRateTextViewClick((TextView) view);
			}
		});
		
		instantSpeedTextView = (TextView) view.findViewById(R.id.instantSpeedTextView);
		
		rRIntervalTextView = (TextView) view.findViewById(R.id.rRIntervalTextView);
		rRIntervalTextView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				handleRRIntervalTextViewClick((TextView) view);
			}
		});
		
		instantHeartRateTextView = (TextView) view.findViewById(R.id.instantHeartRateTextView);
		instantHeartRateTextView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				handleInstantHeartRateTextViewClick((TextView) view);
			}
		});
	}
	
	void handleHeartRateTextViewClick(TextView textView) {
		HintHelper.createAndPositionHint(getActivity(), R.string.heartRate, textView).show();
	}
	
	void handleInstantHeartRateTextViewClick(TextView textView) {
		HintHelper.createAndPositionHint(getActivity(), R.string.instantHeartRate, textView).show();
	}
	
	void handleRRIntervalTextViewClick(TextView textView) {
		HintHelper.createAndPositionHint(getActivity(), R.string.rRInterval, textView).show();
	}
	
	private class HttpRequestTask extends AsyncTask<Void, Void, User> {
        @Override
        protected User doInBackground(Void... params) {
            try {
                final String url = "http://rest-service.guides.spring.io/greeting";
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                User greeting = restTemplate.getForObject(url, User.class);
                return greeting;
            } catch (Exception e) {
                Log.e("MainActivity", e.getMessage(), e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(User user) {
            testTextView.setText(user.getUserId()+user.getFacebookId());
        }

    }
	
}
