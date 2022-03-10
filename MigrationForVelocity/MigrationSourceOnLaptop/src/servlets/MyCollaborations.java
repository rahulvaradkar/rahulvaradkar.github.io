package servlets;
/*
 *  This presents a list of collaboration available to a user.
 */
import java.io.*;
import java.util.*;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import javax.servlet.*;
import javax.servlet.http.*;

import com.boardwalk.database.*;
import com.boardwalk.exception.NeighborhoodException;
import com.boardwalk.exception.*;
import com.boardwalk.collaboration.*;

import com.boardwalk.whiteboard.WhiteboardManager;
import com.boardwalk.user.*;


import com.boardwalk.neighborhood.*;
import java.lang.Exception;

import java.sql.*;                  // JDBC package
import javax.sql.*;                 // extended JDBC package
import com.boardwalk.util.*;


public class MyCollaborations extends HttpServlet
{

    public void doPost(HttpServletRequest request,
    HttpServletResponse response)throws ServletException, IOException {

        MyCollaborationsLogic logic = new MyCollaborationsLogic(this);
        logic.doPost(request, response);
    }

    public void doGet(HttpServletRequest request,
    HttpServletResponse response)throws ServletException, IOException
    {
        doPost(request, response);
    }
}
