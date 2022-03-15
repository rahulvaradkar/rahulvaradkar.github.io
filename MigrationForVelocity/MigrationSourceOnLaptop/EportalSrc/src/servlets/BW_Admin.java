package servlets;

/*
 * The administration "home page" for Boardwalk users. In this
 * section of the UI the users will be able to create and manage
 * Neighborhoods, users and memberships.
 */
import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import com.boardwalk.util.*;

public class BW_Admin extends BWServlet
{
//    BoardwalkDB bwdb;
    ServletContext sc;
    HttpServletRequest req;
    HttpServletResponse res;

    public void doPost (HttpServletRequest request,
	    HttpServletResponse response)throws ServletException, IOException
    {
        if (authenticate(request, response) == false)
        	return;

		// get the session and go to login if necessary
		sc = getServletContext();
		req = request;
		res = response;

		// check the action requested
		String action = request.getParameter ("action");

		// no action means go to the admin main page
		if (action == null) {
			adminMain();
		}
		else if (action.equals("nhMain")) {
			nhMain();
		}
		else if (action.equals("userMain")) {
			userMain();
		}
    }

    public void adminMain() throws ServletException, IOException
    {
	// forward the request
	sc.getRequestDispatcher("/admin/admin_main.html").forward(req,res);
    }

    public void nhMain() throws ServletException, IOException
    {
	// forward the request
	sc.getRequestDispatcher("/BW_Neighborhoods").forward(req,res);
    }

    public void userMain() throws ServletException, IOException
    {
	// forward the request
	sc.getRequestDispatcher("/BW_Users").forward(req,res);
    }

    // Handle the get methods here, forward the request and response to
    // the post method to handle
    public void doGet (HttpServletRequest request,
	    HttpServletResponse response)throws ServletException, IOException

    {
	doPost(request, response);
    }
}
