package com.boardwalk.distribution;

import java.io.*;
import java.util.*;

import org.apache.xerces.parsers.DOMParser;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import boardwalk.common.*;

public class XmlParse
{
	String lsFilePath;
	String xmlFile;	
	private String msLineSeparator = System.getProperty("line.separator");
	BoardwalkUtility Utility = new BoardwalkUtility();

	public XmlParse(String asFilePath,String asFilePathtarget)
	{
		lsFilePath = asFilePath;
		xmlFile = asFilePathtarget;
	}
	
	public ArrayList parse() 
	{
      DOMParser parser = new DOMParser();
      try 
	  {
		FileReader ioFR_xmlFile = new FileReader(lsFilePath);
		BufferedReader ioBR_xmlFile = new BufferedReader(ioFR_xmlFile);
		String lsLine = null;
		ArrayList ALParentDistributionList = new  ArrayList();
		File targetXml = new File(xmlFile);
		if(targetXml.exists())
			targetXml.delete();
		RandomAccessFile ioRAF_tempFile = new RandomAccessFile(xmlFile,"rw");
		while( (lsLine = (ioBR_xmlFile.readLine()))!= null )
		{
			lsLine = lsLine.trim();
			lsLine = Utility.replaceString(lsLine,msLineSeparator,"");
			lsLine = Utility.replaceString(lsLine,"\n","");
			
			ioRAF_tempFile.write(lsLine.getBytes());
		}

			parser.parse(xmlFile);
			//gets the xml file

			Document document = parser.getDocument();
			//gets the root node
			
			Element Root = document.getDocumentElement();
			//System.out.println(" Document Root " + Root.getNodeName());

			ALParentDistributionList = createListNode(Root);

			ioRAF_tempFile.close();
			ioBR_xmlFile.close();
			return ALParentDistributionList;
		} 
	  catch (SAXException e) 
	  {
         e.printStackTrace();
      } 
	  catch (IOException e) 
	  {
           e.printStackTrace();
	  }
	 return null;
   }
	

	public ArrayList createListNode(Node node)
	{
		ArrayList ALDistributionList = new  ArrayList();

		if(node.hasChildNodes())
		{
			NodeList childNodes = node.getChildNodes();
			int mlRecordCount =  childNodes.getLength();
			//System.out.println("childNodes.getLength()	::"+childNodes.getLength());
			String lsRootValue = "";
			for(int i=0;i<mlRecordCount;i++) 
			{
				lsRootValue = "";
				Node Currnode	= childNodes.item(i);
				
				if(Currnode.getNodeType() == Node.COMMENT_NODE)
					continue;
				
				if(Currnode.getNodeType() != Node.TEXT_NODE)
					//System.out.println(" < CreateNode >" +Currnode.getNodeName());
			
				if(Currnode.getNodeType() == Node.TEXT_NODE)
					lsRootValue = Currnode.getNodeValue();

				if(Currnode.getNodeName().equals("distribution"))
				{
					//System.out.println("Inside Distribution");
					Distribution distributionObj = populateDistribution(Currnode);
					ALDistributionList.add(distributionObj);
					//System.out.println("Exiting Distribution");
				}
			}
		}
		return ALDistributionList;
	}

