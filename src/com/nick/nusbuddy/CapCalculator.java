package com.nick.nusbuddy;

import android.os.Bundle;
import android.app.Activity;

public class CapCalculator extends BaseActivity {
	
	
	
	@Override
	protected Activity getCurrentActivity() {
		return this;
	}

	@Override
	protected int getCurrentActivityLayout() {
		return R.layout.activity_cap_calculator;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	protected void createPageContents() {
	}

}
