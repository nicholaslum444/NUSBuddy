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


import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Spanned;
import android.util.Log;
import android.view.MenuItem;
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

public class Homework extends BaseActivity implements ModulesAsyncTaskListener {
	
	private static final int REQUEST_CODE_FOR_VIEW = 2;
	private static final int REQUEST_CODE_FOR_ADD = 1;
	private static final int ADD_ITEM_CODE = 0;
	private static final int VIEW_ITEMS_CODE = 1;
	
	private QuickAction mQuickAction;
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
		return R.layout.contents_homework;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		createQuickActionBar();
		
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
		
		LinearLayout layoutHomework = (LinearLayout) findViewById(R.id.Layout_homework);
		
		// FOR TESTING PURPOSES ONLY
		//for (int h = 0; h < 3; h++) {
		// THIS WILL DUPLICATE MODULES X TIMES TO SIMULATE MANY MODULES
		
		// for each module in ivle
		for (int i = 0; i < numOfModules; i++) {
			String moduleCode = modulesCodeList.get(i);
			if (moduleCode != null) {
				
				// create a container for that module
				View.inflate(this, R.layout.container_homework_module, layoutHomework);
				LinearLayout containerForModule = (LinearLayout) layoutHomework.findViewById(R.id.Layout_homework_module);
				
				// pointers to the internal layouts
				TextView containerName = (TextView) containerForModule.findViewById(R.id.TextView_homework_module_name);
				LinearLayout containerForItems = (LinearLayout) containerForModule.findViewById(R.id.Layout_homework_module_items);
				
				// call it the name of the module
				containerName.setText(moduleCode);
				// setting a tag to keep track of the module code textview since we change its id later
				containerName.setTag("moduleCode");
				
				ArrayList<EventHomework> thisModuleHomeworks = database.getAllEventHomeworksFrom(moduleCode);
				Log.w("this mods", thisModuleHomeworks.toString());
				// for each homework under this module
				for (int j = 0; j < thisModuleHomeworks.size(); j++) {
					EventHomework homework = thisModuleHomeworks.get(j);
					
					View.inflate(this, R.layout.container_homework_module_item, containerForItems);
					LinearLayout layoutItem = (LinearLayout) containerForItems.findViewById(R.id.Layout_homework_module_item);
					
					TextView itemTitle = (TextView) layoutItem.findViewById(R.id.TextView_homework_item_title);
					itemTitle.setText(homework.getTitle());
					
					long unixTime = homework.getUnixTime();
					Calendar cal = Calendar.getInstance();
			    	cal.setTimeInMillis(unixTime);
			    	SimpleDateFormat sdf;
			    	
			    	if (homework.isOnlyDateSet()) {
			    		if (homework.isRecurWeekly()) {
			    			sdf = new SimpleDateFormat(DATE_FORMAT_RECUR_WEEKLY, Locale.US);
				    	} else if (homework.isRecurEvenWeek()) {
				    		sdf = new SimpleDateFormat(DATE_FORMAT_RECUR_EVEN, Locale.US);
				    	} else if (homework.isRecurOddWeek()) {
				    		sdf = new SimpleDateFormat(DATE_FORMAT_RECUR_ODD, Locale.US);
				    	} else {
				    		sdf = new SimpleDateFormat(DATE_FORMAT_NO_RECUR, Locale.US);
				    	}
			    	} else {
			    		if (homework.isRecurWeekly()) {
			    			sdf = new SimpleDateFormat(DATE_TIME_FORMAT_RECUR_WEEKLY, Locale.US);
				    	} else if (homework.isRecurEvenWeek()) {
				    		sdf = new SimpleDateFormat(DATE_TIME_FORMAT_RECUR_EVEN, Locale.US);
				    	} else if (homework.isRecurOddWeek()) {
				    		sdf = new SimpleDateFormat(DATE_TIME_FORMAT_RECUR_ODD, Locale.US);
				    	} else {
				    		sdf = new SimpleDateFormat(DATE_TIME_FORMAT_NO_RECUR, Locale.US);
				    	}
			    	}
			    	
			    	String dateTimeString = sdf.format(cal.getTime());
			    	
			    	TextView itemDue = (TextView) layoutItem.findViewById(R.id.TextView_homework_item_due);
			    	itemDue.setText(dateTimeString);
			    	
			    	// change the ID of the new item
			    	if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
			    		layoutItem.setId(View.generateViewId());
					} else {
						layoutItem.setId(new Random().nextInt(Integer.MAX_VALUE));
					}
				}
				
				// changing the IDs so that we can uniquely identify each module
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
					containerName.setId(View.generateViewId());
					containerForModule.setId(View.generateViewId());
					containerForItems.setId(View.generateViewId());
				} else {
					containerName.setId(new Random().nextInt(Integer.MAX_VALUE));
					containerForModule.setId(new Random().nextInt(Integer.MAX_VALUE));
					containerForItems.setId(new Random().nextInt(Integer.MAX_VALUE));
				}
				
				
			} else { // module code == null
				
			}
			
			pd.dismiss();
		}
		
		// testing
		//}
		// testing
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_FOR_ADD) {
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
	
 	void createQuickActionBar() {
	 	ActionItem addItem      = new ActionItem(ADD_ITEM_CODE, "Add", getResources().getDrawable(R.drawable.ic_add));
	    ActionItem acceptItem   = new ActionItem(VIEW_ITEMS_CODE, "View All", getResources().getDrawable(R.drawable.ic_up));
	    //ActionItem uploadItem   = new ActionItem(2, "Upload", getResources().getDrawable(R.drawable.ic_up));
	
	    //use setSticky(true) to disable QuickAction dialog being dismissed after an item is clicked
	    //uploadItem.setSticky(true);
	
	    mQuickAction  = new QuickAction(this);
	
	    mQuickAction.addActionItem(addItem);
	    mQuickAction.addActionItem(acceptItem);
	
	    //setup the action item click listener
	    mQuickAction.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {
	        @Override
	        public void onItemClick(QuickAction quickAction, int pos, int actionId) {
	            ActionItem actionItem = quickAction.getActionItem(pos);
	            
	            LinearLayout anchor = (LinearLayout) mQuickAction.mAnchor;
            	TextView textViewModuleCode = (TextView) anchor.findViewWithTag("moduleCode");
            	String moduleCode = textViewModuleCode.getText().toString();
	            
	            switch (actionId) {
	            
	            case ADD_ITEM_CODE:
	            	//Toast.makeText(getApplicationContext(), "Add item selected", Toast.LENGTH_SHORT).show();
	            	
	            	int viewId = anchor.getId();
	            	//Toast.makeText(Homework.this, ""+viewId, Toast.LENGTH_LONG).show();
	            	
	                Intent addIntent = new Intent(Homework.this, AddHomework.class);
	        	    addIntent.putExtra("moduleCode", moduleCode);
	        	    
	        	    startActivityForResult(addIntent, REQUEST_CODE_FOR_ADD);
	        	    
	        	    break;
	        	    
	            case VIEW_ITEMS_CODE:
	            	//Toast.makeText(getApplicationContext(), actionItem.getTitle() + " selected", Toast.LENGTH_SHORT).show();
	            	
	            	Intent viewIntent = new Intent(Homework.this, ViewHomework.class);
	            	viewIntent.putExtra("moduleCode", moduleCode);
	            	startActivityForResult(viewIntent, REQUEST_CODE_FOR_VIEW);
	            	break;
	            	
	            }
	        }
	    });
	
	    mQuickAction.setOnDismissListener(new QuickAction.OnDismissListener() {
	        @Override
	        public void onDismiss() {
	            //Toast.makeText(getApplicationContext(), "Ups..dismissed", Toast.LENGTH_SHORT).show();
	        }
	    });
 	}
 	
 	public void showQuickActionBar(View view) {
 		
 		mQuickAction.show(view);
 	}
 	
 	public void clear(View v) {
 		database.deleteAllEventHomeworks();
 		refreshContents();
 	}
	
 	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			clear(null);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
 	
}
