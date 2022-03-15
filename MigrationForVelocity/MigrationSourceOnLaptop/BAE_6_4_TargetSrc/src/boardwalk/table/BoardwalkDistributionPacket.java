/*
 * @(#)BoardwalkDistributionPacket.java 1.0
 *
 * Copyright 2004 BoardwalkTech, Inc. All rights reserved.
 * BoardwalkTech/CONFIDENTIAL. Use is subject to license terms.
 */

package boardwalk.table;

import java.util.*;
import java.util.zip.*;
import java.io.*;
import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.*;

import servlets.xlLinkImportService;
import com.boardwalk.database.*;
import com.boardwalk.exception.*;
import com.boardwalk.neighborhood.*;
import com.boardwalk.table.*;
import com.boardwalk.user.*;
import com.boardwalk.member.*;
import java.sql.*;                  // JDBC package
import javax.sql.*;                 // extended JDBC package

import boardwalk.table.BoardwalkTableContents;
import boardwalk.connection.BoardwalkConnection;
import boardwalk.neighborhood.BoardwalkMember;

/**
* A <code>BoardwalkDistributionPacket</code> is an in-memory container for
* delivering Boardwalk data to users that can be manifested in their
* local spreadsheets using instructions defined in the packet and in an
* attached Excel Template.
* <p> Boardwalk data (tables) are added to the packet using the
* BoardwalkDistributionManager service.
* <p> Further, the packet has methods to define how the table will be
* displayed in spreadsheet and also UI controls/actions that can aid the
* user in navigating, modifying and synchronizing the Boardwalk data in
* the manifested spreadsheet.
* <p> The top level display entity is a Sheet. A sheet can contain one or
* more <code>BoardwalkTableDisplay</code> objects.
* <p> The <code>BoardwalkTableDisplay</code> object has a one-to-one map
* with the Boardwalk tables added to the packet using <code>BoardwalkDistributionManager</code>
* service.
* <p> The <code>BoardwalkTableDisplay</code> contains one or more <code>BoardwalkDataArea</code>
* objects
* <p> The <code>BoardwalkDataArea</code> contains one or more <code>BoardwalkRowGroup</code>
* and <code>BoardwalkColumnGroup</code> objects.
* <p> The <code>BoardwalkRowGroup</code> and <code>BoardwalkColumnGroup</code> objects partition
* the rows and columns of the table into groups for display purposes.
* <p> The <code>BoardwalkDistributionPacket</code> object is also used to define UI controls/
* actions to work on the boardwalk data in the spreadsheet and synchronize the spreadsheet
* with the server. The UI actions are created using images stored in the Excel template
* that are associated dynamically with macros specified in the API at the time the spreadsheet is
* manifested on the user desktop.
*/

public class BoardwalkDistributionPacket
{
    protected FileInputStream template; // the template file
	protected BoardwalkConnection connection;
    protected byte[] bArray;
    protected Hashtable tableIdsByExternalName;

	private final static String Seperator = new Character((char)1).toString();
	private final static String ContentDelimeter = new Character((char)2).toString();
	private static final int BUFFER = 512;
	Hashtable sheets;
	Vector sheetNames;
	String activeSheet;
	private Hashtable tableDisplaysByName;
	private BoardwalkClientAction onBwsLoadAction;
	File temp ;

    /**
    * Creates a new distribution packet object
    * <p> Boardwalk tables can be added to the packet using the
    * <code>BoardwalkDistributionManager</code>
    * <p> The distribution packet has methods to define the display of
    * these tables in the spreadsheet client and add UI controls/actions
    * to modify the tables locally in the spreadsheet and communicate with
    * the boardwalk server
    * @param template an Excel template that contains predefined images,
    * text, macros and data positioning information.
    */
	public BoardwalkDistributionPacket (FileInputStream template, BoardwalkConnection bwcon)
	{
		//template = a_template;
		bArray = getByteArray(new BufferedInputStream(template));
		connection = bwcon;
		tableIdsByExternalName = new Hashtable();
		sheets = new Hashtable();
		sheetNames = new Vector();
		activeSheet = null;
		tableDisplaysByName = new Hashtable();
		onBwsLoadAction = new BoardwalkClientAction("", 0, "","");
	}
    /**
    * Creates a new distribution packet object
    * <p> Boardwalk tables can be added to the packet using the
    * <code>BoardwalkDistributionManager</code>
    * <p> The distribution packet has methods to define the display of
    * these tables in the spreadsheet client and add UI controls/actions
    * to modify the tables locally in the spreadsheet and communicate with
    * the boardwalk server
    * @param template an Excel template that contains predefined images,
    * text, macros and data positioning information.
    */
	public BoardwalkDistributionPacket(byte[] template, BoardwalkConnection bwcon)
	{
		//template = a_template;
		bArray = template;
		connection = bwcon;
		tableIdsByExternalName = new Hashtable();
		sheets = new Hashtable();
		sheetNames = new Vector();
		activeSheet = null;
		onBwsLoadAction = new BoardwalkClientAction("", 0, "","");
	}

