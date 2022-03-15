/*
 * NeighborhoodManager.java
 *
 * Created on June 20, 2002, 7:33 AM
 */

package com.boardwalk.neighborhood;

import com.boardwalk.database.DatabaseLoader;
import com.boardwalk.exception.NeighborhoodException;
import com.boardwalk.exception.BoardwalkException;
import com.boardwalk.exception.SystemException;
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 *
 * @author  Sarang Kulkarni
 */

public final class NeighborhoodManager
{

	public static final String CALL_BW_GET_NH_RELS = "{CALL BW_GET_NH_RELS(?)}";


    /** Creates a new instance of NeighborhoodManager */
    public NeighborhoodManager() {
    }

    public static void  purgeNeighborhood(Connection connection,  int nhid)
    throws SystemException, BoardwalkException
    {		System.out.println("inside purge");
        //String CALL_BW_PURGE_NH_AND_CHILDREN = "{CALL BW_PURGE_NH_AND_CHILDREN(?)}";
		String CALL_BW_PURGE_NH_ACCESS="{CALL BW_PURGE_NH_ACCESS(?,?)}";
	
        CallableStatement callablestatement = null;

        int numPurged;
		int status=-1;
        try {
			System.out.println("Before purge");
            callablestatement = connection.prepareCall(CALL_BW_PURGE_NH_ACCESS);
            callablestatement.setInt(1, nhid);
			callablestatement.registerOutParameter(2,java.sql.Types.INTEGER);
            numPurged = callablestatement.executeUpdate();
			status = callablestatement.getInt(2);
			System.out.println("After Purge" +numPurged);
			System.out.println("Status in Purge"+status);
            if (status == 0)
                throw new BoardwalkException(10004);
        }
        catch(SQLException sqlexception) {

            throw new SystemException(sqlexception);
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

     public static Hashtable getNeighborhoodRelationships(Connection connection, int nhid)throws SystemException
		    {
		        Hashtable relationships = new Hashtable();
				PreparedStatement preparedstatement = null;
				ResultSet resultset = null;
			//	System.out.println("getNeighborhoodRelationships for nh" + nhid);
				 try
				   {
					    if ( DatabaseLoader.getDatabaseType().trim().equalsIgnoreCase("ORACLE"))
						{
							preparedstatement = DatabaseLoader.getPreparedStatementFromPreLoadedQueries("BW_GET_NH_RELS", connection );
							preparedstatement.setInt(1,nhid);

						}
						else
						{
				            preparedstatement = connection.prepareStatement(CALL_BW_GET_NH_RELS);
				            preparedstatement.setInt(1, nhid);
						}
						resultset = preparedstatement.executeQuery();
						while( resultset.next() )
						{
							int  targetNHId = resultset.getInt("ID");
							String name = resultset.getString("NAME");
							String relationship = resultset.getString("REL");
							NeighborhoodId nh = new NeighborhoodId(targetNHId, name);

							if ( relationships.get(relationship ) == null )
							{
								relationships.put(relationship, new Vector());
							}

						   ( (Vector)relationships.get(relationship )).add(nh);

						}
					}
					catch(SQLException sqlexception)
					{
						throw new SystemException(sqlexception);
					}
					finally
					{
							try
							{
								if ( resultset!= null )
									resultset.close();

								if ( preparedstatement != null )
								   preparedstatement.close();
							}
							catch(SQLException sqlexception1)
							{
								throw new SystemException(sqlexception1);
							}
					}

	        return relationships;
	    }


  public static Vector getBoardwalkPaths( Connection connection, int nhid )
  {

	   String nhPath = null;
	  Vector nhPaths = new Vector();

	  try
	  {


	  Neighborhood nh = getNeighborhoodById( connection, nhid );
	  nhPath = nh.getName();
	  String pathSep = System.getProperty("file.separator");


	  if ( nh.getLevels() == 0 )
	  {
		  nhPaths.add(nhPath);
	  }

	  if (nh.getLevels() == 1)
	  {
		  NeighborhoodLevel_0 nh_0 = NeighborhoodManagerLevel_0.getNeighborhoodLevel_0_by_Neighborhood_Level_0_Id(connection, nh.getLevel0Id());
		  nhPaths.add(nh_0.getName() + pathSep + nhPath );
		  nhPaths.add(nhPath);
	  }

	  if (nh.getLevels() == 2)
	  {
		  NeighborhoodLevel_0 nh_0 = NeighborhoodManagerLevel_0.getNeighborhoodLevel_0_by_Neighborhood_Level_0_Id(connection, nh.getLevel0Id());
		  NeighborhoodLevel_1 nh_1 = NeighborhoodManagerLevel_1.getNeighborhoodLevel_1_by_Neighborhood_Level_1_Id(connection, nh.getLevel1Id());
		  nhPaths.add(nh_1.getName() + pathSep + nh_0.getName() + pathSep+ nhPath );
		  nhPaths.add(nh_0.getName() + pathSep+ nhPath );
		  nhPaths.add(nhPath);
	  }

	   if ( nh.getLevels() == 3 )
	  {
		  NeighborhoodLevel_0 nh_0 = NeighborhoodManagerLevel_0.getNeighborhoodLevel_0_by_Neighborhood_Level_0_Id(connection, nh.getLevel0Id());
		  NeighborhoodLevel_1 nh_1 = NeighborhoodManagerLevel_1.getNeighborhoodLevel_1_by_Neighborhood_Level_1_Id(connection, nh.getLevel1Id());
		  NeighborhoodLevel_2 nh_2 = NeighborhoodManagerLevel_2.getNeighborhoodLevel_2_by_Neighborhood_Level_2_Id(connection, nh.getLevel2Id());
		  nhPaths.add(nh_2.getName() + pathSep + nh_1.getName() + pathSep + nh_0.getName() + pathSep + nhPath );
		  nhPaths.add(nh_1.getName() +pathSep+ nh_0.getName() + pathSep + nhPath );
		  nhPaths.add(nh_0.getName() + pathSep+ nhPath );
		  nhPaths.add(nhPath);
	  }

  	}
  	catch( Exception ex )
  	{


	}

	  return nhPaths;

  }




    public static Vector getNeighborhoodRelations(Connection connection, int nhid)
                throws SystemException
   {
       String GET_NH_RELS = "{CALL BW_GET_NH_REL(?)}";
       PreparedStatement preparedstatement = null;
       ResultSet resultset = null;
       int target_nh_id;
       int relation_id;
       int id;
       String relation = "???";
       Vector relations = null;
       NeighborhoodRelation nhrelation;
       try
       {
		   if ( DatabaseLoader.getDatabaseType().trim().equalsIgnoreCase("ORACLE"))
			{
				preparedstatement = DatabaseLoader.getPreparedStatementFromPreLoadedQueries("BW_GET_NH_REL", connection );
				preparedstatement.setInt(1,nhid);

			}
			else
			{
			   preparedstatement = connection.prepareStatement(GET_NH_RELS);
			   preparedstatement.setInt(1, nhid);
		   }
           resultset = preparedstatement.executeQuery();
           while ( resultset.next() ) {
               id = resultset.getInt("ID");
               relation = resultset.getString("REL");
               target_nh_id = resultset.getInt("TARGET_NH_ID");
	     //      System.out.println("Target " + target_nh_id);

               if (relations == null)
                 relations = new Vector();

               nhrelation = new NeighborhoodRelation(
                           //            id,
                           //            nhid,
                                       target_nh_id,
                                       0,  // relation_id right now denormailzed
                                       relation);
               relations.add(nhrelation);
           }
       }
       catch(SQLException sqlexception) {
           throw new SystemException(sqlexception);
       }
       finally {
           try {
			   if (resultset != null)
				resultset.close();
               if (preparedstatement != null)
				preparedstatement.close();
           }
           catch(SQLException sqlexception1) {
               throw new SystemException(sqlexception1);
           }
       }

       return relations;
   }

    public static Vector getNeighborhoodTree(Connection connection, int a_userId)
    {
        return (getNeighborhoodTree(connection,a_userId,false));
    }

    public static Vector getNeighborhoodTree(Connection connection, int a_userId,boolean bRelations)
    {
        Vector vNHTree = new Vector();

        // get all level 0 nh
        Vector nh0list = new Vector();
        try
        {
            nh0list = NeighborhoodManagerLevel_0.getNeighborhoodsAtLevel_0(connection, a_userId);
        }
        catch (Exception e)
        {
			e.printStackTrace();
        }

        // for each of these add it to the nh tree
        Iterator nh0Iter = nh0list.iterator();
        while (nh0Iter.hasNext()) {
            NeighborhoodLevel_0 nh0 = (NeighborhoodLevel_0)nh0Iter.next();
            int nhid = nh0.getNhId();
            Neighborhood nh = new Neighborhood (nhid, 0, nh0.getId(), -1 , -1, -1, true,nh0.getName());
            addNHTreeAndRelations(connection, vNHTree, nh, nh0.getName(),bRelations);
        }

        return vNHTree;
    }

    public static Vector getNeighborhoodTreeUnderSpecificNeighborhood(Connection connection, int a_nhId)
    throws com.boardwalk.exception.SystemException
	    {
	        Vector vNHTree = new Vector();
			Neighborhood nh = getNeighborhoodById( connection, a_nhId );
			addNHTreeAndRelations(connection, vNHTree, nh, nh.getName(),false);
	        return vNHTree;
    }



    private static void addNHTreeAndRelations(Connection connection, Vector vNHTree, Neighborhood nh, String nme,boolean bRelations)
    {
        Vector ch = new Vector();
        if (bRelations) {
          try {
           // get the children to this nh
           Vector relations = getNeighborhoodRelations(connection, nh.getId());
           nh.setRelations(relations);
          } catch (Exception e) {
              e.printStackTrace();
          }
       }
      try {
            // get the children to this nh
            System.out.println("getChildrenAndNamesForNeighborhood() for "+ nh.getName());
            ch = getChildrenAndNamesForNeighborhood(connection, nh);
      } catch (Exception e) {
            e.printStackTrace();
      }

	// List of child NHTree entries
	Vector vcch = new Vector();

	// make an entry for each of the children
	Iterator chIter = ch.iterator();
	while(chIter.hasNext()) {
	    NhName cnh = (NhName)chIter.next();
	    System.out.println("addNHTreeAndRelations() for "+ cnh.name);
	    addNHTreeAndRelations(connection, vcch, cnh.nh, cnh.name,bRelations);
	}

	NHTree i = new NHTree(nh, vcch, nme);
	vNHTree.addElement(i);
    }



    public static NeighborhoodLevelId getNeighborhoodLevelId(Connection connection,String nh0, String nh1,String nh2,String nh3, int level ) throws SystemException
	       {
	           String GET_NH_IDS_BY_NAMES = "{CALL BW_GET_NHID(?,?,?,?,?)}";
	           CallableStatement callablestatement = null;
	           ResultSet resultset = null;
	            NeighborhoodLevelId nhl  = null;
	           try {

				   if ( nh0.equals("") )
				   	nh0=" ";
				   if ( nh1.equals("") )
				   	nh1=" ";
				   	if ( nh2.equals("") )
				   	nh2=" ";
				   	if ( nh3.equals("") )
				   	nh3=" ";

	               callablestatement = connection.prepareCall(GET_NH_IDS_BY_NAMES);
	               callablestatement.setString(1, nh0);
	               callablestatement.setString(2, nh1);
	               callablestatement.setString(3, nh2);
	               callablestatement.setString(4, nh3);
	               callablestatement.setInt(5, level);
	               resultset = callablestatement.executeQuery();

	               SQLWarning sqlwarning = callablestatement.getWarnings();


			   if ( sqlwarning != null )
			   {

				   while ( sqlwarning != null )
				   {
						   System.out.println(" sqlwarning " + sqlwarning.getMessage() );
						   sqlwarning = sqlwarning.getNextWarning();
				   }
			  	}



	               if (resultset!= null &&  resultset.next() )
	               {
	                   int ID = resultset.getInt("NH_ID");
	                   int levelId = resultset.getInt("NHL_ID");
	                   String name = resultset.getString("NAME");
	                   nhl = new NeighborhoodLevelId(ID, name,levelId, level );
	                }

	           }
	           catch(SQLException sqlexception)
	           {
				   sqlexception.printStackTrace();
	               return null;
	           }
	           finally {
	               try
	               {
					   if ( resultset != null )
					   {
	                   	resultset.close();
						}

						if ( callablestatement != null )
					   {
	                   	callablestatement.close();
						}
	               }
	               catch(SQLException sqlexception1) {
	                   throw new SystemException(sqlexception1);
	               }
	           }

	           return nhl;
    }

    public static NhName getNeighborhoodNameById(Connection connection, int nhid) throws SystemException
       {
           String GET_NH_NAME_BY_ID = "{CALL BW_GET_NH_AND_NAME(?,?,?,?,?,?,?,?,?,?)}";
           CallableStatement callablestatement = null;
           ResultSet resultset = null;
           try
           {
               callablestatement = connection.prepareCall(GET_NH_NAME_BY_ID);
               callablestatement.setInt(1, nhid);

               callablestatement.registerOutParameter(2,java.sql.Types.INTEGER);
               callablestatement.registerOutParameter(3,java.sql.Types.INTEGER);
               callablestatement.registerOutParameter(4,java.sql.Types.INTEGER);
               callablestatement.registerOutParameter(5,java.sql.Types.INTEGER);
               callablestatement.registerOutParameter(6,java.sql.Types.INTEGER);
               callablestatement.registerOutParameter(7,java.sql.Types.INTEGER);
               callablestatement.registerOutParameter(8,java.sql.Types.INTEGER);
               callablestatement.registerOutParameter(9,java.sql.Types.INTEGER);
               callablestatement.registerOutParameter(10,java.sql.Types.VARCHAR);

               callablestatement.execute();

			   int levels = callablestatement.getInt(2);
			   int level0Id = callablestatement.getInt(3);
			   int level1Id = callablestatement.getInt(4);
			   int level2Id = callablestatement.getInt(5);
			   int level3Id = callablestatement.getInt(6);
			   boolean isSecure = callablestatement.getBoolean(7);
			   boolean isActive = callablestatement.getBoolean(8);
			   int     managedby = callablestatement.getInt(9);
			   String name = callablestatement.getString(10);
			   Neighborhood nh = new Neighborhood(nhid, levels, level0Id, level1Id, level2Id, level3Id, isSecure,name);
			   nh.setManagedby(managedby);
			   return new NhName (nh, name);

           }
           catch(SQLException sqlexception)
           {
               throw new SystemException(sqlexception);
           }
           finally
           {
               try
               {     if ( callablestatement != null )
               		{
                   			callablestatement.close();
					}
               }
               catch(SQLException sqlexception1)
               {
                   throw new SystemException(sqlexception1);
               }
           }


    }


    public static Neighborhood getNeighborhoodById( Connection connection, int nhid) throws SystemException
    {
        String BW_GET_NH_BY_ID =   "select * " +
                                                    "from BW_NH AS NH " +
                                                    "where NH.ID = ? AND NH.IS_ACTIVE=1 ";
        ResultSet resultset = null;
        PreparedStatement preparedstatement = null;
        try {
            preparedstatement = connection.prepareStatement(BW_GET_NH_BY_ID);
            preparedstatement.setInt(1, nhid);
            resultset = preparedstatement.executeQuery();
            if ( resultset.next() ) {
				int nhId = resultset.getInt("ID");
                int levels = resultset.getInt("NEIGHBORHOOD_LEVEL");
                int level0Id = resultset.getInt("LEVEL_0_ID");
                int level1Id = resultset.getInt("LEVEL_1_ID");
                int level2Id = resultset.getInt("LEVEL_2_ID");
                int level3Id = resultset.getInt("LEVEL_3_ID");
                boolean isSecure = resultset.getBoolean("IS_SECURE");
                int transactionId  = resultset.getInt("TX_ID");
                boolean isActive = resultset.getBoolean("IS_ACTIVE");
                String  name = resultset.getString("NAME");
                return new Neighborhood(nhid, levels, level0Id, level1Id, level2Id, level3Id, isSecure,name);
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

        return null;
    }



    public static Neighborhood getNeighborhoodByName( Connection connection, String nhname) throws SystemException
    {
        String BW_GET_NH_BY_NAME =   "select * " +
                                                    "from BW_NH AS NH " +
                                                    "where NH.NAME = ? AND NH.IS_ACTIVE=1 ";
        ResultSet resultset = null;
        PreparedStatement preparedstatement = null;
        try {
            preparedstatement = connection.prepareStatement(BW_GET_NH_BY_NAME);
            preparedstatement.setString(1, nhname);
            resultset = preparedstatement.executeQuery();
            if ( resultset.next() ) {
                int nhid = resultset.getInt("ID");
                int levels = resultset.getInt("NEIGHBORHOOD_LEVEL");
                int level0Id = resultset.getInt("LEVEL_0_ID");
                int level1Id = resultset.getInt("LEVEL_1_ID");
                int level2Id = resultset.getInt("LEVEL_2_ID");
                int level3Id = resultset.getInt("LEVEL_3_ID");
                boolean isSecure = resultset.getBoolean("IS_SECURE");
                boolean isActive = resultset.getBoolean("IS_ACTIVE");
                String  name = resultset.getString("NAME");
                return new Neighborhood(nhid, levels, level0Id, level1Id, level2Id, level3Id, isSecure,name);
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

        return null;
    }




    public static Vector getChildrenAndNamesForNeighborhood(Connection connection, Neighborhood nh) throws SystemException
    {
        Vector chList = new Vector();
        int parent_level = nh.getLevels();
        String query = new String();
        if (parent_level == 0) { // find nh1 children
            query =     "select NH.* " +
                        "from " +
                        "  BW_NH as PARENT_NH, " +
                        "  BW_NH as NH, " +
                        "  BW_NH_LEVEL_1 as NH1 " +
                        "where " +
                        "  NH.ID = NH1.NEIGHBORHOOD_ID " +
                        "  and PARENT_NH.NEIGHBORHOOD_LEVEL = 0 " +
                        "  and PARENT_NH.LEVEL_0_ID = NH.LEVEL_0_ID " +
                        "  and PARENT_NH.ID = ? ";
        } else if (parent_level == 1) { // find nh2 children
            query =     "select NH.* " +
                        "from " +
                        "  BW_NH as PARENT_NH, " +
                        "  BW_NH as NH, " +
                        "  BW_NH_LEVEL_2 as NH2 " +
                        "where " +
                        "  NH.ID = NH2.NEIGHBORHOOD_ID " +
                        "  and PARENT_NH.NEIGHBORHOOD_LEVEL = 1 " +
                        "  and PARENT_NH.LEVEL_1_ID = NH.LEVEL_1_ID " +
                        "  and PARENT_NH.ID = ? ";
        } else if (parent_level == 2) { // find nh3 children
            query =     "select NH.* " +
                        "from " +
                        "  BW_NH as PARENT_NH, " +
                        "  BW_NH as NH, " +
                        "  BW_NH_LEVEL_3 as NH3 " +
                        "where " +
                        "  NH.ID = NH3.NEIGHBORHOOD_ID " +
                        "  and PARENT_NH.NEIGHBORHOOD_LEVEL = 2 " +
                        "  and PARENT_NH.LEVEL_2_ID = NH.LEVEL_2_ID " +
                        "  and PARENT_NH.ID = ? ";
        }
        else
            return chList;
    //    System.out.println(query + "NHID = " + nh.getId());
        ResultSet resultset = null;
        PreparedStatement preparedstatement = null;
        try {
            preparedstatement = connection.prepareStatement(query);
            preparedstatement.setInt(1, nh.getId());
            resultset = preparedstatement.executeQuery();
            while ( resultset.next() ) {
                int nhid = resultset.getInt("ID");
                int levels = resultset.getInt("NEIGHBORHOOD_LEVEL");
                int level0Id = resultset.getInt("LEVEL_0_ID");
                int level1Id = resultset.getInt("LEVEL_1_ID");
                int level2Id = resultset.getInt("LEVEL_2_ID");
                int level3Id = resultset.getInt("LEVEL_3_ID");
                boolean isSecure = resultset.getBoolean("IS_SECURE");
                boolean isActive = resultset.getBoolean("IS_ACTIVE");
                String name = resultset.getString("NAME");
      //          System.out.println(name);
                NhName nn =  new NhName(
                     new Neighborhood(nhid, levels, level0Id, level1Id, level2Id, level3Id, isSecure, name),name);
                chList.addElement(nn);
				System.out.println("processing child nh "+ nn.name);

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

        return chList;
    }


    public static void addNewRelation( Connection connection,int nhId,String relation, Vector targetNhIds, int tid)
    throws SystemException
    {
		CallableStatement callablestatement = null;
		 	String CALL_BW_ADD_NEW_NH_REL =
		         "{CALL BW_ADD_NEW_NH_REL(?,?,?,?)}";
		     String CALL_BW_ADD_NEW_NH_REL_TO_OBJECTS  =
		         "{CALL BW_ADD_NEW_NH_REL_TO_OBJECTS(?,?,?)}";

		     try
		     {




		      for ( int r = 0; r < targetNhIds.size(); r++ )
		      {
				   callablestatement = connection.prepareCall(CALL_BW_ADD_NEW_NH_REL);
				    int targetNhId = ((Integer)targetNhIds.elementAt(r)).intValue();
				   callablestatement.setInt(1, nhId);
				   callablestatement.setString(2, relation);
				   callablestatement.setInt(3, targetNhId);
				   callablestatement.setInt(4, tid);
				   callablestatement.executeUpdate();
				   callablestatement.close();
			 		callablestatement = null;
			   }



			    callablestatement = connection.prepareCall(CALL_BW_ADD_NEW_NH_REL_TO_OBJECTS);
			   callablestatement.setInt(1, nhId);
			   callablestatement.setString(2, relation);
			   callablestatement.setInt(3, tid);
			   callablestatement.executeUpdate();



			   callablestatement.close();
			      callablestatement = null;

		     }
		     catch(SQLException sqlexception)
		     {


		       throw new SystemException(sqlexception);
		     }
		     finally
		     {
		        try
		        {
					if ( callablestatement != null )
		           			callablestatement.close();
		        }
		        catch(SQLException sqlexception1)
		        {
		           throw new SystemException(sqlexception1);
		        }
     		}
	}



	 public static void updateNewRelation( Connection connection,int nhId,String relation, Vector targetNhIds, int tid)
	    throws SystemException
	    {
			CallableStatement callablestatement = null;
			 	String CALL_BW_ADD_NEW_NH_REL =
			         "{CALL BW_ADD_NEW_NH_REL(?,?,?,?)}";
			     String CALL_BW_DEL_FOR_UPD_NH_REL  =
			         "{CALL BW_DEL_FOR_UPD_NH_REL(?,?)}";

			     try
			     {
						callablestatement = connection.prepareCall(CALL_BW_DEL_FOR_UPD_NH_REL);
					   callablestatement.setInt(1, nhId);
					   callablestatement.setString(2, relation);
					   callablestatement.executeUpdate();
					   callablestatement.close();
					   callablestatement = null;



					  for ( int r = 0; r < targetNhIds.size(); r++ )
					  {
						   callablestatement = connection.prepareCall(CALL_BW_ADD_NEW_NH_REL);
						   Integer lItargetNhId = (Integer)targetNhIds.elementAt(r);
						   int targetNhId = lItargetNhId.intValue();
						   callablestatement.setInt(1, nhId);
						   callablestatement.setString(2, relation);
						   callablestatement.setInt(3, targetNhId);
						   callablestatement.setInt(4, tid);
						   callablestatement.executeUpdate();
						   callablestatement.close();
							callablestatement = null;
					   }
			     }
			     catch(SQLException sqlexception)
			     {


			       throw new SystemException(sqlexception);
			     }
			     finally
			     {
			        try
			        {
						if ( callablestatement != null )
			           			callablestatement.close();
			        }
			        catch(SQLException sqlexception1)
			        {
			           throw new SystemException(sqlexception1);
			        }
	     		}
	}


public static void deleteRelation(Connection connection,int nhId,String reln , int tid)
          throws NeighborhoodException, SystemException
   {

     CallableStatement callablestatement = null;

     String CALL_DEL_NH_RELS =
         "{CALL BW_DEL_NH_REL(?,?)}";
     int ok;
     boolean bFirstFlag = true;
     try {
       DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
       callablestatement = connection.prepareCall(CALL_DEL_NH_RELS);
       callablestatement.setInt(1, nhId);
       callablestatement.setString(2,reln);
       ok = callablestatement.executeUpdate();

     }
     catch(SQLException sqlexception) {
       throw new SystemException(sqlexception);
     }
     finally {
        try {
           callablestatement.close();
        }
        catch(SQLException sqlexception1) {
           throw new SystemException(sqlexception1);
        }
     }
  }

}
