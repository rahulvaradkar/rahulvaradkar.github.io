package servlets;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.boardwalk.database.DatabaseLoader;
import com.boardwalk.exception.BoardwalkMessages;
import com.boardwalk.user.User;
import com.boardwalk.user.UserManager;

/**
 * Servlet implementation class xlLogoutService
 */
public class xlLogoutService extends HttpServlet {
      
	public final static String Seperator = new Character((char)1).toString();
	
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		xlServiceLogic logic = new xlServiceLogic(this);
		StringTokenizer st;
        
		BoardwalkMessages bwMsgs = new BoardwalkMessages();

		String buf = logic.getRequestBuffer(request).toString();
		//System.out.println("Recieved Buffer = " + buf);
		st = new StringTokenizer( buf );

		String wrkstr;
		int action;

		// requested action
		wrkstr = st.nextToken (Seperator);
		action = Integer.parseInt(wrkstr);

		System.out.println("user is valid action request = " + action);
		
		if (action == 1)
		{
			logoutUserSession(logic, request, response);
		}
		
	}

	
	public void logoutUserSession(xlServiceLogic logic, HttpServletRequest request,HttpServletResponse response)
	{
		StringBuffer responseToUpdate = new StringBuffer();
		String responseBuffer = null;

		try
		{
			HttpSession hs = request.getSession(true);
			hs.invalidate();
			responseToUpdate.append("Success");
			responseBuffer = responseToUpdate.toString();
			try
			{
				logic.commitResponseBuffer(responseBuffer, response);
			}
			catch( java.io.IOException ioe)
			{
				ioe.printStackTrace();
			}
		}
		catch ( Exception e )
		{
			e.printStackTrace();
		}

	}	
	
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
