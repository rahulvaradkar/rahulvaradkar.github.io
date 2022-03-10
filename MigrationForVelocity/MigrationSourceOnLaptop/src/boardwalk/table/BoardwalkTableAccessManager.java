/*
 * @(#)BoardwalkTableAccessManager.java    1.0
 *
 * Copyright 2004 BoardwalkTech, Inc. All rights reserved.
 * BoardwalkTech/CONFIDENTIAL. Use is subject to license terms.
 */
package boardwalk.table;

import java.sql.*;
import java.util.*;
import com.boardwalk.table.*;
import com.boardwalk.exception.BoardwalkException;
import com.boardwalk.database.*;
import com.boardwalk.neighborhood.*;
import boardwalk.connection.*;
/**
 * Service to get and set the access on the table.
 * <p>
 * In Boardwalk, the access control is given on relations. A relation
 * defines how other neighborhoods, and therefore the members of those
 * neighborhoods relate to you/your neighborhood. In programming terms,
 * relation is a <code>String</code> object that maps your neighborhood
 * with a list of neighborhoods. </p>
 *<p> Some relations come out of the box:
 *<br>CREATOR : This is a special relation that identifies the creator of table
 *<br>PRIVATE : The relation that identifies the members of your own neighborhood
 *<br>DOMAIN  : The relation that identifies all members of your heirarchy
 *<br>CHILDREN: The relation that identifies all members of your children neighborhoods
 *<br>PUBLIC  : This is special relation that identifies every Boardwalk member in the system
 *<p>
 * Custom relations can also be created using the <code>BoardwalkNeighborhoodManager</code>
 *</p>
 *<p>
 * The access control that is defined on these relations is of three type: Admin, Row and Column
 * based access control. Each of these types can be independently controlled.
 *</p>
 *<p>
 * The following code demonstrates how to get a table access and then modify it for
 * the "PUBLIC" relation.
 * <p>
 *<blockquote><pre>
*    // get the access
*    BoardwalkTableAccess bta = BoardwalkTableAccessManager.getTableAccess(bwcon,tableId);
*    BoardwalkAdminAccess baa = bta.getAdminAccess("PUBLIC");
*    baa.flipAddRow();
*    baa.flipDeleteRow();
*    baa.flipAdministerColumn();
*    baa.flipAdministerTable();
*    BoardwalkTableAccessManager.setAdminAccess(bwcon, baa);
*
*    BoardwalkRowAccess bra = bta.getRowAccess("PUBLIC");
*    bra.flipReadAllRows();
*    bra.flipWriteMyRows();
*    bra.flipWriteAllRows();
*    bra.flipReadMyNeighborhoodRows();
*    bra.flipWriteMyNeighborhoodRows();
*    bra.flipReadMyNeighborhoodImmediateChildrenRows();
*    bra.flipWriteMyNeighborhoodImmediateChildrenRows();
*    bra.flipReadMyNeighborhoodHeirarchyRows();
*    bra.flipWriteMyNeighborhoodHeirarchyRows();
*    BoardwalkTableAccessManager.setRowAccess(bwcon, bra);
*
*    try{
*    	BoardwalkTableContents btc = BoardwalkTableManager.getTableContents(bwcon, bta.getTableId());
*    	Vector cols = btc.getColumns();
*    	Iterator colsi = cols.iterator();
*    	while (colsi.hasNext())
*    	{
*    		BoardwalkColumn c = (BoardwalkColumn)colsi.next();
*    		BoardwalkColumnAccess bca = bta.getColumnAccess("PUBLIC", c.getId());
*    		if (bca.canRead())
*    			bca.setNoAccess();
*    		else
*    			bca.setWriteAccess();
*    		BoardwalkTableAccessManager.setColumnAccess(bwcon, bca);
*    	}
*    }catch(Exception e){
*    	e.printStackTrace();
*    }
 * </pre></blockquote>
 * </p>
 */
 public class BoardwalkTableAccessManager
 {

    /**
    * Fetch the access control for a given Boardwalk table.
    * @param connection a jdbc database connection object
    * @param tableId the database id of the table
    * @return <code>BoardwalkTableAccess</code> object
    * @exception BoardwalkException if a database access error occurs
    */
	public static BoardwalkTableAccess getTableAccess(
										BoardwalkConnection connection,
										int tableId)
	throws BoardwalkException
	{
		BoardwalkTableAccess bta = null;

		try
		{
			Hashtable taht = TableManager.getTableAccess(
												connection.getConnection(),
											 	tableId);
			ColumnAccessList cal = ColumnManager.getColumnAccess(
												connection.getConnection(),
											 	tableId);
			bta = new BoardwalkTableAccess(taht, cal, tableId);

		}
		catch (Exception e)
		{
			//bwe
			e.printStackTrace();
		}


		return bta;
	}
    /**
    * Set admin access to the table
    * @param connection a jdbc database connection object
    * @param adminAccess the admin access object retrieved from the
    *  <code>BoardwalkTableAccess</code> object.
    * @exception BoardwalkException if a database access error occurs
    */
	public static void setAdminAccess(
										BoardwalkConnection connection,
										BoardwalkAdminAccess adminAccess)
	throws BoardwalkException
	{
		TransactionManager tm = null;
		try
		{
			tm = new TransactionManager(connection.getConnection(), connection.getUserId());
			int tid = tm.startTransaction();

			TableManager.setTableAccess(
										connection.getConnection(),
										adminAccess.tal.getTableId(),
										adminAccess.tal,
										tid
										);
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
    * Set row access to the table
    * @param connection a jdbc database connection object
    * @param rowAccess the row access object retrieved from the
    *  <code>BoardwalkTableAccess</code> object.
    * @exception BoardwalkException if a database access error occurs
    */
	public static void setRowAccess(
							BoardwalkConnection connection,
							BoardwalkRowAccess rowAccess)
	throws BoardwalkException
	{
		TransactionManager tm = null;
		try
		{
			tm = new TransactionManager(connection.getConnection(), connection.getUserId());
			int tid = tm.startTransaction();

			TableManager.setTableAccess(
										connection.getConnection(),
										rowAccess.tal.getTableId(),
										rowAccess.tal,
										tid
										);
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
    * Set column access to the table
    * @param connection a jdbc database connection object
    * @param columnAccess the column access object retrieved from the
    *  <code>BoardwalkTableAccess</code> object.
    * @exception BoardwalkException if a database access error occurs
    */
	public static void setColumnAccess(
							BoardwalkConnection connection,
							BoardwalkColumnAccess columnAccess)
	throws BoardwalkException
	{
		TransactionManager tm = null;
		try
		{
			tm = new TransactionManager(connection.getConnection(), connection.getUserId());
			int tid = tm.startTransaction();
			if (columnAccess.columnRestricted == false)
			{
				ColumnManager.addNewColumnAccess(
								connection.getConnection(),
								columnAccess.ca.getColumnId(),
								tid);
			}
			ColumnManager.updateColumnAccess(
								connection.getConnection(),
								columnAccess.ca.getColumnId(),
								columnAccess.ca.getRelationship(),
								columnAccess.ca.getAccess(),
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