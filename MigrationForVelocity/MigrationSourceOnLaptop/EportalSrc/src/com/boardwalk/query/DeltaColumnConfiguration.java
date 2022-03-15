package com.boardwalk.query;

import java.util.*;
import java.io.*;
import java.text.*;

import com.boardwalk.table.*;


public class DeltaColumnConfiguration
{

  Hashtable   m_upRowIds = new Hashtable();
  Hashtable   m_downRowIds = new Hashtable();
  Hashtable   m_noChangeRowIds = new Hashtable();
  Hashtable   m_newRowIds = new Hashtable();
  Hashtable   m_noValueRowIds = new Hashtable();

  Hashtable  m_cellIds;
  Hashtable  m_difference = new Hashtable();

  String m_asOfDate;
  int m_columnId;
 String m_viewPreference;
 boolean m_previousConfiguration = false;
 boolean processedStringCellType = false;
 boolean isStringDouble = false;
 boolean isStringCurrency = false;
 boolean isStringPerecentage = false;

 boolean ignoreStringColumn = false;

  public DeltaColumnConfiguration() {}
  public DeltaColumnConfiguration
  									(
	  									TableConfiguration prevTbCon,
	  									TableContents tbcon,
  										int a_columnId,
										String a_viewPreference,
										String a_asOfDate,
										boolean a_previousConfiguration
  									 )


  {





        m_cellIds  = prevTbCon.getCellsByCellId();

        m_columnId = a_columnId;
        m_viewPreference = a_viewPreference;
        m_asOfDate = a_asOfDate;
        m_previousConfiguration = a_previousConfiguration;


		Vector orderedRowIds = new Vector();
		Vector rowids = tbcon.getRowIds();
		Vector columns = tbcon.getColumnsSortedBySeqNum();
		int colIndex = 0;
		String colType = "";

		for ( int c = 0; c < columns.size(); c++)
		{
			if ( ((Column)columns.elementAt(c)).getId() == a_columnId )
			{
				colIndex = c;
				colType = ((Column)columns.elementAt(c)).getType();
				break;
			}
		}

		//System.out.println("ColumnConfiguration: colIndex =  " + colIndex);
		Hashtable cellsByRowId = tbcon.getCellsByRowId();

		// special processing to figure out string types
		// they can be simple integers or doubles or currencies

		for ( int rowIndex=0; rowIndex < rowids.size(); rowIndex++ )
		{
			Integer a_rowIntegerId = (Integer)rowids.elementAt( rowIndex);
			Vector cells = (Vector) ( (Vector)cellsByRowId.get(a_rowIntegerId)).elementAt(0);
			VersionedCell vc = (VersionedCell)cells.elementAt(colIndex);
			//System.out.println("ColumnConfiguration: newValue =  " + vc.getValueAsString());
			VersionedCell  oldValue =   (VersionedCell)m_cellIds.get( new Integer(vc.getId()));

			if ( oldValue != null)
			{
				//System.out.println("ColumnConfiguration: oldValue =  " + oldValue.getValueAsString());
				DeltaValue result = null;
				if ( colType.equals("INTEGER") )
				{
					result = compareAsIntegers( oldValue.getIntValue(), vc.getIntValue() );
				}
				else
				if ( colType.equals("FLOAT") )
				{
					result =compareAsDouble( oldValue.getDoubleValue(), vc.getDoubleValue() );
				}
				else
				if ( colType.equals("STRING") )
				{
					if ( ! processedStringCellType )
					{
						processCellWithStringType(  vc.getValueAsString() );
					}

					if  ( processedStringCellType ==true  && ignoreStringColumn == false )
					{
						if  (  isStringDouble )
						{
							result = compareAsDouble( oldValue.getValueAsString(), vc.getValueAsString());
						}
						else if ( isStringCurrency )
						{
							result = compareAsCurrency( oldValue.getValueAsString(), vc.getValueAsString());
						}
						else if ( isStringPerecentage )
						{
							result = compareAsPercentage( oldValue.getValueAsString(), vc.getValueAsString());
						}

					}
					else
					{
						result =  new DeltaValue( -2,"");
					}
				}


				if ( result.result == 0 )
				{
					m_noChangeRowIds.put( a_rowIntegerId,a_rowIntegerId);
					m_difference.put ( new Integer(vc.getId()) , result.difference );
				}
				else if ( result.result == 1 )
				{
					m_upRowIds.put( a_rowIntegerId,a_rowIntegerId);
					m_difference.put ( new Integer(vc.getId()) , result.difference );
				}
				else if ( result.result == -1 )
				{
					m_downRowIds.put( a_rowIntegerId,a_rowIntegerId);
					m_difference.put ( new Integer(vc.getId()) , result.difference );
				}
				else if ( result.result == -2 )
				{
					m_noValueRowIds.put( a_rowIntegerId,a_rowIntegerId);
				}

			}
			else
			{
				m_newRowIds.put( a_rowIntegerId,a_rowIntegerId);
			}

	 	}

		orderedRowIds.addAll( m_upRowIds.values() );
		orderedRowIds.addAll( m_downRowIds.values() );
		orderedRowIds.addAll( m_noChangeRowIds.values() );
		orderedRowIds.addAll( m_newRowIds.values() );
		orderedRowIds.addAll( m_noValueRowIds.values() );
		tbcon.setRowIds(orderedRowIds);

		/*
		Enumeration cellids = m_difference.keys();

		while ( cellids.hasMoreElements() )
		{
			Integer cellIdWithDiff = (Integer)cellids.nextElement();
			String diff = (String)m_difference.get(cellIdWithDiff);
			System.out.println("Diff cellId " +cellIdWithDiff  + " value = " +  diff);
		}
		*/

  }

