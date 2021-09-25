import java.io.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.pdf.PDFParser;
import org.apache.tika.sax.BodyContentHandler;

import org.xml.sax.SAXException;

import java.util.*;
import java.net.*;
import java.text.*;

import java.util.zip.*;
import sun.misc.BASE64Encoder;
import org.apache.axis.encoding.Base64;



public class PdfParseT {

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
					System.out.println("Calling Link export  ..................................");
//					jd[i].JobType = "LE";
					linkExportTask(threadJDArray[i], linkExportTableBufferForThread, colCount, rowCount);
//					incrementSequence();
//					System.out.println("Incrementing volatile variable.....");
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

////here
		public void linkExportTask(JobDetails jd, String dataUpload, int colCount, int rowCount)
		{

		 	try
		 	{

		 		System.out.println("IIIIIIIINNNNNNNNNNNNNSSSSSSSSSSSSIIIIIIIIIIIIIDDDDDDDDDEEEEEEEEEEEE  linkExportTask.....");

		 		System.out.println("dataUpload linkExportTask.....:" + dataUpload);
		 		System.out.println("colCount linkExportTask.....:" + colCount);
		 		System.out.println("rowCount linkExportTask.....:" + rowCount);

	//	 		System.out.println("buffer ---------------> " + buffer);
		 		jd.ThreadName = Thread.currentThread().getName();

		 		String[] linkExportSendBuffer = linkExportHeaderForThread.split(new Character((char)1).toString());

		 		System.out.println("linkExportHeaderForThread UserID = " + linkExportSendBuffer[0]);
		 		System.out.println("linkExportHeaderForThread UserName = " + linkExportSendBuffer[1]);
		 		System.out.println("linkExportHeaderForThread MemberID = " + linkExportSendBuffer[3]);

		 		String xUserId = linkExportSendBuffer[0];
		 		String xUserName = linkExportSendBuffer[1];
		 		String xUserPwd = "0";
		 		String xMemberId = linkExportSendBuffer[3];
		 		int xTableId = -1;
		 		int xNhId = 2;
		 		int xColCount = 2;
		 		int xRowCount = -1;

		 		String xHeader = xUserId + Seperator + xUserName + Seperator + xUserPwd + Seperator + xMemberId + Seperator + "{TableId}" + Seperator + xNhId + Seperator + xColCount + Seperator + rowCount + ContentDelimeter;
		 		String colHeaders = "Name" + Seperator + "Value" + ContentDelimeter;;

		 		// jd.UserID =	linkExportSendBuffer[0];
		 		// jd.UserName = linkExportSendBuffer[1];
		 		// jd.MemberID = linkExportSendBuffer[3];
		 		// jd.JobType = "LE";
			
		 		// jd.JobStartTime = getCurrentDateTime();			//  new Date().toString();
				
		 		URL url = new URL(serverURL + "/xlCollaborationService");

		 		HttpURLConnection conn = (HttpURLConnection) url.openConnection();

		 		String line = "";

		 		conn.setRequestMethod("POST");
		 		conn.setDoInput(true);
		 		conn.setDoOutput(true);

		 		Random randomGenerator = new Random();
	//	 		int randomInt = randomGenerator.nextInt(MAX_LINK_IMPORT_TABLE_IDS);
			
		 		DataOutputStream wr = new DataOutputStream (conn.getOutputStream());

					System.out.println(linkExportHeaderForThread);

		 		WorkerThread wk1 = new WorkerThread();
		 		wk1.incrementSequence();

		 		String tableName = linkExportPrefix + "_" +  wk1.getLinkExportSequence() ;

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


		 		System.out.println("tableName = " + tableName);
		 		String linkExportBufferToUse = linkExportHeaderForThread.replaceAll("\\{Table Name\\}" , tableName);
				
		 		System.out.println("linkExportBufferToUse------------------> " + linkExportBufferToUse);

				ConvertBase64Buffer cb64 = new ConvertBase64Buffer();

				String buffer64 = cb64.GenerateBase64Buffer(linkExportBufferToUse);

		 		System.out.println("buffer64------------------> " + buffer64);

		 		// wr.writeBytes(linkExportBufferToUse); 
				 wr.writeBytes(buffer64);
				
		 		wr.flush();
		 		wr.close();

	///ddd

				//URL urlexp = new URL(serverURL + "/xlExportChangesService");
				//HttpURLConnection expconn = (HttpURLConnection) urlexp.openConnection();
				//line = "";
				//expconn.setRequestMethod("POST");
				//expconn.setDoInput(true);
				//expconn.setDoOutput(true);
				//DataOutputStream expwr = new DataOutputStream (expconn.getOutputStream());
				//expwr.writeBytes(buffer64); 
				//expwr.flush();
				//expwr.close();


	///dddd


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

		//				System.out.println(response.toString());
				String retVal = "";
				retVal = response.toString();
				
				System.out.println("Output from server of /xlCollaborationService.CreateTable all = " + retVal);
				
		//				System.out.println(retVal.substring(8, retVal.length()-2));

	///new

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
					jd.TableID = tabID;

		//					System.out.println("linkExportTableBufferForThread........." + linkExportTableBufferForThread);
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

					jd.DataSizeSent = buffer64.length();

		//					System.out.println("....After expwr.close()");

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


					//System.out.println("Decoded Response : " + DecodeData(response.toString()));
					//String dResponse = DecodeData(response.toString());

					exprd.close();
					expconn.disconnect();
					
					urlexp = null;
		//					System.out.println("....After exprd.close()");
					
					jd.DataSizeReceived = dResponse.length();
					
					String[] linkExportReceivedBuffer = dResponse.toString().split(new Character((char)2).toString());
					String[] linkExportReceivedHeader = linkExportReceivedBuffer[0].split(new Character((char)1).toString());
					
					jd.JobStatus = linkExportReceivedHeader[0];
					jd.ColCount = linkExportReceivedHeader[1];
					jd.RowCount = linkExportReceivedHeader[2];
					jd.TxID =  linkExportReceivedHeader[3];
					jd.FormulaCount =  "NA";

					System.out.println(Thread.currentThread().getName() + response.toString());
				}

				//Start here to link export table into newly created table
				//linkExportTableBufferForThread
				jd.JobEndTime = getCurrentDateTime();		// new Date().toString();

		 	}
			catch (Exception e)
			{
				System.out.println(e.toString());
				e.printStackTrace();			
			} 
		}

