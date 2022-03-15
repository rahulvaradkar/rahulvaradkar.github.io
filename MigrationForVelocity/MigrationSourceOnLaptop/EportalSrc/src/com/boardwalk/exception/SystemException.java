// Decompiled by Decafe PRO - Java Decompiler
// Classes: 1   Methods: 5   Fields: 1

package com.boardwalk.exception;


public class SystemException extends Exception
{

    Exception systemException;

    public SystemException(Exception exception)
    {
        systemException = null;
        systemException = exception;
    }

    public String toString()
    {
        return systemException.toString();
    }

    public String getErrorMessage()
    {
        return systemException.getMessage();
    }

    public String getPotentialSolution()
    {
        return "Please review the underlying System Message and rectify the problem";
    }



    public void printStackTrace()
    {
        systemException.printStackTrace();
    }
}
