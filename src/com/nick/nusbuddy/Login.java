package com.nick.nusbuddy;

import java.util.Locale;
import org.json.*;

import android.os.*;
import android.app.*;
import android.content.*;
import android.content.SharedPreferences.*;
import android.view.*;
import android.widget.*;

public class Login extends Activity implements 
StudentNameAsyncTaskListener,
ModulesAsyncTaskListener,
TokenValidateAsyncTaskListener,
LoginAsyncTaskListener {
	
	// this view and layout
	Context context;
	ProgressDialog pd;
	EditText editTextUserId;
	EditText editTextPassword;
	
	// persisting values
	String apiKey;
	String authToken;
	
	
	// login values
	String inputUserId;
	String inputPassword;
	
	// shared preferences
	SharedPreferences sharedPrefs;
	Editor sharedPrefsEditor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		context = this;
		pd = new ProgressDialog(context);
		
		editTextUserId = (EditText) findViewById(R.id.EditText_userid);
		editTextPassword = (EditText) findViewById(R.id.EditText_password);
		
		apiKey = getString(R.string.api_key_working);
		
		sharedPrefs = context.getSharedPreferences("NUSBuddyPrefs", MODE_PRIVATE);
		sharedPrefsEditor = sharedPrefs.edit();
		
		String oldToken = sharedPrefs.getString("authToken", null);
		//Toast.makeText(context, oldToken, Toast.LENGTH_SHORT).show();
		
		if (oldToken != null) {
			pd.setMessage("Verifying stored token");
			pd.show();
			
			runValidate(oldToken);
			
		}
		
		
	}
	
	// for the testing button there. goes into app without login.
	public void next(View v) {
    	Intent i = new Intent(this, HomePage.class);
    	startActivity(i); 
    }
	
	
	// when sign in button is pressed
	// runs the async task
	public void onClickButtonLogin(View v) {
		pd.setMessage("Logging in... Please Wait... ");
		pd.show();
		
		inputUserId = editTextUserId.getText().toString();
		inputPassword = editTextPassword.getText().toString();
		
		if (inputUserId.length() <= 0 || inputPassword.length() <= 0) {
			pd.setMessage("Alert: please key in valid user id and password");
		} else { 
			runLogin(inputUserId, inputPassword);
		}
	}
	
	public void runLogin(String userId, String password) {
		LoginAsyncTask loginTask = new LoginAsyncTask(this);
		loginTask.execute(apiKey, userId, password);
		
	}
	
	@Override
	public void onLoginTaskComplete(String responseContent, String userId, String password) {
		try {
			
			JSONObject loginResponse = new JSONObject(responseContent);
			JSONObject loginJSONResult = loginResponse.getJSONObject("Login_JSONResult");
			boolean loginSuccess = loginJSONResult.getBoolean("Success");
			
			if (loginSuccess) {
				authToken = loginJSONResult.getString("Token");
				
				sharedPrefsEditor.putString("authToken", authToken);
				sharedPrefsEditor.putString("userId", userId.toLowerCase(Locale.US));
				sharedPrefsEditor.putString("password", password);
				sharedPrefsEditor.commit();
				
				runGetStudentName(authToken);
				
				
			} else {
				pd.setMessage("Login Failed: Invalid userID or password. Please sign in again.");
			}
			
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	
	public void runValidate(String oldToken) {
		TokenValidateAsyncTask validateTask = new TokenValidateAsyncTask(this);
		validateTask.execute(apiKey, oldToken);
		
	}
	
	@Override
	public void onTokenValidateTaskComplete(String responseContent) {
		try {
			
			JSONObject result = new JSONObject(responseContent);
			boolean receivedSuccess = result.getBoolean("Success");
			
			if (receivedSuccess) {
				authToken = result.getString("Token");
				sharedPrefsEditor.putString("authToken", authToken);
				sharedPrefsEditor.commit();
				
				runGetStudentName(authToken);
				
			} else {
				String savedUserId = sharedPrefs.getString("userId", null);
				String savedPassword = sharedPrefs.getString("password", null);
				if (savedUserId == null || savedPassword == null) {
					pd.setMessage("Please sign in with your NUS UserID and Password");
				} else {
					runLogin(savedUserId, savedPassword);
				}
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
			pd.setMessage(e.toString());
			pd.show();
		}
		
	}
	
	public void runGetStudentName(String authToken) {
		
		GetStudentNameAsyncTask t = new GetStudentNameAsyncTask(this);
		
		t.execute(apiKey, authToken);
	}
	
	@Override
	public void onStudentNameTaskComplete(String nameResponse) {
		sharedPrefsEditor.putString("studentName", nameResponse.substring(1, nameResponse.length()-1));
		sharedPrefsEditor.commit();
		
		runGetModules(authToken, sharedPrefs.getString("userId", null));
	}
	
	
	public void runGetModules(String authToken, String userId) {
		
		pd.setMessage("Retrieving modules");
		
		GetModulesAsyncTask modulesTask = new GetModulesAsyncTask(this);
		
		modulesTask.execute(apiKey, authToken, userId);
	} 
	
	@Override
	public void onModulesTaskComplete(String modulesResponse) {
		sharedPrefsEditor.putString("modulesInfo", modulesResponse);
		sharedPrefsEditor.commit();
		pd.dismiss();
		
		// go to home page
		Intent intent = new Intent(this, HomePage.class); 
		startActivity(intent);
		finish(); 
	}
}
