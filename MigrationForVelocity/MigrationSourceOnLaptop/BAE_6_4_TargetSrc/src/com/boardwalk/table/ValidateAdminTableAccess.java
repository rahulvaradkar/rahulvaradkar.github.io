package com.boardwalk.table;

import java.io.*;
import java.util.*;
import com.boardwalk.database.*;
import java.sql.*;  
import javax.sql.*;
import com.boardwalk.util.*;

public class ValidateAdminTableAccess
{
	public ValidateAdminTableAccess(){}
	Connection connection = null; 
	TransactionManager tm = null;
	int tid = -1;
	// validate Nh
	public boolean canDeleteNH(int aNHid, int aUserId) throws IOException
	{
		ArrayList NhidsToBeDeleted  = new ArrayList();
		int deleteNhId				= -1;
		boolean returnvalue			= false;
		boolean canDelete			= true;
		try
		{
			DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
			connection					= databaseloader.getConnection();
			NhidsToBeDeleted			= getNhChildrenForNHid(aNHid);

			for(int i=0 ; i < NhidsToBeDeleted.size() ; i++)
			{
				deleteNhId			= ((Integer)NhidsToBeDeleted.get(i)).intValue();
				boolean canDeleteNh = ValidateNHForDelete(connection,deleteNhId,aUserId);
				if(canDeleteNh == false && canDelete == true)
				{	
					canDelete = false;
					break;
				}	
			}
			returnvalue = canDelete;
		}
		catch (IOException ioexe)
		{
			ioexe.printStackTrace();
			
		}
		catch (SQLException sqlexe)
		{
			sqlexe.printStackTrace();
			
		}
			//System.out.println("value of returnvalue of canDeleteNH "+returnvalue);
		return returnvalue;
	}
	
	public ArrayList getChildrenForNh(Connection connection,int aiNhId, int aiLevel)
	{
		ArrayList retValue = new ArrayList();
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try
		{
			
			String lsQuery = "";

			if(aiLevel == 3)
				lsQuery = "SELECT NH.ID as NHID FROM BW_NH AS PARENT_NH, BW_NH AS NH, BW_NH_LEVEL_3 AS NH3 WHERE  NH.ID = NH3.NEIGHBORHOOD_ID AND PARENT_NH.NEIGHBORHOOD_LEVEL = 2 AND PARENT_NH.LEVEL_2_ID = NH.LEVEL_2_ID  AND PARENT_NH.ID =?" ;
			if(aiLevel == 2)
				lsQuery = "SELECT NH.ID as NHID FROM BW_NH AS PARENT_NH, BW_NH AS NH, BW_NH_LEVEL_2 AS NH2 WHERE NH.ID = NH2.NEIGHBORHOOD_ID AND PARENT_NH.NEIGHBORHOOD_LEVEL = 1  AND PARENT_NH.LEVEL_1_ID = NH.LEVEL_1_ID  AND PARENT_NH.ID =?" ;
			if(aiLevel == 1)
				lsQuery = "SELECT NH.ID as NHID FROM BW_NH AS PARENT_NH, BW_NH AS NH, BW_NH_LEVEL_1 AS NH1 WHERE NH.ID = NH1.NEIGHBORHOOD_ID AND PARENT_NH.NEIGHBORHOOD_LEVEL = 0  AND PARENT_NH.LEVEL_0_ID = NH.LEVEL_0_ID AND PARENT_NH.ID =?" ;

			pstmt	= connection.prepareStatement(lsQuery);
			pstmt.setInt(1,aiNhId);
			rs	= pstmt.executeQuery();

//			System.out.println(" lsQuery lsQuery "+ lsQuery);

			while (rs.next())
			{
				int nhID	= rs.getInt("NHID");
				retValue.add(new Integer(nhID));
			}

			pstmt = null;
			rs	 = null;
			
		}
		catch (Exception sqe)
		{
			pstmt = null;
			rs	 = null;
			sqe.printStackTrace();
		}

		return retValue;
	}

