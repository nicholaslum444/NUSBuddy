package com.nick.nusbuddy;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import org.json.JSONException;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class AddTest extends Activity {
	
	public class DateTimeInfo {
		int day, month, year, hour, minute;
		long todayDate;
		boolean dateSet, timeSet;
	}
	
	public class EmptyTitleFieldException extends Exception {
		private static final long serialVersionUID = -885526777237707415L;
	}
	
	public class InvalidTimeException extends Exception {
		private static final long serialVersionUID = -7070245316521346702L;
	}
	
	public class EmptyDateFieldException extends Exception {
		private static final long serialVersionUID = 919582192965705223L;
		
	}
	
	DateTimeInfo dtf;
	
	EditText testTitleEditText;
	EditText testLocationEditText;
	EditText testDescriptionEditText;
	
	TextView dueDateTextView;
	TextView dueTimeTextView;
	
	int mYear, mMonth, mDay, mHour, mMinute;
	
	Button addButton;
	Button cancelButton;
	Button editButton;
	
	EventTest testToEdit;
	
	NUSBuddyDatabaseHelper database;
	
	Intent incomingIntent;
	
	public final static String EXTRA_MESSAGE = "com.nick.nusbuddy.addtest.MESSAGE";
	
	final String DATE_FIELD_FORMAT = "EEE, d MMM yyyy";
	final String TIME_FIELD_FORMAT = "h:mm a";
	final SimpleDateFormat SDF_DATE_FIELD = new SimpleDateFormat(DATE_FIELD_FORMAT, Locale.US);
	final SimpleDateFormat SDF_TIME_FIELD = new SimpleDateFormat(TIME_FIELD_FORMAT, Locale.US);
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contents_add_test);
		
		database = new NUSBuddyDatabaseHelper(this);
		
		dtf = new DateTimeInfo();
		
		testTitleEditText = (EditText) findViewById(R.id.testTitleEditText);
		testLocationEditText = (EditText) findViewById(R.id.testLocationEditText);
		testDescriptionEditText = (EditText) findViewById(R.id.testDescriptionEditText);
		
		dueDateTextView = (TextView) findViewById(R.id.dueDateTextView);
		dueTimeTextView = (TextView) findViewById(R.id.dueTimeTextView);
		
		addButton = (Button) findViewById(R.id.addButton);
		cancelButton = (Button) findViewById(R.id.cancelButton);
		editButton = (Button) findViewById(R.id.editButton);
		
		incomingIntent = this.getIntent();
		if (incomingIntent.getExtras().containsKey("moduleCode")) {
			String moduleCode = incomingIntent.getExtras().getString("moduleCode");
			this.setTitle(moduleCode);
		}
		
		// if Editing mode
		if (getIntent().getExtras().getBoolean("edit")) {
			
			// kill the add button, so only the edit button is visible
			addButton.setVisibility(View.GONE);
			// retrieve the test string
			String testString = getIntent().getExtras().getString("test");
			Log.w("test string", testString);
			try {
				testToEdit = new EventTest(testString);
				
				// logic to fill up the details
				testTitleEditText.setText(testToEdit.getTitle());
				testLocationEditText.setText(testToEdit.getLocation());
				testDescriptionEditText.setText(testToEdit.getDescription());
				
				//get formatted date and time
				
				long unixTime = testToEdit.getUnixTime();
				Calendar cal = Calendar.getInstance();
		    	cal.setTimeInMillis(unixTime);
		    	
		    	dtf.year = cal.get(Calendar.YEAR);
	            dtf.month = cal.get(Calendar.MONTH);
	            dtf.day = cal.get(Calendar.DAY_OF_MONTH);
	            dtf.hour = cal.get(Calendar.HOUR_OF_DAY);
	            dtf.minute = cal.get(Calendar.MINUTE);
	            
	            dueDateTextView.setText(SDF_DATE_FIELD.format(cal.getTime()));
            	dtf.dateSet = true;
	            
            	if (!testToEdit.isOnlyDateSet()) { // time is set
            		// fill up the time field
    	            
		    		String dateTimeString = SDF_TIME_FIELD.format(cal.getTime());
			    	dueTimeTextView.setText(dateTimeString);
			    	dtf.timeSet = true;
		    	}
				
				// end logic
				
				
			} catch (JSONException e) { // poblem with creating test
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else { // not in edit mode, means in normal add mode
			// kill the edit button, only left the add button
			editButton.setVisibility(View.GONE);
		}
	}
	
	private void showTitleEmptyError() {
		testTitleEditText.setError("Required field");
	}
	
	private void showDateEmptyError() {
		dueDateTextView.setError("Required field");
	}
	
	private void showInvalidTimeError() {
		Toast.makeText(this, "Invalid due date", Toast.LENGTH_LONG).show();
		dueTimeTextView.setError("Invalid due date");
	}
	
	public EventTest putInformationIntoEvent(Intent output) throws InvalidTimeException, EmptyTitleFieldException, EmptyDateFieldException {
		EventTest test = new EventTest();
		String testTitle = testTitleEditText.getText().toString();
		String testLocation = testLocationEditText.getText().toString();
		String date = dueDateTextView.getText().toString();
		String description = testDescriptionEditText.getText().toString();
		
		Calendar c = Calendar.getInstance();
		if (dtf.timeSet) {
			c.set(dtf.year, dtf.month, dtf.day, dtf.hour, dtf.minute);
		} else {
			c.set(dtf.year, dtf.month, dtf.day, 23, 59);
		}
		long unixTimeValue = c.getTimeInMillis();
		
		if (testTitle == null || testTitle.length() == 0) {
			throw new EmptyTitleFieldException();
			
		} else if (date == null || date.length() == 0) {
			throw new EmptyDateFieldException();
			
		} else if (unixTimeValue < System.currentTimeMillis() && dtf.timeSet) {
			throw new InvalidTimeException();
			
		} else {
			test.setModule(output.getExtras().getString("moduleCode"));
			test.setTitle(testTitle);
			test.setLocation(testLocation);
			test.setDescription(description);
			test.setUnixTime(unixTimeValue);
			test.setOnlyDateSet(dueTimeTextView.getText().length() == 0);
			
		}
		
		return test;
	}
	
	// EDIT BUTTON
	public void editButtonPressed(View v) {
		Intent output = this.getIntent();
		EventTest test;
		try {
			test = putInformationIntoEvent(output);
			
			test.setId(testToEdit.getId());
			database.updateEventTest(test);
			output.putExtra("changed", true);
			
			setResult(RESULT_OK, output);
			finish();
			
		} catch (InvalidTimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			showInvalidTimeError();
			
		} catch (EmptyTitleFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			showTitleEmptyError();
			
		} catch (EmptyDateFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			showDateEmptyError();
			
		}
	}
	
	public void addButtonPressed(View v) {
		Intent output = this.getIntent();
		EventTest test;
		try {
			test = putInformationIntoEvent(output);
			
			database.addEventTest(test);
			
			setResult(RESULT_OK, output);
			finish();
			
		} catch (InvalidTimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			showInvalidTimeError();
			
		} catch (EmptyTitleFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			showTitleEmptyError();
			
		} catch (EmptyDateFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			showDateEmptyError();
			
		}
	}
	
	public void cancelButtonPressed(View v) {
		finish();
	}
	
	public void showDatePickerDialog(View v) {
        
        if (!dtf.dateSet) {
        	Calendar c = Calendar.getInstance();
            dtf.year = c.get(Calendar.YEAR);
            dtf.month = c.get(Calendar.MONTH);
            dtf.day = c.get(Calendar.DAY_OF_MONTH);
            
        }
        
        //Day, date, month, year format
	
        // Launch Date Picker Dialog
        DatePickerDialog.OnDateSetListener dpdl = new DatePickerDialog.OnDateSetListener() {
       	 
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                // Display Selected date in textbox
            	Calendar c = Calendar.getInstance();
            	c.set(Calendar.YEAR, year);
            	c.set(Calendar.MONTH, monthOfYear);
            	c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            	dueDateTextView.setText(SDF_DATE_FIELD.format(c.getTime()));
            	dtf.year = year;
            	dtf.month = monthOfYear;
            	dtf.day = dayOfMonth;
            	dtf.dateSet = true;
            }
        };
     
        DatePickerDialog dpd = new DatePickerDialog(AddTest.this, dpdl, dtf.year, dtf.month, dtf.day);
        
        dpd.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        
        dpd.show();
	}

	
	public void showTimePickerDialog(View v) {
		
		if (!dtf.timeSet) {
        	Calendar c = Calendar.getInstance();
            dtf.hour = c.get(Calendar.HOUR_OF_DAY);
            dtf.minute = c.get(Calendar.MINUTE);
        }

        // Launch Time Picker Dialog
        TimePickerDialog.OnTimeSetListener tpdl =  new TimePickerDialog.OnTimeSetListener() {
       	 
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            
            	// Display Selected time in textbox
            	Calendar c = Calendar.getInstance();
            	c.set(Calendar.HOUR_OF_DAY, hourOfDay);
            	c.set(Calendar.MINUTE, minute);
            	dueTimeTextView.setText(SDF_TIME_FIELD.format(c.getTime()));
            	dtf.hour = hourOfDay;
            	dtf.minute = minute;
            	dtf.timeSet = true;
            }
        };
        
        TimePickerDialog tpd = new TimePickerDialog(AddTest.this, tpdl, dtf.hour, dtf.minute, false);
        
        // making a "clear" button to unset the time
        tpd.setButton(TimePickerDialog.BUTTON_NEUTRAL, "Clear", new DialogInterface.OnClickListener() {
        	
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dtf.hour = 0;
				dtf.minute = 0;
				dtf.timeSet = false;
				dueTimeTextView.setText("");
				dialog.dismiss();
			}
			
        });
        
        tpd.show();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.add_test, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
