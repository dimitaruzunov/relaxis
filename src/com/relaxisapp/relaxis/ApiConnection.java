package com.relaxisapp.relaxis;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import android.os.AsyncTask;
import android.text.format.DateFormat;
import android.util.Log;

public class ApiConnection {
	public static String FbUserId;
	public static String FbUserName;
	public static int UserId;
	
	public static class CheckUserTask extends AsyncTask<Void, Void, User> {
		@Override
		protected User doInBackground(Void... params) {
			try {
				final String url = "http://relaxisapp.com/api/users/"
						+ ApiConnection.FbUserId;
				RestTemplate restTemplate = new RestTemplate();
				restTemplate.getMessageConverters().add(
						new MappingJackson2HttpMessageConverter());
				User user = restTemplate.getForObject(url, User.class);
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
				ApiConnection.UserId = user.getUserId();
			}
		}

	}

	public static class CreateUserTask extends AsyncTask<Void, Void, User> {
		@Override
		protected User doInBackground(Void... params) {
			try {
				final String url = "http://relaxisapp.com/api/users/";
				RestTemplate restTemplate = new RestTemplate();
				restTemplate.getMessageConverters().add(
						new MappingJackson2HttpMessageConverter());
				User user = restTemplate.postForObject(url, new User(
						ApiConnection.FbUserId, ApiConnection.FbUserName),
						User.class);
				return user;
			} catch (Exception e) {
				Log.e("MainActivity", e.getMessage(), e);
			}

			return null;
		}

		@Override
		protected void onPostExecute(User user) {
			ApiConnection.UserId = user.getUserId();
		}

	}

	public static class AddBreathingScoreTask extends AsyncTask<Void, Void, String> {
		@Override
		protected String doInBackground(Void... params) {
			try {
				final String url = "http://relaxisapp.com/api/breathingscores/";
				RestTemplate restTemplate = new RestTemplate();
				restTemplate.getMessageConverters().add(
						new MappingJackson2HttpMessageConverter());
				
				Calendar cal = Calendar.getInstance();
				
				String uri = restTemplate.postForLocation(url, 
						new BreathingScore(ApiConnection.UserId, 
								BreathingFragment.score, 
								SimpleDateFormat.getDateTimeInstance().format(cal.getTime()))).toString();
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
				RestTemplate restTemplate = new RestTemplate();
				restTemplate.getMessageConverters().add(
						new MappingJackson2HttpMessageConverter());
				
				Calendar cal = Calendar.getInstance();
				
				String uri = restTemplate.postForLocation(url, 
						new StressScore(ApiConnection.UserId, 
								BreathingFragment.score, 
								SimpleDateFormat.getDateTimeInstance().format(cal.getTime()))).toString();
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
}
