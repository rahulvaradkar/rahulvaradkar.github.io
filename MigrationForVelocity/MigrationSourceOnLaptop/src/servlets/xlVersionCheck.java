package servlets;
/*
 *  This presents a list of collaboration available to a user
 */
import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import com.boardwalk.exception.*;
import com.boardwalk.database.*;
import java.sql.*;                  // JDBC package
import javax.sql.*;                 // extended JDBC packa
import boardwalk.common.*;


public class xlVersionCheck extends HttpServlet implements SingleThreadModel
{
	public void service(HttpServletRequest request,
		HttpServletResponse response)throws ServletException, IOException
	{
		xlServiceLogic logic = new xlServiceLogic(this);

		Connection connection = null;
		String responseBuffer = "FAILURE";

		try
		{
			System.out.println("Checking version information for Boardwalk client and template");
			// parse the request
			String buf = logic.getRequestBuffer(request).toString();
			//System.out.println("xlVersionCheck buf = " + buf);
			// parse the information
			String[] headerInfo = buf.split(xlServiceLogic.Seperator);
			int xlMajorVersion = Integer.parseInt(headerInfo[0]);
			System.out.println("Excel Major Version = " + xlMajorVersion);
			int xlMinorVersion = Integer.parseInt(headerInfo[1]);
			System.out.println("Excel Minor Version = " + xlMinorVersion);
			String templateName = headerInfo[2];
			System.out.println("Template Name = " + templateName);
			int templateMajorVersion = Integer.parseInt(headerInfo[3]);
			System.out.println("Template Major Version = " + templateMajorVersion);
			int templateMinorVersion = Integer.parseInt(headerInfo[4]);
			System.out.println("Template Minor Version = " + templateMinorVersion);

			// Start a connection
			DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
			connection = databaseloader.getConnection();

			// check the version information
			checkExcelVersion(connection, xlMajorVersion, xlMinorVersion);
			if (templateName != null && !templateName.trim().equalsIgnoreCase(""))
			{
				System.out.println("No template check required");
				checkTemplateVersion(connection, templateName, templateMajorVersion, templateMinorVersion);
			}
			System.out.println("Version Check OK");
			responseBuffer = "SUCCESS";

		}
		catch (BoardwalkException bwe)
		{
			System.out.println("Boardwalk error code = " + bwe.getErrorCode());
			responseBuffer = "FAILURE" + xlServiceLogic.Seperator + bwe.getErrorCode() + xlServiceLogic.Seperator + bwe.getMessage();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			responseBuffer = e.toString();
		}
		finally
		{
			try
			{
				if (connection != null)
					connection.close();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}

			logic.commitResponseBuffer(responseBuffer, response);
		}
	}

	protected void checkTemplateVersion(Connection connection, String templateName, int majorVersion, int minorVersion)
		throws BoardwalkException
	{
		boolean foundMajorVersion = false;
		boolean foundMinorVersion = false;
		try
		{
			PreparedStatement stmt = connection.prepareStatement("SELECT MINOR_VERSION FROM BW_TEMPLATE_VERSION WHERE NAME  = ? AND MAJOR_VERSION = ?");
			stmt.setString(1, templateName);
			stmt.setInt(2, majorVersion);
			ResultSet rs = stmt.executeQuery();

			while (rs.next ())
			{
				foundMajorVersion = true;
				int mVer = rs.getInt(1);
				if (mVer == minorVersion)
				{
					foundMinorVersion = true;
				}
			}
			stmt.close();
			stmt = null;
			rs.close();
			rs = null;
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}

		if (!foundMajorVersion)
		{
			throw new BoardwalkException(13006);
		}
		if (!foundMinorVersion)
		{
			throw new BoardwalkException(13004);
		}
	}

	protected void checkExcelVersion(Connection connection, int excelMajorVersion, int excelMinorVersion)
		throws BoardwalkException
	{
		boolean foundMajorVersion = false;
		boolean foundMinorVersion = false;
		try
		{
			PreparedStatement stmt = connection.prepareStatement("SELECT MINOR_VERSION FROM BW_EXCEL_VERSION WHERE MAJOR_VERSION = ? ");
			stmt.setInt(1, excelMajorVersion);
			ResultSet rs = stmt.executeQuery();
			while (rs.next())
			{
				foundMajorVersion = true;
				int mVer = rs.getInt(1);
				if (mVer == excelMinorVersion)
				{
					foundMinorVersion = true;
				}
			}
			stmt.close();
			stmt = null;
			rs.close();
			rs = null;
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}

		if (!foundMajorVersion)
		{
			throw new BoardwalkException(13005);
		}
		if (!foundMinorVersion)
		{
			throw new BoardwalkException(13003);
		}
	}

}

