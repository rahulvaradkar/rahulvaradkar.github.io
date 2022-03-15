package boardwalk.rest;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Properties;
import org.apache.commons.codec.binary.Base64;

import com.boardwalk.database.DatabaseLoader;
import com.boardwalk.exception.BoardwalkException;
import com.boardwalk.exception.SystemException;
import com.boardwalk.member.MemberNode;
import com.boardwalk.user.UserManager;

import boardwalk.connection.BoardwalkConnection;
import boardwalk.connection.BoardwalkConnectionManager;
import io.swagger.model.ErrorRequestObject;

public class bwAuthorization {

    private static String CALL_BW_GET_ALL_MEMBERSHIPS_INFO = "{CALL BW_GET_ALL_MEMBERSHIPS_INFO}";
	
	public bwAuthorization()
	{	
	}
	

	public static BoardwalkConnection AuthenticateUser(String authBase64String, ArrayList<Integer> memberNh ,  ArrayList<ErrorRequestObject> ErrResps)
	{
		ErrorRequestObject erb;
    	Connection connection = null;
		BoardwalkConnection bwcon = null;
		
		byte[] authSetting = Base64.decodeBase64(authBase64String);
		String auth = new String(authSetting);		

		String loginName = null;
		String loginPwd = null;
		String nhPath = null;
		
		try 
		{
			String[] userLogin = auth.split(":");
			loginName = userLogin[0];
			loginPwd = userLogin[1];
			nhPath = userLogin[2];
		}
		catch (Exception e)
		{
		    System.out.println("Invalid Authorization Format");
			erb = new ErrorRequestObject();
			erb.setError("Invalid Authorization Format");
			erb.setPath("bwAuthorization.AuthenticateUser::auth.split()");
			erb.setProposedSolution("Authorization Header shuld be Base64 string user:pwd:nhPath");
			ErrResps.add(erb);
		}
		
		//System.out.println("loginName :" + loginName);		
		//System.out.println("loginPwd :" + loginPwd);		
		//System.out.println("nhPath :" + nhPath);		


    	try
    	{
    		// Start a connection
    		DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
    		connection = databaseloader.getConnection();
    		// Get an authenticated boardwalk connection
    		try
    		{
    		    bwcon = BoardwalkConnectionManager.getBoardwalkConnection(connection, loginName, loginPwd, -1);

        		if (bwcon == null)
        		{
        			throw new BoardwalkException(11004); 
        		}
    		    System.out.println("Successfully obtained authenticated Boardwalk connection");
    		    
        		int userId = bwcon.getUserId();
        		int memberId = UserManager.checkMembershipStatus(connection, userId, "USER", nhPath, -1);

        		if (memberId == -1)
        		{
        		    System.out.println("Invalid Authorization. User is not a member of Neighborhood Path.");
        			erb = new ErrorRequestObject();
        			erb.setError("Invalid Authorization. User is not a member of Neighborhood Path.");
        			erb.setPath("bwAuthorization.AuthenticateUser::UserManager.checkMembershipStatus()");
        			erb.setProposedSolution("User must have valid Membership of Neighborhood Collaboration of Grid");
        			ErrResps.add(erb);
        			return bwcon;
        		}

				Hashtable members = getAllMemberships(connection);
				//System.out.println("memberId : " + memberId);
				MemberNode mn = (MemberNode) members.get(memberId);
				memberNh.add(0, mn.getMemberId() );
				memberNh.add(1, mn.getNhId()  );
    		}
    		catch(BoardwalkException bwe)
    		{
    			erb = new ErrorRequestObject();
    			erb.setError("Authentication_Connection_Failure");
    			erb.setPath("bwAuthorization.AuthenticateUser::getBoradwalkConnection");
    			erb.setProposedSolution("Authentication Failed. Use correct login/password and try aagain.");
    			ErrResps.add(erb);
    		    System.out.println("Authentication Failed");
//    		    return cbfReturn;
    		}
    		

    		//Custom Code Starts
    	}
		catch (SQLException sqe)
		{
			System.out.println("Get DBConnection failed. Contact Boardwalk System Administrator");
			erb = new ErrorRequestObject();
			erb.setError("SQLException:" + sqe.getCause());
			erb.setPath("bwAuthorization.AuthenticateUser::getConnection");
			erb.setProposedSolution("Get DBConnection failed. Contact Boardwalk System Administrator");
			ErrResps.add(erb);
			sqe.printStackTrace();
		}
        catch ( SystemException s)
		{
        	System.out.println("SystemException thrown in bwAuthorization.AuthenticateUser: Possibly from getAllMemberships()");
        	s.printStackTrace();
        	erb = new ErrorRequestObject();
        	erb.setError("SystemException: " + s.getErrorMessage());
        	erb.setPath("bwAuthorization.AuthenticateUser::bwAuthorization.getAllMemberships");
			erb.setProposedSolution(s.getPotentialSolution());
        	ErrResps.add(erb);
		}  					
    	
    	return bwcon;
	}

    public static Hashtable getAllMemberships(Connection connection) throws SystemException
    {
    	Hashtable ht = new Hashtable();
        ResultSet rs = null;
        CallableStatement cs  = null;

        try
        {
			cs = connection.prepareCall(CALL_BW_GET_ALL_MEMBERSHIPS_INFO);

			System.out.println("before calling CALL_BW_GET_ALL_MEMBERSHIPS_INFO i.e. "  + CALL_BW_GET_ALL_MEMBERSHIPS_INFO);
			cs.execute();
            rs = cs.getResultSet();
			System.out.println("after calling CALL_BW_GET_ALL_MEMBERSHIPS_INFO");

			int memberId, userId, nhId, nhLevel;
			String firstName, lastName, Email;

			//System.out.println("before while rs loop");

            while ( rs.next() )
            {
            	//System.out.println("inside while rs loop");
                memberId = rs.getInt("MemberId");
                userId = rs.getInt("UserId");
                firstName = rs.getString("FirstName");
                lastName = rs.getString("LastName");
                Email = rs.getString("Email_Address");
                nhId = rs.getInt("NhId");
                nhLevel = rs.getInt("NhLevel");
                //System.out.println("MemberNode-> memberId:" + memberId + ", userId:" + userId + ", firstName:" + firstName + ", lastName:" + lastName + ", Email:" + Email + ", nhId:" + nhId + ", nhLevel:" + nhLevel);            
                ht.put(memberId, new MemberNode(memberId, userId, firstName, lastName, Email, nhId, nhLevel));
            }
			System.out.println("outside while rs loop");
        }
        catch(SQLException sqlexception)
        {
			System.out.println(sqlexception.toString());
            throw new SystemException(sqlexception);
        }
        finally
        {
            try
            {
				if ( rs != null )
					rs.close();
				if ( cs != null )
					cs.close();
            }
            catch(SQLException sqlexception1) {
				System.out.println("throwing  sqlexception1");
                throw new SystemException(sqlexception1);
            }
        }
        return ht;
    }
	
	
}


