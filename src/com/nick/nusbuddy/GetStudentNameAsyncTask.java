package com.nick.nusbuddy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import android.os.AsyncTask;
import android.util.Log;

public class GetStudentNameAsyncTask extends AsyncTask<String, Void, String> {
	
	HttpsURLConnection connection;
	String responseContent;
	int responseCode;
	
	@Override
	protected String doInBackground(String... params) {
		
		String apiKey = params[0];
		String authToken = params[1];
		
		if (authToken == null || apiKey == null) {
			return null;
		}
		
		try {
			Log.d("api", apiKey);
			Log.d("auth", authToken);
			URL url = new URL("https://ivle.nus.edu.sg/api/Lapi.svc/UserName_Get?APIKey=" + apiKey + "&Token=" + authToken + "&output=json");
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
