package com.nick.nusbuddy;

import java.util.Locale;

import helpers.com.nick.nusbuddy.ViewHelper;

import android.os.Bundle;
import android.text.InputFilter;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

public class CapCalculator extends BaseActivity {
	
	// the hardcoded array for grade points :(
	// TODO find a better way to store this
	double[] points = {5, 5, 4.5, 4.0, 3.5, 3, 2.5, 2, 1.5, 1, 0, -1};
	
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
		super.onCreate(savedInstanceState);
		
		vh = new ViewHelper();
		
		layoutModules = (LinearLayout) findViewById(R.id.Layout_modules);
		
		createPageContents();
	}
	
	@Override
	protected void createPageContents() {
		// maybe wanna fill up the page with previous selections
		
		// start off with 4 modules
		for (int i = 0; i < 4; i++) {
			//addModule(null);
		}
	}
	
	public void addModule(View v) {
		View.inflate(this, R.layout.container_cap_calculator_module, layoutModules);
		//LinearLayout newModule = (LinearLayout) layoutModules.findViewById(R.id.Layout_module);
		TableRow newModule = (TableRow) layoutModules.findViewById(R.id.Layout_module);
		vh.changeViewId(newModule);
		
		// set up the mc spinner default selection
		Spinner mcSpinner = (Spinner) newModule.findViewById(R.id.Spinner_mc);
		mcSpinner.setSelection(3);
	}
	
	public void removeModule(View v) {
		layoutModules.removeView((View) v.getParent());
	}
	
	private void calculateCap() {
		
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
		
		case R.id.action_calculate:
			calculateCap();
			break;
		}
       
       return super.onOptionsItemSelected(item);
    }
	
}
