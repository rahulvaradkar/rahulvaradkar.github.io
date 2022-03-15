package servlets;

/*
 * The Whiteboard contains a list of tables to collaborate with
 * It provides methods to create new tables as well as to delete
 * and edit existing tables
 */
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class Whiteboard extends HttpServlet {

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		WhiteboardLogic logic = new WhiteboardLogic(this);
		logic.doPost(request, response);
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}
}