	public ArrayList getNhChildrenForNHid(int aiSelNhId)
	{
		int liNHLevel = -1;
		ArrayList NHids = new ArrayList();
		ArrayList TempNhIdsLevel1 = new ArrayList();
		ArrayList TempNhIdsLevel2 = new ArrayList();
		ArrayList TempNhIdsLevel3 = new ArrayList();
		ArrayList ReturnList = new ArrayList();
		PreparedStatement pstmt = null;

		try
		{
			DatabaseLoader databaseloader   = new DatabaseLoader(new Properties());
			connection						= databaseloader.getConnection();
			//System.out.println("================inside getNhChildrenForNHid=============");
			pstmt	= connection.prepareStatement("SELECT NEIGHBORHOOD_LEVEL FROM BW_NH WHERE ID =?");
			pstmt.setInt(1,aiSelNhId);
			ResultSet rs	= pstmt.executeQuery();

			while(rs.next())
			{
				liNHLevel	= rs.getInt("NEIGHBORHOOD_LEVEL");
	 			//System.out.println(" NEIGHBORHOOD_LEVEL "+ liNHLevel);
			}

			int liTempValue = -1;

			if(liNHLevel == 0)
			{
				NHids.add(aiSelNhId);
				// get the level 1 ids first
				TempNhIdsLevel1 = getChildrenForNh(connection,aiSelNhId, 1);

				if(TempNhIdsLevel1.size() > 0 )
				{
					for(int i=0 ; i < TempNhIdsLevel1.size() ; i++)
					{
						NHids.add(TempNhIdsLevel1.get(i));
						liTempValue = ((Integer)TempNhIdsLevel1.get(i)).intValue();
						TempNhIdsLevel2 = getChildrenForNh(connection,liTempValue, 2);
						for(int j=0 ; j < TempNhIdsLevel2.size() ; j++)
						{
							NHids.add(TempNhIdsLevel2.get(j));
							liTempValue = ((Integer)TempNhIdsLevel2.get(j)).intValue();
							TempNhIdsLevel3 = getChildrenForNh(connection,liTempValue, 3);
							for(int k=0 ; k < TempNhIdsLevel3.size() ; k++)
							{
								NHids.add(TempNhIdsLevel3.get(k));
							}
						}
					}
				}
			}

			if(liNHLevel == 1)
			{
				NHids.add(aiSelNhId);
				// get the level 1 ids first
				TempNhIdsLevel2 = getChildrenForNh(connection,aiSelNhId, 2);

				if(TempNhIdsLevel2.size() > 0 )
				{
					for(int i=0 ; i < TempNhIdsLevel2.size() ; i++)
					{
						NHids.add(TempNhIdsLevel2.get(i));
						liTempValue = ((Integer)TempNhIdsLevel2.get(i)).intValue();
						TempNhIdsLevel3 = getChildrenForNh(connection,liTempValue, 3);
						for(int j=0 ; j < TempNhIdsLevel3.size() ; j++)
						{
							NHids.add(TempNhIdsLevel3.get(j));
						}
					}
				}
			}

			if(liNHLevel == 2)
			{
				NHids.add(aiSelNhId);
				// get the level 1 ids first
				TempNhIdsLevel3 = getChildrenForNh(connection,aiSelNhId, 3);

				if(TempNhIdsLevel3.size() > 0 )
				{
					for(int i=0 ; i < TempNhIdsLevel3.size() ; i++)
					{
						NHids.add(TempNhIdsLevel3.get(i));
					}
				}
			}

			if(liNHLevel == 3)
			{
				NHids.add(aiSelNhId);
			}
	
			for(int i= NHids.size()-1 ; i >= 0 ; i--)
			{
				System.out.println(" Count  "+i+" "+NHids.get(i));
				ReturnList.add(NHids.get(i));
			}
		}
		catch (Exception sqe)
		{
			//connection.close();
			sqe.printStackTrace();
		}
		
		return ReturnList;
	}

	public boolean ValidateNHForDelete(Connection connection,int aNHid, int aUserId) throws IOException ,SQLException
	{
		boolean lbReturnValue = false ;
		boolean canDelete = true;
		PreparedStatement pstmt = null;
		ResultSet rs   = null;
		try
		{
			int memberID	= -1;
			pstmt			= connection.prepareStatement("SELECT ID FROM BW_MEMBER WHERE USER_ID = ? AND NEIGHBORHOOD_ID = ?");
			pstmt.setInt(1,aUserId);
			pstmt.setInt(2,aNHid);
			rs				= pstmt.executeQuery();

			while (rs.next())
			{ 
				memberID	= rs.getInt("ID");
			}

			pstmt	= connection.prepareStatement("SELECT ID FROM BW_TBL WHERE BW_WB_ID IN (SELECT ID FROM BW_WB WHERE BW_COLLAB_ID IN (SELECT ID FROM BW_COLLAB WHERE NEIGHBORHOOD_ID = ?)) ORDER BY BW_TBL.ID");
			pstmt.setInt(1,aNHid);
			rs		= pstmt.executeQuery();
			while(rs.next())
			{
				if(memberID > 0)
				{
					int currTableId				= rs.getInt("ID");
					TableAccessList objTBLList	= null;
					objTBLList			= TableManager.getTableAccessForMember( connection, memberID, currTableId);
					boolean canAdministerTbl	= objTBLList.canAdministerTable();
					if(canAdministerTbl == false && canDelete == true)
					{	
						canDelete = false;
						break;
					}
				}
				else
					return lbReturnValue;
						
			}

			lbReturnValue = canDelete;
			return lbReturnValue;
		}
		catch (Exception sqe)
		{
			if(pstmt != null)
				pstmt = null;

			if(rs != null)
				rs = null;

			lbReturnValue = false;
			sqe.printStackTrace();
		}
		return lbReturnValue ;
   }

// Validate Collab

