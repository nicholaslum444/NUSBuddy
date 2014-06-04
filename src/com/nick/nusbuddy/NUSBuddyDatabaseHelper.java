package com.nick.nusbuddy;

import java.util.ArrayList;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class NUSBuddyDatabaseHelper extends SQLiteOpenHelper {
	
	
	private static final String DATABASE_NAME = "NUSBuddyDatabase";
	private static final int DATABASE_VERSION = 1;
	
	// homework table name
    private static final String TABLE_HOMEWORK = "homework";
	// tests table name
    private static final String TABLE_TESTS = "tests";

    // homework Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_MODULE = "module";
    private static final String KEY_TITLE = "title";
    private static final String KEY_LOCATION = "location";
    private static final String KEY_UNIX_TIME = "unixTime";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_RECUR_WEEKLY = "recurWeekly";
    private static final String KEY_RECUR_ODD_WEEK = "recurOddWeek";
    private static final String KEY_RECUR_EVEN_WEEK = "recurEvenWeek";
    
    private static final String[] COLUMNS = {
    	KEY_ID,
    	KEY_MODULE,
    	KEY_TITLE,
    	KEY_LOCATION,
    	KEY_UNIX_TIME,
    	KEY_DESCRIPTION,
    	KEY_RECUR_WEEKLY,
    	KEY_RECUR_ODD_WEEK,
    	KEY_RECUR_EVEN_WEEK
    };
	

	public NUSBuddyDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        
    }

	@Override
	public void onCreate(SQLiteDatabase db) {
		// SQL statement to create book table
        String CREATE_HOMEWORK_TABLE = "CREATE TABLE homework ( " +
                KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + 
                KEY_MODULE + " TEXT, " +
                KEY_TITLE + " TEXT, " + 
            	KEY_LOCATION + " TEXT, " +
            	KEY_UNIX_TIME + " LONG, " +
            	KEY_DESCRIPTION + " TEXT, " +
            	KEY_RECUR_WEEKLY + " BOOLEAN, " +
            	KEY_RECUR_ODD_WEEK + " BOOLEAN, " +
            	KEY_RECUR_EVEN_WEEK + " BOOLEAN)"; 
 
        // create homework table
        db.execSQL(CREATE_HOMEWORK_TABLE);
        
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVer, int newVer) {
		// Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HOMEWORK);
 
        // create fresh table
        this.onCreate(db);
	}
	
	public void addEvent(Event event){
        //for logging
		Log.w("addBook", event.toString()); 
		
		// 1. get reference to writable DB
		SQLiteDatabase db = this.getWritableDatabase();
		
		// 2. create ContentValues to add key "column"/value
		ContentValues values = new ContentValues();
		values.put(KEY_MODULE, event.getModule()); 
		values.put(KEY_TITLE, event.getTitle());
		values.put(KEY_LOCATION, event.getLocation());
		values.put(KEY_UNIX_TIME, event.getUnixTime());
		values.put(KEY_DESCRIPTION, event.getDescription());
		values.put(KEY_RECUR_WEEKLY, event.isRecurWeekly());
		values.put(KEY_RECUR_ODD_WEEK, event.isRecurOddWeek());
		values.put(KEY_RECUR_EVEN_WEEK, event.isRecurEvenWeek());
		
		// 3. insert
		db.insert(TABLE_HOMEWORK, // table
		        null, //nullColumnHack
		        values); // key/value -> keys = column names/ values = column values
		
		// 4. close
		db.close(); 
	}
	
	/**
	 * updates the entry of an event in the db. must preserve the event id!
	 * @param event the new event object that corresponds to the updated event
	 */
	public void updateEvent(Event event) {
   	 
        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
     
        // 2. create ContentValues to add key "column"/value
 		ContentValues values = new ContentValues();
 		values.put(KEY_MODULE, event.getModule()); 
 		values.put(KEY_TITLE, event.getTitle());
 		values.put(KEY_LOCATION, event.getLocation());
 		values.put(KEY_UNIX_TIME, event.getUnixTime());
 		values.put(KEY_DESCRIPTION, event.getDescription());
 		values.put(KEY_RECUR_WEEKLY, event.isRecurWeekly());
 		values.put(KEY_RECUR_ODD_WEEK, event.isRecurOddWeek());
 		values.put(KEY_RECUR_EVEN_WEEK, event.isRecurEvenWeek());
     
        // 3. updating row. needs the event ID!
        db.update(TABLE_HOMEWORK, //table
                values, // column/value
                KEY_ID+" = ?", // selections
                new String[] { String.valueOf(event.getId()) }); //selection args
     
        // 4. close
        db.close();
     
    }
	
	/**
	 * 
	 * @param id
	 * @return Null if string stored in db is invalid.
	 */
	public Event getEvent(int id) {
   	 
        // 1. get reference to readable DB
        SQLiteDatabase db = this.getReadableDatabase();
     
        // 2. build query
        Cursor cursor = 
                db.query(TABLE_HOMEWORK, // a. table
	                COLUMNS, // b. column names
	                " id = ?", // c. selections 
	                new String[] { String.valueOf(id) }, // d. selections args
	                null, // e. group by
	                null, // f. having
	                null, // g. order by
	                null); // h. limit
     
        Event event = null;
        
        // 3. if we got results get the first one
        if (cursor != null) {
            cursor.moveToFirst();
            
            // 4. build book object
	        try {
				JSONObject obj = new JSONObject(cursor.getString(1));
				event = new Event(obj);
				event.setModule(cursor.getString(0));
				//log 
		        Log.w("getBook("+id+")", event.toString());
			} catch (JSONException e) {
				e.printStackTrace();
				
			}
        }
        
        // 5. return book
        return event;
    }
	
	
	/**
	 * gets all events corresponding to a certain module
	 * @param moduleCode the module that the events belong under
	 * @return an arraylist of events
	 */
	public ArrayList<Event> getAllEvents(String moduleCode) {
		ArrayList<Event> events = new ArrayList<Event>();
		
		// 1. build query
		String query = "SELECT * FROM " + TABLE_HOMEWORK + " WHERE " + KEY_MODULE + " = ?";
		
		// 2. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, new String[]{moduleCode});
  
        // 3. go over each row, build event and add it to list
        Event event = new Event();
        if (cursor.moveToFirst()) {
            do {
				event.setId(cursor.getInt(0));
				event.setModule(cursor.getString(1));
				event.setTitle(cursor.getString(2));
		        event.setLocation(cursor.getString(3));
		        event.setUnixTime(cursor.getLong(4));
		        event.setDescription(cursor.getString(5));
		        event.setRecurWeekly(cursor.getInt(6) == 1);
		        event.setRecurOddWeek(cursor.getInt(7) == 1);
		        event.setRecurEvenWeek(cursor.getInt(8) == 1);
            	
                // Add event to events
            	events.add(event); 
            	
            } while (cursor.moveToNext());
        }
        
        // return events
        return events;
	}
	
	/**
	 * gets all events in the db
	 * @return an arraylist containing all the events in the db
	 */
	public ArrayList<Event> getAllEvents() {
        ArrayList<Event> events = new ArrayList<Event>();
  
        // 1. build the query
        String query = "SELECT  * FROM " + TABLE_HOMEWORK;
  
        // 2. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
  
        // 3. go over each row, build event and add it to list
        Event event = null;
        if (cursor.moveToFirst()) {
            do {
            	event = new Event();
				event.setId(cursor.getInt(0));
				event.setModule(cursor.getString(1));
				event.setTitle(cursor.getString(2));
				event.setLocation(cursor.getString(3));
				event.setUnixTime(cursor.getLong(4));
				event.setDescription(cursor.getString(5));
				event.setRecurWeekly(cursor.getInt(6) == 1);
				event.setRecurOddWeek(cursor.getInt(7) == 1);
				event.setRecurEvenWeek(cursor.getInt(8) == 1);
            	
                // Add event to events
            	events.add(event);
            	
            } while (cursor.moveToNext());
        }
  
        // return events
        return events;
    }
	
	public void deleteEvent(Event event) {
   	 
        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
 
        // 2. delete
        db.delete(TABLE_HOMEWORK, //table name
                KEY_ID+" = ?",  // selections
                new String[] { String.valueOf(event.getId()) }); //selections args
 
        // 3. close
        db.close();
 
    }
	
	public void deleteAllEvents() {
		// 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
 
        // 2. delete
        db.delete(TABLE_HOMEWORK, //table name
                null,  // selections
                null); //selections args
 
        // 3. close
        db.close();
	}
	
}