	public Distribution populateDistribution(Node node)
	{
		Distribution objDist = new Distribution();
		
		ArrayList	mALManagers		= new ArrayList();
		ArrayList	mALTemplates	= new ArrayList();

		if(node.hasChildNodes())
		{
			NodeList childNodes = node.getChildNodes();
			int mlRecordCount =  childNodes.getLength();
			String lsNodeValue = "";
			
			if(node.hasAttributes())
			{
				NamedNodeMap attrs = node.getAttributes();
				int numAttrs = attrs.getLength();

				for (int k = 0; k < numAttrs; k++)
				{
					Node attr			= attrs.item(k);
					String attrName		= attr.getNodeName();
					String attrValue	= attr.getNodeValue();
					if(attrName.equals("name"))
						objDist.setmsName(attrValue);
				}
			}

			for(int i=0;i<mlRecordCount;i++) 
			{
				Node CurrentNode	= childNodes.item(i);
				
				if(CurrentNode.getNodeType() == Node.COMMENT_NODE)
					continue;
				
				//System.out.println(" Processing Node " +CurrentNode.getNodeName());
			
				if(CurrentNode.getNodeType() == Node.TEXT_NODE)
					lsNodeValue	= CurrentNode.getNodeValue();

				if(CurrentNode.getNodeName().equals("manager"))
				{
					DistributionAdmin objDistAdmin = populateManager(CurrentNode);
					mALManagers.add(objDistAdmin);
				}

				if(CurrentNode.getNodeName().equals("template"))
				{
					DistributionTemplate objDistTemp = populateTemplate(CurrentNode);
					mALTemplates.add(objDistTemp);
				}

			}
			objDist.setManagers(mALManagers);
			objDist.setTemplates(mALTemplates);
		}

		return objDist;
	}

	public DistributionAdmin populateManager(Node node)
	{
		DistributionAdmin objDistAdmin = new DistributionAdmin();
		if(node.hasChildNodes())
		{
			NodeList childNodes = node.getChildNodes();
			int mlRecordCount =  childNodes.getLength();
			String lsNodeValue = "";
			for(int i=0;i<mlRecordCount;i++) 
			{
				Node CurrentNode	= childNodes.item(i);
				
				if(CurrentNode.getNodeType() == Node.COMMENT_NODE)
					continue;
				
				//System.out.println(" Processing Node " +CurrentNode.getNodeName());
			
				if(CurrentNode.getNodeType() == Node.TEXT_NODE)
					lsNodeValue	= CurrentNode.getNodeValue();
				if(CurrentNode.getNodeName().equals("member"))
				{
					NodeList memberchildNodes = CurrentNode.getChildNodes();
					for(int x=0;x<memberchildNodes.getLength();x++)
					{
						Node memberNode = memberchildNodes.item(x);
						
						if(memberNode.getNodeName().equals("user"))
						{
							if(memberNode.hasChildNodes())
							{
								Node textNode = memberNode.getFirstChild();
								objDistAdmin.setmsUserEmail(textNode.getNodeValue());
							}
						}
						if(memberNode.getNodeName().equals("neighborhood"))
						{
							if(memberNode.hasChildNodes())
							{
								Node textNode = memberNode.getFirstChild();
								objDistAdmin.setmsNeighbourhood(textNode.getNodeValue());
							}
						}
					}
				}
			}
		}
		return objDistAdmin;
	}

