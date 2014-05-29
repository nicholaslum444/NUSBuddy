package com.nick.nusbuddy;

import java.util.Calendar;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

public class AddHomework extends Activity {
	
	EditText eventTitleEditText;
	EditText eventLocationEditText;
	
	TextView dueDateTextView;
	TextView dueTimeTextView;
	
	private int mYear, mMonth, mDay, mHour, mMinute;
	
	TextView descriptionTextView;
	
	CheckBox recurWeeklyCheckBox;
	CheckBox recurBiWeeklyCheckBox;
	
	Button addEventButton;
	Button cancelButton;
	
	public final static String EXTRA_MESSAGE = "com.nick.nusbuddy.addhomework.MESSAGE";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contents_add_homework);
		
		eventTitleEditText = (EditText) findViewById(R.id.eventTitleEditText);
		eventLocationEditText = (EditText) findViewById(R.id.eventLocationEditText);
		
		dueDateTextView = (TextView) findViewById(R.id.dueDateTextView);
		dueTimeTextView = (TextView) findViewById(R.id.dueTimeTextView);
		
		recurWeeklyCheckBox = (CheckBox) findViewById(R.id.recurWeeklyCheckBox);
		recurBiWeeklyCheckBox = (CheckBox) findViewById(R.id.recurBiWeeklyCheckBox);
		
		addEventButton = (Button) findViewById(R.id.addEventButton);
		cancelButton = (Button) findViewById(R.id.cancelButton);
	}
	
	

	/*public OnClickListener dueDateTextViewListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (v == dueDateTextView) {
				
				// Process to get Current Date
	            final Calendar c = Calendar.getInstance();
	            mYear = c.get(Calendar.YEAR);
	            mMonth = c.get(Calendar.MONTH);
	            mDay = c.get(Calendar.DAY_OF_MONTH);
	            
	            //DatePicker dp = 
			
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
	         
	            DatePickerDialog dpd = new DatePickerDialog(AddHomework.this, dpdl, mYear, mMonth, mDay);
	            dpd.show();
			}
		}
		
	};*/
	
	public void showDatePickerDialog(View v) {
		
		// Process to get Current Date
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
	
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
                // Display Selected time in textbox
            	dueTimeTextView.setText(hourOfDay + ":" + minute);
            }
        };
        
        TimePickerDialog tpd = new TimePickerDialog(AddHomework.this, tpdl, mHour, mMinute, false);
        tpd.show();
	}
	
	/*public OnClickListener dueTimeTextViewListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (v == dueTimeTextView) {
				
				// Process to get Current Time
	            final Calendar c = Calendar.getInstance();
	            mHour = c.get(Calendar.HOUR_OF_DAY);
	            mMinute = c.get(Calendar.MINUTE);
	 
	            // Launch Time Picker Dialog
	            TimePickerDialog.OnTimeSetListener tpdl =  new TimePickerDialog.OnTimeSetListener() {
	           	 
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                            int minute) {
                        // Display Selected time in textbox
                    	dueTimeTextView.setText(hourOfDay + ":" + minute);
                    }
                };
	            
	            TimePickerDialog tpd = new TimePickerDialog(AddHomework.this, tpdl, mHour, mMinute, false);
	            tpd.show();
			}
			
		}
		
	};*/
	
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
