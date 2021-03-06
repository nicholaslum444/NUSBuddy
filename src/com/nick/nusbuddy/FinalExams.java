package com.nick.nusbuddy;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TableLayout.LayoutParams;

public class FinalExams extends RefreshableActivity implements ModulesAsyncTaskListener, FinalExamAsyncTaskListener {

	public SharedPreferences sharedPrefs;
	public Editor sharedPrefsEditor;
	public Context context;
	public ProgressDialog pd;
	public AlertDialog.Builder b;
	
	public String apiKey;
	public String authToken;
	public String userId;
	public String modulesInfo;
	private int numOfModules;
	private ArrayList<JSONObject> modulesList;
	private ArrayList<String> modulesCodeList;
	private ArrayList<String> modulesIdList;
	private ArrayList<String> modulesFinalsDataList;
	
	int count;
	
	


	@Override
	protected Activity getCurrentActivity() {
		return this;
	}

	@Override
	protected int getCurrentActivityLayout() {
		return R.layout.contents_final_exams;
	}
	
	@Override
	protected RefreshableActivity getCurrentRefreshableActivity() {
		return this;
	}
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		context = this;
		sharedPrefs = this.getSharedPreferences("NUSBuddyPrefs", MODE_PRIVATE);
		sharedPrefsEditor = sharedPrefs.edit();
		sharedPrefsEditor.commit();
		
		apiKey = getString(R.string.api_key_mine);
		userId = sharedPrefs.getString("userId", null);
		authToken = sharedPrefs.getString("authToken", null);
		modulesInfo = sharedPrefs.getString("modulesInfo", null);
		
		Log.d("apiKey", apiKey);
		Log.d("authToken", authToken);
		