	public DistributionTemplate populateTemplate(Node node)
	{
		DistributionTemplate objTmpl = new DistributionTemplate();
		
		ArrayList alTemplWorkBook	= new ArrayList();
		ArrayList alTemplUser		= new ArrayList();

		if(node.hasChildNodes())
		{
			NodeList childNodes = node.getChildNodes();
			int mlRecordCount =  childNodes.getLength();
			String lsNodeValue = "";

			if(node.hasAttributes())
			{
				NamedNodeMap attrs = node.getAttributes();
				int numAttrs = attrs.getLength();

				for (int k = 0; k < numAttrs; k++)
				{
					Node attr			= attrs.item(k);
					String attrName		= attr.getNodeName();
					String attrValue	= attr.getNodeValue();
					if(attrName.equals("name"))
						objTmpl.setmsTemplateName(attrValue);

					if(attrName.equals("location"))
						objTmpl.setmsTemplateLocation(attrValue);
				}
			}

			for(int i=0;i<mlRecordCount;i++) 
			{
				Node CurrentNode	= childNodes.item(i);
				
				if(CurrentNode.getNodeType() == Node.COMMENT_NODE)
					continue;
				
				//System.out.println("CurrentNode Processing Node " +CurrentNode.getNodeName());

				if(CurrentNode.getNodeName().equals("subject"))
				{
					if(CurrentNode.hasChildNodes())
					{
						Node textNode = CurrentNode.getFirstChild();
						objTmpl.setmssubject(textNode.getNodeValue());
					}
				}
				
				if(CurrentNode.getNodeName().equals("message"))
				{
					if(CurrentNode.hasChildNodes())
					{
						Node textNode = CurrentNode.getFirstChild();
						objTmpl.setmsmessage(textNode.getNodeValue());
					}
				}
				
				if(CurrentNode.getNodeName().equals("workbook"))
				{
					DistributionWorkBook objWorkBook = populateWookbook(CurrentNode);
					alTemplWorkBook.add(objWorkBook);
				}

				if(CurrentNode.getNodeName().equals("memberList"))
				{
					if(CurrentNode.hasChildNodes())
					{
						NodeList memberListchildNodes = CurrentNode.getChildNodes();

						for(int j=0;j<memberListchildNodes.getLength();j++) 
						{
							Node CurrentChildNode	= memberListchildNodes.item(j);
							
							if(CurrentChildNode.getNodeType() == Node.COMMENT_NODE)
								continue;
							
							//System.out.println(" Processing Node " +CurrentChildNode.getNodeName());

							if(CurrentChildNode.getNodeName().equals("member"))
							{
								DistributionUser objDistUser = populateTemplateUser(CurrentChildNode);
								alTemplUser.add(objDistUser);
							}
						}
					}
				}

			}

			objTmpl.setmALTemplateWorkBook(alTemplWorkBook);
			objTmpl.setmALTemplateUsers(alTemplUser);
		}
		return objTmpl;
	}

	public DistributionWorkBook populateWookbook(Node node)
	{
		DistributionWorkBook objWorkBook = new DistributionWorkBook();

		ArrayList msWorkSheets				= new ArrayList();
		ArrayList mALOnLoadEventArguments	= new ArrayList();

		if(node.hasChildNodes())
		{
			NodeList childNodes = node.getChildNodes();
			int mlRecordCount	= childNodes.getLength();
			String lsNodeValue	= "";

			if(node.hasAttributes())
			{
				NamedNodeMap attrs = node.getAttributes();
				int numAttrs = attrs.getLength();

				for (int k = 0; k < numAttrs; k++)
				{
					Node attr			= attrs.item(k);
					String attrName		= attr.getNodeName();
					String attrValue	= attr.getNodeValue();
					if(attrName.equals("name"))
						objWorkBook.setmsName(attrValue);

				}
			}
			
			for(int i=0;i<mlRecordCount;i++) 
			{
				Node CurrentNode	= childNodes.item(i);
				
				if(CurrentNode.getNodeType() == Node.COMMENT_NODE)
					continue;
				
				//System.out.println(" Processing Node " +CurrentNode.getNodeName());
			
				if(CurrentNode.getNodeType() == Node.TEXT_NODE)
					lsNodeValue	= CurrentNode.getNodeValue();

				if(CurrentNode.getNodeName().equals("worksheet"))
				{
					DistributionTemplateWorkSheet objWorkSheet = populateWorkSheet(CurrentNode);
					msWorkSheets.add(objWorkSheet);
				}

				if(CurrentNode.getNodeName().equals("onLoadEvent"))
				{
					mALOnLoadEventArguments = populateOnLoadEventArguments(CurrentNode);
					objWorkBook.setmALOnLoadEvent(mALOnLoadEventArguments);
				}
			}

			objWorkBook.setmsWorkSheets(msWorkSheets);
		}
		return objWorkBook;
	}


