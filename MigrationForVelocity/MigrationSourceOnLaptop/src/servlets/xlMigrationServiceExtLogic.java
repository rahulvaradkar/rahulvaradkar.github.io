package servlets;
/*

 *  Added on 25-February-2022 by Rahul
 *  xlMigrationServiceExt.java
 */

import com.boardwalk.table.*;
import com.boardwalk.excel.*;
import com.boardwalk.exception.*;
import com.boardwalk.database.*;
import java.sql.*; // JDBC package
import javax.sql.*; // extended JDBC packa
import com.boardwalk.member.Member;
import com.boardwalk.user.UserManager;

import java.lang.Exception;
import java.security.SecureRandom;
import java.util.zip.*;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.*;



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
import org.apache.commons.codec.binary.Base64;

import java.util.zip.*;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.*;


//public class xlMigrationServiceExt extends xlService implements SingleThreadModel
public class xlMigrationServiceExtLogic extends xlServiceLogic
{

	int _pos = 0;
	ZipInputStream zipIn = null;
	BufferedReader reader = null;
	com.boardwalk.util.UnicodeInputStream uis = null;


	private boolean isMultipart;
	private String filePath;
	private int maxFileSize = 50 * 1024;
	private int maxMemSize = 4 * 1024;
	private File file ;

	private final static Long MILLS_IN_DAY = 86400000L;

	public final static String Seperator = new Character((char)1).toString();
	public final static String ContentDelimeter = new Character((char)2).toString();
	public final static String DataBlockSeperator = new Character((char)3).toString();
	public final static String PipeDelimeter = new Character((char)124).toString();


	private static String PIPE_CHAR = "|";

	private static String CALL_INSERT_DIAGRAM_LINE="{CALL InsertDiagramLine(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
	private static String CALL_INSERT_NH_LIST_LINE="{CALL InsertNhListLine(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";

	private static String CALL_CREATE_DIAGRAM="{CALL CREATE_DIAGRAM(?,?,?,?,?)}";
	private static String CALL_GET_DIAGRAMS="{CALL GET_DIAGRAMS(?)}";

	private static String CALL_GET_DIAGRAM="{CALL GET_DIAGRAM(?)}";
	private static String CALL_GET_DIAGRAM_RECTANGLES="{CALL GET_DIAGRAM_RECTANGLES(?)}";
	private static String CALL_GET_DIAGRAM_NH_LIST="{CALL GET_DIAGRAM_NH_LIST(?)}";


	//SPs added by Rahul for Migration from Source Server to Target Server
	private static String CALL_GET_MIGRATION_INFO="{CALL BW_GET_MIGRATION_INFO_EXT}";
	private static String CALL_ADD_MIGRATION_OBJECT_MAP="{CALL BW_ADD_MIGRATION_OBJECT_MAP (?,?,?,?,?,?,?,?)}";
	private static String CALL_ADD_DEPLOYED_PACKAGES="{CALL BW_ADD_DEPLOYED_PACKAGES(?,?,?,?)}";
	private static String CALL_GET_MIGRATED_OBJECT_ID_FROM_MIGRATION_OBJECT_MAP = "{CALL BW_GET_MIGRATED_OBJECT_ID_FROM_MIGRATION_OBJECT_MAP(?,?,?,?,?)}";


	HttpServletRequest req;
	HttpServletResponse res;

	static StringBuffer sb = null;
	Connection connection = null;
	BoardwalkConnection bwcon = null;

	String migrationUserName;
	String migrationUserPassword;
	int migrationUserId;
	int migrationUserMembershipId;

	String sourceServer; 
	String targetServer; 
	int sourceObjectId;
	int targetObjectId;
	String packageName;

	HashMap <String, String> filePathMap ;

	HashMap <Integer, Integer> sourceSequenceRowIdMap ;
	HashMap <Integer, Integer> sourceSequenceColIdMap ;
	HashMap <Integer, String> sourceSequenceColumnNameMap ;

	//for linkimport functionality ........start ------------------------------------------------------------------

	int numColumns	= 0;
	int numRows		= 0;
	int tableId		= -1;
	int nhId		= -1;

	ArrayList	columnIds = null;
	ArrayList	rowIds = null;
	ArrayList	formulaIds = null;
	ArrayList	strValIds = null;
	String		formulaString = null;

	int userId = -1;
	String userName	= null;
	String userPassword =null;
	int	   memberId		= -1;
	//ArrayList formulaArray = new ArrayList();
	String view = null;//		= "LATEST"; // Will default to Latest.
	String query =null;//= "";

	// Error vector to all the Exceptions
	Vector xlErrorCells = null; //new Vector();
	// access variables
	boolean canAddRows = false;
	boolean canDeleteRows = false;
	boolean canAdministerColumns = false;

//	Connection connection		= null;
	PreparedStatement stmt		= null;
	TransactionManager tm = null;
	int tid = -1;

	int MAX_RETRY_ATTEMPTS = 5;
	int RETRY_WAIT_TIME_MIN = 1000;
	int RETRY_WAIT_TIME_MAX = 3000;


	//for linkimport functionality ......... end ------------------------------------------------------------------


    public xlMigrationServiceExtLogic (xlMigrationServiceExt srv) 
	{
        super(srv);
    }


	public void init( ){
		// Get the file location where it would be stored.
		System.out.println("INSIDE init()");
		//filePath = getServletConfig().getInitParameter("file-upload"); 
		filePath = "D:\\tomcat8.5\\webapps\\BAE_4_6_1_TARGET\\uploadfiles\\";
		System.out.println("INSIDE init()   ---  filePath : " +  filePath);
	}
	
