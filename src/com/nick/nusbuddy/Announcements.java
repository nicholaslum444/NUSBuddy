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
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.annotation.TargetApi;
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
	 * modules taken. returns all modules taken so far. does not return mcs (how dumb)
	 * https://ivle.nus.edu.sg/api/Lapi.svc/Modules_Taken?
	 * APIKey={System.String}
	 * &AuthToken={System.String}
	 * &StudentID={System.String}
	 */
	
	public class GetModulesTask extends AsyncTask<Void, Void, Boolean> {
		
		HttpsURLConnection connection;
		String responseContent;
		int responseCode;
		
		@Override
		protected void onPreExecute() {
			
		}
		
		@Override
		protected Boolean doInBackground(Void... params) {
			
			responseContent = modulesInfo;
			
			if (responseContent == null) {
			
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
				} finally {
					connection.disconnect();
				}
				
				return true;
			
			} else {
				responseCode = 200;
				return true;
			}
		}
		
		@Override
		protected void onPostExecute(Boolean b) {
			
			pd.dismiss();
			
			if (responseCode == 200 && responseContent != null) {
				try {
					sharedPrefsEditor.putString("modulesInfo", responseContent);
					sharedPrefsEditor.commit();
					
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
					
					/*modulesAnnouncementsTitlesList = new ArrayList<ArrayList<String>>();
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
					}*/
					
					createPageContents();
					
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} else {
				pd.setMessage(""+responseCode);
				pd.show();
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
	String modulesInfo;
	
	ArrayList<JSONObject> modulesList;
	ArrayList<String> modulesCodeList;
	ArrayList<JSONArray> modulesAnnouncementsList;
	/*ArrayList<ArrayList<String>> modulesAnnouncementsTitlesList;
	ArrayList<ArrayList<String>> modulesAnnouncementsContentsList;
	ArrayList<ArrayList<String>> modulesAnnouncementsAuthorsList;
	ArrayList<ArrayList<String>> modulesAnnouncementsDatesList;*/
	
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
		modulesInfo = sharedPrefs.getString("modulesInfo", null);
		
		Log.d("token", loginToken);
		
		pd = new ProgressDialog(this);
		
		pd.setMessage(userId);
		pd.show();
		
		new GetModulesTask().execute((Void)null);
			
	}
	
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
	@Override
	protected void createPageContents() {
		LinearLayout layoutAnnouncements = (LinearLayout) findViewById(R.id.Layout_announcements);
		
		// for each module, add its container if it has announcements
		/*for (int i = 0; i < numOfModules; i++) {
			
			int numOfAnnouncements = modulesAnnouncementsList.get(i).length();
			
			if (numOfAnnouncements > 0) {
				
				LinearLayout containerForModule = (LinearLayout) View.inflate(this, R.layout.container_announcements_module, null);
				layoutAnnouncements.addView(containerForModule);
				
				TextView containerName = (TextView) findViewById(R.id.TextView_announcements_module_name);
				containerName.setText(modulesCodeList.get(i));
				
				LinearLayout containerForAnnouncements = (LinearLayout) findViewById(R.id.Layout_announcements_module_announcements);
				
				// for each announcement, add the announcement title into the container
				for (int j = 0; j < numOfAnnouncements; j++) {
					TextView announcementTitle = (TextView) View.inflate(this, R.layout.textview_announcements_title, null);
					announcementTitle.setText(modulesAnnouncementsTitlesList.get(i).get(j));
					
					// TODO: set a clickListener so that user can open the announcement contents if she clicks on the title.
					
					containerForAnnouncements.addView(announcementTitle);
				}
			}
		}*/
		
		
		
		
		for (int i = 0; i < numOfModules; i++) {
			JSONArray announcements = modulesAnnouncementsList.get(i);
			if (announcements != null) {
				
				LinearLayout containerForModule = (LinearLayout) View.inflate(this, R.layout.container_announcements_module, null);
				layoutAnnouncements.addView(containerForModule);
				
				TextView containerName = (TextView) findViewById(R.id.TextView_announcements_module_name);
				containerName.setText(modulesCodeList.get(i));
				if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR1) {
					containerName.setId(View.generateViewId());
				} else {
					containerName.setId(i);
				}
				
				LinearLayout containerForAnnouncements = (LinearLayout) findViewById(R.id.Layout_announcements_module_announcements);
				if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR1) {
					containerForAnnouncements.setId(View.generateViewId());
				} else {
					containerForAnnouncements.setId(i);
				}
				
				// for each announcement, add the announcement title into the container
				int numOfAnnouncements = announcements.length();
				for (int j = 0; j < numOfAnnouncements; j++) {
					try {
						JSONObject announcement = announcements.getJSONObject(j);
						TextView announcementTitle = (TextView) View.inflate(this, R.layout.textview_announcements_title, null);
						containerForAnnouncements.addView(announcementTitle);
						announcementTitle.setText(announcement.getString("Title"));
						if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR1) {
							announcementTitle.setId(View.generateViewId());
						} else {
							announcementTitle.setId(j);
						}
						// TODO: set a clickListener so that user can open the announcement contents if she clicks on the title.
						
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	// MAJOR TODO: set the clicklisterner to open the announcements contents. format popup in xml.
	// TODO: add a refresh button! (gradebook also)
	
}
