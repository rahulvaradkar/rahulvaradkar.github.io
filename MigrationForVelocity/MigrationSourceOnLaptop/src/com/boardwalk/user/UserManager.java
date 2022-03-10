package com.boardwalk.user;

import com.boardwalk.database.DatabaseLoader;
import java.util.*;
import java.io.*;
import com.boardwalk.database.*;
import com.boardwalk.exception.SystemException;
import com.boardwalk.member.*;

import java.sql.*;                  // JDBC package
import javax.sql.*;                 // extended JDBC package
import com.boardwalk.util.*;
import com.boardwalk.exception.BoardwalkException;

public class UserManager
{
	private static String CALL_BW_UPD_USER_PASSWORD = "{ CALL BW_UPD_USER_PASSWORD(?,?)}";
    private static String CALL_BW_CR_GROUP="{CALL BW_CR_GROUP(?,?,?,?,?,?)}";
    private static String CALL_BW_CR_USER="{CALL BW_CR_USER(?,?,?,?,?,?,?)}";
    private static String BW_GET_GROUP="{SELECT BW_GROUP.ID AS ID, NAME,EXTERNAL_GROUP_ID,IS_SECURE,EMAIL_ADDRESS FROM BW_GROUP,BW_USER WHERE BW_GROUP.ID = ? AND BW_GROUP.MANAGED_BY = BW_USER.ID }";
	private static String BW_GET_USER_BY_USERNAME="SELECT * FROM BW_USER WHERE BW_USER.EMAIL_ADDRESS=?";
    private static String BW_GET_USER_BY_ID="select * from BW_USER where BW_USER.ID=?";
    private static String BW_AUTHENTICATE_USER="select ID,ACTIVE from BW_USER where BW_USER.EMAIL_ADDRESS=? AND BW_USER.PASSWORD=?";
    private static String BW_AUTHENTICATE_PASSWORD="select ID, PASSWORD from BW_USER where BW_USER.ID=?";
	//private static String BW_AUTHENTICATE_PASSWORD="select ID from BW_USER where BW_USER.ID=? AND BW_USER.PASSWORD=?";
	private static String CALL_BW_GET_USER_PROFILE = "{CALL BW_GET_USER_PROFILE(?)}";
    // oracle fix - added additional param for output cursor - shirish 20150724
	//private static String CALL_BW_GET_USER_PROFILE = "{CALL BW_GET_USER_PROFILE(?,?)}";
	private static String CALL_BW_UPD_USER_PROFILE = "{CALL BW_UPD_USER_PROFILE(?,?,?,?,?)}";
	private static String BW_GET_USER_LIST="select * from BW_USER";
	private static String BW_AUTHENTICATE_MEMBER=" select BW_USER.ID, BW_NH.ID, BW_NH.NAME " +
												 " from BW_USER, BW_MEMBER, BW_NH " +
												 " where BW_USER.EMAIL_ADDRESS=? " +
												 " AND BW_NH.ID= BW_MEMBER.NEIGHBORHOOD_ID " +
												 //" AND BW_USER.PASSWORD=? " +
												 " AND BW_MEMBER.USER_ID = BW_USER.ID " +
												 " AND BW_MEMBER.ID = ? ";

	private static String BW_CHECK_USER_MEMBERSHIP = "{ CALL BW_CHECK_USER_MEMBERSHIP(?,?,?,?)}";	//Added to get the Membership ID in case of Multiple Membership
	private static String CALL_BW_CHECK_USER = "{ CALL BW_CHECK_USER(?,?)}";
	private static String CALL_BW_UPD_USER_LOGIN_SUCCESS = "{CALL BW_UPD_USER_LOGIN_SUCCESS(?)}";
	private static String CALL_BW_UPD_USER_ACC_LOCK = "{CALL BW_UPD_USER_ACC_LOCK(?,?)}";
	private static String FORCE_CHANGE_PASSWORD = "UPDATE BW_USER SET PASSWORD_CHANGED_ON = CREATED_ON WHERE ID = ?";
	private static String BW_GET_SYSTEM_CONFIGURATION = "{CALL BW_GET_SYSTEM_CONFIGURATION(?)}"; //Changes related to Login Enhancements for Password Complexity and User Authentication on 20180323
	private static String CALL_BW_CREATE_PASSWORD_HISTORY = "{CALL BW_CREATE_PASSWORD_HISTORY(?)}"; //Added by Lakshman on 20180323 to fix the Issue Id: 14248
	private static String CALL_BW_GET_PASSWORD_HISTORY = "{CALL BW_GET_PASSWORD_HISTORY(?)}"; //Added by Lakshman on 20180323 to fix the Issue Id: 14248

	///////////////////////////////////////////////////////////////////////////////
	///// Authenticate User and member
	///////////////////////////////////////////////////////////////////////////////