	public ArrayList populateOnLoadEventArguments(Node node)//onLoadEvent
	{
		ArrayList mALArguments = new ArrayList();
		if(node.hasChildNodes())
		{
			NodeList childNodes = node.getChildNodes();
			int mlRecordCount	= childNodes.getLength();
			String lsNodeValue	= "";

			for(int i=0;i<mlRecordCount;i++) 
			{
				Node CurrentNode	= childNodes.item(i);
				
				if(CurrentNode.getNodeType() == Node.COMMENT_NODE)
					continue;
				
				//System.out.println(" Processing Node " +CurrentNode.getNodeName());
			
				if(CurrentNode.getNodeName().equals("argument"))
				{
					if(CurrentNode.hasChildNodes())
					{
						Node textNode = CurrentNode.getFirstChild();
						mALArguments.add(textNode.getNodeValue());
					}
				}
			}
		}
		return mALArguments;
	}

	public DistributionTemplateWorkSheet populateWorkSheet(Node node)
	{
		DistributionTemplateWorkSheet objWorkSheet = new DistributionTemplateWorkSheet();
		
		ArrayList mALTableDisplay = new ArrayList();
		ArrayList mALSheetAction = new ArrayList();

		if(node.hasChildNodes())
		{
			NodeList childNodes = node.getChildNodes();
			int mlRecordCount	= childNodes.getLength();
			String lsNodeValue	= "";

			if(node.hasAttributes())
			{
				NamedNodeMap attrs = node.getAttributes();
				int numAttrs = attrs.getLength();

				for (int k = 0; k < numAttrs; k++)
				{
					Node attr			= attrs.item(k);
					String attrName		= attr.getNodeName();
					String attrValue	= attr.getNodeValue();
					if(attrName.equals("name"))
						objWorkSheet.setmsWorkSheetName(attrValue);
					if(attrName.equals("useTemplateSheet"))
						objWorkSheet.setmsUseTemplateSheet(attrValue);
				}
			}
			
			for(int i=0;i<mlRecordCount;i++) 
			{
				Node CurrentNode	= childNodes.item(i);
				
				if(CurrentNode.getNodeType() == Node.COMMENT_NODE)
					continue;
				
				//System.out.println(" Processing Node " +CurrentNode.getNodeName());
			
				if(CurrentNode.getNodeType() == Node.TEXT_NODE)
					lsNodeValue	= CurrentNode.getNodeValue();

				if(CurrentNode.getNodeName().equals("tableDisplay"))
				{
					DistributionTableDisplay objTableDisplay = populateTableDisplay(CurrentNode);
					mALTableDisplay.add(objTableDisplay);
				}

				if(CurrentNode.getNodeName().equals("sheetActionList"))
				{
					mALSheetAction  = populateAndGetAction(CurrentNode);
					objWorkSheet.setmALSheetAction(mALSheetAction);
				}
			}

			objWorkSheet.setmALTableDisplay(mALTableDisplay);
		}
		return objWorkSheet;
	}

