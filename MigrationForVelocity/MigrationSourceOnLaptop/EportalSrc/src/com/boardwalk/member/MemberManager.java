/*
 * MemberManager.java
 *
 * Created on July 5, 2002, 1:44 PM
 */

package com.boardwalk.member;
import javax.servlet.*;
import javax.servlet.http.*;


import com.boardwalk.database.*;
import com.boardwalk.exception.SystemException;
import com.boardwalk.user.*;

import java.sql.*;                  // JDBC package
import javax.sql.*;                 // extended JDBC package
import java.util.Hashtable;
import java.util.Vector;
import java.util.*;

/**
 *
 * @author  Anuradha Kulkarni
 */
public final class MemberManager
{

	private static String CALL_BW_GET_NH_FOR_USER="{CALL BW_GET_NH_FOR_USER(?,?)}";


    static public void deleteMember (Connection connection, int memberId)
    throws SystemException
    {
		System.out.println("------------inside delete mebrerr>>>>>>>>>>>>>>");
        String query = "delete from  BW_MEMBER "+
                       "where  BW_MEMBER.ID = ?";
        PreparedStatement preparedstatement = null;
        try {
            preparedstatement = connection.prepareStatement(query);
            preparedstatement.setInt(1, memberId);
            preparedstatement.executeUpdate();
        }catch(SQLException sqlexception) {
            throw new SystemException(sqlexception);
        }
        finally {
            try {
                preparedstatement.close();
            }
            catch(SQLException sqlexception1) {
                throw new SystemException(sqlexception1);
            }
        }
    }

	static public void  activateUser(int userId)
{	Connection connection = null;
	 PreparedStatement preparedstatement = null;
	String  lsSql ="UPDATE BW_USER SET ACTIVE =1 WHERE ID = ?";
	   try {
			     DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
			    connection = databaseloader.getConnection();
			    preparedstatement = connection.prepareStatement(lsSql);
				preparedstatement.setInt(1, userId);
				 preparedstatement.executeUpdate();
	}
	 catch(SQLException sqlexception) 
		 {
			sqlexception.printStackTrace();
	      }
		  finally
	        {
	            try
	            {
	             
	                preparedstatement.close();
	            }
	            catch(SQLException sqlexception1)
	            {
	                sqlexception1.printStackTrace();
	            }
	        }

}


static public void  activateUser(Connection connection, int userId)
{
	 PreparedStatement preparedstatement = null;
	String  lsSql ="UPDATE BW_USER SET ACTIVE =1 WHERE ID = ?";
	   try {
			   
			    preparedstatement = connection.prepareStatement(lsSql);
				preparedstatement.setInt(1, userId);
				 preparedstatement.executeUpdate();
	}
	 catch(SQLException sqlexception) 
		 {
			sqlexception.printStackTrace();
	      }
		  finally
	        {
	            try
	            {
	             
	                preparedstatement.close();
	            }
	            catch(SQLException sqlexception1)
	            {
	                sqlexception1.printStackTrace();
	            }
	        }

}

  static public void deactivateUser(Connection connection, int userId)
    throws SystemException
    {	
		 
        String query = "UPDATE BW_USER "+
						"SET ACTIVE = 0 "+
                       "WHERE  BW_USER.ID = ?";
        PreparedStatement preparedstatement = null;
        try {
			preparedstatement = connection.prepareStatement(query);
            preparedstatement.setInt(1, userId);
            preparedstatement.executeUpdate();
        }catch(SQLException sqlexception) {
            throw new SystemException(sqlexception);
        }
        finally 
		{
            try 
			{
                preparedstatement.close();
            }
            catch(SQLException sqlexception1) {
                throw new SystemException(sqlexception1);
            }
        }
    }


	static public void deactivateUser(int userId)
    throws SystemException
    {	
		Connection connection = null;
		 
        String query = "UPDATE BW_USER "+
						"SET ACTIVE = 0 "+
                       "WHERE  BW_USER.ID = ?";
        PreparedStatement preparedstatement = null;
        try {
			DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
			connection = databaseloader.getConnection();
            preparedstatement = connection.prepareStatement(query);
            preparedstatement.setInt(1, userId);
            preparedstatement.executeUpdate();
        }catch(SQLException sqlexception) {
            throw new SystemException(sqlexception);
        }
        finally {
            try {
                preparedstatement.close();
            }
            catch(SQLException sqlexception1) {
                throw new SystemException(sqlexception1);
            }
        }
    }


