//javax.xml-1.3.4.jar
//w3c-dom.jar

//Files in this exercise
// xmlParserHandler.java
// xmlParser.java
// TestXmlSaxParser.java
// sample.xml


import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
//import java.util.LinkedHashSet;
import java.util.*;


import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;


public class xmlParser 
{
    xmlParserHandler handler;

    public void parseXml(String source)
    {

        try
        {
            //Create default handler instance
            // xmlParserHandler handler = new xmlParserHandler();
            handler = new xmlParserHandler();

            //Create parser from factory
            XMLReader parser = XMLReaderFactory.createXMLReader();
 
            //Register handler with parser
            parser.setContentHandler(handler);
 
            //Create an input source from the XML input stream
            // InputSource source = new InputSource(in);
 
            //parse the document
            parser.parse(source);
 
        } 
        catch (SAXException e) 
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } 

        catch (IOException e) 
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } 

        finally {
 
        }

    }

    public void parseXml(InputStream in)
    {

        try
        {
            //Create default handler instance
            // xmlParserHandler handler = new xmlParserHandler();
            handler = new xmlParserHandler();

            //Create parser from factory
            XMLReader parser = XMLReaderFactory.createXMLReader();
 
            //Register handler with parser
            parser.setContentHandler(handler);
 
            //Create an input source from the XML input stream
            InputSource source = new InputSource(in);
 
            //parse the document
            parser.parse(source);
 
        } 
        catch (SAXException e) 
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } 

        catch (IOException e) 
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } 

        finally {
 
        }

    }


    public Iterator<String> getUniquePaths() throws SAXException
    {

        Iterator<String> itr;

        itr = handler.getUniquePaths();

        // LinkedHashSet<String> al=new LinkedHashSet<String>();  

        // allPaths.forEach(k->{
        //     // System.out.println("All Paths....."+k);
        //     al.add((String) k);
        // });

        // Iterator<String> itr = al.iterator();  
        // while(itr.hasNext())
        // {  
        //  System.out.println("Unique Paths : " + itr.next());  
        // }  
        return itr;

    }

}