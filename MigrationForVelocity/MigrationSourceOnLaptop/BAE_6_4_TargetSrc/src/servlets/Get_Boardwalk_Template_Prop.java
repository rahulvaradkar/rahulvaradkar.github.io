package servlets;

/* *
	This servlet will check if there is any valid significant update after this particular Tx_id.
*/
import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import com.boardwalk.util.*;
import com.boardwalk.database.*;
import com.boardwalk.table.*;
import boardwalk.table.*;
import com.boardwalk.exception.*;
import boardwalk.connection.*;
import com.boardwalk.query.*;
import boardwalk.common.*;

import java.sql.*;      // JDBC package
import javax.sql.*;     // extended JDBC package
import java.text.*;

public class Get_Boardwalk_Template_Prop extends HttpServlet implements SingleThreadModel
{
	public Get_Boardwalk_Template_Prop()
	{
	}

	public void doPost (HttpServletRequest request,	HttpServletResponse response)   throws ServletException, IOException
	{
		Get_Boardwalk_Template_PropLogic logic = new Get_Boardwalk_Template_PropLogic(this);
		logic.doPost(request, response);
	}

	public void doGet (HttpServletRequest request, HttpServletResponse response)  throws ServletException, IOException
	{
		doPost(request, response);
	}
}

