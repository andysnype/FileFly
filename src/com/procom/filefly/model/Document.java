package com.procom.filefly.model;

import java.util.Date;

public class Document
/**
 * The Document class models the properties of transmitted files/documents for the
 * purpose of storing the information in a Collection for easy access by such classes
 * as DocumentListFragment.
 */
{
	
	private String _filename; /* Name and extension of the file */
	private String _ownerFirstName; /* Owner's first name */
	private String _ownerLastName; /* Owner's last name */
	private Date _dateTransferred; /* Date and time of transfer */
	
	public Document(String filename, String ownerFirstName, String ownerLastName, Date dateTransferred)
	/**
	 * Constructor for the Document class.  Additional constructor may be added
	 * to take in String fields to populate the DateTransferred member variable.
	 */
	{	
		_filename = filename;
		_ownerFirstName = ownerFirstName;
		_ownerLastName = ownerLastName;
		_dateTransferred = dateTransferred;
	}
	
	/* Getters for the various private fields of the Document class */
	public String getFilename() { return new String(_filename); }
	public String getOwnerFirstName() { return new String(_ownerFirstName); }
	public String getOwnerLastName() { return new String(_ownerLastName); }
	public Date getDateTransferred() { return new Date(_dateTransferred.getTime()); }
}