/*
 * @(#)BoardwalkConnectionManager.java	1.0
 *
 * Copyright 2004 BoardwalkTech, Inc. All rights reserved.
 * BoardwalkTech/CONFIDENTIAL. Use is subject to license terms.
 */

package boardwalk.connection;

import java.util.*;
import java.sql.*;

import com.boardwalk.exception.*;
import com.boardwalk.user.*;
import com.boardwalk.member.*;
import com.boardwalk.database.DatabaseLoader;
import com.boardwalk.database.QueryLoader;


/**
 * Basic service to create a <code>BoardwalkConnection</code>
 */
 public class BoardwalkConnectionManager
 {

	/**
	* Establish an authenticated Boardwalk connection to the given database connection URL.
	*
	* @param connectionURL a database connection URL
	* @param userName the Boardwalk user on whose behalf the connection is being
    *   made
    * @param password the Boardwalk user's password
    * @param memberId the database id for the member.
    *   it can be -1 if there the user is a member of a single neighborhood
	* @return a <code>BoardwalkConnection</code> object
	* @exception BoardwalkException if a database access error occurs
	*/
	public static BoardwalkConnection getBoardwalkConnection(
												String connectionURL,
												String userName,
												String password,
												int memberId
												)
	throws BoardwalkException
	{
        Connection connection = null;
        try
        {
            //DriverManager.registerDriver(new com.microsoft.jdbc.sqlserver.SQLServerDriver());
			Class.forName("net.sourceforge.jtds.jdbc.Driver");
        }
        catch(Exception e)
        {
            System.out.println("Problem registering JDBC driver 3");
            e.printStackTrace();
        }

        try
        {
            connection =  DriverManager.getConnection(connectionURL);
            //System.out.println("Connection established successfully");
        }
        catch( SQLException sqe )
        {
            System.out.println("There is a Database connection problem");
            sqe.printStackTrace();
        }

		int userId = -1;
		String nhName = null;
		int nhId = -1;
		Member m = null;
		BoardwalkConnection bwcon = null;

		// Assume SQLSERVER for now
		DatabaseLoader.databasetype = "SQLSERVER";
		DatabaseLoader.sqlpath = "sql\\";
		DatabaseLoader.queryLoader = new QueryLoader(null);
		userId = UserManager.authenticateUser(connection, userName, password);
		if (userId != -1)
		{
			try
			{
				Vector mList = UserManager.getMembershipListForUser(connection, userId);

				if (mList.size() != 1)
				{
					Iterator mi = mList.iterator();

					while (mi.hasNext())
					{
						m = (Member)mi.next();
						if (m.getId() == memberId)
						{
							nhName = m.getNeighborhoodName();
							nhId = m.getNeighborhoodId();
							break;
						}
					}
				}
				else
				{
					m = (Member)mList.firstElement();
					memberId = m.getId();
					nhName = m.getNeighborhoodName();
					nhId = m.getNeighborhoodId();
				}
			}
			catch (SystemException se)
			{
				//bwe
				se.printStackTrace();
			}

		}
		if (m != null)
			bwcon = new BoardwalkConnection(userId,memberId,nhName,nhId,userName,connection);

		return bwcon;
	}

	/**
	* Establish an authenticated Boardwalk connection to the given database connection.
	*
	* @param connection a jdbc database connection object
	* @param userName the Boardwalk user on whose behalf the connection is being
    *   made
    * @param password the Boardwalk user's password
    * @param memberId the database id for the member.
    *   it can be -1 if there the user is a member of a single neighborhood
	* @return a <code>BoardwalkConnection</code> object
	* @exception BoardwalkException if a database access error occurs
	*/
	public static BoardwalkConnection getBoardwalkConnection( java.sql.Connection connection,
												String userName,
												String password,
												int memberId
												)
	throws BoardwalkException
	{
		int userId = -1;
		String nhName = null;
		int nhId = -1;
		Member m = null;
		BoardwalkConnection bwcon = null;

		// Assume SQLSERVER for now
		DatabaseLoader.databasetype = "SQLSERVER";
		DatabaseLoader.sqlpath = "sql\\";
		DatabaseLoader.queryLoader = new QueryLoader(null);
		userId = UserManager.authenticateUser(connection, userName, password);
		if (userId != -1)
		{
			try
			{
				Vector mList = UserManager.getMembershipListForUser(connection, userId);

				if (mList.size() != 1)
				{
					Iterator mi = mList.iterator();

					while (mi.hasNext())
					{
						m = (Member)mi.next();
						if (m.getId() == memberId)
						{
							nhName = m.getNeighborhoodName();
							nhId = m.getNeighborhoodId();
							break;
						}
					}
				}
				else
				{
					m = (Member)mList.firstElement();
					memberId = m.getId();
					nhName = m.getNeighborhoodName();
					nhId = m.getNeighborhoodId();
				}
			}
			catch (SystemException se)
			{
				//bwe
				se.printStackTrace();
			}

		}
		if (m != null)
			bwcon = new BoardwalkConnection(userId,memberId,nhName,nhId,userName,connection);

		return bwcon;
	}

	/**
	* We need to discuss on the ethics of this method.
	* To provide a Boardwalk connection to the given database connection.
	* Here we dont authenticate the user as this call is called after the user has logged 
	* in to the system.
	*
	* @param connection a jdbc database connection object
	* @param userId the Boardwalk user on whose behalf the connection is being
    *   made
	* @param userName the Boardwalk user on whose behalf the connection is being
    *   made
    * @param memberId the database id for the member.
    *   it can be -1 if there the user is a member of a single neighborhood
	* @return a <code>BoardwalkConnection</code> object
	* @exception BoardwalkException if a database access error occurs
	*/
	public static BoardwalkConnection getBoardwalkConnection( java.sql.Connection connection,
												int aiUserId,
												String asUserName,
												int memberId
												)
	throws BoardwalkException
	{
		int userId = aiUserId;
		String nhName = null;
		int nhId = -1;
		Member m = null;
		BoardwalkConnection bwcon = null;

		// Assume SQLSERVER for now
		DatabaseLoader.databasetype = "SQLSERVER";
		DatabaseLoader.sqlpath = "sql\\";
		DatabaseLoader.queryLoader = new QueryLoader(null);
		if (userId != -1)
		{
			try
			{
				Vector mList = UserManager.getMembershipListForUser(connection, userId);

				if (mList.size() != 1)
				{
					Iterator mi = mList.iterator();

					while (mi.hasNext())
					{
						m = (Member)mi.next();
						if (m.getId() == memberId)
						{
							nhName = m.getNeighborhoodName();
							nhId = m.getNeighborhoodId();
							break;
						}
					}
				}
				else
				{
					m = (Member)mList.firstElement();
					memberId = m.getId();
					nhName = m.getNeighborhoodName();
					nhId = m.getNeighborhoodId();
				}
			}
			catch (SystemException se)
			{
				//bwe
				se.printStackTrace();
			}

		}
		if (m != null)
			bwcon = new BoardwalkConnection(userId,memberId,nhName,nhId,asUserName,connection);

		return bwcon;
	}

	/**
	* Retrieves a list of memberships for a given login
	*
	* @param connection a jdbc database connection object
	* @param userName the Boardwalk user on whose behalf the connection is being
    *   made
    * @param password the Boardwalk user's password
	* @return a <code>Vector</code> of <code>BoardwalkMember</code> objects
	* @exception BoardwalkException if a database access error occurs
	*/
	public static Vector getMemberships( java.sql.Connection connection,
												String userName,
												String password
												)
	throws BoardwalkException
	{
		int userId = -1;
		String nhName = null;
		int nhId = -1;
		Member m = null;
		Vector memberList = new Vector();

		// Assume SQLSERVER for now
		DatabaseLoader.databasetype = "SQLSERVER";
		DatabaseLoader.sqlpath = "sql\\";
		DatabaseLoader.queryLoader = new QueryLoader(null);

		// Authenticate the user and fetch the user id
		userId = UserManager.authenticateUser(connection, userName, password);

		if (userId != -1)
		{
			try
			{
				Vector mList = UserManager.getMembershipListForUser(connection, userId);

				Iterator mi = mList.iterator();

				while (mi.hasNext())
				{
					m = (Member)mi.next();

					memberList.addElement(new boardwalk.neighborhood.BoardwalkMember(
													m.getId(),
													m.getUserId(),
													m.getNeighborhoodId(),
													m.getNeighborhoodName()));
				}

			}
			catch (SystemException se)
			{
				//bwe
				se.printStackTrace();
			}

		}

		return memberList;
	}

 };