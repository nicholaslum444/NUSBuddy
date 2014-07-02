package com.nick.nusbuddy;

import helpers.com.nick.nusbuddy.GlobalValues;

import java.util.Locale;

import org.json.*;

import android.net.ConnectivityManager;
import android.os.*;
import android.app.*;
import android.content.*;
import android.content.SharedPreferences.*;
import android.graphics.Typeface;
import android.util.Log;
import android.view.*;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import android.widget.TextView.OnEditorActionListener;

public class Login extends Activity implements 
StudentNameAsyncTaskListener,
ModulesAsyncTaskListener,
TokenValidateAsyncTaskListener,
LoginAsyncTaskListener,
ProfileAsyncTaskListener {
	 
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
		setContentView(R.layout.contents_login);
		//getActionBar().hide(); 
		
		TextView t = (TextView) findViewById(R.id.TextView_login);
		t.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Bauhaus 93.ttf"));
		
		editTextUserId = (EditText) findViewById(R.id.EditText_userid);
		editTextPassword = (EditText) findViewById(R.id.EditText_password);
		editTextPassword.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
	        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
	            if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
	            	onClickButtonLogin(v);
	            }    
	            return false;
	        }
	    });
		
		context = this;
		pd = new ProgressDialog(context);
		
		apiKey = getString(R.string.api_key_working);
		
		sharedPrefs = context.getSharedPreferences("NUSBuddyPrefs", MODE_PRIVATE);
		sharedPrefsEditor = sharedPrefs.edit();
		
		String oldToken = sharedPrefs.getString("authToken", null);
		String oldUserId = sharedPrefs.getString("userId", null);
		String oldPassword = sharedPrefs.getString("password", null);
		if (oldUserId != null && oldPassword != null) {
			editTextUserId.setText(oldUserId);
			editTextPassword.setText(oldPassword);
		}
		
		if (oldToken != null) {
			pd.setMessage("Verifying stored token");
			//pd.show();
			
			showSplashScreen(true);
			runValidate(oldToken);
			
		}
		
		
	}
	
	public void showSplashScreen(boolean show) {
		LinearLayout fields = (LinearLayout) findViewById(R.id.Layout_login_fields);
		ProgressBar pb = (ProgressBar) findViewById(R.id.Progress_login);
		if (show) {
			InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(editTextPassword.getWindowToken(), 0);
			
			fields.setVisibility(View.GONE);
			pb.setVisibility(View.VISIBLE);
		} else {
			fields.setVisibility(View.VISIBLE);
			pb.setVisibility(View.GONE);
		}
	}
	
	// when sign in button is pressed
	// runs the async task
	public void onClickButtonLogin(View v) {
		if (!hasInternetConnection()) {
			showSplashScreen(false);
			Toast.makeText(this, "Please check your Wifi or 3G connection", Toast.LENGTH_LONG).show();
			return;
		}
		pd.setMessage("Logging in... Please Wait... ");
		//pd.show();
		
		showSplashScreen(true);
		runLogin();
	}
	
	public boolean hasInternetConnection() {
	    ConnectivityManager localConnectivityManager = (ConnectivityManager)getSystemService("connectivity");
	    return (localConnectivityManager.getActiveNetworkInfo() != null) && 
	    		(localConnectivityManager.getActiveNetworkInfo().isAvailable()) && 
	    		(localConnectivityManager.getActiveNetworkInfo().isConnected());
	}
	
	public void runLogin() {
		if (!hasInternetConnection()) {
			showSplashScreen(false);
			Toast.makeText(this, "Please check your Wifi or 3G connection", Toast.LENGTH_LONG).show();
			return;
		}
		
		inputUserId = editTextUserId.getText().toString();
		inputPassword = editTextPassword.getText().toString();
		
		if (inputUserId.length() <= 0 || inputPassword.length() <= 0) {
			Toast.makeText(this, "Please enter UserID and password.\nOmit \"NUSSTU\\\" from UserID.", Toast.LENGTH_LONG).show();
			showSplashScreen(false);
		} else {
			LoginAsyncTask loginTask = new LoginAsyncTask(this);
			loginTask.execute(apiKey, inputUserId, inputPassword);
		}
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
				
				GlobalValues.userId = userId.toLowerCase(Locale.US);
				
				Log.w("api", apiKey);
				Log.w("token", authToken);
				
				runGetStudentName(authToken);
				
				
			} else {
				Toast.makeText(this, "Invalid UserID or password.\nOmit \"NUSSTU\\\" from UserID.", Toast.LENGTH_LONG).show();
				showSplashScreen(false);
			}
			
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	
	public void runValidate(String oldToken) {
		if (!hasInternetConnection()) {
			showSplashScreen(false);
			Toast.makeText(this, "Please check your Wifi or 3G connection", Toast.LENGTH_LONG).show();
			return;
		}
		
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
				
				String userid = sharedPrefs.getString("userId", null);
				GlobalValues.userId = userid;
				
				Log.w("usr", sharedPrefs.getString("userId", "no usr"));
				Log.w("pwd", sharedPrefs.getString("password", "no pwd"));
				
				runGetStudentName(authToken);
				
			} else {
				String savedUserId = sharedPrefs.getString("userId", null);
				String savedPassword = sharedPrefs.getString("password", null);
				if (savedUserId == null || savedPassword == null) {
					Toast.makeText(this, "Please sign in with your IVLE UserID and password.\nOmit \"NUSSTU\\\" from UserID.", Toast.LENGTH_LONG).show();
					showSplashScreen(false);
				} else {
					editTextUserId.setText(savedUserId);
					editTextPassword.setText(savedPassword);
					showSplashScreen(true);
					runLogin();
				}
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
			pd.setMessage(e.toString());
			//pd.show();
		}
		
	}
	
	public void runGetStudentName(String authToken) {
		if (!hasInternetConnection()) {
			showSplashScreen(false);
			Toast.makeText(this, "Please check your Wifi or 3G connection", Toast.LENGTH_LONG).show();
			return;
		}
		
		if (sharedPrefs.getString("studentName", null) != null) {
			runGetModules(authToken, sharedPrefs.getString("userId", null));
			return;
		}
		
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
		if (!hasInternetConnection()) {
			showSplashScreen(false);
			Toast.makeText(this, "Please check your Wifi or 3G connection", Toast.LENGTH_LONG).show();
			return;
		}
		
		if (sharedPrefs.getString("modulesInfo", null) != null) {
			runGetProfile(authToken);
			return;
		}
		
		pd.setMessage("Retrieving modules");
		
		GetModulesAsyncTask modulesTask = new GetModulesAsyncTask(this);
		
		modulesTask.execute(apiKey, authToken, userId);
	} 
	
	@Override
	public void onModulesTaskComplete(String modulesResponse) {
		sharedPrefsEditor.putString("modulesInfo", modulesResponse);
		sharedPrefsEditor.commit();
		
		runGetProfile(authToken);
	}
	
	public void runGetProfile(String authToken) {
		if (!hasInternetConnection()) {
			showSplashScreen(false);
			Toast.makeText(this, "Please check your Wifi or 3G connection", Toast.LENGTH_LONG).show();
			return;
		}
		
		if (sharedPrefs.getString("profileInfo", null) != null) {
			goToHome();
			return;
		}
		
		pd.setMessage("Retrieving profile");
		
		GetProfileAsyncTask profileTask = new GetProfileAsyncTask(this);
		
		profileTask.execute(apiKey, authToken);
	}
	
	@Override
	public void onProfileTaskComplete(String profileResponse) {
		sharedPrefsEditor.putString("profileInfo", profileResponse);
		sharedPrefsEditor.commit();
		pd.dismiss();
		
		goToHome();
	}
	
	public void goToHome() {
		// go to home page
		Intent intent = new Intent(this, HomePage.class); 
		startActivity(intent);
		finish();
	}
	

	
	// for the testing button there. goes into app without login.
	public void next(View v) {
    	Intent i = new Intent(this, HomePage.class);
    	startActivity(i); 
    }
}
