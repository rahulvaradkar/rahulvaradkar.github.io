package com.boardwalk.query;

import java.util.*;
import java.io.*;
import com.boardwalk.table.*;


public class ColumnConfiguration
{

  Hashtable   m_upRowIds = new Hashtable();
  Hashtable   m_downRowIds = new Hashtable();
  Hashtable   m_noChangeRowIds = new Hashtable();
  Hashtable   m_newRowIds = new Hashtable();
  Hashtable   m_noValueRowIds = new Hashtable();



  Hashtable  m_cellIds;
  Hashtable  m_orderedValueToSequence;

  String m_asOfDate;
  int m_columnId;
  String m_viewPreference;
	boolean m_previousConfiguration = false;


  public ColumnConfiguration
  									(
	  									Hashtable a_cellIds,
	  									Hashtable a_orderedValueToSequence,
	  									TableContents tbcon,
  										int a_columnId,
										String a_viewPreference,
										String a_asOfDate,
										boolean a_previousConfiguration
  									 )


  {

        m_orderedValueToSequence = a_orderedValueToSequence;
        m_cellIds = a_cellIds;
        m_columnId = a_columnId;
        m_viewPreference = a_viewPreference;
        m_asOfDate = a_asOfDate;
        m_previousConfiguration = a_previousConfiguration;

		System.out.println("ColumnConfiguration: " + a_previousConfiguration );
		System.out.println("ColumnConfiguration: a_cellIds" + a_cellIds );




		Vector orderedRowIds = new Vector();
		Vector rowids = tbcon.getRowIds();
		Vector columns = tbcon.getColumnsSortedBySeqNum();
		int colIndex = 0;

		for ( int c = 0; c < columns.size(); c++)
		{
				if ( ((Column)columns.elementAt(c)).getId() == a_columnId )
				{
						colIndex = c;
						break;
				}
		}

		System.out.println("ColumnConfiguration: colIndex =  " + colIndex);

		Hashtable cellsByRowId = tbcon.getCellsByRowId();

		for ( int rowIndex=0; rowIndex < rowids.size(); rowIndex++ )
			{
				Integer a_rowIntegerId = (Integer)rowids.elementAt( rowIndex);
				Vector cells = (Vector) ( (Vector)cellsByRowId.get(a_rowIntegerId)).elementAt(0);
				VersionedCell vc = (VersionedCell)cells.elementAt(colIndex);
				System.out.println("ColumnConfiguration: newValue =  " + vc.getValueAsString());
				if ( m_orderedValueToSequence.containsKey( vc.getValueAsString() ) )
				{
							System.out.println("ColumnConfiguration:  Seq Number for new Value =  " + m_orderedValueToSequence.get(vc.getValueAsString()));
							int currenSequenceId =  ( (Integer)a_orderedValueToSequence.get( vc.getValueAsString() )).intValue();
							VersionedCell  oldValue =   (VersionedCell)a_cellIds.get( new Integer(vc.getId() ) );


							if ( oldValue != null )
							{
											System.out.println("ColumnConfiguration: oldValue =  " + oldValue.getValueAsString());

											Integer seqId = (Integer)a_orderedValueToSequence.get(oldValue.getValueAsString() );

											if (seqId != null)
											{

												int oldSequenceId=  seqId.intValue();

												if ( currenSequenceId < oldSequenceId )
												{
													m_upRowIds.put( a_rowIntegerId,a_rowIntegerId);

												}
												else  if ( currenSequenceId > oldSequenceId )
												{
													m_downRowIds.put( a_rowIntegerId,a_rowIntegerId);

												}
												else
												{
													m_noChangeRowIds.put( a_rowIntegerId,a_rowIntegerId);
												}
										}
										else
										{
												m_noValueRowIds.put( a_rowIntegerId,a_rowIntegerId);
										}
							}
							else
							{
									m_newRowIds.put( a_rowIntegerId,a_rowIntegerId);
							}

				}
				else
				{
					m_noValueRowIds.put( a_rowIntegerId,a_rowIntegerId);
				}

		 }


			orderedRowIds.addAll( m_upRowIds.values() );
			orderedRowIds.addAll( m_downRowIds.values() );
			orderedRowIds.addAll( m_noChangeRowIds.values() );
			orderedRowIds.addAll( m_newRowIds.values() );
			orderedRowIds.addAll( m_noValueRowIds.values() );
			tbcon.setRowIds(orderedRowIds);
  }

  public Hashtable getUpRows()
  {
    return m_upRowIds;
  }

   public Hashtable getNoChangeRows()
    {
      return m_noChangeRowIds;
  }
public Hashtable getNoValueRows()
    {
      return m_noValueRowIds;
  }

public Hashtable getNewRows()
    {
      return m_newRowIds;
  }


  public Hashtable getCellsByCellId()
  {
    return m_cellIds;
  }
  public Hashtable getDownRows()
   {
	   return m_downRowIds;
  }

public Hashtable getOrderedValueToSequence()
 {
   			return m_orderedValueToSequence;
  }

public int getColumnId()
   {
	   return m_columnId;
  }

  public boolean isPreviousConfiguration()
  {
	  	return m_previousConfiguration;

  }

  public String  getConfigurationDate()
     {
  	   return m_asOfDate;
  }

   public String  getViewPreference()
   {
    	return m_viewPreference;
  }
};