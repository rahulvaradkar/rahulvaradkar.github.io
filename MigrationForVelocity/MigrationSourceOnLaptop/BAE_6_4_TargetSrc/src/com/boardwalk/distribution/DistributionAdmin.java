package com.boardwalk.distribution;

import java.util.*;


// Basically this class is meant for Distribution Managers
public class DistributionAdmin implements java.io.Serializable 
{
	private String msUserEmail;
	private String msNeighbourhood;
	
	public DistributionAdmin()
	{
		msUserEmail		= "";
		msNeighbourhood	= "";
	}

	/*************************************************/	
	public String getmsUserEmail() 
	{
		return msUserEmail;
	}
	
	public void setmsUserEmail(String msUserEmail) 
	{
		this.msUserEmail = msUserEmail;
		//System.out.println("DistributionAdmin msUserEmail :: "+msUserEmail);
	}

	/**************************************************/	
	public String getmsNeighbourhood() 
	{
		return msNeighbourhood;
	}
	
	public void setmsNeighbourhood(String msNeighbourhood) 
	{
		this.msNeighbourhood = msNeighbourhood;
		//System.out.println("DistributionAdmin msNeighbourhood :: "+msNeighbourhood);
	}
	/**************************************************/	

};


