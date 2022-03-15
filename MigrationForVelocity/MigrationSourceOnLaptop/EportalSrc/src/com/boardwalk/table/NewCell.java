package com.boardwalk.table;

import java.util.*;
import java.io.*;
import boardwalk.common.*;

public class NewCell {
    int     m_id;
    int     m_rowid;
    int     m_columnid;
    String  m_colname;
    String  m_stringvalue;

    public NewCell (int a_id, int a_rowid, int a_columnid, String a_colname, String a_stringvalue) {

		m_id			= a_id;
	    m_rowid			= a_rowid;
        m_columnid		= a_columnid;
	    m_colname		= a_colname;
        m_stringvalue	= a_stringvalue;

    }

    public NewCell () {
    }

	public int getId () {
		return m_id;
    }

	public int getRowId () {
		return m_rowid;
    }

	public int getColumnId () {
		return m_columnid;
    }

    public String getColName () {

		if(!BoardwalkUtility.checkIfNullOrBlank(m_colname))
			return m_colname.trim();
		else
			return m_colname;
    }

    public String getStringValue () {

		if(!BoardwalkUtility.checkIfNullOrBlank(m_stringvalue))
			return m_stringvalue.trim();
		else
			return m_stringvalue;
    }

};


