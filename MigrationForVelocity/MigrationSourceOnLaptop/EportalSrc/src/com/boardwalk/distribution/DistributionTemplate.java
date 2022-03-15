package com.boardwalk.distribution;

import java.util.*;

public class DistributionTemplate implements java.io.Serializable 
{
	private String msTemplateName;
	private String msTemplateLocation;
	private String mssubject;
	private String msmessage;
	private ArrayList mALTemplateWorkBook;
	private ArrayList mALTemplateUsers;
	
	public DistributionTemplate()
	{
		msTemplateName		= "";
		msTemplateLocation	= "";
	}

	/*************************************************/	
	public String getmsTemplateName() 
	{
		return msTemplateName;
	}
	
	public void setmsTemplateName(String msTemplateName) 
	{
		this.msTemplateName = msTemplateName;
		//System.out.println("DistributionTemplate msTemplateName :: " +msTemplateName);
	}

	/**************************************************/	
	public String getmsTemplateLocation() 
	{
		return msTemplateLocation;
	}
	
	public void setmsTemplateLocation(String msTemplateLocation) 
	{
		this.msTemplateLocation = msTemplateLocation;
		//System.out.println("DistributionTemplate msTemplateLocation :: " +msTemplateLocation);
	}

	/**************************************************/	
	public String getmssubject() 
	{
		return mssubject;
	}
	
	public void setmssubject(String mssubject) 
	{
		this.mssubject = mssubject;
		//System.out.println("DistributionTemplate mssubject :: " +mssubject);
	}

	/**************************************************/	
	public String getmsmessage() 
	{
		return msmessage;
	}
	
	public void setmsmessage(String msmessage) 
	{
		this.msmessage = msmessage;
	}

	/**************************************************/	
	public ArrayList getmALTemplateWorkBook() 
	{
		return mALTemplateWorkBook;
	}
	
	public void setmALTemplateWorkBook(ArrayList mALTemplateWorkBook) 
	{
		this.mALTemplateWorkBook = mALTemplateWorkBook;
	}

	/**************************************************/	
	public ArrayList getmALTemplateUsers() 
	{
		return mALTemplateUsers;
	}
	
	public void setmALTemplateUsers(ArrayList mALTemplateUsers) 
	{
		this.mALTemplateUsers = mALTemplateUsers;
	}

};


