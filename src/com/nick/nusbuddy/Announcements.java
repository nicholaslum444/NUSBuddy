package com.nick.nusbuddy;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

public class Announcements extends BaseActivity implements ModulesAsyncTaskListener {
	
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
	
	
	SharedPreferences sharedPrefs;
	Editor sharedPrefsEditor;
	Context context;
	
	String apiKey;
	String authToken;
	String userId;
	ProgressDialog pd;
	
	int numOfModules;
	String modulesInfo;
	
	ArrayList<JSONObject> modulesList;
	ArrayList<String> modulesCodeList;
	ArrayList<JSONArray> modulesAnnouncementsList;
	
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
		
		apiKey = getString(R.string.api_key_mine);
		userId = sharedPrefs.getString("userId", null);
		authToken = sharedPrefs.getString("authToken", null);
		modulesInfo = sharedPrefs.getString("modulesInfo", null);
		
		pd = new ProgressDialog(this);
		
		pd.setMessage(userId);
		pd.show();
		
		if (modulesInfo == null) {
			runGetModules();
		} else {
			Log.d("modules", modulesInfo);
			runParseModules();
		}
			
	}
	
	public void runGetModules() {
		
		pd.setMessage("Retrieving modules");
		
		GetModulesAsyncTask modulesTask = new GetModulesAsyncTask(this);
		
		modulesTask.execute(apiKey, authToken, userId);
	}
	
	@Override
	public void onModulesTaskComplete(String responseContent) {
		modulesInfo = responseContent;
		sharedPrefsEditor.putString("modulesInfo", responseContent);
		sharedPrefsEditor.commit();
		
		runParseModules();
	}
	
	public void runParseModules() {
		try {
			JSONObject responseObject = new JSONObject(modulesInfo);
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
				
				JSONArray anns = obj.getJSONArray("Announcements");
				if (anns.length() > 0) {
					modulesAnnouncementsList.add(anns);
				} else {
					modulesAnnouncementsList.add(null);
				}
			}
			
			createPageContents();
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
	@Override
	protected void createPageContents() {
		
		LinearLayout layoutAnnouncements = (LinearLayout) findViewById(R.id.Layout_announcements);
		
		
		
		// FOR TESTING PURPOSES ONLY
		for (int h = 0; h < 10; h++) {
		// THIS WILL DUPLICATE MODULES 4 TIMES TO SIMULATE MANY MODULES
		
			
		for (int i = 0; i < numOfModules; i++) {
			JSONArray announcements = modulesAnnouncementsList.get(i);
			if (announcements != null) {
				
				LinearLayout containerForModule = (LinearLayout) View.inflate(this, R.layout.container_announcements_module, null);
				layoutAnnouncements.addView(containerForModule);
				
				TextView containerName = (TextView) findViewById(R.id.TextView_announcements_module_name);
				containerName.setText(modulesCodeList.get(i));
				
				LinearLayout containerForAnnouncements = (LinearLayout) findViewById(R.id.Layout_announcements_module_announcements);
				if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR1) {
					containerName.setId(View.generateViewId());
					containerForAnnouncements.setId(View.generateViewId());
				} else {
					containerName.setId(i);
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
			} else { // announcements == null : no annoucements
				// TODO make a textview that says "No Announcements"
			}
		}
		
		pd.dismiss();
		
		// THE TESTING } IS HERE
		}
		// THE TESTING } IS HERE
	}

	
	
	// MAJOR TODO: set the clicklisterner to open the announcements contents. format popup in xml.
	// TODO: add a refresh button! (gradebook also)
	
}
