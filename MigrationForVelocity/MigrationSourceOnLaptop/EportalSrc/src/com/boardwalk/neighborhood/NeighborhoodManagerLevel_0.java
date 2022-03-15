// Decompiled by Decafe PRO - Java Decompiler
// Classes: 1   Methods: 8   Fields: 2

package com.boardwalk.neighborhood;

import com.boardwalk.database.DatabaseLoader;
import com.boardwalk.exception.NeighborhoodException;
import com.boardwalk.exception.SystemException;
import java.io.PrintStream;
import java.sql.*;
import java.util.Properties;
import java.util.Vector;

// Referenced classes of package com.boardwalk.neighborhood:
// NeighborhoodLevel_0

public class NeighborhoodManagerLevel_0 {

    private static String CALL_CR_NH_LEVEL_0 = "{CALL BW_CR_NH_LEVEL_0(?,?,?,?,?)}";
    private static String CALL_DEL_NH_LEVEL_0 = "{CALL BW_DEL_NH_LEVEL_0(?,?,?)}";
    private static String CALL_BW_GET_NHS_AT_LEVEL_0 = "{CALL BW_GET_NHS_AT_LEVEL_0(?)}";

    private static String BW_GET_NH_AT_LEVEL_0_USING_LEVEL_0_ID = " SELECT BW_NH_LEVEL_0.ID,"+
                        " BW_NH_LEVEL_0.NAME,"+
                        " BW_NH_LEVEL_0.NEIGHBORHOOD_ID,"+
                        " BW_NH.IS_SECURE, "+
                        " BW_NH.IS_ACTIVE, "+
                        " BW_NH.MANAGED_BY "+
                        " FROM BW_NH_LEVEL_0,BW_NH  "+
                        " WHERE BW_NH_LEVEL_0.ID = ?" +
    					  " AND BW_NH.ID = BW_NH_LEVEL_0.NEIGHBORHOOD_ID";
    private static String BW_GET_NH_AT_LEVEL_0_USING_NH_ID = "SELECT BW_NH_LEVEL_0.ID,"+
                        "BW_NH_LEVEL_0.NAME,"+
                        "BW_NH_LEVEL_0.NEIGHBORHOOD_ID,"+
                        "BW_NH.IS_SECURE, "+
                        "BW_NH.IS_ACTIVE, "+
                        "BW_NH.MANAGED_BY  " +
                        "FROM BW_NH_LEVEL_0,BW_NH "+
                        "WHERE BW_NH_LEVEL_0.NEIGHBORHOOD_ID = ?" +
    					" AND BW_NH.ID = BW_NH_LEVEL_0.NEIGHBORHOOD_ID";




    public static NeighborhoodLevel_0 createNeighborhood(Connection connection, String name, int transactionId, boolean isSecureFlag)
            throws NeighborhoodException, SystemException
    {
        NeighborhoodLevel_0 neighborhoodlevel_0 = null;
        CallableStatement callablestatement = null;

        try {
            callablestatement = connection.prepareCall(CALL_CR_NH_LEVEL_0);
            callablestatement.setString(1, name);
            callablestatement.setInt(2, transactionId);
            callablestatement.setBoolean(3, isSecureFlag);

            callablestatement.registerOutParameter(4, java.sql.Types.INTEGER);
            callablestatement.registerOutParameter(5, java.sql.Types.INTEGER);

            int l = callablestatement.executeUpdate();
            int nhid = callablestatement.getInt(4);
            int id = callablestatement.getInt(5);
            neighborhoodlevel_0 = new NeighborhoodLevel_0(name, transactionId, nhid, isSecureFlag, true);
        }
        catch(SQLException sqlexception)
        {
            SQLWarning sqlwarning = null;
            try {
                sqlwarning = connection.getWarnings();
                for(SQLWarning sqlwarning1 = null; (sqlwarning1 = sqlwarning.getNextWarning()) != null;)
                    sqlwarning = sqlwarning1;
            }
            catch(SQLException sqlexception1)
            {
                throw new SystemException(sqlexception1);
            }
            sqlwarning.printStackTrace();
            throw new NeighborhoodException(sqlwarning);
        }
        finally
        {
            try
            {
                callablestatement.close();
            }
            catch(SQLException sqlexception2)
            {
                throw new SystemException(sqlexception2);
            }
        }
        return neighborhoodlevel_0;
    }