	public static boolean isUserAccessPresent(Connection a_connection)
	{
		Connection connection3 = a_connection;
		Statement stmt	= null;
		ResultSet rs	= null;
		boolean check_access = false;
		PreparedStatement preparedstatement = null;
		
		try
		{
//			DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
//			connection3 = databaseloader.getConnection();
			String a_table_name = "_BW_USER_ACCESS";
			String lsquery = "SELECT COUNT(*)  FROM BW_TBL WHERE NAME = '" + a_table_name + "' ";
			stmt = connection3.createStatement();
			rs = stmt.executeQuery(lsquery);
			int table_count = 0;
			
			while (rs.next())
			{
				table_count = rs.getInt(1);
			}
			//System.out.println(" ------------Value of count ---------"+table_count);
			if (table_count > 0)
				check_access = true;
			//hs.setAttribute("check_access", check_access);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
        finally {
            try
            {
				if(rs != null)
					rs.close();
            }
            catch( Exception sql ) {
                sql.printStackTrace();
			}
            try
            {
				if(stmt != null)
					stmt.close();
            }
            catch( Exception sql ) {
                sql.printStackTrace();
			}
		}
		return check_access;
	}


	static public HashMap get_priviliges_for_user(String userName, String nhName)
	{

		PreparedStatement preparedstatement = null;
		ResultSet resultSet = null;
		Connection connection = null;
		String CallStmt = null;
		boolean addNewUser = false;
		boolean deactivateuser = false;
		boolean addMember = false;
		boolean deleteMember = false;
		CallableStatement callableStatement = null;
		HashMap userMgmtAccess = new HashMap();
		try
		{
			//System.out.println("*********** inside get_priviliges_for_user ********" + userName);
			System.out.println("*********** inside get_priviliges_for_user ********" + nhName);

			DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
			connection = databaseloader.getConnection();
			preparedstatement = connection.prepareStatement("{CALL BW_GET_ACCESS_PRIVILIGE_FOR_USER(?,?)}");
			preparedstatement.setString(1, userName);
			preparedstatement.setString(2, nhName);
			resultSet = preparedstatement.executeQuery();
			System.out.println("DatabaseLoader.getDatabaseType().trim().equalsIgnoreCase "+resultSet);				
			while (resultSet.next())
			{
				System.out.println("*********** INSIDE WHILE NEXT LLOP ********");	
				String name = resultSet.getString(3);
				String Value = resultSet.getString(2);
				
				name = name.trim();
				//System.out.println("***********value of name*********" + name + "   Value "+Value);
				if (name.equals("Add New User"))
				{
					String addnew_user = resultSet.getString("STRING_VALUE");
					System.out.println("Add New User = " + addnew_user);
					if (addnew_user != null)
					{
						if (addnew_user.trim().equalsIgnoreCase("Y"))
						{
							userMgmtAccess.put(name, new Boolean(true));
						}
						else
						{
							userMgmtAccess.put(name, new Boolean(false));
						}
					}

				}
				else if (name.equals("Deactivate User"))
				{
					String deactivate_user = resultSet.getString("STRING_VALUE");
					if (deactivate_user != null)
					{
						if (deactivate_user.trim().equalsIgnoreCase("Y"))
						{
							userMgmtAccess.put(name, new Boolean(true));
						}
						else
						{
							userMgmtAccess.put(name, new Boolean(false));
						}
					}
				}
				else if (name.equals("Add Member"))
				{
					String add_member = resultSet.getString("STRING_VALUE");

					if (add_member != null)
					{
						if (add_member.trim().equalsIgnoreCase("Y"))
						{
							userMgmtAccess.put(name, new Boolean(true));
						}
						else
						{
							userMgmtAccess.put(name, new Boolean(false));
						}
					}
				}
				else if (name.equals("Delete Member"))
				{
					String delete_member = resultSet.getString("STRING_VALUE");

					if (delete_member != null)
					{
						if (delete_member.trim().equalsIgnoreCase("Y"))
						{
							userMgmtAccess.put(name, new Boolean(true));
						}
						else
						{
							userMgmtAccess.put(name, new Boolean(false));
						}
					}
				}
			}
		
		if (resultSet != null )
			resultSet.close();
		 
		if (preparedstatement != null )
			preparedstatement.close();

		if (connection != null )
		connection.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return userMgmtAccess;
	}

    static public Member authenticateMember(Connection a_connection, String userEmail, int memberId) { //Modified by Tekvision on 20180207 for Clear Text Password(Issue Id: 14241)
        PreparedStatement preparedstatement = null;
        ResultSet rs = null;
        int userId = -1;
        int nhId = -1;
        String nhName= "";
		Member memberObj = null;
        try {

            preparedstatement = a_connection.prepareStatement(BW_AUTHENTICATE_MEMBER);

            preparedstatement.setString(1,userEmail);
            //preparedstatement.setString(2,userEmail); // User can now be verified based on either loginid or email
            //PasswordService ps = PasswordService.getInstance(); //Modified by Tekvision on 20180207 for Clear Text Password(Issue Id: 14241)
            //String encryptedPassword = ps.encrypt( password ); Authentication should be done in the filter
            //preparedstatement.setString(2,encryptedPassword);
            preparedstatement.setInt(2,memberId);
            rs = preparedstatement.executeQuery();
            if ( rs.next() ) {
                userId = rs.getInt(1);
                nhId = rs.getInt(2);
                nhName = rs.getString(3);
				//memberObj = new Member(memberId,userId,nhId,nhName,"","");
				memberObj = new Member(memberId,userId,nhId,nhName);
            }
        }
        catch( Exception e ) {
            e.printStackTrace();
        }
        finally {
            try
            {
                rs.close();
                preparedstatement.close();
            }
            catch( Exception sql ) {
                sql.printStackTrace();
            }
        }

		return memberObj;
    }


	/////////////////////////////////////////////////////////////////////////////
	// List of all users in the Boardwalk System
	////////////////////////////////////////////////////////////////////////////
	static public Vector getUserList(Connection a_connection)
	{
		PreparedStatement preparedstatement = null;
		ResultSet rs = null;
		Vector userList = new Vector();
		try {
			preparedstatement = a_connection.prepareStatement(BW_GET_USER_LIST);
			rs = preparedstatement.executeQuery();
			while ( rs.next() ) {
				int id = rs.getInt("ID");
				String userName = rs.getString("EMAIL_ADDRESS");
				//String userName = rs.getString("EXTERNAL_USER_ID"); // get email address - shirish 8/4/2015
				//String userEmail = rs.getString("EMAIL_ADDRESS"); // get email address - shirish 8/4/2015
				String userEmail = rs.getString("EXTERNAL_USER_ID"); 
				String firstName = rs.getString("FIRSTNAME");
				String lastName = rs.getString("LASTNAME");
				int isActive = rs.getInt("ACTIVE");			// Added by Rahul Varadkar on 08-August-2015
				//System.out.println(id + "  " +  userName + "  " +   userEmail + "  " +   firstName + "  " +   lastName + "  " +   isActive);
				//userList.addElement(new User(id, userName, firstName, lastName));
				userList.addElement(new User(id, userName, userEmail, firstName, lastName, isActive)); //  added email address shirish 8/4/2015 
				
				//userList.addElement(new User(id, userName, firstName, lastName, isActive)); //  added email address shirish 8/4/2015 
				// isActive Added by Rahul Varadkar on 08-August-2015
			}
		}
		catch( Exception e ) {
			e.printStackTrace();
		}
		finally {
			try
			{
				rs.close();
				preparedstatement.close();
			}
			catch( Exception sql ) {
				sql.printStackTrace();
			}
		}

		return userList;
	}
//////////////////////////////////////////////////////////////////////////////////////
// change the profile
/////////////////////////////////////////////////////////////////////////////////////
	static public void updateProfile( Connection a_connection,
										int userId,
										String firstName,
										String lastName,
										String emailAddress,
										String alias)
	throws BoardwalkException
	{

		int m_user_id = -1;

		CallableStatement callablestatement = null;

		try
		{
			// then update the user with the new password
			callablestatement = a_connection.prepareCall(CALL_BW_UPD_USER_PROFILE);
			callablestatement.setInt(1, userId);
			callablestatement.setString(2, firstName);
			callablestatement.setString(3, lastName);
			callablestatement.setString(4, emailAddress);
			callablestatement.setString(5, alias);
			int l = callablestatement.executeUpdate();

		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if (callablestatement != null)
				{
					callablestatement.close();
				}

			}
			catch( SQLException sql )
			{
				sql.printStackTrace();
			}
		}

    }

	//////////////////////////////////////////////////////////////////////////////////////
	// Change the Password
	/////////////////////////////////////////////////////////////////////////////////////
	static public void updatePassword(	Connection a_connection,
										int userId,
										String oldPassword,
										String newPassword)
	throws BoardwalkException
	{
		int					m_user_id					= -1;
		String				m_encrypted_pwd				= null;
		CallableStatement	callablestatement			= null;
		PreparedStatement	preparedstatement			= null;
		PreparedStatement	preparedStatementHistory	= null;
		ResultSet			rs							= null;
		ResultSet			rsHistory					= null;
		String				encryptedHistoryPassword	= "";
		boolean				matchPasswordHistory		= false;
		PasswordService		ps							= PasswordService.getInstance();

		try
		{
			//First Authenticate the Old Password for the User
			preparedstatement = a_connection.prepareStatement(BW_AUTHENTICATE_PASSWORD);
			preparedstatement.setInt(1,userId);

			//Commented for PBKDF2 Encryption - Shirish 20150724
			//String encryptedOldPassword = ps.encrypt(oldPassword );
			//preparedstatement.setString(2,encryptedOldPassword);
			rs = preparedstatement.executeQuery();
			
			if (rs.next())
			{
				m_user_id		= rs.getInt("ID");
				m_encrypted_pwd	= rs.getString("PASSWORD"); //Added for PBKDF2 Encryption - Shirish 20150724
			}
			preparedstatement.close();
			preparedstatement = null;
			
			//Added for PBKDF2 Encryption - Shirish 20150724
			if(!ps.validatePassword(oldPassword, m_encrypted_pwd))
				m_user_id = -1;
		}
		catch( Exception e ) {
			if ( e.getClass().getName().equals("com.boardwalk.exception.BoardwalkException") )
			{
				throw (BoardwalkException)e;
			}
			else
			{
				e.printStackTrace();
			}
		}

		try
		{
			if (m_user_id == -1)
			{
				System.out.println("UserManager::updatePassword()->The Old Password is not correct");
				throw new BoardwalkException(10009);
			}
			//Modified by Lakshman on 20180323 to fix Issue Id: 14248 - START
			else
			{
				preparedStatementHistory = a_connection.prepareStatement(CALL_BW_GET_PASSWORD_HISTORY);
				preparedStatementHistory.setInt(1,userId);
				rsHistory = preparedStatementHistory.executeQuery();

				while (rsHistory.next())
				{
					encryptedHistoryPassword = rsHistory.getString("PASSWORD");

					if(ps.validatePassword(newPassword, encryptedHistoryPassword))
					{
						matchPasswordHistory = true;
						break;
					}
				}

				if(matchPasswordHistory)
				{
					System.out.println("UserManager:updatePassword: New Password Matched with One of the Old Passwords");
					throw new BoardwalkException(11021);
				}
				else
				{
					//Update the New Password
					callablestatement = a_connection.prepareCall(CALL_BW_UPD_USER_PASSWORD);
					callablestatement.setInt(1, userId);
					String encryptedNewPassword = ps.encrypt(newPassword);
					callablestatement.setString(2, encryptedNewPassword);
					int l = callablestatement.executeUpdate();
				}

				preparedStatementHistory.close();
				preparedStatementHistory = null;
			}
			//Modified by Lakshman on 20180323 to fix Issue Id: 14248 - END

			callablestatement.close();
			callablestatement = null;
		}
		catch( Exception e )
		{
			if ( e.getClass().getName().equals("com.boardwalk.exception.BoardwalkException") )
			{
				throw (BoardwalkException)e;
			}
			else
			{
				e.printStackTrace();
			}
		}

		finally
		{
			try
			{
				if (callablestatement != null)
				{
					callablestatement.close();
				}

				if (preparedstatement != null)
				{
					preparedstatement.close();
				}

				if (preparedStatementHistory != null)
				{
					preparedStatementHistory.close();
				}
			}
			catch( SQLException sql )
			{
				sql.printStackTrace();
			}
		}
    }
//////////////////////////////////////////////////////////////////////////////////////
// change the password if user has forgot the password
/////////////////////////////////////////////////////////////////////////////////////
	static public boolean updatePassword( Connection a_connection,
										String userName,
										String newPassword)
	throws BoardwalkException
	{
		int m_user_id = -1;
		boolean success = true;

		CallableStatement callablestatement = null;
		PreparedStatement preparedstatement = null;
		ResultSet rs = null;
		PasswordService ps = PasswordService.getInstance();

        try
		{
			// first get the user id
            preparedstatement = a_connection.prepareStatement(BW_GET_USER_BY_USERNAME);
            preparedstatement.setString(1,userName);
            //preparedstatement.setString(2,userName);
            rs = preparedstatement.executeQuery();
            if ( rs.next() )
			{
                m_user_id = rs.getInt("ID");
            }
        }
        catch( Exception e )
		{
			success = false;
            e.printStackTrace();
        }
        finally {
            try
            {
				rs.close();
                preparedstatement.close();
            }
            catch( Exception sql ) {
                sql.printStackTrace();
            }
        }


		try
		{
			if (m_user_id == -1)
			{
				System.out.println("UserManager::updatePassword()->The User Name is not correct");
				throw new BoardwalkException(11004);
			}
			else
			{
				// then update the user with the new password
				callablestatement = a_connection.prepareCall(CALL_BW_UPD_USER_PASSWORD);
				callablestatement.setInt(1, m_user_id);
				String encryptedNewPassword = ps.encrypt(newPassword );
				callablestatement.setString(2, encryptedNewPassword);
				int l = callablestatement.executeUpdate();
			}

			callablestatement.close();
			callablestatement = null;
		}
		catch( Exception e )
		{
			success = false;
			if ( e.getClass().getName().equals("com.boardwalk.exception.BoardwalkException") )
			{
				System.out.println("Here 1");
				throw (BoardwalkException)e;
			}
			else
			{
				e.printStackTrace();
			}
		}

		finally
		{
			try
			{
				if (callablestatement != null)
				{
					callablestatement.close();
				}

				if (preparedstatement != null)
				{
					preparedstatement.close();
				}
			}
			catch( SQLException sql )
			{
				sql.printStackTrace();
			}
		}
		return success;
    }

//////////////////////////////////////////////////////////////////////////////////////

//////////////////////////////////////////////////////////////////////////////////////
// get the user profile
/////////////////////////////////////////////////////////////////////////////////////
	static public NewUser getUserProfile(Connection a_connection, int userId) {
        PreparedStatement preparedstatement = null;
		CallableStatement callablestatement = null;
        ResultSet rs = null;
        NewUser nu = null;
        /*
        	EMAIL_ADDRESS,
		    EXTERNAL_USER_ID,
		    PASSWORD,
		    FIRSTNAME,
    		LASTNAME
    	*/
        try {
            preparedstatement = a_connection.prepareStatement(CALL_BW_GET_USER_PROFILE);
            preparedstatement.setInt(1,userId);
            rs = preparedstatement.executeQuery();
            if ( rs.next() ) {
                String a_address = rs.getString("EMAIL_ADDRESS");
                String a_externalUserId = rs.getString("EXTERNAL_USER_ID");
                String a_password = rs.getString("PASSWORD");
                String a_fname = rs.getString("FIRSTNAME");
                String a_lname = rs.getString("LASTNAME");

  				nu = new NewUser(a_address,a_externalUserId, a_password, a_fname, a_lname, 1);
            }
        }
        catch( Exception e ) {
            e.printStackTrace();
        }
        finally {
            try
            {
				rs.close();
                preparedstatement.close();
            }
            catch( Exception sql ) {
                sql.printStackTrace();
            }
        }

        return nu;
    }
/////////////////////////////////////////////////////
////////////////////////////////////////////////////
    static public Hashtable getMembershipsForUser(Connection connection, int userId)
	throws SystemException
    {
		String CALL_BW_MEMBERSHIPS_FOR_USER = "{CALL BW_GET_MEMBERSHIPS_FOR_USER(?)}";
        Hashtable ht = null;
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;

        System.out.println(DatabaseLoader.getDatabaseType() + "DBTYPE ");
        System.out.println("Inside getMembershipsForUser() : userId = " + userId);
		try {
			preparedStatement = connection.prepareStatement(CALL_BW_MEMBERSHIPS_FOR_USER);
			preparedStatement.setInt(1, userId);

            rs = preparedStatement.executeQuery();
            ht = new Hashtable();
            while (rs.next()) {
                int memberId = rs.getInt("ID");
                String nhName = rs.getString("NEIGHBORHOOD_NAME");
                int nhid = rs.getInt("NEIGHBORHOOD_ID");
				ht.put(new Integer(memberId),new Member(memberId, userId,nhid ,nhName));
				//ht.put(new Integer(memberId),new Member(memberId, userId,nhid ,nhName,"",""));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
				rs.close();
                preparedStatement.close();
            } catch (SQLException sql) {
                throw new SystemException(sql);
            }
        }

        return ht;
    }


    static public Vector  getMembershipListForUser(Connection connection, int userId)
	throws SystemException
	{
			String CALL_BW_MEMBERSHIPS_FOR_USER = "{CALL BW_GET_MEMBERSHIPS_FOR_USER(?)}";
	        Vector ht = new Vector();
	        PreparedStatement preparedStatement = null;
	        ResultSet rs = null;
	        System.out.println("Inside getMembershipsForUser() : userId = " + userId);
	        try
	        {
				preparedStatement = connection.prepareStatement(CALL_BW_MEMBERSHIPS_FOR_USER);
				preparedStatement.setInt(1, userId);

	            rs = preparedStatement.executeQuery();
	            while (rs.next())
	            {
	                int memberId = rs.getInt("ID");
	                String nhName = rs.getString("NEIGHBORHOOD_NAME");
	                int nhid = rs.getInt("NEIGHBORHOOD_ID");
	                ht.addElement(new Member(memberId, userId,nhid ,nhName));
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	        } finally {
	            try {
					rs.close();
	                preparedStatement.close();
	            } catch (SQLException sql) {
	                throw new SystemException(sql);
	            }
	        }

	        return ht;
    }

    // modified to handle PBKDF2 encryption - shirish 20150724
    static public int authenticateUser(Connection a_connection, String userEmail, String password, boolean passwordChange) {
        PreparedStatement preparedstatement = null;
		CallableStatement callablestatement = null;
        ResultSet rs = null;
        int userId = -1;
		String active = "";
		String lsQuery = "";
		System.out.println("userEmail : "+userEmail);
		System.out.println("password : "+password);
		System.out.println("passwordChange : "+passwordChange);

		boolean checkStatus = false;
        try {
			System.out.println("--------Inside UserManager::AunthenticateUser--------");
			checkStatus = isUserAccessPresent(a_connection);
			System.out.println("--------After isuserAccessPresent--------");
			//Changes related to Login Enhancements for Password Complexity and User Authentication on 20170524 - START
			if(checkStatus)
			{
				lsQuery = "SELECT ID, ACTIVE, PASSWORD from BW_USER WHERE BW_USER.EMAIL_ADDRESS=? AND ACTIVE =1";
				preparedstatement = a_connection.prepareStatement(lsQuery);
				preparedstatement.setString(1,userEmail);

				rs = preparedstatement.executeQuery();
			}
			else
			{
				System.out.println("--------checkStatus == false--------");
				//lsQuery = "select ID, PASSWORD from BW_USER where BW_USER.EMAIL_ADDRESS=?";

				callablestatement = a_connection.prepareCall(CALL_BW_CHECK_USER);
				callablestatement.setString(1,userEmail);
				callablestatement.setBoolean(2,passwordChange);
				rs = callablestatement.executeQuery();
			}
			//Changes related to Login Enhancements for Password Complexity and User Authentication on 20170524 - END
			PasswordService ps = PasswordService.getInstance();
			String encryptedPassword = "";
	
			if ( rs.next() ) {
				userId = rs.getInt("ID");
				if(checkStatus)
				{
					active = rs.getString("ACTIVE");
					System.out.println("--------checkStatus == true--------");
				}
				encryptedPassword =  rs.getString("PASSWORD");
			}
			
			// call PasswordService to validate the password set userid= -1 if incorrect password 			- shirish 20150724
			//Changes related to Login Enhancements for Password Complexity and User Authentication on 20170524 - START
			if (!encryptedPassword.equals("Invalid User"))
			{
				if(!ps.validatePassword(password, encryptedPassword))
				{
					System.out.println("Wrong password entered");
					
					if (userId != -7) //Added by Lakshman on 20181011 to fix the Issue Id: 14348
					{
						callablestatement = a_connection.prepareCall(CALL_BW_UPD_USER_ACC_LOCK);
						callablestatement.setInt(1,userId);
						callablestatement.setString(2,"LOCK");
						callablestatement.execute();

						userId = -1;
					}
				}
			}
			//Changes related to Login Enhancements for Password Complexity and User Authentication on 20170524 - END
		}
        catch( Exception e ) {
        	System.out.println("Exception - Inside UserManager.authenticateUser");
			userId = 0;
            e.printStackTrace();
        }
        finally {
            try
            {
                rs.close();
				if(preparedstatement !=null)
					preparedstatement.close();

				if(callablestatement !=null)
					callablestatement.close();
			}
            catch( Exception sql ) {
                sql.printStackTrace();
            }
        }
		System.out.println("value of userId is :::::::::"+userId);
        return userId;
    }

	//Modified by Tekvision on 20180207 for Clear Text Password(Issue Id: 14241) - START
    static public int authenticateUser(Connection a_connection, String userEmail, boolean passwordChange) {
        PreparedStatement preparedstatement = null;
		CallableStatement callablestatement = null;
        ResultSet rs = null;
        int userId = -1;
		String active = "";
		String lsQuery = "";
		boolean checkStatus = false;
        try {
			System.out.println("--------Inside UserManager::AunthenticateUser--------");
			checkStatus = isUserAccessPresent(a_connection);
			System.out.println("--------After isuserAccessPresent--------");
			//Changes related to Login Enhancements for Password Complexity and User Authentication on 20170524 - START
			if(checkStatus)
			{
				lsQuery = "SELECT ID, ACTIVE, PASSWORD from BW_USER WHERE BW_USER.EMAIL_ADDRESS=? AND ACTIVE =1";
				preparedstatement = a_connection.prepareStatement(lsQuery);
				preparedstatement.setString(1,userEmail);

				rs = preparedstatement.executeQuery();
			}
			else
			{
				System.out.println("--------checkStatus == false--------");
				//lsQuery = "select ID, PASSWORD from BW_USER where BW_USER.EMAIL_ADDRESS=?";

				callablestatement = a_connection.prepareCall(CALL_BW_CHECK_USER);
				callablestatement.setString(1,userEmail);
				callablestatement.setBoolean(2,passwordChange);
				rs = callablestatement.executeQuery();
			}
			//Changes related to Login Enhancements for Password Complexity and User Authentication on 20170524 - END
			if ( rs.next() ) {
				userId = rs.getInt("ID");
				if(checkStatus)
				{
					active = rs.getString("ACTIVE");
					System.out.println("--------checkStatus == true--------");
				}
				//encryptedPassword =  rs.getString("PASSWORD");
			
        
			// call PasswordService to validate the password set userid= -1 if incorrect password 			- shirish 20150724
			//Changes related to Login Enhancements for Password Complexity and User Authentication on 20170524 - START			
			}
			//Changes related to Login Enhancements for Password Complexity and User Authentication on 20170524 - END
		}
        catch( Exception e ) {
        	System.out.println("Exception - Inside UserManager.authenticateUser");
			userId = 0;
            e.printStackTrace();
        }
        finally {
            try
            {
                rs.close();
				if(preparedstatement !=null)
					preparedstatement.close();

				if(callablestatement !=null)
					callablestatement.close();
			}
            catch( Exception sql ) {
                sql.printStackTrace();
            }
        }
		System.out.println("value of userId is :::::::::"+userId);
        return userId;
    }
	//Modified by Tekvision on 20180207 for Clear Text Password(Issue Id: 14241) - END

    static public User getUser(Connection a_connection, String userName) {

		System.out.println("Inside UserManager.getUser");
        PreparedStatement preparedstatement = null;
        ResultSet rs = null;
        User nu = null;
		try {
			preparedstatement = a_connection.prepareStatement(BW_GET_USER_BY_USERNAME);
            preparedstatement.setString(1,userName);
            //preparedstatement.setString(2,userName);
            rs = preparedstatement.executeQuery();
			if ( rs.next() ) {
                int id = rs.getInt("ID");
				String firstName = rs.getString("FIRSTNAME");
				String lastName = rs.getString("LASTNAME");
				String Email = rs.getString("EMAIL_ADDRESS");
                int active = rs.getInt("ACTIVE");
	
				if (active == 0)
					id = 0;

				System.out.println("id : " + id);
				System.out.println("userName : " + userName);
				System.out.println("firstName : " + firstName);
				System.out.println("lastName : " + lastName);

				nu = new User(id, userName, firstName, lastName);
            }
        }
        catch( Exception e ) {
            e.printStackTrace();
        }
        finally {
            try
            {
				rs.close();
                preparedstatement.close();
            }
            catch( Exception sql ) {
                sql.printStackTrace();
            }
        }

		System.out.println("end of UserManager.getUser");

        return nu;
    }
	//get user id if the user is deactivated 05-16-2016
	static public User getdeactivatedUser(Connection a_connection, String userName) {
        PreparedStatement preparedstatement = null;
        ResultSet rs = null;
        User nu = null;
		try {
			preparedstatement = a_connection.prepareStatement(BW_GET_USER_BY_USERNAME);
            preparedstatement.setString(1,userName);
            //preparedstatement.setString(2,userName);
            rs = preparedstatement.executeQuery();
			if ( rs.next() ) {
                int id = rs.getInt("ID");
				String firstName = rs.getString("FIRSTNAME");
				String lastName = rs.getString("LASTNAME");
				String Email = rs.getString("EMAIL_ADDRESS");
                int active = rs.getInt("ACTIVE");
	
				//if (active == 0)
					//id = 0;

				nu = new User(id, userName, firstName, lastName);
            }
        }
        catch( Exception e ) {
            e.printStackTrace();
        }
        finally {
            try
            {
				rs.close();
                preparedstatement.close();
            }
            catch( Exception sql ) {
                sql.printStackTrace();
            }
        }

        return nu;
    }


    static public Vector getuserlist(String a_groupName ) {
        return null;
    }

    static public int addGroup( Connection a_connection, NewGroup a_group, NewUser a_user) {

     /* @NAME NVARCHAR(32),
        @EXTERNAL_GROUP_ID  INTEGER,
        @EMAIL_ADDRESS VARCHAR(32),
        @PASSWORD   VARCHAR(32),
        @IS_SECURE BIT,
        @GROUP_ID INTEGER OUTPUTeturn 0;
      */
        int m_group_id = -1;

        CallableStatement callablestatement = null;

        try {
            a_connection.setAutoCommit(false);
            callablestatement = a_connection.prepareCall(CALL_BW_CR_GROUP);
            callablestatement.setString(1,a_group.getName());
            callablestatement.setInt(2,a_group.getExternalId());
            callablestatement.setString(3,a_user.getAddress());
            callablestatement.setString(4,a_user.getEncryptedPassword());
            callablestatement.setInt(5,a_group.getIsSecure());
            callablestatement.registerOutParameter(6,java.sql.Types.INTEGER);
            int l = callablestatement.executeUpdate();
            m_group_id = callablestatement.getInt(6);
            a_connection.commit();
        }
        catch( Exception e ) {
            try {
                a_connection.rollback();
            }
            catch( Exception sql ) {
                sql.printStackTrace();
            }
        }
        finally {
            try {
                callablestatement.close();
            }
            catch( Exception sql ) {
                sql.printStackTrace();
            }
        }

        return m_group_id;
    }

    static public Group getGroup(Connection a_connection, String a_group_name) {
        /*
        PreparedStatement preparedstatement = null;
        ResultSet rs = null;
        com.boardwalk.user.Group g = null;
        try
        {
            a_connection.setAutoCommit(false);
            preparedstatement = a_connection.prepareStatement(BW_GET_GROUP);
            preparedstatement.setString(1,a_group_name);
            rs = preparedstatement.executeQuery();
            if ( rs.next() )
            {
                int m_group_id = rs.getInt("ID");
                int m_external_id = rs.getInt("EXTERNAL_ID");
                String m_group_name = rs
                int    m_is_secure = g.getIsSecure();
                String m_managed_by = g.getManagingUser();
                g = new Group(m_group_id, m_external_id, m_group_name,m_is_secure,m_managed_by );

            }
        }
        catch( Exception e )
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                preparedstatement.close();
                a_connection.close();
            }
            catch( Exception sql )
            {
                sql.printStackTrace();
            }
        }
         */
        return null;

    }

	static public void createUserInNh(Connection a_connection, String fName, String lName, String userName, String password, String nhName, int txid)
	{
//CREATE PROCEDURE BW_CR_USER_IN_NH
//(
//@FNAME AS VARCHAR(256),
//@LNAME AS VARCHAR(256),
//@USERNAME AS VARCHAR(256),
//@PASSWORD AS VARCHAR(256),
//@NHNAME AS VARCHAR(256),
//@USERID AS VARCHAR(256),
//@TX_ID AS VARCHAR(256)
//)
		CallableStatement callablestatement = null;
		try
		{
			PasswordService ps = PasswordService.getInstance();
			callablestatement = a_connection.prepareCall("{CALL BW_CR_USER_IN_NH(?,?,?,?,?,?)}");
			callablestatement.setString(1, fName);
			callablestatement.setString(2, lName);
			callablestatement.setString(3, userName);
			callablestatement.setString(4, ps.encrypt(password));
			callablestatement.setString(5, nhName);
			callablestatement.setInt(6, txid);
			int l = callablestatement.executeUpdate();
		}
		catch (SQLException e)
		{
			e.printStackTrace ();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				callablestatement.close();
			}
			catch (SQLException sql)
			{
				sql.printStackTrace();
			}
		}
	}

    static public int createUser( Connection a_connection, NewUser nu) {
      /*
      @EMAIL_ADDRESS NVARCHAR(32),
      @PASSWORD    NVARCHAR(32),
      @EXTERNAL_USER_ID NVARCHAR(32),
      @GROUP_NAME NVARCHAR(32),
      @USER_ID INTEGER OUTPUT
       */
        int m_user_id = -1;
		//System.out.println("<<<<<<<<<<<<<<<values inside create User>>>>>>>>>>"+nu.getAddress()+"values "+nu.getEncryptedPassword()+"value" +nu.getAddress()+"values" +nu.getFirstName()+"values" +nu.getLastName());
        CallableStatement callablestatement = null;
		PreparedStatement preparedstatement = null; //Added by Lakshman on 20180323 to fix the Issue Id: 14248

        try {
            callablestatement = a_connection.prepareCall(CALL_BW_CR_USER);
            callablestatement.setString(1, nu.getAddress());
            callablestatement.setString(2, nu.getEncryptedPassword());
            //callablestatement.setString(3, nu.getAddress());
            callablestatement.setString(3, nu.getExternalUserId());
            callablestatement.setString(4, nu.getFirstName());
            callablestatement.setString(5, nu.getLastName());
			callablestatement.setInt(6, nu.getIsActive());
            callablestatement.registerOutParameter(7,java.sql.Types.INTEGER);
            int l = callablestatement.executeUpdate();
            m_user_id = callablestatement.getInt(7);

			System.out.println("Inside UserManager.createUser : New user careated " + m_user_id);

			//Added by Lakshman on 20180323 to fix the Issue Id: 14248
			if (m_user_id > -1 )
			{
				preparedstatement = a_connection.prepareStatement(CALL_BW_CREATE_PASSWORD_HISTORY);
				preparedstatement.setInt(1,m_user_id);
				preparedstatement.execute();
			}
		}
        catch( SQLException e ) {
		            return -1;
        }
        catch( Exception e ) {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                callablestatement.close();
				preparedstatement.close();
            }
            catch( SQLException sql )
            {
                sql.printStackTrace();
            }
        }

        return m_user_id;
    }




