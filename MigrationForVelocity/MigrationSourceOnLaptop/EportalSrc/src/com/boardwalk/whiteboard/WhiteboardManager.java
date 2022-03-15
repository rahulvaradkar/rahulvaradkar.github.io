package com.boardwalk.whiteboard;

import java.util.*;
import java.io.*;

import com.boardwalk.database.*;
import com.boardwalk.exception.NeighborhoodException;
import com.boardwalk.exception.SystemException;
import com.boardwalk.table.*;
import com.boardwalk.collaboration.*;


import java.sql.*;                  // JDBC package
import javax.sql.*;                 // extended JDBC package



public class WhiteboardManager{

    private static String CALL_BW_CR_WB="{CALL BW_CR_WB(?,?,?,?,?,?,?,?)}";

    private static String CALL_BW_GET_WBS_BY_COLLAB_AND_BL="{CALL BW_GET_WBS_BY_COLLAB_AND_BL(?,?)}";
    private static String CALL_BW_GET_WB_INFO_FOR_USER="{CALL BW_GET_WB_INFO_FOR_USER(?,?)}";
    private static String  CALL_BW_GET_COLLAB_TBLS_FOR_MEMBER = "{CALL BW_GET_COLLAB_TBLS_FOR_MEMBER(?,?)}";

    public WhiteboardManager() {}


	public static int getIdByCollabAndName(Connection connection, int collabId, String wbName)
	throws SystemException
	{
		ResultSet resultset = null;
		PreparedStatement preparedstatement = null;
		int wbId = -1;
		try
		{
			preparedstatement = connection.prepareStatement("SELECT ID FROM BW_WB WHERE BW_COLLAB_ID = ? AND NAME = ?");
			preparedstatement.setInt(1, collabId);
			preparedstatement.setString(2, wbName);
			resultset = preparedstatement.executeQuery();

			while (resultset.next())
			{
				wbId = resultset.getInt("ID");
			}

			return wbId;
		}
		catch (SQLException sqlexception)
		{
			throw new SystemException(sqlexception);
		}
		finally
		{
			try
			{
				if (resultset != null)
					resultset.close();
				if (preparedstatement != null)
					preparedstatement.close();
			}
			catch (SQLException sqlexception1)
			{
				throw new SystemException(sqlexception1);
			}
		}
	}

    public static void purgeWhiteboard(Connection connection, int wbid)
    throws SystemException
    {
        String CALL_BW_PURGE_WB="{CALL BW_PURGE_WB(?)}";
        CallableStatement cs = null;

        try {
            System.out.println("purgeWhiteboard : Purging whiteboard " + wbid + " from database");
            cs = connection.prepareCall(CALL_BW_PURGE_WB);
            cs.setInt(1, wbid);
            cs.executeUpdate();
        } catch (SQLException sqle1) {
            throw new SystemException (sqle1);
        } finally {
            try {
                cs.close();
            } catch (SQLException sqle2) {
                throw new SystemException(sqle2);
            }
        }
    }

