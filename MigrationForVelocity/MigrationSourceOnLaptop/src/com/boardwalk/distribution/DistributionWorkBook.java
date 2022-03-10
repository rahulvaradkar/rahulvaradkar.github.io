package com.boardwalk.distribution;

import java.util.*;

public class DistributionWorkBook implements java.io.Serializable 
{
	private String msName;
	private ArrayList msWorkSheets;
	private ArrayList mALOnLoadEventArguments;
	
	public DistributionWorkBook()
	{
		msName	= "";
	}

	/*************************************************/	
	public String getmsName() 
	{
		return msName;
	}
	
	public void setmsName(String msName) 
	{
		this.msName = msName;
		//System.out.println("DistributionWorkBook msName :: "+msName);
	}

	/**************************************************/	
	public ArrayList getmsWorkSheets() 
	{
		return msWorkSheets;
	}
	
	public void setmsWorkSheets(ArrayList msWorkSheets) 
	{
		this.msWorkSheets = msWorkSheets;
	}

	/**************************************************/	
	public ArrayList getmALOnLoadEvent() 
	{
		return mALOnLoadEventArguments;
	}
	
	public void setmALOnLoadEvent(ArrayList mALOnLoadEventArguments) 
	{
		this.mALOnLoadEventArguments = mALOnLoadEventArguments;
	}
};


