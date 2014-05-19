package com.nick.nusbuddy;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

import javax.net.ssl.*;

import org.json.*;

import android.os.*;
import android.app.*;
import android.content.*;
import android.content.SharedPreferences.*;
import android.view.*;
import android.widget.*;

public class Login extends Activity {
	
	
	public class LoginTask extends AsyncTask<Void, Void, Boolean> {
		
		// exceptions
		Exception exception;
		boolean loginNoExceptions;
		
		// for posting
		URL loginUrl;
		HttpsURLConnection loginHttpsConnection;
		JSONObject loginJsonObject;
		OutputStreamWriter loginOutputWriter;
		
		// recieving
		int responseCode;
		String responseContent;
		
		// login response details
		boolean loginSuccess;
		String loginToken;
		String loginInfo;
		String loginValid;
		String loginValidJs;
		
		@Override
		protected void onPreExecute() {
			loginNoExceptions = true;
			
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			//Looper.prepare();
			try {
				
				// setting up the https connection
				loginUrl = new URL(loginUrlString);
				loginHttpsConnection = (HttpsURLConnection)loginUrl.openConnection();
				loginHttpsConnection.setConnectTimeout(60000);
				loginHttpsConnection.setReadTimeout(60000);
				loginHttpsConnection.setRequestProperty("Content-Type", "application/json");
				loginHttpsConnection.setDoInput(true);
				loginHttpsConnection.setDoOutput(true);
				
				// creating the json credentials object
				loginJsonObject = new JSONObject();
				loginJsonObject.put("APIKey", loginApiKey);
				loginJsonObject.put("UserID", loginUserId);
				loginJsonObject.put("Password", loginPassword);
				loginJsonObject.put("Domain", "");
				
				// sending the json object into the output stream as string
				loginOutputWriter = new OutputStreamWriter(loginHttpsConnection.getOutputStream());
				loginOutputWriter.write(loginJsonObject.toString());
				loginOutputWriter.flush();
				loginOutputWriter.close();
				
				// receiving the response from server
				responseCode = loginHttpsConnection.getResponseCode();
				
				if (responseCode == 200) {
					BufferedReader br = new BufferedReader(new InputStreamReader(loginHttpsConnection.getInputStream()));
					StringBuilder sb = new StringBuilder();
					String line = br.readLine();
					while(line != null) {
						sb.append(line + "\n");
						line = br.readLine();
					}
					br.close();
					responseContent = sb.toString();
					
					//Log.d("response", sb.toString());
				}
				
				
			} catch (Exception e) {
				e.printStackTrace();
				exception = e;
				loginNoExceptions = false;
				
			} finally {
				loginHttpsConnection.disconnect();
			}
			
			return loginNoExceptions;
		}
		
		@Override
		protected void onPostExecute(Boolean noExceptions) {
			pd.dismiss();
			if (noExceptions) {
				if (responseCode == 200 && responseContent.contains("Login_JSONResult")) {
					try {
						JSONObject result = new JSONObject(responseContent);
						result = result.getJSONObject("Login_JSONResult");
						loginToken = result.getString("Token");
						loginSuccess = result.getBoolean("Success");
						loginInfo = result.getString("Info");
						loginValid = result.getString("ValidTill");
						loginValidJs = result.getString("ValidTill_js");
						
						if (loginSuccess) {
							token = loginToken;
							
							prefsEdit.putString("userId", loginUserId);
							prefsEdit.putString("password", loginPassword);
							prefsEdit.putString("loginToken", loginToken);
							prefsEdit.putBoolean("loggedIn", true);
							prefsEdit.commit();
							
							new GetUserNameTask().execute(loginToken);
							
						} else {
							pd.setMessage(prefs.getString("loginToken", "Invalid UserID or Password"));
							pd.show();
							
						}
						
					} catch (Exception e) {
						e.printStackTrace();
						StackTraceElement[] a = e.getStackTrace();
						StringBuilder sb = new StringBuilder();
						sb.append(e.getMessage() + "\n");
						for (int i = 0; i < a.length; i++) {
							sb.append(a[i] + "\n");
						}
						pd.setMessage(sb.toString());
						pd.show();
					}
				} else {
					pd.setMessage(responseCode + ". ok =" + HttpURLConnection.HTTP_OK);
					pd.show();
				}
			} else {
				StackTraceElement[] a = exception.getStackTrace();
				StringBuilder sb = new StringBuilder();
				sb.append(exception.getMessage() + "\n");
				for (int i = 0; i < a.length; i++) {
					sb.append(a[i] + "\n");
				}
				pd.setMessage(sb.toString());
				pd.show();
			}
		}
		
	}
	
