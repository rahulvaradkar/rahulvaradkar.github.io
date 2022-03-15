package servlets;

/*
 * Sarang 03/03/06
 * Upload Documents from Excel
 *
 */

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class fileUploadService extends HttpServlet
implements SingleThreadModel
{

    public void service (HttpServletRequest req,
                            HttpServletResponse res)
    throws ServletException, IOException
    {
		fileUploadServiceLogic fusl = new fileUploadServiceLogic(this);
		fusl.service(req, res);
    }
}
