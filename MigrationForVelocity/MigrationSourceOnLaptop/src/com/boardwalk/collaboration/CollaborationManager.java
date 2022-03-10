package com.boardwalk.collaboration;

import java.util.*;
import java.io.*;

import com.boardwalk.database.*;
import com.boardwalk.exception.*;
import com.boardwalk.table.*;
import com.boardwalk.whiteboard.*;



import java.sql.*;                  // JDBC package
import javax.sql.*;                 // extended JDBC package



public class CollaborationManager{

    private static String CALL_BW_COPY_COLLAB="{CALL BW_COPY_COLLAB(?,?,?,?)}";
    private static String CALL_BW_CR_COLLAB="{CALL BW_CR_COLLAB(?,?,?,?,?,?)}";
    private static String  CALL_BW_CR_BL="{CALL BW_CR_BL(?,?,?,?,?)}";
    /*
    private static String BW_GET_COLLAB_LIST_BY_USER=""+
    " SELECT DISTINCT (COLLAB.ID) , COLLAB.NAME, COLLAB.PURPOSE,COLLAB.IS_ACTIVE, 'ACCESS' = "+
    " CASE "+
    " WHEN ( COLLAB.NEIGHBORHOOD_ID = FRIEND.NEIGHBORHOOD_ID AND FRIEND.FRIEND_NH_ID = MY_NH.ID ) THEN COLLAB.FRIEND_ACCESS"+
    " WHEN ( COLLAB.NEIGHBORHOOD_ID = PEER.NEIGHBORHOOD_ID   AND   PEER.PEER_NH_ID = MY_NH.ID ) THEN COLLAB.PEER_ACCESS"+
    " WHEN ( COLLAB.NEIGHBORHOOD_ID = MY_NH.ID ) THEN COLLAB.PRIVATE_ACCESS"+
    " ELSE 0"+
    " END"+
    " FROM BW_COLLAB AS COLLAB, BW_FRIEND_NHS AS FRIEND, BW_PEER_NHS AS PEER, BW_MEMBER AS MEMBER, BW_NH AS MY_NH"+
    " WHERE  "+
    "	   ("+
    "	     ( COLLAB.NEIGHBORHOOD_ID = MY_NH.ID"+
    "	     AND COLLAB.PRIVATE_ACCESS >= 1"+
    "	     OR  COLLAB.MEMBER_ID = MEMBER.ID"+
    "	     )"+
    "            OR "+
    "           ( COLLAB.NEIGHBORHOOD_ID = FRIEND.NEIGHBORHOOD_ID"+
    "	     AND   FRIEND.FRIEND_NH_ID = MY_NH.ID"+
    "             AND   COLLAB.FRIEND_ACCESS >= 1"+
    "           )"+
    "             OR"+
    "           ( COLLAB.NEIGHBORHOOD_ID = PEER.NEIGHBORHOOD_ID"+
    "             AND   PEER.PEER_NH_ID = MY_NH.ID"+
    "             AND   COLLAB.PEER_ACCESS >= 1"+
    ""+
    "           ))"+
    "	     AND MY_NH.ID = MEMBER.NEIGHBORHOOD_ID"+
    "             AND MEMBER.USER_ID = ?"+
    "             AND COLLAB.IS_ACTIVE=?";
     */
    private static String CALL_BW_GET_COLLABS_FOR_NH = "{CALL BW_GET_COLLABS_FOR_NH(?)}";
    private static String BW_GET_COLLAB_INFO_FOR_USER = "{CALL BW_GET_COLLAB_INFO_FOR_USER(?,?)}";
	private static String CALL_BW_GET_COLLAB_STATUS = "{CALL BW_GET_COLLAB_STATUS(?)}";
	private static String CALL_BW_GET_COLLAB_ACTIVITY = "{CALL BW_GET_COLLAB_ACTIVITY(?,?,?)}";
    private static String BW_GET_BLS_BY_COLLAB = "{CALL BW_GET_BLS_BY_COLLAB(?,?)}";
    private static String CALL_BW_GET_COLLAB_ID_BY_NAME = "{CALL BW_GET_COLLAB_ID_BY_NAME(?,?)}";
    public CollaborationManager() {}


