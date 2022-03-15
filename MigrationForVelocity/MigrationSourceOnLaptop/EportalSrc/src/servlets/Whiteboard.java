package servlets;
/*
 * The Whiteboard contains a list of tables to collaborate with
 * It provides methods to create new tables as well as to delete
 * and edit existing tables
 */
import java.io.*;
import java.util.*;
import java.util.zip.*;
import javax.servlet.*;
import javax.servlet.http.*;

import com.boardwalk.database.*;
import com.boardwalk.exception.*;
import com.boardwalk.exception.SystemException;
import com.boardwalk.table.*;
import com.boardwalk.whiteboard.*;
import com.boardwalk.collaboration.*;
import com.boardwalk.util.*;
import com.boardwalk.neighborhood.*;


import java.sql.*;                  // JDBC package
import javax.sql.*;                 // extended JDBC package

public class Whiteboard extends BWServlet {
    //    BoardwalkDB bwdb ;
    ServletContext sc;
    HttpServletRequest req;
    HttpServletResponse res;
    int wbid = -1;
    int userId = -1;
    int  memberid = -1;

    public void doPost(HttpServletRequest request,
    HttpServletResponse response)throws ServletException, IOException {
	if (authenticate(request, response) == false)
		return;

		// get the user id for the session
		HttpSession hs = request.getSession(true);
		userId = ((Integer)hs.getAttribute("userId")).intValue();
		memberid = ((Integer)hs.getAttribute("memberId")).intValue();

		// get the session and go to login if necessary
		sc = getServletContext();
		req = request;
		res = response;

		 BoardwalkSession bws = 	(BoardwalkSession)hs.getAttribute("bwSession");
		 req.setAttribute("bwSession",bws);


		Enumeration names = req.getParameterNames();
		while ( names.hasMoreElements()) {
			String name = (String)names.nextElement();
			System.out.println(" Param Name " + name );
			System.out.println(" Param value " + req.getParameter(name) );
		}

		String action = req.getParameter("action");

		System.out.println(" Whiteboard servlet action param " + action );


		if ( action == null || action.trim().equals("") )
		{
			String redirectURL = "/MyCollaborations";
			response.sendRedirect(request.getContextPath() + redirectURL);
		}

		if ( action.equals("openWhiteboardBaseline") ) {
			openWhiteboardBaseline();
			return;
		}
		else if (action.equals("createBaseline")) {
			createBaseline();
		}
		else if (action.equals("addNewWhiteboard")){
			addNewWhiteboard();
		}
		else if (action.equals("commitWhiteboard")){
			commitWhiteboard();
		}
		else if (action.equals("delete")) {
			deleteWhiteboard();
		}
		 else if (action.equalsIgnoreCase("switchCurrentMembership"))
			  {
							switchCurrentMembership(req,res);
		}
		/*
		else if (action.equalsIgnoreCase("bwsFormat"))
		{
						bwsFormat();
		}
		*/
		else {
			editWhiteboard();
		}
    }

    public void deleteWhiteboard()
    throws ServletException, IOException
    {
        wbid = Integer.parseInt(req.getParameter("wbid"));
        int   collabId   = Integer.parseInt(req.getParameter("collabId"));
        Connection connection = null;
        int tid = -1;
        TransactionManager tm = null;

        try
        {
            DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
            connection = databaseloader.getConnection();
            tm = new TransactionManager( connection, userId);
            tid = tm.startTransaction();
            WhiteboardManager.purgeWhiteboard(connection, wbid);
            tm.commitTransaction();
        }
        catch (Exception e) {
            req.setAttribute("com.boardwalk.exception.BoardwalkException",
                            new BoardwalkException(1003, e));
            try {
                tm.rollbackTransaction();
            }
            catch( SQLException sqlfatal ) {
                sqlfatal.printStackTrace();
            }
        }
        finally
        {
            try {
                connection.close();
            }
            catch ( SQLException sql ) {
                sql.printStackTrace();
            }
        }

        String redirectURL = "/MyCollaborations?collabId="+collabId+"&action=editCollab";
		res.sendRedirect(req.getContextPath() + redirectURL);




    }

    public void addNewWhiteboard()
    throws ServletException, IOException {
        int   collabId   = Integer.parseInt(req.getParameter("collabId"));
        req.setAttribute("collabId", new Integer(collabId) );
        // forward the request to the jsp page
        sc.getRequestDispatcher("/jsp/collaboration/create_whiteboard.jsp").
        forward(req,res);
    }

