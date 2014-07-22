package com.nick.nusbuddy;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.text.TextUtils;
import android.widget.Toast;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class Settings extends PreferenceActivity {
	/**
	 * Determines whether to always show the simplified settings UI, where
	 * settings are presented in a single list. When false, settings are shown
	 * as a master/detail two-pane view on tablets. When true, a single pane is
	 * shown on tablets.
	 */
	private static final boolean ALWAYS_SIMPLE_PREFS = true;

	@SuppressWarnings("deprecation")
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		
		// originally the shared preferences file used to save the prefs was the default one.
		// this line changes the shared preferences file to my own so that it saves in this one
		getPreferenceManager().setSharedPreferencesName("NUSBuddyPrefs");
		
		
		PreferenceManager.setDefaultValues(this, R.xml.pref_home_page, true);
		setupSimplePreferencesScreen();
		
		
		// setting up individual items options
		
		Preference prefCurrentCap = this.findPreference("currentCap");
		prefCurrentCap.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
			
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				// TODO Auto-generated method stub
				String newCurrentCap = (String) newValue;
				
				double capValue = Double.parseDouble(newCurrentCap);
				if (capValue > 5 || capValue < 0) {
					Toast.makeText(Settings.this, "Invalid CAP value", Toast.LENGTH_LONG).show();
					return false;
				}
				return true;
				
			}
		});
		
		Preference prefTargetCap = this.findPreference("targetCap");
		prefTargetCap.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
			
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				// TODO Auto-generated method stub
				String newTargetCap = (String) newValue;
				
				double capValue = Double.parseDouble(newTargetCap);
				if (capValue > 5 || capValue < 0) {
					Toast.makeText(Settings.this, "Invalid CAP value", Toast.LENGTH_LONG).show();
					return false;
				} 
				return true;
			}
		});
		
		Preference prefMc = this.findPreference("currentMcs");
		
		prefMc.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				// TODO Auto-generated method stub
				String newMcs = (String) newValue;
				
				int McsValue = Integer.parseInt(newMcs);
				if (McsValue > 500 || McsValue < 0) {
					Toast.makeText(Settings.this, "Invalid MC value", Toast.LENGTH_LONG).show();
					return false;
				}
				return true;
			}
			
		});
		
		SharedPreferences sharedPrefs = getSharedPreferences("NUSBuddyPrefs", MODE_PRIVATE);
		Editor sharedPrefsEditor = sharedPrefs.edit();
		boolean hasSecondMajor = false;
		String summary = "";
		
		Preference prefSecondSpec = this.findPreference("specialisation2");
		prefSecondSpec.setShouldDisableView(true);
		
		try {
			JSONObject responseObject = new JSONObject(sharedPrefs.getString("profileInfo", null));
			JSONArray profilesArray = responseObject.getJSONArray("Results");
			JSONObject myProfile = profilesArray.getJSONObject(0);
			String secondMajor = myProfile.getString("SecondMajor");
			
			if (secondMajor == null || secondMajor.length() <= 0) {
				sharedPrefsEditor.putBoolean("hasSecondMajor", false);
				hasSecondMajor = false;
				summary = "No Second Major";
				prefSecondSpec.setSummary(summary);
			
			} else {
				
				sharedPrefsEditor.putBoolean("hasSecondMajor", true);
				hasSecondMajor = true;
				bindPreferenceSummaryToValue(prefSecondSpec);
			}
			prefSecondSpec.setEnabled(hasSecondMajor);
			
			sharedPrefsEditor.commit();
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		bindPreferenceSummaryToValue(findPreference("specialisation"));
		
		
		/*
		 * setting up the announcements background refresh thingy
		 */
		
		Preference prefAnnouncements = findPreference("check_announcements");
		prefAnnouncements.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
			
			AnnouncementsAlarmReceiver alarm = new AnnouncementsAlarmReceiver();
			long interval;
			
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				// TODO Auto-generated method stub
				Boolean autoRefreshSelected = (Boolean) newValue;
				
				if (autoRefreshSelected) {
					SharedPreferences sharedPrefs = getSharedPreferences("NUSBuddyPrefs", MODE_PRIVATE);
					String intervalString = sharedPrefs.getString("check_announcements_interval", "21600");
					interval = Long.parseLong(intervalString) * 1000; // in milisecs
					alarm.setAlarm(Settings.this, interval);
					
					Toast.makeText(Settings.this, intervalString, Toast.LENGTH_LONG).show();
					
				} else {
					alarm.cancelAlarm(Settings.this);
					Toast.makeText(Settings.this, "cancel", Toast.LENGTH_LONG).show();
				}
				
				return true;
			}
			
		});
		
		
		Preference prefAnnouncementsInterval = findPreference("check_announcements_interval");
		prefAnnouncementsInterval.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
			
			AnnouncementsAlarmReceiver alarm = new AnnouncementsAlarmReceiver();
			
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				// TODO Auto-generated method stub
				
				String intervalString = (String) newValue;
				
				long interval = Long.parseLong(intervalString) * 1000;
				
				alarm.setAlarm(Settings.this, interval);
				
				return true;
			}
			
		});
		
		
		
		
	}

	/**
	 * Shows the simplified settings UI if the device configuration if the
	 * device configuration dictates that a simplified, single-pane UI should be
	 * shown.
	 */
	@SuppressWarnings("deprecation")
	private void setupSimplePreferencesScreen() {
		if (!isSimplePreferences(this)) {
			
			return;
		}

		// In the simplified UI, fragments are not used at all and we instead
		// use the older PreferenceActivity APIs.

		// Add 'general' preferences. This is blank.
		addPreferencesFromResource(R.xml.pref_general2); 

		// Add 'home page' preferences, and a corresponding header.
		PreferenceCategory fakeHeader = new PreferenceCategory(this);
		fakeHeader.setTitle("home");
		getPreferenceScreen().addPreference(fakeHeader);
		addPreferencesFromResource(R.xml.pref_home_page);

		// Add 'profile' preferences, and a corresponding header.
		fakeHeader = new PreferenceCategory(this);
		fakeHeader.setTitle("profile");
		getPreferenceScreen().addPreference(fakeHeader);
		addPreferencesFromResource(R.xml.pref_profile);
		
		// Add 'announcements' preferences, and a corresponding header.
		fakeHeader = new PreferenceCategory(this);
		fakeHeader.setTitle("Announcements");
		getPreferenceScreen().addPreference(fakeHeader);
		addPreferencesFromResource(R.xml.pref_announcements);

		// Bind the summaries of EditText/List/Dialog/Ringtone preferences to
		// their values. When their values change, their summaries are updated
		// to reflect the new value, per the Android Design guidelines.
		//bindPreferenceSummaryToValue(findPreference("example_text"));
		//bindPreferenceSummaryToValue(findPreference("example_list"));
		//bindPreferenceSummaryToValue(findPreference("notifications_new_message_ringtone"));
		//bindPreferenceSummaryToValue(findPreference("sync_frequency"));
		//bindPreferenceSummaryToValue(findPreference("show_homework"));
	}

	/** {@inheritDoc} */
	@Override
	public boolean onIsMultiPane() {
		return isXLargeTablet(this) && !isSimplePreferences(this);
	}

	/**
	 * Helper method to determine if the device has an extra-large screen. For
	 * example, 10" tablets are extra-large.
	 */
	private static boolean isXLargeTablet(Context context) {
		return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
	}

	/**
	 * Determines whether the simplified settings UI should be shown. This is
	 * true if this is forced via {@link #ALWAYS_SIMPLE_PREFS}, or the device
	 * doesn't have newer APIs like {@link PreferenceFragment}, or the device
	 * doesn't have an extra-large screen. In these cases, a single-pane
	 * "simplified" settings UI should be shown.
	 */
	private static boolean isSimplePreferences(Context context) {
		return ALWAYS_SIMPLE_PREFS
				|| Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB
				|| !isXLargeTablet(context);
	}

	/** {@inheritDoc} */
	@Override
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public void onBuildHeaders(List<Header> target) {
		if (!isSimplePreferences(this)) {
			loadHeadersFromResource(R.xml.pref_headers, target);
		}
	}

	/**
	 * A preference value change listener that updates the preference's summary
	 * to reflect its new value.
	 */
	private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
		@Override
		public boolean onPreferenceChange(Preference preference, Object value) {
			String stringValue = value.toString();

			if (preference instanceof ListPreference) {
				// For list preferences, look up the correct display value in
				// the preference's 'entries' list.
				ListPreference listPreference = (ListPreference) preference;
				int index = listPreference.findIndexOfValue(stringValue);

				// Set the summary to reflect the new value.
				preference
						.setSummary(index >= 0 ? listPreference.getEntries()[index]
								: null);

			} else if (preference instanceof RingtonePreference) {
				// For ringtone preferences, look up the correct display value
				// using RingtoneManager.
				if (TextUtils.isEmpty(stringValue)) {
					// Empty values correspond to 'silent' (no ringtone).
					preference.setSummary(R.string.pref_ringtone_silent);

				} else {
					Ringtone ringtone = RingtoneManager.getRingtone(
							preference.getContext(), Uri.parse(stringValue));

					if (ringtone == null) {
						// Clear the summary if there was a lookup error.
						preference.setSummary(null);
					} else {
						// Set the summary to reflect the new ringtone display
						// name.
						String name = ringtone
								.getTitle(preference.getContext());
						preference.setSummary(name);
					}
				}

			} else {
				// For all other preferences, set the summary to the value's
				// simple string representation.
				preference.setSummary(stringValue);
			}
			return true;
		}
	};

	/**
	 * Binds a preference's summary to its value. More specifically, when the
	 * preference's value is changed, its summary (line of text below the
	 * preference title) is updated to reflect the value. The summary is also
	 * immediately updated upon calling this method. The exact display format is
	 * dependent on the type of preference.
	 * 
	 * @see #sBindPreferenceSummaryToValueListener
	 */
	private static void bindPreferenceSummaryToValue(Preference preference) {
		// Set the listener to watch for value changes.
		preference
				.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

		// Trigger the listener immediately with the preference's
		// current value.
		sBindPreferenceSummaryToValueListener.onPreferenceChange(
				preference,
				PreferenceManager.getDefaultSharedPreferences(
						preference.getContext()).getString(preference.getKey(),
						""));
	}

	/**
	 * This fragment shows general preferences only. It is used when the
	 * activity is showing a two-pane settings UI.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static class GeneralPreferenceFragment extends PreferenceFragment {
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.pref_general);

			// Bind the summaries of EditText/List/Dialog/Ringtone preferences
			// to their values. When their values change, their summaries are
			// updated to reflect the new value, per the Android Design
			// guidelines.
			bindPreferenceSummaryToValue(findPreference("example_text"));
			bindPreferenceSummaryToValue(findPreference("example_list"));
		}
	}

	/**
	 * This fragment shows notification preferences only. It is used when the
	 * activity is showing a two-pane settings UI.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static class NotificationPreferenceFragment extends
			PreferenceFragment {
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.pref_notification);

			// Bind the summaries of EditText/List/Dialog/Ringtone preferences
			// to their values. When their values change, their summaries are
			// updated to reflect the new value, per the Android Design
			// guidelines.
			bindPreferenceSummaryToValue(findPreference("notifications_new_message_ringtone"));
		}
	}

	/**
	 * This fragment shows data and sync preferences only. It is used when the
	 * activity is showing a two-pane settings UI.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static class DataSyncPreferenceFragment extends PreferenceFragment {
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.pref_data_sync);

			// Bind the summaries of EditText/List/Dialog/Ringtone preferences
			// to their values. When their values change, their summaries are
			// updated to reflect the new value, per the Android Design
			// guidelines.
			bindPreferenceSummaryToValue(findPreference("sync_frequency"));
		}
	}
}
