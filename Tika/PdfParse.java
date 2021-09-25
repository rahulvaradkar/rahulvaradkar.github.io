import java.io.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.BodyContentHandler;

// for PDF parser
import org.apache.tika.parser.pdf.PDFParser;

import org.apache.tika.Tika;

//For autodetect parser
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.AutoDetectParser;


import org.xml.sax.SAXException;

import java.util.*;
import java.net.*;
import java.text.*;
import java.util.Scanner;


import java.util.zip.*;
import sun.misc.BASE64Encoder;
import org.apache.axis.encoding.Base64;



public class PdfParse {

	private final static String Seperator = new Character((char)1).toString();
	private final static String ContentDelimeter = new Character((char)2).toString();
	private static final int BLOCK_SIZE = 1024000;

	static String serverURL;
	static int MAX_THREADS ;
	static int MAX_JOBS ;
	static String JOB_FILE_NAME;
	static String LINK_EXPORT_USERS_FILE;
	static String LINK_EXPORT_DATA_FILE_NAMES;
	static String LINK_EXPORT_HEADER_FILE;
	static String [][] taskArray;
	static JobDetails jdArray[][];

	static String [] linkExportHeader;
	static String [] linkExportTableBuffer;

	static String linkExportPrefix;
	static String ResultFileName;

	private static class WorkerThread extends Thread 
	{
		String username;
		String output;
		String[] threadTaskArray;
		JobDetails[] threadJDArray;
		String linkImportHeaderForThread;
		String linkExportHeaderForThread;
		String createTableHeaderForThread;
		String linkExportTableBufferForThread;
		String refreshHeaderForThread;
		String submitHeaderForThread;
		int colCount ;
		int rowCount ;
		static volatile int leSequence;


		public WorkerThread(String[] ourArray, String liHeader, String leHeader, String letHeader, String reHeader, String suHeader, JobDetails[] jdArrayForThread, int colCount, int rowCount) 
		{
			this.threadTaskArray = ourArray;
			this.linkImportHeaderForThread = liHeader;
			this.linkExportHeaderForThread = leHeader;
			this.linkExportTableBufferForThread = letHeader;

			this.refreshHeaderForThread = reHeader;
			this.submitHeaderForThread = suHeader;

//			this.threadJDArray[MAX_JOBS] = new JobDetails();
			System.out.println("before Setting threadJDArray for individual thread");
			this.threadJDArray = jdArrayForThread;
			System.out.println("after Setting threadJDArray for individual thread");
		
			this.colCount = colCount;
			this.rowCount = rowCount;
		}

		public WorkerThread() 
		{
		}

		public static void setlinkExportSequence(int setValue)
		{
			leSequence = setValue;
		}

		//synchronized
		public static void incrementSequence()
		{
			leSequence++;
			try {
				System.out.println("Writing Setting.ini -> LinkExportTableSequenceStart before ........... ");
				// Properties p = new Properties();
				// p.load(new FileInputStream("Settings.ini"));
				// p.setProperty ("LinkExportTableSequenceStart", Integer.toString(leSequence));
		        FileInputStream in = new FileInputStream("Settings.ini");
		        Properties props = new Properties();
		        props.load(in);
		        in.close();

		        FileOutputStream out = new FileOutputStream("Settings.ini");
		        props.setProperty("LinkExportTableSequenceStart", Integer.toString(leSequence));
		        props.store(out, null);
		        out.close();
				System.out.println("Writing Setting.ini -> LinkExportTableSequenceStart after ...........");
			}
			catch (Exception e) 
			{
				System.out.println("Writing Setting.ini -> Error.............................");
				System.out.println(e.toString());
				e.printStackTrace();
			}
		}

		public int getLinkExportSequence()
		{
			return leSequence;
		}

		// Find the maximum value in our particular piece of the array
		public void run() 
		{
			for (int i = 0; i < threadTaskArray.length; i++)
			{
				System.out.println(Thread.currentThread().getName() + "____" + threadTaskArray[i]);
				if (threadTaskArray[i].equals("LE"))
				{

				}
			}
		}

		public String getOutput() 
		{
			return output;
		}