		pd = new ProgressDialog(context);
		b = new AlertDialog.Builder(this);
		b.setCancelable(true);
		b.setPositiveButton("OK", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
			
		});
		
		pd.setMessage("Getting exam details...");
		pd.show();
		
		if (modulesInfo == null) {
			
			runGetModules();
			
		} else {
			
			runParseModules();
		}
		
		
	}
	
	public boolean hasInternetConnection() {
	    ConnectivityManager localConnectivityManager = (ConnectivityManager)getSystemService("connectivity");
	    return (localConnectivityManager.getActiveNetworkInfo() != null) && 
	    		(localConnectivityManager.getActiveNetworkInfo().isAvailable()) && 
	    		(localConnectivityManager.getActiveNetworkInfo().isConnected());
	}
	
	@Override
	protected void onRefresh() {
		if (!hasInternetConnection()) {
			Toast.makeText(this, "Please check your Wifi or 3G connection", Toast.LENGTH_LONG).show();
			return;
		}
		pd.setMessage("Refreshing...");
		pd.show();
		super.onCreate(null);
		runGetModules();
	}

	private void runGetModules() {
		if (!hasInternetConnection()) {
			Toast.makeText(this, "Please check your Wifi or 3G connection", Toast.LENGTH_LONG).show();
			return;
		}
		new GetModulesAsyncTask(this).execute(apiKey, authToken, userId);
	}

	@Override
	public void onModulesTaskComplete(String responseContent) {
		modulesInfo = responseContent;
		sharedPrefsEditor.putString("modulesInfo", responseContent);
		sharedPrefsEditor.commit();
		
		runParseModules();
	}

	private void runParseModules() {
		
		// get an arraylist of module IDs
		// then for each ID, we run the getFinals. 
		
		try {
			JSONObject responseObject = new JSONObject(modulesInfo);
			JSONArray modulesArray = responseObject.getJSONArray("Results");
			numOfModules = modulesArray.length();
			
			
			
			modulesList = new ArrayList<JSONObject>(); 
			for (int i = 0; i < numOfModules; i++) {
				//for (int j = 0; j < 10; j++) {
				modulesList.add(modulesArray.getJSONObject(i));
				//}
			}
			
			modulesCodeList = new ArrayList<String>();
			modulesIdList = new ArrayList<String>();
			
			for (int i = 0; i < modulesList.size(); i++) {
				JSONObject obj = modulesList.get(i);
				modulesCodeList.add(obj.getString("CourseCode"));
				modulesIdList.add(obj.getString("ID"));
			}
			//pd.setMessage(modulesCodeList.toString() + " " + modulesIdList.toString());
			
			modulesFinalsDataList = new ArrayList<String>();
			
			runGetFinals();
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	private void runGetFinals() {
		if (!hasInternetConnection()) {
			Toast.makeText(this, "Please check your Wifi or 3G connection", Toast.LENGTH_LONG).show();
			return;
		}
		if (modulesIdList.size() > 0) {
			String id = modulesIdList.remove(0);
			new GetFinalExamAsyncTask(this).execute(apiKey, authToken, id);
		} else {
			check();
		}
		
	}
	
	@Override
	public void onFinalExamTaskComplete(String responseContent) {
		modulesFinalsDataList.add(responseContent);
		check();
	}
	
	public void check() {
		if (modulesFinalsDataList.size() == modulesList.size()) {
			
			createPageContents();
		} else {
			runGetFinals();
		}
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
	@Override
	protected void createPageContents() {
		
		LinearLayout layoutFinalExams = (LinearLayout) findViewById(R.id.Layout_final_exams);
		
		// FOR TESTING PURPOSES ONLY
		// for (int h = 0; h < 15; h++) {
		// THIS WILL DUPLICATE MODULES 4 TIMES TO SIMULATE MANY MODULES
		
			
		for (int i = 0; i < numOfModules; i++) {
			
			final String finalsJSONString = modulesFinalsDataList.get(i);
			
			if (finalsJSONString != null) {
				//Toast.makeText(this, finalsJSONString, Toast.LENGTH_LONG).show();
				try {
					JSONObject obj = new JSONObject(finalsJSONString);
					JSONObject finalsData = obj.getJSONArray("Results").getJSONObject(0);
					
					String unixTimeString = finalsData.getString("ExamDate");
					final String moduleCode = finalsData.getString("ModuleCode");
					String session = finalsData.getString("ExamSession");
					final String examInfo = finalsData.getString("ExamInfo");

					// parsing the time
					long unixTimeLong = Long.parseLong(unixTimeString.substring(6, 19));
					SimpleDateFormat dateFormat = new SimpleDateFormat("d MMM yyyy", Locale.ENGLISH);
					SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
					String examDate = dateFormat.format(unixTimeLong);
					String examTime = timeFormat.format(unixTimeLong);
					
					LinearLayout containerForModule = (LinearLayout) View.inflate(this, R.layout.container_final_exam_module, layoutFinalExams);
					//layoutFinalExams.addView(containerForModule);
					//containerForModule.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
					
					TextView containerName = (TextView) findViewById(R.id.TextView_final_exams_module_name);
					containerName.setText(moduleCode);
					
					TextView examDateValue = (TextView) findViewById(R.id.TextView_final_exams_exam_date_value);
					examDateValue.setText(examDate);
					
					TextView examTimeValue = (TextView) findViewById(R.id.TextView_final_exams_exam_time_value);
					examTimeValue.setText(examTime);
					
					TextView examSessionValue = (TextView) findViewById(R.id.TextView_final_exams_exam_session_value);
					examSessionValue.setText(session);
					
					LinearLayout containerForModuleDetails = (LinearLayout) findViewById(R.id.Layout_final_exam_module_details);
					
					// uniquely set all the IDs so that they don't conflict when i add more of this layout.
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
						containerName.setId(View.generateViewId());
						examDateValue.setId(View.generateViewId());
						examTimeValue.setId(View.generateViewId());
						examSessionValue.setId(View.generateViewId());
						containerForModuleDetails.setId(View.generateViewId());
					} else {
						containerName.setId(new Random().nextInt(Integer.MAX_VALUE));
						examDateValue.setId(new Random().nextInt(Integer.MAX_VALUE));
						examTimeValue.setId(new Random().nextInt(Integer.MAX_VALUE));
						examSessionValue.setId(new Random().nextInt(Integer.MAX_VALUE));
						containerForModuleDetails.setId(View.generateViewId());
					}
					
					// TODO set a clicklestener to show the "exam info" popup when clicked
					containerForModuleDetails.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							//Toast.makeText(FinalExams.this, finalsJSONString, Toast.LENGTH_LONG).show();
							b.setTitle(moduleCode);
							b.setMessage(finalsJSONString);
							b.create().show();
						}
						
					});
					
					
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} else { 
				
			}
		}
		
		pd.dismiss();
		
		// THE TESTING } IS HERE
		//}
		// THE TESTING } IS HERE
	}

}
