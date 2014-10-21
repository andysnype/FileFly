package com.procom.filefly;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import android.app.AlertDialog;
import android.app.Fragment;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.os.Environment;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.procom.filefly.ChooseFileDialogFragment.ChooseFileDialogListener;

/**
 * Inflates the user interface for the "Sends" tab.
 * <p>
 * SenderFragment creates the user interface for the "Sends" tab of {@link com.procom.filefly.MainActivity}
 * and implements a button to open a dialog for selecting a file to be transferred via the NFC Android Beam
 * API.
 * <p>
 * This class implements {@link android.view.View.OnClickListener} in order to process button clicks.
 * 
 * @author Peter Piech
 *
 */
public class SendFragment extends Fragment implements OnClickListener, ChooseFileDialogListener
{
	/* Member variables used for the Android Beam API: */
	private NfcAdapter mNfcAdapter; // Models the hardware NFC adapter
	private Uri[] mFileUris = new Uri[1]; // Array of length 1 to hold Uri of file to be transmitted
	private FileUriCallback mFileUriCallback; // Instance of inner class to provide Uris to Android Beam
	
	/* Member variables corresponding to text Views: */
	private EditText mFNameEditText; // View containing the first name of the sender
	private EditText mLNameEditText; // View containing the last name of the sender
	private TextView mFilenameTextView; // View containing the name of the file in the FileFly directory
	private Button mChooseFileButton; // Button corresponding to the "Choose File" button
	private Button mSendButton; // Button corresponding to the "Send" button
	
	private static final int sMaxLength = 50; // the maximum length allowed in the EditTexts
	private static final String sAcceptedChars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"; // the allowed characters the user can input
	
	/**
	 * Implements an array of {@link android.text.InputFilter}s that will be set on {@link android.widget.EditText}s to cleanse user input.
	 * 
	 * @author Peter Piech
	 */
	private static final InputFilter[] filterArray = new InputFilter[] {
		new InputFilter.LengthFilter(SendFragment.sMaxLength),
		
		new InputFilter() {

			@Override
			public CharSequence filter(CharSequence source, int start, int end,Spanned dest, int dstart, int dend) {
				if (end > start) {
					char[] acceptedChars = SendFragment.sAcceptedChars.toCharArray();
					for (int i = start; i < end; ++i) {
						if (!new String(acceptedChars).contains(String.valueOf(source.charAt(i)))) {
							return "";
						}
					}
				}
				return null;
			}
	}};
	
	/**
	 * Instantiates the class members prior to the view being inflated.
	 * 
	 * @author Peter Piech
	 */
	@Override
	public void onCreate(Bundle savedInstanceState)
	// TODO: verify NFC adapter is actually enabled
	// TODO: display dialog to user to enable NFC using a call to startActivity(new Intent(Settings.ACTION_NFC_SETTINGS))
	{
		super.onCreate(savedInstanceState);
		
		mNfcAdapter = NfcAdapter.getDefaultAdapter(getActivity()); // Get the NFC adapter
		if (mNfcAdapter != null) // i.e. the device has NFC
		{
			if (mNfcAdapter.isEnabled()) // verify that NFC adapter is turned on
			{
				mFileUriCallback = new FileUriCallback(); // Instantiate the Callback class used by the Android Beam API
				mNfcAdapter.setBeamPushUrisCallback(mFileUriCallback, getActivity()); // actually set the callback instance for the Android Beam API
			}
			else // i.e. the user must turn on NFC
			{
				//AlertDialog alert = new AlertDialog.Builder(getActivity()).create();
			}
		}
		else // i.e. the device does not have NFC
		{
			
		}
	}

	/**
	 * Inflates the layout from XML, gets the NFC Adapter, instantiates the Callback class used by
	 * the Android Beam API, and fetches the Views by their Ids.
	 * 
	 * @author Peter Piech
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View rootView = inflater.inflate(R.layout.send_fragment, container, false); // Populate the user interface
		
		/* Retrieve the various views in the viewgroup for later use */
		mFNameEditText = (EditText) rootView.findViewById(R.id.input_fname); // first name (of sender) EditText
		mLNameEditText = (EditText) rootView.findViewById(R.id.input_lname); // last name (of sender) EditText
		mFilenameTextView = (TextView) rootView.findViewById(R.id.selected_file); // filename (of file to transfer) TextView
		mChooseFileButton = (Button) rootView.findViewById(R.id.choose_file); // choose file button
		mSendButton = (Button) rootView.findViewById(R.id.send_file); // send file button
		
		mFNameEditText.setFilters(filterArray); // set the filterArray to filter user input
		mFNameEditText.setFilters(filterArray); // set the filterArray to filter user input
		
		/* Set the OnClickListeners for the buttons */
		mChooseFileButton.setOnClickListener(this); // set this instance of SenderFragment to be the OnClickListener handler
		mSendButton.setOnClickListener(this); // set this instance of SenderFragment to be the OnClickListener handler
		
