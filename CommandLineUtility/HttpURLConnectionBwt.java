import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.FileReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Base64;
import java.util.zip.ZipOutputStream;

import javax.lang.model.util.ElementScanner6;

import java.util.zip.ZipEntry;
import java.util.List;
import java.util.Map;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;

import java.io.File;
import java.util.zip.ZipInputStream;

// java HttpURLConnectionBwt GN boardwalk "AcmeBank|GN||" "migration1 (1).zip"
// java HttpURLConnectionBwt process.manager boardwalk "AcmeBank|BU-1||" DTX1_NH_UM_TMP.zip

public class HttpURLConnectionBwt {

	private static final String USER_AGENT = "Mozilla/5.0";

//	private static final String GET_URL = "http://localhost:8080/BW_VELOCITY_DEV/MyCollaborations";
//	private static final String POST_URL = "http://localhost:8080/BW_VELOCITY_DEV/MyTemplates";

    private static final String DEPLOY_PACKAGE_URL = "http://localhost:8080/BW_VELOCITY_TARGET/xlMigrationServiceExt";
//    private static final String BOARDWALK_LOGIN_URL = "http://localhost:8080/BW_VELOCITY_DEV/MyCollaborations";

//	private static final String POST_PARAMS = "userName=Pankaj";
    private static final String BOUNDRY = "---------------------------0123456789012";

	public final static String Seperator = new Character((char)1).toString();
	public final static String ContentDelimeter = new Character((char)2).toString();
	public final static String DataBlockSeperator = new Character((char)3).toString();
	public final static String PipeDelimeter = new Character((char)124).toString();
	public final static String tab = "\u0009";
    private static final String BOARDWALK_LOGIN_URL = "http://localhost:8080/BW_VELOCITY_TARGET/httpt_vb_Login";
	private static final String BOARDWALK_LOGOUT_URL = "http://localhost:8080/BW_VELOCITY_TARGET/xlLogoutService";

	//    private static final String BOARDWALK_LOGIN_URL = "https://pe.boardwalktech.com/BW_VELOCITY_48_TRG_REL/httpt_vb_Login";
	
    private static final String LOGIN_BUFFER_PATTERN =  "{user}"+Seperator+"{password}"+Seperator+"DESIGN"+Seperator+"{NhHierarchy}"+Seperator;
    // Buffer : process.managerboardwalkDESIGNAcmeBank|BU-1||

	private static URL objUrl = null;
	private static HttpURLConnection con = null;

	private static String SESSION_ID = null;

	public static void main(String[] args) throws IOException 
    {
//		sendGET();
//		System.out.println("GET DONE");

//		System.out.println("--------------------------------------------  SENDING POST REQUEST ----------------------------------------------------");
//		sendPOST();
//		System.out.println("POST DONE");

		System.out.println("");
		System.out.println("");
		System.out.println("---------------------------------------------------------------------------------------------------------------------------------------");
      	System.out.println("--------------------------------------------  BOARDWALK PACKAGE DEPLOYMENT UTILITY ----------------------------------------------------");
		System.out.println("---------------------------------------------------------------------------------------------------------------------------------------");
		System.out.println("");
		System.out.println("");

		//Check if all Arguments are Valid
		if (args.length < 4)
		{
			System.out.println("Syntax : java HttpURLConnectionBwt <userName> <password> <nhhierarchy> <packageFile>");
			System.out.println( "Example : java HttpURLConnectionBwt -process.manager boardwalk \"AcmeBank|BU-1||\"  migrateEUC.zip ");
			return;
		}


		String user = args[0];
		String pwd = args[1];
		String nhHierarchy = args[2];
		String packageFile = args[3];


		// Check if PackageFile Exists on given location
		if (!checkIfFileExists(packageFile))
		{
			System.out.println("Missing Package File. Check If Package File Exists and Give Correct FileName Path.");
			return;
		}
		else
		{
			//Get Confirmation message here from user to Continue!!!
			// System.out.println("user " + user);
			// System.out.println("pwd " + pwd);
			// System.out.println("nhHierarchy " + nhHierarchy);
			// System.out.println("packageFile " + packageFile);

		}

		String loginResponse = null;
		String fixedUser = "-" + user;
		loginResponse = loginToBoardwalkServer(fixedUser, pwd, nhHierarchy);

		if (!loginResponse.toUpperCase().startsWith("SUCCESS")) {
			System.out.println("loginResponse : " + loginResponse);
			return;
		}

		System.out.println("loginResponse : " + loginResponse);

		String content_type = "multipart/form-data; boundary=" + BOUNDRY;

		String[] splitString = nhHierarchy.split("\\|");
		String nh0 = splitString[0];

		String strAuthorization;
		// strAuthorization =
		// getBase64EnCodedString("process.manager:boardwalk:AcmeBank");
//		strAuthorization = getBase64EnCodedString(user.substring(1) + ":" + pwd + ":" + nhHierarchy);

		// System.out.println("nh0 : " +  nh0);
		String sAuthKey = user + ":" + pwd + ":" + nh0;
		strAuthorization = getBase64EnCodedString(sAuthKey);
		System.out.println("sAuthKey : " + sAuthKey);
		System.out.println("strAuthorization : " + strAuthorization);

		String deploymentLog = null;
		deploymentLog = deployPackageOnBoardwalkServer("POST", DEPLOY_PACKAGE_URL, packageFile, strAuthorization);

		if (deploymentLog.toUpperCase().startsWith("SUCCESS"))
			displayDeploymentLog(deploymentLog);
		else
			System.out.println(deploymentLog);

		System.out.println("Calling logout routines...");
		logoutToBoardwalkServer();

    }

