package com.nick.nusbuddy;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class AnnouncementsBootReceiver extends BroadcastReceiver {
	
	AnnouncementsAlarmReceiver alarm = new AnnouncementsAlarmReceiver();
	private long interval;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
			/*SharedPreferences sharedPrefs = getSharedPreferences("NUSBuddyPrefs", MODE_PRIVATE);
			String intervalString = sharedPrefs.getString("check_announcements_interval", "21600");
			interval = Long.parseLong(intervalString) * 1000; // in milisecs*/
			alarm.setAlarm(context, 15 * 1000);
		}

	}

}
