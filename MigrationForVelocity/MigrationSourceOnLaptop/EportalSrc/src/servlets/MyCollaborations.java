package servlets;
/*
 *  This presents a list of collaboration available to a user
 */
import java.io.*;
import java.util.*;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import javax.servlet.*;
import javax.servlet.http.*;

import com.boardwalk.database.*;
import com.boardwalk.exception.NeighborhoodException;
import com.boardwalk.exception.*;
import com.boardwalk.collaboration.*;

import com.boardwalk.whiteboard.WhiteboardManager;
import com.boardwalk.user.*;


import com.boardwalk.neighborhood.*;
import java.lang.Exception;

import java.sql.*;                  // JDBC package
import javax.sql.*;                 // extended JDBC package
import com.boardwalk.util.*;


public class MyCollaborations extends BWServlet

{
    //    BoardwalkDB bwdb;
    HttpServletRequest req;
    HttpServletResponse res;

    ServletContext sc;





    public void doPost(HttpServletRequest request,
    HttpServletResponse response)throws ServletException, IOException {

        sc = getServletContext();
        req = request;
        res = response;

        System.out.println("In MyCollaborations");

		// authenticate and set session
		boolean status = setSession(request, response);
		if (status == false)
		{
			return;
		}
        
		// check the action requested
        String action = request.getParameter("action");
        String context = "";								//added by shirish on 2/29/08
        System.out.println(" action = " + action );
        if ( action == null ) {
            // show the list of available collaborations
            collaborationTree();
		} else if (action.equals("logout")) {
			HttpSession hs = req.getSession(true);
			hs.invalidate();
			context = request.getParameter("context");		//added by shirish on 2/29/08
			System.out.println(" context = " + context );
			if(context == null ||context.trim().equals(""))
				res.sendRedirect("MyCollaborations");
			else if (context.equals("MyCollaborations"))
				res.sendRedirect("MyCollaborations");
			else if (context.equals("InvitationManager"))
				res.sendRedirect("InvitationManager");
			else
				res.sendRedirect("MyCollaborations");
        } else if (action.equals("createCollab")) {
            // take him to the create collaboration page
            createCollab();
        }
        else if (action.equals("commitCollab")) {
            //  commit the collaboration and show the collab lists
            commitCollab();
        }
        else if (action.equals("copyCollab")) {
            //  commit the collaboration and show the collab lists
            copyCollab();
        }
        else if (action.equals("commitCopy")) {
            //  commit the collaboration and show the collab lists
            commitCopy();
        }
        else if (action.equals("removeCollab")) {
            //  commit the collaboration and show the collab lists
            removeCollab();
        }
        else if (action.equals("editCollab")) {
            //  commit the collaboration and show the collab lists
            editCollab();
        }
        else if (action.equals("showBaselineList")) {
            //  show a list of baselines for the selected collaboration
            showBaselineList();
        }
        else if (action.equals("openCollabBaseline")) {
            //  show a list of baselines for the selected collaboration
            openCollabBaseline();
        }
        else if (action.equals("commitBaseline")) {
            //  show a list of baselines for the selected collaboration
            commitBaseline();
        }
        else if (action.equals("removeCollabBaseline")) {
            //  show a list of baselines for the selected collaboration
            purgeBaseline();
        }
        else if (action.equals("addNewWhiteboard")){
            addNewWhiteboard();
        }
        else if (action.equals("commitWhiteboard")){
            commitWhiteboard();

        }
        else if (action.equals("statusReport")){
            tableStatusByNeighborhood();
        }
        else if (action.equals("activityReport")){
            tableActivityByNeighborhood();
        }
        else if (action.equals("collaborationTree")){
		    collaborationTree();
        }
        else if (action.equalsIgnoreCase("switchCurrentMembership"))
	    {
			switchCurrentMembership(req,res);
        }

    }

    public void addNewWhiteboard()
    throws ServletException, IOException {
        int   collabId   = Integer.parseInt(req.getParameter("collabId"));
        req.setAttribute("collabId", new Integer(collabId) );
        // forward the request to the jsp page
        sc.getRequestDispatcher("/jsp/collaboration/create_whiteboard.jsp").
        forward(req,res);
    }

