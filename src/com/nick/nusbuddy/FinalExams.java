package com.nick.nusbuddy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.nick.nusbuddy.Gradebook.GetModulesTask;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;
import android.view.Menu;

public class FinalExams extends Activity {

	public class GetExamsTask extends AsyncTask<String, Void, Boolean> {
		
		HttpsURLConnection connection;
		String responseContent;
		int responseCode;
		private String moduleId;
		
		@Override
		protected void onPreExecute() {
			
		}
		
		@Override
		// only take in the module Id. login token should be gotten from the sharedPrefs.
		protected Boolean doInBackground(String... params) {
			
			
			moduleId = params[0];
			
			if (loginToken == null) {
				return false;
			}
			
			try {
				URL url = new URL("https://ivle.nus.edu.sg/api/Lapi.svc/Timetable_ModuleExam?APIKey=" + getString(R.string.api_key_mine)
							+ "&AuthToken=" + loginToken + "&CourseID=" + moduleId + "&output=json");
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
				}
				
				
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				connection.disconnect();
			}
			
			return true;
		}
		
		@Override
		protected void onPostExecute(Boolean b) {
			pd.dismiss();
			if (responseCode == 200 && responseContent != null) {
				
				
				
			} else {
				pd.setMessage(responseCode+"");
				pd.show();
			}
		}
	}
	
	
	public SharedPreferences sharedPrefs;
	public Editor sharedPrefsEditor;
	public Context context;
	public ProgressDialog pd;
	public String loginToken;
	public String userId;
	public String modulesInfo;
	private int numOfModules;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_final_exams);
		
		context = this;
		sharedPrefs = this.getSharedPreferences("NUSBuddyPrefs", MODE_PRIVATE);
		sharedPrefsEditor = sharedPrefs.edit();
		sharedPrefsEditor.commit();
		
		userId = sharedPrefs.getString("userId", null);
		loginToken = sharedPrefs.getString("loginToken", null);
		modulesInfo = sharedPrefs.getString("modulesInfo", null);
		numOfModules = sharedPrefs.getInt("numOfModules", -1);
		
		if (numOfModules == -1) {
			try {
				JSONObject modulesInfoObject = new JSONObject(modulesInfo);
				JSONArray modulesArray = modulesInfoObject.getJSONArray("Results");
				numOfModules = modulesArray.length();
				
				sharedPrefsEditor.putInt("numOfModules", numOfModules);
				sharedPrefsEditor.commit();
				
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		Log.d("token", loginToken);
		
		pd = new ProgressDialog(context);
		
		pd.setMessage(userId);
		pd.show();
		
		for (int i = 0; i < numOfModules; i++) {
			String moduleId = "";
			new GetExamsTask().execute(moduleId);
			
		}
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.final_exams, menu);
		return true;
	}

}
