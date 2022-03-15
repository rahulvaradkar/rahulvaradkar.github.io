package servlets;
/*
 *  This presents a list of collaboration available to a user
 */
import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import com.boardwalk.exception.*;
import com.boardwalk.database.*;
import java.sql.*;                  // JDBC package
import javax.sql.*;                 // extended JDBC packa
import boardwalk.common.*;
import boardwalk.table.*;
import com.boardwalk.table.*;

public class DisplayDocument extends HttpServlet implements SingleThreadModel
{

    public void doPost (HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
			DisplayDocumentLogic logic = new DisplayDocumentLogic(this);
			logic.doPost(request, response);

	}

	public void doGet (HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException
	{
		doPost(request, response);
  }
}