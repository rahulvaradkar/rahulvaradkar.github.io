package servlets;
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

import java.sql.*;                  // JDBC package
import javax.sql.*;                 // extended JDBC packa

public class httpt_vb_Login extends HttpServlet implements SingleThreadModel {

    public void service (HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException
    {
		httpt_vb_LoginLogic logic = new httpt_vb_LoginLogic(this);
		logic.service(request, response);
    }
}
