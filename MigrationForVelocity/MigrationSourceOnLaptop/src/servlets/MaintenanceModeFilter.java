package servlets;
import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class MaintenanceModeFilter extends HttpServlet implements Filter
{
	public static final int MODE_NORMAL_OPERATION = 0;
	protected int    mode              = MODE_NORMAL_OPERATION;
	protected String maintenanceUrl    = null;
	protected String maintenanceGuiUrl = null;
	protected String password          = null;
	protected String Comment    = null;

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
		MaintenanceModeFilterLogic logic = new MaintenanceModeFilterLogic(this);
		logic.doFilter(request, response, filterChain);
	}

	public void destroy() 
	{}
}
