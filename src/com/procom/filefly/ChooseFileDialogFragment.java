package com.procom.filefly;

import java.io.File;
import java.io.FilenameFilter;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.app.DialogFragment;

/**
 * Handles the user input of selecting a file and
 * draws the user interface for this interaction.
 * 
 * @author Peter Piech
 */
public class ChooseFileDialogFragment extends DialogFragment
{
	private String[] mFileList; // Array of filenames found
	private File mPath; // Path to external storage directory
	private String mChosenFile; // Filename the user selected
	private ChooseFileDialogListener mListener; // interface instance; listens for item selection
	
	/**
	 * Constructor that initializes private member variables
	 * 
	 * @author Peter Piech
	 */
	public ChooseFileDialogFragment()
	{
		mChosenFile = null; // initialize to null so it can be returned later
		mPath = new File(Environment.getExternalStorageDirectory() + "/FileFly"); // gets the directory to the SD Card FileFly folder
		loadFileList(); // initializes the array of strings with filenames
	}

	/**
	 * Initializes the callback variable by retrieving a reference to
	 * the target fragment which must implement the {@link ChooseFileDialogListener}
	 * interface.
	 * 
	 * @author Peter Piech
	 */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		try
		{
			mListener = (ChooseFileDialogListener) getTargetFragment();
		}
		catch (ClassCastException e)
		{
			throw new ClassCastException("Calling Fragment must implement com.procom.filefly.ChooseFileDialogFragment.ChooseFileDialogListener");
		}
	}

	/**
	 * Draws the dialog box and sets the content using an {@link android.app.AlertDialog.Builder}.
	 * 
	 * @author Peter Piech
	 */
	@Override
	@NonNull
	public Dialog onCreateDialog(Bundle savedInstanceState)
	{
		AlertDialog.Builder builder = new Builder(getActivity()); // create a new dialog builder
		builder.setTitle(R.string.choose_file); // set the title of the dialog window
		builder.setItems(mFileList, new DialogInterface.OnClickListener() // set the items the user chooses from
		{
			/**
			 * Marks the chosen file for return later
			 * 
			 * @author Peter Piech
			 */
			@Override
			public void onClick(DialogInterface dialog, int which) // implement the onClick method to determine what happens when the user chooses an item
			{
				mChosenFile = mFileList[which]; // set the chosen file to be the string at the index clicked on
				mListener.onFileChosen(ChooseFileDialogFragment.this); // the actual callback to the target fragment with the result passed as a parameter
			}
		});
		return builder.create(); // return the actual dialog
	}
	
	/**
	 * Retrieves the filename the user selected.
	 * 
	 * @author Peter Piech
	 */
	public String getChosenFilename()
	{
		return new String(mChosenFile); // return a copy of the string, not the reference itself
	}
	
	/**
	 * Fetches the list of files in the app's folder on external storage.
	 * 
	 * @author Peter Piech
	 */
	private void loadFileList()
	{
	    if(mPath.exists()) // i.e. does the directory on the SD Card exist
	    {
	        FilenameFilter filter = new FilenameFilter() // filter all files we don't care about
	        {

	            /**
	             * Accepts files with any extension at all.
	             * <p>
	             * The purpose for this is that the Android Beam API behaves badly when a file with no extension is provided.
	             * 
	             * @author Peter Piech
	             */
	            @Override
	            public boolean accept(File dir, String filename)
	            {
	            	int extBeginIndex = filename.lastIndexOf("."); // find the index of the last period
	            	if (extBeginIndex == -1) // if no period is in the file name
	            	{
	            		return false; // reject the file
	            	}
	                String extension = filename.substring(extBeginIndex + 1, filename.length()); // get the extension
	                if (extension.equals("")) // i.e. the period is the last character in the filename
	                {
	                	return false; // reject the file
	                }
	                return true; // the file has an extension
	            }
	        };
	        mFileList = mPath.list(filter); // actually perform the filtered search
	    }
	    else
	    {
	        mFileList= new String[0]; // no-elements array since no files are present
	    }
	}
	
	/**
	 * Interface that the target fragment must implement in order to
	 * receive the result code of the choose file dialog window
	 * selection by the user.
	 * 
	 * @author Peter Piech
	 */
	public interface ChooseFileDialogListener
	{
		public void onFileChosen(ChooseFileDialogFragment dialog);
	}
}