	private static boolean checkIfFileExists(String filePathName)
	{
		File file = new File(filePathName);
		return file.exists();
	}

/*
 * private static void sendGET() throws IOException
 * {
 * URL obj = new URL(GET_URL);
 * HttpURLConnection con = (HttpURLConnection) obj.openConnection();
 * con.setRequestMethod("GET");
 * con.setRequestProperty("User-Agent", USER_AGENT);
 * int responseCode = con.getResponseCode();
 * System.out.println("GET Response Code :: " + responseCode);
 * if (responseCode == HttpURLConnection.HTTP_OK)
 * { // success
 * BufferedReader in = new BufferedReader(new
 * InputStreamReader(con.getInputStream()));
 * String inputLine;
 * StringBuffer response = new StringBuffer();
 * 
 * while ((inputLine = in.readLine()) != null)
 * {
 * response.append(inputLine);
 * }
 * in.close();
 * 
 * // print result
 * System.out.println(response.toString());
 * } else
 * {
 * System.out.println("GET request not worked");
 * }
 * }
 */
	//Logout from Boardwalk Server
	private static String logoutToBoardwalkServer()
	{
		String logoutBuffer = "1" + Seperator;

		String fixedLogoutBuffer = "-" + logoutBuffer;

		String responseBuffer = null;
		try {
			// System.out.println("login buffer pattern : " + LOGIN_BUFFER_PATTERN);

			// String loginBuffer = LOGIN_BUFFER_PATTERN;
			// loginBuffer = loginBuffer.replace("{user}", userName);
			// loginBuffer = loginBuffer.replace("{password}", pwd);
			// loginBuffer = loginBuffer.replace("{NhHierarchy}", nhHierarchy);

			// System.out.println("fixedLogoutBuffer : " + fixedLogoutBuffer);

			String compressedBuffer = compressBuffer(fixedLogoutBuffer);
			String contentLength = Integer.toString(compressedBuffer.length());

			// System.out.println("compressedBuffer : " + compressedBuffer);
			// System.out.println("compressedBuffer.length : " + compressedBuffer.length());
			// System.out.println("Content-Length : " + contentLength);

			int timeOut = 5000; // 5 Seconds

			objUrl = new URL(BOARDWALK_LOGOUT_URL);
			con = (HttpURLConnection) objUrl.openConnection();

			con.setConnectTimeout(timeOut);
			con.setRequestMethod("POST");
			con.setRequestProperty("User-Agent", USER_AGENT);
			// con.setRequestProperty("Content-Type", content_type);
			con.setRequestProperty("Content-Length", contentLength);
			// con.setRequestProperty("Authorization", strAuthorization);
			con.setRequestProperty("X-client", "WinExcel");
			 con.setRequestProperty("Cookie", "JSESSIONID=" + SESSION_ID);

			System.out.println("Connectiong to :" + BOARDWALK_LOGOUT_URL);
			// System.out.println("logoutBuffer : " + logoutBuffer);

			// System.out.println("Before calling connection............");

			con.setDoOutput(true);
			OutputStream os = con.getOutputStream();
			os.write(compressedBuffer.getBytes());
			os.flush();
			os.close();

			// System.out.println("After calling connection............");
			if (con.getResponseCode() == HttpURLConnection.HTTP_OK) // 200
			{
				// System.out.println("Status Code : " + con.getResponseCode());
				BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
				String inputLine;
				StringBuffer response = new StringBuffer();
				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
				in.close();
				// System.out.println(response.toString());

				responseBuffer = unCompressBuffer(response.toString(), "response.txt", false);
				// System.out.println("responseBuffer : " + responseBuffer);


				if (responseBuffer.toUpperCase().startsWith("SUCCESS")) 
					System.out.println("You have been logged out Successfully on Server : " + SESSION_ID);
				else
					System.out.println("User log out Failed on Server : " + SESSION_ID);

			} else {
				System.out.println("Status Code : " + con.getResponseCode());
				System.out.println("Response: " + con.getResponseMessage()  );
			}

		} 
		catch (Exception e) 
		{
			System.out.println(e.getMessage());
		}

		return responseBuffer;
		// Buffer : process.managerboardwalkDESIGNAcmeBank|BU-1||

		// response = HTTP.callBoardwalk("httpt_vb_Login", reqBuffer, True)

	}

