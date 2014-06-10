package com.nick.nusbuddy;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.json.JSONException;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class AddHomework extends Activity {
	
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
	
	EditText eventTitleEditText;
	EditText eventLocationEditText;
	EditText descriptionEditText;
	
	TextView dueDateTextView;
	TextView dueTimeTextView;
	
	int mYear, mMonth, mDay, mHour, mMinute;
	
	CheckBox recurCheckBox;
	
	RadioGroup recurRadioGroup;
	RadioButton recurWeeklyRadioButton;
	RadioButton recurOddWeekButton;
	RadioButton recurEvenWeekButton;
	
	Button addEventButton;
	Button cancelButton;
	Button editButton;
	
	Event eventToEdit;
	
	NUSBuddyDatabaseHelper database;
	
	Intent incomingIntent;
	
	
	public final static String EXTRA_MESSAGE = "com.nick.nusbuddy.addhomework.MESSAGE";
	
	final String DATE_FIELD_FORMAT = "EEE, d MMM yyyy";
	final String TIME_FIELD_FORMAT = "h:mm a";
	final SimpleDateFormat SDF_DATE_FIELD = new SimpleDateFormat(DATE_FIELD_FORMAT, Locale.US);
	final SimpleDateFormat SDF_TIME_FIELD = new SimpleDateFormat(TIME_FIELD_FORMAT, Locale.US);
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contents_add_homework);
		
		database = new NUSBuddyDatabaseHelper(this);
		
		dtf = new DateTimeInfo();
		
		eventTitleEditText = (EditText) findViewById(R.id.eventTitleEditText);
		eventLocationEditText = (EditText) findViewById(R.id.eventLocationEditText);
		descriptionEditText = (EditText) findViewById(R.id.descriptionEditText);
		
		dueDateTextView = (TextView) findViewById(R.id.dueDateTextView);
		dueTimeTextView = (TextView) findViewById(R.id.dueTimeTextView);
		
		recurCheckBox = (CheckBox) findViewById(R.id.checkBoxRecur);
		recurRadioGroup = (RadioGroup) findViewById(R.id.RadioGroupReccur);
		recurWeeklyRadioButton = (RadioButton) findViewById(R.id.RadioButtonRecurWeekly);
		recurOddWeekButton = (RadioButton) findViewById(R.id.RadioButtonOddWeek);
		recurEvenWeekButton = (RadioButton) findViewById(R.id.RadioButtonEvenWeek);
		
		recurCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					recurRadioGroup.setVisibility(View.VISIBLE);
				} else {
					recurRadioGroup.setVisibility(View.GONE);
				}
			}
		});
		
		addEventButton = (Button) findViewById(R.id.addEventButton);
		cancelButton = (Button) findViewById(R.id.cancelButton);
		editButton = (Button) findViewById(R.id.editEventButton);
		
		incomingIntent = this.getIntent();
		if (incomingIntent.getExtras().containsKey("moduleCode")) {
			String moduleCode = incomingIntent.getExtras().getString("moduleCode");
			this.setTitle(moduleCode);
		}
		
		// if Editing mode
		if (getIntent().getExtras().getBoolean("edit")) {
			
			// kill the add button, so only the edit button is visible
			addEventButton.setVisibility(View.GONE);
			// retrieve the event string
			String eventString = getIntent().getExtras().getString("event");
			
			try {
				eventToEdit = new Event(eventString);
				
				// logic to fill up the details
				
				eventTitleEditText.setText(eventToEdit.getTitle());
				eventLocationEditText.setText(eventToEdit.getLocation());
				descriptionEditText.setText(eventToEdit.getDescription());
				
				//get formatted date and time
				
				long unixTime = eventToEdit.getUnixTime();
				Calendar cal = Calendar.getInstance();
		    	cal.setTimeInMillis(unixTime);
		    	
		    	dtf.year = cal.get(Calendar.YEAR);
	            dtf.month = cal.get(Calendar.MONTH);
	            dtf.day = cal.get(Calendar.DAY_OF_MONTH);
	            dtf.hour = cal.get(Calendar.HOUR_OF_DAY);
	            dtf.minute = cal.get(Calendar.MINUTE);
	            
	            dueDateTextView.setText(SDF_DATE_FIELD.format(cal.getTime()));
            	dtf.dateSet = true;
	            
            	if (!eventToEdit.isOnlyDateSet()) { // time is set
            		// fill up the time field
    	            
		    		String dateTimeString = SDF_TIME_FIELD.format(cal.getTime());
			    	dueTimeTextView.setText(dateTimeString);
			    	dtf.timeSet = true;
		    	}
            	
				
				//check checkboxes and radio buttons
				if (eventToEdit.isRecur()) {
					
					recurCheckBox.setChecked(true);
					recurRadioGroup.setVisibility(View.VISIBLE);
					
					if (eventToEdit.isRecurWeekly()) {
						recurWeeklyRadioButton.setChecked(true);
						
					} else if (eventToEdit.isRecurEvenWeek()) {
						recurEvenWeekButton.setChecked(true);
						
					} else {
						recurOddWeekButton.setChecked(true);
						
					}
				} else {
					recurCheckBox.setChecked(false);
				}
				
				// end logic
				
				
			} catch (JSONException e) { // poblem with creating event
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else { // not in edit mode, means in normal add mode
			// kill the edit button, only left the add button
			editButton.setVisibility(View.GONE);
		}
	}
	
	private void showTitleEmptyError() {
		eventTitleEditText.setError("Required fields");
	}
	
	private void showDateEmptyError() {
		dueDateTextView.setError("Required fields");
	}
	
	private void showInvalidTimeError() {
		//Toast.makeText(this, "Invalid due date", Toast.LENGTH_LONG).show();
		dueTimeTextView.setError("Invalid due date");
	}
	
	public Event putInformationIntoEvent(Intent output) throws InvalidTimeException, EmptyTitleFieldException, EmptyDateFieldException {
		Event event = new Event();
		String eventTitle = eventTitleEditText.getText().toString(); 
		String eventLocation = eventLocationEditText.getText().toString();
		
		//Log.w("loc", eventLocation);
		
		String date = dueDateTextView.getText().toString();
		String description = descriptionEditText.getText().toString();
		
		Calendar c = Calendar.getInstance();
		if (dtf.timeSet) {
			c.set(dtf.year, dtf.month, dtf.day, dtf.hour, dtf.minute);
		} else {
			c.set(dtf.year, dtf.month, dtf.day, 23, 59);
		}
		long unixTimeValue = c.getTimeInMillis();
		
		if (eventTitle == null || eventTitle.length() == 0) {
			throw new EmptyTitleFieldException();
			
		} else if (date == null || date.length() == 0) {
			throw new EmptyDateFieldException();
			
		} else if (unixTimeValue < System.currentTimeMillis() && dtf.timeSet) {
			throw new InvalidTimeException();
			
		} else {
			event.setModule(output.getExtras().getString("moduleCode"));
			event.setTitle(eventTitle);
			event.setLocation(eventLocation);
			event.setDescription(description);
			event.setUnixTime(unixTimeValue);
			event.setOnlyDateSet(dueTimeTextView.getText().length() == 0);
			
			if (recurCheckBox.isChecked()) {
				RadioButton rb = (RadioButton)findViewById(recurRadioGroup.getCheckedRadioButtonId());
				switch (rb.getId()) {
				case R.id.RadioButtonRecurWeekly:
					output.putExtra("recurWeekly", true);
					event.setRecurWeekly(true);
					break;
				case R.id.RadioButtonEvenWeek:
					output.putExtra("recurEvenWeek", true);
					event.setRecurEvenWeek(true);
					break;
				case R.id.RadioButtonOddWeek:
					output.putExtra("recurOddWeek", true);
					event.setRecurOddWeek(true);
					break;
				}
			}
		}
		return event;
	}
	
	// EDIT BUTTON
	public void editEvent(View v) {
		Intent output = this.getIntent();
		Event event;
		try {
			event = putInformationIntoEvent(output);
			
			event.setId(eventToEdit.getId());
			database.updateEvent(event);
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
	
	public void addEvent(View v) {
		Intent output = this.getIntent();
		Event event;
		try {
			event = putInformationIntoEvent(output);
			
			database.addEvent(event);
			
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
	
	public void cancelEvent(View v) {
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
     
        DatePickerDialog dpd = new DatePickerDialog(AddHomework.this, dpdl, dtf.year, dtf.month, dtf.day);
        
        dpd.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        
        dpd.show();
	}

	
	public void showTimePickerDialog(View v) {
		
		if (!dtf.timeSet) {
        	Calendar c = Calendar.getInstance();
            dtf.hour = c.get(Calendar.HOUR);
            dtf.minute = c.get(Calendar.MINUTE);
        }

        // Launch Time Picker Dialog
        TimePickerDialog.OnTimeSetListener tpdl =  new TimePickerDialog.OnTimeSetListener() {
       	 
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            	
                	
                //Displaying noon and midnight
            	/*if (hourOfDay == 0 || hourOfDay == 12) {
            		hourOfDay = 12;
            	}*/
            	
            
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
        
        TimePickerDialog tpd = new TimePickerDialog(AddHomework.this, tpdl, dtf.hour, dtf.minute, false);
        
        // making a "clear" button to unset the time
        tpd.setButton(TimePickerDialog.BUTTON_NEUTRAL, (CharSequence)"Clear", new DialogInterface.OnClickListener() {
        	
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
		getMenuInflater().inflate(R.menu.add_homework, menu);
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
