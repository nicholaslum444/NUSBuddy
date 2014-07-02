package com.nick.nusbuddy;

import helpers.com.nick.nusbuddy.UnixTimeComparator;

import java.text.SimpleDateFormat;
import java.util.*;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


public class HomePage extends BaseActivity {

	Context context;
    LinearLayout layoutPageContent;
    SharedPreferences sharedPrefs;
    SharedPreferences sharedPrefsDefault;
    Editor sharedPrefsEditor;
    
    long currentTime;
    
    final String DATE_TIME_FORMAT = "EEE, h:mm a";
    final String DATE_FORMAT = "EEE";
    final SimpleDateFormat sdfDateTimeFormat = new SimpleDateFormat(DATE_TIME_FORMAT, Locale.US);
    final SimpleDateFormat sdfDateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.US);
    
    NUSBuddyDatabaseHelper db;
    
    @Override
	protected Activity getCurrentActivity() {
		return this;
	}
    
    @Override
    protected int getCurrentActivityLayout() {
    	return R.layout.contents_home_page;
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	
    	contentSetAlready = true;
    	// uses the base activity to create the layout, drawer, everything.
    	super.onCreate(savedInstanceState);
    	
    	DrawerLayout d = (DrawerLayout) findViewById(R.id.Layout_drawer);
    	d.removeViewAt(0);
    	View v = View.inflate(this, getCurrentActivityLayout(), null);
    	v.setBackground(getResources().getDrawable(R.drawable.landscape7));
    	d.addView(v, 0);
    	
        
        context = this;
        sharedPrefs = getSharedPreferences("NUSBuddyPrefs", MODE_PRIVATE);
        sharedPrefsEditor = sharedPrefs.edit();
        sharedPrefsDefault = PreferenceManager.getDefaultSharedPreferences(this);
        
        currentTime = System.currentTimeMillis();
        
    	db = new NUSBuddyDatabaseHelper(this);
    	
    	//
    	sharedPrefsEditor.remove("currentCap");
    	sharedPrefsEditor.remove("targetCap");
    	sharedPrefsEditor.commit();
    	//
        
        createqa();
        createPageContents();
        
    }
    
    
    @Override
    protected void createPageContents() {
    	// TODO set welcome message morning evening etc and name
    	TextView welcomeNameView = (TextView) findViewById(R.id.TextView_home_page_welcome_name);
    	String defaultName = getString(R.string.TextView_welcome_name_default);
    	String studentName = sharedPrefs.getString("studentName", defaultName);
    	welcomeNameView.setText(studentName);
    	
    	TextView textViewWelcomeMessage = (TextView) findViewById(R.id.TextView_home_page_welcome_message);
    	String message = getString(R.string.TextView_welcome_message_default);
    	
    	// choose the message based on the current time
    	// 4 <= h < 12 = morning
    	// 12 <= h < 17 = afternoon
    	// 17 <= h < 4 = evening
    	Calendar c = Calendar.getInstance();
    	int h = c.get(Calendar.HOUR_OF_DAY);
    	
    	if (4 <= h && h < 12) {
    		message = getString(R.string.TextView_welcome_message_morning);
    	} else if (12 <= h && h < 17) {
    		message = getString(R.string.TextView_welcome_message_afternoon);
    	} else if (17 <= h || (0 <= h && h < 4) ){
    		message = getString(R.string.TextView_welcome_message_evening);
    	}
    	
    	textViewWelcomeMessage.setText(message);
    	
    	// TODO set target cap and required cap
    	
    	// get all the relevant settings. 
    	sharedPrefs = getSharedPreferences("NUSBuddyPrefs", MODE_PRIVATE);
    	boolean showUpcoming = sharedPrefs.getBoolean("show_homework", false);
    	int numOfItemsToShow = Integer.parseInt(sharedPrefs.getString("number_of_items", "3"));
    	int numOfDaysHomework = Integer.parseInt(sharedPrefs.getString("homework_days", "3"));
    	int numOfDaysTests = Integer.parseInt(sharedPrefs.getString("test_days", "3"));
    	Log.w("sho", showUpcoming+"");
    	Log.w("num", numOfItemsToShow+"");
    	Log.w("numdaysh", numOfDaysHomework+"");
    	Log.w("numdayst", numOfDaysTests+"");
    	
    	LinearLayout layoutUpcoming = (LinearLayout) findViewById(R.id.Layout_todo_tostudy);
    	if (!showUpcoming) {
    		layoutUpcoming.setVisibility(View.INVISIBLE);
    	} else {
    		layoutUpcoming.setVisibility(View.VISIBLE);
    	}
    	
    	// TODO allow user to change how many days of homrwork to show
    	ArrayList<EventHomework> eventsDueSoon = db.getAllEventHomeworksBetween(System.currentTimeMillis(), System.currentTimeMillis() + (86400000 * numOfDaysHomework));
    	Collections.sort(eventsDueSoon, new UnixTimeComparator());
    	
    	LinearLayout homeworkListLayout = (LinearLayout) findViewById(R.id.Layout_home_page_homework_list);
    	homeworkListLayout.removeAllViews();
    	int numOfHomework = eventsDueSoon.size();
    	for (int i = 0; i < numOfItemsToShow; i++) {
    		
    		View.inflate(this, R.layout.container_homepage_homework_item, homeworkListLayout);
    		LinearLayout newHomeworkLayout = (LinearLayout) homeworkListLayout.findViewById(R.id.layout_homepage_homework_item);
    		
    		if (i >= numOfHomework) {
    			newHomeworkLayout.setVisibility(View.INVISIBLE);
    		
    		} else {
    			EventHomework e = eventsDueSoon.get(i);
    		
	    		TextView titleField = (TextView) newHomeworkLayout.findViewById(R.id.title);
	    		titleField.setText(e.getTitle());
	    		
    		}
    		newHomeworkLayout.setId(View.generateViewId());
    	}
    	if (numOfHomework > numOfItemsToShow) {
    		findViewById(R.id.TextView_home_page_homework_see_more).setVisibility(View.VISIBLE);
    	} else {
    		findViewById(R.id.TextView_home_page_homework_see_more).setVisibility(View.INVISIBLE);
    	}
    	
    	
    	// TODO set quizzes list
    	ArrayList<EventTest> testsDueSoon = db.getAllEventTestsBetween(System.currentTimeMillis(), System.currentTimeMillis() + (86400000 * numOfDaysTests));
    	Collections.sort(testsDueSoon, new UnixTimeComparator());
    	
    	LinearLayout testsListLayout = (LinearLayout) findViewById(R.id.Layout_home_page_tests_list);
    	testsListLayout.removeAllViews();
    	int numOfTests = testsDueSoon.size();
    	for (int i = 0; i < numOfItemsToShow; i++) {
    		
    		View.inflate(this, R.layout.container_homepage_test_item, testsListLayout);
    		LinearLayout newTestLayout = (LinearLayout) testsListLayout.findViewById(R.id.layout_homepage_test_item);
    		
    		if (i >= numOfTests) {
    			newTestLayout.setVisibility(View.INVISIBLE);
    		} else {
	    		
	    		EventTest e = testsDueSoon.get(i);
	    		
	    		TextView titleField = (TextView) newTestLayout.findViewById(R.id.title);
	    		titleField.setText(e.getTitle());
	    		
    		}
    		newTestLayout.setId(View.generateViewId());
    	}
    	if (numOfTests > numOfItemsToShow) {
    		findViewById(R.id.TextView_home_page_test_see_more).setVisibility(View.VISIBLE);
    	} else {
    		findViewById(R.id.TextView_home_page_test_see_more).setVisibility(View.INVISIBLE);
    	}
    	
    }
    
    
    // pressing the logout button on the screen.
    public void logout(View v) {
    	AlertDialog.Builder builder = new AlertDialog.Builder(context)
    									.setTitle("Log out")
    									.setMessage("Are you sure?")
    									.setPositiveButton("Log out", new DialogInterface.OnClickListener() {
    											public void onClick(DialogInterface dialog, int id) {
    												// User clicked OK button
    												dialog.cancel();
    										    	sharedPrefsEditor.clear();
    										    	sharedPrefsEditor.commit();
    												startActivity(new Intent(context, Login.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
    										    	((Activity) context).finish();
    											}
    									});
    								    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
    								    		public void onClick(DialogInterface dialog, int id) {
    								    			// User clicked cancel button
    								    			dialog.cancel();
    								    		}
    								    });
    	builder.create().show();
    	
    }
 	
 	
 	QuickAction mQuickAction;
 	
 	void createqa() {
	 	ActionItem addItem      = new ActionItem(0, "Add", getResources().getDrawable(R.drawable.ic_add));
	    ActionItem acceptItem   = new ActionItem(1, "Accept", getResources().getDrawable(R.drawable.ic_accept));
	    ActionItem uploadItem   = new ActionItem(2, "Upload", getResources().getDrawable(R.drawable.ic_up));
	
	    //use setSticky(true) to disable QuickAction dialog being dismissed after an item is clicked
	    uploadItem.setSticky(true);
	
	     mQuickAction  = new QuickAction(this);
	
	    mQuickAction.addActionItem(addItem);
	    mQuickAction.addActionItem(acceptItem);
	    mQuickAction.addActionItem(uploadItem);
	
	    //setup the action item click listener
	    mQuickAction.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {
	        @Override
	        public void onItemClick(QuickAction quickAction, int pos, int actionId) {
	            ActionItem actionItem = quickAction.getActionItem(pos);
	
	            if (actionId == 0) {
	                Toast.makeText(getApplicationContext(), "Add item selected", Toast.LENGTH_SHORT).show();
	            } else {
	                Toast.makeText(getApplicationContext(), actionItem.getTitle() + " selected", Toast.LENGTH_SHORT).show();
	            }
	        }
	    });
	
	    mQuickAction.setOnDismissListener(new QuickAction.OnDismissListener() {
	        @Override
	        public void onDismiss() {
	            Toast.makeText(getApplicationContext(), "Ups..dismissed", Toast.LENGTH_SHORT).show();
	        }
	    });
 	}
 	
 	public void openqa(View v) {
 		/*NUSBuddyDatabaseHelper db = new NUSBuddyDatabaseHelper(this);
 		db.onUpgrade(db.getWritableDatabase(), 1, 1);*/
 	}
 	
 	public void refreshContents() {
		createPageContents();
	}
 	
 	@Override
 	protected void onResume() {
 		super.onResume();
 		refreshContents();
 	}
 	
 	public void goToHomework(View v) {
 		Intent i = new Intent(this, Homework.class);
 		startActivity(i);
 		
 	}
 	
 	public void goToTest(View v) {
 		Intent i = new Intent(this, TestsQuizzes.class);
 		startActivity(i);
 		
 	}
 	
}