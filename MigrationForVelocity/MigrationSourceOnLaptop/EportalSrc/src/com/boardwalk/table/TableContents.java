package com.boardwalk.table;

import java.util.*;
import java.io.*;


public class TableContents
{

  Vector    m_columnNames;
  Hashtable m_cellsByRowid; // ordered by row id and vector of cells ordered by column sequence
  Vector    m_rowIds;       // ordered by sequence number and this ordered set must be used to access the hashtable
  Vector    m_rowNames;
  Hashtable m_columnsByColumnId; //acesss by column_id
  Vector    m_columnsSortedBySeqNum; //acesss by column_id
  Hashtable m_RowObjsByRowId;


  TableAccessList m_tbACL;

  String m_sourceType; // Describe the Table Content Source Type....could be a single table or multiple tables or whitebaord(s) or collaboration(s)

  public TableContents (
						Vector a_rowIds,
						Vector a_rowNames,
						Vector a_columnNames,
						Hashtable a_VectorsOfCellsByRowid, // Rowid--> Vector(s) --> Vector of Cells Ordered by Columns
						Hashtable a_columnsByColumnId, // columns by colId
						String a_sourceType,
						TableAccessList a_tbACL,
						Vector a_columnsSortedBySeqNum,
						Hashtable a_RowObjsByRowId
					 )


  {
        m_rowIds        = a_rowIds;
        m_rowNames      = a_rowNames;
        m_columnNames   = a_columnNames;
        m_cellsByRowid  = a_VectorsOfCellsByRowid;
        m_columnsByColumnId = a_columnsByColumnId;
		m_sourceType = a_sourceType;
		m_tbACL = a_tbACL;
		m_columnsSortedBySeqNum = a_columnsSortedBySeqNum;
		m_RowObjsByRowId = a_RowObjsByRowId;
  }

	public Vector getRowIds()
	{
		return m_rowIds;
	}

	public void  setRowIds(Vector rowIds)
	{
		m_rowIds = rowIds;
	}

	public Vector getRowNames()
	{
		return m_rowNames;
	}

	public Vector getColumnNames()
	{
		return m_columnNames;
	}

	public Hashtable getCellsByRowId()
	{
		return m_cellsByRowid;
	}

	public Hashtable getRowObjsByRowId ()
	{
		return m_RowObjsByRowId;
	}

	public Hashtable getColumnsByColumnId()
	{
		return m_columnsByColumnId;
	}

	public Vector  getColumnsSortedBySeqNum()
	{
		return m_columnsSortedBySeqNum;
	}

	public String getSourceType()
	{
		return m_sourceType;
	}

	public TableAccessList getTableAccessList()
	{
		return m_tbACL;
	}

};