package com.boardwalk.neighborhood;

import com.boardwalk.database.DatabaseLoader;
import com.boardwalk.exception.NeighborhoodException;
import com.boardwalk.exception.SystemException;
import java.io.PrintStream;
import java.sql.*;
import java.util.Properties;
import java.util.Vector;

// Referenced classes of package com.boardwalk.neighborhood:
// NeighborhoodLevel_2

public class NeighborhoodManagerLevel_2 {

    private static String CALL_CR_NH_LEVEL_2 = "{CALL BW_CR_NH_LEVEL_2(?,?,?,?,?,?)}";
    private static String CALL_DEL_NH_LEVEL_2 = "{CALL BW_DEL_NH_LEVEL_2(?,?,?)}";
    private static String BW_GET_NHS_AT_LEVEL_2 = " SELECT BW_NH_LEVEL_2.ID, " +
    "BW_NH_LEVEL_2.NAME, " +
    "BW_NH_LEVEL_2.NEIGHBORHOOD_ID, " +
    "BW_NH.IS_SECURE,  " +
    "BW_NH.MANAGED_BY  " +
    "FROM BW_NH_LEVEL_2, BW_NH  " +
    "WHERE BW_NH_LEVEL_2.NEIGHBORHOOD_ID = BW_NH.ID  " +
    "AND BW_NH.IS_ACTIVE=1";
    private static String BW_GET_NH_AT_LEVEL_2_USING_LEVEL_2_ID = "SELECT BW_NH_LEVEL_2.ID, "+
    "BW_NH_LEVEL_2.NAME,     "+
    "BW_NH_LEVEL_2.NEIGHBORHOOD_ID,     "+
    "BW_NH.IS_SECURE, "+
    "BW_NH.IS_ACTIVE, "+
    "BW_NH.MANAGED_BY " +
    "FROM BW_NH_LEVEL_2,  BW_NH  "+
    "WHERE BW_NH_LEVEL_2.ID = ? " +
    "AND BW_NH.ID = BW_NH_LEVEL_2.NEIGHBORHOOD_ID";
    private static String BW_GET_NH_AT_LEVEL_2_USING_NH_ID = "SELECT BW_NH_LEVEL_2.ID,     "+
    "BW_NH_LEVEL_2.NAME,     "+
    "BW_NH_LEVEL_2.NEIGHBORHOOD_ID,     "+
    "BW_NH.IS_SECURE, "+
    "BW_NH.IS_ACTIVE, "+
    "BW_NH.MANAGED_BY  " +
    "FROM BW_NH_LEVEL_2,     BW_NH  "+
    "WHERE BW_NH_LEVEL_2.NEIGHBORHOOD_ID = ? " +
    "AND BW_NH.ID = BW_NH_LEVEL_2.NEIGHBORHOOD_ID";




    public static NeighborhoodLevel_2 createNeighborhood(Connection connection, String name, int parentNh1Id, int transactionId, boolean isSecureFlag)
    throws NeighborhoodException, SystemException
    {
        NeighborhoodLevel_2 neighborhoodlevel_2 = null;
        CallableStatement callablestatement = null;

        try {

            callablestatement = connection.prepareCall(CALL_CR_NH_LEVEL_2);
            callablestatement.setString(1, name);
            callablestatement.setInt(2, parentNh1Id);
            callablestatement.setInt(3, transactionId);
            callablestatement.setBoolean(4, isSecureFlag);
            callablestatement.registerOutParameter(5, java.sql.Types.INTEGER);
            callablestatement.registerOutParameter(6, java.sql.Types.INTEGER);

            int l = callablestatement.executeUpdate();

            SQLWarning sqlwarning = callablestatement.getWarnings();
            SQLWarning sqlwarningBW = null;
            if ( l < 1 ) {

                if ( sqlwarning != null ) {

                    while ( sqlwarning != null ) {

                        if ( sqlwarning.getErrorCode() >= 80000 ) {
                            sqlwarningBW = sqlwarning;
                            break;
                        }
                        else
                            sqlwarning = sqlwarning.getNextWarning();
                    }
                }

                if ( sqlwarningBW != null ) {
                    throw new NeighborhoodException(sqlwarningBW);
                }
            }


            int nhid = callablestatement.getInt(5);
            int id = callablestatement.getInt(6);

            neighborhoodlevel_2 = new NeighborhoodLevel_2(name, id, nhid, isSecureFlag, true);
        }
        catch(SQLException sqlexception) {
            sqlexception.printStackTrace();
            SQLWarning sqlwarning = null;
            try {
                sqlwarning = connection.getWarnings();
                for(SQLWarning sqlwarning1 = null; (sqlwarning1 = sqlwarning.getNextWarning()) != null;)
                    sqlwarning = sqlwarning1;

            }
            catch(SQLException sqlexception1) {
                throw new SystemException(sqlexception1);
            }
            throw new NeighborhoodException(sqlwarning);
        }
        finally {
            try {
                callablestatement.close();
            }
            catch(SQLException sqlexception2) {
                throw new SystemException(sqlexception2);
            }
        }
        return neighborhoodlevel_2;
    }

