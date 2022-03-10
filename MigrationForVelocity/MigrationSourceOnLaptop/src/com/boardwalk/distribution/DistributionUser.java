package com.boardwalk.distribution;

import java.util.*;

public class DistributionUser implements java.io.Serializable 
{
	private String msUserEmail;
	private String msNeighbourhood;
	
	public DistributionUser()
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
		//System.out.println("DistributionUser msUserEmail :: "+msUserEmail);
	}

	/**************************************************/	
	public String getmsNeighbourhood() 
	{
		return msNeighbourhood;
	}
	
	public void setmsNeighbourhood(String msNeighbourhood) 
	{
		this.msNeighbourhood = msNeighbourhood;
		//System.out.println("DistributionUser msNeighbourhood :: "+msNeighbourhood);
	}
	/**************************************************/	

};