	public DeltaValue getDiff(VersionedCell vc, VersionedCell oldValue)
	{
		DeltaValue result = null;
		String colType = vc.getType();
		if ( oldValue != null)
		{
			if ( colType.equals("INTEGER") )
			{
				result = compareAsIntegers( oldValue.getIntValue(), vc.getIntValue() );
			}
			else if ( colType.equals("FLOAT") )
			{
				result =compareAsDouble( oldValue.getDoubleValue(), vc.getDoubleValue() );
			}
			else if ( colType.equals("STRING") )
			{
				if  ( processCellWithStringType(vc.getValueAsString()) == true)
				{
					if  (  isStringDouble )
					{
						result = compareAsDouble( oldValue.getValueAsString(), vc.getValueAsString());
					}
					else if ( isStringCurrency )
					{
						result = compareAsCurrency( oldValue.getValueAsString(), vc.getValueAsString());
					}
					else if ( isStringPerecentage )
					{
						result = compareAsPercentage( oldValue.getValueAsString(), vc.getValueAsString());
					}
				}
				else
				{
					result =  new DeltaValue( -2,"");
				}
			}
		}

		return result;
	}




public boolean processCellWithStringType( String value )
{
	boolean ok = true;
	try
	{
		//System.out.println(" Let us see if the string is of double type");
		double doubleValue = Double.parseDouble( value );
		processedStringCellType = true;
		isStringDouble = true;
	}
	catch ( Exception ne )
	{
		//System.out.println(" Let us see if the string is of currency type");
		try
		{
			NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();
			Number defaultNumber = currencyFormat.parse( value );
			double defaultDoubleNumber = defaultNumber.doubleValue();
			processedStringCellType = true;
			isStringCurrency = true;
			//System.out.println(" It is a currency type string "  + value + " = " + defaultDoubleNumber);
		}
		catch( ParseException pe )
		{
				//System.out.println(" Let us see if the string is of percentage type");
				try
				{
					NumberFormat percentFormat = NumberFormat.getPercentInstance();
					Number defaultNumber = percentFormat.parse( value );
					double defaultDoubleNumber = defaultNumber.doubleValue();
					processedStringCellType = true;
					isStringPerecentage = true;
					//System.out.println(" It is a percentage type string "  + value + " = " + defaultDoubleNumber);
				}
				catch( ParseException percentagePe )
				{
						//System.out.println("these strings are not comparable");
						processedStringCellType = true;
						ignoreStringColumn = true;
						ok = false;
				}
		}
	}

	return ok;

}

private DeltaValue compareAsIntegers( int old, int newVal  )
{
	  		if ( old == newVal )
	  		{
				return new DeltaValue( 0,"0");
			}
			else
			if ( old < newVal )
			{
				return new DeltaValue( 1,"+"+(newVal-old), old, newVal);
			}
			else
			{
				return new DeltaValue( -1,"-"+(old-newVal), old, newVal);
			}
  }

