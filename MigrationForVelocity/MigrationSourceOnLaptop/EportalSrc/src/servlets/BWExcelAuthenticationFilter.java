package servlets;

import java.io.*;
import java.util.*;
import java.sql.*;                  // JDBC package
import javax.sql.*;                 // extended JDBC package

import com.boardwalk.database.*;
import com.boardwalk.user.UserManager;
import com.boardwalk.user.User;
import boardwalk.connection.ExcelAuthenticationFilter;
import javax.servlet.*;
import javax.servlet.http.*;

public class BWExcelAuthenticationFilter
extends ExcelAuthenticationFilter
{
	public boolean authenticate(ServletRequest request)
	{
		boolean ok = false;
		int userId = -1;
		System.out.println("Trying Boardwalk authentication");
		String userName = (String)request.getAttribute("authFilter.username");
		System.out.println("userName = " + userName);
		String userPassword = (String)request.getAttribute("authFilter.password");
		System.out.println("userPassword = " + userPassword);

		// do the authentication stuff here
		Connection connection = null;
		try
		{
			DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
			connection = databaseloader.getConnection();
			userId = UserManager.authenticateUser(connection, userName, userPassword);
			if (userId > 0)
            {
				System.out.println("BWExcelAuthenticationFilter::authenticate() : User is valid");
				ok = true;
			}
			else
			{
				System.out.println("BWExcelAuthenticationFilter::authenticate() : User is not valid");
			}
		}
		catch ( Exception e )
		{
		   e.printStackTrace();
		}
		finally
		{
		  try
		  {
			  if ( connection != null )
					connection.close();
		  }
		  catch ( SQLException sql )
		  {
			sql.printStackTrace();
		  }
		}

		return ok;
	}
}
