package com.nick.nusbuddy;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.LinearLayout;

public class ViewHomework extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.contents_view_homework);
		
		LinearLayout layoutViewHomework = (LinearLayout) findViewById(R.id.Layout_view_homework);
		
		View.inflate(this, R.layout.container_view_homework_item, layoutViewHomework);
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.view_homework, menu);
		return true;
	}
	
	public void expandItem(View v) {
		LinearLayout hiddenLayout = (LinearLayout) findViewById(R.id.Layout_homework_item_hidden);
		if (hiddenLayout.getVisibility() == View.VISIBLE) {
			hiddenLayout.setVisibility(View.GONE);
		} else {
			hiddenLayout.setVisibility(View.VISIBLE);
		}
	}

}
