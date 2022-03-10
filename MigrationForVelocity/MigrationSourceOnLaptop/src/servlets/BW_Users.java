package servlets;

import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

import com.boardwalk.database.*;
import com.boardwalk.user.*;
import com.boardwalk.exception.*;

import java.sql.*;                  // JDBC package
import javax.sql.*;                 // extended JDBC package

import com.boardwalk.util.*;
import com.boardwalk.member.*;


public class BW_Users extends HttpServlet implements SingleThreadModel
{
    public void doPost (HttpServletRequest request,
	    HttpServletResponse response)throws ServletException, IOException
    {
		BW_UsersLogic logic = new BW_UsersLogic(this);
		logic.doPost(request, response);
    }

    // Handle the get methods here, forward the request and response to
    // the post method to handle
    public void doGet (HttpServletRequest request,
	    HttpServletResponse response)throws ServletException, IOException
    {
		doPost(request, response);
    }
}
