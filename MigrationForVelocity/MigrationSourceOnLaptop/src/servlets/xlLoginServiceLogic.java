package servlets;

/*
 * Sarang 06/27/05
 * Manage Boardwalk Collaborations
 * Lakshman 05/30/2016
 * Removed Forgot Password Logic and created a separate Servlet 
 *
 */

import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.*; // JDBC package
import javax.sql.*; // extended JDBC packa
import javax.mail.*;
import javax.mail.internet.*;
import java.net.*;

import com.boardwalk.neighborhood.*;
import com.boardwalk.exception.*;
import com.boardwalk.database.*;
import com.boardwalk.member.*;
import com.boardwalk.user.*;
import com.boardwalk.util.*;
import boardwalk.common.*;

public class xlLoginServiceLogic extends xlServiceLogic {

	public final static String Seperator = new Character((char) 1).toString();
	public final static String ContentDelimeter = new Character((char) 2).toString();
	StringTokenizer st;
	int tid;
	String msSmtpServer;
	String msSmtpPort;
	String msUserName;
	String msPassword;

	public xlLoginServiceLogic(xlLoginService srv) {
		super(srv);
	}

	public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		BoardwalkMessages bwMsgs = new BoardwalkMessages();

		String buf = getRequestBuffer(request).toString();
		// System.out.println("Recieved Buffer = " + buf);
		st = new StringTokenizer(buf);
		msSmtpServer = getServletConfig().getInitParameter("smptserver");
		msSmtpPort = getServletConfig().getInitParameter("smtpport");
		msUserName = getServletConfig().getInitParameter("username");
		msPassword = getServletConfig().getInitParameter("password");

