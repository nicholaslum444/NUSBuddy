package com.nick.nusbuddy;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class EventTest extends Event {
	

	private String location;
	
	public EventTest() {
	}

	public EventTest(JSONObject obj) throws JSONException {
		super(obj);
		setLocation(obj.getString("location"));
		
		setJsonRepresentation(obj);
	}

	public EventTest(String string) throws JSONException {
		super(string);
		JSONObject obj = new JSONObject(string);
		setLocation(obj.getString("location"));
		
		setJsonRepresentation(obj);
	}
	
	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
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
			obj.put("onlyDateSet", isOnlyDateSet());
			setJsonRepresentation(obj);
		} catch (JSONException e) {
			e.printStackTrace();
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
