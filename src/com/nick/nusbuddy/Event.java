package com.nick.nusbuddy;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

/**
 * Event class that holds information about events.
 * 
 * This is to help hold the information and has a toString method for storage in 
 * the database.
 * @author Nicholas
 *
 */
public class Event {
	
	// event id is set by the database as the row primary key. 
	
	private int id;
	private String module;
	private String title;
	private String location;
	private long unixTime;
	private boolean onlyDateSet;
	private String description;
	private boolean recurWeekly;
	private boolean recurEvenWeek;
	private boolean recurOddWeek;
	private JSONObject jsonRepresentation;

	/**
	 * Empty constructor
	 */
	public Event() {
	}
	
	/**
	 * Constructor that takes in a JSONObject.
	 * The event info is set from the appropriate key-value mappings in the object.
	 * @param obj The JSONObject that holds the event information
	 * @throws JSONException Throws exception when a key-value mapping is not 
	 * found, i.e. the JSONObject is invalid
	 */
	public Event(JSONObject obj) throws JSONException {
		setModule(obj.getString("module"));
		setTitle(obj.getString("title"));
		setLocation(obj.getString("location"));
		setUnixTime(obj.getLong("unixTime"));
		setOnlyDateSet(obj.getBoolean("onlyDateSet"));
		setDescription(obj.getString("description"));
		setRecurWeekly(obj.getBoolean("recurWeekly"));
		setRecurEvenWeek(obj.getBoolean("recurEvenWeek"));
		setRecurOddWeek(obj.getBoolean("recurOddWeek"));
		jsonRepresentation = obj;
	}
	
	public Event(String string) throws JSONException {
		JSONObject obj = new JSONObject(string);
		setModule(obj.getString("module"));
		setTitle(obj.getString("title"));
		setLocation(obj.getString("location"));
		setUnixTime(obj.getLong("unixTime"));
		setOnlyDateSet(obj.getBoolean("onlyDateSet"));
		setDescription(obj.getString("description"));
		setRecurWeekly(obj.getBoolean("recurWeekly"));
		setRecurEvenWeek(obj.getBoolean("recurEvenWeek"));
		setRecurOddWeek(obj.getBoolean("recurOddWeek"));
		jsonRepresentation = obj;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public long getUnixTime() {
		return unixTime;
	}

	public void setUnixTime(long unixTimeValue) {
		this.unixTime = unixTimeValue;
	}
	
	public boolean isOnlyDateSet() {
		return onlyDateSet;
	}

	public void setOnlyDateSet(boolean onlyDateSet) {
		this.onlyDateSet = onlyDateSet;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isRecur() {
		return recurWeekly || recurEvenWeek || recurOddWeek;
	}

	public boolean isRecurWeekly() {
		return recurWeekly;
	}

	public void setRecurWeekly(boolean recurWeekly) {
		if (recurWeekly) {
			setRecurEvenWeek(false);
			setRecurOddWeek(false);
		}
		this.recurWeekly = recurWeekly;
	}

	public boolean isRecurEvenWeek() {
		return recurEvenWeek;
	}
	
	public boolean isRecurOddWeek() {
		return recurOddWeek;
	}
	
	

	public void setRecurEvenWeek(boolean recurEvenWeek) {
		if (recurEvenWeek) {
			setRecurWeekly(false);
		}
		this.recurEvenWeek = recurEvenWeek;
	}
	
	public void setRecurOddWeek(boolean recurOddWeek) {
		if (recurOddWeek) {
			setRecurWeekly(false);
		}
		this.recurOddWeek = recurOddWeek;
	}
	/**
	 * This method makes a JSON representation of the object from the information 
	 * it holds.
	 * @return the JSONObject that holds all the information, or null if there's 
	 * a JSONException while putting information.
	 */
	public JSONObject makeJsonRepresentation() {
		JSONObject obj = new JSONObject();
		try {
			obj.put("module", module);
			obj.put("title", title);
			obj.put("location", location);
			obj.put("unixTime", unixTime);
			obj.put("onlyDateSet", onlyDateSet);
			obj.put("description", description);
			obj.put("recurWeekly", recurWeekly);
			obj.put("recurEvenWeek", recurEvenWeek);
			obj.put("recurOddWeek", recurOddWeek);
			jsonRepresentation = obj;
		} catch (JSONException e) {
			e.printStackTrace();
			jsonRepresentation = null;
		}
		return jsonRepresentation;
	}
	
	/**
	 * Runs makeJsonRepresentation() and applies toString() on the result.
	 * @return The string representation of the event object. The string is in JSON 
	 * format. 
	 * Returns null if makeJsonRepresentation() fails.
	 */
	@Override
	public String toString() {
		JSONObject obj = makeJsonRepresentation();
		if (obj == null) {
			Log.w("fail", "you fail");
			return null;
		} else {
			Log.w("fail", obj.toString());
			return obj.toString();
		}
	}

}
