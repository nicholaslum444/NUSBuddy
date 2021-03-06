package com.nick.nusbuddy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;

public class GetModulesAsyncTask extends AsyncTask<String, Void, String> {
		
	HttpsURLConnection connection;
	String responseContent;
	int responseCode;
	Context caller;
	
	public GetModulesAsyncTask(Context c) {
		caller = c;
	}
	
	@Override
	protected String doInBackground(String... params) {
		
		String apiKey = params[0];
		String authToken = params[1];
		String userId = params[2];
		
		if (userId == null || authToken == null || apiKey == null) {
			return null;
		}
		
		try {
			URL url = new URL("https://ivle.nus.edu.sg/api/Lapi.svc/Modules_Student?APIKey=" + apiKey
						+ "&AuthToken=" + authToken + "&StudentID=" + userId + "&Duration=0" + "&IncludeAllInfo=true" + "&output=json");
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
			} else {
				return null;
			}
			
		
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} finally {
			connection.disconnect();
		}
		
		return responseContent;
	}
	
	@Override
	protected void onPostExecute(String s) {
		ModulesAsyncTaskListener callerListener = (ModulesAsyncTaskListener) caller;
		callerListener.onModulesTaskComplete(responseContent);
	}
}
