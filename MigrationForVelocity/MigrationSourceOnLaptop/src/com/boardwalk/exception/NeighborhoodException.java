// Decompiled by Decafe PRO - Java Decompiler
// Classes: 1   Methods: 4   Fields: 2

package com.boardwalk.exception;

import java.sql.SQLException;
import java.sql.SQLWarning;

public class NeighborhoodException extends Exception
{

    int boardwalkErrorCode;
    String Message;

    public NeighborhoodException(SQLWarning sqlwarning)
    {
        boardwalkErrorCode = 0;
        Message = "";
        boardwalkErrorCode = sqlwarning.getErrorCode();
        Message = sqlwarning.getMessage();
        System.out.println("Error:" + boardwalkErrorCode + " Message :" + Message );

    }

    public int getErrorCode()
    {
        return boardwalkErrorCode;
    }

    public String getErrorMessage()
    {
        return Message;
    }

    public String getPotentialSolution()
    {

        if ( getErrorCode() == 80000 )
        	return "Please try again with a unique Neighborhood name";
        else
        	return "Please try again with a correct Level 0 Neighborhood name";
    }
}
