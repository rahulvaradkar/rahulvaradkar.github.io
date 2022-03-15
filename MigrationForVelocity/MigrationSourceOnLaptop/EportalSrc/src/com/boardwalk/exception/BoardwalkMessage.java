/*
 * BoardwalkMessage.java
 *
 * Created on May 1, 2002, 8:29 AM
 */

package com.boardwalk.exception;

/**
 *
 * @author  administrator
 * @version 
 */
public class BoardwalkMessage {

    /** Creates new BoardwalkMessage */
    int m_errorCode = -1;
    String m_category;
    int m_severity;
    String m_cause;
    String m_potentialSolution;
    
    public BoardwalkMessage(int a_errorCode, String a_category, int a_severity ,String a_cause, String a_potentialSolution ) 
    {
        m_errorCode = a_errorCode;
        m_category = a_category;
        m_severity = a_severity;
        m_cause = a_cause;
        m_potentialSolution = a_potentialSolution;
    }
    
    public int getErrorCode()
    {
        return m_errorCode;
    }
    
    public String getCategory()
    {
        return m_category;
    }
    
    public int getSeverity()
    {
        
        return m_severity;
    }
    
    
    public String getCause()
    {
        return m_cause;
    }
    
    public String getPotentialSolution()
    {
        
        return m_potentialSolution;
    }
};
