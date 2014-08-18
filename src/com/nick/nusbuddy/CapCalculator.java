package com.nick.nusbuddy;

import java.text.DecimalFormat;

import helpers.com.nick.nusbuddy.ViewHelper;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class CapCalculator extends BaseActivity {
	
	public class Pair<T1, T2> {
		private T1 head;
		private T2 tail;
		public Pair(T1 head, T2 tail) {
			this.head = head;
			this.tail = tail;
		}
		public T1 getHead() {
			return head;
		}
		public T2 getTail() {
			return tail;
		}
	}
	
	// to format the cap value field
	
	DecimalFormat capFormat;
	
	// the hardcoded array for grade points :(
	// TODO find a better way to store this
	double[] points = {5, 5, 4.5, 4.0, 3.5, 3, 2.5, 2, 1.5, 1, 0, -1};
	
	SharedPreferences sharedPrefs;
	Editor sharedPrefsEditor;
	
	LinearLayout layoutModules;
	ViewHelper vh;
	
	@Override
	protected Activity getCurrentActivity() {
		return this;
	}

	@Override
	protected int getCurrentActivityLayout() {
		return R.layout.contents_cap_calculator;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		doNotSetBackground = true;
		super.onCreate(savedInstanceState);
		
		vh = new ViewHelper();
		
		// format for the cap value
		// this means numbers only.
		capFormat = new DecimalFormat("0.000");
		
		layoutModules = (LinearLayout) findViewById(R.id.Layout_modules);
		
		sharedPrefs = this.getSharedPreferences("NUSBuddyPrefs", MODE_PRIVATE);
		sharedPrefsEditor = sharedPrefs.edit();
		
		createPageContents();
	}
	
	@Override
	protected void createPageContents() {
		// maybe wanna fill up the page with previous selections
		
		// start off with 4 modules
		for (int i = 0; i < 4; i++) {
			addModule();
		}
		
		// set cap
		EditText editTextCurrentCap = (EditText) findViewById(R.id.Edittext_current_cap);
		float sharedPrefsCap = Float.parseFloat(sharedPrefs.getString("currentCap", -1+""));
		if (sharedPrefsCap >= 0) {
			editTextCurrentCap.setText(sharedPrefsCap + "");
		}
		
	}
	
	public void updateCalculateButton() {
		// show number of modules in the calc button
		int numOfModulesAdded = layoutModules.getChildCount();
		Button buttonCalculate = (Button) findViewById(R.id.Button_calculate_cap);
		if (numOfModulesAdded == 1) {
			buttonCalculate.setText("Calculate!\n("+ numOfModulesAdded + " module)");
		} else {
			buttonCalculate.setText("Calculate!\n("+ numOfModulesAdded + " modules)");
		}
	}
	
	public void addModule() {
		View.inflate(this, R.layout.container_cap_calculator_module, layoutModules);
		TableRow newModule = (TableRow) layoutModules.findViewById(R.id.Layout_module);
		vh.changeViewId(newModule);
		
		// set up the mc spinner default selection
		Spinner mcSpinner = (Spinner) newModule.findViewById(R.id.Spinner_mc);
		mcSpinner.setSelection(3);
		
		updateCalculateButton();
	}
	
	public void removeModule(View v) {
		layoutModules.removeView((View) v.getParent());
		updateCalculateButton();
	}
	
	public void calculateCap(View v) {
		
		if (layoutModules.getChildCount() <= 0) {
			Toast.makeText(this, "Please add at least one module!", Toast.LENGTH_LONG).show();
		} else {
			
			EditText editTextCurrentCap = (EditText) findViewById(R.id.Edittext_current_cap);
			EditText editTextMcsTaken = (EditText) findViewById(R.id.Edittext_mcs_taken);
			
			TextView textViewCalculatedCap = (TextView) findViewById(R.id.Textview_calculated_value);
			
			String currentCapString = editTextCurrentCap.getText().toString();
			String mcsTakenString = editTextMcsTaken.getText().toString();
			
			boolean currentCapFilled = currentCapString.length() > 0;
			boolean mcsTakenFilled = mcsTakenString.length() > 0;
			boolean noError = true;
			
			int mcsTotal = 0;
			double pointsTotal = 0;
			double cap = 0;
			
			// add up from the modules first
			
			for (int i = 0; i < layoutModules.getChildCount(); i++) {
				
				LinearLayout currentModule = (LinearLayout) layoutModules.getChildAt(i);
				
				// get the grade spinner
				Spinner gradeSpinner = (Spinner) currentModule.getChildAt(2);
				
				// get the points for the selected grade (-1 for s/u)
				double points = getPoints(gradeSpinner.getSelectedItemPosition());
				
				// check if s/u selected; if selected, do nothing
				if (points >= 0) {
					// s/u not selected
					
					// get the MCs
					Spinner creditsSpinner = (Spinner) currentModule.getChildAt(1);
					
					// the MCs are 1-indexed. so we just add 1 to the position
					int mc = creditsSpinner.getSelectedItemPosition() + 1;
					
					// get the score and sum up
					double score = points * mc;
					mcsTotal += mc;
					pointsTotal += score;
				}
				
			}
			
			
			if (currentCapFilled && mcsTakenFilled) { // use details when both are provided
				
				// TODO check the inputs for current cap and mcs taken
				double currentCap = Double.parseDouble(currentCapString);
				int mcsTaken = Integer.parseInt(mcsTakenString);
				
				if (currentCap > 5) {
					editTextCurrentCap.setError("Invalid Input");
					noError = false;
				} else if (mcsTaken > 500) {
					editTextMcsTaken.setError("Invalid Input");
					noError = false;
				} else {
					mcsTotal += mcsTaken;
					pointsTotal += currentCap * mcsTaken;
				}
				
			// else show error if only half filled
			} else if (currentCapFilled && !mcsTakenFilled) { 
				editTextMcsTaken.setError("Invalid Input");
				noError = false;
			} else if (!currentCapFilled && mcsTakenFilled) {
				editTextCurrentCap.setError("Invalid Input");
				noError = false;
			}
			
			// update the calculated cap value
			if (mcsTotal > 0 && noError) { // to prevent divide by 0 error
				cap = pointsTotal / mcsTotal;
				textViewCalculatedCap.setText(capFormat.format(cap));
			}
			
			
		}
	}
	
	public double getPoints(int position) {
		return points[position];
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.cap_calculator, menu);
		return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        // ActionBarDrawerToggle will take care of this.
		
		int itemId = item.getItemId();
		
		switch (itemId) {
		
		case R.id.action_add_module:
			addModule();
			break;
		}
       
       return super.onOptionsItemSelected(item);
    }
	
}
