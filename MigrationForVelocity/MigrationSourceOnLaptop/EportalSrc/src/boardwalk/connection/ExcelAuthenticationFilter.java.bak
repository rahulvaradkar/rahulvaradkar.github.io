package boardwalk.connection;

import com.boardwalk.exception.*;

import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import servlets.*;

public abstract class ExcelAuthenticationFilter extends xlService
implements Filter, SingleThreadModel
{
    private final static String Seperator = new Character((char)1).toString();
    private final static String ContentDelimeter = new Character((char)2).toString();
    private FilterConfig filterConfig = null;

    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain)
	throws IOException, ServletException
	{
	    int userId;
		String userName;
		String userPassword;
		int memberId;
		int nhId;
		String nhName;

		boolean browserRequest = true;
		// Collect the username and password from the form
		userName = null;
		userPassword = null;
		userName = request.getParameter("username");
		userPassword = request.getParameter("password");
		if (userName == null ) // The request is coming from Excel
		{
			System.out.println("Authenticating Excel Request to Boardwalk");


			String line = getRequestBuffer(request).toString();
			
			System.out.println("line = " + line);

			String wrkstr;
			StringTokenizer st = new StringTokenizer(line);
			wrkstr = st.nextToken(Seperator);
			userName = wrkstr;
			wrkstr = st.nextToken(Seperator);
			userPassword = wrkstr;
	
			System.out.println("userName = " + userName);
			System.out.println("userPassword = " + userPassword);

			browserRequest = false;
		}
		else
		{
			// if this is a registration request then send him off to the registration page
			String action = request.getParameter("action");
			if (action != null && action.equalsIgnoreCase("register"))
			{
				System.out.println("Forwarding request to registration page");
				filterConfig.getServletContext().getRequestDispatcher("/jsp/admin/register.jsp").forward(request, response);
				return;
			}
			System.out.println("Authenticating browser request to Boardwalk");
		}

		request.setAttribute("authFilter.username", userName);
		request.setAttribute("authFilter.password", userPassword);

		if (authenticate(request) == true)
		{
			// pass on the request for processing
			request.setAttribute("userName", userName);
			request.setAttribute("userPassword", userPassword);
      		chain.doFilter(request, response);
		}
		else
		{
			if (browserRequest == false)
			{
				// block the request
				String invalid = new String("failure");

				commitResponseBuffer(invalid, response);
			}
			else
			{
				request.setAttribute("com.boardwalk.exception.BoardwalkException", new BoardwalkException(11004));
				filterConfig.getServletContext().getRequestDispatcher("/jsp/admin/login.jsp").forward(request, response);
			}
		}
    }

	public abstract boolean authenticate(ServletRequest request);

    public void destroy()
    {

    }

    public void init(FilterConfig filterConfig)
    {
		this.filterConfig = filterConfig;
    }

}

