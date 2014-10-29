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
 * @version 0.2a
 * @since 2014-10-18
 */
public class ChooseFileDialogFragment extends DialogFragment
{
	/** The {@link java.lang.reflect.Array} of filenames found in the FileFly directory on external storage */
	private String[] mFileList;
	
	/** The {@link java.io.File} representing the FileFly external storage directory */
	private File mPath;
	
	/** The filename (including extension) of the file the user selected */
	private String mChosenFile;
	
	/** Instance of the abstract {@link com.procom.filefly.ChooseFileDialogFragment.ChooseFileDialogListener}
	 * interface (which means it is an instance of a class that implements it.  This object listens for item
	 * selection through the callback method {@link com.procom.filefly.ChooseFileDialogFragment.ChooseFileDialogListener#onFileChosen}
	 * called in {@link }*/
	private ChooseFileDialogListener mListener;
	
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
	 * If no files exist in the FileFly directory, a message dialog window is shown to the user
	 * to indicate this instead of a list of filenames.
	 * 
	 * @author Peter Piech
	 */
	@Override
	@NonNull
	public Dialog onCreateDialog(Bundle savedInstanceState)
	{
		AlertDialog.Builder builder = new Builder(getActivity()); // create a new dialog builder
		
		if (mFileList.length < 1) // i.e. no files in FileFly folder
		{
			builder.setTitle(R.string.no_files_title); // set the title of the dialog window
			builder.setMessage(R.string.no_files_text); // set the dialog window to have a message instead of a list
		}
		else // i.e. one or more files (with extensions) are in the FileFly folder
		{
			builder.setTitle(R.string.choose_file); // set the title of the dialog window
			builder.setItems(mFileList, new DialogInterface.OnClickListener() // set the items the user chooses from
			{
				/**
				 * Marks the chosen file for return later
				 * @author Peter Piech
				 */
				@Override
				public void onClick(DialogInterface dialog, int which) // implement the onClick method to determine what happens when the user chooses an item
				{
					mChosenFile = mFileList[which]; // set the chosen file to be the string at the index clicked on
					mListener.onFileChosen(ChooseFileDialogFragment.this); // the actual callback to the target fragment with the result passed as a parameter
					}
				});
		}
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
		/**
		 * Calls back to the instance of {@link com.procom.filefly.ChooseFileDialogFragment.ChooseFileDialogListener}
		 * to notify of the result of the {@link com.procom.filefly.ChooseFileDialogFragment}.
		 * 
		 * @param dialog The instance of the {@link com.procom.filefly.ChooseFileDialogFragment} shown to the user.
		 * 
		 */
		public void onFileChosen(ChooseFileDialogFragment dialog);
	}
}