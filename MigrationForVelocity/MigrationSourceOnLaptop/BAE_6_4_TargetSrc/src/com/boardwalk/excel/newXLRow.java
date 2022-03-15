package com.boardwalk.excel;

import java.util.*;
import java.io.*;
import com.boardwalk.util.*;

public class newXLRow {
    int m_previousRowId;
	int m_offset;
	int m_idx;


	public newXLRow(
					int a_previousRowId,
					int a_offset,
					int a_idx
				 )
	{
		m_previousRowId    = a_previousRowId;
		m_offset = a_offset;
		m_idx = a_idx;
	}

   public int getPreviousRowId()
   {
   		return m_previousRowId;
   }

	public int getOffset()
	{
		return m_offset;
	}

	public int getIndex()
	{
		return m_idx;
	}

};