		public String getCurrentDateTime()
		{
			DateFormat dateFormatter;
			dateFormatter = DateFormat.getDateInstance(DateFormat.MEDIUM);
			
			Date today = new Date();
			String dateOut;
			dateOut = dateFormatter.format(today);

			DateFormat timeFormatter =  DateFormat.getTimeInstance(DateFormat.DEFAULT);
			String timeOut;
			timeOut = timeFormatter.format(today);

			return (dateOut + " " + timeOut);
		}

		public synchronized void printJobDetailHeader()
		{
			try
			{
				FileWriter fstream = new FileWriter(ResultFileName);
				BufferedWriter out = new BufferedWriter(fstream);
				//Close the output stream

				StringBuffer JobDetailLine;
				JobDetailLine = new StringBuffer("ThreadName|UserID|UserName|MemberID|JobStartTime|JobEndTime|JobType|JobFileName|TableID|TableName|RowCount|ColCount|FormulaCount|DataSizeSent|DataSizeReceived|JobStatus|TxID" + '\n');
				out.write(JobDetailLine.toString());
				out.close();
				fstream.close();
			}
			catch (Exception e)
			{
				System.out.println(e.toString());
				e.printStackTrace();
			}
		}


		public synchronized void printJobDetails()
		{
			try
			{
				FileWriter fstream = new FileWriter(ResultFileName, true);
				BufferedWriter out = new BufferedWriter(fstream);
				//Close the output stream


				StringBuffer JobDetailLine;

				System.out.println("Job Details of Thread : " + this.getName());
				System.out.println("threadJDArray.length = " + threadJDArray.length);
				for(int threadCount=0; threadCount < threadJDArray.length; threadCount++)
				{
					JobDetailLine = new StringBuffer();
					JobDetailLine = JobDetailLine.append(threadJDArray[threadCount].ThreadName + '|');
					JobDetailLine = JobDetailLine.append(threadJDArray[threadCount].UserID + '|');
					JobDetailLine = JobDetailLine.append(threadJDArray[threadCount].UserName + '|');
					JobDetailLine = JobDetailLine.append(threadJDArray[threadCount].MemberID + '|');
					JobDetailLine = JobDetailLine.append(threadJDArray[threadCount].JobStartTime+ '|');
					JobDetailLine = JobDetailLine.append(threadJDArray[threadCount].JobEndTime + '|');
					JobDetailLine = JobDetailLine.append(threadJDArray[threadCount].JobType + '|');
					JobDetailLine = JobDetailLine.append(threadJDArray[threadCount].JobFileName + '|');
					JobDetailLine = JobDetailLine.append(threadJDArray[threadCount].TableID + '|');
					JobDetailLine = JobDetailLine.append(threadJDArray[threadCount].TableName + '|');
					JobDetailLine = JobDetailLine.append(threadJDArray[threadCount].RowCount + '|');
					JobDetailLine = JobDetailLine.append(threadJDArray[threadCount].ColCount + '|');
					JobDetailLine = JobDetailLine.append(threadJDArray[threadCount].FormulaCount + '|');
					JobDetailLine = JobDetailLine.append(threadJDArray[threadCount].DataSizeSent + " | ");
					JobDetailLine = JobDetailLine.append(threadJDArray[threadCount].DataSizeReceived + " | ");
					JobDetailLine = JobDetailLine.append(threadJDArray[threadCount].JobStatus + " | ");
					JobDetailLine = JobDetailLine.append(threadJDArray[threadCount].TxID + '\n');

					out.write(JobDetailLine.toString());				
				}

				out.close();
				fstream.close();
			}
			catch (Exception e)
			{
				System.out.println(e.toString());
				e.printStackTrace();
			}

		}

	}


