/*
 * BoardwalkException.java
 *
 * Created on May 1, 2002, 8:15 AM
 */

package com.boardwalk.exception;

/**
 *
 * @author  administrator
 * @version
 */
public class BoardwalkException
	extends java.lang.Exception
{

    /** Creates new BoardwalkException */

    int m_errorCode = -1;
    Exception m_causeException = null;
    BoardwalkMessage m_boardwalkMessage = null;
    String errorMessage =null;

    public BoardwalkException(int a_errorCode, Exception a_causeException)
    {
        m_errorCode = a_errorCode;
        m_causeException = a_causeException;
        BoardwalkMessages bwmsgs = new BoardwalkMessages();
        m_boardwalkMessage = bwmsgs.getBoardwalkMessage(m_errorCode);
        // BoardwalkErrors

    }

	public BoardwalkException(int a_errorCode)
	{
	        m_errorCode = a_errorCode;
	        m_causeException = this;
	        BoardwalkMessages bwmsgs = new BoardwalkMessages();
	        m_boardwalkMessage = bwmsgs.getBoardwalkMessage(m_errorCode);

    }

    public BoardwalkException(int a_errorCode, String a_errorMessage)
	{
		m_errorCode = a_errorCode;
		m_causeException = this;
		BoardwalkMessages bwmsgs = new BoardwalkMessages();
		if ( a_errorMessage == null || a_errorMessage.trim().equals("") )
		{
			m_boardwalkMessage = bwmsgs.getBoardwalkMessage(m_errorCode);
		}
		else
		{
			errorMessage = a_errorMessage;
			System.out.println("setting error message to " + errorMessage);
			if ( a_errorCode > 0 )
			{
				m_boardwalkMessage = bwmsgs.getBoardwalkMessage(m_errorCode);
			}
		}
		// BoardwalkErrors
		System.out.println("setting error message to " + getMessage() );
    }

    public java.lang.String getMessage()
    {
		if ( errorMessage == null  ||  errorMessage.trim().equals("") )
		{
        	return m_boardwalkMessage.getCause();
		}
		else
		{
			return errorMessage;
		}
    }

    public int getSeverity()
    {
        return m_boardwalkMessage.getSeverity();

    }

	public int getErrorCode()
	{
	    return m_errorCode;
    }


    public Exception getCauseException()
    {
        return m_causeException;
    }

    public String   getExceptionCategory()
    {
        return m_boardwalkMessage.getCategory();
    }

    public String   getPotentialSolution()
    {
        return m_boardwalkMessage.getPotentialSolution();
    }

}
