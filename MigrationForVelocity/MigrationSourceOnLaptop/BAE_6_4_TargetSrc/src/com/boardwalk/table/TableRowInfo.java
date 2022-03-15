package com.boardwalk.table;

import java.util.*;
import java.io.*;

public class TableRowInfo {
		int     m_table_id;
		boolean isActive;
		Vector rowVec;
		Hashtable rowsHash;

	public TableRowInfo( int a_table_id,
						Vector a_rowVec,
						Hashtable a_rowsHash,
						boolean a_isActive
					)
	{
		m_table_id        = a_table_id;
		rowVec  = a_rowVec;
		rowsHash      = a_rowsHash;
		isActive = a_isActive;
	}


	public int getTableId ()
	{
		return m_table_id;
	}

	public boolean areActiveRows ()
	{
		return isActive;
	}
	public Vector getRowVector()
	{
		return rowVec;
	}

	public Hashtable  getRowHash ()
	{
		return rowsHash;
	}
/*
	public int getMaxTransactionId()
	{
		int maxTid = -1;
		Iterator ri = rowVec.iterator();
		while (ri.hasNext())
		{
			Row r = (Row)ri.next();
			if ( maxTid  < r.getCreationTid() )
			{
				maxTransactionId = r.getCreationTid();
			}

			if ( maxTid  < r.getOwnershipAssignedTid() )
			{
				maxTid = r.getOwnershipAssignedTid();
			}
		}

		return maxTid;
	}
*/
};


