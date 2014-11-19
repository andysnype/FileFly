package com.procom.filefly;

import java.io.File;
import java.util.Locale;

import com.procom.filefly.util.FilesIntentHandler;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

/**
 * The {@link android.support.v4.app.FragmentActivity} responsible for handling the
 * {@link android.app.ActionBar} and {@link android.support.v4.view.ViewPager}
 * user elements, handling selection of tabs corresponding to either of
 * {@link com.procom.filefly.SendFragment} or {@link com.procom.filefly.DocumentListFragment},
 * and initializing write access to the /"SD Card"/FileFly/received/ directory tree.
 * 
 * @author Peter Piech
 * @version 0.7b
 * @since 2014-09-28
 *
 */
public class MainActivity extends FragmentActivity implements ActionBar.TabListener
{

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v13.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;
    
    /** Instance of {@link com.procom.filefly.SendFragment} to be managed by the ViewPager */
    private SendFragment mSendFragment = new SendFragment();
    
    /** The {@link android.support.v4.view.ViewPager} that will host the section contents */
    private ViewPager mViewPager;
    
    /** Instance of {@link com.procom.filefly.SqliteController} to act as an interface with SQLite */
    private SqliteController mSqliteController = new SqliteController(this);
    
    /** Instance of {@link com.procom.filefly.DocumentListFragment} to be managed by the ViewPager */
    private DocumentListFragment mDocumentListFragment = new DocumentListFragment(mSqliteController);
    
    /**
     * Boolean used in conjunction with {@link com.procom.filefly.MainActivity#onNewIntent} to determine if
     * a file was received and if the "Received Files" tab should be opened
     */
    private boolean openReceivedFileAndTab;

    /**
     * Inflates the layout from XML, creates the {@link android.app.ActionBar} as
     * an {@link android.app.ActionBar#NAVIGATION_MODE_TABS}, initializes the
     * {@link android.support.v4.view.ViewPager}, and adds the tabs to
     * {@link android.app.ActionBar}.
     * 
     * @author Peter Piech
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        
        createDirectories(); // create the /FileFly and /FileFly/received directories on the SD Card (external storage)
        
        openReceivedFileAndTab = false; // initialize to false
        
        onNewIntent(getIntent()); // handle the case where a file is being received and the app was not already open
        
        setContentView(R.layout.activity_main); // inflate XML layout

        // Set up the action bar.
        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener()
        {
        	/**
             * Notifies the {@link android.app.ActionBar} of the index of the
             * tab when it is selected by the user.
             * 
             * @author Peter Piech
             */
            @Override
            public void onPageSelected(int position)
            {
                actionBar.setSelectedNavigationItem(position);
            }
            
            /**
             * Notifies the {@link android.app.ActionBar} of the index of the
             * tab when it is scrolled to by the user.
             * 
             * @author Peter Piech
             */
            @Override
            public void onPageScrolled(int position, float offset, int offsetPixels) {}

