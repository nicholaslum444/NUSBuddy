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
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


public class HomePage extends BaseActivity {

	Context context;
    LinearLayout layoutPageContent;
    SharedPreferences sharedPrefs;
    Editor sharedPrefsEditor;
    
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
        
    	db = new NUSBuddyDatabaseHelper(this);
        
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
    	
    	
    	// TODO set target cap and required cap
    	
    	
    	// TODO allow user to change how many days of homrwork to show
    	
    	ArrayList<EventHomework> eventsDueSoon = db.getAllEventHomeworksBetween(System.currentTimeMillis(), System.currentTimeMillis()+86400000);
    	Collections.sort(eventsDueSoon, new UnixTimeComparator());
    	
    	LinearLayout homeworkListLayout = (LinearLayout) findViewById(R.id.Layout_home_page_homework_list);
    	int numOfHomework = eventsDueSoon.size();
    	for (int i = 0; i < 3; i++) {
    		
    		View.inflate(this, R.layout.container_homepage_homework_item, homeworkListLayout);
    		LinearLayout newHomeworkLayout = (LinearLayout) homeworkListLayout.findViewById(R.id.layout_homepage_homework_item);
    		
    		if (i >= numOfHomework) {
    			newHomeworkLayout.setVisibility(View.INVISIBLE);
    		
    		} else {
    			EventHomework e = eventsDueSoon.get(i);
    		
	    		TextView titleField = (TextView) newHomeworkLayout.findViewById(R.id.title);
	    		//TextView moduleField = (TextView) newHomeworkLayout.findViewById(R.id.modulename);
	    		titleField.setText(e.getTitle());
	    		//moduleField.setText(e.getModule());
	    		
	    		//TextView dueField = (TextView) newHomeworkLayout.findViewById(R.id.duedate);
	    		
	    		//Calendar c = Calendar.getInstance();
	    		//c.setTimeInMillis(e.getUnixTime());
	    		
	    		//if (e.isOnlyDateSet()) {
	    			//dueField.setText(sdfDateFormat.format(c.getTime()));
	    		//} else {
	    			//dueField.setText(sdfDateTimeFormat.format(c.getTime()));
	    		//}
	    		
    		}
    		newHomeworkLayout.setId(View.generateViewId());
    	}
    	if (eventsDueSoon.size() > 3) {
    		findViewById(R.id.TextView_home_page_homework_see_more).setVisibility(View.VISIBLE);
    	}
    	
    	
    	// TODO set quizzes list
    	ArrayList<EventTest> testsDueSoon = db.getAllEventTestsBetween(System.currentTimeMillis(), System.currentTimeMillis()+86400000);
    	Collections.sort(testsDueSoon, new UnixTimeComparator());
    	
    	LinearLayout testsListLayout = (LinearLayout) findViewById(R.id.Layout_home_page_tests_list);
    	int numOfTests = testsDueSoon.size();
    	for (int i = 0; i < 3; i++) {
    		
    		View.inflate(this, R.layout.container_homepage_test_item, testsListLayout);
    		LinearLayout newTestLayout = (LinearLayout) testsListLayout.findViewById(R.id.layout_homepage_test_item);
    		
    		if (i >= numOfTests) {
    			newTestLayout.setVisibility(View.INVISIBLE);
    		} else {
	    		
	    		EventTest e = testsDueSoon.get(i);
	    		
	    		TextView titleField = (TextView) newTestLayout.findViewById(R.id.title);
	    		//TextView moduleField = (TextView) newTestLayout.findViewById(R.id.modulename);
	    		titleField.setText(e.getTitle());
	    		//moduleField.setText(e.getModule());
	    		
	    		//TextView dueField = (TextView) newTestLayout.findViewById(R.id.duedate);
	    		
	    		//Calendar c = Calendar.getInstance();
	    		//c.setTimeInMillis(e.getUnixTime());
	    		
	    		//if (e.isOnlyDateSet()) {
	    			//dueField.setText(sdfDateFormat.format(c.getTime()));
	    		//} else {
	    			//dueField.setText(sdfDateTimeFormat.format(c.getTime()));
	    		//}
	    		
    		}
    		newTestLayout.setId(View.generateViewId());
    	}
    	if (eventsDueSoon.size() > 3) {
    		findViewById(R.id.TextView_home_page_test_see_more).setVisibility(View.VISIBLE);
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
 	
 	public void goToHomework(View v) {
 		Intent i = new Intent(this, Homework.class);
 		startActivity(i);
 	}
 	
 	public void goToTest(View v) {
 		Intent i = new Intent(this, TestsQuizzes.class);
 		startActivity(i);
 	}
 	
    
}