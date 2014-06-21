package com.nick.nusbuddy;

import java.util.Locale;

import android.os.Bundle;
import android.text.InputFilter;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

public class ZoldCapCalculator extends BaseActivity {
	

	// to uniquely identify each new module created
	private int count;
	
	// the cap value field that updates when i press go
	private TextView capValue;
	
	// the layout that holds the new modules created
	LinearLayout modulesLayout;
	
	// reference to the cumulative checkbox
	// tick means add current cap to the calculation
	CheckBox cumulativeCheckBox;
	
	// for the old cap
	EditText oldCap;
	
	// for the old mcs
	EditText oldCredits;
	
	// to format the cap value field
	String format;
	
	// the hardcoded array for grade points :(
	// TODO find a better way to store this
	double[] points = {5, 5, 4.5, 4.0, 3.5, 3, 2.5, 2, 1.5, 1, 0, -1};
	
	// start index of modules
	int modulesStart;
	
	// error enum
	public enum ErrorTypes {
		NULL, NO_MODULES, NO_CAP, NO_MC, HIGH_CAP, UNKNOWN;
	}
	
	ErrorTypes error;
	
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
		super.onCreate(savedInstanceState);
		
		/* specific inits */
		
		// format for the cap value
		// this means numbers only.
		format = "%.4g%n";
		
		// init count for unique id
		count = 0;
		
		// init the reference to the respective views
		capValue = (TextView) findViewById(R.id.textView_cap_value);
		modulesLayout = (LinearLayout) findViewById(R.id.ModulesLayout);
		cumulativeCheckBox = (CheckBox) findViewById(R.id.checkBox_old_cap);
		oldCap = (EditText) findViewById(R.id.editText_old_cap);
		oldCredits = (EditText) findViewById(R.id.editText_old_credits);
		
		// update the cap value field with 0.00
		String capText = String.format(Locale.US, format, 0.00);
		capValue.setText(capText);
		
		// set the start index to be equal to the num of views before the modules
		modulesStart = modulesLayout.getChildCount();
		