	public class VerifyTokenTask extends AsyncTask<String, Void, Boolean> {
		
		// exceptions
		Exception exception;
		boolean verifyNoExceptions;
		
		// for verifying
		URL verifyUrl;
		HttpsURLConnection verifyHttpsConnection;
		
		// stuff for verify
		String verifyToken;
		
		// recieving
		int responseCode;
		String responseContent;
		private String receivedToken;
		private boolean receivedSuccess;
		
		
		@Override
		protected void onPreExecute() {
			verifyNoExceptions = true;
			
		}
		
		@Override
		protected Boolean doInBackground(String... params) {
			//Looper.prepare();
			
			if (params == null) {
				return false;
			}
			
			verifyToken = params[0];
			try {
				verifyUrl = new URL("https://ivle.nus.edu.sg/api/Lapi.svc/Validate?APIKey=" + getString(R.string.api_key_mine) + "&Token="
						+ verifyToken + "&output=json");
				verifyHttpsConnection = (HttpsURLConnection) verifyUrl.openConnection();
				verifyHttpsConnection.setConnectTimeout(60000);
				verifyHttpsConnection.setReadTimeout(60000);
				
				responseCode = verifyHttpsConnection.getResponseCode();
				
				if (responseCode == 200) {
					BufferedReader br = new BufferedReader(new InputStreamReader(verifyHttpsConnection.getInputStream()));
					StringBuilder sb = new StringBuilder();
					String line = br.readLine();
					while (line != null) {
						sb.append(line);
						line = br.readLine();
					}
					br.close();
					responseContent = sb.toString();
				}
				
			} catch (Exception e) {
				e.printStackTrace();
				exception = e;
				verifyNoExceptions = false;
				
			} finally {
				verifyHttpsConnection.disconnect();
				
			}
			
			return verifyNoExceptions;
		}
		
		@Override
		protected void onPostExecute(Boolean noExceptions) {
			pd.dismiss();
			
			if (noExceptions) {
				
				try {
					JSONObject result = new JSONObject(responseContent);
					receivedToken = result.getString("Token");
					receivedSuccess = result.getBoolean("Success");
					
					if (receivedSuccess) {
						token = receivedToken;
						prefsEdit.putString("loginToken", receivedToken);
						prefsEdit.commit();
						
						GetUserNameTask getUserNameTask = new GetUserNameTask();
						getUserNameTask.execute(receivedToken);
						
					} else {
						pd.setMessage("Stored token invalid, please sign in again");
						pd.show();
					}
					
				} catch (JSONException e) {
					e.printStackTrace();
					pd.setMessage(e.toString());
					pd.show();
				}
				
			} else {
				pd.setMessage("hello "+exception.toString());
				pd.show();
				
			}
			
		}
		
	}
	
	public class GetUserNameTask extends AsyncTask<String, Void, Boolean> {

		// exceptions
		Exception exception;
		boolean userNameNoExceptions;
		
		// for getting
		URL userNameUrl;
		HttpsURLConnection userNameHttpsConnection;
		
		// stuff for get
		String userNameToken;
		
		// recieving
		int responseCode;
		String responseContent;
		
		
		@Override
		protected void onPreExecute() {
			userNameNoExceptions = true;
			exception = new Exception("empty exception");
		}
		
