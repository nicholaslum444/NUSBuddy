package com.nick.nusbuddy;

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

    // homework Table Columns names
    private static final String KEY_MODULE = "module";
    private static final String KEY_DATA = "data";

    private static final String[] COLUMNS = {KEY_MODULE, KEY_DATA};
	

	public NUSBuddySQLiteOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);  
    }

	@Override
	public void onCreate(SQLiteDatabase db) {
		// SQL statement to create book table
        String CREATE_HOMEWORK_TABLE = "CREATE TABLE homework ( " +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " + 
                "title TEXT, "+
                "author TEXT )";
 
        // create books table
        db.execSQL(CREATE_HOMEWORK_TABLE);
        
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVer, int newVer) {
		// Drop older books table if existed
        db.execSQL("DROP TABLE IF EXISTS books");
 
        // create fresh books table
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

}
