package servlets;

import java.io.IOException;

/*
 *  Link:Export from excel - sarang
 */
import javax.servlet.*;
import javax.servlet.http.*;

public class xlLinkExportService extends HttpServlet implements SingleThreadModel
{
	public void service(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException
	{
        xlLinkExportServiceLogic logic = new xlLinkExportServiceLogic(this);
        logic.service(request, response);
	}
}