    private static String loginToBoardwalkServer(String userName, String pwd, String nhHierarchy)
    {
		String responseBuffer = null;
		try 
		{
			// System.out.println("login buffer pattern : " + LOGIN_BUFFER_PATTERN);

			String loginBuffer = LOGIN_BUFFER_PATTERN;
			loginBuffer = loginBuffer.replace("{user}", userName);
			loginBuffer = loginBuffer.replace("{password}", pwd);
			loginBuffer = loginBuffer.replace("{NhHierarchy}", nhHierarchy);

			// System.out.println("loginBuffer : " + loginBuffer);

			String compressedBuffer = compressBuffer(loginBuffer);
			String contentLength = Integer.toString(compressedBuffer.length());

			// System.out.println("compressedBuffer : " + compressedBuffer);
			// System.out.println("compressedBuffer.length : " + compressedBuffer.length());
			// System.out.println("Content-Length : " + contentLength);

			//LOGIN_BUFFER_PATTERN =  "{user}"+Seperator+"{password}"+Seperator+"DESIGN"+Seperator+"{NhHierarchy}"+Seperator;
			// Buffer : process.managerboardwalkDESIGNAcmeBank|BU-1||

			int timeOut = 5000;				//5 Seconds

			objUrl = new URL(BOARDWALK_LOGIN_URL);
			con = (HttpURLConnection) objUrl.openConnection();

			con.setConnectTimeout(timeOut);
			con.setRequestMethod("POST");
			con.setRequestProperty("User-Agent", USER_AGENT);
//			con.setRequestProperty("Content-Type", content_type); 
			con.setRequestProperty("Content-Length",   contentLength); 
//			con.setRequestProperty("Authorization", strAuthorization);
			con.setRequestProperty("X-client", "WinExcel");
	//        con.setRequestProperty("Cookie", "JSESSIONID=" & ssid);

			System.out.println("Connectiong to :" + BOARDWALK_LOGIN_URL );
			// System.out.println("loginBuffer : " + loginBuffer);

			// System.out.println("Before calling connection............");

			con.setDoOutput(true);
			OutputStream os = con.getOutputStream();
			os.write(compressedBuffer.getBytes());
			os.flush();
			os.close();

			// System.out.println("After calling connection............");
			if (con.getResponseCode() == HttpURLConnection.HTTP_OK)	// 200
			{
				System.out.println("Status Code : " + con.getResponseCode());
				BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
				String inputLine;
				StringBuffer response = new StringBuffer();
				while ((inputLine = in.readLine()) != null)
				{
					response.append(inputLine);
				}
				in.close();
				// System.out.println(response.toString());

				responseBuffer = unCompressBuffer(response.toString(), "response.txt", false);
				// System.out.println("responseBuffer : " + responseBuffer);

				if (responseBuffer.toUpperCase().startsWith("SUCCESS"))
				{
					readSessionIdFromResponseHeader();

					if (SESSION_ID == null) {
						System.out.println(
								"Faile to Create a Session on Server!!!  Try Again. or Contact System Administrator.");
						return null;
					} else
						System.out.println("Session Created Successfully on Server : " + SESSION_ID);
				}
			}
			else
				System.out.println("Status Code : " + con.getResponseCode());
		} 
		catch (Exception e) 
		{
			System.out.println(e.getMessage());
		}
		return responseBuffer;
    }