    public void commitWhiteboard()
    throws ServletException, IOException {
        String wbName = req.getParameter("wbName");
        int   collabId   = Integer.parseInt(req.getParameter("collabId"));

        Connection connection = null;
        int tid = -1;
        TransactionManager tm = null;

        try {
            DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
            connection = databaseloader.getConnection();
            tm = new TransactionManager( connection, userId);
            tid = tm.startTransaction();
            int m_wb_id = WhiteboardManager.createWhiteboard(
                                                    connection,
                                                    wbName,
                                                    2, // peer access
                                                    2, // private access
                                                    2, // friend access
                                                    collabId, // collaboration id
                                                    tid, // transcation id
                                                    1 // status
                                                    );
            tm.commitTransaction();
        }
        catch ( Exception e )
		  {
			  		System.out.println("Got exception ********************************************");
		            e.printStackTrace();

		            try
		            {
		                tm.rollbackTransaction();
		            }
		            catch( SQLException sqlfatal ) {
		                sqlfatal.printStackTrace();
		            }

		            if ( e.getClass().getName().equals("com.boardwalk.exception.BoardwalkException") )
		            {
		                BoardwalkException bwe = (BoardwalkException)e;
		                System.out.println(" Boardwalk Message " + bwe.getMessage());
		                req.setAttribute("BoardwalkException", bwe);
		               addNewWhiteboard();

		            }
        }

        finally {
            try {
                connection.close();
            }
            catch ( SQLException sql ) {
                sql.printStackTrace();
            }
        }


        String redirectURL = "/MyCollaborations?collabId="+collabId+"&action=editCollab";
		res.sendRedirect(req.getContextPath() + redirectURL);



    }

    public void createBaseline()
    throws ServletException, IOException {

        int   collabId   = Integer.parseInt(req.getParameter("collabId"));
        req.setAttribute("collabId", new Integer(collabId) );
        // forward the request to the jsp page
        sc.getRequestDispatcher("/jsp/collaboration/create_baseline.jsp").
        forward(req,res);

    }

