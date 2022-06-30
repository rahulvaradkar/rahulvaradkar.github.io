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


import java.text.SimpleDateFormat;
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
import java.time.format.DateTimeFormatter;  
import java.time.LocalDateTime;    

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
import java.nio.*;
import java.nio.file.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;
import java.io.*;
import java.util.stream.*;


import org.apache.commons.codec.binary.Base64;

import java.util.zip.*;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.*;
import com.boardwalk.model.SopLogger;


//To Read Excel Template Properties on Server
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFName;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


//public class xlMigrationServiceExt extends xlService implements SingleThreadModel
public class xlMigrationServiceExtLogic extends xlServiceLogic
{

	int _pos = 0;
	ZipInputStream zipIn = null;
	BufferedReader reader = null;
	com.boardwalk.util.UnicodeInputStream uis = null;


	private boolean isMultipart;
	private String filePath;
	private int maxFileSize = 90000 * 1024;
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
	private static String CALL_ADD_DEPLOYED_PACKAGES="{CALL BW_ADD_DEPLOYED_PACKAGES(?,?,?,?,?)}";
	private static String CALL_GET_MIGRATED_OBJECT_ID_FROM_MIGRATION_OBJECT_MAP = "{CALL BW_GET_MIGRATED_OBJECT_ID_FROM_MIGRATION_OBJECT_MAP(?,?,?,?,?)}";
	private static String CALL_ADD_CUBOID_ROWID_COLUMN_ID_MAP_FOR_MIGRATION = "{CALL BW_ADD_CUBOID_ROWID_COLUMN_ID_MAP_FOR_MIGRATION(?,?,?,?,?,?,?,?,?,?,?)}";
	private static String CALL_BW_INSERT_PACKAGE_DEPLOYMENT_LOG = "{CALL BW_INSERT_PACKAGE_DEPLOYMENT_LOG(?,?,?,?,?,?,?)}";

	
	//SPs added by Rahul for inserting Integration Rule information into SQL STAGING BEFORE CALLING SUPERMERGE RULE ON TARGET 
	private static String CALL_BW_INSERT_SQL_BRECTDEFINITION = "{CALL BW_INSERT_SQL_BRECTDEFINITION(?,?,?,?,?,?,?,?,?,?)}";
	private static String CALL_BW_INSERT_SQL_KEYSTORE = "{CALL BW_INSERT_SQL_KEYSTORE(?,?,?,?,?,?,?,?)}";
	private static String CALL_BW_INSERT_SQL_C2C_SUPERMERGE_RULES = "{CALL BW_INSERT_SQL_C2C_SUPERMERGE_RULES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
	private static String CALL_BW_INSERT_SQL_C2S_SUPERMERGE_RULES = "{CALL BW_INSERT_SQL_C2S_SUPERMERGE_RULES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
	private static String CALL_BW_INSERT_SQL_MULTI_C2C_SUPERMERGE_RULES = "{CALL BW_INSERT_SQL_MULTI_C2C_SUPERMERGE_RULES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
	private static String CALL_BW_INSERT_SQL_INTEGRATION_RULES = "{CALL BW_INSERT_SQL_INTEGRATION_RULES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
	private static String CALL_BW_INSERT_SQL_S2C_SUPERMERGE_RULES = "{CALL BW_INSERT_SQL_S2C_SUPERMERGE_RULES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";

	
	private static String CALL_BW_SUPERMERGE_SQL_TO_CUBOID_FOR_MIGRATION = "{CALL BW_SUPERMERGE_SQL_TO_CUBOID_FOR_MIGRATION(?,?,?)}";
	private static String CALL_BW_EXTERNAL_QUERY="{CALL BW_EXTERNAL_QUERY(?)}";

	//Migration of Neighborhood Relation 
	private static String CALL_BW_CR_RELATION_USING_NH_PATH_FOR_MIGRATION = "{CALL BW_CR_RELATION_USING_NH_PATH_FOR_MIGRATION(?,?,?,?)}";
	
	//Dropping SQL Objects Before Migration If exists 
	private static String CALL_BW_DROP_OBJECT_BEFORE_MIGRATION = "{CALL BW_DROP_OBJECT_BEFORE_MIGRATION(?,?)}";

	//Migrationg External Queries from Source to Target
	private static String CALL_BW_INSERT_EXT_QUERIES_FOR_MIGRATION = "{CALL BW_INSERT_EXT_QUERIES_FOR_MIGRATION(?,?,?,?)}";

	//Migrating Default Templates from Source to Target. added on 8-June-2022
	private static String CALL_BW_DEPLOYE_DEFAULT_TEMPLATE_ROUTINES =  "{CALL BW_DEPLOYE_DEFAULT_TEMPLATE_ROUTINES(?,?,?,?,?,?,?)}";

	//Migrating User Templates from Source to Target. added on 8-June-2022
	private static String CALL_BW_DEPLOYE_USER_TEMPLATE_ROUTINES =  "{CALL BW_DEPLOYE_USER_TEMPLATE_ROUTINES(?,?,?,?,?,?,?,?,?,?,?,?,?)}";

	//Post Migration Routines To Fix Object Names and Dependent Entreis in Cuboid  24-June-2022
	private static String CALL_BW_POST_MIGRATION_SCRIPT =  "{CALL BW_POST_MIGRATION_SCRIPT()}";


	private static String MESSAGE_TYPE_SUCCESS = "Success";
	private static String MESSAGE_TYPE_FAILURE = "Failure";
	private static String MESSAGE_TYPE_EXCEPTION = "Exception";
	private static String MESSAGE_TYPE_WARNING = "Warning";
	private static String MESSAGE_TYPE_OBJECT_MAP_UPDATE = "UpdateObjectMap";
	private static String MESSAGE_TYPE_STATUS = "Status";

	static StringBuffer logLine = null;

	HttpServletRequest req;
	HttpServletResponse res;


	static StringBuffer sb = null;
	Connection connection = null;
	BoardwalkConnection bwcon = null;

	String migrationUserName;
	String migrationUserPassword;
	int migrationUserId;
	int migrationUserMembershipId;

	//Variables read from PackageInfo data
	String packageName, packageCreationDate, configurationName, sourceTxId, sourceServer, targetServer; 
	int sourceObjectId;
	int targetObjectId;
	
	String cuboidName;
	int sourceCuboidId ;

	//For migration of Templates
	String templateName, downloadUrl, imageUrl, templateFile, templateFilePath, templateSize, nhHierarchy ;
	int blobId, slashIndex ;
	String url, searchSlash, ext, templateNameNoExt ;
	String contentType, fileSize, fileName, templateType; 

	HashMap <String, String> filePathMap ;

	HashMap <Integer, Integer> sourceSequenceRowIdMap ;
	HashMap <Integer, Integer> sourceSequenceColIdMap ;
	HashMap <Integer, String> sourceSequenceColumnNameMap ;

	HashMap <String, String> packageInfoMap;

	//for linkimport functionality ........start ------------------------------------------------------------------

	int numColumnsToMigrate	= 0;
	int numRowsToMigrate	= 0;

	int numColumns	= 0;
	int numRows		= 0;
	int tableId		= -1;
	int nhId		= -1;

	ArrayList   columnIds = null;			// generated in processColumns
	ArrayList	rowIds = null;				// generated in processLinkImportBuffer
	ArrayList	formulaIds = null;
	ArrayList	strValIds = null;
	String		formulaString = null;


	ArrayList<Integer>	sourceColumnIds = null;			// added by Rahul for Migration Cuboid data
	ArrayList<String>	sourceColumnNames = null;			// added by Rahul for Migration Cuboid data
	ArrayList<Integer>	sourceRowIds = null;				// added by Rahul for Migration Cuboid data

	ArrayList<Integer>	migratedColumnIds = null;			// added by Rahul for Migration Cuboid data
	ArrayList<Integer>	migratedRowIds = null;				// added by Rahul for Migration Cuboid data

	int userId = -1;
	String userName	= null;
	String userPassworssd =null;
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


	int targetCuboidCreatorUserId, targetCuboidCreatorMemberId, targetCuboidCreatorNhId; 

	//for linkimport functionality ......... end ------------------------------------------------------------------

	DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");  
	LocalDateTime now;

    public xlMigrationServiceExtLogic (xlMigrationServiceExt srv) 
	{
        super(srv);
    }


    public void service (HttpServletRequest request, HttpServletResponse response) 	throws ServletException, IOException
    {
		String authBase64String = request.getHeader("Authorization");
		byte[] authSetting = Base64.decodeBase64(authBase64String);
		String auth = new String(authSetting);		

		String loginName = null;
		String loginPwd = null;
		String nhPath = null;
	
		req = request;
		String responseBuffer = "";

		try 
		{
			String[] userLogin = auth.split(":");
			loginName = userLogin[0];
			loginPwd = userLogin[1];
			nhPath = userLogin[2];

			System.out.println("loginName : " + loginName);
			//System.out.println("loginPwd : " + loginPwd);
			System.out.println("nhPath : " + nhPath);
		}
		catch (Exception e)
		{
		    System.out.println("Invalid Authorization Format");
			return;
		}

		migrationUserName = loginName;
		migrationUserPassword = loginPwd;

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

//		System.out.println("migrationUserName : " + migrationUserName);
		User suser = UserManager.getUser(connection, migrationUserName);
		migrationUserId = suser.getId();
//		System.out.println("migrationUserId : " + migrationUserId);

		//Creating cuboid using BOARDWALK_APPLICATION 's Neighborhood-0 MEMBERSHIP
		migrationUserMembershipId = getNeighborhood0MembershipId(migrationUserName, nhPath);
		System.out.println("migrationUserMembershipId : " + migrationUserMembershipId);

//		response.setContentType("text/html");
//		java.io.PrintWriter out = response.getWriter( );

		isMultipart = FileUpload.isMultipartContent(request);

		if( !isMultipart ) {
			responseBuffer = "FAILURE" + Seperator +  "Missing Package File In Request." + Seperator ;
			commitResponseBuffer(responseBuffer, response);
		}

		ServletConfig config = getServletConfig();
		ServletContext application = config.getServletContext();
		System.out.println("application.getRealPath(/) ............ " + application.getRealPath("/"));
		String appPath = application.getRealPath("/");

		String strUploadFolder	= config.getInitParameter("file-upload");
		strUploadFolder	= appPath + strUploadFolder;
		System.out.println("strUploadFolder ............ " + strUploadFolder);

		filePath =  strUploadFolder + "\\";

		//filePath = "D://tomcat8.5//webapps//BW_VELOCITY_TARGET//uploadfiles//";
		HttpSession hs = request.getSession(true);
		DiskFileItemFactory factory = new DiskFileItemFactory();
		// maximum size that will be stored in memory
		factory.setSizeThreshold(maxMemSize);
		// Location to save data that is larger than maxMemSize.
//		factory.setRepository(new File("D://temp//"));
		factory.setRepository(new File(filePath));

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

		initLogline();

		now = LocalDateTime.now();  
		System.out.println(dtf.format(now));  

//			now = LocalDateTime.now();  
		writeLogline("", "", MESSAGE_TYPE_SUCCESS,  uploadedZipFileName + " Uploaded" );


//		System.out.println("Displaying Contents of Zip file : " + uploadedZipFileName );
		try 
		{
			FileInputStream fis = new FileInputStream(uploadedZipFileName);
			BufferedInputStream bis = new BufferedInputStream(fis);
			ZipInputStream zis = new ZipInputStream(bis);
			ZipEntry ze;
			while ((ze = zis.getNextEntry()) != null) 
			{
//				System.out.format("File: %s Size: %d Last Modified %s %n", ze.getName(), ze.getSize(), LocalDate.ofEpochDay(ze.getTime() / MILLS_IN_DAY));
			}
		}
		catch(Exception ex) 
		{
			System.out.println(ex);
		}

//		System.out.println("Unzipping the Contents of Zip file started : " + uploadedZipFileName );

		writeLogline("", "", MESSAGE_TYPE_STATUS,  "Unzipping the Contents of Zip file : " + uploadedZipFileName + " is Started...");

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
				//System.out.println("Unzipping to "+newFile.getAbsolutePath());

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

		//System.out.println("Unzipping  the Contents of Zip file : " + uploadedZipFileName + " is Complete" );
		writeLogline("", "", MESSAGE_TYPE_STATUS,  "Unzipping the Contents of Zip file : " + uploadedZipFileName + " is Complete.");

		//System.out.println("Iterating Hashmap filePathMap ...");  
		for(Map.Entry m : filePathMap.entrySet())
		{    
		//	System.out.println(m.getKey()+" : "+m.getValue());    
		}  

		//Read packge.info
		String PackageInfoFile = filePathMap.get("package.info");
		//System.out.println("PackageInfoFile : " + PackageInfoFile);

		writeLogline("", "", MESSAGE_TYPE_SUCCESS, "Reading " + PackageInfoFile + " file started ");

		//Read PackageInfoFile
		try  
		{  
			File file=new File(PackageInfoFile);		//creates a new file instance  
			FileReader fr=new FileReader(file);				//reads the file  
			BufferedReader br=new BufferedReader(fr);		//creates a buffering character input stream  
			packageInfoMap = new HashMap<String, String>();
			String line;  
			while((line=br.readLine())!=null)  
			{
				readPackageInfoMap(line);
			}
			fr.close();    //closes the stream and release the resources  

		//	System.out.println("PackageName : " + packageInfoMap.get("PackageName"));
		//	System.out.println("PackageCreationDate : " + packageInfoMap.get("PackageCreationDate"));
		//	System.out.println("ConfigurationName : " + packageInfoMap.get("ConfigurationName"));
		//	System.out.println("SourceServer : " + packageInfoMap.get("SourceServer"));
		//	System.out.println("SourceTxId : " + packageInfoMap.get("SourceTxId"));
		//	System.out.println("TargetServer : " + packageInfoMap.get("TargetServer"));

			packageName = packageInfoMap.get("PackageName");
			sourceServer = packageInfoMap.get("SourceServer");
			targetServer = packageInfoMap.get("TargetServer");
			packageCreationDate = packageInfoMap.get("PackageCreationDate");
			configurationName = packageInfoMap.get("ConfigurationName");
			sourceTxId = packageInfoMap.get("SourceTxId");
		}  
		catch(IOException e)  
		{  
			e.printStackTrace();  
		}  
 
		writeLogline("", "", MESSAGE_TYPE_STATUS, "Reading " + PackageInfoFile + " file complete ");

		//Read Migration.Commands
		String MigrationCommandsFile = filePathMap.get("Migration.Commands");
		System.out.println("MigrationCommandsFile : " + MigrationCommandsFile);
		System.out.println("mypack.pkg : " + filePathMap.get("mypack.pkg"));


		int deployedPackagetId = -1;
		
		//Read MigrationCommandsFile line by line
		try  
		{  
			writeLogline("", "", MESSAGE_TYPE_SUCCESS, "Reading " + MigrationCommandsFile + " file started ");

			File file=new File(MigrationCommandsFile);		//creates a new file instance  
			FileReader fr=new FileReader(file);				//reads the file  
			BufferedReader br=new BufferedReader(fr);		//creates a buffering character input stream  
			String line;  
			while((line=br.readLine())!=null)  
			{  
				//System.out.println("MigrationCommandsFile: line : " + line);       

				String PackageFileKey = line.trim();
				String PackageFile = filePathMap.get(PackageFileKey);

				//System.out.println("PackageFile : " + PackageFile);       


				writeLogline("", "", MESSAGE_TYPE_SUCCESS, "Reading and Executing Commands in " + PackageFile + " Started.");
					ReadPackageFileAndExecuteCommands (connection , PackageFile);
				writeLogline("", "", MESSAGE_TYPE_SUCCESS, "Reading and Executing Commands in " + PackageFile + " Complete.");
			}  
			fr.close();    //closes the stream and release the resources  

			writeLogline("", "", MESSAGE_TYPE_STATUS, "Running POST Migratoin Routines Started. ");
				RunPostMigrationRoutines(connection );
			writeLogline("", "", MESSAGE_TYPE_STATUS, "Running POST Migratoin Routines Complete. ");


			writeLogline("", "", MESSAGE_TYPE_SUCCESS, "Writing Packge Deployemnt Information Started. ");
			
			deployedPackagetId = AddDeployedPackageInformation (connection, packageName, sourceServer, packageCreationDate);
			writeLogline("", "", MESSAGE_TYPE_SUCCESS, "Writing Packge Deployemnt Information Complete. "  );
			writeLogline("", "", MESSAGE_TYPE_SUCCESS, "Package Deployment Complete . DeployemntPackageId : " + deployedPackagetId );

			writeLogline("", "", MESSAGE_TYPE_SUCCESS, "Reading " + MigrationCommandsFile + " file complete " );
		}  
		catch(SQLException | IOException e)  
		{  
			e.printStackTrace();  
		}  

		
		try
		{
			writeLogline("", "", MESSAGE_TYPE_STATUS, "Deleting Package folder and files Started...." );
			//Delete Package Files if it exists (always exists if package is unzipped correctly
			File outputFolder = FileUtils.getFile(destDir);
			FileUtils.deleteDirectory(outputFolder);
			writeLogline("", "", MESSAGE_TYPE_SUCCESS, "Deleted Successfully folder : " + destDir );

			File zipfile =  new File(uploadedZipFileName);
			FileUtils.forceDelete(zipfile);
			writeLogline("", "", MESSAGE_TYPE_SUCCESS, "Deleted Successfully package file : " + uploadedZipFileName );

			writeLogline("", "", MESSAGE_TYPE_STATUS, "Deleting Package folder and files Complete." );
		}
		catch(IOException e)  
		{  
			writeLogline("", "", MESSAGE_TYPE_FAILURE, "Failed to Delete Package folder and files." );
			e.printStackTrace();  
		}  


		writeLogline("", "", MESSAGE_TYPE_SUCCESS, "Writing Package Deployment Log Started." );
			//System.out.println("Log  : " + getLogline());
			writePackageDeploymentLog(deployedPackagetId, packageName, getLogline()) ;  //start here
		writeLogline("", "", MESSAGE_TYPE_SUCCESS, "Writing Package Deployment Log Complete.");

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
		migrationUserName = "";
		migrationUserPassword = "";
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

		System.out.println("SUCCESS" + Seperator +  "Package Deployed Successfully on Target Server." );
		responseBuffer = "SUCCESS" + Seperator +  "Package Deployed Successfully on Target Server." + ContentDelimeter + getLogline();
		commitResponseBuffer(responseBuffer, response);
	}

	public void initLogline()
	{
		logLine = new StringBuffer();
		logLine.append ("DateTime"  + Seperator + "Command"  + Seperator  +  "Parameters"  + Seperator + "MessageType" + Seperator  +  "Log Line" + ContentDelimeter );
	}

	public void writeLogline(String command , String param, String messageType, String logMessage)
	{
		now = LocalDateTime.now();  
		logLine.append(dtf.format(now) + Seperator + command  + Seperator  +  param  + Seperator + messageType + Seperator  +  logMessage + ContentDelimeter );
	}

	public String getLogline()
	{
		logLine.deleteCharAt(logLine.length()-1);
		return logLine.toString();
	}

	public void readPackageInfoMap(String packageInfoLine)
	{
		//packageInfoMap = new HashMap<String, String>();
		String[] packageInfo = packageInfoLine.split("\\" + PipeDelimeter);
		//System.out.println(packageInfo.length);
		int iCount;
		for (iCount=0; iCount < packageInfo.length ; iCount++ )
		{
			//System.out.println("packageInfo[" + iCount + "] : " + packageInfo[iCount]);
			String[] paramValue = packageInfo[iCount].split("=");

			if (paramValue.length == 2)
			{
			//	System.out.println("paramValue[0] : " + paramValue[0]);
			//	System.out.println("paramValue[1] : " + paramValue[1]);
				packageInfoMap.put(paramValue[0], paramValue[1]);
			}
			//else
			//	System.out.println("No Value defined in PackageInfoLine");
		}
	}

	//Clean
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
		//System.out.println("bwcon : " + bwcon);
		//System.out.println("bwcon.getMemberId() : " + bwcon.getMemberId());

