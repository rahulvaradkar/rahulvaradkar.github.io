/*
 * @(#)InputRowColumnCell.java	1.0
 *
 * Copyright 2004 BoardwalkTech, Inc. All rights reserved.
 * BoardwalkTech/CONFIDENTIAL. Use is subject to license terms.
 */

package boardwalk.table;

/**
 * InputRowColumnCell object is used as input to create/update a
 * Boardwalk cell database object from the <code>BoardwalkTableManager</code>
 * service
 */
 public class InputRowColumnCell
 {
    protected int    m_rowId;
    protected int     m_columnId;
    protected String  m_type;
    protected String  m_value;
    protected int	  m_intValue;
    protected double  m_dblValue;
    protected String  m_formula;

	private InputRowColumnCell(){}
	/**
	* Public constructor for RowColumnCell object
	* @param rowId the database id for Boardwalk row
	* @param columnId the database id for Boardwalk column
	* @param cellType currently support "STRING"
	* @param cellValue the value to be put in the cell
	* @param formula the formula in the Boardwalk neutral format
	* @exception BoardwalkException if a database access error occurs
	*/
	public InputRowColumnCell( int rowId,
							   int columnId,
							   String cellType,
							   String cellValue,
							   String formula
							  )
	{
        m_rowId = rowId;
        m_columnId = columnId;
        m_type = cellType;
        m_value = cellValue;
        setValueAsString(cellValue);
        m_formula = formula;
	}

    private void setValueAsString(String a_valueAsString)
    {
		if (m_type.equals("STRING"))
		{
			if ( a_valueAsString != null )
				m_value = a_valueAsString.trim();
			else
				m_value = "";
		}
		else if (m_type.equals("INTEGER"))
		{
			m_intValue = Integer.parseInt(a_valueAsString);
		}
		else if (m_type.equals("FLOAT"))
		{
			m_dblValue = Double.parseDouble(a_valueAsString);
		}
    }

    public void setType(String cellType)
    {
		m_type = cellType;
	}
    public void setValue(String value)
    {
		m_value = value;
	}
    public void setFormula(String formula)
    {
		m_formula = formula;
	}
 };