	public DistributionTableDisplay populateTableDisplay(Node node)
	{
		DistributionTableDisplay objTableDisplay = new DistributionTableDisplay();
		
		ArrayList mALActionList = new ArrayList();

		if(node.hasChildNodes())
		{
			NodeList childNodes = node.getChildNodes();
			int mlRecordCount	= childNodes.getLength();
			String lsNodeValue	= "";

			if(node.hasAttributes())
			{
				NamedNodeMap attrs = node.getAttributes();
				int numAttrs = attrs.getLength();

				for (int k = 0; k < numAttrs; k++)
				{
					Node attr			= attrs.item(k);
					String attrName		= attr.getNodeName();
					String attrValue	= attr.getNodeValue();
					if(attrName.equals("name"))
						objTableDisplay.setmsTableDisplayName(attrValue);
					if(attrName.equals("xPos"))
						objTableDisplay.setmsXPos(attrValue);
					if(attrName.equals("yPos"))
						objTableDisplay.setmsYPos(attrValue);
					if(attrName.equals("hidden"))
						objTableDisplay.setmsHidden(attrValue);
				}
			}

			for(int i=0;i<mlRecordCount;i++) 
			{
				Node CurrentNode	= childNodes.item(i);
				
				if(CurrentNode.getNodeType() == Node.COMMENT_NODE)
					continue;
				
				//System.out.println(" Processing Node " +CurrentNode.getNodeName());

				if(CurrentNode.getNodeName().equals("collaboration"))
				{
					NamedNodeMap attrs	= CurrentNode.getAttributes();
					int numAttrs		= attrs.getLength();
					Node attr			= attrs.item(0);
					String attrName		= attr.getNodeName();
					String attrValue	= attr.getNodeValue();
					if(attrName.equals("name"))
						objTableDisplay.setmsCollabrationName(attrValue);
				}

				if(CurrentNode.getNodeName().equals("whiteboard"))
				{
					NamedNodeMap attrs	= CurrentNode.getAttributes();
					int numAttrs		= attrs.getLength();
					Node attr			= attrs.item(0);
					String attrName		= attr.getNodeName();
					String attrValue	= attr.getNodeValue();
					if(attrName.equals("name"))
						objTableDisplay.setmsWhiteBoardName(attrValue);
				}
				
				if(CurrentNode.getNodeName().equals("table"))
				{
					NamedNodeMap attrs = CurrentNode.getAttributes();
					int numAttrs = attrs.getLength();

					for (int k = 0; k < numAttrs; k++)
					{
						Node attr			= attrs.item(k);
						String attrName		= attr.getNodeName();
						String attrValue	= attr.getNodeValue();
						if(attrName.equals("name"))
							objTableDisplay.setmsTableName(attrValue);
						if(attrName.equals("transpose"))
						{
							boolean status = false;
							if(attrValue.equals("false"))
								objTableDisplay.setmbTranspose(status);
							else
								objTableDisplay.setmbTranspose(!status);
						}
					}

					if(CurrentNode.hasChildNodes())
					{
						NodeList tablechildNodes	= CurrentNode.getChildNodes();

						for(int a=0;a<tablechildNodes.getLength();a++) 
						{
							Node CurrentChildNode	= tablechildNodes.item(a);
							if(CurrentChildNode.getNodeType() == Node.COMMENT_NODE)
								continue;
							
							//System.out.println(" Processing Node " +CurrentChildNode.getNodeName());
						
							if(CurrentChildNode.getNodeName().equals("tableActionList"))
							{
								mALActionList = populateAndGetAction(CurrentChildNode);
								objTableDisplay.setmsTableActionList(mALActionList);
							}
						}
					}
				}
			}
		}
		return objTableDisplay;
	}

