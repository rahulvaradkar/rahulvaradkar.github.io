/*
 * @(#)BoardwalkInviteManager.java	1.0
 *
 * Copyright 2004 BoardwalkTech, Inc. All rights reserved.
 * BoardwalkTech/CONFIDENTIAL. Use is subject to license terms.
 */

package boardwalk.table;

import java.util.*;
import java.io.*;
import java.util.regex.*;
import java.sql.*;                  // JDBC package
import javax.sql.*;                 // extended JDBC packa
import javax.mail.*;
import java.text.*;
import javax.mail.internet.*;
import javax.activation.*;
import java.net.URLEncoder;

import com.boardwalk.user.UserManager;
import boardwalk.neighborhood.BoardwalkMember;

import com.boardwalk.distribution.*;
import boardwalk.neighborhood.*;
import boardwalk.table.*;
import boardwalk.connection.*;
import boardwalk.common.BoardwalkUtility;


/**
 * BoardwalkInviteManager object is used to send invitations to Users
 * in Boardwalk system
 */
 public class BoardwalkInviteManager
 {
	BoardwalkConnection mConnection = null;
	Connection mCon					= null;
	String msmtpserver				= "";
	String msmtpfrom				= "";
	String msUserEmailId			= "";
	String msTemplatePath			= "";
	ArrayList DistributionObjs		= null;
	String msSubjectText			= "";
	public String msMessagePath		= "";
	public String msport			= "25";
	public String mspassword		= "";
	public String msserveraddress	= "";
	public String msserverport		= "";
	public String mscontext			= "";
	BoardwalkDistributionPacket bdp = null;

	public BoardwalkInviteManager(boardwalk.connection.BoardwalkConnection aConnection, String asmtpserver, String asmtpfrom, String asUserEmailId, ArrayList aDistributionObjs)
	{
		mConnection = aConnection;
		msmtpserver = asmtpserver;
		msmtpfrom = asmtpfrom;
		msUserEmailId = asUserEmailId;
		DistributionObjs = aDistributionObjs;
	}




	public BoardwalkInviteManager(boardwalk.connection.BoardwalkConnection aConnection, String asmtpserver, String asmtpfrom,  String asUserEmailId, ArrayList aDistributionObjs, String port, String password)
		{
			mConnection = aConnection;
			msmtpserver = asmtpserver;
			msmtpfrom = asmtpfrom;
			msUserEmailId = asUserEmailId;
			DistributionObjs = aDistributionObjs;
			msport = port;
			mspassword = password;
	}

	public BoardwalkInviteManager(Connection aConnection, String asmtpserver, String asmtpfrom, ArrayList aDistributionObjs)
	{
		mCon = aConnection;
		msmtpserver = asmtpserver;
		msmtpfrom = asmtpfrom;
		DistributionObjs = aDistributionObjs;
	}

	//Parameters
	//	asUserId - this is the id of the user.
	// This method returns the user email Id based on the user id provided.
	public String getEmailIdOnUserId(String asUserId)
	{
		PreparedStatement statement = null;
		ResultSet rs				= null;
		String lsSql				= "";
		String lsUserEmailId 		= "";
		try
		{
			lsSql = " SELECT EMAIL_ADDRESS FROM BW_USER WHERE ID = ?";
			//lsSql = " SELECT EMAIL_ADDRESS FROM BW_USER WHERE ID = '"+asUserId+"'";
			//System.out.println("######### getEmailIdOnUserId() " +lsSql);
			statement = mCon.prepareStatement(lsSql);
			statement.setString(1, asUserId);
			rs = statement.executeQuery();
			if(rs.next())
			{
				lsUserEmailId = rs.getString(1);
			}
			else
			{
				//System.out.println("\n\n ######### User with Id '"+asUserId+"' is not present #########");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if ( rs != null ) {
					rs.close();
				}
				if ( statement != null ) {
					statement.close();
				}
			}
			catch(SQLException sqlexception1)
			{
				sqlexception1.printStackTrace();
			}
		}
		return lsUserEmailId;
	}

	//Parameters
	//	asUserEmailId - this is the Email id of the user.
	// This method returns the user's First name and last name based on the user email id provided.
	public String getUserInfoOnEmailId(String asUserEmailId)
	{
		PreparedStatement statement = null;
		ResultSet rs				= null;
		String lsSql				= "";
		String lsUserInfo			= "";
		try
		{
			lsSql = " SELECT FIRSTNAME, LASTNAME FROM BW_USER WHERE EMAIL_ADDRESS = ?";
			//System.out.println("######### getUserInfoOnEmailId() " +lsSql);
			statement = mConnection.getConnection().prepareStatement(lsSql);
			statement.setString(1,asUserEmailId);
			rs = statement.executeQuery();
			if(rs.next())
			{
				lsUserInfo = rs.getString(1);
				if(lsUserInfo != null && !lsUserInfo.equals(""))
					lsUserInfo += " ";
				lsUserInfo += rs.getString(2);
			}
			else
			{
				//System.out.println("\n\n ######### User with Email Id '"+asUserEmailId+"' is not present #########");
			}
				
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if ( rs != null ) {
					rs.close();
				}
				if ( statement != null ) {
					statement.close();
				}
			}
			catch(SQLException sqlexception1)
			{
				sqlexception1.printStackTrace();
			}
		}
		return lsUserInfo;
	}

	// This method returns the user's First name and last name based on the user email id provided.
	public String getUserIdForEmailId(String asUserEmailId)
	{
		PreparedStatement statement = null;
		ResultSet rs				= null;
		String lsSql				= "";
		String lsUserId				= "";
		try
		{
			lsSql = " SELECT BW_USER.ID FROM BW_USER WHERE EMAIL_ADDRESS = ?";
			//lsSql = " SELECT BW_USER.ID FROM BW_USER WHERE EMAIL_ADDRESS = '"+asUserEmailId+"'";
			//System.out.println("######### getUserInfoOnEmailId() " +lsSql);
			statement = mConnection.getConnection().prepareStatement(lsSql);
			statement.setString(1, asUserEmailId);
			rs = statement.executeQuery();
			if(rs.next())
			{
				lsUserId = rs.getString(1);
			}
			else
			{
				//System.out.println("\n\n ######### User ID with Email Id '"+asUserEmailId+"' is not present #########");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if ( rs != null ) {
					rs.close();
				}
				if ( statement != null ) {
					statement.close();
				}
			}
			catch(SQLException sqlexception1)
			{
				sqlexception1.printStackTrace();
			}
		}
		return lsUserId;
	}


	//Parameters
	//	emailAddress - this is the email address of the user.
	// This method returns a boolean flag stating whether the given email address is a proper one.
	public boolean isValidEmail(String emailAddress)
	{
		//Set the email pattern string
		Pattern p = Pattern.compile(".+@.+\\.[a-z]+");

		//Match the given string with the pattern
		Matcher m = p.matcher(emailAddress);

		//check whether match is found
		boolean matchFound = m.matches();

		if (matchFound) return true; else return false;
	}


	//Parameters
	//	asCuboidName - The name of the Cuboid
	//	aiWbId - The White board id to which this Cuboid belongs too.
	//	aiNhId - The Neighbourhood id to which this Cuboid belongs too.
	// This method returns the cuboid id for the given cuboid name. We can pass additional info like
	// White board Id and Neighbourhood Id to get accurate result. If these ids are entered as "0"
	// then they are not considered in the query.
	public int getCuboidIdOnName(String asCuboidName, int aiWbId, int aiNhId)
	{
		PreparedStatement preparedstatement = null;
		ResultSet rs						= null;
		String lsSql						= "";
		int liCuboidId						= 0;
		try
		{
			//lsSql = " SELECT ID FROM BW_TBL WHERE NAME LIKE '%"+asCuboidName+"%'";
			lsSql = " SELECT ID FROM BW_TBL WHERE NAME LIKE ? ";
			if( aiWbId > 0 )
				lsSql += " AND BW_WB_ID = "+aiWbId;
			if( aiNhId > 0 )
				lsSql += " AND NEIGHBORHOOD_ID = "+aiNhId;
			//System.out.println("######### getCuboidIdOnName() " +lsSql);
			preparedstatement = mConnection.getConnection().prepareStatement(lsSql);
			preparedstatement.setString(1, "%" + asCuboidName + "%");
			rs = preparedstatement.executeQuery(lsSql);
			if(rs.next())
			{
				liCuboidId = rs.getInt(1);
			}
			else
				System.out.println("\n\n ######### Cuboid with Name '"+asCuboidName+"' is not present #########");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if ( rs != null ) {
					rs.close();
				}
				if ( preparedstatement != null ) {
					preparedstatement.close();
				}
			}
			catch(SQLException sqlexception1)
			{
				sqlexception1.printStackTrace();
			}
		}
		return liCuboidId;
	}

	public int getCuboidId(String asCuboidName, String asWbName, String asCollabName)
	{
		PreparedStatement statement = null;
		ResultSet rs				= null;
		String lsSql				= "";
		int liCuboidId				= 0;
		try
		{
			//lsSql = " SELECT TBL.ID "+
			//		" FROM BW_TBL AS TBL , BW_WB AS WB, BW_COLLAB AS COLLAB, BW_NH AS NH "+
			//		" WHERE COLLAB.NAME LIKE '%"+asCollabName+"%' "+
			//		" AND NH.ID = COLLAB.NEIGHBORHOOD_ID "+
			//		" AND WB.NAME LIKE '%"+asWbName+"%' "+
			//		" AND COLLAB.ID = WB.BW_COLLAB_ID "+
			//		" AND NH.ID = WB.NEIGHBORHOOD_ID "+
			//		" AND TBL.NAME LIKE '%"+asCuboidName+"%' "+
			//		" AND TBL.BW_WB_ID = WB.ID ";

			lsSql = " SELECT TBL.ID "+
					" FROM BW_TBL AS TBL , BW_WB AS WB, BW_COLLAB AS COLLAB, BW_NH AS NH "+
					" WHERE COLLAB.NAME LIKE ? "+
					" AND NH.ID = COLLAB.NEIGHBORHOOD_ID "+
					" AND WB.NAME LIKE ? "+
					" AND COLLAB.ID = WB.BW_COLLAB_ID "+
					" AND NH.ID = WB.NEIGHBORHOOD_ID "+
					" AND TBL.NAME LIKE ? "+
					" AND TBL.BW_WB_ID = WB.ID ";

			System.out.println("######### getCuboidId() " +lsSql);

			statement = mConnection.getConnection().prepareStatement(lsSql);
			statement.setString(1, "%"+asCollabName+"%");
			statement.setString(2, "%"+asWbName+"%");
			statement.setString(3, "%"+asCuboidName+"%");
			
			rs = statement.executeQuery();
			if(rs.next())
			{
				liCuboidId = rs.getInt(1);
			}
			else
				System.out.println("\n\n ######### Cuboid with Name '"+asCuboidName+"' is not present #########");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if ( rs != null ) {
					rs.close();
				}
				if ( statement != null ) {
					statement.close();
				}
			}
			catch(SQLException sqlexception1)
			{
				sqlexception1.printStackTrace();
			}
		}
		return liCuboidId;
	}


	//Parameters
	//	asWbName - The name of the WhiteBoard.
	//	aiCollabId - The Collaboration id to which this WhiteBoard belongs too.
	//	aiNhId - The Neighbourhood id to which this WhiteBoard belongs too.
	// This method returns the WhiteBoard id for the given WhiteBoard name. We can pass additional info like
	// Collaboration Id and Neighbourhood Id to get accurate result. If these ids are entered as "0"
	// then they are not considered in the query.
	public int getWbIdOnName(String asWbName, int aiCollabId, int aiNhId)
	{
		PreparedStatement preparedstatement = null;
		ResultSet rs		= null;
		String lsSql		= "";
		int liWbId		= 0;
		try
		{
			//lsSql = " SELECT ID FROM BW_WB WHERE NAME LIKE '%"+asWbName+"%'";
			lsSql = " SELECT ID FROM BW_WB WHERE NAME LIKE  ? ";
			if( aiCollabId > 0 )
				lsSql += " AND BW_COLLAB_ID = "+aiCollabId;
			if( aiNhId > 0 )
				lsSql += " AND NEIGHBORHOOD_ID = "+aiNhId;
			//System.out.println("######### getWbIdOnName() " +lsSql);
			preparedstatement = mConnection.getConnection().prepareStatement(lsSql);
			preparedstatement.setString(1, "%" + asWbName + "%");
			rs = preparedstatement.executeQuery(lsSql);
			if(rs.next())
			{
				liWbId = rs.getInt(1);
			}
			else
				System.out.println("\n\n ######### White Board with name '"+asWbName+"' is not present #########");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if ( rs != null ) {
					rs.close();
				}
				if ( preparedstatement != null ) {
					preparedstatement.close();
				}
			}
			catch(SQLException sqlexception1)
			{
				sqlexception1.printStackTrace();
			}
		}
		return liWbId;
	}

	//Parameters
	//	asCollabName - The name of the Collaboration.
	//	aiNhId - The Neighbourhood id to which this Collaboration belongs too.
	// This method returns the Collaboration id for the given Collaboration name. We can pass additional info like
	// Neighbourhood Id to get accurate result. If id is entered as "0"
	// then it is not considered in the query.
	public int getCollabIdOnName(String asCollabName, int aiNhId)
	{
		PreparedStatement preparedstatement = null;
		ResultSet rs		= null;
		String lsSql		= "";
		int liCollabId		= 0;
		try
		{
			//lsSql = " SELECT ID FROM BW_COLLAB WHERE NAME LIKE '%"+asCollabName+"%'";
			lsSql = " SELECT ID FROM BW_COLLAB WHERE NAME LIKE  ? ";
			if( aiNhId > 0 )
				lsSql += " AND NEIGHBORHOOD_ID = "+aiNhId;
			//System.out.println("######### getCollabIdOnName() " +lsSql);
			preparedstatement = mConnection.getConnection().prepareStatement(lsSql);
			rs = preparedstatement.executeQuery(lsSql);
			if(rs.next())
			{
				liCollabId = rs.getInt(1);
			}
			else
				System.out.println("\n\n ######### Collaboration with name '"+asCollabName+"' is not present #########");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if ( rs != null ) {
					rs.close();
				}
				if ( preparedstatement != null ) {
					preparedstatement.close();
				}
			}
			catch(SQLException sqlexception1)
			{
				sqlexception1.printStackTrace();
			}
		}
		return liCollabId;
	}

	//Parameters
	//	aliLevel - The Neighbourhood level.
	// This method returns the string representation of the neighbourhood level used in Nokia.
	public String getGeoTypeBasedOnLevelForNokia(int aliLevel)
	{
		switch (aliLevel)
		{
			case 1:
				return "SU";
			case 2:
				return "CO";
			case 3:
				return "SA";
			default :
				return "";
		}
	}

	//Parameters
	//	aiUserId - User Id in Boardwalk.
	//	asTermType - Short term (ST) or Mid Term (MT).
	// This method returns the cuboid id for the user, of which he/she is the member of.
	public int getCuboidForUserFromMembershipForNokia(int aiUserId, String asTermType)
	{
		PreparedStatement statement = null;
		ResultSet rs				= null;
		String lsSql				= "";
		String lsNhName				= "";
		int liLevel					= 0;
		String lsGeoType			= "";
		int liCuboidId				= 0;
		int lsNhId					= 0;
		String concatNhTermGeoType	= "";
		try
		{
			Vector mList = getMembershipListOfUser(aiUserId);
			//System.out.println("######### Membership List size >> "+mList.size());//Debug
			// take the first membership
			if (mList != null && mList.size() > 0)
			{
				for( int i = 0; i < mList.size(); i++)
				{
					//System.out.println("######### Getting Data for Mail for User >> "+aiUserId+" for his "+(i+1)+" membership");//Debug
					BoardwalkMember m = (BoardwalkMember)mList.get(i);
					if(m != null)
					{
						lsNhName	= m.getNeighborhoodName();
						lsNhId		= m.getNeighborhoodId();
						//To get the Neighbourhood level
						lsSql = " SELECT NEIGHBORHOOD_LEVEL FROM BW_NH WHERE ID = ?";
						//lsSql = " SELECT NEIGHBORHOOD_LEVEL FROM BW_NH WHERE ID = "+ lsNhId;
						//System.out.println("######### Neighbourhood level >> "+lsSql);//Debug
						statement = mConnection.getConnection().prepareStatement(lsSql);
						statement.setInt(1, lsNhId);
						rs = statement.executeQuery();
						rs.next();
						liLevel = rs.getInt(1);
						lsGeoType = getGeoTypeBasedOnLevelForNokia(liLevel);
						//System.out.println("######### lsGeoType >> "+lsGeoType+" liLevel >> "+liLevel);//Debug
						if(!lsGeoType.equals(""))
						{
							//To get the Cuboid Id
							concatNhTermGeoType = lsNhName+asTermType+lsGeoType;
							lsSql = " SELECT ID FROM BW_TBL WHERE NAME = ?";
							//lsSql = " SELECT ID FROM BW_TBL WHERE NAME = '"+ lsNhName+asTermType+lsGeoType+"'";
							//System.out.println("######### Cuboid >> "+lsSql);//Debug
							statement = mConnection.getConnection().prepareStatement(lsSql);
							statement.setString(1, concatNhTermGeoType);
							rs = statement.executeQuery();
							if(rs.next())
							{
								liCuboidId = rs.getInt(1);
							}
							else
								System.out.println("\n\n ######### No cuboid present for User with Id '"+aiUserId+"' with name as '"+lsNhName+asTermType+lsGeoType+"' #########");
						}
						else
							System.out.println("\n\n ######### User is member of Sales Area and we dont have cuboids at this level. ######### ");
					}
				}
			}
			else
				System.out.println("\n\n ######### User with Id '"+aiUserId+"' is not a member of any neighbourhood #########");
		}
		catch(Exception e)
		{
			try
			{
				rs.close();
				statement.close();
			}
			catch(SQLException sql)
			{
				sql.printStackTrace();
			}
			e.printStackTrace();
		}
		return liCuboidId;
	}

	//Parameters
	//	aiUserId - User Id in Boardwalk.
	// This method returns a List containing membership details of the user.
	public Vector getMembershipListOfUser(int aiUserId)
	{
		Vector mList = null;
		try
		{
			mList = UserManager.getMembershipListForUser(mConnection.getConnection(), aiUserId);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return mList;
	}

	public BoardwalkDistributionPacket getBWSFile(DistributionWorkBook workBook, String asTemplatePath, String[] asCuboids, int aINhId, int memberId, int aiUserId)
	{
		FileInputStream template = null;	
//		BoardwalkDistributionPacket bdp  = null;
		bdp  = null;
		try
		{
			template = new FileInputStream(asTemplatePath);
            System.out.println("Using template = " + asTemplatePath);
		}
		catch(Exception e)
		{
			System.out.println("\n\n ######### Template file not Found >> "+asTemplatePath+" #########");
			return null;
		}
		try
		{
			System.out.println(" BoardwalkDistributionPacket mConnection"+mConnection);
			bdp = new BoardwalkDistributionPacket(template, mConnection); 


			ArrayList laWorkSheets = workBook.getmsWorkSheets();
			int liCuboidIndex = 0;
			for(int liSheetCount = 0 ; liSheetCount < laWorkSheets.size() ; liSheetCount++)
			{
				DistributionTemplateWorkSheet WorkSheetObj = (DistributionTemplateWorkSheet)laWorkSheets.get(liSheetCount);
				ArrayList TableDisplay = WorkSheetObj.getmALTableDisplay();
				// count accessible cuboids in the sheet
				int cuboidCount = 0;
				for (int liTblCount = 0; liTblCount < TableDisplay.size(); liTblCount++)
				{
					try
					{
						DistributionTableDisplay TableDisplayObj = (DistributionTableDisplay)TableDisplay.get(liTblCount);

						int liCuboidId = 0;
						String[] lsCuboidDetail = BoardwalkUtility.getArrayFromStrTok(asCuboids[liCuboidIndex], "|");
						//System.out.println("######### XML Tbl - > "+TableDisplayObj.getmsTableDisplayName()+" UI Tbl - > "+lsCuboidDetail[0]);
						if (TableDisplayObj.getmsTableDisplayName().equals(lsCuboidDetail[0]) && !TableDisplayObj.getmsHidden().equalsIgnoreCase("yes"))
						{
							liCuboidId = Integer.parseInt(lsCuboidDetail[1]);
						}
						else
						{
							continue;
						}
					}
					catch (Exception e)
					{
						continue;
					}
					cuboidCount = cuboidCount + 1;
				}
				System.out.println("cuboidCount = " + cuboidCount);
				if (cuboidCount <=0)
				{
					continue; // skip this worksheet
				}

//				BoardwalkSheet bsData = bdp.addSheet(WorkSheetObj.getmsWorkSheetName());
				BoardwalkSheet bs = bdp.addSheet(WorkSheetObj.getmsWorkSheetName());
//				bsData.useTemplateSheet(WorkSheetObj.getmsUseTemplateSheet());
				bs.useTemplateSheet(WorkSheetObj.getmsUseTemplateSheet());

				ArrayList SheetAction = WorkSheetObj.getmALSheetAction();
				if(SheetAction != null && SheetAction.size() > 0)
				{
					for(int liActionCount = 0 ; liActionCount < SheetAction.size() ; liActionCount++)
					{

//						bsData.addExportAllAction("SubmitBtn","pre_exportall", "post_exportall");
//						bsData.addImportAllAction("RefreshBtn","pre_importall", "post_importall");
						bs.addExportAllAction("SubmitBtn","pre_exportall", "post_exportall");
						bs.addImportAllAction("RefreshBtn","pre_importall", "post_importall");
					}
				}
				
				if(liSheetCount == 0)
				{
					ArrayList OnLoadEvents = workBook.getmALOnLoadEvent();
					BoardwalkClientAction onload = bdp.getOnLoadAction();	
					for(int i = 0; i < OnLoadEvents.size(); i++)
					{
						if(OnLoadEvents.get(i) != null)
							onload.addArgument(processTags((String)OnLoadEvents.get(i)));  	
					}
				}
				
				for(int liTblCount = 0 ; liTblCount < TableDisplay.size() ; liTblCount++)
				{
					DistributionTableDisplay TableDisplayObj = (DistributionTableDisplay)TableDisplay.get(liTblCount);
					int liCuboidId = 0;
					String[] lsCuboidDetail = BoardwalkUtility.getArrayFromStrTok(asCuboids[liCuboidIndex], "|");
					//System.out.println("######### XML Tbl - > "+TableDisplayObj.getmsTableDisplayName()+" UI Tbl - > "+lsCuboidDetail[0]);
					if(TableDisplayObj.getmsTableDisplayName().equals(lsCuboidDetail[0]) && !TableDisplayObj.getmsHidden().equalsIgnoreCase("yes"))
					{
						liCuboidId = Integer.parseInt(lsCuboidDetail[1]);
						liCuboidIndex++;
						if(liCuboidIndex >= asCuboids.length )
							liCuboidIndex--;
					}
					else
						liCuboidId = getCuboidId(TableDisplayObj.getmsTableName(), TableDisplayObj.getmsWhiteBoardName(), TableDisplayObj.getmsCollabrationName());
					if(liCuboidId > 0)
					{
						//System.out.println("######### Adding Table Display "+TableDisplayObj.getmsTableDisplayName()+" with ID "+liCuboidId);
						//System.out.println("######### Packet 2 "+bdp);
//						BoardwalkDistributionManager.addTableToPacket(mConnection,bdp,liCuboidId,TableDisplayObj.getmsTableDisplayName());	
						bdp.addTableToPacket( liCuboidId,TableDisplayObj.getmsTableDisplayName());
						//System.out.println("######### Packet 3 "+bdp);
//						BoardwalkTableDisplay td0 = bdp.addTableDisplay(bsData.getName(), TableDisplayObj.getmsTableDisplayName());

						bdp.setActiveSheet(((DistributionTemplateWorkSheet)laWorkSheets.get(0)).getmsWorkSheetName()); 
						BoardwalkTableDisplay td0 = bdp.addTableDisplay(bs.getName(), TableDisplayObj.getmsTableDisplayName());
						td0.setTranspose(TableDisplayObj.getmbTranspose());

						// This code is added by Sanjeev and Sandhya on 07-06-2010
						//Change to Set the Mode based on Transpose flag in XML 
						if (td0.getTranspose())
							td0.setMode(1);
						else
							td0.setMode(0);

						td0.setPlacement(Integer.parseInt(TableDisplayObj.getmsXPos()), Integer.parseInt(TableDisplayObj.getmsYPos()));
						
						
						//BoardwalkTableContents btc0 = bdp.getTableContents(liCuboidId);
						//int numrows = btc0.getRows().size();
						//int numcols = btc0.getColumns().size();
						//System.out.println("######### TableId >> " + td0.getTableId()+"    numrows >> " + numrows+"    numcols >> " + numcols);
						//if(numrows == 0)
						//{
							//System.out.println("\n\n######### No rows present in the cuboid >> "+td0.getTableId()+" #########");

						//	return null;
						//}

						//td0.setTranspose(false);
						//td0.setPlacement(Integer.parseInt(TableDisplayObj.getmsXPos()), Integer.parseInt(TableDisplayObj.getmsYPos()));
						//BoardwalkColumnGroup bcg1 = new BoardwalkColumnGroup("", 1, numcols, false, false);
						//BoardwalkRowGroup brg1 = new BoardwalkRowGroup("", 1, numrows, false, false);
						//td0.addDataArea( brg1,bcg1, 0, 0,1,1, true);
					}
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}
		return bdp;
	}

	public InputStream sendFile(String asTemplate, String asCuboids, int aiNhId, int amemberId, int aiUserId)
	{
		String[] lsCuboids = BoardwalkUtility.getArrayFromStrTok(asCuboids, ",");
		String lsTemplatePath = "";
		DistributionWorkBook workBook = getWorkBook(asTemplate);

//		BoardwalkDistributionPacket bdp = getBWSFile(workBook, msTemplatePath, lsCuboids, aiNhId, amemberId, aiUserId);
		bdp = getBWSFile(workBook, msTemplatePath, lsCuboids, aiNhId, amemberId, aiUserId);
		//g2 27 may 11, need to cascade extension till last bws composition
		String TemplatePathforExtension = msTemplatePath;
		msTemplatePath = ""; // reset Template Path
		BoardwalkMember m = new BoardwalkMember(mConnection.getMemberId(),mConnection.getUserId(),mConnection.getNeighborhoodId(),mConnection.getNeighborhoodName());

System.out.println("--------m>< "+m );
System.out.println("-------bdp>< "+bdp );
		try
		{
			MimeBodyPart mbp = bdp.getAttachmentForMember(m, TemplatePathforExtension);
			return mbp.getInputStream();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}

	public String sendInvite(String asTemplate, String asCuboids, int aiNhId, int amemberId, int aiUserId,String laEmailmessage)
	{
		String[] lsCuboids = BoardwalkUtility.getArrayFromStrTok(asCuboids, ",");
		String lsTemplatePath = "";
		String TemplatePathforExtension = "";
		DistributionWorkBook workBook = getWorkBook(asTemplate);
		String lssuccess = "Success";

//		BoardwalkDistributionPacket bdp = getBWSFile(workBook, msTemplatePath, lsCuboids, aiNhId, amemberId, aiUserId);
		bdp = getBWSFile(workBook, msTemplatePath, lsCuboids, aiNhId, amemberId, aiUserId);
		TemplatePathforExtension = msTemplatePath;
		msTemplatePath = ""; // reset Template Path
		//System.out.println("######### BoardwalkDistributionPacket - > "+bdp);
		if(bdp != null)
		{
			BoardwalkMember m = new BoardwalkMember(mConnection.getMemberId(),mConnection.getUserId(),mConnection.getNeighborhoodId(),mConnection.getNeighborhoodName());

			String subjtext = msSubjectText;
			String bodytext = "";
			if(laEmailmessage.equals(""))
				bodytext = getBodyText(msMessagePath);
			else
				bodytext = laEmailmessage;
			System.out.println(" bodytext :"+bodytext);

			if(subjtext == null)
				subjtext = "";

			subjtext		= processTags(subjtext);
			bodytext		= processTags(bodytext);

			Properties props = new Properties();
			SMTPAuthenticator auth = new SMTPAuthenticator();
			System.out.println(" port = " + msport);
			props.put("mail.smtp.host", msmtpserver);
			props.put("mail.smtp.port", msport);
			props.put("mail.smtp.auth", "true");
			Session session = Session.getDefaultInstance(props, auth);
			session.setDebug(true);


			//System.out.println("%%%%%%%% bdp.tableIdsByExternalName "+bdp.tableIdsByExternalName);
			try
			{
				MimeBodyPart mbp = bdp.getAttachmentForMember(m,TemplatePathforExtension);
				// create a message
				MimeMessage msg = new MimeMessage(session);
				msg.setFrom(new InternetAddress(msmtpfrom));
				InternetAddress[] address = {new InternetAddress(msUserEmailId)};
				msg.setRecipients(Message.RecipientType.TO, address);

				msg.setSubject(subjtext);
				msg.setSentDate(new java.util.Date());
				// create and fill the first message part
				MimeBodyPart mbp1 = new MimeBodyPart();
				mbp1.setText(bodytext);
				//System.out.println("######### subjtext >> " + subjtext);//Debug
				//System.out.println("######### bodytextpath >> " + bodytextpath);//Debug
				// create the Multipart and its parts to it
				Multipart mp = new MimeMultipart();
				mp.addBodyPart(mbp1);
				mp.addBodyPart(mbp);

				// add the Multipart to the message
				//msg.setFileName("test.zip");
				msg.setContent(mp);
				// send the message
				Transport.send(msg);
			}
			catch (MessagingException mex)
			{
				lssuccess = "Failure";
				mex.printStackTrace();
				Exception ex = null;
				if ((ex = mex.getNextException()) != null)
				{
					ex.printStackTrace();
				}
			}
			catch (Exception e)
			{
				lssuccess = "Failure";
				e.printStackTrace();
			}
		}
		else
			lssuccess = "Failure";
		return lssuccess;
	}

	public DistributionWorkBook getWorkBook(String asTemplate)
	{
		boolean lbFound = false;
		DistributionWorkBook workBook = null;
		for(int liDbCount = 0 ; liDbCount < DistributionObjs.size() ; liDbCount++)
		{
			Distribution DistributionObj = (Distribution)DistributionObjs.get(liDbCount);
			ArrayList laTemplates = DistributionObj.getTemplates();
			for(int liTempCount = 0 ; liTempCount <  laTemplates.size() ; liTempCount++)
			{
				DistributionTemplate DistributionTemplateObj = (DistributionTemplate)laTemplates.get(liTempCount);
				if(asTemplate.equals(DistributionTemplateObj.getmsTemplateName()))
				{
					msTemplatePath	= DistributionTemplateObj.getmsTemplateLocation();
					msSubjectText	= DistributionTemplateObj.getmssubject();
					msMessagePath	= DistributionTemplateObj.getmsmessage();
					workBook = (DistributionWorkBook)DistributionTemplateObj.getmALTemplateWorkBook().get(0);
					lbFound = true;
					break;
				}
			}
			if(lbFound)
				break;
		}
		return workBook;
	}


	/*public String getMessage(String asTemplate)
	{
		boolean lbFound = false;
		DistributionWorkBook workBook = null;
		if(DistributionObjs != null)
		{
			for(int liDbCount = 0 ; liDbCount < DistributionObjs.size() ; liDbCount++)
			{
				Distribution DistributionObj = (Distribution)DistributionObjs.get(liDbCount);
				ArrayList laTemplates = DistributionObj.getTemplates();
				for(int liTempCount = 0 ; liTempCount <  laTemplates.size() ; liTempCount++)
				{
					DistributionTemplate DistributionTemplateObj = (DistributionTemplate)laTemplates.get(liTempCount);
					if(asTemplate.equals(DistributionTemplateObj.getmsTemplateName()))
					{
						msMessagePath	= DistributionTemplateObj.getmsmessage();
						System.out.println("@@In getMessage(String asTemplate)  msMessagePath >> " + msMessagePath);
						lbFound = true;
						break;
					}
				}
				if(lbFound)
					break;
			}
		}
		return msMessagePath;
	}*/


	public String sendUrlToUsers(String asSendTo)
	{
		String lsSuccess = "Success";

		Properties props = new Properties();
		SMTPAuthenticator auth = new SMTPAuthenticator();
		System.out.println(" port = " + msport);
		props.put("mail.smtp.host", msmtpserver);
		props.put("mail.smtp.port", msport);
		props.put("mail.smtp.auth", "true");
		Session session = Session.getDefaultInstance(props, auth);
		session.setDebug(true);

		//System.out.println("######### asSendTo >> " + asSendTo);//Debug

		String[] lsaTemplates = BoardwalkUtility.getArrayFromStrTok(asSendTo, "^");
		for(int liTempCount = 0 ; liTempCount < lsaTemplates.length ; liTempCount++)
		{
			String[] lsaTempDetail = BoardwalkUtility.getArrayFromStrTok(lsaTemplates[liTempCount], "|");
			String lsTemplateName = lsaTempDetail[0];
			lsTemplateName = URLEncoder.encode(lsTemplateName);
			DistributionWorkBook workBook = getWorkBook(lsTemplateName); // This is to just get the msSubjectText so please correct it later.

			//System.out.println("######### lsTemplateName >> " + lsTemplateName);//Debug
			for(int liNhCount = 1 ; liNhCount < lsaTempDetail.length ; liNhCount++)
			{
				String[] lsaNhandUserDetail = BoardwalkUtility.getArrayFromStrTok(lsaTempDetail[liNhCount], ",");
				//System.out.println("######### lsaNhandUserDetail >> " + lsaTempDetail[liNhCount]);//Debug
				String lsNhId = lsaNhandUserDetail[0];
				//System.out.println("######### lsNhId >> " + lsNhId);//Debug
				for(int liUserCount = 1; liUserCount < lsaNhandUserDetail.length; liUserCount++)
				{
					try
					{
						String lsUserEmailId = getEmailIdOnUserId(lsaNhandUserDetail[liUserCount]);
						String subjtext = msSubjectText;
						//System.out.println("######### msMessagePath >> " + msMessagePath);
						String bodytext = getBodyText(msMessagePath);
						bodytext		= bodytext + "\n\n" ;
						//bodytext		= bodytext + msserveraddress+"/InvitationManager?statictemplate="+lsTemplateName+"&staticnhid="+lsNhId;
						//System.out.println("######### lsUserEmailId >> " + lsUserEmailId);//Debug


						// create a message
						MimeMessage msg = new MimeMessage(session);
						msg.setFrom(new InternetAddress(msmtpfrom));
						InternetAddress[] address = {new InternetAddress(lsUserEmailId)};
						msg.setRecipients(Message.RecipientType.TO, address);

						System.out.println("#########---------------------------------> subjtext >> " + subjtext);//Debug
						System.out.println("#########---------------------------------> msMessagePath >> " + msMessagePath);//Debug
						msg.setSubject(subjtext);
						msg.setSentDate(new java.util.Date());
						// create and fill the first message part
						MimeBodyPart mbp1 = new MimeBodyPart();
						mbp1.setText(bodytext);
						// create the Multipart and its parts to it
						Multipart mp = new MimeMultipart();
						mp.addBodyPart(mbp1);
						msg.setContent(mp);
						// send the message
						Transport.send(msg);
					}
					catch (MessagingException mex)
					{
						lsSuccess = "Failure";
						mex.printStackTrace();
						Exception ex = null;
						if ((ex = mex.getNextException()) != null)
						{
							ex.printStackTrace();
						}
					}
					catch (Exception e)
					{
						lsSuccess = "Failure";
						e.printStackTrace();
					}
				}
			}
		}
		//System.out.println("######### lsSuccess >> " + lsSuccess);//Debug
		return lsSuccess;
	}

	public static String getBodyText(String asPath)
	{
		String lsRetVal = "";
		try
		{
			if(asPath == null || asPath.equals(""))
				return "";
			File lfFile	= new File(asPath);
			if(lfFile.exists())
			{
				FileInputStream fis = new FileInputStream(lfFile);
				int liSize		= fis.available();
				byte[] array	= new byte[liSize];
				fis.read(array);
				fis.close();
				lsRetVal = new String(array);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return lsRetVal;
	}

	public String processTags(String asbodytext)
	{
		if(asbodytext.indexOf("%USERNAME%") > -1)
		{
			String lsUserName = getUserInfoOnEmailId(msUserEmailId);
			if(lsUserName == null || lsUserName.trim().equals(""))
				lsUserName = msUserEmailId;
			asbodytext = replaceString(asbodytext, "%USERNAME%", lsUserName);
		}
		if(asbodytext.indexOf("%USERID%") > -1)
		{
			String lsUserId = getUserIdForEmailId(msUserEmailId);
			if(lsUserId == null || lsUserId.trim().equals(""))
				lsUserId = "0";
			asbodytext = replaceString(asbodytext, "%USERID%", lsUserId);
		}
		if(asbodytext.indexOf("%DATETIME%") > -1)
		{
			SimpleDateFormat formatter   = new SimpleDateFormat ("MMM dd, yyyy hh:mm:ssa");
			String lsDateTime = formatter.format(Calendar.getInstance().getTime());
			asbodytext = replaceString(asbodytext, "%DATETIME%", lsDateTime);
		}
		if(asbodytext.indexOf("%LOGINID%") > -1)
		{
			asbodytext = replaceString(asbodytext, "%LOGINID%", msUserEmailId);
		}

		return asbodytext;
	}

	public static String replaceString(String sStr, String sSearch, String sReplace)
	{
		if(sStr == null) return "";

		StringBuffer sReturn= new StringBuffer();
		int lp=0;
		int pos=0;
		for(int i=0; i<sStr.length();i++)
		{
			pos=sStr.indexOf(sSearch,pos);
			if (pos<0 )
			{
				sReturn.append(sStr.substring(lp,sStr.length()));
				break;
			}
			sReturn.append(sStr.substring(lp,pos)+sReplace);
			pos=pos+sSearch.length();
			lp=pos;
		}
		return sReturn.toString();
	}

	public void deleteBwsFile()
	{
		bdp.deleteTempFile();
	}

	class SMTPAuthenticator extends Authenticator {
				public PasswordAuthentication getPasswordAuthentication()
				{
					//System.out.println(" login = " + msmtpfrom + " password = " + mspassword );
					return new PasswordAuthentication(msmtpfrom, mspassword);
				}
	}

 }