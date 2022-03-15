package com.boardwalk.excel;

import java.util.*;
import java.io.*;
import com.boardwalk.util.*;

public class xlErrorNew {

	int	m_tableId;
    int m_rowId;
    int m_rowAddress;
    int m_columnId;
	int m_columnAddress;
	int m_errorId;

	public xlErrorNew(int   a_tableId, int    a_rowId, int   a_columnId, int   a_errorId )
	{
        	m_tableId	= a_tableId;
		    m_rowId		= a_rowId;
		    m_columnId	= a_columnId;
			m_errorId	= a_errorId;
    }

    public String buildTokenString( )
	{
		StringBuffer m_errorbuffer = new StringBuffer ();
		m_errorbuffer.append( m_errorId );
		m_errorbuffer.append( Constants.Seperator );
		m_errorbuffer.append( m_tableId );
		m_errorbuffer.append( Constants.Seperator );
		m_errorbuffer.append( m_rowId );
		m_errorbuffer.append( Constants.Seperator );
		m_errorbuffer.append( m_columnId );
		m_errorbuffer.append( Constants.ContentDelimeter );
		return m_errorbuffer.toString();
	}
};


