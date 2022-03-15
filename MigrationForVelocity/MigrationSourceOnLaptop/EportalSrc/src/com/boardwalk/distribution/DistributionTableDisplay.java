package com.boardwalk.distribution;

import java.util.*;

public class DistributionTableDisplay implements java.io.Serializable 
{
	private String msTableDisplayName;
	private String msXPos;
	private String msYPos;
	private String msHidden;
	private String msCollabrationName;
	private String msWhiteBoardName;
	private String msTableName;
	private boolean mbTranspose;
	private ArrayList msTableActionList;
	
	public DistributionTableDisplay()
	{
		msTableDisplayName	= "";
		msXPos				= "";
		msYPos				= "";
		msCollabrationName	= "";
		msWhiteBoardName	= "";
		msTableName			= "";
		mbTranspose			= false;
		msHidden			= "";
	}

	/*************************************************/	
	public String getmsTableDisplayName() 
	{
		return msTableDisplayName;
	}
	
	public void setmsTableDisplayName(String msTableDisplayName) 
	{
		this.msTableDisplayName = msTableDisplayName;
		//System.out.println("DistributionTableDisplay msTableDisplayName :: "+msTableDisplayName);
	}

	/*************************************************/	
	public String getmsXPos() 
	{
		return msXPos;
	}
	
	public void setmsXPos(String msXPos) 
	{
		this.msXPos = msXPos;
		//System.out.println("DistributionTableDisplay msXPos :: "+msXPos);
	}

	/*************************************************/	
	public String getmsYPos() 
	{
		return msYPos;
	}
	
	public void setmsYPos(String msYPos) 
	{
		this.msYPos = msYPos;
		//System.out.println("DistributionTableDisplay msYPos :: "+msYPos);
	}

	/**************************************************/	
	public String getmsHidden() 
	{
		return msHidden;
	}
	
	public void setmsHidden(String msHidden) 
	{
		this.msHidden = msHidden;
		//System.out.println("DistributionTableDisplay msHidden :: "+msHidden);
	}

	/**************************************************/	
	public String getmsCollabrationName() 
	{
		return msCollabrationName;
	}
	
	public void setmsCollabrationName(String msCollabrationName) 
	{
		this.msCollabrationName = msCollabrationName;
		//System.out.println("DistributionTableDisplay msCollabrationName :: "+msCollabrationName);
	}

	/**************************************************/	
	public String getmsWhiteBoardName() 
	{
		return msWhiteBoardName;
	}
	
	public void setmsWhiteBoardName(String msWhiteBoardName) 
	{
		this.msWhiteBoardName = msWhiteBoardName;
		//System.out.println("DistributionTableDisplay msWhiteBoardName :: "+msWhiteBoardName);
	}
	/**************************************************/	
	public String getmsTableName() 
	{
		return msTableName;
	}
	
	public void setmsTableName(String msTableName) 
	{
		this.msTableName = msTableName;
		//System.out.println("DistributionTable msTableName :: "+msTableName);
	}

	/**************************************************/	
	public boolean getmbTranspose() 
	{
		return mbTranspose;
	}
	
	public void setmbTranspose(boolean mbTranspose) 
	{
		this.mbTranspose = mbTranspose;
		//System.out.println("DistributionTable mbTranspose :: "+mbTranspose);
	}

	/**************************************************/	
	public ArrayList getmsTableActionList() 
	{
		return msTableActionList;
	}
	
	public void setmsTableActionList(ArrayList msTableActionList) 
	{
		this.msTableActionList = msTableActionList;
	}
};


