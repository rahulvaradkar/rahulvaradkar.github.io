package servlets;

import java.io.*;
import java.util.*;
import java.sql.*;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.sql.*;
import boardwalk.common.BoardwalkUtility;
import com.boardwalk.neighborhood.*;
import com.boardwalk.exception.*;
import com.boardwalk.database.*;
import com.boardwalk.member.*;
import com.boardwalk.user.*;
import com.boardwalk.util.*;
import com.boardwalk.table.*;

public class  UserManagerForAccess extends BWTableObjects
{
	UserAccessRights ObjUsrAccRights;

	public UserManagerForAccess()
	{}
	public UserManagerForAccess(String a_table_name)
	{
	super(a_table_name);
	}
	
	public boolean setAdminAccessForUsers(String strUsrId)
	{	
		System.out.println("-------------Inside setAccessForUsers--------- ");
		System.out.println("-------------Value of strUsrId----------"+strUsrId);

		String userIdret = "";
		String userNh = "";
		String role = "";
		String targetNh = "";
		String addNewUser = "";
		String deactivateUser = "";
		String addMember = "";
		String deleteMember = "";
		int userNhId= -1;
		boolean retAdminAccess = false;

		ObjUsrAccRights = new UserAccessRights(userIdret,userNh,role,targetNh,addNewUser,deactivateUser,
																addMember,deleteMember,userNhId);

		for( int i = 0; i< m_rowCount ; i++)
		{
			for(int j=0; j < m_colCount;j++)
			{
				String UsrCompId = m_getTableContents[i][j];
			    System.out.println("value of UsrCompId" +UsrCompId);
				System.out.println("value of strUsrId" +strUsrId);
				if(UsrCompId.trim().equalsIgnoreCase (strUsrId.trim()))
				{
					System.out.println("Inside If ");

					for(int k = 0; k < m_colCount;k++)
					{
						switch( k ) {
								 case 0:
									userIdret = m_getTableContents[i][k];
									ObjUsrAccRights.setuserId(userIdret);
									//System.out.println("userIdret"+userIdret);
									break;
								case 1:
									userNh= m_getTableContents[i][k];
									ObjUsrAccRights.setuserNh(userNh);
									userNhId=getNameofNh(userNh);
									ObjUsrAccRights.setuserNhId(userNhId);
									//System.out.println("....userNhId.........."+userNhId);
									//System.out.println("userNh"+userNh);
									break;
								case 2:
									role = m_getTableContents[i][k];
									ObjUsrAccRights.setrole(role);
									//System.out.println("role"+role);
									break;
								case 3:
									targetNh = m_getTableContents[i][k] ;
									ObjUsrAccRights.settargetNh(targetNh);
									//System.out.println("targetNh"+targetNh);
									break;
								case 4:
									addNewUser = m_getTableContents[i][k];
									ObjUsrAccRights.setaddNewUser(addNewUser);
									//System.out.println("addNewUser"+addNewUser);
									break;
								case 5:
									deactivateUser = m_getTableContents[i][k];
									ObjUsrAccRights.setdeactivateUser(deactivateUser);
									//System.out.println("deactivateUser"+deactivateUser);
									break;
								case 6:
									addMember = m_getTableContents[i][k];
									ObjUsrAccRights.setaddMember(addMember);
									//System.out.println("addMember"+addMember);
									break;
								case 7:
									deleteMember = m_getTableContents[i][k];
									ObjUsrAccRights.setdeleteMember(deleteMember);
									//System.out.println("deleteMember"+deleteMember);
									break;
								default:
									//System.out.println("No matching Case");
									 }	
									

					}
					retAdminAccess = true;
					return retAdminAccess;
				}
			}
			
		}
			return retAdminAccess;
	}
	
	public UserAccessRights getAdminAccessForUsers()
	{
			return ObjUsrAccRights;
	}

	public static int getTargetNhForUser(String a_targetNh)
	{
		String[] RetUserNh = BoardwalkUtility.getArrayFromStrTok(a_targetNh,"/");
		int len = RetUserNh.length;
		String UserNhTargetVal = "";
		int level = -1;
		 int targetNh_id =-1;
		String targetNh_name = "";
		if(len == 4)
		{
			level = 3;
			UserNhTargetVal = RetUserNh[len-1];
			 System.out.println("---------Value of level3--------"+UserNhTargetVal);
			if(UserNhTargetVal.equals( "*"))
			{
				UserNhTargetVal = RetUserNh[len -2];
			}
			else
				UserNhTargetVal = RetUserNh[len-1];


		}
		if(len == 3)
		{
			level = 2;
			UserNhTargetVal = RetUserNh[len-1];
			 System.out.println("---------Value of level2--------"+UserNhTargetVal);
			if(UserNhTargetVal.equals( "*"))
			{
				UserNhTargetVal = RetUserNh[len -2];
			}
			else
				UserNhTargetVal = RetUserNh[len-1];
		}
		if(len == 2)
		{
			level = 1;
			 UserNhTargetVal = RetUserNh[len-1];
			// System.out.println("---------Value of level1--------"+UserNhTargetVal);
			if(UserNhTargetVal.equals( "*"))
			{
				UserNhTargetVal = RetUserNh[len -2];
				System.out.println("---------Value of level1--------"+UserNhTargetVal);
			}
			else
				UserNhTargetVal = RetUserNh[len-1];
		}
		if(len == 1)
		{
			level = 0;
			UserNhTargetVal = RetUserNh[len-1];
			 System.out.println("---------Value of level0--------"+UserNhTargetVal);
			if(UserNhTargetVal.equals( "*"))
			{
				targetNh_id =0; // When TargetNh is *
			}
			else
				UserNhTargetVal = RetUserNh[len-1];
		}

		int nhid = http_vb_getTableInfo.getNhId(UserNhTargetVal,level);
		System.out.println("Value of nhid-------------------------------"+nhid);
		if (nhid != -1)
		{
			System.out.println("Value of nhid-------------------------------");
			 //targetNh_name = http_vb_getTableInfo.getTargetNhForGivenLevel(nhid,level);
			 targetNh_id =  http_vb_getTableInfo.getTargetNhForGivenLevel(nhid,level);
		}
		

		System.out.println("<<<<<<<<value of targetNh>>>>>>>>>"+UserNhTargetVal);
		System.out.println("<<<<<<<<value of nhid>>>>>>>>>"+targetNh_id);
		
		return targetNh_id;
		
		
	}

}
