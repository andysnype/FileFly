package com.procom.filefly.model;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Models the properties of transmitted files/documents for the
 * purpose of storing the information in a Collection for easy
 * access by such classes as DocumentListFragment.
 * 
 * @author Peter Piech
 * @version 0.7b
 * @since 2014-10-12
 */
public class Document
{
	/** Name and extension of the file */
	private String mFilename;

	/** Sender's first name */
	private String mOwnerFirstName;
	
	/** Sender's last name */
	private String mOwnerLastName;
	
	/** Date and time of transfer */
	private Date mDateTransferred;
	
	/** The {@link java.text.SimpleDateFormat} used to store {@link java.util.Date}s as {@link java.lang.String}s */
	private static final SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss:SS zzz");
	
	/**
	 * Constructs an instance of {@link com.procom.filefly.model.Document}.  Additional constructor may be added
	 * to take in String fields to populate the DateTransferred member variable.
	 * 
	 * @author Peter Piech
	 */
	public Document(String filename, String ownerFirstName, String ownerLastName, Date dateTransferred)
	{	
		mFilename = filename;
		mOwnerFirstName = ownerFirstName;
		mOwnerLastName = ownerLastName;
		mDateTransferred = dateTransferred;
	}
	
	/* Getters for the various private fields of the Document class */
	/**
	 * Returns the value of the filename field.
	 * 
	 * @return The {@link Document}'s file name including extension
	 * @author Peter Piech
	 */
	public String getFilename()
	{
		return new String(mFilename);
	}
	
	/**
	 * Returns the value of the owner first name field.
	 * 
	 * @return The {@link Document}'s sender's first name
	 * @author Peter Piech
	 */
	public String getOwnerFirstName()
	{
		return new String(mOwnerFirstName);
	}
	
	/**
	 * Returns the value of the owner last name field.
	 * 
	 * @return The {@link Document}'s sender's last name
	 * @author Peter Piech
	 */
	public String getOwnerLastName()
	{
		return new String(mOwnerLastName);
	}
	
	/**
	 * Returns the value of the date transferred field.
	 * 
	 * @return The {@link Document}'s date of reception
	 * @author Peter Piech
	 */
	public Date getDateTransferred()
	{
		return new Date(mDateTransferred.getTime());
	}
	
	/**
	 * Returns the string form of the date transferred field.
	 * 
	 * @return The {@link Document}'s date of reception
	 * @author Peter Piech
	 */
	public String getDateTransferredString()
	{
		return mSimpleDateFormat.format(mDateTransferred);
	}
}