    public void createCollab()
    throws ServletException, IOException {
        Connection connection = null;
        try
        {
            DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
            connection = databaseloader.getConnection();
            // get the list of neighbordhoods and member ids for this user

			HttpSession hs = req.getSession(true);
			Integer nhId = (Integer)hs.getAttribute("nhId");


		if ( nhId != null && nhId.intValue() > -1 )
		{

			req.setAttribute("nhId", hs.getAttribute("nhId"));
			req.setAttribute("memberId", hs.getAttribute("memberId"));
			req.setAttribute("nhName", hs.getAttribute("nhName"));
		}
		else
		{
			BoardwalkException bwe = new BoardwalkException( 10006 );
			req.setAttribute("com.boardwalk.exception.BoardwalkException",bwe);
		}

	         // forward the request to the jsp page
            sc.getRequestDispatcher("/jsp/collaboration/create_collab.jsp").
            forward(req,res);
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

    public void createBaseline()
    throws ServletException, IOException {

        int   collabId   = Integer.parseInt(req.getParameter("collabId"));
        req.setAttribute("collabId", new Integer(collabId) );
        // forward the request to the jsp page
        sc.getRequestDispatcher("/jsp/collaboration/create_baseline.jsp").
        forward(req,res);

    }

    public void commitWhiteboard()
    throws ServletException, IOException {

        String wbName = req.getParameter("wbName");
        int   collabId   = Integer.parseInt(req.getParameter("collabId"));
        Connection connection = null;
        int tid = -1;
        TransactionManager tm = null;
        //  memberid is 0 for now

        try {
            DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
            connection = databaseloader.getConnection();
            tm = new TransactionManager( connection, userId);
            tid = tm.startTransaction();
            int m_wb_id =WhiteboardManager.createWhiteboard(
            connection,
            wbName,
            2, // peer access
            2, // private access
            2, // friend access
            collabId,
            tid,
            1 // status
            );
            tm.commitTransaction();


        }
        catch ( Exception e ) {
            e.printStackTrace();

            try {
                tm.rollbackTransaction();
            }
            catch( SQLException sqlfatal ) {
                sqlfatal.printStackTrace();
            }

            if ( e.getClass().getName().equals("com.boardwalk.exception.BoardwalkException") ) {
                BoardwalkException bwe = (BoardwalkException)e;
                System.out.println(" Boardwalk Message " + bwe.getMessage());
                req.setAttribute("com.boardwalk.exception.BoardwalkException", bwe);
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


        String redirectURL = "MyCollaborations?collabId="+collabId+"&action=editCollab";
		res.sendRedirect(req.getContextPath() + redirectURL);

    }

    public void commitCollab()
    throws ServletException, IOException
    {

        String collabName = req.getParameter("collabName");
        String collabDesc = req.getParameter("collabDesc");
        int memberId = Integer.parseInt(req.getParameter("memberId"));
        // get the access controls from the req

        int    m_collaboration_id = -1;
        Connection connection = null;
        int tid = -1;
        TransactionManager tm = null;
        //  memberid is 1 for now, need to update UI to specify the neighborhood to
        // create the collaboration in

        try {
            DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
            connection = databaseloader.getConnection();
            tm = new TransactionManager( connection, userId);
            tid = tm.startTransaction();
            m_collaboration_id = CollaborationManager.createCollaboration(
                                                                        connection,
                                                                        collabName,
                                                                        collabDesc,
                                                                        memberId, // member id
                                                                        tid, // transaction id
                                                                        1 // status
                                                                        );
            tm.commitTransaction();
             collaborationTree();
        }
        catch ( Exception e ) {
            e.printStackTrace();

            try {
                tm.rollbackTransaction();
            }
            catch( SQLException sqlfatal ) {
                sqlfatal.printStackTrace();
            }

            if ( e.getClass().getName().equals("com.boardwalk.exception.BoardwalkException") ) {
                BoardwalkException bwe = (BoardwalkException)e;
                System.out.println(" Boardwalk Message " + bwe.getMessage());
                req.setAttribute("com.boardwalk.exception.BoardwalkException", bwe);
                createCollab();
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
    }
    public void commitCopy()
    throws ServletException, IOException {


		HttpSession hs = req.getSession(true);
		Integer memberId = (Integer)hs.getAttribute("memberId");


        int   origCollabId   = Integer.parseInt(req.getParameter("origCollabId"));
        String collabName = req.getParameter("collabName");
        String collabDesc = req.getParameter("collabDesc");

        System.out.println("copyStructure"+req.getParameter("copyStructure") );
		System.out.println("copyAccess"+ req.getParameter("copyAccess"));
		System.out.println("copyLatestContent"+ req.getParameter("copyLatestContent"));
		System.out.println("copyDesignValues"+ req.getParameter("copyDesignValues"));
		System.out.println("copyUIPreferences"+ req.getParameter("copyUIPreferences"));


		boolean copyStructure =  req.getParameter("copyStructure") != null ? true:false;
		boolean copyAccess = req.getParameter("copyAccess")!= null ? true:false;
		boolean copyLatestContent = req.getParameter("copyLatestContent")!= null ? true:false;
		boolean copyDesignValues = req.getParameter("copyDesignValues")!= null ? true:false;
		boolean copyUIPreferences = req.getParameter("copyUIPreferences")!= null ? true:false;

		if ( memberId == null || memberId.intValue() == - 1 )
		{
				req.setAttribute("origCollabId" , new Integer(origCollabId));
				BoardwalkException e = new BoardwalkException(10006);
				req.setAttribute("com.boardwalk.exception.BoardwalkException",e);
				// forward the request to the jsp page
				sc.getRequestDispatcher("/jsp/collaboration/copy_collab.jsp").forward(req,res);
				return;
		}



        int    m_collaboration_id = -1;
        Connection connection = null;
        int tid = -1;
        TransactionManager tm = null;

        try {
            DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
            connection = databaseloader.getConnection();
            tm = new TransactionManager( connection, userId);
            tid = tm.startTransaction();

            m_collaboration_id = CollaborationManager.createCollaboration(
                                                        connection,
                                                        collabName,
                                                        collabDesc,
                                                        memberid,
                                                        tid,
                                                        1
                                                        );

            System.out.println("sourceid = " + origCollabId + " targetid = " + m_collaboration_id  + " copyContents = " + copyLatestContent);


            CollaborationManager.copyCollaboration(
                                                connection,
                                                origCollabId,
                                                m_collaboration_id,
                                                copyStructure,
											   copyLatestContent,
											   copyDesignValues,
											   copyUIPreferences,
										   		copyAccess,
										   		memberId.intValue(),
                                                tid
                                                );
            tm.commitTransaction();
             collaborationTree();
        }
        catch ( Exception e ) {
            e.printStackTrace();

            try {
                tm.rollbackTransaction();
            }
            catch( SQLException sqlfatal ) {
                sqlfatal.printStackTrace();
            }

            if ( e.getClass().getName().equals("com.boardwalk.exception.BoardwalkException") ) {
                BoardwalkException bwe = (BoardwalkException)e;
                System.out.println(" Boardwalk Message " + bwe.getMessage());
                req.setAttribute("com.boardwalk.exception.BoardwalkException", bwe);
                createCollab();
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
    }

    public void removeCollab()
    throws ServletException, IOException
    {
        // Make sure the user selected a collaboration and get the selected collab id
        String collabIdParam = req.getParameter("collabId");
        if (collabIdParam == null) {
            req.setAttribute("com.boardwalk.exception.BoardwalkException",
                                new BoardwalkException( 10002));
            collaborationTree();

        }
        int   collabId   = Integer.parseInt(collabIdParam);
        Connection connection = null;
        try {
            DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
            connection = databaseloader.getConnection();
            CollaborationManager.purgeCollaboration( connection, collabId);
        }
        catch( Exception e ) {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                connection.close();
            }
            catch( SQLException sql ) {
                sql.printStackTrace();
            }
        }

        // finally show the updated collaboration list
        collaborationTree();


    }

    public void editCollab()
    throws ServletException, IOException {

		HttpSession hs = req.getSession(true);

		int userId = ((Integer)hs.getAttribute("userId")).intValue();
		Integer memberidParam = (Integer)hs.getAttribute("memberId");

		int memberId = -1;

		if ( memberidParam != null )
		{
			memberId = memberidParam.intValue();
		}


        String collabIdParam = req.getParameter("collabId");
        System.out.println("Collab id is" + collabIdParam);

        if (collabIdParam == null)
        {
			req.setAttribute("com.boardwalk.exception.BoardwalkException",
                                new BoardwalkException( 10002 ));
           collaborationTree();
        }

        int   collabId   = Integer.parseInt(collabIdParam);

        Vector wbTables = null;

        Collaboration collab = null;

        Connection connection = null;

        try
        {
            DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
            connection = databaseloader.getConnection();
            collab = CollaborationManager.getCollaborationInfo(connection,userId, collabId);
            wbTables = WhiteboardManager.getWhiteboardAndTablesByCollaborationAndNeighborhood( connection, collabId, memberId );

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

        req.setAttribute("wbTables", wbTables );
        req.setAttribute("Collaboration", collab );

        // forward the request to the jsp page
        sc.getRequestDispatcher("/jsp/collaboration/edit_collab.jsp").
        forward(req,res);

    }

    public void commitBaseline() throws ServletException, IOException {

        int   collabId   = Integer.parseInt(req.getParameter("collabId"));
        String   baselineName   = req.getParameter("baselineName");
        String   baselinePurpose   = req.getParameter("baselineDesc");

        req.setAttribute("collabId", new Integer(collabId) );
        // forward the request to the jsp page
        Connection connection = null;
        int tid = -1;
        TransactionManager tm = null;
        //  memberid is 0 for now

        try {
            DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
            connection = databaseloader.getConnection();
            tm = new TransactionManager( connection,userId);
            tid = tm.startTransaction();
            int m_baseline_id =CollaborationManager.createBaseline(
            connection,
            baselineName,
            baselinePurpose,
            collabId,
            tid
            );
            tm.commitTransaction();
        }
        catch ( Exception e ) {
            e.printStackTrace();

            try {
                tm.rollbackTransaction();
            }
            catch( SQLException sqlfatal ) {
                sqlfatal.printStackTrace();
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


        String redirectURL = "/MyCollaborations?action=showBaselineList&collabId="+collabId;
		res.sendRedirect(req.getContextPath() + redirectURL);



    }

    public void purgeBaseline()
    {
        int   collabId   = Integer.parseInt(req.getParameter("collabId"));
        int   baselineId   = Integer.parseInt(req.getParameter("baselineId"));

       Connection connection = null;
        int tid = -1;
        TransactionManager tm = null;

        try {
            DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
            connection = databaseloader.getConnection();
            tm = new TransactionManager( connection, userId);
            tid = tm.startTransaction();
            CollaborationManager.purgeBaseline(connection, baselineId);
            tm.commitTransaction();
        }
        catch (SystemException e1) {
            try
            {
                tm.rollbackTransaction();
            }
            catch( SQLException sql )
            {
                sql.printStackTrace();
            }
            e1.printStackTrace();
        }
        catch (SQLException sql) {
            try
            {
                tm.rollbackTransaction();
            }
            catch( SQLException sql1 )
            {
                sql1.printStackTrace();
            }
            sql.printStackTrace();
        }
        finally {
            try {
                connection.close();
            }
            catch ( SQLException sql ) {
                sql.printStackTrace();
            }
        }
        try
        {
                 String redirectURL = "/MyCollaborations?action=showBaselineList&collabId="+collabId;
				  res.sendRedirect(req.getContextPath() + redirectURL);
				//sc.getRequestDispatcher(redirectURL).forward(req,res);

        } catch (Exception e2)
        {
            e2.printStackTrace();
        }
    }

    public void openCollabBaseline()
    throws ServletException, IOException
    {
        int   collabId   = Integer.parseInt(req.getParameter("collabId"));
        int   baselineId   = Integer.parseInt(req.getParameter("baselineId"));

        HttpSession hs = req.getSession(true);

		Integer memberidParam = (Integer)hs.getAttribute("memberId");

		int memberId = -1;

		if ( memberidParam != null )
		{
			memberId = memberidParam.intValue();
		}


        Hashtable wbTables = null;
        Connection connection = null;
        try {
            DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
            connection = databaseloader.getConnection();
            wbTables = WhiteboardManager.getWhiteboardsAndTablesByCollaborationAndBaseline( connection, collabId,baselineId, memberId );
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

        req.setAttribute("wbTables", wbTables );
        req.setAttribute("baselineId", new Integer(baselineId) );
        req.setAttribute("collabId", new Integer(collabId) );

        // forward the request to the jsp page
        sc.getRequestDispatcher("/jsp/collaboration/openCollabBaseline.jsp").
        forward(req,res);

    }

    public void copyCollab()
    throws ServletException, IOException {

        int   collabId   = Integer.parseInt(req.getParameter("collabId"));
        System.out.println("Original Collab Id = " + collabId);
        req.setAttribute("origCollabId" , new Integer(collabId));

        // forward the request to the jsp page
        sc.getRequestDispatcher("/jsp/collaboration/copy_collab.jsp").forward(req,res);
    }




    public void collaborationTree()
	   throws ServletException, IOException
	{

		System.out.println( " In collaborationTree" );
		Connection connection  = null;
		HttpSession hs = req.getSession(true);
		BoardwalkSession bws = (BoardwalkSession)hs.getAttribute("bwSession");

		//int userId = bws.userId.intValue();
		//Integer memberidParam =bws.memberId;


		int   selNhid   = -1;
		//int memberId = -1;

		String selNhidParam = req.getParameter("selNhid");

		//System.out.println(" SelNhid = " + selNhid+ " memberid = " + memberidParam );

		if (selNhidParam != null && !selNhidParam.trim().equals(""))
		{
			selNhid = Integer.parseInt(selNhidParam);
		}
		else
		{
			//    if ( memberidParam != null )
			//    {
			//         memberId = memberidParam.intValue();
			//         selNhid   = bws.nhId.intValue();
			//     }
			//}

			//System.out.println(" SelNhid = " + selNhid+ " memberid = " + memberId + " userid " + userId );
			selNhid = nhId;
		}

		try
		{
			DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
			connection = databaseloader.getConnection();
			Hashtable collabTables = new Hashtable();
			if ( selNhid != -1 )
			{

				Vector collabList = CollaborationManager.getCollaborationsOfNeighborhood( connection, selNhid);
				//System.out.println("collablist for nhId " + selNhid + "  count= " + collabList.size() );
				req.setAttribute("collabList" , collabList);
				Iterator cIter = collabList.iterator();
				while (cIter.hasNext())
				{
					CollaborationTreeNode ctn = (CollaborationTreeNode)cIter.next();
					Vector wbTables = null;
					wbTables = WhiteboardManager.getWhiteboardAndTablesByCollaborationAndNeighborhood(connection, ctn.getId(), memberid);
					collabTables.put(new Integer(ctn.getId()), wbTables);

				}
				req.setAttribute("collabTables", collabTables);

				NhName nm = null;
				nm = NeighborhoodManager.getNeighborhoodNameById(connection, selNhid);

				if ( nm != null )
				{
					req.setAttribute("nhIdName", nm.name);
				}
				else
				{
					System.out.println(" nm is null " );
				}

				req.setAttribute("nhId",new Integer(selNhid));


			}

			Vector nhTree = NeighborhoodManager.getNeighborhoodTree(connection, userId);

			req.setAttribute("nhTree", nhTree);
			req.setAttribute("title", "Collaboration Tree");

			// forward the request to the jsp page
			sc.getRequestDispatcher("/jsp/collaboration/collaborationTree.jsp").forward(req,res);

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
    }

    public void tableStatusByNeighborhood()
	   throws ServletException, IOException
	{
		Connection connection  = null;
		HttpSession hs = req.getSession(true);
		BoardwalkSession bws = (BoardwalkSession)hs.getAttribute("bwSession");

		int userId = bws.userId.intValue();
		Integer memberidParam =bws.memberId;


		int   selNhid   = -1;
		int memberId = -1;

		String selNhidParam = req.getParameter("selNhid");

		System.out.println(" SelNhid = " + selNhid+ " memberid = " + memberidParam );

		if ( selNhidParam != null && ! selNhidParam.trim().equals("") )
		{
				selNhid   = Integer.parseInt(selNhidParam);
				if ( memberidParam != null )
					 memberId = memberidParam.intValue();
		}
		else
		{
			if ( memberidParam != null )
			{
				 memberId = memberidParam.intValue();
				 selNhid   = bws.nhId.intValue();
			 }
		}

		System.out.println(" Status report ++++++++++ SelNhid = " + selNhid+ " memberid = " + memberId + " userid " + userId );

		try
		{
			DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
			connection = databaseloader.getConnection();
			Hashtable collabTables = new Hashtable();
			Hashtable collabActivitySummaries = new Hashtable();
			if ( selNhid != -1 )
			{

				Vector collabList = CollaborationManager.getCollaborationsOfNeighborhood( connection, selNhid);
				//System.out.println("collablist for nhId " + selNhid + "  count= " + collabList.size() );
				req.setAttribute("collabList" , collabList);
				Iterator cIter = collabList.iterator();
				while (cIter.hasNext())
				{
					CollaborationTreeNode ctn = (CollaborationTreeNode)cIter.next();
					Vector wbTables = null;
					wbTables = WhiteboardManager.getWhiteboardAndTablesByCollaborationAndNeighborhood( connection,ctn.getId(), memberId );
					collabTables.put(new Integer(ctn.getId()), wbTables);
					Hashtable activitySummary = null;
					activitySummary = CollaborationManager.getStatus(connection, ctn.getId());
					if (activitySummary != null)
					{
						collabActivitySummaries.put(new Integer(ctn.getId()), activitySummary);
					}
				}
				req.setAttribute("collabTables", collabTables);
				req.setAttribute("collabActivitySummaries", collabActivitySummaries);

				NhName nm = null;
				nm = NeighborhoodManager.getNeighborhoodNameById(connection, selNhid);

				if ( nm != null )
				{
					req.setAttribute("nhIdName", nm.name);
				}
				else
				{
					System.out.println(" nm is null " );
				}

				req.setAttribute("nhId",new Integer(selNhid));


			}

			//Vector nhTree = NeighborhoodManager.getNeighborhoodTree(connection, userId);

			//req.setAttribute("nhTree", nhTree);
			req.setAttribute("title", "Boardwalk");

			// forward the request to the jsp page
			sc.getRequestDispatcher("/jsp/collaboration/statusReport.jsp").forward(req,res);

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
    }

    public void tableActivityByNeighborhood()
	   throws ServletException, IOException
	{
		Connection connection  = null;
		HttpSession hs = req.getSession(true);
		BoardwalkSession bws = (BoardwalkSession)hs.getAttribute("bwSession");

		int userId = bws.userId.intValue();
		Integer memberidParam =bws.memberId;


		int   selNhid   = -1;
		int memberId = -1;

		String selNhidParam = req.getParameter("selNhid");

		System.out.println(" SelNhid = " + selNhidParam+ " memberid = " + memberidParam );

		if ( selNhidParam != null && ! selNhidParam.trim().equals("") )
		{
				selNhid   = Integer.parseInt(selNhidParam);
				if ( memberidParam != null )
					 memberId = memberidParam.intValue();
		}
		else
		{
			if ( memberidParam != null )
			{
				 memberId = memberidParam.intValue();
				 selNhid   = bws.nhId.intValue();
			 }
		}

		//System.out.println(" SelNhid = " + selNhid+ " memberid = " + memberId + " userid " + userId );


		long endDate;
		long startDate;

		String endDateStr = req.getParameter("endDate");
		String startDateStr = req.getParameter("startDate");
		java.util.Date d = new java.util.Date();
		endDate = d.getTime();
		startDate = 0;

		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		cal.setTime(d);

		String period = req.getParameter("period");
		if (period.equals("Week"))
		{
			cal.add(Calendar.DATE, -7);
			startDate = cal.getTime().getTime();
		}
		else if (period.equals("Month"))
		{
			cal.add(Calendar.MONTH, -1);
			startDate = cal.getTime().getTime();
		}
		else if (period.equals("Quarter"))
		{
			cal.add(Calendar.MONTH, -3);
			startDate = cal.getTime().getTime();
		}
		else if (period.equals("Year"))
		{
			cal.add(Calendar.YEAR, -1);
			startDate = cal.getTime().getTime();

		}
		else if (period.equals("Custom") && endDateStr != null && startDateStr != null)
		{
			endDate = Long.parseLong(endDateStr);
			startDate = Long.parseLong(startDateStr);
		}


		//java.util.Date endDate = null;
		//java.util.Date startDate = null;
		//try
		//{
		//	SimpleDateFormat df = new SimpleDateFormat("E MMM d hh:mm:ss z y");
		//	endDate = df.parse(endDateStr);
		//	startDate = df.parse(startDateStr);
		//}
		//catch (ParseException pe)
		//{
		//	pe.printStackTrace();
		//}

		try
		{
			DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
			connection = databaseloader.getConnection();
			Hashtable collabTables = new Hashtable();
			Hashtable collabActivitySummaries = new Hashtable();
			if ( selNhid != -1 )
			{

				Vector collabList = CollaborationManager.getCollaborationsOfNeighborhood( connection, selNhid);
				//System.out.println("collablist for nhId " + selNhid + "  count= " + collabList.size() );
				req.setAttribute("collabList" , collabList);
				Iterator cIter = collabList.iterator();
				while (cIter.hasNext())
				{
					CollaborationTreeNode ctn = (CollaborationTreeNode)cIter.next();
					Vector wbTables = null;
					wbTables = WhiteboardManager.getWhiteboardAndTablesByCollaborationAndNeighborhood( connection,ctn.getId(), memberId );
					collabTables.put(new Integer(ctn.getId()), wbTables);
					Hashtable activitySummary = null;

					activitySummary = CollaborationManager.getActivity(connection, ctn.getId(),
					        endDate,
					        startDate);
					if (activitySummary != null)
					{
						collabActivitySummaries.put(new Integer(ctn.getId()), activitySummary);
					}
				}
				req.setAttribute("collabTables", collabTables);
				req.setAttribute("collabActivitySummaries", collabActivitySummaries);

				NhName nm = null;
				nm = NeighborhoodManager.getNeighborhoodNameById(connection, selNhid);

				if ( nm != null )
				{
					req.setAttribute("nhIdName", nm.name);
				}
				else
				{
					System.out.println(" nm is null " );
				}

				req.setAttribute("nhId",new Integer(selNhid));
			}

			//Vector nhTree = NeighborhoodManager.getNeighborhoodTree(connection, userId);

			//req.setAttribute("nhTree", nhTree);
			req.setAttribute("title", "Boardwalk");
			req.setAttribute("startDate", new Long(startDate));
			req.setAttribute("endDate", new Long(endDate));

			// forward the request to the jsp page
			sc.getRequestDispatcher("/jsp/collaboration/activityReport.jsp").forward(req,res);

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
    }

    public void showBaselineList()
    throws ServletException, IOException {
        Connection connection = null;
        int   collabId   = Integer.parseInt(req.getParameter("collabId"));

        try {
            DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
            connection = databaseloader.getConnection();
            Vector baselines =  CollaborationManager.getBaselineList(
                                                                connection,
                                                                userId,
                                                                collabId
                                                                );
            Collaboration collab = CollaborationManager.getCollaborationInfo( connection, userId,
                                                                collabId );

            // set attributes to be forwarded for display
            req.setAttribute("baselineList" , baselines);
            req.setAttribute("collabId" , new Integer(collabId));
            req.setAttribute("collaboration" , collab);


            // forward the request to the jsp page
            sc.getRequestDispatcher("/jsp/collaboration/baseline_list.jsp").forward(req,res);
        }
        catch ( Exception e ) {
            e.printStackTrace();
        }
        finally {
            try {
                connection.close();
            }
            catch ( SQLException sql ) {
                sql.printStackTrace();
            }
        }
    }

    public void doGet(HttpServletRequest request,
    HttpServletResponse response)throws ServletException, IOException

    {
        doPost(request, response);
    }
}
