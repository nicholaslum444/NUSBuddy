package com.nick.nusbuddy;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Event class that holds information about events.
 * 
 * This is to help hold the information and has a toString method for storage in 
 * the database.
 * @author Nicholas
 *
 */
public class Event {
	
	private String module;
	private String title;
	private String location;
	private String date;
	private String time;
	private String description;
	private boolean recurWeekly;
	private boolean recurFortnightly;
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
		setTitle(obj.getString("title"));
		setLocation(obj.getString("location"));
		setDate(obj.getString("date"));
		setTime(obj.getString("time"));
		setDescription(obj.getString("description"));
		setRecurWeekly(obj.getBoolean("recurWeekly"));
		setRecurFortnightly(obj.getBoolean("recurFortnightly"));
		jsonRepresentation = obj;
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

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isRecurWeekly() {
		return recurWeekly;
	}

	public void setRecurWeekly(boolean recurWeekly) {
		if (recurWeekly) {
			setRecurFortnightly(false);
		}
		this.recurWeekly = recurWeekly;
	}

	public boolean isRecurFortnightly() {
		return recurFortnightly;
	}

	public void setRecurFortnightly(boolean recurFortnightly) {
		if (recurFortnightly) {
			setRecurWeekly(false);
		}
		this.recurFortnightly = recurFortnightly;
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
			obj.put("title", title);
			obj.put("lcoation", location);
			obj.put("date", date);
			obj.put("time", time);
			obj.put("description", description);
			obj.put("recurWeekly", recurWeekly);
			obj.put("recurFortnightly", recurFortnightly);
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
			return null;
		} else {
			return obj.toString();
		}
	}

}
