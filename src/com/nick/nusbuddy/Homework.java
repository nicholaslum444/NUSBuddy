package com.nick.nusbuddy;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class Homework extends BaseActivity {
	
	
	private static final int REQUEST_CODE = 1;
	
	QuickAction mQuickAction;
	SharedPreferences sharedPrefs;

	private Editor sharedPrefsEditor;

	private String apiKey;

	private String userId;

	private String authToken;

	private String modulesInfo;

	private int numOfModules;

	private ArrayList<String> modulesCodeList;

	@Override
	protected Activity getCurrentActivity() {
		return this;
	}

	@Override
	protected int getCurrentActivityLayout() {
		return R.layout.contents_homework;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		createqa();
		
		sharedPrefs = getSharedPreferences("NUSBuddyPrefs", MODE_PRIVATE);
		sharedPrefsEditor = sharedPrefs.edit();
		
		apiKey = getString(R.string.api_key_mine);
		userId = sharedPrefs.getString("userId", null);
		authToken = sharedPrefs.getString("authToken", null);
		modulesInfo = sharedPrefs.getString("modulesInfo", null);
		
		if (modulesInfo == null) {
			//runGetModules();
		} else {
			//Log.d("modules", modulesInfo);
			runParseModules();
		}
		
		
	}

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
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
	    	
	    	//check whether they fill in time
	    	//get the must input fields out
	    	String title = data.getExtras().getString("eventTitle"); 
	    	int day = data.getExtras().getInt("mDay");
	    	int month = data.getExtras().getInt("mMonth");
	    	
	    	String location = data.getExtras().getString("eventLocation"); 
	    	int hour = data.getExtras().getInt("mHour"); 
	    	int minute = data.getExtras().getInt("mMinute"); 
	    	
	    	String result = "Title: " + title + "location: " + location 
	    				+ "day: " + day + "month: " + month + "hour: " + hour 
	    				+ "minute" + minute;
	    	Toast.makeText(Homework.this, result, Toast.LENGTH_LONG).show();
	    	TextView t = new TextView(this);
	    	t.setText(result);
	    	//t.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));	            	
	    	int layoutId = data.getExtras().getInt("viewId");
	    	LinearLayout layout = (LinearLayout) findViewById(layoutId);
	    	//layout.addView(t);
	    	layout.addView(t, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
	    	layout.setVisibility(View.VISIBLE);
	    	
	    	
	    	/*
	    	if (data.hasExtra("taskThenDate")) { 
	            String result = data.getExtras().getString("taskThenDate");
	            if (result != null && result.length() > 0) {
	            	TextView t = new TextView(this);
	            	t.setText(result);
	            	t.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));	            	
	            	int layoutId = data.getExtras().getInt("layoutId");
	            	LinearLayout layout = (LinearLayout) findViewById(layoutId);
	            	layout.addView(t);
	            }
	        }
	        */
	    }
	}
	
 	void createqa() {
	 	ActionItem addItem      = new ActionItem(0, "Add", getResources().getDrawable(R.drawable.ic_add));
	    ActionItem acceptItem   = new ActionItem(1, "View", getResources().getDrawable(R.drawable.ic_accept));
	    //ActionItem uploadItem   = new ActionItem(2, "Upload", getResources().getDrawable(R.drawable.ic_up));
	
	    //use setSticky(true) to disable QuickAction dialog being dismissed after an item is clicked
	    //uploadItem.setSticky(true);
	
	    mQuickAction  = new QuickAction(this);
	
	    mQuickAction.addActionItem(addItem);
	    mQuickAction.addActionItem(acceptItem);
	    //mQuickAction.addActionItem(uploadItem);
	
	    //setup the action item click listener
	    mQuickAction.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {
	        @Override
	        public void onItemClick(QuickAction quickAction, int pos, int actionId) {
	            ActionItem actionItem = quickAction.getActionItem(pos);
	
	            if (actionId == 0) {
	                
	            	Toast.makeText(getApplicationContext(), "Add item selected", Toast.LENGTH_SHORT).show();
	                
	            	int viewId = mQuickAction.mAnchor.getId();
	                Intent intent = new Intent(Homework.this, AddHomework.class);
	        	    intent.putExtra("viewId", viewId);
	        	    startActivityForResult(intent, REQUEST_CODE);
	            	
	            } else {
	               
	            	Toast.makeText(getApplicationContext(), actionItem.getTitle() + " selected", Toast.LENGTH_SHORT).show();
	            	
	            	Intent intent = new Intent(Homework.this, ViewHomework.class);
	            	startActivity(intent);

;	            }
	        }
	    });
	
	    mQuickAction.setOnDismissListener(new QuickAction.OnDismissListener() {
	        @Override
	        public void onDismiss() {
	            Toast.makeText(getApplicationContext(), "Ups..dismissed", Toast.LENGTH_SHORT).show();
	        }
	    });
 	}
 	
 	public void call_quick_action_bar(View view) {
 		mQuickAction.show(view);
 	}
 	
}
