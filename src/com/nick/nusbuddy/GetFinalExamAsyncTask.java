package com.nick.nusbuddy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

public class GetFinalExamAsyncTask extends AsyncTask<String, Void, String> {
		
	HttpsURLConnection connection;
	String responseContent;
	int responseCode;
	Activity caller;
	
	public GetFinalExamAsyncTask(Activity c) {
		caller = c;
	}
	
	@Override
	protected String doInBackground(String... params) {
		
		String apiKey = params[0];
		String authToken = params[1];
		String moduleId = params[2];
		
		if (moduleId == null || authToken == null || apiKey == null) {
			Log.d("asd", "nullslslslsl");
			return null;
		}
		
		try {
			URL url = new URL("https://ivle.nus.edu.sg/api/Lapi.svc/Timetable_ModuleExam?APIKey=" + apiKey
						+ "&AuthToken=" + authToken + "&CourseID=" + moduleId + "&output=json");
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
				Log.d("asd", ""+responseCode);
				return null;
			}
			
		
		} catch (MalformedURLException e) {
			Log.d("asd", e.toString());
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			Log.d("asd", e.toString());
			e.printStackTrace();
			return null;
		} finally {
			connection.disconnect();
		}
		
		return responseContent;
	}
	
	@Override
	protected void onPostExecute(String s) {
		FinalExamAsyncTaskListener callerListener = (FinalExamAsyncTaskListener) caller;
		callerListener.onFinalExamTaskComplete(responseContent);
	}
}
