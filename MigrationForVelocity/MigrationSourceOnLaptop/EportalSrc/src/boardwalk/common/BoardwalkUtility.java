/*
 * @(#)BoardwalkUtility.java 1.0
 *
 * Copyright 2004 BoardwalkTech, Inc. All rights reserved.
 * BoardwalkTech/CONFIDENTIAL. Use is subject to license terms.
 */

package boardwalk.common;

import java.util.*;
import java.io.InputStream;
import java.io.FileInputStream;


/**
 *  BoardwalkUtility class will have set of methods which can be invoked from any where.
 *	They will be static functions meant for checking null, replacing string patterns etc.
 */
 public class BoardwalkUtility
 {
	public BoardwalkUtility()
	{

	}

	public static String getHTMLWithEcsapeSequence(String asString)
	{
		// While rendering the HTML content special chars like <>'" etc.
		// require a escape sequence so that it is properly displayed in the browser.
		
		String lsReturnStr = "";
		lsReturnStr = replaceString(asString,">","&gt");
		lsReturnStr = replaceString(lsReturnStr,"<","&lt");
		lsReturnStr = replaceString(lsReturnStr,"'","\\'");
		lsReturnStr = replaceString(lsReturnStr,"'","\\'");
		return lsReturnStr;
	}

	// This method will replace the search pattern in a String
	public static String replaceString (String sStr, String sSearch, String sReplace)
    {
		//String sStr = "this'is'a'is'str'ing";
		//String sSearch = "'";
		//String sReplace="''";
		if(sStr == null) return "";
		
		//Assuming that in 90% cases there will be nothing to replace, this should be faster
		if(sStr.indexOf(sSearch) < 0)
			return sStr;

		StringBuffer sReturn= new StringBuffer();
		int lp=0;
		int pos=0;
		for(int i=0; i<sStr.length();i++)
		{
			pos=sStr.indexOf(sSearch,pos);
			if (pos<0 )
			{
				sReturn.append(sStr.substring(lp,sStr.length()));
				break;
			}
			sReturn.append(sStr.substring(lp,pos)+sReplace);
			pos=pos+sSearch.length();
			lp=pos;
		}
		return sReturn.toString();
	}

	public static boolean checkIfNullOrBlank( String asStr)
	{
		if(asStr == null ||asStr.trim().equals(""))	
			return true;
		return false;	
	}

	/**
		This method returns a String array by splitting a string based on a token.
	**/
	public static String[] getArrayFromStrTok(String str, String tok)
	{
		//------------how to use this function-----------------------------
		//String arr[] = getArrayFromStrTok("this, is, a test, string", ",");
		StringTokenizer st = new StringTokenizer(str,tok);
		String arr[] = new String[st.countTokens()];
		int i=0;
		while (st.hasMoreTokens())
		{
			arr[i++] = st.nextToken();
		}

		return arr;
	}


	/** Modifies the SQL <i>sStr</i> replacing ' " and \ with the corresponding escape characters
		This does the job modifying the SQL with proper escape sequence
	*/
	public static String replaceSQLString(String sStr)
	{
		//String sStr = "this'is'a'is'str'ing";
		//String sSearch = "'";
		//String sReplace="''";
		String sReturn =  replaceString(sStr,"'","''");
		return sReturn;
		
	}
		/**
		This method returns the Properties object based on the input parameter properties filepath.
	**/
	public Properties getPropertiesFile(String asPropertyFilePath)
	{
		//System.out.println("sampo.properties Properties file loaded..."+asPropertyFilePath);
		Properties configProp = new Properties();
		try 
		{			
			InputStream is = new FileInputStream(asPropertyFilePath);
			configProp.load(is);				
			System.out.println("sampo.properties Properties file loaded.");
		} 
		catch (Exception e) 
		{
			System.out.println("Can't read sampo.properties properties file.");
			return null;
		}	
		return configProp;
	}


	/**
		This method returns the string value from the properties file.
	**/
	public String getStringPropsValue(String asProperty , String asDefaultValue , Properties asProps)
	{
		String lsRetVal  = asProps.getProperty(asProperty);
		if(lsRetVal == null || lsRetVal.trim().equals(""))	
				lsRetVal = asDefaultValue;
		return lsRetVal.trim();	
	}


	/**
		This method returns the integer value from the properties file.
	**/
	public int getIntPropsValue(String asProperty , int aiDefaultValue , Properties asProps)
	{
		String lsPropVal	= asProps.getProperty(asProperty);
		int liRetVal		= aiDefaultValue;
		Integer lIPropVal	= new Integer(0);
		if(lsPropVal != null && !lsPropVal.trim().equals(""))
		{
			try
			{
				lIPropVal = new Integer(lsPropVal);
			}
			catch( Exception ex )
			{ 					
				return liRetVal;
			}
			liRetVal = lIPropVal.intValue();
		}
		return liRetVal;
	}	

 };