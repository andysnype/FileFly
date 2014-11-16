/**
 * 
 */
package com.procom.filefly;

import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList; 
import java.util.Date; 
import java.util.List;
import java.util.Locale;

import android.provider.SyncStateContract.Columns;
import android.text.format.DateFormat;
import android.util.Log; 
import android.content.ContentValues; 
import android.content.Context; 
import android.database.Cursor;
import android.database.SQLException;

import com.procom.filefly.model.*;


/**
 * The {@link android.database.sqlite.SQLiteOpenHelper} that  manages database 
 * creation and version management. 
 * Ref.: http://mrbool.com/how-to-insert-data-into-a-sqlite-database-in-android/28895
 * @author Saurabh Sharma
 * @version 0.2a
 * @since 2014-10-30
 *
 */

public class SqliteController extends SQLiteOpenHelper  {
	private static final String LOGCAT = null;
	private SQLiteDatabase tmpDB = null;
	
	public SqliteController(Context applicationcontext) 
	{
		super(applicationcontext, "datastore.db", null, 3);
		Log.d(LOGCAT,"Created");
	}
	
	/**
	 * A {@link java.lang.String} representing a table in the SQlite database as modeled by 
	 * {@link com.procom.filefly.model.Document} 
	 */
	private static final String table1 = "document";
	
	/**
	 * A {@link java.lang.String} representing a table SqliteSequence in the SQlite database   
	 */
	private static final String table2 = "sqlite_sequence";
	
	/**
	 * A {@link java.lang.String} representing '_id' column of table document. 
	 * An auto increment field, hence doesn't find representation in 
	 * {@link com.procom.filefly.model.Document}      
	 */
	private static final String column_id = "_id";
	
	/**
	 * A {@link java.lang.String} representing '_fileName' column of table document 
	 * as represented by '_fileName' field of {@link com.procom.filefly.model.Document}      
	 */
	private static final String column_fileName = "_fileName";
	
	/**
	 * A {@link java.lang.String} representing '_ownerFirstName' column of table document 
	 * as represented by '_ownerFirstName' field of {@link com.procom.filefly.model.Document}      
	 */
	private static final String column_ownerFName = "_ownerFirstName";
	
	/**
	 * A {@link java.lang.String} representing '_ownerLastName' column of table document 
	 * as represented by '_ownerLastName' field of {@link com.procom.filefly.model.Document}      
	 */
	private static final String column_ownerLName = "_ownerLastName";
	
	/**
	 * A {@link java.lang.String} representing '_dateTransferred' column of table document 
	 * as represented by '_dateTransferred' field of {@link com.procom.filefly.model.Document}      
	 */
	private static final String column_dateTransferred = "_dateTransferred";
	
	/**
	 * A {@link java.lang.String} representing 'locale' column of the table android_metadata      
	 */
	private static final String column_locale = "locale";
	
	/**
	 * A {@link java.lang.String} representing an SQL to create an empty database with three
	 * tables: table1, table2 and android_metadata.      
	 */
	private static final String DATABASE_CREATE = 
     "CREATE TABLE " + "'" + table1  + "'" + 
		      "( " +   "'" + column_id  + "'" + " integer primary key autoincrement, " +  
		      		   "'" + column_fileName + "'" + " text not null, " +
		      		   "'" + column_ownerFName + "'" + " text, " + 
		      		   "'" + column_ownerLName + "'" + " text, " + 
		      		   "'" + column_dateTransferred + "'" + " text not null " +
		        ");"+ 
     "CREATE TABLE " + "'" + "android_metadata" + "'" +
	    	    "("  + "'" +  column_locale +   "Text default " + "\"" + "en_US" + "\" " +
		        ");" + 
     "CREATE TABLE " + "'" + table2 + "' " +
		        "( " + "'" + "name" + " TEXT, " +
		        	   "'" + "seq" + "'" + " TEXT " +
		        ");"
		        ;

	/**
	 * Creates an {@link android.database.sqlite.SQLiteDatabase} as per the 
	 * DATABASE_CREATE variable. 
	 */
	@Override
	public void onCreate(SQLiteDatabase database)
	{
		SQLiteStatement stmt = database.compileStatement(DATABASE_CREATE);
		stmt.execute();
	}
	