        return rootView;
	}

	/**
	 * This is the method that the {@link android.view.View.OnClickListener} interface requires.
	 * It responds to button touches by the user with respect to the particular View's Id.
	 * 
	 * @author Peter Piech
	 */
	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.choose_file: // The "Choose File" button was clicked
			((MainActivity)getActivity()).closeKeyboard(); // close the keyboard first
			
			ChooseFileDialogFragment choosefiledialogFrag = new ChooseFileDialogFragment(); // initialize the choose file dialog window fragment
			choosefiledialogFrag.setTargetFragment(this, 0); // set the result target for the choose file dialog window fragment to be this fragment
			choosefiledialogFrag.show(getActivity().getFragmentManager(), "CHOOSE_FILE_DIALOG_FRAG"); // show the user the choose file dialog window fragment
			break;  // end of case R.id.choose_file
			
		case R.id.send_file: // The "Send" button was clicked
			((MainActivity)getActivity()).closeKeyboard(); // close the keyboard first
			
			if (mFNameEditText.getText().toString().equals("") || mLNameEditText.getText().toString().equals("")) // i.e. the user has not entered text into the EditTexts
			{
				Toast.makeText(getActivity(), "Please enter your full name first.", Toast.LENGTH_LONG).show(); // show the user this message
				break;
			}
			
			String transfer_file = mFilenameTextView.getText().toString(); // get the filename String from the TextView's contents
			if (transfer_file.equals("<No File Selected>")) // i.e. the default value that means no file has been chosen yet
			{
				Toast.makeText(getActivity(), "Please choose a file first.", Toast.LENGTH_LONG).show(); // show the user this message
				break; // cancel any further processing of the button click
			}
			
			/* full_name is the prefix of the file to be sent so that
			 * the name of the person can be determined from the filename
			 * itself on the receiving end. */
			String full_name = mLNameEditText.getText().toString() + "_" + mFNameEditText.getText().toString() + "_";
			
			/* tmp_filename is the name of the file to be copied into the
			 * app's private data storage from the root of the FileFly folder
			 * located in the root of the sdcard.  It will then be transmitted
			 * via NFC. */
			String tmp_filename = full_name + transfer_file; // the name of the file to be transferred from the app's private data storage after 
			
			File appDir = Environment.getExternalStorageDirectory(); // returns the path to the sd card
            String appDirPath = appDir.getPath() +  "/FileFly"; // path to FileFly folder on sd card
            File requestFile = new File(appDirPath, transfer_file); // file requested by user to be transmitted
            requestFile.setReadable(true, false); // Android Beam API requires file to be set to readable
            
			File extDir = getActivity().getExternalFilesDir(null); // returns the path to the app's private data storage on the sdcard
			File tmpExtFile = new File (extDir.getPath() + tmp_filename); // path to the temporary file to be transmitted via NFC
			
			FileChannel src = null; // source file stream (the file pointed to by requestFile)
			FileChannel dest = null; // destination file stream (the file pointed to by tmpExtFile)
			
			try // attempt to write the destination file
			{
				tmpExtFile.createNewFile(); // create the file if it doesn't exist
				tmpExtFile.setReadable(true, false); // Android Beam API requires file to be set to readable
				tmpExtFile.setWritable(true, false); // set the file to writable as an added precaution
				src = new FileInputStream(requestFile).getChannel(); // retrieve the FileStream input for the requestFile
				dest = new FileOutputStream(tmpExtFile).getChannel(); // retrieve the FileStream output for the tmpExtFile which will be truncated if it exists
				dest.transferFrom(src, 0, src.size()); // transfer the data from src to dest
			}
			catch (IOException e) // catch and file input/output errors
			{
				Toast.makeText(getActivity(), "File error! Please try again.", Toast.LENGTH_LONG).show(); // show the user this message
				break; // cancel any further processing of the button click
			}
			finally // be sure to close resources to avoid leaks
			{
				try // attempt to close src and dest
				{
					src.close(); // close src file stream
					dest.close(); // close dest file stream
				}
				catch (IOException e) // catch errors while closing
				{
					Toast.makeText(getActivity(), "File error! Please try again.", Toast.LENGTH_LONG).show(); // show the user this message
				}
			}
			
			Uri fileUri = Uri.fromFile(tmpExtFile); // return a Uri for the file to be transferred by NFC as required for the Android Beam API callback
			if (fileUri != null) // verify fileUri was created successfully
			{
				mFileUris[0] = fileUri; // set the single position of the array to be Uri returned for the file
			}
			else // i.e. fileUri is null
			{
				Toast.makeText(getActivity(), "File error! Please try again.", Toast.LENGTH_LONG).show(); // show the user this message
				break; // cancel any further processing of the button click
			}
			Toast.makeText(getActivity(), "Success! Now tap phones.", Toast.LENGTH_LONG).show(); // show the user this message
			break; // end of case R.id.send_file
			
		default:
			break;
		}
	}
	
	/**
	 * Provides the necessary methods for the Callback used by the Android Beam API
	 * for NFC file transfer using an array of {@link android.net.Uri}s corresponding
	 * to the files being transferred.
	 * 
	 * @author Peter Piech
	 *
	 */
	private class FileUriCallback implements NfcAdapter.CreateBeamUrisCallback
	{
		/**
		 * Empty default constructor.
		 * 
		 * @author Peter Piech
		 */
		public FileUriCallback() {}

		/**
		 * Create content {@link android.net.Uri}s as needed to share with another device.
		 * 
		 * @author Peter Piech
		 */
		@Override
		public Uri[] createBeamUris(NfcEvent event)
		{
			return mFileUris; // Simply return the array of Uris. No dynamic generation is performed.
		}
	}

	/**
	 * Interface callback method to retrieve the result of the 
	 * user file selection in the {@link com.procom.filefly.ChooseFileDialogFragment}.
	 * 
	 * @author Peter Piech
	 */
	@Override
	public void onFileChosen(ChooseFileDialogFragment dialog)
	{
		mFilenameTextView.setText(dialog.getChosenFilename());
	}
}