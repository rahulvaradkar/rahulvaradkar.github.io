//javax.xml-1.3.4.jar
//w3c-dom.jar

//Files in this exercise
// xmlParserHandler.java
// xmlParser.java
// TestXmlSaxParser.java
// sample.xml

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.*;

import org.xml.sax.SAXException;

public class TestXmlSaxParser 
{

    public static void main(String[] args) throws FileNotFoundException, SAXException
    {
        //Locate the file
//        File xmlFile = new File("sample.xml");
         File xmlFile = new File(args[0]);
        //Create the parser instance
        xmlParser parser = new xmlParser();
 
        //Parse the file
        // ArrayList users = parser.parseXml(new FileInputStream(xmlFile));
        parser.parseXml(new FileInputStream(xmlFile));

        //Verify the result
        System.out.println("Done");

        Iterator<String> itr;
        itr = parser.getUniquePaths();

        System.out.println("Inside TestXmlSaxParser Unique Paths : ");

        int i = 1;

        while(itr.hasNext())
        {  
            System.out.println("Unique Paths " + i + " : " + itr.next());  
            i=i+1;
        }  

 
    }
	
}