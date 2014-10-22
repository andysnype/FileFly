package com.procom.filefly;

import java.io.File;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;

/**
 * The {@link android.app.Activity} that will launch in response to an
 * ACTION_VIEW intent from the Android Beam API, copy the transferred
 * file into the received subdirectory of the FileFly directory found
 * on the root of the SD Card.
 * 
 * @author Saurabh Sharma, Peter Piech
 *
 */
public class ReceiveActivity extends FragmentActivity
{
	private String mParentPath; // A File object containing the path to the transferred files
    private Intent mIntent; // Incoming Intent
	
	@Override
    protected void onCreate(Bundle savedInstanceState)
    {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_receive);
        // TODO: implement response to ACTION_VIEW intent to fetch transferred file
		// TODO: copy transferred file into SDCard:/FileFly/received directory, and only then:
		// TODO: add file record to internal SQLite database for use by DocumentListFragment
        // TODO: initiate the MainActivity after above operations complete via an Intent with extra information
		// TODO: in MainActivity, parse the extra information to mean that the app should just send an ACTION_VIEW intent to the system to open the file. This is so that the most recent activity of record is MainActivity and not ReceiveActivity.
		// TODO: verify that after the appropriate application opens the file, the back button will bring the user to the MainActivity annd not ReceiveActivity
    }
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
	
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
	 * <Add Description Here Saurabh>
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
                mParentPath = handleFileUri(beamUri);
            } else if (TextUtils.equals(
                    beamUri.getScheme(), "content")) {
                mParentPath = handleContentUri(beamUri);
            }
        }
    }
    
	/**
	 * To get the directory path, get the path part of the URI, which contains all of the URI 
     * except the file: prefix. Create a File from the path part, then get the parent path of the File:
	 * 
	 * @author Saurabh Sharma
	 * 
	 */
    public String handleFileUri(Uri beamUri) 
    {
        // Get the path part of the URI
        String fileName = beamUri.getPath();
        // Create a File object for this filename
        File copiedFile = new File(fileName);
        // Get a string containing the file's parent directory
        return copiedFile.getParent();
    }
    
    /**
     * To test the authority of the content URI and retrieve the the path and file name for the 
     * transferred file
     * 
     * @author Saurabh Sharma
     * 
     */
    public String handleContentUri(Uri beamUri) {
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