/*
 * @(#)BoardwalkUserManager.java   1.0
 *
 * Copyright 2004 BoardwalkTech, Inc. All rights reserved.
 * BoardwalkTech/CONFIDENTIAL. Use is subject to license terms.
 */

package boardwalk.neighborhood;

import java.util.*;
import java.sql.*;
import com.boardwalk.user.*;
import com.boardwalk.exception.BoardwalkException;
import com.boardwalk.database.*;
import boardwalk.connection.BoardwalkConnection;
import boardwalk.neighborhood.BoardwalkMember;
import com.boardwalk.member.Member;

/**
 * Service to manage Boardwalk users
 */
 public class BoardwalkUserManager
 {
     private BoardwalkUserManager(){}
    /**
    * Create a new user in the boardwalk database.
    * @param connection a jdbc database connection object
    * @param emailAddress the email address of the user or login name
    * @param password the password for the user
    * @param firstName the first name of the user
    * @param lastName the last name of the user
    * @return a database id of the new user
    * @exception BoardwalkException if a database access error occurs
    */
    public static int createUser( BoardwalkConnection connection,
								  String emailAddress,
								  String usrName, // external user id - sujith 05/11/2016
								  String password,
								  String firstName,
								  String lastName,
								  int Activeflag
								)
    throws BoardwalkException
    {
        int userId = -1;
        TransactionManager tm = null;
        try
        {
            tm = new TransactionManager(connection.getConnection(), connection.getUserId());
            int tid = tm.startTransaction();
            NewUser nu = new NewUser(
								emailAddress,
								usrName,  // external user id - sujith 05/11/2016
								password,
								firstName,
								lastName,
								Activeflag
								);
            userId = UserManager.createUser(connection.getConnection(), nu);
            tm.commitTransaction();
        }
        catch (Exception e)
        {
            // bwe
            e.printStackTrace();
            try
            {
                tm.rollbackTransaction();
            }
            catch (SQLException sqe)
            {
                //bwe
                sqe.printStackTrace();
            }
        }

        return userId;
    }
    /**
    * Update Boardwalk user profile
    * @param connection a jdbc database connection object
    * @param userId database id of the Boardwalk User
    * @param emailAddress database id of the Boardwalk User
    * @param firstName database id of the Boardwalk User
    * @param lastName database id of the Boardwalk User
    * @exception BoardwalkException if a database access error occurs
    */
    public static void updateProfile( BoardwalkConnection connection,
                                           int userId,
                                           String emailAddress,
                                           String firstName,
                                           String lastName
                                         )
    throws BoardwalkException
    {
        TransactionManager tm = null;
        try
        {
            tm = new TransactionManager(connection.getConnection(), connection.getUserId());
            int tid = tm.startTransaction();
            UserManager.updateProfile(
							connection.getConnection(),
							userId,
							firstName,
							lastName,
							emailAddress,
							emailAddress);
            tm.commitTransaction();
        }
        catch (Exception e)
        {
            // bwe
            e.printStackTrace();
            try
            {
                tm.rollbackTransaction();
            }
            catch (SQLException sqe)
            {
                //bwe
                sqe.printStackTrace();
            }
        }
    }

    /**
    * Update Boardwalk user password
    * @param connection a jdbc database connection object
    * @param userId database id of the Boardwalk User
    * @param newPassword the new password
    * @param oldPassword the old password
    * @exception BoardwalkException if a database access error occurs
    */
    public static void updatePassword( BoardwalkConnection connection,
                                           int userId,
                                           String oldPassword,
                                           String newPassword
                                         )
    throws BoardwalkException
    {
        TransactionManager tm = null;
        try
        {
            tm = new TransactionManager(connection.getConnection(), connection.getUserId());
            int tid = tm.startTransaction();
            UserManager.updatePassword(
							connection.getConnection(),
							userId,
							oldPassword,
							newPassword);
            tm.commitTransaction();
        }
        catch (Exception e)
        {
            // bwe
            e.printStackTrace();
            try
            {
                tm.rollbackTransaction();
            }
            catch (SQLException sqe)
            {
                //bwe
                sqe.printStackTrace();
            }
        }
    }

    /**
    * Get a list of memberships for the specified user
    * @param connection a jdbc database connection object
    * @param userId database id of the Boardwalk User
    * @return <code>Vector</code> of <code>BoardwalkMember</code> objects
    * @exception BoardwalkException if a database access error occurs
    */
    public static Vector getMembershipList( BoardwalkConnection connection,
                                           int userId
                                         )
    throws BoardwalkException
    {
		Vector memberList = new Vector();
        TransactionManager tm = null;
        try
        {
            tm = new TransactionManager(connection.getConnection(), connection.getUserId());
            int tid = tm.startTransaction();
            Vector ml = UserManager.getMembershipListForUser(
										connection.getConnection(),
										userId);
			Iterator mli = ml.iterator();
			while(mli.hasNext())
			{
				Member m = (Member)mli.next();
				memberList.addElement(new BoardwalkMember(m));
			}

 			tm.commitTransaction();
        }
        catch (Exception e)
        {
            // bwe
            e.printStackTrace();
            try
            {
                tm.rollbackTransaction();
            }
            catch (SQLException sqe)
            {
                //bwe
                sqe.printStackTrace();
            }
        }

        return memberList;
    }

    /**
    * Fetch <code>BoardwalkUser</code> given username
    * @param connection a jdbc database connection object
    * @param userName the login username for the user
    * @return a <code>BoardwalkUser</code> object
    * @exception BoardwalkException if a database access error occurs
    */
    public static BoardwalkUser getUser( BoardwalkConnection connection,
                                            String userName
                                         )
    throws BoardwalkException
    {
		BoardwalkUser bu = null;
        TransactionManager tm = null;
        try
        {
            tm = new TransactionManager(connection.getConnection(), connection.getUserId());
            int tid = tm.startTransaction("Read",
            			"Fetching user information for username = " + userName);
            User u = UserManager.getUser(
										connection.getConnection(),
										userName);
		 	bu = new BoardwalkUser(u);

 			tm.commitTransaction();
        }
        catch (Exception e)
        {
            // bwe
            e.printStackTrace();
            try
            {
                tm.rollbackTransaction();
            }
            catch (SQLException sqe)
            {
                //bwe
                sqe.printStackTrace();
            }
        }

        return bu;
    }
    /**
    * Fetch <code>BoardwalkUser</code> given username
    * @param connection a jdbc database connection object
    * @param userName the login username for the user
    * @return a <code>BoardwalkUser</code> object
    * @exception BoardwalkException if a database access error occurs
    */
    public static BoardwalkUser getUser( BoardwalkConnection connection,
                                         int userId
                                         )
    throws BoardwalkException
    {
		BoardwalkUser bu = null;
        TransactionManager tm = null;
        try
        {
            tm = new TransactionManager(connection.getConnection(), connection.getUserId());
            int tid = tm.startTransaction("Read",
            			"Fetching user information for userid = " + userId);
            NewUser nu = UserManager.getUserProfile(
										connection.getConnection(),
										userId);
			User u = new User(userId, nu.getAddress(),
								nu.getFirstName(), nu.getLastName());

		 	bu = new BoardwalkUser(u);

 			tm.commitTransaction();
        }
        catch (Exception e)
        {
            // bwe
        	System.out.println("Exception caught in boardwalk.neighborhood.BoardwalkUser.getUser();");
            e.printStackTrace();
            try
            {
                tm.rollbackTransaction();
            }
            catch (SQLException sqe)
            {
                //bwe
                sqe.printStackTrace();
            }
        }

        return bu;
    }
    /**
    * Fetch list of all Boardwalk users in the database
    * @param connection a jdbc database connection object
    * @return <code>Vector</code> of <code>BoardwalkUser</code> objects
    * @exception BoardwalkException if a database access error occurs
    */
    public static Vector getUserList( BoardwalkConnection connection
                                         )
    throws BoardwalkException
    {
		Vector userList = new Vector();
        TransactionManager tm = null;
        try
        {
            tm = new TransactionManager(connection.getConnection(), connection.getUserId());
            int tid = tm.startTransaction();
            Vector users = UserManager.getUserList(
										connection.getConnection());
			Iterator ui = users.iterator();
			while (ui.hasNext())
			{
				User u = (User)ui.next();
				userList.addElement(new BoardwalkUser(u));
			}
 			tm.commitTransaction();
        }
        catch (Exception e)
        {
            // bwe
            e.printStackTrace();
            try
            {
                tm.rollbackTransaction();
            }
            catch (SQLException sqe)
            {
                //bwe
                sqe.printStackTrace();
            }
        }

        return userList;
    }
 };