    public void editWhiteboard() throws ServletException, IOException {

        Vector tableList = null;
        WhiteboardInfo wbInfo = null;
        Connection connection = null;
        // set the current
        wbid = Integer.parseInt(req.getParameter("wbid"));

		HttpSession hs = req.getSession(true);

		int userId = ((Integer)hs.getAttribute("userId")).intValue();

		Integer memberidParam = (Integer)hs.getAttribute("memberId");

		int memberId = -1;

		if ( memberidParam != null )
		{
			memberId = memberidParam.intValue();
		 }


        // get the tables in the database
        try {
            DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
            connection = databaseloader.getConnection();
            tableList = TableManager.getTablesForWB(connection,
                                                    wbid,
                                                    memberId);
        }         catch( Exception e ) {
            e.printStackTrace();
        }
        finally {
            try {
                connection.close();
            }
            catch( SQLException sql ) {
                sql.printStackTrace();
            }
        }

        // set attributes to be forwarded for display
        req.setAttribute("tableList" , tableList);
        // forward the request to the jsp page
        sc.getRequestDispatcher("/jsp/collaboration/edit_whiteboard.jsp").forward(req,res);
    }

/*

    public void bwsFormat()
		throws ServletException, IOException
		{

				System.out.println("in bws format");
				Vector tableList = null;
				Vector tableContents = new Vector();
				File template = null;

				Connection connection = null;
				// set the current
				wbid = Integer.parseInt(req.getParameter("wbid"));

				HttpSession hs = req.getSession(true);
				BoardwalkSession bws = 	(BoardwalkSession)hs.getAttribute("bwSession");

				int userId = ((Integer)hs.getAttribute("userId")).intValue();

				Integer memberidParam = (Integer)hs.getAttribute("memberId");

				int memberId = -1;

				if ( memberidParam != null )
				{
					memberId = memberidParam.intValue();
					}

				try
				{

					DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
					connection = databaseloader.getConnection();
					tableList = TableManager.getTablesForWB(connection,
															wbid,
															memberId);
					Vector tableTreeNodes = null;
					CollaborationTreeNode cbNode = (CollaborationTreeNode)tableList.elementAt(0);
					WhiteboardTreeNode wbNode = (WhiteboardTreeNode)cbNode.getWhiteboards().elementAt(0);
					tableTreeNodes =   wbNode.getTables();
					Hashtable tableids = new Hashtable();
					if ( tableTreeNodes != null && tableList.size() > 0 )
					{
						for ( int t = 0; t < tableTreeNodes.size(); t++ )
						{
								TableTreeNode tNode = (TableTreeNode)tableTreeNodes.elementAt(t);
								int    bwtableId = tNode.getId();
								tableids.put(tNode.getName(), new Integer( bwtableId ) );

						} // collected all table contents

						String rootTemplateDir = DatabaseLoader.templatedir;
						String pathSeperator =  System.getProperty("file.separator");
						String wbDir = "Collaborations" + pathSeperator + cbNode.getName() + pathSeperator + wbNode.getName() + pathSeperator + "BoardwalkTemplate.xls";
						String collabDir = "Collaborations" + pathSeperator + cbNode.getName() + pathSeperator + "BoardwalkTemplate.xls";
						String templateLocation = null;


						Vector NhPaths = NeighborhoodManager.getBoardwalkPaths( connection, bws.nhId.intValue());

						System.out.println("trying location" +  rootTemplateDir + pathSeperator + (String)NhPaths.elementAt(0) + pathSeperator + wbDir );
					 	template = new File( rootTemplateDir + pathSeperator + (String)NhPaths.elementAt(0) + pathSeperator + wbDir );

						if ( template.exists() == true )
						{
							templateLocation = rootTemplateDir + pathSeperator + (String)NhPaths.elementAt(0) + pathSeperator + wbDir;
						}
						else
						{
							template = null;
							System.out.println("trying location" + rootTemplateDir + pathSeperator + (String)NhPaths.elementAt(0) + pathSeperator + collabDir  );
							template = new File( rootTemplateDir + pathSeperator + (String)NhPaths.elementAt(0) + pathSeperator + collabDir );
							if ( template.exists() == true )
							{
								templateLocation = rootTemplateDir + pathSeperator + (String)NhPaths.elementAt(0) + pathSeperator + collabDir;
							}
							else
							{
								for ( int n = 0; n < NhPaths.size(); n++ )
								{
									String nhPath = (String)NhPaths.elementAt(n);
									template = null;
									System.out.println("trying location" + rootTemplateDir + pathSeperator + nhPath + pathSeperator + "BoardwalkTemplate.xls" );
									template = new File( rootTemplateDir + pathSeperator + nhPath + pathSeperator + "BoardwalkTemplate.xls" );
									if ( template.exists() == true )
									{
										templateLocation = rootTemplateDir + pathSeperator + nhPath + pathSeperator + "BoardwalkTemplate.xls";
										break;
									}
							 	}

							}

						}

						if ( templateLocation == null | templateLocation == "" )
						{
							templateLocation = rootTemplateDir + pathSeperator +  "BoardwalkTemplate.xls";
						}



						System.out.println(" TemplateLocation is ==== " + templateLocation );

						FileInputStream fis = new FileInputStream(templateLocation);

						DistributionManager dm = new DistributionManager();
						DistributionPacket  dp = dm.getPacket(
									connection,
									fis,
									tableids,
									userId,
									memberId,
									bws.nhId.intValue());


						res.setContentType("application/bws");
						res.setHeader("Content-Disposition", "filename=" + "whiteboard.bws");
						ServletOutputStream so = res.getOutputStream();

						// File temp = File.createTempFile((new java.util.Date()).getTime()+"U"+userId, ".bws");
						// temp.deleteOnExit();
						ZipOutputStream out = new ZipOutputStream(so);
						dp.createPacketFile(
											userId,
											memberId,
											bws.nhId.intValue(),
											out
										   );
					    out.flush();
					    out.close();
					//    so.flush();
				    //	so.close();


					}


			}

			catch (SystemException s)
			{
				s.printStackTrace();
				req.setAttribute("com.boardwalk.exception.SystemException", s);
			}
			catch (SQLException sql)
			{
				sql.printStackTrace();
			   return;
			}
			catch( Exception ex )
			{
				ex.printStackTrace();
			}
			finally
			{
			  try
			  {
				connection.close();
			  }
			  catch ( SQLException sql )
			  {
				sql.printStackTrace();
			  }
			}

		}
*/


    public void doGet(HttpServletRequest request,
    HttpServletResponse response)throws ServletException, IOException

    {
        doPost(request, response);
    }

    public void openWhiteboardBaseline() throws ServletException, IOException {
        // int baselineId = ((Integer)req.getAttribute("baselineId")).intValue();
        int baselineId = Integer.parseInt(req.getParameter("baselineId"));


         HttpSession hs = req.getSession(true);

		Integer memberidParam = (Integer)hs.getAttribute("memberId");

		int memberId = -1;

		if ( memberidParam != null )
		{
			memberId = memberidParam.intValue();
		}

        System.out.println(" Baseline Id " + baselineId );
        // set the current
        wbid = Integer.parseInt(req.getParameter("wbid"));



        Vector tableList = null;
        Connection connection = null;
        // get the tables in the database
        try {
            DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
            connection = databaseloader.getConnection();
            tableList = TableManager.getTablesForWBAndBaseline(connection,
                                                               wbid,
                                                               baselineId,
                                                               memberId // memberId
                                                               );
        }
        catch( Exception e ) {
            e.printStackTrace();
        }
        finally {
            try {
                connection.close();
            }
            catch( SQLException sql ) {
                sql.printStackTrace();
            }
        }

        // set attributes to be forwarded for display
        req.setAttribute("tableList" , tableList);

        // forward the request to the jsp page
        sc.getRequestDispatcher("/jsp/collaboration/edit_whiteboard_baseline.jsp").forward(req,res);





    }
}
