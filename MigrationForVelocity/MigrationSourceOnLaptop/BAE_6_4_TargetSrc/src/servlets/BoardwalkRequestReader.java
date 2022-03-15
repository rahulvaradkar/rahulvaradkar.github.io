/*
 * BoardwalkRequestReader.java
 *
 * Created on Nov 25, 2010
 */

package servlets;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.util.zip.*;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.*;
/**
 *
 * @author  Sarang Kulkarni
 */
public class BoardwalkRequestReader
{
	int _pos = 0;
	ZipInputStream zipIn = null;
	BufferedReader reader = null;
	com.boardwalk.util.UnicodeInputStream uis = null;
	
	public BoardwalkRequestReader(ServletRequest request) throws IOException
	{
		try
		{
			StringBuffer sb = new StringBuffer();
			BufferedReader br = request.getReader();
			String line = new String();
			line = br.readLine();

			while (line != null)
			{
				sb.append(line);
				line = br.readLine();
				if (line != null)
				{
					sb.append("\n");
				}
			}

			br.close();
			
			zipIn = new ZipInputStream(new ByteArrayInputStream(Base64.decodeBase64(sb.toString().getBytes())));
			zipIn.getNextEntry();
			// wrap around UnicodeInputStream to take care of Java bug related to BOM mark in UTF-8 encoded files
			String bw_client = ((HttpServletRequest)request).getHeader("X-client");
			if (bw_client != null && !bw_client.equals("MacExcel"))
			{
				uis = new com.boardwalk.util.UnicodeInputStream(zipIn);
				sb = null;
			}

			reader = new BufferedReader(new InputStreamReader(zipIn, "UTF-8"));
			//reader = new BufferedReader(new InputStreamReader(zipIn, "ISO-8859-1")); 
			System.out.println("reader = " + reader);
		}
		catch (IOException e)
		{
			e.printStackTrace();
			throw e;
		}
	}

	// returns data upto next content delimeter
	// returns null if reached end of request buffer
	public String getNextContent() throws IOException
	{
		String retString = null;
		StringBuffer sb = null;
		int ch;

		boolean foundContent = false;
		while ((ch = reader.read()) > -1)
		{
			if (sb == null)
			{
				sb = new StringBuffer();
			}

			if (ch == 2)
			{
				foundContent = true;
				//System.out.println("Found next content = " + sb.toString ());
				break;
			}
			else
			{

				sb.append((char)ch);
			}
		}

		if (sb == null) // no more content
		{
			return null;
		}
		else
		{
			//System.out.println("getNextContent >> " + sb.toString ());
			return sb.toString();
		}
	}

	public void close() throws IOException
	{
		try
		{
			zipIn.closeEntry();
			zipIn.close();
			reader.close();
			uis.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