	public boolean canDeleteCollab( int aCollabid, int aUserId) throws IOException
	{
		boolean lbReturnValue = false ;
		boolean canDelete = true;
		int memberId		= -1;
		PreparedStatement pstmt = null;
		try
		{
			DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
	        connection					  = databaseloader.getConnection();
			pstmt	= connection.prepareStatement(" SELECT BW_MEMBER.ID FROM BW_MEMBER,BW_COLLAB "+
												  " WHERE BW_COLLAB.ID = ?"+
												  " AND BW_MEMBER.USER_ID = ?"+
												  " AND BW_COLLAB.NEIGHBORHOOD_ID = BW_MEMBER.NEIGHBORHOOD_ID");
			pstmt.setInt(1,aCollabid);
			pstmt.setInt(2,aUserId);
			ResultSet rs	= pstmt.executeQuery();
			while (rs.next())
			{
				memberId		= rs.getInt("ID");
			}
			pstmt	= connection.prepareStatement("SELECT ID FROM BW_TBL WHERE BW_WB_ID IN ( SELECT ID FROM BW_WB WHERE BW_COLLAB_ID = ? ) ORDER BY BW_TBL.ID");
			pstmt.setInt(1,aCollabid);
			rs		= pstmt.executeQuery();
						
			while(rs.next())
			{
				int currTableId				= rs.getInt("ID");
				TableAccessList objTBLList	= null;
				objTBLList					= TableManager.getTableAccessForMember( connection, memberId, currTableId);
				boolean canAdministerTbl	= objTBLList.canAdministerTable();
				if(canAdministerTbl == false && canDelete == true)
				{	
					canDelete = false;
					break;
				}
			}	

			lbReturnValue = canDelete;
			return lbReturnValue;	
			
		}
		catch (Exception sqe)
		{
			lbReturnValue = false;
			sqe.printStackTrace();
		}
		return lbReturnValue ;
	}

//Validate WB

	public boolean canDeleteWB(int aWBid, int aUserId) throws IOException
	{
		boolean lbReturnValue	= false ;
		boolean canDelete		= true;
		int memberID			= -1;
		PreparedStatement pstmt = null;
		ResultSet rs			= null;
		try
		{
			DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
			connection					  = databaseloader.getConnection();
			pstmt	= connection.prepareStatement(" SELECT BW_MEMBER.ID FROM BW_COLLAB,BW_WB,BW_MEMBER"+
												  " WHERE BW_WB.ID = ?"+
												  " AND BW_MEMBER.USER_ID = ?"+
												  " AND BW_WB.BW_COLLAB_ID = BW_COLLAB.ID"+
												  " AND BW_WB.NEIGHBORHOOD_ID = BW_COLLAB.NEIGHBORHOOD_ID"+
												  " AND BW_MEMBER.NEIGHBORHOOD_ID = BW_COLLAB.NEIGHBORHOOD_ID");
			pstmt.setInt(1,aWBid);
			pstmt.setInt(2,aUserId);

			rs		= pstmt.executeQuery();
			
			while (rs.next())
			{
				memberID	= rs.getInt("ID");	
			}

			pstmt	= connection.prepareStatement("SELECT ID FROM BW_TBL WHERE BW_WB_ID = ?");
			pstmt.setInt(1,aWBid);
			rs		= pstmt.executeQuery();

			while(rs.next())
			{
				int currTableId				= rs.getInt("ID");
				TableAccessList objTBLList	= null;
				objTBLList					= TableManager.getTableAccessForMember( connection, memberID, currTableId);
				boolean canAdministerTbl	= objTBLList.canAdministerTable();
				if(canAdministerTbl == false && canDelete == true)
				{	
					canDelete = false;
					break;
				}
			}

			lbReturnValue = canDelete;
			return lbReturnValue;
		}
		catch (Exception sqe)
		{
			lbReturnValue = false;
			sqe.printStackTrace();
		}
		//System.out.println("================inside lbReturnValue============="+lbReturnValue);
		return lbReturnValue ;

	}

}


