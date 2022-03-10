package com.boardwalk.distribution;

import java.util.*;

public class DistributionAction implements java.io.Serializable 
{
	private String msActionName;
	private String msActionType;
	private String msPreActionMacro;
	private String msPostActionMacro;
	private ArrayList mALArgument;
	
	public DistributionAction()
	{
		msActionName		= "";
		msActionType		= "";
		msPreActionMacro	= "";
		msPostActionMacro	= "";
	}

	/*************************************************/	
	public String getmsActionName() 
	{
		return msActionName;
	}
	
	public void setmsActionName(String msActionName) 
	{
		this.msActionName = msActionName;
		//System.out.println("DistributionAction msActionName :: "+msActionName);
	}

	/**************************************************/	
	public String getmsActionType() 
	{
		return msActionType;
	}
	
	public void setmsActionType(String msActionType) 
	{
		this.msActionType = msActionType;
		//System.out.println("DistributionAction msActionType :: "+msActionType);
	}

	/**************************************************/	
	public String getmsPreActionMacro() 
	{
		return msPreActionMacro;
	}
	
	public void setmsPreActionMacro(String msPreActionMacro) 
	{
		this.msPreActionMacro = msPreActionMacro;
		//System.out.println("DistributionAction msPreActionMacro :: "+msPreActionMacro);
	}

	/**************************************************/	
	public String getmsPostActionMacro() 
	{
		return msPostActionMacro;
	}
	
	public void setmsPostActionMacro(String msPostActionMacro) 
	{
		this.msPostActionMacro = msPostActionMacro;
		//System.out.println("DistributionAction msPostActionMacro :: "+msPostActionMacro);
	}
	/**************************************************/	
	public ArrayList getmALArgument() 
	{
		return mALArgument;
	}
	
	public void setmALArgument(ArrayList mALArgument) 
	{
		this.mALArgument = mALArgument;
		//System.out.println("DistributionAction mALArgument :: "+mALArgument);
	}
};


