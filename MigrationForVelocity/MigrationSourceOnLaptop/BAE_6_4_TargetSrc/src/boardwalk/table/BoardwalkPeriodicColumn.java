package boardwalk.table;

import java.util.*;

public class BoardwalkPeriodicColumn
{
	String lsColumnName;
	int	liColumnId;
	String lsDefaultAccess;
	String lsCopyValue;
	String lsCopyFormula;
	String lsDefaultValue;
	String lsDefaultFormula;
	String lsDeleteValue;
	String lsDeleteFormula;
	String lsRenameValue;
	String lsPrevColName;
	int ColOrder	= -1;

	int liAccess = -1;

	public BoardwalkPeriodicColumn (String asColName, int aiAccess)
	{
		lsColumnName		= asColName;
		liAccess			= aiAccess;
	}

	public BoardwalkPeriodicColumn (int aiColId, String asColName, String asPrevColName, int aiColOrder)
	{
		liColumnId			= aiColId;
		lsColumnName		= asColName;
		lsDefaultAccess		= "";
		lsPrevColName		= asPrevColName;
		ColOrder			= aiColOrder;
	}

	public BoardwalkPeriodicColumn (String asColName, int aiColId, String asDefAccess, String asCopyValue, String asCopyFormula, String asDefValue, String asDefFormula, String asDelValue, String asDelFormula, String asRenameVal, String asPrevColName)
	{
		lsColumnName		= asColName;
		liColumnId			= aiColId;
		lsDefaultAccess		= asDefAccess;
		lsCopyValue			= asCopyValue;
		lsCopyFormula		= asCopyFormula;
		lsDefaultValue		= asDefValue;
		lsDefaultFormula	= asDefFormula;
		lsDeleteValue		= asDelValue;
		lsDeleteFormula		= asDelFormula;
		lsRenameValue		= asRenameVal;
		lsPrevColName		= asPrevColName;
	}

	public String getName()
	{
		return lsColumnName;
	}

	public int getColumnId()
	{
		return liColumnId;
	}

	public int getColOrder()
	{
		return ColOrder;
	}

	public void setColOrder(int aiColOrder)
	{
		ColOrder = aiColOrder;
	}

	public void setPrevColumnName(String asPrevColumnName)
	{
		lsPrevColName = asPrevColumnName;
	}

	public String getPrevColumnName()
	{
		return lsPrevColName;
	}

	public int getAccess()
	{
		int liRetValue = -1;
		if(lsDefaultAccess.equals("R"))
			liRetValue = 1;
		else if(lsDefaultAccess.equals("W"))
			liRetValue = 2;
		else if(lsDefaultAccess.equals("N"))
			liRetValue = 0;
		return liRetValue;
	}

	public void setAccess(String asDefAccess)
	{
		lsDefaultAccess = asDefAccess.trim();
	}

	public String getCopyColName()
	{
		return lsCopyValue;
	}

	public void setCopyColName(String asColName)
	{
		lsCopyValue = asColName;
	}

	public String getCopyFormulaColName()
	{
		return lsCopyFormula;
	}

	public void setCopyFormulaColName(String asForColName)
	{
		lsCopyFormula = asForColName;
	}

	public String getDefaultValue()
	{
		return lsDefaultValue;
	}

	public void setDefaultValue(String asDefValue)
	{
		lsDefaultValue = asDefValue;
	}

	public String getDefaultFormula()
	{
		return lsDefaultFormula;
	}

	public void setDefaultFormula(String asDefFormula)
	{
		lsDefaultFormula = asDefFormula;
	}

	public String getDeleteValue()
	{
		return lsDeleteValue;
	}

	public void setDeleteValue(String asDeleteVal)
	{
		lsDeleteValue = asDeleteVal;
	}

	public String getDeleteFormulaValue()
	{
		return lsDeleteFormula;
	}

	public void setDeleteFormulaValue(String asDeleteFormulaValue)
	{
		lsDeleteFormula = asDeleteFormulaValue;
	}

	public String getRenameValue()
	{
		return lsRenameValue;
	}

	public void setRenameValue(String asRenameValue)
	{
		lsRenameValue = asRenameValue;
	}

	public boolean equals(Object obj)
	{
		 if (!(obj instanceof BoardwalkPeriodicColumn)) return false;
		 if (((BoardwalkPeriodicColumn)obj).getName().equals(lsColumnName))
		 {
			 return true;
		 }
		 else
		 {
			 return false;
		 }
	}
}