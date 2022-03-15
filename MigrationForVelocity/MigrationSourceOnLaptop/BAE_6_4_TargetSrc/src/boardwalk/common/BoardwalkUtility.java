/*
 * @(#)BoardwalkUtility.java 1.0
 *
 * Copyright 2004 BoardwalkTech, Inc. All rights reserved.
 * BoardwalkTech/CONFIDENTIAL. Use is subject to license terms.
 */

package boardwalk.common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.io.InputStream;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
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

	 public static String getBoardwalkProperty(
			 String propertyKey
	 ) throws Exception {
		 System.out.println("Inside BoardwalkUtility.getPropertyValue");

		 Properties prop = new Properties();
		 String propertyValue = "";

		 try {
			 InputStream is = BoardwalkUtility.class.getClassLoader().getResourceAsStream("boardwalk.properties");
			 prop.load(is);

			 propertyValue = prop.getProperty(propertyKey);
			 //System.out.println("getPropertyValue - propertyValue: " + propertyValue);
		 } catch (Exception e) {
			 System.out.println("ERROR: "+ e);
			 throw e;
		 }

		 return propertyValue;
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
	
	public static String convertExcelDateToNormalDate(double date) {
		String normalDate = "";
		
		try {
			long days = (long)date;
			double serialTime = date - days;
			long secondsOfDay = Math.round(serialTime * 24 * 60 * 60);
			LocalDateTime start = LocalDateTime.of(1900, 1, 1, 0, 0, 0);
			
			LocalDateTime localDateTime = start.plusDays(days).plusSeconds(secondsOfDay).minusDays(2);
			normalDate = localDateTime.toString();
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		return normalDate;
	}

	public static String convertExcelDateToNormalDate(int days) {
		//System.out.println("Inside BoardwalkUtility.convertExcelDateToNormalDate");
		String date = "";
		
		try {
			LocalDate start = LocalDate.of(1900, 1, 1);
			LocalDate localDate = start.plusDays(days).minusDays(2);
			date = localDate.toString();
		} catch(Exception e) {
			e.printStackTrace();
		}

		return date;
	}
	
	public static long convertNormalDateToExcelDate(Date date) {
		//System.out.println("Inside BoardwalkUtility.convertNormalDateToExcelDate");
		long days = -1;

		try {
			LocalDate start = LocalDate.of(1900, 1, 1);

			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			LocalDate dateToConvert = LocalDate.of(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
	       
			days = ChronoUnit.DAYS.between(start, dateToConvert) + 2;
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return days;
	}
	
	public static int getInt(java.sql.ResultSet resultSet, String columnName) {
		// Default value should be 0 if exception is thrown.
		int val = 0;
		// To ignore Exception- Can not convert nvarchar to int
		try {
			 val = resultSet.getInt(columnName);
		} catch (SQLException e) {
			 
		}
		return val;
	}
	
	public static double getDouble(java.sql.ResultSet resultSet, String columnName) {
		// Default value should be 0 if exception is thrown.
		double val = 0.0d;
		// To ignore Exception- Can not convert nvarchar to int
		try {
			 val = resultSet.getDouble(columnName);
		} catch (SQLException e) {
			 
		}
		return val;
	}

	 public static boolean getBoolean(java.sql.ResultSet resultSet, String columnName) {
		 // Default value should be 0 if exception is thrown.
		 boolean val = false;
		 // To ignore Exception- Can not convert nvarchar to int
		 try {
			 val = resultSet.getBoolean(columnName);
		 } catch (SQLException e) {

		 }
		 return val;
	 }
	 
	 public static String getCurrentUTCDateTime() {
		 String currentUTCDateTime = "";
		try {
			 DateTimeFormatter formatter = DateTimeFormatter.ofPattern("YYYYMMdd'_'HHmmssSSSSSS"); 
			 OffsetDateTime now = OffsetDateTime.now( ZoneOffset.UTC );
			 currentUTCDateTime = now.format(formatter);
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return currentUTCDateTime;
	 }

	 public static String getCurrentUTCDateTimeFormatted() {
		 String currentUTCDateTime = "";
		 try {
			 DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
			 OffsetDateTime now = OffsetDateTime.now( ZoneOffset.UTC );
			 currentUTCDateTime = now.format(formatter);
		 } catch(Exception e) {
			 e.printStackTrace();
		 }

		 return currentUTCDateTime;
	 }

	 public static boolean isValidDate(String inDate) {
		 SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		 dateFormat.setLenient(false);

		 try {
			 dateFormat.parse(inDate.trim());
		 } catch (ParseException e) {
			 return false;
		 }

		 return true;
	 }

	 public static boolean isInteger(String strValue) {
		 try {
			 Integer.parseInt(strValue);
		 } catch (Exception e) {
			 return false;
		 }

		 return true;
	 }
	 
	 public static String extractDateFromDateTime(String dateTime) throws Exception {
		 String date = "";
		 
		 try {
			 SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
			 dateFormat.setLenient(false);
			 
			 Date parsedDateTime = dateFormat.parse(dateTime);
			 
			 date = new SimpleDateFormat("yyyy-MM-dd").format(parsedDateTime);
		 } catch (Exception e) {
			 System.out.println("ERROR: "+ e);
			 throw e;
		 }
		 
		 return date;
	 }
	 
	 public static String extractTimeFromDateTime(String dateTime) throws Exception {
		 String time = "";
		 try {
			 SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
			 dateFormat.setLenient(false);
			 
			 Date parsedDateTime = dateFormat.parse(dateTime);
			 
			 time = new SimpleDateFormat("HH:mm:ss.SSS").format(parsedDateTime);
		 } catch (Exception e) {
			 System.out.println("ERROR: "+ e);
			 throw e;
		 } 
		 return time;
	 }
	 
	 public static String addMinutesToDate(String date, int minutes) throws Exception {
		 String outputDate = "";

		 try {
			 if (minutes > 0) {
				 SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
				 dateFormat.setLenient(false);
				 
				 Date inputDate = dateFormat.parse(date);
				 
				 Calendar cal = Calendar.getInstance(); 
				 cal.setTime(inputDate);
				 cal.add(Calendar.MINUTE, minutes); 
				 
				 outputDate = dateFormat.format(cal.getTime());
			 }
		 } catch (Exception e) {
			 System.out.println("ERROR: " + e);
			 throw e;
		 }
		 
		 return outputDate;
	 }
	 
	 public static long getMillisecondsFromMinutes(int minutes) {
		long hoursInMilliseconds = 0;

		if(minutes > 0) {
			hoursInMilliseconds = minutes * 60 * 1000;
		}

		return hoursInMilliseconds;
	 }
	 
	 public static long getDiffBetweenTwoDatesInMilliseconds(String startDate, String endDate) throws Exception {
		 long diffInMilliseconds = 0L;
		 
		 try {
			 SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
			 dateFormat.setLenient(false);
			 
			 long startTime = dateFormat.parse(startDate.trim()).getTime();
			 long endTime = dateFormat.parse(endDate.trim()).getTime();
			 
			 diffInMilliseconds = endTime - startTime;
		 } catch (ParseException e) {
			 System.out.println("ERROR: "+ e);
			 throw e;
		 }
		 return diffInMilliseconds;
	 }
	 
	 public static int compareDate(String firstDate, String secondDate) {
		 int comparison = -1;
		 try {
			 SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
			 dateFormat.setLenient(false);
			 
			 long firstDateTime = dateFormat.parse(firstDate).getTime();
			 long secondDateTime = dateFormat.parse(secondDate).getTime();
			 
			 if (firstDateTime == secondDateTime) {
				 comparison = 0;
			 } else if (firstDateTime > secondDateTime) {
				 comparison = 1;
			 } else {
				 comparison  = -1;
			 }
		 } catch (Exception e) {
			 e.printStackTrace();
		 }
		 
		 return comparison;
	 }

	 public static int compareInt(int int1, int int2) {
		 int comparison = -1;
		 try {
			 if (int1 == int2) {
				 comparison = 0;
			 } else if (int1 > int2) {
				 comparison = 1;
			 }
		 } catch (Exception e) {
			 e.printStackTrace();
		 }

		 return comparison;
	 }

	 public static boolean isDateBetween(String start, String end, String inDate) {
		 try {
			 SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
			 dateFormat.setLenient(false);
			 Date dateToCheck = dateFormat.parse(inDate.trim());
			 
			 return dateFormat.parse(start).getTime() <= dateToCheck.getTime() && dateToCheck.getTime() <= dateFormat.parse(end).getTime();
		 } catch (ParseException e) {
			 return false;
		 }
	 }
	 
	 public static List<Integer> convertStringToList(
			 String s,
			 String delimiter
	 ) {
		 List<Integer> arr = new ArrayList<>();
		 if (s.trim().isEmpty()) {
			 return arr;
		 }
		
		 for (String sub : s.split(Pattern.quote(delimiter))) {
			 try {
				 Integer e = Integer.parseInt(sub.trim());
				 arr.add(e);
			 } catch(NumberFormatException e) {
				 // Ignore the Exception
			 }
		 }
		
		 return arr;
	 }
	 
	 public static <T> List<T> toList(
			 String s,
			 String delimiter
	 ){
		 List<T> arr = new ArrayList<>();
		 if (s.trim().isEmpty()) {
			 return arr;
		 }
		 
		 for (String sub : s.split(Pattern.quote(delimiter))) {
			 @SuppressWarnings("unchecked")
			 T e = (T)sub;
			 arr.add(e);
		 }	 
		 return arr;
	 }
	 
	 public static <T> String toString(
				List<T> arr,
				String delimiter
	 ) {
		 if(arr.isEmpty()) {
			 return "";
		 }
		 
		 StringBuilder sb = new  StringBuilder(); 
		 for(T a : arr) {
			 sb.append(a.toString());
			 sb.append(delimiter);
		 }
		 
		return sb.substring(0, sb.length() - 1);
	 }

	 public static <T extends Comparable<T>> boolean compareArrays(
	 		List<T> l1,
			List<T> l2
	 ) {
		 if (l1 == null && l2 == null) {
			 return true;
		 }

		 if(l1 == null || l2 == null) {
			 return false;
		 }

		 if(l1.size() != l2.size()) {
			 return false;
		 }

		 Collections.sort(l1);
		 Collections.sort(l2);

		 return l1.equals(l2);
	 }
	 
	 public static <K, V> Map<K, V> ListToMap(List<K> keys, List<V> values) throws Exception {
		 if (keys.size() != values.size()) {
			 throw new Exception("Can not combine list");
		 }
		 
	     Iterator<K> keyIter = keys.iterator();
	     Iterator<V> valIter = values.iterator();
	     return IntStream.range(0, keys.size()).boxed()
	            .collect(Collectors.toMap(_i -> keyIter.next(), _i -> valIter.next()));
	 }
	 
	 // It is used to perform a - b
	 public static <T> List<T> getDifference(List<T> aList, List<T> bList){
		
		 if (aList == null || bList == null) {
			 return new ArrayList<>();
		 }
		
		 List<T> difference = aList.stream()
				    .filter(aObject -> ! bList.contains(aObject))
				    .collect(Collectors.toList());
		 
		 return difference;
	 } 
	 
	 public static <T> boolean hasDuplicateValues(List<T> collection) {
		 boolean hasDuplicate = false;
		 
		 if (collection == null) {
			 return hasDuplicate;
		 }
		 
		 List<T> duplicates =    
				 collection.stream()
			    .filter(e -> Collections.frequency(collection, e) > 1)
			    .distinct()
			    .collect(Collectors.toList());
		 
		 if (duplicates.size() > 0) {
			 hasDuplicate = true;
		 }
		 
		 return hasDuplicate;
	 }
 };