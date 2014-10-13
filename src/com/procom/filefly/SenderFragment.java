package com.procom.filefly;

import android.app.Fragment;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

public class SenderFragment extends Fragment implements OnClickListener
{
	private NfcAdapter mNfcAdapter;
	private Uri[] mFileUris = new Uri[1];
	private FileUriCallback mFileUriCallback;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View rootView = inflater.inflate(R.layout.sender_fragment, container, false);
		
		mNfcAdapter = NfcAdapter.getDefaultAdapter(getActivity());
		mFileUriCallback = new FileUriCallback();
		mNfcAdapter.setBeamPushUrisCallback(mFileUriCallback, getActivity());
		
		// TODO: implement setting of the value of mFileUris
		// TODO: test example code below
		//String transferFile = "transferimage.jpg";
        //File extDir = getExternalFilesDir(null);
        //File requestFile = new File(extDir, transferFile);
        //requestFile.setReadable(true, false);
        // Get a URI for the File and add it to the list of URIs
        //fileUri = Uri.fromFile(requestFile)
		
        return rootView;
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.choose_file:
			// TODO: implement choose file dialog
			break;
		case R.id.send_file:
			// TODO: implement send file via NFC
			break;
		default:
			break;
		}
		
	}
	
	private class FileUriCallback implements NfcAdapter.CreateBeamUrisCallback
	{
		
		public FileUriCallback()
		{
			
		}

		@Override
		public Uri[] createBeamUris(NfcEvent event)
		{
			// TODO Auto-generated method stub
			return null;
		}
		
	}
}