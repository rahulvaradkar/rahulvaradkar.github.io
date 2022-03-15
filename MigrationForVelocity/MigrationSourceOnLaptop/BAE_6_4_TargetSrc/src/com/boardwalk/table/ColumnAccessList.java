package com.boardwalk.table;

import java.util.*;
import java.io.*;

public class ColumnAccessList
{
	Vector m_cols;
	Hashtable m_acc;

	public ColumnAccessList ()
	{
		m_cols = new Vector();
		m_acc = new Hashtable();
	}
	public Vector getColumns()
	{
		return m_cols;
	}
	public Hashtable getAccess()
	{
		return m_acc;
	}


};
