package com.nick.nusbuddy;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;
import android.widget.Toast;

public class AnnouncementsAlarmReceiver extends WakefulBroadcastReceiver {

	private AlarmManager alarmMgr;
	private PendingIntent alarmIntent;
	
	public AnnouncementsAlarmReceiver() {
	}

	@Override
	public void onReceive(Context arg0, Intent arg1) {
		Intent service = new Intent(arg0, AnnouncementsSchedulingService.class);
		startWakefulService(arg0, service);
	}
	
	public void setAlarm(Context context, long interval) {
		alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(context, AnnouncementsAlarmReceiver.class);
		alarmIntent = PendingIntent.getBroadcast(context, 12345, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		
		alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP,
				System.currentTimeMillis() + interval, interval, alarmIntent);
		
		ComponentName receiver = new ComponentName(context, AnnouncementsBootReceiver.class);
		PackageManager pm = context.getPackageManager();
		
		pm.setComponentEnabledSetting(receiver, 
				PackageManager.COMPONENT_ENABLED_STATE_ENABLED, 
				PackageManager.DONT_KILL_APP);
	}
	
	public void cancelAlarm(Context context) {
		Intent intent = new Intent(context, AnnouncementsAlarmReceiver.class);
		alarmIntent = PendingIntent.getBroadcast(context, 12345, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		if (alarmMgr != null) {
			alarmMgr.cancel(alarmIntent);
			//Toast.makeText(context, "this", Toast.LENGTH_LONG).show();
			Log.w("cancel","calcen");
		}
		
		ComponentName receiver = new ComponentName(context, AnnouncementsBootReceiver.class);
		PackageManager pm = context.getPackageManager();
		
		pm.setComponentEnabledSetting(receiver, 
				PackageManager.COMPONENT_ENABLED_STATE_DISABLED, 
				PackageManager.DONT_KILL_APP);
	}

}