    static public User getUserbyId( Connection connection, int id) {

       PreparedStatement preparedstatement = null;
        ResultSet rs = null;
        User nu = null;
        try {
            preparedstatement = connection.prepareStatement(BW_GET_USER_BY_ID);
            preparedstatement.setInt(1,id);
            rs = preparedstatement.executeQuery();
            if ( rs.next() ) {
                String userName = rs.getString("EMAIL_ADDRESS");
				//String userName = rs.getString("EXTERNAL_USER_ID");
                String password = rs.getString("PASSWORD");

                nu = new User(id, userName);
                nu.setPassword(password);
            }
        }
        catch( Exception e ) {
            e.printStackTrace();
        }
        finally {
            try
            {
				rs.close();
                preparedstatement.close();
            }
            catch( Exception sql ) {
                sql.printStackTrace();
            }
        }

        return nu;
    }









    static public int getIdByAddress(String a_name, int a_group_id) {

        return -1;
    }


    static public Vector getUsersByGroup( int a_group_id ) {
        return null;

    }

    static public void   deleteGroup( int a_group_id ) {

    }

    static public void   deleteUser( Vector a_usersIds ) {

    }


    ////////////////////////////////////////////////////////////////////////
    /// TEST CODE FOLLOWS //////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////
    public static void TestAddUser( NewUser a_user ) {
        try {
            DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
            Connection connection = databaseloader.getConnection();
            int id = UserManager.createUser(connection,   a_user );
            System.out.println(" User Id is " + id);
        }
        catch( Exception e ) {
            e.printStackTrace();
        }

    }

