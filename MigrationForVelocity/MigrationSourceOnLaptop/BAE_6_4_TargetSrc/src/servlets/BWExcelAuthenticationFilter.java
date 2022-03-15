package servlets;

import java.io.IOException;

import javax.servlet.*;
import javax.servlet.http.*;

public class BWExcelAuthenticationFilter extends HttpServlet implements Filter, SingleThreadModel {

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException
	{
		BWExcelAuthenticationFilterLogic logic = new BWExcelAuthenticationFilterLogic(this);
		logic.doFilter(request, response, chain);
	}
}
