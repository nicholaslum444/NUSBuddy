package com.nick.nusbuddy;

import android.os.Bundle;
import android.app.Activity;

public class Announcements extends BaseActivity {
	
	@Override
	protected Activity getCurrentActivity() {
		return this;
	}

	@Override
	protected int getCurrentActivityLayout() {
		return R.layout.contents_announcements;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
	}

}
