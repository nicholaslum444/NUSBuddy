package com.nick.nusbuddy;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
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
	
	final String DATE_FORMAT = "EEE, dd MMM yyyy";
	final String TIME_FORMAT = "h:mm a";

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
		LinearLayout layoutTest = (LinearLayout) findViewById(R.id.Layout_test_quizzes);
		
		for (int i = 0; i < numOfModules; i++) {
			String moduleCode = modulesCodeList.get(i);
			if (moduleCode != null) {
				
				View.inflate(this, R.layout.container_test_quizzes_module, layoutTest);
				LinearLayout containerForModule = (LinearLayout) layoutTest.findViewById(R.id.Layout_test_quizzes_module);
				
				TextView containerName = (TextView) containerForModule.findViewById(R.id.TextView_test_quizzes_module_name);
				LinearLayout containerForItems = (LinearLayout) containerForModule.findViewById(R.id.Layout_test_quizzes_module_items);
				
				containerName.setText(moduleCode);
				containerName.setTag("moduleCode");
				
				ArrayList<EventTest> thisModuleTests = database.getAllEventTestsFrom(moduleCode);
				
				for (int j = 0; j < thisModuleTests.size(); j++) {
					EventTest test = thisModuleTests.get(j);
					
					View.inflate(this,  R.layout.container_test_quizzes_module_item, containerForItems);
					LinearLayout layoutItem = (LinearLayout) containerForItems.findViewById(R.id.Layout_test_quizzes_module_item);
					
					TextView itemTitle = (TextView) layoutItem.findViewById(R.id.TextView_test_quizzes_item_title);
					itemTitle.setText(test.getTitle());
					
					long unixTime = test.getUnixTime();
					Calendar cal = Calendar.getInstance();
					cal.setTimeInMillis(unixTime);
					SimpleDateFormat sdfDate = new SimpleDateFormat(DATE_FORMAT, Locale.US);
					SimpleDateFormat sdfTime = new SimpleDateFormat(TIME_FORMAT, Locale.US);
					
					TextView timeField = (TextView) layoutItem.findViewById(R.id.TextView_test_quizzes_item_dueTime);
					TextView dateField = (TextView) layoutItem.findViewById(R.id.TextView_test_quizzes_item_dueDate);
					dateField.setText(sdfDate.format(cal.getTime()));
					
					if (test.isOnlyDateSet()) {
						timeField.setVisibility(View.INVISIBLE);
					} else {
						timeField.setText(sdfTime.format(cal.getTime()));
					}
					
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
						layoutItem.setId(View.generateViewId());
					} else {
						layoutItem.setId(new Random().nextInt(Integer.MAX_VALUE));
					}
				}
				
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
					containerForModule.setId(View.generateViewId());
				} else {
					containerForModule.setId(new Random().nextInt(Integer.MAX_VALUE));
				}
			} else {
				
			}
			
			pd.dismiss();
		}
		
		
	}
	
	public void addTest(View v) {
		Intent addIntent = new Intent(TestsQuizzes.this, AddTest.class);
		LinearLayout ll = (LinearLayout) v.getParent();
		TextView tv = (TextView) ll.findViewById(R.id.TextView_test_quizzes_module_name);
		String moduleCode = tv.getText().toString();
	    addIntent.putExtra("moduleCode", moduleCode);
	    startActivityForResult(addIntent, REQUEST_CODE_FOR_ADD);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_FOR_ADD) {
			Log.w("onact", "jjj");
			refreshContents();
		} else if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_FOR_VIEW) {
			if (data.getExtras().getBoolean("changed")) {
				refreshContents();
			}
		}
	}
	
	public void refreshContents() {
		super.onCreate(null);
		createPageContents();
	}
	
	
	
}
