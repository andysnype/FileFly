package com.procom.filefly;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.apache.commons.io.FileUtils;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.widget.Toast;

/**
 * Used to respond to an ACTION_VIEW intent from the Android Beam API.
 * Copies the transferred file into the received subdirectory of the
 * FileFly directory found on the root of the SD Card.
 * 
 * @author Peter Piech, Saurabh Sharma, Jacob Abramson
 * @version 0.2a
 * @since 2014-10-15
 *
 */
public class Receive
{
	/** A {@link java.io.File} representing the path to the transferred file */
	private File mFileSource;
    
    /** The parsed out sender's first name */
    private String mFirstName;
    
    /** The parsed out sender's last name */
    private String mLastName;
    
    /** The parsed out original filename that was selected by the user */
    private String mOriginalFileName;
    
    /** The {@link java.util.Date} representing the time of file transfer */
    private Date mDateTransferred;
    
    /** The attached {@link android.app.Activity} that received the ACTION_VIEW intent */
    private Activity mActivity;
    
    /**
     * Constructs a new {@link com.procom.filefly.Receive} object.
     * 
     * @author Peter Piech
     */
    public Receive(Activity activity)
    {
    	mDateTransferred = new Date();
    	mFirstName = new String();
    	mLastName = new String();
    	mOriginalFileName = new String();
    	mActivity = activity;
    }
	
	/**
	 * Gets the incoming {@link android.content.Intent} with a {@link android.content.Intent#ACTION_VIEW} schema
	 * and hands off the {@link android.net.Uri} to appropriate functions.
	 * 
	 * @author Saurabh Sharma
	 * 
	 */
	public void handleViewIntent() 
    {
		// Get the Intent action
        Intent intent = getIntent();
        String action = intent.getAction();
        /*
         * For ACTION_VIEW, the Activity is being asked to display data.
         * Get the URI.
         */
        if (action.equals(Intent.ACTION_VIEW))
        {
        	// Get the URI from the Intent
            Uri beamUri = intent.getData();
            
            // Test for the type of URI, by getting its scheme value
            if (TextUtils.equals(beamUri.getScheme(), "file"))
            {
            	mFileSource = handleFileUri(beamUri);
            }
            else if (TextUtils.equals(beamUri.getScheme(), "content"))
            {
            	mFileSource = handleContentUri(beamUri);
            }
            else
            {
            	// re-dispatch the intent to the system
            	mActivity.startActivity(intent);
            }

            // call function to save file to FileFly/received folder
            saveFile();
        }
    }

	/**
	 * To get the directory path, get the path part of the URI, which contains all of the URI 
     * except the file: prefix. Create a File from the path part, then get the parent path of the File:
	 * 
	 * @param beamUri The {@link android.net.Uri} embedded in the incoming {@link android.content.Intent}
	 * 
	 * @return The file stored in the {@link android.net.Uri}
	 * 
	 * @author Saurabh Sharma
	 * 
	 */
    private File handleFileUri(Uri beamUri) 
    {
        String fileName = beamUri.getPath(); // Get the path part of the URI
        mOriginalFileName = grabNameFile(fileName); // parse out first/last name and original filename
        return new File(fileName);
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
    private File handleContentUri(Uri beamUri)
    {
        int filenameIndex; // Position of the filename in the query Cursor
        String fileName; // The filename stored in MediaStore

        // Test the authority of the URI
        if (!beamUri.getAuthority().equals(MediaStore.AUTHORITY)) {
            /*
             * As of now not including code to handle content URIs for other content providers
             */
        	return null;
        }
        else // For a MediaStore content URI
        {
            // Get the column that contains the file name
        	
            String[] projection = { MediaStore.MediaColumns.DATA };
            Cursor pathCursor = mActivity.getContentResolver().query(beamUri, projection, null, null, null);
            
            // Check for a valid cursor
            if (pathCursor != null && pathCursor.moveToFirst())
            {
                // Get the column index in the Cursor
                filenameIndex = pathCursor.getColumnIndex(MediaStore.MediaColumns.DATA);
                
                // Get the full file name including path
                fileName = pathCursor.getString(filenameIndex);
                
                mOriginalFileName = grabNameFile(fileName);
                return new File(fileName);
        	}
            else
            {
                // The query didn't work; return null
                return null;
             }
        }
    }
    
    /**
     * Parses out first and last names and original filename
     * 
     * @param The absolute path to a file
     * 
     * @author Jacob Abramson
     */
    private String grabNameFile(String absPath)
    {
    	
    	// parse filename from absolute path
    	String[] delimSlashes = absPath.split("/");
    	String fn = delimSlashes[delimSlashes.length - 1];
    	fn = fn.substring(5); // removes "files" from the beginning which is prepended by the Android Beam API
    	
    	// split string based on underscores
    	String[] result = fn.split("_");
    	
    	if (result.length < 3)
    	{
    		//error, this means first/last name wasn't appended
    		return null;
    	}
    	
    	// the order of the names is (last,first) when sent by the sender:
    	mFirstName = result[1];
    	mLastName = result[0];
    	
    	return fn;
    }
    
    /**
	 * To save the received file to local storage in FileFly/received
	 * on the SD card.
	 * 
	 * @author Jacob Abramson
	 */
	private void saveFile()
	{
		
		// check if external storage is writable
		if (isExternalStorageWritable())
		{
			
			// grab destination folder for transferred file
            String fileDestPath = Environment.getExternalStorageDirectory().getAbsolutePath()  + "/FileFly/received/" + mOriginalFileName; // path to FileFly/received folder PLUS filename
            File fileDest = new File(fileDestPath);
            
            try
            {
            	FileUtils.copyFile(mFileSource, fileDest);
            }
            catch (IOException e)
            {
            	e.printStackTrace();
            }
			
            Toast.makeText(mActivity, "File saved successfully!", Toast.LENGTH_LONG).show(); // show the user this message
		}
		
		else
		{
			Toast.makeText(mActivity, "External storage failure: do not use app.", Toast.LENGTH_LONG).show(); // show the user this message
		}
	}
	
	/**
	 * Checks if external storage is writable
	 * 
	 * @return True if external storage is writable, False otherwise
	 * 
	 * @author Jacob Abramson
	 */
	private boolean isExternalStorageWritable()
	{
	    return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
	}
    
    /**
	 * Retrieves the attached {@link android.app.Activity}'s intent
	 * 
	 * @return The intent from the attached {@link android.app.Activity}
	 * 
	 * @author Peter Piech
	 */
	private Intent getIntent()
	{
		return mActivity.getIntent();
	}
}