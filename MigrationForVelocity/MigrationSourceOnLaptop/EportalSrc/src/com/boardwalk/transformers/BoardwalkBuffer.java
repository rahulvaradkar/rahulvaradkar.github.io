package com.boardwalk.transformers;

import com.boardwalk.table.*;
import java.io.*;
import java.util.*;

public class BoardwalkBuffer
{
	public final static String Seperator = new Character((char)1).toString();
	public final static String ContentDelimeter = new Character((char)2).toString();


	public static String  transformTableContents( int userId, int memberId, int nhid, int tableId, String viewPreference, String sequence,  TableContents 	tbcon	)
	{
			StringBuffer m_outputControlbuffer;
			StringBuffer m_outputDatabuffer;


			m_outputControlbuffer = new StringBuffer ();
			m_outputDatabuffer = new StringBuffer ();

			Hashtable m_columns;
			Vector rowv = tbcon.getRowIds();
			Vector rowNames = tbcon.getRowNames();
			Vector columnNames = tbcon.getColumnNames();
			m_columns = tbcon.getColumnsByColumnId();
			Hashtable RowObjsByRowId = tbcon.getRowObjsByRowId ();
			int TransactionId = -1;
			     int maxTransactionId = 0;

			m_outputControlbuffer = m_outputControlbuffer.append(tableId);
			m_outputControlbuffer = m_outputControlbuffer.append(ContentDelimeter);
			m_outputControlbuffer = m_outputControlbuffer.append(viewPreference);
			m_outputControlbuffer = m_outputControlbuffer.append(ContentDelimeter);
			m_outputControlbuffer = m_outputControlbuffer.append(sequence);
			m_outputControlbuffer = m_outputControlbuffer.append(ContentDelimeter);
			m_outputControlbuffer = m_outputControlbuffer.append(memberId);
			m_outputControlbuffer = m_outputControlbuffer.append(ContentDelimeter);
			m_outputControlbuffer = m_outputControlbuffer.append(nhid);
			m_outputControlbuffer = m_outputControlbuffer.append(ContentDelimeter);
			m_outputControlbuffer = m_outputControlbuffer.append(rowNames.size());
			m_outputControlbuffer = m_outputControlbuffer.append(ContentDelimeter);
			m_outputControlbuffer = m_outputControlbuffer.append(columnNames.size());
			m_outputControlbuffer = m_outputControlbuffer.append(ContentDelimeter);

			// Now let us add rows and columns
			if ( columnNames.size() > 0 )
			{

				Vector  columnIds = tbcon.getColumnsSortedBySeqNum();
				Column previousColumn = null;

				for ( int c = 0; c < columnIds.size(); c++ )
				{
					Column col = (Column)columnIds.elementAt(c);

					if ( maxTransactionId  < col.getCreationTid() )
					{
							maxTransactionId = col.getCreationTid();
					}

					if ( maxTransactionId  < col.getAccessTid() )
					{
							maxTransactionId = col.getAccessTid();
					}
					col.print();

					if ( previousColumn == null )
					{

						if ( col.getCreationTid() > TransactionId || (col.getAccessTid() > TransactionId && col.getPrevAccess() != col.getAccess() && col.getPrevAccess() == 0) )
							addColumn( m_outputDatabuffer ,c+1, col.getId(), col.getColumnName(),col.getType(), col.getSequenceNumber(), -1, -1 ,"SI");
						else
							addColumn( m_outputDatabuffer ,c+1, col.getId(), col.getColumnName(),col.getType(), col.getSequenceNumber(), -1, -1,"SU" );
					}
					else
					{
						if ( col.getCreationTid() > TransactionId || (col.getAccessTid() > TransactionId && col.getPrevAccess() != col.getAccess() && col.getPrevAccess() == 0) )
							addColumn( m_outputDatabuffer ,c+1, col.getId(), col.getColumnName(),col.getType(), col.getSequenceNumber(), c, previousColumn.getId() , "SI");
						else
							addColumn( m_outputDatabuffer ,c+1, col.getId(), col.getColumnName(),col.getType(), col.getSequenceNumber(), c, previousColumn.getId() , "SU" );
					}

					previousColumn = col;
				}
			}

			if ( rowNames.size() > 0 )
			{
				int  previousRowId = -1;
				//System.out.println(" Now adding  row buffer " );

				for ( int r = 0; r < rowv.size(); r++ )
				{
					Integer row = (Integer)rowv.elementAt(r);
					Row rowObject =(Row) RowObjsByRowId.get(row);


					if ( maxTransactionId  < rowObject.getCreationTid() )
					{
							maxTransactionId = rowObject.getCreationTid();
					}

					if ( maxTransactionId  < rowObject.getOwnershipAssignedTid() )
					{
							maxTransactionId = rowObject.getOwnershipAssignedTid();
					}

					//rowObject.print();


					//System.out.println(" xl tid " + TransactionId );

					if ( previousRowId == -1 )
					{
							if ( rowObject.getCreationTid() > TransactionId  ||  rowObject.getOwnershipAssignedTid() > TransactionId  )
									addRow ( m_outputDatabuffer, r+1, row.intValue(), r+1, -1, -1 ,"SI");
							else
									addRow ( m_outputDatabuffer, r+1, row.intValue(), r+1, -1, -1 ,"SU");
					}
					else
					{
							if ( rowObject.getCreationTid() > TransactionId ||  rowObject.getOwnershipAssignedTid() > TransactionId )
									addRow ( m_outputDatabuffer, r+1, row.intValue(), r+1, r, previousRowId,"SI");
							else
									addRow ( m_outputDatabuffer, r+1, row.intValue(), r+1, r, previousRowId,"SU");
					}
					previousRowId = row.intValue();
				}


			}

			if ( rowv.size() > 0 )
			{
				for( int i = 0; i < rowv.size(); i++ )
				{
					Integer rowidint = (Integer) rowv.elementAt(i);
					int rowid = rowidint.intValue();
					String rowname = (String)rowNames.elementAt(i);
					Row rowObject =(Row) RowObjsByRowId.get(rowidint);


					Vector cells = (Vector)((Vector)tbcon.getCellsByRowId().get(rowidint)).elementAt(0);

					for( int clIndex = 0; clIndex < cells.size(); clIndex++ )
					{
						VersionedCell c = (VersionedCell)cells.elementAt(clIndex);
						int columnid = c.getColumnId ();
						Column colObject = (Column)m_columns.get(new Integer(columnid));
						String columnname = (String)columnNames.elementAt(clIndex);
						String ctype = c.getType ();
						String cval = c.getValueAsString ();
						String cFormula = c.getFormula();
						c.printCell();
						if ( maxTransactionId  < c.getTransaction().getId() )
						{
							maxTransactionId = c.getTransaction().getId();
						}

						if ( c.getTransaction().getId() > TransactionId  )
						{
							addCell ( m_outputDatabuffer ,rowidint.toString(), (new Integer(columnid)).toString(), columnname, ctype,cval, c);
						}
						else
						if (  rowObject.getOwnerUserId() == userId &&  rowObject.getOwnershipAssignedTid() > TransactionId  )
						{
							 addCell ( m_outputDatabuffer ,rowidint.toString(), (new Integer(columnid)).toString(), columnname, ctype,cval, c);
						}
						else if (colObject.getAccessTid() > TransactionId && colObject.getPrevAccess() != colObject.getAccess() && colObject.getPrevAccess() == 0)
						{
							 addCell ( m_outputDatabuffer ,rowidint.toString(), (new Integer(columnid)).toString(), columnname, ctype,cval, c);
						}
					}
				}
			}


			if (  maxTransactionId == 0  && TransactionId > 0 )
				maxTransactionId = TransactionId;

			m_outputControlbuffer = m_outputControlbuffer.append(maxTransactionId);
			m_outputControlbuffer = m_outputControlbuffer.append(Seperator);
			String returnBuffer = m_outputControlbuffer.toString() + m_outputDatabuffer.toString();
			return returnBuffer;

	}