	/**
	* Adds a table to a distribution packet with a display name
	* that is unique in the packet.
	*
	* @param tableId the database id of the boardwalk table
	* @param displayName a <code>String</code> as the display handle to the table
	* @exception BoardwalkException if a database access error occurs
	*/

	public void addTableToPacket(
								int tableId,
								String displayName)
	throws BoardwalkException
	{
		//System.out.println("The display name is :"+ displayName+tableId);
		int userId = connection.getUserId();
		int memberId = connection.getMemberId();
		int nhid = connection.getNeighborhoodId();

		if (tableIdsByExternalName.get(displayName) == null)
		{
			tableIdsByExternalName.put(displayName, new Integer(tableId));
		}
		else
		{
			//bwe
			System.out.println("The display name is already in use");
			return;
		}
	}

    /**
    * Get the action associated with the end of BWS loading in the client spreadsheet
    * @return a <code>BoardwalkClientAction</code> object
    */
	public BoardwalkClientAction getOnLoadAction()
	{
		return onBwsLoadAction;
	}
    /**
    * Add a new sheet to the packet
    * @param sheetName the name of the sheet.
    */
	public BoardwalkSheet addSheet(String sheetName)
	throws BoardwalkException
	{
		if (sheets.get(sheetName) != null)
		{
			throw new BoardwalkException(15001);
		}
		BoardwalkSheet bs = new BoardwalkSheet(sheetName);
		sheets.put(sheetName, bs);
		sheetNames.addElement(sheetName);
		return bs;
	}

	/**
	* Add a new sheet to the packet
	* @param sheetName the name of the sheet.
	*/
	public void setActiveSheet(String sheetName)
	throws BoardwalkException
	{
		activeSheet = sheetName;
	}

    /**
    * Get existing sheet from packet
    * @param sheetName the name of the sheet.
    */
	public BoardwalkSheet getSheet(String sheetName)
	throws BoardwalkException
	{
		BoardwalkSheet bs = (BoardwalkSheet)sheets.get(sheetName);
		if ( bs == null)
		{
			throw new BoardwalkException(15002);
		}
		return bs;
	}

    /**
    * Add a new <code>BoardwalkTableDisplay</code> to a sheet
    * @param sheetName the name of the sheet.
    * @param displayName the name of the table display. It has to be the same
    * unique name defined when adding the Boardwalk table to the distribution
    * packet using the <code>BoardwalkDistributionMnager</code> service.
    * @return a <code>BoardwalkTableContents</code> object
    */
	public BoardwalkTableDisplay addTableDisplay(String sheetName, String displayName)
	{
		BoardwalkSheet bs = (BoardwalkSheet)sheets.get(sheetName);

		int tableId = ((Integer)tableIdsByExternalName.get(displayName)).intValue();
		BoardwalkTableDisplay td = new BoardwalkTableDisplay (displayName, tableId);
		tableDisplaysByName.put(displayName, td);
		bs._tableDisplayList.addElement(td);

		return td;
	}


    /**
    * Fetch the in-memory table contents of the Boardwalk table
    * @param member the database id of the Boardwalk member. The appropriate data
    * based on the permission scheme for the member will be extracted from the
    * distribution packet and sent as a .bws file attachment. The mime-type of this
    * bws format is associated with a Boardwalk reader, if the Boardwalk plugin has
    * been installed on the client machine. Clicking on this file will extract the
    * multiple sets of information and render it in the template which is included
    * as part of this attachment.
    * @return a <code>MimeBodyPart</code> object that can be attached to
    * an email.
    */
    public MimeBodyPart getAttachmentForMember(BoardwalkMember member, String TemplatePathforExtension)
	throws IOException, MessagingException, BoardwalkException
    {
		int userId = member.getUserId();
		int memberId = member.getId();
		int nhId = member.getNeighborhoodId();
		System.out.println("Creating Attachment for member");
		System.out.println("userId = " + userId);
		System.out.println("memberId = " + memberId);
		System.out.println("nhid = " + nhId);

		// start a zip file
//		File temp = File.createTempFile((new java.util.Date()).getTime()+"U"+userId, ".bws");
		temp = File.createTempFile((new java.util.Date()).getTime()+"U"+userId, ".bws");
//		temp.deleteOnExit();
		FileOutputStream dest = new FileOutputStream(temp);
		//PipedOutputStream pipeOut = new PipedOutputStream();
		//PipedInputStream pipeIn = new PipedInputStream(pipeOut);
		ZipOutputStream out = new ZipOutputStream(dest);

		createPacketFile(member, out, TemplatePathforExtension);

		out.close();
		//out.finish();

		System.out.println("finished writing zip file");
		MimeBodyPart mbp = new MimeBodyPart();
		javax.activation.DataSource source = new FileDataSource(temp);
		mbp.setDataHandler(new DataHandler(source));
		mbp.setFileName("template.bws");

		//temp.delete();

		return mbp;
    }