    public static WhiteboardInfo getWhiteboardInfo( Connection connection, int a_user_id, int a_whiteboard_id ) throws SystemException {
        ResultSet resultset = null;
        PreparedStatement preparedstatement = null;
        WhiteboardInfo wb = null;
        try {
            preparedstatement = connection.prepareStatement(CALL_BW_GET_WB_INFO_FOR_USER);
            preparedstatement.setInt(1,a_whiteboard_id);
            preparedstatement.setInt(2,a_user_id);
            resultset = preparedstatement.executeQuery();

            if ( resultset.next() ) {
                int a_collab_id;
                String a_name;
                String a_purpose;
                int q_status;
                int q_access;
                int q_private_access;
                int q_friend_access;
                int q_peer_access;
                String q_nh_name;

                int a_wb_id;
                String a_wb_name;
                int a_wb_status;

                a_collab_id = resultset.getInt("ID");
                a_name = resultset.getString("NAME");
                a_purpose = resultset.getString("PURPOSE");
                q_status = resultset.getInt("IS_ACTIVE");
                q_access = resultset.getInt("ACCESS");
                q_private_access = resultset.getInt("PRIVATE_ACCESS");
                q_peer_access= resultset.getInt("PEER_ACCESS");
                q_friend_access= resultset.getInt("FRIEND_ACCESS");
                q_nh_name = resultset.getString("COLLAB_NH_NAME");

				a_wb_name = resultset.getString("WB_NAME");

                a_wb_id = resultset.getInt("WB_ID");
                a_wb_status = resultset.getInt("WB_IS_ACTIVE");


                wb = new WhiteboardInfo( a_collab_id, a_name,a_purpose,"", q_access,q_nh_name,q_status,q_private_access,q_peer_access, q_friend_access, a_wb_id, a_wb_name, a_wb_status   );
            }
                return wb;
        }
        catch(SQLException sqlexception) {
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
    }

  public static Vector  getWhiteboardAndTablesByCollaborationAndNeighborhood( Connection connection, int a_collab_id, int a_memberId) throws SystemException {


	        ResultSet resultset = null;
	        PreparedStatement preparedstatement = null;

	        Vector Collaborations = new Vector();

	        CollaborationTreeNode currentCollab = null;

	        WhiteboardTreeNode currentWhiteboard = null;

	        TableTreeNode currentTable = null;

	        String currentRelationship = null;






	        try {




				if ( DatabaseLoader.getDatabaseType().trim().equalsIgnoreCase("ORACLE"))
				{
					preparedstatement = DatabaseLoader.getPreparedStatementFromPreLoadedQueries("BW_GET_COLLAB_TBLS_FOR_MEMBER", connection );
					preparedstatement.setInt(1,a_memberId);
					preparedstatement.setInt(2,a_collab_id);
					preparedstatement.setInt(3,a_memberId);
					preparedstatement.setInt(4,a_collab_id);
					preparedstatement.setInt(5,a_memberId);
					preparedstatement.setInt(6,a_collab_id);
					preparedstatement.setInt(7,a_collab_id);
					preparedstatement.setInt(8,a_collab_id);
					preparedstatement.setInt(9,a_collab_id);


				}
				else
				{
					preparedstatement = connection.prepareCall(CALL_BW_GET_COLLAB_TBLS_FOR_MEMBER);
					preparedstatement.setInt(1,a_collab_id);
	            	preparedstatement.setInt(2,a_memberId);
				}

	            resultset = preparedstatement.executeQuery();

	            while ( resultset.next() )
	            {
	                int a_collaboration_id;
	                String a_collab_name;
	                String a_collab_purpose;

	                int a_wb_id;
					String a_wb_name;

					int a_table_id;
					String a_table_name;
					String a_table_purpose;

	                String a_relationship;
					int a_access;

	                a_collaboration_id = resultset.getInt("COLLAB_ID");
	                a_collab_name = resultset.getString("COLLAB_NAME");
	                a_collab_purpose = resultset.getString("COLLAB_PURPOSE");
	                a_wb_id = resultset.getInt("WB_ID");
	                a_wb_name = resultset.getString("WB_NAME");
	                a_table_name= resultset.getString("TABLE_NAME");
	                a_table_purpose= resultset.getString("TABLE_PURPOSE");
	                a_relationship= resultset.getString("REL");
	                a_table_id = resultset.getInt("TABLE_ID");
	                a_access= resultset.getInt("ACCESS_");
					if ( currentCollab == null || currentCollab.getId() != a_collaboration_id )
					{
							// Add new Collaboration
							CollaborationTreeNode ctn = new CollaborationTreeNode ( a_collaboration_id,a_collab_name, a_collab_purpose  );
							Collaborations.add( ctn );
							currentCollab = ctn;
					}

					if ( currentWhiteboard == null || currentWhiteboard.getId() != a_wb_id )
					{
							// Add new Whiteboard
							if ( a_wb_id != -1 )
							{
								WhiteboardTreeNode wbtn = new WhiteboardTreeNode ( a_wb_id, a_collaboration_id ,a_wb_name );
								currentCollab.getWhiteboards().add(wbtn);
								currentWhiteboard = wbtn;
							}
					}

					if ( currentTable == null || currentTable.getId() != a_table_id )
					{
							// Add new Table
							if ( a_table_id != -1 )
							{
								TableTreeNode tbtn = new TableTreeNode ( a_table_id, a_wb_id, a_table_name ,a_table_purpose , a_access);
								currentWhiteboard.getTables().add(tbtn);
								currentTable = tbtn;
							}
					}
					else
					{
						 if ( currentTable.getId() == a_table_id )
						 {
								int currentAccess = currentTable.getAccess();
								int newAccess = currentAccess | a_access;
								currentTable.setAccess( newAccess );
						 }
					}

	            }

	            return Collaborations;
	        }
	        catch(SQLException sqlexception) {
	            throw new SystemException(sqlexception);
	        }
	        finally {
	            try {

					if ( resultset != null )
	                		resultset.close();
	               if ( preparedstatement != null )
	                	preparedstatement.close();
	            }
	            catch(SQLException sqlexception1) {
	                throw new SystemException(sqlexception1);
	            }
	        }
    }



    public static Hashtable getWhiteboardsAndTablesByCollaborationAndBaseline( Connection connection, int a_collaboration_id, int a_baseline_id, int a_member_id)
    throws SystemException {


        ResultSet resultset = null;
        PreparedStatement preparedstatement = null;
        Hashtable ht = new Hashtable();

        try {
            preparedstatement = connection.prepareStatement(CALL_BW_GET_WBS_BY_COLLAB_AND_BL);
            preparedstatement.setInt(1,a_collaboration_id);
            preparedstatement.setInt(2,a_baseline_id);

            resultset = preparedstatement.executeQuery();
            while ( resultset.next() )
            {
                int a_id;
                String a_name;
                int q_status;
                int a_sequence_number;
                int a_neighborhood_id;

                a_id = resultset.getInt("WHITEBOARD_ID");
                a_name = resultset.getString("WHITEBOARD_NAME");
                a_sequence_number = resultset.getInt("WHITEBOARD_SEQUENCE_NUMBER");
                q_status = resultset.getInt("WHITEBOARD_IS_ACTIVE");
                a_neighborhood_id = resultset.getInt("NEIGHBORHOOD_ID");

                com.boardwalk.whiteboard.Whiteboard wb = new Whiteboard(a_id, a_collaboration_id, a_name, a_neighborhood_id, q_status, a_sequence_number);
                ht.put(wb, new Vector() );
            }
				  if ( resultset != null )
				{
					resultset.close();
					resultset = null;
				}

				if ( preparedstatement != null )
				{
						preparedstatement.close();
						preparedstatement = null;
          	   }

		   if ( ht.size() > 0 )
				{

						Enumeration wbs = ht.keys();

						while ( wbs.hasMoreElements() )
						{
								Whiteboard wb = (Whiteboard)wbs.nextElement();
								Vector tablesVector = TableManager.getTablesForWBAndBaseline(connection, wb.getId() , a_baseline_id, a_member_id);
								ht.put(wb,  tablesVector );
						}

			}

            return ht;
        }
        catch(SQLException sqlexception)
        {
            throw new SystemException(sqlexception);
        }
        finally {
            try {
                if ( resultset != null ) {
                    resultset.close();
                }
                if ( preparedstatement != null ) {
                    preparedstatement.close();
                }
            }
            catch(SQLException sqlexception1) {
                throw new SystemException(sqlexception1);
            }
        }
    }

    public static int createWhiteboard(
    Connection a_connection,
    String a_name,
    int a_peer_access, int a_private_access, int a_friend_access,
    int a_collaboration_id,
    int a_tid,
    int a_status
    ) throws SQLException {
        /*
         *  @COLLABORATION_ID INTEGER,
            @NAME NVARCHAR(32),
            @PEER_ACCESS INTEGER,
            @PRIVATE_ACCESS INTEGER,
            @FRIEND_ACCESS INTEGER,
            @TX_ID INTEGER,
            @STATUS INTEGER,
            @WHITEBOARD_ID INTEGER OUTPUT
         **/

        int m_whiteboard_id = -1;

        CallableStatement callablestatement = null;
        callablestatement = a_connection.prepareCall(CALL_BW_CR_WB);
        callablestatement.setInt(1,a_collaboration_id);
        callablestatement.setString(2,a_name);
        callablestatement.setInt(3,a_peer_access);
        callablestatement.setInt(4,a_private_access);
        callablestatement.setInt(5,a_friend_access);
        callablestatement.setInt(6,a_tid);
        callablestatement.setInt(7,a_status);

        callablestatement.registerOutParameter(8,java.sql.Types.INTEGER);
        int result = callablestatement.executeUpdate();
        m_whiteboard_id= callablestatement.getInt(8);

		callablestatement.close();

        return m_whiteboard_id;
    }

    public static void TestcreateWhiteboard(int a_collaboration_id, String whiteboardName) {
        Connection connection = null;
        TransactionManager tm = null;
        int a_whiteboard_id = -1;
        try {
            DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
            connection = databaseloader.getConnection();
            tm = new TransactionManager(connection, 2);
            int tid = tm.startTransaction();
            a_whiteboard_id = WhiteboardManager.createWhiteboard(
            connection,
            whiteboardName,
            1, 2, 0,
            a_collaboration_id,
            tid,
            1
            );
            tm.commitTransaction();

        }
        catch ( SQLException e ) {
            e.printStackTrace();
            try {
                tm.rollbackTransaction();
            }
            catch( SQLException sqlfatal ) {
                sqlfatal.printStackTrace();
            }
        }
        finally {

            try {
                tm.commitTransaction();
                connection.close();
            }
            catch ( SQLException sql ) {
                sql.printStackTrace();
            }
        }
    }





};


