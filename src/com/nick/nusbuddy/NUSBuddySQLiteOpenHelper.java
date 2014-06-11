package com.nick.nusbuddy;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class NUSBuddySQLiteOpenHelper extends SQLiteOpenHelper {
	
	
	private static final String DATABASE_NAME = "NUSBuddy";
	private static final int DATABASE_VERSION = 1;
	
	// homework table name
    private static final String TABLE_HOMEWORK = "homework";
	// tests table name
    private static final String TABLE_TESTS = "tests";

    // homework Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_MODULE = "module";
    private static final String KEY_DATA = "data";

    private static final String[] COLUMNS = {KEY_DATA};
	

	public NUSBuddySQLiteOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);  
    }

	@Override
	public void onCreate(SQLiteDatabase db) {
		// SQL statement to create book table
        String CREATE_HOMEWORK_TABLE = "CREATE TABLE homework ( " +
                KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + 
                KEY_MODULE + " TEXT, "+
                KEY_DATA + " TEXT )";
 
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
		values.put(KEY_DATA, event.toString());
		
		// 3. insert
		db.insert(TABLE_HOMEWORK, // table
		        null, //nullColumnHack
		        values); // key/value -> keys = column names/ values = column values
		
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
				event = new EventHomework(obj);
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
            	try {
					event = new EventHomework(cursor.getString(2));
					event.setId(Integer.parseInt(cursor.getString(0)));
	            	event.setModule(cursor.getString(1));
				} catch (JSONException e) {
					e.printStackTrace();
					Log.w("event creating fail", "event creating fail");
				}
            	
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
		onUpgrade(this.getWritableDatabase(), DATABASE_VERSION, DATABASE_VERSION);
	}
	
}
