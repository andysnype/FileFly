package com.procom.filefly;

import java.io.File;
import java.util.Locale;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

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
    private Fragment mSenderFragment = new SenderFragment(); // instance of the SenderFragment to be managed by the ViewPager
    private Fragment mReceiverFragment = new ReceiverFragment(); // instance of the ReceiverFragment to be managed by the ViewPager
    private Fragment mDocumentListFragment = new DocumentListFragment(); // instance of the DocumentListFragment to be managed by the ViewPager

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        /* BEGIN COMMENT SECTION
         * =============================================================================================
         * 
         *  The code below creates two directories on the smartphone SD Card.
         *  1. sdcard/FileFly (to hold documents to be transmitted via NFC)
         *  2. sdcard/Filefly/received (to hold documents received from an NFC transmission)
         *  
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
            @Override
            /**
             * Called whenever a scroll has been fully completed and a different page is now selected.
             * 
             * @author Peter Piech
             */
            public void onPageSelected(int position)
            {
                actionBar.setSelectedNavigationItem(position);
            }
            
            @Override
            public void onPageScrolled(int position, float offset, int offsetPixels) {}

            @Override
            /**
             * Called whenever the the scroll state changes (i.e. right when a scroll is started, or when a scroll finishes).
             * 
             * @author Peter Piech
             */
            public void onPageScrollStateChanged(int state)
            {
            	/* Following two lines close the soft keyboard when it is open after typing in the SenderFragment */
            	final InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mViewPager.getWindowToken(), 0);
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

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction)
    {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {}

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {}

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter
    {

        public SectionsPagerAdapter(FragmentManager fm)
        {
            super(fm);
        }

        @Override
        public Fragment getItem(int position)
        {
        	switch (position)
        	{
        	case 0:
        		return mSenderFragment;
        	case 1:
        		return mReceiverFragment;
        	case 2:
        		return mDocumentListFragment;
        	}
        	return null;
            // getItem is called to instantiate the fragment for the given page.
        }

        @Override
        public int getCount()
        {
            // Show 3 total pages.
            return 3;
        }

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
                case 2:
                    return getString(R.string.title_section3).toUpperCase(l);
            }
            return null;
        }
    }
}