	public static TableContents	transformToTableContents( int userId, String  		tblData )
	{
			return null;
	}



	public static void addCell (  StringBuffer m_outputDatabuffer, String r, String cl, String cn, String ct, String cv, VersionedCell c )
		{

			m_outputDatabuffer = m_outputDatabuffer.append(r);
			m_outputDatabuffer = m_outputDatabuffer.append(ContentDelimeter);
			m_outputDatabuffer = m_outputDatabuffer.append(cl);
			m_outputDatabuffer = m_outputDatabuffer.append(ContentDelimeter);
			m_outputDatabuffer = m_outputDatabuffer.append(cn);
			m_outputDatabuffer = m_outputDatabuffer.append(ContentDelimeter);
			m_outputDatabuffer = m_outputDatabuffer.append(ct);
			m_outputDatabuffer = m_outputDatabuffer.append(ContentDelimeter);
			m_outputDatabuffer = m_outputDatabuffer.append(cv);
			m_outputDatabuffer = m_outputDatabuffer.append(ContentDelimeter);
			if (c.getFormula() != null)
			{
				m_outputDatabuffer = m_outputDatabuffer.append(c.getFormula());
			}
			else
			{
				m_outputDatabuffer = m_outputDatabuffer.append(" ");
			}
			m_outputDatabuffer = m_outputDatabuffer.append(Seperator);
		}


