package com.boardwalk.distribution;

import java.util.*;

public class DistributionList implements java.io.Serializable 
{
	private static ArrayList Distribution;
	
	private DistributionList()
	{

	}
	/*************************************************/	
	private ArrayList getDistribution() 
	{
		return Distribution;
	}
	
	private void setDistribution(ArrayList Distribution) 
	{
		this.Distribution = Distribution;
	}
	/*************************************************/	
	public static ArrayList getObjects(String asSourceXml,String asTargetXml)
	{
		String lsSourceXmlPath	= asSourceXml;
		String lsTargetXmlPath	= asTargetXml;
		XmlParse obj			= new XmlParse(lsSourceXmlPath,lsTargetXmlPath);
		Distribution			= obj.parse();

		if(Distribution != null)
			return Distribution;
		else 
			return null;
	}

	/*public static void main(String args[])
	{
		String lsFilePath = "D:\\From_MyDocuments\\Boardwalk\\Xilinic\\xilinxdistribution.xml";
		String lsFilePathtarget = "D:\\From_MyDocuments\\Boardwalk\\Xilinic\\xilinx.xml";
		ArrayList main = new ArrayList();
		main = DistributionList.getObjects(lsFilePath,lsFilePathtarget);
		//System.out.println("main"+main);
	}*/
};


