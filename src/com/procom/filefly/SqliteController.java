/**
 * 
 */
package com.procom.filefly;

import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList; 
import java.util.HashMap; 
import android.util.Log; 
import android.content.ContentValues; 
import android.content.Context; 
import android.database.Cursor;



/**
 * @author Saurabh
 *
 */
public class SqliteController extends SQLiteOpenHelper  {
	private static final String LOGCAT = null;
	
	public SqliteController(Context applicationcontext) 
	{
		super(applicationcontext, "content.db", null, 3);
		Log.d(LOGCAT,"Created");
	}
	
	public void onCreate(SQLiteDatabase database)
	{
		// Not creating any tables as they have been already created manually using SQL Browser
	}
	
	public void onUpgrade(SQLiteDatabase db, int version_old, int current_version) 
	{ 
		int upgradeTo = version_old + 1;
        while (upgradeTo <= current_version)
        {
            switch (upgradeTo)
            {
                case 4:
                    db.execSQL("Drop table if exists android_metadata");
                    db.execSQL("Drop table if exists document");
                    db.execSQL("Drop table if exists sqlite_sequence");
                    onCreate(db);
                    break;
                case 5:
                    // To be identified
                    break;
                case 6:
                	// To be identified
                    break;
            }
            upgradeTo++;
        } 
	}
	
	public void insertData(HashMap<String, String> queryValues) 
	{
		SQLiteDatabase database = this.getWritableDatabase(); 
		ContentValues values = new ContentValues();
		values.put("_id", queryValues.get("_id"));
		values.put("_fileName", queryValues.get("_fileName"));
		values.put("_ownerFirstName", queryValues.get("_ownerFirstName"));
		values.put("_ownerLastName", queryValues.get("_ownerLastName"));
		values.put("_dateTransferred", queryValues.get("_dateTransferred"));
		database.insert("document", null, values);
		database.close();
	}
	
	//TODO: To decide whether to identify and define other methods such as updateRecord, deleteRecord,  
	// getAllRecords or any others. 
	// If yes, to refer: http://mrbool.com/how-to-insert-data-into-a-sqlite-database-in-android/28895
	public ArrayList<HashMap<String, String>> getAllDocuments() 
	{
		ArrayList<HashMap<String, String>> wordList;
		wordList = new ArrayList<HashMap<String, String>>();
		String selectQuery = "SELECT  * FROM document";
		SQLiteDatabase database = this.getWritableDatabase();
		Cursor cursor = database.rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) 
		{ 
			do 
			{ 
				HashMap<String, String> map = new HashMap<String, String>(); 
				map.put("_id", cursor.getString(0)); 
				map.put("_fileName", cursor.getString(1));
				map.put("_ownerFirstName", cursor.getString(2));
				map.put("_ownerLastName", cursor.getString(3));
				map.put("_dateTransferred", cursor.getString(4));
				wordList.add(map); 
			}while (cursor.moveToNext()); 
		} 
		// return contact list 
		return wordList;
	}
}
