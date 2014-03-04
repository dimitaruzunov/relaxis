package com.relaxisapp.relaxis;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.facebook.widget.ProfilePictureView;

public class LoginFragment extends Fragment {

	public final static String SECTION_TITLE = "section title";

	private UiLifecycleHelper uiHelper;

	private Session mSession;

	private Request meRequest;

	LoginButton authButton;
	ProfilePictureView profilePictureView;
	TextView userName;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		uiHelper = new UiLifecycleHelper(getActivity(), callback);
		uiHelper.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_login, container, false);

		setupViews(view);

		return view;
	}

	private void setupViews(View view) {
		authButton = (LoginButton) view.findViewById(R.id.authButton);
		authButton.setFragment(this);

		profilePictureView = (ProfilePictureView) view
				.findViewById(R.id.profilePic);
		profilePictureView.setCropped(true);

		userName = (TextView) view.findViewById(R.id.userName);
	}

	@Override
	public void onResume() {
		super.onResume();

		// For scenarios where the main activity is launched and user
		// session is not null, the session state change notification
		// may not be triggered. Trigger it if it's open/closed.
		Session session = Session.getActiveSession();
		if (session != null && (session.isOpened() || session.isClosed())) {
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

	private Session.StatusCallback callback = new Session.StatusCallback() {
		@Override
		public void call(Session session, SessionState state,
				Exception exception) {
			onSessionStateChange(session, state, exception);
		}
	};

	private void makeMeRequest(final Session session) {
		// Make an API call to get user data and define a
		// new callback to handle the response.
		meRequest = Request.newMeRequest(session,
				new Request.GraphUserCallback() {

					@Override
					public void onCompleted(GraphUser user, Response response) {
						// If the response is successful
						if (session == Session.getActiveSession()) {
							if (user != null) {
								ApiConnection.FbUserId = user.getId();
								ApiConnection.FbUserName = user.getName();

								userName.setText(ApiConnection.FbUserName);
								profilePictureView
										.setProfileId(ApiConnection.FbUserId);
							}
						}
						new ApiConnection.CheckUserTask().execute();
					}

				});
		Request.executeBatchAsync(meRequest);
	}

	private void onSessionStateChange(Session session, SessionState state,
			Exception exception) {
		if (state.isOpened()) {
			// this is necessary because of a double callback one because of
			// UiLifecycleHelper and second because of LoginFragment.onResume()
			if (mSession == null || isSessionChanged(session)) {
				mSession = session;
				makeMeRequest(session);
				toggleViewsVisibility(0);
			}
		} else if (state.isClosed()) {
			toggleViewsVisibility(4);
		} else {
			// System.out.println(state.toString());
		}
	}

	private boolean isSessionChanged(Session session) {

		// Check if session state changed
		if (mSession.getState() != session.getState())
			return true;

		// Check if accessToken changed
		if (mSession.getAccessToken() != null) {
			if (!mSession.getAccessToken().equals(session.getAccessToken()))
				return true;
		} else if (session.getAccessToken() != null) {
			return true;
		}

		// Nothing changed
		return false;
	}

	private void toggleViewsVisibility(int visibility) {
		userName.setVisibility(visibility);
		profilePictureView.setVisibility(visibility);
	}
}
