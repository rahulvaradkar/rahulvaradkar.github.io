/*
 * @(#)BoardwalkNeighborhoodManager.java   1.0
 *
 * Copyright 2004 BoardwalkTech, Inc. All rights reserved.
 * BoardwalkTech/CONFIDENTIAL. Use is subject to license terms.
 */

package boardwalk.neighborhood;

import java.util.*;
import java.sql.*;
import com.boardwalk.neighborhood.*;
import com.boardwalk.member.*;
import com.boardwalk.exception.BoardwalkException;
import com.boardwalk.database.*;
import boardwalk.connection.BoardwalkConnection;

/**
 * Service to manage Boardwalk Neighborhoods and their Relations
 * <p>
 * Neighborhood in Boardwalk is essentially a group of Boardwalk users.
 * Neighborhoods are heirarchical. The Neighborhood heirarchy serves
 * multiple purposes:
 * </p>
 * <p>
 * 1. Classification of Boardwalk Collaborations: Collaborations created
 *    by members of a specific Neighborhood are attached to the Neighborhood.
 *    If the Neighborhoods represent  departments/groups in an enterprise
 *    the projects or Collaborations initiated by the departments are
 *	  automatically classified under the department
 * </p><p>
 * 2. Access control of Boardwalk Tables: Built into the concept of heirarchical
 *    neighborhoods is the concept of Relations. Neighborhoods are naturally
 *    related to each other by relationships such as PARENT, CHILDREN, PEERS.
 *    Boardwalk also allows creation of custom Relations. These Relations are
 *    then available in Boardwalk Tables to define access control against.
 *    The advantage of this approach is that while the Neighborhood heirachy
 *    and membership may change with Business conditions, so long as the
 *    Relations remain the same, the access control does not need to be modified
 *    for the Boardwalk Tables in the system.
 * </p><p>
 * 3. Automatic consolidation of rows in Boardwalk Tables: Rows in Boardwalk tables
 *    can be assigned to Boardwalk Users. Further, when any user creates a new
 *    row in a Boardwalk Table, the row is automatically assigned to the user.
 *    By intersecting the Neighborhood Heirarchy with the table row assignments,
 *    Boardwalk can naturally provide consolidation of rows at every level in the
 *    Neighborhood heirarchy. As a result, a user can access a different set of
 *    rows depending on his position in the hierarchy.
 * </p>
 */
 public class BoardwalkNeighborhoodManager
 {
     private BoardwalkNeighborhoodManager(){}
    /**
    * Create a new neighborhood in the boardwalk database.
    * @param connection a jdbc database connection object
    * @param name the neighborhood name
    * @param secure a flag that indicates whether users can
    *   register themselves as members of this Neighborhood
    *	false : any user can add themselves to the neighborhood
    *   true  : users have to ask neighborhood owner/admin to register them
    *           as members of this neighborhood.
    * @param parentId the database id of the parent neighborhood. In case of
    *	root neighborhood, this value should be -1
    * @return a database id of the new neighborhood
    * @exception BoardwalkException if a database access error occurs
    */
    public static int createNeighborhood( BoardwalkConnection connection,
                                            String name,
                                            boolean secure,
                                            int parentId
                                         )
    throws BoardwalkException
    {
        NeighborhoodLevel nhl = null;
        TransactionManager tm = null;
        int nhLevel = 0;

        // fetch the parent neighborhood
        Neighborhood parentNH = null;
        if (parentId > 0)
        {
			try
			{
				parentNH = NeighborhoodManager.getNeighborhoodById(connection.getConnection(), parentId);
			}
			catch (Exception e)
			{
				//bwe
				System.out.println("Parent Neigborhood is not valid");
			}
		}
		if (parentNH != null)
		{
			nhLevel = parentNH.getLevels() + 1;
		}

        try
        {
			// create the child neighborhood
            tm = new TransactionManager(connection.getConnection(), connection.getUserId());
            int tid = tm.startTransaction();
            switch (nhLevel) {
                case 0:
                    nhl = NeighborhoodManagerLevel_0.createNeighborhood(
											connection.getConnection(),
											name,
											tid,
											secure);
                    break;
                case 1:
                    nhl = NeighborhoodManagerLevel_1.createNeighborhood(
											connection.getConnection(),
											name,
											parentId,
											tid,
											secure);
                    break;
                case 2:
                    nhl = NeighborhoodManagerLevel_2.createNeighborhood(
											connection.getConnection(),
											name,
											parentId,
											tid,
											secure);
                    break;
                case 3:
                    nhl = NeighborhoodManagerLevel_3.createNeighborhood(
											connection.getConnection(),
											name,
											parentId,
											tid,
											secure);
                    break;
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

        return nhl.getNhId();
    }
    /**
    * Delete a neighborhood from the boardwalk database
    * @param connection a jdbc database connection object
    * @param nhId the database id of the collaboration
    * @exception BoardwalkException if a database access error occurs
    */
    public static void deleteNeighborhood( BoardwalkConnection connection,
                                           int nhId
                                         )
    throws BoardwalkException
    {
        TransactionManager tm = null;
        try
        {
            tm = new TransactionManager(connection.getConnection(), connection.getUserId());
            int tid = tm.startTransaction();
            NeighborhoodManager.purgeNeighborhood(connection.getConnection(),  nhId);
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
    * Fetch a neighborhood tree from the boardwalk database
    * @param connection a jdbc database connection object
    * @param nhId the database id of the root neighborhood
    * @return a <code>Vector</code> of <code>BoardwalkNeighborhoodNode</code>
    *	objects that are the children of the specified root neighborhood
    * @exception BoardwalkException if a database access error occurs
    */
    public static Vector getNeighborhoodTree(
									BoardwalkConnection connection,
                                    int nhId)
    throws BoardwalkException
    {
        Vector nnl = new Vector();
        try
        {
			Vector nhnList =
            NeighborhoodManager.getNeighborhoodTreeUnderSpecificNeighborhood (
							connection.getConnection(),
							nhId);
			Iterator nhnli = nhnList.iterator();
			while (nhnli.hasNext())
			{
				NHTree nht = (NHTree)nhnli.next();
				BoardwalkNeighborhoodNode bnn = new BoardwalkNeighborhoodNode(nht);
				nnl.addElement(bnn);
			}
        }
        catch (Exception e)
        {
            // bwe
            e.printStackTrace();
        }

        return nnl;
    }

    /**
    * Fetch all the members of given neighborhood
    * @param connection a jdbc database connection object
    * @param nhId the database id of the root neighborhood
    * @return a <code>Vector</code> of <code>BoardwalkMember</code> objects
    * @exception BoardwalkException if a database access error occurs
    */
    public static Vector getMemberList(
									BoardwalkConnection connection,
                                    int nhId)
    throws BoardwalkException
    {
        Vector memberList = new Vector();
        try
        {
			Hashtable members =
            MemberManager.getMembersWithUsernameForNeighborhood(
				connection.getConnection(),
				nhId);
			Vector mList = new Vector(members.keySet());
			Iterator mi = mList.iterator();
			while (mi.hasNext())
			{
				Member m = (Member)mi.next();
				memberList.add(new BoardwalkMember(m));
			}
        }
        catch (Exception e)
        {
            // bwe
            e.printStackTrace();
        }

        return memberList;
    }
    /**
    * Create a new member in the boardwalk database for a specified neighborhood.
    * @param connection a jdbc database connection object
    * @param nhId the database id of the neighborhood
    * @param userId the database id of Boardwalk User to add as member
    * @return a database id of the new Member
    * @exception BoardwalkException if a database access error occurs
    */
    public static int createMember( BoardwalkConnection connection,
									int nhId,
									int userId
								   )
    throws BoardwalkException
    {
        int memberId = -1;
        TransactionManager tm = null;
        try
        {
			// create the child neighborhood
            tm = new TransactionManager(connection.getConnection(), connection.getUserId());
            int tid = tm.startTransaction();
            memberId = MemberManager.createMember(
							connection.getConnection(),
            				tid,
            				userId,
            				nhId);
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

        return memberId;
    }

    /**
    * Delete a  member in the boardwalk database
    * @param connection a jdbc database connection object
    * @param memberId the database id of Boardwalk Member
    * @exception BoardwalkException if a database access error occurs
    */
    public static void deleteMember( BoardwalkConnection connection,
									int memberId
								   )
    throws BoardwalkException
    {
        TransactionManager tm = null;
        try
        {
            tm = new TransactionManager(connection.getConnection(), connection.getUserId());
            int tid = tm.startTransaction();
            MemberManager.deleteMember(connection.getConnection(), memberId);
            tm.commitTransaction();
        }
        catch (Exception e)
        {
            // bwe
			System.out.println("rpv rpv rpv sql exception catched in BoardwalkNeighborhodManager.........");

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
			throw new BoardwalkException(10020);		// added by RahulVaradkar on 20-March-2015
        }
    }

    /**
    * Create relation between the specified relation and list of target neighborhoods
    * @param connection a jdbc database connection object
    * @param nhId the database id of the neighborhood
    * @param relation the name of the relation
    * @param targetNeighborhoods a <code>Vector<code> of <code>Integer</code>
    *	database ids of target neighborhoods
    * @exception BoardwalkException if a database access error occurs
    */
    public static void createRelation( BoardwalkConnection connection,
									int nhId,
									String relation,
									Vector targetNeighborhoods
								   )
    throws BoardwalkException
    {
        TransactionManager tm = null;
        try
        {
            tm = new TransactionManager(connection.getConnection(), connection.getUserId());
            int tid = tm.startTransaction();

            NeighborhoodManager.addNewRelation(
					connection.getConnection(),
					nhId,
					relation,
					targetNeighborhoods,
					tid);
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
    * Delete a relation
    * @param connection a jdbc database connection object
    * @param nhId the database id of the neighborhood
    * @param relation the name of the relation
    * @exception BoardwalkException if a database access error occurs
    */
    public static void deleteRelation( BoardwalkConnection connection,
									int nhId,
									String relation
								   )
    throws BoardwalkException
    {
        TransactionManager tm = null;
        try
        {
            tm = new TransactionManager(connection.getConnection(), connection.getUserId());
            int tid = tm.startTransaction();

            NeighborhoodManager.deleteRelation(
									connection.getConnection(),
									nhId,
									relation ,
									tid);
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


 };