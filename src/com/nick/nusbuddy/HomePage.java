package com.nick.nusbuddy;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.Bundle;
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
        
    	// uses the base activity to create the layout, drawer, everything.
    	super.onCreate(savedInstanceState);
        
        context = this;
        sharedPrefs = getSharedPreferences("NUSBuddyPrefs", MODE_PRIVATE);
        sharedPrefsEditor = sharedPrefs.edit();
        
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
    	
    	
    	// set target cap and required cap
    	// TODO
    	
    	
    	// TODO set homework list
    	LinearLayout homeworkListLayout = (LinearLayout) findViewById(R.id.Layout_home_page_homework_list);
    	ArrayList<String> homeworkList = new ArrayList<String>();
    	homeworkList.add("cs1231 tutorial 3");
    	homeworkList.add("asdasd tutoal");
    	homeworkList.add("stuff");
    	for (int i = 0; i < homeworkList.size(); i++) {
    		TextView newHomework = new TextView(context, null, android.R.attr.textAppearanceMedium);
    		newHomework.setGravity(Gravity.CENTER_HORIZONTAL);
    		newHomework.setTextColor(Color.parseColor("#046380"));
    		newHomework.setText(homeworkList.get(i));
    		homeworkListLayout.addView(newHomework, i);
    	}
    	
    	
    	// TODO set quizzes list
    	LinearLayout testsListLayout = (LinearLayout) findViewById(R.id.Layout_home_page_tests_list);
    	ArrayList<String> testsList = new ArrayList<String>();
    	testsList.add("cs1231 tutorial 3");
    	testsList.add("asdasd tutasdasoal");
    	testsList.add("stufftests");
    	for (int i = 0; i < testsList.size(); i++) {
    		TextView newTest = new TextView(context, null, android.R.attr.textAppearanceMedium);
    		newTest.setGravity(Gravity.CENTER_HORIZONTAL);
    		newTest.setTextColor(Color.parseColor("#046380"));
    		newTest.setText(testsList.get(i));
    		testsListLayout.addView(newTest, i);
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
    
    // for the testing button there. goes into app without login.
 	public void next(View v) {
     	Intent i = new Intent(this, Login.class);
     	startActivity(i); 
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
 		NUSBuddyDatabaseHelper db = new NUSBuddyDatabaseHelper(this);
 		db.onUpgrade(db.getWritableDatabase(), 1, 1);
 	}
 	
 	
    
}