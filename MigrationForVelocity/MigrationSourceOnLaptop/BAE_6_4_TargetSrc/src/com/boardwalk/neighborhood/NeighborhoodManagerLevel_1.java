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
// NeighborhoodLevel_1

public class NeighborhoodManagerLevel_1 {

    private static String CALL_CR_NH_LEVEL_1 = "{CALL BW_CR_NH_LEVEL_1(?,?,?,?,?,?)}";
    private static String CALL_DEL_NH_LEVEL_1 = "{CALL BW_DEL_NH_LEVEL_1(?,?,?)}";
    private static String BW_GET_NHS_AT_LEVEL_1 = "SELECT BW_NH_LEVEL_1.ID, "+
    "BW_NH_LEVEL_1.NAME, "+
    "BW_NH_LEVEL_1.NEIGHBORHOOD_ID, "+
    "BW_NH.IS_SECURE, "+
    "BW_NH.MANAGED_BY "+
    "FROM BW_NH_LEVEL_1,BW_NH "+
    "WHERE BW_NH_LEVEL_1.NEIGHBORHOOD_ID = BW_NH.ID "+
    "AND BW_NH.IS_ACTIVE=1";
    private static String BW_GET_NH_AT_LEVEL_1_USING_LEVEL_1_ID = "SELECT BW_NH_LEVEL_1.ID,     "+
    "BW_NH_LEVEL_1.NAME,     "+
    "BW_NH_LEVEL_1.NEIGHBORHOOD_ID,     "+
    "BW_NH.IS_SECURE, "+
    "BW_NH.IS_ACTIVE, "+
    "BW_NH.MANAGED_BY "+
    "FROM BW_NH_LEVEL_1,     BW_NH  "+
    "WHERE BW_NH_LEVEL_1.ID = ? " +
    "AND BW_NH.ID = BW_NH_LEVEL_1.NEIGHBORHOOD_ID";
    private static String BW_GET_NH_AT_LEVEL_1_USING_NH_ID = "SELECT BW_NH_LEVEL_1.ID,     "+
    "BW_NH_LEVEL_1.NAME,     "+
    "BW_NH_LEVEL_1.NEIGHBORHOOD_ID,     "+
    "BW_NH.IS_SECURE, "+
    "BW_NH.IS_ACTIVE, "+
    "BW_NH.MANAGED_BY  "+
    "FROM BW_NH_LEVEL_1,     BW_NH "+
    "WHERE BW_NH_LEVEL_1.NEIGHBORHOOD_ID = ?  " +
    "AND BW_NH.ID = BW_NH_LEVEL_1.NEIGHBORHOOD_ID";



