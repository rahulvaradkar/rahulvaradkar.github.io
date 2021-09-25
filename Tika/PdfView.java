import java.io.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.BodyContentHandler;

// for PDF parser
import org.apache.tika.parser.pdf.PDFParser;

import org.apache.tika.Tika;

//For autodetect parser
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.AutoDetectParser;


import org.xml.sax.SAXException;

import java.util.*;
import java.net.*;
import java.text.*;
import java.util.Scanner;


class PdfView {


	public static void main(final String[] args) throws IOException,TikaException, SAXException 
	{


		//assume example.mp3 is in your current directory
		// File file = new File("teva po.pdf");//
		//File file = new File(args[0]);//

		//Instantiating tika facade class 
		//Tika tika = new Tika();

		//detecting the file type using detect method
		//String filetype = tika.detect(file);
		//System.out.println(filetype);

		//String ft = getFileType(args[0]);
		//System.out.println("filetype :" + ft);

		//String fn = args[0];
		//System.out.println("fn :" + fn);

	        //String fileContents = tika.parseToString(file);
        	//System.out.println("Extracted Contents : " + fileContents);

		//BodyContentHandler handler = new BodyContentHandler();
		//Metadata metadata = new Metadata();
		// FileInputStream inputstream = new FileInputStream(new File("teva po.pdf"));
		//FileInputStream inputstream = new FileInputStream(file);
		//ParseContext pcontext = new ParseContext();

		//System.out.println("Parsing being done using AutoDetectParser");
		//System.out.println("Re-initializing handler, metadata, inputstream, pcontext");

		File file = new File(args[0]);//

		BodyContentHandler handler = new BodyContentHandler();
		Metadata metadata = new Metadata();
		FileInputStream inputstream = new FileInputStream(file);
		ParseContext pcontext = new ParseContext();

		Parser parser = new AutoDetectParser();
		parser.parse(inputstream, handler, metadata, pcontext);

		//getting the content of the document
		//System.out.println("rpv......... Contents of the File :" + handler.toString());
		String fileContent = handler.toString();
		System.out.println(fileContent);
	}


	public static String getFileType(String filename) throws IOException
	{
		File file = new File(filename);//

		//Instantiating tika facade class 
		Tika tika = new Tika();

		//detecting the file type using detect method
		String filetype = tika.detect(file);
		System.out.println(filetype);
		return filetype;
	}

 }
