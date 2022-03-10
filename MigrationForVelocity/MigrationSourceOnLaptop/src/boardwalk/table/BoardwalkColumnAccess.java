/*
 * @(#)BoardwalkColumnAccess.java    1.0
 *
 * Copyright 2004 BoardwalkTech, Inc. All rights reserved.
 * BoardwalkTech/CONFIDENTIAL. Use is subject to license terms.
 */

package boardwalk.table;

import java.util.*;
import com.boardwalk.table.*;
/**
 * BoardwalkColumnAccess object contains access information about
 * a column in a Boardwalk Table
 */
 public class BoardwalkColumnAccess
 {
    protected ColumnAccess ca;
    protected boolean columnRestricted;

    private BoardwalkColumnAccess(){}
	/**
	* Constructs a new <code>BoardwalkColumnAccess</code> object that
	* can be used to set column access for a Boardwalk column using
	* BoardwalkTableAccessManager service.
	* By default the access is set so that no access is given for
	* specified column.
	*
	* @param columnId the database id for table
	* @param reln a <code>String</code> for the relation against which to
	*    to set the column access
	*
	public BoardwalkColumnAccess()
	{
		ca = new ColumnAccess(columnId, reln, 0);
	}
	*/

    protected BoardwalkColumnAccess(ColumnAccess a_ca, boolean a_columnRestricted)
    {
        ca = a_ca;
        columnRestricted = a_columnRestricted;
    }

	// get routines
	public boolean canRead()
	{
		return ca.getAccess() >= 1;
	}
	public boolean canWrite()
	{
		return ca.getAccess() == 2;
	}


	// set routines
	public void setReadOnly()
	{
		ca.setAccess(1);
	}
	public void setWriteAccess()
	{
		ca.setAccess(2);
	}
	public void setNoAccess()
	{
		ca.setAccess(0);
	}
 };