	// Reead Session Id from Response Header into Global Variable
	public static void readSessionIdFromResponseHeader()
	{
		Map<String, List<String>> map = con.getHeaderFields();
		List<String> setCookie = map.get("Set-Cookie");

		if (setCookie.get(0) == null) {
			System.out.println("Cookies Not Found in Response. ");
			return ;
		} else {
	//		System.out.println("Cookie Found : " + setCookie.get(0));
			String SessionCookie = setCookie.get(0);
			int SessionIdStart = -1;
			int SessionIdEnd = -1;
			SessionIdStart = SessionCookie.indexOf("=") + 1;
			SessionIdEnd = SessionCookie.indexOf(";");
//			System.out.println("SessionIdStart : " + SessionIdStart);
//			System.out.println("SessionIdEnd : " + SessionIdEnd);
//			System.out.println("JSESSIONID : " + SessionCookie.substring(SessionIdStart, SessionIdEnd));
			SESSION_ID = SessionCookie.substring(SessionIdStart, SessionIdEnd);
		}
	}

	//Compressed Response from URL is passed to this function and it returns unCompressed String
	public static String unCompressBuffer(String compressedBuffer, String filename, boolean blnReader)
	{
		// System.out.println("......................UnCompressing Response Buffer Begin................... ");
        Base64.Decoder decoder = Base64.getDecoder();  
		byte[] byteArray;
		byteArray = decoder.decode(compressedBuffer);
		// System.out.println("byteArray : " + byteArray.toString());

		String pathSeperator = System.getProperty("file.separator");
		String dir = getCurrentWorkingDirectory();
		long randomNo = System.currentTimeMillis();
		String zipFilename = dir + pathSeperator + "bw_" +  randomNo + ".zip";

		// System.out.println("zipFileName : " + zipFilename);

		// System.out.println("Writing Zip file ...started  : " + zipFilename);
		//Path path = Paths.get(zipFilename);
		try {
			File zipfile = new File(zipFilename);
			OutputStream os = new FileOutputStream(zipfile);
			os.write(byteArray);
			os.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// System.out.println( "Writing Zip file ...complete");
		
		String unZipFolder = dir + pathSeperator + "bw_" + randomNo;
		String unZippedFileName = unZipFolder + pathSeperator + filename;
		// System.out.println("unzipFoldre :" + unZipFolder);
		// System.out.println("unZippedFileName :" + unZippedFileName);

		unzip(zipFilename, unZipFolder);

		String uncompressBuffer = readFileUTF8(unZippedFileName);

		// Deleting Zip files
		File fz = new File(zipFilename);
		fz.delete();
		// if (!fz.delete())
		// 	System.out.println("");		//"File " + zipFilename + " failed to delete.");

		File f = new File(unZippedFileName);
		f.delete();
		// if (!f.delete())
		// 	System.out.println("");  //"File " + unZippedFileName + " failed to delete.");

		//Deleting UnZipFolder  
		File unzipFolderPath = new File(unZipFolder);
		deleteFolder(unzipFolderPath);
		//System.out.println("Folder " + unZipFolder + " deleted successfully........");

		// System.out.println("......................UnCompressing Response Buffer End................... ");
		return uncompressBuffer ;
	}

	private static void unzip(String zipFilePath, String destDir) 
	{
		File dir = new File(destDir);
		// create output directory if it doesn't exist
		if (!dir.exists())
			dir.mkdirs();
		FileInputStream fis;
		// buffer for read and write data to file
		byte[] buffer = new byte[1024];
		try {
			fis = new FileInputStream(zipFilePath);
			ZipInputStream zis = new ZipInputStream(fis);
			ZipEntry ze = zis.getNextEntry();
			while (ze != null) {
				String fileName = ze.getName();
				File newFile = new File(destDir + File.separator + fileName);
				// System.out.println("Unzipping to " + newFile.getAbsolutePath());
				// create directories for sub directories in zip
				new File(newFile.getParent()).mkdirs();
				FileOutputStream fos = new FileOutputStream(newFile);
				int len;
				while ((len = zis.read(buffer)) > 0) {
					fos.write(buffer, 0, len);
				}
				fos.close();
				// close this ZipEntry
				zis.closeEntry();
				ze = zis.getNextEntry();
			}
			// close last ZipEntry
			zis.closeEntry();
			zis.close();
			fis.close();
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	//Writes buffer in UTF8 file. Zip that UTF8 file 
	public static String compressBuffer(String buffer)
	{	
		String pathSeperator = System.getProperty("file.separator");
		String dir = getCurrentWorkingDirectory();
		long randomNo = System.currentTimeMillis();
		String utfFile = dir + pathSeperator + randomNo +  "buffer.txt";
		String zipFile = dir + pathSeperator + randomNo +  "buffer.zip";
		writeFileUTF8(buffer, utfFile);
		zipFile(utfFile, zipFile);
		//Read Zip fiel into Byte Array
		long fileSize = new File(zipFile).length();
		byte[] allBytes = new byte[(int) fileSize];
		try { 
			InputStream inputStream = new FileInputStream(zipFile); 
			int byteRead = inputStream.read(allBytes);
		} 
		catch (Exception ioExp) {ioExp.printStackTrace(); }

		// Deleting Zip file and UTF file
		File f = new File(utfFile);
		if (!f.delete())
			System.out.println("UTF File " + utfFile + " Failed to delete.");
		// else
		// 	System.out.println("UTF File " + utfFile + " Failed to Delete........@#$@#");

		File fz = new File(zipFile);
		fz.delete();
		// if (!fz.delete())
		// 	System.out.println("Zip File " + zipFile + " Failed to delete.");
		// else
		// 	System.out.println("Zip File " + zipFile + " Failed to Delete........@#$@#");

		String base64 = getBase64EnCodedStringOfByteArray(allBytes);
		return base64;
	}

	static final int BUFFER = 1024;

	//Zip dataFile to ZipFile
	public static void zipFile(String dataFileName, String zipFileName)
	{
		ZipOutputStream zos = null;
		BufferedInputStream bis = null;
		FileInputStream fis  = null;
		FileOutputStream fos = null;

		try {
			File dataFile = new File(dataFileName);
			fis = new FileInputStream(dataFile);
			bis = new BufferedInputStream(fis, BUFFER);
			fos = new FileOutputStream(zipFileName);
			zos = new ZipOutputStream(fos);
			ZipEntry ze = new ZipEntry(dataFile.getName());
			zos.putNextEntry(ze);
			byte data[] = new byte[BUFFER];
			int count;
			while ((count=bis.read(data,0,BUFFER)) != -1)
			{
				zos.write(data, 0, count);
			}
		}
		catch (IOException ioExp) { System.out.println("Error while zipping : " + ioExp.getMessage()); }
		finally {
			if(zos != null) {
				try{zos.close();}
				catch (IOException ioExp){ ioExp.printStackTrace();}
			}
			if(bis != null) {
				try{bis.close();}
				catch (IOException ioExp){ ioExp.printStackTrace();}
			}

			if (fis != null) {
				try {
					fis.close();
				} catch (IOException ioExp) {
					ioExp.printStackTrace();
				}
			}
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException ioExp) {
					ioExp.printStackTrace();
				}
			}
		}
	}
	
	//Returns current working directory
	public static String getCurrentWorkingDirectory()
	{
		return (String) System.getProperty("user.dir");
	}

	//Read Unzipped filename into String
	public static String readFileUTF8(String fileName)
	{
		StringBuilder builder = new StringBuilder();
		try {
			BufferedReader buffer = new BufferedReader(new FileReader(fileName));
			String str;
            while ((str = buffer.readLine()) != null) 
			{
                builder.append(str).append("\n");
            }
			buffer.close();
		} 
		catch(IOException ioe)
		{
			ioe.printStackTrace();
		}
		return builder.toString();
	}

	//Write String into File in UTF8 Format
	public static void writeFileUTF8(String buffer, String fileName)
	{
		try {
			Path path = Paths.get(fileName);
			BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8);
			writer.append(buffer);
			writer.flush();
		}
		catch (IOException ioe) {
				System.out.println(ioe.getMessage());
		}
	}

