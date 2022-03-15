package servlets;
/*
 *  This presents a list of collaboration available to a user
 */
import java.io.*;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;
import java.text.*;


import com.boardwalk.database.DatabaseLoader;
import com.boardwalk.exception.NeighborhoodException;
import com.boardwalk.exception.SystemException;
import com.boardwalk.collaboration.CollaborationManager;
import com.boardwalk.collaboration.Collaboration;
import com.boardwalk.collaboration.CollaborationTreeNode;
import com.boardwalk.whiteboard.WhiteboardManager;
import com.boardwalk.whiteboard.WhiteboardTreeNode;
import com.boardwalk.whiteboard.*;
import com.boardwalk.neighborhood.NeighborhoodManager;
import com.boardwalk.database.*;
import com.boardwalk.table.*;
import com.boardwalk.excel.*;
import com.boardwalk.user.UserManager;
import com.boardwalk.member.Member;
import com.boardwalk.exception.*;
import servlets.MyTables;

import java.util.Date;
import java.sql.*;                  // JDBC package
import javax.sql.*;                 // extended JDBC packa

public class xlGetRowUpdates extends HttpServlet implements SingleThreadModel
{
    public void service (HttpServletRequest request, HttpServletResponse response) 	throws ServletException, IOException
    {
			xlGetRowUpdatesLogic logic = new xlGetRowUpdatesLogic(this);
			logic.service(request, response);
    }
}
