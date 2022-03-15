/*
 * @(#)BoardwalkRowAccess.java    1.0
 *
 * Copyright 2004 BoardwalkTech, Inc. All rights reserved.
 * BoardwalkTech/CONFIDENTIAL. Use is subject to license terms.
 */

package boardwalk.table;

import java.util.*;
import com.boardwalk.table.*;
/**
 * BoardwalkRowAccess object contains details about a access of
 * row consolidation for boardwalk table
 */
 public class BoardwalkRowAccess
 {
    protected TableAccessList tal;

    private BoardwalkRowAccess(){}
	/**
	* Constructs a new <code>BoardwalkRowAccess</code> object that
	* can be used to set row access for a Boardwalk table using
	* BoardwalkTableAccessManager service.
	* By default the access is set so that no access is given for
	* any of the row based views.
	*
	* @param tableId the database id for table
	* @param reln a <code>String</code> for the relation against which to
	*    to set the row access
	*
    public BoardwalkRowAccess(a_tableId, a_rel)
    {
		tal = new TableAccessList(-1,a_tableId,rel,0)
	}
	*/
    protected BoardwalkRowAccess(TableAccessList a_tal)
    {
        tal = a_tal;
    }

/*
	// Survey Mode
	public boolean canWriteSurvey()
	{
		return tal.canReadWriteOnMyLatestView();
	}
	public boolean canConsolidateSurvey()
	{
		return tal.canReadLatestViewOfAll();
	}
	public boolean canConsolidateChildrenSurvey()
	{
		return tal.canReadLatestViewOfAllChildren();
	}
*/
  	// Row Consolidation Mode
	public boolean canReadAllRows()
	{
		return tal.canReadLatestOfTable();
	}
	public boolean canWriteMyRows()
	{
		return tal.canReadWriteLatestOfMyRows();
	}
	public boolean canWriteAllRows()
	{
		return tal.canWriteLatestOfTable();
	}
	public boolean canReadMyNeighborhoodRows()
	{
		return tal.canReadLatestofMyGroup();
	}
	public boolean canWriteMyNeighborhoodRows()
	{
		return tal.canReadWriteLatestofMyGroup();
	}
	public boolean canReadMyNeighborhoodImmediateChildrenRows()
	{
		return tal.canReadLatestofMyGroupAndImmediateChildren();
	}
	public boolean canWriteMyNeighborhoodImmediateChildrenRows()
	{
		return tal.canReadWriteLatestofMyGroupAndImmediateChildren();
	}
	public boolean canReadMyNeighborhoodHeirarchyRows()
	{
		return tal.canReadLatestofMyGroupAndAllChildren();
	}
	public boolean canWriteMyNeighborhoodHeirarchyRows()
	{
		return tal.canReadWriteLatestofMyGroupAndAllChildren();
	}
   	////////////////////////////////////////////////////////
   	// Setting access///////////////////////////////////////
   	////////////////////////////////////////////////////////

/*
	// Survey Mode
	public void flipWriteSurvey()
	{
		 tal.flipReadWriteOnMyLatestViewAccess();
	}
	public void flipConsolidateSurvey()
	{
		 tal.flipReadLatestViewOfAllAccess();
	}
	public void flipConsolidateChildrenSurvey()
	{
		 tal.flipReadLatestViewOfAllChildrenAccess();
	}
*/
  	// Row Consolidation Mode
	public void flipReadAllRows()
	{
		 tal.flipReadLatestOfTableAccess();
	}
	public void flipWriteMyRows()
	{
		 tal.flipReadWriteLatestOfMyRowsAccess();
	}
	public void flipWriteAllRows()
	{
		 tal.flipWriteLatestOfTableAccess();
	}
	public void flipReadMyNeighborhoodRows()
	{
		 tal.flipReadLatestofMyGroupAccess();
	}
	public void flipWriteMyNeighborhoodRows()
	{
		 tal.flipReadWriteLatestofMyGroupAccess();
	}
	public void flipReadMyNeighborhoodImmediateChildrenRows()
	{
		 tal.flipReadLatestofMyGroupAndImmediateChildrenAccess();
	}
	public void flipWriteMyNeighborhoodImmediateChildrenRows()
	{
		 tal.flipReadWriteLatestofMyGroupAndImmediateChildrenAccess();
	}
	public void flipReadMyNeighborhoodHeirarchyRows()
	{
		 tal.flipReadLatestofMyGroupAndAllChildrenAccess();
	}
	public void flipWriteMyNeighborhoodHeirarchyRows()
	{
		 tal.flipReadWriteLatestofMyGroupAndAllChildrenAccess();
	}
 };