		@Override
		protected Boolean doInBackground(String... params) {
			try {
				//Looper.prepare();
				
				if (params == null) {
					return false;
				}
				
				userNameToken = params[0];
				try {
					userNameUrl = new URL("https://ivle.nus.edu.sg/api/Lapi.svc/UserName_Get?APIKey=" + getString(R.string.api_key_mine) + "&Token="
							+ userNameToken + "&output=json");
					userNameHttpsConnection = (HttpsURLConnection) userNameUrl.openConnection();
					userNameHttpsConnection.setConnectTimeout(60000);
					userNameHttpsConnection.setReadTimeout(60000);
					
					responseCode = userNameHttpsConnection.getResponseCode();
					
					if (responseCode == 200) {
						BufferedReader br = new BufferedReader(new InputStreamReader(userNameHttpsConnection.getInputStream()));
						StringBuilder sb = new StringBuilder();
						String line = br.readLine();
						while (line != null) {
							sb.append(line);
							line = br.readLine();
						}
						br.close();
						responseContent = sb.toString();
					}
					
				} catch (Exception e) {
					e.printStackTrace();
					exception = e;
					userNameNoExceptions = false;
					
				} finally {
					userNameHttpsConnection.disconnect();
					
				}
				
				return userNameNoExceptions;
			} catch (Exception e) {
				exception = e;
				return false;
			}
		}
		
		@Override
		protected void onPostExecute(Boolean noExceptions) {
			pd.dismiss();
			
			if (noExceptions && responseContent != null) {
				userName = responseContent.substring(1, responseContent.length()-1);
				prefsEdit.putString("userName", userName);
				prefsEdit.commit();
				
				new GetUserIdTask().execute(userNameToken);
				
			} else {
				pd.setMessage("Exception! " + exception.toString());
				pd.show();	
			}	
		}
	}
	
	public class GetUserIdTask extends AsyncTask<String, Void, Boolean> {

		// exceptions
		Exception exception;
		boolean userIdNoExceptions;
		
		// for getting
		URL userIdUrl;
		HttpsURLConnection userIdHttpsConnection;
		
		// stuff for get
		String userIdToken;
		
		// recieving
		int responseCode;
		String responseContent;
		
		
		@Override
		protected void onPreExecute() {
			userIdNoExceptions = true;
			exception = new Exception("empty exception");
		}
		
		@Override
		protected Boolean doInBackground(String... params) {
			try {
				//Looper.prepare();
				
				if (params == null) {
					return false;
				}
				
				userIdToken = params[0];
				try {
					userIdUrl = new URL("https://ivle.nus.edu.sg/api/Lapi.svc/UserID_Get?APIKey=" + getString(R.string.api_key_mine) + "&Token="
							+ userIdToken + "&output=json");
					userIdHttpsConnection = (HttpsURLConnection) userIdUrl.openConnection();
					userIdHttpsConnection.setConnectTimeout(60000);
					userIdHttpsConnection.setReadTimeout(60000);
					
					responseCode = userIdHttpsConnection.getResponseCode();
					
					if (responseCode == 200) {
						BufferedReader br = new BufferedReader(new InputStreamReader(userIdHttpsConnection.getInputStream()));
						StringBuilder sb = new StringBuilder();
						String line = br.readLine();
						while (line != null) {
							sb.append(line);
							line = br.readLine();
						}
						br.close();
						responseContent = sb.toString();
					}
					
				} catch (Exception e) {
					e.printStackTrace();
					exception = e;
					userIdNoExceptions = false;
					
				} finally {
					userIdHttpsConnection.disconnect();
					
				}
				
				return userIdNoExceptions;
				
			} catch (Exception e) {
				exception = e;
				return false;
			}
		}
		
		@Override
		protected void onPostExecute(Boolean noExceptions) {
			pd.dismiss();
			
			if (noExceptions && responseContent != null) {
				
				userId = responseContent.substring(1, responseContent.length()-1);
				prefsEdit.putString("userId", userId);
				prefsEdit.commit();
				
				pd.setMessage("Retrieving modules...");
				pd.show();
				
				new GetModulesTask().execute(userIdToken);
				
			} else {
				pd.setMessage("Exception! " + exception.toString());
				pd.show();	
			}	
		}
	}
	
