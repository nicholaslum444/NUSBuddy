package com.nick.nusbuddy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import javax.net.ssl.HttpsURLConnection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Gradebook extends BaseActivity {

	public class GetModulesTask extends AsyncTask<Void, Void, Boolean> {
		
		HttpsURLConnection connection;
		String responseContent;
		int responseCode;
		
		@Override
		protected void onPreExecute() {
			
		}
		
		@Override
		protected Boolean doInBackground(Void... params) {
			
			responseContent = modulesInfo;
			
			if (responseContent == null) {
			
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
			
			} else {
				responseCode = 200;
				return true;
			}
		}
		
		@Override
		protected void onPostExecute(Boolean b) {
			
			pd.dismiss();
			
			if (responseCode == 200 && responseContent != null) {
				try {
					sharedPrefsEditor.putString("modulesInfo", responseContent);
					sharedPrefsEditor.commit();
					
					JSONObject responseObject = new JSONObject(responseContent);
					JSONArray modulesArray = responseObject.getJSONArray("Results");
					numOfModules = modulesArray.length();
					
					modulesList = new ArrayList<JSONObject>(); 
					for (int i = 0; i < numOfModules; i++) {
						modulesList.add(modulesArray.getJSONObject(i));
					}
					
					modulesCodeList = new ArrayList<String>();
					modulesGradebooksList = new ArrayList<JSONArray>();
					for (int i = 0; i < numOfModules; i++) {
						
						JSONObject obj = modulesList.get(i);
						modulesCodeList.add(obj.getString("CourseCode"));
						modulesGradebooksList.add(obj.getJSONArray("Gradebooks"));
					}
					
					// modulegradebookslist = [ [gb1:{}, gb2:{},...], [gb1:{}, gb2:{},...], [gb1:{}, gb2:{},...], ...]
					
					
					// each gradebooks[] array contains gradebook sets.
					// need to extract out all the gradebooks from each set.
					
					// each gradebook array contains gradebook categories objects.
					// each gradebook category object has an items array
					// each items array contains individual test objects
					// each object has name, grade, etc.
					
					
					allModules = new ArrayList<ArrayList<JSONObject>>();
					
					// for each gradebook array in module gradebook list
					for (int i = 0; i < numOfModules; i++) {
						// get the array
						JSONArray gradebookArray = modulesGradebooksList.get(i);
						// if > 0 categories. 
						int numOfCategories = gradebookArray.length();
						if (numOfCategories > 0) {
							
							ArrayList<JSONObject> perModuleItemList = new ArrayList<JSONObject>();
							
							// for each category in the gradebook array
							for (int j = 0; j < numOfCategories; j++) {
								// get the items array
								JSONObject gradebookCategory = gradebookArray.getJSONObject(j);
								JSONArray gradebookItems = gradebookCategory.getJSONArray("Items");
								// if > 0 items
								int numOfItems = gradebookItems.length();
								if (numOfItems > 0) {
									// for each test in items
									for (int k = 0; k < numOfItems; k++) {
										// get the individual item object
										JSONObject item = gradebookItems.getJSONObject(k);
										// put the item in the arraylist
										perModuleItemList.add(item);
										
										// get ItemName, AverageMedianMarks, MarksObtained, Grade, Percentile
										/*String itemName = item.getString("ItemName");
										String averageMedianMarks = item.getString("AverageMedianMarks");
										String grade = item.getString("Grade");
										String percentile = item.getString("Percentile");
										String MarksObtained = item.getString("MarksObtained");
										String maxMarks = ""+item.getInt("MaxMarks");*/
									}
								}
							}
							allModules.add(perModuleItemList);
							
						} else { 
							// no categories, so the module contents is null.
							allModules.add(null);
						}
					}
					
					createPageContents();
					
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} else {
				pd.setMessage(""+responseCode);
				pd.show();
			}
		}
	}
	
	private ArrayList<ArrayList<JSONObject>> allModules;
	public ArrayList<JSONObject> modulesList;
	public ArrayList<String> modulesCodeList;
	public ArrayList<JSONArray> modulesGradebooksList;
	public int numOfModules;
	
	
	private Context context;
	private SharedPreferences sharedPrefs;
	private Editor sharedPrefsEditor;
	private String userId;
	private String loginToken;
	private String modulesInfo;
	private ProgressDialog pd;

	@Override
	protected Activity getCurrentActivity() {
		return this;
	}
	
	@Override
	protected int getCurrentActivityLayout() {
		return R.layout.contents_gradebook;
	}
	
	@Override
	protected void createPageContents() {
		pd.setMessage(allModules.toString());
		pd.setCanceledOnTouchOutside(true);
		pd.show();
		Log.d("asd", allModules.toString());
		
		
		LinearLayout layoutGradebook = (LinearLayout) findViewById(R.id.Layout_gradebook);
		
		
		// allModules
		// each entry in allModules contains an arraylist of gradebook items.
		
		// for each module, add its container if there are items in the gradebook.
		for (int i = 0; i < numOfModules; i++) {
			ArrayList<JSONObject> items = allModules.get(i);
			if (items != null) {
				
				LinearLayout containerForModule = (LinearLayout) View.inflate(this, R.layout.container_gradebook_module, null);
				layoutGradebook.addView(containerForModule);
				
				TextView containerName = (TextView) findViewById(R.id.TextView_gradebook_module_name);
				containerName.setText(modulesCodeList.get(i));
				
				LinearLayout containerForGrades = (LinearLayout) findViewById(R.id.Layout_gradebook_module_grades);
			}
		}
		
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		context = this;
		sharedPrefs = this.getSharedPreferences("NUSBuddyPrefs", MODE_PRIVATE);
		sharedPrefsEditor = sharedPrefs.edit();
		
		userId = sharedPrefs.getString("userId", null);
		loginToken = sharedPrefs.getString("loginToken", null);
		modulesInfo = sharedPrefs.getString("modulesInfo", null);
		
		Log.d("token", loginToken);
		
		pd = new ProgressDialog(context);
		
		pd.setMessage(userId);
		pd.show();
		
		new GetModulesTask().execute((Void)null);
		
	}

}
