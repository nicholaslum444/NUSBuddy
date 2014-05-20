package com.nick.nusbuddy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

import javax.net.ssl.HttpsURLConnection;

import android.os.AsyncTask;
import android.util.Log;

public class StudentNameGetter {
	
	String apiKey;
	String authToken;

	public static class GetStudentNameAsyncTask extends AsyncTask<String, Void, String> {
		
		HttpsURLConnection connection;
		String responseContent;
		int responseCode;
		
		@Override
		protected String doInBackground(String... params) {
			
			String apiKey2 = params[0];
			String authToken2 = params[1];
			
			if (authToken2 == null) {
				return null;
			}
			
			try {
				Log.d("api", apiKey2);
				Log.d("auth", authToken2);
				URL url = new URL("https://ivle.nus.edu.sg/api/Lapi.svc/UserName_Get?APIKey=" + apiKey2 + "&AuthToken=" + authToken2);
				connection = (HttpsURLConnection) url.openConnection();
				connection.setConnectTimeout(60000);
				connection.setReadTimeout(60000);
				
				responseCode = connection.getResponseCode();
				
				if (responseCode == 200) {
					BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
					StringBuilder sb = new StringBuilder();
					String line = br.readLine();
					while (line != null) {
						sb.append(line);
						line = br.readLine();
					}
					br.close();
					responseContent = sb.toString();
					Log.d("sd", responseContent);
				}
				
				
			} catch (MalformedURLException e) {
				Log.e("error", e.toString());
				e.printStackTrace();
				return null;
			} catch (IOException e) {
				Log.e("error", e.toString());
				e.printStackTrace();
				return null;
			} finally {
				connection.disconnect();
			}
			
			return responseContent;
		}
	}
	
	
	public StudentNameGetter(String apiKey, String authToken) {
		this.apiKey = apiKey;
		this.authToken = authToken;
	}
	
	public String get() {
		GetStudentNameAsyncTask t = new GetStudentNameAsyncTask();
		//t.execute((Void) null);
		
		
		try {
			return t.get();
			
		} catch (InterruptedException e) {
			Log.e("error", e.toString());
			e.printStackTrace();
			return null;
			
		} catch (ExecutionException e) {
			Log.e("error", e.toString());
			e.printStackTrace();
			return null;
		}
	}
	
}
