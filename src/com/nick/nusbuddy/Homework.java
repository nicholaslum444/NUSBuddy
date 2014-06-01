package com.nick.nusbuddy;


import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;


import android.os.Build;
import android.os.Bundle;
import android.text.Spanned;
import android.view.View;
import android.view.View.OnClickListener;
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

public class Homework extends BaseActivity implements ModulesAsyncTaskListener{
	
	
	private static final int REQUEST_CODE = 1;
	
	private QuickAction mQuickAction;
	
	private SharedPreferences sharedPrefs;
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
		createQuickActionBar();
		
		Toast.makeText(this, ""+Build.VERSION.SDK_INT +" "+ Build.VERSION_CODES.JELLY_BEAN_MR1, Toast.LENGTH_LONG).show();
		
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
		
		LinearLayout layoutHomework = (LinearLayout) findViewById(R.id.Layout_homework);
		
		
		// FOR TESTING PURPOSES ONLY
		for (int h = 0; h < 3; h++) {
		// THIS WILL DUPLICATE MODULES X TIMES TO SIMULATE MANY MODULES
		
		for (int i = 0; i < numOfModules; i++) {
			String moduleCode = modulesCodeList.get(i);
			if (moduleCode != null) {
				
				View.inflate(this, R.layout.container_homework_module, layoutHomework);
				
				TextView containerName = (TextView) findViewById(R.id.TextView_homework_module_name);
				containerName.setText(moduleCode);
				
				LinearLayout containerForModule = (LinearLayout) findViewById(R.id.Layout_homework_module);
				LinearLayout containerForItems = (LinearLayout) findViewById(R.id.Layout_homework_module_items);
				
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
					containerName.setId(View.generateViewId());
					containerForModule.setId(View.generateViewId());
					containerForItems.setId(View.generateViewId());
				} else {
					containerName.setId(new Random().nextInt(Integer.MAX_VALUE));
					containerForModule.setId(new Random().nextInt(Integer.MAX_VALUE));
					containerForItems.setId(new Random().nextInt(Integer.MAX_VALUE));
				}
				containerName.setText(""+containerForModule.getId());
				
				
			} else { 
				
			}
		}
		
		// testing
		}
		// testing
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
	    	
	    	//check whether they fill in time
	    	//get the must input fields out
	    	String title = data.getExtras().getString("eventTitle"); 
	    	
	    	int day = data.getExtras().getInt("mDay");
	    	int month = data.getExtras().getInt("mMonth");
	    	int year = data.getExtras().getInt("mYear");
	    	int hour = data.getExtras().getInt("mHour"); 
	    	int minute = data.getExtras().getInt("mMinute"); 
	    	
	    	
	    	// changed to calendar object.
	    	SimpleDateFormat sdfDay = new SimpleDateFormat("EEE", Locale.US);
	    	SimpleDateFormat sdfTime = new SimpleDateFormat("h:mma", Locale.US);
	    	Calendar cal = Calendar.getInstance();
	    	cal.set(year, month, day, hour, minute);
	    	String date = sdfDay.format(cal.getTime());
	    	String time = sdfTime.format(cal.getTime());
	    	
	    	String description = data.getExtras().getString("description");
	    	String location = data.getExtras().getString("eventLocation"); 
	    	
	    	//show title and date, time if applicable
	    	String result = title + " by " + date + ", " + time;
	    	
	    	TextView t = new TextView(this);
	    	t.setText(result);	            	
	    	int layoutId = data.getExtras().getInt("viewId");
	    	Toast.makeText(this, ""+layoutId, Toast.LENGTH_LONG).show();
	    	LinearLayout layout = (LinearLayout) findViewById(layoutId);
	    	if (layout == null) {
	    		Toast.makeText(this, ""+"layoutnull", Toast.LENGTH_LONG).show();
	    	} else {
	    		layout.addView(t, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
	    	}
	    	
	    }
	}
	
 	void createQuickActionBar() {
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
	            	Toast.makeText(Homework.this, ""+viewId, Toast.LENGTH_LONG).show();
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
 	
 	public void showQuickActionBar(View view) {
 		Toast.makeText(this, ""+view.getId(), Toast.LENGTH_LONG).show();
 		mQuickAction.show(view);
 	}
 	
}