////here
	}


	public static void main(final String[] args) throws IOException,TikaException, SAXException {

		BodyContentHandler handler = new BodyContentHandler();
		Metadata metadata = new Metadata();
		FileInputStream inputstream = new FileInputStream(new File("teva po.pdf"));
		ParseContext pcontext = new ParseContext();

		//parsing the document using PDF parser
		PDFParser pdfparser = new PDFParser(); 
		pdfparser.parse(inputstream, handler, metadata, pcontext);

		//getting the content of the document
		System.out.println("rpv......... Contents of the PDF :" + handler.toString());

		String pdfContent = handler.toString();
		//Write Link export pdf contents here. Single Cell Table 
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

		readSettings(dataUpload, colCount, rowCount);


   }

//	public static void linkExportTask(String buffer)


	public static void readSettings(StringBuffer dataUpload, int colCount, int rowCount) 
	{
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

 			System.out.println("synchronized.............");

 			WorkerThread[] threads = new WorkerThread[MAX_THREADS];

 			jdArray = new JobDetails[MAX_THREADS][MAX_JOBS];
 			for (int x=0; x < MAX_THREADS ; x++)
 			{
 				for(int y=0; y < MAX_JOBS; y++)
 					jdArray[x][y] = new JobDetails();
 			}

 			readTaskSettings();


 			System.out.println("after readTaskSettings.............");


// //			jdArray = new JobDetails[MAX_THREADS][MAX_JOBS];

			// Give each thread a slice of the matrix to work with
			for (int i=0; i < MAX_THREADS; i++) 
			{
				System.out.println("before thread initialize............." +  i);

				System.out.println("taskArray[i] : " + taskArray[i]);
				System.out.println(Arrays.toString(taskArray[i]));
				System.out.println("jdArray[i] : " + jdArray[i]);
				System.out.println(Arrays.toString(jdArray[i]));
				System.out.println("linkExportHeader[i] : " + linkExportHeader[i]);
				System.out.println("linkExportTableBuffer[i] : " + linkExportTableBuffer[i]);

				System.out.println("Null starts here is the problem....");

				// threads[i] = new WorkerThread(taskArray[i], linkImportHeader[i], linkExportHeader[i], linkExportTableBuffer[i], refreshHeader[i], submitHeader[i], jdArray[i]);
				threads[i] = new WorkerThread(taskArray[i], null, linkExportHeader[i], dataUpload.toString(), null, null, jdArray[i], colCount, rowCount);
				System.out.println("after thread initialize.............");
				//threads[i] = new WorkerThread();
				threads[i].setName("Thread___" + i);
				threads[i].start();
			}

			wt.printJobDetailHeader();
			// Wait for each thread to finish
			for (int i=0; i < MAX_THREADS; i++) 
			{
				threads[i].join();
				System.out.println("join_____" + i);
				threads[i].printJobDetails();
			}

		}

		catch (Exception e) 
		{
			System.out.println(e.toString());
			e.printStackTrace();
		}
    }



	public static void readTaskSettings()
	{

		try
		{
			//READING TASK FILES			
			String line; 

			System.out.println("READING TASK FILE");

			BufferedReader buffReader = null;
			File file = new File(JOB_FILE_NAME);
			buffReader = new BufferedReader(new FileReader(file));

			taskArray = new String[MAX_THREADS][MAX_JOBS];

			System.out.println("before For_1 looooooooooooooooooooooooooooooooooooooooooooooop..............");
			for (int iThreads=0; iThreads < MAX_THREADS ; iThreads++ )
			{
				line = buffReader.readLine();
//				System.out.println(line);

				String[] theline = line.split(" "); 
				System.out.println("before For_2 looooooooooooooooooooooooooooooooooooooooooooooop..............");
				for(int iJobs=0; iJobs < MAX_JOBS; iJobs++)
				{
					taskArray[iThreads][iJobs] = theline[iJobs];
					//jdArray[iThreads][iJobs].JobType = theline[iJobs];
				}
				System.out.println("After For_2 looooooooooooooooooooooooooooooooooooooooooooooop..............");
			}
			buffReader.close();
			System.out.println("after For looooooooooooooooooooooooooooooooooooooooooooooop..............");


			int iCount = 0;

		// LINK EXPORT

			System.out.println("READING LINK EXPORT USER FILE");
		// READING LINK EXPORT USER FILE

			File leufile = new File(LINK_EXPORT_USERS_FILE);

			BufferedReader lebuffReader = null;
			lebuffReader = new BufferedReader(new FileReader(leufile));

			linkExportHeader = new String[MAX_THREADS];

			iCount = 0;

			System.out.println("Contents of linkExportUsers.txt...Reading first user entry of admin for single task........................................");

			while ((line = lebuffReader.readLine()) != null)
			{
				System.out.println("link export header = " + line);
				if (iCount < MAX_THREADS)
				{
					linkExportHeader[iCount] = line;
					iCount++;
				}
				else
					break;

			}
			lebuffReader.close();


			System.out.println("READING LINK EXPORT TABLE HEADER BUFFER");
		// READING LINK EXPORT TABLE HEADER BUFFER

			File lebfile = new File(LINK_EXPORT_HEADER_FILE);

			BufferedReader lehbuffReader = null;
			lehbuffReader = new BufferedReader(new FileReader(lebfile));

			linkExportTableBuffer = new String[MAX_THREADS];

			iCount = 0;

			System.out.println("Contents of linkExportHeaderBuffer.txt...........................................");

			while ((line = lehbuffReader.readLine()) != null)
			{
				System.out.println("link export table header = " + line);
				if (iCount < MAX_THREADS)
				{
					linkExportTableBuffer[iCount] = line;
					iCount++;
				}
				else
					break;

			}
			lehbuffReader.close();

		}

		catch (Exception e)
		{
			System.out.println(e.toString());
			e.printStackTrace();
		}
	}

//linkexport routine

}