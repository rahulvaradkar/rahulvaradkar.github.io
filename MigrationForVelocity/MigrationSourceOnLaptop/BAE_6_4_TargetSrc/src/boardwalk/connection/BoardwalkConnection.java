/*
 * @(#)BoardwalkConnection.java	1.0
 *
 * Copyright 2004 BoardwalkTech, Inc. All rights reserved.
 * BoardwalkTech/CONFIDENTIAL. Use is subject to license terms.
 */

package boardwalk.connection;

/**
 * BoardwalkConnection object contains information about the authenticated
 * connected started using <code>ConnectionManager</code>
 */
 public class BoardwalkConnection
 {
	int userId;
	int memberId;
	String nhName;
	int nhId;
	String userName;
	java.sql.Connection connection;

	private BoardwalkConnection(){}

	protected BoardwalkConnection( int a_userId,
								   int a_memberId,
								   String a_nhName,
								   int a_nhId,
								   String a_userName,
								   java.sql.Connection a_connection)
	{
		userId = a_userId;
		memberId = a_memberId;
		nhName = a_nhName;
		nhId = a_nhId;
		userName = a_userName;
		connection = a_connection;
	}

	/**
	* Returns the Boardwalk database user id
	*/
	public int	getUserId()
	{
		return userId;
	}
	/**
	* Returns the Boardwalk database member id
	*/
	public int	getMemberId()
	{
	 	return memberId;
	}
	/**
	* Returns the Boardwalk neighborhood name
	*/
	public String	getNeighborhoodName()
	{
	 	return nhName;
	}
	/**
	* Returns the Boardwalk database user id
	*/
	public int	getNeighborhoodId()
	{
	 	return nhId;
	}
	/**
	* Returns the Boardwalk user name
	*/
	public String	getUserName()
	{
	 	return userName;
	}
	/**
	* Returns the database connection
	*/
	public java.sql.Connection	getConnection()
	{
	 	return connection;
	}
 };