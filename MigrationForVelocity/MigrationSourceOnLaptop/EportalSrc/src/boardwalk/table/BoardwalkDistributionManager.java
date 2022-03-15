/*
 * @(#)BoardwalkDistributionManager.java  1.0
 *
 * Copyright 2004 BoardwalkTech, Inc. All rights reserved.
 * BoardwalkTech/CONFIDENTIAL. Use is subject to license terms.
 */

package boardwalk.table;

import java.util.*;
import java.io.*;
import java.sql.*;                  // JDBC package
import javax.sql.*;                 // extended JDBC package

import com.boardwalk.database.*;
import com.boardwalk.exception.*;
import com.boardwalk.table.*;
import com.boardwalk.user.*;
import com.boardwalk.exception.BoardwalkException;

import boardwalk.connection.*;

/**
 * Basic service to manage distribution and delivery of Boardwalk enabled
 * spreadsheet information to participants
 */
 public class BoardwalkDistributionManager
 {
    /**
    * Creates an BoardwalkDistributionPacket with the given Boardwalk tables
    * and a xls template file. The BoardwalkDistributionPacket has methods to
    * extract information specific to the user that the Boardwalk data needs
    * to be delivered using the template
    *
    * @param connection an authenticated BoardwalkConnection object
    * @param template the xls template file containing decorations and
    *    formatting instructions
    * @param tableIdsByExternalName a map of Boardwalk table database ids by
    *    an external name of <code>String</code> type. The external name, when
    *    found in the template is used to place the Boardwalk table at the
    *    location of the name in the spreadsheet
    * @return a <code>BoardwalkDistributionPacket</code> object
    * @exception BoardwalkException if a database access error occurs
    */
    public static BoardwalkDistributionPacket getPacket(
												BoardwalkConnection connection,
												FileInputStream template,
												Hashtable tableIdsByExternalName
                                                )
    throws BoardwalkException
    {
		BoardwalkDistributionPacket dp = new BoardwalkDistributionPacket(template, connection);

		Vector externalNames = new Vector(tableIdsByExternalName.keySet());
		Iterator eni = externalNames.iterator();
		while (eni.hasNext())
		{
			String name = (String)eni.next();
			int tableId = ((Integer)tableIdsByExternalName.get(name)).intValue();

			addTableToPacket(
					connection,
					dp,
					tableId,
					name);
		}

		return dp;
	}

    /**
    * Adds a table to a distribution packet with a display name
    * that is unique in the packet.
    *
    * @param connection an authenticated BoardwalkConnection object
    * @param dp the distribution packet object
    * @param tableId the database id of the boardwalk table
    * @param displayName a <code>String</code> as the display handle to the table
    * @exception BoardwalkException if a database access error occurs
    */

	public static void addTableToPacket(
								BoardwalkConnection connection,
								BoardwalkDistributionPacket dp,
								int tableId,
								String displayName)
	throws BoardwalkException
	{
		int userId = connection.getUserId();
		int memberId = connection.getMemberId();
		int nhid = connection.getNeighborhoodId();
		//System.out.println("Adding table to packet");
		//System.out.println("userId = " + userId);
		//System.out.println("memberId = " + memberId);
		//System.out.println("nhid = " + nhid);
		if (dp.tableIdsByExternalName.get(displayName) == null)
		{
			dp.tableIdsByExternalName.put (displayName, new Integer(tableId));
		}
		else
		{
			//bwe
			System.out.println("The display name is already in use");
			return;
		}
	}
 };