	public static int getCollabIdByName ( Connection connection, String collabName, int nhid)
	throws SystemException
	{
        ResultSet resultset = null;
        PreparedStatement preparedstatement = null;
        int collabId = -1;
        try
        {
            preparedstatement = connection.prepareCall(CALL_BW_GET_COLLAB_ID_BY_NAME);
            preparedstatement.setString(1,collabName);
            preparedstatement.setInt(2,nhid);
            resultset = preparedstatement.executeQuery();

            while ( resultset.next() )
            {
                collabId = resultset.getInt("ID");
            }

            return collabId;
        }
        catch(SQLException sqlexception)
        {
            throw new SystemException(sqlexception);
        }
        finally
        {
            try
            {
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

    public static void purgeCollaboration( Connection connection, int collabId)
    throws SystemException
    {
        PreparedStatement callableStatement = null;
        String BW_PURGE_COLLAB = "{CALL BW_PURGE_COLLAB(?)}";

        try {
            callableStatement = connection.prepareStatement(BW_PURGE_COLLAB);
            callableStatement.setInt(1, collabId);
            callableStatement.executeUpdate();
        } catch (SQLException sql1 ) {
                throw new SystemException(sql1);
        } finally {
            try {
                callableStatement.close();
            }catch (SQLException sql2) {
                throw new SystemException(sql2);
            }
        }
    }

    public static void purgeBaseline (Connection connection, int baselineId )
    throws SystemException
    {
        CallableStatement callableStatement = null;
        String BW_PURGE_BL = "{CALL BW_PURGE_BL(?)}";

        try {
            callableStatement = connection.prepareCall(BW_PURGE_BL);
            callableStatement.setInt(1, baselineId);
            callableStatement.executeUpdate();
        } catch (SQLException sql1) {
            throw new SystemException(sql1);
        } finally {
            try {
                callableStatement.close();
            } catch (SQLException sql2) {
                throw new SystemException(sql2);
            }
        }
    }


	public static Vector getCollaborationsOfNeighborhood( Connection connection, int a_nhid)
	throws SystemException {
        ResultSet resultset = null;
        PreparedStatement preparedstatement = null;
        Vector Collaborations = new Vector();
        try {

            preparedstatement = connection.prepareCall(CALL_BW_GET_COLLABS_FOR_NH);
            preparedstatement.setInt(1,a_nhid);
            resultset = preparedstatement.executeQuery();

            while ( resultset.next() )
            {
                int a_collab_id;
                String a_name;
                String a_purpose;
                a_collab_id = resultset.getInt("ID");
                a_name = resultset.getString("NAME");
                a_purpose = resultset.getString("PURPOSE");



                CollaborationTreeNode  c = new CollaborationTreeNode( a_collab_id, a_name,a_purpose);
                Collaborations.addElement(c);
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


    public static Hashtable  getStatus(
									Connection connection,
									int a_collaboration_id )
									throws SystemException
    {
        ResultSet resultset = null;
        PreparedStatement preparedstatement = null;
        Collaboration collab = null;
        Hashtable cTables = new Hashtable();
        try
        {
			preparedstatement = connection.prepareCall(CALL_BW_GET_COLLAB_STATUS);
			preparedstatement.setInt(1,a_collaboration_id);

            resultset = preparedstatement.executeQuery();

            while ( resultset.next() )
            {
				/*
				  table_tid.id,
				  updtx.tx_id as updatetid,
				  updusr.email_address as updatedby,
				  updtx.created_on as updatedon,
  				  updtx.comment_ as updatecomment
  				*/
				int tableId;
				int createdtid;
				String createdby;
				java.sql.Timestamp createdon;
				int updatetid;
				String updatedby;
				java.sql.Timestamp updatedon;
				String updatecomment;

				Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
				tableId = resultset.getInt("id");
				createdtid = resultset.getInt("createdtid");
				createdby = resultset.getString("createdby");
				createdon = resultset.getTimestamp("createdon", cal);
				updatetid = resultset.getInt("updatetid");
				updatedby = resultset.getString("updatedby");
				updatedon = resultset.getTimestamp("updatedon", cal);
				updatecomment = resultset.getString("updatecomment");

				TableUpdateSummary tus = new TableUpdateSummary(tableId,
																createdtid,
																createdby,
																createdon.getTime(),
																updatetid,
																updatedby,
																updatedon.getTime(),
																updatecomment);

				cTables.put(new Integer(tableId), tus);

            }
            return cTables;
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

    public static Hashtable  getActivity(
									Connection connection,
									int a_collaboration_id,
									long endDate,
									long startDate)
									throws SystemException
    {
        ResultSet resultset = null;
        PreparedStatement preparedstatement = null;
        Collaboration collab = null;
        Hashtable cTables = new Hashtable();
        try
        {
			preparedstatement = connection.prepareCall(CALL_BW_GET_COLLAB_ACTIVITY);
			preparedstatement.setInt(1,a_collaboration_id);
			Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));

			preparedstatement.setTimestamp(2, new java.sql.Timestamp(endDate), cal);
			preparedstatement.setTimestamp(3, new java.sql.Timestamp(startDate), cal);

            resultset = preparedstatement.executeQuery();

            while ( resultset.next() )
            {
				/*
 					bw_tbl.id,
 					updcnt=count (distinct bw_txs.tx_id),
 					action='cellstringupdate'
  				*/
				int tableId = resultset.getInt("id");
				int updcnt = resultset.getInt("updcnt");
				String updaction = resultset.getString("action");

				System.out.println(tableId+"::"+updcnt+"::"+updaction);

				TableActivitySummary tas = null;

				tas = (TableActivitySummary)cTables.get(new Integer(tableId));
				if (tas == null)
				{
					tas = new TableActivitySummary(tableId);
					tas.addItem(updcnt,updaction);
				}
				else
				{
					tas.addItem(updcnt, updaction);
				}

				cTables.put(new Integer(tableId), tas);

            }
            return cTables;
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


    public static Collaboration getCollaborationInfo( Connection connection, int a_user_id, int a_collaboration_id ) throws SystemException {
        ResultSet resultset = null;
        PreparedStatement preparedstatement = null;
        Collaboration collab = null;
        try {
			preparedstatement = connection.prepareCall(BW_GET_COLLAB_INFO_FOR_USER);
			preparedstatement.setInt(1,a_collaboration_id);
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
                a_collab_id = resultset.getInt("ID");
                a_name = resultset.getString("NAME");
                a_purpose = resultset.getString("PURPOSE");
                q_status = resultset.getInt("IS_ACTIVE");
                q_access = resultset.getInt("ACCESS_");
                q_private_access = resultset.getInt("PRIVATE_ACCESS");
                q_peer_access= resultset.getInt("PEER_ACCESS");
                q_friend_access= resultset.getInt("FRIEND_ACCESS");
                q_nh_name = resultset.getString("COLLAB_NH_NAME");



                collab = new Collaboration( a_collab_id, a_name,a_purpose,"", q_access,q_nh_name,q_status,q_private_access,q_peer_access, q_friend_access   );
            }
                return collab;
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



    public static Vector getBaselineList( Connection connection, int a_user_id, int a_collaborationid) throws SystemException {
        ResultSet resultset = null;
        PreparedStatement preparedstatement = null;
        Vector Baselines = new Vector();


        try {
            preparedstatement = connection.prepareStatement(BW_GET_BLS_BY_COLLAB );
            preparedstatement.setInt(1,a_collaborationid);
            preparedstatement.setInt(2,a_user_id);
            resultset = preparedstatement.executeQuery();
            while ( resultset.next() ) {
                int a_baseline_id;
                String a_baseline_name;
                String a_baseline_description;
                String a_createdBy;
              	java.sql.Timestamp t;

                a_baseline_id = resultset.getInt("BASELINE_ID");
                a_baseline_name = resultset.getString("BASELINE_NAME");
                a_baseline_description = resultset.getString("BASELINE_DESCRIPTION");
                a_createdBy = resultset.getString("EMAIL_ADDRESS");
                t = resultset.getTimestamp("CREATED_ON",Calendar.getInstance(TimeZone.getTimeZone("GMT")));


                Baseline b = new Baseline( a_baseline_id, a_baseline_name,a_baseline_description,a_createdBy,t);
                Baselines.addElement(b);
            }
            return Baselines;

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

    public static int createCollaboration(
        Connection a_connection,
        String a_name,
        String  a_purpose,
        int a_memberid,
        int a_tid,
        int a_status
        ) throws BoardwalkException
    {

        int m_collaborationid = -1;

        CallableStatement callablestatement = null;

        try {
            callablestatement = a_connection.prepareCall(CALL_BW_CR_COLLAB);
            callablestatement.setString(1,a_name);
            callablestatement.setString(2,a_purpose);
            callablestatement.setInt(3,a_memberid);
            callablestatement.setInt(4,a_tid);
            callablestatement.setInt(5,a_status);

            callablestatement.registerOutParameter(6,java.sql.Types.INTEGER);

            int result = callablestatement.executeUpdate();

            m_collaborationid= callablestatement.getInt(6);

        }
        catch( SQLException sql ) {
            if ( sql.getErrorCode() == 2627 ) {
                throw new BoardwalkException( 10000, sql );
            }
            else {
                sql.printStackTrace();
            }
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

        return m_collaborationid;
    }

    public static int createBaseline(
    Connection a_connection,
    String a_name,
    String  a_description,
    int a_collaboration_id,
    int a_tid
    )
    throws BoardwalkException {
        /*
         *BW_CR_BL
            (
            @NAME NVARCHAR(32),
            @DESCRIPTION NVARCHAR(32),
            @COLLABORATION_ID INTEGER,
            @TX_ID INTEGER,
            @BASELINE_ID INTEGER OUTPUT
             )

         */
        CallableStatement callablestatement = null;
        int m_baselineId = -1;

        try {
            callablestatement = a_connection.prepareCall(CALL_BW_CR_BL);
            callablestatement.setString(1,a_name);
            callablestatement.setString(2,a_description);
            callablestatement.setInt(3,a_collaboration_id);
            callablestatement.setInt(4,a_tid);
            callablestatement.registerOutParameter(5,java.sql.Types.INTEGER);
            int result = callablestatement.executeUpdate();
            m_baselineId= callablestatement.getInt(5);
        }
        catch( SQLException sql ) {
            sql.printStackTrace();

        }finally
        {
			try
			{
                            callablestatement.close();

			}
			catch( SQLException sql )
			{

			}
		}

        return m_baselineId;
    }
    public static void copyCollaboration(
                Connection a_connection,
                int a_sourceCollaborationId,
                int a_targetCollaborationId,
               boolean copyStructure,
				boolean									   copyLatestContent,
				boolean								   copyDesignValues,
				boolean								   copyUIPreferences,
				boolean						   		copyAccess,
				int			   		memberId,
                int a_tid
                ) throws BoardwalkException
    {
        CallableStatement callablestatement = null;
		int i_copyStructure = copyStructure? 1:0;
		int i_copyLatestContent= copyLatestContent? 1:0;
		int i_copyDesignValues= copyDesignValues? 1:0;
		int i_copyUIPreferences= copyUIPreferences? 1:0;
		int i_copyAccess= copyAccess? 1:0;

						/*
						callablestatement.setInt(2,i_copyAccess);
						callablestatement.setInt(3,i_copyUIPreferences);
						callablestatement.setInt(4,i_copyStructure);
						callablestatement.setInt(5,i_copyLatestContent);
						callablestatement.setInt(6,i_copyDesignValues);

        */

        try {
            callablestatement = a_connection.prepareCall(CALL_BW_COPY_COLLAB);
            callablestatement.setInt(1,a_sourceCollaborationId);
            callablestatement.setInt(2,a_targetCollaborationId);
           	callablestatement.setInt(3,i_copyLatestContent);
            callablestatement.setInt(4,a_tid);
            int result = callablestatement.executeUpdate();

        }
        catch( SQLException sql ) {
            sql.printStackTrace();
        }finally
        {
			try
			{
                            callablestatement.close();

			}
			catch( SQLException sql )
			{

			}
		}
    }



};


