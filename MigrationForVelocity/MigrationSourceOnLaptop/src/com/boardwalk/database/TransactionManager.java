/*
 * Transaction.java
 *
 * Created on April 16, 2001, 9:08 PM
 */

package com.boardwalk.database;


import java.util.*;
import java.io.*;


import java.sql.*;                  // JDBC package
import javax.sql.*;                 // extended JDBC package
/**
 *
 * @author  administrator
 * @version
 */
public class TransactionManager {

    Connection m_connection;
    int        m_tid = 0;
    int        m_user_id;


    /** Creates new Transaction */
    private static String BW_GET_NEW_TX="{CALL BW_GET_NEW_TX_ID(?,?)}";
    private static String BW_GET_NEW_TX_WITH_COMMENT="{CALL BW_GET_NEW_TX_ID_WITH_COMMENT(?,?,?,?)}";
	private static String BW_GET_TX_ID_INFORMATION="{CALL BW_GET_TX_ID_INFORMATION(?,?)}";
	private static String BW_GET_TX_INFO="{CALL BW_GET_TX_INFO(?)}";
    private static String BW_CREATE_SIGNIFICANT_UPDATE ="{CALL BW_CREATE_SIGNIFICANT_UPDATE(?,?,?,?)}";


    public TransactionManager( Connection a_connection, int a_user_id ) throws SQLException
    {
        m_connection = a_connection;
        m_user_id = a_user_id;
        m_connection.setAutoCommit(false);


    }

    public int startTransaction() throws SQLException
    {
        CallableStatement callablestatement = null;
        callablestatement = m_connection.prepareCall(BW_GET_NEW_TX);
        callablestatement.setInt(1,m_user_id);
        callablestatement.registerOutParameter(2,java.sql.Types.INTEGER);
        int result = callablestatement.executeUpdate();
        m_tid = callablestatement.getInt(2);
        callablestatement.close();
        return m_tid;
    }

    public int startTransaction(String description, String comment) throws SQLException
	{
		CallableStatement callablestatement = null;
		callablestatement = m_connection.prepareCall(BW_GET_NEW_TX_WITH_COMMENT);
		callablestatement.setInt(1,m_user_id);
		callablestatement.setString(2,description);
		callablestatement.setString(3,comment);
		callablestatement.registerOutParameter(4,java.sql.Types.INTEGER);
		int result = callablestatement.executeUpdate();
		m_tid = callablestatement.getInt(4);
		callablestatement.close();
		return m_tid;
	}

	// Add Critical transaction to table BW_SIGNIFICANT_TXS
	// This will add selected transaction to critical update table
    public void addSigTransaction(String description, int aiTblId, int aiTxId) throws SQLException
	{
		// Start a connection
		DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
		Connection connection = databaseloader.getConnection();

		PreparedStatement prepStatement = null;
		prepStatement = connection.prepareCall(BW_CREATE_SIGNIFICANT_UPDATE);
		prepStatement.setInt(1,m_user_id);
		prepStatement.setString(2,description);
		prepStatement.setInt(3,aiTblId);
		prepStatement.setInt(4,aiTxId);
		int result = prepStatement.executeUpdate();
	}

    public void commitTransaction() throws SQLException
    {
        m_connection.commit();
        m_connection.setAutoCommit(true);

    }

    public void rollbackTransaction() throws SQLException
    {
        m_connection.rollback();
        m_connection.setAutoCommit(true);
    }

	public static String getTransactionTimeStamp(Connection a_connection, int tid) throws SQLException
	{
		CallableStatement callablestatement = null;
		callablestatement = a_connection.prepareCall(BW_GET_TX_ID_INFORMATION);
		callablestatement.setInt(1,tid);
		callablestatement.registerOutParameter(2,java.sql.Types.TIMESTAMP);
		int result = callablestatement.executeUpdate();
		java.sql.Timestamp ts1 = callablestatement.getTimestamp(2);
		callablestatement.close();
		return ts1.toString();
	}

	public static long getTransactionTime(Connection a_connection, int tid) throws SQLException
	{
		CallableStatement callablestatement = null;
		callablestatement = a_connection.prepareCall(BW_GET_TX_ID_INFORMATION);
		callablestatement.setInt(1,tid);
		callablestatement.registerOutParameter(2,java.sql.Types.TIMESTAMP);
		int result = callablestatement.executeUpdate();
		java.sql.Timestamp ts1 = callablestatement.getTimestamp(2,Calendar.getInstance(TimeZone.getTimeZone("GMT")));
		callablestatement.close();
		return ts1.getTime();
	}

	public static Transaction getTransaction(Connection connection, int txid)
	throws SQLException
	{
        PreparedStatement preparedstatement = null;
        ResultSet rs = null;
        Transaction tx = null;
        try {
            preparedstatement = connection.prepareStatement(BW_GET_TX_INFO);
            preparedstatement.setInt(1,txid);
            rs = preparedstatement.executeQuery();
            if ( rs.next() ) {
                int createdBy = rs.getInt("CREATED_BY");
				String userName = rs.getString("EMAIL_ADDRESS");
				java.sql.Timestamp ts = rs.getTimestamp("CREATED_ON",Calendar.getInstance(TimeZone.getTimeZone("GMT")));
				String description = rs.getString("DESCRIPTION");
				String comment = rs.getString("COMMENT_");

				tx = new Transaction( txid,
				                      createdBy,
				                      userName,
				                      ts.getTime(),
				                      description,
				                      comment);
            }
        }
        catch( Exception e ) {
            e.printStackTrace();
        }
        finally {
            try
            {
				rs.close();
                preparedstatement.close();
            }
            catch( Exception sql ) {
                sql.printStackTrace();
            }
        }

        return tx;
	}
}
