package com.boardwalk.table;

import java.util.*;
import java.io.*;
import com.boardwalk.database.Transaction;
import boardwalk.common.*;

public class UserAccessRights 
{
	String m_userId;
	String m_userNh;
	String m_role;
	String m_targetNh;
	String m_addNewUser;
	String m_deactivateUser;
	String m_addMember;
	String m_deleteMember;
	int m_userNhId;
	
	public UserAccessRights(String a_addNewUser,String a_deactivateUser,String a_addMember,String a_deleteMember,int a_userNhId)
	{
	m_addNewUser		=  a_addNewUser;
	m_deactivateUser	=  a_deactivateUser;
	m_addMember			=  a_addMember;
	m_deleteMember		=  a_deleteMember;
	m_userNhId			=  a_userNhId;
	}
	
	public UserAccessRights(String a_userId,String a_userNh,String a_role,String a_targetNh,String a_addNewUser,
							String a_deactivateUser,String a_addMember,String a_deleteMember,int a_userNhId)
	{

	m_userId			=  a_userId;
	m_userNh			=  a_userNh;
	m_role				=  a_role;
	m_targetNh			=  a_targetNh;
	m_addNewUser		=  a_addNewUser;
	m_deactivateUser	=  a_deactivateUser;
	m_addMember			=  a_addMember;
	m_deleteMember		=  a_deleteMember;
	m_userNhId			=  a_userNhId;
	}

	public String getuserId()
	{
		return m_userId;
	}
	public void setuserId(String asUserId)
	{
		 m_userId = asUserId;
	}

	public int getuserNhId()
	{
		return m_userNhId;
	}
	public void setuserNhId(int asUserNhId)
	{
		m_userNhId = asUserNhId;
	}

	public String getuserNh()
	{
		return m_userNh;
	}
	public void setuserNh(String asUserNh)
	{
		m_userNh = asUserNh;
	}



	public String getRole()
	{
		return m_role;
	}
	public void setrole(String asgetRole)
	{
		m_role = asgetRole;
	}

	public String gettargetNh()
	{
		return m_targetNh;
	}
	public void settargetNh(String astargetNh)
	{
		m_targetNh = astargetNh;
	}

	public String getaddNewUser()
	{
		return m_addNewUser;
	}
	public void setaddNewUser(String asaddNewUser )
	{
		 m_addNewUser = asaddNewUser;
		 
	}

	public String getdeactivateUser()
	{
		return m_deactivateUser;
	}
	public void setdeactivateUser(String asdeactivateUser)
	{
		 m_deactivateUser = asdeactivateUser;
	}

	public String getaddMember()
	{
		return m_addMember;
	}
	public void setaddMember(String asaddMember)
	{
		 m_addMember = asaddMember;
	}

	public String getdeleteMember()
	{
		return m_deleteMember;
	}
	public void setdeleteMember(String asdeleteMember)
	{
		 m_deleteMember = asdeleteMember;
	}

}