    public void service (HttpServletRequest request, HttpServletResponse response) 	throws ServletException, IOException
    {
		String authBase64String = request.getHeader("Authorization");
		byte[] authSetting = Base64.decodeBase64(authBase64String);
		String auth = new String(authSetting);		

		String loginName = null;
		String loginPwd = null;
		String nhPath = null;
		
		try 
		{
			String[] userLogin = auth.split(":");
			loginName = userLogin[0];
			loginPwd = userLogin[1];
			nhPath = userLogin[2];

			System.out.println("loginName : " + loginName);
			System.out.println("loginPwd : " + loginPwd);
			System.out.println("nhPath : " + nhPath);

		}
		catch (Exception e)
		{
		    System.out.println("Invalid Authorization Format");
			return;
//			erb = new ErrorRequestObject();
//			erb.setError("Invalid Authorization Format");
//			erb.setPath("bwAuthorization.AuthenticateUser::auth.split()");
//			erb.setProposedSolution("Authorization Header shuld be Base64 string user:pwd:nhPath");
//			ErrResps.add(erb);
		}



		migrationUserName = loginName;
		migrationUserPassword = loginPwd;

		System.out.println("migrationUserName: " + migrationUserName);
		System.out.println("migrationUserPassword: " + migrationUserPassword);

		DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
		try 
		{
			connection = databaseloader.getConnection();
		} 
		catch (SQLException e2) 
		{
			// TODO Auto-generated catch block
			e2.printStackTrace();
			return;
		}
			

//			bwcon = getBoardwalkConnection(userName, password);

		System.out.println("migrationUserName : " + migrationUserName);
		User suser = UserManager.getUser(connection, migrationUserName);
		migrationUserId = suser.getId();
		System.out.println("migrationUserId : " + migrationUserId);

		//Creating cuboid using BOARDWALK_APPLICATION 's Neighborhood-0 MEMBERSHIP
		migrationUserMembershipId = getNeighborhood0MembershipId(migrationUserName, nhPath);
		System.out.println("migrationUserMembershipId : " + migrationUserMembershipId);

		response.setContentType("text/html");
		java.io.PrintWriter out = response.getWriter( );

		isMultipart = FileUpload.isMultipartContent(request);

		if( !isMultipart ) {
			System.out.println("<p>No file uploaded</p>"); 
			return;
		}

		filePath = "D://tomcat8.5//webapps//BAE_4_6_1_TARGET//uploadfiles//";
		HttpSession hs = request.getSession(true);
		DiskFileItemFactory factory = new DiskFileItemFactory();
		// maximum size that will be stored in memory
		factory.setSizeThreshold(maxMemSize);
		// Location to save data that is larger than maxMemSize.
		factory.setRepository(new File("D://temp//"));
		// Create a new file upload handler
		ServletFileUpload upload = new ServletFileUpload(factory);
		// maximum file size to be uploaded.
		upload.setSizeMax( maxFileSize );
	  
		String uploadedZipFileName = null;

		try 
		{ 
			// Parse the request to get file items.
			List fileItems = upload.parseRequest(request);
			// Process the uploaded file items
			Iterator i = fileItems.iterator();

			System.out.println("<html>");
			System.out.println("<head>");
			System.out.println("<title>Servlet upload</title>");  
			System.out.println("</head>");
			System.out.println("<body>");

			System.out.println("filePath : " + filePath);

			while ( i.hasNext () ) 
			{
				FileItem fi = (FileItem)i.next();
				if ( !fi.isFormField () ) 
				{
					// Get the uploaded file parameters
					String fieldName = fi.getFieldName();
					String fileName = fi.getName();
					String contentType = fi.getContentType();
					boolean isInMemory = fi.isInMemory();
					long sizeInBytes = fi.getSize();

					// Write the file
					if( fileName.lastIndexOf("\\") >= 0 ) 
					{
						file = new File( filePath + fileName.substring( fileName.lastIndexOf("\\"))) ;
						uploadedZipFileName =  filePath + fileName.substring( fileName.lastIndexOf("\\"));
					} 
					else 
					{
						file = new File( filePath + fileName.substring(fileName.lastIndexOf("\\")+1)) ;
						uploadedZipFileName =  filePath + fileName.substring(fileName.lastIndexOf("\\")+1);
					}
					fi.write(file) ;
					System.out.println("Uploaded Filename: " + fileName );
					System.out.println("Uploaded uploadedZipFileName: " + uploadedZipFileName );
				}
			}
			System.out.println("</body>");
			System.out.println("</html>");
		} 
		catch(Exception ex) 
		{
			System.out.println(ex);
		}

		System.out.println("Displaying Contents of Zip file : " + uploadedZipFileName );
		try 
		{
			FileInputStream fis = new FileInputStream(uploadedZipFileName);
			BufferedInputStream bis = new BufferedInputStream(fis);
			ZipInputStream zis = new ZipInputStream(bis);
			ZipEntry ze;
			while ((ze = zis.getNextEntry()) != null) 
			{
				System.out.format("File: %s Size: %d Last Modified %s %n", ze.getName(), ze.getSize(), LocalDate.ofEpochDay(ze.getTime() / MILLS_IN_DAY));
			}
		}
		catch(Exception ex) 
		{
			System.out.println(ex);
		}

		System.out.println("Unzipping the Contents of Zip file : " + uploadedZipFileName );

		byte[] buffer = new byte[2048];
		String destDir = filePath + "\\output";

		File dir = new File(destDir);
		// create output directory if it doesn't exist
		if(!dir.exists()) dir.mkdirs();

		FileInputStream fis;

//		HashMap <String, String> filePathMap = new HashMap<String, String>();
		filePathMap = new HashMap<String, String>();

		try 
		{
			fis = new FileInputStream(uploadedZipFileName);
			ZipInputStream zis = new ZipInputStream(fis);
			ZipEntry ze = zis.getNextEntry();
			while(ze != null)
			{
				String fileName = ze.getName();
				File newFile = new File(destDir + File.separator + fileName);
				System.out.println("Unzipping to "+newFile.getAbsolutePath());

				filePathMap.put(newFile.getName(), newFile.getAbsolutePath());

				//create directories for sub directories in zip
				new File(newFile.getParent()).mkdirs();
				FileOutputStream fos = new FileOutputStream(newFile);
				int len;
				while ((len = zis.read(buffer)) > 0) 
				{
					fos.write(buffer, 0, len);
				}
				fos.close();
				//close this ZipEntry
				zis.closeEntry();
				ze = zis.getNextEntry();
			}
			//close last ZipEntry
			zis.closeEntry();
			zis.close();
			fis.close();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}

		System.out.println("Unzipping the Contents of Zip file : " + uploadedZipFileName + " is Complete" );

		packageName = uploadedZipFileName;
		sourceServer = "server1";
		targetServer = "server2";

		System.out.println("Iterating Hashmap filePathMap ...");  
		for(Map.Entry m : filePathMap.entrySet())
		{    
			System.out.println(m.getKey()+" : "+m.getValue());    
		}  

		//Read Migration.Commands
		String MigrationCommandsFile = filePathMap.get("Migration.Commands");

		System.out.println("MigrationCommandsFile : " + MigrationCommandsFile);
		System.out.println("mypack.pkg : " + filePathMap.get("mypack.pkg"));

		//Read MigrationCommandsFile line by line
		try  
		{  
			File file=new File(MigrationCommandsFile);    //creates a new file instance  
			FileReader fr=new FileReader(file);				//reads the file  
			BufferedReader br=new BufferedReader(fr);		//creates a buffering character input stream  
			String line;  
			while((line=br.readLine())!=null)  
			{  
				System.out.println("MigrationCommandsFile: line : " + line);       

				String PackageFileKey = line.trim();
				String PackageFile = filePathMap.get(PackageFileKey);

				System.out.println("PackageFile : " + PackageFile);       

				ReadPackageFileAndExecuteCommands (PackageFile);
			}  
			fr.close();    //closes the stream and release the resources  
		}  
		catch(IOException e)  
		{  
			e.printStackTrace();  
		}  


		try
		{
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/*
		req = request;
		res = response;

		String buf = getRequestBuffer(request).toString();
		//System.out.println("Data from client" + buf);

		StringTokenizer st;
		st = new StringTokenizer(buf);

		String action;

		String wrkstr;
		action = st.nextToken(Seperator);

		System.out.println("action="+action);


		String failureReason = "";
		String responseBuffer = "";

		if (  action.equals("GET_MIGRATION_INFO") )
		{
			StringBuffer sbResp = new StringBuffer();
			CallableStatement callableStatement = null;
			ResultSet rs = null;
			ResultSet newRs = null;

			System.out.println("selection action ..........GET_MIGRATION_INFO");
			try
			{
				DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
				connection = databaseloader.getConnection();

				String diagType = null;

				callableStatement = connection.prepareCall(CALL_GET_MIGRATION_INFO);

				rs = callableStatement.executeQuery();
				sbResp.append("success");
				while (rs.next())
				{
					sbResp.append( ContentDelimeter + rs.getString(1) + Seperator + rs.getString(2) + Seperator + rs.getString(3))  ;
				}

				sbResp.append( ContentDelimeter );


				callableStatement = null;

				sbResp.deleteCharAt(sbResp.length()-1);
				responseBuffer = sbResp.toString();

			}
			catch( SQLException sql ) 
			{
				sql.printStackTrace();
				 commitResponseBuffer("failure"
									+ xlService.ContentDelimeter
									+ "Failed to GET MIGRATION INFO", response );
			}
			finally
			{
				try
				{
					if ( callableStatement != null )
						callableStatement.close();
					if (rs != null)
						rs.close();
					if (newRs != null)
						newRs.close();
					if ( connection != null )
						connection.close();
				}
				catch( SQLException sql )
				{
					sql.printStackTrace();
				}
				System.out.println("responseBuffer : " + responseBuffer);
				commitResponseBuffer(responseBuffer, response);

			}
		}


		if (  action.equals("RUN_MIGRATION_COMMANDS") )
		{

			DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
			try 
			{
				connection = databaseloader.getConnection();
			} 
			catch (SQLException e2) 
			{
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			
			System.out.println("Connection " + connection);
	
			System.out.println("Connection is set !!!");
			
			String migrationCommands;
			migrationCommands = st.nextToken(DataBlockSeperator);

			migrationUserName = st.nextToken(Seperator);
			migrationUserName = migrationUserName.substring(1);

			String migrationUserPassword;
			migrationUserPassword = st.nextToken(Seperator);

			System.out.println("migrationUserName: " + migrationUserName);
			System.out.println("migrationUserPassword: " + migrationUserPassword);

//			bwcon = getBoardwalkConnection(userName, password);

			System.out.println("migrationUserName : " + migrationUserName);
			User suser = UserManager.getUser(connection, migrationUserName);
			migrationUserId = suser.getId();
			System.out.println("migrationUserId : " + migrationUserId);

			TransactionManager tm = null;
			int tid = -1 ;

			migrationCommands = migrationCommands.substring(1);

			System.out.println("migrationCommands: " + migrationCommands);

			String[] receivedCommands = migrationCommands.split(ContentDelimeter);
			String currCommand;
			boolean blnNhSecure = true;
			int parentNhId;


			StringBuffer sbResp = new StringBuffer();

			sbResp.append("success");

			for (int i = 0; i < receivedCommands.length; i++) 
			{
				String userName ;
				int userId;

				System.out.println("command " + i + ": " + receivedCommands[i]);
				currCommand = receivedCommands[i];
				String[] commandElements = currCommand.split(Seperator);
S				String Seq = commandElements[0];
				String Params = commandElements[1];
				String command = commandElements[2];
				
				System.out.println("current Command: " + currCommand);
				System.out.println("Seq: " + Seq);
				System.out.println("Params: " + Params);
				System.out.println("command: " + command);

				String[] paramEle = Params.split("\\|");

		        NeighborhoodLevel nhl_0 = null;
		        NeighborhoodLevel_1 nhl_1 = null;
		        NeighborhoodLevel_2 nhl_2 = null;
		        NeighborhoodLevel_3 nhl_3 = null;


				if (!Seq.equals("9"))		//CreateCuboid command
				{
					try 
					{
						tm = new TransactionManager( connection, migrationUserId);
						tid = tm.startTransaction();
					} 
					catch (SQLException e1) 
					{
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}


				if (command.equals("CreateNH_0"))
				{
					String nh0Name;
					nh0Name = Params;
					System.out.println("Creating NH_0 : " + nh0Name + " started.");
					try 
					{
						nhl_0 = NeighborhoodManagerLevel_0.createNeighborhood(connection, nh0Name, tid, blnNhSecure);
						sbResp.append( ContentDelimeter + "Creation of NH_0 : " + nh0Name +  Seperator + "Successful")  ;
					} 
					catch (SystemException | NeighborhoodException e) 
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
						sbResp.append( ContentDelimeter + "Creation of NH_0 : " + nh0Name +  Seperator + "Failed")  ;
					}
					System.out.println("Creating NH_0 : " + nh0Name + " Done.");
				}
				else if (command.equals("CreateNH_1"))
				{
					String nh0Name, nh1Name;
					nh0Name = paramEle[0];
					nh1Name = paramEle[1];

					System.out.println("nh0Name : " + nh0Name );
					System.out.println("nh1Name : " + nh1Name );
					System.out.println("Creating NH_1 : " + nh1Name + " started.");
					//get nh_0 info
					NeighborhoodLevelId nhl_id_0;
					try 
					{
						nhl_id_0 = NeighborhoodManager.getNeighborhoodLevelId(connection, nh0Name.trim(), " ", " ", " ", 0);
						int nh_0_id = nhl_id_0.getId();
						System.out.println("nh_0_id : " + nh_0_id );
						System.out.println("nh_0_name : " + nhl_id_0.getName() );

						parentNhId = nh_0_id;
						System.out.println("Creating nh1 : nh1Name " + nh1Name);
						System.out.println("Creating nh1 : parentNhId " + parentNhId);
						
						nhl_1 = NeighborhoodManagerLevel_1.createNeighborhood(connection, nh1Name, parentNhId , tid, blnNhSecure);
						sbResp.append( ContentDelimeter + "Creation of NH_1 : " + nh1Name +  Seperator + "Successful")  ;
					} 
					catch (SystemException | NeighborhoodException e) 
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
						sbResp.append( ContentDelimeter + "Creation of NH_1 : " + nh1Name +  Seperator + "Failed")  ;
					}
					System.out.println("Creating NH_1 : " + nh1Name + " Done.");
				}
				else if (command.equals("CreateNH_2"))
				{
					String nh0Name, nh1Name, nh2Name;
					nh0Name = paramEle[0];
					nh1Name = paramEle[1];
					nh2Name = paramEle[2];

					System.out.println("Creating NH_2 : " + nh2Name + " started.");
					//get nh_1 info
					NeighborhoodLevelId nhl_id_1;
					try {
						nhl_id_1 = NeighborhoodManager.getNeighborhoodLevelId(connection, nh0Name.trim(), nh1Name.trim(), " ", " ", 1);
						System.out.println("Parent NH Name: " + nhl_id_1.getName());
						int nh_1_id = nhl_id_1.getId();
						parentNhId = nh_1_id;
	                    nhl_2 = NeighborhoodManagerLevel_2.createNeighborhood(connection, nh2Name, parentNhId, tid, blnNhSecure);
						sbResp.append( ContentDelimeter + "Creation of NH_2 : " + nh2Name +  Seperator + "Successful")  ;
					} 
					catch (SystemException | NeighborhoodException e) 
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
						sbResp.append( ContentDelimeter + "Creation of NH_2 : " + nh2Name +  Seperator + "Failed")  ;
					}
					System.out.println("Creating NH_2 : " + nh2Name + " Done.");
				}
				else if (command.equals("CreateNH_3"))
				{
					String nh0Name, nh1Name, nh2Name, nh3Name;
					nh0Name = paramEle[0];
					nh1Name = paramEle[1];
					nh2Name = paramEle[2];
					nh3Name = paramEle[3];

					System.out.println("Creating NH_3 : " + nh3Name + " started.");
					//get nh_2 info
					NeighborhoodLevelId nhl_id_2;
					try 
					{
						nhl_id_2 = NeighborhoodManager.getNeighborhoodLevelId(connection, nh0Name.trim(), nh1Name.trim(), nh2Name.trim(), " ", 2);
						System.out.println("Parent NH Name: " + nhl_id_2.getName());
						int nh_2_id = nhl_id_2.getId();
						parentNhId = nh_2_id ;
	                    nhl_3 = NeighborhoodManagerLevel_3.createNeighborhood(connection, nh3Name, parentNhId, tid, blnNhSecure);
						sbResp.append( ContentDelimeter + "Creation of NH_3 : " + nh3Name +  Seperator + "Successful")  ;
					} 
					catch (SystemException | NeighborhoodException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						sbResp.append( ContentDelimeter + "Creation of NH_3 : " + nh3Name +  Seperator + "Failed")  ;
					}
					System.out.println("Creating NH_3 : " + nh3Name + " Done.");

				}
				else if (command.equals("CreateCollab"))
				{
					String nh0Name, nh1Name, nh2Name, nh3Name,  collabName;
					nh0Name = paramEle[0];
					nh1Name = paramEle[1];
					nh2Name = paramEle[2];
					nh3Name = paramEle[3];
					userName = paramEle[4];
					collabName = paramEle[5];
					
					System.out.println("nh0Name : >" + nh0Name + "<");
					System.out.println("nh1Name : >" + nh1Name + "<");
					System.out.println("nh2Name : >" + nh2Name + "<");
					System.out.println("nh3Name : >" + nh3Name + "<");
					System.out.println("userName : " + userName);
					System.out.println("collabName : " + collabName);

					int nhLevel = -1;
					
					if (!nh0Name.isEmpty() && !nh1Name.isEmpty() && !nh2Name.isEmpty() && !nh3Name.isEmpty() )
						nhLevel = 3;
					else if (!nh0Name.isEmpty() && !nh1Name.isEmpty() && !nh2Name.isEmpty() && nh3Name.isEmpty() )
						nhLevel = 2;
					else if (!nh0Name.isEmpty() && !nh1Name.isEmpty() && nh2Name.isEmpty() && nh3Name.isEmpty() )
						nhLevel = 1;
					else if (!nh0Name.isEmpty() && nh1Name.isEmpty() && nh2Name.isEmpty() && nh3Name.isEmpty() )
						nhLevel = 0;
					
					NeighborhoodLevelId nhl;
					int nhId = -1;
					try {
						System.out.println("Before getting neighborhood levelId");
						System.out.println("nhLevel: " + nhLevel);
						nhl = NeighborhoodManager.getNeighborhoodLevelId(connection, nh0Name.trim(), nh1Name.trim(), nh2Name.trim(), nh3Name.trim(), nhLevel);
						nhId = nhl.getId();
						System.out.println("nhId based on neighborhood: " + nhId);
					} catch (SystemException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					System.out.println("before getting userid : " + userName);
					User user = UserManager.getUser(connection, userName);
					userId = user.getId();
					System.out.println("after getting userId : " + userId);

					Hashtable memberships = null;
					Enumeration memberIds = null ;
					try
					{
						memberships  = UserManager.getMembershipsForUser(connection, userId );
						memberIds = memberships.keys();
						System.out.println("memberships.size : " + memberships.size());
					}
					catch ( Exception e )
					{
					   e.printStackTrace();
					}

					int memberId = -1 ;
					int membernhId = -1;
					String nhName;
					if (memberships.size() == 0 )
					{
						System.out.println("Memberships not found");
					}
					else
					{
						if (  memberships.size() > 0 )
						{
							System.out.println("Checking membership...");
							boolean membershipFound = false;
							for (int ii=0; ii < memberships.size(); ii++)
							{
								memberId =((Integer) memberIds.nextElement()).intValue();
								membernhId =((Member) memberships.get(        new Integer(memberId)   )).getNeighborhoodId();
								nhName = ((Member) memberships.get(        new Integer(memberId)   )).getNeighborhoodName();
								System.out.println("nhId based on membershiop : " + membernhId +   " nhname: " + nhName);
								System.out.println("nhId :" + nhId);
								if (nhId == membernhId)
								{
									System.out.println("Membership found.");
									membershipFound = true;
									break;
								}
							}

							if (membershipFound == true)
							{
								try
								{
									System.out.println("Creating collab : " + collabName);
									int collabId;
									collabId = CollaborationManager.createCollaboration(connection, collabName, "Collab created by Migration", memberId, tid, 1);
									System.out.println("Collab created successfully : " + collabId);
									sbResp.append( ContentDelimeter + "Creation of Collaboration : " + collabName +  Seperator + "Successful")  ;
								}
								catch (Exception e)
								{
									e.printStackTrace();
									sbResp.append( ContentDelimeter + "Creation of Collaboration : " + collabName +  Seperator + "Failed")  ;
									System.out.println("Failed to create Collaboration : " + collabName);
								}
							}
							else
								System.out.println("Membership NOT found.");
						}
					}
				}
				else if (command.equals("CreateWb"))
				{
					String nh0Name, nh1Name, nh2Name, nh3Name,  collabName, wbName;
					nh0Name = paramEle[0];
					nh1Name = paramEle[1];
					nh2Name = paramEle[2];
					nh3Name = paramEle[3];
					collabName = paramEle[4];
					wbName = paramEle[5];
					
					System.out.println("nh0Name : >" + nh0Name + "<");
					System.out.println("nh1Name : >" + nh1Name + "<");
					System.out.println("nh2Name : >" + nh2Name + "<");
					System.out.println("nh3Name : >" + nh3Name + "<");
					System.out.println("collabName : " + collabName);
					System.out.println("wbName : " + wbName);

					int nhLevel = -1;
					
					if (!nh0Name.isEmpty() && !nh1Name.isEmpty() && !nh2Name.isEmpty() && !nh3Name.isEmpty() )
						nhLevel = 3;
					else if (!nh0Name.isEmpty() && !nh1Name.isEmpty() && !nh2Name.isEmpty() && nh3Name.isEmpty() )
						nhLevel = 2;
					else if (!nh0Name.isEmpty() && !nh1Name.isEmpty() && nh2Name.isEmpty() && nh3Name.isEmpty() )
						nhLevel = 1;
					else if (!nh0Name.isEmpty() && nh1Name.isEmpty() && nh2Name.isEmpty() && nh3Name.isEmpty() )
						nhLevel = 0;

					//Get neighborhood ID
					NeighborhoodLevelId nhl;
					int nhId = -1;
					try {
						System.out.println("Before getting neighborhood levelId");
						System.out.println("nhLevel: " + nhLevel);
						nhl = NeighborhoodManager.getNeighborhoodLevelId(connection, nh0Name.trim(), nh1Name.trim(), nh2Name.trim(), nh3Name.trim(), nhLevel);
						nhId = nhl.getId();
						System.out.println("nhId based on neighborhood: " + nhId);

						//Get Collaboration ID
						int collabId = -1;
						Vector cl = CollaborationManager.getCollaborationsOfNeighborhood(connection, nhId);
						collabId = -1;
						Iterator cli = cl.iterator();
						while (cli.hasNext())				// check if collaboration already exists
						{
							CollaborationTreeNode ctn = (CollaborationTreeNode)cli.next();
							//collabList.addElement(new Integer(ctn.getId()));

							if (collabName.trim().equals(ctn.getName()))
							{
								collabId = ctn.getId();		// collaboration exists
								break;
							}
						}

						if (collabId == -1)
							System.out.println("Collaboration not found ");
						else
						{
							int wbId = -1;
							wbId = WhiteboardManager.createWhiteboard(connection, wbName,0, 2, 0, collabId, tid, 1);
			
							if (wbId != -1)
							{
								System.out.println("Whiteboard Created : " + wbId);
								sbResp.append( ContentDelimeter + "Creation of Whiteboard : " + wbName +  Seperator + "Successful")  ;
							}
						}
					} 
					catch (Exception e) 
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
						System.out.println("Failed to create Whiteboard");
						sbResp.append( ContentDelimeter + "Creation of Whiteboard : " + wbName +  Seperator + "Failed")  ;
					}
				}

				else if (command.equals("CreateCuboid"))
				{
					String nh0Name, nh1Name, nh2Name, nh3Name,  collabName, wbName, cuboidName;
					nh0Name = paramEle[0];
					nh1Name = paramEle[1];
					nh2Name = paramEle[2];
					nh3Name = paramEle[3];
					collabName = paramEle[4];
					wbName = paramEle[5];
					cuboidName = paramEle[6];
					
					int nh0MemberId ;

					//Creating cuboid using BOARDWALK_APPLICATION 's Neighborhood-0 MEMBERSHIP
					nh0MemberId = getNeighborhood0MembershipId(migrationUserName, nh0Name);

					bwcon = getBoardwalkConnection(migrationUserName, migrationUserPassword, nh0MemberId);

					System.out.println("Membership of Neighborhood Level 0 of User " + migrationUserName + " is : " + nh0MemberId); 

					System.out.println("nh0Name : >" + nh0Name + "<");
					System.out.println("nh1Name : >" + nh1Name + "<");
					System.out.println("nh2Name : >" + nh2Name + "<");
					System.out.println("nh3Name : >" + nh3Name + "<");
					System.out.println("collabName : " + collabName);
					System.out.println("wbName : " + wbName);

					int nhLevel = -1;
					
					if (!nh0Name.isEmpty() && !nh1Name.isEmpty() && !nh2Name.isEmpty() && !nh3Name.isEmpty() )
						nhLevel = 3;
					else if (!nh0Name.isEmpty() && !nh1Name.isEmpty() && !nh2Name.isEmpty() && nh3Name.isEmpty() )
						nhLevel = 2;
					else if (!nh0Name.isEmpty() && !nh1Name.isEmpty() && nh2Name.isEmpty() && nh3Name.isEmpty() )
						nhLevel = 1;
					else if (!nh0Name.isEmpty() && nh1Name.isEmpty() && nh2Name.isEmpty() && nh3Name.isEmpty() )
						nhLevel = 0;

					//Get neighborhood ID
					NeighborhoodLevelId nhl;
					int nhId = -1;
					try 
					{
						System.out.println("Before getting neighborhood levelId");
						System.out.println("nhLevel: " + nhLevel);
						nhl = NeighborhoodManager.getNeighborhoodLevelId(connection, nh0Name.trim(), nh1Name.trim(), nh2Name.trim(), nh3Name.trim(), nhLevel);
						nhId = nhl.getId();
						System.out.println("nhId based on neighborhood: " + nhId);

						//Get Collaboration ID
						int collabId = -1;
						Vector cl = CollaborationManager.getCollaborationsOfNeighborhood(connection, nhId);
						collabId = -1;
						Iterator cli = cl.iterator();
						while (cli.hasNext())				// check if collaboration already exists
						{
							CollaborationTreeNode ctn = (CollaborationTreeNode)cli.next();
							//collabList.addElement(new Integer(ctn.getId()));

							if (collabName.trim().equals(ctn.getName()))
							{
								collabId = ctn.getId();		// collaboration exists
								break;
							}
						}

						if (collabId == -1)
							System.out.println("Collaboration not found ");
						else
						{
							//Get Whiteboard ID
							int wbId = -1;
							BoardwalkCollaborationNode bcn = BoardwalkCollaborationManager.getCollaborationTree(bwcon, collabId);
							System.out.println("Sucessfully fetched the collab tree from the database");
							System.out.println("Collaboration : " + bcn.getName());							

							Vector wv = bcn.getWhiteboards();
							Iterator wvi = wv.iterator();

							while ( wvi.hasNext())
							{
								BoardwalkWhiteboardNode bwn = (BoardwalkWhiteboardNode)wvi.next();
								System.out.println("\tWhiteboard = " + bwn.getName());
								if (wbName.equals(bwn.getName()))
								{
									wbId= bwn.getId();
									System.out.println("WhiteboardID : " + wbId);
									break;
								}
							}							
							
							if (wbId == -1)
								System.out.println("Whiteboard Not found. Failed to create Cuboid");
							else
							{
								//Create Cuboid
								int tableId = -1;
								tableId = BoardwalkTableManager.createTable(bwcon, collabId, wbId, cuboidName, "Added through Migration process");
								if (tableId != -1)
								{
									System.out.println("Cuboid Created Successfully: " + cuboidName);
									sbResp.append( ContentDelimeter + "Creation of Cuboid : " + cuboidName +  Seperator + "Successful")  ;
								}
							}
						}
					} 
					catch (Exception e) 
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
						System.out.println("Failed to create Cuboid");
						sbResp.append( ContentDelimeter + "Creation of Cuboid : " + cuboidName +  Seperator + "Failed")  ;
					}
				
				}
				else if (command.equals("CreateUser"))
				{
					String firstName;
					String lastName;
					String email; 
					String extUserName;
					String pwd;
					firstName = paramEle[0];
					lastName = paramEle[1];
					email = paramEle[2];
					extUserName = paramEle[3];
					pwd = "0";
					int activeFlag = 1;
					userId = -1;
					try
					{
						NewUser nu = new NewUser(email, pwd, firstName, lastName, activeFlag);
						userId = UserManager.createUser(connection, nu);
						if (userId > 0)
						{
							sbResp.append( ContentDelimeter + "Creation of User : " + email +  Seperator + "Successful")  ;
							System.out.println("Created new User : " + email);
						}
					}
					catch (Exception e)
					{
						e.printStackTrace();
						sbResp.append( ContentDelimeter + "Creation of User : " + email +  Seperator + "Failed")  ;
						System.out.println("Failed to create new User : " + email);
					}
				}
				else if (command.equals("CreateMember"))
				{
					String nh0Name, nh1Name, nh2Name, nh3Name, email;
					nh0Name = paramEle[0];
					nh1Name = paramEle[1];
					nh2Name = paramEle[2];
					nh3Name = paramEle[3];
					email = paramEle[4];
					
					System.out.println("nh0Name : >" + nh0Name + "<");
					System.out.println("nh1Name : >" + nh1Name + "<");
					System.out.println("nh2Name : >" + nh2Name + "<");
					System.out.println("nh3Name : >" + nh3Name + "<");
					System.out.println("email : " + email);

					System.out.println("before getting userid : " + email);
					User user = UserManager.getUser(connection, email);
					userId = user.getId();
					System.out.println("after getting userId : " + userId);

					int nhLevel = -1;
					String nhPath = null;
					
					if (!nh0Name.isEmpty() && !nh1Name.isEmpty() && !nh2Name.isEmpty() && !nh3Name.isEmpty() )
					{
						nhLevel = 3;
						nhPath = nh0Name + "->" + nh1Name + "->" + nh2Name + "->" + nh3Name ;
					}
					else if (!nh0Name.isEmpty() && !nh1Name.isEmpty() && !nh2Name.isEmpty() && nh3Name.isEmpty() )
					{
						nhLevel = 2;
						nhPath = nh0Name + "->" + nh1Name + "->" + nh2Name ;
					}
					else if (!nh0Name.isEmpty() && !nh1Name.isEmpty() && nh2Name.isEmpty() && nh3Name.isEmpty() )
					{
						nhLevel = 1;
						nhPath = nh0Name + "->" + nh1Name ;
					}
					else if (!nh0Name.isEmpty() && nh1Name.isEmpty() && nh2Name.isEmpty() && nh3Name.isEmpty() )
					{
						nhLevel = 0;
						nhPath = nh0Name ;
					}

					NeighborhoodLevelId nhl;
					try {
						System.out.println("Before getting neighborhood levelId");
						System.out.println("nhLevel: " + nhLevel);
						nhl = NeighborhoodManager.getNeighborhoodLevelId(connection, nh0Name.trim(), nh1Name.trim(), nh2Name.trim(), nh3Name.trim(), nhLevel);
						int nhId = nhl.getId();
						System.out.println("after getting neighborhood levelId");
						//int memberId = BoardwalkNeighborhoodManager.createMember(bwcon, nhId, userId);
						System.out.println("nhId: " + nhId);
						System.out.println("userId: " + userId);
						System.out.println("tId: " + tid);
						int memberId = -1;
						memberId = MemberManager.createMember(connection, tid, userId, nhId);

						if (memberId > 0)
						{
							sbResp.append( ContentDelimeter + "Creation of new Member : " + email  + " under nhPath : " + nhPath  +  Seperator + "Successful")  ;
							System.out.println("new Member created under nhPath " + nhPath + " : " + memberId);
						}
					} 
					catch (SystemException e) 
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
						sbResp.append( ContentDelimeter + "Creation of new Member : " + email  + " under nhPath : " + nhPath  +  Seperator + "Failed")  ;
					}
				}

				if (!Seq.equals("9"))
				{
					try 
					{
						tm.commitTransaction();
					} 
					catch (SQLException e) 
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}

			System.out.println("receivedCommands.length: " + receivedCommands.length);

			//sbResp.deleteCharAt(sbResp.length()-1);
			responseBuffer = sbResp.toString();
			System.out.println("responseBuffer : " + responseBuffer);
			commitResponseBuffer(responseBuffer, response);

			
		}


		if (  action.equals("GET DIAGRAM") )
		{
			StringBuffer sbResp = new StringBuffer();
			CallableStatement callableStatement = null;
			ResultSet rs = null;
			ResultSet newRs = null;

			System.out.println("selection action ..........GET DIAGRAM");
			try
			{
				DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
				connection = databaseloader.getConnection();

				int diagId;
				diagId = Integer.parseInt(st.nextToken(Seperator));
				System.out.println("diagId ........" + diagId );

				String diagType = null;

				callableStatement = connection.prepareCall(CALL_GET_DIAGRAM);
				callableStatement.setInt(1, diagId);

	
				rs = callableStatement.executeQuery();
				sbResp.append("success");
				while (rs.next())
				{
					diagType = rs.getString(4);
					sbResp.append( ContentDelimeter + rs.getInt(1) + Seperator + rs.getString(2) + Seperator + rs.getString(3) + Seperator + rs.getString(4) + Seperator + rs.getString(5) + Seperator + rs.getDate(6))  ;
				}

				sbResp.append( ContentDelimeter );


				callableStatement = null;


				StringBuffer sbResp2 = new StringBuffer();
				int rectCount = 0;
				callableStatement = connection.prepareCall(CALL_GET_DIAGRAM_RECTANGLES);
				callableStatement.setInt(1, diagId);

				newRs = callableStatement.executeQuery();
				while (newRs.next())
				{
					rectCount = rectCount + 1;
					sbResp2.append(  newRs.getInt(1) + Seperator + newRs.getInt(2) + Seperator + newRs.getString(3) + Seperator + newRs.getString(4)); 
					sbResp2.append(  Seperator + newRs.getString(5) + Seperator + newRs.getString(6) + Seperator + newRs.getString(7) );
					sbResp2.append(  Seperator + newRs.getString(8) + Seperator + newRs.getString(9) + Seperator + newRs.getString(10) );
					sbResp2.append(  Seperator + newRs.getString(11) + Seperator + newRs.getString(12) + Seperator + newRs.getString(13) ); 
					sbResp2.append(  Seperator + newRs.getString(14) + Seperator + newRs.getString(15) + Seperator + newRs.getString(16) ) ;
					sbResp2.append(  Seperator + newRs.getString(17) + Seperator + newRs.getString(18) + Seperator + newRs.getString(19) ) ;
					sbResp2.append(  Seperator + newRs.getString(20) + Seperator + newRs.getString(21) + Seperator + newRs.getString(22) ) ;
					sbResp2.append(  Seperator + newRs.getString(23) + Seperator + newRs.getString(24) + Seperator + newRs.getString(25) + Seperator + newRs.getString(26) +  ContentDelimeter ) ;
				}
//				callableStatement.close();
				sbResp.append( rectCount + ContentDelimeter + sbResp2.toString());


				if (diagType.equals("NhSetup"))
				{
					callableStatement = null;

					StringBuffer sbResp3 = new StringBuffer();
					int nhListCount = 0;

					callableStatement = connection.prepareCall(CALL_GET_DIAGRAM_NH_LIST);
					callableStatement.setInt(1, diagId);

					newRs = callableStatement.executeQuery();
					while (newRs.next())
					{
						nhListCount = nhListCount + 1;
						sbResp3.append(  newRs.getInt(1) + Seperator + newRs.getString(2) + Seperator + newRs.getString(3) + Seperator + newRs.getString(4)); 
						sbResp3.append(  Seperator + newRs.getString(5) + Seperator + newRs.getString(6) + Seperator + newRs.getString(7) );
						sbResp3.append(  Seperator + newRs.getString(8) + Seperator + newRs.getString(9) + Seperator + newRs.getString(10) );
						sbResp3.append(  Seperator + newRs.getString(11) + Seperator + newRs.getString(12) + Seperator + newRs.getString(13) ); 
						sbResp3.append(  Seperator + newRs.getString(14) + Seperator + newRs.getString(15) + Seperator + newRs.getString(16) ) ;
						sbResp3.append(  Seperator + newRs.getString(17) + Seperator + newRs.getInt(18) +  ContentDelimeter ) ;
					}
					sbResp.append( nhListCount + ContentDelimeter + sbResp3.toString());
	//				callableStatement.close();
				}
				
				sbResp.deleteCharAt(sbResp.length()-1);
				responseBuffer = sbResp.toString();

			}
			catch( SQLException sql ) 
			{
				sql.printStackTrace();
				 commitResponseBuffer("failure"
									+ xlService.ContentDelimeter
									+ "Failed to Get Diagram", response );
			}
			finally
			{
				try
				{
					if ( callableStatement != null )
						callableStatement.close();
					if (rs != null)
						rs.close();
					if (newRs != null)
						newRs.close();
					if ( connection != null )
						connection.close();
				}
				catch( SQLException sql )
				{
					sql.printStackTrace();
				}

				commitResponseBuffer(responseBuffer, response);

			}
		}		// end of GET DIAGRAM



		if (  action.equals("GET ALL DIAGRAMS") )
		{
			StringBuffer sbResp = new StringBuffer();
			CallableStatement callableStatement = null;
			ResultSet rs = null;

			System.out.println("selection action ..........GET ALL DIAGRAMS");
			try
			{
				DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
				connection = databaseloader.getConnection();

				String strRoleUser;
				strRoleUser = st.nextToken(Seperator);
				System.out.println("strRoleUser ........" + strRoleUser );


				callableStatement = connection.prepareCall(CALL_GET_DIAGRAMS);
				callableStatement.setString(1, strRoleUser);

				rs = callableStatement.executeQuery();
				responseBuffer = "success";
				while (rs.next())
				{
					responseBuffer = responseBuffer + ContentDelimeter + rs.getInt(1) + Seperator + rs.getString(2) + Seperator + rs.getString(3) + Seperator + rs.getString(4) + Seperator + rs.getString(5)  + Seperator + rs.getDate(6)  ;
				}

				callableStatement.close();
				rs.close();


			}
			catch( SQLException sql ) 
			{
				sql.printStackTrace();
				 commitResponseBuffer("failure"
									+ xlService.ContentDelimeter
									+ "Failed to Get All Diagram", response );
			}
			finally
			{
				try
				{
					if ( callableStatement != null )
						callableStatement.close();
					if (rs != null)
						rs.close();
					if ( connection != null )
						connection.close();
				}
				catch( SQLException sql )
				{
					sql.printStackTrace();
				}

				commitResponseBuffer(responseBuffer, response);

			}
		}		// end of GET ALL DIAGRAMS
		

		if (  action.equals("CREATE DIAGRAM") )			//Create NH Diagram
		{

			int lines = Integer.parseInt(st.nextToken(Seperator));
			StringBuffer sbResp = new StringBuffer();
			System.out.println("lines .........." + lines);

			CallableStatement callableStatement = null;

			System.out.println("selection action ..........CREATE DIAGRAM");
			try
			{
				DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
				connection = databaseloader.getConnection();

				String diagLine;
				String colSeqInfo = null;
				StringTokenizer sti = null;

				String diagHeader;
				diagHeader = st.nextToken(ContentDelimeter);

				StringTokenizer dh;
				dh = new StringTokenizer(diagHeader);


				String diagName, diagDesc, diagType, CreatedBy ;
				diagName = dh.nextToken(Seperator);
				diagDesc = dh.nextToken(Seperator);
				diagType = dh.nextToken(Seperator);
				CreatedBy = dh.nextToken(Seperator);
				System.out.println("diagName ........" + diagName );
				System.out.println("diagDesc ........" + diagDesc );
				System.out.println("diagType ........" + diagType );
				System.out.println("CreatedBy ........" + CreatedBy );

				int diagId;
				diagId = CreateNewDiagram(connection, diagName, diagDesc, diagType, CreatedBy);

				System.out.println("diagId ........" + diagId );
				System.out.println("lines ........" + lines );

				if (diagId > 0)
				{

					callableStatement = connection.prepareCall(CALL_INSERT_DIAGRAM_LINE);

					for (int col=2; col <= lines ; col++)
					{
						diagLine = "";
						diagLine = st.nextToken(ContentDelimeter);
						System.out.println("diagLine ........" + diagLine );

						sti = new StringTokenizer(diagLine);

						callableStatement.setInt(1, diagId);
						callableStatement.setString(2, sti.nextToken(Seperator));
						callableStatement.setString(3, sti.nextToken(Seperator));
						callableStatement.setString(4, sti.nextToken(Seperator));
						callableStatement.setString(5, sti.nextToken(Seperator));
						callableStatement.setString(6, sti.nextToken(Seperator));
						callableStatement.setString(7, sti.nextToken(Seperator));
						callableStatement.setString(8, sti.nextToken(Seperator));
						callableStatement.setString(9, sti.nextToken(Seperator));
						callableStatement.setString(10, sti.nextToken(Seperator));
						callableStatement.setString(11, sti.nextToken(Seperator));
						callableStatement.setString(12, sti.nextToken(Seperator));
						callableStatement.setString(13, sti.nextToken(Seperator));
						callableStatement.setString(14, sti.nextToken(Seperator));
						callableStatement.setString(15, sti.nextToken(Seperator));
						callableStatement.setString(16, sti.nextToken(Seperator));
						callableStatement.setString(17, sti.nextToken(Seperator));
						callableStatement.setString(18, sti.nextToken(Seperator));
						callableStatement.setString(19, sti.nextToken(Seperator));
						callableStatement.setString(20, sti.nextToken(Seperator));
						callableStatement.setString(21, sti.nextToken(Seperator));
						callableStatement.setString(22, sti.nextToken(Seperator));
						callableStatement.setString(23, sti.nextToken(Seperator));
						callableStatement.setString(24, sti.nextToken(Seperator));
						callableStatement.setString(25, sti.nextToken(Seperator));

						callableStatement.addBatch();
					}
					callableStatement.executeBatch();

					System.out.println("Diagram lines inserted.");

					int nhLines = Integer.parseInt(st.nextToken(ContentDelimeter));
					System.out.println("nhlines.... " + nhLines);

					String nhLine;

					callableStatement = null;
					callableStatement = connection.prepareCall(CALL_INSERT_NH_LIST_LINE);
					for (int nol=2; nol <= nhLines ; nol++)
					{
						nhLine = "";
						nhLine = st.nextToken(ContentDelimeter);
						System.out.println("nhLine ........" + nhLine );

						sti = new StringTokenizer(nhLine);

						callableStatement.setInt(1, diagId);
						callableStatement.setString(2, sti.nextToken(Seperator));
						callableStatement.setString(3, sti.nextToken(Seperator));
						callableStatement.setString(4, sti.nextToken(Seperator));
						callableStatement.setString(5, sti.nextToken(Seperator));
						callableStatement.setString(6, sti.nextToken(Seperator));
						callableStatement.setString(7, sti.nextToken(Seperator));
						callableStatement.setString(8, sti.nextToken(Seperator));
						callableStatement.setString(9, sti.nextToken(Seperator));
						callableStatement.setString(10, sti.nextToken(Seperator));
						callableStatement.setString(11, sti.nextToken(Seperator));
						callableStatement.setString(12, sti.nextToken(Seperator));
						callableStatement.setString(13, sti.nextToken(Seperator));
						callableStatement.setString(14, sti.nextToken(Seperator));
						callableStatement.setString(15, sti.nextToken(Seperator));
						callableStatement.setString(16, sti.nextToken(Seperator));
						callableStatement.setString(17, sti.nextToken(Seperator));
						callableStatement.setInt(18, Integer.parseInt(sti.nextToken(Seperator)));

						callableStatement.addBatch();
					}
					callableStatement.executeBatch();

					System.out.println("Nh lines inserted.");

					sbResp.append(diagName + " is exported successfully." + "\n");

					commitResponseBuffer("success"
								+ xlService.ContentDelimeter
								+ sbResp.toString(), response );
				}
			}

			catch( SQLException sql ) 
			{
				sql.printStackTrace();
				 commitResponseBuffer("failure"
									+ xlService.ContentDelimeter
									+ "Failed to Create Diagram", response );
			}
			finally
			{
				try
				{
					if ( callableStatement != null )
						callableStatement.close();

					if ( connection != null )
						connection.close();
				}
				catch( SQLException sql )
				{
					sql.printStackTrace();
				}
			}
		}		// end of CREATE DIAGRAM


	
		if (  action.equals("CREATE CL DIAGRAM") )			//Create Cuboid Link Diagram
		{

			int lines = Integer.parseInt(st.nextToken(Seperator));
			StringBuffer sbResp = new StringBuffer();
			System.out.println("lines .........." + lines);

			CallableStatement callableStatement = null;

			System.out.println("selection action ..........CREATE CL DIAGRAM");
			try
			{
				DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
				connection = databaseloader.getConnection();

				String diagLine;
				String colSeqInfo = null;
				StringTokenizer sti = null;

				String diagHeader;
				diagHeader = st.nextToken(ContentDelimeter);

				StringTokenizer dh;
				dh = new StringTokenizer(diagHeader);


				String diagName, diagDesc, diagType, CreatedBy ;
				diagName = dh.nextToken(Seperator);
				diagDesc = dh.nextToken(Seperator);
				diagType = dh.nextToken(Seperator);
				CreatedBy = dh.nextToken(Seperator);
				System.out.println("diagName ........" + diagName );
				System.out.println("diagDesc ........" + diagDesc );
				System.out.println("diagType ........" + diagType );
				System.out.println("CreatedBy ........" + CreatedBy );

				int diagId;
				diagId = CreateNewDiagram(connection, diagName, diagDesc, diagType, CreatedBy);

				System.out.println("diagId ........" + diagId );
				System.out.println("lines ........" + lines );

				if (diagId > 0)
				{

					callableStatement = connection.prepareCall(CALL_INSERT_DIAGRAM_LINE);

					for (int col=2; col <= lines ; col++)
					{
						diagLine = "";
						diagLine = st.nextToken(ContentDelimeter);
						System.out.println("diagLine ........" + diagLine );

						sti = new StringTokenizer(diagLine);

						callableStatement.setInt(1, diagId);
						callableStatement.setString(2, sti.nextToken(Seperator));
						callableStatement.setString(3, sti.nextToken(Seperator));
						callableStatement.setString(4, sti.nextToken(Seperator));
						callableStatement.setString(5, sti.nextToken(Seperator));
						callableStatement.setString(6, sti.nextToken(Seperator));
						callableStatement.setString(7, sti.nextToken(Seperator));
						callableStatement.setString(8, sti.nextToken(Seperator));
						callableStatement.setString(9, sti.nextToken(Seperator));
						callableStatement.setString(10, sti.nextToken(Seperator));
						callableStatement.setString(11, sti.nextToken(Seperator));
						callableStatement.setString(12, sti.nextToken(Seperator));
						callableStatement.setString(13, sti.nextToken(Seperator));
						callableStatement.setString(14, sti.nextToken(Seperator));
						callableStatement.setString(15, sti.nextToken(Seperator));
						callableStatement.setString(16, sti.nextToken(Seperator));
						callableStatement.setString(17, sti.nextToken(Seperator));
						callableStatement.setString(18, sti.nextToken(Seperator));
						callableStatement.setString(19, sti.nextToken(Seperator));
						callableStatement.setString(20, sti.nextToken(Seperator));
						callableStatement.setString(21, sti.nextToken(Seperator));
						callableStatement.setString(22, sti.nextToken(Seperator));
						callableStatement.setString(23, sti.nextToken(Seperator));
						callableStatement.setString(24, sti.nextToken(Seperator));
						callableStatement.setString(25, sti.nextToken(Seperator));

						callableStatement.addBatch();
					}
					callableStatement.executeBatch();

					System.out.println("Diagram lines inserted.");

					sbResp.append(diagName + " is exported successfully." + "\n");

					commitResponseBuffer("success"
								+ xlService.ContentDelimeter
								+ sbResp.toString(), response );
				}
			}

			catch( SQLException sql ) 
			{
				sql.printStackTrace();
				 commitResponseBuffer("failure"
									+ xlService.ContentDelimeter
									+ "Failed to Create Diagram", response );
			}
			finally
			{
				try
				{
					if ( callableStatement != null )
						callableStatement.close();

					if ( connection != null )
						connection.close();
				}
				catch( SQLException sql )
				{
					sql.printStackTrace();
				}
			}
		}		// end of CREATE CUBOID LINK DIAGRAM

	
	}
*/
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


/*
	public static int CreateNewDiagram(Connection connection, String diagName, String diagDesc, String diagType, String CreatedBy) throws SQLException
	{
		CallableStatement callablestatement = null;
		int diagId = -1;
		try
		{
			callablestatement = connection.prepareCall(CALL_CREATE_DIAGRAM);
			callablestatement.setString(1, diagName);
			callablestatement.setString(2, diagDesc);
			callablestatement.setString(3, diagType);
			callablestatement.setString(4, CreatedBy);
			callablestatement.registerOutParameter(5, java.sql.Types.INTEGER);

			callablestatement.execute();
			diagId = callablestatement.getInt(5);
		}
		catch (SQLException sql1)
		{
			  throw sql1;
		}
		finally
		{
			  try
			  {
					callablestatement.close();
			  }
			  catch (SQLException sql2)
			  {
					throw sql2;
			  }
		}
		return diagId;
	}
*/
			 //delete this
		}
		catch (Exception e)
		{
			 
		}

	
	}


