package servlets;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import java.io.BufferedWriter;
import java.io.Writer;
import java.io.File;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;

class BoardwalkJsonReaderJsonAPI
{
	/**
	 * @param args
	 */
	 BoardwalkJsonReaderJsonAPI(){
	 }
	String json;
	
	BoardwalkJsonReaderJsonAPI(String json){
		this.json=json;
	}
	public String[] jsonreader(String input)
	{
		try 
		{
			FileInputStream fis2 = new FileInputStream(json);
			InputStreamReader isr = new InputStreamReader(fis2, "UTF-8");
			System.out.println("in seperate file");
			String[] headerInfo=null;
			List<String> listheader = new ArrayList<String>();
			System.out.println("hi");
			JSONParser parser = new JSONParser();
			Object obj = parser.parse(new BufferedReader(isr));

			JSONObject jsonObject = (JSONObject) obj;
			//System.out.println("hi"+ jsonObject.toString());
			JSONArray msg = (JSONArray) jsonObject.get(input);
			 headerInfo=new String[msg.size()];
			//System.out.println(jsonObject.get("col1").toString());
			@SuppressWarnings("unchecked")
			int i=0;
			Iterator<String> iterator = msg.iterator();
			while (iterator.hasNext()) {
						listheader.add(iterator.next());
				System.out.println("in list"+listheader.get(i));
				i++;
			}
			headerInfo = listheader.toArray(headerInfo);
		//	System.out.println("in array "+headerInfo[1]);
			return headerInfo;
		}
		catch(Exception e)
		{
			return null;
		}
	}
		
		
	public void filewrite(String input)
	{
		try {
			InputStream fis = new FileInputStream(input);
			Reader rd = new InputStreamReader(fis, "UTF-8");
			BufferedReader in = new BufferedReader(rd);
			//BufferedReader in = new BufferedReader(new FileReader("C:/Users/yoddha/Desktop/bae4_1 files/testingdata2.txt"));
			String line = null;
			String [] a={"HeaderData","ColumnData","RowData","CellData"};
			String [] l=null;
			List s=new ArrayList();
			String t[] = null;
			
			JSONObject obj = new JSONObject();
			
			int x=0;
			while((line = in.readLine()) != null)
			{
				//JSONArray list = new JSONArray();
				//line.replaceAll("", replacement)
				l=line.split("");			//chr(2) content delimiter
			
				//String [] data=a[1].split(",");
				//System.out.println(a[1].replaceAll("", ","));
				//String q=",";
			 
				//list.add(a[1].split(""));
			
				for(int i=0;i<l.length;i++)
				{
					JSONArray jarr=new JSONArray();
					System.out.print("data test  ="+l[i]);
					 
					if(i==2&&l[i].lastIndexOf("")==l[i].length()-1)
					{
						l[i]=l[i]+" ";
					}
					t=l[i].split("");
					for(int j=0;j<t.length;j++)
						jarr.add(t[j].trim());
					//list.put(data[i]);
					obj.put(a[i], jarr);
				}
					//iobj.get(a[0]);
			}
			try 
			{
				File fileDir = new File("d:/tomcat7/webapps/EPortal/Staging_JSON/JSON_Data_File.json");
					 Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileDir), "UTF8"));
		         out.append(obj.toString());
				 out.flush();
		         out.close();
				/* FileWriter file = new FileWriter("C:/Users/yoddha/Desktop/bae4_1 files/testingdata2.json");
				file.write(obj.toString());
				file.flush();
				file.close(); */
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
			
			System.out.println(obj);
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

