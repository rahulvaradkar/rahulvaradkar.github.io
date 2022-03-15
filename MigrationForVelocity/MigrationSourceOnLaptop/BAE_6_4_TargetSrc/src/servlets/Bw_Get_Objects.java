package servlets;
//import org.apache.log4j.Logger;
/*
 *  This presents a list of collaboration available to a user
 */
import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

import com.boardwalk.database.DatabaseLoader;
import com.boardwalk.database.*;
import com.boardwalk.user.UserManager;
import com.boardwalk.user.User;
import com.boardwalk.member.Member;
import com.boardwalk.exception.*;
import boardwalk.common.*;
import java.sql.*;                  // JDBC package
import javax.sql.*;                 // extended JDBC packa

public class Bw_Get_Objects extends HttpServlet implements
 SingleThreadModel   {

    public void service (HttpServletRequest request,
	    HttpServletResponse response)throws ServletException, IOException
    {
		Bw_Get_ObjectsLogic logic = new Bw_Get_ObjectsLogic(this);
		logic.service(request, response);
    }
}
