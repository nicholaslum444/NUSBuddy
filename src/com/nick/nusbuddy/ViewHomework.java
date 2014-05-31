package com.nick.nusbuddy;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class ViewHomework extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.contents_view_homework);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.view_homework, menu);
		return true;
	}

}