	/**
	 * A {@link java.lang.String} representing an SQL to drop table android_metadata      
	 */
	private static final String drop_andMetaData = "Drop table if exists android_metadata";
	
	/**
	 * A {@link java.lang.String} representing an SQL to drop table document      
	 */
	private static final String drop_document = "Drop table if exists document";
	
	/**
	 * A {@link java.lang.String} representing an SQL to drop table sqlite_sequence      
	 */
	private static final String drop_sqliteSeq = "Drop table if exists sqlite_sequence";
	
	/**
	 * Upgrades the version of {@link android.database.sqlite.SQLiteDatabase} by incrementing 
	 * the old version till it becomes equal to the current version and then drops the tables 
	 * and recreates the {@link android.database.sqlite.SQLiteDatabase}.
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int version_old, int current_version) 
	{ 
		int upgradeTo = version_old + 1;
        while (upgradeTo <= current_version)
        {     
            upgradeTo++;
        }
        SQLiteStatement stmt = db.compileStatement(drop_andMetaData);
        stmt.execute();
        stmt = db.compileStatement(drop_document);
        stmt.execute();
        stmt = db.compileStatement(drop_sqliteSeq);
        stmt.execute();
        onCreate(db);
	}
	
	/**
	 * Gets the incoming {@link com.procom.filefly.model.Document} and inserts its attributes into the 
	 * {@link android.database.sqlite.SQLiteDatabase} as per former's getters.
	 */

	public void insertData(Document doc) 
	{ 
		SQLiteDatabase database = this.getWritableDatabase();
		SQLiteStatement stmt = database.compileStatement(
		"insert into "+ table1 + 
		" values " + "(" +
						doc.getFilename() + 
						doc.getOwnerFirstName() + 
						doc.getOwnerLastName() +
						(new SimpleDateFormat("MM/dd/yyyy HH:mm:ss:SS zzz")).format(doc.getDateTransferred()) +
						");"
		);
		stmt.execute();
		database.close();
	}
	
	/**
	 * Takes filename representing {@link com.procom.filefly.model.Document}'s name as input and 
	 * deletes it from the {@link android.database.sqlite.SQLiteDatabase}.
	 * 
	 * Reference: http://mrbool.com/how-to-insert-data-into-a-sqlite-database-in-android/28895
	 */
	public void deleteDocument(String fileName) 
	{ 
		SQLiteDatabase database = this.getWritableDatabase();
		Log.d(LOGCAT,"delete"); 	
		String deleteQuery = "DELETE FROM document where _fileName='"+ fileName +"'"; 
		Log.d("query",deleteQuery);	
		database.execSQL(deleteQuery); 
	}

	
	/**
	 * Retrieves information of all the stored {@link com.procom.filefly.model.Document}. 
	 * 
	 * @return {@link List} of {@link com.procom.filefly.model.Document} present
	 * 
	 * Reference: http://mrbool.com/how-to-insert-data-into-a-sqlite-database-in-android/28895
	 */
	public List<Document> getAllDocuments()
	{
		SQLiteDatabase database = this.getWritableDatabase();
		List<Document> docList;
		String filename, ownerFirstName, ownerLastName;
		Date dateTransferred;
		docList = new ArrayList<Document>();
		String selectQuery = "SELECT  * FROM document";
		Cursor cursor = database.rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) 
		{ 
			do 
			{   
				filename = cursor.getString(1);
				ownerFirstName = cursor.getString(2);
				ownerLastName = cursor.getString(3);
				try
				{
				/*
				 * Formats the date as Tue Nov 04 21:53:43 EST 2003	
				 */
				dateTransferred = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss:SS zzz").parse(cursor.getString(4));
				Document d = new Document(filename, ownerFirstName, ownerLastName, dateTransferred) ;
				docList.add(d);
				Log.d("FILEFLY","dateTransferred = " + dateTransferred);
				}
				catch(ParseException p)
				{
				  	p.printStackTrace();
				}
			}while (cursor.moveToNext()); 
		} 
		// return contact list 
		return docList;
	}
}