	public static void main(final String[] args) throws IOException,TikaException, SAXException 
	{


		//assume example.mp3 is in your current directory
		// File file = new File("teva po.pdf");//
		File file = new File(args[0]);//

		//Instantiating tika facade class 
		Tika tika = new Tika();

		//detecting the file type using detect method
		String filetype = tika.detect(file);
		System.out.println(filetype);

		String ft = getFileType(args[0]);
		System.out.println("filetype :" + ft);

		String fn = args[0];
		System.out.println("fn :" + fn);

        pressAnyKey();

        String fileContents = tika.parseToString(file);
        System.out.println("Extracted Contents : " + fileContents);

        pressAnyKey();

		BodyContentHandler handler = new BodyContentHandler();
		Metadata metadata = new Metadata();
		// FileInputStream inputstream = new FileInputStream(new File("teva po.pdf"));
		FileInputStream inputstream = new FileInputStream(file);
		ParseContext pcontext = new ParseContext();

		//parsing the document using PDF parser
//		System.out.println("Parsing being done using PDFParser");
//		pressAnyKey();

//		PDFParser pdfparser = new PDFParser(); 
//		pdfparser.parse(inputstream, handler, metadata, pcontext);

//		pressAnyKey();

		System.out.println("Parsing being done using AutoDetectParser");
		System.out.println("Re-initializing handler, metadata, inputstream, pcontext");
		pressAnyKey();

		handler = new BodyContentHandler();
		metadata = new Metadata();
		inputstream = new FileInputStream(file);
		pcontext = new ParseContext();

		Parser parser = new AutoDetectParser();
		parser.parse(inputstream, handler, metadata, pcontext);

		pressAnyKey();


		//getting the content of the document
		System.out.println("rpv......... Contents of the File :" + handler.toString());


		String fileContent = handler.toString();

		fileContent = fileContent.substring(0, Math.min(fileContent.length(), 2048));

		//Write Link export pdf contents here. Single Cell Table 
		StringBuffer pdf1 = new StringBuffer("");
		StringBuffer pdfF1 = new StringBuffer("");

		pdf1.append("fileType: " + ft + Seperator + "fileName: " + fn + Seperator +  fileContent + Seperator);
		pdfF1.append("" + Seperator + "" + Seperator + "" + Seperator);

		pdf1.deleteCharAt(pdf1.length()-1);
		pdfF1.deleteCharAt(pdfF1.length()-1);

		System.out.println("pdf1........  : " + pdf1);
		System.out.println("pdfF1........  : " + pdfF1);

		StringBuffer contentUpload = new StringBuffer("");
		contentUpload.append(pdf1);
		contentUpload.append(ContentDelimeter);
		contentUpload.append(pdfF1);
		contentUpload.append(ContentDelimeter);
		System.out.println("contentUpload........  : " + contentUpload);

		int contentRowCount = 3;
		int contentColCount = 1;

		System.out.println(".................................................................................");

		String s = handler.toString();
		String[] words = s.split("\\W+");

		for (int i = 0; i < words.length; i++) {
			System.out.println("....." + words[i]);
			//	System.out.print(words[i] + ".....");
		}      

		// Write Dictionary of Pdf Content here.

		// Getting metadata of the document
		System.out.println("rpv......... Metadata of the PDF:");
		String[] metadataNames = metadata.names();

		int colCount = 2;
		int rowCount = metadataNames.length;

		System.out.println("name  : Value ");
		for(String name : metadataNames) {
			System.out.println( name+ " : " + metadata.get(name));
		}

		StringBuffer col1 = new StringBuffer("");
		StringBuffer col2 = new StringBuffer("");

		StringBuffer colF1 = new StringBuffer("");
		StringBuffer colF2 = new StringBuffer("");

		col1.append("filetype" + Seperator );
		col2.append(filetype + Seperator );
		colF1.append("" + Seperator );
		colF2.append("" + Seperator );
		rowCount = rowCount + 1;

		col1.append("filename" + Seperator );
		col2.append(args[0] + Seperator );
		colF1.append("" + Seperator );
		colF2.append("" + Seperator );
		rowCount = rowCount + 1;

		for(String name : metadataNames) {
			col1.append(name + Seperator );
			col2.append(metadata.get(name) + Seperator );
			colF1.append("" + Seperator );
			colF2.append("" + Seperator );
		}

		col1.deleteCharAt(col1.length()-1);
		col2.deleteCharAt(col2.length()-1);

		colF1.deleteCharAt(colF1.length()-1);
		colF2.deleteCharAt(colF2.length()-1);

		System.out.println("name........  : " + col1);
		System.out.println("value........  : " + col2);

		System.out.println("nameF........  : " + colF1);
		System.out.println("valueF........  : " + colF2);

		StringBuffer dataUpload = new StringBuffer("");
		dataUpload.append(col1);
		dataUpload.append(ContentDelimeter);
		dataUpload.append(colF1);
		dataUpload.append(ContentDelimeter);
		dataUpload.append(col2);
		dataUpload.append(ContentDelimeter);
		dataUpload.append(colF2);
		dataUpload.append(ContentDelimeter);

		System.out.println("dataUpload........  : " + dataUpload);

		//readSettings(dataUpload, colCount, rowCount);

		WorkerThread wk1;

		try
		{
			Properties p = new Properties();
			p.load(new FileInputStream("Settings.ini"));
			System.out.println("user = " + p.getProperty("DBuser"));
			System.out.println("password = " + p.getProperty("DBpassword"));
			System.out.println("location = " + p.getProperty("DBlocation"));
			System.out.println("url = " + p.getProperty("URL"));
			serverURL = p.getProperty("URL");
			MAX_THREADS = Integer.parseInt(p.getProperty("Threads"));
			MAX_JOBS = Integer.parseInt(p.getProperty("JobsPerThread"));
			// MAX_LINK_IMPORT_TABLE_IDS  = Integer.parseInt(p.getProperty("MaxLinkImportTableIds"));
			JOB_FILE_NAME = p.getProperty("JOB_FILE_NAME");

			linkExportPrefix = p.getProperty("LinkExportTableNamePrefix");
			ResultFileName = p.getProperty("ResultFile");
			LINK_EXPORT_USERS_FILE = p.getProperty("LINK_EXPORT_USERS_FILE");
			LINK_EXPORT_DATA_FILE_NAMES = p.getProperty("LINK_EXPORT_DATA_FILE_NAMES");
			LINK_EXPORT_HEADER_FILE = p.getProperty("LINK_EXPORT_HEADER_FILE");
			
			int linkExportSerialStart;
			linkExportSerialStart = Integer.parseInt(p.getProperty("LinkExportTableSequenceStart"));


			System.out.println("Threads=" + MAX_THREADS);
			p.list(System.out);


 			WorkerThread wt = new WorkerThread();
 			System.out.println("before Synchronized");
 			synchronized(wt)
 			{
 				System.out.println("Setting inital Sequecne value ...................." + linkExportSerialStart);
 				wt.setlinkExportSequence(linkExportSerialStart);
 			}

			String xUserId = "03";
			String xUserName = "admin";
			String xUserPwd = "0";
			String xMemberId = "2";
			int xNhId = 2;

			//03admin02210RPV_ThreadLE_Tables{Table Name}Table created for test

	 		wk1 = new WorkerThread();
	 		wk1.incrementSequence();

	 		String colHeaders;

	 		String tableName = linkExportPrefix + "_" +  wk1.getLinkExportSequence() ;
	 		colHeaders = "Name" + Seperator + "Value" + ContentDelimeter;;
			linkExportRoutine(tableName, colHeaders, serverURL, xUserId, xUserName, xUserPwd, xMemberId, xNhId, dataUpload.toString(), colCount, rowCount);

			tableName = linkExportPrefix + "_Contents_" +  wk1.getLinkExportSequence() ;
	 		colHeaders = "Contents" + ContentDelimeter;
			linkExportRoutine(tableName, colHeaders, serverURL, xUserId, xUserName, xUserPwd, xMemberId, xNhId, contentUpload.toString(), contentColCount, contentRowCount);

		}
		catch (Exception e) 
		{
			System.out.println(e.toString());
			e.printStackTrace();
		}
	}


