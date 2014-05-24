package com.nick.nusbuddy;

import android.view.Menu;
import android.view.MenuItem;

public abstract class RefreshableActivity extends BaseActivity {
	
	protected abstract RefreshableActivity getCurrentRefreshableActivity();
	protected abstract void onRefresh();

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.refresh_only, menu);
		return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        // ActionBarDrawerToggle will take care of this.
		
		int itemId = item.getItemId();
		switch(itemId) {
		case R.id.action_refresh:
			getCurrentRefreshableActivity().onRefresh();
			break;
		}
       
       return super.onOptionsItemSelected(item);
   }

}
