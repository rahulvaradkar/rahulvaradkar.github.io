package servlets;
/*
 *  This presents a list of collaboration available to a user
 */
import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import com.boardwalk.table.*;
import com.boardwalk.excel.*;
import com.boardwalk.exception.*;
import com.boardwalk.database.*;
import java.sql.*;                  // JDBC package
import javax.sql.*;                 // extended JDBC packa
import boardwalk.common.*;
import com.boardwalk.member.Member;
import com.boardwalk.user.UserManager;


public class xlNamedSelectionService extends HttpServlet implements SingleThreadModel
{
	public void service(HttpServletRequest request,
		HttpServletResponse response)throws ServletException, IOException
	{
		xlNamedSelectionServiceLogic logic = new xlNamedSelectionServiceLogic(this);
		logic.service(request, response);
	}
}

