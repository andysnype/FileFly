package com.procom.filefly;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

/**
 * The {@link android.app.Activity} that will launch in response to an
 * ACTION_VIEW intent from the Android Beam API, copy the transferred
 * file into the received subdirectory of the FileFly directory found
 * on the root of the SD Card.
 * 
 * @author Peter Piech
 *
 */
public class ReceiveActivity extends Activity
{
	
	@Override
    protected void onCreate(Bundle savedInstanceState)
    {
		super.onCreate(savedInstanceState);
        // TODO: create an XML layout and inflate it here
        // TODO: implement response to ACTION_VIEW intent to fetch transferred file
		// TODO: add file record to internal SQLite database for use by DocumentListFragment
        // TODO: initiate the MainActivity after above operations complete
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
}