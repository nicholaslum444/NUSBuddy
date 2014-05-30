package com.nick.nusbuddy;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class NUSBuddySQLiteOpenHelper extends SQLiteOpenHelper {

	
	
	private static final String DATABASE_NAME = "NUSBuddy";
	private static final int DATABASE_VERSION = 1;

	public NUSBuddySQLiteOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);  
    }

	@Override
	public void onCreate(SQLiteDatabase arg0) {
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
	}

}
