package com.boardwalk.database;

import java.io.PrintStream;
import java.sql.*;
import java.util.Properties;
import java.io.*;
import javax.sql.DataSource;
import java.util.*;

public class QueryLoader
{

	Hashtable queryNameToSQL = new Hashtable();

	public QueryLoader(Properties properties)
	{
		String sqlPath = "/sql/";

			queryNameToSQL.put("BW_GET_COLLABS_FOR_NH",readQueryFromFile(sqlPath+"BW_GET_COLLABS_FOR_NH.sql") );
			queryNameToSQL.put("BW_GET_NHS_AT_LEVEL_0",readQueryFromFile(sqlPath+"BW_GET_NHS_AT_LEVEL_0.sql") );
			queryNameToSQL.put("BW_GET_COLLABS_FOR_NH",readQueryFromFile(sqlPath+"BW_GET_COLLABS_FOR_NH.sql") );
			queryNameToSQL.put("BW_GET_NH_RELS",readQueryFromFile(sqlPath+"BW_GET_NH_RELS.sql") );
			queryNameToSQL.put("BW_GET_NH_REL",readQueryFromFile(sqlPath+"BW_GET_NH_REL.sql") );
			queryNameToSQL.put("BW_GET_MEMBERSHIPS_FOR_USER",readQueryFromFile(sqlPath+"BW_GET_MEMBERSHIPS_FOR_USER.sql") );
			queryNameToSQL.put("BW_GET_COLLAB_TBLS_FOR_MEMBER",readQueryFromFile(sqlPath+"BW_GET_COLLAB_TBLS_FOR_MEMBER.sql") );
			queryNameToSQL.put("BW_GET_COLLAB_INFO_FOR_USER",readQueryFromFile(sqlPath+"BW_GET_COLLAB_INFO_FOR_USER.sql") );
			queryNameToSQL.put("BW_GET_TBLS_BY_WB",readQueryFromFile(sqlPath+"BW_GET_TBLS_BY_WB.sql") );
			queryNameToSQL.put("BW_GET_TBLS_BY_WB_AND_BL",readQueryFromFile(sqlPath+"BW_GET_TBLS_BY_WB_AND_BL.sql") );
			queryNameToSQL.put("BW_GET_COLUMNS_BY_TBL_ID",readQueryFromFile(sqlPath+"BW_GET_COLUMNS_BY_TBL_ID.sql") );
			queryNameToSQL.put("BW_GET_ROWCELLS_BY_TBL_AND_U",readQueryFromFile(sqlPath+"BW_GET_ROWCELLS_BY_TBL_AND_U.sql") );
			queryNameToSQL.put("BW_GET_ROWCELLS_BY_USER",readQueryFromFile(sqlPath+"BW_GET_ROWCELLS_BY_USER.sql") );
			queryNameToSQL.put("BW_GET_ROWS_AND_CELLS_BY_TBL_ID",readQueryFromFile(sqlPath+"BW_GET_ROWS_AND_CELLS_BY_TBL_ID.sql") );
			queryNameToSQL.put("BW_GET_TBL_ACCESS_FOR_MEMBER",readQueryFromFile(sqlPath+"BW_GET_TBL_ACCESS_FOR_MEMBER.sql") );
			queryNameToSQL.put("BW_GET_TBL_INFO",readQueryFromFile(sqlPath+"BW_GET_TBL_INFO.sql") );
			queryNameToSQL.put("BW_GET_TBL_ACTION_UI_VALUES",readQueryFromFile(sqlPath+"BW_GET_TBL_ACTION_UI_VALUES.sql") );
			queryNameToSQL.put("BW_GET_TBLS_USING_LKP",readQueryFromFile(sqlPath+"BW_GET_TBLS_USING_LKP.sql"));
			queryNameToSQL.put("BW_LOCK_TBL_FOR_UPD",readQueryFromFile(sqlPath+"BW_LOCK_TBL_FOR_UPD.sql"));
			queryNameToSQL.put("BW_GET_PREVIOUS_TBL_CONFIG",readQueryFromFile(sqlPath+"BW_GET_PREVIOUS_TBL_CONFIG.sql"));
			queryNameToSQL.put("BW_GET_CELL_VER_BEF_TID",readQueryFromFile(sqlPath+"BW_GET_CELL_VER_BEF_TID.sql"));
			queryNameToSQL.put("BW_GET_CELL_VERSIONS",readQueryFromFile(sqlPath+"BW_GET_CELL_VERSIONS.sql"));
			queryNameToSQL.put("BW_GET_PREVIOUS_CELL_VERSION",readQueryFromFile(sqlPath+"BW_GET_PREVIOUS_CELL_VERSION.sql"));
			queryNameToSQL.put("BW_GET_CL_VERS_FOR_C_FOR_DATE",readQueryFromFile(sqlPath+"BW_GET_CL_VERS_FOR_C_FOR_DATE.sql"));
			queryNameToSQL.put("BW_GET_PREV_CL_VERS_FOR_C",readQueryFromFile(sqlPath+"BW_GET_PREV_CL_VERS_FOR_C.sql"));
			queryNameToSQL.put("BW_GET_CL_AT_LAST_EXP_FOR_USR",readQueryFromFile(sqlPath+"BW_GET_CL_AT_LAST_EXP_FOR_USR.sql"));
			queryNameToSQL.put("BW_GET_RCELLS_BY_USRS_OF_NH",readQueryFromFile(sqlPath+"BW_GET_RCELLS_BY_USRS_OF_NH.sql"));
			queryNameToSQL.put("BW_GET_ROWCELLS_FOR_USER",readQueryFromFile(sqlPath+"BW_GET_ROWCELLS_FOR_USER.sql"));
			queryNameToSQL.put("BW_GET_TBL_CONTENTS_BY_BL",readQueryFromFile(sqlPath+"BW_GET_TBL_CONTENTS_BY_BL.sql"));
			queryNameToSQL.put("BW_GET_DES_CELL_VALS_BY_TBL",readQueryFromFile(sqlPath+"BW_GET_DES_CELL_VALS_BY_TBL.sql"));
			queryNameToSQL.put("BW_GET_NH_RELS_FOR_TBL",readQueryFromFile(sqlPath+"BW_GET_NH_RELS_FOR_TBL.sql"));
			queryNameToSQL.put("BW_GET_TBL_ACCESS",readQueryFromFile(sqlPath+"BW_GET_TBL_ACCESS.sql"));
			queryNameToSQL.put("BW_IS_TBL_LOCKED",readQueryFromFile(sqlPath+"BW_IS_TBL_LOCKED.sql"));
			queryNameToSQL.put("BW_GET_TBLS_USING_LKP_CL",readQueryFromFile(sqlPath+"BW_GET_TBLS_USING_LKP_CL.sql"));
	}

	public String getQueryString(String queryName )
	{
		return (String)queryNameToSQL.get(queryName);
	}



	public String readQueryFromFile(String filename)
	 {
		BufferedReader r = null;// used to read the file line by line
		int lineNo = 1;
		String str;
		String query = "  ";


		try
		{
			//System.out.println("Reading query from file " + filename);
			java.net.URL fileURL = null;
			if (DatabaseLoader.servletcontext == null)
			{
				//System.out.println("Extract from JAR file");
				fileURL = getClass().getResource(filename);
				//System.out.println(fileURL);
			}
			else
			{
				//System.out.println("Extract from web application");
				fileURL = DatabaseLoader.servletcontext.getResource(filename);
				//System.out.println(fileURL);
			}
			r = new BufferedReader(new InputStreamReader(fileURL.openStream()) );

			do
			{
				str = r.readLine();
				if( str!=null )
				{
					query = query + "\n" + str;

					lineNo++;

				}
			} while(str!=null);

		}
		catch(Exception e)
		{
			//e.printStackTrace();
//			System.out.println("Error = " + e);
		}


		return query;
      }

}