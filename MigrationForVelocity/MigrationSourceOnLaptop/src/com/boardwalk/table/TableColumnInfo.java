package com.boardwalk.table;

import java.util.*;
import java.io.*;

public class TableColumnInfo {
		int     m_table_id;
		Vector columnsVec;
		Hashtable columnsHash;

	public TableColumnInfo(	int a_table_id,
									Vector a_columnsVec,
									Hashtable a_columnsHash
							 )
   {
		m_table_id        = a_table_id;
		columnsVec  = a_columnsVec;
		columnsHash      = a_columnsHash;
	}


	public int getTableId ()
	{
		return m_table_id;
	}

	public Vector getColumnVector()
	{
		return columnsVec;
	}

	public Hashtable  getColumnHash ()
	{
		return columnsHash;
	}
/*
	public int getMaxTransactionId()
	{
		int maxTid = -1;
		Iterator ci = columnsVec.iterator();
		while (ci.hasNext())
		{
			Column c = (Column)ci.next();
			if ( maxTid  < c.getCreationTid() )
			{
					maxTid = c.getCreationTid();
			}

			if ( maxTid  < c.getAccessTid() )
			{
					maxTid = c.getAccessTid();
			}
		}
		return maxTid;
	}
*/
};