		// set the error to default to NULL
		error = ErrorTypes.NULL;
		

	}
	
	@Override
	protected void createPageContents() {
		
	}
	
	
	/* specific methods */
	
	/**
	 * Creates the module code textedit
	 * @return
	 */
	public EditText createModuleCodeEdit() {
		
		EditText moduleCodeEdit = new EditText(this);
		moduleCodeEdit.setHint(R.string.editText_moduleCode);
		InputFilter[] filterArray = new InputFilter[1];
		filterArray[0] = new InputFilter.LengthFilter(10);
		moduleCodeEdit.setFilters(filterArray);
		
		// make the thing wrap content
		moduleCodeEdit.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1f));
		
		return moduleCodeEdit;
	}
	
	/**
	 * creates the MC selector
	 * @return
	 */
	public Spinner createModuleCreditsSpinner() {
		Spinner moduleCreditsSpinner = new Spinner(this);
		
		// insert the mc values into the spinner
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.MC_options, android.R.layout.simple_list_item_1);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		moduleCreditsSpinner.setAdapter(adapter);
		
		// set the default selection to 4mc
		moduleCreditsSpinner.setSelection(3);
		
		return moduleCreditsSpinner;
	}
	
	
	/**
	 * creates the letter grade selector
	 * @return
	 */
	public Spinner createModuleGradeSpinner() {
		Spinner moduleGradeSpinner = new Spinner(this);

		// insert the mc values into the spinner
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.Grade_options, android.R.layout.simple_list_item_1);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		moduleGradeSpinner.setAdapter(adapter);

		// set default selection to a+
		moduleGradeSpinner.setSelection(0);
		
		return moduleGradeSpinner;
	}
	
	/**
	 * creates the creates the remove button
	 * style: small button
	 * @return
	 */
	public Button createModuleRemoveButton() {
		Button moduleRemoveButton = new Button(this, null, android.R.attr.buttonStyleSmall);
		
		// the unique ID of the module, used to remove the module
		final int index = count;
		
		moduleRemoveButton.setText(R.string.button_remove);
		
		// sets the click listener to a new clicklistener object
		// overrides the onclick method inside (i think onclicklistener is an abstract?)
		moduleRemoveButton.setOnClickListener(
			new OnClickListener() {
			@Override
			public void onClick(View v) {
				// looks for the current view by the unique ID index, and removes it
				LinearLayout currentModule = (LinearLayout) findViewById(index);
				modulesLayout.removeView(currentModule);
			}});
		
		return moduleRemoveButton;
	}
	
	/**
	 * creates and adds the new module to the modules layout
	 * @param v 
	 * @return
	 */
	public void addModule(View v) {
		LinearLayout currentModule = new LinearLayout(this);
		currentModule.setId(count);
		currentModule.setOrientation(LinearLayout.HORIZONTAL);
		currentModule.setGravity(Gravity.CENTER);
		
		// adding all the internal fields
		currentModule.addView(createModuleCodeEdit());
		currentModule.addView(createModuleCreditsSpinner());
		currentModule.addView(createModuleGradeSpinner());
		currentModule.addView(createModuleRemoveButton());
		
		// finally add the entire mod into the modules field
		modulesLayout.addView(currentModule);
		
		// increase the count to change the unique ID for the next module
		count++;
	}
	
	/**
	 * returns the correct grade points for each letter grade
	 * returns -1 if s/u is selected
	 * @param position
	 * @return
	 */
	public double extractPoints(int position) {
		return points[position];
	}
	
	/**
	 * runs when the calculate button is pressed (see xml)
	 * calculates the cap and updates the cap field accordingly
	 * semester cap formula = Sum(mc * points) / Sum(mc)
	 * cumulative cap formula = (Sum(mc * points) + oldMc * oldCap) / (Sum(mc) + oldMc)  
	 * @param v
	 */
	public void calculateCap(View v) {
		
		// local vars~
		int childCount = modulesLayout.getChildCount();
		double cap = 0.0;
		double pointsTotal = 0.0;
		int creditsTotal = 0;
		double oldCapValue = 0.0;
		int oldCreditsValue = 0;
		
		// reset the error to NULL (no error)
		error = ErrorTypes.NULL;
		
		// set error if there are no modules added yet
		if (childCount <= modulesStart) {
			error = ErrorTypes.NO_MODULES;
		}
		
		// loop through each module to extract information 
		// start at modulesStart because there are other views before the modules
		for (int i = modulesStart; i < childCount; i++) {
			
			// get the module
			LinearLayout currentModule = (LinearLayout) modulesLayout.getChildAt(i);
			
			// get the grade spinner
			Spinner gradeSpinner = (Spinner) currentModule.getChildAt(2);
			
			// get the points for the selected grade (-1 for s/u)
			double points = extractPoints(gradeSpinner.getSelectedItemPosition());
			
			// check if s/u selected; if selected, do nothing
			if (points >= 0) {
				// s/u not selected
				
				// get the MCs
				Spinner creditsSpinner = (Spinner) currentModule.getChildAt(1);
				
				// the MCs are 1-indexed. so we just add 1 to the position
				int credits = creditsSpinner.getSelectedItemPosition() + 1;
				
				// get the score and sum up
				double score = points * credits;
				creditsTotal += credits;
				pointsTotal += score;
			}
		}
		
		// check if cumulative cap is selected
		// parse the old cap and mc fields if selected
		if (cumulativeCheckBox.isChecked()) {
			
			try { // parse the old mc
				oldCreditsValue = Integer.parseInt(oldCredits.getText().toString());
			} catch (NumberFormatException e) {
				error = ErrorTypes.NO_MC;
			} catch (Exception e) {
				error = ErrorTypes.UNKNOWN;
			}
			
			try { // parse the old cap
				oldCapValue = Double.parseDouble(oldCap.getText().toString());
				if (oldCapValue > 5.00) {
					error = ErrorTypes.HIGH_CAP;
				}
			} catch (NumberFormatException e) {
				error = ErrorTypes.NO_CAP;
			} catch (Exception e) {
				error = ErrorTypes.UNKNOWN;
			}
		}
		
		// error handling~
		switch (error) {
		
		case NULL: // no error
			pointsTotal += oldCapValue * oldCreditsValue;
			creditsTotal += oldCreditsValue;
			
			// to prevent divide by 0 error
			if (creditsTotal > 0) {
				cap = pointsTotal / creditsTotal;
			}
			
			// update the cap value
			capValue.setText(String.format(format, cap));
			break;
			
		default: // have error
			
			// create the error alert
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(R.string.popup_title);
			builder.setCancelable(true);
			builder.setNegativeButton(R.string.popup_button_cancel, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int id) {
					dialog.cancel();
				}
			});
			
			// check which kind of error
			switch(error) {
			
			case NO_CAP:
				builder.setMessage(R.string.popup_empty_cap);
				break;
			
			case NO_MC:
				builder.setMessage(R.string.popup_empty_mc);
				break;
			
			case NO_MODULES:
				builder.setMessage(R.string.popup_empty_modules);
				break;
			
			case HIGH_CAP:
				builder.setMessage(R.string.popup_high_cap);
				break;
				
			case UNKNOWN:
				builder.setMessage(R.string.popup_unknown);
				break;
				
			default:
				builder.setMessage(R.string.popup_unknown);
				break;
			}
			
			// finally, throw the popup in yo face
			AlertDialog popup = builder.create();
			popup.show();
		}
	}

}
