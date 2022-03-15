/*
 * @(#)BoardwalkAdminAccess.java    1.0
 *
 * Copyright 2004 BoardwalkTech, Inc. All rights reserved.
 * BoardwalkTech/CONFIDENTIAL. Use is subject to license terms.
 */

package boardwalk.table;

import java.util.*;
import com.boardwalk.table.*;
/**
 * BoardwalkAdminAccess object contains details about access for
 * administration of a table and change to it's structure (adding/removing
 * rows and columns)
 */
 public class BoardwalkAdminAccess
 {
    protected TableAccessList tal;

    private BoardwalkAdminAccess(){}

	/**
	* Constructs a new <code>BoardwalkAdminAccess</code> object that
	* can be used to set admin access for a Boardwalk table using
	* BoardwalkTableAccessManager service.
	* By default the access is set so that no access is given for
	* any of the admin actions.
	*
	* @param tableId the database id for table
	* @param reln a <code>String</code> for the relation against which to
	*    to set the admin access
	*
    public BoardwalkAdminAccess(int tableId, String reln)
    {
		tal = new TableAccessList(-1,tableId,reln,0)
	}
	*/
    protected BoardwalkAdminAccess(TableAccessList a_tal)
    {
        tal = a_tal;
    }

	public boolean canAddRow()
	{
		return tal.canAddRow();
	}
	public boolean canDeleteRow()
	{
		return tal.canDeleteRow();
	}
	public boolean canAdministerColumn()
	{
		return tal.canAdministerColumn();
	}
	public boolean canAdministerTable()
	{
		return tal.canAdministerTable();
	}

	//Set routines
	public void flipAddRow()
	{
		 tal.flipAddRowAccess();
	}
	public void flipDeleteRow()
	{
		 tal.flipDeleteRowAccess();
	}
	public void flipAdministerColumn()
	{
		 tal.flipAdministerColumnAccess();
	}
	public void flipAdministerTable()
	{
		 tal.flipAdministerTableAccess();
	}
 };