package com.boardwalk.table;

import java.util.*;
import java.io.*;

import com.boardwalk.database.DatabaseLoader;
import com.boardwalk.exception.*;
import com.boardwalk.database.*;

import java.sql.*;                  // JDBC package
import javax.sql.*;                 // extended JDBC package

import org.apache.commons.lang3.StringUtils;



public class BlobManager{

    private static String CALL_BW_CR_BLOB="{CALL BW_CR_BLOB(?,?,?,?,?,?,?,?,?,?)}";

    public BlobManager() {}

    public static int addDocumentToCell(
							Connection a_connection,
							int tid,
							InputStream in,
							int fileSize,
							String fileName,
							String ext,
							String type,
							String client,
							int cellId,
							String title,
							String screentip
							 )
    throws SQLException
    {
		int blobId = -1;
        CallableStatement callablestatement = null;

        try
        {
			callablestatement = a_connection.prepareCall(CALL_BW_CR_BLOB);
			callablestatement.setBinaryStream( 1,in, fileSize);
			callablestatement.setString(2, fileName);
			callablestatement.setString(3, ext);
			callablestatement.setString(4, type);
			callablestatement.setString(5, client);
			callablestatement.setInt(6, cellId);
			callablestatement.setString(7, title);
			callablestatement.setString(8, screentip);
			callablestatement.setInt(9,tid);
			callablestatement.registerOutParameter(10,java.sql.Types.INTEGER);
			callablestatement.executeUpdate();

			blobId = callablestatement.getInt(10);
        }
		catch( Exception e ) {
		   e.printStackTrace();
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

		return blobId;
		}
		
	private static String parseFilename(String pFilename) {
		int lastIndexOfSlash = pFilename.lastIndexOf("\\");
		if (lastIndexOfSlash == -1) {
			lastIndexOfSlash = pFilename.lastIndexOf("/");
		}
		if (lastIndexOfSlash != -1) {
			pFilename = pFilename.substring(lastIndexOfSlash + 1);
		}
		return pFilename;
	}

	public static void getDocument(
							Connection connection,
							int id,
							javax.servlet.http.HttpServletResponse res
							 )
    throws SQLException
    {
		PreparedStatement statement = null;
		ResultSet resultset 		= null;
		String query 				= "";
		
		try
		{
			query = "SELECT BW_BLOB.FNAME, BW_BLOB.EXT, BW_BLOB.TYPE, BW_BLOB.CLIENT, BW_BLOB.DOC FROM BW_BLOB where id = ?";
			statement = connection.prepareStatement(query);
			statement.setInt(1, id);

			resultset = statement.executeQuery();

			if (resultset.next())
			{

				String fileName = resultset.getString("FNAME");
				String extension = resultset.getString("EXT");
				String type = resultset.getString("TYPE");
				String client = resultset.getString("CLIENT");
				BufferedInputStream in = new BufferedInputStream(
									resultset.getBinaryStream("DOC"));
				//type = StringUtils.replaceEach(type, new String[] {"\n","\\n","\r","\\r","%0d","%0D","%0a","%0A","\025"}, new String[] {"","","","","","","","",""});
				type = type.replaceAll("\\n|\\r","");
				res.setContentType(type);
				String strFileName = parseFilename(fileName);
				System.out.println("filename = " + strFileName);
				//DefaultHTTPUtilities httpUtilities = new DefaultHTTPUtilities(); 
				//httpUtilities.setHeader("Content-Disposition", String.format("attachment; filename=%s", f.getName()).replaceAll("\\n\\r","")); 
				//httpUtilities.addHeader(res, "Content-Disposition", String.format("attachment; filename=%s", f.getName()).replaceAll("\\n\\r",""));
				//res.setHeader("Content-Disposition", "filename=" + f.getName());//01-28-2015 commented for Show Dialogbox to save files
				 res.addHeader("Content-Disposition", String.format("attachment; filename=%s", strFileName).replaceAll("\\n|\\r","")); //01-28-2015 for Show Dialogbox to save files
				//res.setContentLength( buffer.length());
				javax.servlet.ServletOutputStream out = res.getOutputStream();
				int b;
				byte[] buffer = new byte[10240]; // 10kb buffer
				while ((b = in.read(buffer, 0, 10240)) != -1)
				{
					out.write(buffer, 0, b);
				}
			}
			else
			{
				res.sendError(res.SC_NOT_FOUND);
			}
		}
		catch( Exception ex )
		{
			ex.printStackTrace();
		}
		finally
		{
		  try
		  {
			connection.close();
		  }
		  catch ( SQLException sql )
		  {
			sql.printStackTrace();
		  }
		}
	}

};


