package com.procom.filefly;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

/**
 * The {@link android.support.v4.app.FragmentActivity} that will launch
 * in response to an ACTION_VIEW intent from the Android Beam API, copy
 * the transferred file into the received subdirectory of the FileFly
 * directory found on the root of the SD Card.
 * 
 * @author Peter Piech, Saurabh Sharma, Jacob Abramson
 * @version 0.2a
 * @since 2014-10-15
 *
 */
public class ReceiveActivity extends FragmentActivity
{
	/** A {@link java.io.File} representing the path to the transferred file */
	private String mPath;
	
	/**
	 * The incoming {@link android.content.Intent} with a {@link android.content.Intent#ACTION_VIEW} schema
	 * containing a {@link android.net.Uri} for the transferred file.
	 */
    private Intent mIntent;
    
    /** The parsed out sender's first name */
    private String mFirstName;
    
    /** The parsed out sender's last name */
    private String mLastName;
    
    /** The parsed out original filename that was selected by the user */
    private String mOriginalFileName;
	
    /**
     * Inflates the layout from XML and handles the incoming
     * {@link android.content.Intent} with a {@link android.content.Intent#ACTION_VIEW} schema.
     * 
     * @author Peter Piech
     */
	@Override
    protected void onCreate(Bundle savedInstanceState)
    {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_receive);
		mFirstName = new String();
		mLastName = new String();
		mOriginalFileName = new String();
		handleViewIntent();
		Toast.makeText(this, "Received file successfully..!", Toast.LENGTH_LONG).show(); // show the user this message
		// TODO: copy transferred file into SDCard:/FileFly/received directory, and only then:
		// TODO: add file record to internal SQLite database for use by DocumentListFragment
        // TODO: initiate the MainActivity after above operations complete via an Intent with extra information
		// TODO: in MainActivity, parse the extra information to mean that the app should just send an ACTION_VIEW intent to the system to open the file. This is so that the most recent activity of record is MainActivity and not ReceiveActivity.
		// TODO: verify that after the appropriate application opens the file, the back button will bring the user to the MainActivity annd not ReceiveActivity
		
		
    }

	/**
	 * Inflates the options menu in the {@link android.app.ActionBar} with the Settings menu item
	 * 
	 * @author Peter Piech
	 */
	@Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
	
	/**
	 * Listens for user selection of menu items in the {@link android.app.ActionBar}
	 * 
	 * @author Peter Piech
	 */
	@Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings)
        {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
	
	/**
	 * Gets the incoming {@link android.content.Intent} with a {@link android.content.Intent#ACTION_VIEW} schema
	 * and hands off the {@link android.net.Uri} to appropriate functions.
	 * 
	 * @author Saurabh Sharma
	 * 
	 */
	private void handleViewIntent() 
    {
        // Get the Intent action
        mIntent = getIntent();
        String action = mIntent.getAction();
        /*
         * For ACTION_VIEW, the Activity is being asked to display data.
         * Get the URI.
         */
        if (TextUtils.equals(action, Intent.ACTION_VIEW)) {
            // Get the URI from the Intent
            Uri beamUri = mIntent.getData();
            /*
             * Test for the type of URI, by getting its scheme value
             */
            if (TextUtils.equals(beamUri.getScheme(), "file")) {
                mPath = handleFileUri(beamUri);
            } else if (TextUtils.equals(beamUri.getScheme(), "content")) {
                mPath = handleContentUri(beamUri);
            } else {
            	// re-dispatch the intent to the system
            	startActivity(mIntent);
            }
            
            // call function to save file to FileFly/received folder
            saveFile();
        }
    }
	
	/**
	 * To save the received file to local storage in FileFly/received
	 * on the SD card.
	 * 
	 * @author Jacob Abramson
	 */
	private void saveFile() {
		
		// check if external storage is writable
		if (isExternalStorageWritable()) {
			
			// grab destination folder for transferred file
			File appDir = Environment.getExternalStorageDirectory(); // returns the path to the sd card
            String appReceivedDirPath = appDir.getAbsolutePath() +  "/FileFly/received/" + mOriginalFileName; // path to FileFly/received folder PLUS filename
            File appReceivedDir = new File(appReceivedDirPath);
            
            // grab source destination of file currently stored internally
            File source = new File(mPath);
            
            try {
            	FileUtils.copyFile(source, appReceivedDir);
            } catch (IOException e) {
            	e.printStackTrace();
            }
			
            Toast.makeText(this, "File saved successfully.", Toast.LENGTH_LONG).show(); // show the user this message
		}
		
		else {
			Toast.makeText(this, "External storage failure: do not use app.", Toast.LENGTH_LONG).show(); // show the user this message
		}
	}
    
	/**
	 * Checks if external storage is writable
	 * 
	 * @return True if external storage is writable, False otherwise
	 * 
	 * @author Jacob Abramson
	 */
	private boolean isExternalStorageWritable() {
	    String state = Environment.getExternalStorageState();
	    if (Environment.MEDIA_MOUNTED.equals(state)) {
	        return true;
	    }
	    return false;
	}
	
	/**
	 * To get the directory path, get the path part of the URI, which contains all of the URI 
     * except the file: prefix. Create a File from the path part, then get the parent path of the File:
	 * 
	 * @param beamUri The {@link android.net.Uri} embedded in the incoming {@link android.content.Intent}
	 * 
	 * @return The filename stored in the {@link android.net.Uri}
	 * 
	 * @author Saurabh Sharma
	 * 
	 */
    private String handleFileUri(Uri beamUri) 
    {
        // Get the path part of the URI
        String fileName = beamUri.getPath();
        
        // parse out first/last name and original filename
        grabNameFile(fileName);
        
        // Create a File object for this filename (with original fileName????)
        File copiedFile = new File(mOriginalFileName);
        //File copiedFile = new File(fileName);
        // Get a string containing the file's parent directory
        return copiedFile.getAbsolutePath();
    }
    
    /**
     * Parses out first and last names and original filename
     * 
     * @param The absolute path to a file
     * 
     * @author Jacob Abramson
     */
    private void grabNameFile(String absPath) {
    	
    	// parse filename from absolute path
    	String[] delimSlashes = absPath.split("/");
    	String fn = delimSlashes[delimSlashes.length - 1];
    	
    	// split string based on underscores
    	String[] result = fn.split("_");
    	
    	if (result.length < 3) {
    		//error, this means first/last name wasn't appended
    		return;
    	}
    	
    	mFirstName = result[0];
    	mLastName = result[1];
    	mOriginalFileName = "";
    	
    	// append original filename along with underscores if included
    	for (int i = 2; i < result.length; i++) {
    		mOriginalFileName += result[i];
    		if (i+1 < result.length) {
    			mOriginalFileName += "_";
    		}
    	}
    }
    
    /**
     * To test the authority of the content URI and retrieve the the path and file name for the 
     * transferred file
     * 
     * @param beamUri The {@link android.net.Uri} embedded in the incoming {@link android.content.Intent}
	 * 
	 * @return The filename stored in the {@link android.net.Uri}
     * 
     * @author Saurabh Sharma
     * 
     */
    private String handleContentUri(Uri beamUri) {
        // Position of the filename in the query Cursor
        int filenameIndex;
        // File object for the filename
        File copiedFile;
        // The filename stored in MediaStore
        String fileName;
        // Test the authority of the URI
        if (!TextUtils.equals(beamUri.getAuthority(), MediaStore.AUTHORITY)) {
            /*
             * As of now not including code to handle content URIs for other content providers
             */
        	return null;   
        // For a MediaStore content URI
        } else {
            // Get the column that contains the file name
            String[] projection = { MediaStore.MediaColumns.DATA };
            Cursor pathCursor =
                    getContentResolver().query(beamUri, projection,
                    null, null, null);
            // Check for a valid cursor
            if (pathCursor != null &&
                    pathCursor.moveToFirst()) {
                // Get the column index in the Cursor
                filenameIndex = pathCursor.getColumnIndex(
                        MediaStore.MediaColumns.DATA);
                // Get the full file name including path
                fileName = pathCursor.getString(filenameIndex);
                // Create a File object for the filename
                copiedFile = new File(fileName);
                // Return the parent directory of the file
                return new File(copiedFile.getParent()).toString();
             } else {
                // The query didn't work; return null
                return null;
             }
        }
    }
}