	public static void addColumn(  StringBuffer m_outputDatabuffer,  int colAddress, int colId, String colName, String colType, float colSequenceNumber, int prvColAddress, int prvColId, String action ) {

		    m_outputDatabuffer = m_outputDatabuffer.append(Integer.toString(colAddress));
			m_outputDatabuffer = m_outputDatabuffer.append(ContentDelimeter);
			m_outputDatabuffer = m_outputDatabuffer.append(Integer.toString(colId));
			m_outputDatabuffer = m_outputDatabuffer.append(ContentDelimeter);
		    m_outputDatabuffer = m_outputDatabuffer.append(colName);
			m_outputDatabuffer = m_outputDatabuffer.append(ContentDelimeter);
		    m_outputDatabuffer = m_outputDatabuffer.append(colType);
			m_outputDatabuffer = m_outputDatabuffer.append(ContentDelimeter);
			m_outputDatabuffer = m_outputDatabuffer.append(Float.toString(colSequenceNumber));
			m_outputDatabuffer = m_outputDatabuffer.append(ContentDelimeter);
		    m_outputDatabuffer = m_outputDatabuffer.append(Integer.toString(prvColAddress));
			m_outputDatabuffer = m_outputDatabuffer.append(ContentDelimeter);
		    m_outputDatabuffer = m_outputDatabuffer.append(Integer.toString(prvColId));
		    m_outputDatabuffer = m_outputDatabuffer.append(ContentDelimeter);
			m_outputDatabuffer = m_outputDatabuffer.append(action);
		    m_outputDatabuffer = m_outputDatabuffer.append(Seperator);

	    }


	public static void addRow (  StringBuffer m_outputDatabuffer,  int rowAddress, int rowId, float rowSequenceNumber, int prvRowAddress, int prvRowId , String action)
	{

		    m_outputDatabuffer = m_outputDatabuffer.append(Integer.toString(rowAddress));
			m_outputDatabuffer = m_outputDatabuffer.append(ContentDelimeter);
			m_outputDatabuffer = m_outputDatabuffer.append(Integer.toString(rowId));
			m_outputDatabuffer = m_outputDatabuffer.append(ContentDelimeter);
			m_outputDatabuffer = m_outputDatabuffer.append(Float.toString(rowSequenceNumber));
			m_outputDatabuffer = m_outputDatabuffer.append(ContentDelimeter);
		    m_outputDatabuffer = m_outputDatabuffer.append(Integer.toString(prvRowAddress));
			m_outputDatabuffer = m_outputDatabuffer.append(ContentDelimeter);
		    m_outputDatabuffer = m_outputDatabuffer.append(Integer.toString(prvRowId));
		    m_outputDatabuffer = m_outputDatabuffer.append(ContentDelimeter);
		    m_outputDatabuffer = m_outputDatabuffer.append(action);
		    m_outputDatabuffer = m_outputDatabuffer.append(Seperator);
	    }








};