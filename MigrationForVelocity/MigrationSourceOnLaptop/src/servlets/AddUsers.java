package servlets;
/*
 *  This presents a list of collaboration available to a user
 */
import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

import com.boardwalk.database.*;
import com.boardwalk.exception.NeighborhoodException;
import com.boardwalk.exception.*;
import com.boardwalk.collaboration.*;

import com.boardwalk.whiteboard.WhiteboardManager;
import com.boardwalk.user.*;
import com.boardwalk.util.*;
import com.boardwalk.table.*;
import com.boardwalk.wizard.*;


import com.boardwalk.neighborhood.*;
import java.lang.Exception;

import java.sql.*;                  // JDBC package
import javax.sql.*;                 // extended JDBC package

public class AddUsers extends HttpServlet implements SingleThreadModel

{

    public void service (HttpServletRequest request,
	    					HttpServletResponse response)
	throws ServletException, IOException
    {

			AddUsersLogic logic = new AddUsersLogic(this);
			logic.service(request, response);
    }
}
