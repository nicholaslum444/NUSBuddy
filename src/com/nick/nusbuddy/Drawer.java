package com.nick.nusbuddy;

//import com.nick.nusbuddy.HomePage.DrawerItemClickListener;

import android.app.Activity;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class Drawer {
	
	/* The click listener for ListView in the navigation drawer */
    static class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

	private static String[] drawerItemNames;
	private static DrawerLayout drawerLayout;
	private static ListView drawerList;
	private static ActionBarDrawerToggle drawerToggle;
	protected static String activityTitle;

	public Drawer() {
	}
	
	public static void setup(final Activity context) {
		drawerItemNames = context.getResources().getStringArray(R.array.drawer_item_names);
        drawerLayout = (DrawerLayout) context.findViewById(R.id.Layout_drawer);
        drawerList = (ListView) context.findViewById(R.id.left_drawer);

        // set a custom shadow that overlays the main content when the drawer opens
        drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener
        drawerList.setAdapter(new ArrayAdapter<String>(context, R.layout.drawer_list_item, drawerItemNames));
        drawerList.setOnItemClickListener(new DrawerItemClickListener());
        

        // enable ActionBar app icon to behave as action to toggle nav drawer
        context.getActionBar().setDisplayHomeAsUpEnabled(true);
        context.getActionBar().setHomeButtonEnabled(true);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        drawerToggle = new ActionBarDrawerToggle(
                context,                  /* host Activity */
                drawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
                ) {
            public void onDrawerClosed(View view) {
                context.getActionBar().setTitle(activityTitle);
                context.invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
            	context.getActionBar().setTitle(R.string.drawer_name);
            	context.invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        
        drawerLayout.setDrawerListener(drawerToggle);
	}
	
	
    
    private static void selectItem(int position) {
        // update the main content by replacing fragments
        /*Fragment fragment = new PlanetFragment();
        Bundle args = new Bundle();
        //args.putInt(PlanetFragment.ARG_PLANET_NUMBER, position);
        fragment.setArguments(args);

        FragmentManager fragmentManager = getFragmentManager();
        //fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

        // update selected item and title, then close the drawer
        drawerList.setItemChecked(position, true);*/
        //setTitle(drawerItemNames[position]);
    	
    	
        drawerLayout.closeDrawer(drawerList);
    }

}