    public static void TestgetUser( String a_user_name ) {
        try {
            DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
            Connection connection = databaseloader.getConnection();
            User bwuser = UserManager.getUser(connection, "BW_APPLICATION");
            //System.out.println(" Bw user Id " + bwuser.getId() + " Bw user name " + bwuser.getAddress() );
        }
        catch( Exception e ) {
            e.printStackTrace();
        }

    }

    ///////////////////////////////////////////////////////////////
    //Added to get the Membership ID in case of Multiple Membership
    ///////////////////////////////////////////////////////////////
	public static int checkMembershipStatus(Connection connection, int userId, String templateMode, String nhHierarchy, int manifestId) 
	{
        CallableStatement callablestatement = null;
		ResultSet rs 						= null;
		int rv								= -1;

		try 
		{
			System.out.println("UserManager : userId 		= " + userId);
			System.out.println("UserManager : templateMode 	= " + templateMode);
			System.out.println("UserManager : nhHierarchy 	= " + nhHierarchy);
			System.out.println("UserManager : manifestId 	= " + manifestId);
			
			callablestatement = connection.prepareCall(BW_CHECK_USER_MEMBERSHIP);
			callablestatement.setInt(1,userId);
			callablestatement.setString(2,templateMode);
			callablestatement.setString(3,nhHierarchy);
			callablestatement.setInt(4,manifestId);
			
			rs = callablestatement.executeQuery();
			
 			if ( rs.next() ) 
			{
				rv = rs.getInt(1);
				System.out.println("UserManager:checkMembershipStatus: "+rv);
			}
		}

		catch( Exception e ) 
		{
			e.printStackTrace();
		}

		finally 
		{
			try
			{
				rs.close();
				callablestatement.close();
			}
				catch( Exception sql ) {
				sql.printStackTrace();
			}
		}
		return rv;
    }
	
