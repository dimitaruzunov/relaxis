package com.relaxisapp.relaxis;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.facebook.widget.ProfilePictureView;

public class HomeFragment extends Fragment implements OnBtConnectionChangeListener {
	
	private UiLifecycleHelper uiHelper;

	public final static String SECTION_TITLE = "section title";

	static TextView heartRateTextView;
	static TextView instantSpeedTextView;
	static TextView rRIntervalTextView;
	static TextView instantHeartRateTextView;
	static TextView testTextView;
	
	static ProfilePictureView profilePictureView;
	
	int connectionState = 0;
	Button connectButton;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		uiHelper = new UiLifecycleHelper(getActivity(), callback);
		uiHelper.onCreate(savedInstanceState);
	}

	@Override
	public void onResume() {
		super.onResume();
		
		// For scenarios where the main activity is launched and user
	    // session is not null, the session state change notification
	    // may not be triggered. Trigger it if it's open/closed.
	    Session session = Session.getActiveSession();
	    if (session != null &&
	           (session.isOpened() || session.isClosed()) ) {
	        onSessionStateChange(session, session.getState(), null);
	    }
		
		uiHelper.onResume();
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	    uiHelper.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onPause() {
		super.onPause();
		uiHelper.onPause();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		uiHelper.onDestroy();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		uiHelper.onSaveInstanceState(outState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_home, container, false);

		setupViews(view);

		LoginButton authButton = (LoginButton) view.findViewById(R.id.authButton);
		authButton.setFragment(this);
		
		return view;
	}

	private Session.StatusCallback callback = new Session.StatusCallback() {
		@Override
		public void call(Session session, SessionState state, Exception exception) {
			onSessionStateChange(session, state, exception);
		}
	};
	
	private void makeMeRequest(final Session session) {
	    // Make an API call to get user data and define a 
	    // new callback to handle the response.
		Request request = Request.newMeRequest(session, new Request.GraphUserCallback() {

			@Override
			public void onCompleted(GraphUser user, Response response) {
				// If the response is successful
                if (session == Session.getActiveSession()) {
                    if (user != null) {
                        String user_ID = user.getId();//user id
                        String profileName = user.getName();//user's profile name
            			testTextView.setText(user_ID + " " + profileName);
            			profilePictureView.setProfileId(user.getId());
                    }   
                }   
				
			}   
        }); 
        Request.executeBatchAsync(request);
	} 

	private void onSessionStateChange(Session session, SessionState state, Exception exception) {
		if (state.isOpened()) {
			makeMeRequest(session);
			testTextView.append("/n Logged in");
			Log.i("HomeFragment", "Logged in...");
		} else if (state.isClosed()) {
			testTextView.setText("Logged out");
			Log.i("HomeFragment", "Logged out...");
		} else {

			testTextView.setText(state.toString());
		}
	}

	private void setupViews(View view) {
		connectButton = (Button) view.findViewById(R.id.connectButton);
		connectButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				handleConnectButtonClick((Button) view);
			}
		});
		
		profilePictureView = (ProfilePictureView) view.findViewById(R.id.selection_profile_pic);
		profilePictureView.setCropped(true);
		
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
	
	void handleConnectButtonClick(Button button) {
		if (this.connectionState == 0) {
			((MainActivity) getActivity()).executeConnect(button);
		}
		else if (this.connectionState == 2) {
			((MainActivity) getActivity()).executeDisconnect(button);
		}
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
	
	@Override
	public void onBtConnectionChange(int connectionState, Button button) {
		this.connectionState = connectionState;
		
		switch (connectionState) {
		case 0:
			changeBtIconConnect(button);
			break;
		case 1:
			changeBtIconConnecting(button);
			break;
		case 2:
			changeBtIconConnected(button);
			break;
		}
	}
	
	void changeBtIconConnect(Button button) {
		button.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_action_bluetooth, 0, 0, 0);
		button.setText(R.string.action_bluetooth_connect);
	}

	void changeBtIconConnecting(Button button) {
		button.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_action_bluetooth_searching, 0, 0, 0);
		button.setText(R.string.action_bluetooth_connecting);
	}

	void changeBtIconConnected(Button button) {
		button.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_action_bluetooth_connected, 0, 0, 0);
		button.setText(R.string.action_bluetooth_disconnect);
	}

}
