package com.nick.nusbuddy;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.net.ConnectivityManager;
import android.os.Bundle;
import android.widget.Toast;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class TestsQuizzes extends BaseActivity implements ModulesAsyncTaskListener {

	private static final int REQUEST_CODE_FOR_VIEW = 2;
	private static final int REQUEST_CODE_FOR_ADD = 1;
	private static final int ADD_ITEM_CODE = 0;
	private static final int VIEW_ITEMS_CODE = 1;
	
	//private QuickAction mQuickAction;
	private ProgressDialog pd;
	
	private SharedPreferences sharedPrefs;
	private Editor sharedPrefsEditor;
	
	private String apiKey;
	private String userId;
	private String authToken;
	private String modulesInfo;
	private int numOfModules;

	private ArrayList<String> modulesCodeList;
	
	//NUSBuddySQLiteOpenHelper db;
	private NUSBuddyDatabaseHelper database;
	
	final String DATE_FORMAT_NO_RECUR = "EEE, dd MMM yyyy";
	final String DATE_FORMAT_RECUR_WEEKLY = "'Every' EEE";
	final String DATE_FORMAT_RECUR_EVEN = "'Every even' EEE";
	final String DATE_FORMAT_RECUR_ODD = "'Every odd' EEE";
	final String DATE_TIME_FORMAT_NO_RECUR = "EEE, dd MMM yyyy 'at' h:mm a";
	final String DATE_TIME_FORMAT_RECUR_WEEKLY = "'Every' EEE 'at' h:mm a";
	final String DATE_TIME_FORMAT_RECUR_EVEN = "'Every even' EEE 'at' h:mm a";
	final String DATE_TIME_FORMAT_RECUR_ODD = "'Every odd' EEE 'at' h:mm a";

	@Override
	protected Activity getCurrentActivity() {
		return this;
	}

	@Override
	protected int getCurrentActivityLayout() {
		return R.layout.contents_test_quizzes;
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//createQuickActionBar();
		
		database = new NUSBuddyDatabaseHelper(this);
		
		//Toast.makeText(this, ""+Build.VERSION.SDK_INT +" "+ Build.VERSION_CODES.JELLY_BEAN_MR1, Toast.LENGTH_LONG).show();
		
		sharedPrefs = getSharedPreferences("NUSBuddyPrefs", MODE_PRIVATE);
		sharedPrefsEditor = sharedPrefs.edit();
		
		apiKey = getString(R.string.api_key_mine);
		userId = sharedPrefs.getString("userId", null);
		authToken = sharedPrefs.getString("authToken", null);
		modulesInfo = sharedPrefs.getString("modulesInfo", null);
		
		pd = new ProgressDialog(this);
		pd.setMessage("Retrieving items");
		pd.show();
		
		if (modulesInfo == null) {
			runGetModules();
		} else {
			runParseModules();
		}
	}
	
	public boolean hasInternetConnection() {
	    ConnectivityManager localConnectivityManager = (ConnectivityManager)getSystemService("connectivity");
	    return (localConnectivityManager.getActiveNetworkInfo() != null) && 
	    		(localConnectivityManager.getActiveNetworkInfo().isAvailable()) && 
	    		(localConnectivityManager.getActiveNetworkInfo().isConnected());
	}
	
	public void runGetModules() {
		if (!hasInternetConnection()) {
			Toast.makeText(this, "Please check your Wifi or 3G connection", Toast.LENGTH_LONG).show();
			return;
		}
		new GetModulesAsyncTask(this).execute(apiKey, authToken, userId);
	}
	
	@Override
	public void onModulesTaskComplete(String responseContent) {
		Toast.makeText(this, responseContent, Toast.LENGTH_LONG).show();
		modulesInfo = responseContent;
		sharedPrefsEditor.putString("modulesInfo", responseContent);
		sharedPrefsEditor.commit();
		
		runParseModules();
	}
	
	/**
	 * Just wanna get the module codes only.
	 *  
	 */
	public void runParseModules() {
		
		modulesCodeList = new ArrayList<String>();
		
		try {
			JSONObject responseObject = new JSONObject(modulesInfo);
			JSONArray modulesArray = responseObject.getJSONArray("Results");
			numOfModules = modulesArray.length();
			
			for (int i = 0; i < numOfModules; i++) {
				modulesCodeList.add(modulesArray.getJSONObject(i).getString("CourseCode"));
			}
			
			createPageContents();
			
		} catch (JSONException e) {
			e.printStackTrace();
			
		}
	}

	@Override
	protected void createPageContents() {
		
	}

}