  private DeltaValue compareAsDouble( double old, double  newVal  )
    {
  	  		if ( old == newVal )
  	  		{
  				return new DeltaValue( 0,"0");
  			}
  			else
  			if ( old < newVal )
  			{
  				return new DeltaValue( 1,"+"+(newVal-old), old, newVal);
  			}
  			else
  			{
  				return new DeltaValue( -1,"-"+(old-newVal), old, newVal);
  			}
  }

  private DeltaValue compareAsIntegers( String oldValue, String newValue  )
  {
	  try
	  {
	  		int old  =  Integer.parseInt( oldValue );
	  		int intnewVal  =  Integer.parseInt( newValue );
	  		return compareAsIntegers(  old,  intnewVal  );
		}
		catch ( NumberFormatException ne )
		{
			return new DeltaValue( -2,"");
		}
  }


  private DeltaValue compareAsDouble( String oldValue, String newValue  )
    {
  	  try
  	  {
  	  		double dblold  =  Double.parseDouble( oldValue );
  	  		double dblnewVal  =  Double.parseDouble( newValue );
			return compareAsDouble( dblold, dblnewVal );
  		}
  		catch ( NumberFormatException ne )
  		{
  			return new DeltaValue( -2,"");
  		}
  		catch ( Exception e )
  		{
			return new DeltaValue( -2,"");
		}

  }

  private DeltaValue compareAsCurrency( String oldValue, String newValue  )
      {
    	  try
    	  {
			    //System.out.println("Comparing  difference between " + oldValue + " " + newValue  );

			  	NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();
				double old;
				double currencyNewVal;

				try
				{
					Number  oldNumber = currencyFormat.parse( oldValue );
					old = oldNumber.doubleValue();
				}
				catch( Exception e )
				{
					old = Double.parseDouble(	oldValue );
				}


				try
				{
					Number  newNumber = currencyFormat.parse( newValue );
					currencyNewVal = newNumber.doubleValue();
				}
				catch( Exception e )
				{
					currencyNewVal = Double.parseDouble(	newValue );
				}

				//System.out.println("Currency differenc between dbl values " + old + " " + currencyNewVal );

				if ( old == currencyNewVal )
				{
					return new DeltaValue( 0,currencyFormat.format(0));
				}
				else
				if ( old < currencyNewVal )
				{
					return new DeltaValue( 1,"+"+currencyFormat.format(currencyNewVal-old),old,currencyNewVal);
				}
				else
				{
					return new DeltaValue( -1,"-"+currencyFormat.format(old-currencyNewVal),old,currencyNewVal);
  				}

    		}
    		catch ( Exception  ne )
    		{
				NumberFormat curFormat = NumberFormat.getCurrencyInstance();
    			return new DeltaValue( -2,curFormat.format(0));
    		}

  }

  private DeltaValue compareAsPercentage( String oldValue, String newValue  )
        {
      	  try
      	  {
  			    //System.out.println("Comparing  difference between " + oldValue + " " + newValue  );

  			  	NumberFormat percentageFormat = NumberFormat.getPercentInstance();
				double old;
				double percentageNewVal;

				try
				{
					Number  oldNumber = percentageFormat.parse( oldValue );
					old = oldNumber.doubleValue();
				}
				catch( Exception e )
				{
					old = Double.parseDouble(	oldValue )/100;
				}

				try
				{
					Number  newNumber = percentageFormat.parse( newValue );
					percentageNewVal = newNumber.doubleValue();
				}
				catch( Exception e )
				{
					percentageNewVal = Double.parseDouble(	newValue )/100;
				}

  				//System.out.println("Percentage differenc between dbl values " + old + " " + percentageNewVal );

  				if ( old == percentageNewVal )
  				{
  					return new DeltaValue( 0,percentageFormat.format(0));
  				}
  				else
  				if ( old < percentageNewVal )
  				{
  					return new DeltaValue( 1,"+"+percentageFormat.format(percentageNewVal-old),old,percentageNewVal);
  				}
  				else
  				{
					return new DeltaValue( -1,"-"+percentageFormat.format(old-percentageNewVal),old,percentageNewVal);
				}

      		}
      		catch ( Exception ne )
      		{
  				NumberFormat perFormat = NumberFormat.getPercentInstance();
      			return new DeltaValue( -2,perFormat.format(0));
      		}

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

public Hashtable getDifferenceValues()
{
	return m_difference;
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