	//Added to update LAST_LOGGED_ON of BW_USER
	static public void userLoginSuccess(Connection a_connection, int userId) {
		CallableStatement callablestatement = null;
		
		try {
			callablestatement = a_connection.prepareCall(CALL_BW_UPD_USER_LOGIN_SUCCESS);
			callablestatement.setInt(1,userId);
			callablestatement.execute();
			
			System.out.println("UserManager:userLoginSuccess: ");
			
		}
		catch( Exception e ) {
			e.printStackTrace();
		}
		finally 
		{
			try
			{
				callablestatement.close();
			}
				catch( Exception sql ) {
				sql.printStackTrace();
			}
		}
	}

    //Added to Unlock an User
	public static int unlockUser(Connection connection, String userName) 
	{
        CallableStatement callablestatement = null;
		int rv								= 0;
		
		try 
		{
			User u = UserManager.getUser(connection, userName);
			
			if (u != null && u.getId() > -1 )
			{
				callablestatement = connection.prepareCall(CALL_BW_UPD_USER_ACC_LOCK);
				callablestatement.setInt(1,u.getId());
				callablestatement.setString(2,"UNLOCK");

				callablestatement.execute();
				rv = 1;
			}

 			System.out.println("UserManager:unlockUser: "+rv);
		}

		catch( Exception e ) 
		{
			e.printStackTrace();
		}

		finally 
		{
			try
			{
				callablestatement.close();
			}
				catch( Exception sql ) {
				sql.printStackTrace();
			}
		}

		return rv;
    }

