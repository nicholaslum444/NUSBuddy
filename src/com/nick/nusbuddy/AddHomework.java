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
	
	EditText eventTitleEditText;
	EditText eventLocationEditText;
	EditText descriptionEditText;
	
	TextView dueDateTextView;
	TextView dueTimeTextView;
	
	private int mYear, mMonth, mDay, mHour, mMinute;
	
	CheckBox recurCheckBox;
	
	RadioGroup recurRadioGroup;
	RadioButton recurWeeklyRadioButton;
	RadioButton recurBiWeeklyRadioButton;
	
	Button addEventButton;
	Button cancelButton;
	
	
	public final static String EXTRA_MESSAGE = "com.nick.nusbuddy.addhomework.MESSAGE";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contents_add_homework);
		
		eventTitleEditText = (EditText) findViewById(R.id.eventTitleEditText);
		eventLocationEditText = (EditText) findViewById(R.id.eventLocationEditText);
		descriptionEditText = (EditText) findViewById(R.id.descriptionEditText);
		
		dueDateTextView = (TextView) findViewById(R.id.dueDateTextView);
		dueTimeTextView = (TextView) findViewById(R.id.dueTimeTextView);
		
		recurCheckBox = (CheckBox) findViewById(R.id.checkBoxRecur);
		
		recurRadioGroup = (RadioGroup) findViewById(R.id.RadioGroupReccur);
		recurWeeklyRadioButton = (RadioButton) findViewById(R.id.RadioButtonRecurWeekly);
		recurBiWeeklyRadioButton = (RadioButton) findViewById(R.id.RadioButtonRecurBiweekly);
		
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
		eventTitleEditText.setError("Invalid input");
		dueDateTextView.setError("Invalid input");
		
	}
	
	//how to alert for every odd week/ even week?
	
	public void addEvent(View v) {
		Event event = new Event();
		String eventTitle = eventTitleEditText.getText().toString(); 
		String eventLocation = eventLocationEditText.getText().toString(); 
		String date = dueDateTextView.getText().toString();
		String description = descriptionEditText.getText().toString();
		
		if (eventTitle == "" ||
			date == "") {
			
			showError();
		
		} else {
			
			Intent output = this.getIntent();
			output.putExtra("eventTitle", eventTitle);
			event.setTitle(eventTitle);
			
			output.putExtra("eventLocation", eventLocation);
			event.setLocation(eventLocation);
			
			output.putExtra("description", description);
			event.setDescription(description);
			
			//month need to plus 1
			output.putExtra("mDay", mDay);
			output.putExtra("mMonth", mMonth);
			output.putExtra("mYear", mYear);
	
			output.putExtra("mHour", mHour);
			output.putExtra("mMinute", mMinute);
			
			output.putExtra("recurCheckBox", recurCheckBox.isChecked());
			
			if (recurCheckBox.isChecked()) {
				String selectedRadioValue =((RadioButton)findViewById(recurRadioGroup.getCheckedRadioButtonId())).getText().toString();
				output.putExtra("selectedRadioValue", selectedRadioValue);
				
			}
			
			setResult(RESULT_OK, output);
			finish();
		}
	}
	
	public void cancelEvent(View v) {
		finish();
	}
	
	public void showDatePickerDialog(View v) {
		
		// Process to get Current Date
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        
        //Day, date, month, year format
	
        // Launch Date Picker Dialog
        DatePickerDialog.OnDateSetListener dpdl = new DatePickerDialog.OnDateSetListener() {
       	 
            @Override
            public void onDateSet(DatePicker view, int year,
                    int monthOfYear, int dayOfMonth) {
                // Display Selected date in textbox
            	dueDateTextView.setText(dayOfMonth + "-"
                        + (monthOfYear + 1) + "-" + year);

            }
        };
     
        DatePickerDialog dpd2 = new DatePickerDialog(AddHomework.this, dpdl, mYear, mMonth, mDay);
        dpd2.show();
	}
	
	public void showTimePickerDialog(View v) {
		
		// Process to get Current Time
        final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);

        // Launch Time Picker Dialog
        TimePickerDialog.OnTimeSetListener tpdl =  new TimePickerDialog.OnTimeSetListener() {
       	 
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay,
                    int minute) {
            	
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
            }
        };
        
        TimePickerDialog tpd = new TimePickerDialog(AddHomework.this, tpdl, mHour, mMinute, false);
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