    public static NeighborhoodLevel_1 createNeighborhood(Connection connection, String name, int parentNh0Id, int transactionId, boolean isSecureFlag)
    throws NeighborhoodException, SystemException
    {
        NeighborhoodLevel_1 neighborhoodlevel_1 = null;
        CallableStatement callablestatement = null;
        try {

            callablestatement = connection.prepareCall(CALL_CR_NH_LEVEL_1);
            callablestatement.setString(1, name);
            callablestatement.setInt(2, parentNh0Id);
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
            neighborhoodlevel_1 = new NeighborhoodLevel_1(name, id, nhid, isSecureFlag, true);
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
        return neighborhoodlevel_1;
    }

    public static void printNeighborhoodsAtLevel_1(Connection connection)
    throws SystemException {
        Object obj = null;
        ResultSet resultset = null;
        PreparedStatement preparedstatement = null;
        try {
            preparedstatement = connection.prepareStatement(BW_GET_NHS_AT_LEVEL_1);
            resultset = preparedstatement.executeQuery();
            System.out.println(" NEIGHBORHOODS AT LEVEL 1 ");
            int i;
            int j;
            boolean flag;
            String s;
            for(; resultset.next(); System.out.println(" NEIGHBORHOODID=" + i + " ---LEVEL_1_ID=" + j + " ---NAME=" + s + " ---IS_SECURE=" + flag)) {
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

    public static boolean existsNeighborhoodLevel_1_Id(int i) {
        return false;
    }

    public static NeighborhoodLevel_1 getNeighborhoodLevel_1_by_Neighborhood_Id(Connection connection, int neighborhood_Id)
    throws SystemException {
        Object obj = null;
        ResultSet resultset = null;
        PreparedStatement preparedstatement = null;
        NeighborhoodLevel_1 neighborhoodlevel_1 = null;
        try {
            preparedstatement = connection.prepareStatement(BW_GET_NH_AT_LEVEL_1_USING_NH_ID);
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
                neighborhoodlevel_1 = new NeighborhoodLevel_1(name, id, nhid, isSecure, isActive);
                return neighborhoodlevel_1;
            }
            else
                return null;

        }
        catch(SQLException sqlexception) {
            throw new SystemException(sqlexception);
        }
        finally {
            try {

				if ( resultset != null )
				{
                	resultset.close();
				}
				if ( preparedstatement != null )
				{
                	preparedstatement.close();
				}
            }
            catch(SQLException sqlexception1) {
                throw new SystemException(sqlexception1);
            }
        }
    }


    public static NeighborhoodLevel_1 getNeighborhoodLevel_1_by_Neighborhood_Level_1_Id(Connection connection, int Neighborhood_Level_1_Id)
    throws SystemException {

        Object obj = null;
        ResultSet resultset = null;
        PreparedStatement preparedstatement = null;
        NeighborhoodLevel_1 neighborhoodlevel_1 = null;
        try {
            preparedstatement = connection.prepareStatement(BW_GET_NH_AT_LEVEL_1_USING_LEVEL_1_ID);
            preparedstatement.setInt(1,Neighborhood_Level_1_Id);
            
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
                neighborhoodlevel_1 = new NeighborhoodLevel_1(name, id, nhid, isSecure, isActive);
                return neighborhoodlevel_1;
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



    public static void deleteNeighborhoodLevel_1_by_NeighborhoodLevel_1_Id( Connection connection, int NeighborhoodLevel_1_Id, boolean purge ,int transactionId)
    throws NeighborhoodException, SystemException {

        CallableStatement callablestatement = null;
        try {
            callablestatement = connection.prepareCall(CALL_DEL_NH_LEVEL_1);
            callablestatement.setInt(1, NeighborhoodLevel_1_Id);
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

        NeighborhoodLevel_1 nl1_0 = null;
        NeighborhoodLevel_1 nl1_1 = null;
        NeighborhoodLevel_1 nl1_2 = null;
        NeighborhoodLevel_1 nl1_3 = null;
        NeighborhoodLevel_1 nl1_4 = null;

        try {
            System.out.println("------------------------------------------------------------");
            System.out.println("Creating 6 New Level 1 Neighborhoods");

            nl0_0 = NeighborhoodManagerLevel_0.createNeighborhood("level_0_nh_0", 1, false);
            nl0_1 = NeighborhoodManagerLevel_0.createNeighborhood("level_0_nh_1", 1, false);

            nl1_0 = createNeighborhood("level_1_nh_0", nl0_0.getId(),1, false);
            nl1_1 = createNeighborhood("level_1_nh_1", nl0_0.getId(),1, false);
            nl1_2 = createNeighborhood("level_1_nh_2", nl0_1.getId(),1, false);
            nl1_3 = createNeighborhood("level_1_nh_3", nl0_1.getId(),1, false);

            System.out.println("------------------------------------------------------------");
            System.out.println("Printing New Neighborhoods at level 1");
            printNeighborhoodsAtLevel_1();
            System.out.println("------------------------------------------------------------");


            System.out.println("Getting Neighborhood by Level 1 Id" + nl1_0.getId() );
            NeighborhoodLevel_1 nl1_0_x = getNeighborhoodLevel_1_by_Neighborhood_Level_1_Id(nl1_0.getId() );
            System.out.println(nl1_0.toString());
            System.out.println("Printing the retrieved Neighborhood");
            System.out.println(nl1_0_x.toString());
            if ( nl1_0_x.isEquals(nl1_0) )
                System.out.println("Getting Neighborhood by Level 1 Id test successfull");
            else
                System.out.println("Getting Neighborhood by Level 1 Id test failed");

            System.out.println("------------------------------------------------------------");
            System.out.println("Getting Neighborhood by Nh Id" + nl1_0.getNhId() );
            nl1_0_x = null;
            nl1_0_x = getNeighborhoodLevel_1_by_Neighborhood_Id(nl1_0.getNhId() );
            System.out.println("Printing the retrieved Neighborhood");
            System.out.println(nl1_0_x.toString());
            if ( nl1_0_x.isEquals(nl1_0) )
                System.out.println("Getting Neighborhood by Nh Id test successfull");
            else
                System.out.println("Getting Neighborhood by Nh Id test failed");

            System.out.println("------------------------------------------------------------");
            System.out.println("Deleting Neighborhood by Nh level 1 Id" + nl1_0.getId() );
            deleteNeighborhoodLevel_1_by_NeighborhoodLevel_1_Id(nl1_0.getId(), true,1 );
            nl1_0_x = null;
            nl1_0_x = getNeighborhoodLevel_1_by_Neighborhood_Level_1_Id(nl1_0.getId() );
            if ( nl1_0_x == null )
                System.out.println("Deleting Neighborhood by Nh level 1 Id test successfull");
            else
                System.out.println("Deleting Neighborhood by Nh level 1 Id test failed");

            System.out.println("------------------------------------------------------------");


            NeighborhoodManagerLevel_0.printNeighborhoodsAtLevel_0();
            printNeighborhoodsAtLevel_1();

            System.out.println("Name uniqueness test .. we should catch a name uniqueness violation exception" );
            System.out.println("Creating an nh of name level_1_nh_3 under level 0 nh " + nl0_1.getId() );
            boolean uniqueNameTestSuccess = false;
            try {
                nl1_4 = createNeighborhood("level_1_nh_3",nl0_1.getId(), 1, false);
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


            System.out.println("------------------------------------------------------------");

            System.out.println("Name uniqueness test .. we should create a level 1 nh with the same name within a different parent level 0 nh" );

            uniqueNameTestSuccess = true;
            try {
                nl1_4 = createNeighborhood("level_1_nh_3",nl0_0.getId(), 1, false);
            }
            catch( NeighborhoodException neighborhoodexception ) {
                uniqueNameTestSuccess = false;
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

            System.out.println("Nh Constraint test we should create a level 1 nh with a non existent level 0 neighborhood and get an exception" );

            boolean nhConstraintSuccess = false;
            try {
                NeighborhoodLevel_1 nl1_5 = createNeighborhood("level_1_nh_3",15000, 1, false);
            }
            catch( NeighborhoodException neighborhoodexception ) {
                nhConstraintSuccess = true;
                System.out.println("BOARDWALK ERROR::");
                System.out.println("ERROR_CODE::" + neighborhoodexception.getErrorCode());
                System.out.println("ERROR_MESSAGE::" + neighborhoodexception.getErrorMessage());
                System.out.println("SUGGESTED_SOLUTION::" + neighborhoodexception.getPotentialSolution());
            }


            if ( nhConstraintSuccess ) {
                System.out.println("Nh Constraint test successfull");
            }
            else
                System.out.println("Nh Constraint test failed");

            System.out.println("------------------------------------------------------------");

            System.out.println("Now deleting Neighborhood all the created neighborhoods");

            deleteNeighborhoodLevel_1_by_NeighborhoodLevel_1_Id(nl1_1.getId(), true,1);
            deleteNeighborhoodLevel_1_by_NeighborhoodLevel_1_Id(nl1_2.getId(), true,1);
            deleteNeighborhoodLevel_1_by_NeighborhoodLevel_1_Id(nl1_3.getId(), true,1);
            deleteNeighborhoodLevel_1_by_NeighborhoodLevel_1_Id(nl1_4.getId(), true,1);

            NeighborhoodManagerLevel_0.deleteNeighborhoodLevel_0_by_NeighborhoodLevel_0_Id(nl0_0.getId(), true,1);
            NeighborhoodManagerLevel_0.deleteNeighborhoodLevel_0_by_NeighborhoodLevel_0_Id(nl0_1.getId(), true,1);

            System.out.println("Printing Neighborhoods at level 1 ");


            printNeighborhoodsAtLevel_1();


            System.out.println("Level 1 Tests successfully completed");

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
