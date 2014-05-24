package com.nick.nusbuddy;

import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Gradebook extends RefreshableActivity implements ModulesAsyncTaskListener {
	
	
	public int numOfModules;
	private String modulesInfo;
	public ArrayList<ArrayList<JSONObject>> perModuleItemsList; // 1 internal arraylist = 1 module
	public ArrayList<JSONObject> modulesList;
	public ArrayList<String> modulesCodeList;
	public ArrayList<JSONArray> modulesGradebooksList;
	
	private Context context;
	private ProgressDialog pd;
	private AlertDialog.Builder b;
	private SharedPreferences sharedPrefs;
	private Editor sharedPrefsEditor;
	
	private String userId;
	private String authToken;
	private String apiKey;
	

	@Override
	protected Activity getCurrentActivity() {
		return this;
	}
	
	@Override
	protected int getCurrentActivityLayout() {
		return R.layout.contents_gradebook;
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
		
		apiKey = getString(R.string.api_key_mine);
		userId = sharedPrefs.getString("userId", null);
		authToken = sharedPrefs.getString("authToken", null);
		modulesInfo = sharedPrefs.getString("modulesInfo", null);
		
		pd = new ProgressDialog(context);
		b = new AlertDialog.Builder(this);
		b.setCancelable(true);
		b.setPositiveButton("OK", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
			
		});
		
		
		pd.setMessage("Getting gradebooks...");
		pd.show();
		
		if (modulesInfo == null) {
			runGetModules();
		} else {
			runParseModules();
		}
	}
	
	@Override
	protected void onRefresh() {
		pd.setMessage("Refreshing...");
		pd.show();
		super.onCreate(null);
		runGetModules();
	}
	
	private void runGetModules() {
		new GetModulesAsyncTask(this).execute(apiKey, authToken, userId);
	}
	
	@Override
	public void onModulesTaskComplete(String responseContent) {
		modulesInfo = responseContent;
		
		if (modulesInfo == null) {
			pd.setMessage("modulesInfo == null");
			return;
		}
		sharedPrefsEditor.putString("modulesInfo", responseContent);
		sharedPrefsEditor.commit();
		
		runParseModules();
	}

	private void runParseModules() {
		try {
			JSONObject responseObject = new JSONObject(modulesInfo);
			JSONArray modulesArray = responseObject.getJSONArray("Results");
			numOfModules = modulesArray.length();
			
			sharedPrefsEditor.putString("modulesInfo", modulesInfo);
			sharedPrefsEditor.putInt("numOfModules", numOfModules);
			sharedPrefsEditor.commit();
			
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
			
			
			perModuleItemsList = new ArrayList<ArrayList<JSONObject>>();
			
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
								
							}
						}
					}
					perModuleItemsList.add(perModuleItemList);
					
				} else { 
					// no categories, so the module contents is null.
					perModuleItemsList.add(null);
				}
			}
			
			createPageContents();
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
	@Override
	protected void createPageContents() {
		
		LinearLayout layoutGradebook = (LinearLayout) findViewById(R.id.Layout_gradebook);
		
		
		// allModules
		// each entry in allModules contains an arraylist of gradebook items.
		
		// for each module, add its container if there are items in the gradebook.
		
		
		// FOR TESTING PURPOSES ONLY
		for (int h = 0; h < 10; h++) {
		// THIS WILL DUPLICATE MODULES 4 TIMES TO SIMULATE MANY MODULES
		
			
		for (int i = 0; i < numOfModules; i++) {
			ArrayList<JSONObject> items = perModuleItemsList.get(i);
			if (items != null) {
				
				LinearLayout containerForModule = (LinearLayout) View.inflate(this, R.layout.container_gradebook_module, null);
				layoutGradebook.addView(containerForModule);
				
				TextView containerName = (TextView) findViewById(R.id.TextView_gradebook_module_name);
				containerName.setText(modulesCodeList.get(i));
				
				LinearLayout containerForGrades = (LinearLayout) findViewById(R.id.Layout_gradebook_module_grades);
				
				if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR1) {
					containerName.setId(View.generateViewId());
					containerForGrades.setId(View.generateViewId());
				} else {
					containerName.setId(i);
					containerForGrades.setId(i);
				}
				
				int numOfItems = items.size();
				for (int j = 0; j < numOfItems; j++) {
					JSONObject item = items.get(j);
					try {
						final String itemName = item.getString("ItemName");
						String grade = item.getString("Grade");
						String percentile = item.getString("Percentile");
						String marksObtained = item.getString("MarksObtained");
						String maxMarks = ""+item.getInt("MaxMarks");
						
						final String itemDescription = item.getString("ItemDescription");
						final String highestLowestMarks = item.getString("HighestLowestMarks");
						final String averageMedianMarks = item.getString("AverageMedianMarks");
						final String remark = item.getString("Remark");
						final String dateEntered = item.getString("DateEntered");
						
						LinearLayout containerForItem = (LinearLayout) View.inflate(this, R.layout.container_gradebook_item, null);
						containerForGrades.addView(containerForItem);
						containerForItem.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View arg0) {
								b.setTitle(itemName);
								b.setMessage("Description: " + itemDescription + "\n"
										+ "Highest/Lowest Marks: " + highestLowestMarks + "\n"
										+ "Average/Median Marks: " + averageMedianMarks + "\n"
										+ "Remarks: " + remark + "\n"
										+ "Date Entered: " + dateEntered);
								b.create().show();
							}
							
						});
						
						for (int k = 0; k < 4; k++) {
							int id = 0;
							String text = "";
							
							switch(k) {
							
							case 0:
								id = R.id.TextView_item_name_value;
								text = itemName;
								break;
							case 1:
								id = R.id.TextView_item_marks_value;
								if (marksObtained.length() <= 0) {
									text = "--";
								} else if (maxMarks.length() <= 0) {
									text = marksObtained;
								} else {
									text = marksObtained + " / " + maxMarks;
								}
								break;
							case 2:
								id = R.id.TextView_item_percentile_value;
								text = percentile;
								break;
							case 3:
								id = R.id.TextView_item_grade_value;
								text = grade;
								break;
							default:
								id = R.id.TextView_item_name_value;
								text = "--";
								break;
							}
							
							TextView t = (TextView) findViewById(id);
							
							if (text.length() <= 0) {
								text = "--";
							}
							
							t.setText(text);
							
							if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR1) {
								t.setId(View.generateViewId());
							} else {
								t.setId(j);
							}
						}
						
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			} else { // items == null : there are no items in this module's gradebook.
				// TODO make a text view that says there are no items in the gardebook. 
			}
		}
		
		
		
		// THE DEBUG } IS HERE
		}
		// THE DEBUG } IS HERE
		
		pd.dismiss();
	}
}
