package com.nick.nusbuddy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.AsyncTask;

public class LoginAsyncTask extends AsyncTask<String, Void, String> {
	
	HttpsURLConnection connection;
	String responseContent;
	int responseCode;
	private JSONObject loginJsonObject;
	private OutputStreamWriter loginOutputWriter;
	Activity caller;
	
	String userId;
	String password;
	
	public LoginAsyncTask(Activity c) {
		caller = c;
	}
	
	@Override
	protected String doInBackground(String... params) {
		
		String apiKey = params[0];
		userId = params[1];
		password = params[2];
		
		try {
			URL url = new URL("https://ivle.nus.edu.sg/api/Lapi.svc/Login_JSON");
			connection = (HttpsURLConnection) url.openConnection();
			connection.setConnectTimeout(60000);
			connection.setReadTimeout(60000);
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setDoInput(true);
			connection.setDoOutput(true);
			
			// creating the json credentials object
			loginJsonObject = new JSONObject();
			loginJsonObject.put("APIKey", apiKey);
			loginJsonObject.put("UserID", userId);
			loginJsonObject.put("Password", password);
			loginJsonObject.put("Domain", "");
			
			// sending the json object into the output stream as string
			loginOutputWriter = new OutputStreamWriter(connection.getOutputStream());
			loginOutputWriter.write(loginJsonObject.toString());
			loginOutputWriter.flush();
			loginOutputWriter.close();
			
			// receiving the response from server
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
			}
			
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		} finally {
			connection.disconnect();
		}
		
		return responseContent;
	}
	

	@Override
	protected void onPostExecute(String s) {
		LoginAsyncTaskListener callerListener = (LoginAsyncTaskListener) caller;
		callerListener.onLoginTaskComplete(responseContent, userId, password);
	}
}
