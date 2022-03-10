package boardwalk.connection;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

import servlets.*;

public abstract class ExcelAuthenticationFilterLogic extends xlServiceLogic
{
    protected boolean browserRequest ;

    public ExcelAuthenticationFilterLogic(HttpServlet srv) {
        super(srv);
    }
    
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain)
	throws IOException, ServletException
	{
	    String bw_client = ((HttpServletRequest)request).getHeader("X-client");
		if (bw_client != null)
		{ 
			if(bw_client.equals("MacExcel")||bw_client.equals("WinExcel")) 
				browserRequest = false;
		}
		else
			browserRequest = true;
		
		System.out.println("ExcelAuthenticationFilter URI .." + ((HttpServletRequest)request).getRequestURI());
		/*request on user registration*/
		if(browserRequest){
			// if this is a registration request then send him off to the registration page
			String action = request.getParameter("action");
			// uri check for weblogic deployment
			if (action != null && action.equalsIgnoreCase("register") && !((HttpServletRequest)request).getRequestURI().endsWith(".jsp"))
			{
				System.out.println("Forwarding request to registration page ..");
				request.getServletContext().getRequestDispatcher("/jsp/admin/register.jsp").forward(request, response);
				return;
			}

			// uri check on user registration to bypass filter for weblogic deployment
			if (action != null && ((HttpServletRequest)request).getRequestURI().endsWith(".jsp"))
			{
				chain.doFilter(request, response); 
				return;
			}
			
			if (action != null && action.equalsIgnoreCase("commitUser"))
			{
				chain.doFilter(request, response); 
				return;
			}
	
		}
		
		// forward  request to BWExcelAuthenticationFilter(or customFilter specific to customer)
		authenticate(request, response, chain);
/*		
		HttpSession hs = ((HttpServletRequest)request).getSession(false);
		if (hs != null && hs.getAttribute("userId") != null) 
		{
			System.out.println("Session is active. Forwarding request to service..."+hs.getId());
			chain.doFilter(request, response); //session is active
		}
		else
		{
			System.out.println("Session not active for user. Redirect to Login ");
			authenticate(request, response, chain);
			
		}
*/
    }

	public abstract boolean authenticate(ServletRequest request, ServletResponse response, FilterChain chain);

    public void destroy()
    {

    }
}
