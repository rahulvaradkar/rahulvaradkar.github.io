package servlets;
/*
 * The Whiteboard contains a list of tables to collaborate with
 * It provides methods to create new tables as well as to delete
 * and edit existing tables
 */
import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.net.URLEncoder;

import com.boardwalk.database.DatabaseLoader;
import com.boardwalk.exception.*;
import com.boardwalk.collaboration.CollaborationManager;
import com.boardwalk.whiteboard.WhiteboardManager;
import com.boardwalk.table.*;
import boardwalk.table.*;
import com.boardwalk.database.*;
import com.boardwalk.user.UserManager;
import com.boardwalk.query.*;
import com.boardwalk.neighborhood.NeighborhoodManager;
import com.boardwalk.util.*;

import org.apache.commons.fileupload.*;
import org.apache.commons.fileupload.disk.*;
import org.apache.commons.fileupload.servlet.*;

//import org.apache.commons.io.output.*;
import javax.activation.MimetypesFileTypeMap;

import java.sql.*;                  // JDBC package
import javax.sql.*;                 // extended JDBC package

public class MyTables  extends HttpServlet implements SingleThreadModel
{
    ServletContext sc;
    HttpServletRequest req;
    HttpServletResponse res;
    String tableName;
    int	tableId;
    Hashtable rows;
    String userEmailAddress;

    public void doPost (HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
			MyTablesLogic logic = new MyTablesLogic(this);
			logic.doPost(request, response);
	}

    public void doGet (HttpServletRequest request,
	    HttpServletResponse response)throws ServletException, IOException

    {
			doPost(request, response);
    }



}