	public BoardwalkConnection getBoardwalkConnection(String userName, String Password, int memberId) throws IOException
	{
		try
		{
			DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
			connection = databaseloader.getConnection();
			bwcon = BoardwalkConnectionManager.getBoardwalkConnection(connection, userName, Password, memberId);
		}
		catch (SQLException sqe)
		{
			System.out.println("There is a Database connection problem.");
			commitResponseBuffer("There is a Database connection problem. \nContact Boardwalk Administrator for support.", res);
		}
		catch (BoardwalkException bwe)
		{
			System.out.println("Authentication/Connection Failed.");
			commitResponseBuffer("User Authentication/Connection Failed. \nYou have entered wrong User or Password. Try again.", res);
		}
		System.out.println("bwcon : " + bwcon);
		System.out.println("bwcon.getMemberId() : " + bwcon.getMemberId());

		return bwcon;
	}


	public int getNeighborhood0MembershipId(String userName, String nh0Name)
	{
		String nh1Name, nh2Name, nh3Name,  collabName;
		nh1Name = "";
		nh2Name = "";
		nh3Name = "";

		System.out.println("nh0Name : >" + nh0Name + "<");
		System.out.println("nh1Name : >" + nh1Name + "<");
		System.out.println("nh2Name : >" + nh2Name + "<");
		System.out.println("nh3Name : >" + nh3Name + "<");
		System.out.println("userName : " + userName);

		int nhLevel = 0;
					
		NeighborhoodLevelId nhl;
		int nhId = -1;
		try {
			System.out.println("Before getting neighborhood levelId");
			System.out.println("nhLevel: " + nhLevel);
			nhl = NeighborhoodManager.getNeighborhoodLevelId(connection, nh0Name.trim(), nh1Name.trim(), nh2Name.trim(), nh3Name.trim(), nhLevel);
			nhId = nhl.getId();
			System.out.println("nhId based on neighborhood: " + nhId);
		} catch (SystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("before getting userid migrationUserName: " + userName);
		User user = UserManager.getUser(connection, userName);
		int userId = user.getId();
		System.out.println("after getting userId : " + userId);

		Hashtable memberships = null;
		Enumeration memberIds = null ;
		try
		{
			memberships  = UserManager.getMembershipsForUser(connection, userId );
			memberIds = memberships.keys();
			System.out.println("memberships.size : " + memberships.size());
		}
		catch ( Exception e )
		{
		   e.printStackTrace();
		}

		int memberId = -1 ;
		int membernhId = -1;
		int retMembernhId = -1;
		String nhName;
		if (memberships.size() == 0 )
		{
			System.out.println("Memberships not found");
		}
		else
		{
			if (  memberships.size() > 0 )
			{
				System.out.println("Checking membership...");
				boolean membershipFound = false;
				for (int ii=0; ii < memberships.size(); ii++)
				{
					memberId =((Integer) memberIds.nextElement()).intValue();
					membernhId =((Member) memberships.get(        new Integer(memberId)   )).getNeighborhoodId();
					nhName = ((Member) memberships.get(        new Integer(memberId)   )).getNeighborhoodName();
					System.out.println("nhId based on membership : " + membernhId +   " nhname: " + nhName);
					System.out.println("nhId :" + nhId);
					if (nhId == membernhId)
					{
						System.out.println("Membership found.");
						membershipFound = true;
						retMembernhId = membernhId;
						break;
					}
				}

				if (membershipFound == false)
					System.out.println("Membership NOT found.");
			}
		}

		return retMembernhId;
	}


	public void ReadPackageFileAndExecuteCommands(String PackageFile)
	{
		System.out.println("Reading PackageFile : " + PackageFile);

		HashMap <String, String> commandParamMap = new HashMap<String, String>();

		boolean blnNhSecure = true;
		int parentNhId;

		String nh0Name, nh1Name, nh2Name, nh3Name, userName, collabName, wbName, cuboidName;
		String firstName, lastName, email, extUserName, pwd;
		String dataFile, mapFile;
		String collabCreatorName;
		int nhLevel = -1;
		int nh0MemberId = -1 ;

		int sourceCuboidId ;

		NeighborhoodLevelId nhl_id_0, nhl_id_1, nhl_id_2;

		User user;

		int userId;
		NeighborhoodLevelId nhl;

		DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
		try 
		{
			connection = databaseloader.getConnection();
		} 
		catch (SQLException e2) 
		{
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		
		System.out.println("Connection " + connection);

		System.out.println("Connection is set !!!");

		//Tranaction manager was here to start transaction
		TransactionManager tm = null;



		try  
		{  
			File file=new File(PackageFile);				//creates a new file instance  
			FileReader fr=new FileReader(file);				//reads the file  
			BufferedReader br=new BufferedReader(fr);		//creates a buffering character input stream  
			String commandLine;  
			String currCommand ;
			while((commandLine=br.readLine())!=null)  
			{  
				//NH_LEVEL=0|NH0_ID=2|NH0_NAME=Root|COMMAND=CreateNH_0|SEQUENCE=1
				System.out.println("PackageCommand:  " + commandLine);       
				
				commandParamMap = new HashMap<String, String>();

		        NeighborhoodLevel nhl_0 = null;
		        NeighborhoodLevel_1 nhl_1 = null;
		        NeighborhoodLevel_2 nhl_2 = null;
		        NeighborhoodLevel_3 nhl_3 = null;

				currCommand = commandLine;
				String[] commandElements = currCommand.split("\\" + PipeDelimeter);

				System.out.println(commandElements.length);

				int iCount;

				for (iCount=0; iCount < commandElements.length ; iCount++ )
				{
					System.out.println("commandElements[" + iCount + "] : " + commandElements[iCount]);
					String[] paramValue = commandElements[iCount].split("=");

					if (paramValue.length == 2)
					{
						System.out.println("paramValue[0] : " + paramValue[0]);
						System.out.println("paramValue[1] : " + paramValue[1]);
						commandParamMap.put(paramValue[0], paramValue[1]);
					}
					else
						System.out.println("No Value defined");
				}

				int tid = -1 ;


				System.out.println("commandParamMap.get(COMMAND) : " + commandParamMap.get("COMMAND"));
				switch(commandParamMap.get("COMMAND"))
				{
					case "CreateNH_0":
						//NH_LEVEL=0|NH0_ID=2|NH0_NAME=Root|COMMAND=CreateNH_0|SEQUENCE=1
						//String nh0Name;

						int sourceNh0Id = Integer.parseInt(commandParamMap.get("NH0_ID"));
						nh0Name = commandParamMap.get("NH0_NAME");
						System.out.println("Creating NH_0 : " + nh0Name + " started.");

						nhl_id_0 = null;
						try 
						{

							nhl_id_0 = NeighborhoodManager.getNeighborhoodLevelId(connection, nh0Name.trim(), "", "", "", 0);

							if (nhl_id_0 == null)
							{
								System.out.println("Neighbourhood DOES NOT EXISTS at Level 0 : " + nh0Name ); 
								System.out.println("Creating Neighbourhood at Level 0 : " + nh0Name); 

								try  { 	tm = new TransactionManager( connection, migrationUserId); 	tid = tm.startTransaction(); } 
								catch (SQLException e1) { e1.printStackTrace();}

								nhl_0 = NeighborhoodManagerLevel_0.createNeighborhood(connection, nh0Name, tid, blnNhSecure);
								System.out.println("Creation of NH_0 ..... nh0Name : " + nh0Name +  " Successful");
								System.out.println("Creation of NH_0 ..... nhl_0.getId() : " + nhl_0.getNhId() );
								int nh_0_id = nhl_0.getNhId();
								System.out.println("Creation of NH_0 ..... nh_0_id : " + nh_0_id );


								System.out.println("Passing parameters to MAp ");
								System.out.println("nh0Name : " + nh0Name);
								System.out.println("sourceServer : " + sourceServer);
								System.out.println("targetServer : " + targetServer);

								System.out.println("sourceNh0Id : " + sourceNh0Id);
								System.out.println("nh_0_id : " + nh_0_id);
								System.out.println("tid : " + tid);
								System.out.println("packageName : " + packageName);

								//sbResp.append( ContentDelimeter + "Creation of NH_0 : " + nh0Name +  Seperator + "Successful")  ;
								//public static void AddMigrationObjectMap(Connection connection, String sourceServer, String targetServer, int sourceObjectId, int targetObjectId, int tId, String objectType, String packageName) throws SQLException
								AddMigrationObjectMap(connection, nh0Name, sourceServer, targetServer, sourceNh0Id, nh_0_id, tid, "NH_0", packageName); 

								try {	System.out.println("commiting  transaction"); tm.commitTransaction();} 
								catch (SQLException e) { e.printStackTrace(); }
							}
							else
							{
								int nh_0_id = nhl_id_0.getId();
								System.out.println("Neighbourhood at Level 0 Already Exists : " + nh0Name ); 
								System.out.println("Neighbourhood at Level 0 Already Exists with ID : " + nh_0_id); 
								AddMigrationObjectMap(connection, nh0Name, sourceServer, targetServer, sourceNh0Id, nh_0_id, tid, "NH_0", packageName); 
							}

						} 
						catch (SystemException | NeighborhoodException | SQLException e) 
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
							System.out.println("Creation of NH_0 : " + nh0Name +  " Failed");
							//sbResp.append( ContentDelimeter + "Creation of NH_0 : " + nh0Name +  Seperator + "Failed")  ;
						}
						break;
						

					case "CreateNH_1":
						//String nh0Name, nh1Name;
						nh0Name = commandParamMap.get("NH0_NAME");
						nh1Name = commandParamMap.get("NH1_NAME");

						int sourceNh1Id = Integer.parseInt(commandParamMap.get("NH1_ID"));

						System.out.println("nh0Name : " + nh0Name );
						System.out.println("nh1Name : " + nh1Name );
						System.out.println("Creating NH_1 : " + nh1Name + " started.");

						try
						{

							nhl = null;
							try
							{
								nhl = NeighborhoodManager.getNeighborhoodLevelId(connection, nh0Name.trim(), nh1Name.trim(), "", "", 1);
							}
							catch (SystemException e) 
							{
								e.printStackTrace();
								//sbResp.append( ContentDelimeter + "Creation of NH_1 : " + nh1Name +  Seperator + "Failed")  ;
							}

							if (nhl == null)
							{
								System.out.println("Neighbourhood DOES NOT EXISTS at Level 1 : " + nh0Name + "->" + nh1Name); 
								System.out.println("Creating Neighbourhood at Level 1 : " + nh0Name + "->" + nh1Name); 

								try 
								{
									nhl_id_0 = NeighborhoodManager.getNeighborhoodLevelId(connection, nh0Name.trim(), " ", " ", " ", 0);
									int nh_0_id = nhl_id_0.getId();
									System.out.println("nh_0_id : " + nh_0_id );
									System.out.println("nh_0_name : " + nhl_id_0.getName() );

									parentNhId = nh_0_id;
									System.out.println("Creating nh1 : nh1Name " + nh1Name);
									System.out.println("Creating nh1 : parentNhId " + parentNhId);
									
									try  { 	tm = new TransactionManager( connection, migrationUserId); 	tid = tm.startTransaction(); } 
									catch (SQLException e1) { e1.printStackTrace();}

									nhl_1 = NeighborhoodManagerLevel_1.createNeighborhood(connection, nh1Name, parentNhId , tid, blnNhSecure);
									//sbResp.append( ContentDelimeter + "Creation of NH_1 : " + nh1Name +  Seperator + "Successful")  ;
									AddMigrationObjectMap(connection, nh1Name, sourceServer, targetServer, sourceNh1Id, nhl_1.getNhId(), tid, "NH_1", packageName); 

									try {	System.out.println("commiting  transaction"); tm.commitTransaction();} 
									catch (SQLException e) { e.printStackTrace(); }
								} 
								catch (SystemException | NeighborhoodException | SQLException e) 
								{
									e.printStackTrace();
									//sbResp.append( ContentDelimeter + "Creation of NH_1 : " + nh1Name +  Seperator + "Failed")  ;
								}
								System.out.println("Creating NH_1 : " + nh1Name + " Done.");
							}
							else
							{
								int nh_1_id = nhl.getId();
								System.out.println("Neighbourhood at Level 1 Already Exists : " + nh1Name ); 
								System.out.println("Neighbourhood at Level 1 Already Exists with ID : " + nh_1_id); 
								AddMigrationObjectMap(connection, nh1Name, sourceServer, targetServer, sourceNh1Id, nh_1_id, tid, "NH_1", packageName); 
								System.out.println("Neighbourhood at Level 1 Already Exists : " + nh0Name + "->" + nh1Name); 
							}

						}
						catch ( SQLException e) 
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
							System.out.println("Creation of NH_1 : " + nh1Name +  " Failed");
							//sbResp.append( ContentDelimeter + "Creation of NH_1 : " + nh1Name +  Seperator + "Failed")  ;
						}

						break;

					case "CreateNH_2":
						//String nh0Name, nh1Name, nh2Name;
						nh0Name = commandParamMap.get("NH0_NAME");
						nh1Name = commandParamMap.get("NH1_NAME");
						nh2Name = commandParamMap.get("NH2_NAME");

						int sourceNh2Id = Integer.parseInt(commandParamMap.get("NH2_ID"));

						System.out.println("nh0Name : " + nh0Name );
						System.out.println("nh1Name : " + nh1Name );
						System.out.println("nh2Name : " + nh2Name );

						try
						{
							nhl = null;
							try
							{
								nhl = NeighborhoodManager.getNeighborhoodLevelId(connection, nh0Name.trim(), nh1Name.trim(), nh2Name.trim(), "", 2);
							}
							catch (SystemException e) 
							{
								e.printStackTrace();
								//sbResp.append( ContentDelimeter + "Creation of NH_1 : " + nh1Name +  Seperator + "Failed")  ;
							}

							if (nhl == null)
							{
								System.out.println("Neighbourhood DOES NOT EXISTS at Level 2 : " + nh0Name + "->" + nh1Name + "->" + nh2Name); 
								System.out.println("Creating Neighbourhood at Level 2 : " + nh0Name + "->" + nh1Name + "->" + nh2Name); 

								try 
								{
									nhl_id_1 = NeighborhoodManager.getNeighborhoodLevelId(connection, nh0Name.trim(), nh1Name.trim(), " ", " ", 1);
									int nh_1_id = nhl_id_1.getId();
									System.out.println("nh_1_id : " + nh_1_id );
									System.out.println("nh_1_name : " + nhl_id_1.getName() );

									parentNhId = nh_1_id;
									System.out.println("Creating nh2 : nh2Name " + nh2Name);
									System.out.println("Creating nh2 : parentNhId " + parentNhId);
									
									try  { 	tm = new TransactionManager( connection, migrationUserId); 	tid = tm.startTransaction(); } 
									catch (SQLException e1) { e1.printStackTrace();}

									nhl_2 = NeighborhoodManagerLevel_2.createNeighborhood(connection, nh2Name, parentNhId, tid, blnNhSecure);
									System.out.println("Creation of NH_2 : " + nh2Name +  " Successful");
									AddMigrationObjectMap(connection, nh2Name, sourceServer, targetServer, sourceNh2Id, nhl_2.getNhId(), tid, "NH_2", packageName); 

									try {	System.out.println("commiting  transaction"); tm.commitTransaction();} 
									catch (SQLException e) { e.printStackTrace(); }
								} 
								catch (SystemException | NeighborhoodException | SQLException e) 
								{
									e.printStackTrace();
									//sbResp.append( ContentDelimeter + "Creation of NH_1 : " + nh1Name +  Seperator + "Failed")  ;
								}
								System.out.println("Creating NH_2 : " + nh2Name + " Done.");
							}
							else
							{
								int nh_2_id = nhl.getId();
								System.out.println("Neighbourhood at Level 2 Already Exists : " + nh2Name ); 
								System.out.println("Neighbourhood at Level 2 Already Exists with ID : " + nh_2_id); 
								AddMigrationObjectMap(connection, nh2Name, sourceServer, targetServer, sourceNh2Id, nh_2_id, tid, "NH_2", packageName); 
								System.out.println("Neighbourhood at Level 2 Already Exists : " + nh0Name + "->" + nh1Name + "->" + nh2Name); 
							}
						}
						catch ( SQLException e) 
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
							System.out.println("Creation of NH_2 : " + nh2Name +  " Failed");
							//sbResp.append( ContentDelimeter + "Creation of NH_2 : " + nh2Name +  Seperator + "Failed")  ;
						}
						
						break;

					case "CreateNH_3":
						//String nh0Name, nh1Name, nh2Name, nh3Name;
						nh0Name = commandParamMap.get("NH0_NAME");
						nh1Name = commandParamMap.get("NH1_NAME");
						nh2Name = commandParamMap.get("NH2_NAME");
						nh3Name = commandParamMap.get("NH3_NAME");

						int sourceNh3Id = Integer.parseInt(commandParamMap.get("NH3_ID"));

						System.out.println("nh0Name : " + nh0Name );
						System.out.println("nh1Name : " + nh1Name );
						System.out.println("nh2Name : " + nh2Name );
						System.out.println("nh3Name : " + nh3Name );

						try
						{
							
							nhl = null;
							try
							{
								nhl = NeighborhoodManager.getNeighborhoodLevelId(connection, nh0Name.trim(), nh1Name.trim(), nh2Name.trim(), nh3Name.trim(), 3);
							}
							catch (SystemException e) 
							{
								e.printStackTrace();
								//sbResp.append( ContentDelimeter + "Creation of NH_1 : " + nh1Name +  Seperator + "Failed")  ;
							}

							if (nhl == null)
							{
								System.out.println("Neighbourhood DOES NOT EXISTS at Level 3 : " + nh0Name + "->" + nh1Name + "->" + nh2Name + "->" + nh3Name); 
								System.out.println("Creating Neighbourhood at Level 2 : " + nh0Name + "->" + nh1Name + "->" + nh2Name + "->" + nh3Name); 

								try 
								{
									nhl_id_2 = NeighborhoodManager.getNeighborhoodLevelId(connection, nh0Name.trim(), nh1Name.trim(), nh2Name.trim(), " ", 2);
									int nh_2_id = nhl_id_2.getId();
									System.out.println("nh_2_id : " + nh_2_id );
									System.out.println("nh_2_name : " + nhl_id_2.getName() );

									parentNhId = nh_2_id;
									System.out.println("Creating nh3 : nh3Name " + nh3Name);
									System.out.println("Creating nh3 : parentNhId " + parentNhId);
									
									try  { 	tm = new TransactionManager( connection, migrationUserId); 	tid = tm.startTransaction(); } 
									catch (SQLException e1) { e1.printStackTrace();}

									nhl_3 = NeighborhoodManagerLevel_3.createNeighborhood(connection, nh3Name, parentNhId, tid, blnNhSecure);
									System.out.println("Creation of NH_3 : " + nh3Name +  " Successful");
									AddMigrationObjectMap(connection, nh3Name, sourceServer, targetServer, sourceNh3Id, nhl_3.getNhId(), tid, "NH_3", packageName); 

									try {	System.out.println("commiting  transaction"); tm.commitTransaction();} 
									catch (SQLException e) { e.printStackTrace(); }
								} 
								catch (SystemException | NeighborhoodException | SQLException e) 
								{
									e.printStackTrace();
									//sbResp.append( ContentDelimeter + "Creation of NH_1 : " + nh1Name +  Seperator + "Failed")  ;
								}
								System.out.println("Creating NH_3 : " + nh3Name + " Done.");

							}
							else
							{

								int nh_3_id = nhl.getId();
								System.out.println("Neighbourhood at Level 3 Already Exists : " + nh3Name ); 
								System.out.println("Neighbourhood at Level 3 Already Exists with ID : " + nh_3_id); 
								AddMigrationObjectMap(connection, nh3Name, sourceServer, targetServer, sourceNh3Id, nh_3_id, tid, "NH_3", packageName); 
								
								System.out.println("Neighbourhood at Level 3 Already Exists : " + nh0Name + "->" + nh1Name + "->" + nh2Name + "->" + nh3Name); 
							}

						}
						catch ( SQLException e) 
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
							System.out.println("Creation of NH_3 : " + nh3Name +  " Failed");
							//sbResp.append( ContentDelimeter + "Creation of NH_3 : " + nh3Name +  Seperator + "Failed")  ;
						}
						
						break;

					case  "CreateCollab":
						//String nh0Name, nh1Name, nh2Name, nh3Name,  collabName;
						nh0Name = "";
						nh1Name = "";
						nh2Name = "";
						nh3Name = "";

						nh0Name = commandParamMap.get("NH0_NAME") != null ? commandParamMap.get("NH0_NAME").toString() : "";
						nh1Name = commandParamMap.get("NH1_NAME") != null ? commandParamMap.get("NH1_NAME").toString() : "";
						nh2Name = commandParamMap.get("NH2_NAME") != null ? commandParamMap.get("NH2_NAME").toString() : "";
						nh3Name = commandParamMap.get("NH3_NAME") != null ? commandParamMap.get("NH3_NAME").toString() : "";

						int sourceCollabId = Integer.parseInt(commandParamMap.get("COLLAB_ID"));
						int collabCreatorId ;

						collabCreatorName = commandParamMap.get("COLLAB_CREATOR");
						collabName = commandParamMap.get("COLLAB_NAME");

						System.out.println("nh0Name : >" + nh0Name + "<");
						System.out.println("nh1Name : >" + nh1Name + "<");
						System.out.println("nh2Name : >" + nh2Name + "<");
						System.out.println("nh3Name : >" + nh3Name + "<");
						System.out.println("collabName : " + collabName);
						System.out.println("COLLAB_CREATOR : " + collabCreatorName);

						nhLevel = -1;
						
						if (!nh0Name.isEmpty() && !nh1Name.isEmpty() && !nh2Name.isEmpty() && !nh3Name.isEmpty() )
							nhLevel = 3;
						else if (!nh0Name.isEmpty() && !nh1Name.isEmpty() && !nh2Name.isEmpty() && nh3Name.isEmpty() )
							nhLevel = 2;
						else if (!nh0Name.isEmpty() && !nh1Name.isEmpty() && nh2Name.isEmpty() && nh3Name.isEmpty() )
							nhLevel = 1;
						else if (!nh0Name.isEmpty() && nh1Name.isEmpty() && nh2Name.isEmpty() && nh3Name.isEmpty() )
							nhLevel = 0;
						
						nhl = null;
						int nhId = -1;
						try {
							System.out.println("Before getting neighborhood levelId");
							System.out.println("nhLevel: " + nhLevel);
							nhl = NeighborhoodManager.getNeighborhoodLevelId(connection, nh0Name.trim(), nh1Name.trim(), nh2Name.trim(), nh3Name.trim(), nhLevel);
							nhId = nhl.getId();
							System.out.println("nhId based on neighborhood: " + nhId);
						} catch (SystemException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						System.out.println("before getting collabCreatorName : " + collabCreatorName);
						user = UserManager.getUser(connection, collabCreatorName);
						collabCreatorId = user.getId();
						System.out.println("after getting collabCreator Id : " + collabCreatorId);

						//System.out.println("before getting userid : " + migrationUserName);
						//user = UserManager.getUser(connection, migrationUserName);
						//userId = user.getId();
						//System.out.println("after getting userId : " + userId);

						Hashtable memberships = null;
						Enumeration memberIds = null ;
						try
						{
							memberships  = UserManager.getMembershipsForUser(connection, collabCreatorId  );
							memberIds = memberships.keys();
							System.out.println("memberships.size : " + memberships.size());
						}
						catch ( Exception e )
						{
						   e.printStackTrace();
						}

						int memberId = -1 ;
						int membernhId = -1;
						String nhName;
						if (memberships.size() == 0 )
						{
							System.out.println("Memberships not found");
						}
						else
						{
							if (  memberships.size() > 0 )
							{
								System.out.println("Checking membership...");
								boolean membershipFound = false;
								for (int ii=0; ii < memberships.size(); ii++)
								{
									memberId =((Integer) memberIds.nextElement()).intValue();
									membernhId =((Member) memberships.get(        new Integer(memberId)   )).getNeighborhoodId();
									nhName = ((Member) memberships.get(        new Integer(memberId)   )).getNeighborhoodName();
									System.out.println("nhId based on membershiop : " + membernhId +   " nhname: " + nhName);
									System.out.println("nhId :" + nhId);
									if (nhId == membernhId)
									{
										System.out.println("Membership found.");
										membershipFound = true;
										break;
									}
								}

								if (membershipFound == true)
								{
									try
									{
										System.out.println("Creating collab : " + collabName);
										//Check Collaboration Exists
										int collabId = -1;
										boolean collabFound = false;
										Vector cl = CollaborationManager.getCollaborationsOfNeighborhood(connection, nhId);
										collabId = -1;
										Iterator cli = cl.iterator();
										while (cli.hasNext())				// check if collaboration already exists
										{
											CollaborationTreeNode ctn = (CollaborationTreeNode)cli.next();
											//collabList.addElement(new Integer(ctn.getId()));

											if (collabName.trim().equals(ctn.getName()))
											{
												collabId = ctn.getId();		// collaboration exists
												collabFound = true;
												break;
											}
										}

										if (!collabFound)
										{
											try  { 	tm = new TransactionManager( connection, collabCreatorId ); 	tid = tm.startTransaction(); } 
											catch (SQLException e1) { e1.printStackTrace();}

											//Earlier Collab was created by COLLAB_CREATOR membership of the Neighborhood
											collabId = CollaborationManager.createCollaboration(connection, collabName, "Collab created by Migration", memberId, tid, 1);

											AddMigrationObjectMap(connection, collabName, sourceServer, targetServer, sourceCollabId, collabId, tid, "Collab", packageName); 

											try {	System.out.println("commiting  transaction"); tm.commitTransaction();} 
											catch (SQLException e) { e.printStackTrace(); }

											//Here it is created by "admin" who is member of "ROOT". Using ROOT membership of "admin" user	
											//System.out.println("Collab creation started using 'admin' membership of 'ROOT' memberid : " + membernhId);
											//collabId = CollaborationManager.createCollaboration(connection, collabName, "Collab created by Migration", membernhId, tid, 1);
											System.out.println("Collab created successfully : " + collabId);
											//sbResp.append( ContentDelimeter + "Creation of Collaboration : " + collabName +  Seperator + "Successful")  ;
										}
										else
										{
											AddMigrationObjectMap(connection, collabName, sourceServer, targetServer, sourceCollabId, collabId, tid, "Collab", packageName); 
											System.out.println("Collaboration Already Exists :" + collabName);
										}
									}
									catch (Exception   e)
									{
										e.printStackTrace();
										//sbResp.append( ContentDelimeter + "Creation of Collaboration : " + collabName +  Seperator + "Failed")  ;
										System.out.println("Failed to create Collaboration : " + collabName);
									}
								}
								else
									System.out.println("Membership NOT found.");
							}
						}
						break;

					case  "CreateWb":
						//String nh0Name, nh1Name, nh2Name, nh3Name,  collabName, wbName;
						nh0Name = "";
						nh1Name = "";
						nh2Name = "";
						nh3Name = "";
						nhLevel = Integer.parseInt(commandParamMap.get("NH_LEVEL"));

						nh0Name = commandParamMap.get("NH0_NAME") != null ? commandParamMap.get("NH0_NAME").toString() : "";
						nh1Name = commandParamMap.get("NH1_NAME") != null ? commandParamMap.get("NH1_NAME").toString() : "";
						nh2Name = commandParamMap.get("NH2_NAME") != null ? commandParamMap.get("NH2_NAME").toString() : "";
						nh3Name = commandParamMap.get("NH3_NAME") != null ? commandParamMap.get("NH3_NAME").toString() : "";
						nh3Name = commandParamMap.get("NH3_NAME") != null ? commandParamMap.get("NH3_NAME").toString() : "";
						
						collabName = commandParamMap.get("COLLAB_NAME");
						wbName = commandParamMap.get("WB_NAME");
						
						int sourceWbId = Integer.parseInt(commandParamMap.get("WB_ID"));


						System.out.println("nh0Name : >" + nh0Name + "<");
						System.out.println("nh1Name : >" + nh1Name + "<");
						System.out.println("nh2Name : >" + nh2Name + "<");
						System.out.println("nh3Name : >" + nh3Name + "<");
						System.out.println("collabName : " + collabName);
						System.out.println("wbName : " + wbName);


						//Get neighborhood ID
						nhl = null;
						nhId = -1;
						try {
							System.out.println("Before getting neighborhood levelId");
							System.out.println("nhLevel: " + nhLevel);
							nhl = NeighborhoodManager.getNeighborhoodLevelId(connection, nh0Name.trim(), nh1Name.trim(), nh2Name.trim(), nh3Name.trim(), nhLevel);
							nhId = nhl.getId();
							System.out.println("nhId based on neighborhood: " + nhId);

							//Get Collaboration ID
							int collabId = -1;
							Vector cl = CollaborationManager.getCollaborationsOfNeighborhood(connection, nhId);
							collabId = -1;
							Iterator cli = cl.iterator();
							while (cli.hasNext())				// check if collaboration already exists
							{
								CollaborationTreeNode ctn = (CollaborationTreeNode)cli.next();
								//collabList.addElement(new Integer(ctn.getId()));

								if (collabName.trim().equals(ctn.getName()))
								{
									collabId = ctn.getId();		// collaboration exists
									System.out.println("Collaboration Found. CollabId = " + collabId);
									break;
								}
							}

							if (collabId == -1)
								System.out.println("Collaboration not found ");
							else
							{
								int wbId = -1;

								//Creating cuboid using BOARDWALK_APPLICATION 's Neighborhood-0 MEMBERSHIP
								nh0MemberId = getNeighborhood0MembershipId(migrationUserName, nh0Name);
								bwcon = getBoardwalkConnection(migrationUserName, migrationUserPassword, nh0MemberId);

								BoardwalkCollaborationNode bcn = null;
								bcn = BoardwalkCollaborationManager.getCollaborationTree(bwcon, collabId);
								
								if (bcn == null) 
								{
									//throw new NoSuchElementException("Collaboration Id NOT FOUND") ;
									throw new BoardwalkException( 10018 );
								}    		
								System.out.println("Sucessfully fetched the collab tree from the database");

								boolean wbExists = false;
								Vector<?> wv = bcn.getWhiteboards();
								Iterator<?> wvi = wv.iterator();
								while ( wvi.hasNext())
								{
									BoardwalkWhiteboardNode bwn = (BoardwalkWhiteboardNode)wvi.next();
									System.out.println("\tWhiteboard = " + bwn.getName());

									if (wbName.trim().equals(bwn.getName()))
									{
										wbId = bwn.getId();
										System.out.println("\tWhiteboard ALREADY EXISTS = " + bwn.getName());
										wbExists = true;
										break;
									}
								}
							
								if(wbExists == false)
								{
									try  { 	tm = new TransactionManager( connection, migrationUserId); 	tid = tm.startTransaction(); } 
									catch (SQLException e1) { e1.printStackTrace();}
									
									wbId = WhiteboardManager.createWhiteboard(connection, wbName,0, 2, 0, collabId, tid, 1);

									AddMigrationObjectMap(connection, wbName, sourceServer, targetServer, sourceWbId, wbId, tid, "Wb", packageName); 

									try {	System.out.println("commiting  transaction"); tm.commitTransaction();} 
									catch (SQLException e) { e.printStackTrace(); }

									if (wbId != -1)
									{
										System.out.println("Whiteboard Created : " + wbId);
										//sbResp.append( ContentDelimeter + "Creation of Whiteboard : " + wbName +  Seperator + "Successful")  ;
									}
								}
								else
								{
									AddMigrationObjectMap(connection, wbName, sourceServer, targetServer, sourceWbId, wbId, tid, "Wb", packageName); 
								}
							}
						} 
						catch (Exception  e) 
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
							System.out.println("Failed to create Whiteboard : " + wbName );
							//sbResp.append( ContentDelimeter + "Creation of Whiteboard : " + wbName +  Seperator + "Failed")  ;
						}
						break;

					case "CreateCuboid":
						//String nh0Name, nh1Name, nh2Name, nh3Name,  collabName, wbName, cuboidName;
						boolean cuboidExist = false;
						String tableName;

						nh0Name = "";
						nh1Name = "";
						nh2Name = "";
						nh3Name = "";

						nhLevel = Integer.parseInt(commandParamMap.get("NH_LEVEL"));

						nh0Name = commandParamMap.get("NH0_NAME") != null ? commandParamMap.get("NH0_NAME").toString() : "";
						nh1Name = commandParamMap.get("NH1_NAME") != null ? commandParamMap.get("NH1_NAME").toString() : "";
						nh2Name = commandParamMap.get("NH2_NAME") != null ? commandParamMap.get("NH2_NAME").toString() : "";
						nh3Name = commandParamMap.get("NH3_NAME") != null ? commandParamMap.get("NH3_NAME").toString() : "";

						collabName = commandParamMap.get("COLLAB_NAME");
						wbName = commandParamMap.get("WB_NAME");
						cuboidName = commandParamMap.get("CUBOID_NAME");
						
						sourceCuboidId = Integer.parseInt(commandParamMap.get("CUBOID_ID"));

						//Creating cuboid using BOARDWALK_APPLICATION 's Neighborhood-0 MEMBERSHIP
						nh0MemberId = getNeighborhood0MembershipId(migrationUserName, nh0Name);

						bwcon = getBoardwalkConnection(migrationUserName, migrationUserPassword, nh0MemberId);

						System.out.println("Membership of Neighborhood Level 0 of User " + migrationUserName + " is : " + nh0MemberId); 

						System.out.println("nh0Name : >" + nh0Name + "<");
						System.out.println("nh1Name : >" + nh1Name + "<");
						System.out.println("nh2Name : >" + nh2Name + "<");
						System.out.println("nh3Name : >" + nh3Name + "<");
						System.out.println("collabName : " + collabName);
						System.out.println("wbName : " + wbName);

						//Get neighborhood ID
						nhl = null;
						nhId = -1;
						try 
						{
							System.out.println("Before getting neighborhood levelId");
							System.out.println("nhLevel: " + nhLevel);
							nhl = NeighborhoodManager.getNeighborhoodLevelId(connection, nh0Name.trim(), nh1Name.trim(), nh2Name.trim(), nh3Name.trim(), nhLevel);
							nhId = nhl.getId();
							System.out.println("nhId based on neighborhood: " + nhId);

							//Get Collaboration ID
							int collabId = -1;
							Vector cl = CollaborationManager.getCollaborationsOfNeighborhood(connection, nhId);
							collabId = -1;
							Iterator cli = cl.iterator();
							while (cli.hasNext())				// check if collaboration already exists
							{
								CollaborationTreeNode ctn = (CollaborationTreeNode)cli.next();
								//collabList.addElement(new Integer(ctn.getId()));

								if (collabName.trim().equals(ctn.getName()))
								{
									collabId = ctn.getId();		// collaboration exists
									break;
								}
							}

							if (collabId == -1)
								System.out.println("Collaboration not found ");
							else
							{
								//Get Whiteboard ID
								int wbId = -1;
								BoardwalkCollaborationNode bcn = BoardwalkCollaborationManager.getCollaborationTree(bwcon, collabId);
								System.out.println("Sucessfully fetched the collab tree from the database");
								System.out.println("Collaboration : " + bcn.getName());							

								Vector wv = bcn.getWhiteboards();
								Iterator wvi = wv.iterator();

								while ( wvi.hasNext())
								{
									BoardwalkWhiteboardNode bwn = (BoardwalkWhiteboardNode)wvi.next();
									System.out.println("\tWhiteboard = " + bwn.getName());
									if (wbName.equals(bwn.getName()))
									{
										wbId= bwn.getId();
										System.out.println("WhiteboardID : " + wbId);

										Vector tv = bwn.getTables();
										Iterator tvi = tv.iterator();

										cuboidExist = false;

										if (tvi.hasNext())
										{
											while (tvi.hasNext())
											{
												tableName = "";
												BoardwalkTableNode btn = (BoardwalkTableNode)tvi.next();
												System.out.println("\t\tTable = " + btn.getName());
												tableName = btn.getName();
												if (cuboidName.equals(btn.getName()))
												{
													cuboidExist = true;
													tableId = btn.getId();
													System.out.println("Cuboid Aready Exists : " + tableName);
													break;
												}
												//Collabline = collabName + Seperator + whiteBoard + Seperator + tableName + Seperator ;
												//Collabline = Collabline + collabId + Seperator + wbId + Seperator + tableId + Seperator ;
												//Collabline = Collabline + collabName + "\\" + whiteBoard + "\\" + tableName ;
												//sb.append(Collabline + "\n");
											}  // End of while Table ITERATOR
										}

										if (cuboidExist)
										{
											break;
										}
									}
								}		// End of While	Whiteboard				
								
								if (wbId == -1)
									System.out.println("Whiteboard Not found. Failed to create Cuboid");
								else
								{
									if (cuboidExist)
									{
										AddMigrationObjectMap(connection, cuboidName, sourceServer, targetServer, sourceCuboidId, tableId, -1, "Cuboid", packageName); 
									}
									else
									{
										//Create Cuboid
										int tableId = -1;
										//try  { 	tm = new TransactionManager( connection, migrationUserId); 	tid = tm.startTransaction(); } 
										//catch (SQLException e1) { e1.printStackTrace();}
										tableId = BoardwalkTableManager.createTable(bwcon, collabId, wbId, cuboidName, "Added through Migration process");
										AddMigrationObjectMap(connection, cuboidName, sourceServer, targetServer, sourceCuboidId, tableId, -1, "Cuboid", packageName); 
										//try {	System.out.println("commiting  transaction"); tm.commitTransaction();} 
										//catch (SQLException e) { e.printStackTrace(); }
										if (tableId != -1)
										{
											System.out.println("Cuboid Created Successfully: " + cuboidName);
											//sbResp.append( ContentDelimeter + "Creation of Cuboid : " + cuboidName +  Seperator + "Successful")  ;
										}
									}
								}
							}
						} 
						catch (Exception e) 
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
							System.out.println("Failed to create Cuboid");
							//sbResp.append( ContentDelimeter + "Creation of Cuboid : " + cuboidName +  Seperator + "Failed")  ;
						}

						break;

					case "LinkImportCuboid":
//NH_LEVEL=0|NH0_ID=2|NH0_NAME=Root|NH1_ID=|NH1_NAME=|NH2_ID=|NH2_NAME=|NH3_ID=|NH3_NAME=|COLLAB_ID=2|COLLAB_NAME=Sonic|WB_ID=2|WB_NAME=Main|CUBOID_ID=2000002|CUBOID_NAME=New Buy|COMMAND=LinkImportCuboid|SEQUENCE=10|DATA_FILE=LinkExport_2000002.le|MAP_FILE=LinkExport_2000002.mp
						//String nh0Name, nh1Name, nh2Name, nh3Name,  collabName, wbName, cuboidName;
						nh0Name = "";
						nh1Name = "";
						nh2Name = "";
						nh3Name = "";
						collabName = "";
						wbName = "";
						cuboidName = "";
						dataFile = "";
						mapFile = "";

						sourceCuboidId = Integer.parseInt(commandParamMap.get("CUBOID_ID"));

						nh0Name = commandParamMap.get("NH0_NAME") != null ? commandParamMap.get("NH0_NAME").toString() : "";
						nh1Name = commandParamMap.get("NH1_NAME") != null ? commandParamMap.get("NH1_NAME").toString() : "";
						nh2Name = commandParamMap.get("NH2_NAME") != null ? commandParamMap.get("NH2_NAME").toString() : "";
						nh3Name = commandParamMap.get("NH3_NAME") != null ? commandParamMap.get("NH3_NAME").toString() : "";

						collabName = commandParamMap.get("COLLAB_NAME");
						wbName = commandParamMap.get("WB_NAME");
						cuboidName = commandParamMap.get("CUBOID_NAME");
						dataFile = commandParamMap.get("DATA_FILE");
						mapFile = commandParamMap.get("MAP_FILE");


						int targetCuboidId = -1;

						try
						{
							targetCuboidId = GetMigratedObjectIdOnTargetServerUsingObjectMap(connection, cuboidName, sourceServer, sourceCuboidId, "Cuboid");
						}
						catch (SQLException e)
						{
							e.printStackTrace();
						}

						System.out.println("Source Cuboid Id : " + sourceCuboidId);
						System.out.println("Target Cuboid Id : " + targetCuboidId);

						//Read Migration.Commands
						String linkImportDataFile = filePathMap.get(dataFile);
						String linkImportMapFile = filePathMap.get(mapFile);

						System.out.println("linkImportDataFile : " + linkImportDataFile);
						System.out.println("linkImportMapFile : " + linkImportMapFile);

						readLinkImportMapFile (linkImportMapFile);

						readLinkImportBufferfFile (targetCuboidId, linkImportDataFile, linkImportMapFile);

						break;

					case "CreateUser":
						firstName = "";
						lastName = "";
						email = "";
						extUserName = "";
						pwd = "";

//USER_ID=100|FIRSTNAME=r|LASTNAME=pv|EMAIL_ADDRESS=rpv1@hotmail.com|EXTERNAL_USER_ID=rpv1@hotmail.com|COMMAND=CreateUser|SEQUENCE=5

						firstName = commandParamMap.get("FIRSTNAME") != null ? commandParamMap.get("FIRSTNAME").toString() : "";
						lastName = commandParamMap.get("LASTNAME") != null ? commandParamMap.get("LASTNAME").toString() : "";
						email = commandParamMap.get("EMAIL_ADDRESS") != null ? commandParamMap.get("EMAIL_ADDRESS").toString() : "";
						extUserName = commandParamMap.get("EXTERNAL_USER_ID") != null ? commandParamMap.get("EXTERNAL_USER_ID").toString() : "";

						int sourceUserId = Integer.parseInt(commandParamMap.get("USER_ID"));

						//firstName = paramEle[0];
						//lastName = paramEle[1];
						//email = paramEle[2];
						//extUserName = paramEle[3];
						pwd = "0";
						int activeFlag = 1;
						userId = -1;
						try
						{
							System.out.println("Getting User if iexists : " + email);
							User ue = UserManager.getUser(connection, email);
							
							if (ue == null)
							{
								System.out.println("User Not Found : " + email);
								System.out.println("Creating User : " + email);
								NewUser nu = new NewUser(email, pwd, firstName, lastName, activeFlag);
								userId = UserManager.createUser(connection, nu);
							}
							else
							{
								System.out.println("User Found : " + email);
								userId = ue.getId();
								System.out.println("User Found with Id : " + userId);
							}

							if (userId > 0)
							{
								//sbResp.append( ContentDelimeter + "Creation of User : " + email +  Seperator + "Successful")  ;
								System.out.println("Created new User : " + email);
								AddMigrationObjectMap(connection, email, sourceServer, targetServer, sourceUserId, userId, -1, "User", packageName); 
							}
						}
						catch (Exception e)
						{
							e.printStackTrace();
							//sbResp.append( ContentDelimeter + "Creation of User : " + email +  Seperator + "Failed")  ;
							System.out.println("Failed to create new User : " + email);
						}
						break;

					case "CreateMember":
						//String nh0Name, nh1Name, nh2Name, nh3Name, email;
						nh0Name = "";
						nh1Name = "";
						nh2Name = "";
						nh3Name = "";
						email = "";


						//NH_LEVEL=0|NH0_ID=2|NH1_ID=-1|NH2_ID=-1|NH3_ID=-1|USER_ID=25|MEMBER_ID=82|NH0_NAME=Root|NH1_NAME=|NH2_NAME=|NH3_NAME=|EMAIL_ADDRESS=systemshead@gmail.com|COMMAND=CreateMember|SEQUENCE=6

						nh0Name = commandParamMap.get("NH0_NAME") != null ? commandParamMap.get("NH0_NAME").toString() : "";
						nh1Name = commandParamMap.get("NH1_NAME") != null ? commandParamMap.get("NH1_NAME").toString() : "";
						nh2Name = commandParamMap.get("NH2_NAME") != null ? commandParamMap.get("NH2_NAME").toString() : "";
						nh3Name = commandParamMap.get("NH3_NAME") != null ? commandParamMap.get("NH3_NAME").toString() : "";
						email = commandParamMap.get("EMAIL_ADDRESS") != null ? commandParamMap.get("EMAIL_ADDRESS").toString() : "";

						int sourceMemberId = Integer.parseInt(commandParamMap.get("MEMBER_ID"));

						//nh0Name = paramEle[0];
						//nh1Name = paramEle[1];
						//nh2Name = paramEle[2];
						//nh3Name = paramEle[3];
						//email = paramEle[4];
						
						System.out.println("nh0Name : >" + nh0Name + "<");
						System.out.println("nh1Name : >" + nh1Name + "<");
						System.out.println("nh2Name : >" + nh2Name + "<");
						System.out.println("nh3Name : >" + nh3Name + "<");
						System.out.println("email : " + email);

						System.out.println("before getting userid : " + email);
						user = UserManager.getUser(connection, email);
						userId = user.getId();
						System.out.println("after getting userId : " + userId);

						nhLevel = -1;
						String nhPath = null;
						
						if (!nh0Name.isEmpty() && !nh1Name.isEmpty() && !nh2Name.isEmpty() && !nh3Name.isEmpty() )
						{
							nhLevel = 3;
							nhPath = nh0Name + "->" + nh1Name + "->" + nh2Name + "->" + nh3Name ;
						}
						else if (!nh0Name.isEmpty() && !nh1Name.isEmpty() && !nh2Name.isEmpty() && nh3Name.isEmpty() )
						{
							nhLevel = 2;
							nhPath = nh0Name + "->" + nh1Name + "->" + nh2Name ;
						}
						else if (!nh0Name.isEmpty() && !nh1Name.isEmpty() && nh2Name.isEmpty() && nh3Name.isEmpty() )
						{
							nhLevel = 1;
							nhPath = nh0Name + "->" + nh1Name ;
						}
						else if (!nh0Name.isEmpty() && nh1Name.isEmpty() && nh2Name.isEmpty() && nh3Name.isEmpty() )
						{
							nhLevel = 0;
							nhPath = nh0Name ;
						}

						//NeighborhoodLevelId nhl;
						try {
							System.out.println("Before getting neighborhood levelId");
							System.out.println("nhLevel: " + nhLevel);
							nhl = NeighborhoodManager.getNeighborhoodLevelId(connection, nh0Name.trim(), nh1Name.trim(), nh2Name.trim(), nh3Name.trim(), nhLevel);
							nhId = nhl.getId();
							System.out.println("after getting neighborhood levelId");
							//int memberId = BoardwalkNeighborhoodManager.createMember(bwcon, nhId, userId);
							System.out.println("nhId: " + nhId);
							System.out.println("userId: " + userId);
							System.out.println("tId: " + tid);

							try  { 	tm = new TransactionManager( connection, migrationUserId); 	tid = tm.startTransaction(); } 
							catch (SQLException e1) { e1.printStackTrace();}

							memberId = -1;
							memberId = MemberManager.createMember(connection, tid, userId, nhId);

							AddMigrationObjectMap(connection, email, sourceServer, targetServer, sourceMemberId, memberId, tid, "Member", packageName); 

							try {	System.out.println("commiting  transaction"); tm.commitTransaction();} 
							catch (SQLException e) { e.printStackTrace(); }

							if (memberId > 0)
							{
								//sbResp.append( ContentDelimeter + "Creation of new Member : " + email  + " under nhPath : " + nhPath  +  Seperator + "Successful")  ;
								System.out.println("new Member created under nhPath " + nhPath + " : " + memberId);
							}
						} 
						catch (SystemException | SQLException e) 
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
							System.out.println("Creation of new Member : " + email  + " under nhPath : " + nhPath  +  Seperator + "Failed")  ;
							//sbResp.append( ContentDelimeter + "Creation of new Member : " + email  + " under nhPath : " + nhPath  +  Seperator + "Failed")  ;
						}
						break;
					}		//END OF SWITCH
			}  
			fr.close();    //closes the stream and release the resources  
		}  
		catch(IOException e)  
		{  
			e.printStackTrace();  
		}  
	}


	public void readLinkImportMapFile(String linkImportMapFile)
	{

/*
				objectId = rs.getInt(1);			//rowid or column id
				objectName = rs.getString(2);		//blank for Row, Column Name for Column
				tableId = rs.getInt(3);				//table id
				objectSequence = rs.getInt(4);		//Row Sequence or Column Sequence
				objectType = rs.getString(5);		//BW_ROW or BW_COLUMN
				orderCode = rs.getString(6);		//ORDER CODE

data
208342000002336BW_ROWA
208352000002337BW_ROWA
50Buyer20000021BW_COLUMNB
51Supplier20000022BW_COLUMNB

				sb.append(objectId + Seperator + objectName + Seperator + tableId  + Seperator + objectSequence  + Seperator + objectType  + Seperator + orderCode + "\n");

*/

		try  
		{  
			File file=new File(linkImportMapFile);		//creates a new file instance  
			FileReader fr=new FileReader(file);			//reads the file  
			BufferedReader br=new BufferedReader(fr);	//creates a buffering character input stream  
			StringBuffer sb=new StringBuffer();			//constructs a string buffer with no characters  
			String line;  
			int objectId, cuboidId, sequence;
			String objectName, objectType, order;

			sourceSequenceRowIdMap = new HashMap<Integer, Integer>();
			sourceSequenceColIdMap = new HashMap<Integer, Integer>();
			sourceSequenceColumnNameMap = new HashMap<Integer, String>();

			while((line=br.readLine())!=null)  
			{  
				String[] lineArr = line.split(Seperator);

				objectId = Integer.parseInt(lineArr[0]);
				objectName = lineArr[1];
				cuboidId = Integer.parseInt(lineArr[2]);
				sequence = Integer.parseInt(lineArr[3]);
				objectType = lineArr[4];
				order = lineArr[5];

				switch (objectType)
				{
					case	"BW_ROW"	:
						sourceSequenceRowIdMap.put(sequence, objectId);

						break;

					case	"BW_COLUMN"	:
						sourceSequenceColIdMap.put(sequence, objectId);
						sourceSequenceColumnNameMap.put(sequence, objectName);
						break;
				}
				//sb.append(line);      //appends line to string buffer  
				//sb.append("\n");     //line feed   
			}  
			fr.close();    //closes the stream and release the resources  
			System.out.println("Contents of File: ");  
			//System.out.println(sb.toString());   //returns a string that textually represents the object  
		}  
		catch(IOException e)  
		{  
			e.printStackTrace();  
		}  
	}


	public void readLinkImportBufferfFile(int targetCuboidId, String filePathName, String linkImportMapFile)
	{
		StringBuffer sb = null;
		try
		{
			FileReader fr =	new FileReader(filePathName);
			BufferedReader br = new BufferedReader(fr);
			String line = new String();

			sb = new StringBuffer();

			line = br.readLine();

			while (line != null)
			{
				sb.append(line);
				line = br.readLine();
				if (line != null)
				{
					sb.append("\n");
				}
			}

			ZipInputStream zipIn = new ZipInputStream(new ByteArrayInputStream(Base64.decodeBase64(sb.toString().getBytes())));
			zipIn.getNextEntry();
			// wrap around UnicodeInputStream to take care of Java bug related to BOM mark in UTF-8 encoded files
			//String bw_client = ((HttpServletRequest)request).getHeader("X-client");
			com.boardwalk.util.UnicodeInputStream uis = null;
			//if (bw_client != null && !bw_client.equals("MacExcel"))
			//{
				uis = new com.boardwalk.util.UnicodeInputStream(zipIn);
				sb = null;
			//}
			sb = new StringBuffer();

			reader = new BufferedReader(new InputStreamReader(zipIn, "UTF-8"));

			processLinkImportBuffer(targetCuboidId);

			updateRowIdColumnIdMigrationMap();


//			ByteArrayOutputStream out = new ByteArrayOutputStream();
//			IOUtils.copy(zipIn, out);
//			sb = new StringBuffer();
//			sb.append(out.toString("UTF-8"));
//			System.out.println(" getRequestBuffer " + out.toString("UTF-8"));
//			zipIn.closeEntry();
//			zipIn.close();
			if (uis != null) uis.close();
//			out.close();
//			Writer wout = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("C:\\Request.txt"), "UTF8"));
//			wout.write(sb.toString()); 
//			wout.close(); 
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		System.out.println("sb");
		System.out.println(sb.toString());
		//return sb;

		//reader = new BufferedReader(new InputStreamReader(zipIn, "UTF-8"));
//		reader = new BufferedReader(sb.toString());

//		processHeader(reader.getNextContent());

		


	}


	public void updateRowIdColumnIdMigrationMap()
	{
		System.out.println("Inside updateRowIdColumnIdMigrationMap....");
/*
		for (int ri = 0; ri < numRows - 1; ri++)
		{
			System.out.println("Target RowID : "  + rowIds.get(ri) + " .......... Source RowID : "  + 
			//responseToUpdate.append(rowIds.get(ri) + Seperator);
		}

		for (int i = 0; i < numColumns * 2; i = i + 2)
		{
			System.out.println("Target ColumnID : "  + rowIds.get(ri) + " .......... Source ColumnID : "  + 

			int columnIdx = i / 2;
			//System.out.println("Processing column num = " + columnIdx);
			//processColumnData(fullTableArr[i + 2], fullTableArr[i + 3], columnIdx);
			String cellBuff = getNextContent();
			String fmlaBuff = getNextContent();
			processColumnData(cellBuff, fmlaBuff, columnIdx);
			cellBuff = null;
			fmlaBuff = null;
		}

//		String val = (String)newmap.get(3);


//		for(Map.Entry m : filePathMap.entrySet())
//		{    
//			System.out.println(m.getKey()+" : "+m.getValue());    
//		}  

//	HashMap <Integer, Integer> sourceSequenceRowIdMap ;
//	HashMap <Integer, Integer> sourceSequenceColIdMap ;
//	HashMap <Integer, String> sourceSequenceColumnNameMap ;
**/

		System.out.println("Printing Source RowIdMap............");

		for (Integer sequence : sourceSequenceRowIdMap.keySet()) {
			int key = sequence;
			int value = sourceSequenceRowIdMap.get(sequence);
//			int rowkey = key - 1;
//			int targetRowId = rowIds.get(Integer.parseInt(key)-1);
			System.out.println(key + " = Source : " + value  );
//			System.out.println("Target Row ID : " + rowIds.get(rowkey)); 
		}

		for (int ri = 0; ri < numRows - 1; ri++)
		{
			System.out.println("Target RowID : "  + rowIds.get(ri) + " .......... Source RowID : "  ); 
			//responseToUpdate.append(rowIds.get(ri) + Seperator);
		}


		System.out.println("Printing Source ColumnIdMap............");

		for (Integer sequence : sourceSequenceColIdMap.keySet()) {
			int key = sequence;
			int value = sourceSequenceColIdMap.get(sequence);
//			int targetColunId = columnIds.get(key-1);

			System.out.println(key + " = Source : " + value  );
		}


/*
		for(Integer key: sourceSequenceRowIdMap.keySet())
		{
			for(Integer value: sourceSequenceRowIdMap.get(key)) 
			{
				// use the value here
				System.out.println("Key : " + key + " .......... value = " + value); 
			}
		}

		for(Integer key: sourceSequenceColIdMap.keySet())
		{
			for(Integer value: sourceSequenceColIdMap.get(key)) 
			{
				// use the value here
				System.out.println("Key : " + key + " .......... value = " + value); 
			}
		}

		for(Integer key: sourceSequenceColumnNameMap.keySet())
		{
			for(String value: sourceSequenceColumnNameMap.get(key)) 
			{
				// use the value here
				System.out.println("Key : " + key + " .......... value = " + value); 
			}
		}
*/

	}


	public void processLinkImportBuffer (int targetCuboidId)
	{

		System.out.println("xlMigrationServiceExt: Time to read the buffer = " + getElapsedTime());
		try
		{
			MAX_RETRY_ATTEMPTS = Integer.parseInt(getServletConfig().getInitParameter("MAX_RETRY_ATTEMPTS"));
			RETRY_WAIT_TIME_MIN = Integer.parseInt(getServletConfig().getInitParameter("RETRY_WAIT_TIME_MIN"));
			RETRY_WAIT_TIME_MAX = Integer.parseInt(getServletConfig().getInitParameter("RETRY_WAIT_TIME_MAX"));
			System.out.println("MAX_RETRY_ATTEMPTS=" + MAX_RETRY_ATTEMPTS);
			System.out.println("RETRY_WAIT_TIME_MIN=" + RETRY_WAIT_TIME_MIN);
			System.out.println("RETRY_WAIT_TIME_MAX=" + RETRY_WAIT_TIME_MAX);
		}
		catch (Exception e)
		{
			System.out.println("Deadlock parameters not set. Using defaults...");
		}

		String failureReason = "";

/*		try
		{
			String header = getNextContent();

			System.out.println("header : " + header);

	//		processColumns(reader.getNextContent());
			String columns = getNextContent();
			System.out.println("columns : " + columns);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
*/

		StringBuffer responseToUpdate = null; //new StringBuffer();
		String responseBuffer = null;

		for (int ti = 0; ti < MAX_RETRY_ATTEMPTS; ti++)
		{
			responseToUpdate = new StringBuffer ();
			responseBuffer = null;
			
			try
			{

				//processHeader(fullTableArr[0]);
				String header = getNextContent();
				processHeader(header, targetCuboidId);

				//processColumns(fullTableArr[1]);
				String columns = getNextContent();
				processColumns(columns, targetCuboidId);

				// Add rows
				System.out.println("xlMigrationServiceExt:service() : userId = " + userId);
				System.out.println("xlMigrationServiceExt:service() : tableId = " + tableId);
				System.out.println("xlMigrationServiceExt:service() : tid = " + tid);
				System.out.println("xlMigrationServiceExt:service() : numRows = " + numRows);
				System.out.println("xlMigrationServiceExt:service() : numColumns = " + numColumns);
				if (numRows > 0)
				{
					if (canAdministerColumns && canAddRows)
						rowIds = TableManager.createRowsNewTable(connection, tableId, tid, userId, numRows);
					System.out.println("xlMigrationServiceExt: Time to create rows = " + getElapsedTime());
					
					/* PWC_ZZZ_1_29

					// Add Cells
					query =
						" INSERT INTO BW_CELL (BW_ROW_ID, BW_COLUMN_ID, CELL_TYPE, TX_ID) " +
						" SELECT BW_ROW.ID, BW_COLUMN.ID, 'STRING', ? " +
						" FROM BW_ROW, BW_COLUMN " +
						" WHERE " +
						" BW_ROW.TX_ID = ? " +
						" AND BW_COLUMN.TX_ID = ? ";
					stmt = connection.prepareStatement(query);
					stmt.setInt(1, tid);
					stmt.setInt(2, tid);
					stmt.setInt(3, tid);
					stmt.executeUpdate();
					stmt.close();
					stmt = null;
					System.out.println("xlMigrationServiceExt: Time to insert into bw_cell table = " + getElapsedTime());

					// Create new bw cell status records
					String q2 = "INSERT INTO BW_CELL_STATUS " +
							   "  SELECT BW_CELL.ID, 1, BW_CELL.TX_ID " +
							   "  FROM BW_CELL " +
							   "  WHERE BW_CELL.TX_ID = ?";
					stmt = connection.prepareStatement(q2);
					stmt.setInt(1, tid);
					stmt.executeUpdate();
					stmt.close();
					stmt = null;
					System.out.println("xlMigrationServiceExt: Time to create bw cell status records= " + getElapsedTime());
					*/
					// Insert into BW_RC_STRING_VALUE
					for (int i = 0; i < numColumns * 2; i = i + 2)
					{
						int columnIdx = i / 2;
						//System.out.println("Processing column num = " + columnIdx);
						//processColumnData(fullTableArr[i + 2], fullTableArr[i + 3], columnIdx);
						String cellBuff = getNextContent();
						String fmlaBuff = getNextContent();
						processColumnData(cellBuff, fmlaBuff, columnIdx);
						cellBuff = null;
						fmlaBuff = null;
					}
					System.out.println("xlMigrationServiceExt: Time to insert into rcsv table = " + getElapsedTime());


					System.out.println("xlMigrationServiceExt: xlErrorCells.size() " + xlErrorCells.size());
					if (xlErrorCells.size() > 0)
					{
						throw new BoardwalkException(12011);
					}

					query = "{CALL BW_UPD_CELL_FROM_RCSV_LINK_EXPORT(?,?,?)}";
					CallableStatement cstmt = connection.prepareCall(query);
					cstmt.setInt(1, tid);
					cstmt.setInt(2, tableId);
					cstmt.setInt(3, userId);
					int updCount = cstmt.executeUpdate();
					cstmt.close();
					cstmt = null;
					System.out.println("xlMigrationServiceExt: Time to execute BW_UPD_CELL_FROM_RCSV_LINK_EXPORT = " + getElapsedTime());
				}
				// commit the transaction
				tm.commitTransaction();
				tm = null;
				//tm.rollbackTransaction(); // FOR NOW

				// create the response
				responseToUpdate.append("Success" + Seperator);
				responseToUpdate.append(numColumns + Seperator);
				responseToUpdate.append(numRows + Seperator);
				responseToUpdate.append(tid + ContentDelimeter);

				responseToUpdate.append(tableId + ContentDelimeter + memberId + ContentDelimeter);

				int ri = 0;
				int ci = 0;

				for (ri = 0; ri < numRows - 1; ri++)
				{
					responseToUpdate.append(rowIds.get(ri) + Seperator);
				}

				if (numRows > 0)
					responseToUpdate.append(rowIds.get(ri) + ContentDelimeter);//last rowid
				else
					responseToUpdate.append(ContentDelimeter);//last rowid

				for (ci = 0; ci < numColumns - 1; ci++)
				{
					responseToUpdate.append(columnIds.get(ci) + Seperator);
				}

				responseToUpdate.append(columnIds.get(ci) + ContentDelimeter);//last columnid

				responseToUpdate.append(formulaString + ContentDelimeter);

				ti = MAX_RETRY_ATTEMPTS; // dont try again

				failureReason = "";

			}
			catch (SQLException sqe)
			{

				sqe.printStackTrace();
				try
				{
					if (tm != null)
						tm.rollbackTransaction();
				}
				catch (Exception e1)
				{
					e1.printStackTrace();
				}
				if (sqe.getErrorCode() == 1205)
				{
					if (ti == MAX_RETRY_ATTEMPTS - 1)
					{
						failureReason = (new xlErrorNew(tableId, 0, 0, 13001)).buildTokenString();
						System.out.println("xlMigrationServiceExt: Deadlock maximum attempts exhausted. Sending server busy message to client ");

						System.out.println("xlMigrationServiceExt:failureReason = " + failureReason);
					}
					System.out.println("xlMigrationServiceExt:Deadlock attempt number = " + (ti + 1) + " out of max = " + MAX_RETRY_ATTEMPTS);
					//sqe.printStackTrace();
					try
					{
						int sleepTime = RETRY_WAIT_TIME_MIN + (new SecureRandom()).nextInt(RETRY_WAIT_TIME_MAX - RETRY_WAIT_TIME_MIN);
						System.out.println("Sleeping for " + sleepTime + "ms");
						Thread.sleep(sleepTime);
					}
					catch (InterruptedException e)
					{
						e.printStackTrace();
					}
				}
				else
				{
					failureReason = sqe.getMessage();
					ti = MAX_RETRY_ATTEMPTS; // dont try again
				}

			}
			catch (BoardwalkException bwe)
			{
				ti = MAX_RETRY_ATTEMPTS; // dont try again
				bwe.printStackTrace();
				if (xlErrorCells.size() > 0)
				{
					StringBuffer errorBuffer = new StringBuffer();

					for (int errorIndex = 0; errorIndex < xlErrorCells.size(); errorIndex++)
					{
						xlErrorNew excelError = (xlErrorNew)(xlErrorCells.elementAt(errorIndex));
						errorBuffer.append(excelError.buildTokenString());
					}
					errorBuffer.append(Seperator);
					failureReason = errorBuffer.toString();
					
					try
					{
						if (tm != null)
							tm.rollbackTransaction();
					}
					catch (Exception e1)
					{
						e1.printStackTrace();
					}
				}
			}
			catch (Exception e)
			{
				ti = MAX_RETRY_ATTEMPTS; // dont try again
				e.printStackTrace();
				try
				{
					if (tm != null)
						tm.rollbackTransaction();
				}
				catch (Exception e1)
				{
					e1.printStackTrace();
				}
				failureReason = e.getMessage();
			}
			finally
			{
				// close the connection
				try
				{
					reader.close();
					connection.close();
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}

				// clean up
				numColumns = 0;
				numRows = 0;
				tableId = -1;
				nhId = -1;

				columnIds = null;
				rowIds = null;
				formulaIds = null;
				strValIds = null;
				formulaString = null;

				userId = -1;
				userName = "";
				userPassword = "";
				memberId = -1;
				//formulaArray = null;
				view = null;
				query = "";
				xlErrorCells = null;
				
				canAddRows = false;
				canDeleteRows = false;
				canAdministerColumns = false;

				connection = null;
				stmt = null;
				tm = null;
				tid = -1;
			}
		}

		// The response
		if (failureReason.length() == 0)
		{
			responseBuffer = responseToUpdate.toString();
			//commitResponseBuffer(responseBuffer, response);
			System.out.println("xlMigrationServiceExt: Success : responseBuffer = " + responseBuffer);
			System.out.println("xlMigrationServiceExt: Success : Time to prepare response = " + getElapsedTime());
		}
		else
		{
			failureReason = "FAILURE" + ContentDelimeter + failureReason;
			//commitResponseBuffer(failureReason, response);

			System.out.println("xlMigrationServiceExt: Failure : Time to prepare response = " + getElapsedTime());
			System.out.println("xlMigrationServiceExt: failureReason = " + failureReason);
		}

	}


	// returns data upto next content delimeter
	// returns null if reached end of request buffer
	public String getNextContent() throws IOException
	{
		String retString = null;
		StringBuffer sb = null;
		int ch;

		boolean foundContent = false;
		while ((ch = reader.read()) > -1)
		{
			if (sb == null)
			{
				sb = new StringBuffer();
			}

			if (ch == 2)
			{
				foundContent = true;
				//System.out.println("Found next content = " + sb.toString ());
				break;
			}
			else
			{

				sb.append((char)ch);
			}
		}

		if (sb == null) // no more content
		{
			return null;
		}
		else
		{
			//System.out.println("getNextContent >> " + sb.toString ());
			return sb.toString();
		}
	}

	public void processColumnData(String cellData, String formulaData, int columnIdx) throws SQLException 
	{
		String[] cellArr = cellData.split(Seperator);
		String[] formulaArr = formulaData.split(Seperator);
		int columnId = ((Integer)columnIds.get(columnIdx)).intValue();
		boolean emptyColumn = false;
		//System.out.println("cellArr.length = " + cellArr.length);
		if (cellArr.length == 0) // empty column
		{
			emptyColumn = true;
			//System.out.println("Column is empty");
		}
		boolean emptyFormulae = false;
		if (formulaArr.length == 0) // empty column
		{
			emptyFormulae = true;
			//System.out.println("Formulae is empty");
		}

		// insert into bw_rc_string_value 
		query = 
			" INSERT INTO BW_RC_STRING_VALUE " + 
			" (BW_ROW_ID, BW_COLUMN_ID, STRING_VALUE, FORMULA, TX_ID, CHANGE_FLAG) " +
			" VALUES " +
			" (?, ?, ?, ?, ?, ?) ";

		stmt = connection.prepareStatement(query);
		for (int i = 0; i < numRows; i++)
		{
			int rowId = ((Integer)rowIds.get(i)).intValue ();
			String cellValue = "";
			String formula = null;
			if (emptyColumn == false)
			{
				try
				{
					cellValue = cellArr[i];
				}
				catch (ArrayIndexOutOfBoundsException e)
				{
					cellValue = "";
				}
			}
			if (emptyFormulae == false)
			{
				try
				{
					formula = formulaArr[i];
					if (formula.indexOf("=") < 0)
					{
						formula = null;
					}
				}
				catch (ArrayIndexOutOfBoundsException e)
				{
					formula = null;
				}
			}
			//System.out.println("INSERT INTO BW_RC_STRING_VALUE rowId = " + rowId + " columnId = " + columnId + " cellValue = " + cellValue + " formula = " + formula);

			stmt.setInt(1, rowId);
			stmt.setInt(2, columnId);
			stmt.setString(3, cellValue);
			stmt.setString(4, formula);
			stmt.setInt(5, tid);
			stmt.setInt(6, 12);
			stmt.addBatch();
		}
		int[] rescnt = stmt.executeBatch();
		stmt.close();
		stmt = null;

	}

	public void processColumns(String columnInfo, int targetCuboidId) throws BoardwalkException, SQLException
	{
		// If the user has access to add new Columns then procede forward
		if(canAdministerColumns)
		{
			columnIds = new ArrayList(numColumns);
			String[] columnNames = columnInfo.split(Seperator);
			Vector columns = new Vector();
			query = " INSERT INTO BW_COLUMN " +
					   " (NAME, BW_TBL_ID, COLUMN_TYPE, SEQUENCE_NUMBER, TX_ID) " +
					   " VALUES " +
					   " (?,?,?,?,?)";

			stmt = connection.prepareStatement(query);
			// Add columns
			for (int cni = 0; cni < numColumns; cni++)
			{
				//System.out.println("Adding column : " + columnNames[cni]);
				stmt.setString(1, columnNames[cni]);
				stmt.setInt(2, tableId);
				stmt.setString(3, "STRING");
				stmt.setFloat(4, cni + 1);
				stmt.setInt(5, tid);
				stmt.addBatch();
			}
			int[] rescnt = stmt.executeBatch();
			stmt.clearBatch();
			stmt.close();
			stmt = null;
		}

		//HashMap columnHash = new HashMap();
		ResultSet resultset = null;
		query = "select id from bw_column where tx_id = ? order by sequence_number";
		stmt = connection.prepareStatement(query);
		stmt.setInt(1, tid);
		resultset = stmt.executeQuery();
		while (resultset.next())
		{
			int columnId = resultset.getInt(1);
			//int columnIdx = resultset.getFloat (2);
			columnIds.add (new Integer(columnId));
			//columnHash.put (new Integer(columnIdx), new Integer(columnId));
			//System.out.println("columnid = " + columnId);
		}
		stmt.close();
		stmt = null;
		resultset.close();
		resultset = null;
	}

	public void processHeader(String header, int targetCuboidId) throws BoardwalkException, SQLException, SystemException
	{

		System.out.println("header = " + header);


		String[] headerInfo = header.split(Seperator);

		//Modified by Tekvision on 20180207 for Clear Text Password(Issue Id: 14241) - START
		userId				= Integer.parseInt(headerInfo[0]);
		userName			= headerInfo[1];
		//userPassword        = headerInfo[2];
		memberId			= Integer.parseInt (headerInfo[2]);
		tableId				= Integer.parseInt (headerInfo[3]);
		nhId				= Integer.parseInt (headerInfo[4]);
		numColumns			= Integer.parseInt(headerInfo[5]);
		numRows				= Integer.parseInt (headerInfo[6]);
		view = "LATEST";



		System.out.println("source server UserId : " + userId);
		System.out.println("source server CuboidId : " + tableId);
		System.out.println("source server MemberId : " + memberId);


		int tsUserId = -1;
		int tsMemberId = -1;
		int tsNhId = -1;

		try
		{
			tsUserId = GetMigratedObjectIdOnTargetServerUsingObjectMap(connection, "", sourceServer, userId, "User");
//			targetCuboidId = GetMigratedObjectIdOnTargetServerUsingObjectMap(connection, "", sourceServer, tableId, "Cuboid");
//			tsNhId = GetMigratedObjectIdOnTargetServerUsingObjectMap(connection, "", sourceServer, nhId, "Cuboid");
			tsMemberId = GetMigratedObjectIdOnTargetServerUsingObjectMap(connection, "", sourceServer, memberId, "Member");

			System.out.println("target server UserId : " + tsUserId);
			System.out.println("target server CuboidId : " + targetCuboidId);
			System.out.println("target server MemberId : " + tsMemberId);

		
			Member memberObj = UserManager.authenticateMember(connection, userName, tsMemberId); //Modified by Tekvision on 20180207 for Clear Text Password(Issue Id: 14241)
			if (memberObj == null)
			{
				//System.out.println("Authentication failed for user : " + userName);
				xlErrorCells.add( new xlErrorNew( targetCuboidId, 0, 0, 11004));
				throw new BoardwalkException(11004);
			}
			else
			{
				//System.out.println("Authentication succeeded for user : " + userName);
				tsNhId = memberObj.getNeighborhoodId();
			}

		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}

		//Using TargetServerIds to proceed
		userId = tsUserId;
		memberId = tsMemberId;
		tableId = targetCuboidId;
		nhId = tsNhId;

		xlErrorCells = new Vector();
		//Modified by Tekvision on 20180207 for Clear Text Password(Issue Id: 14241) - END

		// Start a connection
		DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
		connection = databaseloader.getConnection();

		//	Access control checks
		TableInfo tinfo = TableManager.getTableInfo(connection, userId, tableId);
		TableAccessList ftal = TableViewManager.getSuggestedAccess(connection, tinfo, userId, memberId, nhId);
		//if (view == null ||  view.trim().equals(""))
		//{
		//    view = ftal.getSuggestedViewPreferenceBasedOnAccess();
		//    System.out.println("Suggested view pref = " + view);
		//    if(view == null)
		//        view = "None";
		//}
		// Check access control :: TBD
		int raccess = 1;
		int ACLFromDB = ftal.getACL();
		TableAccessRequest wAccess = new TableAccessRequest(tableId, "LATEST", true);
		int wACL = wAccess.getACL();
		int awACL = wACL & ACLFromDB;

		canAddRows				= ftal.canAddRow();
		canDeleteRows			= ftal.canDeleteRow();
		canAdministerColumns	= ftal.canAdministerColumn();

		// authenticate the user
		Member memberObj = UserManager.authenticateMember(connection, userName, memberId); //Modified by Tekvision on 20180207 for Clear Text Password(Issue Id: 14241)
		if (memberObj == null)
		{
			//System.out.println("Authentication failed for user : " + userName);
			xlErrorCells.add( new xlErrorNew( tableId, 0, 0, 11004));
			throw new BoardwalkException(11004);
		}
		else
		{
			//System.out.println("Authentication succeeded for user : " + userName);
			nhId = memberObj.getNeighborhoodId();
			tm = new TransactionManager(connection, userId);
			tid = tm.startTransaction("Link export new table", "Link export new table");
		}

		System.out.println("Time to authenticate user = " + getElapsedTime());

		if(canAdministerColumns == false)
		{
			// User does not have access to add columns
			xlErrorCells.add( new xlErrorNew( tableId,0,0,12010));
			System.out.println("No access to add column");
		}

		if(canAddRows == false)
		{
			xlErrorCells.add( new xlErrorNew( tableId,0,0,12012));
			System.out.println("No access to add rows");
		}
		//System.out.println("view = " + view);
		if(view.equals("None"))
		{
			xlErrorCells.add( new xlErrorNew(tableId, 0, 0, 10005));
		}
	}


//CALL_ADD_DEPLOYED_PACKAGES="{CALL BW_ADD_DEPLOYED_PACKAGES(?,?,?,?)}";
//	@PACKAGE_NAME VARCHAR(512), 
//	@SOURCE_SERVER VARCHAR(512), 
//	@PACKAGE_CREATION_DATE DATETIME,
//	@PACKAGE_ID INTEGER OUTPUT

	public static int AddDeployedPackageInformation(Connection connection, String packageName, String sourceServer, String packageCreationDate) throws SQLException
	{

		java.sql.Date date_ = new java.sql.Date(java.sql.Date.parse(packageCreationDate));

		CallableStatement callablestatement = null;
		int deployedId = -1;
		try
		{
			callablestatement = connection.prepareCall(CALL_ADD_DEPLOYED_PACKAGES);
			callablestatement.setString(1, packageName);
			callablestatement.setString(2, sourceServer);
			callablestatement.setDate(3, date_);
			callablestatement.registerOutParameter(4, java.sql.Types.INTEGER);

			callablestatement.execute();
			deployedId = callablestatement.getInt(4);
		}
		catch (SQLException sql1)
		{
			  throw sql1;
		}
		finally
		{
			  try
			  {
					callablestatement.close();
			  }
			  catch (SQLException sql2)
			  {
					throw sql2;
			  }
		}
		return deployedId;
	}


//	CALL_ADD_MIGRATION_OBJECT_MAP="{CALL BW_ADD_MIGRATION_OBJECT_MAP (?,?,?,?,?,?,?)}";
//	@SOURCE_SERVER VARCHAR(512), 
//	@TARGET_SERVER VARCHAR(512), 
//	@SOURCE_OBJECT_ID INT, 
//	@TARGET_OBJECT_ID INT, 
//	@CREATION_TX_ID INT, 
//	@OBJECT_TYPE VARCHAR(512), 
//	@PACKAGE_NAME	 VARCHAR(512)

	public static void AddMigrationObjectMap(Connection connection, String objectName, String sourceServer, String targetServer, int sourceObjectId, int targetObjectId, int tId, String objectType, String packageName) throws SQLException
	{

		CallableStatement callablestatement = null;

		try
		{
			callablestatement = connection.prepareCall(CALL_ADD_MIGRATION_OBJECT_MAP);
			callablestatement.setString(1, objectName);
			callablestatement.setString(2, sourceServer);
			callablestatement.setString(3, targetServer);
			callablestatement.setInt(4, sourceObjectId);
			callablestatement.setInt(5, targetObjectId);
			callablestatement.setInt(6, tId);
			callablestatement.setString(7, objectType);
			callablestatement.setString(8, packageName);
			callablestatement.execute();
		}
		catch (SQLException sql1)
		{
			System.out.println("Failed to Insert Migration Object Map");
			throw sql1;
		}
		finally
		{
			  try
			  {
					callablestatement.close();
			  }
			  catch (SQLException sql2)
			  {
					throw sql2;
			  }
		}
	}


	public static int GetMigratedObjectIdOnTargetServerUsingObjectMap(Connection connection, String objectName, String sourceServer, int sourceObjectId, String objectType) throws SQLException
	{

		CallableStatement callablestatement = null;
		int targetObjectId = -1;
		try
		{
			callablestatement = connection.prepareCall(CALL_GET_MIGRATED_OBJECT_ID_FROM_MIGRATION_OBJECT_MAP );
			callablestatement.setString(1, objectName);
			callablestatement.setString(2, sourceServer);
			callablestatement.setInt(3, sourceObjectId);
			callablestatement.setString(4, objectType);
			callablestatement.registerOutParameter(5, java.sql.Types.INTEGER);

			callablestatement.execute();
			targetObjectId = callablestatement.getInt(5);
		}
		catch (SQLException sql1)
		{
			  throw sql1;
		}
		finally
		{
			  try
			  {
					callablestatement.close();
			  }
			  catch (SQLException sql2)
			  {
				sql2.printStackTrace();
			  }
		}
		return targetObjectId;
	}


}