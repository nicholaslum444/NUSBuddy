package com.nick.nusbuddy;

import helpers.com.nick.nusbuddy.CheckDigitException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.InputType;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Profile extends RefreshableActivity implements ProfileAsyncTaskListener {
	

	private SharedPreferences sharedPrefs;
	private Editor sharedPrefsEditor;
	
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
		super.onCreate(savedInstanceState);

		sharedPrefs = this.getSharedPreferences("NUSBuddyPrefs", MODE_PRIVATE);
		sharedPrefsEditor = sharedPrefs.edit();
		
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
			LinearLayout layoutMajor2 = (LinearLayout) findViewById(R.id.Layout_major_2);
			TextView textviewMajor2 = (TextView) findViewById(R.id.Textview_major_2);
			
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
			} else {
				textviewMajor2.setText(major2);
			}
			
			
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
		b = new AlertDialog.Builder(this);
		b.setTitle(textviewMajor.getText());
		final EditText specInput = new EditText(this);
		specInput.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
		
		final View layout = View.inflate(this, R.layout.dialog_profile_specialisation, null);
		TextView message = (TextView) layout.findViewById(R.id.Textview_message);
		message.append(textviewMajor.getText());
		
		b.setView(layout);
		b.setCancelable(true);
		b.setPositiveButton("Set", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				EditText input = (EditText) layout.findViewById(R.id.Edittext_input);
				textviewSpecialisation.setText(input.getText());
				dialog.dismiss();
			}
		});
		b.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		b.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				textviewSpecialisation.setText(null);
				dialog.dismiss();
			}
		});
		
		b.show();
	}

}
