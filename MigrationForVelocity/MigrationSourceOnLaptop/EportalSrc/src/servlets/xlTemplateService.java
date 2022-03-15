package servlets;
/*
 *  This presents a list of collaboration available to a user
 */
import java.io.*;
import java.text.*;
import java.util.*;
import java.sql.*;
import java.util.*;
import java.util.regex.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.lang.Runtime;
import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.*;
import java.net.URLEncoder;

import com.boardwalk.database.DatabaseLoader;
import com.boardwalk.util.BoardwalkSession;
import com.boardwalk.distribution.*;
import com.boardwalk.member.*;
import com.boardwalk.collaboration.*;
import com.boardwalk.whiteboard.*;
import com.boardwalk.table.*;

import boardwalk.connection.*;
import boardwalk.common.BoardwalkUtility;
import boardwalk.table.*;
import boardwalk.neighborhood.*;

import org.apache.commons.codec.binary.Base64;
public class xlTemplateService extends xlService {
	public final static String Seperator = new Character((char)1).toString();
	public final static String ContentDelimeter = new Character((char)2).toString();
    public void service (HttpServletRequest request,
	    HttpServletResponse response)throws ServletException, IOException
    {
        System.out.println("In xlTemplateService");
		String action = request.getParameter("action");
		System.out.println("action = " + action);
		int UserID = Integer.parseInt((String)request.getParameter("userId"));
		System.out.println("nhId = " + UserID);
		if(action == null)
		{
			getTemplateList(request, response, UserID);
		}
		else if (action.equals("download"))
		{
			downloadTemplate(request, response);
		}

    }
/* Coomented on 22nd Dec for Multimembership support in Invitation Manager 
	public void getTemplateList(HttpServletRequest req,
		HttpServletResponse res, int nhId)
		throws ServletException, IOException
	{
		Connection connection = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		String query = null;
		StringBuffer resData = null;
		String response = null;
		try
		{
			resData = new StringBuffer(10000);

			DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
			connection = databaseloader.getConnection();

			query =
" SELECT DISTINCT PRO.STRING_VALUE, TMPLT.STRING_VALUE \n" +
" FROM   BW_CELL AS TMPLT, BW_COLUMN TMPLTCOL, BW_TBL TMPLTTL, BW_ROW,  \n" +
"        BW_CELL GRP, BW_COLUMN GRPCOL, BW_NH, \n" +
"        BW_CELL PRO, BW_COLUMN PROCOL \n" +
" WHERE \n" +
" 	TMPLT.BW_COLUMN_ID = TMPLTCOL.ID \n" +
" AND TMPLTCOL.BW_TBL_ID = TMPLTTL.ID \n" +
" AND TMPLTCOL.NAME = 'WORKBOOK' \n" +
" AND TMPLTTL.NAME = '__BW_TEMPLATE' \n" +
" AND TMPLT.BW_ROW_ID = BW_ROW.ID \n" +
" AND TMPLT.ACTIVE = 1  \n" +
" AND PRO.BW_ROW_ID = BW_ROW.ID \n" +
" AND PRO.BW_COLUMN_ID = PROCOL.ID \n" +
" AND PROCOL.NAME = 'PROCESS' \n" +
" AND GRP.BW_ROW_ID = BW_ROW.ID \n" +
" AND GRP.BW_COLUMN_ID = GRPCOL.ID \n" +
" AND GRPCOL.NAME = 'USER GROUP' \n" +
" AND GRP.STRING_VALUE = BW_NH.NAME \n" +
" AND BW_NH.ID = ? ";
			System.out.println("query = " + query);
			ps = connection.prepareStatement(query);
			ps.setInt(1, nhId);
			System.out.println("nhId = " + nhId);
			rs = ps.executeQuery();
			while (rs.next())
			{
				resData.append(rs.getString(1) + Seperator);
				resData.append(rs.getString(2) + ContentDelimeter);
			}

			response = resData.toString();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			response = "Failure";
		}
		finally
		{
			try
			{
				if (connection != null)
					connection.close();
				if (ps != null)
					ps.close();
				if (rs != null)
					rs.close();
				connection = null;
				ps = null;
				rs = null;
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			commitResponseBuffer(response, res);
		}
	} */