 static public Vector getNonMembersWithUsername(Connection connection, int nhid)
        throws SystemException
        {
			System.out.println("nhid = " + nhid );
            String query = "   select EMAIL_ADDRESS, EXTERNAL_USER_ID,ID "+
                           "   from "+
                           "   BW_USER "+
                           "   where ID NOT IN" +
                           "   ( SELECT USER_ID FROM BW_MEMBER where bw_member.neighborhood_id = ?)" +
                           " and Id > 1 " +
                           "  order by BW_USER.EMAIL_ADDRESS " ;
            Vector vt = new Vector();
            ResultSet resultset = null;
            PreparedStatement preparedstatement = null;
            try {
                preparedstatement = connection.prepareStatement(query);
                preparedstatement.setInt(1, nhid);
                resultset = preparedstatement.executeQuery();
                while ( resultset.next() ) {

                    String userAddress = resultset.getString("EMAIL_ADDRESS");
                    String userName = resultset.getString("EXTERNAL_USER_ID");
                    int userId = resultset.getInt("ID");
                    User usr = new User (userId, userAddress) ;
                    vt.add( usr);
                }
            } catch(SQLException sqlexception) {
				sqlexception.printStackTrace();
                throw new SystemException(sqlexception);
            }
            finally {
                try {
					if ( resultset != null )
					{
                    	resultset.close();
					}
					if ( preparedstatement != null )
					{
                    	preparedstatement.close();
					}
                }
                catch(SQLException sqlexception1) {
                    throw new SystemException(sqlexception1);
                }
            }

            return vt;
        }

