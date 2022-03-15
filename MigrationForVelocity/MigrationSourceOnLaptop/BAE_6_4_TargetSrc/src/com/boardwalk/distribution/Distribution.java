package com.boardwalk.distribution;

import java.util.*;

public class Distribution implements java.io.Serializable 
{
	private String		msName;
	private ArrayList	Managers;
	private ArrayList	Templates;
	
	public Distribution()
	{
		msName		= "";
	}

	/*************************************************/	
	public String getmsName() 
	{
		return msName;
	}
	
	public void setmsName(String msName) 
	{
		this.msName = msName;
		//System.out.println("Distribution msName :: "+msName);
	}

	/*************************************************/	
	public ArrayList getManagers() 
	{
		return Managers;
	}
	
	public void setManagers(ArrayList Managers) 
	{
		this.Managers = Managers;

	}

	/*************************************************/	
	public ArrayList getTemplates() 
	{
		return Templates;
	}
	
	public void setTemplates(ArrayList Templates) 
	{
		this.Templates = Templates;
	}

	/*************************************************/	

};


