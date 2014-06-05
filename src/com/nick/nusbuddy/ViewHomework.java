package com.nick.nusbuddy;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Random;

import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class ViewHomework extends Activity {
	
	ArrayList<Event> thisModuleItems;
	NUSBuddyDatabaseHelper database;
	private static final int REQUEST_CODE_FOR_EDIT = 3;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.contents_view_homework);
		
		database = new NUSBuddyDatabaseHelper(this);
		
		String moduleCode = getIntent().getExtras().getString("moduleCode");
		thisModuleItems = database.getAllEvents(moduleCode);
		
		LinearLayout layoutViewHomework = (LinearLayout) findViewById(R.id.Layout_view_homework);
		
		ListView list = (ListView)findViewById(R.id.ListView_homework_items);

        // Creating the list adapter and populating the list
        ArrayAdapter<Event> listAdapter = new EventListAdapter(this, R.layout.container_view_homework_item);
		
		for (Event event : thisModuleItems) {
			listAdapter.add(event);
		}
		
		list.setAdapter(listAdapter);
		
		// Creating an item click listener, to open/close our toolbar for each item
        /*list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
            	Log.w("vis", "clik");
                expandItem(view);
                
                // Creating the expand animation for the item
                //ExpandAnimation expandAni = new ExpandAnimation(toolbar, 500);

                // Start the animation on the toolbar
                //toolbar.startAnimation(expandAni);
            }
        });*/
		
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.view_homework, menu);
		return true;
	}
	
	public void expandItem(View v) {
		Log.w("vis", "exp");
		LinearLayout hiddenLayout = (LinearLayout) v.findViewById(R.id.Layout_homework_item_hidden);
		if (hiddenLayout.getVisibility() == View.VISIBLE) {
			Log.w("vis", "vis");
			hiddenLayout.setVisibility(View.GONE);
		} else {
			Log.w("vis", "gon");
			hiddenLayout.setVisibility(View.VISIBLE);
		}
	}
	
	class EventListAdapter extends ArrayAdapter<Event> {

        public EventListAdapter(Context context, int textViewResourceId) {
            super(context, textViewResourceId);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.container_view_homework_item, null);
            }
            
            // logic for filling up the event details
            Event event = getItem(position);
            convertView.setTag(event);
            
			LinearLayout hiddenLayout = (LinearLayout) convertView.findViewById(R.id.Layout_homework_item_hidden);
			hiddenLayout.setTag("hiddenLayout");
			
			TextView itemTitle = (TextView) convertView.findViewById(R.id.TextView_homework_item_title);
			itemTitle.setText(event.getTitle());
			
			long unixTime = event.getUnixTime();
			Calendar cal = Calendar.getInstance();
	    	cal.setTimeInMillis(unixTime);
	    	SimpleDateFormat sdf;
	    	// TODO set different text for recur vs no recur
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
	    	
	    	TextView itemDue = (TextView) convertView.findViewById(R.id.TextView_homework_item_due);
	    	itemDue.setText(dateTimeString);
	    	
	    	LinearLayout hiddenTextViews = (LinearLayout) convertView.findViewById(R.id.Layout_homework_item_hidden_textviews);
	    	
	    	if (event.getLocation() != null && event.getLocation().length() > 0) {
	    		TextView tt = new TextView(getContext(), null, android.R.attr.textAppearanceMedium);
	    		tt.setText(event.getLocation());
	    		hiddenTextViews.addView(tt);
	    	}
	    	
	    	if (event.getDescription() != null && event.getDescription().length() > 0) {
	    		TextView dd = new TextView(getContext(), null, android.R.attr.textAppearanceMedium);
	    		dd.setText(event.getDescription());
	    		hiddenTextViews.addView(dd);
	    	}
            
            // end logic
            

            // Resets the toolbar to be closed
            hiddenLayout.setVisibility(View.GONE);

            return convertView;
        }
    }
	
	public void refreshContents() {
		onCreate(null);
	}
	
	public void deleteItem(View v) {
		Event e = (Event) ((View) v.getParent().getParent().getParent()).getTag();
		database.deleteEvent(e);
		refreshContents();
		getIntent().putExtra("changed", true);
	}
	
	public void editItem(View v) {
		Event e = (Event) ((View) v.getParent().getParent().getParent()).getTag();
		getIntent().putExtra("edit", true);
		getIntent().putExtra("event", e.toString());
		Intent i = new Intent(this, AddHomework.class);
		i.putExtras(getIntent());
		startActivityForResult(i, REQUEST_CODE_FOR_EDIT);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_FOR_EDIT) {
	    	getIntent().putExtra("changed", true);
	    	refreshContents();
	    } 
	}
	
	@Override
	public void onBackPressed() {
		setResult(RESULT_OK, getIntent());
		super.onBackPressed();
	}

}
