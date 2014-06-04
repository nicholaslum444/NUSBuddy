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
	
	
	private static final int REQUEST_CODE = 1;
	private static final int ADD_ITEM_CODE = 0;
	private static final int VIEW_ITEMS_CODE = 1;
	
	private QuickAction mQuickAction;
	
	private SharedPreferences sharedPrefs;
	private Editor sharedPrefsEditor;
	
	private String apiKey;
	private String userId;
	private String authToken;
	private String modulesInfo;
	private int numOfModules;

	private ArrayList<String> modulesCodeList;
	
	//NUSBuddySQLiteOpenHelper db;
	NUSBuddyDatabaseHelper database;

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
		
		//db = new NUSBuddySQLiteOpenHelper(this);
		database = new NUSBuddyDatabaseHelper(this);
		
		//Toast.makeText(this, ""+Build.VERSION.SDK_INT +" "+ Build.VERSION_CODES.JELLY_BEAN_MR1, Toast.LENGTH_LONG).show();
		
		sharedPrefs = getSharedPreferences("NUSBuddyPrefs", MODE_PRIVATE);
		sharedPrefsEditor = sharedPrefs.edit();
		
		apiKey = getString(R.string.api_key_mine);
		userId = sharedPrefs.getString("userId", null);
		authToken = sharedPrefs.getString("authToken", null);
		modulesInfo = sharedPrefs.getString("modulesInfo", null);
		
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
		
		// get a list of all events in the database
		ArrayList<Event> allEvents = database.getAllEvents();
		Log.w("events", allEvents.toString());
		
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
				
				// call it the name of the module
				TextView containerName = (TextView) findViewById(R.id.TextView_homework_module_name);
				containerName.setText(moduleCode);
				// setting a tag to keep track of the module code textview since we change its id later
				containerName.setTag("moduleCode");
				
				// pointers to the internal layouts
				LinearLayout containerForModule = (LinearLayout) findViewById(R.id.Layout_homework_module);
				LinearLayout containerForItems = (LinearLayout) findViewById(R.id.Layout_homework_module_items);
				
				ArrayList<Event> thisModuleEvents = database.getAllEvents(moduleCode);
				Log.w("this mods", thisModuleEvents.toString());
				// for each event under this module
				for (int j = 0; j < thisModuleEvents.size(); j++) {
					Event event = thisModuleEvents.get(j);
					
					// add a textview for the new item
			    	View.inflate(this, R.layout.textview_homework_item_small, containerForItems);
			    	
			    	// getting the time object
			    	long unixTime = event.getUnixTime();
			    	Calendar cal = Calendar.getInstance();
			    	cal.setTimeInMillis(unixTime);
			    	SimpleDateFormat sdf;
			    	if (event.isOnlyDateSet()) {
			    		sdf = new SimpleDateFormat("EEE", Locale.US);
			    	} else {
			    		sdf = new SimpleDateFormat("EEE, h:mm a", Locale.US);
			    	}
			    	String dateTimeString = sdf.format(cal.getTime());
			    	
			    	// check recur
			    	if (event.isRecurWeekly()) {
			    		dateTimeString = "every " + dateTimeString;
			    	} else if (event.isRecurEvenWeek()) {
			    		dateTimeString = "every even " + dateTimeString;
			    	} else if (event.isRecurOddWeek()) {
			    		dateTimeString = "every odd " + dateTimeString;
			    	}
			    	
			    	// display the item text
			    	TextView t = (TextView) findViewById(R.id.TextView_homework_item_small);
			    	t.setText(event.getTitle() + ", due " + dateTimeString);
			    	//t.setText(event.getTitle());
			    	
			    	// change the ID of the new item
			    	if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
			    		t.setId(View.generateViewId());
					} else {
						t.setId(new Random().nextInt(Integer.MAX_VALUE));
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
		}
		
		// testing
		//}
		// testing
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
	    	
	    	//check whether they fill in time
	    	//get the must input fields out
	    	/*String title = data.getExtras().getString("eventTitle"); 
	    	
	    	String module = data.getExtras().getString("moduleCode");
	    	
	    	int day = data.getExtras().getInt("mDay");
	    	int month = data.getExtras().getInt("mMonth");
	    	int year = data.getExtras().getInt("mYear");
	    	int hour = data.getExtras().getInt("mHour"); 
	    	int minute = data.getExtras().getInt("mMinute"); 
	    	
	    	
	    	// changed to calendar object.
	    	SimpleDateFormat sdfDay = new SimpleDateFormat("EEE", Locale.US);
	    	SimpleDateFormat sdfTime = new SimpleDateFormat("h:mma", Locale.US);
	    	Calendar cal = Calendar.getInstance();
	    	//cal.set(year, month, day, hour, minute);
	    	cal.setTimeInMillis(data.getExtras().getLong("unixTime"));
	    	String date = sdfDay.format(cal.getTime());
	    	String time = sdfTime.format(cal.getTime());
	    	
	    	cal.setTimeInMillis(data.getExtras().getLong("unixTime"));
	    	
	    	String description = data.getExtras().getString("description");
	    	String location = data.getExtras().getString("eventLocation"); 
	    	
	    	String eventString = data.getExtras().getString("eventString");
	    	
	    	//show title and date, time if applicable
	    	String result = title + " by " + date + ", " + time + " " + module;
	    	
	    	// get the module container
	    	int layoutId = data.getExtras().getInt("viewId");
	    	LinearLayout layout = (LinearLayout) findViewById(layoutId);
	    	
	    	// add a textview for the new item
	    	View.inflate(this, R.layout.textview_homework_item_small, layout);
	    	
	    	// display the item text
	    	TextView t = (TextView) findViewById(R.id.TextView_homework_item_small);
	    	t.setText(result);
	    	
	    	// change the ID of the new item
	    	if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
	    		t.setId(View.generateViewId());
			} else {
				t.setId(new Random().nextInt(Integer.MAX_VALUE));
			}*/
	    	
	    	//Toast.makeText(this, ""+layoutId, Toast.LENGTH_LONG).show();
	    	
	    	// trying with the db
	    	refreshContents();
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
	            
	            switch (actionId) {
	            
	            case ADD_ITEM_CODE:
	            	//Toast.makeText(getApplicationContext(), "Add item selected", Toast.LENGTH_SHORT).show();
	                
	            	LinearLayout anchor = (LinearLayout) mQuickAction.mAnchor;
	            	TextView textViewModuleCode = (TextView) anchor.findViewWithTag("moduleCode");
	            	String moduleCode = textViewModuleCode.getText().toString();
	            	
	            	int viewId = anchor.getId();
	            	//Toast.makeText(Homework.this, ""+viewId, Toast.LENGTH_LONG).show();
	            	
	                Intent addIntent = new Intent(Homework.this, AddHomework.class);
	                
	        	    addIntent.putExtra("viewId", viewId);
	        	    addIntent.putExtra("moduleCode", moduleCode);
	        	    
	        	    startActivityForResult(addIntent, REQUEST_CODE);
	        	    
	        	    break;
	        	    
	            case VIEW_ITEMS_CODE:
	            	//Toast.makeText(getApplicationContext(), actionItem.getTitle() + " selected", Toast.LENGTH_SHORT).show();
	            	
	            	Intent viewIntent = new Intent(Homework.this, ViewHomework.class);
	            	startActivity(viewIntent);
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
 		database.deleteAllEvents();
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
