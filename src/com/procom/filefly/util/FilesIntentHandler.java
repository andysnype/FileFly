package com.procom.filefly.util;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.apache.commons.io.FileExistsException;
import org.apache.commons.io.FileUtils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

/**
 * Set of methods used to handle and issue new
 * ACTION_VIEW intents. 
 * 
 * @author Peter Piech, Saurabh Sharma, Jacob Abramson
 * @version 0.2a
 * @since 2014-11-7
 *
 */
public class FilesIntentHandler
{
	/** A {@link java.io.File} representing the path to the transferred file */
	private File mFileSource;
	
	/** A {@link java.io.File} representing the path to the file after being saved locally */
	private File mFileDest;
	
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
     * Constructs a new {@link com.procom.filefly.util.FilesIntentHandler} object
     * and initializes member fields.
     * 
     * @author Peter Piech
     */
    public FilesIntentHandler(Activity activity)
    {
    	mDateTransferred = new Date(); // sets this date as the current timestamp
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
        mFileSource = null;
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
     * @return The unparsed proper file name which excludes the "files" prefix that the Android Beam API prepends
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
	 * Saves the received file to local storage in FileFly/received
	 * on the SD card.
	 * @param fileSrc The {@link java.io.File} representing the file to be moved
	 * @return The {@link java.io.File} representing the file saved in the received folder
	 * @author Jacob Abramson
	 */
	private void saveFile()
	{
		// check if external storage is writable
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
		{
			
			// grab destination folder for transferred file
            String fileDestPath = Environment.getExternalStorageDirectory().getAbsolutePath()  + "/FileFly/received/" + mOriginalFileName; // path to FileFly/received folder PLUS filename
            mFileDest = new File(fileDestPath);
            
            try
            {
            	// TODO: remove Apache Commons IO library
            	// TODO: implement file move algorithm using JDK 1.6 methods only that handles filename exists conflicts
            	FileUtils.copyFile(mFileSource, mFileDest);
            }
            catch (FileExistsException e)
            {
            	mFileDest = null;
            	e.printStackTrace();
            }
            catch (IOException e)
            {
            	mFileDest = null;
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
	 * Creates an {@link android.content.Intent} to start an Android application
	 * on the user's device capable of opening the provided {@link java.io.File}
	 * 
	 * @return An {@link android.content.Intent} that will open the file provided
	 * @author Jacob Abramson, Peter Piech
	 */
	public static Intent openFile(File fileSrc)
	{
		// create URI
		Uri uri = Uri.fromFile(fileSrc);
		// create intent
		Intent intent = new Intent(Intent.ACTION_VIEW);
		
		String extension = MimeTypeMap.getFileExtensionFromUrl(fileSrc.getAbsolutePath()); // get the extension from the filename
		String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension); // figure out the MIME type by the extension
		
		intent.setDataAndTypeAndNormalize(uri, mimeType); // set the data Uri and MIME type String on the Intent
		return intent;
	}
	
	/**
	 * Retrieves the attached {@link android.app.Activity}'s intent
	 * 
	 * @return The intent from the attached {@link android.app.Activity}
	 * @author Peter Piech
	 */
	private Intent getIntent()
	{
		return mActivity.getIntent();
	}
}