    public static Vector getNeighborhoodsAtLevel_0(Connection connection, int a_userid)
    throws SystemException
    {
        Vector nhList = new Vector();
        Object obj = null;
        ResultSet resultset = null;
        PreparedStatement preparedstatement = null;
        try {
			if ( DatabaseLoader.getDatabaseType().trim().equalsIgnoreCase("ORACLE"))
			{
				preparedstatement = DatabaseLoader.getPreparedStatementFromPreLoadedQueries("BW_GET_NHS_AT_LEVEL_0", connection );
				preparedstatement.setInt(1,a_userid);

			}
			else
			{
				preparedstatement = connection.prepareStatement(CALL_BW_GET_NHS_AT_LEVEL_0);
				preparedstatement.setInt(1, a_userid );
			}
            resultset = preparedstatement.executeQuery();
            System.out.println(" NEIGHBORHOODS AT LEVEL 0 ");
            int i;
            int j;
            boolean flag;
            String s;
            for(; resultset.next(); System.out.println(" NEIGHBORHOODID=" + i + " ---LEVEL_O_ID=" + j + " ---NAME=" + s + " ---IS_SECURE=" + flag)) {
                flag = true;
                j = resultset.getInt("ID");
                s = resultset.getString("NAME");
                i = resultset.getInt("NEIGHBORHOOD_ID");
                flag = resultset.getBoolean("IS_SECURE");
                nhList.addElement(new NeighborhoodLevel_0(s, j, i, flag, true));
            }
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

        return nhList;
    }

    public static void printNeighborhoodsAtLevel_0(Connection connection)
    throws SystemException {
        Object obj = null;
        ResultSet resultset = null;
        PreparedStatement preparedstatement = null;
        try {
            preparedstatement = connection.prepareStatement(CALL_BW_GET_NHS_AT_LEVEL_0);
            preparedstatement.setInt(1,1);
            resultset = preparedstatement.executeQuery();
            System.out.println(" NEIGHBORHOODS AT LEVEL 0 ");
            int i;
            int j;
            boolean flag;
            String s;
            for(; resultset.next(); System.out.println(" NEIGHBORHOODID=" + i + " ---LEVEL_O_ID=" + j + " ---NAME=" + s + " ---IS_SECURE=" + flag)) {
                flag = true;
                 j = resultset.getInt("ID");
				s = resultset.getString("NAME");
				i = resultset.getInt("NEIGHBORHOOD_ID");
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

    public static boolean existsNeighborhoodLevel_0_Id(int i) {
        return false;
    }

    public static NeighborhoodLevel_0 getNeighborhoodLevel_0_by_Neighborhood_Id(Connection connection, int neighborhood_Id)
    throws SystemException {
        Object obj = null;
        ResultSet resultset = null;
        PreparedStatement preparedstatement = null;
        NeighborhoodLevel_0 neighborhoodlevel_0 = null;
        try {
            preparedstatement = connection.prepareStatement(BW_GET_NH_AT_LEVEL_0_USING_NH_ID);
            preparedstatement.setInt(1,neighborhood_Id);
            resultset = preparedstatement.executeQuery();
            if ( resultset.next() ) {
                int id;
                int nhid;
                boolean isSecure = true;
                boolean isActive = true;
                String s;
                id = resultset.getInt("ID");
                s = resultset.getString("NAME");
                nhid = resultset.getInt("NEIGHBORHOOD_ID");
                isSecure = resultset.getBoolean("IS_SECURE");
                isActive = resultset.getBoolean("IS_ACTIVE");
                neighborhoodlevel_0 = new NeighborhoodLevel_0(s, id, nhid, isSecure, isActive);
                return neighborhoodlevel_0;
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


    public static NeighborhoodLevel_0 getNeighborhoodLevel_0_by_Neighborhood_Level_0_Id(Connection connection, int Neighborhood_Level_0_Id)
    throws SystemException {


        Object obj = null;
        ResultSet resultset = null;
        PreparedStatement preparedstatement = null;
        NeighborhoodLevel_0 neighborhoodlevel_0 = null;
        try {
            preparedstatement = connection.prepareStatement(BW_GET_NH_AT_LEVEL_0_USING_LEVEL_0_ID);
            preparedstatement.setInt(1,Neighborhood_Level_0_Id);
            resultset = preparedstatement.executeQuery();
            if ( resultset.next() ) {
                int id;
                int nhid;
                boolean isSecure = true;
                boolean isActive = true;

                String s;
 				id = resultset.getInt("ID");
                s = resultset.getString("NAME");
                nhid = resultset.getInt("NEIGHBORHOOD_ID");
                isSecure = resultset.getBoolean("IS_SECURE");
                isActive = resultset.getBoolean("IS_ACTIVE");
                neighborhoodlevel_0 = new NeighborhoodLevel_0(s, id, nhid, isSecure, isActive);
                return neighborhoodlevel_0;
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



    public static void deleteNeighborhoodLevel_0_by_NeighborhoodLevel_0_Id( Connection connection , int NeighborhoodLevel_0_Id, boolean purge, int transactionId )
    throws NeighborhoodException, SystemException {

        CallableStatement callablestatement = null;
        try {
            callablestatement = connection.prepareCall(CALL_DEL_NH_LEVEL_0);
            callablestatement.setInt(1, NeighborhoodLevel_0_Id);
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

    /*
    public static void main(String args[]) {

        NeighborhoodLevel_0 nl0_0 = null;
        NeighborhoodLevel_0 nl0_1 = null;
        NeighborhoodLevel_0 nl0_2 = null;
        NeighborhoodLevel_0 nl0_3 = null;
        NeighborhoodLevel_0 nl0_4 = null;
        NeighborhoodLevel_0 nl0_5 = null;
        NeighborhoodLevel_0 nl0_6 = null;

        try {
            System.out.println("------------------------------------------------------------");
            System.out.println("Creating 6 New Neighborhoods");
            nl0_0 = createNeighborhood("level_0_nh_0", 1, false);
            nl0_1 = createNeighborhood("level_0_nh_1", 1, false);
            nl0_2 = createNeighborhood("level_0_nh_2", 1, false);
            nl0_3 = createNeighborhood("level_0_nh_3", 1, false);
            nl0_4 = createNeighborhood("level_0_nh_4", 1, false);
            nl0_5 = createNeighborhood("level_0_nh_5", 1, false);
            System.out.println("------------------------------------------------------------");
            System.out.println("Printing New Neighborhoods");
            printNeighborhoodsAtLevel_0();
            System.out.println("------------------------------------------------------------");
            System.out.println("Getting Neighborhood by Level 0 Id" + nl0_0.getId() );
            NeighborhoodLevel_0 nl0_0_x = getNeighborhoodLevel_0_by_Neighborhood_Level_0_Id(nl0_0.getId() );
            if ( nl0_0_x.isEquals(nl0_0) )
                System.out.println("Getting Neighborhood by Level 0 Id test successfull");
            else
                System.out.println("Getting Neighborhood by Level 0 Id test failed");
            System.out.println("------------------------------------------------------------");
            System.out.println("Getting Neighborhood by Nh Id" + nl0_0.getNhId() );
            nl0_0_x = null;
            nl0_0_x = getNeighborhoodLevel_0_by_Neighborhood_Id(nl0_0.getNhId() );
            if ( nl0_0_x.isEquals(nl0_0) )
                System.out.println("Getting Neighborhood by Nh Id test successfull");
            else
                System.out.println("Getting Neighborhood by Nh Id test failed");
            System.out.println("------------------------------------------------------------");
            System.out.println("Deleting Neighborhood by Nh level 0 Id" + nl0_0.getId() );
            deleteNeighborhoodLevel_0_by_NeighborhoodLevel_0_Id(nl0_0.getNhId(), true,1 );
            nl0_0_x = null;
            nl0_0_x = getNeighborhoodLevel_0_by_Neighborhood_Level_0_Id(nl0_0.getId() );
            if ( nl0_0_x == null )
                System.out.println("Deleting Neighborhood by Nh level 0 Id test successfull");
            else
                System.out.println("Deleting Neighborhood by Nh level 0 Id test failed");

            System.out.println("------------------------------------------------------------");
            System.out.println("Name uniqueness test .. we should catch a name uniqueness violation exception" );
            boolean uniqueNameTestSuccess = false;
            try {
                nl0_6 = createNeighborhood("level_0_nh_5", 1, false);
            }
            catch( NeighborhoodException neighborhoodexception ) {
                uniqueNameTestSuccess = true;
                System.out.println("BOARDWALK ERROR::");
                System.out.println("ERROR_CODE::" + neighborhoodexception.getErrorCode());
                System.out.println("ERROR_MESSAGE::" + neighborhoodexception.getErrorMessage());
                System.out.println("SUGGESTED_SOLUTION::" + neighborhoodexception.getPotentialSolution());
            }


            if ( uniqueNameTestSuccess ) {
                System.out.println("Name uniqueness test successfull");
            }
            else
                System.out.println("Name uniqueness test failed");
            System.out.println("------------------------------------------------------------");
            System.out.println("Now deleting Neighborhood all the created neighborhoods");
            deleteNeighborhoodLevel_0_by_NeighborhoodLevel_0_Id(nl0_1.getId(), true,1);
            deleteNeighborhoodLevel_0_by_NeighborhoodLevel_0_Id(nl0_2.getId(), true,1);
            deleteNeighborhoodLevel_0_by_NeighborhoodLevel_0_Id(nl0_3.getId(), true,1);
            deleteNeighborhoodLevel_0_by_NeighborhoodLevel_0_Id(nl0_4.getId(), true,1);
            deleteNeighborhoodLevel_0_by_NeighborhoodLevel_0_Id(nl0_5.getId(), true,1);
            System.out.println("Printing Neighborhoods");
            printNeighborhoodsAtLevel_0();
            System.out.println("Tests complete");

        }
        catch(NeighborhoodException neighborhoodexception) {
            System.out.println("BOARDWALK ERROR::");
            System.out.println("ERROR_CODE::" + neighborhoodexception.getErrorCode());
            System.out.println("ERROR_MESSAGE::" + neighborhoodexception.getErrorMessage());
            System.out.println("SUGGESTED_SOLUTION::" + neighborhoodexception.getPotentialSolution());
        }
        catch(SystemException systemexception) {
            systemexception.printStackTrace();
        }
    }
     */

}