    public static void printNeighborhoodsAtLevel_2(Connection connection)
    throws SystemException {
        Object obj = null;
        ResultSet resultset = null;
        PreparedStatement preparedstatement = null;
        try {
            preparedstatement = connection.prepareStatement(BW_GET_NHS_AT_LEVEL_2);
            resultset = preparedstatement.executeQuery();
            System.out.println(" NEIGHBORHOODS AT LEVEL 2 ");
            int i;
            int j;
            boolean flag;
            String s;
            for(; resultset.next(); System.out.println(" NEIGHBORHOODID=" + i + " ---LEVEL_2_ID=" + j + " ---NAME=" + s + " ---IS_SECURE=" + flag)) {
                flag = true;
                i = resultset.getInt("NEIGHBORHOOD_ID");
                s = resultset.getString("NAME");
                j = resultset.getInt("ID");
                flag = resultset.getBoolean("IS_SECURE");
            }

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

    public static boolean existsNeighborhoodLevel_2_Id(int i) {
        return false;
    }

    public static NeighborhoodLevel_2 getNeighborhoodLevel_2_by_Neighborhood_Id(Connection connection,int neighborhood_Id)
    throws SystemException {
        Object obj = null;
        ResultSet resultset = null;
        PreparedStatement preparedstatement = null;
        NeighborhoodLevel_2 neighborhoodlevel_2 = null;
        try {
            preparedstatement = connection.prepareStatement(BW_GET_NH_AT_LEVEL_2_USING_NH_ID);
            preparedstatement.setInt(1,neighborhood_Id);
            resultset = preparedstatement.executeQuery();
            if ( resultset.next() ) {
                int id;
                int nhid;
                boolean isSecure = true;
                boolean isActive = true;
                String name;
                id = resultset.getInt("ID");
                name = resultset.getString("NAME");
                nhid = resultset.getInt("NEIGHBORHOOD_ID");
                isSecure = resultset.getBoolean("IS_SECURE");
                isActive = resultset.getBoolean("IS_ACTIVE");
                neighborhoodlevel_2 = new NeighborhoodLevel_2(name, id, nhid, isSecure, isActive);
                return neighborhoodlevel_2;
            }
            else
                return null;

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


    public static NeighborhoodLevel_2 getNeighborhoodLevel_2_by_Neighborhood_Level_2_Id(Connection connection, int Neighborhood_Level_2_Id)
    throws SystemException
    {
        Object obj = null;
        ResultSet resultset = null;
        PreparedStatement preparedstatement = null;
        NeighborhoodLevel_2 neighborhoodlevel_2 = null;
        try {
            preparedstatement = connection.prepareStatement(BW_GET_NH_AT_LEVEL_2_USING_LEVEL_2_ID);
            preparedstatement.setInt(1,Neighborhood_Level_2_Id);
            resultset = preparedstatement.executeQuery();
            if ( resultset.next() ) {
                int id;
                int nhid;
                boolean isSecure = true;
                boolean isActive = true;
                String name;
				 id = resultset.getInt("ID");
				 name = resultset.getString("NAME");
                nhid = resultset.getInt("NEIGHBORHOOD_ID");

                isSecure = resultset.getBoolean("IS_SECURE");
                isActive = resultset.getBoolean("IS_ACTIVE");
                neighborhoodlevel_2 = new NeighborhoodLevel_2(name, id, nhid, isSecure, isActive);
                return neighborhoodlevel_2;
            }
            else {
                return null;
            }

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

    public static void deleteNeighborhoodLevel_2_by_NeighborhoodLevel_2_Id( Connection connection, int NeighborhoodLevel_2_Id, boolean purge,int transactionId )
    throws NeighborhoodException, SystemException
    {
        CallableStatement callablestatement = null;
        try {
            callablestatement = connection.prepareCall(CALL_DEL_NH_LEVEL_2);
            callablestatement.setInt(1, NeighborhoodLevel_2_Id);
            callablestatement.setBoolean(2, purge);
            callablestatement.setInt(3, transactionId);
            int l = callablestatement.executeUpdate();

        }
        catch(SQLException sqlexception) {
            sqlexception.printStackTrace();
            SQLWarning sqlwarning = null;
            try {
                sqlwarning = connection.getWarnings();
                for(SQLWarning sqlwarning1 = null; (sqlwarning1 = sqlwarning.getNextWarning()) != null;)
                    sqlwarning = sqlwarning1;
            }
            catch(SQLException sqlexception1) {
                throw new SystemException(sqlexception1);
            }
            throw new NeighborhoodException(sqlwarning);
        }
        finally {
            try {
                callablestatement.close();
            }
            catch(SQLException sqlexception2) {
                throw new SystemException(sqlexception2);
            }
        }
    }


}



