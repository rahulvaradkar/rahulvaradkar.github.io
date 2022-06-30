package servlets;
/*

 *  Added on 25-February-2022 by Rahul
 *  xlMigrationServiceExt.java
 */

import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

import com.boardwalk.database.DatabaseLoader;
import com.boardwalk.database.*;
import com.boardwalk.table.ColumnManager;
import com.boardwalk.table.TableManager;
import com.boardwalk.user.*;
//import com.boardwalk.member.Member;
import com.boardwalk.member.*;
import com.boardwalk.exception.*;
import boardwalk.neighborhood.*;
import boardwalk.collaboration.*;
import boardwalk.table.*;

import com.boardwalk.neighborhood.*;
import com.boardwalk.collaboration.*;
import com.boardwalk.whiteboard.*;

import boardwalk.common.*;			//added for BcpLogManager
import boardwalk.connection.BoardwalkConnection;
import boardwalk.connection.BoardwalkConnectionManager;

import java.sql.*;                  // JDBC package
import javax.sql.*;                 // extended JDBC packa


//Added to supplort Migration Package Upload by Rahul on 25-FEB-2022
import org.apache.commons.fileupload.*;
import org.apache.commons.fileupload.disk.*;
import org.apache.commons.fileupload.servlet.*;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;


import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

public class xlMigrationServiceExt extends HttpServlet implements SingleThreadModel
{	
    public void service (HttpServletRequest request, HttpServletResponse response) 	throws ServletException, IOException
    {
		xlMigrationServiceExtLogic logic = new xlMigrationServiceExtLogic(this);
		logic.service(request, response);
    }
}