    //Added to force an User to change the password
	public static int forceChangePassword(Connection connection, String userName) 
	{
		PreparedStatement preparedstatement = null;
		int rv								= 0;
		
		try 
		{
			User u = UserManager.getUser(connection, userName);
			
			if (u != null && u.getId() > -1 )
			{
				preparedstatement = connection.prepareStatement(FORCE_CHANGE_PASSWORD);
				preparedstatement.setInt(1,u.getId());

				preparedstatement.execute();
				rv = 1;
			}

 			System.out.println("UserManager:forceChangePassword: "+rv);
		}

		catch( Exception e ) 
		{
			e.printStackTrace();
		}

		finally 
		{
			try
			{
				preparedstatement.close();
			}
				catch( Exception sql ) {
				sql.printStackTrace();
			}
		}

		return rv;
    }

	//Changes related to Login Enhancements for Password Complexity and User Authentication on 20180323
	public static HashMap<String,String> getSystemConfiguration(Connection connection) 
	{
		System.out.println("Inside UserManager:getSystemConfiguration");
		PreparedStatement		preparedstatement	= null;
		ResultSet				rs					= null;
		HashMap<String,String>	result				= new HashMap<String,String>();

		try 
		{
			preparedstatement = connection.prepareStatement(BW_GET_SYSTEM_CONFIGURATION);
			preparedstatement.setString(1,"SECURITY");
			rs = preparedstatement.executeQuery();
			
			while (rs.next())
			{
				result.put(rs.getString(2),rs.getString(3));
			}
		}

		catch( Exception e ) 
		{
			e.printStackTrace();
		}

		finally 
		{
			try
			{
				preparedstatement.close();
			}
				catch( Exception sql ) {
				sql.printStackTrace();
			}
		}
		return result;
    }
};