	public class GetModulesTask extends AsyncTask<String, Void, Boolean> {
		
		HttpsURLConnection connection;
		String responseContent;
		int responseCode;
		private String loginToken;
		
		@Override
		protected void onPreExecute() {
			
		}
		
		@Override
		protected Boolean doInBackground(String... params) {
			
			loginToken = params[0];
			
			if (userId == null || loginToken == null) {
				return false;
			}
			
			try {
				URL url = new URL("https://ivle.nus.edu.sg/api/Lapi.svc/Modules_Student?APIKey=" + getString(R.string.api_key_mine)
							+ "&AuthToken=" + loginToken + "&StudentID=" + userId + "&Duration=0" + "&IncludeAllInfo=true" + "&output=json");
				connection = (HttpsURLConnection) url.openConnection();
				connection.setConnectTimeout(60000);
				connection.setReadTimeout(60000);
				
				responseCode = connection.getResponseCode();
				
				if (responseCode == 200) {
					BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
					StringBuilder sb = new StringBuilder();
					String line = br.readLine();
					while (line != null) {
						sb.append(line);
						line = br.readLine();
					}
					br.close();
					responseContent = sb.toString();
				}
				
				
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			return true;
		}
		
		@Override
		protected void onPostExecute(Boolean b) {
			pd.dismiss();
			if (responseCode == 200 && responseContent != null) {
				
				prefsEdit.putString("modulesInfo", responseContent);
				prefsEdit.commit();
				
				// go to home page
				Intent intent = new Intent(context, HomePage.class);
				startActivity(intent);
				finish();
				
			} else {
				pd.setMessage(responseCode+"");
				pd.show();
			}
		}
	}
	
	// this view and layout
	Context context;
	ProgressDialog pd;
	EditText editTextUserId;
	EditText editTextPassword;
	
	// verify login token
	VerifyTokenTask verifyTokenTask;
	
	// actual login
	LoginTask loginTask;
	String loginUrlString;
	String loginUserId;
	String loginPassword;
	String loginApiKey;
	
	// shared preferences
	SharedPreferences prefs;
	Editor prefsEdit;
	
	// actual user info
	String userId;
	String userName;
	String password;
	String token;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		context = this;
		pd = new ProgressDialog(context);
		editTextUserId = (EditText) findViewById(R.id.EditText_userid);
		editTextPassword = (EditText) findViewById(R.id.EditText_password);
		
		loginUrlString = "https://ivle.nus.edu.sg/api/Lapi.svc/Login_JSON";
		loginApiKey = getString(R.string.api_key_working);
		
		prefs = context.getSharedPreferences("NUSBuddyPrefs", MODE_PRIVATE);
		prefsEdit = prefs.edit();
		
		String oldToken = prefs.getString("loginToken", null);
		boolean loggedIn = prefs.getBoolean("loggedIn", false);
		Toast.makeText(context, oldToken, Toast.LENGTH_SHORT).show();
		
		if (loggedIn && oldToken != null) {
			pd.setMessage("Verifying stored token");
			pd.show();
			
			verifyTokenTask = new VerifyTokenTask();
			verifyTokenTask.execute(oldToken);
		} else {
			prefsEdit.putBoolean("loggedIn", false);
			prefsEdit.putString("loginToken", "");
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}
	
	// for the testing button there
	public void next(View v) {
    	Intent i = new Intent(this, HomePage.class);
    	startActivity(i);
    }
	
	// when sign in button is pressed
	// runs the async task
	public void onClickButtonLogin(View v) {
		pd.setMessage("Logging in... Please Wait... ");
		pd.show();
		
		loginUserId = editTextUserId.getText().toString();
		loginPassword = editTextPassword.getText().toString();
		loginTask = new LoginTask();
		loginTask.execute((Void) null);
	}

}
