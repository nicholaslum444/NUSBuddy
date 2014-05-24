package com.nick.nusbuddy;

import android.os.Bundle;
import android.app.Activity;

public class Homework extends BaseActivity {
	


	@Override
	protected Activity getCurrentActivity() {
		return this;
	}

	@Override
	protected int getCurrentActivityLayout() {
		return R.layout.activity_homework;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void createPageContents() {
	}

}
