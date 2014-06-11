package com.nick.nusbuddy;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class EventHomework extends Event {
	

	private boolean recurWeekly;
	private boolean recurEvenWeek;
	private boolean recurOddWeek;
	
	
	public EventHomework() {
	}

	public EventHomework(JSONObject obj) throws JSONException {
		super(obj);
		setRecurWeekly(obj.getBoolean("recurWeekly"));
		setRecurEvenWeek(obj.getBoolean("recurEvenWeek"));
		setRecurOddWeek(obj.getBoolean("recurOddWeek"));
		
		setJsonRepresentation(obj);

	}

	public EventHomework(String string) throws JSONException {
		super(string);
		JSONObject obj = new JSONObject(string);
		setRecurWeekly(obj.getBoolean("recurWeekly"));
		setRecurEvenWeek(obj.getBoolean("recurEvenWeek"));
		setRecurOddWeek(obj.getBoolean("recurOddWeek"));
		
		setJsonRepresentation(obj);

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
			setRecurOddWeek(false);
		}
		this.recurEvenWeek = recurEvenWeek;
	}
	
	public void setRecurOddWeek(boolean recurOddWeek) {
		if (recurOddWeek) {
			setRecurWeekly(false);
			setRecurEvenWeek(false);
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
			obj.put("id", getId());
			obj.put("module", getModule());
			obj.put("title", getTitle());
			obj.put("unixTime", getUnixTime());
			obj.put("onlyDateSet", isOnlyDateSet());
			obj.put("description", getDescription());
			obj.put("recurWeekly", recurWeekly);
			obj.put("recurEvenWeek", recurEvenWeek);
			obj.put("recurOddWeek", recurOddWeek);
			obj.put("onlyDateSet", isOnlyDateSet());
			setJsonRepresentation(obj);
		} catch (JSONException e) {
			e.printStackTrace();
			Log.w("making failed", "failed");
			setJsonRepresentation(null);
		}
		return getJsonRepresentation();
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
			Log.w("obj made", obj.toString());
			return obj.toString();
		}
	}
}
