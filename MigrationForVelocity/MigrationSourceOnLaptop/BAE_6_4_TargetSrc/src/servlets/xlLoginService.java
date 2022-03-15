package servlets;

/*
 * Sarang 06/27/05
 * Manage Boardwalk Collaborations
 * Lakshman 05/30/2016
 * Removed Forgot Password Logic and created a separate Servlet 
 *
 */

import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.*;                  // JDBC package
import javax.sql.*;                 // extended JDBC packa
import javax.mail.*;
import javax.mail.internet.*;
import java.net.*;

import com.boardwalk.neighborhood.*;
import com.boardwalk.exception.*;
import com.boardwalk.database.*;
import com.boardwalk.member.*;
import com.boardwalk.user.*;
import com.boardwalk.util.*;
import boardwalk.common.*;


public class xlLoginService extends HttpServlet
{

    public void service (HttpServletRequest request,
	    					HttpServletResponse response)
		throws ServletException, IOException
    {

			xlLoginServiceLogic logic= new xlLoginServiceLogic(this);
			logic.service(request, response);
    }
}