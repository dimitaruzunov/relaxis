package com.relaxisapp.relaxis;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import android.os.AsyncTask;
import android.util.Log;

public class ApiConnection {
	public static String FbUserId;
	public static String FbUserName;
	public static int UserId;
	public static ArrayList<BreathingScore> currentUserBreathingScores = null;
	public static ArrayList<StressScore> currentUserStressScores = null;
	
	public static class CheckUserTask extends AsyncTask<Void, Void, User> {
		@Override
		protected User doInBackground(Void... params) {
			try {
				Log.d("USER", "dasdsafasdf");
				final String url = "http://relaxisapp.com/api/users/"
						+ ApiConnection.FbUserId;
				HttpHeaders requestHeaders = new HttpHeaders();
				requestHeaders.add("Authorization-Token", "$kG7j2lr&");
				requestHeaders.add("Content-Type", "application/json");
				requestHeaders.add("Host", "relaxisapp.com");
				HttpEntity<?> httpEntity = new HttpEntity<Object>(requestHeaders);
				RestTemplate restTemplate = new RestTemplate();
				restTemplate.getMessageConverters().add(
						new MappingJackson2HttpMessageConverter());
				User user = restTemplate.exchange(url, HttpMethod.GET, httpEntity, User.class).getBody();
				return user;
			} catch (HttpClientErrorException e) {
				if (e.getStatusCode().value() == 404) {
					new CreateUserTask().execute();
				} else {
					Log.e("MainActivity", e.getMessage(), e);
				}
			}

			return null;
		}

		@Override
		protected void onPostExecute(User user) {
			if (user != null) {
				Log.d("USER", String.valueOf(user.getUserId()));
				ApiConnection.UserId = user.getUserId();
				new GetCurrentUserBreathingScoresTask().execute();
				new GetCurrentUserStressScoresTask().execute();
			}
		}

	}

	public static class CreateUserTask extends AsyncTask<Void, Void, User> {
		@Override
		protected User doInBackground(Void... params) {
			try {
				final String url = "http://relaxisapp.com/api/users/";
				HttpHeaders requestHeaders = new HttpHeaders();
				requestHeaders.add("Authorization-Token", "$kG7j2lr&");
				requestHeaders.add("Content-Type", "application/json");
				requestHeaders.add("Host", "relaxisapp.com");
				User body = new User(
						ApiConnection.FbUserId, ApiConnection.FbUserName);
				HttpEntity<User> httpEntity = new HttpEntity<User>(body, requestHeaders);
				RestTemplate restTemplate = new RestTemplate();
				restTemplate.getMessageConverters().add(
						new MappingJackson2HttpMessageConverter());
				User user = restTemplate.exchange(url, HttpMethod.POST, httpEntity, User.class).getBody();
				return user;
			} catch (Exception e) {
				Log.e("MainActivity", e.getMessage(), e);
			}

			return null;
		}

		@Override
		protected void onPostExecute(User user) {
			if (user != null) {
				ApiConnection.UserId = user.getUserId();
				new GetCurrentUserBreathingScoresTask().execute();
				new GetCurrentUserStressScoresTask().execute();
			}
		}

	}

	public static class AddBreathingScoreTask extends AsyncTask<Void, Void, String> {
		@Override
		protected String doInBackground(Void... params) {
			try {
				final String url = "http://relaxisapp.com/api/breathingscores/";
				HttpHeaders requestHeaders = new HttpHeaders();
				requestHeaders.add("Authorization-Token", "$kG7j2lr&");
				requestHeaders.add("Content-Type", "application/json");
				requestHeaders.add("Host", "relaxisapp.com");
				
				Calendar cal = Calendar.getInstance();
				
				BreathingScore body = new BreathingScore(ApiConnection.UserId, 
						BreathingFragment.score, 
						SimpleDateFormat.getDateTimeInstance().format(cal.getTime()));
				HttpEntity<BreathingScore> httpEntity = new HttpEntity<BreathingScore>(body, requestHeaders);
				RestTemplate restTemplate = new RestTemplate();
				restTemplate.getMessageConverters().add(
						new MappingJackson2HttpMessageConverter());

				String uri = restTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class).getBody();
				return uri;
			} catch (Exception e) {
				Log.e("MainActivity", e.getMessage(), e);
			}

