package servlets;
/*
 *  xlAdminService.java
 */
import com.boardwalk.exception.*;
import java.io.*;
import java.text.*;
import java.util.*;
import java.sql.*;
import java.util.*;
import java.util.regex.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.lang.Runtime;
import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.*;
import java.net.URLEncoder;

//import com.boardwalk.exception.BoardwalkException;
import com.boardwalk.database.*;
import com.boardwalk.database.DatabaseLoader;
import com.boardwalk.user.User;	//
import com.boardwalk.user.UserManager;	//
import com.boardwalk.util.BoardwalkSession;
import com.boardwalk.member.*;
import com.boardwalk.collaboration.*;
import com.boardwalk.whiteboard.*;
import com.boardwalk.table.*;
import com.boardwalk.neighborhood.*;

import boardwalk.connection.*;
import boardwalk.common.BoardwalkUtility;
import boardwalk.table.*;
import boardwalk.neighborhood.*;

import boardwalk.collaboration.*;
import boardwalk.table.BoardwalkTableManager;

import org.apache.commons.codec.binary.Base64;

public class xlPackagingService extends HttpServlet implements SingleThreadModel
{	
    public void service (HttpServletRequest request, HttpServletResponse response) 	throws ServletException, IOException
    {
		xlPackagingServiceLogic logic = new xlPackagingServiceLogic(this);
		logic.service(request, response);
    }
}