	public ArrayList populateAndGetAction(Node node)//tableactionlist or sheetactionlist
	{
		//System.out.println(" populateAndGetAction " +node.getNodeName());
		ArrayList mALTemp = new ArrayList();
		ArrayList mALArgument = new ArrayList();

		if(node.hasChildNodes())
		{
			NodeList childNodes = node.getChildNodes();
			int mlRecordCount	= childNodes.getLength();
			String lsNodeValue	= "";

			for(int i=0;i<mlRecordCount;i++) // to get action nodes
			{
				DistributionAction objDistAction = new DistributionAction();
				Node CurrentNode	= childNodes.item(i);
				
				if(CurrentNode.getNodeType() == Node.COMMENT_NODE)
					continue;
				
				//System.out.println(" Processing Node " +CurrentNode.getNodeName());
			
				Node actionChildNode = CurrentNode.getFirstChild();//bw...action
				//System.out.println(" actionChildNode Node " +actionChildNode.getNodeName());
			
				if(CurrentNode.hasAttributes())
				{
					NamedNodeMap attrs = CurrentNode.getAttributes();
					int numAttrs = attrs.getLength();

					for (int k = 0; k < numAttrs; k++)
					{
						Node attr			= attrs.item(k);
						String attrName		= attr.getNodeName();
						String attrValue	= attr.getNodeValue();
						if(attrName.equals("name"))
						{
							//System.out.println(" attrName"+attrName+attrValue);
							objDistAction.setmsActionName(attrValue);
						}
					}
				}

				if(actionChildNode.getNodeName().equals("bwInsertRowAction"))
				{
					objDistAction.setmsActionType("bwInsertRowAction");
				}
				
				if(actionChildNode.getNodeName().equals("bwDeleteRowAction"))
				{
					objDistAction.setmsActionType("bwDeleteRowAction");
				}

				if(actionChildNode.getNodeName().equals("bwSubmitAllAction"))
				{
					objDistAction.setmsActionType("bwSubmitAllAction");
				}
				
				if(actionChildNode.getNodeName().equals("bwRefreshAllAction"))
				{
					objDistAction.setmsActionType("bwRefreshAllAction");
				}
				
				if(actionChildNode.getNodeName().equals("bwCheckStatusAllAction"))
				{
					objDistAction.setmsActionType("bwCheckStatusAllAction");
				}
				
				if(actionChildNode.getNodeName().equals("bwCustomAction"))
				{
					objDistAction.setmsActionType("bwCustomAction");

				}
					//System.out.println("actionChildNode.getNodeName()"+actionChildNode.getNodeName());
				NodeList MacroList = actionChildNode.getChildNodes();
				for(int x=0;x<MacroList.getLength();x++)//to get pre, post ,argument
				{
					Node MacroNode	= MacroList.item(x);
					lsNodeValue	= MacroNode.getNodeName();
					//System.out.println("MacroNode.getNodeName()"+MacroNode.getNodeName());
					if(lsNodeValue.equals("preMacro"))
					{
						NamedNodeMap attrs = MacroNode.getAttributes();
						int numAttrs = attrs.getLength();

						for (int k = 0; k < numAttrs; k++)
						{
							Node attr			= attrs.item(k);
							String attrName		= attr.getNodeName();
							String attrValue	= attr.getNodeValue();
							if(attrName.equals("name"))
									objDistAction.setmsPreActionMacro(attrValue);
						}
					}
						
					if(lsNodeValue.equals("postMacro"))
					{
						NamedNodeMap attrs = MacroNode.getAttributes();
						int numAttrs = attrs.getLength();

						for (int k = 0; k < numAttrs; k++)
						{
							Node attr			= attrs.item(k);
							String attrName		= attr.getNodeName();
							String attrValue	= attr.getNodeValue();
							if(attrName.equals("name"))
								objDistAction.setmsPostActionMacro(attrValue);
						}
					}

					if(lsNodeValue.equals("argument"))
					{
						if(MacroNode.hasChildNodes())
						{
							Node textNode = MacroNode.getFirstChild();
							mALArgument.add(textNode.getNodeValue());
						}
					}
				}
				if(lsNodeValue.equals("argument"))
					objDistAction.setmALArgument(mALArgument);
				mALTemp.add(objDistAction);
			}
		}
		return mALTemp;
	}
	
	
	public DistributionUser populateTemplateUser(Node node)//member
	{
		DistributionUser objUser = new DistributionUser();

		if(node.hasChildNodes())
		{
			NodeList childNodes = node.getChildNodes();
			int mlRecordCount	= childNodes.getLength();
			String lsNodeValue	= "";

			for(int i=0;i<mlRecordCount;i++) 
			{
				lsNodeValue = "";
				Node CurrentNode	= childNodes.item(i);
				
				if(CurrentNode.getNodeType() == Node.COMMENT_NODE)
					continue;
				
				//System.out.println(" Processing Node " +CurrentNode.getNodeName());
				
				if(CurrentNode.getNodeName().equals("user"))
				{
					if(CurrentNode.hasChildNodes())
					{
						Node textNode = CurrentNode.getFirstChild();
						objUser.setmsUserEmail(textNode.getNodeValue());
					}
				}	
				if(CurrentNode.getNodeName().equals("neighborhood"))
				{
					if(CurrentNode.hasChildNodes())
					{
						Node textNode = CurrentNode.getFirstChild();
						objUser.setmsNeighbourhood(textNode.getNodeValue());
					}
				}
			}
		}
		return objUser;
	}
}