            /**
             * Notifies the {@link android.app.ActionBar} when a scroll
             * is initiated or finished by the user.
             * <p>
             * Called whenever the the scroll state changes (i.e. right when a scroll
             * is started, or when a scroll finishes).
             * 
             * @author Peter Piech
             */
            @Override
            public void onPageScrollStateChanged(int state)
            {
            	/* Following two lines close the soft keyboard when it is open after typing in the SenderFragment */
            	closeKeyboard(); // close the keyboard
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++)
        {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                             .setText(mSectionsPagerAdapter.getPageTitle(i))
                             .setTabListener(this));
        }
    }
    
    /**
     * Handles resumption from being paused.  This will check if a file was received and
     * open the "Received Files" tab if so. This method is called by the system
     * after {@link #onNewIntent} finishes.
     * 
     * @author Peter Piech
     */
    @Override
    protected void onResume()
    {
    	super.onResume();
    	if (openReceivedFileAndTab)
    	{
    		openReceivedFileAndTab = false;
    		mDocumentListFragment.notifyDataBaseChanged();
    		getActionBar().setSelectedNavigationItem(1); // open the "Received Files" tab
    	}
    }
    
    /**
     * Handles new, incoming {@link android.content.Intent}s. This method only performs actions
     * on the intent if it has the {@link android.content.Intent#ACTION_VIEW} schema
     * indicating it is an incoming file.
     * 
     * @author Peter Piech
     */
    @Override
	protected void onNewIntent(Intent intent)
	{
		super.onNewIntent(intent);
		setIntent(intent); // replace the Activity's existing intent with this new intent
		String intentAction = intent.getAction(); // retrieve the intent action
		if (intentAction.equals(Intent.ACTION_VIEW)) // i.e. an incoming file intent
    	{
    		FilesIntentHandler receive = new FilesIntentHandler(this);
    		String filename = receive.handleViewIntent();
    		openReceivedFileAndTab = true;
    		startActivity(FilesIntentHandler.openFile(this, filename));
    	}
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
     * Handles the {@link android.app.ActionBar}'s response
     * when a tab is selected by the user.
     * 
     * @author Peter Piech
     */
    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction)
    {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    /**
     * Handles the {@link android.app.ActionBar}'s response
     * when a tab is un-selected by the user.
     * 
     * @author Peter Piech
     */
    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {}

    /**
     * Handles the {@link android.app.ActionBar}'s response
     * when a tab is re-selected by the user.
     * 
     * @author Peter Piech
     */
    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {}
    
    /**
     * Checks the status of the external storage and creates necessary
     * folders on the SD Card.
     * 
     * @author Peter Piech
     */
    private void createDirectories()
    {
    	/* BEGIN COMMENT SECTION
         * =============================================================================================
         * 
         *  The code below creates two directories on the smartphone SD Card.
         *  1. sdcard/FileFly (to hold documents to be transmitted via NFC)
         *  2. sdcard/Filefly/received (to hold documents received from an NFC transmission)
         *  =============================================================================================
         */
        
        boolean mExternalStorageAvailable = false;
        boolean mExternalStorageWriteable = false;

        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state))
        {
            mExternalStorageAvailable = true;
            mExternalStorageWriteable = true;
        }
        else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state))
        {
            mExternalStorageAvailable = true;
            mExternalStorageWriteable = false;
        }
        else
        {
            mExternalStorageAvailable = false;
            mExternalStorageWriteable = false;
        }
        boolean success = mExternalStorageAvailable && mExternalStorageWriteable;
        if (success)
        {
        	File appDir = Environment.getExternalStorageDirectory(); // returns the path to the sd card
            String appDirPath = appDir.getPath() +  "/FileFly"; // path to FileFly folder on sd card
            appDir = new File(appDirPath);
            success = appDir.exists(); // test to see if folder exists already
            if (!success) // i.e. if the folder does not already exist
            {
            	success = appDir.mkdir(); // create the directory
            }
            if (success)
            {
            	appDir = new File(appDirPath + "/received");
            	success = appDir.exists(); // test to see if folder exists already
            	if (!success) // i.e. if the folder does not already exist
            	{
            		success = appDir.mkdir(); // create the directory
            	}
            }
        }
        if (!success) // i.e. the directory(s) doesn't exist and could not create
        {
        	Toast.makeText(this, "External storage failure: do not use app.", Toast.LENGTH_LONG).show(); // show the user this message
        }
        
        /* END COMMENT SECTION
         * =============================================================================================
         */
    }
    
    /**
     * Simple method to close the keyboard.
     * Known callers: MainActivity.onPageScrollStateChanged(), SendFragment.onClick()
     * 
     * @author Peter Piech
     */
    public void closeKeyboard()
    {
    	final InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mViewPager.getWindowToken(), 0);
    }
    
    /**
     * Getter for the {@link com.procom.filefly.SqliteController}
     * 
     * @return The {@link com.procom.filefly.SqliteController}
     * 
     * @author Peter Piech
     */
    public SqliteController getSqliteController() { return mSqliteController; }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     * 
     * @author Peter Piech
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter
    {

    	/**
    	 * Contructs an instance of {@link com.procom.filefly.MainActivity.SectionsPagerAdapter}
    	 * using the supertype {@link android.support.v13.app.FragmentPagerAdapter} constructor.
    	 * 
    	 * @author Peter Piech
    	 */
        public SectionsPagerAdapter(FragmentManager fm)
        {
            super(fm);
        }

        /**
         * Returns a reference to the {@link android.app.Fragment}
         * at the given tab index
         * 
         * @return A reference to the {@link android.app.Fragment}
         * at the given tab index
         * 
         * @author Peter Piech
         */
        @Override
        public Fragment getItem(int position)
        {
        	// getItem is called to instantiate the fragment for the given page.
        	switch (position)
        	{
        	case 0:
        		return mSendFragment;
        	case 1:
        		return mDocumentListFragment;
        	default:
        		return null;
        	}            
        }

        /**
         * Returns the number of tabs being
         * managed by the {@link android.support.v4.view.ViewPager}
         * 
         * @return The number of tabs being
         * managed by the {@link android.support.v4.view.ViewPager}
         * 
         * @author Peter Piech
         */
        @Override
        public int getCount()
        {
            // Show 2 total pages.
            return 2;
        }

        /**
         * Returns the title of the tab at the given
         * tab index
         * 
         * @return The title of the tab at the given
         * tab index
         * 
         * @author Peter Piech
         */
        @Override
        public CharSequence getPageTitle(int position)
        {
            Locale l = Locale.getDefault();
            switch (position)
            {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
                default:
                	return null;
            }
        }
    }
}