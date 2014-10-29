package com.procom.filefly.model;

import java.util.Date;

/**
 * Models the properties of transmitted files/documents for the
 * purpose of storing the information in a Collection for easy
 * access by such classes as DocumentListFragment.
 * 
 * @author Peter Piech
 * @version 0.2a
 * @since 2014-09-14
 */
public class Document
{
	/** Name and extension of the file */
	private String _filename;

	/** Sender's first name */
	private String _ownerFirstName;
	
	/** Sender's last name */
	private String _ownerLastName;
	
	/** Date and time of transfer */
	private Date _dateTransferred;
	
	/**
	 * Constructs an instance of {@link com.procom.filefly.model.Document}.  Additional constructor may be added
	 * to take in String fields to populate the DateTransferred member variable.
	 * 
	 * @author Peter Piech
	 */
	public Document(String filename, String ownerFirstName, String ownerLastName, Date dateTransferred)
	{	
		_filename = filename;
		_ownerFirstName = ownerFirstName;
		_ownerLastName = ownerLastName;
		_dateTransferred = dateTransferred;
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
		return new String(_filename);
	}
	
	/**
	 * Returns the value of the owner first name field.
	 * 
	 * @return The {@link Document}'s sender's first name
	 * @author Peter Piech
	 */
	public String getOwnerFirstName()
	{
		return new String(_ownerFirstName);
	}
	
	/**
	 * Returns the value of the owner last name field.
	 * 
	 * @return The {@link Document}'s sender's last name
	 * @author Peter Piech
	 */
	public String getOwnerLastName()
	{
		return new String(_ownerLastName);
	}
	
	/**
	 * Returns the value of the date transferred field.
	 * 
	 * @return The {@link Document}'s date of reception
	 * @author Peter Piech
	 */
	public Date getDateTransferred()
	{
		return new Date(_dateTransferred.getTime());
	}
}