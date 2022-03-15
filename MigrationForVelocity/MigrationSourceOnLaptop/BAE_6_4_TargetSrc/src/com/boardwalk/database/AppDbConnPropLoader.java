package com.boardwalk.database;

import java.io.*;
import java.lang.*;
import java.util.*;


public class AppDbConnPropLoader
{
	public AppDbConnPropLoader()
	{
	}

	public Properties getPropertiesFile(String asPropertyFilePath)
	{
		Properties configProp = new Properties();
		try
		{
			InputStream is = new FileInputStream(asPropertyFilePath);//"D:/Tomcat55/webapps/BoardwalkDev/props/sampo.properties");
			configProp.load(is);
			System.out.println(" SIDL: boardwalk.properties Properties file loaded.");
		}
		catch (Exception e)
		{
			System.out.println(" SIDL: Can't read boardwalk.properties properties file.");
			return null;
		}
		return configProp;
	}

	public String getStringPropsValue(String asProperty , String asDefaultValue , Properties asProps)
	{
		String lsRetVal  = asProps.getProperty(asProperty);
		if(lsRetVal == null || lsRetVal.trim().equals(""))
				lsRetVal = asDefaultValue;
		return lsRetVal.trim();
	}

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

	public static String[] getArrayFromStrTok2(String asStr, String tok)
	{
		return getArrayFromStrTok2( asStr, tok, 5000);
	}

	public static String[] getArrayFromStrTok2(String asStr, String tok, int alCount)
	{
		char lcSeparator = tok.charAt(0);
		if(checkIfNullOrBlank(asStr))
			return new String[0];

		String[] larrsTokField = new String[alCount];
		int liFieldCount = 0;
		int liPrevIndex = 0;
		int liStrLen = asStr.length();
		for(int i=0;i<liStrLen;i++)
		{
			if(asStr.charAt(i) == lcSeparator)
			{
				if(liPrevIndex == i)
					larrsTokField[liFieldCount] = "";
				else
					larrsTokField[liFieldCount] = asStr.substring(liPrevIndex,i);

				liPrevIndex = i+1;
				liFieldCount++;
			}
		}

		if(liPrevIndex < liStrLen)
		{
			larrsTokField[liFieldCount] = asStr.substring(liPrevIndex);
			liFieldCount++;
		}

		if(liPrevIndex == liStrLen)
		{
			larrsTokField[liFieldCount] = "";
			liFieldCount++;
		}

		String[] laRetArr = new String[liFieldCount];
		for(int i=0;i<liFieldCount;i++)
			laRetArr[i] = larrsTokField[i];
		return laRetArr;
	}
	public static boolean checkIfNullOrBlank( String asStr)
	{
		if(asStr == null ||asStr.trim().equals(""))
			return true;
		return false;
	}

}