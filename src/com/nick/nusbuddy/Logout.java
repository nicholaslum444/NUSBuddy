package com.nick.nusbuddy;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class Logout extends Activity {

	private Logout context;
	private SharedPreferences sharedPrefs;
	private Editor sharedPrefsEditor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contents_logout);
		

        context = this;
        sharedPrefs = getSharedPreferences("NUSBuddyPrefs", MODE_PRIVATE);
        sharedPrefsEditor = sharedPrefs.edit();
        
        logout(null);
	}
	
    // pressing the logout button on the screen.
    public void logout(View v) {
    	AlertDialog.Builder builder = new AlertDialog.Builder(context)
    									.setTitle("Log out")
    									.setMessage("Are you sure?")
    									.setPositiveButton("Log out", new DialogInterface.OnClickListener() {
    											public void onClick(DialogInterface dialog, int id) {
    												// User clicked OK button
    												dialog.cancel();
    										    	sharedPrefsEditor.clear();
    										    	sharedPrefsEditor.commit();
    												startActivity(new Intent(context, Login.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
    										    	((Activity) context).finish();
    											}
    									});
    								    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
    								    		public void onClick(DialogInterface dialog, int id) {
    								    			// User clicked cancel button
    								    			dialog.cancel();
    								    			finish();
    								    		}
    								    });
    	AlertDialog a = builder.create();
    	a.setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				// TODO Auto-generated method stub
				context.finish();
			}
    		
    	});
    	a.show();
    }
    
    @Override
	public void onBackPressed() {
    	finish();
    }
}
