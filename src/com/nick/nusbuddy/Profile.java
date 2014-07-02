package com.nick.nusbuddy;

import java.text.DecimalFormat;

import helpers.com.nick.nusbuddy.CheckDigitException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Profile extends RefreshableActivity implements ProfileAsyncTaskListener {
	

	private SharedPreferences sharedPrefs;
	private Editor sharedPrefsEditor;
	
	private DecimalFormat capFormat;
	
	private float currentCap;
	private float targetCap;
	private String spec1;
	private String spec2;
	
	private String authToken;
	private String apiKey;
	
	private String profileInfo;
	
	private JSONObject profile;
	private ProgressDialog pd;
	private AlertDialog.Builder b;

	@Override
	protected Activity getCurrentActivity() {
		return this;
	}

	@Override
	protected RefreshableActivity getCurrentRefreshableActivity() {
		return this;
	}

	@Override
	protected int getCurrentActivityLayout() {
		return R.layout.contents_profile;
	}

	@Override
	protected void onRefresh() {
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		doNotSetBackground = true;
		super.onCreate(savedInstanceState);

		sharedPrefs = this.getSharedPreferences("NUSBuddyPrefs", MODE_PRIVATE);
		sharedPrefsEditor = sharedPrefs.edit();
		
		capFormat = new DecimalFormat("0.00");
		
		currentCap = Float.parseFloat(sharedPrefs.getString("currentCap", -1+""));
		targetCap = Float.parseFloat(sharedPrefs.getString("targetCap", -1+""));
		spec1 = sharedPrefs.getString("specialisation", null);
		spec2 = sharedPrefs.getString("specialisation2", null);
		
		apiKey = getString(R.string.api_key_mine);
		authToken = sharedPrefs.getString("authToken", null);
		profileInfo = sharedPrefs.getString("profileInfo", null);
		
		pd = new ProgressDialog(this);
		
		pd.setMessage("Getting gradebooks...");
		pd.show();
		
		if (profileInfo == null) {
			runGetProfile();
		} else {
			runParseProfile();
		}
		
	}

	private void runGetProfile() {
		new GetProfileAsyncTask(this).execute(apiKey, authToken);
	}

	@Override
	public void onProfileTaskComplete(String responseContent) {
		profileInfo = responseContent;
		
		if (profileInfo == null) {
			return;
		}
		
		sharedPrefsEditor.putString("profileInfo", responseContent);
		sharedPrefsEditor.commit();
		
		runParseProfile();
	}

	private void runParseProfile() {
		
		try {
			JSONObject responseObject = new JSONObject(profileInfo);
			JSONArray profilesArray = responseObject.getJSONArray("Results");
			profile = profilesArray.getJSONObject(0);
			
			createPageContents();
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
	}


	@Override
	protected void createPageContents() {
		try {
			pd.setMessage(profile.toString());
			
			TextView textviewStudentName = (TextView) findViewById(R.id.Textview_student_name);
			TextView textviewMatricNumber = (TextView) findViewById(R.id.Textview_matric_number);
			TextView textviewEmail = (TextView) findViewById(R.id.Textview_email);
			TextView textviewFaculty = (TextView) findViewById(R.id.Textview_faculty);
			TextView textviewMajor1 = (TextView) findViewById(R.id.Textview_major_1);
			TextView textviewSpec1 = (TextView) findViewById(R.id.Textview_specialisation_1);
			LinearLayout layoutMajor2 = (LinearLayout) findViewById(R.id.Layout_major_2);
			TextView textviewMajor2 = (TextView) findViewById(R.id.Textview_major_2);
			TextView textviewSpec2 = (TextView) findViewById(R.id.Textview_specialisation_2);
			TextView textviewCurrentCapValue = (TextView) findViewById(R.id.Textview_current_cap_edit_value);
			TextView textviewTargetCapValue = (TextView) findViewById(R.id.Textview_target_cap_edit_value);
			
			// set name
			textviewStudentName.setText(profile.getString("Name"));
			
			// set matric num
			String userId = profile.getString("UserID");
			char checkDigit = getCheckDigit(userId);
			String matricNumber = (userId + checkDigit).toUpperCase();
			textviewMatricNumber.setText(matricNumber);
			
			// set email
			textviewEmail.setText(profile.getString("Email"));
			
			// set faculty
			textviewFaculty.setText(profile.getString("Faculty"));
			
			// set major 1 (always have)
			textviewMajor1.setText(profile.getString("FirstMajor"));
			
			// set major 2 or hide if does not exist
			String major2 = profile.getString("SecondMajor");
			if (major2 == null || major2.length() == 0) {
				layoutMajor2.setVisibility(View.GONE);
				sharedPrefsEditor.putBoolean("hasSecondMajor", false);
			} else {
				textviewMajor2.setText(major2);
				sharedPrefsEditor.putBoolean("hasSecondMajor", true);
			}
			sharedPrefsEditor.commit();
			
			// set up specialisations
			if (spec1 != null) {
				textviewSpec1.setText(spec1);
			}
			if (spec2 != null) {
				textviewSpec2.setText(spec2);
			}
			
			// set up cap values
			if (currentCap > 0) {
				textviewCurrentCapValue.setText(capFormat.format(currentCap));
			}
			if (targetCap > 0) {
				textviewTargetCapValue.setText(capFormat.format(targetCap));
			}
			
			// set up academic history
			
			// TODO
			// get the list of modules taken, then for each mod, get the MC from api.nusmods.com
			// http://api.nusmods.com/2012-2013/2/modules/CS2020.json
			
			
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (CheckDigitException e) {
			e.printStackTrace();
		}
		
		pd.dismiss();
		
	}
	
	public char getCheckDigit(String userId) throws CheckDigitException {
		char[] checkDigits = {'Y','X','W','U','R','N','M','L','J','H','E','A','B'};
		int divisor = 13;
		
		if (userId == null || userId.length() <= 0) {
			throw new CheckDigitException();
		}
		
		int sum = 0;
		for (int i = 0; i < userId.length(); i++) {
			char c = userId.charAt(i);
			if (Character.isDigit(c)) {
				sum += Integer.parseInt(c+"");
			}
		}
		
		int remainder = sum % divisor;
		
		return checkDigits[remainder];
	}
	
	public void popupEditSpecialisation(View v) {
		final TextView textviewSpecialisation = (TextView) v;
		LinearLayout layoutMajor = (LinearLayout) textviewSpecialisation.getParent();
		TextView textviewMajor = (TextView) layoutMajor.getChildAt(0);
		
		final String tag = (String) textviewSpecialisation.getTag(); 
		
		final View layout = View.inflate(this, R.layout.dialog_profile_specialisation, null);
		TextView message = (TextView) layout.findViewById(R.id.Textview_message);
		EditText input = (EditText) layout.findViewById(R.id.Edittext_input);
		message.append(textviewMajor.getText());
		input.setText(textviewSpecialisation.getText());
		
		b = new AlertDialog.Builder(this);
		b.setTitle(textviewMajor.getText());
		b.setView(layout);
		b.setCancelable(true);
		b.setPositiveButton("Set", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				EditText input = (EditText) layout.findViewById(R.id.Edittext_input);
				String value = input.getText().toString();
				if (value.length() <= 0) {
					return;
				} else {
					// set the view
					textviewSpecialisation.setText(value);
					// set the shared prefs
					sharedPrefsEditor.putString(tag, value);
					sharedPrefsEditor.commit();
					
					dialog.dismiss();
				}
			}
		});
		b.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		b.setNeutralButton("Clear", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// clear view
				textviewSpecialisation.setText(null);
				// clear shared prefs
				sharedPrefsEditor.remove(tag);
				sharedPrefsEditor.commit();
				dialog.dismiss();
			}
		});
		
		b.show();
	}
	
	public void popupEditCap(View v) {
		final TextView textviewCapValue = (TextView) v;
		LinearLayout layoutCapEdit = (LinearLayout) textviewCapValue.getParent();
		TextView textviewCapEditHeader = (TextView) layoutCapEdit.getChildAt(0);
		
		final String tag = (String) textviewCapValue.getTag();
		
		final View layout = View.inflate(this, R.layout.dialog_profile_cap_edit, null);
		TextView message = (TextView) layout.findViewById(R.id.Textview_message);
		EditText input = (EditText) layout.findViewById(R.id.Edittext_input);
		message.append(textviewCapEditHeader.getText());
		input.setText(textviewCapValue.getText());
		
		b = new AlertDialog.Builder(this);
		b.setTitle(textviewCapEditHeader.getText());
		b.setView(layout);
		b.setCancelable(true);
		b.setPositiveButton("Set", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				EditText input = (EditText) layout.findViewById(R.id.Edittext_input);
				String value = input.getText().toString();
				if (value.length() <= 0) {
					return;
				}
				
				String capString = "";
				// check the value.
				double capValue = Double.parseDouble(value);
				if (capValue > 5 || capValue < 0) {
					Toast.makeText(Profile.this, "Invalid CAP value", Toast.LENGTH_LONG).show();
				} else {
					capString = capFormat.format(capValue);
					
					// set the view
					textviewCapValue.setText(capString);
					// save it in shared prefs.
					sharedPrefsEditor.putString(tag, capString);
					sharedPrefsEditor.commit();
					
					dialog.dismiss();
				}
			}
		});
		b.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		b.setNeutralButton("Clear", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// clear the view
				textviewCapValue.setText(null);
				// clear from shared prefs.
				sharedPrefsEditor.remove(tag);
				sharedPrefsEditor.commit();
				
				dialog.dismiss();
			}
		});
		
		b.show();
	}
	
	public void changeDisplayPic(View v) {
		
	}

}
