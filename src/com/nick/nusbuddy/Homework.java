package com.nick.nusbuddy;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;
import android.content.Intent;

public class Homework extends BaseActivity {
	
	QuickAction mQuickAction;
	private static final int REQUEST_CODE = 1;

	@Override
	protected Activity getCurrentActivity() {
		return this;
	}

	@Override
	protected int getCurrentActivityLayout() {
		return R.layout.contents_homework;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		createqa();
		createPageContents();
	}

	@Override
	protected void createPageContents() {
		
	}
	
	@Override
	  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
	    	if (data.hasExtra("taskThenDate")) {
	            String result = data.getExtras().getString("taskThenDate");
	            if (result != null && result.length() > 0) {
	            	TextView t = new TextView(this);
	            	t.setText(result);
	            	t.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));	            	
	            	int layoutId = data.getExtras().getInt("layoutId");
	            	LinearLayout layout = (LinearLayout) findViewById(layoutId);
	            	layout.addView(t);
	            }
	        }
	    }
	}
	
 	void createqa() {
	 	ActionItem addItem      = new ActionItem(0, "Add", getResources().getDrawable(R.drawable.ic_add));
	    ActionItem acceptItem   = new ActionItem(1, "View", getResources().getDrawable(R.drawable.ic_accept));
	    //ActionItem uploadItem   = new ActionItem(2, "Upload", getResources().getDrawable(R.drawable.ic_up));
	
	    //use setSticky(true) to disable QuickAction dialog being dismissed after an item is clicked
	    //uploadItem.setSticky(true);
	
	    mQuickAction  = new QuickAction(this);
	
	    mQuickAction.addActionItem(addItem);
	    mQuickAction.addActionItem(acceptItem);
	    //mQuickAction.addActionItem(uploadItem);
	
	    //setup the action item click listener
	    mQuickAction.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {
	        @Override
	        public void onItemClick(QuickAction quickAction, int pos, int actionId) {
	            ActionItem actionItem = quickAction.getActionItem(pos);
	
	            if (actionId == 0) {
	                
	            	Toast.makeText(getApplicationContext(), "Add item selected", Toast.LENGTH_SHORT).show();
	                
	            	int viewId = mQuickAction.mAnchor.getId();
	                Intent intent = new Intent(Homework.this, AddHomework.class);
	        	    intent.putExtra("viewId", viewId);
	        	    startActivityForResult(intent, REQUEST_CODE);
	            	
	            } else {
	               
	            	Toast.makeText(getApplicationContext(), actionItem.getTitle() + " selected", Toast.LENGTH_SHORT).show();
	            	
	            	Intent intent = new Intent(Homework.this, ViewHomework.class);
	            	startActivity(intent);

;	            }
	        }
	    });
	
	    mQuickAction.setOnDismissListener(new QuickAction.OnDismissListener() {
	        @Override
	        public void onDismiss() {
	            Toast.makeText(getApplicationContext(), "Ups..dismissed", Toast.LENGTH_SHORT).show();
	        }
	    });
 	}
 	
 	public void call_quick_action_bar(View view) {
 		mQuickAction.show(view);
 	}
 	
}
