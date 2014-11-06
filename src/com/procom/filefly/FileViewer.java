package com.procom.filefly;

import java.io.File;
import java.io.IOException;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;


/**
 * Utility class for opening files
 * 
 * @author Jacob Abramson
 * @version 0.2a
 * @since 2014-11-06
 *
 */
public class FileViewer {

	/**
	 * Creates an {@link android.content.Intent} to start an Android application
	 * on the user's device capable of opening the provided {@link java.io.File}
	 * 
	 * USAGE: File myFile = new File("file url goes here");
	 * 		  FileViewer.openFile(mContext, myFile);
	 * 
	 * @return void
	 * @author Jacob Abramson
	 */
	public static void openFile(Context context, File url) throws IOException {
		
		// create URI
		File file = url;
		Uri uri = Uri.fromFile(file);
		
		// create intent
		Intent intent = new Intent(Intent.ACTION_VIEW);
		
		// check what kind of file is being opened by comparing url with extensions
		// so Android knows what application to use to open the file
		
		// word document
		if (url.toString().contains(".doc") || url.toString().contains(".docx")) {
			intent.setDataAndType(uri,  "application/msword");
		}
		
		// pdf document
		else if (url.toString().contains(".pdf")) {
			intent.setDataAndType(uri, "application/pdf");
		}
		
		// powerpoint presentation
		else if (url.toString().contains(".ppt") || url.toString().contains(".pptx")) {
			intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
		}
		
		// excel spreadsheet
		else if (url.toString().contains(".xls") || url.toString().contains(".xlsx")) {
			intent.setDataAndType(uri, "application/vnd.ms-excel");
		}
		
		// ZIP file
		else if (url.toString().contains(".zip") || url.toString().contains(".rar")) {
			intent.setDataAndType(uri, "application/zip");
		}
		
		// RTF file
		else if (url.toString().contains(".rtf")) {
			intent.setDataAndType(uri, "application/rtf");
		}
		
		// WAV audio file
		else if (url.toString().contains(".wav") || url.toString().contains(".mp3")) {
			intent.setDataAndType(uri, "audio/x-wav");
		}
		
		// GIF file
		else if (url.toString().contains(".gif")) {
			intent.setDataAndType(uri, "image/gif");
		}
		
		// JPG, JPEG, or PNG image
		else if (url.toString().contains(".jpg") || url.toString().contains(".jpeg") || url.toString().contains(".png")) {
			intent.setDataAndType(uri, ".jpeg");
		}
		
		// text file
		else if (url.toString().contains(".txt")) {
			intent.setDataAndType(uri, "text/plain");
		}
		
		// video files
		else if (url.toString().contains(".3gp") || url.toString().contains(".mpg") || url.toString().contains(".mpeg") ||
				 url.toString().contains(".mpe") || url.toString().contains(".mp4") || url.toString().contains(".avi")) {
			intent.setDataAndType(uri, "video/*");
		}
		
		// manage other extensions
		else {
			
			// Android should show all applications installed on the device
			// and user can choose which application to open it with
			intent.setDataAndType(uri, "*/*");	
		}
		
		// start activity with flagged intent
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}
}
