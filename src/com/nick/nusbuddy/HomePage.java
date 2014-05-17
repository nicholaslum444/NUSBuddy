package com.nick.nusbuddy;

import java.util.ArrayList;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

/**
 * This example illustrates a common usage of the DrawerLayout widget
 * in the Android support library.
 * <p/>
 * <p>When a navigation (left) drawer is present, the host activity should detect presses of
 * the action bar's Up affordance as a signal to open and close the navigation drawer. The
 * ActionBarDrawerToggle facilitates this behavior.
 * Items within the drawer should fall into one of two categories:</p>
 * <p/>
 * <ul>
 * <li><strong>View switches</strong>. A view switch follows the same basic policies as
 * list or tab navigation in that a view switch does not create navigation history.
 * This pattern should only be used at the root activity of a task, leaving some form
 * of Up navigation active for activities further down the navigation hierarchy.</li>
 * <li><strong>Selective Up</strong>. The drawer allows the user to choose an alternate
 * parent for Up navigation. This allows a user to jump across an app's navigation
 * hierarchy at will. The application should treat this as it treats Up navigation from
 * a different task, replacing the current task stack using TaskStackBuilder or similar.
 * This is the only form of navigation drawer that should be used outside of the root
 * activity of a task.</li>
 * </ul>
 * <p/>
 * <p>Right side drawers should be used for actions, not navigation. This follows the pattern
 * established by the Action Bar that navigation should be to the left and actions to the right.
 * An action should be an operation performed on the current contents of the window,
 * for example enabling or disabling a data overlay on top of the current content.</p>
 */
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
    	return R.layout.activity_home_page;
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        
    	// uses the base activity to create the layout, drawer, everything.
    	super.onCreate(savedInstanceState);
        
        context = this;
        sharedPrefs = getSharedPreferences("NUSBuddyPrefs", MODE_PRIVATE);
        
        createPageContents();
    }
    
    @Override
    protected void createPageContents() {
    	// TODO set welcome message morning evening etc and name
    	TextView welcomeNameView = (TextView) findViewById(R.id.TextView_home_page_welcome_name);
    	String userName = sharedPrefs.getString("userName", getResources().getString(R.string.TextView_welcome_name_default));
    	welcomeNameView.setText(userName);
    	
    	
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
    	
    	
    	// TODO set test list
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
}