    static public Hashtable getMembersWithUsernameForNeighborhood(
										Connection connection, int nhid)
    throws SystemException
    {
        String query = "select MEMBER.*, BW_USER.EMAIL_ADDRESS, BW_USER.EXTERNAL_USER_ID, NH.NAME "+
                       " from "+
                       "   BW_NH AS NH, "+
                       "   BW_USER AS BW_USER, "+
                       "   BW_MEMBER AS MEMBER "+
                       " where "+
                       "   NH.ID = MEMBER.NEIGHBORHOOD_ID "+
                       "   and BW_USER.ID = MEMBER.USER_ID "+
                       "   and MEMBER.NEIGHBORHOOD_ID = ? ";
        Hashtable ht = new Hashtable();
        ResultSet resultset = null;
        PreparedStatement preparedstatement = null;
        try {
            preparedstatement = connection.prepareStatement(query);
            preparedstatement.setInt(1, nhid);
            resultset = preparedstatement.executeQuery();
            while ( resultset.next() ) {
                int id = resultset.getInt("ID");
                int userId = resultset.getInt("USER_ID");
                boolean isActive = resultset.getBoolean("IS_ACTIVE");
                String userAddress = resultset.getString("EMAIL_ADDRESS");
                String userName = resultset.getString("EXTERNAL_USER_ID");
                String nhName =  resultset.getString("NAME");
                ht.put( new Member(id, userId, nhid, nhName), userAddress);
            }
        } catch(SQLException sqlexception) {
			sqlexception.printStackTrace();
            //throw new SystemException(sqlexception);
        }
        finally {
            try {
                resultset.close();
                preparedstatement.close();
            }
            catch(SQLException sqlexception1) {
				sqlexception1.printStackTrace();
                //throw new SystemException(sqlexception1);
            }
        }

        return ht;
    }
    static public Vector getMembersForNeighborhood(
										Connection connection, int nhid)
    throws SystemException
    {
        String query = "select MEMBER.*, BW_USER.EMAIL_ADDRESS, BW_USER.EXTERNAL_USER_ID, NH.NAME "+
                       " from "+
                       "   BW_NH AS NH, "+
                       "   BW_USER AS BW_USER, "+
                       "   BW_MEMBER AS MEMBER "+
                       " where "+
                       "   NH.ID = MEMBER.NEIGHBORHOOD_ID "+
                       "   and BW_USER.ID = MEMBER.USER_ID "+
                       "   and MEMBER.NEIGHBORHOOD_ID = ? ";
        Vector members = new Vector();
        ResultSet resultset = null;
        PreparedStatement preparedstatement = null;
        try {
            preparedstatement = connection.prepareStatement(query);
            preparedstatement.setInt(1, nhid);
            resultset = preparedstatement.executeQuery();
            while ( resultset.next() ) {
                int id = resultset.getInt("ID");
                int userId = resultset.getInt("USER_ID");
                boolean isActive = resultset.getBoolean("IS_ACTIVE");
                String userAddress = resultset.getString("EMAIL_ADDRESS");
                String userName = resultset.getString("EXTERNAL_USER_ID");
                String nhName =  resultset.getString("NAME");
                members.addElement( new Member(id, userId, nhid, nhName));
            }
        } catch(SQLException sqlexception) {
            throw new SystemException(sqlexception);
        }
        finally {
            try {
                resultset.close();
                preparedstatement.close();
            }
            catch(SQLException sqlexception1) {
                throw new SystemException(sqlexception1);
            }
        }

        return members;
    }

public static MemberTableAccess inferNeighborhoodForTable( Connection connection, int a_user_id, int table_id ) throws SystemException {
	        ResultSet resultset = null;
	        PreparedStatement preparedstatement = null;
	        MemberTableAccess   memberAccess = null;

	        try
	        {
	            preparedstatement = connection.prepareStatement(CALL_BW_GET_NH_FOR_USER);
	            preparedstatement.setInt(1,table_id);
	            preparedstatement.setInt(2,a_user_id);
	            resultset = preparedstatement.executeQuery();

	           while( resultset.next() )
	            {
					int    a_nh_id;
					int    a_member_id;
					String  a_access_relationship;
					int  a_acl;
					String a_nh_name;



					a_nh_id = resultset.getInt("NH_ID");
					a_nh_name = resultset.getString("NH_NAME");
					a_member_id = resultset.getInt("MEMBER_ID");
					a_access_relationship = resultset.getString("REL");
					a_acl = resultset.getInt("ACCESS");

					MemberTableAccess memberAccessfromDB = new MemberTableAccess(a_member_id, a_nh_id, a_nh_name, a_user_id, a_acl, a_access_relationship);
					System.out.println("access result from db");
				//	memberAccessfromDB.print();

					if ( memberAccess == null )
					{
						memberAccess = memberAccessfromDB;
					}
					else
					{
						if ( !a_access_relationship.equals("PARENT")  && !a_access_relationship.equals("CHILDREN") && !a_access_relationship.equals("PEER") && !a_access_relationship.equals("PRIVATE")  && !a_access_relationship.equals("DOMAIN")  && !a_access_relationship.equals("PUBLIC")  )
						{
								memberAccess = memberAccessfromDB;
						}
					}

	            }
				return memberAccess;
	        }
	        catch(SQLException sqlexception)
	        {
	            throw new SystemException(sqlexception);
	        }
	        finally
	        {
	            try
	            {
	                resultset.close();
	                preparedstatement.close();
	            }
	            catch(SQLException sqlexception1)
	            {
	                throw new SystemException(sqlexception1);
	            }
	        }
    }

   static public int createMember( Connection a_connection, int transactionId, int userId, int nhid) {

           int memberId = -1;
           String call ="{CALL BW_CR_MEMBER(?,?,?,?)}";

           CallableStatement callablestatement = null;

           try {
               callablestatement = a_connection.prepareCall(call);
               callablestatement.setInt(1, userId);
               callablestatement.setInt(2, nhid);
               callablestatement.setInt(3, transactionId);
               callablestatement.registerOutParameter(4,java.sql.Types.INTEGER);
               int l = callablestatement.executeUpdate();
               memberId = callablestatement.getInt(4);
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
   			catch( SQLException sql )
   			{
   				sql.printStackTrace();
   			}
   		}

           return memberId;
    }
}