			return null;
		}

		@Override
		protected void onPostExecute(String uri) {
			if (uri != null) {
				Log.i("URI", uri);
			}
		}

	}

	public static class AddStressScoreTask extends AsyncTask<Void, Void, String> {
		@Override
		protected String doInBackground(Void... params) {
			try {
				final String url = "http://relaxisapp.com/api/stressscores/";
				HttpHeaders requestHeaders = new HttpHeaders();
				requestHeaders.add("Authorization-Token", "$kG7j2lr&");
				requestHeaders.add("Content-Type", "application/json");
				requestHeaders.add("Host", "relaxisapp.com");
				
				Calendar cal = Calendar.getInstance();
				
				StressScore body = new StressScore(ApiConnection.UserId, 
						StressEstimationFragment.stressLevel * 10, 
						SimpleDateFormat.getDateTimeInstance().format(cal.getTime()));
				HttpEntity<StressScore> httpEntity = new HttpEntity<StressScore>(body, requestHeaders);
				RestTemplate restTemplate = new RestTemplate();
				restTemplate.getMessageConverters().add(
						new MappingJackson2HttpMessageConverter());

				String uri = restTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class).getBody();
				return uri;
			} catch (Exception e) {
				Log.e("MainActivity", e.getMessage(), e);
			}

			return null;
		}

		@Override
		protected void onPostExecute(String uri) {
			if (uri != null) {
				Log.i("URI", uri);
			}
		}

	}

	public static class GetCurrentUserBreathingScoresTask extends AsyncTask<Void, Void, BreathingScore[]> {
		@Override
		protected BreathingScore[] doInBackground(Void... params) {
			try {
				final String url = "http://relaxisapp.com/api/users/"+ ApiConnection.UserId +"/breathingscores";
				HttpHeaders requestHeaders = new HttpHeaders();
				requestHeaders.add("Authorization-Token", "$kG7j2lr&");
				requestHeaders.add("Content-Type", "application/json");
				requestHeaders.add("Host", "relaxisapp.com");
				HttpEntity<?> httpEntity = new HttpEntity<Object>(requestHeaders);
				RestTemplate restTemplate = new RestTemplate();
				restTemplate.getMessageConverters().add(
						new MappingJackson2HttpMessageConverter());
				
				BreathingScore[] scores = restTemplate.exchange(url, HttpMethod.GET, httpEntity, BreathingScore[].class).getBody();
				return scores;
			} catch (Exception e) {
				Log.e("MainActivity", e.getMessage(), e);
			}

			return null;
		}

		@Override
		protected void onPostExecute(BreathingScore[] scores) {
			if (scores != null) {
				Log.i("Scores", scores.toString());
				currentUserBreathingScores = new ArrayList<BreathingScore>();
				for (int i = 0; i < scores.length; i++) {
					currentUserBreathingScores.add(scores[i]);
				}
			}
		}

	}

	public static class GetCurrentUserStressScoresTask extends AsyncTask<Void, Void, StressScore[]> {
		@Override
		protected StressScore[] doInBackground(Void... params) {
			try {
				final String url = "http://relaxisapp.com/api/users/"+ ApiConnection.UserId +"/stressscores";
				HttpHeaders requestHeaders = new HttpHeaders();
				requestHeaders.add("Authorization-Token", "$kG7j2lr&");
				requestHeaders.add("Content-Type", "application/json");
				requestHeaders.add("Host", "relaxisapp.com");
				HttpEntity<?> httpEntity = new HttpEntity<Object>(requestHeaders);
				RestTemplate restTemplate = new RestTemplate();
				restTemplate.getMessageConverters().add(
						new MappingJackson2HttpMessageConverter());
				StressScore[] scores = restTemplate.exchange(url, HttpMethod.GET, httpEntity, StressScore[].class).getBody();
				return scores;
			} catch (Exception e) {
				Log.e("MainActivity", e.getMessage(), e);
			}

			return null;
		}

		@Override
		protected void onPostExecute(StressScore[] scores) {
			if (scores != null) {
				Log.i("Scores", scores.toString());
				
				currentUserStressScores = new ArrayList<StressScore>();
				for (int i = 0; i < scores.length; i++) {
					currentUserStressScores.add(scores[i]);
				}
			}
		}

	}
}
