package com.boardwalk.distribution;

import java.util.*;

public class DistributionTemplateWorkSheet implements java.io.Serializable 
{
	private String msWorkSheetName;
	private String msUseTemplateSheet;
	private ArrayList mALTableDisplay;
	private ArrayList mALSheetAction;

	public DistributionTemplateWorkSheet()
	{
		msWorkSheetName		= "";
		msUseTemplateSheet	= "";
	}

	/*************************************************/	
	public String getmsWorkSheetName() 
	{
		return msWorkSheetName;
	}
	
	public void setmsWorkSheetName(String msWorkSheetName) 
	{
		this.msWorkSheetName = msWorkSheetName;
		//System.out.println("DistributionTemplateWorkSheet msWorkSheetName :: "+msWorkSheetName);
	}

	/**************************************************/	
	public String getmsUseTemplateSheet() 
	{
		return msUseTemplateSheet;
	}
	
	public void setmsUseTemplateSheet(String msUseTemplateSheet) 
	{
		this.msUseTemplateSheet = msUseTemplateSheet;
		//System.out.println("DistributionTemplateWorkSheet msUseTemplateSheet :: "+msUseTemplateSheet);
	}

	/**************************************************/	
	public ArrayList getmALTableDisplay() 
	{
		return mALTableDisplay;
	}
	
	public void setmALTableDisplay(ArrayList mALTableDisplay) 
	{
		this.mALTableDisplay = mALTableDisplay;
	}

	/**************************************************/	
	public ArrayList getmALSheetAction() 
	{
		return mALSheetAction;
	}
	
	public void setmALSheetAction(ArrayList mALSheetAction) 
	{
		this.mALSheetAction = mALSheetAction;
	}

};


