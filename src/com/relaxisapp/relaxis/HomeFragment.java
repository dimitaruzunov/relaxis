package com.relaxisapp.relaxis;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.LoginButton;

public class HomeFragment extends Fragment {

	private HomeFragment homeFragment;
//	private UiLifecycleHelper uiHelper;

	public final static String SECTION_TITLE = "section title";

	static TextView heartRateTextView;
	static TextView instantSpeedTextView;
	static TextView rRIntervalTextView;
	static TextView instantHeartRateTextView;
	static TextView testTextView;

//	@Override
//	public void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		uiHelper = new UiLifecycleHelper(getActivity(), callback);
//		uiHelper.onCreate(savedInstanceState);
//	}
//
//	@Override
//	public void onResume() {
//		super.onResume();
//		
//		// For scenarios where the main activity is launched and user
//	    // session is not null, the session state change notification
//	    // may not be triggered. Trigger it if it's open/closed.
//	    Session session = Session.getActiveSession();
//	    if (session != null &&
//	           (session.isOpened() || session.isClosed()) ) {
//	        onSessionStateChange(session, session.getState(), null);
//	    }
//		
//		uiHelper.onResume();
//	}
//
//	@Override
//	public void onActivityResult(int requestCode, int resultCode, Intent data) {
//		super.onActivityResult(requestCode, resultCode, data);
//		uiHelper.onActivityResult(requestCode, resultCode, data);
//	}
//
//	@Override
//	public void onPause() {
//		super.onPause();
//		uiHelper.onPause();
//	}
//
//	@Override
//	public void onDestroy() {
//		super.onDestroy();
//		uiHelper.onDestroy();
//	}
//
//	@Override
//	public void onSaveInstanceState(Bundle outState) {
//		super.onSaveInstanceState(outState);
//		uiHelper.onSaveInstanceState(outState);
//	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_home, container, false);

		setupViews(view);

		if (savedInstanceState == null) {
			// Add the fragment on initial activity setup
			homeFragment = new HomeFragment();
			getFragmentManager().beginTransaction()
					.add(android.R.id.content, homeFragment).commit();
		} else {
			// Or set the fragment from restored state info
			homeFragment = (HomeFragment) getFragmentManager()
					.findFragmentById(android.R.id.content);
		}

//		LoginButton authButton = (LoginButton) view
//				.findViewById(R.id.authButton);
//		authButton.setFragment(this);

		return view;
	}

//	private Session.StatusCallback callback = new Session.StatusCallback() {
//		@Override
//		public void call(Session session, SessionState state,
//				Exception exception) {
//			onSessionStateChange(session, state, exception);
//		}
//	};
//
//	private void onSessionStateChange(Session session, SessionState state,
//			Exception exception) {
//		if (state.isOpened()) {
//			Log.i("HomeFragment", "Logged in...");
//		} else if (state.isClosed()) {
//			Log.i("HomeFragment", "Logged out...");
//		}
//	}

	private void setupViews(View view) {
		testTextView = (TextView) view.findViewById(R.id.testTextView);

		heartRateTextView = (TextView) view
				.findViewById(R.id.heartRateTextView);
		heartRateTextView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				handleHeartRateTextViewClick((TextView) view);
			}
		});

		instantSpeedTextView = (TextView) view
				.findViewById(R.id.instantSpeedTextView);

		rRIntervalTextView = (TextView) view
				.findViewById(R.id.rRIntervalTextView);
		rRIntervalTextView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				handleRRIntervalTextViewClick((TextView) view);
			}
		});

		instantHeartRateTextView = (TextView) view
				.findViewById(R.id.instantHeartRateTextView);
		instantHeartRateTextView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				handleInstantHeartRateTextViewClick((TextView) view);
			}
		});
	}

	void handleHeartRateTextViewClick(TextView textView) {
		HintHelper.createAndPositionHint(getActivity(), R.string.heartRate,
				textView).show();
	}

	void handleInstantHeartRateTextViewClick(TextView textView) {
		HintHelper.createAndPositionHint(getActivity(),
				R.string.instantHeartRate, textView).show();
	}

	void handleRRIntervalTextViewClick(TextView textView) {
		HintHelper.createAndPositionHint(getActivity(), R.string.rRInterval,
				textView).show();
	}

	private class HttpRequestTask extends AsyncTask<Void, Void, User> {
		@Override
		protected User doInBackground(Void... params) {
			try {
				final String url = "http://rest-service.guides.spring.io/greeting";
				RestTemplate restTemplate = new RestTemplate();
				restTemplate.getMessageConverters().add(
						new MappingJackson2HttpMessageConverter());
				User greeting = restTemplate.getForObject(url, User.class);
				return greeting;
			} catch (Exception e) {
				Log.e("MainActivity", e.getMessage(), e);
			}

			return null;
		}

		@Override
		protected void onPostExecute(User user) {
			testTextView.setText(user.getUserId() + user.getFacebookId());
		}

	}

}