		String wrkstr;
		int action;
		// requested action
		wrkstr = st.nextToken(Seperator);
		action = Integer.parseInt(wrkstr);
		System.out.println("user is valid action request = " + action);
		if (action == 1) {
			createUserInNH(response);
		}
		if (action == 2) {
			IsUserActive(response);
		}
		if (action == 4) {
			getNhCount(response);
		}
		if (action == 5) {
			changePassword(response);
		}
	}

	public void createUserInNH(HttpServletResponse response) {
		StringBuffer responseToUpdate = new StringBuffer();
		String responseBuffer = null;
		String wrkstr;

		String username;
		String password;
		String fname;
		String lname;
		String nhName;

		wrkstr = st.nextToken(Seperator);
		username = wrkstr;

		wrkstr = st.nextToken(Seperator);
		password = wrkstr;

		wrkstr = st.nextToken(Seperator);
		fname = wrkstr;

		wrkstr = st.nextToken(Seperator);
		lname = wrkstr;

		wrkstr = st.nextToken(Seperator);
		nhName = wrkstr;

		Connection connection = null;
		int tid = -1;
		TransactionManager tm = null;

		try {
			DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
			connection = databaseloader.getConnection();
			tm = new TransactionManager(connection, 1);
			tid = tm.startTransaction();
			UserManager.createUserInNh(connection, fname, lname, username, password, nhName, tid);
			tm.commitTransaction();
			responseToUpdate.append("Success");
			responseBuffer = responseToUpdate.toString();
			try {
				commitResponseBuffer(responseBuffer, response);
			} catch (java.io.IOException ioe) {
				ioe.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
			try {
				tm.rollbackTransaction();
			} catch (SQLException sqlfatal) {
				sqlfatal.printStackTrace();
			}

			if (e.getClass().getName().equals("com.boardwalk.exception.BoardwalkException")) {
				BoardwalkException bwe = (BoardwalkException) e;
				responseToUpdate.append("Failure");
				responseToUpdate.append(Seperator);
				responseToUpdate.append(bwe.getErrorCode());
				responseToUpdate.append(ContentDelimeter);
				responseToUpdate.append(bwe.getMessage());
				responseToUpdate.append(ContentDelimeter);
				responseToUpdate.append(bwe.getPotentialSolution());
				responseToUpdate.append(ContentDelimeter);
				responseToUpdate.append(Seperator);
				responseToUpdate.append(Seperator);
				responseBuffer = responseToUpdate.toString();
				try {
					commitResponseBuffer(responseBuffer, response);
				} catch (java.io.IOException ioe) {
					ioe.printStackTrace();
				}
				return;
			}
		} finally {
			try {
				connection.close();
			} catch (SQLException sql) {
				sql.printStackTrace();
			}

		}
	}

	public void IsUserActive(HttpServletResponse response) {
		StringBuffer responseToUpdate = new StringBuffer();
		String responseBuffer = null;
		String wrkstr;
		String username;
		wrkstr = st.nextToken(Seperator);
		username = wrkstr;
		Connection connection = null;

		try {
			DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
			connection = databaseloader.getConnection();
			User nu = UserManager.getUser(connection, username);
			responseToUpdate.append("Success");
			responseToUpdate.append(Seperator);
			responseToUpdate.append(nu.getId());
			responseBuffer = responseToUpdate.toString();
			try {
				commitResponseBuffer(responseBuffer, response);
			} catch (java.io.IOException ioe) {
				ioe.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				connection.close();
			} catch (SQLException sql) {
				sql.printStackTrace();
			}
		}
	}

	public void changePassword(HttpServletResponse response) {

		StringTokenizer st2;
		StringBuffer responseToUpdate = new StringBuffer();
		String responseBuffer = null;
		String wrkstr;

		String username;
		String oldPassword;
		String newPassword;

		wrkstr = st.nextToken(Seperator);
		username = wrkstr;
		wrkstr = st.nextToken(Seperator);
		oldPassword = wrkstr;
		wrkstr = st.nextToken(Seperator);
		newPassword = wrkstr;

		int m_user_id = -1;
		Connection connection = null;
		int tid = -1;
		TransactionManager tm = null;

		try {
			DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
			connection = databaseloader.getConnection();
			tm = new TransactionManager(connection, 1);
			tid = tm.startTransaction();
			User nu = UserManager.getUser(connection, username);
			m_user_id = nu.getId();

			UserManager.updatePassword(connection, m_user_id, oldPassword, newPassword);
			tm.commitTransaction();

			responseToUpdate.append("Success" + Seperator + Seperator);
			responseBuffer = responseToUpdate.toString();
			try {
				commitResponseBuffer(responseBuffer, response);
			} catch (java.io.IOException ioe) {
				ioe.printStackTrace();
			}
		}

		catch (Exception e) {
			try {
				tm.rollbackTransaction();
			} catch (SQLException sqlfatal) {
				sqlfatal.printStackTrace();
			}

			if (e.getClass().getName().equals("com.boardwalk.exception.BoardwalkException")) {
				BoardwalkException bwe = (BoardwalkException) e;
				responseToUpdate.append("Failure");
				responseToUpdate.append(Seperator);
				responseToUpdate.append(bwe.getErrorCode());
				responseToUpdate.append(ContentDelimeter);
				responseToUpdate.append(bwe.getMessage());
				responseToUpdate.append(ContentDelimeter);
				responseToUpdate.append(bwe.getPotentialSolution());
				responseToUpdate.append(ContentDelimeter);
				responseToUpdate.append(Seperator);
				responseToUpdate.append(Seperator);
				responseBuffer = responseToUpdate.toString();
				try {
					commitResponseBuffer(responseBuffer, response);
				} catch (java.io.IOException ioe) {
					ioe.printStackTrace();
				}
				return;
			}
		} finally {
			try {
				connection.close();
			} catch (SQLException sql) {
				sql.printStackTrace();
			}

		}
	}

	public void getNhCount(HttpServletResponse response) {
		Connection connection = null;
		StringBuffer responseToUpdate = new StringBuffer();
		String responseBuffer = null;
		try {
			DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
			connection = databaseloader.getConnection();

			Vector nhTree = NeighborhoodManager.getNeighborhoodTree(connection, 1);
			responseToUpdate.append("Success");

			// Traverse the tree to create a buffer.
			int nhCount = 0;
			Iterator nhIter = nhTree.iterator();
			while (nhIter.hasNext()) {
				nhCount++;
				NHTree nht = (NHTree) nhIter.next();
				Neighborhood nh0 = nht.getNeighborhood();
				Vector nh1Tree = nht.getChildren();

				if (!nh1Tree.isEmpty()) {

					Iterator nh1Iter = nh1Tree.iterator();
					while (nh1Iter.hasNext()) {
						nhCount++;
						NHTree nh1t = (NHTree) nh1Iter.next();
						Neighborhood nh1 = nh1t.getNeighborhood();
						Vector nh2Tree = nh1t.getChildren();

						if (!nh2Tree.isEmpty()) {
							Iterator nh2Iter = nh2Tree.iterator();
							while (nh2Iter.hasNext()) {
								nhCount++;
								NHTree nh2t = (NHTree) nh2Iter.next();
								Neighborhood nh2 = nh2t.getNeighborhood();
								Vector nh3Tree = nh2t.getChildren();

								if (!nh3Tree.isEmpty()) {
									Iterator nh3Iter = nh3Tree.iterator();
									while (nh3Iter.hasNext()) {
										nhCount++;
										NHTree nh3t = (NHTree) nh3Iter.next();
										Neighborhood nh3 = nh3t.getNeighborhood();
									}
								} // if
							} // while
						} // if
					} // while
				} // if
			} // while

			responseToUpdate.append(Seperator + nhCount + Seperator + Seperator);
			responseBuffer = responseToUpdate.toString();

			try {
				commitResponseBuffer(responseBuffer, response);
			} catch (java.io.IOException ioe) {
				ioe.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();

			if (e.getClass().getName().equals("com.boardwalk.exception.BoardwalkException")) {
				BoardwalkException bwe = (BoardwalkException) e;
				responseToUpdate.append("Failure");
				responseToUpdate.append(Seperator);
				responseToUpdate.append(bwe.getErrorCode());
				responseToUpdate.append(ContentDelimeter);
				responseToUpdate.append(bwe.getMessage());
				responseToUpdate.append(ContentDelimeter);
				responseToUpdate.append(bwe.getPotentialSolution());
				responseToUpdate.append(ContentDelimeter);
				responseToUpdate.append(Seperator);
				responseToUpdate.append(Seperator);
				responseBuffer = responseToUpdate.toString();
				try {
					commitResponseBuffer(responseBuffer, response);
				} catch (java.io.IOException ioe) {
					ioe.printStackTrace();
				}
				return;
			}
		} finally {
			try {
				connection.close();
			} catch (SQLException sql) {
				sql.printStackTrace();
			}

		}
	}

}