    private static String  deployPackageOnBoardwalkServer(String requestMethod, String migrationURL, String packageFile, String strAuthorization) throws IOException
    {
		//("POST", DEPLOY_PACKAGE_URL, packageFile, strAuthorization)
		objUrl = new URL(migrationURL);
        con = (HttpURLConnection) objUrl.openConnection();

		int timeout = 1800000;
		StringBuilder responseBody = null;

		con.setDoOutput(true);
		
		con.setRequestProperty("User-Agent", USER_AGENT);
		con.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDRY );
		con.setRequestProperty("X-client", "WinExcel");
		con.setRequestProperty("Authorization", strAuthorization);
		con.setRequestProperty("Cookie", "JSESSIONID=" + SESSION_ID);

		con.setRequestMethod(requestMethod);			//POST
		con.setConnectTimeout(timeout);
		con.setReadTimeout(timeout);

		String charset = "UTF-8";
		String CRLF = "\r\n"; // Line separator required by multipart/form-data.
		String content = "application/octet-stream" ;

		try 
		{
			File binaryFile = new File(packageFile);
			String filename = binaryFile.getName();
			System.out.println("filename : " + filename);
			System.out.println("The Pacakge is uploaded on Server and Deployment Process has started. Please wait ....");
			try (
				OutputStream output = con.getOutputStream();
				PrintWriter writer = new PrintWriter(new OutputStreamWriter(output, charset), true);
			)
			{
				// Send binary file.
				writer.append("--" + BOUNDRY).append(CRLF);
				writer.append("Content-Disposition: form-data; name=\"File\"; ").append(CRLF);
				writer.append(" filename=\"" + filename + "\" ").append(CRLF);
				writer.append("Content-Transfer-Encoding: binary").append(CRLF);
				writer.append(" Content-Type:" + content).append(CRLF);
				writer.append(CRLF).flush();
				Files.copy(binaryFile.toPath(), output);
				output.flush(); 						// Important before continuing with writer!

				writer.append(CRLF).flush(); 			// CRLF is important! It indicates end of boundary.
				// End of multipart/form-data.
				writer.append("--").append(BOUNDRY).append("--").append(CRLF).flush();

				int responseCode = con.getResponseCode();
				System.out.println("Statu Code : " + responseCode); // Should be 200

                // Buffering response body
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader((con.getInputStream())));
                responseBody = new StringBuilder();
                String responseBodyLine;
                while ((responseBodyLine = bufferedReader.readLine()) != null) 
				{
                    responseBody.append(responseBodyLine);
				}
                //System.out.println("Server returned http status "
                 //       + responseCode
                  //      + " from url "
                   //     + " with response body "
                     //   + responseBody.toString());
			}				
		}
		catch (Exception e) 
		{
			e.printStackTrace();			
		}

		String responseBuffer = null;
		responseBuffer = unCompressBuffer(responseBody.toString(), "response.txt", false);
	//	System.out.println("responseBuffer : " + responseBuffer);

		return responseBuffer;
	}


	private static void displayDeploymentLog(String deploymentLog)
	{
		String[] arrLogLines = deploymentLog.split(ContentDelimeter);
		String[] arrLineColumns;
		
		System.out.println("=======================================================================================================================================");
		for(int i=1; i < arrLogLines.length;i++)
		{	
			//System.out.println(arrLogLines[i]);
			arrLineColumns =  arrLogLines[i].split(Seperator);
			// for (int j=0 ; j < arrLineColumns.length;j++)
			// {
			// 	System.out.print(   pad(arrLineColumns[j], 15, " ", "left"));
			// }
			System.out.print(pad(arrLineColumns[0], 19, " ", "right",2));  //Date
			// System.out.print(" ");
			System.out.print(pad(arrLineColumns[1], 16, " ", "right",2));  //Command
			// System.out.print(" ");
			System.out.print(pad(arrLineColumns[3], 7, " ", "right",2));  //Status
			// System.out.print(" ");
			System.out.print(pad(arrLineColumns[4], 120, " ", "right",2)); //Log
			// System.out.println();
//			System.out.println(   pad(arrLineColumns[0], 15, " ", "left") + tab +
//					pad(arrLineColumns[2], 15, " ", "left") + tab +
//					pad(arrLineColumns[3], 15, " ", "left") );
			if (i == 1) 
			{
				System.out.println("");
				System.out.println("=======================================================================================================================================");
			}
			else
				System.out.println("");
				
		}
		System.out.println("=======================================================================================================================================");
	}

	private static void extracted2() {
		extracted();
	}

	private static void extracted() {
		System.out.println();
	}


