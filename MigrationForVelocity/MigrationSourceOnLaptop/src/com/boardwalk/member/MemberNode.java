package com.boardwalk.member;
import java.util.*;
import java.io.*;

public class MemberNode {
	int     m_memberId;
	int     m_userId;
	String  m_firstName;
	String  m_lastName;
	String  m_Email;
	int m_nhId;
	int m_nhLevel;

	public MemberNode (int memberId, int userId, String firstName, String lastName, String Email, int nhId, int nhLevel  ) 
	{
		m_memberId = memberId;
		m_userId    = userId;
		m_firstName = firstName;
		m_lastName = lastName;
		m_Email = Email;
		m_nhId = nhId;
		m_nhLevel = nhLevel;
	}

	public int getMemberId() 
	{
		return m_memberId;
	}

	public int getUserId() 
	{
		return m_userId;
	}

	public String getFirstName() 
	{
		return m_firstName;
	}

	public String getLastName() 
	{
		return m_lastName;
	}

	public String getEmail() 
	{
		return m_Email;
	}
	public int getNhId() 
	{
		return m_nhId;
	}

	public int getNhLevel() 
	{
		return m_nhLevel;
	}

};
