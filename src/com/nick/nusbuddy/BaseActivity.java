package com.nick.nusbuddy;

import java.util.*;

import android.os.*;
import android.app.*;
import android.content.*;
import android.content.res.*;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.*;
import android.support.v4.widget.*;
import android.view.*;
import android.widget.*;

public abstract class BaseActivity extends Activity {
	
	public BaseActivity() {	
		super();
	}
	
	private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    
    private String mDrawerTitle;
    private String mTitle;
    private String[] drawerItemNames;
    
    private Activity currentActivity;
    private int currentActivityLayout;
    
    // needs <?> because class requires parameterisation, the ? is a wildcard
	private ArrayList<Class<?>> drawerItems;
	private ScrollView layoutPageContent;
    
    protected abstract Activity getCurrentActivity();
    
    protected abstract int getCurrentActivityLayout();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		// adding the activity classes to the drawer so that they can be opened
		// must add in proper order (select item 0 opens activity at position 0)
		drawerItems = new ArrayList<Class<?>>();
		drawerItems.add(HomePage.class);
		drawerItems.add(Announcements.class);
		
		
		// the rest of the onCreate
		currentActivityLayout = getCurrentActivityLayout();
		currentActivity = getCurrentActivity();
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.drawer_layout);
		layoutPageContent = (ScrollView) findViewById(R.id.Layout_page_content);
    	View v = View.inflate(currentActivity, currentActivityLayout, null);
    	layoutPageContent.addView(v);
		
		
		mTitle = (String) currentActivity.getTitle();
		mDrawerTitle = getResources().getString(R.string.drawer_name);
        drawerItemNames = getResources().getStringArray(R.array.drawer_item_names);
        mDrawerLayout = (DrawerLayout) currentActivity.findViewById(R.id.Layout_drawer);
        mDrawerList = (ListView) currentActivity.findViewById(R.id.left_drawer);
        
        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        
        // set up the drawer's list view with items and click listener
        mDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, drawerItemNames));
        
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        // enable ActionBar app icon to behave as action to toggle nav drawer
        currentActivity.getActionBar().setDisplayHomeAsUpEnabled(true);
        currentActivity.getActionBar().setHomeButtonEnabled(true);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
        		currentActivity,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
                ) {
            public void onDrawerClosed(View view) {
            	currentActivity.getActionBar().setTitle(mTitle);
                //invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
            	currentActivity.getActionBar().setTitle(mDrawerTitle);
                //invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.base, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	/* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        //boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        //menu.findItem(R.id.action_websearch).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
         // The action bar home/up action should open or close the drawer.
         // ActionBarDrawerToggle will take care of this.
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        
        return super.onOptionsItemSelected(item);
    }

    /* The click listner for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    private void selectItem(int position) {
    	// switching activities when item is selected.
    	startActivity(new Intent(currentActivity, drawerItems.get(position)));
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = (String) title;
        currentActivity.getActionBar().setTitle(mTitle);
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }
    
}
