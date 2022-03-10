package servlets;

import java.io.*;
import java.util.*;
import java.sql.*;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.sql.*;

import com.boardwalk.neighborhood.*;
import com.boardwalk.exception.*;
import com.boardwalk.database.*;
import com.boardwalk.member.*;
import com.boardwalk.user.*;
import com.boardwalk.util.*;
import com.boardwalk.table.*;



public class BW_NeighborhoodsLogic extends BWServletLogic 
{
    ServletContext sc;
    HttpServletRequest req;
    HttpServletResponse res;
    int userId = -1;
    int memberid = -1;

    public BW_NeighborhoodsLogic(HttpServlet servlet) {
        super(servlet);
    }

    public void doPost (HttpServletRequest request,
	    HttpServletResponse response)throws ServletException, IOException
    {
        if (authenticate(request, response) == false)
        	return;

        // if authenticated set the user ID
        HttpSession hs = request.getSession(true);
		userId = ((Integer)hs.getAttribute("userId")).intValue();
		memberid = ((Integer)hs.getAttribute("memberId")).intValue();


		sc = getServletContext();
		req = request;
		res = response;

		BoardwalkSession bws = 	(BoardwalkSession)hs.getAttribute("bwSession");
		req.setAttribute("bwSession",bws);

		System.out.println("getContextPath() " + req.getContextPath());
		System.out.println("getPathInfo() " + req.getPathInfo());
		System.out.println("getQueryString() " + req.getQueryString());
		System.out.println("getRequestURI() " + req.getRequestURI());
		System.out.println("getServletPath() " + req.getServletPath());

        // check the action requested
        String action = request.getParameter ("action");

	// no action means go to the neighborhood report page
	if (action == null) {
	    nhReport();
	}
	else if (action.equals("newRelation")) {
			    newRelation();
	}
	else if (action.equals("editRelation")) {
				    editRelation();
	}
	else if (action.equals("saveRelation")) {
			    nhCommitRelations();
	}
	else if (action.equals("deleteRelation")) {
		    deleteRelation();
	}
	else if (action.equals("createNH")) {
	    nhCreate();
	}
	else if (action.equals("commitNH")) {
	    nhCommit();
	}
	else if (action.equals("deleteNH")) {
	    nhPurge();
	}
	else if (action.equals("membersNH")) {
	    nhMembers();
	}
	else if (action.equals("joinMembers")) {
		nhJoinMember();
	}

	else if (action.equals("memberCreate")) {
		    nhJoinMember();
	}
	else if (action.equals("memberCommit")) {
	    commitMember();
	}
    else if (action.equalsIgnoreCase("memberDelete"))
    {
            deleteMember();
     }
	  else if (action.equalsIgnoreCase("addUser"))
     {
					register();
     }
	  else if (action.equalsIgnoreCase("deactivateUser"))
     {
					deactivateUser();
     }
     else if (action.equalsIgnoreCase("NHDetails"))
     {
		            setnhDetails();
     }
     else if (action.equalsIgnoreCase("switchCurrentMembership"))
	  {
					switchCurrentMembership(req,res);
     }

	else {
	    // default is the report page
	    nhReport();
	}
    }

public void deactivateUser()
	  {
	        String members[] = req.getParameterValues("memberId");
	        int selNhid = Integer.parseInt(req.getParameter("selNhid"));
			System.out.println("<<<<<<<<<<<<<<<<<---------Inside Deactivate User----------------->>>>>>>>");
	        Connection connection = null;
			PreparedStatement stmt = null;
	        int tid = -1;
	     //   System.out.println("Deleting " + members.length + " members ");
	        try {
	            DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
	            connection = databaseloader.getConnection();
	            int memberId;
	            HttpSession hs = req.getSession(true);
				Integer userId = (Integer)hs.getAttribute("userId");
				BoardwalkSession bws = (BoardwalkSession)hs.getAttribute("bwSession");

	            // create the membership for userAddress
	            for (int i=0;i<members.length;i++)
	            {
					int userIdForMember = -1;
					  memberId = Integer.parseInt(members[i]);
					  String query = "SELECT USER_ID FROM BW_MEMBER WHERE ID = ?";
					try {
						stmt = connection.prepareStatement(query);
						stmt.setInt(1, memberId);
						ResultSet rs = stmt.executeQuery();
						while (rs.next())
						{
							userIdForMember	= rs.getInt("USER_ID");
						}
						}catch(SQLException sqlexception)
						{
							throw new SystemException(sqlexception);
						}

					  System.out.println("<<<<<<<<<<<<<<<<<<<<<<member Id >>>>>>>>>>>>>>" + memberId);
	                  MemberManager.deactivateUser(userIdForMember);
					  MemberManager.deleteMember(connection, memberId);
	            }



			  if ( userId.intValue()  == bws.userId.intValue())
			   {
							Hashtable memberships  = UserManager.getMembershipsForUser( connection, bws.userId.intValue());
							Vector membershipList = UserManager.getMembershipListForUser( connection, bws.userId.intValue() );
							bws.membershipList = membershipList;
							bws.memberIdToMember = memberships;

							if ( bws.membershipList.size() == 0 )
								{
										bws.memberId = new Integer(-1);
										bws.nhId = new Integer(-1);
										bws.nhName ="";
										bws.selNhid = new Integer(-1);
								}
								else
								{
									if ( memberships.get(bws.memberId )== null )
									{
										Member m =(Member) membershipList.elementAt(0);
										bws.memberId = new Integer(m.getId());
										bws.nhId = new Integer(m.getNeighborhoodId());
										bws.nhName =m.getNeighborhoodName();
										bws.selNhid = new Integer(m.getNeighborhoodId());
									}
								}
				}


				// go back to the membership table with update
					 String redirectURL = "/BW_Neighborhoods?action=membersNH&selNhid="+selNhid;
				   	 res.sendRedirect(req.getContextPath() + redirectURL);

	        }
	        catch ( Exception e ) {
	            e.printStackTrace();

	            if ( e.getClass().getName().equals("com.boardwalk.exception.BoardwalkException") ) {
	                BoardwalkException bwe = (BoardwalkException)e;
	          //      System.out.println(" Boardwalk Message " + bwe.getMessage());
	                req.setAttribute("BoardwalkException", bwe);
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
	public void register()
	{
		String selNhid = req.getParameter("selNhid");
		//int selNhid = Integer.parseInt(req.getParameter("selNhid"));
		try
		{
					req.setAttribute("selNhid", selNhid);
		            sc.getRequestDispatcher("/jsp/admin/register.jsp").forward(req, res);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}


    public void deleteMember()
	    throws ServletException, IOException
	    {
	        String members[] = req.getParameterValues("memberId");

	        int selNhid = Integer.parseInt(req.getParameter("selNhid"));
	        Connection connection = null;
	        int tid = -1;
	        TransactionManager tm = null;
	     //   System.out.println("Deleting " + members.length + " members ");
	        try {
	            DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
	            connection = databaseloader.getConnection();
	            tm = new TransactionManager( connection, userId);
	            tid = tm.startTransaction();
	            int memberId;
	            HttpSession hs = req.getSession(true);
				Integer userId = (Integer)hs.getAttribute("userId");
				BoardwalkSession bws = (BoardwalkSession)hs.getAttribute("bwSession");

	            // create the membership for userAddress
	            for (int i=0;i<members.length;i++)
	            {
					System.out.println(" members[i] --> "+members[i]);
					memberId = Integer.parseInt(members[i]);
	                MemberManager.deleteMember(connection, memberId);
					System.out.println("member Id " + memberId);
	            }



			  if ( userId.intValue()  == bws.userId.intValue())
			   {
							System.out.println("");
							Hashtable memberships  = UserManager.getMembershipsForUser( connection, bws.userId.intValue());
							Vector membershipList = UserManager.getMembershipListForUser( connection, bws.userId.intValue() );
							bws.membershipList = membershipList;
							bws.memberIdToMember = memberships;

							if ( bws.membershipList.size() == 0 )
								{
										bws.memberId = new Integer(-1);
										bws.nhId = new Integer(-1);
										bws.nhName ="";
										bws.selNhid = new Integer(-1);
								}
								else
								{
									if ( memberships.get(bws.memberId )== null )
									{
										Member m =(Member) membershipList.elementAt(0);
										bws.memberId = new Integer(m.getId());
										bws.nhId = new Integer(m.getNeighborhoodId());
										bws.nhName =m.getNeighborhoodName();
										bws.selNhid = new Integer(m.getNeighborhoodId());
									}
								}
				}

				tm.commitTransaction();


				// go back to the membership table with update
					 String redirectURL = "/BW_Neighborhoods?action=membersNH&selNhid="+selNhid;
				   	 res.sendRedirect(req.getContextPath() + redirectURL);

	        }
	        catch ( Exception e ) {
	            e.printStackTrace();
				try
			   {
					tm.rollbackTransaction();
				}
				catch( SQLException sqe )
				{
					sqe.printStackTrace();
	            }
	            if ( e.getClass().getName().equals("com.boardwalk.exception.BoardwalkException") ) {
	                BoardwalkException bwe = (BoardwalkException)e;
	          //      System.out.println(" Boardwalk Message " + bwe.getMessage());
	                req.setAttribute("BoardwalkException", bwe);
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

public void commitMember()
    throws ServletException, IOException
    {
        int selNhid = Integer.parseInt(req.getParameter("selNhid"));
        // get the selected userAddress
        String userIds = req.getParameter("userList");
        Connection connection = null;
        TransactionManager tm;
        int tid = -1;
        String uid;
        Enumeration params = req.getParameterNames();

        //System.out.println(userIds);
        int startind = 0;
        int endind = 0;
        int id;

        try {
            DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
            connection = databaseloader.getConnection();
            tm = new TransactionManager( connection, userId);
             tid = tm.startTransaction();
            // create the membership for userAddress
            while (!"".equals(userIds))
            {
                startind = userIds.lastIndexOf("+") + 1;
                endind = userIds.length();
                uid = userIds.substring(startind,endind);
             if (startind == 0)
                  userIds = "";
                else
                   userIds = userIds.substring(0,startind-1);


               id = Integer.parseInt(uid);

			  MemberManager.activateUser(id);


			    int memberId = MemberManager.createMember(connection, tid, id, selNhid);

           //    System.out.println("CREATED MEMBER");
                if ( memberId != -1 && memberId != 0 )
                {

     					HttpSession hs = req.getSession(true);
            	       	Integer userId = (Integer)hs.getAttribute("userId");
            	       if ( userId.intValue()  == id )
            	       {
									Neighborhood nh = NeighborhoodManager.getNeighborhoodById( connection , selNhid);
									hs.setAttribute("memberId", new Integer(memberId));
									hs.setAttribute("selNhid", new Integer(selNhid));
									hs.setAttribute("nhId", new Integer(selNhid));
									hs.setAttribute("nhName", nh.getName());

									Hashtable memberships  = UserManager.getMembershipsForUser( connection, id );
									Vector membershipList = UserManager.getMembershipListForUser( connection, id );

									BoardwalkSession bws = (BoardwalkSession)hs.getAttribute("bwSession");
									bws.userId = new Integer(id);
									bws.memberId = new Integer(memberId);
									bws.nhId = new Integer(selNhid);
									bws.nhName =nh.getName();
									bws.selNhid = new Integer(selNhid);
									bws.membershipList = membershipList;
									bws.memberIdToMember = memberships;
                        }
                }

          }

             tm.commitTransaction();


            // go back to the membership table with update
            // nhMembers();

              String redirectURL = "/BW_Neighborhoods?action=membersNH&selNhid="+selNhid;
			 res.sendRedirect(req.getContextPath() + redirectURL);

        }
        catch ( Exception e ) {
            e.printStackTrace();

            if ( e.getClass().getName().equals("com.boardwalk.exception.BoardwalkException") )
            {
                BoardwalkException bwe = (BoardwalkException)e;
        		 //       System.out.println(" Boardwalk Message " + bwe.getMessage());
                req.setAttribute("BoardwalkException", bwe);
                nhJoinMember();
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



    public void createMember() throws ServletException, IOException
    {
	if (req.getParameter("selNhid") != null) {
	    int selNhid = Integer.parseInt(req.getParameter("selNhid"));


            Connection connection = null;
            try {
                DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
                connection = databaseloader.getConnection();

                NhName nhName = NeighborhoodManager.getNeighborhoodNameById(connection, selNhid);
                req.setAttribute("nhName", nhName.name);
                // forward the request
                sc.getRequestDispatcher("/jsp/admin/member_create.jsp").forward(req,res);
            } catch ( Exception e) {

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
	}
    }



     public void nhJoinMember() throws ServletException, IOException
	    {
		if (req.getParameter("selNhid") != null)
		{
		    	int selNhid = Integer.parseInt(req.getParameter("selNhid"));
	            String nhName = req.getParameter("nhName");

	            Connection connection = null;
	            try {
	                DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
	                connection = databaseloader.getConnection();
	                Vector nhUsers = MemberManager.getNonMembersWithUsername(connection,selNhid);
	                req.setAttribute("nhName", nhName);
	                req.setAttribute("nhUsers",nhUsers);
	                // forward the request
	                sc.getRequestDispatcher("/jsp/admin/member_create.jsp").forward(req,res);
	            } catch ( Exception e) {

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
		}
    }



    // Report the memberships to this neighborhood
	    public void nhMembers() throws ServletException, IOException {
	        // get the selected NH
	        if (req.getParameter("selNhid") != null)
	        {
	            int selNhid = Integer.parseInt(req.getParameter("selNhid"));
				System.out.println("value of NeighborhoodID"+selNhid);
	     //       System.out.println(" nhMembers for " + selNhid );
	            Connection connection  = null;
	            try {
	                DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
	                connection = databaseloader.getConnection();

	                NhName nhName = NeighborhoodManager.getNeighborhoodNameById(connection, selNhid);
	                Neighborhood nh = nhName.nh;
	                int mUserId = nh.getManagedby();
					System.out.println("value of userId"+mUserId);
	                User managedUser = UserManager.getUserbyId(connection,mUserId);
					
					req.setAttribute("managedbyEmail", (String) managedUser.getAddress());
	          //      System.out.println("Managed by "+managedUser.getAddress());
	                req.setAttribute("isSecure",new Boolean(nh.isSecure()));
	          //      System.out.println( "NAME " + nhName.name+"isSecure " + nh.isSecure());
	                req.setAttribute("nhName", nhName.name);
	                req.setAttribute("memberId",new Integer(memberid));
	                req.setAttribute("userId",new Integer(userId));
	         //       System.out.println("User Id " + userId + " memberId " + memberid);
	                boolean isManager = false;
	                if (userId == nh.getManagedby()) {
	                     isManager = true;
	                }
	                req.setAttribute("isManager",new Boolean(isManager));
	                boolean isMbrofNH = false;

	                Hashtable nhMembers = MemberManager.getMembersWithUsernameForNeighborhood(connection, selNhid);
	                if (!isMbrofNH)
	                {
	                  Enumeration nhMbr = nhMembers.keys();
	                  int member_uid;

	                  Member mbr;
	                  while (nhMbr.hasMoreElements())
	                  {
	                    mbr = (Member) nhMbr.nextElement();
	                    member_uid = mbr.getUserId();
	             //       System.out.println("Member UID " + member_uid);
	                    if (member_uid == userId)
	                    {
	                      isMbrofNH = true;
	                      break;
	                    }
	                  }
	                }
	        //        System.out.println("isMbrofNH "+isMbrofNH);
	                req.setAttribute("isMbrofNH",new Boolean(isMbrofNH));
	                req.setAttribute("nhMembers", nhMembers);

	                // forward the request
	                sc.getRequestDispatcher("/jsp/admin/nh_members.jsp").forward(req,res);
	            } catch (Exception e) {
	                e.printStackTrace();
	            } finally {
	                try {
	                    connection.close();
	                }
	                catch( SQLException sql ) {
	                    sql.printStackTrace();
	                }
	            }
	        }
	    }


    public void nhCommit() throws ServletException, IOException
    {
		int nhLevel = 0;
		boolean secure;

		int parentId = Integer.parseInt(req.getParameter("parentId"));
		int parentLevel = Integer.parseInt(req.getParameter("parentLevel"));

		//   System.out.println("Parent ID = " + parentId);
		String nhName = req.getParameter("nhName");
		if (req.getParameter("secure") == null)
		secure = false;
		else
		secure = true;

		if (parentId >= 0 ) { // has a parent
		nhLevel = parentLevel + 1;
		if (nhLevel > 3) {
				// REPORT ERROR
				nhReport();
				return;
			}
		}
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

            switch (nhLevel) {
                case 0:
					System.out.println("CD LEVEL0 = " + nhName);
                    NeighborhoodManagerLevel_0.createNeighborhood(connection, nhName, tid, secure);
                    break;
                case 1:
					System.out.println("CD LEVEL1 = " + nhName);
                    NeighborhoodManagerLevel_1.createNeighborhood(connection, nhName, parentId, tid, secure);
                    break;
                case 2:
					System.out.println("CD LEVEL2 = " + nhName);
                    NeighborhoodManagerLevel_2.createNeighborhood(connection, nhName, parentId, tid, secure);
                    break;
                case 3:
					System.out.println("CD LEVEL3 = " + nhName);
					NeighborhoodManagerLevel_3.createNeighborhood(connection, nhName, parentId, tid, secure);
                    break;
            }
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
          //      System.out.println(" Boardwalk Message " + bwe.getMessage());
                req.setAttribute("com.boardwalk.exception.BoardwalkException", bwe);
                nhReport();
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
	// Go back to the report page
	     String redirectURL = "/BW_Neighborhoods";
		 res.sendRedirect(req.getContextPath() + redirectURL);
    }


    public void nhPurge() throws IOException, ServletException
    {
      //  System.out.println("Inside nhpurge");
        String selNhidParam = req.getParameter("selNhid");
        if (selNhidParam == null) {
            req.setAttribute("com.boardwalk.exception.BoardwalkException", new BoardwalkException(11004));
        }
        int nhid			 = Integer.parseInt(selNhidParam);

        Connection connection = null;
        int tid = -1;
        TransactionManager tm = null;
        //  memberid is 1 for now, need to update UI to specify the neighborhood to
        // create the collaboration in

        try {
            DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
            connection = databaseloader.getConnection();
			ValidateAdminTableAccess objVal = new ValidateAdminTableAccess();
			ArrayList NhidsToBeDeleted	 = objVal.getNhChildrenForNHid(nhid);
			tm = new TransactionManager( connection, userId);
            tid = tm.startTransaction();
			for (int i = 0; i < NhidsToBeDeleted.size() ;i++ )
			{
				int deleteNhId = ((Integer)NhidsToBeDeleted.get(i)).intValue();
				System.out.println("((((((((((value of nhid in Bw_nh ))))))))))))"+deleteNhId);
				NeighborhoodManager.purgeNeighborhood(connection,deleteNhId);
			}
            //NeighborhoodManager.purgeNeighborhood(connection,nhid);
            tm.commitTransaction();
        } catch (BoardwalkException bwe)
        {
            try
            {
				bwe.printStackTrace();
                tm.rollbackTransaction();
            }
            catch( SQLException sqe )
            {
                sqe.printStackTrace();
            }
			req.setAttribute("com.boardwalk.exception.BoardwalkException", bwe);
			nhReport();
		 }
        catch (SystemException sys)
        {
            try
            {
				sys.printStackTrace();
                tm.rollbackTransaction();
            }
            catch( SQLException sqe )
            {
                sqe.printStackTrace();
            }
            sys.printStackTrace();
        }
        catch (SQLException sqe)
        {
            try
            {
				sqe.printStackTrace();
                tm.rollbackTransaction();
            }
            catch( SQLException sqe1 )
            {
                sqe1.printStackTrace();
            }
            sqe.printStackTrace();
        }
        finally
        {
            try{

             connection.close();
            }
            catch( SQLException sqe )
            {
                sqe.printStackTrace();
            }
        }

     	// Go back to the report page
	 	String redirectURL = "/BW_Neighborhoods";
		res.sendRedirect(req.getContextPath() + redirectURL);
	}
    public void nhCreate() throws ServletException, IOException
    {
	// is there a parent?
		if (req.getParameter("selNhid") != null && !req.getParameter("selNhid").trim().equals(""))
		{
			// who is the parent
			System.out.println("CD was here ***************************** " );
			int parentId = Integer.parseInt(req.getParameter("selNhid"));
			if ( parentId != -1 )
			{


						// get the Parent NH and name
							Connection connection = null;

						try {
							NhName nm = null;
							DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
							connection = databaseloader.getConnection();

							nm = NeighborhoodManager.getNeighborhoodNameById(connection, parentId);

							req.setAttribute("parentName", nm.name);
							req.setAttribute("parentLevel", new Integer(nm.nh.getLevels()));
						} catch (Exception e) {
							e.printStackTrace();
						}
						finally
						{
							try
							{
								connection.close();
							} catch (Exception e)
							{
								e.printStackTrace();
							}
						}
		 	}
        }

	// forward the request
	sc.getRequestDispatcher("/jsp/admin/nh_create.jsp").forward(req,res);
    }


/*
 *  Puts out a report of the neighborhoods in your control, and their
 *  heirarchy.
 */
   public void nhReport()
   {
	// get the nh tree heirarchy from the database
	Connection connection = null;
	HttpSession hs = req.getSession(true);
	int userId = ((Integer)hs.getAttribute("userId")).intValue();
	int memberid = ((Integer)hs.getAttribute("memberId")).intValue();

	String selNhid = req.getParameter ("selNhid");

	int nhId  =  selNhid == null ? -1: Integer.parseInt( selNhid );

	if (nhId == 0 || nhId == -1)
	{
		Integer sessionNhId = (Integer)hs.getAttribute("nhId");
		nhId =sessionNhId != null ?  sessionNhId.intValue() : -1;
	}

	try
	{
	   DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
	   connection = databaseloader.getConnection();
	   NhName nm = null;
	   Vector nhRel = null;

	   Vector nhTree = NeighborhoodManager.getNeighborhoodTree(connection, userId);


		if ( nhId != -1 )
		{
		   nm = NeighborhoodManager.getNeighborhoodNameById(connection, nhId);
		   nhRel = NeighborhoodManager.getNeighborhoodRelations(connection,nhId);
		}

		req.setAttribute("nhId",new Integer(nhId));

		if ( nm != null )
		{
			req.setAttribute("nhIdName", nm.name);
			req.setAttribute("parentLevel", new Integer(nm.nh.getLevels()));
		}

		if ( nhRel != null )
		req.setAttribute("nhRel",nhRel);

		req.setAttribute("nhTitle","Neighborhood Details");
		req.setAttribute("nhTree", nhTree);

		// forward the request
		sc.getRequestDispatcher("/jsp/admin/nh_reportt.jsp").forward(req,res);

		} catch (ServletException se) {
		   se.printStackTrace();
		} catch (IOException ie) {
		   ie.printStackTrace();
		}
		catch (SQLException sqe)
		{
		   sqe.printStackTrace();
		}
		catch (SystemException  syse)
		{
					  syse.printStackTrace();
		}
		finally
		{
		   try {
			   connection.close();
		   } catch (Exception e) {
			   e.printStackTrace();
		   }
		}

    }

public void newRelation()
       {
   	// get the nh tree heirarchy from the database
           Connection connection = null;
           HttpSession hs = req.getSession(true);
		   int userId = ((Integer)hs.getAttribute("userId")).intValue();
		   int memberid = ((Integer)hs.getAttribute("memberId")).intValue();

			String selNhid = req.getParameter ("selNhid");
		//	System.out.println("Selected Nhid =" + selNhid);

		   	int nhId  =  selNhid == null ? -1: Integer.parseInt( selNhid );

		   	if (nhId == 0 || nhId == -1)
		   	{
				Integer sessionNhId = (Integer)hs.getAttribute("nhId");
				nhId =sessionNhId != null ?  sessionNhId.intValue() : -1;
			}


           try
           {
               DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
               connection = databaseloader.getConnection();
               NhName nm = null;
			   Vector nhRel = null;

				if ( nhId != -1 )
				{
				   nm = NeighborhoodManager.getNeighborhoodNameById(connection, nhId);
				   nhRel = NeighborhoodManager.getNeighborhoodRelations(connection,nhId);
		   		}

               Vector nhTree = NeighborhoodManager.getNeighborhoodTree(connection, userId);

                req.setAttribute("nhId",new Integer(nhId));
               	req.setAttribute("parentName", nm.name);
			    req.setAttribute("parentLevel", new Integer(nm.nh.getLevels()));
			    req.setAttribute("nhTitle","Neighborhood Details");
	            req.setAttribute("nhRel",nhRel);
                req.setAttribute("nhTree", nhTree);
                req.setAttribute("relName", "");

               // forward the request
               sc.getRequestDispatcher("/jsp/admin/rel_editor.jsp").forward(req,res);
           } catch (ServletException se) {
               se.printStackTrace();
           } catch (IOException ie) {
               ie.printStackTrace();
           }
           catch (SQLException sqe)
           {
               sqe.printStackTrace();
           }
           catch (SystemException  syse)
		   {
		                  syse.printStackTrace();
           }
           finally {
                   try {
                       connection.close();
                   } catch (Exception e) {
                       e.printStackTrace();
                   }
               }

    }
public void editRelation()
       {
   	// get the nh tree heirarchy from the database
           Connection connection = null;
           HttpSession hs = req.getSession(true);
		   int userId = ((Integer)hs.getAttribute("userId")).intValue();
		   int memberid = ((Integer)hs.getAttribute("memberId")).intValue();

			String selNhid = req.getParameter ("selNhid");
			String selectedRelation = req.getParameter("selectedRelation");

		//	System.out.println("Selected Nhid =" + selNhid);
		//	System.out.println(" editRelation::selectedRelation =" + selectedRelation);


		   	int nhId  =  selNhid == null ? -1: Integer.parseInt( selNhid );

		   	if (nhId == 0 || nhId == -1)
		   	{
				Integer sessionNhId = (Integer)hs.getAttribute("nhId");
				nhId =sessionNhId != null ?  sessionNhId.intValue() : -1;
			}


           try
           {
               DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
               connection = databaseloader.getConnection();
               NhName nm = null;
			   Vector nhRel = null;

				if ( nhId != -1 )
				{
				   nm = NeighborhoodManager.getNeighborhoodNameById(connection, nhId);
				   nhRel = NeighborhoodManager.getNeighborhoodRelations(connection,nhId);
		   		}

               Vector nhTree = NeighborhoodManager.getNeighborhoodTree(connection, userId);

                req.setAttribute("nhId",new Integer(nhId));
               	req.setAttribute("parentName", nm.name);
			    req.setAttribute("parentLevel", new Integer(nm.nh.getLevels()));
			    req.setAttribute("nhTitle","Neighborhood Details");
	            req.setAttribute("nhRel",nhRel);
                req.setAttribute("nhTree", nhTree);
                req.setAttribute("relName", "");
                req.setAttribute("selectedRelation", selectedRelation);

               // forward the request
               sc.getRequestDispatcher("/jsp/admin/rel_editor.jsp").forward(req,res);
           } catch (ServletException se) {
               se.printStackTrace();
           } catch (IOException ie) {
               ie.printStackTrace();
           }
           catch (SQLException sqe)
           {
               sqe.printStackTrace();
           }
           catch (SystemException  syse)
		   {
		                  syse.printStackTrace();
           }
           finally {
                   try {
                       connection.close();
                   } catch (Exception e) {
                       e.printStackTrace();
                   }
               }

    }


    public void deleteRelation() throws ServletException, IOException
	       {
	   	// get the nh tree heirarchy from the database
	           Connection connection = null;
	           TransactionManager tm = null;
	           int tid = -1;
	           HttpSession hs = req.getSession(true);
			   int userId = ((Integer)hs.getAttribute("userId")).intValue();
			   int memberid = ((Integer)hs.getAttribute("memberId")).intValue();

				String selNhid = req.getParameter ("selNhid");
				String selectedRelation = req.getParameter("selectedRelation");

		//		System.out.println("Selected Nhid =" + selNhid);
		//		System.out.println(" deleteRelation::selectedRelation =" + selectedRelation);


			   	int nhId  =  selNhid == null ? -1: Integer.parseInt( selNhid );

			   	if (nhId == 0 || nhId == -1)
			   	{
					Integer sessionNhId = (Integer)hs.getAttribute("nhId");
					nhId =sessionNhId != null ?  sessionNhId.intValue() : -1;
				}


	           try
	           {
	               DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
	               connection = databaseloader.getConnection();
	                tm = new TransactionManager( connection, userId);
            		tid = tm.startTransaction();

	               NeighborhoodManager.deleteRelation( connection, nhId, selectedRelation, tid );

	               tm.commitTransaction();




	           }
	           catch (SQLException sqe)
	           {
	               sqe.printStackTrace();
	             try
				   {
							tm.rollbackTransaction();
					}
					catch( SQLException se )
					{
							se.printStackTrace();
					}


	           }
	           catch (SystemException  sysex)
			   {
			                  sysex.printStackTrace();
			                  try
							  				   {
							  							tm.rollbackTransaction();
							  					}
							  					catch( SQLException sysex1 )
							  					{
							  							sysex1.printStackTrace();
					}


	           }
	            catch (NeighborhoodException  nbe)
			   			   {
			   			                  nbe.printStackTrace();
			   			                  try
			   							  				   {
			   							  							tm.rollbackTransaction();
			   							  					}
			   							  					catch( SQLException sysex2 )
			   							  					{
			   							  							sysex2.printStackTrace();
			   									}


	           }
	           finally
	           {
	                   try
	                   {
						   if ( connection != null )
	                       				connection.close();
	                   } catch (Exception e) {
	                       e.printStackTrace();
	                   }
	               }

	 			// forward the request



		     String redirectURL = "/BW_Neighborhoods?selNhid="+ Integer.parseInt(selNhid) + "&action=nhReport";
			  res.sendRedirect(req.getContextPath() + redirectURL);
    }


    /*
	 *  Puts out a report of the neighborhoods in your control, and their
	 *  heirarchy.
	 */
	// get the nh tree heirarchy from the database
	    public void setnhDetails()
	    {
	      Connection connection = null;
	      int nhid  = Integer.parseInt(req.getParameter ("selNhid"));

	      req.setAttribute("nhId",new Integer(nhid));
	      try
	      {
	           DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
	           connection = databaseloader.getConnection();

	           NhName nm = null;
	           nm = NeighborhoodManager.getNeighborhoodNameById(connection, nhid);

	           req.setAttribute("parentName", nm.name);
	           req.setAttribute("parentLevel", new Integer(nm.nh.getLevels()));
	           Vector nhTree = NeighborhoodManager.getNeighborhoodTree(connection, userId);
	           Vector nhRel = NeighborhoodManager.getNeighborhoodRelations(connection,nhid);
	           req.setAttribute("nhTitle","Neighborhood Details");
	           req.setAttribute("nhTree", nhTree);
	           req.setAttribute("nhRel",nhRel);
	        // forward the request
	           sc.getRequestDispatcher("/jsp/admin/nh_details.jsp").forward(req,res);
	      } catch (ServletException se) {
	        se.printStackTrace();
	      } catch (IOException ie) {
	        ie.printStackTrace();
	      }
	      catch (SQLException sqe)
	      {
	        sqe.printStackTrace();
	      }
	      catch (SystemException e)
	      {
	        e.printStackTrace();
	      }
	      finally {
	            try {
	                connection.close();
	            } catch (Exception e) {
	                e.printStackTrace();
	            }
	      }
    }

        public void nhCommitRelations() throws ServletException, IOException
	        {

				Connection connection = null;
				TransactionManager tm = null;

				int nhId = -1;
				Vector targetNhIds = new Vector();

	            String selNhid = req.getParameter("selNhid");
	            String selectedRelation =  req.getParameter("selectedRelation");
	            String relName = req.getParameter("relName");


	           if  ( selNhid != null && ! selNhid.trim().equals("") )
	           	           nhId = Integer.parseInt(selNhid);


	//            System.out.println("selNhid = " + selNhid + " selectedRelation = " + selectedRelation + "  relationname = " + relName );


				try
				{



				for (Enumeration en=req.getParameterNames();  en.hasMoreElements(); )
				    {
				            String name = (String)en.nextElement();
				            String value = req.getParameter(name);
				//            System.out.println(">>>>>> passed name: " + name + " value: " + value);

				            int index = name.lastIndexOf("selNH");

				            if ( index != -1 && index== 0 && ! name.equals("selNhid")  )
				            {
								targetNhIds.add(new Integer(Integer.parseInt(value)));
							}
				}

		/*		for ( int n = 0 ; n < targetNhIds.size() ; n++ )
				{
					System.out.println(" selected nhid = " + (String)targetNhIds.elementAt(n) );

				} */

				DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
	           	connection = databaseloader.getConnection();
				 tm = new TransactionManager( connection, userId);

				int tid = tm.startTransaction();

		//		System.out.println("selectedRelation=" + selectedRelation );

				if   ( selectedRelation != null && ! selectedRelation.trim().equals("") )
				{
			//		System.out.println(" Updating existing  relation " + selectedRelation );
					NeighborhoodManager.updateNewRelation( connection,nhId,relName, targetNhIds,tid);
				}
				else
				{
		//				System.out.println(" Adding new relation " + relName );
						NeighborhoodManager.addNewRelation( connection,nhId,relName, targetNhIds,tid);
				}




			   NhName nm = null;
			   Vector nhRel = null;

				if ( nhId != -1 )
				{
				   nm = NeighborhoodManager.getNeighborhoodNameById(connection, nhId);
				   nhRel = NeighborhoodManager.getNeighborhoodRelations(connection,nhId);
				}



			   Vector nhTree = NeighborhoodManager.getNeighborhoodTree(connection, userId);

				req.setAttribute("nhId",new Integer(nhId));
				req.setAttribute("parentName", nm.name);
				req.setAttribute("parentLevel", new Integer(nm.nh.getLevels()));
				req.setAttribute("nhTitle","Neighborhood Details");
				req.setAttribute("nhRel",nhRel);
				req.setAttribute("nhTree", nhTree);
				req.setAttribute("relName", "");
				req.setAttribute("selectedRelation", selectedRelation);
				req.setAttribute("selNhid",selNhid);
				req.setAttribute("relName",relName);

				tm.commitTransaction();

	            }
	            catch( Exception e )
	            {		try
	            	{
							if (tm != null) {
								tm. rollbackTransaction();
							}
						}
						catch(SQLException sqe )
						{
							sqe.printStackTrace();
						}
							e.printStackTrace();
				}
	            finally
	            {
	                try
	                {
						if ( connection != null )
	                    	connection.close();
	                }
	                catch ( SQLException sql )
	                {
	                    sql.printStackTrace();
	                }
	            }
	           	// forward the request
	            // sc.getRequestDispatcher("/jsp/admin/rel_editor.jsp").forward(req,res);

				 String redirectURL = "/BW_Neighborhoods?selNhid="+ Integer.parseInt(selNhid)+ "&action=nhReport";
			  	res.sendRedirect(req.getContextPath() + redirectURL);



	    }


    // Handle the get methods here, forward the request and response to
    // the post method to handle
    public void doGet (HttpServletRequest request,
	    HttpServletResponse response)throws ServletException, IOException

    {
	doPost(request, response);
    }




}
