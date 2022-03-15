package servlets;
/*
 *  This presents a list of collaboration available to a user
 */
import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.*;

import javax.servlet.*;
import javax.servlet.http.*;
import com.boardwalk.table.*;
import com.boardwalk.excel.*;
import com.boardwalk.exception.*;
import com.boardwalk.database.*;
import java.sql.*;                  // JDBC package
import javax.sql.*;                 // extended JDBC packa
import boardwalk.common.*;
import com.boardwalk.member.Member;
import com.boardwalk.user.UserManager;

import org.apache.commons.codec.binary.Base64;

public class xlExportChangesService extends HttpServlet implements SingleThreadModel
{
	// global variables : should be cleaned up in finally
	int userId = -1;
	int memberId = -1;
	int tableId = -1;
	int importTid = -1;
	int exportTid = -1;
	int isCritical = -1;
	int criticalLevel = 1;
	int numColumns = 0;
	int numRows = 0;
	String asTxComment = "";
	// Error vector to all the Exceptions
	Vector xlErrorCells = new Vector();
	// access variables
	boolean canAddRows = false;
	boolean canDeleteRows = false;
	boolean canAdministerColumns = false;
	StringBuffer newRowBuffer = new StringBuffer();
	StringBuffer newColBuffer = new StringBuffer();
	// access filter
	// see if there is a criterea table associated with this table
	int criteriaTableId = -1;
	// column access
	HashMap accCols = new HashMap();
	int defaultAccess = 2;
	HashMap colCellAccess = new HashMap();
	HashMap accessQueryXrowSet = new HashMap();
	HashMap rowIdHash = new HashMap();
	Vector xlDeleteRows = new Vector();
	HashMap colIdHash = new HashMap();
	boolean RowsDeleted = false; // This will help in detecting if a row was deleted or not.
	boolean ColsDeleted = false; // This will help in detecting if a column was deleted or not.
	ArrayList columnIds = null;


	int MAX_RETRY_ATTEMPTS = 5;
	int RETRY_WAIT_TIME_MIN = 1000;
	int RETRY_WAIT_TIME_MAX = 3000;

	public void service(HttpServletRequest request,
		HttpServletResponse response)throws ServletException, IOException
	{
		xlExportChangesServiceLogic logic = new xlExportChangesServiceLogic(this);
		logic.service(request, response);
	}
}