	public static String getFileType(String filename) throws IOException
	{
		File file = new File(filename);//

		//Instantiating tika facade class 
		Tika tika = new Tika();

		//detecting the file type using detect method
		String filetype = tika.detect(file);
		System.out.println(filetype);
		return filetype;
	}

	public static void pressAnyKey()
	{
		Scanner scan = new Scanner(System.in);

	     System.out.print("Press any key to continue . . . ");
	     scan.nextLine();
	}


//linkexport routine

		// public void linkExportRoutine(JobDetails jd, String dataUpload, int colCount, int rowCount)
		public static void linkExportRoutine(String tableName, String colHeaders, String serverURL, String xUserId, String xUserName, String xUserPwd, String xMemberId, int xNhId, String dataUpload, int xColCount, int xRowCount)
		{

			// WorkerThread wk1;

		 	try
		 	{

		 		System.out.println("IIIIIIIINNNNNNNNNNNNNSSSSSSSSSSSSIIIIIIIIIIIIIDDDDDDDDDEEEEEEEEEEEE  linkExportRoutine.....");

		 		System.out.println("dataUpload linkExportRoutine.....:" + dataUpload);
		 		System.out.println("colCount linkExportRoutine.....:" + xColCount);
		 		System.out.println("rowCount linkExportRoutine.....:" + xRowCount);

		 		System.out.println("serverURL.....:" + serverURL);
		 		System.out.println("xUserId.....:" + xUserId);
		 		System.out.println("xUserName.....:" + xUserName);
		 		System.out.println("xUserPwd.....:" + xUserPwd);
		 		System.out.println("xMemberId.....:" + xMemberId);
		 		System.out.println("xNhId.....:" + xNhId);

		 		int xTableId = -1;

		 		String xHeader = xUserId + Seperator + xUserName + Seperator + xUserPwd + Seperator + xMemberId + Seperator + "{TableId}" + Seperator + xNhId + Seperator + xColCount + Seperator + xRowCount + ContentDelimeter;

		 		URL url = new URL(serverURL + "/xlCollaborationService");

		 		HttpURLConnection conn = (HttpURLConnection) url.openConnection();

		 		String line = "";

		 		conn.setRequestMethod("POST");
		 		conn.setDoInput(true);
		 		conn.setDoOutput(true);

		 		DataOutputStream wr = new DataOutputStream (conn.getOutputStream());

				// System.out.println(linkExportHeaderForThread);

		 		// wk1 = new WorkerThread();
		 		// wk1.incrementSequence();

		 		// String tableName = linkExportPrefix + "_" +  wk1.getLinkExportSequence() ;

		// 		jd.TableName = tableName;

	    // Buffer = "" & m_userId & VBA.Chr(1) & _
	    //          m_userName & VBA.Chr(1) & _
	    //          m_userPassword & VBA.Chr(1) _
	    //          & m_memberId & VBA.Chr(1) _
	    //          & m_nhId & VBA.Chr(1) _
	    //          & 10 & VBA.Chr(1) _
	    //          & CollaborationName & VBA.Chr(1) _
	    //          & WhiteBoardName & VBA.Chr(1) _
	    //          & tableName & VBA.Chr(1) _
	    //          & tableDesc & VBA.Chr(1) _
	    //          & VBA.Chr(2)

		 		String createCuboidHeader = xUserId + Seperator + xUserName + Seperator + xUserPwd + Seperator + xMemberId + Seperator +  xNhId + Seperator + "10" +  Seperator + "Facebook" + Seperator + "Documents" + Seperator + "{Table Name}" + Seperator + "Table created for test" + Seperator + ContentDelimeter;
	 		
		 		System.out.println("tableName = " + tableName);
		 		// String linkExportBufferToUse = linkExportHeaderForThread.replaceAll("\\{Table Name\\}" , tableName);
		 		String linkExportBufferToUse = createCuboidHeader.replaceAll("\\{Table Name\\}" , tableName);
				
		 		System.out.println("linkExportBufferToUse------------------> " + linkExportBufferToUse);

				ConvertBase64Buffer cb64 = new ConvertBase64Buffer();

				String buffer64 = cb64.GenerateBase64Buffer(linkExportBufferToUse);

		 		System.out.println("buffer64------------------> " + buffer64);

		 		// wr.writeBytes(linkExportBufferToUse); 
				 wr.writeBytes(buffer64);
				
		 		wr.flush();
		 		wr.close();

		 		InputStream is = conn.getInputStream();
		 		BufferedReader rd = new BufferedReader(new InputStreamReader(is));
				
		 		StringBuffer response = new StringBuffer();

				while ((line = rd.readLine()) != null)
				{
					response.append(line);
					response.append('\r');			
				}
				
				rd.close();
				conn.disconnect();
				url = null;

				String retVal = "";
				retVal = response.toString();
				
				System.out.println("Output from server of /xlCollaborationService.CreateTable all = " + retVal);
				
				ConvertBase64Buffer abc64 = new ConvertBase64Buffer();

				System.out.println("Decoded Response : " + abc64.DecodeBase64Buffer(response.toString()));
				retVal = abc64.DecodeBase64Buffer(response.toString());

				String tabID = "";
				
				if (retVal.equals(""))
				{
					System.out.println(Thread.currentThread().getName() + "No Response from Server....");
					return;
				}

				if (retVal.substring(0,7).equals("Success"))
				{

					tabID = retVal.split(new Character((char)1).toString())[1];
					System.out.println("Newly Table Created ID = " + tabID);
				}

				
				if (tabID.length() > 0 )
				{

					//String xUserId, String xUserName, String xUserPwd, String xMemberId, int xNhId, 
					String linkExportTableBufferForThread = xUserId + Seperator + xUserName + Seperator + xUserPwd + Seperator  + xMemberId + Seperator + "{Table Id}" + Seperator + xNhId + Seperator + xColCount + Seperator + xRowCount + ContentDelimeter;
					// Example linkExportTableBufferForThread = "	3rahulvcwp4{Table Id}1";
					// System.out.println("linkExportTableBufferForThread........." + linkExportTableBufferForThread);
					System.out.println("tabID = " + tabID);
					String linkExportTableBufferToUse = linkExportTableBufferForThread.replaceAll("\\{Table Id\\}" , tabID);
					System.out.println("linkExportTableBufferToUse............" + linkExportTableBufferToUse);
				

					xHeader = xHeader.replaceAll("\\{TableId\\}" , tabID);
					//xHeader = xHeader.replaceAll("\\{RowCount\\}" , Integer.toString(rowCount));
					System.out.println("xHeader............" + xHeader);
				
					String xDataWithHeader = xHeader + colHeaders + dataUpload;

					System.out.println("xDataWithHeader............" + xDataWithHeader);

					cb64 = new ConvertBase64Buffer();
					//buffer64 = cb64.GenerateBase64Buffer(exportBufferLine);
					buffer64 = cb64.GenerateBase64Buffer(xDataWithHeader);

					System.out.println("^^^^^^^^^^^^^^^ buffer64 : " + buffer64);

					// Link Export Table Header
	    // header = "" & m_userId & VBA.Chr(1) & _
	    //          m_userName & VBA.Chr(1) & _
	    //          m_userPassword & VBA.Chr(1) _
	    //          & m_memberId & VBA.Chr(1) _
	    //          & TableId & VBA.Chr(1) _
	    //          & m_nhId & VBA.Chr(1) _
	    //          & colCount & VBA.Chr(1) _
	    //          & expRange.Rows.count - 1 & VBA.Chr(2)

					URL urlexp = new URL(serverURL + "/xlLinkExportService");

					HttpURLConnection expconn = (HttpURLConnection) urlexp.openConnection();
					
					line = "";

					expconn.setRequestMethod("POST");
					expconn.setDoInput(true);
					expconn.setDoOutput(true);

					DataOutputStream expwr = new DataOutputStream (expconn.getOutputStream());

					expwr.writeBytes(buffer64); 
					
					expwr.flush();
					expwr.close();

					InputStream expis = expconn.getInputStream();
					BufferedReader exprd = new BufferedReader(new InputStreamReader(expis));
					
					response = new StringBuffer();

					while ((line = exprd.readLine()) != null)
					{
						response.append(line);
						response.append('\r');			
					}
					
					System.out.println("Link Export Response : __________________ " + response);

					ConvertBase64Buffer bc64 = new ConvertBase64Buffer();

					System.out.println("Decoded Response : " + bc64.DecodeBase64Buffer(response.toString()));
					String dResponse = bc64.DecodeBase64Buffer(response.toString());

					exprd.close();
					expconn.disconnect();
					
					urlexp = null;

					String[] linkExportReceivedBuffer = dResponse.toString().split(new Character((char)2).toString());
					String[] linkExportReceivedHeader = linkExportReceivedBuffer[0].split(new Character((char)1).toString());
					
					System.out.println(Thread.currentThread().getName() + response.toString());
				}
		 	}
			catch (Exception e)
			{
				System.out.println(e.toString());
				e.printStackTrace();			
			} 
		}
}