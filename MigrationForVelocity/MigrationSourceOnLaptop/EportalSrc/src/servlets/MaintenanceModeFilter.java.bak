package servlets;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class MaintenanceModeFilter extends xlService implements Filter
{
	public static final int MODE_NORMAL_OPERATION = 0;
	protected int    mode              = MODE_NORMAL_OPERATION;
	protected String maintenanceUrl    = null;
	protected String maintenanceGuiUrl = null;
	protected String password          = null;
	protected String defaultComment    = null;
	protected String Comment    = null;
	protected String Status    = null;

	public void init(FilterConfig filterConfig) throws ServletException 
	{
		this.maintenanceUrl    = filterConfig.getInitParameter("maintenanceUrl");
		this.maintenanceGuiUrl = filterConfig.getInitParameter("maintenanceGuiUrl");
		this.password          = filterConfig.getInitParameter("password");
		this.Comment           = filterConfig.getInitParameter("Comment");
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
														throws IOException, ServletException 
	{
		HttpServletRequest   httpRequest  = (HttpServletRequest)  request;
		if(request.getParameter("maintenance-mode") != null)
		{
			if(password.equals(request.getParameter("password")))
			{
				mode    = Integer.parseInt(request.getParameter("maintenance-mode"));
				//defaultComment = request.getParameter("aComment");
				//request.getRequestDispatcher(this.maintenanceGuiUrl).include(request, response);
			}
		//return;
		}
		Status = "Normal operation";
		if(mode != MODE_NORMAL_OPERATION) Status = "Down for maintenance";
		defaultComment = "The application is offline for maintenance. Please visit later.";
		if (!this.Comment.equals("")) defaultComment = Comment;
		if(httpRequest.getServletPath().equals(maintenanceGuiUrl))
		{
			request.setAttribute("Status", Status);
			request.getRequestDispatcher(this.maintenanceGuiUrl).include(request, response);
			return;
		}
		else if(mode != MODE_NORMAL_OPERATION)
		{
			//if(!httpRequest.getServletPath().equals(maintenanceGuiUrl))
			//{
				//String ctxname = "";
				String url = "";
				Integer pos = 0;
				String urlxl="";
				// ServletContext servletContext = null;
				// servletContext = httpRequest.getSession().getServletContext();
				//servletContext = httpRequest.
				url = httpRequest.getHeader("User-Agent");
				url = httpRequest.getRequestURI();
				pos = url.indexOf("/",1);
				url = url.substring(pos+1,url.length());
				urlxl= url.substring(0,2);
				if (
						(url.compareToIgnoreCase("checksignificantupdate") == 0) 
							|| (url.compareToIgnoreCase("http_vb_gettableinfo") == 0)
								|| (url.compareToIgnoreCase("httpt_vb_login") == 0) 
									|| (urlxl.compareToIgnoreCase("xl") == 0)
					) 
				{
					defaultComment = "Failure" + Seperator + defaultComment;
					commitResponseBuffer(defaultComment, response);
				}
				request.setAttribute("Comment", defaultComment);
				request.getRequestDispatcher(this.maintenanceUrl).include(request, response);
				return;
			//}
		}
		filterChain.doFilter(request, response);
	}

	public void destroy() 
	{}
}
