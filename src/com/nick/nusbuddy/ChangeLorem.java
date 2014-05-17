package com.nick.nusbuddy;

import android.app.Activity;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ChangeLorem {

	public ChangeLorem() {
	}
	
	public static void change(Activity a) {
		LinearLayout l = (LinearLayout) a.findViewById(R.id.Layout_page_content);
		TextView t = (TextView) l.getChildAt(0);
		Log.e(t.toString(), t.toString());
		t.setText("loremsmsmsmsm");
	}

}
