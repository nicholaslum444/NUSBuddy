package com.nick.nusbuddy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class AnnouncementsSchedulingService extends IntentService implements ModulesAsyncTaskListener {

	private NotificationManager notificationManager;
	private NotificationCompat.Builder builder;
	
	SharedPreferences sharedPrefs;
	Editor sharedPrefsEditor;
	
	String apiKey;
	String authToken;
	String userId;
	
	Intent handleIntent;
	
	HttpsURLConnection connection;
	String responseContent;
	int responseCode;
	
	public AnnouncementsSchedulingService() {
		super("AnnouncementsSchedulingService");
		
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		//sendNotification("asdasd");
		//sendNotification("qweasdasdasd");
		Log.w("on handle", "jhk");
		sharedPrefs = getSharedPreferences("NUSBuddyPrefs", MODE_PRIVATE);
		sharedPrefsEditor = sharedPrefs.edit();
		
		apiKey = getString(R.string.api_key_mine);
		userId = sharedPrefs.getString("userId", null);
		authToken = sharedPrefs.getString("authToken", null);
		
		if (userId == null || authToken == null || apiKey == null) {
			Log.w("got null", "jkh");
			return;
		}
		Log.w("ok to make connection", "kh");
		
		try {
			URL url = new URL("https://ivle.nus.edu.sg/api/Lapi.svc/Modules_Student?APIKey=" + apiKey
						+ "&AuthToken=" + authToken + "&StudentID=" + userId + "&Duration=0" + "&IncludeAllInfo=true" + "&output=json");
			//URL url = new URL("https://ivle.nus.edu.sg/api/Lapi.svc/PublicNews?APIKey=" + apiKey + "&TitleOnly=false");
			connection = (HttpsURLConnection) url.openConnection();
			connection.setConnectTimeout(60000);
			connection.setReadTimeout(60000);
			
			Log.w("create connection", "kj");
			
			responseCode = connection.getResponseCode();
			
			Log.w("open connection", "k");
			
			if (responseCode == 200) {
				Log.w("200 connection", responseCode+"");
				BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				StringBuilder sb = new StringBuilder();
				String line = br.readLine();
				while (line != null) {
					sb.append(line);
					line = br.readLine();
				}
				br.close();
				responseContent = sb.toString();
			} else {
				Log.w("fail connection", "ljk");
				return;
			}
			
		
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return;
		} catch (IOException e) {
			e.printStackTrace();
			return;
		} finally {
			connection.disconnect();
			Log.w("dc connection", "u");
			
		}
		
		Log.w(" send noti", "u");
		String oldInfo = sharedPrefs.getString("modulesInfo", null);
		if (oldInfo != null && responseContent.equals(oldInfo)) {
			sharedPrefsEditor.putString("modulesInfo", responseContent);
			sharedPrefsEditor.commit();
			
			sendNotification("new announcements" + responseContent.length());
		} else {
			sendNotification("NO new announcements" + responseContent.length());
			
		}
		
		AnnouncementsAlarmReceiver.completeWakefulIntent(intent);
		
	}
	
	private void sendNotification(String msg) {
		Log.w("create notif", "jhg");
		notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
		
		PendingIntent contentIntent = PendingIntent.getActivity(this, 1, new Intent(this, Announcements.class), 0);
		
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
		.setSmallIcon(R.drawable.ic_launcher)
		.setContentTitle("New Announcement")
		.setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
		.setAutoCancel(true)
		.setContentText(msg);
		
		mBuilder.setContentIntent(contentIntent);
		notificationManager.notify(1, mBuilder.build());
	}

	@Override
	public void onModulesTaskComplete(String responseContent) {
		// TODO Auto-generated method stub
		
		
		sendNotification(responseContent);
	}
	
	

}
