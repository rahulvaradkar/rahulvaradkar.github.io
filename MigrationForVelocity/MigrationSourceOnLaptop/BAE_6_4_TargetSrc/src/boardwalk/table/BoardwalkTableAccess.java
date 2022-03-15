/*
 * @(#)BoardwalkTableAccess.java    1.0
 *
 * Copyright 2004 BoardwalkTech, Inc. All rights reserved.
 * BoardwalkTech/CONFIDENTIAL. Use is subject to license terms.
 */

package boardwalk.table;

import java.util.*;
import com.boardwalk.table.*;
/**
 * BoardwalkRollupAccess object contains details about a access of
 * row consolidation for boardwalk table
 */
 public class BoardwalkTableAccess
 {
    private Hashtable tal;
    private ColumnAccessList cal;
    private int tableId;

    private BoardwalkTableAccess() {}

    protected BoardwalkTableAccess(Hashtable a_tal, ColumnAccessList a_cal, int a_tableId)
    {
		tal = a_tal;
		cal = a_cal;
		tableId = a_tableId;
	}

	public int getTableId()
	{
		return tableId;
	}

	public Vector getRelationList ()
	{
		Vector rvec = new Vector(tal.keySet());
		return rvec;
	}

	public BoardwalkAdminAccess getAdminAccess(String rel)
	{
		BoardwalkAdminAccess baa = null;
		if (tal.get(rel) != null)
		{
			baa = new BoardwalkAdminAccess ((TableAccessList)tal.get(rel));
		}

		return baa;
	}

	public BoardwalkRowAccess getRowAccess(String rel)
	{
		BoardwalkRowAccess bra = null;
		if (tal.get(rel) != null)
		{
			bra = new BoardwalkRowAccess ((TableAccessList)tal.get(rel));
		}

		return bra;
	}

	public BoardwalkColumnAccess getColumnAccess(String rel, int columnId)
	{
		BoardwalkColumnAccess bca = null;
		if (cal.getAccess().get(rel+":"+columnId) != null)
		{
			Integer acc = (Integer)cal.getAccess().get(rel+":"+columnId);
			ColumnAccess ca = new ColumnAccess(columnId, rel, acc.intValue());
			bca = new BoardwalkColumnAccess (ca,true);
		}
		else if (tal.get(rel) != null)
		{
			ColumnAccess ca = new ColumnAccess(columnId, rel, 2);
			bca = new BoardwalkColumnAccess (ca, false);
		}

		return bca;
	}
 };