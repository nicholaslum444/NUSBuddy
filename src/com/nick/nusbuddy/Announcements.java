package com.nick.nusbuddy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.R.dimen;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class Announcements extends BaseActivity {
	
	/*
	 * polling for all modules
	 * https://ivle.nus.edu.sg/api/Lapi.svc/Modules_Student?
	 * APIKey={System.String}
	 * &AuthToken={System.String}
	 * &Duration={System.Int32}
	 * &IncludeAllInfo={System.Boolean}
	 * 
	 * prolly use this. just modules taken.
	 * https://ivle.nus.edu.sg/api/Lapi.svc/Modules_Taken?
	 * APIKey={System.String}
	 * &AuthToken={System.String}
	 * &StudentID={System.String}
	 */
	public class GetModuleCodesTask extends AsyncTask<Void, Void, Boolean> {
		
		HttpsURLConnection connection;
		String responseContent;
		int responseCode;
		
		@Override
		protected void onPreExecute() {
			
		}
		
		@Override
		protected Boolean doInBackground(Void... params) {
			
			if (userId == null || loginToken == null) {
				return false;
			}
			
			try {
				URL url = new URL("https://ivle.nus.edu.sg/api/Lapi.svc/Modules_Student?APIKey=" + getString(R.string.api_key_mine)
							+ "&AuthToken=" + loginToken + "&StudentID=" + userId + "&Duration=0" + "&IncludeAllInfo=true" + "&output=json");
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
			}
			
			return true;
		}
		
		@Override
		protected void onPostExecute(Boolean b) {
			pd.dismiss();
			if (responseCode == 200 && responseContent != null) {
				try {
					
					JSONObject responseObject = new JSONObject(responseContent);
					JSONArray modulesArray = responseObject.getJSONArray("Results");
					numOfModules = modulesArray.length();
					
					modulesList = new ArrayList<JSONObject>(); 
					for (int i = 0; i < numOfModules; i++) {
						modulesList.add(modulesArray.getJSONObject(i));
					}
					
					modulesCodeList = new ArrayList<String>();
					modulesAnnouncementsList = new ArrayList<JSONArray>();
					for (int i = 0; i < numOfModules; i++) {
						JSONObject obj = modulesList.get(i);
						modulesCodeList.add(obj.getString("CourseCode"));
						modulesAnnouncementsList.add(obj.getJSONArray("Announcements"));
					}
					
					modulesAnnouncementsTitlesList = new ArrayList<ArrayList<String>>();
					modulesAnnouncementsContentsList = new ArrayList<ArrayList<String>>();
					modulesAnnouncementsAuthorsList = new ArrayList<ArrayList<String>>();
					modulesAnnouncementsDatesList = new ArrayList<ArrayList<String>>();
					for (int i = 0; i < numOfModules; i++) {
						
						JSONArray arr = modulesAnnouncementsList.get(i);
						if (arr.length() > 0) {
							
							ArrayList<String> titles = new ArrayList<String>();
							ArrayList<String> contents = new ArrayList<String>();
							ArrayList<String> authors = new ArrayList<String>();
							ArrayList<String> dates = new ArrayList<String>();
							for (int j = 0; j < arr.length(); j++) {
								
								JSONObject obj = arr.getJSONObject(j);
								
								titles.add(obj.getString("Title"));
								contents.add(obj.getString("Description"));
								authors.add(obj.getJSONObject("Creator").getString("Name"));
								dates.add(obj.getString("CreatedDate_js"));
							}
							
							modulesAnnouncementsTitlesList.add(titles);
							modulesAnnouncementsContentsList.add(contents);
							modulesAnnouncementsAuthorsList.add(authors);
							modulesAnnouncementsDatesList.add(dates);
							
						} else {
							modulesAnnouncementsTitlesList.add(null);
							modulesAnnouncementsContentsList.add(null);
							modulesAnnouncementsAuthorsList.add(null);
							modulesAnnouncementsDatesList.add(null);
						}
					}
					
					createPageContents();
					
					// debug
					pd.setMessage(""+modulesAnnouncementsDatesList.toString()); 
					//pd.show();
					
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
		
	}
	
	
	SharedPreferences sharedPrefs;
	Editor sharedPrefsEditor;
	Context context;
	String loginToken;
	String userId;
	ProgressDialog pd;
	
	int numOfModules;
	
	ArrayList<JSONObject> modulesList;
	ArrayList<String> modulesCodeList;
	ArrayList<JSONArray> modulesAnnouncementsList;
	ArrayList<ArrayList<String>> modulesAnnouncementsTitlesList;
	ArrayList<ArrayList<String>> modulesAnnouncementsContentsList;
	ArrayList<ArrayList<String>> modulesAnnouncementsAuthorsList;
	ArrayList<ArrayList<String>> modulesAnnouncementsDatesList;
	
	@Override
	protected Activity getCurrentActivity() {
		return this;
	}

	@Override
	protected int getCurrentActivityLayout() {
		return R.layout.contents_announcements;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		context = this;
		sharedPrefs = this.getSharedPreferences("NUSBuddyPrefs", MODE_PRIVATE);
		sharedPrefsEditor = sharedPrefs.edit();
		
		userId = sharedPrefs.getString("userId", null);
		loginToken = sharedPrefs.getString("loginToken", null);
		Log.e("token", loginToken);
		
		pd = new ProgressDialog(this);
		
		pd.setMessage(userId);
		pd.show();
		
		new GetModuleCodesTask().execute((Void)null);
	}
	
	@Override
	protected void createPageContents() {
		LinearLayout layoutAnnouncements = (LinearLayout) findViewById(R.id.Layout_announcements);
		
		ArrayList<LinearLayout> individualModuleLayouts = new ArrayList<LinearLayout>();
		
		for (int i = 0; i < numOfModules; i++) {
			int numOfAnnouncements = modulesAnnouncementsList.get(i).length();
			if (numOfAnnouncements > 0) {
				LinearLayout containerForModule = new LinearLayout(this);
				containerForModule.setOrientation(LinearLayout.VERTICAL);
				containerForModule.setPadding(10, 10, 10, 10);
				containerForModule.setBackground(getResources().getDrawable(R.drawable.border));
				
				TextView containerName = new TextView(this, null, android.R.attr.textAppearanceLarge);
				containerName.setText(modulesCodeList.get(i));
				
				LinearLayout containerForAnnouncements = new LinearLayout(this);
				containerForAnnouncements.setOrientation(LinearLayout.VERTICAL);
				
				for (int j = 0; j < numOfAnnouncements; j++) {
					TextView announcementTitle = new TextView(this, null, android.R.attr.textAppearanceMedium);
					announcementTitle.setMaxLines(1);
					announcementTitle.setText(modulesAnnouncementsTitlesList.get(i).get(j));
					Log.i("asdasd", modulesAnnouncementsTitlesList.get(i).get(j));
					containerForAnnouncements.addView(announcementTitle);
				}
				
				containerForModule.addView(containerName);
				containerForModule.addView(containerForAnnouncements);
				individualModuleLayouts.add(containerForModule);
			}
		}
		
		Log.i("asd", individualModuleLayouts.toString());
		
		for (int i = 0; i < individualModuleLayouts.size(); i++) {
			layoutAnnouncements.addView(individualModuleLayouts.get(i));
		}
		
	}

}