		return bwcon;
	}


	//Clean
	public int getNeighborhood0MembershipId(String userName, String nh0Name)
	{
		String nh1Name, nh2Name, nh3Name,  collabName;
		nh1Name = "";
		nh2Name = "";
		nh3Name = "";

		//System.out.println("nh0Name : >" + nh0Name + "<");
		//System.out.println("nh1Name : >" + nh1Name + "<");
		//System.out.println("nh2Name : >" + nh2Name + "<");
		//System.out.println("nh3Name : >" + nh3Name + "<");
		//System.out.println("userName : " + userName);

		int nhLevel = 0;
					
		NeighborhoodLevelId nhl;
		int nhId = -1;
		try {
		//	System.out.println("Before getting neighborhood levelId");
		//	System.out.println("nhLevel: " + nhLevel);
			nhl = NeighborhoodManager.getNeighborhoodLevelId(connection, nh0Name.trim(), nh1Name.trim(), nh2Name.trim(), nh3Name.trim(), nhLevel);
			nhId = nhl.getId();
		//	System.out.println("nhId based on neighborhood: " + nhId);
		} catch (SystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//System.out.println("before getting userid migrationUserName: " + userName);
		User user = UserManager.getUser(connection, userName);
		int userId = user.getId();
		//System.out.println("after getting userId : " + userId);

		Hashtable memberships = null;
		Enumeration memberIds = null ;
		try
		{
			memberships  = UserManager.getMembershipsForUser(connection, userId );
			memberIds = memberships.keys();
		//	System.out.println("memberships.size : " + memberships.size());
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
		//	System.out.println("Memberships not found");
		}
		else
		{
			if (  memberships.size() > 0 )
			{
				//System.out.println("Checking membership...");
				boolean membershipFound = false;
				for (int ii=0; ii < memberships.size(); ii++)
				{
					memberId =((Integer) memberIds.nextElement()).intValue();
					membernhId =((Member) memberships.get(        new Integer(memberId)   )).getNeighborhoodId();
					nhName = ((Member) memberships.get(        new Integer(memberId)   )).getNeighborhoodName();
				//	System.out.println("nhId based on membership : " + membernhId +   " nhname: " + nhName);
				//	System.out.println("nhId :" + nhId);
					if (nhId == membernhId)
					{
				//		System.out.println("Membership found.");
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


	// ------------------------------------------------------------------------------------------------
	//           START HERE -- After all Package Instuction are Executed. Calling Stored Procedure BW_POST_MIGRATION_SCRIPT
	// ------------------------------------------------------------------------------------------------
	public void RunPostMigrationRoutines(Connection connection) throws SQLException
	{
		PreparedStatement	prepstatement	= null;
		try
		{
			prepstatement = connection.prepareStatement(CALL_BW_POST_MIGRATION_SCRIPT);
			prepstatement.execute();
		}
		catch (SQLException sqlexe)
		{
			  throw sqlexe;
		}
		finally
		{
			  try
			  {
					prepstatement.close();
			  }
			  catch (SQLException sqlexe1)
			  {
					throw sqlexe1;
			  }
		}
	}

	

	// ------------------------------------------------------------------------------------------------
	//           START HERE -- packaging reading and executing command starts here
	// ------------------------------------------------------------------------------------------------
	public void ReadPackageFileAndExecuteCommands(Connection connection, String PackageFile)
	{


		System.out.println("Reading PackageFile : " + PackageFile);

		HashMap <String, String> commandParamMap = new HashMap<String, String>();

		boolean blnNhSecure = false;
		int parentNhId;

		String nh0Name, nh1Name, nh2Name, nh3Name, userName, collabName, wbName, migrateWhat;
		String firstName, lastName, email, extUserName, pwd;
		String dataFile, mapFile;
		String collabCreatorName;
		
		int sourceNh0Id , sourceNh1Id , sourceNh2Id ,  sourceNh3Id ;

		String cuboidCreatorUserName, cuboidCreatorNhName ;
		int cuboidCreatorUserId, cuboidCreatorNhId, cuboidCreatorNhLevel, cuboidCreatorMemberId, sourceImportTxId ;

		String cuboidPath, cuboidQuery, command, sequence;
		String cvsFileName, CVSDataFile, extQueryParamValues ;
		String lsQueryID, lsParameters;
		String lsReportTitle ;

		String collabNhName ;
		int sourceCollabId, sourceWbId	;
		int nhId, nhMembershipId;
		boolean cuboidExist;

		String objectName, objectType, sqlDDLfilename, createDDLFilePathname ;		//variables for Migrating SQL Objects, ie. SP, SQL TABLES

		//Used for Blob Information
		long bytes, kilobytes, megabytes;
		int  dotIndex;
		File dtfile;
		String ext, searchSlash, contentType;


		int nhLevel = -1;
		int nh0MemberId = -1 ;

		NeighborhoodLevelId nhl_id_0, nhl_id_1, nhl_id_2;
		User user;

		int userId;
		NeighborhoodLevelId nhl;

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
				//System.out.println("PackageCommand:  " + commandLine);       
				
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
					//System.out.println("commandElements[" + iCount + "] : " + commandElements[iCount]);
					String[] paramValue = commandElements[iCount].split("=");

					if (paramValue.length == 2)
					{
					//	System.out.println("paramValue[0] : " + paramValue[0]);
					//	System.out.println("paramValue[1] : " + paramValue[1]);
						commandParamMap.put(paramValue[0], paramValue[1]);
					}
					//else
						//System.out.println("No Value defined");
				}

				int tid = -1 ;


				//System.out.println("commandParamMap.get(COMMAND) : " + commandParamMap.get("COMMAND"));

				String thisCommand = commandParamMap.get("COMMAND");

				switch(commandParamMap.get("COMMAND"))
				{
					case "CreateNH_0":
						//NH_LEVEL=0|NH0_ID=2|NH0_NAME=Root|COMMAND=CreateNH_0|SEQUENCE=1
						//String nh0Name;

						nh0Name = "";
						nh1Name = "";
						nh2Name = "";
						nh3Name = "";

						nh0Name = commandParamMap.get("NH0_NAME") != null ? commandParamMap.get("NH0_NAME").toString() : "";
						nh1Name = commandParamMap.get("NH1_NAME") != null ? commandParamMap.get("NH1_NAME").toString() : "";
						nh2Name = commandParamMap.get("NH2_NAME") != null ? commandParamMap.get("NH2_NAME").toString() : "";
						nh3Name = commandParamMap.get("NH3_NAME") != null ? commandParamMap.get("NH3_NAME").toString() : "";

//						System.out.println("nh0Name : >" + nh0Name + "<");
//						System.out.println("nh1Name : >" + nh1Name + "<");
//						System.out.println("nh2Name : >" + nh2Name + "<");
//						System.out.println("nh3Name : >" + nh3Name + "<");

						nhLevel = -1;
						nhLevel = commandParamMap.get("NH_LEVEL") != null ? Integer.parseInt(commandParamMap.get("NH_LEVEL").toString()) : -1;
						sourceNh0Id = commandParamMap.get("NH0_ID") != null ? Integer.parseInt(commandParamMap.get("NH0_ID").toString()) : -1;
						sourceNh1Id = commandParamMap.get("NH1_ID") != null ? Integer.parseInt(commandParamMap.get("NH1_ID").toString()) : -1;
						sourceNh2Id = commandParamMap.get("NH2_ID") != null ? Integer.parseInt(commandParamMap.get("NH2_ID").toString()) : -1;
						sourceNh3Id = commandParamMap.get("NH3_ID") != null ? Integer.parseInt(commandParamMap.get("NH3_ID").toString()) : -1;


//						System.out.println("sourceNh0Id : >" + sourceNh0Id + "<");
//						System.out.println("sourceNh1Id : >" + sourceNh1Id + "<");
//						System.out.println("sourceNh2Id : >" + sourceNh2Id + "<");
//						System.out.println("sourceNh3Id : >" + sourceNh3Id + "<");


						System.out.println("Creating NH_0 : " + nh0Name + " started.");

						CheckIfNeighborhoodExistsIfNotCreateIt(connection, thisCommand, commandLine, nhLevel, nh0Name, nh1Name, nh2Name, nh3Name,  sourceNh0Id, sourceNh1Id, sourceNh2Id, sourceNh3Id );

//						CheckIf_Nh_0_Exists_IfNot_CreateIt(connection, thisCommand, commandLine, sourceNh0Id, nh0Name);

/*						nhl_id_0 = null;
						try 
						{

							writeLogline(thisCommand, commandLine, MESSAGE_TYPE_SUCCESS, "Creating Neighbourhood at Level 0 : [" + nh0Name + "] Started.");

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

								//Add admin/deployer user as member of newly created neighborhood
								int newMemberId = -1;
								newMemberId = MemberManager.createMember(connection, tid, migrationUserId, nh_0_id);
								AddMigrationObjectMap(connection, migrationUserName, sourceServer, targetServer, -1, newMemberId, tid, "Member", packageName); 
								writeLogline(thisCommand, commandLine, MESSAGE_TYPE_SUCCESS, "Success: Added MigrationUser to Neighbourhood [" + nh0Name +  "] at Level 0 : [" + nhl_0.getNhId() + "]");

								writeLogline(thisCommand, commandLine, MESSAGE_TYPE_SUCCESS, "Success: Created Neighbourhood [" + nh0Name +  "] at Level 0 : [" + nhl_0.getNhId() + "]");

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
								writeLogline(thisCommand, commandLine, MESSAGE_TYPE_WARNING, "Warning : Neighbourhood [" + nh0Name + "] at Level 0 Already Exists : ");
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
							writeLogline(thisCommand, commandLine, MESSAGE_TYPE_FAILURE, "Failure : Creation of Neighborhood [" + nh0Name +  "] at Level 0 Failed. Reason : " + e.getMessage());
							//sbResp.append( ContentDelimeter + "Creation of NH_0 : " + nh0Name +  Seperator + "Failed")  ;
						}
*/
						break;

					case "CreateNH_1":
						//String nh0Name, nh1Name;
						nh0Name = "";
						nh1Name = "";
						nh2Name = "";
						nh3Name = "";

						nh0Name = commandParamMap.get("NH0_NAME") != null ? commandParamMap.get("NH0_NAME").toString() : "";
						nh1Name = commandParamMap.get("NH1_NAME") != null ? commandParamMap.get("NH1_NAME").toString() : "";
						nh2Name = commandParamMap.get("NH2_NAME") != null ? commandParamMap.get("NH2_NAME").toString() : "";
						nh3Name = commandParamMap.get("NH3_NAME") != null ? commandParamMap.get("NH3_NAME").toString() : "";

//						System.out.println("nh0Name : >" + nh0Name + "<");
//						System.out.println("nh1Name : >" + nh1Name + "<");
//						System.out.println("nh2Name : >" + nh2Name + "<");
//						System.out.println("nh3Name : >" + nh3Name + "<");

						nhLevel = -1;
						nhLevel = commandParamMap.get("NH_LEVEL") != null ? Integer.parseInt(commandParamMap.get("NH_LEVEL").toString()) : -1;
						sourceNh0Id = commandParamMap.get("NH0_ID") != null ? Integer.parseInt(commandParamMap.get("NH0_ID").toString()) : -1;
						sourceNh1Id = commandParamMap.get("NH1_ID") != null ? Integer.parseInt(commandParamMap.get("NH1_ID").toString()) : -1;
						sourceNh2Id = commandParamMap.get("NH2_ID") != null ? Integer.parseInt(commandParamMap.get("NH2_ID").toString()) : -1;
						sourceNh3Id = commandParamMap.get("NH3_ID") != null ? Integer.parseInt(commandParamMap.get("NH3_ID").toString()) : -1;


//						System.out.println("sourceNh0Id : >" + sourceNh0Id + "<");
//						System.out.println("sourceNh1Id : >" + sourceNh1Id + "<");
//						System.out.println("sourceNh2Id : >" + sourceNh2Id + "<");
//						System.out.println("sourceNh3Id : >" + sourceNh3Id + "<");


						CheckIfNeighborhoodExistsIfNotCreateIt(connection, thisCommand, commandLine, nhLevel, nh0Name, nh1Name, nh2Name, nh3Name,  sourceNh0Id, sourceNh1Id, sourceNh2Id, sourceNh3Id );

/*						CheckIf_Nh_0_Exists_IfNot_CreateIt(connection, thisCommand, commandLine, sourceNh0Id, nh0Name);

						CheckIf_Nh_1_Exists_IfNot_CreateIt(connection, thisCommand, commandLine, sourceNh1Id, nh0Name, nh1Name);

						System.out.println("Creating NH_1 : " + nh1Name + " started.");

						try
						{
							nhl = null;
							try
							{

								writeLogline(thisCommand, commandLine, MESSAGE_TYPE_SUCCESS, "Creating Neighbourhood at Level 1 : [" + nh1Name + "] Started.");

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

									System.out.println("Creation of NH_1 ..... nh1Name : " + nh1Name +  " Successful");
									System.out.println("Creation of NH_1 ..... nhl_1.getId() : " + nhl_1.getNhId() );
									int nh_1_id = nhl_1.getNhId();
									System.out.println("Creation of NH_1 ..... nh_1_id : " + nh_1_id );

									//Add admin/deployer user as member of newly created neighborhood
									int newMemberId = -1;
									newMemberId = MemberManager.createMember(connection, tid, migrationUserId, nh_1_id);
									AddMigrationObjectMap(connection, migrationUserName, sourceServer, targetServer, -1, newMemberId, tid, "Member", packageName); 
									
									writeLogline(thisCommand, commandLine, MESSAGE_TYPE_SUCCESS, "Success: Added MigrationUser to Neighbourhood [" + nh1Name +  "] at Level 1 : [" + nhl_1.getNhId() + "]");

									writeLogline(thisCommand, commandLine, MESSAGE_TYPE_SUCCESS, "Success: Created Neighbourhood [" + nh1Name +  "] at Level 1 : [" + nhl_1.getNhId() + "]");
									
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
								writeLogline(thisCommand, commandLine, MESSAGE_TYPE_WARNING, "Warning : Neighbourhood [" + nh1Name + "] at Level 1 Already Exists : ");
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
							writeLogline(thisCommand, commandLine, MESSAGE_TYPE_FAILURE, "Failure : Creation of Neighborhood [" + nh1Name +  "] at Level 1 Failed. Reason : " + e.getMessage());
							//sbResp.append( ContentDelimeter + "Creation of NH_1 : " + nh1Name +  Seperator + "Failed")  ;
						}
*/
						break;

					case "CreateNH_2":
						//String nh0Name, nh1Name, nh2Name;
						nh0Name = "";
						nh1Name = "";
						nh2Name = "";
						nh3Name = "";

						nh0Name = commandParamMap.get("NH0_NAME") != null ? commandParamMap.get("NH0_NAME").toString() : "";
						nh1Name = commandParamMap.get("NH1_NAME") != null ? commandParamMap.get("NH1_NAME").toString() : "";
						nh2Name = commandParamMap.get("NH2_NAME") != null ? commandParamMap.get("NH2_NAME").toString() : "";
						nh3Name = commandParamMap.get("NH3_NAME") != null ? commandParamMap.get("NH3_NAME").toString() : "";

//						System.out.println("nh0Name : >" + nh0Name + "<");
//						System.out.println("nh1Name : >" + nh1Name + "<");
//						System.out.println("nh2Name : >" + nh2Name + "<");
//						System.out.println("nh3Name : >" + nh3Name + "<");

						nhLevel = -1;
						nhLevel = commandParamMap.get("NH_LEVEL") != null ? Integer.parseInt(commandParamMap.get("NH_LEVEL").toString()) : -1;
						sourceNh0Id = commandParamMap.get("NH0_ID") != null ? Integer.parseInt(commandParamMap.get("NH0_ID").toString()) : -1;
						sourceNh1Id = commandParamMap.get("NH1_ID") != null ? Integer.parseInt(commandParamMap.get("NH1_ID").toString()) : -1;
						sourceNh2Id = commandParamMap.get("NH2_ID") != null ? Integer.parseInt(commandParamMap.get("NH2_ID").toString()) : -1;
						sourceNh3Id = commandParamMap.get("NH3_ID") != null ? Integer.parseInt(commandParamMap.get("NH3_ID").toString()) : -1;


//						System.out.println("sourceNh0Id : >" + sourceNh0Id + "<");
//						System.out.println("sourceNh1Id : >" + sourceNh1Id + "<");
//						System.out.println("sourceNh2Id : >" + sourceNh2Id + "<");
//						System.out.println("sourceNh3Id : >" + sourceNh3Id + "<");


						CheckIfNeighborhoodExistsIfNotCreateIt(connection, thisCommand, commandLine, nhLevel, nh0Name, nh1Name, nh2Name, nh3Name,  sourceNh0Id, sourceNh1Id, sourceNh2Id, sourceNh3Id );

/*						CheckIf_Nh_0_Exists_IfNot_CreateIt(connection, thisCommand, commandLine, sourceNh0Id, nh0Name);

						CheckIf_Nh_1_Exists_IfNot_CreateIt(connection, thisCommand, commandLine, sourceNh1Id, nh0Name, nh1Name);

						CheckIf_Nh_2_Exists_IfNot_CreateIt(connection, thisCommand, commandLine, sourceNh2Id, nh0Name, nh1Name, nh2Name);
						try
						{
							writeLogline(thisCommand, commandLine, MESSAGE_TYPE_SUCCESS, "Creating Neighbourhood at Level 2 : [" + nh2Name + "] Started.");

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

									System.out.println("Creation of NH_2 ..... nhl_1.getId() : " + nhl_2.getNhId() );
									int nh_2_id = nhl_2.getNhId();
									System.out.println("Creation of NH_2 ..... nh_2_id : " + nh_2_id );


									//Add admin/deployer user as member of newly created neighborhood
									int newMemberId = -1;
									newMemberId = MemberManager.createMember(connection, tid, migrationUserId, nh_2_id);
									AddMigrationObjectMap(connection, migrationUserName, sourceServer, targetServer, -1, newMemberId, tid, "Member", packageName); 
									writeLogline(thisCommand, commandLine, MESSAGE_TYPE_SUCCESS, "Success: Added MigrationUser to Neighbourhood [" + nh2Name +  "] at Level 2 : [" + nhl_2.getNhId() + "]");

									writeLogline(thisCommand, commandLine, MESSAGE_TYPE_SUCCESS, "Success: Created Neighbourhood [" + nh2Name +  "] at Level 2 : [" + nhl_2.getNhId() + "]");
									
									AddMigrationObjectMap(connection, nh2Name, sourceServer, targetServer, sourceNh2Id, nhl_2.getNhId(), tid, "NH_2", packageName); 

									try {	System.out.println("commiting  transaction"); tm.commitTransaction();} 
									catch (SQLException e) { e.printStackTrace(); }
								} 
								catch (SystemException | NeighborhoodException | SQLException e) 
								{
									e.printStackTrace();
									//sbResp.append( ContentDelimeter + "Creation of NH_1 : " + nh1Name +  Seperator + "Failed")  ;
									writeLogline(thisCommand, commandLine, MESSAGE_TYPE_FAILURE, "Failure : Creation of Neighborhood [" + nh2Name +  "] at Level 2 Failed. Reason : " + e.getMessage());
								}
								System.out.println("Creating NH_2 : " + nh2Name + " Done.");
							}
							else
							{
								int nh_2_id = nhl.getId();

								writeLogline(thisCommand, commandLine, MESSAGE_TYPE_WARNING, "Warning : Neighbourhood [" + nh2Name + "] at Level 2 Already Exists : ");

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
							writeLogline(thisCommand, commandLine, MESSAGE_TYPE_FAILURE, "Failure : Creation of Neighborhood [" + nh2Name +  "] at Level 2 Failed. Reason : " + e.getMessage());
							//sbResp.append( ContentDelimeter + "Creation of NH_2 : " + nh2Name +  Seperator + "Failed")  ;
						}
*/						
						break;

					case "CreateNH_3":
						//String nh0Name, nh1Name, nh2Name, nh3Name;

						nh0Name = "";
						nh1Name = "";
						nh2Name = "";
						nh3Name = "";

						nh0Name = commandParamMap.get("NH0_NAME") != null ? commandParamMap.get("NH0_NAME").toString() : "";
						nh1Name = commandParamMap.get("NH1_NAME") != null ? commandParamMap.get("NH1_NAME").toString() : "";
						nh2Name = commandParamMap.get("NH2_NAME") != null ? commandParamMap.get("NH2_NAME").toString() : "";
						nh3Name = commandParamMap.get("NH3_NAME") != null ? commandParamMap.get("NH3_NAME").toString() : "";

//						System.out.println("nh0Name : >" + nh0Name + "<");
//						System.out.println("nh1Name : >" + nh1Name + "<");
//						System.out.println("nh2Name : >" + nh2Name + "<");
//						System.out.println("nh3Name : >" + nh3Name + "<");

						nhLevel = -1;
						nhLevel = commandParamMap.get("NH_LEVEL") != null ? Integer.parseInt(commandParamMap.get("NH_LEVEL").toString()) : -1;
						sourceNh0Id = commandParamMap.get("NH0_ID") != null ? Integer.parseInt(commandParamMap.get("NH0_ID").toString()) : -1;
						sourceNh1Id = commandParamMap.get("NH1_ID") != null ? Integer.parseInt(commandParamMap.get("NH1_ID").toString()) : -1;
						sourceNh2Id = commandParamMap.get("NH2_ID") != null ? Integer.parseInt(commandParamMap.get("NH2_ID").toString()) : -1;
						sourceNh3Id = commandParamMap.get("NH3_ID") != null ? Integer.parseInt(commandParamMap.get("NH3_ID").toString()) : -1;


//						System.out.println("sourceNh0Id : >" + sourceNh0Id + "<");
//						System.out.println("sourceNh1Id : >" + sourceNh1Id + "<");
//						System.out.println("sourceNh2Id : >" + sourceNh2Id + "<");
//						System.out.println("sourceNh3Id : >" + sourceNh3Id + "<");


						CheckIfNeighborhoodExistsIfNotCreateIt(connection, thisCommand, commandLine, nhLevel, nh0Name, nh1Name, nh2Name, nh3Name,  sourceNh0Id, sourceNh1Id, sourceNh2Id, sourceNh3Id );

/*						CheckIf_Nh_0_Exists_IfNot_CreateIt(connection, thisCommand, commandLine, sourceNh0Id, nh0Name);

						CheckIf_Nh_1_Exists_IfNot_CreateIt(connection, thisCommand, commandLine, sourceNh1Id, nh0Name, nh1Name);

						CheckIf_Nh_2_Exists_IfNot_CreateIt(connection, thisCommand, commandLine, sourceNh2Id, nh0Name, nh1Name, nh2Name);

						CheckIf_Nh_3_Exists_IfNot_CreateIt(connection, thisCommand, commandLine, sourceNh3Id, nh0Name, nh1Name, nh2Name, nh3Name);

						try
						{
							
							writeLogline(thisCommand, commandLine, MESSAGE_TYPE_SUCCESS, "Creating Neighbourhood at Level 3 : [" + nh3Name + "] Started.");

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

									System.out.println("Creation of NH_3 ..... nhl_3.getId() : " + nhl_3.getNhId() );
									int nh_3_id = nhl_3.getNhId();
									System.out.println("Creation of NH_3 ..... nh_3_id : " + nh_3_id );


									//Add admin/deployer user as member of newly created neighborhood
									int newMemberId = -1;
									newMemberId = MemberManager.createMember(connection, tid, migrationUserId, nh_3_id);
									AddMigrationObjectMap(connection, migrationUserName, sourceServer, targetServer, -1, newMemberId, tid, "Member", packageName); 
									writeLogline(thisCommand, commandLine, MESSAGE_TYPE_SUCCESS, "Success: Added MigrationUser to Neighbourhood [" + nh3Name +  "] at Level 3 : [" + nhl_3.getNhId() + "]");

									writeLogline(thisCommand, commandLine, MESSAGE_TYPE_SUCCESS, "Success: Created Neighbourhood [" + nh3Name +  "] at Level 3 : [" + nhl_3.getNhId() + "]");

									System.out.println("Creation of NH_3 : " + nh3Name +  " Successful");
									AddMigrationObjectMap(connection, nh3Name, sourceServer, targetServer, sourceNh3Id, nhl_3.getNhId(), tid, "NH_3", packageName); 

									try {	System.out.println("commiting  transaction"); tm.commitTransaction();} 
									catch (SQLException e) { e.printStackTrace(); }
								} 
								catch (SystemException | NeighborhoodException | SQLException e) 
								{
									e.printStackTrace();
									//sbResp.append( ContentDelimeter + "Creation of NH_1 : " + nh1Name +  Seperator + "Failed")  ;
									writeLogline(thisCommand, commandLine, MESSAGE_TYPE_FAILURE, "Failure : Creation of Neighborhood [" + nh3Name +  "] at Level 3 Failed. Reason : " + e.getMessage());
								}
								System.out.println("Creating NH_3 : " + nh3Name + " Done.");

							}
							else
							{

								int nh_3_id = nhl.getId();

								writeLogline(thisCommand, commandLine, MESSAGE_TYPE_WARNING, "Warning : Neighbourhood [" + nh3Name + "] at Level 3 Already Exists : ");

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
							writeLogline(thisCommand, commandLine, MESSAGE_TYPE_FAILURE, "Failure : Creation of Neighborhood [" + nh3Name +  "] at Level 3 Failed. Reason : " + e.getMessage());
							//sbResp.append( ContentDelimeter + "Creation of NH_3 : " + nh3Name +  Seperator + "Failed")  ;
						}
*/						
						break;

					case  "CreateCollab":
						//String nh0Name, nh1Name, nh2Name, nh3Name,  collabName;
						//System.out.println("case is CreateCollab...............");
						nh0Name = "";
						nh1Name = "";
						nh2Name = "";
						nh3Name = "";
						collabNhName = "";
						nh0Name = commandParamMap.get("NH0_NAME") != null ? commandParamMap.get("NH0_NAME").toString() : "";
						nh1Name = commandParamMap.get("NH1_NAME") != null ? commandParamMap.get("NH1_NAME").toString() : "";
						nh2Name = commandParamMap.get("NH2_NAME") != null ? commandParamMap.get("NH2_NAME").toString() : "";
						nh3Name = commandParamMap.get("NH3_NAME") != null ? commandParamMap.get("NH3_NAME").toString() : "";

//						System.out.println("nh0Name : >" + nh0Name + "<");
//						System.out.println("nh1Name : >" + nh1Name + "<");
//						System.out.println("nh2Name : >" + nh2Name + "<");
//						System.out.println("nh3Name : >" + nh3Name + "<");

						nhLevel = -1;
						nhLevel = commandParamMap.get("NH_LEVEL") != null ? Integer.parseInt(commandParamMap.get("NH_LEVEL").toString()) : -1;
						sourceNh0Id = commandParamMap.get("NH0_ID") != null ? Integer.parseInt(commandParamMap.get("NH0_ID").toString()) : -1;
						sourceNh1Id = commandParamMap.get("NH1_ID") != null ? Integer.parseInt(commandParamMap.get("NH1_ID").toString()) : -1;
						sourceNh2Id = commandParamMap.get("NH2_ID") != null ? Integer.parseInt(commandParamMap.get("NH2_ID").toString()) : -1;
						sourceNh3Id = commandParamMap.get("NH3_ID") != null ? Integer.parseInt(commandParamMap.get("NH3_ID").toString()) : -1;


//						System.out.println("sourceNh0Id : >" + sourceNh0Id + "<");
//						System.out.println("sourceNh1Id : >" + sourceNh1Id + "<");
//						System.out.println("sourceNh2Id : >" + sourceNh2Id + "<");
//						System.out.println("sourceNh3Id : >" + sourceNh3Id + "<");

						sourceCollabId = Integer.parseInt(commandParamMap.get("COLLAB_ID"));
						int collabCreatorId ;

						collabCreatorName = commandParamMap.get("COLLAB_CREATOR");
						collabName = commandParamMap.get("COLLAB_NAME");

//						System.out.println("nh0Name : >" + nh0Name + "<");
//						System.out.println("nh1Name : >" + nh1Name + "<");
//						System.out.println("nh2Name : >" + nh2Name + "<");
//						System.out.println("nh3Name : >" + nh3Name + "<");
//						System.out.println("collabName : " + collabName);
//						System.out.println("COLLAB_CREATOR : " + collabCreatorName);

						writeLogline(thisCommand, commandLine, MESSAGE_TYPE_SUCCESS, "Creating Collaboration : [" + collabName + "] Started.");
						
						CheckIfNeighborhoodExistsIfNotCreateIt(connection, thisCommand, commandLine, nhLevel, nh0Name, nh1Name, nh2Name, nh3Name,  sourceNh0Id, sourceNh1Id, sourceNh2Id, sourceNh3Id );
/*
						if (!nh0Name.isEmpty() && !nh1Name.isEmpty() && !nh2Name.isEmpty() && !nh3Name.isEmpty() )
						{
							nhLevel = 3;
							CheckIf_Nh_0_Exists_IfNot_CreateIt(connection, thisCommand, commandLine, sourceNh0Id, nh0Name);
							CheckIf_Nh_1_Exists_IfNot_CreateIt(connection, thisCommand, commandLine, sourceNh1Id, nh0Name, nh1Name);
							CheckIf_Nh_2_Exists_IfNot_CreateIt(connection, thisCommand, commandLine, sourceNh2Id, nh0Name, nh1Name, nh2Name);
							CheckIf_Nh_3_Exists_IfNot_CreateIt(connection, thisCommand, commandLine, sourceNh3Id, nh0Name, nh1Name, nh2Name, nh3Name);
						}
						else if (!nh0Name.isEmpty() && !nh1Name.isEmpty() && !nh2Name.isEmpty() && nh3Name.isEmpty() )
						{
							nhLevel = 2;
							CheckIf_Nh_0_Exists_IfNot_CreateIt(connection, thisCommand, commandLine, sourceNh0Id, nh0Name);
							CheckIf_Nh_1_Exists_IfNot_CreateIt(connection, thisCommand, commandLine, sourceNh1Id, nh0Name, nh1Name);
							CheckIf_Nh_2_Exists_IfNot_CreateIt(connection, thisCommand, commandLine, sourceNh2Id, nh0Name, nh1Name, nh2Name);
						}
						else if (!nh0Name.isEmpty() && !nh1Name.isEmpty() && nh2Name.isEmpty() && nh3Name.isEmpty() )
						{
							nhLevel = 1;
							CheckIf_Nh_0_Exists_IfNot_CreateIt(connection, thisCommand, commandLine, sourceNh0Id, nh0Name);
							CheckIf_Nh_1_Exists_IfNot_CreateIt(connection, thisCommand, commandLine, sourceNh1Id, nh0Name, nh1Name);
						}
						else if (!nh0Name.isEmpty() && nh1Name.isEmpty() && nh2Name.isEmpty() && nh3Name.isEmpty() )
						{
							nhLevel = 0;
							CheckIf_Nh_0_Exists_IfNot_CreateIt(connection, thisCommand, commandLine, sourceNh0Id, nh0Name);
						}
*/						
						nhl = null;
						nhId = -1;

						try {
//							System.out.println("Before getting neighborhood levelId");
						//	System.out.println("nhLevel: " + nhLevel);
							nhl = NeighborhoodManager.getNeighborhoodLevelId(connection, nh0Name.trim(), nh1Name.trim(), nh2Name.trim(), nh3Name.trim(), nhLevel);
							nhId = nhl.getId();
							collabNhName = nhl.getName();
						//	System.out.println("nhId based on neighborhood: " + nhId);
						//	System.out.println("nhId based on neighborhood Name: " + collabNhName);
						} 
						catch (SystemException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						//Getting or Creating Neighborhood membershiop for Migration USER
						nhMembershipId = GetNeighborhoodMembershipIfNotExistCreateIt(connection, migrationUserId, nhId);
						
						CheckIf_Collab_Exists_IfNot_CreateIt(connection, thisCommand, commandLine, sourceCollabId, migrationUserId, nhId, nhMembershipId, collabNhName, collabName);

						break;

/*
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
							writeLogline(thisCommand, commandLine, MESSAGE_TYPE_FAILURE, "Failure : Creation of Collaboration [" + collabName +  "] Failed. Reason : [" + collabCreatorName + "] doest not have any membership in any Neighborhood.");
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
											writeLogline(thisCommand, commandLine, MESSAGE_TYPE_SUCCESS, "Success: Collaboration [" + collabName +  "]  is created successfully in Neighborhood [" + collabNhName + "].");

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
											writeLogline(thisCommand, commandLine, MESSAGE_TYPE_WARNING, "Warning : Collaboration [" + collabName + "] Already Exists : ");
											AddMigrationObjectMap(connection, collabName, sourceServer, targetServer, sourceCollabId, collabId, tid, "Collab", packageName); 
											System.out.println("Collaboration Already Exists :" + collabName);
										}
									}
									catch (Exception   e)
									{
										e.printStackTrace();
										//sbResp.append( ContentDelimeter + "Creation of Collaboration : " + collabName +  Seperator + "Failed")  ;
										System.out.println("Failed to create Collaboration : " + collabName);
										writeLogline(thisCommand, commandLine, MESSAGE_TYPE_FAILURE, "Failure : Creation of Collaboration [" + collabName +  "] is Failed. Reason : " + e.getMessage());
									}
								}
								else
								{
									writeLogline(thisCommand, commandLine, MESSAGE_TYPE_FAILURE, "Failure : Creation of Collaboration [" + collabName +  "] Failed. Reason : [" + collabCreatorName + "] is not a member of Neighborhood [" + collabNhName + "].");
									System.out.println("Membership NOT found in [" + collabNhName + "].");
								}
							}


						}
						break;
*/

					case  "CreateWb":
						//String nh0Name, nh1Name, nh2Name, nh3Name,  collabName, wbName;

//NH_LEVEL=2|NH0_ID=1|NH0_NAME=AcmeBank|NH1_ID=2|NH1_NAME=BU-1|NH2_ID=5|NH2_NAME=euc|NH3_ID=|NH3_NAME=|COLLAB_ID=1000|COLLAB_NAME=euc|WB_ID=1000|WB_NAME=Main|COMMAND=CreateWb|SEQUENCE=8
//NH_LEVEL=2|NH0_ID=1|NH0_NAME=AcmeBank|NH1_ID=2|NH1_NAME=BU-1|NH2_ID=5|NH2_NAME=euc|NH3_ID=|NH3_NAME=|COLLAB_ID=1001|COLLAB_NAME=__DocumentChain|WB_ID=1001|WB_NAME=Main|COMMAND=CreateWb|SEQUENCE=8

						nh0Name = "";
						nh1Name = "";
						nh2Name = "";
						nh3Name = "";
						collabNhName = "";
						wbName = "";
						nh0Name = commandParamMap.get("NH0_NAME") != null ? commandParamMap.get("NH0_NAME").toString() : "";
						nh1Name = commandParamMap.get("NH1_NAME") != null ? commandParamMap.get("NH1_NAME").toString() : "";
						nh2Name = commandParamMap.get("NH2_NAME") != null ? commandParamMap.get("NH2_NAME").toString() : "";
						nh3Name = commandParamMap.get("NH3_NAME") != null ? commandParamMap.get("NH3_NAME").toString() : "";


						nhLevel = -1;
						nhLevel = commandParamMap.get("NH_LEVEL") != null ? Integer.parseInt(commandParamMap.get("NH_LEVEL").toString()) : -1;
						sourceNh0Id = commandParamMap.get("NH0_ID") != null ? Integer.parseInt(commandParamMap.get("NH0_ID").toString()) : -1;
						sourceNh1Id = commandParamMap.get("NH1_ID") != null ? Integer.parseInt(commandParamMap.get("NH1_ID").toString()) : -1;
						sourceNh2Id = commandParamMap.get("NH2_ID") != null ? Integer.parseInt(commandParamMap.get("NH2_ID").toString()) : -1;
						sourceNh3Id = commandParamMap.get("NH3_ID") != null ? Integer.parseInt(commandParamMap.get("NH3_ID").toString()) : -1;

						sourceCollabId = Integer.parseInt(commandParamMap.get("COLLAB_ID"));
						sourceWbId = Integer.parseInt(commandParamMap.get("WB_ID"));
						collabName = commandParamMap.get("COLLAB_NAME");
						wbName = commandParamMap.get("WB_NAME");
						
//						System.out.println("nh0Name : >" + nh0Name + "<");
//						System.out.println("nh1Name : >" + nh1Name + "<");
//						System.out.println("nh2Name : >" + nh2Name + "<");
//						System.out.println("nh3Name : >" + nh3Name + "<");
//						System.out.println("nhLevel : >" + nhLevel + "<");

//						System.out.println("sourceNh0Id : >" + sourceNh0Id + "<");
//						System.out.println("sourceNh1Id : >" + sourceNh1Id + "<");
//						System.out.println("sourceNh2Id : >" + sourceNh2Id + "<");
//						System.out.println("sourceNh3Id : >" + sourceNh3Id + "<");

//						System.out.println("collabName : " + collabName);
//						System.out.println("sourceCollabId : " + sourceCollabId);
//						System.out.println("wbName : " + wbName);
//						System.out.println("sourceWbId : " + sourceWbId);

						//Get neighborhood ID

						writeLogline(thisCommand, commandLine, MESSAGE_TYPE_SUCCESS, "Creating Whiteboard : [" + wbName + "] Started.");

						CheckIfNeighborhoodExistsIfNotCreateIt(connection, thisCommand, commandLine, nhLevel, nh0Name, nh1Name, nh2Name, nh3Name,  sourceNh0Id, sourceNh1Id, sourceNh2Id, sourceNh3Id );

						nhl = null;
						nhId = -1;

						try {
							//System.out.println("Before getting neighborhood levelId");
							//System.out.println("nhLevel: " + nhLevel);
							nhl = NeighborhoodManager.getNeighborhoodLevelId(connection, nh0Name.trim(), nh1Name.trim(), nh2Name.trim(), nh3Name.trim(), nhLevel);
							nhId = nhl.getId();
							collabNhName = nhl.getName();
						//	System.out.println("nhId based on neighborhood: " + nhId);
						//	System.out.println("nhId based on neighborhood Name: " + collabNhName);
						} 
						catch (SystemException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}


						//Getting or Creating Neighborhood membershiop for Migration USER
						nhMembershipId = GetNeighborhoodMembershipIfNotExistCreateIt(connection, migrationUserId, nhId);
						
						CheckIf_Collab_Exists_IfNot_CreateIt(connection, thisCommand, commandLine, sourceCollabId, migrationUserId, nhId, nhMembershipId, collabNhName, collabName);

						CheckIf_Whiteboard_Exists_IfNot_CreateIt(connection, thisCommand, commandLine, sourceCollabId, migrationUserId, nhId, nhMembershipId, collabNhName, collabName, sourceWbId, wbName);

						break;


					/*	try {
							System.out.println("Before getting neighborhood levelId");
							System.out.println("nhLevel: " + nhLevel);
							nhl = NeighborhoodManager.getNeighborhoodLevelId(connection, nh0Name.trim(), nh1Name.trim(), nh2Name.trim(), nh3Name.trim(), nhLevel);
							nhId = nhl.getId();
							collabNhName = nhl.getName();
							System.out.println("nhId based on neighborhood: " + nhId);
							System.out.println("nhName based on neighborhood: " + collabNhName);

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
							{
								System.out.println("Collaboration not found ");
								writeLogline(thisCommand, commandLine, MESSAGE_TYPE_FAILURE, "Failure : Creation of Whiteboard [" + wbName +  "] Failed. Reason : [" + collabNhName + "] does not exists in Neighborhood [" + collabNhName + "].");
							}
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

						*/

					case "CreateCuboid":
						//String nh0Name, nh1Name, nh2Name, nh3Name,  collabName, wbName, cuboidName;
						cuboidExist = false;
						String tableName;

						nh0Name = "";
						nh1Name = "";
						nh2Name = "";
						nh3Name = "";
						collabNhName = "";
						wbName = "";


						nh0Name = commandParamMap.get("NH0_NAME") != null ? commandParamMap.get("NH0_NAME").toString() : "";
						nh1Name = commandParamMap.get("NH1_NAME") != null ? commandParamMap.get("NH1_NAME").toString() : "";
						nh2Name = commandParamMap.get("NH2_NAME") != null ? commandParamMap.get("NH2_NAME").toString() : "";
						nh3Name = commandParamMap.get("NH3_NAME") != null ? commandParamMap.get("NH3_NAME").toString() : "";

						nhLevel = -1;
						nhLevel = Integer.parseInt(commandParamMap.get("NH_LEVEL"));
						sourceNh0Id = commandParamMap.get("NH0_ID") != null ? Integer.parseInt(commandParamMap.get("NH0_ID").toString()) : -1;
						sourceNh1Id = commandParamMap.get("NH1_ID") != null ? Integer.parseInt(commandParamMap.get("NH1_ID").toString()) : -1;
						sourceNh2Id = commandParamMap.get("NH2_ID") != null ? Integer.parseInt(commandParamMap.get("NH2_ID").toString()) : -1;
						sourceNh3Id = commandParamMap.get("NH3_ID") != null ? Integer.parseInt(commandParamMap.get("NH3_ID").toString()) : -1;

						sourceCollabId = Integer.parseInt(commandParamMap.get("COLLAB_ID"));
						sourceWbId = Integer.parseInt(commandParamMap.get("WB_ID"));
						collabName = commandParamMap.get("COLLAB_NAME");
						wbName = commandParamMap.get("WB_NAME");
						cuboidName = commandParamMap.get("CUBOID_NAME");
						sourceCuboidId = Integer.parseInt(commandParamMap.get("CUBOID_ID"));

//						System.out.println("nh0Name : >" + nh0Name + "<");
//						System.out.println("nh1Name : >" + nh1Name + "<");
//						System.out.println("nh2Name : >" + nh2Name + "<");
//						System.out.println("nh3Name : >" + nh3Name + "<");
//						System.out.println("nhLevel : >" + nhLevel + "<");

//						System.out.println("sourceNh0Id : >" + sourceNh0Id + "<");
//						System.out.println("sourceNh1Id : >" + sourceNh1Id + "<");
//						System.out.println("sourceNh2Id : >" + sourceNh2Id + "<");
//						System.out.println("sourceNh3Id : >" + sourceNh3Id + "<");

//						System.out.println("collabName : " + collabName);
//						System.out.println("sourceCollabId : " + sourceCollabId);
//						System.out.println("wbName : " + wbName);
//						System.out.println("sourceWbId : " + sourceWbId);
//						System.out.println("cuboidName : " + cuboidName);
//						System.out.println("sourceCuboidId : " + sourceCuboidId);

						writeLogline(thisCommand, commandLine, MESSAGE_TYPE_SUCCESS, "Creating Cuboid : [" + cuboidName + "] Started.");
						//Create nh tree of doesnot exists
						CheckIfNeighborhoodExistsIfNotCreateIt(connection, thisCommand, commandLine, nhLevel, nh0Name, nh1Name, nh2Name, nh3Name,  sourceNh0Id, sourceNh1Id, sourceNh2Id, sourceNh3Id );

						nhl = null;
						nhId = -1;

						try {
							//System.out.println("Before getting neighborhood levelId");
							//System.out.println("nhLevel: " + nhLevel);
							nhl = NeighborhoodManager.getNeighborhoodLevelId(connection, nh0Name.trim(), nh1Name.trim(), nh2Name.trim(), nh3Name.trim(), nhLevel);
							nhId = nhl.getId();
							collabNhName = nhl.getName();
						//	System.out.println("nhId based on neighborhood: " + nhId);
						//	System.out.println("nhId based on neighborhood Name: " + collabNhName);
						} 
						catch (SystemException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						//Getting or Creating Neighborhood membershiop for Migration USER
						nhMembershipId = GetNeighborhoodMembershipIfNotExistCreateIt(connection, migrationUserId, nhId);

						//Create collab of doesnot exists
						CheckIf_Collab_Exists_IfNot_CreateIt(connection, thisCommand, commandLine, sourceCollabId, migrationUserId, nhId, nhMembershipId, collabNhName, collabName);
						//Create whiteboard of doesnot exists
						CheckIf_Whiteboard_Exists_IfNot_CreateIt(connection, thisCommand, commandLine, sourceCollabId, migrationUserId, nhId, nhMembershipId, collabNhName, collabName, sourceWbId, wbName);
						//Create Cuboid if it doesnot exists
						CheckIf_Cuboid_Exists_IfNot_CreateIt(connection, thisCommand, commandLine, sourceCollabId, migrationUserId, nhId, nhMembershipId, collabNhName, collabName, sourceWbId, wbName, sourceCuboidId, cuboidName);

						break;

/*
						//String cuboidCreatorUserName, cuboidCreatorNhName ;
						//int cuboidCreatorUserId, cuboidCreatorNhId, cuboidCreatorNhLevel, cuboidCreatorMemberId, sourceImportTxId ;
						
						cuboidCreatorUserId = Integer.parseInt(commandParamMap.get("CUBOID_CREATOR_USER_ID"));
						cuboidCreatorUserName = commandParamMap.get("CUBOID_CREATOR_USER_NAME");
						cuboidCreatorNhId = Integer.parseInt(commandParamMap.get("CUBOID_CREATOR_NH_ID"));
						cuboidCreatorNhName = commandParamMap.get("CUBOID_CREATOR_NH_NAME");
						cuboidCreatorNhLevel = Integer.parseInt(commandParamMap.get("CUBOID_CREATOR_NH_LEVEL"));
						cuboidCreatorMemberId = Integer.parseInt(commandParamMap.get("CUBOID_CREATOR_MEMBER_ID"));
						//sourceImportTxId = Integer.parseInt(commandParamMap.get("SOURCE_IMPORT_TXID"));
						sourceCuboidId = Integer.parseInt(commandParamMap.get("CUBOID_ID"));

						targetCuboidCreatorUserId = -1;
						targetCuboidCreatorMemberId = -1;
						targetCuboidCreatorNhId = -1;

						try
						{
							targetCuboidCreatorUserId = GetMigratedObjectIdOnTargetServerUsingObjectMap(connection, cuboidCreatorUserName, sourceServer, cuboidCreatorUserId, "User");

							if (cuboidCreatorNhLevel == 0)
								targetCuboidCreatorNhId = GetMigratedObjectIdOnTargetServerUsingObjectMap(connection, cuboidCreatorNhName, sourceServer, cuboidCreatorNhId, "NH_0");
							if (cuboidCreatorNhLevel == 1)
								targetCuboidCreatorNhId = GetMigratedObjectIdOnTargetServerUsingObjectMap(connection, cuboidCreatorNhName, sourceServer, cuboidCreatorNhId, "NH_1");
							if (cuboidCreatorNhLevel == 2)
								targetCuboidCreatorNhId = GetMigratedObjectIdOnTargetServerUsingObjectMap(connection, cuboidCreatorNhName, sourceServer, cuboidCreatorNhId, "NH_2");
							if (cuboidCreatorNhLevel == 3)
								targetCuboidCreatorNhId = GetMigratedObjectIdOnTargetServerUsingObjectMap(connection, cuboidCreatorNhName, sourceServer, cuboidCreatorNhId, "NH_3");

							targetCuboidCreatorMemberId = GetMigratedObjectIdOnTargetServerUsingObjectMap(connection, cuboidCreatorUserName, sourceServer, cuboidCreatorMemberId, "Member");
						}
						catch (SQLException sqe)
						{
							sqe.printStackTrace();
						}

						//targetCuboidId = GetMigratedObjectIdOnTargetServerUsingObjectMap(connection, "", sourceServer, tableId, "Cuboid");
						//tsNhId = GetMigratedObjectIdOnTargetServerUsingObjectMap(connection, "", sourceServer, nhId, "Cuboid");
						//tsMemberId = GetMigratedObjectIdOnTargetServerUsingObjectMap(connection, "", sourceServer, memberId, "Member");
						//Creating cuboid using BOARDWALK_APPLICATION 's Neighborhood-0 MEMBERSHIP
						//nh0MemberId = getNeighborhood0MembershipId(migrationUserName, nh0Name);

						//comment next 2 lines
						//bwcon = getBoardwalkConnection(migrationUserName, migrationUserPassword, targetCuboidCreatorMemberId);

						//System.out.println("Membership of Neighborhood Level 0 of User " + migrationUserName + " is : " + nh0MemberId); 

						System.out.println("nh0Name : >" + nh0Name + "<");
						System.out.println("nh1Name : >" + nh1Name + "<");
						System.out.println("nh2Name : >" + nh2Name + "<");
						System.out.println("nh3Name : >" + nh3Name + "<");
						System.out.println("collabName : " + collabName);
						System.out.println("wbName : " + wbName);

						System.out.println("targetCuboidCreatorUserId : " + targetCuboidCreatorUserId);
						System.out.println("targetCuboidCreatorMemberId : " + targetCuboidCreatorMemberId);
						System.out.println("targetCuboidCreatorNhId : " + targetCuboidCreatorNhId);

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

							//bwcon = BoardwalkConnectionManager.getBoardwalkConnection(connection , 2, "admin", 1003);
							bwcon = BoardwalkConnectionManager.getBoardwalkConnection(connection , targetCuboidCreatorUserId, cuboidCreatorUserName, targetCuboidCreatorMemberId);	

							System.out.println("bwcon is created using Target Server Ids:");
							System.out.println("targetCuboidCreatorUserId : " + targetCuboidCreatorUserId);
							System.out.println("cuboidCreatorUserName : " + cuboidCreatorUserName);
							System.out.println("targetCuboidCreatorMemberId : " + targetCuboidCreatorMemberId);

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
												//sb.append(Collabline );
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

										System.out.println("Before calling BoardwalkTableManager.createTable : bwcon.getMemberId() : " + bwcon.getMemberId());
										tableId = BoardwalkTableManager.createTable(bwcon, collabId, wbId, cuboidName, "Added through Migration process");
										AddMigrationObjectMap(connection, cuboidName, sourceServer, targetServer, sourceCuboidId, tableId, -1, "Cuboid", packageName); 
										//try {	System.out.println("commiting  transaction"); tm.commitTransaction();} 
										//catch (SQLException e) { e.printStackTrace(); }
										if (tableId != -1)
										{
											System.out.println("Cuboid Created Successfully: " + cuboidName +  "  cuboidID : " + tableId);
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
*/

					case "LinkImportCuboid":
//NH_LEVEL=0|NH0_ID=2|NH0_NAME=Root|NH1_ID=|NH1_NAME=|NH2_ID=|NH2_NAME=|NH3_ID=|NH3_NAME=|COLLAB_ID=2|COLLAB_NAME=Sonic|WB_ID=2|WB_NAME=Main|CUBOID_ID=2000002|CUBOID_NAME=New Buy|COMMAND=LinkImportCuboid|SEQUENCE=10|DATA_FILE=LinkExport_2000002.le|MAP_FILE=LinkExport_2000002.mp

//Migrate Only structure : MIGRATE_WHAT=LI_ONLY_STRUCTURE
//NH_LEVEL=2|NH0_ID=1|NH0_NAME=AcmeBank|NH1_ID=2|NH1_NAME=BU-1|NH2_ID=5|NH2_NAME=euc|NH3_ID=|NH3_NAME=|COLLAB_ID=1000|COLLAB_NAME=euc|WB_ID=1000|WB_NAME=Main|CUBOID_ID=2000058|CUBOID_NAME=Only Headers|CUBOID_CREATOR_USER_ID=1003|CUBOID_CREATOR_USER_NAME=package.manager|CUBOID_CREATOR_NH_ID=2|CUBOID_CREATOR_NH_NAME=BU-1|CUBOID_CREATOR_NH_LEVEL=1|CUBOID_CREATOR_MEMBER_ID=1009|COMMAND=LinkImportCuboid|SEQUENCE=10|MIGRATE_WHAT=LI_ONLY_STRUCTURE|FileName=linkImportCuboid_2000058.buf|DATA_FILE=LinkExport_2000058.le|MAP_FILE=LinkExport_2000058.mp|SOURCE_IMPORT_TXID=3517

//NH_LEVEL=2|NH0_ID=1|NH0_NAME=AcmeBank|NH1_ID=2|NH1_NAME=BU-1|NH2_ID=5|NH2_NAME=euc|NH3_ID=|NH3_NAME=|COLLAB_ID=1000|COLLAB_NAME=euc|WB_ID=1002|WB_NAME=Checker|CUBOID_ID=2000028|CUBOID_NAME=FX_Conversion|CUBOID_CREATOR_USER_ID=1000|CUBOID_CREATOR_USER_NAME=rahul|CUBOID_CREATOR_NH_ID=5|CUBOID_CREATOR_NH_NAME=euc|CUBOID_CREATOR_NH_LEVEL=2|CUBOID_CREATOR_MEMBER_ID=1003|COMMAND=LinkImportCuboid|SEQUENCE=10|FileName=linkImportCuboid_2000028.buf|DATA_FILE=LinkExport_2000028.le|MAP_FILE=LinkExport_2000028.mp|SOURCE_IMPORT_TXID=1198

						cuboidExist = false;
						tableName = "";

						nh0Name = "";
						nh1Name = "";
						nh2Name = "";
						nh3Name = "";
						collabNhName = "";
						wbName = "";


						nh0Name = commandParamMap.get("NH0_NAME") != null ? commandParamMap.get("NH0_NAME").toString() : "";
						nh1Name = commandParamMap.get("NH1_NAME") != null ? commandParamMap.get("NH1_NAME").toString() : "";
						nh2Name = commandParamMap.get("NH2_NAME") != null ? commandParamMap.get("NH2_NAME").toString() : "";
						nh3Name = commandParamMap.get("NH3_NAME") != null ? commandParamMap.get("NH3_NAME").toString() : "";

						nhLevel = -1;
						nhLevel = Integer.parseInt(commandParamMap.get("NH_LEVEL"));
						sourceNh0Id = commandParamMap.get("NH0_ID") != null ? Integer.parseInt(commandParamMap.get("NH0_ID").toString()) : -1;
						sourceNh1Id = commandParamMap.get("NH1_ID") != null ? Integer.parseInt(commandParamMap.get("NH1_ID").toString()) : -1;
						sourceNh2Id = commandParamMap.get("NH2_ID") != null ? Integer.parseInt(commandParamMap.get("NH2_ID").toString()) : -1;
						sourceNh3Id = commandParamMap.get("NH3_ID") != null ? Integer.parseInt(commandParamMap.get("NH3_ID").toString()) : -1;

						sourceCollabId = Integer.parseInt(commandParamMap.get("COLLAB_ID"));
						sourceWbId = Integer.parseInt(commandParamMap.get("WB_ID"));
						collabName = commandParamMap.get("COLLAB_NAME");
						wbName = commandParamMap.get("WB_NAME");
						cuboidName = commandParamMap.get("CUBOID_NAME");
						sourceCuboidId = Integer.parseInt(commandParamMap.get("CUBOID_ID"));

//						System.out.println("nh0Name : >" + nh0Name + "<");
//						System.out.println("nh1Name : >" + nh1Name + "<");
//						System.out.println("nh2Name : >" + nh2Name + "<");
//						System.out.println("nh3Name : >" + nh3Name + "<");
//						System.out.println("nhLevel : >" + nhLevel + "<");

//						System.out.println("sourceNh0Id : >" + sourceNh0Id + "<");
//						System.out.println("sourceNh1Id : >" + sourceNh1Id + "<");
//						System.out.println("sourceNh2Id : >" + sourceNh2Id + "<");
//						System.out.println("sourceNh3Id : >" + sourceNh3Id + "<");

//						System.out.println("collabName : " + collabName);
//						System.out.println("sourceCollabId : " + sourceCollabId);
//						System.out.println("wbName : " + wbName);
//						System.out.println("sourceWbId : " + sourceWbId);
//						System.out.println("cuboidName : " + cuboidName);
//						System.out.println("sourceCuboidId : " + sourceCuboidId);

						writeLogline(thisCommand, commandLine, MESSAGE_TYPE_SUCCESS, "Creating Cuboid : [" + cuboidName + "] Started.");
						//Create nh tree of doesnot exists
						CheckIfNeighborhoodExistsIfNotCreateIt(connection, thisCommand, commandLine, nhLevel, nh0Name, nh1Name, nh2Name, nh3Name,  sourceNh0Id, sourceNh1Id, sourceNh2Id, sourceNh3Id );

						nhl = null;
						nhId = -1;

						try {
							//System.out.println("Before getting neighborhood levelId");
							//System.out.println("nhLevel: " + nhLevel);
							nhl = NeighborhoodManager.getNeighborhoodLevelId(connection, nh0Name.trim(), nh1Name.trim(), nh2Name.trim(), nh3Name.trim(), nhLevel);
							nhId = nhl.getId();
							collabNhName = nhl.getName();
						//	System.out.println("nhId based on neighborhood: " + nhId);
						//	System.out.println("nhId based on neighborhood Name: " + collabNhName);
						} 
						catch (SystemException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						//Getting or Creating Neighborhood membershiop for Migration USER
						nhMembershipId = GetNeighborhoodMembershipIfNotExistCreateIt(connection, migrationUserId, nhId);

						targetCuboidCreatorUserId = migrationUserId;
						targetCuboidCreatorMemberId = nhMembershipId;
						targetCuboidCreatorNhId = nhId;

						//Create collab of doesnot exists
						CheckIf_Collab_Exists_IfNot_CreateIt(connection, thisCommand, commandLine, sourceCollabId, migrationUserId, nhId, nhMembershipId, collabNhName, collabName);
						//Create whiteboard of doesnot exists
						CheckIf_Whiteboard_Exists_IfNot_CreateIt(connection, thisCommand, commandLine, sourceCollabId, migrationUserId, nhId, nhMembershipId, collabNhName, collabName, sourceWbId, wbName);
						//Create Cuboid if it doesnot exists
						CheckIf_Cuboid_Exists_IfNot_CreateIt(connection, thisCommand, commandLine, sourceCollabId, migrationUserId, nhId, nhMembershipId, collabNhName, collabName, sourceWbId, wbName, sourceCuboidId, cuboidName);

						dataFile = "";
						mapFile = "";
						migrateWhat = "";

						dataFile = commandParamMap.get("DATA_FILE");
						mapFile = commandParamMap.get("MAP_FILE");
						migrateWhat = commandParamMap.get("MIGRATE_WHAT");

/*
						cuboidCreatorUserId = Integer.parseInt(commandParamMap.get("CUBOID_CREATOR_USER_ID"));
						cuboidCreatorUserName = commandParamMap.get("CUBOID_CREATOR_USER_NAME");
						cuboidCreatorNhId = Integer.parseInt(commandParamMap.get("CUBOID_CREATOR_NH_ID"));
						cuboidCreatorNhName = commandParamMap.get("CUBOID_CREATOR_NH_NAME");
						cuboidCreatorNhLevel = Integer.parseInt(commandParamMap.get("CUBOID_CREATOR_NH_LEVEL"));
						cuboidCreatorMemberId = Integer.parseInt(commandParamMap.get("CUBOID_CREATOR_MEMBER_ID"));
						//sourceImportTxId = Integer.parseInt(commandParamMap.get("SOURCE_IMPORT_TXID"));
						sourceCuboidId = Integer.parseInt(commandParamMap.get("CUBOID_ID"));
*/
						int targetCuboidId = -1;

						//int targetCuboidCreatorUserId, targetCuboidCreatorMemberId, targetCuboidCreatorNhId; 
/*						targetCuboidCreatorUserId = -1;
						targetCuboidCreatorMemberId = -1;
						targetCuboidCreatorNhId = -1;
*/
						try
						{
/*							targetCuboidCreatorUserId = GetMigratedObjectIdOnTargetServerUsingObjectMap(connection, cuboidCreatorUserName, sourceServer, cuboidCreatorUserId, "User");

							if (cuboidCreatorNhLevel == 0)
								targetCuboidCreatorNhId = GetMigratedObjectIdOnTargetServerUsingObjectMap(connection, cuboidCreatorNhName, sourceServer, cuboidCreatorNhId, "NH_0");
							if (cuboidCreatorNhLevel == 1)
								targetCuboidCreatorNhId = GetMigratedObjectIdOnTargetServerUsingObjectMap(connection, cuboidCreatorNhName, sourceServer, cuboidCreatorNhId, "NH_1");
							if (cuboidCreatorNhLevel == 2)
								targetCuboidCreatorNhId = GetMigratedObjectIdOnTargetServerUsingObjectMap(connection, cuboidCreatorNhName, sourceServer, cuboidCreatorNhId, "NH_2");
							if (cuboidCreatorNhLevel == 3)
								targetCuboidCreatorNhId = GetMigratedObjectIdOnTargetServerUsingObjectMap(connection, cuboidCreatorNhName, sourceServer, cuboidCreatorNhId, "NH_3");

							targetCuboidCreatorMemberId = GetMigratedObjectIdOnTargetServerUsingObjectMap(connection, cuboidCreatorUserName, sourceServer, cuboidCreatorMemberId, "Member");
*/
							targetCuboidId = GetMigratedObjectIdOnTargetServerUsingObjectMap(connection, cuboidName, sourceServer, sourceCuboidId, "Cuboid");
						}
						catch (SQLException sqe)
						{
							sqe.printStackTrace();
						}

//						System.out.println("nh0Name : >" + nh0Name + "<");
//						System.out.println("nh1Name : >" + nh1Name + "<");
//						System.out.println("nh2Name : >" + nh2Name + "<");
//						System.out.println("nh3Name : >" + nh3Name + "<");
//						System.out.println("collabName : " + collabName);
//						System.out.println("wbName : " + wbName);
//						System.out.println("Source Cuboid Id : " + sourceCuboidId);
//						System.out.println("Target Cuboid Id : " + targetCuboidId);

/*						System.out.println("targetCuboidCreatorUserId : " + targetCuboidCreatorUserId);
						System.out.println("targetCuboidCreatorMemberId : " + targetCuboidCreatorMemberId);
						System.out.println("targetCuboidCreatorNhId : " + targetCuboidCreatorNhId);
*/
						//Read Migration.Commands
						String linkImportDataFile = filePathMap.get(dataFile);
						String linkImportMapFile = filePathMap.get(mapFile);

						//System.out.println("linkImportDataFile : " + linkImportDataFile);
						//System.out.println("linkImportMapFile : " + linkImportMapFile);
						//System.out.println("migrateWhat : " + migrateWhat);

						readLinkImportMapFile (linkImportMapFile);

						readLinkImportBufferfFile (targetCuboidId, linkImportDataFile, linkImportMapFile, migrateWhat);

						//updateRowIdColumnIdMigrationMap(connection, cuboidName, sourceServer, targetServer, sourceCuboidId, targetCuboidId , packageName);

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

						pwd = "0";
						int activeFlag = 1;
						userId = -1;
						try
						{
						//	System.out.println("Getting User if iexists : " + email);
							User ue = UserManager.getUser(connection, email);
							
							if (ue == null)
							{
						//		System.out.println("User Not Found : " + email);
						//		System.out.println("Creating User : " + email);
								NewUser nu = new NewUser(email, pwd, firstName, lastName, activeFlag);
								userId = UserManager.createUser(connection, nu);
							}
							else
							{
						//		System.out.println("User Found : " + email);
								userId = ue.getId();
						//		System.out.println("User Found with Id : " + userId);
							}

							if (userId > 0)
							{
								//sbResp.append( ContentDelimeter + "Creation of User : " + email +  Seperator + "Successful")  ;
						//		System.out.println("Created new User : " + email);
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

//						System.out.println("nh0Name : >" + nh0Name + "<");
//						System.out.println("nh1Name : >" + nh1Name + "<");
//						System.out.println("nh2Name : >" + nh2Name + "<");
//						System.out.println("nh3Name : >" + nh3Name + "<");
//						System.out.println("email : " + email);

						//System.out.println("before getting userid : " + email);
						user = UserManager.getUser(connection, email);
						userId = user.getId();
						//System.out.println("after getting userId : " + userId);

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
							//System.out.println("Before getting neighborhood levelId");
							//System.out.println("nhLevel: " + nhLevel);
							nhl = NeighborhoodManager.getNeighborhoodLevelId(connection, nh0Name.trim(), nh1Name.trim(), nh2Name.trim(), nh3Name.trim(), nhLevel);
							if (nhl == null)
							{
								System.out.println("Warning : Neighborhood at Level 3 Not Found. It may not be deployed as part of Migration Plan. Moving to Next Migration Task.");
								break;
							}
							nhId = nhl.getId();
							//System.out.println("after getting neighborhood levelId");
							//int memberId = BoardwalkNeighborhoodManager.createMember(bwcon, nhId, userId);
							//System.out.println("nhId: " + nhId);
							//System.out.println("userId: " + userId);
							//System.out.println("tId: " + tid);

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
							//	System.out.println("new Member created under nhPath " + nhPath + " : " + memberId);
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

					case "Integrate_Brect":

		//				System.out.println("RUNNING ............. Integrate_Brect");

//CuboidPath=AcmeBank/SYSTEM/BRect/BRectDefinition|CuboidQuery=?Root NH=AcmeBank&&Business Unit=BU-1&&App=euc|
//COMMAND=Integrate_Brect|SEQUENCE=12|EXT_QUERY_ID=44|EXT_QUERY_PARAMS_VALUES=1000004^BRectDifinition_Migration^{username}|
//CVS_FILE=IntegrationRule_BRectDefinition.cvs|EXT_QUERY_ID=44|QUERY_PARAMS=
	
						lsQueryID = "";
						lsParameters		= "";

						cuboidPath = commandParamMap.get("CuboidPath");
						cuboidQuery = commandParamMap.get("CuboidQuery");
						cvsFileName = commandParamMap.get("CVS_FILE");
						extQueryParamValues = commandParamMap.get("EXT_QUERY_PARAMS_VALUES");
						lsQueryID = commandParamMap.get("EXT_QUERY_ID");

				//		System.out.println("lsQueryID  : " + lsQueryID );
				//		System.out.println("extQueryParamValues  : " + extQueryParamValues );

						lsParameters = extQueryParamValues;

						lsParameters = lsParameters.replace("^","|");
						lsParameters = lsParameters.replace("{username}",migrationUserName);
						//lsParameters = lsParameters.replace("{userid}",Integer.toString(migrationUserId));
						//lsParameters = lsParameters.replace("{memberid}",Integer.toString(migrationUserMembershipId));

				//		System.out.println("lsParameters  : " + lsParameters );

						//Read Migration.Commands
						CVSDataFile = filePathMap.get(cvsFileName);

				//		System.out.println("cvsFileName : " + cvsFileName);
				//		System.out.println("CVSDataFile : " + CVSDataFile);

						lsReportTitle = "Integrate_Brect";

						try
						{
							readCVSFileintoSqlTable(CVSDataFile, CALL_BW_INSERT_SQL_BRECTDEFINITION);

//							System.out.println("after readCVSFileintoSqlTable");

//							System.out.println("before calling Externalry Query");
				//			System.out.println("lsQueryID : " +  lsQueryID);
				//			System.out.println("lsParameters : " + lsParameters);

							DBcall lsQueryString;
							lsQueryString = getExternalDefinedQuery(Integer.parseInt(lsQueryID), lsParameters);
							String lsResponseStr = getPartValidationBuffer(lsReportTitle, lsQueryString, lsParameters, migrationUserId);
				//			System.out.println(lsResponseStr);
				//			System.out.println("Migration of Integrate_Brect [BRECTDEFINITION] Successful")  ;
							writeLogline("", "", MESSAGE_TYPE_SUCCESS, "Migration of Integrate_Brect [BRECTDEFINITION] Successful ");
						}
						catch (SQLException e) 
						{
							// TODO Auto-generated catch block
				//			e.printStackTrace();
				//			System.out.println("SQLException : While Migration of BRECTDEFINITION Cuboid : " + e.getMessage());
							System.out.println("Migration of Integrate_Brect [BRECTDEFINITION] Failed")  ;
							writeLogline("", "", MESSAGE_TYPE_FAILURE, "Migration of Integrate_Brect [BRECTDEFINITION] Failed : " + e.getMessage());
						}

						//runSuperMergeRuleForBRectDefinition

						break;

					case "Integrate_C2C":
				//		System.out.println("RUNNING ............. Integrate_C2C");

//CuboidPath=AcmeBank/SYSTEM/SuperMerge/C2C_SuperMerge_Rules|CuboidQuery=?Root NH=AcmeBank&&Business Unit=BU-3&&App=EUC-SP|COMMAND=Integrate_C2C|SEQUENCE=12|EXT_QUERY_ID=1001|EXT_QUERY_PARAMS_VALUES=1000004^C2C_SuperMerge_Rules_Migration^{username}|CVS_FILE=IntegrationRule_C2C_SuperMerge_Rules.cvs|EXT_QUERY_ID=1001|QUERY_PARAMS=

						cuboidPath = commandParamMap.get("CuboidPath");
						cuboidQuery = commandParamMap.get("CuboidQuery");
						cvsFileName = commandParamMap.get("CVS_FILE");
						extQueryParamValues = commandParamMap.get("EXT_QUERY_PARAMS_VALUES");
						lsQueryID = commandParamMap.get("EXT_QUERY_ID");

				//		System.out.println("lsQueryID  : " + lsQueryID );
				//		System.out.println("extQueryParamValues  : " + extQueryParamValues );

						lsParameters = extQueryParamValues;

						lsParameters = lsParameters.replace("^","|");
						lsParameters = lsParameters.replace("{username}",migrationUserName);
						//lsParameters = lsParameters.replace("{userid}",Integer.toString(migrationUserId));
						//lsParameters = lsParameters.replace("{memberid}",Integer.toString(migrationUserMembershipId));

				//		System.out.println("lsParameters  : " + lsParameters );

						//Read Migration.Commands
						CVSDataFile = filePathMap.get(cvsFileName);

				//		System.out.println("cvsFileName : " + cvsFileName);
				//		System.out.println("CVSDataFile : " + CVSDataFile);

						lsReportTitle = "Integrate_C2C";

						try
						{
							readCVSFileintoSqlTable(CVSDataFile, CALL_BW_INSERT_SQL_C2C_SUPERMERGE_RULES);

//							System.out.println("after readCVSFileintoSqlTable");

//							System.out.println("before calling Externalry Query");
				//			System.out.println("lsQueryID : " +  lsQueryID);
				//			System.out.println("lsParameters : " + lsParameters);

							DBcall lsQueryString;
							lsQueryString = getExternalDefinedQuery(Integer.parseInt(lsQueryID), lsParameters);
							String lsResponseStr = getPartValidationBuffer(lsReportTitle, lsQueryString, lsParameters, migrationUserId);
							System.out.println("Migration of Integrate_C2C [C2C_SuperMerge_Rules] Successful")  ;
							writeLogline("", "", MESSAGE_TYPE_SUCCESS, "Migration of Integrate_C2C [C2C_SuperMerge_Rules] Successful ");
						}
						catch (SQLException e) 
						{
							// TODO Auto-generated catch block
				//			e.printStackTrace();
				//			System.out.println("SQLException : While Migration of C2C_SuperMerge_Rules Cuboid : " + e.getMessage());
							System.out.println("Migration of Integrate_C2C [C2C_SuperMerge_Rules] Failed")  ;
							writeLogline("", "", MESSAGE_TYPE_FAILURE, "Migration of Integrate_C2C [C2C_SuperMerge_Rules] Failed : " + e.getMessage());
						}
						break;

					case "Integrate_C2S":
				//		System.out.println("RUNNING ............. Integrate_C2S");

//CuboidPath=AcmeBank/SYSTEM/SuperMerge/C2S_SuperMerge_Rules|CuboidQuery=?Root NH=AcmeBank&&Business Unit=BU-3&&App=EUC-SP|COMMAND=Integrate_C2S|SEQUENCE=12|EXT_QUERY_ID=1001|EXT_QUERY_PARAMS_VALUES=1000004^C2S_SuperMerge_Rules_Migration^{username}|CVS_FILE=IntegrationRule_C2S_SuperMerge_Rules.cvs|EXT_QUERY_ID=1001|QUERY_PARAMS=

						cuboidPath = commandParamMap.get("CuboidPath");
						cuboidQuery = commandParamMap.get("CuboidQuery");
						cvsFileName = commandParamMap.get("CVS_FILE");
						extQueryParamValues = commandParamMap.get("EXT_QUERY_PARAMS_VALUES");
						lsQueryID = commandParamMap.get("EXT_QUERY_ID");

				//		System.out.println("lsQueryID  : " + lsQueryID );
				//		System.out.println("extQueryParamValues  : " + extQueryParamValues );

						lsParameters = extQueryParamValues;

						lsParameters = lsParameters.replace("^","|");
						lsParameters = lsParameters.replace("{username}",migrationUserName);

				//		System.out.println("lsParameters  : " + lsParameters );

						//Read Migration.Commands
						CVSDataFile = filePathMap.get(cvsFileName);

				//		System.out.println("cvsFileName : " + cvsFileName);
				//		System.out.println("CVSDataFile : " + CVSDataFile);

						lsReportTitle = "Integrate_C2S";

						try
						{
							readCVSFileintoSqlTable(CVSDataFile, CALL_BW_INSERT_SQL_C2S_SUPERMERGE_RULES);

//							System.out.println("after readCVSFileintoSqlTable");

//							System.out.println("before calling Externalry Query");
				//			System.out.println("lsQueryID : " +  lsQueryID);
				//			System.out.println("lsParameters : " + lsParameters);

							DBcall lsQueryString;
							lsQueryString = getExternalDefinedQuery(Integer.parseInt(lsQueryID), lsParameters);
							String lsResponseStr = getPartValidationBuffer(lsReportTitle, lsQueryString, lsParameters, migrationUserId);
							System.out.println("Migration of Integrate_C2S [C2S_SuperMerge_Rules] Successful")  ;
							writeLogline("", "", MESSAGE_TYPE_SUCCESS, "Migration of Integrate_C2S [C2S_SuperMerge_Rules] Successful ");
						}
						catch (SQLException e) 
						{
							// TODO Auto-generated catch block
				//			e.printStackTrace();
				//			System.out.println("SQLException : While Migration of C2S_SuperMerge_Rules Cuboid : " + e.getMessage());
							System.out.println("Migration of Integrate_C2S [C2S_SuperMerge_Rules] Failed")  ;
							writeLogline("", "", MESSAGE_TYPE_FAILURE, "Migration of Integrate_C2S [C2S_SuperMerge_Rules] Failed : " + e.getMessage());
						}
						break;

					case "Integrate_KeyStore":
				//		System.out.println("RUNNING ............. Integrate_KeyStore");
						//CuboidPath=AcmeBank/SYSTEM/KeyStore/KeyStore|CuboidQuery=?Root NH=AcmeBank&&Business Unit=BU-3&&App=EUC-SP|COMMAND=Integrate_KeyStore|SEQUENCE=12|EXT_QUERY_ID=1001|EXT_QUERY_PARAMS_VALUES=1000004^KeyStore_Migration^{username}|CVS_FILE=IntegrationRule_KeyStore.cvs|EXT_QUERY_ID=1001|QUERY_PARAMS=
						lsQueryID = "";
						lsParameters	= "";

						cuboidPath = commandParamMap.get("CuboidPath");
						cuboidQuery = commandParamMap.get("CuboidQuery");
						cvsFileName = commandParamMap.get("CVS_FILE");
						extQueryParamValues = commandParamMap.get("EXT_QUERY_PARAMS_VALUES");
						lsQueryID = commandParamMap.get("EXT_QUERY_ID");

				//		System.out.println("lsQueryID  : " + lsQueryID );
				//		System.out.println("extQueryParamValues  : " + extQueryParamValues );

						lsParameters = extQueryParamValues;

						lsParameters = lsParameters.replace("^","|");
						lsParameters = lsParameters.replace("{username}",migrationUserName);
						//lsParameters = lsParameters.replace("{userid}",Integer.toString(migrationUserId));
						//lsParameters = lsParameters.replace("{memberid}",Integer.toString(migrationUserMembershipId));

				//		System.out.println("lsParameters  : " + lsParameters );

						//Read Migration.Commands
						CVSDataFile = filePathMap.get(cvsFileName);

				//		System.out.println("cvsFileName : " + cvsFileName);
				//		System.out.println("CVSDataFile : " + CVSDataFile);

						lsReportTitle = "Integrate_KeyStore";

						try
						{
							readCVSFileintoSqlTable(CVSDataFile, CALL_BW_INSERT_SQL_KEYSTORE);

//							System.out.println("after readCVSFileintoSqlTable");

//							System.out.println("before calling Externalry Query");
				//			System.out.println("lsQueryID : " +  lsQueryID);
				//			System.out.println("lsParameters : " + lsParameters);

							DBcall lsQueryString;
							lsQueryString = getExternalDefinedQuery(Integer.parseInt(lsQueryID), lsParameters);
							String lsResponseStr = getPartValidationBuffer(lsReportTitle, lsQueryString, lsParameters, migrationUserId);
							System.out.println("Migration of Integrate_KeyStore [KeyStore] Successful")  ;
							writeLogline("", "", MESSAGE_TYPE_SUCCESS, "Migration of Integrate_KeyStore [KeyStore] Successful ");
						}
						catch (SQLException e) 
						{
							// TODO Auto-generated catch block
				//			e.printStackTrace();
				//			System.out.println("Migration of Integrate_KeyStore Failed")  ;
				//			System.out.println("SQLException : While Migration of KeyStore Cuboid : " + e.getMessage());
							System.out.println("Migration of Integrate_KeyStore [KeyStore] Failed")  ;
							writeLogline("", "", MESSAGE_TYPE_FAILURE, "Migration of Integrate_KeyStore [KeyStore] Failed : " + e.getMessage());
						}
						break;

					case "Integrate_MultiC2C":
				//		System.out.println("RUNNING ............. Integrate_MultiC2C");

//CuboidPath=AcmeBank/SYSTEM/SuperMerge/Multi_C2C_SuperMerge_Rules|CuboidQuery=?Root NH=AcmeBank&&Business Unit=BU-3&&App=EUC-SP|COMMAND=Integrate_MultiC2C|SEQUENCE=12|EXT_QUERY_ID=1001|EXT_QUERY_PARAMS_VALUES=1000004^Multi_C2C_SuperMerge_Rules_Migration^{username}|CVS_FILE=IntegrationRule_Multi_C2C_SuperMerge_Rules.cvs|EXT_QUERY_ID=1001|QUERY_PARAMS=

						cuboidPath = commandParamMap.get("CuboidPath");
						cuboidQuery = commandParamMap.get("CuboidQuery");
						cvsFileName = commandParamMap.get("CVS_FILE");
						extQueryParamValues = commandParamMap.get("EXT_QUERY_PARAMS_VALUES");
						lsQueryID = commandParamMap.get("EXT_QUERY_ID");

				//		System.out.println("lsQueryID  : " + lsQueryID );
				//		System.out.println("extQueryParamValues  : " + extQueryParamValues );

						lsParameters = extQueryParamValues;

						lsParameters = lsParameters.replace("^","|");
						lsParameters = lsParameters.replace("{username}",migrationUserName);

				//		System.out.println("lsParameters  : " + lsParameters );

						//Read Migration.Commands
						CVSDataFile = filePathMap.get(cvsFileName);

				//		System.out.println("cvsFileName : " + cvsFileName);
				//		System.out.println("CVSDataFile : " + CVSDataFile);

						lsReportTitle = "Integrate_MultiC2C";

						try
						{
							readCVSFileintoSqlTable(CVSDataFile, CALL_BW_INSERT_SQL_MULTI_C2C_SUPERMERGE_RULES);

//							System.out.println("after readCVSFileintoSqlTable");

//							System.out.println("before calling Externalry Query");
				//			System.out.println("lsQueryID : " +  lsQueryID);
				//			System.out.println("lsParameters : " + lsParameters);

							DBcall lsQueryString;
							lsQueryString = getExternalDefinedQuery(Integer.parseInt(lsQueryID), lsParameters);
							String lsResponseStr = getPartValidationBuffer(lsReportTitle, lsQueryString, lsParameters, migrationUserId);
							System.out.println("Migration of Integrate_MultiC2C [Multi_C2C_SuperMerge_Rules] Successful")  ;
							writeLogline("", "", MESSAGE_TYPE_SUCCESS, "Migration of Integrate_MultiC2C [Multi_C2C_SuperMerge_Rules] Successful ");
						}
						catch (SQLException e) 
						{
							// TODO Auto-generated catch block
				//			e.printStackTrace();
				//			System.out.println("Migration of Integrate_MultiC2C Failed")  ;
				//			System.out.println("SQLException : While Migration of Multi_C2C_SuperMerge_Rules Cuboid : " + e.getMessage());
							System.out.println("Migration of Integrate_MultiC2C [Multi_C2C_SuperMerge_Rules] Failed")  ;
							writeLogline("", "", MESSAGE_TYPE_FAILURE, "Migration of Integrate_MultiC2C [Multi_C2C_SuperMerge_Rules] Failed : " + e.getMessage());
						}
						break;

					case "Integrate_Rules":
				//		System.out.println("RUNNING ............. Integrate_Rules");
//CuboidPath=AcmeBank/SYSTEM/SuperMerge/INTEGRATION_RULES|CuboidQuery=?Root NH=AcmeBank&&Business Unit=BU-3&&App=EUC-SP|COMMAND=Integrate_Rules|SEQUENCE=12|EXT_QUERY_ID=1001|EXT_QUERY_PARAMS_VALUES=1000004^INTEGRATION_RULES_Migration^{username}|CVS_FILE=IntegrationRule_INTEGRATION_RULES.cvs|EXT_QUERY_ID=1001|QUERY_PARAMS=

						cuboidPath = commandParamMap.get("CuboidPath");
						cuboidQuery = commandParamMap.get("CuboidQuery");
						cvsFileName = commandParamMap.get("CVS_FILE");
						extQueryParamValues = commandParamMap.get("EXT_QUERY_PARAMS_VALUES");
						lsQueryID = commandParamMap.get("EXT_QUERY_ID");

				//		System.out.println("lsQueryID  : " + lsQueryID );
				//		System.out.println("extQueryParamValues  : " + extQueryParamValues );

						lsParameters = extQueryParamValues;

						lsParameters = lsParameters.replace("^","|");
						lsParameters = lsParameters.replace("{username}",migrationUserName);

				//		System.out.println("lsParameters  : " + lsParameters );

						//Read Migration.Commands
						CVSDataFile = filePathMap.get(cvsFileName);

				//		System.out.println("cvsFileName : " + cvsFileName);
				//		System.out.println("CVSDataFile : " + CVSDataFile);

						lsReportTitle = "Integrate_Rules";

						try
						{
							readCVSFileintoSqlTable(CVSDataFile, CALL_BW_INSERT_SQL_INTEGRATION_RULES);

//							System.out.println("after readCVSFileintoSqlTable");

//							System.out.println("before calling Externalry Query");
				//			System.out.println("lsQueryID : " +  lsQueryID);
				//			System.out.println("lsParameters : " + lsParameters);

							DBcall lsQueryString;
							lsQueryString = getExternalDefinedQuery(Integer.parseInt(lsQueryID), lsParameters);
							String lsResponseStr = getPartValidationBuffer(lsReportTitle, lsQueryString, lsParameters, migrationUserId);
							System.out.println("Migration of Integrate_Rules [INTEGRATION_RULES] Successful")  ;
							writeLogline("", "", MESSAGE_TYPE_SUCCESS, "Migration of Integrate_Rules [INTEGRATION_RULES] Successful ");
						}
						catch (SQLException e) 
						{
							// TODO Auto-generated catch block
				//			e.printStackTrace();
				//			System.out.println("Migration of Integrate_Rules Failed")  ;
				//			System.out.println("SQLException : While Migration of INTEGRATION_RULES Cuboid : " + e.getMessage());
							System.out.println("Migration of Integrate_Rules [INTEGRATION_RULES] Failed")  ;
							writeLogline("", "", MESSAGE_TYPE_FAILURE, "Migration of Integrate_Rules [INTEGRATION_RULES] Failed : " + e.getMessage());
						}
						break;

					case "Integrate_S2C":
				//		System.out.println("RUNNING ............. Integrate_S2C");
//CuboidPath=AcmeBank/SYSTEM/SuperMerge/S2C_SuperMerge_Rules|CuboidQuery=?Root NH=AcmeBank&&Business Unit=BU-3&&App=EUC-SP|COMMAND=Integrate_S2C|SEQUENCE=12|EXT_QUERY_ID=1001|EXT_QUERY_PARAMS_VALUES=1000004^S2C_SuperMerge_Rules_Migration^{username}|CVS_FILE=IntegrationRule_S2C_SuperMerge_Rules.cvs|EXT_QUERY_ID=1001|QUERY_PARAMS=

						cuboidPath = commandParamMap.get("CuboidPath");
						cuboidQuery = commandParamMap.get("CuboidQuery");
						cvsFileName = commandParamMap.get("CVS_FILE");
						extQueryParamValues = commandParamMap.get("EXT_QUERY_PARAMS_VALUES");
						lsQueryID = commandParamMap.get("EXT_QUERY_ID");

				//		System.out.println("lsQueryID  : " + lsQueryID );
				//		System.out.println("extQueryParamValues  : " + extQueryParamValues );

						lsParameters = extQueryParamValues;

						lsParameters = lsParameters.replace("^","|");
						lsParameters = lsParameters.replace("{username}",migrationUserName);

				//		System.out.println("lsParameters  : " + lsParameters );

						//Read Migration.Commands
						CVSDataFile = filePathMap.get(cvsFileName);

				//		System.out.println("cvsFileName : " + cvsFileName);
				//		System.out.println("CVSDataFile : " + CVSDataFile);

						lsReportTitle = "Integrate_S2C";

						try
						{
							readCVSFileintoSqlTable(CVSDataFile, CALL_BW_INSERT_SQL_S2C_SUPERMERGE_RULES);

//							System.out.println("after readCVSFileintoSqlTable");

//							System.out.println("before calling Externalry Query");
				//			System.out.println("lsQueryID : " +  lsQueryID);
				//			System.out.println("lsParameters : " + lsParameters);

							DBcall lsQueryString;
							lsQueryString = getExternalDefinedQuery(Integer.parseInt(lsQueryID), lsParameters);
							String lsResponseStr = getPartValidationBuffer(lsReportTitle, lsQueryString, lsParameters, migrationUserId);
							System.out.println("Migration of Integrate_S2C [S2C_SuperMerge_Rules] Successful")  ;
							writeLogline("", "", MESSAGE_TYPE_SUCCESS, "Migration of Integrate_S2C [S2C_SuperMerge_Rules] Successful ");
						}
						catch (SQLException e) 
						{
							// TODO Auto-generated catch block
				//			e.printStackTrace();
				//			System.out.println("Migration of Integrate_S2C Failed")  ;
				//			System.out.println("SQLException : While Migration of S2C_SuperMerge_Rules Cuboid : " + e.getMessage());
							System.out.println("Migration of Integrate_S2C [S2C_SuperMerge_Rules] Failed")  ;
							writeLogline("", "", MESSAGE_TYPE_FAILURE, "Migration of Integrate_S2C [S2C_SuperMerge_Rules] Failed : " + e.getMessage());
						}
						break;

					case "importRelation":
				//		System.out.println("RUNNING ............. importRelation");
//SELECTED_NH_NAME=AcmeBank|SELECTED_NH_ID=1|RELATION=ALL BUS|RELATED_NH_NAME=AcmeBank/BU-1^AcmeBank/BU-2^AcmeBank/BU-3|RELATED_NH_ID=2^3^4|TaskName=Migrate Relation|Command=importRelation|Sequence=25
						String selectedNhName, selectedNhId, relationName, relatedNhName, relatedNhId;

						selectedNhName = commandParamMap.get("SELECTED_NH_NAME");
						selectedNhId = commandParamMap.get("SELECTED_NH_ID");
						relationName = commandParamMap.get("RELATION");
						relatedNhName = commandParamMap.get("RELATED_NH_NAME");
						relatedNhId = commandParamMap.get("RELATED_NH_ID");

						try 
						{
							try  { 	tm = new TransactionManager( connection, migrationUserId); 	tid = tm.startTransaction(); } 
							catch (SQLException e1) { e1.printStackTrace();}

							AddRelationByMigrationProcess (connection,  relationName, selectedNhName, relatedNhName, tid) ;

							try {	System.out.println("commiting  transaction"); tm.commitTransaction();} 
							catch (SQLException e) { e.printStackTrace();} 
						}
						catch (SQLException e) 
						{
							// TODO Auto-generated catch block
				//			e.printStackTrace();
							System.out.println("Creation of Relation : " + relationName +  " Failed");
							writeLogline(thisCommand, commandLine, MESSAGE_TYPE_FAILURE, "Failure : Creation of Relation [" + relationName +  "] Failed. Reason : " + e.getMessage());
							//sbResp.append( ContentDelimeter + "Creation of NH_0 : " + nh0Name +  Seperator + "Failed")  ;
						}
						break;

					case "CreateSQLTable":
				//		System.out.println("RUNNING ....start here......... createSQLTable");
//ObjectID=13243102|ObjectName=MY_TEST_TABLE_ABCD|ObjectType=U|CreateDate=44710.9909762731|ModifyDate=44710.9909762731|COMMAND=CreateSQLTable|TaskName=Migrate Table|Sequence=20|SQL_FILE=MY_TEST_TABLE_ABCD.SQL
						objectName = commandParamMap.get("ObjectName");
						sqlDDLfilename = commandParamMap.get("SQL_FILE");
						objectType = commandParamMap.get("ObjectType");

						//Read Migration.Commands
						createDDLFilePathname = filePathMap.get(sqlDDLfilename);

				//		System.out.println("objectName : " + objectName);
				//		System.out.println("sqlDDLfilename : " + sqlDDLfilename);
				//		System.out.println("createDDLFilePathname : " + createDDLFilePathname);

						try
						{
							readSqlDDLfromFileAndExecuteCommand(connection, objectName, createDDLFilePathname, objectType);
							System.out.println("Migration of SQL Table [ " + sqlDDLfilename + " ] Successful." )  ;
						}
						catch (Exception e)		//SQLException e 
						{
							// TODO Auto-generated catch block
				//			e.printStackTrace();
							System.out.println("Migration of SQL Table [ " + sqlDDLfilename + " ] Failed. Reason : " + e.getMessage() )  ;
						}
						break;

					case "CreateSP":
			//			System.out.println("RUNNING ............. createSP");
//ObjectID=2144726693|ObjectName=BW_THIS_IS_NEW_SP|ObjectType=P|CreateDate=44710.4920107292|ModifyDate=44710.4920107292|COMMAND=CreateSP|TaskName=Migrate SP|Sequence=21|SQL_FILE=BW_THIS_IS_NEW_SP.SQL

						objectName = commandParamMap.get("ObjectName");
						sqlDDLfilename = commandParamMap.get("SQL_FILE");
						objectType = commandParamMap.get("ObjectType");

						//Read Migration.Commands
						createDDLFilePathname = filePathMap.get(sqlDDLfilename);

				//		System.out.println("objectName : " + objectName);
				//		System.out.println("sqlDDLfilename : " + sqlDDLfilename);
				//		System.out.println("createDDLFilePathname : " + createDDLFilePathname);

						try
						{
							readSqlDDLfromFileAndExecuteCommand(connection, objectName, createDDLFilePathname, objectType);
							System.out.println("Migration of Stored Procedure [ " + sqlDDLfilename +  " ] Successful." )  ;
				//			System.out.println("after readSqlDDLfromFileAndExecuteCommand");
						}
						catch (Exception e)		//SQLException e 
						{
							// TODO Auto-generated catch block
				//			e.printStackTrace();
							System.out.println("Migration of Stored Procedure [ " + sqlDDLfilename +  " ] Failed. Reason : " + e.getMessage() )  ;
						}
						break;

					case "CreateExternalQuery":
					//	System.out.println("RUNNING ............. CreateExternalQuery");
//QueryId=6|QueryDesc=UPDATE TEMPLATE VERSION|QueryCall={CALL BW_UPDATE_TEMPLATE_VERSION(?,?,?)}|QueryParams=STRING#INT#INT|COMMAND=CreateExternalQuery|SEQUENCE=22|TaskName=Migrate ExternalQuery|INSERT_STMT=INSERT BW_EXT_QUERIES VALUES ('UPDATE TEMPLATE VERSION', '{CALL BW_UPDATE_TEMPLATE_VERSION(?,?,?)}', 'STRING^INT^INT' )

						int extQueryId;
						String extQueryDesc, extQueryCall, extQueryParams;

						extQueryId = Integer.parseInt(commandParamMap.get("QueryId"));
						extQueryDesc = commandParamMap.get("QueryDesc");
						extQueryCall = commandParamMap.get("QueryCall");
						extQueryParams = commandParamMap.get("QueryParams").replace("#", "\\|");

						try
						{
							insertExternalQueryForMigration(connection, extQueryId, extQueryDesc, extQueryCall, extQueryParams);
							System.out.println("Migration of External Query [ " + extQueryDesc + " ] Successful."  );
						}
						catch (SQLException e)		//SQLException e 
						{
							// TODO Auto-generated catch block
							System.out.println("Migration of External Query [" + extQueryDesc + "] Failed. Reason : " + e.getMessage());
						}
						break;


					case "importTemplate":
						System.out.println("RUNNING ............. importTemplate");
//NH0=AcmeBank|NH1=BU-1|NH2=euc|NH3=Checker|TemplateName=Checker.xlsm|DownloadURL=http://localhost:8080/BW_VELOCITY_DEV/DisplayDocument?id=1|
//OwnedBy=rahul|UpdatedBy=|UpdatedOn=Apr 29 2022  3:09PM|Comments=|BlobID=1|COMMAND=importTemplate|TaskName=Migrate Template|Sequence=24|TEMPLATE_FILE=Checker.xlsm
//						String templateName, downloadUrl, templateFile, templateFilePath;
//						int blobId;

						nh0Name = commandParamMap.get("NH0");
						nh1Name = commandParamMap.get("NH1");
						nh2Name = commandParamMap.get("NH2");
						nh3Name = commandParamMap.get("NH3");
						templateName = commandParamMap.get("TemplateName");
						//downloadUrl = commandParamMap.get("DownloadURL");
						blobId = Integer.parseInt(commandParamMap.get("BlobID"));
						templateFile = commandParamMap.get("TEMPLATE_FILE");
						//templateSize = "";
						nhHierarchy = nh0Name + "~" + nh1Name + "~" + nh2Name + "~" + nh3Name ;

						String templateFilePath = filePathMap.get(templateFile);

					//	System.out.println("blobId : " + blobId);
					//	System.out.println("templateFilePath : " + templateFilePath);

						//Reading File Information
						dtfile = new File(templateFilePath);
						bytes = dtfile.length();
						kilobytes = (bytes / 1024);
						megabytes = (kilobytes / 1024);


						ext = "";
						dotIndex = templateFilePath.lastIndexOf('.');
						if (dotIndex > 0) 
						{
							ext = templateFilePath.substring(dotIndex + 1);
						}
					
						//fileName = templateName + "." + ext;

						templateNameNoExt = templateName.replace("."+ext, "");

						contentType = ext + " File";
						fileSize = megabytes + " MB";

					//	System.out.println("fileName : " + fileName);
					//	System.out.println("ext : " + ext);
					//	System.out.println("contentType : " + contentType);
					//	System.out.println("filesize : " + fileSize);

						// ADD OBJECT MAP OF newBlobId vs blobId

						templateSize = fileSize;
						templateType = contentType;

						//String downloadUrl = GetSystemProperty("BOARDWALK_SERVER_URL");
						url = req.getRequestURL().toString();
						searchSlash = "/";

						slashIndex = url.lastIndexOf(searchSlash);
						if (slashIndex > 0) {
							url = url.substring(0, slashIndex);
						}

						writeLogline(thisCommand, commandLine, MESSAGE_TYPE_STATUS, "Status : Saving User-Role Template Blob into Database started [" + templateName + "].");							
							int thisBlobId = -1;
							thisBlobId = SaveTemplateAsBlobIntoDatabase(connection, templateNameNoExt, templateFilePath);
						writeLogline(thisCommand, commandLine, MESSAGE_TYPE_STATUS, "Status : Saving User-Role Template Blob into Database complete [" + templateName + "].");							

						//String args[] = new String[4]; //FNAME,EXT,TYPE,CLIENT
						//BufferedInputStream in = BlobManager.getDocument(connection, thisBlobId, args);

						//Reading the Properties from template here

						FileInputStream in = new FileInputStream(new File(templateFilePath));
						//FileInputStream inputStream = new FileInputStream(new File(templateFilePath));
						//Workbook workbook = new XSSFWorkbook(inputStream);
       
						//Read Template Major Version and Minor Version from Template
						//Read BWTemplateMajorVersion, BWTemplateMinorVersion from System Properties
						//Read the System Properties 
						XSSFWorkbook workbook = new XSSFWorkbook(in);
						XSSFName TemplateMajorVersion = workbook.getName("BWTemplateMajorVersion");
						XSSFName TemplateMinorVersion = workbook.getName("BWTemplateMinorVersion");

						// retrieve the cell at the named range and test its contents
						CellReference cRef;
						Sheet s ;
						org.apache.poi.ss.usermodel.Row r ;
						org.apache.poi.ss.usermodel.Cell c;

						//Major Version
						cRef = new CellReference(TemplateMajorVersion.getRefersToFormula());
						s = workbook.getSheet(cRef.getSheetName());
						r = s.getRow(cRef.getRow());
						c = r.getCell(cRef.getCol());
						int templateMajorVersion  = (int) c.getNumericCellValue();
						
						//Minor Version
						cRef = new CellReference(TemplateMinorVersion.getRefersToFormula());
						s = workbook.getSheet(cRef.getSheetName());
						r = s.getRow(cRef.getRow());
						c = r.getCell(cRef.getCol());
						int templateMinorVersion  = (int) c.getNumericCellValue();
						workbook.close();
						in.close();

					//	System.out.println("majorVersion = " + templateMajorVersion);
					//	System.out.println("minorVersion = " + templateMinorVersion);


/*
							//Read Template Major Version and Minor Version from Template
							//Read BWTemplateMajorVersion, BWTemplateMinorVersion from System Properties
							//Read the System Properties 
							XSSFWorkbook workbook = new XSSFWorkbook(in);
							XSSFName TemplateMajorVersion = workbook.getName("BWTemplateMajorVersion");
							XSSFName TemplateMinorVersion = workbook.getName("BWTemplateMinorVersion");

							// retrieve the cell at the named range and test its contents
							CellReference cRef;
							Sheet s ;
							org.apache.poi.ss.usermodel.Row r ;
							org.apache.poi.ss.usermodel.Cell c;

							//Major Version
							cRef = new CellReference(TemplateMajorVersion.getRefersToFormula());
							s = workbook.getSheet(cRef.getSheetName());
							r = s.getRow(cRef.getRow());
							c = r.getCell(cRef.getCol());
							double majorVersion = c.getNumericCellValue();
							
							//Minor Version
							cRef = new CellReference(TemplateMinorVersion.getRefersToFormula());
							s = workbook.getSheet(cRef.getSheetName());
							r = s.getRow(cRef.getRow());
							c = r.getCell(cRef.getCol());
							double minorVersion = c.getNumericCellValue();
							workbook.close();

							System.out.println("majorVersion = " + majorVersion);
							System.out.println("minorVersion = " + minorVersion);
*/
						//String downloadUrl = GetSystemProperty("BOARDWALK_SERVER_URL");
						url = req.getRequestURL().toString();
						searchSlash = "/";

						int slashIndex = url.lastIndexOf(searchSlash);
						if (slashIndex > 0) {
							url = url.substring(0, slashIndex + 1);		//including that slash at the end
						}

						//NOT ADDING DisplayDocument?id=BLOBID PART
						System.out.print("url : " + url);
						downloadUrl = url ;							// + "/DisplayDocument?id=" + thisBlobId ;   //+ "&nhHierarchy=" + nhHierarchy;

						String imageUrl ;
						imageUrl = url + "images/exicon.gif";

					//	System.out.print("downloadUrl : " + downloadUrl);
					//	System.out.print("imageUrl : " + imageUrl);


						//nhHierarchy = nh0Name + "|" + nh1Name + "|" + nh2Name + "|" + nh3Name;
//	public void RunImportUserTemplateRoutines(Connection connection, String templateUploadedBy, String templateName, String templateVersion, String templateSize, String downloadUrl, int newBlobId, String nh0Name, String nh1Name, String nh2Name, String nh3Name, String imageUrl, String nhHierarchy)

						writeLogline(thisCommand, commandLine, MESSAGE_TYPE_STATUS, "Status : Importing User-Role Template Routines started [" + templateName + "].");							
							String spLog = null;
							spLog =	RunImportUserTemplateRoutines(connection, migrationUserName, templateName, templateMajorVersion, templateMinorVersion, templateSize, downloadUrl, thisBlobId, nh0Name, nh1Name, nh2Name, nh3Name, imageUrl);
						writeLogline(thisCommand, commandLine, MESSAGE_TYPE_STATUS, "Stored Procedure Log : [" + templateName + "]." + spLog);							
						writeLogline(thisCommand, commandLine, MESSAGE_TYPE_STATUS, "Status : Importing User-Role Template Routines complete [" + templateName + "].");							

						break;


					case "importDefaultTemplate":
						System.out.println("RUNNING ............. importDefaultTemplate");
//ROLE=Governance|TEMPLATE=Velocity Governance Template|UPDATE_CUBOID_HIERARCHY=NO|TARGET_COLLAB_NAME=|TARGET_WB_NAME=|BLOB_ID=4|MANIFEST_PATH=AcmeBank^^^/Governance/Manifest/GovernanceManifest|ACTIVE=True|COMMAND=importDefaultTemplate|TaskName=Migrate Default Template|Sequence=24|TEMPLATE_FILE=Velocity Governance Template.xlsm
//ACTIVE=True|COMMAND=importDefaultTemplate|TaskName=Migrate Default Template|Sequence=24|TEMPLATE_FILE=Velocity Governance Template.xlsm

						String roleName;
						templateSize = "";
						String updateCuboidHierarchy, targetCollabName, targetWbName, manifestPath;
						boolean active;

						//nh0Name = commandParamMap.get("NH0_NAME") != null ? commandParamMap.get("NH0_NAME").toString() : "";

						roleName = commandParamMap.get("ROLE");
						templateNameNoExt = commandParamMap.get("TEMPLATE");
						updateCuboidHierarchy = commandParamMap.get("UPDATE_CUBOID_HIERARCHY");
						targetCollabName = commandParamMap.get("TARGET_COLLAB_NAME");
						targetWbName = commandParamMap.get("TARGET_WB_NAME");
						manifestPath = commandParamMap.get("MANIFEST_PATH") != null ? commandParamMap.get("MANIFEST_PATH").toString() : "";
						manifestPath = manifestPath.replace("^", "|");
 
						templateFile = commandParamMap.get("TEMPLATE_FILE");
						blobId = Integer.parseInt(commandParamMap.get("BLOB_ID"));
						active = Boolean.valueOf(commandParamMap.get("ACTIVE"));

						templateFilePath = filePathMap.get(templateFile);

					//	System.out.println("blobId : " + blobId);
					//	System.out.println("templateFilePath : " + templateFilePath);

						writeLogline(thisCommand, commandLine, MESSAGE_TYPE_STATUS, "Status : Saving Default Template Blob into Database started [" + templateNameNoExt + "].");							
							int newBlobId = -1;
							newBlobId = SaveTemplateAsBlobIntoDatabase(connection, templateNameNoExt, templateFilePath);
						writeLogline(thisCommand, commandLine, MESSAGE_TYPE_STATUS, "Status : Saving Default Template Blob into Database complete [" + templateNameNoExt + "].");						

						//Reading File Information
						dtfile = new File(templateFilePath);
						bytes = dtfile.length();
						kilobytes = (bytes / 1024);
						megabytes = (kilobytes / 1024);

						ext = "";
						dotIndex = templateFilePath.lastIndexOf('.');
						if (dotIndex > 0) 
						{
							ext = templateFilePath.substring(dotIndex + 1);
						}
					
						fileName = templateNameNoExt + "." + ext;
						contentType = ext + " File";
						fileSize = megabytes + " MB";

				//		System.out.println("fileName : " + fileName);
				//		System.out.println("ext : " + ext);
				//		System.out.println("contentType : " + contentType);
				//		System.out.println("filesize : " + fileSize);

						// ADD OBJECT MAP OF newBlobId vs blobId

						templateSize = fileSize;
						templateType = contentType;

						//String downloadUrl = GetSystemProperty("BOARDWALK_SERVER_URL");
						url = req.getRequestURL().toString();
						searchSlash = "/";

						slashIndex = url.lastIndexOf(searchSlash);
						if (slashIndex > 0) {
							url = url.substring(0, slashIndex);
						}

//-- for (user templte 
//  "http://localhost:800/bw_velocity_dev/"
// for default tempalte
//  "http://localhost:800/bw_velocity_dev/displaydocuent?id-sdsf,nhhierarchy=a"
						System.out.print("url : " + url);
						downloadUrl = url + "/DisplayDocument?id=" + newBlobId ;  // Check if nhhierarchy needed in DOWNLOAD LINK FOR DEFAULT TEMPLATE + "&nhHierarchy=" + nhHierarchy;
						imageUrl = url + "/images/exicon.gif";

				//		System.out.print("downloadUrl : " + downloadUrl);
				//		System.out.print("imageUrl : " + imageUrl);

						//String nhRootHierarchy = "<ROOT>|||";

						RunImportDefaultTemplateRoutines(connection, templateNameNoExt, templateType, migrationUserName,  newBlobId, templateSize, downloadUrl, imageUrl);

						break;

					case "importSQLData":
						System.out.println("RUNNING   26............. importSQLData");
//TABLE_NAME=BW_TEMPLATE_VERSION|INSERT_SQL=INSERT INTO BW_TEMPLATE_VERSION( [NAME], [MAJOR_VERSION], [MINOR_VERSION]) VALUES ('ProcessManagement Template','3','5')|ALL_KEYS=NAME^MAJOR_VERSION^MINOR_VERSION|PK_KEYS=|COMMAND=importSQLData|TaskName=Migrate SQL Data|Sequence=26

						String sqlTableName, insertSQLStmt, allKeys, primaryKeys; 
						sqlTableName = commandParamMap.get("TABLE_NAME");
						insertSQLStmt = commandParamMap.get("INSERT_SQL");
						allKeys = commandParamMap.get("ALL_KEYS");
						primaryKeys = commandParamMap.get("PK_KEYS") != null ? commandParamMap.get("PK_KEYS").toString() : "";
						primaryKeys = primaryKeys.replace("^", "|");

				//		System.out.println("sqlTableName : " + sqlTableName);
				//		System.out.println("insertSQLStmt : " + insertSQLStmt);
				//		System.out.println("allKeys : " + allKeys);
				//		System.out.println("primaryKeys : " + primaryKeys);

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


	//Read Source RowId/ColumnId Mapping file into Hash Table
	public void readLinkImportMapFile(String linkImportMapFile)
	{
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

			sourceRowIds = new ArrayList();
			sourceColumnIds  = new ArrayList();
			sourceColumnNames  = new ArrayList();

			while((line=br.readLine())!=null)  
			{  
				//System.out.println("Line : " + line);
				String[] lineArr = line.split(Seperator);

				objectId = Integer.parseInt(lineArr[0]);
				objectName = lineArr[1];
				cuboidId = Integer.parseInt(lineArr[2]);
				sequence = Integer.parseInt(lineArr[3]);
				objectType = lineArr[4];
				order = lineArr[5];

				//System.out.println("objectId : " + objectId);

				switch (objectType)
				{
					case	"BW_ROW"	:
						sourceSequenceRowIdMap.put(sequence, objectId);
						sourceRowIds.add(objectId);
						break;

					case	"BW_COLUMN"	:
						sourceSequenceColIdMap.put(sequence, objectId);
						sourceSequenceColumnNameMap.put(sequence, objectName);
						sourceColumnIds.add(objectId);
						sourceColumnNames.add(objectName);
						break;
				}
				//sb.append(line);      //appends line to string buffer  
				//sb.append("\n");     //line feed   
			}  
			fr.close();    //closes the stream and release the resources  
			//System.out.println("Contents of File: ");  
			//System.out.println(sb.toString());   //returns a string that textually represents the object  
		}  
		catch(IOException e)  
		{  				
			e.printStackTrace();  
		}  
	}

	//Insert External Query Entries into Target Dataser
	public void insertExternalQueryForMigration(Connection connection, int extQueryId, String extQueryDesc, String extQueryCall, String extQueryParams) throws SQLException
	{
		CallableStatement callablestatement = null;
		try
		{
			callablestatement = connection.prepareCall(CALL_BW_INSERT_EXT_QUERIES_FOR_MIGRATION);
			callablestatement.setInt(1, extQueryId);
			callablestatement.setString(2, extQueryDesc);
			callablestatement.setString(3, extQueryCall);
			callablestatement.setString(4,extQueryParams);
			callablestatement.execute();
			//writeLogline("ObjectMap", objectType, MESSAGE_TYPE_OBJECT_MAP_UPDATE, "Success : Updating Migration Object Map [ObjectType:" + objectType + "] [SourceObjectID: " + sourceObjectId + "] [TargetObjectID: " + targetObjectId + "] TxId: [" + tId + "]");
		}
		catch (SQLException sql1)
		{
			//writeLogline("ObjectMap", objectType, MESSAGE_TYPE_EXCEPTION, "Failure : Updating Migration Object Map [ObjectType:" + objectType + "] [SourceObjectID: " + sourceObjectId + "] [TargetObjectID: " + targetObjectId + "] TxId: [" + tId + "]. Reason : " + sql1.getMessage());
			System.out.println("Failed to Insert External Query for Migration");
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


	//Read DDL Of STored Procedure from file name and Execute it on Database
	public void readSqlDDLfromFileAndExecuteCommand(Connection connection, String objectName, String createDDLFilePathname, String objectType)
	{
		//System.out.println("inside readSqlDDLfromFileAndExecuteCommand : objectName : " + objectName);
		//System.out.println("inside readSqlDDLfromFileAndExecuteCommand : createDDLFilePathname : " + createDDLFilePathname);
		//System.out.println("inside readSqlDDLfromFileAndExecuteCommand : objectType : " + objectType);

		Statement cstmt = null;
		Path filePath = Paths.get(createDDLFilePathname);
		String fileContent = "";

		try
		{
			byte[] content = Files.readAllBytes(Paths.get(createDDLFilePathname));
			System.out.println(new String(content));			
			fileContent = new String(content);

			System.out.println("create stored procedure lines ....");
			System.out.println("fileContent : " + fileContent);

			connection.setAutoCommit(false); //Now, transactions won't be committed automatically.

			try
			{
				dropSqlObjectIfExistsBeforeMigration(objectName, objectType);

				cstmt = connection.createStatement();

				cstmt.addBatch(fileContent);		//Create SP Stmt / Create Table stmt
				cstmt.executeBatch();
				System.out.println("Drop and Create Batch Statement executed for ObjectType : " + objectType + ". objectName : "  + objectName );
			}
			catch (Exception e)
			{
				System.out.println("Error while Deleting ObjectType : " + objectType + ". objectName : " + objectName );
				e.printStackTrace();
			}
			connection.commit(); //commit all the transactions
		} 
		catch(SQLException sqlException) 
		{
            sqlException.printStackTrace();
        } 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		finally 
		{
            try 
			{
                if(cstmt != null) 
                    cstmt.close();    // Close Statement Object
            }
            catch (SQLException sqlException) 
			{
                sqlException.printStackTrace();
            }
		}
	}

	//Drop Stored Procedure or SQL Table if Exists on Target Database before Re-Creating using Migration Process
	public void dropSqlObjectIfExistsBeforeMigration(String objectName, String objectType) throws SQLException
	{
		//System.out.println("Inside dropSqlObjectIfExistsBeforeMigration : ");
		//System.out.println("objectType  : " + objectType);
		//System.out.println("objectName : " + objectName);

		CallableStatement callablestatement = null;

		try
		{
			callablestatement = connection.prepareCall(CALL_BW_DROP_OBJECT_BEFORE_MIGRATION);
			callablestatement.setString(1, objectName);
			callablestatement.setString(2, objectType);
			callablestatement.execute();
		}
		catch (SQLException sql1)
		{
			System.out.println("Delete Object failed : " + sql1.getMessage());
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


	public void readCVSFileintoSqlTable(String CVSDataFilePathname, String InsertSQLStatementCall)
	{
		//System.out.println("inside readCVSFileintoSqlTable ,,,,, CVSDataFilePathname : " + CVSDataFilePathname);
		//System.out.println("inside readCVSFileintoSqlTable ,,,,, InsertSQLStatementCall : " + InsertSQLStatementCall);

		PreparedStatement pstmt		= null;

		String currentDatabase = null;

		try
		{

			if (InsertSQLStatementCall.equals(CALL_BW_INSERT_SQL_BRECTDEFINITION))
			{
				currentDatabase = getCurrentDatabaseName();
				//System.out.println("currentDatabase : " + currentDatabase);
			}
			
			// Open the file
			FileInputStream fstream = new FileInputStream(CVSDataFilePathname);
			BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
			String strLine;

			//Read File Line By Line
			while ((strLine = br.readLine()) != null)   
			{
				// Print the content on the console
				System.out.println ("Before databasename replacement : " + strLine);
				if (InsertSQLStatementCall.equals(CALL_BW_INSERT_SQL_BRECTDEFINITION))
				{
					strLine = strLine.replace("{TARGET_DATABASE_NAME}",currentDatabase);
				}
				//System.out.println ("After databasename replacement : " + strLine);
		
				String[] linesArray =  strLine.split(ContentDelimeter);
				String currentLine = null;
				try
				{
					int batchCount = 0;

//					System.out.println("Header Line of " + CVSDataFilePathname + "  ..........");
//					System.out.println(linesArray[0]);

				//	System.out.println("Number OF LINES ............." + linesArray.length);

				//	System.out.println("Calling " + InsertSQLStatementCall + " in Batch mode.........");
					//pstmt = null;
					//stmt = connection.prepareStatement(CALL_BW_INSERT_SQL_BRECTDEFINITION);
				
				//	System.out.println("BEFORE making prepared Statement : " + InsertSQLStatementCall);
					pstmt = connection.prepareStatement(InsertSQLStatementCall);
				//	System.out.println("AFTER making prepared Statement : " + InsertSQLStatementCall);

					pstmt.clearBatch();
				//	System.out.println("AFTER CLEARBARCH  " + InsertSQLStatementCall);
							
					for (int i = 1; i < linesArray.length; i++)
					{
						currentLine = linesArray[i];
//						System.out.println("currentLine : " + currentLine);
//						System.out.println("currentLine isEmpty : " + currentLine.isEmpty());

						String[] arr = currentLine.split(Seperator);   

				//		System.out.println("arr.lenght : " + arr.length);

/*						System.out.println("arr[0] : " + arr[0] +  "->"  + arr[0].isEmpty());
						System.out.println("arr[1] : " + arr[1] +  "->"  + arr[1].isEmpty());
						System.out.println("arr[2] : " + arr[2] +  "->"  + arr[2].isEmpty());
						System.out.println("arr[3] : " + arr[3] +  "->"  + arr[3].isEmpty());
						System.out.println("arr[4] : " + arr[4] +  "->"  + arr[4].isEmpty());
						System.out.println("arr[5] : " + arr[5] +  "->"  + arr[5].isEmpty());
						System.out.println("arr[6] : " + arr[6] +  "->"  + arr[6].isEmpty());
						System.out.println("arr[7] : " + arr[7] +  "->"  + arr[7].isEmpty());
						System.out.println("arr[8] : " + arr[8] +  "->"  + arr[8].isEmpty());
						System.out.println("arr[9] : " + arr[9] +  "->"  + arr[9].isEmpty());
*/

						for (int j = 0; j < arr.length; j++)
						{
				//			System.out.println("arr[j] : " + arr[j] +  "->"  + arr[j].isEmpty());
							pstmt.setString(j+1, arr[j]);			//ROOT NH
						}


//						stmt.setString(1, arr[0]);			//ROOT NH
//						stmt.setString(2, arr[1]);				//BUSINESS UNIT
//						stmt.setString(3, arr[2]);			//APP
//						stmt.setString(4, arr[3]);			//BRECTNAME
//						stmt.setString(5, arr[4]);			//DATABASE
//						stmt.setString(6, arr[5]);			//TYPE
//						stmt.setString(7, arr[6]);			//NAME
//						stmt.setString(8, arr[7]);			//WHITEBOARD
//						stmt.setString(9, arr[8]);			//COLLABORATION
//						stmt.setString(10, arr[9]);			//NEIGHBORHOOD
		 
				//		System.out.println("Adding to batch....");
						pstmt.addBatch();
						batchCount = batchCount + 1;
					}

				//	System.out.println("Executing batch....started");
					//stmt.execute();
					int[] rescnt = pstmt.executeBatch();
					pstmt.clearBatch();
					pstmt.close();
					pstmt = null;

				//	System.out.println("Executing batch....complete");

				//	System.out.println("batchCount = " + batchCount);

				}
				catch (SQLException sql1)
				{
					System.out.println(sql1.getMessage());
					System.out.println("Failed to Insert " + InsertSQLStatementCall);
					//throw sql1;
				}
			}

			//Close the input stream
			fstream.close();
		}
		catch (IOException ioe)
		{
			System.out.println("IOException thrown " );
			ioe.printStackTrace();  
		}
//		System.out.println("End of readCVSFileintoSqlTable........");
	}


	public void readLinkImportBufferfFile(int targetCuboidId, String filePathName, String linkImportMapFile, String migrateWhat)
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

			processLinkImportBuffer(targetCuboidId, migrateWhat);

			


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
	//	System.out.println("sb");
	//	System.out.println(sb.toString());
		//return sb;

		//reader = new BufferedReader(new InputStreamReader(zipIn, "UTF-8"));
//		reader = new BufferedReader(sb.toString());

//		processHeader(reader.getNextContent());

	}


	// For LinkImport RowID ColumnID Mapping is updated 
	public void updateRowIdColumnIdMigrationMap(String cuboidName, String sourceServer, String targetServer, int sourceCuboidId, int targetCuboidId , String packageName)
	{

		int batchCount = 0;
	//	System.out.println("Inside updateRowIdColumnIdMigrationMap....");

	//	System.out.println("numRows  ...." + numRows);
	//	System.out.println("numRowsToMigrate  ...." + numRowsToMigrate);
		//System.out.println("Rows To Migrate: "  + migratedRowIds.size() ); DONT UNCOMMENT

		for (int ri = 0; ri < numRowsToMigrate ; ri++)
		{
		//	System.out.println("Target RowID : "  + migratedRowIds.get(ri) );
		}

		//System.out.println("numColumns  ...." + numColumns);
		//System.out.println("numColumnsToMigrate  ...." + numColumnsToMigrate);
		//System.out.println("Columns To Migrate: "  + migratedColumnIds.size() );

		for (int i = 0; i < numColumnsToMigrate; i++)
		{
			int columnId = ((Integer)migratedColumnIds.get(i)).intValue();
		//	System.out.println("Target ColumnID : "  + columnId ); 
		}

		//System.out.println("Printing Source-Target RowIdMap............");

		for (int ri = 0; ri < numRowsToMigrate; ri++)
		{
			int Sequence = ri+1;
		//	System.out.println("Sequence : " + Sequence + " ........ Source RowID : "  + sourceRowIds.get(ri) + " ........ Target RowID : "  + migratedRowIds.get(ri) );
			//responseToUpdate.append(rowIds.get(ri) + Seperator);
		}


//		System.out.println("Printing Source-Target ColumnIdMap............");
//		System.out.println("Using index");
		
		for (int i = 0; i < numColumnsToMigrate; i++)
		{
			int Sequence = i+1;
//			System.out.println("Sequence : " + Sequence + " : " +   sourceColumnNames.get(i) + " ........ Source ColID : "  + sourceColumnIds.get(i) + " ........ Target ColumnID : "  + migratedColumnIds.get(i) );
		}

		try
		{
			
			writeLogline("ObjectMap", "ColumnID", MESSAGE_TYPE_OBJECT_MAP_UPDATE, "Success : Updating ColumnId Object Map started.");							

			int[] rescnt ;
//			System.out.println("Calling CALL_ADD_CUBOID_ROWID_COLUMN_ID_MAP_FOR_MIGRATION in Batch mode.........");
			stmt = null;
			stmt = connection.prepareStatement(CALL_ADD_CUBOID_ROWID_COLUMN_ID_MAP_FOR_MIGRATION);
			stmt.clearBatch();
					
			for (int i = 0; i < numColumnsToMigrate; i++)
			{
				int Sequence = i+1;
//				System.out.println("sourceColumnNames.get(i) " + sourceColumnNames.get(i));
				stmt.setString(1, sourceColumnNames.get(i).toString());		//objectName
				stmt.setString(2, cuboidName);
				stmt.setString(3, sourceServer);
				stmt.setString(4, targetServer);
				stmt.setInt(5, sourceCuboidId);
				stmt.setInt(6, targetCuboidId);
				stmt.setInt(7, sourceColumnIds.get(i));				//sourceObjectId
				stmt.setInt(8, migratedColumnIds.get(i));			//targetObjectId
				stmt.setString(9, "BW_COLUMN");
				stmt.setInt(10, Sequence);
				stmt.setString(11, packageName);
				stmt.addBatch();
				batchCount = batchCount + 1;
				//Connection connection, String cuboidName, String sourceServer, String targetServer, int sourceCuboidId, int targetCuboidId , String packageName
			}

			stmt.execute();
			rescnt = stmt.executeBatch();
			stmt.clearBatch();
			writeLogline("ObjectMap", "ColumnID", MESSAGE_TYPE_OBJECT_MAP_UPDATE, "Success : Updating ColumnId Object Map complete.");							

			//For Migration of Cuboid as LI_ONLY_STRUCTURE there are no Rows migrated.so numRowsToMigrate is ZERO
			if (numRowsToMigrate == 0)
			{
				writeLogline("ObjectMap", "RowID", MESSAGE_TYPE_OBJECT_MAP_UPDATE, "Success : Updating RowId Object Map started.");							

				for (int ri = 0; ri < numRowsToMigrate; ri++)
				{
					int Sequence = ri+1;
					//int Sequence = ri+1;
					//System.out.println("Sequence : " + Sequence + " ........ Source RowID : "  + sourceRowIds.get(ri) + " ........ Target RowID : "  + migratedRowIds.get(ri) );
					stmt.setString(1, "");		//objectName
					stmt.setString(2, cuboidName);
					stmt.setString(3, sourceServer);
					stmt.setString(4, targetServer);
					stmt.setInt(5, sourceCuboidId);
					stmt.setInt(6, targetCuboidId);
					stmt.setInt(7, sourceRowIds.get(ri));				//sourceObjectId
					stmt.setInt(8, migratedRowIds.get(ri));				//targetObjectId
					stmt.setString(9, "BW_ROW");
					stmt.setInt(10, Sequence);
					stmt.setString(11, packageName);
					stmt.addBatch();
					batchCount = batchCount + 1;

					//Connection connection, String cuboidName, String sourceServer, String targetServer, int sourceCuboidId, int targetCuboidId , String packageName
				}
				
				stmt.execute();
				rescnt = stmt.executeBatch();
				stmt.clearBatch();
	
				writeLogline("ObjectMap", "RowID", MESSAGE_TYPE_OBJECT_MAP_UPDATE, "Success : Updating RowId Object Map complete.");							
			}

			stmt.close();
			stmt = null;

//			System.out.println("batchCount = " + batchCount);

		}
		catch (SQLException sql1)
		{
			System.out.println("Failed to Insert Cuboid RowId ColumnId Migration Map");
			//throw sql1;
		}
		
		sourceColumnIds = new ArrayList<Integer>();
		sourceColumnNames = new ArrayList<String>();
		sourceRowIds = new ArrayList<Integer>();

		migratedColumnIds = null;
		migratedRowIds = null;

	}


	public void writePackageDeploymentLog(int deployedPackagetId, String packageName, String logLines )
	{
		String commandRunDate, command , param, messageType, logData ;
		String[] logline = logLines.split(ContentDelimeter);
		String currentLine = null;
		try
		{
			int batchCount = 0;

//			System.out.println("Calling CALL_BW_INSERT_PACKAGE_DEPLOYMENT_LOG in Batch mode.........");
			stmt = null;
			stmt = connection.prepareStatement(CALL_BW_INSERT_PACKAGE_DEPLOYMENT_LOG);
			stmt.clearBatch();
					
			for (int i = 0; i < logline.length; i++)
			{
				currentLine = logline[i];
				//System.out.println("currentLine : " + currentLine);
				String[] arr = currentLine.split(Seperator);   //dtf.format(now) + Seperator + command  + Seperator  +  param + Seperator  +  logMessage + PipeDelimeter 

				stmt.setInt(1, deployedPackagetId);			//deployedpackageId
				stmt.setString(2, packageName);				//packageName
				stmt.setString(3, arr[0]);	
				stmt.setString(4, arr[1]);
				stmt.setString(5, arr[2]);
				stmt.setString(6, arr[3]);
 				stmt.setString(7, arr[4]);
 
				stmt.addBatch();
				batchCount = batchCount + 1;
			}

			stmt.execute();
			int[] rescnt = stmt.executeBatch();
			stmt.clearBatch();
			stmt.close();
			stmt = null;

//			System.out.println("batchCount = " + batchCount);

		}
		catch (SQLException sql1)
		{
			System.out.println("Failed to Write Package Deployment Log");
			//throw sql1;
		}
	}


	//Process LinkImport Buffer to insert Column and Rows into Cuboid 
	public void processLinkImportBuffer(int targetCuboidId, String migrateWhat)
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
			//System.out.println("Deadlock parameters not set. Using defaults...");
		}

		String failureReason = "";
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


			//	System.out.println("userId : " + userId);
			//	System.out.println("migrationUserId : " + migrationUserId);
			//	System.out.println("targetCuboidCreatorUserId : " + targetCuboidCreatorUserId);

				tm = new TransactionManager(connection, targetCuboidCreatorUserId);			//userId is replaced with migrationUserId OR targetCuboidCreatorUserId by Rahul on 02-JUNE-2022
				tid = tm.startTransaction("Link export new table", "Link export new table");

				//processColumns(fullTableArr[1]);
				String columns = getNextContent();
				
				if(canAdministerColumns)
				{
					processColumns(columns, targetCuboidId);
//					System.out.println("Column are created Successfully in Cuboid : " + targetCuboidId); 
				}

				if (migrateWhat.equals("LI_ONLY_STRUCTURE"))
				{
					//Only Columns need to be created. Hence No need to process rows
//					System.out.println("migrateWhat : " + migrateWhat); 
//					System.out.println("No Rows are Created on TargetCuboid"); 
					numRows = 0;

					//Added by Rahul for Migration
					writeLogline("ObjectMap", "ColumnID", MESSAGE_TYPE_STATUS, "Status : Updating ColumnId Object Map started (LI_ONLY_STRUCTURE).");							
						updateRowIdColumnIdMigrationMap(cuboidName, sourceServer, targetServer, sourceCuboidId, targetCuboidId , packageName);
					writeLogline("ObjectMap", "ColumnID", MESSAGE_TYPE_STATUS, "Status : Updating ColumnId Object Map complete (LI_ONLY_STRUCTURE).");							

				}
				else if (migrateWhat.equals("LI_STRUCTURE_AND_DATA"))
				{
					// Add rows
					//System.out.println("migrateWhat : " + migrateWhat); 
					//System.out.println("Structure and Data created on Server"); 
					//System.out.println("xlMigrationServiceExt:service() : userId = " + userId);
					//System.out.println("xlMigrationServiceExt:service() : migrationUserId = " + migrationUserId);
					//System.out.println("xlMigrationServiceExt:service() : targetCuboidCreatorUserId = " + targetCuboidCreatorUserId);
					//System.out.println("xlMigrationServiceExt:service() : targetCuboidId = " + targetCuboidId);
					//System.out.println("xlMigrationServiceExt:service() : tid = " + tid);
					//System.out.println("xlMigrationServiceExt:service() : numRows = " + numRows);
					//System.out.println("xlMigrationServiceExt:service() : numColumns = " + numColumns);
					if (numRows > 0)
					{
						if (canAdministerColumns && canAddRows)
							rowIds = TableManager.createRowsNewTable(connection, targetCuboidId, tid, targetCuboidCreatorUserId , numRows);			// userId is replaced with migrationUserId OR targetCuboidCreatorUserId by Rahul on 02-JUNE-2022
//							System.out.println("xlMigrationServiceExt: Time to create rows = " + getElapsedTime());
						
							migratedRowIds = (ArrayList)rowIds.clone();		//	Added by Rahul to Store/Process Migration Row Ids on Target Server
							numRowsToMigrate = migratedRowIds.size();		//	Added by Rahul to Store/Process Migration Row Ids on Target Server

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

							query = "{CALL BW_UPD_CELL_FROM_RCSV_LINK_EXPORT(?,?,?)}";
							CallableStatement cstmt = connection.prepareCall(query);
							cstmt.setInt(1, tid);
							cstmt.setInt(2, targetCuboidId);
							cstmt.setInt(3, targetCuboidCreatorUserId);				// userId replaced with migrationUserId OR targetCuboidCreatorUserId by Rahul on 02-JUNE-2022 
							int updCount = cstmt.executeUpdate();
							cstmt.close();
							cstmt = null;
							System.out.println("xlMigrationServiceExt: Time to execute BW_UPD_CELL_FROM_RCSV_LINK_EXPORT = " + getElapsedTime());

						//Added by Rahul for Migration
						writeLogline("ObjectMap", "RowID,ColumnID", MESSAGE_TYPE_OBJECT_MAP_UPDATE, "Success : Updating RowId, ColumnId Object Map started.");							
						updateRowIdColumnIdMigrationMap(cuboidName, sourceServer, targetServer, sourceCuboidId, targetCuboidId , packageName);
						writeLogline("ObjectMap", "RowID,ColumnID", MESSAGE_TYPE_OBJECT_MAP_UPDATE, "Success : Updating RowId, ColumnId Object Map complete.");							
					}

				}

				// commit the transaction
				tm.commitTransaction();
				tm = null;

				// create the response
				responseToUpdate.append("Success" + Seperator);
				responseToUpdate.append(numColumns + Seperator);
				responseToUpdate.append(numRows + Seperator);
				responseToUpdate.append(tid + ContentDelimeter);

				//responseToUpdate.append(tableId + ContentDelimeter + memberId + ContentDelimeter);
				responseToUpdate.append(targetCuboidId + ContentDelimeter + targetCuboidCreatorMemberId + ContentDelimeter);

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
						 
						//failureReason = (new xlErrorNew(tableId, 0, 0, 13001)).buildTokenString();
						failureReason = (new xlErrorNew(targetCuboidId, 0, 0, 13001)).buildTokenString();
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
//					connection.close();			commented by Rahul on 12-march-2022
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}

		// The response
		if (failureReason.length() == 0)
		{
			responseBuffer = responseToUpdate.toString();
			//commitResponseBuffer(responseBuffer, response);
//			System.out.println("xlMigrationServiceExt: Success : responseBuffer = " + responseBuffer);
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


	//Insert BW_COLUMN table to Create Cuboid Columns
	public void processColumns(String columnInfo, int targetCuboidId) throws BoardwalkException, SQLException
	{
		// If the user has access to add new Columns then procede forward
//		if(canAdministerColumns)
//		{
			columnIds = new ArrayList(numColumns);
			migratedColumnIds = new ArrayList(numColumns);		//Added by Rahul for Migration of Cuboid data on 10-March-2022

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
//		}

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
			migratedColumnIds.add(new Integer(columnId));				//Added by Rahul for Migration of Cuboid data on 10-March-2022
			//columnHash.put (new Integer(columnIdx), new Integer(columnId));
			//System.out.println("columnid = " + columnId);
		}
		stmt.close();
		stmt = null;
		resultset.close();
		resultset = null;

		numColumnsToMigrate = columnIds.size();		//Added by Rahul to Store/Process Migration Column Ids on Target Server
	}



	//This Function Check the User Access on Cuboid if user can canAddRows, canDeleteRows , canAdministerColumns	
	public void processHeader(String header, int targetCuboidId) throws BoardwalkException, SQLException, SystemException
	{

		//System.out.println("header = " + header);

		String[] headerInfo = header.split(Seperator);

		//Modified by Tekvision on 20180207 for Clear Text Password(Issue Id: 14241) - START
		userId				= Integer.parseInt(headerInfo[0]);
		userId				= Integer.parseInt(headerInfo[0]);
		userName			= headerInfo[1];
		//userPassword        = headerInfo[2];
		memberId			= Integer.parseInt (headerInfo[2]);
		tableId				= Integer.parseInt (headerInfo[3]);
		nhId				= Integer.parseInt (headerInfo[4]);
		numColumns			= Integer.parseInt(headerInfo[5]);
		numRows				= Integer.parseInt (headerInfo[6]);
		view = "LATEST";

//nhMembershipId
//

//		System.out.println("source server UserId : " + userId);
//		System.out.println("source server CuboidId : " + tableId);
//		System.out.println("source server MemberId : " + memberId);
//		System.out.println("source server nhId : " + nhId);

//		System.out.println("target server UserId : " + targetCuboidCreatorUserId);
//		System.out.println("target server CuboidId : " + targetCuboidId);
//		System.out.println("target server MemberId : " + targetCuboidCreatorMemberId);
//		System.out.println("target server nhId : " + targetCuboidCreatorNhId);

		userId = targetCuboidCreatorUserId;
		memberId = targetCuboidCreatorMemberId;
		tableId = targetCuboidId;
		nhId = targetCuboidCreatorNhId;

		//	Access control checks
		TableInfo tinfo = TableManager.getTableInfo(connection, userId, tableId);
		TableAccessList ftal = TableViewManager.getSuggestedAccess(connection, tinfo, userId, memberId, nhId);

		// Check access control :: TBD
		int raccess = 1;
		int ACLFromDB = ftal.getACL();
		TableAccessRequest wAccess = new TableAccessRequest(tableId, "LATEST", true);
		int wACL = wAccess.getACL();
		int awACL = wACL & ACLFromDB;

		canAddRows				= ftal.canAddRow();
		canDeleteRows			= ftal.canDeleteRow();
		canAdministerColumns	= ftal.canAdministerColumn();

//		System.out.println("canAddRows : " + canAddRows);
//		System.out.println("canDeleteRows : " + canDeleteRows);
//		System.out.println("canAdministerColumns : " + canAdministerColumns);
	}



	//Add Relation to Target Server
	public void AddRelationByMigrationProcess(Connection connection, String relationName, String sourceNhName, String targetNhNames, int txId) throws SQLException
	{

//		System.out.println("Inside AddRelationByMigrationProcess: ");
//		System.out.println("relationName  : " + relationName);
//		System.out.println("sourceNhNames : " + sourceNhName);
//		System.out.println("targetNhNames : " + targetNhNames);

		CallableStatement callablestatement = null;

		try
		{
			callablestatement = connection.prepareCall(CALL_BW_CR_RELATION_USING_NH_PATH_FOR_MIGRATION);
			callablestatement.setString(1, relationName);
			callablestatement.setString(2, sourceNhName);
			callablestatement.setString(3, targetNhNames);
			callablestatement.setInt(4, txId);

			callablestatement.execute();
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
	}



	//CALL_ADD_DEPLOYED_PACKAGES="{CALL BW_ADD_DEPLOYED_PACKAGES(?,?,?,?)}";
	//	@PACKAGE_NAME VARCHAR(512), 
	//	@SOURCE_SERVER VARCHAR(512), 
	//	@PACKAGE_CREATION_DATE DATETIME,
	//	@PACKAGE_ID INTEGER OUTPUT

	public  int AddDeployedPackageInformation(Connection connection, String packageName, String sourceServer, String packageCreationDate) throws SQLException
	{

//		System.out.println("packageCreationDate : " + packageCreationDate);
		long localTimeAfter1970 = Long.parseLong(packageCreationDate);
		java.util.Date date_ = new java.util.Date(localTimeAfter1970);
//		System.out.println("date_ : " + date_);

		java.sql.Date sqlPackageDate = new java.sql.Date(date_.getTime());
//		System.out.println("sqlPackageDate : " + sqlPackageDate);

		String packageInfo;
		StringBuffer sb = new StringBuffer();

		//HashMap <String, String> packageInfoMap;

//		System.out.println("Iterating Hashmap...");  
		for(Map.Entry m : packageInfoMap.entrySet()){    
			System.out.println(m.getKey()+"="+m.getValue());    
			sb.append(m.getKey()+"="+m.getValue() + "|");
		}  

		if (sb.length() > 0)
		{
			//System.out.println("Removing last character from SB");
			sb.deleteCharAt(sb.length()-1);
		}
		packageInfo = sb.toString();

		CallableStatement callablestatement = null;
		int deployedId = -1;
		try
		{
			callablestatement = connection.prepareCall(CALL_ADD_DEPLOYED_PACKAGES);
			callablestatement.setString(1, packageName);
			callablestatement.setString(2, sourceServer);
			callablestatement.setDate(3, sqlPackageDate);
			callablestatement.setString(4, packageInfo);
			callablestatement.registerOutParameter(5, java.sql.Types.INTEGER);

			callablestatement.execute();
			deployedId = callablestatement.getInt(5);
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








//CREATE PROCEDURE BW_ADD_CUBOID_ROWID_COLUMN_ID_MAP_FOR_MIGRATION 
//	@OBJECT_NAME VARCHAR(512) , 
//	@CUBOID_NAME VARCHAR(512) , 
//	@SOURCE_SERVER VARCHAR(512) , 
//	@TARGET_SERVER  VARCHAR(512) , 
//	@SOURCE_CUBOID_ID INT , 
//	@TARGET_CUBOID_ID INT , 
//	@SOURCE_OBJECT_ID INT , 
//	@TARGET_OBJECT_ID INT , 
//	@OBJECT_TYPE VARCHAR(512) , 
//	@SEQUENCE_NUMBER INT ,
//	@PACKAGE_NAME VARCHAR(512) 

/*
	public static void AddCuboidRowIdColumnIdMapForMigration(Connection connection, String objectName, String cuboidName,
					String sourceServer, String targetServer, int sourceCuboidId, int targetCuboidId, 
						int sourceObjectId, int targetObjectId, String objectType, int sequenceNumber, 
								String packageName) throws SQLException
	{
		CallableStatement callablestatement = null;
		try
		{
			callablestatement = connection.prepareCall(CALL_ADD_CUBOID_ROWID_COLUMN_ID_MAP_FOR_MIGRATION);
			callablestatement.setString(1, objectName);
			callablestatement.setString(2, cuboidName);
			callablestatement.setString(3, sourceServer);
			callablestatement.setString(4, targetServer);
			callablestatement.setInt(5, sourceCuboidId);
			callablestatement.setInt(6, targetCuboidId);
			callablestatement.setInt(7, sourceObjectId);
			callablestatement.setInt(8, targetObjectId);
			callablestatement.setString(7, objectType);
			callablestatement.setInt(5, sequenceNumber);
			callablestatement.setString(8, packageName);
			callablestatement.execute();
		}
		catch (SQLException sql1)
		{
			System.out.println("Failed to Insert Cuboid RowId ColumnId Migration Map");
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
*/


//	CALL_ADD_MIGRATION_OBJECT_MAP="{CALL BW_ADD_MIGRATION_OBJECT_MAP (?,?,?,?,?,?,?)}";
//	@SOURCE_SERVER VARCHAR(512), 
//	@TARGET_SERVER VARCHAR(512), 
//	@SOURCE_OBJECT_ID INT, 
//	@TARGET_OBJECT_ID INT, 
//	@CREATION_TX_ID INT, 
//	@OBJECT_TYPE VARCHAR(512), 
//	@PACKAGE_NAME	 VARCHAR(512)

	public void AddMigrationObjectMap(Connection connection, String objectName, String sourceServer, String targetServer, int sourceObjectId, int targetObjectId, int tId, String objectType, String packageName) throws SQLException
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

			writeLogline("ObjectMap", objectType, MESSAGE_TYPE_OBJECT_MAP_UPDATE, "Success : Updating Migration Object Map [ObjectType:" + objectType + "] [SourceObjectID: " + sourceObjectId + "] [TargetObjectID: " + targetObjectId + "] TxId: [" + tId + "]");

		}
		catch (SQLException sql1)
		{
			writeLogline("ObjectMap", objectType, MESSAGE_TYPE_EXCEPTION, "Failure : Updating Migration Object Map [ObjectType:" + objectType + "] [SourceObjectID: " + sourceObjectId + "] [TargetObjectID: " + targetObjectId + "] TxId: [" + tId + "]. Reason : " + sql1.getMessage());
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

	// Create Nieghborhood Hierarchy if it does not exists already
	public void CheckIfNeighborhoodExistsIfNotCreateIt(Connection connection, String thisCommand, String commandLine, int nhLevel, 
				String nh0Name, String nh1Name, String nh2Name, String nh3Name,  int sourceNh0Id, int sourceNh1Id, int sourceNh2Id, int sourceNh3Id )
	{
		if (!nh0Name.isEmpty() && !nh1Name.isEmpty() && !nh2Name.isEmpty() && !nh3Name.isEmpty() )
		{
			CheckIf_Nh_0_Exists_IfNot_CreateIt(connection, thisCommand, commandLine, sourceNh0Id, nh0Name);
			CheckIf_Nh_1_Exists_IfNot_CreateIt(connection, thisCommand, commandLine, sourceNh1Id, nh0Name, nh1Name);
			CheckIf_Nh_2_Exists_IfNot_CreateIt(connection, thisCommand, commandLine, sourceNh2Id, nh0Name, nh1Name, nh2Name);
			CheckIf_Nh_3_Exists_IfNot_CreateIt(connection, thisCommand, commandLine, sourceNh3Id, nh0Name, nh1Name, nh2Name, nh3Name);
		}
		else if (!nh0Name.isEmpty() && !nh1Name.isEmpty() && !nh2Name.isEmpty() && nh3Name.isEmpty() )
		{
			CheckIf_Nh_0_Exists_IfNot_CreateIt(connection, thisCommand, commandLine, sourceNh0Id, nh0Name);
			CheckIf_Nh_1_Exists_IfNot_CreateIt(connection, thisCommand, commandLine, sourceNh1Id, nh0Name, nh1Name);
			CheckIf_Nh_2_Exists_IfNot_CreateIt(connection, thisCommand, commandLine, sourceNh2Id, nh0Name, nh1Name, nh2Name);
		}
		else if (!nh0Name.isEmpty() && !nh1Name.isEmpty() && nh2Name.isEmpty() && nh3Name.isEmpty() )
		{
			CheckIf_Nh_0_Exists_IfNot_CreateIt(connection, thisCommand, commandLine, sourceNh0Id, nh0Name);
			CheckIf_Nh_1_Exists_IfNot_CreateIt(connection, thisCommand, commandLine, sourceNh1Id, nh0Name, nh1Name);
		}
		else if (!nh0Name.isEmpty() && nh1Name.isEmpty() && nh2Name.isEmpty() && nh3Name.isEmpty() )
		{
			CheckIf_Nh_0_Exists_IfNot_CreateIt(connection, thisCommand, commandLine, sourceNh0Id, nh0Name);
		}
	}


	//Called from CreateNH_0 , CreateNH_1
	public void CheckIf_Nh_0_Exists_IfNot_CreateIt(Connection connection, String thisCommand, String commandLine, int sourceNh0Id, String nh0Name)
	{
		NeighborhoodLevelId nhl_id_0 = null;
		NeighborhoodLevel nhl_0 = null;
		boolean blnNhSecure = false;

		try 
		{
			nhl_id_0 = NeighborhoodManager.getNeighborhoodLevelId(connection, nh0Name.trim(), "", "", "", 0);

			if (nhl_id_0 == null)
			{
				writeLogline(thisCommand, commandLine, MESSAGE_TYPE_SUCCESS, "Creating Neighbourhood at Level 0 : [" + nh0Name + "] Started.");

		//		System.out.println("Neighbourhood DOES NOT EXISTS at Level 0 : " + nh0Name ); 
		//		System.out.println("Creating Neighbourhood at Level 0 : " + nh0Name); 

				try  { 	tm = new TransactionManager( connection, migrationUserId); 	tid = tm.startTransaction(); } 
				catch (SQLException e1) { e1.printStackTrace();}

				nhl_0 = NeighborhoodManagerLevel_0.createNeighborhood(connection, nh0Name, tid, blnNhSecure);
		//		System.out.println("Creation of NH_0 ..... nh0Name : " + nh0Name +  " Successful");
		//		System.out.println("Creation of NH_0 ..... nhl_0.getId() : " + nhl_0.getNhId() );
				int nh_0_id = nhl_0.getNhId();
		//		System.out.println("Creation of NH_0 ..... nh_0_id : " + nh_0_id );

				//  Commented as Deployer will only create his own membership at NH02 LEVEL
				//  Add admin/deployer user as member of newly created neighborhood
				int newMemberId = -1;
				newMemberId = MemberManager.createMember(connection, tid, migrationUserId, nh_0_id);
				AddMigrationObjectMap(connection, migrationUserName, sourceServer, targetServer, -1, newMemberId, tid, "Member", packageName); 
				writeLogline(thisCommand, commandLine, MESSAGE_TYPE_SUCCESS, "Success: Added MigrationUser to Neighbourhood [" + nh0Name +  "] at Level 0 : [" + nhl_0.getNhId() + "]");

				writeLogline(thisCommand, commandLine, MESSAGE_TYPE_SUCCESS, "Success: Created Neighbourhood [" + nh0Name +  "] at Level 0 : [" + nhl_0.getNhId() + "]");

				//System.out.println("Passing parameters to MAp ");
				//System.out.println("nh0Name : " + nh0Name);
				//System.out.println("sourceServer : " + sourceServer);
				//System.out.println("targetServer : " + targetServer);

				//System.out.println("sourceNh0Id : " + sourceNh0Id);
				//System.out.println("nh_0_id : " + nh_0_id);
				//System.out.println("tid : " + tid);
				//System.out.println("packageName : " + packageName);

				//sbResp.append( ContentDelimeter + "Creation of NH_0 : " + nh0Name +  Seperator + "Successful")  ;
				//public static void AddMigrationObjectMap(Connection connection, String sourceServer, String targetServer, int sourceObjectId, int targetObjectId, int tId, String objectType, String packageName) throws SQLException
				AddMigrationObjectMap(connection, nh0Name, sourceServer, targetServer, sourceNh0Id, nh_0_id, tid, "NH_0", packageName); 

				try {	System.out.println("commiting  transaction"); tm.commitTransaction();} 
				catch (SQLException e) { e.printStackTrace(); }
			}
			else
			{
				int nh_0_id = nhl_id_0.getId();
				writeLogline(thisCommand, commandLine, MESSAGE_TYPE_WARNING, "Warning : Neighbourhood [" + nh0Name + "] at Level 0 Already Exists : ");
		//		System.out.println("Neighbourhood at Level 0 Already Exists : " + nh0Name ); 
		//		System.out.println("Neighbourhood at Level 0 Already Exists with ID : " + nh_0_id); 
				//If Nh0 already exists not adding to MAPPING
				//AddMigrationObjectMap(connection, nh0Name, sourceServer, targetServer, sourceNh0Id, nh_0_id, tid, "NH_0", packageName); 
			}
			System.out.println("Creating NH_0 : " + nh0Name + " Done.");
		} 
		catch (SystemException | NeighborhoodException | SQLException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		//	System.out.println("Creation of NH_0 : " + nh0Name +  " Failed");
			writeLogline(thisCommand, commandLine, MESSAGE_TYPE_FAILURE, "Failure : Creation of Neighborhood [" + nh0Name +  "] at Level 0 Failed. Reason : " + e.getMessage());
			//sbResp.append( ContentDelimeter + "Creation of NH_0 : " + nh0Name +  Seperator + "Failed")  ;
		}
	}



	
	//Called from CreateNH_1.  Creation of NH0 is already called before this so Parent of NH01 i.e. NH0 already exists 
	public void CheckIf_Nh_1_Exists_IfNot_CreateIt(Connection connection, String thisCommand, String commandLine, int sourceNh1Id, String nh0Name, String nh1Name)
	{
		System.out.println("Inside CheckIf_Nh_1_Exists_IfNot_CreateIt");

		boolean blnNhSecure = false;
		NeighborhoodLevelId nhl = null;
		NeighborhoodLevelId nhl_id_0 = null;
		int parentNhId;
		NeighborhoodLevel_1 nhl_1 = null;

		try
		{
			nhl = NeighborhoodManager.getNeighborhoodLevelId(connection, nh0Name.trim(), nh1Name.trim(), "", "", 1);

			if (nhl == null)
			{
		//		System.out.println("Neighbourhood DOES NOT EXISTS at Level 1 : [/" + nh0Name + "/" + nh1Name + "]"); 
		//		System.out.println("Creating Neighbourhood at Level 1 : [/" + nh0Name + "/" + nh1Name + "]");
				try 
				{
		//			System.out.println("Creating NH_1 at level 1:  [/" + nh0Name + "/"  + nh1Name + " started.");
					writeLogline(thisCommand, commandLine, MESSAGE_TYPE_SUCCESS, "Creating Neighbourhood at Level 1 :  [/" + nh0Name + "/"  + nh1Name + "] Started.");

					//Getting parent
					nhl_id_0 = NeighborhoodManager.getNeighborhoodLevelId(connection, nh0Name.trim(), " ", " ", " ", 0);
					int nh_0_id = nhl_id_0.getId();
		//			System.out.println("nh_0_id : " + nh_0_id );
		//			System.out.println("nh_0_name : " + nhl_id_0.getName() );

					parentNhId = nh_0_id;
		//			System.out.println("Creating nh1 : nh1Name " + nh1Name);
		//			System.out.println("Creating nh1 : parentNhId " + parentNhId);
					
					try  { 	tm = new TransactionManager( connection, migrationUserId); 	tid = tm.startTransaction(); } 
					catch (SQLException e1) { e1.printStackTrace();}

					nhl_1 = NeighborhoodManagerLevel_1.createNeighborhood(connection, nh1Name, parentNhId , tid, blnNhSecure);

		//			System.out.println("Creation of NH_1 ..... nh1Name : " + nh1Name +  " Successful");
		//			System.out.println("Creation of NH_1 ..... nhl_1.getId() : " + nhl_1.getNhId() );
					int nh_1_id = nhl_1.getNhId();
		//			System.out.println("Creation of NH_1 ..... nh_1_id : " + nh_1_id );


					//  Commented as Deployer will only create his own membership at NH02 LEVEL
					//Add admin/deployer user as member of newly created neighborhood
					int newMemberId = -1;
					newMemberId = MemberManager.createMember(connection, tid, migrationUserId, nh_1_id);
					AddMigrationObjectMap(connection, migrationUserName, sourceServer, targetServer, -1, newMemberId, tid, "Member", packageName); 
					
					writeLogline(thisCommand, commandLine, MESSAGE_TYPE_SUCCESS, "Success: Added MigrationUser to Neighbourhood [/" + nh0Name + "/" + nh1Name +  "] at Level 1 : [" + nhl_1.getNhId() + "]");

					writeLogline(thisCommand, commandLine, MESSAGE_TYPE_SUCCESS, "Success: Created Neighbourhood [/" + nh0Name + "/" + nh1Name +  "] at Level 1 : [" + nhl_1.getNhId() + "]");

					AddMigrationObjectMap(connection, nh1Name, sourceServer, targetServer, sourceNh1Id, nhl_1.getNhId(), tid, "NH_1", packageName); 

					try {	System.out.println("commiting  transaction"); tm.commitTransaction();} 
					catch (SQLException e) { e.printStackTrace(); }
				} 
				catch (SystemException | NeighborhoodException | SQLException e) 
				{
					e.printStackTrace();
					System.out.println("Creation of NH_1 : /" + nh0Name + "/" +  nh1Name +  " Failed");
					writeLogline(thisCommand, commandLine, MESSAGE_TYPE_FAILURE, "Failure : Creation of Neighborhood [/" + nh0Name + "/" + nh1Name +  "] at Level 1 Failed. Reason : " + e.getMessage());
				}
				System.out.println("Creating NH_1 : " + nh1Name + " Done.");
			}
			else
			{
				int nh_1_id = nhl.getId();
				writeLogline(thisCommand, commandLine, MESSAGE_TYPE_WARNING, "Warning : Neighbourhood [/" + nh0Name + "/" + nh1Name + "] at Level 1 Already Exists : ");
		//		System.out.println("Neighbourhood at Level 1 Already Exists : /" + nh0Name + "/" + nh1Name ); 
		//		System.out.println("Neighbourhood at Level 1 Already Exists with ID : " + nh_1_id); 
				//If Nh1 already exists not adding to MAPPING
				//AddMigrationObjectMap(connection, nh1Name, sourceServer, targetServer, sourceNh1Id, nh_1_id, tid, "NH_1", packageName); 
				//System.out.println("Neighbourhood at Level 1 Already Exists : " + nh0Name + "->" + nh1Name); 
			}
		}
		catch (SystemException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Creation of NH_1 : /" + nh0Name + "/" + nh1Name +  " Failed");
			writeLogline(thisCommand, commandLine, MESSAGE_TYPE_FAILURE, "Failure : Creation of Neighborhood [/" + nh0Name + "/"  + nh1Name +  "] at Level 1 Failed. Reason : " + e.getMessage());
		}
	}



	//Called from CreateNH_2.  Creation of NH0, NH1 is already called before this so Parent of NH02 i.e. NH0 AND NH1 already exists 
	public void CheckIf_Nh_2_Exists_IfNot_CreateIt(Connection connection, String thisCommand, String commandLine, int sourceNh2Id, String nh0Name, String nh1Name, String nh2Name)
	{
		System.out.println("Inside CheckIf_Nh_2_Exists_IfNot_CreateIt");

		boolean blnNhSecure = false;
		NeighborhoodLevelId nhl = null;
		NeighborhoodLevelId nhl_id_1 = null;
		int parentNhId;
		NeighborhoodLevel_2 nhl_2 = null;

		try
		{
			nhl = NeighborhoodManager.getNeighborhoodLevelId(connection, nh0Name.trim(), nh1Name.trim(), nh2Name.trim(), "", 2);

			if (nhl == null)
			{
		//		System.out.println("Neighbourhood DOES NOT EXISTS at Level 2 : [" + nh0Name + "/" + nh1Name + "/" + nh2Name + "]"); 
		//		System.out.println("Creating Neighbourhood at Level 2 : [" + nh0Name + "/" + nh1Name + "/" + nh2Name + "]");

				try 
				{
		//			System.out.println("Creating NH_2 at level 2 :  [/" + nh0Name + "/"  + nh1Name + "/"  + nh2Name + " started.");
					writeLogline(thisCommand, commandLine, MESSAGE_TYPE_SUCCESS, "Creating Neighbourhood at Level 2 : [/" + nh0Name + "/"  + nh1Name + "/"  + nh2Name + "] started.");
					//Getting parent
					nhl_id_1 = NeighborhoodManager.getNeighborhoodLevelId(connection, nh0Name.trim(), nh1Name.trim(), " ", " ", 1);
					int nh_1_id = nhl_id_1.getId();
		//			System.out.println("nh_1_id : " + nh_1_id );
		//			System.out.println("nh_1_name : " + nhl_id_1.getName() );

					parentNhId = nh_1_id;
		//			System.out.println("Creating nh2 : nh2Name " + nh2Name);
		//			System.out.println("Creating nh2 : parentNhId " + parentNhId);
					
					try  { 	tm = new TransactionManager( connection, migrationUserId); 	tid = tm.startTransaction(); } 
					catch (SQLException e1) { e1.printStackTrace();}

					nhl_2 = NeighborhoodManagerLevel_2.createNeighborhood(connection, nh2Name, parentNhId, tid, blnNhSecure);

		//			System.out.println("Creation of NH_2  ..... nh2Name : " + nh2Name +  " Successful");
		//			System.out.println("Creation of NH_2 ..... nhl_2.getId() : " + nhl_2.getNhId() );
					int nh_2_id = nhl_2.getNhId();
		//			System.out.println("Creation of NH_2 ..... nh_2_id : " + nh_2_id );

					//Deployer will create his own membership in NH2 as to create Collaboration Tree
					//Add admin/deployer user as member of newly created neighborhood
					int newMemberId = -1;
					newMemberId = MemberManager.createMember(connection, tid, migrationUserId, nh_2_id);
					AddMigrationObjectMap(connection, migrationUserName, sourceServer, targetServer, -1, newMemberId, tid, "Member", packageName); 
					writeLogline(thisCommand, commandLine, MESSAGE_TYPE_SUCCESS, "Success: Added MigrationUser to Neighbourhood [/" + nh0Name + "/" + nh1Name + "/" + nh2Name +  "] at Level 2 : [" + nhl_2.getNhId() + "]");

					writeLogline(thisCommand, commandLine, MESSAGE_TYPE_SUCCESS, "Success: Created Neighbourhood [/" + nh0Name + "/" + nh1Name + "/" + nh2Name +  "] at Level 2 : [" + nhl_2.getNhId() + "]");
					
					AddMigrationObjectMap(connection, nh2Name, sourceServer, targetServer, sourceNh2Id, nhl_2.getNhId(), tid, "NH_2", packageName); 

					try {	System.out.println("commiting  transaction"); tm.commitTransaction();} 
					catch (SQLException e) { e.printStackTrace(); }
				} 
				catch (SystemException | NeighborhoodException | SQLException e) 
				{
					e.printStackTrace();
					System.out.println("Creation of NH_2 : /" + nh0Name + "/" +  nh1Name  + "/" +  nh2Name +  " Failed");
					writeLogline(thisCommand, commandLine, MESSAGE_TYPE_FAILURE, "Failure : Creation of Neighborhood [ /" + nh0Name + "/" +  nh1Name  + "/"  + nh2Name +  "] at Level 2 Failed. Reason : " + e.getMessage());
				}
				System.out.println("Creating NH_2 : " + nh2Name + " Done.");
			}
			else
			{
				int nh_2_id = nhl.getId();
				writeLogline(thisCommand, commandLine, MESSAGE_TYPE_WARNING, "Warning : Neighbourhood [/" + nh0Name + "/" + nh1Name + "/" + nh2Name + "] at Level 2 Already Exists : ");
		//		System.out.println("Neighbourhood at Level 2 Already Exists : " + nh2Name ); 
		//		System.out.println("Neighbourhood at Level 2 Already Exists with ID : " + nh_2_id); 
				//If Nh1 already exists not adding to MAPPING
				//AddMigrationObjectMap(connection, nh2Name, sourceServer, targetServer, sourceNh2Id, nh_2_id, tid, "NH_2", packageName); 
				//System.out.println("Neighbourhood at Level 2 Already Exists : " + nh0Name + "->" + nh1Name + "->" + nh2Name); 
			}
		}
		catch (Exception e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Creation of NH_2 : [/" + nh0Name + "/" + nh1Name + "/" + nh2Name +  "] Failed");
			writeLogline(thisCommand, commandLine, MESSAGE_TYPE_FAILURE, "Failure : Creation of Neighborhood [ /" + nh0Name + "/" + nh1Name + "/" + nh2Name +  "] at Level 2 Failed. Reason : " + e.getMessage());
		}
	}


	//Called from CreateNH_3.  Creation of NH0, NH1, NH2 is already called before this so Parent of NH03 i.e. NH0, NH1 and NH2 already exists 
	public void CheckIf_Nh_3_Exists_IfNot_CreateIt(Connection connection, String thisCommand, String commandLine, int sourceNh3Id, String nh0Name, String nh1Name, String nh2Name, String nh3Name)
	{
		System.out.println("Inside CheckIf_Nh_2_Exists_IfNot_CreateIt");

		boolean blnNhSecure = false;
		NeighborhoodLevelId nhl = null;
		NeighborhoodLevelId nhl_id_2 = null;
		int parentNhId;
		NeighborhoodLevel_3 nhl_3 = null;

		try
		{

			nhl = NeighborhoodManager.getNeighborhoodLevelId(connection, nh0Name.trim(), nh1Name.trim(), nh2Name.trim(), nh3Name.trim(), 3);

			if (nhl == null)
			{
		//		System.out.println("Neighbourhood DOES NOT EXISTS at Level 3 : [" + nh0Name + "/" + nh1Name + "/" + nh2Name + "/" + nh3Name + "]"); 
		//		System.out.println("Creating Neighbourhood at Level 3 : [" + nh0Name + "/" + nh1Name + "/" + nh2Name + "/" + nh3Name + "]"); 

				try 
				{
		//			System.out.println("Creating NH_3 at level 3:  [/" + nh0Name + "/"  + nh1Name + "/"  + nh2Name + "/" + nh3Name + "] started.");
					writeLogline(thisCommand, commandLine, MESSAGE_TYPE_SUCCESS, "Creating Neighbourhood at Level 3 : [/" + nh0Name + "/" + nh1Name + "/" + nh2Name + "/" + nh3Name + "] Started.");
					//Getting parent
					nhl_id_2 = NeighborhoodManager.getNeighborhoodLevelId(connection, nh0Name.trim(), nh1Name.trim(), nh2Name.trim(), " ", 2);
					int nh_2_id = nhl_id_2.getId();
		//			System.out.println("nh_2_id : " + nh_2_id );
		//			System.out.println("nh_2_name : " + nhl_id_2.getName() );

					parentNhId = nh_2_id;
		//			System.out.println("Creating nh3 : nh3Name " + nh3Name);
		//			System.out.println("Creating nh3 : parentNhId " + parentNhId);
					
					try  { 	tm = new TransactionManager( connection, migrationUserId); 	tid = tm.startTransaction(); } 
					catch (SQLException e1) { e1.printStackTrace();}

					nhl_3 = NeighborhoodManagerLevel_3.createNeighborhood(connection, nh3Name, parentNhId, tid, blnNhSecure);

		//			System.out.println("Creation of NH_3  ..... nh3Name : " + nh3Name +  " Successful");
		//			System.out.println("Creation of NH_3 ..... nhl_3.getId() : " + nhl_3.getNhId() );
					int nh_3_id = nhl_3.getNhId();
		//			System.out.println("Creation of NH_3 ..... nh_3_id : " + nh_3_id );

					//Add admin/deployer user as member of newly created neighborhood ONLY AT NH02 Neighborhod LEVEL
					int newMemberId = -1;
					newMemberId = MemberManager.createMember(connection, tid, migrationUserId, nh_3_id);
					AddMigrationObjectMap(connection, migrationUserName, sourceServer, targetServer, -1, newMemberId, tid, "Member", packageName); 
					writeLogline(thisCommand, commandLine, MESSAGE_TYPE_SUCCESS, "Success: Added MigrationUser to Neighbourhood [/" + nh0Name + "/" + nh1Name + "/" + nh2Name + "/"  + nh3Name +  "] at Level 3 : [" + nhl_3.getNhId() + "]");

					writeLogline(thisCommand, commandLine, MESSAGE_TYPE_SUCCESS, "Success: Created Neighbourhood [/" + nh0Name + "/" + nh1Name + "/" + nh2Name +  "/" + nh3Name +  "] at Level 3 : [" + nhl_3.getNhId() + "]");

					System.out.println("Creation of NH_3 : " + nh3Name +  " Successful");
					AddMigrationObjectMap(connection, nh3Name, sourceServer, targetServer, sourceNh3Id, nhl_3.getNhId(), tid, "NH_3", packageName); 

					try {	System.out.println("commiting  transaction"); tm.commitTransaction();} 
					catch (SQLException e) { e.printStackTrace(); }
				} 
				catch (SystemException | NeighborhoodException | SQLException e) 
				{
					e.printStackTrace();
					System.out.println("Creation of NH_3 : /" + nh0Name + "/" +  nh1Name  + "/" +  nh2Name + "/" +  nh3Name + " Failed");
					writeLogline(thisCommand, commandLine, MESSAGE_TYPE_FAILURE, "Failure : Creation of Neighborhood [/" + nh0Name + "/" +  nh1Name  + "/" +  nh2Name + "/" +  nh3Name +  "] at Level 3 Failed. Reason : " + e.getMessage());
				}
				System.out.println("Creating NH_3 : " + nh3Name + " Done.");
			}
			else
			{
				int nh_3_id = nhl.getId();
				writeLogline(thisCommand, commandLine, MESSAGE_TYPE_WARNING, "Warning : Neighbourhood [/" + nh0Name + "/" + nh1Name + "/" + nh2Name + "/" + nh3Name + "] at Level 3 Already Exists : ");
		//		System.out.println("Neighbourhood at Level 3 Already Exists : " + nh3Name ); 
		//		System.out.println("Neighbourhood at Level 3 Already Exists with ID : " + nh_3_id); 
				//If Nh1 already exists not adding to MAPPING
				//AddMigrationObjectMap(connection, nh3Name, sourceServer, targetServer, sourceNh3Id, nh_3_id, tid, "NH_3", packageName); 
				//System.out.println("Neighbourhood at Level 3 Already Exists : [/" + nh0Name + "/" + nh1Name + "/" + nh2Name + "/" + nh3Name); 
			}

		}
		catch (SystemException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Creation of NH_3 : [/" + nh0Name + "/" + nh1Name + "/" + nh2Name + "/" + nh3Name +  "] Failed");
			writeLogline(thisCommand, commandLine, MESSAGE_TYPE_FAILURE, "Failure : Creation of Neighborhood [ /" + nh0Name + "/" + nh1Name + "/" + nh2Name +  "/" + nh3Name +  "] at Level 3 Failed. Reason : " + e.getMessage());
		}
	}

	
	//Create Cuboid if it does not exists. Before calling this all NH Tree and Collab/Wb are already created  
	public void CheckIf_Cuboid_Exists_IfNot_CreateIt(Connection connection, String thisCommand, String commandLine, int sourceCollabId, int migrationUserId, int nhId, int nhMembershipId, String collabNhName, String collabName, int sourceWbId, String wbName, int sourceCuboidId, String cuboidName)
	{
		//Get neighborhood ID
//		nhl = null;
//		nhId = -1;
		try 
		{
			boolean cuboidExist = false;
			String tableName;
//			System.out.println("Before getting neighborhood levelId");
//			System.out.println("nhLevel: " + nhLevel);
//			nhl = NeighborhoodManager.getNeighborhoodLevelId(connection, nh0Name.trim(), nh1Name.trim(), nh2Name.trim(), nh3Name.trim(), nhLevel);
//			nhId = nhl.getId();
//			System.out.println("nhId based on neighborhood: " + nhId);

			//bwcon = BoardwalkConnectionManager.getBoardwalkConnection(connection , 2, "admin", 1003);
/*			bwcon = BoardwalkConnectionManager.getBoardwalkConnection(connection , targetCuboidCreatorUserId, cuboidCreatorUserName, targetCuboidCreatorMemberId);	
			System.out.println("bwcon is created using Target Server Ids:");
			System.out.println("targetCuboidCreatorUserId : " + targetCuboidCreatorUserId);
			System.out.println("cuboidCreatorUserName : " + cuboidCreatorUserName);
			System.out.println("targetCuboidCreatorMemberId : " + targetCuboidCreatorMemberId);
*/

		//	System.out.println("bwcon is created using Migration User and Password:");
			//Membership of Neighborhood used to create WB
			bwcon = getBoardwalkConnection(migrationUserName, migrationUserPassword, nhMembershipId);

			//Get Collaboration ID
			int collabId = -1;
			Vector cl = CollaborationManager.getCollaborationsOfNeighborhood(connection, nhId);
			collabId = -1;
			Iterator cli = cl.iterator();
			while (cli.hasNext())				// check if collaboration already exists
			{
				CollaborationTreeNode ctn = (CollaborationTreeNode)cli.next();
				if (collabName.trim().equals(ctn.getName()))
				{
					collabId = ctn.getId();		// collaboration exists
					break;
				}
			}

			if (collabId == -1)
			{
		//		System.out.println("Collaboration not found ");
				writeLogline(thisCommand, commandLine, MESSAGE_TYPE_FAILURE, "Failure : Failed to create Cuboid [" + cuboidName +  "]. Reason : Collaboration Not Found");
			}
			else
			{
				//Get Whiteboard ID
				int wbId = -1;
				BoardwalkCollaborationNode bcn = BoardwalkCollaborationManager.getCollaborationTree(bwcon, collabId);
				//System.out.println("Sucessfully fetched the collab tree from the database");
				//System.out.println("Collaboration : " + bcn.getName());							

				Vector wv = bcn.getWhiteboards();
				Iterator wvi = wv.iterator();

				while ( wvi.hasNext())
				{
					BoardwalkWhiteboardNode bwn = (BoardwalkWhiteboardNode)wvi.next();
					//System.out.println("\tWhiteboard = " + bwn.getName());
					if (wbName.equals(bwn.getName()))
					{
						wbId= bwn.getId();
						//System.out.println("WhiteboardID : " + wbId);

						Vector tv = bwn.getTables();
						Iterator tvi = tv.iterator();

						cuboidExist = false;

						if (tvi.hasNext())
						{
							while (tvi.hasNext())
							{
								tableName = "";
								BoardwalkTableNode btn = (BoardwalkTableNode)tvi.next();
								//System.out.println("\t\tTable = " + btn.getName());
								tableName = btn.getName();
								if (cuboidName.equals(btn.getName()))
								{
									cuboidExist = true;
									tableId = btn.getId();
									//System.out.println("Cuboid Aready Exists : " + tableName);
									break;
								}
							}  // End of while Table ITERATOR
						}

						if (cuboidExist)
						{
							break;
						}
					}
				}		// End of While	Whiteboard				
				
				if (wbId == -1)
				{
		//			System.out.println("Whiteboard Not found. Failed to create Cuboid");
					writeLogline(thisCommand, commandLine, MESSAGE_TYPE_FAILURE, "Failure : Failed to create Cuboid [" + cuboidName +  "] . Reason : Whiteboard Not Found");
				}
				else
				{
					if (cuboidExist)
					{
						writeLogline(thisCommand, commandLine, MESSAGE_TYPE_WARNING, "Warning : Cuboid [" + cuboidName +  "] Already Exists : ");
						//If Cuboid already exists not adding to MAPPING
						//AddMigrationObjectMap(connection, cuboidName, sourceServer, targetServer, sourceCuboidId, tableId, -1, "Cuboid", packageName); 
		//				System.out.println("Cuboid Already Exists :" + cuboidName);
					}
					else
					{
						//Create Cuboid
						int tableId = -1;

					//	System.out.println("Before calling BoardwalkTableManager.createTable : bwcon.getMemberId() : " + bwcon.getMemberId());
					//	tableId = BoardwalkTableManager.createTable(bwcon, collabId, wbId, cuboidName, "Added through Migration process");

					//	SopLogger.write2EntitlementLog(" Table Creation  :: New Table ["+ cuboidName + "] by Member Id ["+ nhMembershipId + "] Successfuliy");

					//////////////////////Set Access on Newly Created Cuboid. here starts .... Added by Rahul on 11-June-2022

						try  { 	tm = new TransactionManager( connection, migrationUserId); 	tid = tm.startTransaction(); } 
						catch (SQLException e1) { e1.printStackTrace();}

						writeLogline(thisCommand, commandLine, MESSAGE_TYPE_STATUS , "Creating new Cuboid [" + cuboidName + "] started. ");


						tableId = TableManager.createTable (
												   connection,
												   wbId,
												   cuboidName,
												   "Added through Migration process",
												   2, 1, 1,"LATEST",
												   nhMembershipId,
												   tid,
												   1
												 );


						writeLogline(thisCommand, commandLine, MESSAGE_TYPE_STATUS , "Setting Neighborhood Access on Cuboid [" + cuboidName + "] started. [" + tableId + "]");


						Hashtable relationships = NeighborhoodManager.getNeighborhoodRelationships(connection, nhId);

						Vector accessLists = new Vector();

						//CREATOR, PUBLIC

						Enumeration relationKeys = relationships.keys();

						if (relationships.size() > 0)
						{
							while (relationKeys.hasMoreElements())
							{
								String relationship = (String)relationKeys.nextElement();

								NewTableAccessList accessList = new NewTableAccessList(-1, tableId, relationship);

								if (relationship.equals("PRIVATE"))
								{
									accessList.setAddRow();
									accessList.setDeleteRow();
									accessList.setReadLatestOfTable();
									accessList.setWriteLatestOfTable();
									accessList.setReadWriteLatestOfMyRows();
								}
								accessLists.add(accessList);
							}
						}

						// sak 12/06/09 increased public access for pwc
						NewTableAccessList publicAccessList = new NewTableAccessList(-1, tableId, "PUBLIC");
						//creatorAccessList.setAdministerTable();
						publicAccessList.setAdministerColumn();
						publicAccessList.setAddRow();
						publicAccessList.setDeleteRow();
						publicAccessList.setReadLatestOfTable();
						publicAccessList.setWriteLatestOfTable();
						publicAccessList.setReadWriteLatestOfMyRows();

						NewTableAccessList creatorAccessList = new NewTableAccessList(-1, tableId, "CREATOR");
						creatorAccessList.setAdministerTable();
						creatorAccessList.setAdministerColumn();
						creatorAccessList.setAddRow();
						creatorAccessList.setDeleteRow();
						creatorAccessList.setReadLatestOfTable();
						creatorAccessList.setWriteLatestOfTable();
						creatorAccessList.setReadWriteLatestOfMyRows();

						accessLists.add(creatorAccessList);
						accessLists.add(publicAccessList);

						if (accessLists.size() > 0)
						{
							TableManager.addAccesstoTable
												  (
												  connection,
												  tableId,
												  accessLists,
												  tid
												  );
						}

						try {	System.out.println("commiting  transaction"); tm.commitTransaction();} 
						catch (SQLException e) { e.printStackTrace(); }

						writeLogline(thisCommand, commandLine, MESSAGE_TYPE_STATUS , "Setting Neighborhood Access on Cuboid [" + cuboidName + "] is Completed Successfully. [" + tableId + "]");

					//////////////////////Set Access on Newly Created Cuboid. here.. Ends  Added by Rahul on 11-June-2022

						if (tableId != -1)
						{
							AddMigrationObjectMap(connection, cuboidName, sourceServer, targetServer, sourceCuboidId, tableId, -1, "Cuboid", packageName); 

							writeLogline(thisCommand, commandLine, MESSAGE_TYPE_SUCCESS, "Success: Created Cuboid [" + cuboidName + "] successfully : [" + tableId + "]");
							System.out.println("Cuboid Created Successfully: [" + cuboidName +  "]  cuboidId : [" + tableId + "]");
						}
					}
				}
			}
		} 
		catch (Exception e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Failed to create Cuboid [" +  cuboidName + "]");
			writeLogline(thisCommand, commandLine, MESSAGE_TYPE_FAILURE, "Failure : Creation of cuboid [" + cuboidName + "] Failed. Reason : " + e.getMessage());
		}
	}



	//Create Collaboration if it does not exists 
	public void CheckIf_Whiteboard_Exists_IfNot_CreateIt(Connection connection, String thisCommand, String commandLine, int sourceCollabId, int migrationUserId, int nhId, int nhMembershipId, String collabNhName, String collabName, int sourceWbId, String wbName)
	{
		try 
		{
			//Get Collaboration ID
			int collabId = -1;
			Vector cl = CollaborationManager.getCollaborationsOfNeighborhood(connection, nhId);
			collabId = -1;
			Iterator cli = cl.iterator();
			while (cli.hasNext())				// check if collaboration already exists
			{
				CollaborationTreeNode ctn = (CollaborationTreeNode)cli.next();
				if (collabName.trim().equals(ctn.getName()))
				{
					collabId = ctn.getId();		// collaboration exists
		//			System.out.println("Collaboration Found. CollabId = " + collabId);
					break;
				}
			}

			if (collabId == -1)
			{
		//		System.out.println("Collaboration not found ");
				writeLogline(thisCommand, commandLine, MESSAGE_TYPE_FAILURE, "Failure : Creation of Whiteboard [" + wbName +  "] Failed. Reason : [" + collabName + "] does not exists in Neighborhood [" + collabNhName + "].");
			}
			else
			{
				int wbId = -1;

				//Membership of Neighborhood used to create WB
				bwcon = getBoardwalkConnection(migrationUserName, migrationUserPassword, nhMembershipId);

				BoardwalkCollaborationNode bcn = null;
				bcn = BoardwalkCollaborationManager.getCollaborationTree(bwcon, collabId);
				
				if (bcn == null) 
				{
					//throw new NoSuchElementException("Collaboration Id NOT FOUND") ;
					throw new BoardwalkException( 10018 );
				}    		
		//		System.out.println("Sucessfully fetched the collab tree from the database");

				boolean wbExists = false;
				Vector<?> wv = bcn.getWhiteboards();
				Iterator<?> wvi = wv.iterator();
				while ( wvi.hasNext())
				{
					BoardwalkWhiteboardNode bwn = (BoardwalkWhiteboardNode)wvi.next();
		//			System.out.println("\tWhiteboard = " + bwn.getName());

					if (wbName.trim().equals(bwn.getName()))
					{
						wbId = bwn.getId();
		//				System.out.println("\tWhiteboard ALREADY EXISTS = " + bwn.getName());
						wbExists = true;
						break;
					}
				}
			
				if(wbExists == false)
				{
					try  { 	tm = new TransactionManager( connection, migrationUserId); 	tid = tm.startTransaction(); } 
					catch (SQLException e1) { e1.printStackTrace();}
					
					wbId = WhiteboardManager.createWhiteboard(connection, wbName,0, 2, 0, collabId, tid, 1);

					writeLogline(thisCommand, commandLine, MESSAGE_TYPE_SUCCESS, "Success: Whitebaod [" + wbName +  "]  is created successfully in Collaboration [" + collabName + "].");
	
					AddMigrationObjectMap(connection, wbName, sourceServer, targetServer, sourceWbId, wbId, tid, "Wb", packageName); 

					try {	System.out.println("commiting  transaction"); tm.commitTransaction();} 
					catch (SQLException e) { e.printStackTrace(); }

					if (wbId != -1)
					{
		//				System.out.println("Success: Whiteboard Created : " + wbId);
					}
				}
				else
				{
					writeLogline(thisCommand, commandLine, MESSAGE_TYPE_WARNING, "Warning : Whiteboard [" + wbName +  "] Already Exists : ");
					//If Whiteboard already exists not adding to MAPPING
					//AddMigrationObjectMap(connection, wbName, sourceServer, targetServer, sourceWbId, wbId, tid, "Wb", packageName); 
					System.out.println("Whiteboard Already Exists :" + wbName);
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
	}


	//Create Collaboration if it does not exists. Neighborhood Tree is already generated
	public void CheckIf_Collab_Exists_IfNot_CreateIt(Connection connection, String thisCommand, String commandLine, int sourceCollabId,
			int migrationUserId, int nhId, int memberId, String collabNhName,  String collabName)
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
				try  { 	tm = new TransactionManager( connection, migrationUserId ); 	tid = tm.startTransaction(); } 
				catch (SQLException e1) { e1.printStackTrace();}

				//Earlier Collab was created by COLLAB_CREATOR membership of the Neighborhood
				collabId = CollaborationManager.createCollaboration(connection, collabName, "Collab created by Migration", memberId, tid, 1);
				writeLogline(thisCommand, commandLine, MESSAGE_TYPE_SUCCESS, "Success: Collaboration [" + collabName +  "]  is created successfully in Neighborhood [" + collabNhName + "].");

				AddMigrationObjectMap(connection, collabName, sourceServer, targetServer, sourceCollabId, collabId, tid, "Collab", packageName); 

				try {	System.out.println("commiting  transaction"); tm.commitTransaction();} 
				catch (SQLException e) { e.printStackTrace(); }

				//Here it is created by "admin" who is member of "ROOT". Using ROOT membership of "admin" user	
				//System.out.println("Collab creation started using 'admin' membership of 'ROOT' memberid : " + membernhId);
				//collabId = CollaborationManager.createCollaboration(connection, collabName, "Collab created by Migration", membernhId, tid, 1);
		//		System.out.println("Collab created successfully : " + collabId);
				//sbResp.append( ContentDelimeter + "Creation of Collaboration : " + collabName +  Seperator + "Successful")  ;
			}
			else
			{
				writeLogline(thisCommand, commandLine, MESSAGE_TYPE_WARNING, "Warning : Collaboration [" + collabName + "] Already Exists : ");
				//If Collab already exists not adding to MAPPING
				//AddMigrationObjectMap(connection, collabName, sourceServer, targetServer, sourceCollabId, collabId, tid, "Collab", packageName); 
		//		System.out.println("Collaboration Already Exists :" + collabName);
			}
		}
		catch (Exception   e)
		{
			e.printStackTrace();
			//sbResp.append( ContentDelimeter + "Creation of Collaboration : " + collabName +  Seperator + "Failed")  ;
		//	System.out.println("Failed to create Collaboration : " + collabName);
			writeLogline(thisCommand, commandLine, MESSAGE_TYPE_FAILURE, "Failure : Creation of Collaboration [" + collabName +  "] is Failed. Reason : " + e.getMessage());
		}

	}


	//Check Membership exists for User in the Neighborhood. If exists return MemberId, IF NOT EXISTS create Membership and return memberId
	public int GetNeighborhoodMembershipIfNotExistCreateIt(Connection connectoin, int userId, int nhId )
	{
		int newMemberId = -1;
		Hashtable memberships = null;
		Enumeration memberIds = null ;
		try
		{
			memberships  = UserManager.getMembershipsForUser(connection, userId);
			memberIds = memberships.keys();
		//	System.out.println("memberships.size : " + memberships.size());
		}
		catch ( Exception e )
		{
		   e.printStackTrace();
		}

		boolean membershipFound = false;
		int memberId = -1 ;
		int membernhId = -1;
		String nhName;
		if (memberships.size() == 0 )
		{
		//	System.out.println("Memberships of Migration User not found in nhId : " + nhId);
		}
		else
		{
			if (  memberships.size() > 0 )
			{
		//		System.out.println("Checking membership...");
				for (int ii=0; ii < memberships.size(); ii++)
				{
					memberId =((Integer) memberIds.nextElement()).intValue();
					membernhId =((Member) memberships.get(        new Integer(memberId)   )).getNeighborhoodId();
					nhName = ((Member) memberships.get(        new Integer(memberId)   )).getNeighborhoodName();
		//			System.out.println("nhId based on membershiop : " + membernhId +   " nhname: " + nhName);
		//			System.out.println("nhId :" + nhId);
					if (nhId == membernhId)
					{
						System.out.println("Membership found.");
						membershipFound = true;
						return memberId;
					}
				}
			}
		}
		if (membershipFound == false)
		{
			try
			{
				newMemberId = BoardwalkNeighborhoodManager.createMember(bwcon, nhId, userId);
				AddMigrationObjectMap(connection, migrationUserName, sourceServer, targetServer, -1, newMemberId, tid, "Member", packageName); 
			}
			catch (BoardwalkException be)
			{
				System.out.println(be.getMessage());
			}
			catch (SQLException sqe)
			{
				System.out.println(sqe.getMessage());
				sqe.printStackTrace();
			}
		}
		return newMemberId;
	}



	public static int GetMigratedObjectIdOnTargetServerUsingObjectMap(Connection connection, String objectName, String sourceServer, int sourceObjectId, String objectType) throws SQLException
	{
	//	System.out.println("Inside GetMigratedObjectIdOnTargetServerUsingObjectMap....");
	//	System.out.println("objectName .... : " + objectName);
	//	System.out.println("sourceServer .... : " + sourceServer);
	//	System.out.println("sourceObjectId .... : " + sourceObjectId);
	//	System.out.println("objectType .... : " + objectType);
		
		CallableStatement callablestatement = null ;
		int targetObjectId = -1;
		try
		{
			//if (connection == null)
			//	System.out.println("connection is NULL");
			//else
			//	System.out.println("connection is NOT NULL");

			//System.out.println("PrepareCall inside GetMigratedObjectIdOnTargetServerUsingObjectMap @@@@@@@@@@@ ");
			callablestatement = connection.prepareCall(CALL_GET_MIGRATED_OBJECT_ID_FROM_MIGRATION_OBJECT_MAP);
			//System.out.println("AFTER PrepareCall inside GetMigratedObjectIdOnTargetServerUsingObjectMap @@@@@@@@@@@ ");
			callablestatement.setString(1, objectName);
			callablestatement.setString(2, sourceServer);
			callablestatement.setInt(3, sourceObjectId);
			callablestatement.setString(4, objectType);
			callablestatement.registerOutParameter(5, java.sql.Types.INTEGER);

			callablestatement.execute();
			targetObjectId = callablestatement.getInt(5);

			callablestatement.close();
	//		System.out.println("After PrepareCall Execute GetMigratedObjectIdOnTargetServerUsingObjectMap @@@@@@@@@@@ ");
		}
		catch (SQLException sql1)
		{
			  throw sql1;
		}
/*		finally
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
*/
	//	System.out.println("Return Value .... : " + targetObjectId);

		return targetObjectId;
	}


	//After BLOB is created, all Routines needed to run for importing User Template Routines throug Migration process
	public String RunImportUserTemplateRoutines(Connection connection, String templateUploadedBy, String templateName,  int templateMajorVersion, int templateMinorVersion,  String templateSize, String downloadUrl, int newBlobId, String nh0Name, String nh1Name, String nh2Name, String nh3Name, String imageUrl)
	{
	//	System.out.println("Inside RunImportUserTemplateRoutines : ");

	//	System.out.println("templateUploadedBy : " + templateUploadedBy);
	//	System.out.println("templateName : " + templateName);
	//	System.out.println("templateMajorVersion : " + templateMajorVersion);
	//	System.out.println("templateMinorVersion : " + templateMinorVersion);
	//	System.out.println("templateSize : " + templateSize);
	//	System.out.println("downloadUrl : " + downloadUrl);
	//	System.out.println("newBlobId : " + newBlobId);
	//	System.out.println("nh0Name : " + nh0Name);
	//	System.out.println("nh1Name : " + nh1Name);
	//	System.out.println("nh2Name : " + nh2Name);
	//	System.out.println("nh3Name : " + nh3Name);
	//	System.out.println("nh3Name : " + imageUrl);
	//	System.out.println("nh3Name : " + nhHierarchy);

		String spLog = null;

		CallableStatement callablestatement = null;

		try
		{
			callablestatement = connection.prepareCall(CALL_BW_DEPLOYE_USER_TEMPLATE_ROUTINES);
			callablestatement.setString(1, templateUploadedBy);
			callablestatement.setString(2, templateName);
			callablestatement.setInt(3, templateMajorVersion);
			callablestatement.setInt(4, templateMinorVersion);
			callablestatement.setString(5, templateSize);
			callablestatement.setString(6, downloadUrl);
			callablestatement.setInt(7, newBlobId);
			callablestatement.setString(8, nh0Name);
			callablestatement.setString(9, nh1Name);
			callablestatement.setString(10, nh2Name);
			callablestatement.setString(11, nh3Name);
			callablestatement.setString(12, imageUrl);

			callablestatement.registerOutParameter (13, java.sql.Types.VARCHAR );
			callablestatement.execute();
			spLog = callablestatement.getString(13);

		}
		catch (SQLException sql1)
		{
			System.out.println("RunImportUserTemplateRoutines Failed : " + sql1.getMessage());
			spLog = "Error occured while calling BW_DEPLOYE_USER_TEMPLATE_ROUTINES : " + sql1.getMessage();
		}
		finally
		{
			  try
			  {
					callablestatement.close();
			  }
			  catch (SQLException sql2)
			  {
				System.out.println("Error in RunImportUserTemplateRoutines : " + sql2.getMessage());
			  }
		}
		return spLog;
	}



	//After BLOB is created, all Routines needed to run for importing Deault Templates through Migration process
	public void RunImportDefaultTemplateRoutines(Connection connection, String templateName, String templateType, String templateUploadedBy, int newBlobId, String templateSize, String downloadUrl, String imageUrl)
	{
	//	System.out.println("Inside RunImportDefaultTemplateRoutines: ");
	//	System.out.println("templateName : " + templateName);
	//	System.out.println("templateType : " + templateType);
	//	System.out.println("templateUploadedBy : " + templateUploadedBy);
	//	System.out.println("newBlobId : " + newBlobId);
	//	System.out.println("templateSize : " + templateSize);
	//	System.out.println("downloadUrl : " + downloadUrl);
	//	System.out.println("imageUrl : " + imageUrl);
//		System.out.println("nhHierarchy : " + nhHierarchy);

		CallableStatement callablestatement = null;

		try
		{
			callablestatement = connection.prepareCall(CALL_BW_DEPLOYE_DEFAULT_TEMPLATE_ROUTINES);
			callablestatement.setString(1, templateName);		//Template Name	
			callablestatement.setString(2, templateType);		//XLSM File
			callablestatement.setString(3, templateUploadedBy);	//Admin
			callablestatement.setInt(4, newBlobId);				//3433
			callablestatement.setString(5, templateSize);	// 6mb
			callablestatement.setString(6, downloadUrl);	// 6mb
			callablestatement.setString(7, imageUrl);	// 6mb

//			callablestatement.setString(6, nhHierarchy);	// AcmeBank|||
			callablestatement.execute();
		}
		catch (SQLException sql1)
		{
			System.out.println("RunImportDefaultTemplateRoutines Failed : " + sql1.getMessage());
		}
		finally
		{
			  try
			  {
					callablestatement.close();
			  }
			  catch (SQLException sql2)
			  {
				System.out.println("Error in RunImportDefaultTemplateRoutines : " + sql2.getMessage());
			  }
		}
	}


	//Template is Saved as Blob in Database. BlobId is returned to caller
	public int SaveTemplateAsBlobIntoDatabase(Connection connection, String templateNameNoExt, String templateFilePath)
	{
		int tid = -1;
		int thisBlobId = -1;
		try
		{

			File file = new File(templateFilePath);
            long bytes = file.length();
            long kilobytes = (bytes / 1024);
			long megabytes = (kilobytes / 1024);

			InputStream in = new FileInputStream(file);

			String ext = "";
			int index = templateFilePath.lastIndexOf('.');
			if (index > 0) 
			{
				ext = templateFilePath.substring(index + 1);
			}

			String fileName = templateNameNoExt + "." + ext;
			String contentType = ext + " File";
			String fileSize = megabytes + " MB";

			//tm = new TransactionManager(connection, userId);
			//int tid = tm.startTransaction();
		//	System.out.println("fileName : " + fileName);
		//	System.out.println("ext : " + ext);
		//	System.out.println("contentType : " + contentType);
		//	System.out.println("filesize : " + fileSize);
								
			try
			{
		//		System.out.println("Creating a blob");
				thisBlobId = BlobManager.addDocumentToCell(
								connection,
								tid,
								in,
								(int)bytes,
								fileName,
								ext,
								contentType,
								null,
								-1,
								null,
								null
								 );
		//		System.out.println("New BlobId : " + thisBlobId);
			}
			catch (Exception e)
			{
			   e.printStackTrace();
			}
			//tm.commitTransaction();
			in.close();
		}
		catch (Exception e)
		{
			   e.printStackTrace();
		}
		return thisBlobId;
	}



	public DBcall getExternalDefinedQuery(int QueryId, String lsParameters) throws SQLException
	{
		SopLogger.debug(this.getClass(), "Inside getExternalDefinedQuery");
		ResultSet resultset = null;
		PreparedStatement ps = null;
		String queryCall = null;
		String queryParam = null;

		//if (com.boardwalk.model.Sop.isDebugEnabled()) com.boardwalk.model.Sop.debug(this.getClass(), "lsParameters = " + lsParameters);
		if(QueryId == 8)
		{
		StringTokenizer prmtr = new StringTokenizer(lsParameters, "|");
		int paramIdx = 1;
		String userIdCrit="";
		String CuboidID ="";
		String CriteriaCuboidID	="";
		
		
					while (prmtr.hasMoreElements()) {	
						if (paramIdx ==1)
						{
						userIdCrit = prmtr.nextToken();
						}
						if (paramIdx ==2)
						{
						CuboidID = prmtr.nextToken();
						}
						if (paramIdx ==3)
						{
						CriteriaCuboidID = prmtr.nextToken();
						}
						paramIdx++;
					}
						SopLogger.write2EntitlementLog(" Row Acess Mgr   :: Criteria Assigned for Table Id [" + CuboidID+"] Criteria Table Id is ["+CriteriaCuboidID+ "] by User ["+userIdCrit+"]");
					
		}	
		if(QueryId == 17)
		{
		StringTokenizer prmtr = new StringTokenizer(lsParameters, "|");
		int paramIdx = 1;
		String CuboidID ="";
					while (prmtr.hasMoreElements()) {	
						if (paramIdx ==2)
						{
						CuboidID = prmtr.nextToken();
						}
						paramIdx++;
					}
						SopLogger.write2EntitlementLog(" Row Acess Mgr   :: Criteria Removed for Table Id [" + CuboidID+"]");			
		}	

		try
		{
			DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
			connection = databaseloader.getConnection();

			resultset = null;
			ps = connection.prepareStatement(CALL_BW_EXTERNAL_QUERY);
			ps.setInt(1, QueryId);
			resultset = ps.executeQuery();
			
			
			while (resultset.next())
			{
				queryCall = resultset.getString(3);
				queryParam = resultset.getString(4);
			}

			ps.close();
			ps = null;
			resultset.close();
			resultset = null;

			SopLogger.debug(this.getClass(), "Get_Boardwalk_Template_Prop:queryCall: " + queryCall);
			//if (com.boardwalk.model.Sop.isDebugEnabled()) com.boardwalk.model.Sop.debug(this.getClass(), "queryParam : " + queryParam);
			//if (com.boardwalk.model.Sop.isDebugEnabled()) com.boardwalk.model.Sop.debug(this.getClass(), "queryCall before insert param : " + queryCall);

			if (queryParam == null) {
				SopLogger.debug(this.getClass(), "Query not found for id = " + QueryId);
			}
		}
		catch( Exception e ) {
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if (ps != null)
					ps.close();
				if (resultset != null)
					resultset.close();
				ps = null;
				resultset = null;
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}

		return new DBcall(queryCall, queryParam);
	}


	public String getPartValidationBuffer(String asRepTitle, DBcall asQuery, String lsParameters, int aiUserId)throws SQLException
	{
		PreparedStatement	statement = null;
		ResultSet			resultset = null;

		try
		{
			DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
			connection = databaseloader.getConnection();

			if(connection == null)
				SopLogger.debug(this.getClass(), "Connection is null" );
		}
		catch (SQLException sql)
		{
			sql.printStackTrace();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		ResultSetMetaData rsMetaData	= null;

		int liColCount				= -1;
		int liRowCount				= 0;

		String colName				= "";
		String colType				= "";
		String lsRowCountQuery		= "";

		HashMap colTypeCollection	= new HashMap();

		StringBuffer resData		= new StringBuffer(10000000);
		StringBuffer resDataTemp	= new StringBuffer(10000000);
		StringBuffer accData		= new StringBuffer();
		StringBuffer fmlData		= new StringBuffer();
		StringBuffer resHeader		= new StringBuffer();

		long liDateTime = Calendar.getInstance().getTimeInMillis();

		try
		{
			if(connection == null)
			{
				SopLogger.debug(this.getClass(), "Connection is closed: " + connection);
			}
			else
			{
				SopLogger.debug(this.getClass(), "asQuery.SPname = " + asQuery.SPname);
				//if (com.boardwalk.model.Sop.isDebugEnabled()) com.boardwalk.model.Sop.debug(this.getClass(), "lsParameters = " + lsParameters);
				//if (com.boardwalk.model.Sop.isDebugEnabled()) com.boardwalk.model.Sop.debug(this.getClass(), "asQuery.paramType " + asQuery.paramType);
				
				statement = connection.prepareStatement(asQuery.SPname);
				
				StringTokenizer prmtr = new StringTokenizer(lsParameters, "|");

				if (asQuery.paramType != null)
				{ 
					StringTokenizer qryprmtr = new StringTokenizer(asQuery.paramType, "|");

					String paramType=null;
					String paramValue=null;
					//String dQuote = "\"";
					int paramIdx = 1;
					
					while (prmtr.hasMoreElements()) {	
						paramType = qryprmtr.nextToken();
						paramValue = prmtr.nextToken();
						//if (com.boardwalk.model.Sop.isDebugEnabled()) com.boardwalk.model.Sop.debug(this.getClass(), "parameter type: " + paramType);
						//if (com.boardwalk.model.Sop.isDebugEnabled()) com.boardwalk.model.Sop.debug(this.getClass(), "parameter value: " + paramValue);
					
						if (paramType.equalsIgnoreCase("string")) {
							//paramValue = dQuote.concat(paramValue).concat(dQuote);
							statement.setString(paramIdx, paramValue);
						}
						else if (paramType.equalsIgnoreCase("int")) {
							statement.setInt(paramIdx, Integer.parseInt(paramValue) );
						}
						paramIdx++;
					}
				}

	//			System.out.println("Before calling  .... " + asQuery.SPname +  " ..... parameters " +  asQuery.paramType );

				writeLogline("", "", MESSAGE_TYPE_STATUS, "Stored Procedure Call [ " + asQuery.SPname +" ] with Parameters [ " + lsParameters + " ] Started...");
				resultset = statement.executeQuery();
				writeLogline("", "", MESSAGE_TYPE_STATUS, "Stored Procedure Call [ " + asQuery.SPname +" ] Completed successfully.");
				
	//			System.out.println("After calling  .... " + asQuery.SPname );
				
				rsMetaData	= resultset.getMetaData();
				liColCount	= rsMetaData.getColumnCount();

				for(int i=1; i <= liColCount  ; i++)
				{
					colName = rsMetaData.getColumnName(i);
					colType = rsMetaData.getColumnTypeName(i);

					colTypeCollection.put(new Integer(i) , colType);
					resData.append(colName + Seperator); // Only column names will be sufficent
				}
				resData.append(ContentDelimeter);

				String lsColType	= "";
				String cellval		= "";
				String cellFormula	= "";
				int	cellAccess		= 2;
				int CurrRow			= -1;

				ArrayList RepRows = new ArrayList(1000);
				ArrayList SingleRow = null; 

				while(resultset.next())
				{
					CurrRow	++;
					SingleRow = new ArrayList(liColCount);

					for(int i = 1 ; i <= liColCount; i++)
					{
						//if (com.boardwalk.model.Sop.isDebugEnabled()) com.boardwalk.model.Sop.debug(this.getClass(), " Current Count > "+i);

						lsColType = (String) colTypeCollection.get(i);
						cellval		= "";
						//if (com.boardwalk.model.Sop.isDebugEnabled()) com.boardwalk.model.Sop.debug(this.getClass(), " lsColType of the coumns  "+lsColType+" and value is "+resultset.getString(i));

						if(lsColType.equals("int identity") || lsColType.equals("int") || lsColType.equals("bit") || lsColType.equals("bigint")) //for getting int type
						{
							cellval = resultset.getInt(i)+"";
							if(BoardwalkUtility.checkIfNullOrBlank(cellval) || cellval.equals("null"))
								cellval = "";
						}
						else if(lsColType.equals("datetime")) //for getting date time type
						{
							if(resultset.getDate(i) == null)
								cellval = "";
							else
								cellval = resultset.getDate(i)+"";
						}
						else if(lsColType.equals("varchar") || lsColType.equals("nvarchar") || lsColType.equals("text") || lsColType.equals("ntext")) //for getting vharchar type
						{
							if(BoardwalkUtility.checkIfNullOrBlank(resultset.getString(i)))
								cellval = "";
							else
								cellval = resultset.getString(i);
						}
						else if(lsColType.equals("double")) //for getting double type
						{
							cellval = resultset.getDouble(i)+"";
							if(BoardwalkUtility.checkIfNullOrBlank(cellval) || cellval.equals("null"))
								cellval = "";
						}
						else if(lsColType.equals("float")) //for getting float type
						{
							cellval = resultset.getFloat(i)+"";
							if(BoardwalkUtility.checkIfNullOrBlank(cellval) || cellval.equals("null"))
								cellval = "";
						}
						else if(lsColType.equals("image")) //for getting image type
						{
							//Will this case be required
							//BufferedInputStream in = new BufferedInputStream(resultset.getBinaryStream("DOC")
							cellval = "image";
						}

						if(BoardwalkUtility.checkIfNullOrBlank(cellval))
							cellval = "";

						//lsCellRC[CurrRow][i-1]= cellval;
						SingleRow.add(cellval);
					}
					RepRows.add(SingleRow);
				}

				liRowCount = CurrRow+1;
				String lsCellRC[][] = new String[liRowCount][liColCount];
				ArrayList ReadRow = null;

				for(int rowCount = 0 ; rowCount < liRowCount ; rowCount++)
				{
					ReadRow =  (ArrayList)	RepRows.get(rowCount); 
					for( int colCount = 0 ; colCount < liColCount ; colCount++)
					{
						lsCellRC[rowCount][colCount] = (String) ReadRow.get(colCount);
					}
				}

				SopLogger.debug(this.getClass(), " Total selected Rows "+liRowCount);

				cellFormula = "";
				cellAccess	= 2; // DefaultWrite

				// prepare the buffer
				for(int colindex=0 ; colindex < liColCount; colindex++)
				{
					for(int rowindex=0; rowindex < liRowCount; rowindex++)
					{
						cellval = lsCellRC[rowindex][colindex];
						cellFormula = cellval;

						if (rowindex == liRowCount-1) // last cell of the column
						{
							resDataTemp.append(cellval);
							fmlData.append(cellFormula);
							accData.append(cellAccess);
						}
						else
						{
							resDataTemp.append(cellval + Seperator);
							fmlData.append(cellFormula + Seperator);
							accData.append(cellAccess + Seperator);
						}
					}
					resDataTemp.append(ContentDelimeter);
					fmlData.append(ContentDelimeter);
					accData.append(ContentDelimeter);
				}

				// now prepare the contents of the rowid
				for(int i=0; i < liRowCount ; i++)
					resData.append(i+1 + Seperator);

				resData.append(ContentDelimeter);

				// Tempdata which holds the cell data
				resData.append(resDataTemp.toString());
				// Formula Data
				resData.append(fmlData.toString());
				// access
				resData.append(accData.toString());

				resHeader.append("Success" + Seperator);
				resHeader.append(asRepTitle + Seperator);
				resHeader.append("For Report Sub title " + Seperator);
				resHeader.append(liDateTime+ Seperator);  // Report Date
				resHeader.append(aiUserId + Seperator); // Report Created by
				resHeader.append(liRowCount + Seperator); // number of Rows
				resHeader.append(colTypeCollection.size() + Seperator); // number of column
			}
		}
		catch(SQLException sqlexception)
		{
			System.out.println("SQLException while calline External Query: " + sqlexception.getMessage());
			sqlexception.printStackTrace();
			throw sqlexception;
		}
		finally
		{

			try{
				if(resultset != null)
					resultset.close();
				if(statement != null)
					statement.close();
				//if(connection != null)			//Commented by Rahul Varadkar on 12-May-2022
					//connection.close();			//Commented by Rahul Varadkar on 12-May-2022
			}
			catch( SQLException sql )
			{
				System.out.println("SQLException while closing recordset in External Query call: " + sql.getMessage());
				sql.printStackTrace();
			}

		}

		SopLogger.debug(this.getClass(), "Get_Boardwalk_Template_Prop:Done");

		return resHeader.toString() + ContentDelimeter + resData.toString();
	}

	//Added by Rahul Varadkar on 14-May-2022
	public String getCurrentDatabaseName() 
	{
		PreparedStatement preparedstatement = null;
		ResultSet rs = null;
		String databaseName = null;
		try
		{
			DatabaseLoader databaseloader = new DatabaseLoader(new Properties());
			connection = databaseloader.getConnection();
			String dbNameQry = "SELECT DB_NAME() DB_NAME" ;
			preparedstatement = connection.prepareStatement(dbNameQry);

			//preparedstatement.setInt(1,userId);
			rs = preparedstatement.executeQuery();
			while ( rs.next() )
				databaseName = rs.getString("DB_NAME");
			
			preparedstatement.close();
			preparedstatement = null;
		}
		catch (SQLException sqe)
		{
			System.out.println("SQLException : " + sqe.getMessage());
		}
		finally
		{
			try
			{
				if (preparedstatement != null)
				{
					preparedstatement.close();
				}
			}
			catch( SQLException sql )
			{
				System.out.println("SQLException : " + sql.getMessage());
			}
		}
		return databaseName;
	}


	class DBcall {
		String SPname;
		String paramType;
		DBcall() {}
		DBcall(String SPname, String paramType) {
			this.SPname = SPname;
			this.paramType = paramType;
		}
	}


	



}