/*
 * private static void sendPOST() throws IOException
 * {
 * URL obj = new URL(POST_URL);
 * HttpURLConnection con = (HttpURLConnection) obj.openConnection();
 * con.setRequestMethod("POST");
 * con.setRequestProperty("User-Agent", USER_AGENT);
 * 
 * // For POST only - START
 * con.setDoOutput(true);
 * OutputStream os = con.getOutputStream();
 * os.write(POST_PARAMS.getBytes());
 * os.flush();
 * os.close();
 * // For POST only - END
 * 
 * int responseCode = con.getResponseCode();
 * System.out.println("POST Response Code :: " + responseCode);
 * 
 * if (responseCode == HttpURLConnection.HTTP_OK) { //success
 * BufferedReader in = new BufferedReader(new InputStreamReader(
 * con.getInputStream()));
 * String inputLine;
 * StringBuffer response = new StringBuffer();
 * 
 * while ((inputLine = in.readLine()) != null) {
 * response.append(inputLine);
 * }
 * in.close();
 * 
 * // print result
 * System.out.println(response.toString());
 * } else {
 * System.out.println("POST request not worked");
 * }
 * }
 */
	//Return  byteArray Encoded into Base64
	public static String getBase64EnCodedStringOfByteArray(byte[] byteArray)
    {
        // Getting encoder  
        Base64.Encoder encoder = Base64.getEncoder();  
		return encoder.encodeToString(byteArray); 
	}

	//Convert String to Base64 and Return
    public static String getBase64EnCodedString(String inputString)
    {
        // Getting encoder  
        Base64.Encoder encoder = Base64.getEncoder();  
        return encoder.encodeToString(inputString.getBytes()); 
    }

	//Decode Base64 String and return 
    public static String getBase64DeCodedString(String base64String)
    {
        // Getting decoder  
        Base64.Decoder decoder = Base64.getDecoder();  
        // Decoding string  
        return (new String(decoder.decode(base64String)));  
    }

	//Delete folder recursively
	static void deleteFolder(File file) 
	{
		for (File subFile : file.listFiles()) 
		{
			if (subFile.isDirectory()) 
			{ deleteFolder(subFile);} 
			else 
			{ subFile.delete(); }
		}
		file.delete();
	}

	/*
	 * Pad Left or Right to a String with <padChar>.
	 * pad(name, 100, ".", "right")
	 * pad(name, 100, ".", "right"));
	 * pad(name, 10, ".", "right")
	 * pad(name, 10, ".", "left")
	 */
	//pad(arrLineColumns[0],16," ","right",2))
	private static String pad(String strInput, int maxlen, String padChar, String padType, int gapSize) 
	{
		//Generate Gap of Spaces after Every printed field
		StringBuffer gapString = null;
		gapString = new StringBuffer();
		for (int i = 0; i < gapSize; i++)
			gapString.append(" ");		

		if (strInput.length() >= maxlen) 
		{
			strInput = strInput.substring(0, maxlen) + gapString.toString();
			return strInput;
		} 
		else 
		{
			if (strInput.length()==0)
			{
				strInput = new String(new char[maxlen+gapSize]).replace('\0', ' ');
				return strInput;
			}

			StringBuffer paddedString = null;
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < (maxlen - strInput.length()); i++)
				sb.append(padChar);

			if (padType.equals("right")) {
				paddedString = new StringBuffer();
				paddedString.append(strInput);
				paddedString.append(sb.toString());
			} else if (padType.equals("left")) {
				paddedString = new StringBuffer();
				paddedString.append(sb.toString());
				paddedString.append(strInput);
			}
			String strReturn =  paddedString.toString() + gapString.toString();
			// System.out.println("---" + strReturn + "---");
			// System.out.println("cutsize " + (maxlen + gapSize)) ;
			return strReturn;
		}
	}

}