package com.nick.nusbuddy;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Random;

import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class ViewHomework extends Activity {
	
	ArrayList<Event> thisModuleItems;
	
	String moduleCode;
	
	NUSBuddyDatabaseHelper database;
	
	private static final int REQUEST_CODE_FOR_EDIT = 3;
	private static final int REQUEST_CODE_FOR_ADD = 1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.contents_view_homework);
		
		Intent incomingIntent = this.getIntent();
		if (incomingIntent.getExtras().containsKey("moduleCode")) {
			moduleCode = incomingIntent.getExtras().getString("moduleCode");
			this.setTitle(moduleCode);
		}
		
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

	
	
	public void expandItem(View v) {
		Log.w("vis", "exp");
		LinearLayout hiddenLayout = (LinearLayout) v.findViewById(R.id.Layout_homework_item_hidden);
		ImageView img = (ImageView) v.findViewById(R.id.Image_expand_collapse);
		if (hiddenLayout.getVisibility() == View.VISIBLE) {
			Log.w("vis", "vis");
			hiddenLayout.setVisibility(View.GONE);
			img.setRotation(0);
		} else {
			Log.w("vis", "gon");
			hiddenLayout.setVisibility(View.VISIBLE);
			img.setRotation(180);
			
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
	    		dateTimeString = "Every " + dateTimeString;
	    	} else if (event.isRecurEvenWeek()) {
	    		dateTimeString = "Every even " + dateTimeString;
	    	} else if (event.isRecurOddWeek()) {
	    		dateTimeString = "Every odd " + dateTimeString;
	    	}
	    	
	    	TextView itemDue = (TextView) convertView.findViewById(R.id.TextView_homework_item_due);
	    	itemDue.setText(dateTimeString);
	    	
	    	LinearLayout hiddenTextViews = (LinearLayout) convertView.findViewById(R.id.Layout_homework_item_hidden_textviews);
	    	
	    	if (event.getLocation() != null && event.getLocation().length() > 0) {
	    		TextView tt = (TextView) convertView.findViewById(R.id.Layout_homework_item_hidden_location);
	    		tt.setText("Location: " + event.getLocation());
	    		tt.setVisibility(View.VISIBLE);
	    		Log.w("location", event.getTitle());
	    	}
	    	
	    	if (event.getDescription() != null && event.getDescription().length() > 0) {
	    		TextView dd = (TextView) convertView.findViewById(R.id.Layout_homework_item_hidden_description);
	    		dd.setText("Description: " + event.getDescription());
	    		dd.setVisibility(View.VISIBLE);
	    		Log.w("desctp", event.getTitle());
	    	}
            
            // end logic
            
            // Resets the toolbar to be closed
            hiddenLayout.setVisibility(View.GONE);

            return convertView;
        }
    }
	
	public void refreshContents() {
		/*startActivity(new Intent(this, ViewHomework.class).putExtra("moduleCode", moduleCode).putExtra("changed", true));
		finish();*/
		onCreate(null);
	}
	
	public void deleteItem(final View v) { // shows an alert
		AlertDialog.Builder b = new AlertDialog.Builder(this);
		b.setCancelable(true);
		b.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				deleteItemConfirm(v);
			}
		});
		b.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		b.setTitle(((TextView) ((LinearLayout) v.getParent().getParent().getParent().getParent()).findViewById(R.id.TextView_homework_item_title)).getText().toString());
		b.setMessage("Delete item?");
		b.create().show();
	}
	
	public void deleteItemConfirm(View v) {
		Event e = (Event) ((View) v.getParent().getParent().getParent().getParent()).getTag();
		database.deleteEvent(e);
		getIntent().putExtra("changed", true);
		refreshContents();
	}
	
	public void editItem(View v) {
		Event e = (Event) ((View) v.getParent().getParent().getParent().getParent()).getTag();
		getIntent().putExtra("edit", true);
		getIntent().putExtra("event", e.toString());
		Intent i = new Intent(this, AddHomework.class);
		i.putExtras(getIntent());
		startActivityForResult(i, REQUEST_CODE_FOR_EDIT);
	}
	
	public void addItem() {
		Intent intent = new Intent(this, AddHomework.class);
		intent.putExtra("moduleCode", moduleCode);
		startActivityForResult(intent, REQUEST_CODE_FOR_ADD);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (resultCode == RESULT_OK && (requestCode == REQUEST_CODE_FOR_EDIT || requestCode == REQUEST_CODE_FOR_ADD)) {
	    	getIntent().putExtra("changed", true);
	    	refreshContents();
	    }
	}
	
	@Override
	public void onBackPressed() {
		setResult(RESULT_OK, getIntent());
		super.onBackPressed();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.view_homework, menu);
		return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        // ActionBarDrawerToggle will take care of this.
		
		int itemId = item.getItemId();
		switch(itemId) {
		case R.id.action_add_button:
			addItem();
			break;
		}
       
       return super.onOptionsItemSelected(item);
   }

}
