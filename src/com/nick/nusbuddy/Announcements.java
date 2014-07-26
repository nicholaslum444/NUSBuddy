package com.nick.nusbuddy;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Build;
import android.os.Bundle;
import android.text.Spanned;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class Announcements extends RefreshableActivity implements ModulesAsyncTaskListener {
	
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
	AlertDialog.Builder b;
	
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
	protected RefreshableActivity getCurrentRefreshableActivity() {
		return this;
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
		b = new AlertDialog.Builder(this);
		b.setCancelable(true);
		b.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		
		pd.setMessage("Getting announcements...");
		pd.show();
		
		boolean toRefreshAnnouncements = sharedPrefs.getBoolean("toRefreshAnnouncements", false);
		
		if (modulesInfo == null || toRefreshAnnouncements) {
			sharedPrefsEditor.putBoolean("toRefreshAnnouncements", false);
			sharedPrefsEditor.commit();
			runGetModules();
		} else {
			//Log.d("modules", modulesInfo);
			runParseModules();
		}
			
	}

	@Override
	protected void onRefresh() {
		pd.setMessage("Refreshing...");
		pd.show();
		super.onCreate(null);
		runGetModules();
	}
	
	public void runGetModules() {
		new GetModulesAsyncTask(this).execute(apiKey, authToken, userId);
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
		//for (int h = 0; h < 10; h++) {
		// THIS WILL DUPLICATE MODULES X TIMES TO SIMULATE MANY MODULES
		
			
		for (int i = 0; i < numOfModules; i++) {
			JSONArray announcements = modulesAnnouncementsList.get(i);
			if (announcements != null) {
				
				View.inflate(this, R.layout.container_announcements_module, layoutAnnouncements);
				
				TextView containerName = (TextView) findViewById(R.id.TextView_announcements_module_name);
				containerName.setText(modulesCodeList.get(i));
				
				LinearLayout containerForAnnouncements = (LinearLayout) findViewById(R.id.Layout_announcements_module_announcements);
				
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
					containerName.setId(View.generateViewId());
					containerForAnnouncements.setId(View.generateViewId());
				} else {
					containerName.setId(new Random().nextInt(Integer.MAX_VALUE));
					containerForAnnouncements.setId(new Random().nextInt(Integer.MAX_VALUE));
				}
				
				// for each announcement, add the announcement title into the container
				int numOfAnnouncements = announcements.length();
				for (int j = 0; j < numOfAnnouncements; j++) {
					try {
						JSONObject announcement = announcements.getJSONObject(j);
						
						View.inflate(this, R.layout.textview_announcements_title, containerForAnnouncements);
						TextView announcementTitle = (TextView) findViewById(R.id.TextView_announcements_title);
						
						final String title = announcement.getString("Title"); 
						String content = announcement.getString("Description");
						String unixTimeString = announcement.getString("CreatedDate");
						
						final Spanned contentFormatted = android.text.Html.fromHtml(content);

						// parsing the time
						long unixTimeLong = Long.parseLong(unixTimeString.substring(6, 19));
						SimpleDateFormat dateTimeFormat = new SimpleDateFormat("d MMM yyyy, HH:mm", Locale.ENGLISH);
						final String createdDate = dateTimeFormat.format(unixTimeLong);
						
						announcementTitle.setText(title);
						
						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
							announcementTitle.setId(View.generateViewId());
						} else {
							announcementTitle.setId(new Random().nextInt(Integer.MAX_VALUE));
						}
						//  set a clickListener so that user can open the announcement contents if she clicks on the title.
						announcementTitle.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								b.setTitle(title);
								b.setMessage(contentFormatted + "\n\nPosted on: " + createdDate);
								b.create().show();
							}
						});
						
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
		//}
		// THE TESTING } IS HERE
	}

	
	// TODO: add a refresh button! (gradebook also)
	
}