    /**
    * Populates the zip output stream with the bws data.
    * @param member the database id of the Boardwalk member. The appropriate data
    * based on the permission scheme for the member will be extracted from the
    * distribution packet and sent as a .bws file attachment. The mime-type of this
    * bws format is associated with a Boardwalk reader, if the Boardwalk plugin has
    * been installed on the client machine. Clicking on this file will extract the
    * multiple sets of information and render it in the template which is included
    * as part of this attachment.
    * @param out the zip output stream.
    * @return a <code>MimeBodyPart</code> object that can be attached to
    * an email.
    */
	public void createPacketFile(
								BoardwalkMember member,
								ZipOutputStream out,
								String TemplatePathforExtension
								)
	throws IOException, MessagingException, BoardwalkException
	{
		int userId = member.getUserId();
		int memberId = member.getId();
		int nhId = member.getNeighborhoodId();
		Random randomGenerator = new Random();
		int randomInt = randomGenerator.nextInt(1000);
		
		int mid= TemplatePathforExtension.lastIndexOf(".");
		String ext=TemplatePathforExtension.substring(mid,TemplatePathforExtension.length());
		System.out.println("****************************Extension = " + ext);
		String templateName = "template" + (new Integer(randomInt)).toString() + ext;
		//String templateName = "template" + (new Integer(randomInt)).toString() + ".xlsb";
		ZipEntry entry = new ZipEntry(templateName);

		out.putNextEntry(entry);
		out.write(bArray);
		out.closeEntry();
		//System.out.println("Finished writing xls file to zip");

		Vector extNames = new Vector (tableIdsByExternalName.keySet());
		Iterator eni = extNames.iterator();
		while (eni.hasNext())
		{
			String tName = (String)eni.next();
			int tableId = ((Integer)tableIdsByExternalName.get(tName)).intValue();
			BoardwalkTableDisplay td = (BoardwalkTableDisplay)tableDisplaysByName.get(tName);

			if (td != null)
			{
				td.resetGroupIndices();
			}

			//System.out.println("Writing table buffer for table id = " + tableId + "...");
			try
			{


				String tBuf = TableViewManager.getTableBuffer(
											connection.getConnection(),
											tableId,
											userId,
											memberId,
											nhId,
											-1,
											td.getView(),
											td.getMode()
										);
				entry = new ZipEntry(tName + ".xlet");
				out.putNextEntry(entry);
				//System.out.println("tbuf = " + tBuf.toString());
				out.write(tBuf.getBytes());


				out.closeEntry();
				//System.out.println("Finished table buffer for table id = " + tableId + "...");

			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}

		}

		// the layout / display packet
		if (sheets.size() > 0)
		{
			System.out.println("Writing Layout Information");
			StringBuffer m_outputDatabuffer = new StringBuffer();
			entry = new ZipEntry("template.blyt");
			out.putNextEntry(entry);
			m_outputDatabuffer = m_outputDatabuffer.append(1);
			m_outputDatabuffer = m_outputDatabuffer.append(ContentDelimeter);
			m_outputDatabuffer = m_outputDatabuffer.append(onBwsLoadAction._controlName);
			m_outputDatabuffer = m_outputDatabuffer.append(ContentDelimeter);
			m_outputDatabuffer = m_outputDatabuffer.append(onBwsLoadAction._action);
			m_outputDatabuffer = m_outputDatabuffer.append(ContentDelimeter);
			m_outputDatabuffer = m_outputDatabuffer.append(onBwsLoadAction._preAction);
			m_outputDatabuffer = m_outputDatabuffer.append(ContentDelimeter);
			m_outputDatabuffer = m_outputDatabuffer.append(onBwsLoadAction._postAction);
			m_outputDatabuffer = m_outputDatabuffer.append(ContentDelimeter);
			m_outputDatabuffer = m_outputDatabuffer.append(onBwsLoadAction._args.size());
			m_outputDatabuffer = m_outputDatabuffer.append(ContentDelimeter);

			Iterator loadai = onBwsLoadAction._args.iterator();
			while (loadai.hasNext())
			{
				m_outputDatabuffer = m_outputDatabuffer.append(loadai.next());
				m_outputDatabuffer = m_outputDatabuffer.append(ContentDelimeter);
			}

			//Vector shList = new Vector (sheets.keySet());
			m_outputDatabuffer = m_outputDatabuffer.append(sheetNames.size());
			m_outputDatabuffer = m_outputDatabuffer.append(ContentDelimeter);
			m_outputDatabuffer = m_outputDatabuffer.append(activeSheet);
			m_outputDatabuffer = m_outputDatabuffer.append(ContentDelimeter);
			//System.out.println("Number of sheets = " + shList.size());
			Iterator shi = sheetNames.iterator();
			while (shi.hasNext())
			{
				BoardwalkSheet bs = (BoardwalkSheet)sheets.get((String)shi.next());
				//System.out.println("Processing sheet = " + bs.getName());
				m_outputDatabuffer = m_outputDatabuffer.append(bs.getName());
				m_outputDatabuffer = m_outputDatabuffer.append(ContentDelimeter);

				m_outputDatabuffer = m_outputDatabuffer.append(bs.getTemplateSheet());
				m_outputDatabuffer = m_outputDatabuffer.append(ContentDelimeter);

				m_outputDatabuffer = m_outputDatabuffer.append(bs.isVisible());
				m_outputDatabuffer = m_outputDatabuffer.append(ContentDelimeter);

				Vector shActionList = (Vector)bs.getSheetActionList();
				m_outputDatabuffer = m_outputDatabuffer.append(shActionList.size());
				m_outputDatabuffer = m_outputDatabuffer.append(ContentDelimeter);
				Iterator sai = shActionList.iterator();
				while (sai.hasNext())
				{
					BoardwalkClientAction bca = (BoardwalkClientAction)sai.next();
					m_outputDatabuffer = m_outputDatabuffer.append(bca._controlName);
					m_outputDatabuffer = m_outputDatabuffer.append(ContentDelimeter);
					m_outputDatabuffer = m_outputDatabuffer.append(bca._action);
					m_outputDatabuffer = m_outputDatabuffer.append(ContentDelimeter);
					m_outputDatabuffer = m_outputDatabuffer.append(bca._preAction);
					m_outputDatabuffer = m_outputDatabuffer.append(ContentDelimeter);
					m_outputDatabuffer = m_outputDatabuffer.append(bca._postAction);
					m_outputDatabuffer = m_outputDatabuffer.append(ContentDelimeter);
					m_outputDatabuffer = m_outputDatabuffer.append(bca._args.size());
					m_outputDatabuffer = m_outputDatabuffer.append(ContentDelimeter);
					Iterator argi = bca._args.iterator();
					while (argi.hasNext())
					{
						m_outputDatabuffer = m_outputDatabuffer.append(argi.next());
						m_outputDatabuffer = m_outputDatabuffer.append(ContentDelimeter);
					}
				}

				Vector tdList = (Vector)bs.getTableDisplayList();
				m_outputDatabuffer = m_outputDatabuffer.append(tdList.size());
				m_outputDatabuffer = m_outputDatabuffer.append(ContentDelimeter);
				Iterator tdi = tdList.iterator();
				while (tdi.hasNext())
				{
					BoardwalkTableDisplay td = (BoardwalkTableDisplay)tdi.next();
					//System.out.println("Table Display id = " + td.getTableId() + " name = " + td.getName());

					m_outputDatabuffer = m_outputDatabuffer.append(td.getName());
					m_outputDatabuffer = m_outputDatabuffer.append(ContentDelimeter);
					m_outputDatabuffer = m_outputDatabuffer.append(td.getTableId());
					m_outputDatabuffer = m_outputDatabuffer.append(ContentDelimeter);
					m_outputDatabuffer = m_outputDatabuffer.append(td.getRowPlacement());
					m_outputDatabuffer = m_outputDatabuffer.append(ContentDelimeter);
					m_outputDatabuffer = m_outputDatabuffer.append(td.getColumnPlacement());
					m_outputDatabuffer = m_outputDatabuffer.append(ContentDelimeter);
					m_outputDatabuffer = m_outputDatabuffer.append(td.getTranspose());
					m_outputDatabuffer = m_outputDatabuffer.append(ContentDelimeter);
				}
				//System.out.println("Done processing this sheet");
			}
			//m_outputDatabuffer = m_outputDatabuffer.append(Seperator);
			out.write(m_outputDatabuffer.toString().getBytes());
			out.closeEntry();
		}
	}

	
	public byte[] getByteArray(/*Buffered*/InputStream in)
	{
		try {
			final int chunkSize = 2048;
			ByteArrayOutputStream byteStream
				= new ByteArrayOutputStream(chunkSize);
			int val;

			while ((val=in.read()) != -1)
				byteStream.write(val);

			return byteStream.toByteArray();
		}
		catch (Exception e) { e.printStackTrace(); }
		return null;
	}

	public void deleteTempFile()
	{
		if (temp != null)
			temp.delete();
	}	
};


