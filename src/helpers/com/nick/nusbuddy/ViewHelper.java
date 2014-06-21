package helpers.com.nick.nusbuddy;

import java.util.Random;

import android.os.Build;
import android.view.View;

public class ViewHelper {
	
	Random r = new Random();
	
	public int changeViewId(View v) {
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
			v.setId(View.generateViewId());
		} else {
			v.setId(r.nextInt(Integer.MAX_VALUE));
		}
		
		return v.getId();
	}

}
