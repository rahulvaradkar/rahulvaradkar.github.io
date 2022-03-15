package com.boardwalk.util;

import java.util.*;
import java.sql.*;
public class BoardwalkSession
{
	public Integer userId = new Integer(-1);
	public Integer memberId = new Integer(-1);
	public Integer selNhid =  new Integer(-1);
	public Integer nhId =  new Integer(-1);
	public String nhName = "";
	public String Referer = "";
	public String userEmailAddress = "";
	public Vector membershipList = new Vector();
	public Hashtable memberIdToMember = new Hashtable();
	public String bwXLVersion =  "-1";

	public BoardwalkSession()  {}
	
	public BoardwalkSession(Connection connection)
	{		
		try
		{
			PreparedStatement stmt = connection.prepareStatement(" SELECT Top 1 MAJOR_VERSION, MINOR_VERSION FROM BW_EXCEL_VERSION ");
			ResultSet rs = stmt.executeQuery();
			if (rs.next())
			{
				bwXLVersion = rs.getString(1) + "." + rs.getString(2);					
			}
			rs.close();
			rs = null;
			stmt.close();
			stmt = null;
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
	}
}