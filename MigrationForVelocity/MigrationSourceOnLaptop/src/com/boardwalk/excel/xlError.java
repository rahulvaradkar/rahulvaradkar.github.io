package com.boardwalk.excel;

import java.util.*;
import java.io.*;
import com.boardwalk.util.*;

public class xlError {

    int   m_tableId;
    int    m_rowId;
    int   m_rowAddress;
    int   m_columnId;
	int   m_columnAddress;
	int   m_errorId;
	String m_errorType;
	String  m_errorComment;





    public xlError(
								 	int   a_tableId,
								    int    a_rowId,
								    int   a_rowAddress,
								    int   a_columnId,
									int   a_columnAddress,
									int   a_errorId,
									String a_errorType,
									String   a_errorComment
    						 )
   {
        	m_tableId = a_tableId;
		    m_rowId = a_rowId;
		    m_rowAddress = a_rowAddress;
		    m_columnId = a_columnId;
			m_columnAddress= a_columnAddress;
			m_errorId = a_errorId;
			m_errorType = a_errorType;
			m_errorComment = a_errorComment;

    }

    public String buildTokenString( )
	{


				/*
				Form1.appendTableBuffer rowAddress
				Form1.appendTableBuffer ContentDelimeter
				Form1.appendTableBuffer rowId
				Form1.appendTableBuffer ContentDelimeter
				Form1.appendTableBuffer sequenceNumber
				Form1.appendTableBuffer ContentDelimeter
				Form1.appendTableBuffer previousRowAddress
				Form1.appendTableBuffer ContentDelimeter
				Form1.appendTableBuffer previousRowId
				Form1.appendTableBuffer ContentDelimeter
				Form1.appendTableBuffer action*/


				StringBuffer m_errorbuffer = new StringBuffer ();
				m_errorbuffer.append( m_errorId );
				m_errorbuffer.append( Constants.ContentDelimeter );
				m_errorbuffer.append( m_errorType );
				m_errorbuffer.append( Constants.ContentDelimeter );
				m_errorbuffer.append( m_errorComment );
				m_errorbuffer.append( Constants.ContentDelimeter );
				m_errorbuffer.append( m_tableId );
				m_errorbuffer.append( Constants.ContentDelimeter );
				m_errorbuffer.append( m_rowId );
				m_errorbuffer.append( Constants.ContentDelimeter );
				m_errorbuffer.append( m_rowAddress );
				m_errorbuffer.append( Constants.ContentDelimeter );
				m_errorbuffer.append( m_columnId );
				m_errorbuffer.append( Constants.ContentDelimeter );
				m_errorbuffer.append( m_columnAddress );
				m_errorbuffer.append( Constants.Seperator );
				return m_errorbuffer.toString();

	}
};


