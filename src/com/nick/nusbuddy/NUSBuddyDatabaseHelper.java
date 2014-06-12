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
	private static final int DATABASE_VERSION = 3;
	
	// Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_MODULE = "module";
    private static final String KEY_TITLE = "title";
    private static final String KEY_UNIX_TIME = "unixTime";
    private static final String KEY_LOCATION = "location";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_RECUR_WEEKLY = "recurWeekly";
    private static final String KEY_RECUR_ODD_WEEK = "recurOddWeek";
    private static final String KEY_RECUR_EVEN_WEEK = "recurEvenWeek";
    private static final String KEY_IS_ONLY_DATE_SET = "onlyDateSet";
	
	// homework table name
    private static final String TABLE_HOMEWORK = "homework";
    
    private static final String[] TABLE_HOMEWORK_COLUMNS = {
    	KEY_ID,
    	KEY_MODULE,
    	KEY_TITLE,
    	KEY_UNIX_TIME,
    	KEY_DESCRIPTION,
    	KEY_RECUR_WEEKLY,
    	KEY_RECUR_ODD_WEEK,
    	KEY_RECUR_EVEN_WEEK,
    	KEY_IS_ONLY_DATE_SET
    };
    

	// tests table name
    private static final String TABLE_TESTS = "tests";
    
    private static final String[] TABLE_TESTS_COLUMNS = {
    	KEY_ID,
    	KEY_MODULE,
    	KEY_TITLE,
    	KEY_UNIX_TIME,
    	KEY_LOCATION,
    	KEY_DESCRIPTION,
    	KEY_IS_ONLY_DATE_SET
    };
    
	public NUSBuddyDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

	@Override
	public void onCreate(SQLiteDatabase db) {
		// SQL statement to create hw table
        String CREATE_HOMEWORK_TABLE = "CREATE TABLE " + TABLE_HOMEWORK + " ( " +
                KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + 
                KEY_MODULE + " TEXT, " +
                KEY_TITLE + " TEXT, " + 
            	KEY_UNIX_TIME + " LONG, " +
            	KEY_DESCRIPTION + " TEXT, " +
            	KEY_RECUR_WEEKLY + " BOOLEAN, " +
            	KEY_RECUR_ODD_WEEK + " BOOLEAN, " +
            	KEY_RECUR_EVEN_WEEK + " BOOLEAN, " +
            	KEY_IS_ONLY_DATE_SET + " BOOLEAN)"; 
 
        // create homework table
        db.execSQL(CREATE_HOMEWORK_TABLE);
        
     // SQL statement to create hw table
        String CREATE_TEST_TABLE = "CREATE TABLE " + TABLE_TESTS + " ( " +
                KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + 
                KEY_MODULE + " TEXT, " +
                KEY_TITLE + " TEXT, " + 
            	KEY_UNIX_TIME + " LONG, " +
                KEY_LOCATION + " TEXT, " +
            	KEY_DESCRIPTION + " TEXT, " +
            	KEY_IS_ONLY_DATE_SET + " BOOLEAN)"; 
 
        // create homework table
        db.execSQL(CREATE_TEST_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVer, int newVer) {
		// Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HOMEWORK);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TESTS);
        
        // create fresh tables
        this.onCreate(db);
	}
	
	public void addEventHomework(EventHomework homework){
        //for logging
		Log.w("addBook", homework.toString()); 
		
		// 1. get reference to writable DB
		SQLiteDatabase db = this.getWritableDatabase();
		
		// 2. create ContentValues to add key "column"/value
		ContentValues values = new ContentValues();
		values.put(KEY_MODULE, homework.getModule()); 
		values.put(KEY_TITLE, homework.getTitle());
		values.put(KEY_UNIX_TIME, homework.getUnixTime());
		values.put(KEY_DESCRIPTION, homework.getDescription());
		values.put(KEY_RECUR_WEEKLY, homework.isRecurWeekly());
		values.put(KEY_RECUR_ODD_WEEK, homework.isRecurOddWeek());
		values.put(KEY_RECUR_EVEN_WEEK, homework.isRecurEvenWeek());
		values.put(KEY_IS_ONLY_DATE_SET, homework.isOnlyDateSet());
		
		// 3. insert
		db.insert(TABLE_HOMEWORK, // table
		        null, //nullColumnHack
		        values); // key/value -> keys = column names/ values = column values
		
		// 4. close
		db.close(); 
	}
	
	public void addEventTest(EventTest test){
        //for logging
		Log.w("addBook", test.toString()); 
		
		// 1. get reference to writable DB
		SQLiteDatabase db = this.getWritableDatabase();
		
		// 2. create ContentValues to add key "column"/value
		ContentValues values = new ContentValues();
		values.put(KEY_MODULE, test.getModule()); 
		values.put(KEY_TITLE, test.getTitle());
		values.put(KEY_UNIX_TIME, test.getUnixTime());
		values.put(KEY_LOCATION, test.getLocation());
		values.put(KEY_DESCRIPTION, test.getDescription());
		values.put(KEY_IS_ONLY_DATE_SET, test.isOnlyDateSet());
		
		// 3. insert
		db.insert(TABLE_TESTS, // table
		        null, //nullColumnHack
		        values); // key/value -> keys = column names/ values = column values
		
		// 4. close
		db.close(); 
	}
	
	/**
	 * updates the entry of an homework in the db. must preserve the homework id!
	 * @param homework the new homework object that corresponds to the updated homework
	 */
	public void updateEventHomework(EventHomework homework) {
   	 
        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
     
        // 2. create ContentValues to add key "column"/value
 		ContentValues values = new ContentValues();
 		values.put(KEY_MODULE, homework.getModule()); 
 		values.put(KEY_TITLE, homework.getTitle());
 		values.put(KEY_UNIX_TIME, homework.getUnixTime());
 		values.put(KEY_DESCRIPTION, homework.getDescription());
 		values.put(KEY_RECUR_WEEKLY, homework.isRecurWeekly());
 		values.put(KEY_RECUR_ODD_WEEK, homework.isRecurOddWeek());
 		values.put(KEY_RECUR_EVEN_WEEK, homework.isRecurEvenWeek());
 		values.put(KEY_IS_ONLY_DATE_SET, homework.isOnlyDateSet());
     
        // 3. updating row. needs the homework ID!
        db.update(TABLE_HOMEWORK, //table
                values, // column/value
                KEY_ID+" = ?", // selections
                new String[] { homework.getId()+"" }); //selection args
     
        Log.w("homework", homework.toString());
        // 4. close
        db.close();
     
    }
	
	public void updateEventTest(EventTest test) {
	   	 
        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
     
        // 2. create ContentValues to add key "column"/value
 		ContentValues values = new ContentValues();
 		values.put(KEY_MODULE, test.getModule()); 
 		values.put(KEY_TITLE, test.getTitle());
 		values.put(KEY_UNIX_TIME, test.getUnixTime());
 		values.put(KEY_LOCATION, test.getLocation());
 		values.put(KEY_DESCRIPTION, test.getDescription());
 		values.put(KEY_IS_ONLY_DATE_SET, test.isOnlyDateSet());
     
        // 3. updating row. needs the homework ID!
        db.update(TABLE_TESTS, //table
                values, // column/value
                KEY_ID+" = ?", // selections
                new String[] { test.getId()+"" }); //selection args
     
        Log.w("homework", test.toString());
        // 4. close
        db.close();
     
    }
	
	/**
	 * 
	 * @param id
	 * @return Null if string stored in db is invalid.
	 */
	public EventHomework getEventHomework(int id) {
   	 
        // 1. get reference to readable DB
        SQLiteDatabase db = this.getReadableDatabase();
     
        // 2. build query
        Cursor cursor = 
                db.query(TABLE_HOMEWORK, // a. table
	                TABLE_HOMEWORK_COLUMNS, // b. column names
	                " id = ?", // c. selections 
	                new String[] { String.valueOf(id) }, // d. selections args
	                null, // e. group by
	                null, // f. having
	                null, // g. order by
	                null); // h. limit
     
        EventHomework homework = null;
        
        // 3. if we got results get the first one
        if (cursor != null) {
            cursor.moveToFirst();
            
            // 4. build book object
	        try {
				JSONObject obj = new JSONObject(cursor.getString(1));
				homework = new EventHomework(obj);
				homework.setModule(cursor.getString(0));
				//log 
		        Log.w("getBook("+id+")", homework.toString());
			} catch (JSONException e) {
				e.printStackTrace();
				
			}
        }
        
        // 5. return book
        return homework;
    }
	
	public ArrayList<EventHomework> getAllEventHomeworksBetween(long now, long then) {
		ArrayList<EventHomework> events = new ArrayList<EventHomework>();
		
		// 1. build query
		String query = "SELECT * FROM " + TABLE_HOMEWORK + " WHERE " + KEY_UNIX_TIME + " BETWEEN ? AND ?";
		
		// 2. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, new String[] {now+"", then+""} );
		
        // 3. go over each row, build homework and add it to list
        if (cursor.moveToFirst()) {
            do { 
            	EventHomework homework = new EventHomework();
				homework.setId(cursor.getInt(0)); 
				homework.setModule(cursor.getString(1));
				homework.setTitle(cursor.getString(2));
		        homework.setUnixTime(cursor.getLong(3));
		        homework.setDescription(cursor.getString(4));
		        homework.setRecurWeekly(cursor.getInt(5) == 1);
		        homework.setRecurOddWeek(cursor.getInt(6) == 1);
		        homework.setRecurEvenWeek(cursor.getInt(7) == 1);
		        homework.setOnlyDateSet(cursor.getInt(8) == 1);
            	
                // Add homework to events
            	events.add(homework); 
            	
            } while (cursor.moveToNext());
        }
        
        Log.w("eventsbetween", events.toString());
        // return events
        return events;
	}
	
	public ArrayList<EventTest> getAllEventTestsBetween(long now, long then) {
		ArrayList<EventTest> events = new ArrayList<EventTest>();
		
		// 1. build query
		String query = "SELECT * FROM " + TABLE_TESTS + " WHERE " + KEY_UNIX_TIME + " BETWEEN ? AND ?";
		
		// 2. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, new String[] {now+"", then+""} );
		
        // 3. go over each row, build homework and add it to list
        if (cursor.moveToFirst()) {
            do { 
            	EventTest test = new EventTest();
				test.setId(cursor.getInt(0)); 
				test.setModule(cursor.getString(1));
				test.setTitle(cursor.getString(2));
				test.setUnixTime(cursor.getLong(3));
				test.setLocation(cursor.getString(4));
				test.setDescription(cursor.getString(5));
				test.setOnlyDateSet(cursor.getInt(6) == 1);
            	
                // Add homework to events
            	events.add(test); 
            	
            } while (cursor.moveToNext());
        }
        
        Log.w("eventsbetween", events.toString());
        // return events
        return events;
	}
	
	
	/**
	 * gets all events corresponding to a certain module
	 * @param moduleCode the module that the events belong under
	 * @return an arraylist of events
	 */
	public ArrayList<EventHomework> getAllEventHomeworksFrom(String moduleCode) {
		ArrayList<EventHomework> events = new ArrayList<EventHomework>();
		
		// 1. build query
		String query = "SELECT * FROM " + TABLE_HOMEWORK + " WHERE " + KEY_MODULE + " = ?";
		
		// 2. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, new String[]{moduleCode});
  
        // 3. go over each row, build homework and add it to list
        if (cursor.moveToFirst()) {
            do {
            	EventHomework homework = new EventHomework();
				homework.setId(cursor.getInt(0));
				homework.setModule(cursor.getString(1));
				homework.setTitle(cursor.getString(2));
		        homework.setUnixTime(cursor.getLong(3));
		        homework.setDescription(cursor.getString(4));
		        homework.setRecurWeekly(cursor.getInt(5) == 1);
		        homework.setRecurOddWeek(cursor.getInt(6) == 1);
		        homework.setRecurEvenWeek(cursor.getInt(7) == 1);
		        homework.setOnlyDateSet(cursor.getInt(8) == 1);
            	
                // Add homework to events
            	events.add(homework); 
            	
            } while (cursor.moveToNext());
        }
        
        // return events
        return events;
	}
	
	public ArrayList<EventTest> getAllEventTestsFrom(String moduleCode) {
		ArrayList<EventTest> events = new ArrayList<EventTest>();
		
		// 1. build query
		String query = "SELECT * FROM " + TABLE_TESTS + " WHERE " + KEY_MODULE + " = ?";
		
		// 2. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, new String[] {moduleCode} );
		
        // 3. go over each row, build homework and add it to list
        if (cursor.moveToFirst()) {
            do { 
            	EventTest test = new EventTest();
				test.setId(cursor.getInt(0)); 
				test.setModule(cursor.getString(1));
				test.setTitle(cursor.getString(2));
				test.setUnixTime(cursor.getLong(3));
				test.setLocation(cursor.getString(4));
				test.setDescription(cursor.getString(5));
				test.setOnlyDateSet(cursor.getInt(6) == 1);
            	
                // Add homework to events
            	events.add(test); 
            	
            } while (cursor.moveToNext());
        }
        Log.w("events from", moduleCode);
        Log.w("events from", events.toString());
        // return events
        return events;
	}
	
	/**
	 * gets all events in the db
	 * @return an arraylist containing all the events in the db
	 */
	public ArrayList<EventHomework> getAllEventHomeworks() {
        ArrayList<EventHomework> events = new ArrayList<EventHomework>();
  
        // 1. build the query
        String query = "SELECT * FROM " + TABLE_HOMEWORK;
  
        // 2. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
  
        // 3. go over each row, build homework and add it to list
        EventHomework homework = null;
        if (cursor.moveToFirst()) {
            do {
            	homework = new EventHomework();
				homework.setId(cursor.getInt(0));
				homework.setModule(cursor.getString(1));
				homework.setTitle(cursor.getString(2));
		        homework.setUnixTime(cursor.getLong(3));
		        homework.setDescription(cursor.getString(4));
		        homework.setRecurWeekly(cursor.getInt(5) == 1);
		        homework.setRecurOddWeek(cursor.getInt(6) == 1);
		        homework.setRecurEvenWeek(cursor.getInt(7) == 1);
		        homework.setOnlyDateSet(cursor.getInt(8) == 1);
            	
                // Add homework to events
            	events.add(homework);
            	
            } while (cursor.moveToNext());
        }
  
        // return events
        return events;
    }
	
	public void deleteEventHomework(EventHomework homework) {
   	 
        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
 
        // 2. delete
        db.delete(TABLE_HOMEWORK, //table name
                KEY_ID+" = ?",  // selections
                new String[] { String.valueOf(homework.getId()) }); //selections args
 
        // 3. close
        db.close();
 
    }
	
	public void deleteEventTest(EventTest test) {
	   	 
        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
 
        // 2. delete
        db.delete(TABLE_TESTS, //table name
                KEY_ID+" = ?",  // selections
                new String[] { String.valueOf(test.getId()) }); //selections args
 
        // 3. close
        db.close();
 
    }
	
	public void deleteAllEventHomeworks() {
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
