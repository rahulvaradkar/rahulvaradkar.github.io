//javax.xml-1.3.4.jar
//w3c-dom.jar

//Files in this exercise
// UserParserHandler.java
// user.java
// UsersXmlParser.java
// TestSaxParser.java
// sample.xml


import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
 
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;


public class UsersXmlParser 
{
  public ArrayList parseXml(InputStream in)
    {
        //Create a empty link of users initially
        ArrayList<User> users = new ArrayList<User>();
        try
        {
            //Create default handler instance
            UserParserHandler handler = new UserParserHandler();
 
            //Create parser from factory
            XMLReader parser = XMLReaderFactory.createXMLReader();
 
            //Register handler with parser
            parser.setContentHandler(handler);
 
            //Create an input source from the XML input stream
            InputSource source = new InputSource(in);
 
            //parse the document
            parser.parse(source);
 
            //populate the parsed users list in above created empty list; You can return from here also.
            users = handler.getUsers();
 
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
        return users;
    }	
}