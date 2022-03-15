/*
 * @(#)BoardwalkCollaborationManager.java   1.0
 *
 * Copyright 2004 BoardwalkTech, Inc. All rights reserved.
 * BoardwalkTech/CONFIDENTIAL. Use is subject to license terms.
 */

package boardwalk.collaboration;

import java.util.*;
import java.sql.*;
import com.boardwalk.collaboration.*;
import com.boardwalk.whiteboard.*;
import com.boardwalk.exception.BoardwalkException;
import com.boardwalk.database.*;
import boardwalk.connection.BoardwalkConnection;

/**
 * Service to manage a Boardwalk collaborations and whiteboards.
 * <p>
 * Collaborations and Whiteboards are Boardwalk entities to manage the tables
 * (in which the actual spreadsheet data resides).
 * Boardwalk Collaborations represent an idea of a project. For example, a
 * collaboration "Budget2005" is a higher level idea and it can contain tables
 * that hold the actual data for Salary Plans, Inventory Plans, Revenue Forecasts etc.
 * </p><p>
 * Whiteboards are equivalent to folders and are used to organize tables in any
 * collaboration. A default "Main" folder is created with a new Collaboration. A
 * Whiteboard contains Boardwalk Tables
 * </p><p>
 * Collaborations themselves hang from Boardwalk Neighborhoods. Since Boardwalk
 * Neighborhoods group the users, the Collaborations get classified by groups/departments.
 * </p>
 */
 public class BoardwalkCollaborationManager
 {
	 private BoardwalkCollaborationManager(){}
    /**
    * Create a new collaboration in the boardwalk database. The new collaboration
    * will be created in the Neighborhood that you are currently member of.
    * @param connection a jdbc database connection object
    * @param name the collaboration name
    * @param description a short description
    * @return a database id of the new collaboration
    * @exception BoardwalkException if a database access error occurs
    */
    public static int createCollaboration( BoardwalkConnection connection,
											String name,
											String description
											)
    throws BoardwalkException
    {
		int collabId = -1;
		TransactionManager tm = null;
		try
		{
			tm = new TransactionManager(connection.getConnection(), connection.getUserId());
			int tid = tm.startTransaction();
			collabId = CollaborationManager.createCollaboration(
												connection.getConnection(),
												name,
												description,
												connection.getMemberId(),
												tid,
												1);
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

		return collabId;
    }
    /**
    * Delete a collaboration from the boardwalk database
    * @param connection a jdbc database connection object
    * @param collabId database id of the collaboration
    * @exception BoardwalkException if a database access error occurs
    */
    public static void deleteCollaboration( BoardwalkConnection connection,
										   int collabId
										 )
    throws BoardwalkException
    {
		TransactionManager tm = null;
		try
		{
			tm = new TransactionManager(connection.getConnection(), connection.getUserId());
			int tid = tm.startTransaction();
			CollaborationManager.purgeCollaboration(
												connection.getConnection(),
												collabId);
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

    public static int createWhiteboard( BoardwalkConnection connection,
    									 String name,
    									 int collabId )
    throws BoardwalkException
    {
		int wbid = -1;
		TransactionManager tm = null;
		try
		{
			tm = new TransactionManager(connection.getConnection(), connection.getUserId());
			int tid = tm.startTransaction();
			wbid = WhiteboardManager.createWhiteboard(
										connection.getConnection(),
										name,
										0, 2, 0,
										collabId,
										tid,
										1);
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

		return wbid;
	}
    /**
    * Delete a whiteboard from the boardwalk database
    * @param connection a jdbc database connection object
    * @param whiteboardId database id of the collaboration
    * @exception BoardwalkException if a database access error occurs
    */
    public static void deleteWhiteboard( BoardwalkConnection connection,
										   int whiteboardId
										 )
    throws BoardwalkException
    {
		TransactionManager tm = null;
		try
		{
			tm = new TransactionManager(connection.getConnection(), connection.getUserId());
			int tid = tm.startTransaction();
			WhiteboardManager.purgeWhiteboard(connection.getConnection(), whiteboardId);
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

	public static BoardwalkCollaborationNode getCollaborationTree(BoardwalkConnection connection,
																  int collabId)
	throws BoardwalkException
	{
		BoardwalkCollaborationNode bcn = null;
		try
		{
			Vector wbnt = WhiteboardManager.getWhiteboardAndTablesByCollaborationAndNeighborhood(
											connection.getConnection(),
											collabId,
											connection.getMemberId());
			CollaborationTreeNode ctn = (CollaborationTreeNode)wbnt.firstElement();
			bcn = new BoardwalkCollaborationNode(ctn);

		}
		catch (Exception e)
		{
			// bwe
			e.printStackTrace();
		}

		return bcn;
	}

	public static BoardwalkCollaborationNode getCollaborationTree(BoardwalkConnection connection,
																  String collabName)
	throws BoardwalkException
	{
		BoardwalkCollaborationNode bcn = null;
		try
		{
			int collabId = -1;
			collabId = CollaborationManager.getCollabIdByName(
												connection.getConnection(),
												collabName,
												connection.getNeighborhoodId()
												);
			if (collabId > 0)
			{
				Vector wbnt = WhiteboardManager.getWhiteboardAndTablesByCollaborationAndNeighborhood(
												connection.getConnection(),
												collabId,
												connection.getMemberId());
				CollaborationTreeNode ctn = (CollaborationTreeNode)wbnt.firstElement();
				bcn = new BoardwalkCollaborationNode(ctn);
			}

		}
		catch (Exception e)
		{
			// bwe
			e.printStackTrace();
		}

		return bcn;
	}
    /**
    * Get a list of collaborations for the specified neighborhood
    * @param connection a jdbc database connection object
    * @param nhId the database id of the neighborhood
    * @return a <code>Vector</code> of <code>Integer</code>
    *	objects that are the database ids of Boardwalk collaborations
    * @exception BoardwalkException if a database access error occurs
    */
    public static Vector getCollaborationsForNeighborhood(
										BoardwalkConnection connection,
										int nhId
									   )
    throws BoardwalkException
    {
        TransactionManager tm = null;
        Vector collabList = new Vector();
        try
        {
            tm = new TransactionManager(connection.getConnection(), connection.getUserId());
            int tid = tm.startTransaction();

            Vector cl = CollaborationManager.getCollaborationsOfNeighborhood(
											connection.getConnection(),
											nhId);
			Iterator cli = cl.iterator();
			while (cli.hasNext())
			{
				CollaborationTreeNode ctn = (CollaborationTreeNode)cli.next();
				collabList.addElement(new Integer(ctn.getId()));
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

        return collabList;
    }

 };