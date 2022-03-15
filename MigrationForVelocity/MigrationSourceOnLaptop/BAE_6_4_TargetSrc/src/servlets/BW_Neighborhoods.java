package servlets;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;

public class BW_Neighborhoods extends HttpServlet implements SingleThreadModel
{
    public void doPost (HttpServletRequest request,
	    HttpServletResponse response)throws ServletException, IOException
    {
		BW_NeighborhoodsLogic logic = new BW_NeighborhoodsLogic(this);
		logic.doPost(request, response);
    }

    public void doGet (HttpServletRequest request,
	    HttpServletResponse response)throws ServletException, IOException
    {
		doPost(request, response);
    }
}
