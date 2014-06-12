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
public abstract class Event {
	
	// event id is set by the database as the row primary key. 
	
	private int id;
	private String module;
	private String title;
	private long unixTime;
	private boolean onlyDateSet;
	private String description;
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
		setId(obj.getInt("id"));
		setModule(obj.getString("module"));
		setTitle(obj.getString("title"));
		setUnixTime(obj.getLong("unixTime"));
		setOnlyDateSet(obj.getBoolean("onlyDateSet"));
		setDescription(obj.getString("description"));
		setOnlyDateSet(obj.getBoolean("onlyDateSet"));
		setJsonRepresentation(obj);
	}
	
	public Event(String string) throws JSONException {
		JSONObject obj = new JSONObject(string);
		setId(obj.getInt("id"));
		setModule(obj.getString("module"));
		setTitle(obj.getString("title"));
		setUnixTime(obj.getLong("unixTime"));
		setOnlyDateSet(obj.getBoolean("onlyDateSet"));
		setDescription(obj.getString("description"));
		setJsonRepresentation(obj);
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

	public abstract JSONObject makeJsonRepresentation();
	
	public JSONObject getJsonRepresentation() {
		return this.jsonRepresentation;
	}

	public void setJsonRepresentation(JSONObject jsonRepresentation) {
		this.jsonRepresentation = jsonRepresentation;
	}
	
	/**
	 * Runs makeJsonRepresentation() and applies toString() on the result.
	 * @return The string representation of the event object. The string is in JSON 
	 * format. 
	 * Returns null if makeJsonRepresentation() fails.
	 */
	@Override
	public abstract String toString();
}