	public void getTemplateList(HttpServletRequest req, HttpServletResponse res, int aiUserID) throws ServletException, IOException
	{
		Connection connection = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		String query = null;
		StringBuffer resData = null;
		String response = null;
		try
		{
			resData = new StringBuffer(10000);

			DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
			connection = databaseloader.getConnection();

			query =
					" SELECT DISTINCT PRO.STRING_VALUE, TMPLT.STRING_VALUE, BW_NH.ID, BW_NH.NAME, BW_MEMBER.ID  \n" +
					" FROM   BW_CELL AS TMPLT, BW_COLUMN TMPLTCOL, BW_TBL TMPLTTL, BW_ROW,  \n" +
					"        BW_CELL GRP, BW_COLUMN GRPCOL, BW_NH, BW_MEMBER, \n" +
					"        BW_CELL PRO, BW_COLUMN PROCOL \n" +
					" WHERE \n" +
					" 	TMPLT.BW_COLUMN_ID = TMPLTCOL.ID \n" +
					" AND TMPLTCOL.BW_TBL_ID = TMPLTTL.ID \n" +
					" AND TMPLTCOL.NAME = 'WORKBOOK' \n" +
					" AND TMPLTTL.NAME = '__BW_TEMPLATE' \n" +
					" AND TMPLT.BW_ROW_ID = BW_ROW.ID \n" +
					" AND TMPLT.ACTIVE = 1  \n" +
					" AND PRO.BW_ROW_ID = BW_ROW.ID \n" +
					" AND PRO.BW_COLUMN_ID = PROCOL.ID \n" +
					" AND PROCOL.NAME = 'PROCESS' \n" +
					" AND GRP.BW_ROW_ID = BW_ROW.ID \n" +
					" AND GRP.BW_COLUMN_ID = GRPCOL.ID \n" +
					" AND GRPCOL.NAME = 'USER GROUP' \n" +
					" AND GRP.STRING_VALUE = BW_NH.NAME \n" +
					" AND BW_MEMBER.USER_ID = ? \n" +
					" AND BW_NH.ID = BW_MEMBER.NEIGHBORHOOD_ID \n" +
					" AND BW_NH.ID IN (SELECT NEIGHBORHOOD_ID FROM BW_MEMBER WHERE USER_ID = ? ) ";

			System.out.println("query = " + query);
			ps = connection.prepareStatement(query);
			ps.setInt(1, aiUserID);
			ps.setInt(2, aiUserID);

			System.out.println("aiUserID = " + aiUserID);
			rs = ps.executeQuery();

			while (rs.next())
			{
				resData.append(rs.getString(1) + Seperator); 
				resData.append(rs.getString(2) + Seperator);	
				resData.append(rs.getString(3) + Seperator);		// NH ID	
				resData.append(rs.getString(4) + Seperator);		// NH name
				resData.append(rs.getString(5) + ContentDelimeter); // member id
			}

			response = resData.toString();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			response = "Failure";
		}
		finally
		{
			try
			{
				if (connection != null)
					connection.close();
				if (ps != null)
					ps.close();
				if (rs != null)
					rs.close();
				connection = null;
				ps = null;
				rs = null;
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			commitResponseBuffer(response, res);
		}
	}


	public void downloadTemplate(HttpServletRequest req,
		HttpServletResponse res)
		throws ServletException
	{
		Connection connection = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		String query = null;
		StringBuffer resData = null;
		try
		{
			String template = req.getParameter("template");
			int userId = Integer.parseInt((String)req.getParameter("userId"));
			int memberId = Integer.parseInt((String)req.getParameter("memberId"));
			String nhName = (String)req.getParameter("nhName");
			int nhId = Integer.parseInt((String)req.getParameter("nhId"));
			String userName = (String)req.getParameter("userName");
			String processName = (String)req.getParameter("processName"); //Added by Trupti 08/03/2011 to have Process value of Table base invitation manager

			System.out.println("template = " + template);
			System.out.println("userId = " + userId);
			System.out.println("memberId = " + memberId);
			System.out.println("nhName = " + nhName);
			System.out.println("nhId = " + nhId);
			System.out.println("userName = " + userName);
			System.out.println("processName = " + processName);//Added by Trupti 08/03/2011

			//String templatePath = DatabaseLoader.templatedir;
			//System.out.println("templatePath = " + templatePath);
			String templateFilePath = getServletContext().getRealPath("/templates") + "\\" + template;
			System.out.println("Using template = " + templateFilePath);
			FileInputStream templateFile = null;
			templateFile = new FileInputStream(templateFilePath);

			DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
			connection = databaseloader.getConnection();

			BoardwalkDistributionPacket bdp = null;
			BoardwalkConnection bwcon = BoardwalkConnectionManager.getBoardwalkConnection(
				connection, userId, userName, memberId);
			bdp = new BoardwalkDistributionPacket(templateFile, bwcon);

			// get the list of tables for given template
			// Modified by Kavita on 21-12-2010 to capture Collaboration and WB name.
			query =
"SELECT C.bw_row_id BW_ROW_ID, c.bw_column_id COLID, c.string_value STRING_VALUE, col.NAME COLNAME\n" +
 "INTO #TEMPLATETBL \n" +
 "FROM BW_CELL C,BW_TBL T ,BW_ROW R,BW_COLUMN  COL \n" +
 "WHERE T.ID=(SELECT ID FROM BW_TBL WHERE NAME='__BW_TEMPLATE' AND ACTIVE=1) \n" +
 "AND R.BW_TBL_ID=T.ID \n" +
 "AND C.BW_ROW_ID=R.ID \n" +
 "AND COL.ID=C.BW_COLUMN_ID \n" +
 "AND COL.IS_ACTIVE = 1\n" +
 "AND R.IS_ACTIVE = 1\n" +
 "AND c.ACTIVE = 1\n" +
 "CREATE INDEX IDX_TEMP_CELL on #TEMPLATETBL(BW_ROW_ID,COLID)\n" +
 " SELECT rtrim(ltrim(SHT.STRING_VALUE)), X.STRING_VALUE, Y.STRING_VALUE, TBL.NAME, TBL.ID, QRY.STRING_VALUE, MODE.STRING_VALUE, rtrim(ltrim(PRO.STRING_VALUE))  \n" +
 " FROM   #TEMPLATETBL SHT,  \n" +
 "        #TEMPLATETBL X, \n" +
 "        #TEMPLATETBL Y,    \n" +
 "        #TEMPLATETBL T, \n" +
 "        #TEMPLATETBL GRP,\n" +
 "        #TEMPLATETBL NME, \n" +
 "        #TEMPLATETBL QRY, \n" +
 "        #TEMPLATETBL MODE, \n" +
 "        #TEMPLATETBL PRO, \n" +
 "         BW_ROW, BW_NH, BW_TBL TBL, \n" +
 "        BW_WB WB, BW_COLLAB COLB         \n" +
 " WHERE \n" +
 " SHT.COLNAME = 'WORKSHEET'\n" +
 " AND SHT.BW_ROW_ID = BW_ROW.ID \n" +
 " AND X.BW_ROW_ID = BW_ROW.ID\n" +
 " AND X.COLNAME='XPOS'\n" +
 " AND Y.BW_ROW_ID = BW_ROW.ID\n" +
 " AND Y.COLNAME='YPOS'\n" +
 " AND QRY.BW_ROW_ID = BW_ROW.ID\n" +
 " AND QRY.COLNAME='VIEW'\n" +
 " AND MODE.BW_ROW_ID = BW_ROW.ID\n" +
 " AND MODE.COLNAME='MODE'\n" +
 " AND T.BW_ROW_ID = BW_ROW.ID\n" +
 " AND T.COLNAME='TABLE'\n" +
 " AND COLB.NAME = case when (charindex('/', rtrim(ltrim(T.STRING_VALUE))) - 1) > 1 then left(rtrim(ltrim(T.STRING_VALUE)), charindex('/', rtrim(ltrim(T.STRING_VALUE))) - 1)else '' end \n" +
 " AND COLB.ID = WB.BW_COLLAB_ID \n" +
 " AND LTRIM(RTRIM(WB.NAME)) =case when (charindex('/', rtrim(ltrim(T.STRING_VALUE))) - 1) > 1 then substring(ltrim(rtrim(T.STRING_VALUE)),len(substring(ltrim(rtrim(T.STRING_VALUE)),0,charindex('/',ltrim(rtrim(T.STRING_VALUE)))))+2,charindex('/',substring(ltrim(rtrim(T.STRING_VALUE)),len(substring(ltrim(rtrim(T.STRING_VALUE)),0,charindex('/',ltrim(rtrim(T.STRING_VALUE)))))+2,len(ltrim(rtrim(T.STRING_VALUE)))))-1) else '' end \n" +
 " AND TBL.BW_WB_ID = WB.ID \n" +
 " AND TBL.NAME = case when (charindex('/', rtrim(ltrim(T.STRING_VALUE))) - 1) > 1 then right(T.STRING_VALUE, charindex('/',reverse(T.STRING_VALUE)) -1 ) else '' end  \n" +
 " AND GRP.BW_ROW_ID = BW_ROW.ID \n" +
 " AND GRP.COLNAME='USER GROUP' \n" +
 " AND GRP.STRING_VALUE = BW_NH.NAME \n" +
 " AND BW_NH.ID = ? \n" +
 " AND NME.BW_ROW_ID = BW_ROW.ID \n" +
 " AND NME.COLNAME='WORKBOOK'\n" +
 " AND NME.STRING_VALUE =? \n" +
 " AND PRO.BW_ROW_ID = BW_ROW.ID \n" +
 " AND PRO.COLNAME='PROCESS'\n" +
 " AND PRO.STRING_VALUE = ? \n" +
 " ORDER BY BW_ROW.SEQUENCE_NUMBER \n" +
 " DROP TABLE #TEMPLATETBL" ;
			System.out.println("query = " + query);
			ps = connection.prepareStatement(query);
			ps.setInt(1, nhId);
			ps.setString(2, template);
			ps.setString(3, processName);//Added by Trupti 08/03/2011
			rs = ps.executeQuery();
			boolean activeSheetSet = false;
			while (rs.next())
			{
				String sheetName = rs.getString(1);
				int xPos = Integer.parseInt(rs.getString(2));
				int yPos = Integer.parseInt(rs.getString(3));
				String tableName = rs.getString(4);
				int tableId = rs.getInt(5);
				String view = rs.getString(6);
				int mode = Integer.parseInt(rs.getString(7));
				BoardwalkSheet bs = bdp.addSheet(sheetName);
				bs.useTemplateSheet(sheetName);
				if (activeSheetSet == false)
				{
					bdp.setActiveSheet(sheetName);
					activeSheetSet = true;
				}
				bdp.addTableToPacket(tableId, tableName);
				BoardwalkTableDisplay td0 = bdp.addTableDisplay(bs.getName(), tableName);
				td0.setTranspose(false);
				td0.setPlacement(xPos, yPos);
				td0.setView(view);
				td0.setMode(mode);
			}
			ps.close();
			ps = null;
			rs.close();
			rs = null;

			BoardwalkMember m = new BoardwalkMember(bwcon.getMemberId(), bwcon.getUserId(), bwcon.getNeighborhoodId(), bwcon.getNeighborhoodName());
			MimeBodyPart mbp = bdp.getAttachmentForMember(m, templateFilePath);//change recommended by sanjeev " for using invitation define extension of file "
			System.out.println("xlTemplateService: Got attachment for member");
			java.io.InputStream  fileio = mbp.getInputStream();
			String b64String = Base64.encodeBase64String(bdp.getByteArray(fileio));
			// set the response
			ServletOutputStream servletOut = res.getOutputStream();
			res.setContentLength(b64String.length());
			System.out.println("Compressed Response Buffer : Size = " + b64String.length());
			servletOut.print(b64String);
			servletOut.close();
			servletOut.flush();
			System.out.println("Finished writing out the response");
			if (fileio != null)
				fileio.close();

			bdp.deleteTempFile();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			String response = "Failure";
			try
			{
				commitResponseBuffer(response, res);
			}
			catch (Exception e1)
			{
				e1.printStackTrace();
			}
		}
		finally
		{
			try
			{
				if (connection != null)
					connection.close();
				if (ps != null)
					ps.close();
				if (rs != null)
					rs.close();
				connection = null;
				ps = null;
				rs = null;
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}
}
