package com.boardwalk.table;

import java.util.*;
import java.io.*;

public class ColumnAccess
{
	int col_id;
	String rel;
	int access;

	public ColumnAccess (int cid, String r, int acc)
	{
		col_id = cid;
		rel = r;
		access = acc;
	}
	public int getColumnId()
	{
		return col_id;
	}
	public String getRelationship()
	{
		return rel;
	}
	public int getAccess()
	{
		return access;
	}

	public void setAccess (int a_access)
	{
		access = a_access;
	}

};


