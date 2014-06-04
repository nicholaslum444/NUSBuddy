package com.nick.nusbuddy;

import java.util.Calendar;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
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

public class AddHomework extends Activity {
	
	public class DateTimeInfo {
		int day, month, year, hour, minute;
		boolean dateSet, timeSet;
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
	
	NUSBuddyDatabaseHelper database;
	
	
	public final static String EXTRA_MESSAGE = "com.nick.nusbuddy.addhomework.MESSAGE";
	
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
		
		addEventButton = (Button) findViewById(R.id.addEventButton);
		cancelButton = (Button) findViewById(R.id.cancelButton);
		
		
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
	}
	
	private void showError() {
		eventTitleEditText.setError("Required fields");
		dueDateTextView.setError("Required fields");
		
	}
	
	//how to alert for every odd week/ even week?
	
	public void addEvent(View v) {
		Event event = new Event();
		String eventTitle = eventTitleEditText.getText().toString(); 
		String eventLocation = eventLocationEditText.getText().toString(); 
		String date = dueDateTextView.getText().toString();
		String description = descriptionEditText.getText().toString();
		
		if (eventTitle == "" || date == "") {
			
			showError();
		
		} else {
			
			Intent output = this.getIntent();
			event.setModule(output.getExtras().getString("moduleCode"));
			event.setTitle(eventTitle);
			event.setLocation(eventLocation);
			event.setDescription(description);
			
			Calendar c = Calendar.getInstance();
			c.set(dtf.year, dtf.month, dtf.day, dtf.hour, dtf.minute);
			long unixTimeValue = c.getTimeInMillis();
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
			} else {
				
			}
			
			database.addEvent(event);
			
			setResult(RESULT_OK, output);
			finish();
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
            	dueDateTextView.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
            	dtf.year = year;
            	dtf.month = monthOfYear;
            	dtf.day = dayOfMonth;
            	dtf.dateSet = true;
            }
        };
     
        DatePickerDialog dpd = new DatePickerDialog(AddHomework.this, dpdl, dtf.year, dtf.month, dtf.day);
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
            	
            	//Calculate AM or PM
                String amOrPm = "";
                	if (hourOfDay == 0 || hourOfDay < 12) {
            		    amOrPm = " AM";  

            		} else {
            			hourOfDay = hourOfDay - 12;
            		    amOrPm = " PM";
            		}
                	
                //Displaying noon and midnight
                	if (hourOfDay == 0 || hourOfDay == 12) {
                		hourOfDay = 12;
                	}
                	
                //Display minute
                	String minuteString = "";
                	if (minute < 10) {
                		minuteString = "0" + minute;
                	
                	} else {
                		minuteString = Integer.valueOf(minute).toString();
                	}
                
            	// Display Selected time in textbox
            	dueTimeTextView.setText(hourOfDay + ":" + minuteString + amOrPm);
            	dtf.hour = hourOfDay;
            	dtf.minute = minute;
            	dtf.timeSet = true;
            }
        };
        
        TimePickerDialog tpd = new TimePickerDialog(AddHomework.this, tpdl, dtf.hour, dtf.minute, false);
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
