package com.boardwalk.table;

import java.util.*;
import java.io.*;

public class RangeFormula
{
	int _id;
	int start_col_id;
	int end_col_id;
	int start_row_id;
	int end_row_id;
	int ref_col_id;
	int ref_row_id;
	String formula;

	public RangeFormula (int id, int sc, int ec, int sr, int er, int rc, int rr, String f)
	{
		_id = id;
		start_col_id = sc;
		end_col_id = ec;
		start_row_id = sr;
		end_row_id = er;
		ref_col_id = rc;
		ref_row_id = rr;
		formula= f;
	}

	public int getId()
	{
		return _id;
	}

	public int getStartColumnId()
	{
		return start_col_id;
	}
	public int getEndColumnId()
	{
		return end_col_id;
	}
	public int getStartRowId()
	{
		return start_row_id;
	}
	public int getEndRowId()
	{
		return end_row_id;
	}
	public int getRefColumnId()
	{
		return ref_col_id;
	}
	public int getRefRowId()
	{
		return ref_row_id;
	}
	public String getFormula()
	{
		return formula;
	}


};
