package com.boardwalk.exception;

import java.util.*;
import java.io.*;


public class QuerySyntaxException extends Exception{

	  private String message = null;
      public QuerySyntaxException(String exceptionMessage )
	  {
			message = exceptionMessage;
	  }


	  public java.lang.String getMessage()
	  {
		  return message;
	  }
};


